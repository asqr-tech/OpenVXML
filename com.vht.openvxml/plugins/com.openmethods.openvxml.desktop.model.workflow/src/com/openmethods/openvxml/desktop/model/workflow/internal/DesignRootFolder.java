package com.openmethods.openvxml.desktop.model.workflow.internal;

import org.eclipse.core.resources.IFolder;
import org.eclipse.vtp.desktop.model.core.IWorkflowResource;

import com.openmethods.openvxml.desktop.model.workflow.IDesignRootFolder;

/**
 * @author trip
 */
public class DesignRootFolder extends DesignItemContainer implements IDesignRootFolder
{
	private WorkflowProjectAspect aspect = null;

	/**
	 * 
	 */
	public DesignRootFolder(WorkflowProjectAspect aspect, IFolder folder)
	{
		super(folder);
		this.aspect = aspect;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IWorkflowResource#getParent()
	 */
	public IWorkflowResource getParent()
	{
		return aspect.getHostProject();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.internal.WorkflowResource#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapterClass)
	{
		if(IDesignRootFolder.class.isAssignableFrom(adapterClass))
			return this;
		return super.getAdapter(adapterClass);
	}
}
