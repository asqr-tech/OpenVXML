package com.openmethods.openvxml.desktop.model.dependencies.internal;

import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.core.IWorkflowResource;
import org.eclipse.vtp.desktop.model.core.spi.OpenVXMLProjectAspect;
import org.w3c.dom.Element;

import com.openmethods.openvxml.desktop.model.dependencies.IDependencySet;
import com.openmethods.openvxml.desktop.model.dependencies.IExternalDependenciesProjectAspect;

public class ExternalDependenciesProjectAspect extends OpenVXMLProjectAspect
		implements IExternalDependenciesProjectAspect
{
	private DependencySet dependencySet = null;

	public ExternalDependenciesProjectAspect(IOpenVXMLProject project,
			Element aspectConfiguration)
	{
		super(project);
		IFolder dependenciesFolder = project.getUnderlyingProject().getFolder("Dependencies");
		dependencySet = new DependencySet(this, dependenciesFolder);
	}

	@Override
	public String getAspectId()
	{
		return ASPECT_ID;
	}

	/**
	 * @return The <code>IDependencySet</code> folder resource that contains
	 * the set of web services defined for this application
	 */
	public IDependencySet getDependencySet()
	{
		return dependencySet;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.spi.OpenVXMLProjectAspect#getAspectResources()
	 */
	@Override
	public void getAspectResources(List<IWorkflowResource> resources)
	{
		resources.add(dependencySet);
	}

	@Override
	public void writeConfiguration(Element aspectElement)
	{
	}

	@Override
	public void removeProjectLayout()
	{
		IFolder dependenciesFolder = getHostProject().getUnderlyingProject().getFolder("Dependencies");
		try
		{
			dependenciesFolder.delete(true, null);
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public boolean removeProjectConfiguration(IProjectDescription description)
	{
		return false;
	}

}
