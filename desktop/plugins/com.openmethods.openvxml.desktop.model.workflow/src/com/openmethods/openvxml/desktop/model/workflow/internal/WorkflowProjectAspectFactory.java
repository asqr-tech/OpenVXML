package com.openmethods.openvxml.desktop.model.workflow.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.core.spi.IOpenVXMLProjectAspectFactory;
import org.eclipse.vtp.desktop.model.core.spi.OpenVXMLProjectAspect;
import org.eclipse.vtp.framework.util.Guid;
import org.w3c.dom.Element;

import com.openmethods.openvxml.desktop.model.branding.IBrandingProjectAspect;
import com.openmethods.openvxml.desktop.model.businessobjects.IBusinessObjectProjectAspect;
import com.openmethods.openvxml.desktop.model.workflow.IWorkflowProjectAspect;
import com.openmethods.openvxml.desktop.model.workflow.builders.WorkflowProjectBuilder;

public class WorkflowProjectAspectFactory implements
		IOpenVXMLProjectAspectFactory
{

	public WorkflowProjectAspectFactory()
	{
	}

	@Override
	public String getAspectId()
	{
		return IWorkflowProjectAspect.ASPECT_ID;
	}

	@Override
	public List<String> getRequiredAspects()
	{
		List<String> ret = new LinkedList<String>();
		ret.add(IBrandingProjectAspect.ASPECT_ID);
		ret.add(IBusinessObjectProjectAspect.ASPECT_ID);
		return ret;
	}

	@Override
	public boolean configureProject(IOpenVXMLProject project,
			IProjectDescription desc, Element aspectConfiguration)
	{
		ICommand[] commands = desc.getBuildSpec();

		for(int i = 0; i < commands.length; ++i)
		{
			if(commands[i].getBuilderName()
							  .equals(WorkflowProjectBuilder.BUILDER_ID))
			{
				return false;
			}
		}

		ICommand[] newCommands = new ICommand[commands.length + 1];
		System.arraycopy(commands, 0, newCommands, 0, commands.length);

		ICommand command = desc.newCommand();
		command.setBuilderName(WorkflowProjectBuilder.BUILDER_ID);
		newCommands[newCommands.length - 1] = command;
		desc.setBuildSpec(newCommands);
		return true;
	}

	@Override
	public void createProjectLayout(IOpenVXMLProject project,
			Element aspectConfiguration)
	{
		IFolder designFolder = project.getUnderlyingProject().getFolder("Workflow Design");
		if(!designFolder.exists())
		{
			try
			{
				designFolder.create(true, true, null);
				IFile mainDesignDocument = designFolder.getFile("Main Canvas.canvas");
				String template = getTemplate("primary_design_document_template.xml");
				template = template.replaceAll("\\[\\[flow_id\\]\\]", Guid.createGUID());
				template = template.replaceAll("\\[\\[begin_id\\]\\]", Guid.createGUID());
				mainDesignDocument.create(new ByteArrayInputStream(template.getBytes()), true, null);
			}
			catch (CoreException e)
			{
				e.printStackTrace();
				return;
			}
		}
	}

	@Override
	public OpenVXMLProjectAspect createProjectAspect(IOpenVXMLProject project,
			Element aspectConfiguration)
	{
		return new WorkflowProjectAspect(project, aspectConfiguration);
	}

	public String getTemplate(String name)
	{
		try
		{
			InputStream templateIn = this.getClass().getClassLoader().getResourceAsStream(name);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buf = new byte[10240];
			int len = templateIn.read(buf);
			while(len != -1)
			{
				baos.write(buf, 0, len);
				len = templateIn.read(buf);
			}
			templateIn.close();
			return baos.toString();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
