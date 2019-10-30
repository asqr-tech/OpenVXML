package com.openmethods.openvxml.desktop.model.workflow;

import java.util.List;

import org.eclipse.vtp.desktop.model.core.IOpenVXMLProjectAspect;

public interface IWorkflowProjectAspect extends IOpenVXMLProjectAspect {
	public static String ASPECT_ID = "com.openmethods.openvxml.desktop.model.aspect.workflow";

	public IDesignRootFolder getDesignRootFolder();

	public IWorkflowEntry getWorkflowEntry(String id);

	public IWorkflowEntry getWorkflowEntryByName(String name);

	/**
	 * @return A list of the entry points into this workflow project
	 */
	public List<IWorkflowEntry> getWorkflowEntries();

	/**
	 * @return A list of exit points out of this workflow project that are
	 *         reachable from the given entry point.
	 */
	public List<IWorkflowExit> getWorkflowExits(IWorkflowEntry entryPoint);

	public void navigateToElement(String elementId);
}
