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
import java.util.List;

import org.eclipse.vtp.desktop.model.elements.core.PrimitiveInformationProvider;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.eclipse.vtp.framework.util.XMLUtilities;
import org.eclipse.vtp.modules.standard.ui.ExtendedInteractiveEventManager;
import org.w3c.dom.NodeList;

import com.openmethods.openvxml.desktop.model.workflow.design.IDesignElement;
import com.openmethods.openvxml.desktop.model.workflow.design.IDesignElementConnectionPoint;
import com.openmethods.openvxml.desktop.model.workflow.internal.design.ConnectorRecord;

/**
 * @author Trip
 * @version 1.0
 */
public class SubmitInformationProvider extends PrimitiveInformationProvider {
	List<ConnectorRecord> connectorRecords = new ArrayList<ConnectorRecord>();
	private List<SubmitInput> inputs = new ArrayList<SubmitInput>();
	private String url = "";
	private String method = "";

	/**
	 * @param name
	 */
	public SubmitInformationProvider(PrimitiveElement element) {
		super(element);
		// connectorRecords.add(new ConnectorRecord(element, "error.submit",
		// IDesignElementConnectionPoint.ConnectionPointType.ERROR_POINT));
		// connectorRecords.add(new ConnectorRecord(element,
		// "error.disconnect.hangup",
		// IDesignElementConnectionPoint.ConnectionPointType.ERROR_POINT));
		// connectorRecords.add(new ConnectorRecord(element, "error.badfetch",
		// IDesignElementConnectionPoint.ConnectionPointType.ERROR_POINT));
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

	public String getURL() {
		return url;
	}

	public void setURL(String url) {
		this.url = url;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	@Override
	public void writeConfiguration(org.w3c.dom.Element configuration) {
		configuration.setAttribute("url", XMLUtilities.encodeAttribute(url));
		configuration.setAttribute("method",
				XMLUtilities.encodeAttribute(method));
		org.w3c.dom.Element inputsElement = configuration.getOwnerDocument()
				.createElement("inputs");
		configuration.appendChild(inputsElement);
		for (int i = 0; i < inputs.size(); i++) {
			SubmitInput si = inputs.get(i);
			org.w3c.dom.Element inputElement = configuration.getOwnerDocument()
					.createElement("input");
			inputsElement.appendChild(inputElement);
			inputElement.setAttribute("name", si.name);
			inputElement.setAttribute("type", Integer.toString(si.type));
			inputElement.setAttribute("value", si.value);
		}
	}

	@Override
	public void readConfiguration(org.w3c.dom.Element configuration) {
		url = configuration.getAttribute("url");
		method = configuration.getAttribute("method");
		NodeList nl = configuration.getElementsByTagName("inputs");
		if (nl.getLength() > 0) {
			org.w3c.dom.Element inputsElement = (org.w3c.dom.Element) nl
					.item(0);
			nl = inputsElement.getElementsByTagName("input");
			for (int i = 0; i < nl.getLength(); i++) {
				org.w3c.dom.Element inputElement = (org.w3c.dom.Element) nl
						.item(i);
				String inputName = inputElement.getAttribute("name");
				int inputType = Integer.parseInt(inputElement
						.getAttribute("type"));
				String inputValue = inputElement.getAttribute("value");
				inputs.add(new SubmitInput(inputName, inputType, inputValue));
			}
		}
	}

	public List<SubmitInput> getInputs() {
		return inputs;
	}

	public void setInputs(List<SubmitInput> inputs) {
		this.inputs = inputs;
	}

	public class SubmitInput {
		public String name = "";
		public int type = 0;
		public String value = "";

		public SubmitInput(String name, int type, String value) {
			super();
			this.name = name;
			this.type = type;
			this.value = value;
		}
	}

	@Override
	public boolean acceptsConnector(IDesignElement origin) {
		return true;
	}

	@Override
	public boolean hasConnectors() {
		return false;
	}
}
