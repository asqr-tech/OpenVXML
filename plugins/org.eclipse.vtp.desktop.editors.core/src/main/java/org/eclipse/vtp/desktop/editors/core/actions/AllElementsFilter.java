package org.eclipse.vtp.desktop.editors.core.actions;

import com.openmethods.openvxml.desktop.model.workflow.design.IDesignElement;

public class AllElementsFilter implements DesignElementActionFilter
{

	public AllElementsFilter()
	{
	}

	public boolean isApplicable(IDesignElement designElement)
	{
		return true;
	}

}
