package com.openmethods.openvxml.desktop.model.branding.internal;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.core.spi.IOpenVXMLProjectAspectFactory;
import org.eclipse.vtp.desktop.model.core.spi.OpenVXMLProjectAspect;
import org.w3c.dom.Element;

public class BrandingProjectAspectFactory implements
		IOpenVXMLProjectAspectFactory {

	public BrandingProjectAspectFactory() {
	}

	@Override
	public String getAspectId() {
		return BrandingProjectAspect.ASPECT_ID;
	}

	@Override
	public List<String> getRequiredAspects() {
		return Collections.emptyList();
	}

	@Override
	public boolean configureProject(IOpenVXMLProject project,
			IProjectDescription description, Element aspectConfiguration) {
		return false;
	}

	@Override
	public void createProjectLayout(IOpenVXMLProject project,
			Element aspectConfiguration) {
	}

	@Override
	public OpenVXMLProjectAspect createProjectAspect(IOpenVXMLProject project,
			Element aspectConfiguration) {
		return new BrandingProjectAspect(project, aspectConfiguration);
	}

}
