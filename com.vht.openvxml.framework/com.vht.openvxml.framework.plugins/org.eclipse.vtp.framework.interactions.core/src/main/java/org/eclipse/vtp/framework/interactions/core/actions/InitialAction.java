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
package org.eclipse.vtp.framework.interactions.core.actions;

import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.vtp.framework.common.IBooleanObject;
import org.eclipse.vtp.framework.common.IBrand;
import org.eclipse.vtp.framework.common.IBrandRegistry;
import org.eclipse.vtp.framework.common.IBrandSelection;
import org.eclipse.vtp.framework.common.IDataObject;
import org.eclipse.vtp.framework.common.IDataTypeRegistry;
import org.eclipse.vtp.framework.common.IDateObject;
import org.eclipse.vtp.framework.common.IDecimalObject;
import org.eclipse.vtp.framework.common.IMapObject;
import org.eclipse.vtp.framework.common.INumberObject;
import org.eclipse.vtp.framework.common.IStringObject;
import org.eclipse.vtp.framework.common.IVariableRegistry;
import org.eclipse.vtp.framework.common.actions.AssignmentAction;
import org.eclipse.vtp.framework.common.configurations.AssignmentConfiguration;
import org.eclipse.vtp.framework.common.configurations.InitialConfiguration;
import org.eclipse.vtp.framework.core.IActionContext;
import org.eclipse.vtp.framework.core.IActionResult;
import org.eclipse.vtp.framework.core.IReporter;
import org.eclipse.vtp.framework.interactions.core.ILanguageRegistry;
import org.eclipse.vtp.framework.interactions.core.ILanguageSelection;
import org.eclipse.vtp.framework.interactions.core.conversation.IConversation;
import org.eclipse.vtp.framework.interactions.core.platforms.IPlatformSelector;
import org.eclipse.vtp.framework.interactions.core.support.AbstractPlatform;

/**
 * InitialAction.
 * 
 * @author Lonnie Pryor
 */
public class InitialAction extends AssignmentAction {
	private static final Set INITAL_TYPES = Collections
			.unmodifiableSet(new HashSet(Arrays.asList(new String[] {
					IBooleanObject.TYPE_NAME, IDateObject.TYPE_NAME,
					IDecimalObject.TYPE_NAME, INumberObject.TYPE_NAME,
					IStringObject.TYPE_NAME })));

	/** The conversation to use. */
	private final IConversation conversation;
	/** The brand to use. */
	private final IBrandSelection brandSelection;
	private final IDataTypeRegistry dataTypeRegistry;
	private final IPlatformSelector platformSelector;
	private final InitialConfiguration initialConfig;
	private final IBrandRegistry brandRegistry;
	private final ILanguageSelection languageSelection;
	private final ILanguageRegistry languageRegistry;

	/**
	 * Creates a new InitialAction.
	 * 
	 * @param context
	 * @param variableRegistry
	 * @param assignConfigs
	 * @param conversation
	 *            The conversation to use.
	 */
	public InitialAction(IActionContext context,
			IVariableRegistry variableRegistry,
			IDataTypeRegistry dataTypeRegistry,
			AssignmentConfiguration[] assignConfigs,
			IConversation conversation, IBrandSelection brandSelection,
			IPlatformSelector platformSelector,
			InitialConfiguration initialConfig, IBrandRegistry brandRegistry,
			ILanguageSelection languageSelection,
			ILanguageRegistry languageRegistry) {
		super(context, variableRegistry, assignConfigs);
		this.conversation = conversation;
		this.brandSelection = brandSelection;
		this.dataTypeRegistry = dataTypeRegistry;
		this.platformSelector = platformSelector;
		this.initialConfig = initialConfig;
		this.brandRegistry = brandRegistry;
		this.languageSelection = languageSelection;
		this.languageRegistry = languageRegistry;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.common.actions.AssignmentAction#execute()
	 */
	@Override
	public IActionResult execute() {
		if (variableRegistry.getVariable("Platform") != null) {
			context.info("Skipping initial action as Platform variable already exists");
			return execute(Collections.<String, String> emptyMap(), false);
		}
		String resultParameterName = ACTION_PREFIX
				+ context.getActionID().replace(':', '_');
		String result = context.getParameter(resultParameterName);
		if (IConversation.RESULT_NAME_FILLED.equals(result)) {
			if (context.isReportingEnabled()) {
				Dictionary<String, Object> props = new Hashtable<String, Object>();
				props.put("event", "initial.after");
				context.report(IReporter.SEVERITY_INFO,
						"Processing initial variables.", props);
			}
			IDataObject platform = variableRegistry
					.createVariable(IMapObject.TYPE_NAME);
			String anivalue = context.getParameter("PLATFORM_ANI");
			if (anivalue != null) {
				if (context.isReportingEnabled()) {
					Dictionary props2 = new Hashtable();
					props2.put("event", "assignment");
					props2.put("event.key", "Platform.ANI");
					props2.put("event.value", String.valueOf(anivalue));
					context.report(IReporter.SEVERITY_INFO,
							"Assigned variable \"Platform.ANI\" to \""
									+ anivalue + "\"", props2);
				}
				IStringObject aniVar = (IStringObject) variableRegistry
						.createVariable(IStringObject.TYPE_NAME);
				aniVar.setValue(anivalue);
				platform.setEntry("ANI", aniVar);
				platform.setEntry("PLATFORM_ANI", aniVar);
			}
			String dnisvalue = context.getParameter("PLATFORM_DNIS");
			if (dnisvalue != null) {
				if (context.isReportingEnabled()) {
					Dictionary props2 = new Hashtable();
					props2.put("event", "assignment");
					props2.put("event.key", "Platform.DNIS");
					props2.put("event.value", String.valueOf(dnisvalue));
					context.report(IReporter.SEVERITY_INFO,
							"Assigned variable \"Platform.DNIS\" to \""
									+ dnisvalue + "\"", props2);
				}
				IStringObject dnisVar = (IStringObject) variableRegistry
						.createVariable(IStringObject.TYPE_NAME);
				dnisVar.setValue(dnisvalue);
				platform.setEntry("DNIS", dnisVar);
				platform.setEntry("PLATFORM_DNIS", dnisVar);
			}
			IStringObject brandVar = (IStringObject) variableRegistry
					.createVariable(IStringObject.TYPE_NAME);
			IBrand b = brandSelection.getSelectedBrand();
			if (b != null) {
				brandVar.setValue(b.getPath());
			} else {
				b = brandRegistry.getBrandById(initialConfig
						.getDefaultBrandId());
				if (b != null) {
					brandVar.setValue(b.getPath());
				} else {
					brandVar.setValue(brandRegistry.getDefaultBrand().getPath());
				}
			}
			platform.setEntry("Brand", brandVar);
			languageSelection.setDefaultLanguage(initialConfig
					.getDefaultLanguageName());
			variableRegistry.setVariable("Platform", platform);
			Map values = new HashMap();
			for (AssignmentConfiguration configuration : configurations) {
				String value = context.getParameter(configuration.getName());
				if (value != null && value.length() > 0) {
					values.put(configuration.getName(), value);
				}
			}
			// platform extension variables
			AbstractPlatform abstractPlatform = (AbstractPlatform) platformSelector
					.getSelectedPlatform();
			List<String> incomingParametersNames = abstractPlatform
					.getPlatformVariableNames();
			context.info("Platform Variables");
			for (String vname : incomingParametersNames) {
				context.info("\t" + vname);
			}
			if (incomingParametersNames.size() > 0) {
				IMapObject initialParameters = (IMapObject) variableRegistry
						.createVariable(IMapObject.TYPE_NAME);
				for (int i = 0; i < incomingParametersNames.size(); i++) {
					incomingParametersNames.set(
							i,
							incomingParametersNames.get(i).replaceAll(
									Pattern.quote("."), "_"));
					incomingParametersNames
							.set(i,
									incomingParametersNames.get(i).replaceAll(
											"-", "_"));
				}
				for (int i = 0; i < incomingParametersNames.size(); i++) {
					String parameter = abstractPlatform
							.postProcessInitialVariable(incomingParametersNames
									.get(i), context
									.getParameter(incomingParametersNames
											.get(i)));
					context.info("\t"
							+ incomingParametersNames.get(i)
							+ " = "
							+ parameter
							+ " ["
							+ context.getParameter(incomingParametersNames
									.get(i)) + "]");
					IStringObject field = (IStringObject) variableRegistry
							.createVariable(IStringObject.TYPE_NAME);
					field.setValue(parameter == null ? "" : parameter);
					initialParameters.setEntry(incomingParametersNames.get(i),
							field);
				}
				variableRegistry.setVariable("PlatformVariables",
						initialParameters);
			} else {
				context.info("\tNone");
			}
			return execute(values, false);
		} else if (IConversation.RESULT_NAME_HANGUP.equals(result)) {
			if (context.isReportingEnabled()) {
				Dictionary<String, Object> props = new Hashtable<String, Object>();
				props.put("event", "error.disconnect.hangup");
				context.report(IReporter.SEVERITY_INFO,
						"Got disconnect during interaction.", props);
			}
			return context.createResult(IConversation.RESULT_NAME_HANGUP);
		} else if (result != null) {
			return context.createResult(result);
		} else {
			try {
				String[] incomingParametersNames = context.getParameterNames();
				for (int i = 0; i < incomingParametersNames.length; i++) {
					incomingParametersNames[i] = incomingParametersNames[i]
							.replaceAll(Pattern.quote("."), "_");
					incomingParametersNames[i] = incomingParametersNames[i]
							.replaceAll("-", "_");
				}
				IMapObject initialParameters = (IMapObject) variableRegistry
						.createVariable(IMapObject.TYPE_NAME);
				for (String incomingParametersName : incomingParametersNames) {
					String parameter = context
							.getParameter(incomingParametersName);
					IStringObject field = (IStringObject) variableRegistry
							.createVariable(IStringObject.TYPE_NAME);
					field.setValue(parameter == null ? "" : parameter);
					initialParameters.setEntry(incomingParametersName, field);
				}
				variableRegistry.setVariable("InitialParameters",
						initialParameters);
				if (context.isReportingEnabled()) {
					Dictionary<String, Object> props = new Hashtable<String, Object>();
					props.put("event", "initial.before");
					context.report(IReporter.SEVERITY_INFO,
							"Requesting initial variables.", props);
				}
				Map vars = new LinkedHashMap();
				if ("true".equals(context.getAttribute("subdialog"))) {
					for (AssignmentConfiguration configuration : configurations) {
						if (INITAL_TYPES.contains(configuration.getType())) {
							vars.put(configuration.getName(),
									configuration.getValue());
						}
					}
				}
				conversation.createInitial(resultParameterName, vars).enqueue();
			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			return context.createResult(IActionResult.RESULT_NAME_REPEAT);
		}
	}
}
