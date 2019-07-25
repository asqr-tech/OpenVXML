package com.openmethods.openvxml.desktop.model.businessobjects.internal;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.core.spi.OpenVXMLProjectAspect;
import org.w3c.dom.Element;

import com.openmethods.openvxml.desktop.model.businessobjects.IBusinessObjectProjectAspect;
import com.openmethods.openvxml.desktop.model.businessobjects.IBusinessObjectSet;

public class BusinessObjectProjectAspect extends OpenVXMLProjectAspect
		implements IBusinessObjectProjectAspect
{
	private BusinessObjectSet businessObjectSet = null;

	public BusinessObjectProjectAspect(IOpenVXMLProject hostProject,
			Element aspectConfiguration)
	{
		super(hostProject);
		IFolder dependenciesFolder = hostProject.getUnderlyingProject().getFolder("Business Objects");
		businessObjectSet = new BusinessObjectSet(this, dependenciesFolder);
	}

	@Override
	public String getAspectId()
	{
		return IBusinessObjectProjectAspect.ASPECT_ID;
	}

	@Override
	public IBusinessObjectSet getBusinessObjectSet()
	{
		return businessObjectSet;
	}

	@Override
	public boolean removeProjectConfiguration(IProjectDescription description)
	{
		return false;
	}

	@Override
	public void removeProjectLayout()
	{
		IFolder businessObjectsFolder = getHostProject().getUnderlyingProject().getFolder("Business Objects");
		try
		{
			businessObjectsFolder.delete(true, null);
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
