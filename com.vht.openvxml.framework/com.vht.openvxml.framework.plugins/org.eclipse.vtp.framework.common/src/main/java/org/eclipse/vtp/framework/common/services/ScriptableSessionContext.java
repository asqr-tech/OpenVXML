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
package org.eclipse.vtp.framework.common.services;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.vtp.framework.common.IScriptable;
import org.eclipse.vtp.framework.core.ISessionContext;

/**
 * An {@link IScriptable} implementation that makes the {@link ISessionContext}
 * instance available as a scripting object.
 * 
 * <p>
 * This service will make available a "Session" object to all scripts in its
 * scope. The variable supports the following properties:
 * <ul>
 * <li><code>id</code>: a string containing the session ID</li>
 * <li><code>attributes</code>: an object containing the session attributes</li>
 * </ul>
 * The attributes object listed above will have a property for each session
 * attribute defined in the session context. New attributes may be added by
 * assigning to non-existent properties of the object. The attributes object
 * will not have an implicit value.
 * </p>
 * 
 * <p>
 * The "Session" scripting object uses the session ID as the implicit value,
 * thus it can be compared to other string objects.
 * </p>
 * 
 * @author Lonnie Pryor
 * @see ISessionContext
 */
public class ScriptableSessionContext implements IScriptable {
	/**
	 * Scripted access to the session attributes.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class ScriptableAttributes implements IScriptable {
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#clearEntry(
		 * java.lang.String)
		 */
		@Override
		public boolean clearEntry(String name) {
			context.clearAttribute(name);
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.vtp.framework.spi.scripting.IScriptable#clearItem(int)
		 */
		@Override
		public boolean clearItem(int index) {
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getEntry(
		 * java.lang.String)
		 */
		@Override
		public Object getEntry(String name) {
			return context.getAttribute(name);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#
		 * getFunctionNames()
		 */
		@Override
		public String[] getFunctionNames() {
			return new String[] {};
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getItem(int)
		 */
		@Override
		public Object getItem(int index) {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getName()
		 */
		@Override
		public String getName() {
			return "attributes"; //$NON-NLS-1$
		}

		@Override
		public String[] getPropertyNames() {
			return context.getAttributeNames();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasEntry(
		 * java.lang.String)
		 */
		@Override
		public boolean hasEntry(String name) {
			return context.getAttribute(name) != null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasItem(int)
		 */
		@Override
		public boolean hasItem(int index) {
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasValue()
		 */
		@Override
		public boolean hasValue() {
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.vtp.framework.spi.scripting.IScriptable#invokeFunction(
		 * java.lang.String, java.lang.Object[])
		 */
		@Override
		public Object invokeFunction(String name, Object[] arguments) {
			return null;
		}

		@Override
		public boolean isMutable() {
			// TODO Auto-generated method stub
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#setEntry(
		 * java.lang.String, java.lang.Object)
		 */
		@Override
		public boolean setEntry(String name, Object value) {
			context.setAttribute(name, value);
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#setItem(int,
		 * java.lang.Object)
		 */
		@Override
		public boolean setItem(int index, Object value) {
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#toValue()
		 */
		@Override
		public Object toValue() {
			return null;
		}
	}

	/** The context to provide scripting services for. */
	private final ISessionContext context;

	/** The scripted view of the session attributes. */
	private final ScriptableAttributes attributes = new ScriptableAttributes();

	/**
	 * Creates a new ScriptableSessionContext.
	 * 
	 * @param context
	 *            The context to provide scripting services for.
	 */
	public ScriptableSessionContext(ISessionContext context) {
		this.context = context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#clearEntry(
	 * java.lang.String)
	 */
	@Override
	public final boolean clearEntry(String name) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#clearItem(int)
	 */
	@Override
	public final boolean clearItem(int index) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getEntry(
	 * java.lang.String)
	 */
	@Override
	public final Object getEntry(String name) {
		if ("id".equals(name)) {
			return context.getSessionID();
		}
		if (attributes.getName().equals(name)) {
			return attributes;
		}
		if ("startTime".equals(name)) {
			return context.getSessionStartTime();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.framework.spi.scripting.IScriptable#getFunctionNames()
	 */
	@Override
	public final String[] getFunctionNames() {
		return new String[] {};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getItem(int)
	 */
	@Override
	public final Object getItem(int index) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getName()
	 */
	@Override
	public final String getName() {
		return "Session"; //$NON-NLS-1$
	}

	@Override
	public String[] getPropertyNames() {
		final List<String> propNames = new ArrayList<String>();
		propNames.add("id");
		propNames.add("startTime");
		for (final String prop : attributes.getPropertyNames()) {
			propNames.add(prop);
		}
		return propNames.toArray(new String[propNames.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasEntry(
	 * java.lang.String)
	 */
	@Override
	public final boolean hasEntry(String name) {
		return "id".equals(name) || attributes.getName().equals(name) || "startTime".equals(name); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasItem(int)
	 */
	@Override
	public final boolean hasItem(int index) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasValue()
	 */
	@Override
	public boolean hasValue() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#invokeFunction(
	 * java.lang.String, java.lang.Object[])
	 */
	@Override
	public final Object invokeFunction(String name, Object[] arguments) {
		return null;
	}

	@Override
	public boolean isMutable() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#setEntry(
	 * java.lang.String, java.lang.Object)
	 */
	@Override
	public final boolean setEntry(String name, Object value) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#setItem(int,
	 * java.lang.Object)
	 */
	@Override
	public final boolean setItem(int index, Object value) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#toValue()
	 */
	@Override
	public Object toValue() {
		return getEntry("id"); //$NON-NLS-1$
	}
}
