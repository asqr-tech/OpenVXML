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
package org.eclipse.vtp.desktop.projects.interactive.core.wizards;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.core.WorkflowCore;
import org.eclipse.vtp.desktop.model.core.internal.OpenVXMLProject;
import org.eclipse.vtp.desktop.model.core.natures.WorkflowProjectNature5_0;
import org.eclipse.vtp.desktop.model.interactive.core.IInteractiveProjectAspect;
import org.eclipse.vtp.desktop.model.interactive.core.ILanguageSupportProjectAspect;
import org.eclipse.vtp.desktop.model.interactive.core.InteractionType;
import org.eclipse.vtp.desktop.model.interactive.core.InteractionTypeManager;
import org.eclipse.vtp.desktop.model.interactive.core.internal.InteractionTypeSupport;
import org.eclipse.vtp.desktop.model.interactive.core.internal.InteractiveProjectAspect;
import org.eclipse.vtp.desktop.model.interactive.core.internal.LanguageSupportProjectAspect;
import org.eclipse.vtp.desktop.projects.core.util.BrandConfigurationScreen;
import org.eclipse.vtp.desktop.projects.core.util.ConfigurationBrandManager;
import org.eclipse.vtp.desktop.projects.interactive.core.util.InteractionSupportManager;
import org.eclipse.vtp.desktop.projects.interactive.core.util.InteractionTypeConfigurationScreen;
import org.eclipse.vtp.desktop.projects.interactive.core.util.LanguageConfigurationScreen;
import org.eclipse.vtp.framework.util.Guid;

import com.openmethods.openvxml.desktop.model.branding.IBrandingProjectAspect;
import com.openmethods.openvxml.desktop.model.branding.internal.Brand;
import com.openmethods.openvxml.desktop.model.branding.internal.DefaultBrandManager;
import com.openmethods.openvxml.desktop.model.businessobjects.IBusinessObjectProjectAspect;
import com.openmethods.openvxml.desktop.model.dependencies.IExternalDependenciesProjectAspect;
import com.openmethods.openvxml.desktop.model.workflow.IWorkflowProjectAspect;

/**
 * This wizard walks the user through the steps required to create
 * a new OpenVXML voice application project.
 *
 * <b>Step 1)</b> Enter a name for the application.<br>
 * The name must be unique among the other projects contained in the
 * eclipse workspace.  The user will not be able to move to the next
 * step until the name is entered and is unique.
 * <br>
 * <b>Step 2)</b> Determine supported languages.<br>
 * A persona project must be associated with each language the application
 * will support.<br>
 *
 * Note: Currently all applications must support both English and Spanish.
 *
 * The new project is created by this wizard automatically and requires
 * nothing of the caller of the wizard.
 *
 * @author Trip
 * @version 1.0
 */
public class CreateInteractiveWorkflowProjectWizard extends Wizard implements INewWizard,
	IExecutableExtension
{
	/**
	 * Wizard page that collects the name of the new application project.
	 */
	ApplicationPage applicationPage = null;
	
	/**
	 * Wizard page that configures the build path of the new application project.
	 */
	BuildPathPage buildPathPage = null;
	InteractionTypePage interactionTypePage = null;
	LanguagePage languagePage = null;

	IConfigurationElement configElement = null;

	private ConfigurationBrandManager brandManager = null;
	private InteractionSupportManager supportManager = null;
	private ConfigurationBrandManager currentBrandManager = null;
	private InteractionSupportManager currentSupportManager = null;

	/**
	 * Creates a new <code>CreateApplicationWizard</code> instance with default
	 * values.
	 */
	public CreateInteractiveWorkflowProjectWizard()
	{
		super();
		DefaultBrandManager defaultManager = new DefaultBrandManager();
		defaultManager.setDefaultBrand(new Brand(Guid.createGUID(), "Default"));
		currentBrandManager = brandManager = new ConfigurationBrandManager(defaultManager);
		List<InteractionTypeSupport> supportList = new LinkedList<InteractionTypeSupport>();
		List<InteractionType> installedTypes = InteractionTypeManager.getInstance().getInteractionTypes();
		for(InteractionType installedType : installedTypes)
		{
			InteractionTypeSupport typeSupport = new InteractionTypeSupport(installedType.getId(), installedType.getName());
			supportList.add(typeSupport);
			typeSupport.addLanguageSupport("English");
		}
		currentSupportManager = supportManager = new InteractionSupportManager();
		supportManager.init(supportList);
		this.applicationPage = new ApplicationPage();
		this.buildPathPage= new BuildPathPage();
		this.interactionTypePage = new InteractionTypePage();
		this.languagePage = new LanguagePage();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	public void addPages()
	{
		addPage(applicationPage);
		addPage(buildPathPage);
		addPage(interactionTypePage);
		addPage(languagePage);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish()
	{
		OpenVXMLProject workflowProject = (OpenVXMLProject)WorkflowCore.getDefault().getWorkflowModel().createWorkflowProject(WorkflowProjectNature5_0.NATURE_ID, applicationPage.nameField.getText());
		try
		{
			IOpenVXMLProject parent = applicationPage.getParentProject();
			if(parent != null)
				workflowProject.setParentProject(parent);
			IBrandingProjectAspect brandingAspect = (IBrandingProjectAspect)workflowProject.addProjectAspect(IBrandingProjectAspect.ASPECT_ID);
			if(parent == null)
				buildPathPage.configureBuildPath(brandingAspect);
			IBusinessObjectProjectAspect businessObjectAspect = (IBusinessObjectProjectAspect)workflowProject.addProjectAspect(IBusinessObjectProjectAspect.ASPECT_ID);
			IWorkflowProjectAspect workflowAspect = (IWorkflowProjectAspect)workflowProject.addProjectAspect(IWorkflowProjectAspect.ASPECT_ID);
			IExternalDependenciesProjectAspect dependenciesAspect = (IExternalDependenciesProjectAspect)workflowProject.addProjectAspect(IExternalDependenciesProjectAspect.ASPECT_ID);
			LanguageSupportProjectAspect languageSupportAspect = (LanguageSupportProjectAspect)workflowProject.addProjectAspect(ILanguageSupportProjectAspect.ASPECT_ID);
			if(parent == null)
				languageSupportAspect.setInteractionTypeSupport(currentSupportManager.getSupport());
			InteractiveProjectAspect interactiveAspect = (InteractiveProjectAspect)workflowProject.addProjectAspect(IInteractiveProjectAspect.ASPECT_ID);
			workflowProject.storeBuildPath();
			BasicNewProjectResourceWizard.updatePerspective(configElement);
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement, java.lang.String, java.lang.Object)
	 */
	public void setInitializationData(IConfigurationElement cfig,
		String propertyName, Object data)
	{
		configElement = cfig;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
	}
	
	public class ApplicationPage extends WizardPage
	{
		Text nameField = null;
		Button localConfigButton = null;
		Button parentConfigButton = null;
		Combo parentCombo = null;
		List<IOpenVXMLProject> umbrellaProjects = new ArrayList<IOpenVXMLProject>();

		public ApplicationPage()
		{
			super("CreateApplicationPage", "Create Application", null);
			List<IOpenVXMLProject> projects = WorkflowCore.getDefault().getWorkflowModel().listWorkflowProjects();
			for(IOpenVXMLProject project : projects)
			{
				if(project.getProjectAspect("com.openmethods.openvxml.desktop.model.aspect.umbrella") != null)
				{
					umbrellaProjects.add(project);
				}
			}
			this.setPageComplete(false);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
		 */
		public void createControl(Composite parent)
		{
			Composite comp = new Composite(parent, SWT.NONE);
			comp.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
			setControl(comp);

			Label hostLabel = new Label(comp, SWT.NONE);
			hostLabel.setBackground(comp.getBackground());
			hostLabel.setText("Application Name:");
			hostLabel.setSize(hostLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			nameField = new Text(comp, SWT.SINGLE | SWT.BORDER);
			nameField.addVerifyListener(new VerifyListener()
			{

				public void verifyText(VerifyEvent e)
                {
	                String text = e.text;
	                char[] chars = text.toCharArray();
					String currentName = nameField.getText().substring(0, e.start) + e.text + nameField.getText(e.end, (nameField.getText().length() - 1));
	                if(currentName.length() > 255)
	                {
	                	e.doit = false;
	                	return;
	                }
	                for(int i = 0; i < chars.length; i++)
	                {
	                	if(Character.isLetterOrDigit(chars[i]))
	                		continue;
	                	if(chars[i] == '$')
	                		continue;
	                	if(chars[i] == '_')
	                		continue;
	                	e.doit = false;
	                	return;
	                }
	                
                }
				
			});
			nameField.addKeyListener(new KeyListener()
				{
					public void keyPressed(KeyEvent e)
					{
					}

					public void keyReleased(KeyEvent e)
					{
						if(nameField.getText().length() == 0)
						{
							setPageComplete(false);
						}
						else
						{
							IProject[] existingProjects =
								ResourcesPlugin.getWorkspace().getRoot()
											   .getProjects();

							for(int i = 0; i < existingProjects.length; i++)
							{
								if(nameField.getText()
												.equalsIgnoreCase(existingProjects[i]
											.getName()))
								{
									setPageComplete(false);
									setErrorMessage(
										"Another project already exists with that name.");

									return;
								}
							}

							setPageComplete(true);
							setErrorMessage(null);
						}
					}
				});

			localConfigButton = new Button(comp, SWT.RADIO);
			hostLabel.setBackground(comp.getBackground());
			localConfigButton.setText("Configure settings for this project locally");
			parentConfigButton = new Button(comp, SWT.RADIO);
			hostLabel.setBackground(comp.getBackground());
			parentConfigButton.setText("Inherit settings from an Umbrella project");
			localConfigButton.setSelection(true);
			Label projectLabel = new Label(comp, SWT.NONE);
			hostLabel.setBackground(comp.getBackground());
			projectLabel.setText("Umbrella Project");
			projectLabel.setLayoutData(new GridData());
			parentCombo = new Combo(comp, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.BORDER);
			for(IOpenVXMLProject project : umbrellaProjects)
			{
				parentCombo.add(project.getName());
			}
			if(umbrellaProjects.size() > 0)
			{
				parentCombo.select(0);
			}
			else
				parentConfigButton.setEnabled(false);
			parentCombo.addSelectionListener(new SelectionListener()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					IOpenVXMLProject parentProject = umbrellaProjects.get(parentCombo.getSelectionIndex());
					IBrandingProjectAspect brandingAspect = (IBrandingProjectAspect)parentProject.getProjectAspect(IBrandingProjectAspect.ASPECT_ID);
					currentBrandManager = new ConfigurationBrandManager(brandingAspect.getBrandManager());
					LanguageSupportProjectAspect languageSupportAspect = (LanguageSupportProjectAspect)parentProject.getProjectAspect(ILanguageSupportProjectAspect.ASPECT_ID);
					currentSupportManager = new InteractionSupportManager();
					currentSupportManager.init(languageSupportAspect.getInteractionTypeSupport());
					buildPathPage.changeBrandManager();
					interactionTypePage.updateSupportManager();
					languagePage.updateManagers();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e)
				{
				}
			});
			parentCombo.setEnabled(false);
			parentConfigButton.addSelectionListener(new SelectionListener()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					parentCombo.setEnabled(parentConfigButton.getSelection());
					buildPathPage.enableControls(!parentConfigButton.getSelection());
					interactionTypePage.enableControls(!parentConfigButton.getSelection());
					languagePage.enableControls(!parentConfigButton.getSelection());
					if(parentConfigButton.getSelection())
					{
						IOpenVXMLProject parentProject = umbrellaProjects.get(parentCombo.getSelectionIndex());
						if(parentProject == null)
						{
							parentCombo.select(0);
							parentProject = umbrellaProjects.get(0);
						}
						IBrandingProjectAspect brandingAspect = (IBrandingProjectAspect)parentProject.getProjectAspect(IBrandingProjectAspect.ASPECT_ID);
						currentBrandManager = new ConfigurationBrandManager(brandingAspect.getBrandManager());
						LanguageSupportProjectAspect languageSupportAspect = (LanguageSupportProjectAspect)parentProject.getProjectAspect(ILanguageSupportProjectAspect.ASPECT_ID);
						currentSupportManager = new InteractionSupportManager();
						currentSupportManager.init(languageSupportAspect.getInteractionTypeSupport());
						buildPathPage.changeBrandManager();
						interactionTypePage.updateSupportManager();
						languagePage.updateManagers();
					}
					else
					{
						currentBrandManager = brandManager;
						currentSupportManager = supportManager;
						buildPathPage.changeBrandManager();
						interactionTypePage.updateSupportManager();
						languagePage.updateManagers();
					}
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e)
				{
				}
			});

			FormLayout formLayout = new FormLayout();
			formLayout.marginHeight = 10;
			formLayout.marginWidth = 10;
			comp.setLayout(formLayout);

			FormData hostLabelFormData = new FormData();
			hostLabelFormData.left = new FormAttachment(0, 10);
			hostLabelFormData.top = new FormAttachment(0, 10);
			hostLabelFormData.right = new FormAttachment(0,
					10 + hostLabel.getSize().x);
			hostLabelFormData.bottom = new FormAttachment(0,
					10 + hostLabel.getSize().y);
			hostLabel.setLayoutData(hostLabelFormData);

			FormData hostFieldFormData = new FormData();
			hostFieldFormData.left = new FormAttachment(hostLabel, 6);
			hostFieldFormData.top = new FormAttachment(0, 10);
			hostFieldFormData.right = new FormAttachment(100, -10);
			nameField.setLayoutData(hostFieldFormData);
			
			FormData localConfigButtonData = new FormData();
			localConfigButtonData.left = new FormAttachment(0, 10);
			localConfigButtonData.top = new FormAttachment(hostLabel, 10);
			localConfigButtonData.right = new FormAttachment(100, -10);
			localConfigButton.setLayoutData(localConfigButtonData);
			
			FormData parentConfigButtonData = new FormData();
			parentConfigButtonData.left = new FormAttachment(0, 10);
			parentConfigButtonData.top = new FormAttachment(localConfigButton, 10);
			parentConfigButtonData.right = new FormAttachment(100, -10);
			parentConfigButton.setLayoutData(parentConfigButtonData);

			FormData parentLabelButtonData = new FormData();
			parentLabelButtonData.left = new FormAttachment(0, 30);
			parentLabelButtonData.top = new FormAttachment(parentConfigButton, 10);
			projectLabel.setLayoutData(parentLabelButtonData);

			FormData projectComboButtonData = new FormData();
			projectComboButtonData.left = new FormAttachment(projectLabel, 10);
			projectComboButtonData.top = new FormAttachment(parentConfigButton, 8);
			projectComboButtonData.right = new FormAttachment(100, -10);
			parentCombo.setLayoutData(projectComboButtonData);

		}

		/**
		 * @return
		 */
		public String getApplicationName()
		{
			return nameField.getText();
		}
	
		public IOpenVXMLProject getParentProject()
		{
			if(parentConfigButton.getSelection())
			{
				return umbrellaProjects.get(parentCombo.getSelectionIndex());
			}
			return null;
		}
	}

	public class BuildPathPage extends WizardPage
	{
		BrandConfigurationScreen screen = new BrandConfigurationScreen();

		public BuildPathPage()
		{
			super("ConfigureBuildPathPage", "Branding", null);
			screen.init(currentBrandManager);
			this.setPageComplete(true);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
		 */
		public void createControl(Composite parent)
		{
			Composite comp = new Composite(parent, SWT.NONE);
			comp.setLayout(new FillLayout());
			setControl(comp);
			screen.createContents(comp);
		}
		
		public void enableControls(boolean enabled)
		{
			screen.enableControls(enabled);
		}
		
		public void changeBrandManager()
		{
			screen.init(currentBrandManager);
			screen.updateBrands();
		}

		/**
		 * @param project
		 */
		void configureBuildPath(IBrandingProjectAspect project) {
			currentBrandManager.saveTo(project.getBrandManager(), true);
		}
		
	}

	public class InteractionTypePage extends WizardPage
	{
		InteractionTypeConfigurationScreen screen = new InteractionTypeConfigurationScreen();

		public InteractionTypePage()
		{
			super("InteractionTypePage", "Interaction Type Support", null);
			screen.setSupport(currentSupportManager);
			this.setPageComplete(true);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
		 */
		public void createControl(Composite parent)
		{
			Composite comp = new Composite(parent, SWT.NONE);
			comp.setLayout(new FillLayout());
			setControl(comp);
			screen.createContents(comp);
		}

		public void enableControls(boolean enabled)
		{
			screen.enableControls(enabled);
		}
		
		public void updateSupportManager()
		{
			screen.setSupport(currentSupportManager);
		}

		/**
		 * @param project
		 */
		void configureBuildPath(InteractiveProjectAspect project)
		{
		}
		
	}

	public class LanguagePage extends WizardPage
	{
		LanguageConfigurationScreen screen = new LanguageConfigurationScreen();

		public LanguagePage()
		{
			super("LanguagePage", "Languge Support", null);
			screen.init(currentBrandManager, currentSupportManager);
			this.setPageComplete(true);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
		 */
		public void createControl(Composite parent)
		{
			Composite comp = new Composite(parent, SWT.NONE);
			comp.setLayout(new FillLayout());
			setControl(comp);
			screen.createContents(comp);
		}

		public void enableControls(boolean enabled)
		{
			screen.enableControls(enabled);
		}
		
		public void updateManagers()
		{
			screen.init(currentBrandManager, currentSupportManager);
		}

		/**
		 * @param project
		 */
		void configureBuildPath(InteractiveProjectAspect project)
		{
		}
		
	}
}
