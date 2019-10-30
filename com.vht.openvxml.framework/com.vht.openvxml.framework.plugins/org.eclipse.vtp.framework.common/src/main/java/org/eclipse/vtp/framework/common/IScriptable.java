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
package org.eclipse.vtp.framework.common;

/**
 * IScriptable.
 * 
 * @author Lonnie Pryor
 */
public interface IScriptable {
	boolean clearEntry(String name);

	boolean clearItem(int index);

	Object getEntry(String name);

	String[] getFunctionNames();

	Object getItem(int index);

	String getName();

	String[] getPropertyNames();

	boolean hasEntry(String name);

	boolean hasItem(int index);

	boolean hasValue();

	Object invokeFunction(String name, Object[] arguments) throws Exception;

	boolean isMutable();

	boolean setEntry(String name, Object value);

	boolean setItem(int index, Object value);

	Object toValue();
}
