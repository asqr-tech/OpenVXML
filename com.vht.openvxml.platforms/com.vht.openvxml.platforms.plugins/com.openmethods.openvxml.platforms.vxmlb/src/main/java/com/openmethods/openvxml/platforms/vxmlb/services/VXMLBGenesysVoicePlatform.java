// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   VXMLBVoicePlatform.java

package com.openmethods.openvxml.platforms.vxmlb.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.vtp.framework.common.IVariableRegistry;
import org.eclipse.vtp.framework.core.IExecutionContext;
import org.eclipse.vtp.framework.interactions.core.commands.InitialCommand;
import org.eclipse.vtp.framework.interactions.core.commands.MetaDataMessageCommand;
import org.eclipse.vtp.framework.interactions.core.platforms.IDocument;
import org.eclipse.vtp.framework.interactions.core.platforms.ILink;
import org.eclipse.vtp.framework.interactions.core.platforms.ILinkFactory;
import org.eclipse.vtp.framework.interactions.core.services.ExtendedActionEventManager;
import org.eclipse.vtp.framework.interactions.voice.services.VoicePlatform;
import org.eclipse.vtp.framework.interactions.voice.vxml.Assignment;
import org.eclipse.vtp.framework.interactions.voice.vxml.Block;
import org.eclipse.vtp.framework.interactions.voice.vxml.Catch;
import org.eclipse.vtp.framework.interactions.voice.vxml.Dialog;
import org.eclipse.vtp.framework.interactions.voice.vxml.Filled;
import org.eclipse.vtp.framework.interactions.voice.vxml.Form;
import org.eclipse.vtp.framework.interactions.voice.vxml.Goto;
import org.eclipse.vtp.framework.interactions.voice.vxml.If;
import org.eclipse.vtp.framework.interactions.voice.vxml.Parameter;
import org.eclipse.vtp.framework.interactions.voice.vxml.Script;
import org.eclipse.vtp.framework.interactions.voice.vxml.Submit;
import org.eclipse.vtp.framework.interactions.voice.vxml.VXMLDocument;
import org.eclipse.vtp.framework.interactions.voice.vxml.Variable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.openmethods.openvxml.platforms.vxmlb.vxml.UserData;

public class VXMLBGenesysVoicePlatform extends VoicePlatform {
	private static final ObjectMapper mapper = new ObjectMapper();
	private IVariableRegistry variableRegistry;

	public VXMLBGenesysVoicePlatform(IExecutionContext context,
			IVariableRegistry variableRegistry) {
		super(context);
		this.variableRegistry = variableRegistry;
	}

	@Override
	protected VXMLDocument createVXMLDocument(ILinkFactory links, Dialog dialog) {
		VXMLDocument document = super.createVXMLDocument(links, dialog);
		document.setProperty("documentmaxage", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		document.setProperty("documentmaxstale", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		document.setProperty("com.openmethods.externalevents.enable", "true");
		return document;
	}

	@Override
	public void generateInitialVariableRequests(Map variables) {
		super.generateInitialVariableRequests(variables);
		variables
				.put("genesysUUID",
						"session.connection.protocol.sip.rawheaders['X-Genesys-CallUUID']");
	}

	@Override
	public List<String> getPlatformVariableNames() {
		List<String> names = super.getPlatformVariableNames();
		names.add("genesysHeaders");
		names.add("genesysUUID");
		return names;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.framework.interactions.core.support.AbstractPlatform#
	 * postProcessInitialVariable(java.lang.String, java.lang.String)
	 */
	@Override
	public String postProcessInitialVariable(String name, String originalValue) {
		if (name.equals("genesysHeaders")) {
			try {
				ObjectNode copy = mapper.createObjectNode();
				ObjectNode root = (ObjectNode) mapper.readTree(originalValue);
				Iterator<Map.Entry<String, JsonNode>> it = root.fields();
				while (it.hasNext()) {
					Map.Entry<String, JsonNode> entry = it.next();
					String key = entry.getKey();
					JsonNode value = entry.getValue();
					if (key.startsWith("X-Genesys-")) {
						copy.put(key.substring(10), value);
					}
				}
				return copy.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return super.postProcessInitialVariable(name, originalValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.framework.interactions.core.support.AbstractPlatform#
	 * renderInitialDocument(
	 * org.eclipse.vtp.framework.interactions.core.platforms.ILinkFactory,
	 * org.eclipse.vtp.framework.interactions.core.commands.InitialCommand)
	 */
	@Override
	protected IDocument renderInitialDocument(ILinkFactory links,
			InitialCommand initialCommand) {
		Form form = new Form("InitialForm"); //$NON-NLS-1$
		Map<String, String> varMap = new LinkedHashMap<String, String>();
		generateInitialVariableRequests(varMap);
		for (String key : varMap.keySet()) {
			form.addVariable(new Variable(key, "''")); //$NON-NLS-1$
		}
		form.addVariable(new Variable("genesysHeaders", "''"));
		String[] variables = initialCommand.getVariableNames();
		for (String variable : variables) {
			String value = initialCommand.getVariableValue(variable);
			if (value == null) {
				value = ""; //$NON-NLS-1$
			}
			form.addVariable(new Variable(variable, "'" + value + "'"));
		}
		Block block = new Block("InitialBlock"); //$NON-NLS-1$
		for (String key : varMap.keySet()) {
			block.addAction(new Assignment(key, varMap.get(key)));
		}
		Script headerScript = new Script();
		// headerScript.appendText("var genesysHeadersTemp = new Object();\r\n");
		// headerScript.appendText("for(var h in session.connection.protocol.sip.rawheaders)\r\n");
		// headerScript.appendText("{\r\n");
		// headerScript.appendText("\tif(h.substring(0,10) == 'X-Genesys-')\r\n");
		// headerScript.appendText("\t{\r\n");
		// headerScript.appendText("\t\tgenesysHeadersTemp[h.substring(10)] = session.connection.protocol.sip.rawheaders[h];\r\n");
		// headerScript.appendText("\t}\r\n");
		// headerScript.appendText("}\r\n");
		headerScript
				.appendText("genesysHeaders = JSON.stringify(session.connection.protocol.sip.rawheaders);\r\n");
		block.addAction(headerScript);
		ILink nextLink = links.createNextLink();
		String[] parameterNames = initialCommand.getParameterNames();
		for (String parameterName : parameterNames) {
			nextLink.setParameters(parameterName,
					initialCommand.getParameterValues(parameterName));
		}
		nextLink.setParameter(initialCommand.getResultName(),
				initialCommand.getResultValue());
		String[] fields = new String[varMap.size() + variables.length + 1];
		int j = 0;
		for (String key : varMap.keySet()) {
			fields[j] = key;
			++j;
		}
		System.arraycopy(variables, 0, fields, varMap.size(), variables.length);
		fields[fields.length - 1] = "genesysHeaders";
		Submit submit = new Submit(nextLink.toString(), fields);
		submit.setMethod("post");
		block.addAction(submit);
		form.addFormElement(block);
		ILink hangupLink = links.createNextLink();
		for (String parameterName : parameterNames) {
			hangupLink.setParameters(parameterName,
					initialCommand.getParameterValues(parameterName));
		}
		hangupLink.setParameter(initialCommand.getResultName(),
				initialCommand.getHangupResultValue());
		Catch disconnectCatch = new Catch("connection.disconnect.hangup");
		disconnectCatch.addAction(new Goto(hangupLink.toString()));
		form.addEventHandler(disconnectCatch);
		VXMLDocument document = createVXMLDocument(links, form);
		document.setProperty("documentmaxage", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		document.setProperty("documentmaxstale", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		Script jsonInclude = new Script();
		jsonInclude.setSrc(links.createIncludeLink(
				"com.openmethods.openvxml.platforms.genesys/includes/json.js")
				.toString());
		document.addScript(jsonInclude);

		Map<String, String[]> parameterMap = new HashMap<String, String[]>();
		for (String parameterName : parameterNames) {
			parameterMap.put(parameterName,
					initialCommand.getParameterValues(parameterName));
		}
		form = (Form) addExtendedEvents(links, initialCommand.getResultName(),
				parameterMap, form);
		// List<String> events =
		// ExtendedActionEventManager.getDefault().getExtendedEvents();
		// for(String event : events)
		// {
		// ILink eventLink = links.createNextLink();
		// for (int i = 0; i < parameterNames.length; ++i)
		// eventLink.setParameters(parameterNames[i], initialCommand
		// .getParameterValues(parameterNames[i]));
		// eventLink.setParameter(initialCommand.getResultName(), event);
		// Catch eventCatch = new Catch(event);
		// eventCatch.addAction(new Goto(eventLink.toString()));
		// form.addEventHandler(eventCatch);
		// }
		return document;
	}

	@Override
	protected IDocument renderMetaDataMessage(ILinkFactory links,
			MetaDataMessageCommand metaDataMessageCommand) {
		Form form = new Form("SetAttachedDataForm");

		UserData userData = new UserData("SetAttachedData");
		String[] names = metaDataMessageCommand.getMetaDataNames();
		for (String name : names) {
			userData.addParameter(new Parameter(name, "'"
					+ metaDataMessageCommand.getMetaDataValue(name) + "'"));
		}
		Filled filled = new Filled();
		ILink createNextLink = links.createNextLink();
		createNextLink.setParameter(metaDataMessageCommand.getResultName(),
				metaDataMessageCommand.getFilledResultValue());
		String[] params = metaDataMessageCommand.getParameterNames();
		for (String param : params) {
			createNextLink.setParameters(param,
					metaDataMessageCommand.getParameterValues(param));
		}
		filled.addAction(new Goto(createNextLink.toString()));
		userData.addFilledHandler(filled);
		Catch catchHandler = new Catch("");
		if (metaDataMessageCommand.isIgnoreErrors()) {
			catchHandler.addAction(new Goto(createNextLink.toString()));
		} else {
			ILink errorLink = links.createNextLink();
			errorLink.setParameter(metaDataMessageCommand.getResultName(),
					"error");
			for (String param : params) {
				errorLink.setParameters(param,
						metaDataMessageCommand.getParameterValues(param));
			}
			catchHandler.addAction(new Goto(errorLink.toString()));
		}
		userData.addEventHandler(catchHandler);
		form.addFormElement(userData);
		ILink hangupLink = links.createNextLink();
		hangupLink.setParameter(metaDataMessageCommand.getResultName(),
				metaDataMessageCommand.getHangupResultValue());
		Catch disconnectCatch = new Catch("connection.disconnect.hangup");
		disconnectCatch.addAction(new Goto(hangupLink.toString()));
		form.addEventHandler(disconnectCatch);
		return this.createVXMLDocument(links, form);
	}

	private Dialog addExtendedEvents(ILinkFactory links, String resultName,
			Map<String, String[]> parameterMap, Dialog form) {
		List<String> events = ExtendedActionEventManager.getDefault()
				.getExtendedEvents();
		String cpaPrefix = "externalmessage.cpa";
		// if(events.contains(cpaPrefix))
		if (false) {
			List<String> cpaEvents = new ArrayList<String>();
			for (String event : events) {
				if (event.startsWith(cpaPrefix)) {
					cpaEvents.add(event);
				} else {
					ILink eventLink = links.createNextLink();
					eventLink.setParameter(resultName, event);
					Catch eventCatch = new Catch(event);
					eventCatch.addAction(new Goto(eventLink.toString()));
					form.addEventHandler(eventCatch);
				}
			}
			// cpa events
			Catch cpaCatch = new Catch(cpaPrefix);

			for (String cpaEvent : cpaEvents) {
				if (!cpaPrefix.equals(cpaEvent)) {
					ILink eventLink = links.createNextLink();
					if (null != parameterMap) {
						for (Entry<String, String[]> entry : parameterMap
								.entrySet()) {
							eventLink.setParameters(entry.getKey(),
									entry.getValue());
						}
					}
					eventLink.setParameter(resultName, cpaEvent);
					// If eventIf = new If("_event==Õ" + cpaEvent + "Õ");
					If eventIf = new If("_event=='" + cpaEvent + "'");
					eventIf.addAction(new Goto(eventLink.toString()));
					cpaCatch.addIfClause(eventIf);
				}
			}
			ILink cpaLink = links.createNextLink();
			if (null != parameterMap) {
				for (Entry<String, String[]> entry : parameterMap.entrySet()) {
					cpaLink.setParameters(entry.getKey(), entry.getValue());
				}
			}
			cpaLink.setParameter(resultName, cpaPrefix);
			cpaCatch.addAction(new Goto(cpaLink.toString()));
			form.addEventHandler(cpaCatch);
		} else {
			for (String event : events) {
				ILink eventLink = links.createNextLink();
				if (null != parameterMap) {
					for (Entry<String, String[]> entry : parameterMap
							.entrySet()) {
						eventLink.setParameters(entry.getKey(),
								entry.getValue());
					}
				}
				eventLink.setParameter(resultName, event);
				Catch eventCatch = new Catch(event);
				eventCatch.addAction(new Goto(eventLink.toString()));
				form.addEventHandler(eventCatch);
			}
		}
		return form;
	}
}
