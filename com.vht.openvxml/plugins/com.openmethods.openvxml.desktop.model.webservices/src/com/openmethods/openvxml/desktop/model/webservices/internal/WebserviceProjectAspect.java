package com.openmethods.openvxml.desktop.model.webservices.internal;

import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.core.IWorkflowResource;
import org.eclipse.vtp.desktop.model.core.spi.OpenVXMLProjectAspect;
import org.w3c.dom.Element;

import com.openmethods.openvxml.desktop.model.webservices.IWebserviceProjectAspect;
import com.openmethods.openvxml.desktop.model.webservices.IWebserviceSet;
import com.openmethods.openvxml.desktop.model.webservices.builders.WebserviceModelBuilder;

public class WebserviceProjectAspect extends OpenVXMLProjectAspect implements
		IWebserviceProjectAspect
{
	private WebserviceSet webserviceSet = null;

	public WebserviceProjectAspect(IOpenVXMLProject project,
			Element aspectConfiguration)
	{
		super(project);
		IFolder webservicesFolder = project.getUnderlyingProject().getFolder("Webservices");
		webserviceSet = new WebserviceSet(this, webservicesFolder);
	}

	@Override
	public String getAspectId()
	{
		return IWebserviceProjectAspect.ASPECT_ID;
	}

	@Override
	public IWebserviceSet getWebserviceSet()
	{
		return webserviceSet;
	}

	@Override
	public void getAspectResources(List<IWorkflowResource> resources)
	{
		resources.add(webserviceSet);
	}

	@Override
	public boolean removeProjectConfiguration(IProjectDescription description)
	{
		ICommand[] commands = description.getBuildSpec();

		for(int i = 0; i < commands.length; ++i)
		{
			if(commands[i].getBuilderName()
							  .equals(WebserviceModelBuilder.BUILDER_ID))
			{
				ICommand[] newCommands = new ICommand[commands.length - 1];
				System.arraycopy(commands, 0, newCommands, 0, i);
				System.arraycopy(commands, i + 1, newCommands, i,
					commands.length - i - 1);
				description.setBuildSpec(newCommands);

				return true;
			}
		}
		return false;
	}

	@Override
	public void removeProjectLayout()
	{
		IFolder webservicesFolder = getHostProject().getUnderlyingProject().getFolder("Webservices");
		try
		{
			webservicesFolder.delete(true, null);
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
