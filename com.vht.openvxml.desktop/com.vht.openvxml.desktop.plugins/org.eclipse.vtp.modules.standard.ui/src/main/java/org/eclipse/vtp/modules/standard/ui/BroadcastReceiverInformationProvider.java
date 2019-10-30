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
package org.eclipse.vtp.modules.standard.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.elements.core.PrimitiveBroadcastReceiverProvider;
import org.eclipse.vtp.desktop.model.elements.core.PrimitiveInformationProvider;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.w3c.dom.NodeList;

import com.openmethods.openvxml.desktop.model.businessobjects.FieldType;
import com.openmethods.openvxml.desktop.model.businessobjects.IBusinessObjectProjectAspect;
import com.openmethods.openvxml.desktop.model.businessobjects.IBusinessObjectSet;
import com.openmethods.openvxml.desktop.model.workflow.design.IDesignElement;
import com.openmethods.openvxml.desktop.model.workflow.design.IDesignElementConnectionPoint;
import com.openmethods.openvxml.desktop.model.workflow.design.IExitBroadcastReceiver;
import com.openmethods.openvxml.desktop.model.workflow.design.Variable;
import com.openmethods.openvxml.desktop.model.workflow.internal.VariableHelper;
import com.openmethods.openvxml.desktop.model.workflow.internal.design.ConnectorRecord;
import com.openmethods.openvxml.desktop.model.workflow.internal.design.ExitBroadcastReceiver;

public class BroadcastReceiverInformationProvider extends PrimitiveInformationProvider implements PrimitiveBroadcastReceiverProvider
{
	private IBusinessObjectSet businessObjectSet = null;
	List<ConnectorRecord> connectorRecords = new ArrayList<ConnectorRecord>();
	private List<IExitBroadcastReceiver> receivers = new ArrayList<IExitBroadcastReceiver>();
	
	public BroadcastReceiverInformationProvider(PrimitiveElement element)
	{
		super(element);
		connectorRecords.add(new ConnectorRecord(element, "Continue", IDesignElementConnectionPoint.ConnectionPointType.EXIT_POINT));
	}
	
	public void setExitBroadcastReceivers(List<IExitBroadcastReceiver> receivers)
	{
		this.receivers = receivers;
		getElement().markDirty();
	}

	public boolean acceptsConnector(IDesignElement origin)
	{
		return false;
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
		return connectorRecords;
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
		NodeList recGroupList = configuration.getElementsByTagName("receivers");
		if(recGroupList.getLength() != 1)
			return;
		org.w3c.dom.Element recGroupElement = (org.w3c.dom.Element)recGroupList.item(0);
		NodeList recList =
			recGroupElement.getElementsByTagName("receiver");

		for(int v = 0; v < recList.getLength(); v++)
		{
			org.w3c.dom.Element recElement =
				(org.w3c.dom.Element)recList.item(v);
			String pattern = recElement.getAttribute("pattern");
			ExitBroadcastReceiver ebr = new ExitBroadcastReceiver(pattern);
			receivers.add(ebr);
		}

	}

	public void writeConfiguration(org.w3c.dom.Element configuration)
	{
		org.w3c.dom.Element receiversElement =
			configuration.getOwnerDocument().createElement("receivers");
		configuration.appendChild(receiversElement);

		for(int i = 0; i < receivers.size(); i++)
		{
			IExitBroadcastReceiver receiver = receivers.get(i);
			org.w3c.dom.Element receiverElement =
				receiversElement.getOwnerDocument().createElement("receiver");
			receiversElement.appendChild(receiverElement);
			receiverElement.setAttribute("pattern",
				(receiver.getExitPattern() == null) ? "" : receiver.getExitPattern());
		}
	}

//	public List getPropertiesPanels()
//	{
//		List ret = new ArrayList();
//		ret.add(new ApplicationStartVariablesPropertyPanel("Variables", getElement()));
//		return ret;
//	}
	
	public List<Variable> getOutgoingVariables(String exitPoint, boolean localOnly)
    {
		List<Variable> variables = new ArrayList<Variable>();
		Variable platform = VariableHelper.constructVariable("receivedExit", businessObjectSet, FieldType.STRING);
		variables.add(platform);
	    return variables;
    }

	public boolean canDelete()
	{
		return true;
	}
	
	public boolean hasPathToStart(Map<String, IDesignElement> path)
	{
		return true;
	}
	public boolean hasConnectors()
    {
	    return true;
    }

	public String getId()
	{
		return getElement().getId();
	}

	public String getName()
	{
		return getElement().getName();
	}

	@Override
	public List<IExitBroadcastReceiver> getExitBroadcastReceivers()
	{
		return receivers;
	}
	
}
