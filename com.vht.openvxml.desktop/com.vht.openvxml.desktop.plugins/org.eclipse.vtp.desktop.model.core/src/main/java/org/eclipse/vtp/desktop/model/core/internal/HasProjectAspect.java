package org.eclipse.vtp.desktop.model.core.internal;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.core.WorkflowCore;

public class HasProjectAspect extends PropertyTester {

	public HasProjectAspect() {
	}

	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		IOpenVXMLProject project = null;
		if (receiver instanceof IOpenVXMLProject) {
			project = (IOpenVXMLProject) receiver;
		} else if (receiver instanceof IProject) {
			IProject ip = (IProject) receiver;
			if (!WorkflowCore.getDefault().getWorkflowModel()
					.isWorkflowProject(ip)) {
				return false;
			}
			project = WorkflowCore.getDefault().getWorkflowModel()
					.convertToWorkflowProject(ip);
		} else {
			return false;
		}
		return project.getProjectAspect((String) expectedValue) != null;
	}

}
