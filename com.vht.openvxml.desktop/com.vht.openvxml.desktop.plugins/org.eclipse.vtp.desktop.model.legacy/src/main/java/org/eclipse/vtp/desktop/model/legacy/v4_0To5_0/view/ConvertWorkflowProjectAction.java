/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006-2007 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.desktop.model.legacy.v4_0To5_0.view;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.core.WorkflowCore;
import org.eclipse.vtp.desktop.model.core.natures.WorkflowProjectNature;
import org.eclipse.vtp.desktop.model.interactive.voice.natures.VoiceProjectNature;
import org.eclipse.vtp.desktop.model.legacy.v4_0To5_0.ProjectConverter;
import org.eclipse.vtp.desktop.model.legacy.v4_0To5_0.VoiceConverter;
import org.eclipse.vtp.desktop.model.legacy.v4_0To5_0.dialogs.ConversionSelectionDialog;

import com.openmethods.openvxml.desktop.model.workflow.IDesignDocument;
import com.openmethods.openvxml.desktop.model.workflow.IDesignItemContainer;
import com.openmethods.openvxml.desktop.model.workflow.IWorkflowProjectAspect;

/**
 * Used in context menus to initiate the creation of a new application. This
 * action is not view or perspective specific. The resulting wizard will be
 * centered on the current shell associated with the current UI thread. This
 * action will terminate upon opening of the wizard dialog, and does not block
 * until its completion or cancellation.
 *
 * @author Trip
 * @version 1.0
 */
@SuppressWarnings("deprecation")
public class ConvertWorkflowProjectAction extends SelectionListenerAction {
	/**
	 * Constructs a new <code>CreateApplicationAction</code> instance with the
	 * default values.
	 */
	public ConvertWorkflowProjectAction() {
		super("Convert to 5.0 OpenVXML Project");
	}

	@Override
	public void run() {
		ConversionSelectionDialog dialog = new ConversionSelectionDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		if (dialog.open() == Dialog.OK) {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceDescription description = workspace.getDescription();
			boolean autoBuild = description.isAutoBuilding();
			if (autoBuild) {
				description.setAutoBuilding(false);
				try {
					workspace.setDescription(description);
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
			ProjectConverter pc = new ProjectConverter();
			VoiceConverter voiceConverter = new VoiceConverter();
			List<IProject> projects = dialog.getProjectsToConvert();
			for (IProject project : projects) {
				try {
					if (project.hasNature(VoiceProjectNature.NATURE_ID)) {
						voiceConverter.convertVoice(project);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			for (IProject project : projects) {
				try {
					if (project.hasNature(WorkflowProjectNature.NATURE_ID)
							|| project
									.hasNature("org.eclipse.vtp.desktop.model.interactive.core.InteractiveWorkflowProjectNature")) {
						pc.convertProject(project);
					} else {
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			for (IProject project : projects) {
				try {
					if (WorkflowCore.getDefault().getWorkflowModel()
							.isWorkflowProject(project)) {
						IOpenVXMLProject op = WorkflowCore.getDefault()
								.getWorkflowModel()
								.convertToWorkflowProject(project);
						IWorkflowProjectAspect workflowAspect = (IWorkflowProjectAspect) op
								.getProjectAspect(IWorkflowProjectAspect.ASPECT_ID);
						if (workflowAspect != null) {
							IDesignItemContainer container = workflowAspect
									.getDesignRootFolder();
							touchContainer(container);
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			if (autoBuild) {
				description.setAutoBuilding(true);
				try {
					workspace.setDescription(description);
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void touchContainer(IDesignItemContainer container) {
		for (IDesignItemContainer childContainer : container.getDesignFolders()) {
			touchContainer(childContainer);
		}
		for (IDesignDocument child : container.getDesignDocuments()) {
			child.becomeWorkingCopy(true);
			try {
				child.commitWorkingCopy();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
