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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.vtp.desktop.model.elements.core.PrimitiveInformationProvider;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;

import com.openmethods.openvxml.desktop.model.workflow.design.IDesign;
import com.openmethods.openvxml.desktop.model.workflow.design.IDesignElement;
import com.openmethods.openvxml.desktop.model.workflow.design.IDesignElementConnectionPoint;
import com.openmethods.openvxml.desktop.model.workflow.design.Variable;
import com.openmethods.openvxml.desktop.model.workflow.internal.design.ConnectorRecord;

public class BeginDialogInformationProvider extends
        PrimitiveInformationProvider
{
	List<ConnectorRecord> connectorRecords = new ArrayList<ConnectorRecord>();

	public BeginDialogInformationProvider(PrimitiveElement element)
	{
		super(element);
		connectorRecords.add(new ConnectorRecord(element, "Continue", IDesignElementConnectionPoint.ConnectionPointType.EXIT_POINT));
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

//	public List getPropertiesPanels()
//	{
//		List ret = new ArrayList();
//		ret.add(new PrimitiveGeneralPropertiesPanel("General", getElement()));
//		return ret;
//	}

	public void readConfiguration(org.w3c.dom.Element configuration)
	{
	}

	public void writeConfiguration(org.w3c.dom.Element configuration)
	{
	}

	public boolean canDelete()
    {
	    return false;
    }

	public boolean hasPathToStart(Map<String, IDesignElement> path)
    {
	    return true;
    }

	public boolean hasConnectors()
    {
	    return true;
    }

	public List<Variable> getOutgoingVariables(String exitPoint, boolean localOnly)
    {
		if(!localOnly)
		{
			IDesign mainDesign = getElement().getDesign().getDocument().getMainDesign();
			IDesignElement dialogElement = mainDesign.getDesignElement(getElement().getDesign().getDesignId());
			if(dialogElement != null)
			{
				return mainDesign.getVariablesFor(dialogElement);
			}
		}
		return Collections.emptyList();
    }

}
