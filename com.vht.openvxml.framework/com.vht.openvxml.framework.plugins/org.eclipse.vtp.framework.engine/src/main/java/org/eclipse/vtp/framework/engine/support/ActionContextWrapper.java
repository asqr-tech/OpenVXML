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
package org.eclipse.vtp.framework.engine.support;

import java.util.Date;
import java.util.Dictionary;

import org.eclipse.vtp.framework.core.IActionContext;
import org.eclipse.vtp.framework.core.IActionResult;

/**
 * A wrapper for the {@link IActionContext} interface.
 * 
 * @author Lonnie Pryor
 */
public abstract class ActionContextWrapper implements IActionContext {
	/**
	 * Creates a new ActionContextWrapper.
	 */
	protected ActionContextWrapper() {
	}

	/**
	 * Returns true if the specified identifier is implemented by the context.
	 * 
	 * @param identifier
	 *            The identifier to check.
	 * @return True if the specified identifier is implemented by the context.
	 */
	protected boolean isReservedIdentifier(String identifier) {
		return AbstractActionContext.RESERVED_SERVICE_IDENTIFIERS
				.contains(identifier);
	}

	/**
	 * Returns the wrapped action context.
	 * 
	 * @return The wrapped action context.
	 */
	protected abstract IActionContext getActionContext();

	// IReporter Methods //

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.IReporter#isSeverityEnabled(int)
	 */
	@Override
	public boolean isSeverityEnabled(int severity) {
		return getActionContext().isSeverityEnabled(severity);
	}

	@Override
	public boolean isReportingEnabled() {
		return getActionContext().isReportingEnabled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IReporter#report(int,
	 * java.lang.String)
	 */
	@Override
	public void report(int severity, String message) {
		getActionContext().report(severity, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IReporter#report(int,
	 * java.lang.String, java.util.Dictionary)
	 */
	@Override
	public void report(int severity, String message, Dictionary properties) {
		getActionContext().report(severity, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IReporter#report(int,
	 * java.lang.String[], java.lang.String)
	 */
	@Override
	public void report(int severity, String[] categories, String message) {
		getActionContext().report(severity, categories, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IReporter#report(int,
	 * java.lang.String[], java.lang.String, java.util.Dictionary)
	 */
	@Override
	public void report(int severity, String[] categories, String message,
			Dictionary properties) {
		getActionContext().report(severity, categories, message, properties);
	}

	// ILogger Methods //

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#log(int, java.lang.String)
	 */
	@Override
	public void log(int severity, String message) {
		getActionContext().log(severity, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#log(int, java.lang.String,
	 * java.util.Dictionary)
	 */
	@Override
	public void log(int severity, String message, Dictionary properties) {
		getActionContext().log(severity, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#log(int, java.lang.String[],
	 * java.lang.String)
	 */
	@Override
	public void log(int severity, String[] categories, String message) {
		getActionContext().log(severity, categories, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#log(int, java.lang.String[],
	 * java.lang.String, java.util.Dictionary)
	 */
	@Override
	public void log(int severity, String[] categories, String message,
			Dictionary properties) {
		getActionContext().log(severity, categories, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#isErrorEnabled()
	 */
	@Override
	public boolean isErrorEnabled() {
		return getActionContext().isErrorEnabled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#error(java.lang.String)
	 */
	@Override
	public void error(String message) {
		getActionContext().error(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#error(java.lang.String,
	 * java.util.Dictionary)
	 */
	@Override
	public void error(String message, Dictionary properties) {
		getActionContext().error(message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#error(java.lang.String[],
	 * java.lang.String)
	 */
	@Override
	public void error(String[] categories, String message) {
		getActionContext().error(categories, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#error(java.lang.String[],
	 * java.lang.String, java.util.Dictionary)
	 */
	@Override
	public void error(String[] categories, String message, Dictionary properties) {
		getActionContext().error(categories, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#isWarnEnabled()
	 */
	@Override
	public boolean isWarnEnabled() {
		return getActionContext().isWarnEnabled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#warn(java.lang.String)
	 */
	@Override
	public void warn(String message) {
		getActionContext().warn(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#warn(java.lang.String,
	 * java.util.Dictionary)
	 */
	@Override
	public void warn(String message, Dictionary properties) {
		getActionContext().warn(message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#warn(java.lang.String[],
	 * java.lang.String)
	 */
	@Override
	public void warn(String[] categories, String message) {
		getActionContext().warn(categories, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#warn(java.lang.String[],
	 * java.lang.String, java.util.Dictionary)
	 */
	@Override
	public void warn(String[] categories, String message, Dictionary properties) {
		getActionContext().warn(categories, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#isInfoEnabled()
	 */
	@Override
	public boolean isInfoEnabled() {
		return getActionContext().isInfoEnabled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#info(java.lang.String)
	 */
	@Override
	public void info(String message) {
		getActionContext().info(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#info(java.lang.String,
	 * java.util.Dictionary)
	 */
	@Override
	public void info(String message, Dictionary properties) {
		getActionContext().info(message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#info(java.lang.String[],
	 * java.lang.String)
	 */
	@Override
	public void info(String[] categories, String message) {
		getActionContext().info(categories, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#info(java.lang.String[],
	 * java.lang.String, java.util.Dictionary)
	 */
	@Override
	public void info(String[] categories, String message, Dictionary properties) {
		getActionContext().info(categories, message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#isDebugEnabled()
	 */
	@Override
	public boolean isDebugEnabled() {
		return getActionContext().isDebugEnabled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#debug(java.lang.String)
	 */
	@Override
	public void debug(String message) {
		getActionContext().debug(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#debug(java.lang.String,
	 * java.util.Dictionary)
	 */
	@Override
	public void debug(String message, Dictionary properties) {
		getActionContext().debug(message, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#debug(java.lang.String[],
	 * java.lang.String)
	 */
	@Override
	public void debug(String[] categories, String message) {
		getActionContext().debug(categories, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ILogger#debug(java.lang.String[],
	 * java.lang.String, java.util.Dictionary)
	 */
	@Override
	public void debug(String[] categories, String message, Dictionary properties) {
		getActionContext().debug(categories, message, properties);
	}

	// IServiceRegistry Methods //

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IServiceRegistry#lookupService(
	 * java.lang.String)
	 */
	@Override
	public Object lookup(String identifier) throws NullPointerException {
		if (isReservedIdentifier(identifier)) {
			return this;
		}
		return getActionContext().lookup(identifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IServiceRegistry#lookupAllServices(
	 * java.lang.String)
	 */
	@Override
	public Object[] lookupAll(String identifier) throws NullPointerException {
		if (isReservedIdentifier(identifier)) {
			return new Object[] { this };
		}
		return getActionContext().lookupAll(identifier);
	}

	// IProcessContext Methods //

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IProcessContext#getProcessID()
	 */
	@Override
	public String getProcessID() {
		return getActionContext().getProcessID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IProcessContext#getProperty(
	 * java.lang.String)
	 */
	@Override
	public Object getProperty(String propertyName) throws NullPointerException {
		return getActionContext().getProperty(propertyName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IProcessContext#loadClass(
	 * java.lang.String)
	 */
	@Override
	public Class loadClass(String className) throws ClassNotFoundException,
			NullPointerException {
		return getActionContext().loadClass(className);
	}

	// ISessionContext Methods //

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#getSessionID()
	 */
	@Override
	public String getSessionID() {
		return getActionContext().getSessionID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#getSessionStartTime()
	 */
	@Override
	public Date getSessionStartTime() {
		return getActionContext().getSessionStartTime();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#getAttributeNames()
	 */
	@Override
	public String[] getAttributeNames() {
		return getActionContext().getAttributeNames();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#getAttribute(
	 * java.lang.String)
	 */
	@Override
	public Object getAttribute(String attributeName)
			throws NullPointerException {
		return getActionContext().getAttribute(attributeName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#setAttribute(
	 * java.lang.String, java.lang.Object)
	 */
	@Override
	public void setAttribute(String attributeName, Object attributeValue)
			throws NullPointerException {
		getActionContext().setAttribute(attributeName, attributeValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#clearAttribute(
	 * java.lang.String)
	 */
	@Override
	public void clearAttribute(String attributeName)
			throws NullPointerException {
		getActionContext().clearAttribute(attributeName);
	}

	@Override
	public Object getInheritedAttribute(String attributeName)
			throws NullPointerException {
		return getActionContext().getInheritedAttribute(attributeName);
	}

	// IExecutionContext Methods //

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IExecutionContext#getExecutionID()
	 */
	@Override
	public String getExecutionID() {
		return getActionContext().getExecutionID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IExecutionContext#getParameterNames()
	 */
	@Override
	public String[] getParameterNames() {
		return getActionContext().getParameterNames();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IExecutionContext#getParameter(
	 * java.lang.String)
	 */
	@Override
	public String getParameter(String parameterName)
			throws NullPointerException {
		return getActionContext().getParameter(parameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IExecutionContext#getParameters(
	 * java.lang.String)
	 */
	@Override
	public String[] getParameters(String parameterName)
			throws NullPointerException {
		return getActionContext().getParameters(parameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IExecutionContext#setParameter(
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void setParameter(String parameterName, String value)
			throws NullPointerException {
		getActionContext().setParameter(parameterName, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IExecutionContext#setParameters(
	 * java.lang.String, java.lang.String[])
	 */
	@Override
	public void setParameters(String parameterName, String[] values)
			throws NullPointerException {
		getActionContext().setParameters(parameterName, values);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IExecutionContext#clearParameter(
	 * java.lang.String)
	 */
	@Override
	public void clearParameter(String parameterName)
			throws NullPointerException {
		getActionContext().clearParameter(parameterName);
	}

	@Override
	public void clearParameters() {
		getActionContext().clearParameters();
	}

	// IActionContext Methods //

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IActionContext#getActionID()
	 */
	@Override
	public String getActionID() {
		return getActionContext().getActionID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IActionContext#getActionName()
	 */
	@Override
	public String getActionName() {
		return getActionContext().getActionName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IActionContext#getActionState()
	 */
	@Override
	public int getActionState() {
		return getActionContext().getActionState();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IActionContext#createResult(
	 * java.lang.String)
	 */
	@Override
	public IActionResult createResult(String resultName) {
		return getActionContext().createResult(resultName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IActionContext#createResult(
	 * java.lang.String, java.lang.Throwable)
	 */
	@Override
	public IActionResult createResult(String resultName, Throwable failureCause) {
		return getActionContext().createResult(resultName, failureCause);
	}

	@Override
	public String[] getRootAttributeNames() {
		return getActionContext().getRootAttributeNames();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#getAttribute(
	 * java.lang.String)
	 */
	@Override
	public Object getRootAttribute(String attributeName)
			throws NullPointerException {
		return getActionContext().getRootAttribute(attributeName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#setAttribute(
	 * java.lang.String, java.lang.Object)
	 */
	@Override
	public void setRootAttribute(String attributeName, Object attributeValue)
			throws NullPointerException {
		getActionContext().setRootAttribute(attributeName, attributeValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.ISessionContext#clearAttribute(
	 * java.lang.String)
	 */
	@Override
	public void clearRootAttribute(String attributeName)
			throws NullPointerException {
		getActionContext().clearRootAttribute(attributeName);
	}
}
