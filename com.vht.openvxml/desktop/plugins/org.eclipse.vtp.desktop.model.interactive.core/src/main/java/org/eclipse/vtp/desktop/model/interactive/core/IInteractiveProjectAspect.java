package org.eclipse.vtp.desktop.model.interactive.core;

import org.eclipse.vtp.desktop.model.core.IOpenVXMLProjectAspect;
import org.eclipse.vtp.desktop.model.interactive.core.mediadefaults.IMediaDefaultSettings;

public interface IInteractiveProjectAspect extends IOpenVXMLProjectAspect
{
	public static String ASPECT_ID = "com.openmethods.openvxml.desktop.model.aspect.interactive";

	public IMediaDefaultSettings getMediaDefaultSettings();

}
