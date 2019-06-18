package org.eclipse.vtp.desktop.model.interactive.core.internal;

import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.core.spi.OpenVXMLProjectAspect;
import org.eclipse.vtp.desktop.model.interactive.core.IUmbrellaProjectAspect;
import org.w3c.dom.Element;

public class UmbrellaProjectAspect extends OpenVXMLProjectAspect implements
		IUmbrellaProjectAspect
{

	public UmbrellaProjectAspect(IOpenVXMLProject hostProject,
			Element aspectConfiguration)
	{
		super(hostProject);
	}

	@Override
	public String getAspectId()
	{
		return ASPECT_ID;
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
	}

}
