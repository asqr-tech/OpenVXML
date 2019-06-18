package com.openmethods.openvxml.desktop.model.webservices;

import org.eclipse.vtp.desktop.model.core.IOpenVXMLProjectAspect;

public interface IWebserviceProjectAspect extends IOpenVXMLProjectAspect
{
	public static String ASPECT_ID = "com.openmethods.openvxml.desktop.model.aspect.webservices";
	
	public IWebserviceSet getWebserviceSet();
}
