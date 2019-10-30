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
package org.eclipse.vtp.desktop.projects.core.properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.core.WorkflowCore;
import org.eclipse.vtp.desktop.model.core.internal.OpenVXMLProject;
import org.eclipse.vtp.desktop.projects.core.util.BrandConfigurationScreen;
import org.eclipse.vtp.desktop.projects.core.util.ConfigurationBrandManager;
import org.eclipse.vtp.desktop.projects.core.wizards.ConnectWorkflowToUmbrellaWizard;
import org.eclipse.vtp.desktop.projects.core.wizards.DisconnectWorkflowWizard;
import org.eclipse.vtp.framework.util.Guid;

import com.openmethods.openvxml.desktop.model.branding.IBrandingProjectAspect;
import com.openmethods.openvxml.desktop.model.branding.internal.Brand;
import com.openmethods.openvxml.desktop.model.branding.internal.DefaultBrandManager;

public class WorkflowProjectConfigurationPropertyPage extends PropertyPage {
	private BrandConfigurationScreen screen = new BrandConfigurationScreen();
	private OpenVXMLProject applicationProject = null;
	private IOpenVXMLProject parentProject = null;
	private ConfigurationBrandManager brandManager = null;
	private IBrandingProjectAspect brandingAspect = null;

	public WorkflowProjectConfigurationPropertyPage() {
		DefaultBrandManager defaultManager = new DefaultBrandManager();
		defaultManager.setDefaultBrand(new Brand(Guid.createGUID(), "Default"));
		brandManager = new ConfigurationBrandManager(defaultManager);
		screen.init(brandManager);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.dialogs.PropertyPage#setElement(org.eclipse.core.runtime
	 * .IAdaptable)
	 */
	@Override
	public void setElement(IAdaptable element) {
		super.setElement(element);
		try {
			if (element instanceof OpenVXMLProject) {
				applicationProject = (OpenVXMLProject) element;
			} else if (element instanceof IProject) {
				IProject project = (IProject) element;
				if (WorkflowCore.getDefault().getWorkflowModel()
						.isWorkflowProject(project)) {
					applicationProject = (OpenVXMLProject) WorkflowCore
							.getDefault().getWorkflowModel()
							.convertToWorkflowProject(project);
				} else {
					throw new RuntimeException("Unsupported element type");
				}
			} else {
				throw new RuntimeException("Unsupported element type");
			}
			parentProject = applicationProject.getParentProject();
			if (parentProject == null) {
				brandingAspect = (IBrandingProjectAspect) applicationProject
						.getProjectAspect(IBrandingProjectAspect.ASPECT_ID);
				brandManager = new ConfigurationBrandManager(
						brandingAspect.getBrandManager());
				screen.init(brandManager);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse
	 * .swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(1, false));
		if (parentProject == null) {
			Link connectLink = new Link(comp, SWT.NONE);
			connectLink.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			connectLink.setText("<A>Connect project to umbrella</A>");
			connectLink.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					WizardDialog wizard = new WizardDialog(PlatformUI
							.getWorkbench().getActiveWorkbenchWindow()
							.getShell(), new ConnectWorkflowToUmbrellaWizard(
							applicationProject));
					wizard.open();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
			Composite screenComp = new Composite(comp, SWT.NONE);
			screenComp.setLayout(new FillLayout());
			screenComp.setLayoutData(new GridData(GridData.FILL_BOTH));
			screen.createContents(screenComp);
		} else {
			Label inheritLabel = new Label(comp, SWT.NONE);
			inheritLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			inheritLabel
					.setText("This project is currently inheriting its configuration from the "
							+ applicationProject.getParentProject().getName()
							+ " Umbrella project.");
			Link disconnectLink = new Link(comp, SWT.NONE);
			disconnectLink
					.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			disconnectLink.setText("<A>Disconnect project from umbrella</A>");
			disconnectLink.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					WizardDialog wizard = new WizardDialog(PlatformUI
							.getWorkbench().getActiveWorkbenchWindow()
							.getShell(), new DisconnectWorkflowWizard(
							applicationProject));
					wizard.open();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
		}
		return comp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	@Override
	protected void performDefaults() {
		if (parentProject == null) {
			if (applicationProject != null) {
				brandManager = new ConfigurationBrandManager(
						brandingAspect.getBrandManager());
			} else {
				DefaultBrandManager defaultManager = new DefaultBrandManager();
				defaultManager.setDefaultBrand(new Brand(Guid.createGUID(),
						"Default"));
				brandManager = new ConfigurationBrandManager(defaultManager);
			}
			screen.init(brandManager);
		}
		super.performDefaults();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		if (parentProject == null) {
			brandManager.saveTo(brandingAspect.getBrandManager(), false);
			applicationProject.storeBuildPath();
		}
		return true;
	}
}
