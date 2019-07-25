package org.eclipse.vtp.desktop.model.interactive.core;

import java.util.List;

import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.framework.interactions.core.media.IMediaProvider;

import com.openmethods.openvxml.desktop.model.branding.IBrand;

public interface IMediaProviderManager
{
	public IOpenVXMLProject getOpenVXMLProject();
	
	public List<String> getSupportedInteractionTypes();
	
	public List<String> getSupportedLanguages(String interactionType);
	
	public IMediaProject getMediaProject(String interactionType, IBrand brand, String language);
	
	public IMediaProvider getMediaProvider(String interactionType, IBrand brand, String language);
}
