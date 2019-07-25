/**
 * 
 */
package org.eclipse.vtp.desktop.model.core.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.core.natures.WorkflowProjectNature5_0;

/**
 * @author trip
 *
 */
public class OpenVXMLProjectFactory
{

	/**
	 * 
	 */
	public OpenVXMLProjectFactory()
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IWorkflowProjectFactory#convertToWorkflowProject(org.eclipse.core.resources.IProject)
	 */
	public OpenVXMLProject convertToWorkflowProject(IProject project)
	{
		return new OpenVXMLProject(project);
	}

	public IOpenVXMLProject createWorkflowProject(String name)
	{
		IProject newProject = ResourcesPlugin.getWorkspace().getRoot()
		    .getProject(name);
		try
		{
			newProject.create(null);
			newProject.open(null);
			
			IProjectDescription desc = newProject.getDescription();
			desc.setNatureIds(new String[] {WorkflowProjectNature5_0.NATURE_ID});
			newProject.setDescription(desc, null);
			return convertToWorkflowProject(newProject);
		}
		catch(Exception ce)
		{
			ce.printStackTrace();
		}
		return null;
	}

}
