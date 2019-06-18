package org.eclipse.vtp.desktop.model.core.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.vtp.desktop.model.core.spi.IOpenVXMLProjectAspectFactory;
import org.osgi.framework.Bundle;

public class ProjectAspectRegistry
{
	public static final String projectAspectsExtensionId = "org.eclipse.vtp.desktop.model.core.projectAspects";
	private static final ProjectAspectRegistry instance = new ProjectAspectRegistry();
	
	public static ProjectAspectRegistry getInstance()
	{
		return instance;
	}

	private List<IOpenVXMLProjectAspectFactory> aspectRecords = new ArrayList<IOpenVXMLProjectAspectFactory>();
	private Map<String, IOpenVXMLProjectAspectFactory> aspectsById = new HashMap<String, IOpenVXMLProjectAspectFactory>();

	public ProjectAspectRegistry()
	{
		IConfigurationElement[] aspectExtensions = Platform.getExtensionRegistry().getConfigurationElementsFor(projectAspectsExtensionId);
		for(IConfigurationElement element : aspectExtensions)
		{
			String aspectId = element.getAttribute("id");
			String className = element.getAttribute("factory");
			System.out.println("Registering aspect: " + aspectId + " " + className);
			Bundle contributor = Platform.getBundle(element.getContributor().getName());
			try
			{
				@SuppressWarnings("unchecked")
				Class<IOpenVXMLProjectAspectFactory> factoryClass = (Class<IOpenVXMLProjectAspectFactory>)contributor.loadClass(className);
				IOpenVXMLProjectAspectFactory factory = factoryClass.newInstance();
				System.out.println("Loaded aspect factory class: " + factory);
				aspectRecords.add(factory);
				aspectsById.put(aspectId, factory);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				continue;
			}
		}
	}
	
	public List<IOpenVXMLProjectAspectFactory> getAspectFactories()
	{
		return Collections.unmodifiableList(aspectRecords);
	}

	public IOpenVXMLProjectAspectFactory getAspectFactory(String aspectId)
	{
		return aspectsById.get(aspectId);
	}
}
