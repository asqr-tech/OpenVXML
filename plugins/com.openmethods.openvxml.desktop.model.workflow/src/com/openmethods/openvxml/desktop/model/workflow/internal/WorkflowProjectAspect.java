package com.openmethods.openvxml.desktop.model.workflow.internal;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.core.IWorkflowResource;
import org.eclipse.vtp.desktop.model.core.WorkflowCore;
import org.eclipse.vtp.desktop.model.core.spi.OpenVXMLProjectAspect;
import org.w3c.dom.Element;

import com.openmethods.openvxml.desktop.model.workflow.IDesignDocument;
import com.openmethods.openvxml.desktop.model.workflow.IDesignRootFolder;
import com.openmethods.openvxml.desktop.model.workflow.IWorkflowEntry;
import com.openmethods.openvxml.desktop.model.workflow.IWorkflowExit;
import com.openmethods.openvxml.desktop.model.workflow.IWorkflowProjectAspect;
import com.openmethods.openvxml.desktop.model.workflow.builders.WorkflowProjectBuilder;
import com.openmethods.openvxml.desktop.model.workflow.design.IDesignViewer;

public class WorkflowProjectAspect extends OpenVXMLProjectAspect implements
		IWorkflowProjectAspect
{
	private DesignRootFolder designRootFolder = null;

	public WorkflowProjectAspect(IOpenVXMLProject hostProject,
			Element aspectConfiguration)
	{
		super(hostProject);
		IFolder designFolder = hostProject.getUnderlyingProject().getFolder("Workflow Design");
		designRootFolder = new DesignRootFolder(this, designFolder);
	}

	@Override
	public String getAspectId()
	{
		return IWorkflowProjectAspect.ASPECT_ID;
	}

	public IDesignRootFolder getDesignRootFolder()
	{
		return designRootFolder;
	}
	
	public IWorkflowEntry getWorkflowEntry(String id)
	{
		WorkflowTraversalHelper wth = new WorkflowTraversalHelper(this, new LinkedList<IDesignDocument>());
		return wth.getWorkflowEntry(id);
	}
	
	public IWorkflowEntry getWorkflowEntryByName(String name)
	{
		WorkflowTraversalHelper wth = new WorkflowTraversalHelper(this, new LinkedList<IDesignDocument>());
		for(IWorkflowEntry entry : wth.getAllWorkflowEntries())
			if(entry.getName().equals(name))
				return entry;
		return null;
	}

	public List<IWorkflowEntry> getWorkflowEntries()
	{
		WorkflowTraversalHelper wth = new WorkflowTraversalHelper(this, new LinkedList<IDesignDocument>());
		return wth.getAllWorkflowEntries();
	}

	public List<IWorkflowExit> getWorkflowExits(IWorkflowEntry entryPoint)
	{
		return null;
	}

	public void navigateToElement(String elementId)
    {
		WorkflowIndex index = WorkflowIndexService.getInstance().getIndex(getHostProject().getUnderlyingProject());
		String documentPath = index.locateElement(elementId);
		if(documentPath != null)
		{
			IFile file = getHostProject().getUnderlyingProject().getFile(documentPath);
			if(file.exists())
			{
				IWorkflowResource workflowResource = WorkflowCore.getDefault().getWorkflowModel().convertToWorkflowResource(file);
				if(workflowResource instanceof IDesignDocument)
				{
					IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
					if(workbenchWindow == null)
					{
						if(PlatformUI.getWorkbench().getWorkbenchWindowCount() > 0)
							workbenchWindow = PlatformUI.getWorkbench().getWorkbenchWindows()[0];
					}
					if(workbenchWindow != null)
					{
						try
						{
							IEditorPart editor = IDE.openEditor(workbenchWindow.getActivePage(), file, true);
							if(editor instanceof IDesignViewer)
							{
								((IDesignViewer)editor).displayElement(elementId);
								return;
							}
						}
						catch (PartInitException e)
						{
							e.printStackTrace();
						}
					}
				}
			}
			
		}
		//should show an error box here or something
		System.err.println("could not locate element: " + elementId);
    }
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.spi.OpenVXMLProjectAspect#getAspectResources(java.util.List)
	 */
	@Override
	public void getAspectResources(List<IWorkflowResource> resources)
	{
		resources.add(designRootFolder);
	}

	@Override
	public boolean removeProjectConfiguration(IProjectDescription description)
	{
		ICommand[] commands = description.getBuildSpec();

		for(int i = 0; i < commands.length; ++i)
		{
			if(commands[i].getBuilderName()
							  .equals(WorkflowProjectBuilder.BUILDER_ID))
			{
				ICommand[] newCommands = new ICommand[commands.length - 1];
				System.arraycopy(commands, 0, newCommands, 0, i);
				System.arraycopy(commands, i + 1, newCommands, i,
					commands.length - i - 1);
				description.setBuildSpec(newCommands);

				return true;
			}
		}
		return false;
	}

	@Override
	public void removeProjectLayout()
	{
		IFolder dependenciesFolder = getHostProject().getUnderlyingProject().getFolder("Workflow Design");
		try
		{
			dependenciesFolder.delete(true, null);
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void writeConfiguration(Element aspectElement)
	{
	}

}
