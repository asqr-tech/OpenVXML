/**
 * 
 */
package org.eclipse.vtp.desktop.model.core.natures;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

/**
 * @author trip
 *
 */
public class WorkflowProjectNature5_0 implements IProjectNature
{
	public static final String NATURE_ID = "org.eclipse.vtp.desktop.model.core.WorkflowProjectNature5_0";

	/**
	 * The project to which this nature applies.
	 */
	private IProject project;

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.core.resources.IProjectNature#configure()
	 */
	public void configure() throws CoreException
	{
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.core.resources.IProjectNature#deconfigure()
	 */
	public void deconfigure() throws CoreException
	{
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.core.resources.IProjectNature#getProject()
	 */
	public IProject getProject()
	{
		return project;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.core.resources.IProjectNature#setProject(org.eclipse.core.resources.IProject)
	 */
	public void setProject(IProject project)
	{
		this.project = project;
	}
}
