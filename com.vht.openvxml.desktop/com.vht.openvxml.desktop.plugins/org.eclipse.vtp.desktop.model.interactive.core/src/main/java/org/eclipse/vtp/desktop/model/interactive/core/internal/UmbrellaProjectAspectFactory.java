package org.eclipse.vtp.desktop.model.interactive.core.internal;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.core.spi.IOpenVXMLProjectAspectFactory;
import org.eclipse.vtp.desktop.model.core.spi.OpenVXMLProjectAspect;
import org.eclipse.vtp.desktop.model.interactive.core.ILanguageSupportProjectAspect;
import org.eclipse.vtp.desktop.model.interactive.core.IUmbrellaProjectAspect;
import org.w3c.dom.Element;

import com.openmethods.openvxml.desktop.model.branding.IBrandingProjectAspect;

public class UmbrellaProjectAspectFactory implements
		IOpenVXMLProjectAspectFactory {

	public UmbrellaProjectAspectFactory() {
	}

	@Override
	public String getAspectId() {
		return IUmbrellaProjectAspect.ASPECT_ID;
	}

	@Override
	public List<String> getRequiredAspects() {
		List<String> ret = new LinkedList<String>();
		ret.add(IBrandingProjectAspect.ASPECT_ID);
		ret.add(ILanguageSupportProjectAspect.ASPECT_ID);
		return ret;
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
		return new UmbrellaProjectAspect(project, aspectConfiguration);
	}

}
