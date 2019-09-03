/**
 * 
 */
package org.eclipse.vtp.desktop.model.interactive.core.internal.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.core.configuration.ConfigurationContext;
import org.eclipse.vtp.desktop.model.interactive.core.ILanguageSupportProjectAspect;
import org.eclipse.vtp.desktop.model.interactive.core.InteractionType;
import org.eclipse.vtp.desktop.model.interactive.core.InteractionTypeManager;

/**
 * @author trip
 *
 */
public class InteractionTypeContext implements ConfigurationContext
{
	public static final String CONTEXT_ID = "org.eclipse.vtp.desktop.model.interactive.core.interactiontypecontext";
	private static final String CONTEXT_NAME = "Interaction Type";
	private IOpenVXMLProject project = null;

	/**
	 * 
	 */
	public InteractionTypeContext()
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.configuration.ConfigurationContext#getId()
	 */
	public String getId()
	{
		return CONTEXT_ID;
	}
	
	public String getName()
	{
		return CONTEXT_NAME;
	}
	
	public void setProject(IOpenVXMLProject project)
	{
		this.project = project;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.configuration.ConfigurationContext#getLabel(java.lang.Object)
	 */
	public String getLabel(Object obj)
	{
		return ((InteractionType)obj).getName();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.configuration.ConfigurationContext#getValues()
	 */
	public List<Object> getValues()
	{
		List<Object> ret = new ArrayList<Object>();
		ILanguageSupportProjectAspect languageSupportAspect = (ILanguageSupportProjectAspect)project.getProjectAspect(ILanguageSupportProjectAspect.ASPECT_ID);
		List<String> typeIds = languageSupportAspect.getMediaProviderManager().getSupportedInteractionTypes();
		for(String typeId : typeIds)
		{
			ret.add(InteractionTypeManager.getInstance().getType(typeId));
		}
		return ret;
	}

	public boolean setConfigurationContext(Map<String, Object> values)
	{
		return false;
	}

}
