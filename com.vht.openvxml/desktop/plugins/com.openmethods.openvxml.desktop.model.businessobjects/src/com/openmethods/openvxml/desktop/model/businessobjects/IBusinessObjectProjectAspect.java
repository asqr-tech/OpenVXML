package com.openmethods.openvxml.desktop.model.businessobjects;

import org.eclipse.vtp.desktop.model.core.IOpenVXMLProjectAspect;

public interface IBusinessObjectProjectAspect extends IOpenVXMLProjectAspect
{
	public static String ASPECT_ID = "com.openmethods.openvxml.desktop.model.aspect.businessobjects";

	/**
	 * @return The <code>IBusinessObjectSet</code> folder resource that contains
	 * the set of business objects defined for this application
	 */
	public IBusinessObjectSet getBusinessObjectSet();
}
