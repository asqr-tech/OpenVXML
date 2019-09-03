/*--------------------------------------------------------------------------
 * Copyright (c) 2009 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package com.openmethods.openvxml.desktop.model.workflow;

import java.net.URL;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.graphics.Image;
import org.eclipse.vtp.desktop.model.core.IWorkflowResource;

import com.openmethods.openvxml.desktop.model.workflow.design.IDesign;
import com.openmethods.openvxml.desktop.model.workflow.design.IDesignEntryPoint;
import com.openmethods.openvxml.desktop.model.workflow.design.IDesignExitPoint;

public interface IDesignDocument extends IWorkflowResource
{
	public IDesignItemContainer getParentDesignContainer();
	
	public boolean hasWorkflowEntry();
	
	public List<IWorkflowEntry> getWorkflowEntries();
	
	public List<IWorkflowEntry> getUpStreamWorkflowEntries(IWorkflowExit workflowExit);
	
	public List<IWorkflowEntry> getUpStreamWorkflowEntries(IDesignExitPoint designExit);
	
	public List<IWorkflowExit> getWorkflowExits();
	
	public List<IWorkflowReference> getWorkflowReferences();
	
	public List<IWorkflowExit> getDownStreamWorkflowExits(IWorkflowEntry workflowEntry);

	public List<IWorkflowExit> getDownStreamWorkflowExits(IDesignEntryPoint designEntry);
	
	public List<IDesignEntryPoint> getDesignEntryPoints();
	
	public List<IDesignEntryPoint> getUpStreamDesignEntries(IWorkflowExit workflowExit);
	
	public List<IDesignEntryPoint> getUpStreamDesignEntries(IDesignExitPoint designExit);
	
	public List<IDesignExitPoint> getDesignExitPoints();
	
	public List<IDesignExitPoint> getDownStreamDesignExits(IWorkflowEntry workflowEntry);
	
	public List<IDesignExitPoint> getDownStreamDesignExits(IDesignEntryPoint designEntry);

	public void becomeWorkingCopy();
	
	public void becomeWorkingCopy(boolean followExternalReferences);
	
	public void discardWorkingCopy();
	
	public void restoreWorkingCopy();
	
	public void commitWorkingCopy() throws Exception;
	
	public boolean isWorkingCopy();
	
//	public boolean hasWorkingCopy();
	
	public IDesign getMainDesign();
	
	public List<IDesign> getDialogDesigns();
	
	public IDesign addDialogDesign(String id, URL templateURL);
	
	public IDesign getDialogDesign(String id);
	
	public IFile getUnderlyingFile();
	
	public Image getDesignThumbnail(String designId);
	
	public void addDocumentListener(IDesignDocumentListener listener);
	
	public void removeDocumentListener(IDesignDocumentListener listener);
}
