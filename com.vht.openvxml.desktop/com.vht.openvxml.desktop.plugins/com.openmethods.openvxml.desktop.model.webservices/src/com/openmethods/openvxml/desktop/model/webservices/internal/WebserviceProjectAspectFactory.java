package com.openmethods.openvxml.desktop.model.webservices.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.core.spi.IOpenVXMLProjectAspectFactory;
import org.eclipse.vtp.desktop.model.core.spi.OpenVXMLProjectAspect;
import org.w3c.dom.Element;

import com.openmethods.openvxml.desktop.model.webservices.IWebserviceProjectAspect;
import com.openmethods.openvxml.desktop.model.webservices.builders.WebserviceModelBuilder;
import com.openmethods.openvxml.desktop.model.workflow.IWorkflowProjectAspect;

public class WebserviceProjectAspectFactory implements
		IOpenVXMLProjectAspectFactory {

	public WebserviceProjectAspectFactory() {
	}

	@Override
	public String getAspectId() {
		return IWebserviceProjectAspect.ASPECT_ID;
	}

	@Override
	public List<String> getRequiredAspects() {
		List<String> ret = new ArrayList<String>();
		ret.add(IWorkflowProjectAspect.ASPECT_ID);
		return ret;
	}

	@Override
	public boolean configureProject(IOpenVXMLProject project,
			IProjectDescription desc, Element aspectConfiguration) {
		ICommand[] commands = desc.getBuildSpec();

		for (ICommand command : commands) {
			if (command.getBuilderName().equals(
					WebserviceModelBuilder.BUILDER_ID)) {
				return false;
			}
		}

		ICommand[] newCommands = new ICommand[commands.length + 1];
		System.arraycopy(commands, 0, newCommands, 0, commands.length);

		ICommand command = desc.newCommand();
		command.setBuilderName(WebserviceModelBuilder.BUILDER_ID);
		newCommands[newCommands.length - 1] = command;
		desc.setBuildSpec(newCommands);
		return true;
	}

	@Override
	public void createProjectLayout(IOpenVXMLProject project,
			Element aspectConfiguration) {
		IFolder webservicesFolder = project.getUnderlyingProject().getFolder(
				"Webservices");
		if (!webservicesFolder.exists()) {
			try {
				webservicesFolder.create(true, true, null);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public OpenVXMLProjectAspect createProjectAspect(IOpenVXMLProject project,
			Element aspectConfiguration) {
		return new WebserviceProjectAspect(project, aspectConfiguration);
	}

}
