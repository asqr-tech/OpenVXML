package com.openmethods.openvxml.desktop.model.databases.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.core.spi.IOpenVXMLProjectAspectFactory;
import org.eclipse.vtp.desktop.model.core.spi.OpenVXMLProjectAspect;
import org.w3c.dom.Element;

import com.openmethods.openvxml.desktop.model.databases.IDatabaseProjectAspect;
import com.openmethods.openvxml.desktop.model.workflow.IWorkflowProjectAspect;

public class DatabaseProjectAspectFactory implements
		IOpenVXMLProjectAspectFactory
{

	public DatabaseProjectAspectFactory()
	{
	}

	@Override
	public String getAspectId()
	{
		return IDatabaseProjectAspect.ASPECT_ID;
	}

	@Override
	public List<String> getRequiredAspects()
	{
		List<String> ret = new ArrayList<String>();
		ret.add(IWorkflowProjectAspect.ASPECT_ID);
		return ret;
	}

	@Override
	public boolean configureProject(IOpenVXMLProject project,
			IProjectDescription description, Element aspectConfiguration)
	{
		return false;
	}

	@Override
	public void createProjectLayout(IOpenVXMLProject project,
			Element aspectConfiguration)
	{
		IFolder databasesFolder = project.getUnderlyingProject().getFolder("Databases");
		if(!databasesFolder.exists())
			try
			{
				databasesFolder.create(true, true, null);
			}
			catch (CoreException e)
			{
				e.printStackTrace();
			}
	}

	@Override
	public OpenVXMLProjectAspect createProjectAspect(IOpenVXMLProject project,
			Element aspectConfiguration)
	{
		return new DatabaseProjectAspect(project, aspectConfiguration);
	}

}
