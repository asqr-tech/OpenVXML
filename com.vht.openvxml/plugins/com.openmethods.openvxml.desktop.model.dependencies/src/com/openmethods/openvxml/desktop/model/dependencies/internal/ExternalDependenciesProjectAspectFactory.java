package com.openmethods.openvxml.desktop.model.dependencies.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.core.spi.IOpenVXMLProjectAspectFactory;
import org.eclipse.vtp.desktop.model.core.spi.OpenVXMLProjectAspect;
import org.w3c.dom.Element;

import com.openmethods.openvxml.desktop.model.dependencies.IExternalDependenciesProjectAspect;
import com.openmethods.openvxml.desktop.model.workflow.IWorkflowProjectAspect;

public class ExternalDependenciesProjectAspectFactory implements
		IOpenVXMLProjectAspectFactory
{

	public ExternalDependenciesProjectAspectFactory()
	{
	}

	@Override
	public String getAspectId()
	{
		return IExternalDependenciesProjectAspect.ASPECT_ID;
	}

	@Override
	public List<String> getRequiredAspects()
	{
		List<String> ret = new ArrayList<String>();
		ret.add(IWorkflowProjectAspect.ASPECT_ID);
		return ret;
	}

	@Override
	public void createProjectLayout(IOpenVXMLProject project,
			Element aspectConfiguration)
	{
		IFolder dependenciesFolder = project.getUnderlyingProject().getFolder("Dependencies");
		if(!dependenciesFolder.exists())
			try
			{
				dependenciesFolder.create(true, true, null);
			}
			catch (CoreException e)
			{
				e.printStackTrace();
			}
	}

	@Override
	public boolean configureProject(IOpenVXMLProject project,
			IProjectDescription description, Element aspectConfiguration)
	{
		return false;
	}
	
	@Override
	public OpenVXMLProjectAspect createProjectAspect(IOpenVXMLProject project,
			Element aspectConfiguration)
	{
		return new ExternalDependenciesProjectAspect(project, aspectConfiguration);
	}

}
