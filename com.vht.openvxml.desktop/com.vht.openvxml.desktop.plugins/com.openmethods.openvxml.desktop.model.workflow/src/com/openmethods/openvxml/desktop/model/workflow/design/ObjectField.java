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
package com.openmethods.openvxml.desktop.model.workflow.design;

import com.openmethods.openvxml.desktop.model.businessobjects.FieldType;

/**
 * @author Trip
 *
 */
public class ObjectField extends ObjectDefinition {
	ObjectDefinition parent;
	boolean readOnly;

	/**
	 * @param name
	 * @param fieldType
	 * @param readOnly
	 */
	public ObjectField(String name, FieldType fieldType, boolean readOnly) {
		super(name, fieldType);
		this.readOnly = readOnly;
	}

	/**
	 * @param parent
	 */
	public void setParent(ObjectDefinition parent) {
		this.parent = parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.desktop.model.core.ObjectDefinition#getPath()
	 */
	@Override
	public String getPath() {
		return parent.getPath() + "." + getName();
	}

	/**
	 * @return
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * @return
	 */
	public ObjectDefinition getParent() {
		return parent;
	}
}
