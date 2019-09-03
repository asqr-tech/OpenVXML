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
package com.openmethods.openvxml.desktop.model.workflow.configuration;

public interface ConfigurationManager extends Cloneable
{
	public String getType();
	
	public String getXMLVersion();
	
	public void readConfiguration(org.w3c.dom.Element configuration) throws ConfigurationException;
	
	public void writeConfiguration(org.w3c.dom.Element configuration);
	
	public Object clone();
}
