/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006-2007 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods), 
 *    Randy Childers (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.framework.interactions.core.actions;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.vtp.framework.common.IArrayObject;
import org.eclipse.vtp.framework.common.IBrandSelection;
import org.eclipse.vtp.framework.common.IDataObject;
import org.eclipse.vtp.framework.common.IMapObject;
import org.eclipse.vtp.framework.common.IStringObject;
import org.eclipse.vtp.framework.common.IVariableRegistry;
import org.eclipse.vtp.framework.core.IAction;
import org.eclipse.vtp.framework.core.IActionContext;
import org.eclipse.vtp.framework.core.IActionResult;
import org.eclipse.vtp.framework.core.IReporter;
import org.eclipse.vtp.framework.interactions.core.configurations.MetaDataRequestConfiguration;
import org.eclipse.vtp.framework.interactions.core.conversation.IConversation;
import org.eclipse.vtp.framework.interactions.core.platforms.IPlatformSelector;
import org.eclipse.vtp.framework.interactions.core.support.AbstractPlatform;

/**
 * An action that enqueues a meta-data request.
 * 
 * @author Lonnie Pryor
 */
/**
 * @author trip
 *
 */
/**
 * @author trip
 *
 */
public class MetaDataRequestAction implements IAction {
	/** The context to use. */
	private final IActionContext context;
	/** The conversation to use. */
	private final IConversation conversation;
	/** The configuration to use. */
	private final MetaDataRequestConfiguration configuration;
	/** The variable registry to use. */
	private final IVariableRegistry variables;
	/** The currently selected brand. */
	private final IBrandSelection brandSelection;
	private final IPlatformSelector platformSelector;

	/**
	 * Creates a new MetaDataMessageAction.
	 * 
	 * @param context
	 *            The context to use.
	 * @param conversation
	 *            The conversation to use.
	 * @param configuration
	 *            The configuration to use.
	 * @param variables
	 *            The variable registry to use.
	 */
	public MetaDataRequestAction(IActionContext context,
			IConversation conversation,
			MetaDataRequestConfiguration configuration,
			IVariableRegistry variables, IBrandSelection brandSelection,
			IPlatformSelector platformSelector) {
		this.context = context;
		this.conversation = conversation;
		this.configuration = configuration;
		this.variables = variables;
		this.brandSelection = brandSelection;
		this.platformSelector = platformSelector;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IAction#execute()
	 */
	@Override
	public IActionResult execute() {
		String resultParameterName = ACTION_PREFIX
				+ context.getActionID().replace(':', '_');
		try {
			if (context.isDebugEnabled()) {
				context.debug(getClass().getName().substring(
						getClass().getName().lastIndexOf('.') + 1));
			}
			String value = context.getParameter(resultParameterName);
			context.clearParameter(resultParameterName);
			if ("success.filled".equals(value)) {
				if (context.isReportingEnabled()) {
					Dictionary props = new Hashtable();
					props.put("event", "metadata.request.filled");
					context.report(IReporter.SEVERITY_INFO,
							"Received meta-data.", props);
				}

				/*
				 * IBrand brand = brandSelection.getSelectedBrand();
				 * MetaDataItemConfiguration[] items = null; for (; brand !=
				 * null && items == null; brand = brand.getParentBrand()) {
				 * items = configuration.getItem(brand.getId() + "" + ""); }
				 */

				AbstractPlatform platform = (AbstractPlatform) platformSelector
						.getSelectedPlatform();
				Map dataMap = platform.processMetaDataResponse(configuration,
						context);

				IMapObject outputMap;
				IDataObject ido = variables.getVariable(configuration
						.getOutput());
				if (ido instanceof IMapObject) {
					outputMap = (IMapObject) ido;
				} else {
					outputMap = (IMapObject) variables
							.createVariable(IMapObject.TYPE_NAME);
				}

				for (String keyName : configuration.getKvpMap().keySet()) {
					IStringObject so = (IStringObject) variables
							.createVariable(IStringObject.TYPE_NAME);
					so.setValue(dataMap.get(keyName));
					outputMap.setEntry(keyName, so);
				}
				variables.setVariable(configuration.getOutput(), outputMap);

				/*
				 * if (items != null) { for (int i = 0; i < items.length; ++i) {
				 * IDataObject object =
				 * variables.getVariable(items[i].getValue()); if (object ==
				 * null) variables.setVariable(items[i].getValue(), object =
				 * variables .createVariable(IStringObject.TYPE_NAME));
				 * IDataObject toSet = object; String primaryField =
				 * object.getType().getPrimaryFieldName(); if (primaryField !=
				 * null) toSet = object.getField(primaryField); if (toSet
				 * instanceof IBooleanObject)
				 * ((IBooleanObject)toSet).setValue(dataMap.get(items[i]
				 * .getName())); else if (toSet instanceof IDateObject)
				 * ((IDateObject)toSet).setValue(dataMap.get(items[i]
				 * .getName())); else if (toSet instanceof IDecimalObject)
				 * ((IDecimalObject)toSet).setValue(dataMap.get(items[i]
				 * .getName())); else if (toSet instanceof INumberObject)
				 * ((INumberObject)toSet).setValue(dataMap.get(items[i]
				 * .getName())); else if (toSet instanceof IStringObject)
				 * ((IStringObject)toSet).setValue(dataMap.get(items[i]
				 * .getName())); } }
				 */

				return context.createResult(IActionResult.RESULT_NAME_DEFAULT);
			} else if (IConversation.RESULT_NAME_HANGUP.equals(value)) {
				if (context.isReportingEnabled()) {
					Dictionary props = new Hashtable();
					props.put("event", "error.disconnect.hangup");
					context.report(IReporter.SEVERITY_INFO,
							"Got disconnect during interaction.", props);
				}
				return context.createResult(IConversation.RESULT_NAME_HANGUP);
			} else if (value != null) {
				return context.createResult(value);
			} else {
				if (context.isReportingEnabled()) {
					Dictionary props = new Hashtable();
					props.put("event", "metadata.request.before");
					context.report(IReporter.SEVERITY_INFO,
							"Requesting meta-data.", props);
				}

				Map<String, IDataObject> kvpMap = new HashMap<String, IDataObject>();
				IArrayObject inputStringObjectArray;
				IDataObject ido = variables.getVariable(configuration
						.getInput());
				if (ido instanceof IArrayObject) {
					inputStringObjectArray = (IArrayObject) ido;
				} else {
					inputStringObjectArray = (IArrayObject) variables
							.createVariable(IArrayObject.TYPE_NAME);
				}

				for (int b = 0; b < inputStringObjectArray.getLength()
						.getValue(); b++) {
					String keyName = inputStringObjectArray.getElement(b)
							.toString();
					kvpMap.put(keyName, null);
				}
				configuration.setKvpMap(kvpMap);

				if (conversation.createMetaDataRequest(configuration,
						resultParameterName).enqueue()) {
					return context
							.createResult(IActionResult.RESULT_NAME_REPEAT);
				}
			}
		} catch (Exception e) {
			return context.createResult("error.meta-data.request", e); //$NON-NLS-1$
		}

		return context.createResult("error.meta-data.request"); //$NON-NLS-1$
	}
}
