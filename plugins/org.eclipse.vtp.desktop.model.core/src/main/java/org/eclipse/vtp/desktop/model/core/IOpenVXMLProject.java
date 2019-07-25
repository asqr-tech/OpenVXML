package org.eclipse.vtp.desktop.model.core;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

public interface IOpenVXMLProject extends IWorkflowResourceContainer
{
	public String getId();

	/**
	 * Allows access to the underlying Eclipse project model
	 *
	 * @return The eclipse project object for this project
	 */
	public IProject getUnderlyingProject();
	
	public IOpenVXMLProject getParentProject();
	
	public void setParentProject(IOpenVXMLProject parent);

	public IOpenVXMLProjectAspect addProjectAspect(String aspectId) throws CoreException;
	
	public IOpenVXMLProjectAspect getProjectAspect(String aspectId);
	
	public List<IOpenVXMLProjectAspect> getProjectAspects();
	
	public void removeProjectAspect(String aspectId) throws CoreException;
}
