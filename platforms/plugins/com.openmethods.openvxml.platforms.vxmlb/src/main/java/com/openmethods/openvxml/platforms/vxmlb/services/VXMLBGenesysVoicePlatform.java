// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   VXMLBVoicePlatform.java

package com.openmethods.openvxml.platforms.vxmlb.services;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import org.eclipse.vtp.framework.interactions.voice.vxml.Parameter;
import org.eclipse.vtp.framework.interactions.voice.vxml.Script;
import org.eclipse.vtp.framework.interactions.voice.vxml.Submit;
import org.eclipse.vtp.framework.interactions.voice.vxml.VXMLDocument;
import org.eclipse.vtp.framework.interactions.voice.vxml.Variable;

import com.openmethods.openvxml.platforms.vxmlb.vxml.UserData;

public class VXMLBGenesysVoicePlatform extends VoicePlatform
{
	private IVariableRegistry variableRegistry;

    public VXMLBGenesysVoicePlatform(IExecutionContext context, IVariableRegistry variableRegistry)
    {
    	super(context);
    	this.variableRegistry = variableRegistry;
    }

	@Override
    protected VXMLDocument createVXMLDocument(ILinkFactory links, Dialog dialog)
    {
		VXMLDocument document = super.createVXMLDocument(links, dialog);
		document.setProperty("documentmaxage", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		document.setProperty("documentmaxstale", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		document.setProperty("com.openmethods.externalevents.enable", "true");
		return document;
    }

    public List<String> getPlatformVariableNames()
    {
        List<String> names = super.getPlatformVariableNames();
        names.add("genesysHeaders");
        return names;
    }
    
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.support.AbstractPlatform#
	 *      renderInitialDocument(
	 *      org.eclipse.vtp.framework.interactions.core.platforms.ILinkFactory,
	 *      org.eclipse.vtp.framework.interactions.core.commands.InitialCommand)
	 */
	protected IDocument renderInitialDocument(ILinkFactory links,
			InitialCommand initialCommand)
	{
		Form form = new Form("InitialForm"); //$NON-NLS-1$
		Map<String, String> varMap = new LinkedHashMap<String, String>();
		generateInitialVariableRequests(varMap);
		for (String key : varMap.keySet())
		{
			form.addVariable(new Variable(key, "''")); //$NON-NLS-1$
		}
		form.addVariable(new Variable("genesysHeaders", "''"));
		String[] variables = initialCommand.getVariableNames();
		for (int i = 0; i < variables.length; ++i)
		{
			String value = initialCommand.getVariableValue(variables[i]);
			if (value == null)
				value = ""; //$NON-NLS-1$
			form.addVariable(new Variable(variables[i], "'" + value + "'"));
		}
		Block block = new Block("InitialBlock"); //$NON-NLS-1$
		for (String key : varMap.keySet())
		{
			block.addAction(new Assignment(key, varMap.get(key)));
		}
		Script headerScript = new Script();
		headerScript.appendText("for(var h in session.connection.protocol.sip.rawheaders)\r\n");
		headerScript.appendText("{\r\n");
		headerScript.appendText("\tvar genesysHeadersTemp = new Array();\r\n");
		headerScript.appendText("\tif(h.startsWith('X-Genesys-'))\r\n");
		headerScript.appendText("\t{\r\n");
		headerScript.appendText("\t\tgenesysHeadersTemp.push(h.substring(10));\r\n");
		headerScript.appendText("\t}\r\n");
		headerScript.appendText("\tgenesysHeaders = JSON.stringify(genesysHeadersTemp);\r\n");
		headerScript.appendText("}\r\n");
		ILink nextLink = links.createNextLink();
		String[] parameterNames = initialCommand.getParameterNames();
		for (int i = 0; i < parameterNames.length; ++i)
			nextLink.setParameters(parameterNames[i], initialCommand
					.getParameterValues(parameterNames[i]));
		nextLink.setParameter(initialCommand.getResultName(), initialCommand
				.getResultValue());
		String[] fields = new String[varMap.size() + variables.length + 1];
		int j = 0;
		for (String key : varMap.keySet())
		{
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
		for (int i = 0; i < parameterNames.length; ++i)
			hangupLink.setParameters(parameterNames[i], initialCommand
					.getParameterValues(parameterNames[i]));
		hangupLink.setParameter(initialCommand.getResultName(),
				initialCommand.getHangupResultValue());
		Catch disconnectCatch = new Catch("connection.disconnect.hangup");
		disconnectCatch.addAction(new Goto(hangupLink.toString()));
		form.addEventHandler(disconnectCatch);
		VXMLDocument document = createVXMLDocument(links, form);
		document.setProperty("documentmaxage", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		document.setProperty("documentmaxstale", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		Script jsonInclude = new Script();
		jsonInclude.setSrc(links.createIncludeLink("com.openmethods.openvxml.platforms.genesys/includes/json.js").toString());
		document.addScript(jsonInclude);
		List<String> events = ExtendedActionEventManager.getDefault().getExtendedEvents();
		for(String event : events)
		{
			ILink eventLink = links.createNextLink();
			for (int i = 0; i < parameterNames.length; ++i)
				eventLink.setParameters(parameterNames[i], initialCommand
						.getParameterValues(parameterNames[i]));
			eventLink.setParameter(initialCommand.getResultName(), event);
			Catch eventCatch = new Catch(event);
			eventCatch.addAction(new Goto(eventLink.toString()));
			form.addEventHandler(eventCatch);
		}
		return document;
	}

	protected IDocument renderMetaDataMessage(ILinkFactory links, MetaDataMessageCommand metaDataMessageCommand)
    {
		Form form = new Form("SetAttachedDataForm");
		
		UserData userData = new UserData("SetAttachedData");
		String[] names = metaDataMessageCommand.getMetaDataNames();
		for(int i = 0; i < names.length; i++)
        {
	        userData.addParameter(new Parameter(names[i], "'"+metaDataMessageCommand.getMetaDataValue(names[i])+"'"));
        }
		Filled filled = new Filled();
		ILink createNextLink = links.createNextLink();
		createNextLink.setParameter(metaDataMessageCommand.getResultName(), metaDataMessageCommand.getFilledResultValue());
		String[] params = metaDataMessageCommand.getParameterNames();
		for(int i = 0; i < params.length; i++)
        {
			createNextLink.setParameters(params[i], metaDataMessageCommand.getParameterValues(params[i]));
        }
		filled.addAction(new Goto(createNextLink.toString()));
		userData.addFilledHandler(filled);
		Catch catchHandler = new Catch("");
		if(metaDataMessageCommand.isIgnoreErrors())
		{
			catchHandler.addAction(new Goto(createNextLink.toString()));
		}
		else
		{
			ILink errorLink = links.createNextLink();
			errorLink.setParameter(metaDataMessageCommand.getResultName(), "error");
			for(int i = 0; i < params.length; i++)
	        {
				errorLink.setParameters(params[i], metaDataMessageCommand.getParameterValues(params[i]));
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
}
