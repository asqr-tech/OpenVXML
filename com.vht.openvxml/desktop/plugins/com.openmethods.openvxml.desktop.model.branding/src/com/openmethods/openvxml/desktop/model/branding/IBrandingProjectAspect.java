package com.openmethods.openvxml.desktop.model.branding;

import org.eclipse.vtp.desktop.model.core.IOpenVXMLProjectAspect;

public interface IBrandingProjectAspect extends IOpenVXMLProjectAspect
{
	public static String ASPECT_ID = "com.openmethods.openvxml.desktop.model.aspect.branding";
	
	/**
	 * @return The brand manager this project uses for brand resolution
	 */
	public BrandManager getBrandManager();

}
