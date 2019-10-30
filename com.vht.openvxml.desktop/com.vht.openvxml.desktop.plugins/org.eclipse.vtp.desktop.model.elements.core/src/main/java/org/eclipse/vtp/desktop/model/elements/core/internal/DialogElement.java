/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006-2007 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.desktop.model.elements.core.internal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.swt.graphics.Image;
import org.eclipse.vtp.desktop.model.elements.core.IDialogExit;

import com.openmethods.openvxml.desktop.model.workflow.design.IDesign;
import com.openmethods.openvxml.desktop.model.workflow.design.IDesignComponent;
import com.openmethods.openvxml.desktop.model.workflow.design.IDesignConnector;
import com.openmethods.openvxml.desktop.model.workflow.design.IDesignConstants;
import com.openmethods.openvxml.desktop.model.workflow.design.IDesignElement;
import com.openmethods.openvxml.desktop.model.workflow.design.IDesignElementConnectionPoint;
import com.openmethods.openvxml.desktop.model.workflow.design.ModelListener;
import com.openmethods.openvxml.desktop.model.workflow.design.Variable;
import com.openmethods.openvxml.desktop.model.workflow.internal.DesignDocument;
import com.openmethods.openvxml.desktop.model.workflow.internal.design.ConnectorRecord;
import com.openmethods.openvxml.desktop.model.workflow.internal.design.Design;
import com.openmethods.openvxml.desktop.model.workflow.internal.design.DesignElement;

public class DialogElement extends DesignElement implements ModelListener {
	public static final String ELEMENT_TYPE = "org.eclipse.vtp.desktop.model.elements.core.dialog";

	private boolean hasConnectors = false;
	private List<ConnectorRecord> connectorRecords = new ArrayList<ConnectorRecord>();
	private Map<String, NameWatcher> watchers = new HashMap<String, NameWatcher>();
	private IDesign dialogDesign = null;

	public DialogElement(String name) {
		super(name);
	}

	public DialogElement(String id, String name, Properties properties) {
		super(id, name, properties);
	}

	@Override
	public String getType() {
		return ELEMENT_TYPE;
	}

	@Override
	public void resolve() {
		dialogDesign = getDesign().getDocument().getDialogDesign(getId());
		dialogDesign.addListener(this);
		for (IDesignElement designElement : dialogDesign.getDesignElements()) {
			IDialogExit exit = (IDialogExit) designElement
					.getAdapter(IDialogExit.class);
			if (exit != null) {
				IDesignElementConnectionPoint.ConnectionPointType type = exit
						.getType();
				ConnectorRecord connectorRecord = new ConnectorRecord(
						(DesignElement) designElement, designElement.getName(),
						type);
				connectorRecords.add(connectorRecord);
				NameWatcher nameWatcher = new NameWatcher(connectorRecord);
				designElement.addPropertyListener(nameWatcher);
				watchers.put(designElement.getId(), nameWatcher);
				hasConnectors = true;
			}
		}
	}

	@Override
	public IDesignElementConnectionPoint getConnectorRecord(String recordName) {
		for (int i = 0; i < connectorRecords.size(); i++) {
			ConnectorRecord cr = connectorRecords.get(i);
			if (cr.getName().equals(recordName)) {
				return cr;
			}
		}
		return null;
	}

	@Override
	public List<IDesignElementConnectionPoint> getConnectorRecords() {
		return new LinkedList<IDesignElementConnectionPoint>(connectorRecords);
	}

	@Override
	public List<IDesignElementConnectionPoint> getConnectorRecords(
			IDesignElementConnectionPoint.ConnectionPointType... types) {
		List<IDesignElementConnectionPoint> ret = new LinkedList<IDesignElementConnectionPoint>();
		for (ConnectorRecord cr : connectorRecords) {
			if (cr.getType().isSet(
					IDesignElementConnectionPoint.ConnectionPointType
							.getFlagSet(types))) {
				ret.add(cr);
			}
		}
		return ret;
	}

	@Override
	public void readCustomConfiguration(org.w3c.dom.Element configuration) {
	}

	@Override
	public void writeCustomConfiguration(org.w3c.dom.Element customElement) {
	}

	@Override
	public boolean acceptsConnector(IDesignElement origin) {
		return true;
	}

	@Override
	public String getTypeName() {
		return "Dialog";
	}

	@Override
	public Image getIcon() {
		return null;
	}

	@Override
	protected void setId(String id) {
		super.setId(id);
		((Design) dialogDesign).setDesignId(id);
	}

	@Override
	public boolean hasConnectors() {
		return hasConnectors;
	}

	@Override
	public void nameChanged(IDesign model) {
		setName(model.getName());
	}

	@Override
	public void componentAdded(IDesign model, IDesignComponent component) {
		if (component instanceof DesignElement) {
			DesignElement designElement = (DesignElement) component;
			IDialogExit exit = (IDialogExit) designElement
					.getAdapter(IDialogExit.class);
			if (exit != null) {
				IDesignElementConnectionPoint.ConnectionPointType type = exit
						.getType();
				ConnectorRecord connectorRecord = new ConnectorRecord(
						designElement, designElement.getName(), type);
				connectorRecords.add(connectorRecord);
				NameWatcher nameWatcher = new NameWatcher(connectorRecord);
				designElement.addPropertyListener(nameWatcher);
				watchers.put(designElement.getId(), nameWatcher);
				hasConnectors = true;
			}
		}
	}

	@Override
	public void componentRemoved(IDesign model, IDesignComponent component) {
		if (component instanceof DesignElement) {
			DesignElement designElement = (DesignElement) component;
			IDialogExit exit = (IDialogExit) designElement
					.getAdapter(IDialogExit.class);
			if (exit != null) {
				NameWatcher nameWatcher = watchers
						.remove(designElement.getId());
				designElement.removePropertyListener(nameWatcher);
				connectorRecords.remove(nameWatcher.watched);
				if (nameWatcher.watched.getDesignConnector() != null) {
					IDesignConnector connector = nameWatcher.watched
							.getDesignConnector();
					connector.removeConnectionPoint(nameWatcher.watched);
					if (connector.getConnectionPoints().size() == 0) // removed
																		// last
																		// connector
																		// record
					{
						getDesign().removeDesignConnector(connector);
					}
				}
			}
		}
	}

	private class NameWatcher implements PropertyChangeListener {
		ConnectorRecord watched = null;

		public NameWatcher(ConnectorRecord watched) {
			super();
			this.watched = watched;
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(IDesignConstants.PROP_NAME)) {
				watched.setName((String) evt.getNewValue());
			} else if (evt.getPropertyName().equals(IDialogExit.PROP_EXIT_TYPE)) {
				DesignElement designElement = (DesignElement) evt.getSource();
				IDialogExit exit = (IDialogExit) designElement
						.getAdapter(IDialogExit.class);
				if (exit != null) {
					watched.setType(exit.getType());
				}
			}
		}
	}

	@Override
	public List<Variable> getOutgoingVariables(String exitPoint,
			boolean localOnly) {
		System.out.println("getting dialog variables for exit: " + exitPoint);
		IDesign dialogDesign = getDesign().getDocument().getDialogDesign(
				getId());
		List<IDesignElement> elements = dialogDesign.getDesignElements();
		for (IDesignElement designElement : elements) {
			IDialogExit dialogExit = (IDialogExit) designElement
					.getAdapter(IDialogExit.class);
			if (dialogExit != null) {
				System.out.println("found dialog exit: "
						+ designElement.getName());
				if (designElement.getName().equals(exitPoint)) {
					return dialogDesign.getVariablesFor(designElement, true);
				}
			}
		}
		return Collections.emptyList();
	}

	@Override
	public void orientationChanged(IDesign model) {
	}

	@Override
	public void paperSizeChanged(IDesign model) {
	}

	@Override
	public boolean canBeContainedBy(IDesign design) {
		return design.equals(design.getDocument().getMainDesign());
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter.isAssignableFrom(getClass())) {
			return this;
		}
		return null;
	}

	@Override
	public void delete() {
		((DesignDocument) getDesign().getDocument()).removeDialogDesign(this
				.getId());
		super.delete();
	}
}
