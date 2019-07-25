package org.eclipse.vtp.desktop.model.interactive.core.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.core.spi.OpenVXMLProjectAspect;
import org.eclipse.vtp.desktop.model.interactive.core.IInteractiveProjectAspect;
import org.eclipse.vtp.desktop.model.interactive.core.internal.mediadefaults.WorkspaceMediaDefaultSettings;
import org.eclipse.vtp.desktop.model.interactive.core.mediadefaults.IMediaDefaultSetting;
import org.eclipse.vtp.desktop.model.interactive.core.mediadefaults.IMediaDefaultSettings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class InteractiveProjectAspect extends OpenVXMLProjectAspect implements IInteractiveProjectAspect
{
	private MediaDefaultSettings mediaDefaultSettings =
		new MediaDefaultSettings();
	
	/**
	 * Creates a new <code>InteractiveWorkflowProject</code> for the given eclipse
	 * project resource.
	 *
	 * @param project The underlying eclipse project resource
	 */
	public InteractiveProjectAspect(IOpenVXMLProject project,
			Element aspectConfiguration)
	{
		super(project);
		if(aspectConfiguration != null)
		{
			NodeList sections = aspectConfiguration.getChildNodes();
			for(int s = 0; s < sections.getLength(); s++)
			{
				if(sections.item(s).getNodeType() == Node.ELEMENT_NODE)
				{
					Element sectionElement = (Element)sections.item(s);
					if(sectionElement.getNodeName().equals("media-defaults"))
					{
						NodeList nl = sectionElement.getElementsByTagName("media-default");
						for(int i = 0; i < nl.getLength(); i++)
						{
							Element defaultElement = (Element)nl.item(i);
							String interactionType = defaultElement.getAttribute("interaction-type");
							String elementType = defaultElement.getAttribute("element-type");
							String settingName = defaultElement.getAttribute("name");
							String value = defaultElement.getAttribute("value");
							if(value != null && !value.equals(""))
								mediaDefaultSettings.addDefaultSetting(interactionType, elementType, settingName, value);
						}
					}
				}
			}
		}
	}

	@Override
	public String getAspectId()
	{
		return ASPECT_ID;
	}

	public IMediaDefaultSettings getMediaDefaultSettings()
    {
	    return mediaDefaultSettings;
    }
	
	public class MediaDefaultSettings implements IMediaDefaultSettings
	{
		public Map<String, MediaDefaultSetting> settings =
			new HashMap<String, MediaDefaultSetting>();

		/* (non-Javadoc)
		 * @see org.eclipse.vtp.desktop.core.configuration.IMediaDefaultSettings#getDefaultSetting(java.lang.String, java.lang.String, java.lang.String)
		 */
		public IMediaDefaultSetting getDefaultSetting(String interactionType,
                String elementType, String setting)
        {
			String key = interactionType + ":" + elementType + ":" + setting;
	        MediaDefaultSetting defaultSetting = settings.get(key);
	        if(defaultSetting == null)
	        {
	        	defaultSetting = new MediaDefaultSetting(interactionType, elementType, setting);
	        	settings.put(key, defaultSetting);
	        }
			return defaultSetting;
        }
		
		void addDefaultSetting(String interactionType, String elementType, String settingName, String value)
		{
			String key = interactionType + ":" + elementType + ":" + settingName;
			MediaDefaultSetting setting = settings.get(key);
			if(setting == null)
			{
				setting = new MediaDefaultSetting(interactionType, elementType, settingName);
				settings.put(key, setting);
			}
			setting.setValue(value);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.vtp.desktop.core.configuration.IMediaDefaultSettings#inheritanceSupported()
		 */
		public boolean inheritanceSupported()
        {
	        return true;
        }
	}
	
	private class MediaDefaultSetting implements IMediaDefaultSetting
	{
		String interactionType = "";
		String elementType = "";
		String setting = "";
		String settingValue = "";
		boolean inherited = false;
		
		/**
		 * @param interactionType
		 * @param elementType
		 * @param setting
		 */
		public MediaDefaultSetting(String interactionType, String elementType, String setting)
		{
			super();
			this.interactionType = interactionType;
			this.elementType = elementType;
			this.setting = setting;
        	inherited = true;
        	settingValue = WorkspaceMediaDefaultSettings.getInstance().getDefaultSetting(interactionType, elementType, setting).getValue();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.vtp.desktop.core.configuration.IMediaDefaultSetting#getElementType()
		 */
		public String getElementType()
        {
	        return elementType;
        }

		/* (non-Javadoc)
		 * @see org.eclipse.vtp.desktop.core.configuration.IMediaDefaultSetting#getInteractionType()
		 */
		public String getInteractionType()
        {
	        return interactionType;
        }

		/* (non-Javadoc)
		 * @see org.eclipse.vtp.desktop.core.configuration.IMediaDefaultSetting#getName()
		 */
		public String getName()
        {
	        return setting;
        }

		/* (non-Javadoc)
		 * @see org.eclipse.vtp.desktop.core.configuration.IMediaDefaultSetting#getValue()
		 */
		public String getValue()
        {
	        return settingValue;
        }

		/* (non-Javadoc)
		 * @see org.eclipse.vtp.desktop.core.configuration.IMediaDefaultSetting#isValueInherited()
		 */
		public boolean isValueInherited()
        {
	        return inherited;
        }

		/* (non-Javadoc)
		 * @see org.eclipse.vtp.desktop.core.configuration.IMediaDefaultSetting#setValue(java.lang.String)
		 */
		public void setValue(String value)
        {
			if(inherited)
			{
				if(value != null && !value.equals(""))
				{
					inherited = false;
					settingValue = value;
				}
			}
			else
			{
				if(value == null || value.equals(""))
				{
					inherited = true;
					settingValue = WorkspaceMediaDefaultSettings.getInstance().getDefaultSetting(interactionType, elementType, setting).getValue();
				}
				else
				{
					settingValue = value;
				}
			}
        }
		
	}

	@Override
	public boolean removeProjectConfiguration(IProjectDescription description)
	{
		return false;
	}

	@Override
	public void removeProjectLayout()
	{
	}

	@Override
	public void writeConfiguration(Element aspectElement)
	{
		Document document = aspectElement.getOwnerDocument();
		Element mediaDefaultsElement = document.createElement("media-defaults");
		aspectElement.appendChild(mediaDefaultsElement);
		for(MediaDefaultSetting mediaDefaultSetting : mediaDefaultSettings.settings.values())
		{
			if(!mediaDefaultSetting.isValueInherited() && mediaDefaultSetting.getValue() != null && !(mediaDefaultSetting.getValue().equals("")))
			{
				Element mediaDefaultElement = document.createElement("media-default");
				mediaDefaultsElement.appendChild(mediaDefaultElement);
				mediaDefaultElement.setAttribute("interaction-type", mediaDefaultSetting.getInteractionType());
				mediaDefaultElement.setAttribute("element-type", mediaDefaultSetting.getElementType());
				mediaDefaultElement.setAttribute("name", mediaDefaultSetting.getName());
				mediaDefaultElement.setAttribute("value", mediaDefaultSetting.getValue());
			}
		}
	}

}
