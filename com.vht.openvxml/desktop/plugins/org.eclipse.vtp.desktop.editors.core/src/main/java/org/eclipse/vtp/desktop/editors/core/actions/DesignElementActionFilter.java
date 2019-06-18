package org.eclipse.vtp.desktop.editors.core.actions;

import com.openmethods.openvxml.desktop.model.workflow.design.IDesignElement;

public interface DesignElementActionFilter
{
	public boolean isApplicable(IDesignElement designElement);
}
