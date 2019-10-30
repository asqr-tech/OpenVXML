package com.openmethods.openvxml.desktop.model.businessobjects.internal;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.core.spi.IOpenVXMLProjectAspectFactory;
import org.eclipse.vtp.desktop.model.core.spi.OpenVXMLProjectAspect;
import org.w3c.dom.Element;

import com.openmethods.openvxml.desktop.model.businessobjects.IBusinessObjectProjectAspect;

public class BusinessObjectProjectAspectFactory implements
		IOpenVXMLProjectAspectFactory {

	public BusinessObjectProjectAspectFactory() {
	}

	@Override
	public String getAspectId() {
		return IBusinessObjectProjectAspect.ASPECT_ID;
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
		IFolder businessObjectsFolder = project.getUnderlyingProject()
				.getFolder("Business Objects");
		if (!businessObjectsFolder.exists()) {
			try {
				businessObjectsFolder.create(true, true, null);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public OpenVXMLProjectAspect createProjectAspect(IOpenVXMLProject project,
			Element aspectConfiguration) {
		return new BusinessObjectProjectAspect(project, aspectConfiguration);
	}

}
