/**
 * 
 */
package org.eclipse.vtp.desktop.model.interactive.core.internal;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.core.spi.IOpenVXMLProjectAspectFactory;
import org.eclipse.vtp.desktop.model.core.spi.OpenVXMLProjectAspect;
import org.eclipse.vtp.desktop.model.interactive.core.IInteractiveProjectAspect;
import org.eclipse.vtp.desktop.model.interactive.core.ILanguageSupportProjectAspect;
import org.w3c.dom.Element;

import com.openmethods.openvxml.desktop.model.branding.IBrandingProjectAspect;
import com.openmethods.openvxml.desktop.model.businessobjects.IBusinessObjectProjectAspect;
import com.openmethods.openvxml.desktop.model.workflow.IWorkflowProjectAspect;

/**
 * @author trip
 *
 */
public class InteractiveProjectAspectFactory implements
		IOpenVXMLProjectAspectFactory {

	/**
	 * 
	 */
	public InteractiveProjectAspectFactory() {
	}

	@Override
	public String getAspectId() {
		return IInteractiveProjectAspect.ASPECT_ID;
	}

	@Override
	public List<String> getRequiredAspects() {
		List<String> ret = new LinkedList<String>();
		ret.add(IBrandingProjectAspect.ASPECT_ID);
		ret.add(ILanguageSupportProjectAspect.ASPECT_ID);
		ret.add(IBusinessObjectProjectAspect.ASPECT_ID);
		ret.add(IWorkflowProjectAspect.ASPECT_ID);
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
		return new InteractiveProjectAspect(project, aspectConfiguration);
	}

}
