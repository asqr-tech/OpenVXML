package org.eclipse.vtp.desktop.projects.interactive.core.view;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.core.WorkflowCore;
import org.eclipse.vtp.desktop.model.interactive.core.IInteractiveProjectAspect;

public class InteractiveWorkflowTest extends PropertyTester {

	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (receiver instanceof IProject) {
			IOpenVXMLProject wp = WorkflowCore.getDefault().getWorkflowModel()
					.convertToWorkflowProject((IProject) receiver);
			if (wp == null) {
				return false;
			}
			IInteractiveProjectAspect interactiveAspect = (IInteractiveProjectAspect) wp
					.getProjectAspect(IInteractiveProjectAspect.ASPECT_ID);
			return interactiveAspect != null;
		}
		return false;
	}

}
