package org.eclipse.vtp.desktop.projects.interactive.core.wizards;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.core.internal.OpenVXMLProject;
import org.eclipse.vtp.desktop.model.interactive.core.ILanguageSupportProjectAspect;
import org.eclipse.vtp.desktop.model.interactive.core.internal.LanguageSupportProjectAspect;
import org.eclipse.vtp.desktop.projects.core.util.ConfigurationBrandManager;
import org.eclipse.vtp.desktop.projects.interactive.core.util.InteractionSupportManager;

import com.openmethods.openvxml.desktop.model.branding.IBrandingProjectAspect;

public class DisconnectInteractiveWorkflowWizard extends Wizard {
	private OpenVXMLProject vxmlProject;
	private IOpenVXMLProject umbrellaProject;
	private ConfigurationBrandManager parentBrandManager;
	private InteractionSupportManager parentInteractionManager;

	public DisconnectInteractiveWorkflowWizard(OpenVXMLProject vxmlProject) {
		super();
		this.vxmlProject = vxmlProject;
		this.umbrellaProject = vxmlProject.getParentProject();
		IBrandingProjectAspect brandingAspect = (IBrandingProjectAspect) umbrellaProject
				.getProjectAspect(IBrandingProjectAspect.ASPECT_ID);
		parentBrandManager = new ConfigurationBrandManager(
				brandingAspect.getBrandManager());
		LanguageSupportProjectAspect languageAspect = (LanguageSupportProjectAspect) umbrellaProject
				.getProjectAspect(ILanguageSupportProjectAspect.ASPECT_ID);
		parentInteractionManager = new InteractionSupportManager();
		parentInteractionManager.init(languageAspect
				.getInteractionTypeSupport());
		this.addPage(new ConfirmPage());
	}

	@Override
	public boolean performFinish() {
		vxmlProject.setParentProject(null);
		IBrandingProjectAspect childBrandingAspect = (IBrandingProjectAspect) vxmlProject
				.getProjectAspect(IBrandingProjectAspect.ASPECT_ID);
		parentBrandManager.saveTo(childBrandingAspect.getBrandManager(), true);
		LanguageSupportProjectAspect childLanguageAspect = (LanguageSupportProjectAspect) vxmlProject
				.getProjectAspect(ILanguageSupportProjectAspect.ASPECT_ID);
		if (childLanguageAspect != null) {
			childLanguageAspect
					.setInteractionTypeSupport(parentInteractionManager
							.getSupport());
		}
		vxmlProject.storeBuildPath();
		return true;
	}

	public class ConfirmPage extends WizardPage {
		public ConfirmPage() {
			super("Confirm", "Disconnect from Umbrella project", null);
		}

		@Override
		public void createControl(Composite parent) {
			Composite comp = new Composite(parent, SWT.NONE);
			comp.setLayout(new FillLayout());
			Label label = new Label(comp, SWT.WRAP);
			label.setText("This will disconnect the "
					+ vxmlProject.getName()
					+ " project from its umbrella project "
					+ umbrellaProject.getName()
					+ ".  All settings currently inherited will be copied into "
					+ vxmlProject.getName()
					+ ".\r\nNote: You will need to close and reopen the project properties dialog to make changes.");
			setControl(comp);
		}

	}
}
