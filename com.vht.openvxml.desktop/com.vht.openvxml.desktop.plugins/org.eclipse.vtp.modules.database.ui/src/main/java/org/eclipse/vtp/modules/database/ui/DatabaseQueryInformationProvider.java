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
package org.eclipse.vtp.modules.database.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.elements.core.PrimitiveInformationProvider;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.eclipse.vtp.modules.database.ui.properties.DatabaseQuerySettingsStructure;
import org.w3c.dom.NodeList;

import com.openmethods.openvxml.desktop.model.businessobjects.IBusinessObjectProjectAspect;
import com.openmethods.openvxml.desktop.model.workflow.design.IDesignElement;
import com.openmethods.openvxml.desktop.model.workflow.design.IDesignElementConnectionPoint;
import com.openmethods.openvxml.desktop.model.workflow.design.Variable;
import com.openmethods.openvxml.desktop.model.workflow.internal.VariableHelper;
import com.openmethods.openvxml.desktop.model.workflow.internal.design.ConnectorRecord;

public class DatabaseQueryInformationProvider extends
		PrimitiveInformationProvider {
	IBusinessObjectProjectAspect businessObjectAspect = null;
	List<ConnectorRecord> connectorRecords = new ArrayList<ConnectorRecord>();
	DatabaseQuerySettingsStructure settings = null;;

	public DatabaseQueryInformationProvider(PrimitiveElement element) {
		super(element);
		connectorRecords.add(new ConnectorRecord(element, "Continue",
				IDesignElementConnectionPoint.ConnectionPointType.EXIT_POINT));
		connectorRecords.add(new ConnectorRecord(element,
				"error.database.connection",
				IDesignElementConnectionPoint.ConnectionPointType.ERROR_POINT));
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
		return connectorRecords;
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
		IOpenVXMLProject project = getElement().getDesign().getDocument()
				.getProject();
		businessObjectAspect = (IBusinessObjectProjectAspect) project
				.getProjectAspect(IBusinessObjectProjectAspect.ASPECT_ID);
		NodeList nl = configuration.getElementsByTagName("settings");

		if (nl.getLength() > 0) {
			getSettings().read((org.w3c.dom.Element) nl.item(0));
		}
	}

	@Override
	public void writeConfiguration(org.w3c.dom.Element configuration) {
		org.w3c.dom.Element settingsElement = configuration.getOwnerDocument()
				.createElement("settings");
		configuration.appendChild(settingsElement);
		getSettings().write(settingsElement);
	}

	public DatabaseQuerySettingsStructure getSettings() {
		if (settings == null) {
			settings = new DatabaseQuerySettingsStructure(
					businessObjectAspect.getBusinessObjectSet());
		}
		return settings;
	}

	public void setSettings(DatabaseQuerySettingsStructure settings) {
		this.settings = settings;
	}

	@Override
	public List<Variable> getOutgoingVariables(String exitPoint,
			boolean localOnly) {
		List<Variable> ret = new ArrayList<Variable>();
		if (exitPoint.equals("Continue") && !settings.isTargetVariableExists()
				&& !settings.getTargetVariableName().equals("")) {
			Variable v = new Variable(settings.getTargetVariableName(),
					settings.getTargetVariableType());
			VariableHelper.buildObjectFields(v,
					businessObjectAspect.getBusinessObjectSet());
			ret.add(v);
		}
		return ret;
	}

	@Override
	public boolean hasConnectors() {
		return true;
	}

}
