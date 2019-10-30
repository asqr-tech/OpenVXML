package org.eclipse.vtp.framework.common.services;

import org.eclipse.vtp.framework.common.IDataObject;
import org.eclipse.vtp.framework.common.IDataType;
import org.eclipse.vtp.framework.common.IValueObject;
import org.eclipse.vtp.framework.common.IVariableStorage;
import org.eclipse.vtp.framework.util.Guid;

/**
 * Basic implementation of an {@link IDataObject}.
 * 
 * @author Lonnie Pryor
 */
public abstract class DataObject implements IDataObject {
	/**
	 * 
	 */
	private final IVariableStorage variableStorage;
	/** The ID of this variable. */
	final String id;
	/** The type of this variable. */
	final IDataType type;
	boolean secured = false;

	/**
	 * Creates a new DataObject.
	 * 
	 * @param id
	 *            The ID of this instance or <code>null</code> to generate a new
	 *            ID.
	 * @param type
	 *            The type of this instance.
	 */
	protected DataObject(IVariableStorage variableStorage, String id,
			IDataType type) {
		this.variableStorage = variableStorage;
		if (id == null) {
			this.id = VariableRegistry.RECORD_PREFIX + Guid.createGUID();
		} else {
			this.id = id;
		}
		this.type = type;
	}

	@Override
	public String getId() {
		return id;
	}

	/**
	 * Loads this variable's data from the registry.
	 * 
	 * @return This variable's data from the registry.
	 */
	protected Object load() {
		final Object[] record = variableStorage.getRecord(id);
		if (record == null) {
			return null;
		}
		final Object[] value = (Object[]) record[1];
		this.secured = ((Boolean) value[0]).booleanValue();
		return value[1];
	}

	/**
	 * Saves this variable's data in the registry.
	 * 
	 * @param The
	 *            value to save in the registry.
	 * @throws IllegalStateException
	 *             If this object is read-only.
	 */
	protected void save(Object value) throws IllegalStateException {
		if (isReadOnly()) {
			throw new IllegalStateException();
		}
		variableStorage.setRecord(id, type, new Object[] {
				new Boolean(secured), value });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IDataObject#getType()
	 */
	@Override
	public final IDataType getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IDataObject#isReadOnly()
	 */
	@Override
	public boolean isReadOnly() {
		return false;
	}

	@Override
	public boolean isSecured() {
		return secured;
	}

	@Override
	public void setSecured(boolean secured) {
		final Object load = load();
		this.secured = secured;
		save(load);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IDataObject#getField(
	 * java.lang.String)
	 */
	@Override
	public IDataObject getField(String fieldName) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IDataObject#setField(
	 * java.lang.String, org.eclipse.vtp.framework.core.IDataObject)
	 */
	@Override
	public boolean setField(String fieldName, IDataObject variable) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IDataObject#isEqualTo(
	 * java.lang.Object)
	 */
	@Override
	public boolean isEqualTo(Object object) {
		return equals(object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.AbstractSessionScope.DataObject#
	 * isGreaterThan(java.lang.Object)
	 */
	@Override
	public boolean isGreaterThan(Object object) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.AbstractSessionScope.DataObject#
	 * isGreaterThanOrEqualTo(java.lang.Object)
	 */
	@Override
	public boolean isGreaterThanOrEqualTo(Object object) {
		return isGreaterThan(object) || isEqualTo(object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.AbstractSessionScope.DataObject#
	 * isLessThan(java.lang.Object)
	 */
	@Override
	public boolean isLessThan(Object object) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.AbstractSessionScope.DataObject#
	 * isLessThanOrEqualTo(java.lang.Object)
	 */
	@Override
	public boolean isLessThanOrEqualTo(Object object) {
		return isLessThan(object) || isEqualTo(object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public final boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof IDataObject)) {
			return false;
		}
		return id.equals(((IDataObject) obj).getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getName()
	 */
	@Override
	public final String getName() {
		return id;
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
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#toValue()
	 */
	@Override
	public Object toValue() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#
	 * getFunctionNames()
	 */
	@Override
	public String[] getFunctionNames() {
		return new String[] { "getType", //$NON-NLS-1$
				"isReadOnly", //$NON-NLS-1$
				"isEqualTo", //$NON-NLS-1$
				"isGreaterThan", //$NON-NLS-1$
				"isGreaterThanOrEqualTo", //$NON-NLS-1$
				"isLessThan", //$NON-NLS-1$
				"isLessThanOrEqualTo", //$NON-NLS-1$
				"isSecured", //$NON-NLS-1$
				"toString" }; //$NON-NLS-1$
	}

	@Override
	public String[] getPropertyNames() {
		return type.getFieldNames();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#invokeFunction(
	 * java.lang.String, java.lang.Object[])
	 */
	@Override
	public Object invokeFunction(String name, Object[] arguments)
			throws Exception {
		if ("getType".equals(name)) {
			return getType();
		}
		if ("isReadOnly".equals(name)) {
			return isReadOnly() ? Boolean.TRUE : Boolean.FALSE;
		}
		if ("isSecured".equals(name)) {
			return isSecured() ? Boolean.TRUE : Boolean.FALSE;
		}
		if (arguments.length > 0) {
			if ("isEqualTo".equals(name)) {
				return isEqualTo(arguments[0]) ? Boolean.TRUE : Boolean.FALSE;
			}
			if ("isGreaterThan".equals(name)) {
				return isGreaterThan(arguments[0]) ? Boolean.TRUE
						: Boolean.FALSE;
			}
			if ("isGreaterThanOrEqualTo".equals(name)) {
				return isGreaterThanOrEqualTo(arguments[0]) ? Boolean.TRUE
						: Boolean.FALSE;
			}
			if ("isLessThan".equals(name)) {
				return isLessThan(arguments[0]) ? Boolean.TRUE : Boolean.FALSE;
			}
			if ("isLessThanOrEqualTo".equals(name)) {
				return isLessThanOrEqualTo(arguments[0]) ? Boolean.TRUE
						: Boolean.FALSE;
			}
		}
		if ("toString".equals(name)) {
			return toString();
		}
		return null;
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
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasEntry(
	 * java.lang.String)
	 */
	@Override
	public boolean hasEntry(String name) {
		return getField(name) != null;
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
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getEntry(
	 * java.lang.String)
	 */
	@Override
	public Object getEntry(String name) {
		return getField(name);
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
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#setEntry(
	 * java.lang.String, java.lang.Object)
	 */
	@Override
	public boolean setEntry(String name, Object value) {
		if (value instanceof IDataObject) {
			return setField(name, (IDataObject) value);
		}
		IDataObject variable = getField(name);
		if (variable == null) {
			setField(
					name,
					variable = variableStorage.createVariable(type
							.getFieldType(name)));
		}
		if (variable instanceof IValueObject) {
			((IValueObject) variable).setValue(value);
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#clearItem(int)
	 */
	@Override
	public boolean clearItem(int index) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#clearEntry(
	 * java.lang.String)
	 */
	@Override
	public boolean clearEntry(String name) {
		return setField(name, null);
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	protected IVariableStorage getStorage() {
		return variableStorage;
	}
}