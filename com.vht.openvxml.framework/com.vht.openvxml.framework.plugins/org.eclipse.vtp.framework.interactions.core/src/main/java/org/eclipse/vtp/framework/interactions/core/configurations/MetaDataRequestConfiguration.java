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
package org.eclipse.vtp.framework.interactions.core.configurations;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.vtp.framework.common.IDataObject;
import org.eclipse.vtp.framework.core.IConfiguration;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class MetaDataRequestConfiguration implements IConfiguration,
		InteractionsConstants {
	private static final String inputTagName = "input";
	private static final String outputTagName = "output";
	/** The value to use for the left-hand side of the operation. */
	private String input = "";
	/** The value to use for the right-hand side of the operation. */
	private String output = "";
	/**
	 * A map of the key names and, after the request completes, their associated
	 * value as IDataObjects.
	 */
	private Map<String, IDataObject> kvpMap = new HashMap<String, IDataObject>();

	public MetaDataRequestConfiguration() {
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input == null ? "" : input;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output == null ? "" : output;
	}

	public Map<String, IDataObject> getKvpMap() {
		return kvpMap;
	}

	public void setKvpMap(Map<String, IDataObject> kvpMap) {
		this.kvpMap = kvpMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#load(
	 * org.w3c.dom.Element)
	 */
	@Override
	public void load(Element configurationElement) {
		input = "";
		NodeList inputList = configurationElement.getElementsByTagNameNS(
				NAMESPACE_URI, inputTagName);
		if (inputList.getLength() > 0) {
			Element inputElement = (Element) inputList.item(0);
			input = inputElement.getAttribute(NAME_VALUE);
		}

		output = "";
		NodeList outputList = configurationElement.getElementsByTagNameNS(
				NAMESPACE_URI, outputTagName);
		if (outputList.getLength() > 0) {
			Element outputElement = (Element) outputList.item(0);
			output = outputElement.getAttribute(NAME_VALUE);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#save(
	 * org.w3c.dom.Element)
	 */
	@Override
	public void save(Element configurationElement) {
		String inName = inputTagName;
		String outName = outputTagName;
		String prefix = configurationElement.getPrefix();
		if (prefix != null && prefix.length() > 0) {
			inName = prefix + ":" + inName;
			outName = prefix + ":" + outName;
		}

		Element inputElement = configurationElement.getOwnerDocument()
				.createElementNS(NAMESPACE_URI, inName);
		inputElement.setAttribute(NAME_VALUE, input);
		configurationElement.appendChild(inputElement);

		Element outputElement = configurationElement.getOwnerDocument()
				.createElementNS(NAMESPACE_URI, outName);
		outputElement.setAttribute(NAME_VALUE, output);
		configurationElement.appendChild(outputElement);
	}
}
