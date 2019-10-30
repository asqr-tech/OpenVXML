/*--------------------------------------------------------------------------
 * Copyright (c) 2009 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package com.openmethods.openvxml.desktop.model.workflow;

import java.util.List;

import org.eclipse.core.runtime.IAdaptable;

import com.openmethods.openvxml.desktop.model.workflow.design.Variable;

public interface IWorkflowExit extends IAdaptable {
	public static final String NORMAL = "NORMAL";
	public static final String ERROR = "ERROR";

	public String getId();

	public String getName();

	public String getType();

	public List<Variable> getExportedVariables();
}
