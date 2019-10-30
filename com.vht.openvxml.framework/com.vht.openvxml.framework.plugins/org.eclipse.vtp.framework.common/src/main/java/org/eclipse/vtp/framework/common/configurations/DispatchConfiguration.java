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
package org.eclipse.vtp.framework.common.configurations;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.vtp.framework.core.IConfiguration;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A single configuration for forwarding to or including another process.
 * 
 * @author Lonnie Pryor
 */
public class DispatchConfiguration implements IConfiguration, CommonConstants {
	/** The URI of the process to dispatch to. */
	private String targetProcessURI = ""; //$NON-NLS-1$
	/** The variables that will be passed to the target process. */
	private final Map<String, VariableMappingConfiguration> variableMappings = new HashMap<String, VariableMappingConfiguration>();
	/** The variables that will be passed back from the target process. */
	private final Map<String, Map<String, String>> outgoingData = new HashMap<String, Map<String, String>>();

	/**
	 * Creates a new DispatchItemConfiguration.
	 * 
	 */
	public DispatchConfiguration() {
	}

	/**
	 * Clears all the currently configured information in this item.
	 */
	public void clear() {
		targetProcessURI = ""; //$NON-NLS-1$
		variableMappings.clear();
	}

	public String[] getOutgoingDataNames(String path) {
		final Map<String, String> map = outgoingData.get(path);
		if (map == null) {
			return new String[0];
		}
		return map.keySet().toArray(new String[map.size()]);
	}

	public String getOutgoingDataValue(String path, String name) {
		final Map<String, String> map = outgoingData.get(path);
		if (map == null) {
			return null;
		}
		return map.get(name);
	}

	public String[] getOutgoingPaths() {
		return outgoingData.keySet().toArray(new String[outgoingData.size()]);
	}

	/**
	 * Returns the URI of the process to dispatch to.
	 * 
	 * @return The URI of the process to dispatch to.
	 */
	public String getTargetProcessURI() {
		return targetProcessURI;
	}

	/**
	 * Returns the configuration for the specified variable in the target
	 * process.
	 * 
	 * @param variableName
	 *            The name of the variable in the target process to configure.
	 * @return The configuration for the specified variable in the target
	 *         process.
	 */
	public VariableMappingConfiguration getVariableMapping(String variableName) {
		if (variableName == null) {
			return null;
		}
		return variableMappings.get(variableName);
	}

	/**
	 * Returns the names of the variables that will be initialized in the target
	 * process.
	 * 
	 * @return The names of the variables that will be initialized in the target
	 *         process.
	 */
	public String[] getVariableNames() {
		return variableMappings.keySet().toArray(
				new String[variableMappings.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.framework.core.IConfiguration#load(org.w3c.dom.Element)
	 */
	@Override
	public void load(Element configurationElement) {
		targetProcessURI = configurationElement.getAttribute(NAME_URI);
		variableMappings.clear();
		outgoingData.clear();
		NodeList nodes = configurationElement.getElementsByTagNameNS(
				NAMESPACE_URI, NAME_VARIABLE_MAPPING);
		for (int i = 0; i < nodes.getLength(); ++i) {
			final Element mappingElement = (Element) nodes.item(i);
			final String name = mappingElement.getAttribute(NAME_NAME);
			final VariableMappingConfiguration mapping = new VariableMappingConfiguration();
			mapping.load(mappingElement);
			variableMappings.put(name, mapping);
		}
		nodes = configurationElement.getElementsByTagNameNS(NAMESPACE_URI,
				NAME_OUTGOING);
		for (int i = 0; i < nodes.getLength(); ++i) {
			final Element outgoingElement = (Element) nodes.item(i);
			final String path = outgoingElement.getAttribute(NAME_PATH);
			final NodeList nodes2 = outgoingElement.getElementsByTagNameNS(
					NAMESPACE_URI, NAME_ENTRY);
			for (int j = 0; j < nodes2.getLength(); ++j) {
				final Element entryElement = (Element) nodes2.item(j);
				final String name = entryElement.getAttribute(NAME_KEY);
				setOutgoingDataValue(path, name,
						entryElement.getAttribute(NAME_VALUE));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.framework.core.IConfiguration#save(org.w3c.dom.Element)
	 */
	@Override
	public void save(Element configurationElement) {
		configurationElement.setAttribute(NAME_URI, targetProcessURI);
		String variableMappingName = NAME_VARIABLE_MAPPING;
		String outgoingName = NAME_OUTGOING;
		String entryName = NAME_ENTRY;
		final String prefix = configurationElement.getPrefix();
		if (prefix != null && prefix.length() > 0) {
			variableMappingName = prefix + ":" + variableMappingName; //$NON-NLS-1$
			outgoingName = prefix + ":" + outgoingName; //$NON-NLS-1$
			entryName = prefix + ":" + entryName; //$NON-NLS-1$
		}
		for (final Map.Entry<String, VariableMappingConfiguration> entry : variableMappings
				.entrySet()) {
			final Element mappingElement = configurationElement
					.getOwnerDocument().createElementNS(NAMESPACE_URI,
							variableMappingName);
			entry.getValue().save(mappingElement);
			mappingElement.setAttribute(NAME_NAME, entry.getKey());
			configurationElement.appendChild(mappingElement);
		}
		for (final String path : outgoingData.keySet()) {
			final Element outgoingElement = configurationElement
					.getOwnerDocument().createElementNS(NAMESPACE_URI,
							outgoingName);
			outgoingElement.setAttribute(NAME_PATH, path);
			final Map<String, String> map = outgoingData.get(path);
			for (final String name : map.keySet()) {
				final Element entryElement = configurationElement
						.getOwnerDocument().createElementNS(NAMESPACE_URI,
								entryName);
				entryElement.setAttribute(NAME_KEY, name);
				entryElement.setAttribute(NAME_VALUE, map.get(name));
				outgoingElement.appendChild(entryElement);
			}
			configurationElement.appendChild(outgoingElement);
		}
	}

	public void setOutgoingDataValue(String path, String name, String value) {
		if (path == null || name == null) {
			return;
		}
		Map<String, String> map = outgoingData.get(path);
		if (map == null) {
			if (value == null) {
				return;
			}
			outgoingData.put(path, map = new HashMap<String, String>());
		}
		if (value == null) {
			map.remove(name);
			if (map.isEmpty()) {
				outgoingData.remove(path);
			}
		} else {
			map.put(name, value);
		}
	}

	/**
	 * Sets the URI of the process to dispatch to.
	 * 
	 * @param destinationURI
	 *            The URI of the process to dispatch to.
	 */
	public void setTargetProcessURI(String destinationURI) {
		this.targetProcessURI = destinationURI == null ? "" //$NON-NLS-1$
				: destinationURI;
	}

	/**
	 * Sets the configuration for the specified variable in the target process.
	 * 
	 * @param variableName
	 *            The name of the variable in the target process.
	 * @param variableMapping
	 *            The configuration for the specified variable in the target
	 *            process.
	 */
	public void setVariableMapping(String variableName,
			VariableMappingConfiguration variableMapping) {
		if (variableName == null) {
			return;
		}
		if (variableMapping == null) {
			variableMappings.remove(variableName);
		} else {
			variableMappings.put(variableName, variableMapping);
		}
	}
}
