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
package org.eclipse.vtp.desktop.projects.interactive.core.properties;

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
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.core.WorkflowCore;
import org.eclipse.vtp.desktop.model.core.internal.OpenVXMLProject;
import org.eclipse.vtp.desktop.model.interactive.core.ILanguageSupportProjectAspect;
import org.eclipse.vtp.desktop.model.interactive.core.IUmbrellaProjectAspect;
import org.eclipse.vtp.desktop.model.interactive.core.internal.LanguageSupportProjectAspect;
import org.eclipse.vtp.desktop.projects.core.util.BrandConfigurationScreen;
import org.eclipse.vtp.desktop.projects.core.util.ConfigurationBrandManager;
import org.eclipse.vtp.desktop.projects.core.wizards.ConnectWorkflowToUmbrellaWizard;
import org.eclipse.vtp.desktop.projects.interactive.core.util.InteractionSupportManager;
import org.eclipse.vtp.desktop.projects.interactive.core.util.InteractionTypeConfigurationScreen;
import org.eclipse.vtp.desktop.projects.interactive.core.util.LanguageConfigurationScreen;
import org.eclipse.vtp.desktop.projects.interactive.core.wizards.DisconnectInteractiveWorkflowWizard;

import com.openmethods.openvxml.desktop.model.branding.IBrandingProjectAspect;

public class ApplicationProjectConfigurationPropertyPage extends PropertyPage {
	private BrandConfigurationScreen brandScreen = new BrandConfigurationScreen();
	private InteractionTypeConfigurationScreen interactionScreen = new InteractionTypeConfigurationScreen();
	private LanguageConfigurationScreen languageScreen = new LanguageConfigurationScreen();
	private OpenVXMLProject applicationProject = null;
	private IOpenVXMLProject parentProject = null;
	private ConfigurationBrandManager brandManager = null;
	private InteractionSupportManager supportManager = null;
	private IBrandingProjectAspect brandingAspect = null;
	private LanguageSupportProjectAspect languageSupportAspect = null;

	public ApplicationProjectConfigurationPropertyPage() {
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
				languageSupportAspect = (LanguageSupportProjectAspect) applicationProject
						.getProjectAspect(ILanguageSupportProjectAspect.ASPECT_ID);
				supportManager = new InteractionSupportManager();
				supportManager.init(languageSupportAspect
						.getInteractionTypeSupport());
				brandScreen.init(brandManager);
				interactionScreen.setSupport(supportManager);
				languageScreen.init(brandManager, supportManager);
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
			if (applicationProject
					.getProjectAspect(IUmbrellaProjectAspect.ASPECT_ID) == null) {
				Link connectLink = new Link(comp, SWT.NONE);
				connectLink
						.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				connectLink.setText("<A>Connect project to umbrella</A>");
				connectLink.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						WizardDialog wizard = new WizardDialog(PlatformUI
								.getWorkbench().getActiveWorkbenchWindow()
								.getShell(),
								new ConnectWorkflowToUmbrellaWizard(
										applicationProject));
						wizard.open();
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
					}
				});
			}
			TabFolder tabFolder = new TabFolder(comp, SWT.NONE);
			tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
			TabItem brandItem = new TabItem(tabFolder, SWT.NONE);
			brandItem.setText("Brands");
			Composite brandComp = new Composite(tabFolder, SWT.NONE);
			brandComp.setLayout(new FillLayout());
			brandScreen.createContents(brandComp);
			brandItem.setControl(brandComp);
			TabItem interactionItem = new TabItem(tabFolder, SWT.NONE);
			interactionItem.setText("Interaction Types");
			Composite interactionComp = new Composite(tabFolder, SWT.NONE);
			interactionComp.setLayout(new FillLayout());
			interactionScreen.createContents(interactionComp);
			interactionItem.setControl(interactionComp);
			TabItem languageItem = new TabItem(tabFolder, SWT.NONE);
			languageItem.setText("Languages");
			Composite languageComp = new Composite(tabFolder, SWT.NONE);
			languageComp.setLayout(new FillLayout());
			languageScreen.createContents(languageComp);
			languageItem.setControl(languageComp);
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
							.getShell(),
							new DisconnectInteractiveWorkflowWizard(
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
			brandManager = new ConfigurationBrandManager(
					brandingAspect.getBrandManager());
			supportManager = new InteractionSupportManager();
			supportManager.init(languageSupportAspect
					.getInteractionTypeSupport());
			brandScreen.init(brandManager);
			interactionScreen.setSupport(supportManager);
			languageScreen.init(brandManager, supportManager);
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
			brandManager.saveTo(brandingAspect.getBrandManager());
			languageSupportAspect.setInteractionTypeSupport(supportManager
					.getSupport());
			applicationProject.storeBuildPath();
		}
		return true;
	}
}
