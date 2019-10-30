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
package org.eclipse.vtp.modules.attacheddata.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.vtp.desktop.model.elements.core.PrimitiveInformationProvider;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.w3c.dom.NodeList;

import com.openmethods.openvxml.desktop.model.workflow.design.IDesignElement;
import com.openmethods.openvxml.desktop.model.workflow.design.IDesignElementConnectionPoint;
import com.openmethods.openvxml.desktop.model.workflow.design.Variable;
import com.openmethods.openvxml.desktop.model.workflow.internal.design.ConnectorRecord;

public class ReceiveAttachedDataInformationProvider extends
		PrimitiveInformationProvider {
	List<ConnectorRecord> connectorRecords = new ArrayList<ConnectorRecord>();
	private String output = "";
	private String input = "";

	public ReceiveAttachedDataInformationProvider(PrimitiveElement element) {
		super(element);
		connectorRecords.add(new ConnectorRecord(element, "Continue",
				IDesignElementConnectionPoint.ConnectionPointType.EXIT_POINT));
		connectorRecords.add(new ConnectorRecord(element,
				"error.disconnect.hangup",
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
		NodeList nl = configuration.getElementsByTagName("meta-data-request");
		org.w3c.dom.Element metaDataRequestElement = null;
		if (nl.getLength() == 0) {
			metaDataRequestElement = configuration.getOwnerDocument()
					.createElement("meta-data-request");
		} else {
			metaDataRequestElement = (org.w3c.dom.Element) nl.item(0);
		}

		input = metaDataRequestElement.getAttribute("input");
		output = metaDataRequestElement.getAttribute("output");
	}

	@Override
	public void writeConfiguration(org.w3c.dom.Element configuration) {
		org.w3c.dom.Element comparisonElement = configuration
				.getOwnerDocument().createElement("meta-data-request");
		comparisonElement.setAttribute("input", input);
		comparisonElement.setAttribute("output", output);
		configuration.appendChild(comparisonElement);
	}

	@Override
	public List<Variable> getOutgoingVariables(String exitPoint,
			boolean localOnly) {
		return Collections.emptyList();
	}

	@Override
	public boolean hasConnectors() {
		return true;
	}

	public void setOutput(String path) {
		this.output = path;
	}

	public String getOutput() {
		return output;
	}

	public void setInput(String path) {
		this.input = path;
	}

	public String getInput() {
		return input;
	}
}
