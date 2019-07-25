package org.eclipse.vtp.desktop.model.interactive.core.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.core.spi.OpenVXMLProjectAspect;
import org.eclipse.vtp.desktop.model.interactive.core.ILanguageSupportProjectAspect;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaProject;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaProviderManager;
import org.eclipse.vtp.desktop.model.interactive.core.InteractiveWorkflowCore;
import org.eclipse.vtp.framework.interactions.core.media.IMediaProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.openmethods.openvxml.desktop.model.branding.BrandManager;
import com.openmethods.openvxml.desktop.model.branding.IBrand;
import com.openmethods.openvxml.desktop.model.branding.IBrandingProjectAspect;

public class LanguageSupportProjectAspect extends OpenVXMLProjectAspect
		implements ILanguageSupportProjectAspect, IMediaProviderManager
{
	private Map<String, InteractionTypeSupport> supportedInteractionTypes =
			new HashMap<String, InteractionTypeSupport>();
	private IBrandingProjectAspect brandingAspect = null;

	public LanguageSupportProjectAspect(IOpenVXMLProject project,
			Element aspectConfiguration)
	{
		super(project);
		brandingAspect = (IBrandingProjectAspect)project.getProjectAspect(IBrandingProjectAspect.ASPECT_ID);
		if(project.getParentProject() != null)
		{
			LanguageSupportProjectAspect parentAspect = (LanguageSupportProjectAspect)project.getParentProject().getProjectAspect(ASPECT_ID);
			supportedInteractionTypes = parentAspect.supportedInteractionTypes;
		}
		else
		{
			if(aspectConfiguration != null)
			{
				NodeList sections = aspectConfiguration.getChildNodes();
				for(int s = 0; s < sections.getLength(); s++)
				{
					if(sections.item(s).getNodeType() == Node.ELEMENT_NODE)
					{
						Element sectionElement = (Element)sections.item(s);
						if(sectionElement.getNodeName().equals("interaction-type-support"))
						{
							NodeList itList = sectionElement.getElementsByTagName("interaction-type");
							for(int i = 0; i < itList.getLength(); i++)
							{
								Element it = (Element)itList.item(i);
								String interactionType = it.getAttribute("id");
								String interactionTypeName = it.getAttribute("name");
								InteractionTypeSupport interactionTypeSupport = 
									supportedInteractionTypes.get(interactionType);
								if(interactionTypeSupport == null)
								{
									supportedInteractionTypes.put(interactionType, interactionTypeSupport = new InteractionTypeSupport(interactionType, interactionTypeName));
								}
								NodeList lmList = it.getElementsByTagName("language-mapping");
								for(int l = 0; l < lmList.getLength(); l++)
								{
									Element lm = (Element)lmList.item(l);
									String language = lm.getAttribute("language");
									LanguageSupport languageSupport = interactionTypeSupport.getLanguageSupport(language);
									if(languageSupport == null)
										languageSupport = interactionTypeSupport.addLanguageSupport(language);
									NodeList bmList = lm.getElementsByTagName("brand-mapping");
									for(int b = 0; b < bmList.getLength(); b++)
									{
										Element bm = (Element)bmList.item(b);
										String brandId = bm.getAttribute("brand-id");
										String mediaProjectId = bm.getAttribute("media-project-id");
										languageSupport.assignMediaProject(brandId, mediaProjectId);
									}
								}
							}
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
	
	public IOpenVXMLProject getOpenVXMLProject()
	{
		return this.getHostProject();
	}

	public List<String> getSupportedInteractionTypes()
	{
		return new ArrayList<String>(supportedInteractionTypes.keySet());
	}
	
	public List<String> getSupportedLanguages(String interactionType)
	{
		List<String> languages = new LinkedList<String>();
		InteractionTypeSupport interactionTypeSupport = supportedInteractionTypes.get(interactionType);
		if(interactionTypeSupport != null)
		{
			List<LanguageSupport> supportedLanguages = interactionTypeSupport.getSupportedLanguages();
			for(LanguageSupport language : supportedLanguages)
			{
				languages.add(language.getLanguage());
			}
		}
		return languages;
	}
	
	public List<InteractionTypeSupport> getInteractionTypeSupport()
	{
		List<InteractionTypeSupport> ret = new ArrayList<InteractionTypeSupport>();
		for(Map.Entry<String, InteractionTypeSupport> entry : supportedInteractionTypes.entrySet())
		{
			ret.add((InteractionTypeSupport)entry.getValue().clone());
		}
		return ret;
	}
	
	public void setInteractionTypeSupport(List<InteractionTypeSupport> newSupport)
	{
		supportedInteractionTypes.clear();
		for(InteractionTypeSupport its : newSupport)
		{
			supportedInteractionTypes.put(its.getInteractionType(), its);
		}
	}
	
	public IMediaProviderManager getMediaProviderManager()
	{
		return this;
	}

	public IMediaProject getMediaProject(String interactionType, IBrand brand,
			String language)
	{
		InteractionTypeSupport typeSupport = supportedInteractionTypes.get(interactionType);
		if(typeSupport != null)
		{
			LanguageSupport languageSupport = typeSupport.getLanguageSupport(language);
			if(languageSupport != null)
			{
				String mediaProjectId = languageSupport.getMediaProjectId(brand, true);
				if(mediaProjectId != null)
				{
					IMediaProject mediaProject = InteractiveWorkflowCore.getDefault().getInteractiveWorkflowModel().getMediaProject(mediaProjectId);
					if(mediaProject != null)
					{
						return mediaProject;
					}
				}
			}
		}
		return null;
	}

	public IMediaProvider getMediaProvider(String interactionType, IBrand brand,
			String language)
	{
		IMediaProject mediaProject = getMediaProject(interactionType, brand, language);
		if (mediaProject != null)
			return mediaProject.getMediaProvider();
		return null;
	}

	private List<IBrand> flattenBrands()
	{
		List<IBrand> ret = new LinkedList<IBrand>();
		BrandManager brandManager = brandingAspect.getBrandManager();
		IBrand defaultBrand = brandManager.getDefaultBrand();
		flattenBrand(ret, defaultBrand);
		return ret;
	}
	
	private void flattenBrand(List<IBrand> list, IBrand brand)
	{
		list.add(brand);
		for(IBrand child : brand.getChildBrands())
		{
			flattenBrand(list, child);
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
		Element interactionTypeSupportElement = document.createElement("interaction-type-support");
		aspectElement.appendChild(interactionTypeSupportElement);
		for(InteractionTypeSupport interactionTypeSupport : supportedInteractionTypes.values())
		{
			Element interactionTypeElement = document.createElement("interaction-type");
			interactionTypeSupportElement.appendChild(interactionTypeElement);
			interactionTypeElement.setAttribute("id", interactionTypeSupport.getInteractionType());
			interactionTypeElement.setAttribute("name", interactionTypeSupport.getInteractionTypeName());
			List<LanguageSupport> supportedLanguages = interactionTypeSupport.getSupportedLanguages();
			for(LanguageSupport languageSupport : supportedLanguages)
			{
				Element languageSupportElement = document.createElement("language-mapping");
				interactionTypeElement.appendChild(languageSupportElement);
				languageSupportElement.setAttribute("language", languageSupport.getLanguage());
				for(IBrand brand : flattenBrands())
				{
					String mediaProjectId = languageSupport.getMediaProjectId(brand, false);
					if(mediaProjectId != null)
					{
						Element brandMappingElement = document.createElement("brand-mapping");
						languageSupportElement.appendChild(brandMappingElement);
						brandMappingElement.setAttribute("brand-id", brand.getId());
						brandMappingElement.setAttribute("media-project-id", mediaProjectId);
					}
				}
			}
		}
	}

}
