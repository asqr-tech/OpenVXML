package com.openmethods.openvxml.desktop.model.workflow.internal;

import com.openmethods.openvxml.desktop.model.workflow.design.IDesignComponent;

public interface IDesignFilter
{
	public boolean matches(IDesignComponent component);
}
