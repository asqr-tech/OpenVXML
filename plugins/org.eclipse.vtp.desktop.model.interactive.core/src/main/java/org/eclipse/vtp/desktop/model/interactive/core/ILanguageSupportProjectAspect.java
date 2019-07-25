package org.eclipse.vtp.desktop.model.interactive.core;

import org.eclipse.vtp.desktop.model.core.IOpenVXMLProjectAspect;

public interface ILanguageSupportProjectAspect extends IOpenVXMLProjectAspect
{
	public static String ASPECT_ID = "com.openmethods.openvxml.desktop.model.aspect.languagesupport";

	public IMediaProviderManager getMediaProviderManager();
}
