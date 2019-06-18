package org.eclipse.vtp.desktop.export;

import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;

public interface IWorkflowExporter extends IProjectExporter {
	
	IOpenVXMLProject getWorkflowProject();
	
}
