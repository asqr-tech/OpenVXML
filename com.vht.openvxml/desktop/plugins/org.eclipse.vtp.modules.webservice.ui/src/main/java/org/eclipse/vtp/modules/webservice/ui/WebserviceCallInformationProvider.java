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
package org.eclipse.vtp.modules.webservice.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.elements.core.PrimitiveInformationProvider;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.eclipse.vtp.modules.webservice.ui.configuration.WebserviceBindingManager;

import com.openmethods.openvxml.desktop.model.businessobjects.FieldType;
import com.openmethods.openvxml.desktop.model.businessobjects.IBusinessObject;
import com.openmethods.openvxml.desktop.model.businessobjects.IBusinessObjectProjectAspect;
import com.openmethods.openvxml.desktop.model.businessobjects.IBusinessObjectSet;
import com.openmethods.openvxml.desktop.model.businessobjects.internal.BusinessObject;
import com.openmethods.openvxml.desktop.model.workflow.design.IDesignElement;
import com.openmethods.openvxml.desktop.model.workflow.design.IDesignElementConnectionPoint;
import com.openmethods.openvxml.desktop.model.workflow.design.Variable;
import com.openmethods.openvxml.desktop.model.workflow.internal.VariableHelper;
import com.openmethods.openvxml.desktop.model.workflow.internal.design.ConnectorRecord;

public class WebserviceCallInformationProvider extends PrimitiveInformationProvider
{
	List<ConnectorRecord> connectorRecords = new ArrayList<ConnectorRecord>();
	private IBusinessObjectSet businessObjectSet = null;
	
	public WebserviceCallInformationProvider(PrimitiveElement element)
	{
		super(element);
		connectorRecords.add(new ConnectorRecord(element, "Continue", IDesignElementConnectionPoint.ConnectionPointType.EXIT_POINT));
		connectorRecords.add(new ConnectorRecord(element, "error.webservice", IDesignElementConnectionPoint.ConnectionPointType.ERROR_POINT));
	}

	public boolean acceptsConnector(IDesignElement origin)
	{
		return true;
	}

	public ConnectorRecord getConnectorRecord(String recordName)
	{
		for(int i = 0; i < connectorRecords.size(); i++)
		{
			ConnectorRecord cr = connectorRecords.get(i);
			if(cr.getName().equals(recordName))
				return cr;
		}
		return null;
	}

	public List<ConnectorRecord> getConnectorRecords()
	{
		return Collections.unmodifiableList(connectorRecords);
	}

	public List<ConnectorRecord> getConnectorRecords(IDesignElementConnectionPoint.ConnectionPointType... types)
	{
		List<ConnectorRecord> ret = new ArrayList<ConnectorRecord>();
		for(int i = 0; i < connectorRecords.size(); i++)
		{
			ConnectorRecord cr = connectorRecords.get(i);
			if(cr.getType().isSet(IDesignElementConnectionPoint.ConnectionPointType.getFlagSet(types)))
				ret.add(cr);
		}
		return ret;
	}

	public void readConfiguration(org.w3c.dom.Element configuration)
	{
		IOpenVXMLProject project = getElement().getDesign().getDocument().getProject();
		IBusinessObjectProjectAspect businessObjectAspect = (IBusinessObjectProjectAspect)project.getProjectAspect(IBusinessObjectProjectAspect.ASPECT_ID);
		businessObjectSet = businessObjectAspect.getBusinessObjectSet();
	}

	public void writeConfiguration(org.w3c.dom.Element configuration)
	{
	}

/*	public List getPropertiesPanels()
	{
		WebServiceCallSettingsStructure copy =
			(WebServiceCallSettingsStructure)settings.clone();
		List ret = new ArrayList();
		ret.add(new WebServiceCallTargetVariablePropertiesPanel(getElement(), copy));
		ret.add(new WebServiceCallServicePropertiesPanel(getElement(), copy));
		ret.add(new WebServiceCallInputPropertiesPanel(getElement(), copy));
		ret.add(new WebServiceCallOutputPropertiesPanel(getElement(), copy));

		return ret;
	}
*/	
	public List<Variable> getOutgoingVariables(String exitPoint, boolean localOnly)
    {
		List<Variable> ret = new ArrayList<Variable>();
		if(exitPoint.equals("Continue"))
		{
			WebserviceBindingManager manager = (WebserviceBindingManager)this.getElement().getConfigurationManager(WebserviceBindingManager.TYPE_ID);
			Variable v = VariableHelper.constructVariable(manager.getOutputBinding().getVariableName(), businessObjectSet, new FieldType(businessObjectSet.getBusinessObject("WSResponse")));
			this.getElement().rollbackConfigurationChanges(manager);
			ret.add(v);
		}
		return ret;
    }

	public boolean hasConnectors()
    {
	    return true;
    }

	@Override
	public void declareBusinessObjects()
	{
		System.out.println("creating bos");
		boolean foundWSHeader = false;
		boolean foundWSResponse = false;
		List<IBusinessObject> currentObjects = businessObjectSet.getBusinessObjects();
		for(IBusinessObject businessObject : currentObjects)
		{
			if(businessObject.getName().equals("WSHeader"))
			{
				foundWSHeader = true;
			}
			else if(businessObject.getName().equals("WSResponse"))
			{
				foundWSResponse = true;
			}
		}
		if(!foundWSHeader)
		{
			try
            {
	            IBusinessObject wsHeader = businessObjectSet.createBusinessObject("WSHeader");
    			((BusinessObject)wsHeader).write(getClass().getResourceAsStream("/WSHeader.dod"));
            }
            catch(Exception e)
            {
	            e.printStackTrace();
            }
		}
		if(!foundWSResponse)
		{
			try
            {
	            IBusinessObject wsResponse = businessObjectSet.createBusinessObject("WSResponse");
    			((BusinessObject)wsResponse).write(getClass().getResourceAsStream("/WSResponse.dod"));
            }
            catch(Exception e)
            {
	            e.printStackTrace();
            }
		}
	}

}
