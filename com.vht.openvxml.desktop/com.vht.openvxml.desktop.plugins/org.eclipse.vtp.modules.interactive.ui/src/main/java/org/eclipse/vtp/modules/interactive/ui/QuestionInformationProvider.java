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
package org.eclipse.vtp.modules.interactive.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.vtp.desktop.model.elements.core.PrimitiveInformationProvider;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.eclipse.vtp.modules.standard.ui.ExtendedInteractiveEventManager;

import com.openmethods.openvxml.desktop.model.businessobjects.FieldType;
import com.openmethods.openvxml.desktop.model.workflow.design.IDesignElement;
import com.openmethods.openvxml.desktop.model.workflow.design.IDesignElementConnectionPoint;
import com.openmethods.openvxml.desktop.model.workflow.design.ISecurableElement;
import com.openmethods.openvxml.desktop.model.workflow.design.Variable;
import com.openmethods.openvxml.desktop.model.workflow.internal.design.ConnectorRecord;

public class QuestionInformationProvider extends PrimitiveInformationProvider
		implements ISecurableElement {
	List<ConnectorRecord> connectorRecords = new ArrayList<ConnectorRecord>();
	String variableName = "";
	boolean secured = false;

	public QuestionInformationProvider(PrimitiveElement element) {
		super(element);
		connectorRecords.add(new ConnectorRecord(element, "Continue",
				IDesignElementConnectionPoint.ConnectionPointType.EXIT_POINT));
		connectorRecords.add(new ConnectorRecord(element,
				"error.input.noinput",
				IDesignElementConnectionPoint.ConnectionPointType.ERROR_POINT));
		connectorRecords.add(new ConnectorRecord(element,
				"error.input.nomatch",
				IDesignElementConnectionPoint.ConnectionPointType.ERROR_POINT));
		connectorRecords.add(new ConnectorRecord(element,
				"error.disconnect.hangup",
				IDesignElementConnectionPoint.ConnectionPointType.ERROR_POINT));
		List<String> events = ExtendedInteractiveEventManager.getDefault()
				.getExtendedEvents();
		for (String event : events) {
			connectorRecords
					.add(new ConnectorRecord(
							element,
							event,
							IDesignElementConnectionPoint.ConnectionPointType.EXIT_POINT));
		}
	}

	@Override
	public boolean acceptsConnector(IDesignElement origin) {
		return true;
	}

	@Override
	public ConnectorRecord getConnectorRecord(String recordName) {
		for (int i = 0; i < connectorRecords.size(); i++) {
			ConnectorRecord cr = connectorRecords.get(i);
			if (cr.getName().equals(recordName)) {
				return cr;
			}
		}
		return null;
	}

	@Override
	public List<ConnectorRecord> getConnectorRecords() {
		return Collections.unmodifiableList(connectorRecords);
	}

	@Override
	public List<ConnectorRecord> getConnectorRecords(
			IDesignElementConnectionPoint.ConnectionPointType... types) {
		List<ConnectorRecord> ret = new ArrayList<ConnectorRecord>();
		for (int i = 0; i < connectorRecords.size(); i++) {
			ConnectorRecord cr = connectorRecords.get(i);
			if (cr.getType().isSet(
					IDesignElementConnectionPoint.ConnectionPointType
							.getFlagSet(types))) {
				ret.add(cr);
			}
		}
		return ret;
	}

	@Override
	public void readConfiguration(org.w3c.dom.Element configuration) {
		variableName = configuration.getAttribute("variable-name");
		secured = Boolean.parseBoolean(configuration.getAttribute("secured"));
	}

	@Override
	public void writeConfiguration(org.w3c.dom.Element configuration) {
		configuration.setAttribute("variable-name", variableName);
		configuration.setAttribute("secured", Boolean.toString(secured));
	}

	// public List getPropertiesPanels()
	// {
	// List ret = new ArrayList();
	// ret.add(new QuestionGeneralPropertiesPanel("General", getElement()));
	// ret.add(new QuestionCombinedMediaPropertiesPanel(getElement()));
	// return ret;
	// }

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String text) {
		this.variableName = text;
	}

	@Override
	public List<Variable> getOutgoingVariables(String exitPoint,
			boolean localOnly) {
		List<Variable> ret = new ArrayList<Variable>();
		if (exitPoint.equals("Continue") && !variableName.equals("")) {
			Variable var = new Variable(variableName, FieldType.STRING);
			ret.add(var);
		}
		return ret;
	}

	@Override
	public boolean hasConnectors() {
		return true;
	}

	@Override
	public boolean isSecured() {
		return secured;
	}

	@Override
	public void setSecured(boolean secured) {
		this.secured = secured;
	}
}
