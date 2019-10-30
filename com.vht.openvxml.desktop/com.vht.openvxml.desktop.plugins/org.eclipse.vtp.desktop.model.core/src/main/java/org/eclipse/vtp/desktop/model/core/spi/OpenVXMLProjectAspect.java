package org.eclipse.vtp.desktop.model.core.spi;

import java.util.List;

import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.core.IOpenVXMLProjectAspect;
import org.eclipse.vtp.desktop.model.core.IWorkflowResource;
import org.w3c.dom.Element;

public abstract class OpenVXMLProjectAspect implements IOpenVXMLProjectAspect {
	private IOpenVXMLProject hostProject = null;

	public OpenVXMLProjectAspect(IOpenVXMLProject hostProject) {
		this.hostProject = hostProject;
	}

	@Override
	public IOpenVXMLProject getHostProject() {
		return hostProject;
	}

	public abstract boolean removeProjectConfiguration(
			IProjectDescription description);

	public abstract void removeProjectLayout();

	public abstract void writeConfiguration(Element aspectElement);

	public void getAspectResources(List<IWorkflowResource> resources) {
	}
}
