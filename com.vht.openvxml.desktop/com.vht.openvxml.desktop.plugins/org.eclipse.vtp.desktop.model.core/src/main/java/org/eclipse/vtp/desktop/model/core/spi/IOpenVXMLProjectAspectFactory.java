package org.eclipse.vtp.desktop.model.core.spi;

import java.util.List;

import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.w3c.dom.Element;

public interface IOpenVXMLProjectAspectFactory {
	public String getAspectId();

	public List<String> getRequiredAspects();

	public boolean configureProject(IOpenVXMLProject project,
			IProjectDescription description, Element aspectConfiguration);

	public void createProjectLayout(IOpenVXMLProject project,
			Element aspectConfiguration);

	public OpenVXMLProjectAspect createProjectAspect(IOpenVXMLProject project,
			Element aspectConfiguration);
}
