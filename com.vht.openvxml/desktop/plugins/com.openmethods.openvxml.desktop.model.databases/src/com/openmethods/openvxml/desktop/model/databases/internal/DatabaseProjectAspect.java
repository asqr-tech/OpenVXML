package com.openmethods.openvxml.desktop.model.databases.internal;

import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.core.IWorkflowResource;
import org.eclipse.vtp.desktop.model.core.spi.OpenVXMLProjectAspect;
import org.w3c.dom.Element;

import com.openmethods.openvxml.desktop.model.databases.IDatabaseProjectAspect;
import com.openmethods.openvxml.desktop.model.databases.IDatabaseSet;

public class DatabaseProjectAspect extends OpenVXMLProjectAspect implements
		IDatabaseProjectAspect
{
	private DatabaseSet databaseSet = null;

	public DatabaseProjectAspect(IOpenVXMLProject project,
			Element aspectConfiguration)
	{
		super(project);
		IFolder databasesFolder = project.getUnderlyingProject().getFolder("Databases");
		databaseSet = new DatabaseSet(this, databasesFolder);
	}

	@Override
	public String getAspectId()
	{
		return IDatabaseProjectAspect.ASPECT_ID;
	}

	public IDatabaseSet getDatabaseSet()
	{
		return databaseSet;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.spi.OpenVXMLProjectAspect#getAspectResources()
	 */
	@Override
	public void getAspectResources(List<IWorkflowResource> resources)
	{
		resources.add(databaseSet);
	}

	@Override
	public boolean removeProjectConfiguration(IProjectDescription description)
	{
		return false;
	}

	@Override
	public void removeProjectLayout()
	{
		IFolder databasesFolder = getHostProject().getUnderlyingProject().getFolder("Databases");
		try
		{
			databasesFolder.delete(true, null);
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void writeConfiguration(Element aspectElement)
	{
	}

}
