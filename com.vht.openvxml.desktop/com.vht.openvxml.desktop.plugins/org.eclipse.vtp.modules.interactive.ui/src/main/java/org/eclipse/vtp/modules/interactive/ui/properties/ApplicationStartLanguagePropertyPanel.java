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
package org.eclipse.vtp.modules.interactive.ui.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.vtp.desktop.editors.core.configuration.DesignElementPropertiesPanel;
import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.eclipse.vtp.desktop.model.interactive.core.ILanguageSupportProjectAspect;
import org.eclipse.vtp.desktop.model.interactive.core.InteractionType;
import org.eclipse.vtp.desktop.model.interactive.core.internal.context.InteractionTypeContext;
import org.eclipse.vtp.modules.standard.ui.BeginInformationProvider;

import com.openmethods.openvxml.desktop.model.workflow.design.IDesignElement;

/**
 * The graphical user interface used to configure an application's begin module
 * 
 * @author Trip Gilman
 */
public class ApplicationStartLanguagePropertyPanel extends
		DesignElementPropertiesPanel {
	private Combo defaultLanguageCombo = null;
	private List<String> languages = null;

	/**
	 * @param name
	 */
	public ApplicationStartLanguagePropertyPanel(String name,
			IDesignElement element) {
		super(name, element);
		IOpenVXMLProject project = getElement().getDesign().getDocument()
				.getProject();
		ILanguageSupportProjectAspect iwp = (ILanguageSupportProjectAspect) project
				.getProjectAspect(ILanguageSupportProjectAspect.ASPECT_ID);
		languages = iwp.getMediaProviderManager().getSupportedLanguages(
				"org.eclipse.vtp.framework.interactions.voice.interaction");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.desktop.editors.core.elements.PrimitivePropertiesPanel
	 * #createControls(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControls(Composite parent) {
		parent.setLayout(new GridLayout(2, false));
		parent.setBackgroundMode(SWT.INHERIT_DEFAULT);

		Composite nameComp = new Composite(parent, SWT.NONE);
		nameComp.setBackground(parent.getBackground());
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		nameComp.setLayoutData(gd);
		nameComp.setLayout(new GridLayout(2, false));

		Label defaultLanguageLabel = new Label(nameComp, SWT.NONE);
		defaultLanguageLabel.setText("Default Language");
		defaultLanguageLabel.setLayoutData(new GridData());
		defaultLanguageCombo = new Combo(nameComp, SWT.READ_ONLY
				| SWT.DROP_DOWN);
		defaultLanguageCombo.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));
		for (String lang : languages) {
			defaultLanguageCombo.add(lang);
		}
		String currentDefault = ((BeginInformationProvider) ((PrimitiveElement) getElement())
				.getInformationProvider()).getDefaultLanguage();
		for (int i = 0; i < languages.size(); i++) {
			if (languages.get(i).equals(currentDefault)) {
				defaultLanguageCombo.select(i);
				break;
			}
		}
		if (defaultLanguageCombo.getSelectionIndex() == -1) {
			defaultLanguageCombo.select(0);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.desktop.editors.core.elements.PrimitivePropertiesPanel
	 * #save()
	 */
	@Override
	public void save() {
		((BeginInformationProvider) ((PrimitiveElement) getElement())
				.getInformationProvider()).setDefaultLanguage(languages
				.get(defaultLanguageCombo.getSelectionIndex()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.desktop.model.core.configuration.ComponentPropertiesPanel
	 * #cancel()
	 */
	@Override
	public void cancel() {
	}

	@Override
	public void setConfigurationContext(Map<String, Object> values) {
		Object object = values.get(InteractionTypeContext.CONTEXT_ID);
		defaultLanguageCombo
				.setEnabled("org.eclipse.vtp.framework.interactions.voice.interaction"
						.equals(((InteractionType) object).getId()));
	}

	@Override
	public List<String> getApplicableContexts() {
		List<String> ret = new ArrayList<String>();
		ret.add(InteractionTypeContext.CONTEXT_ID);
		return ret;
	}

	@Override
	public int getRanking() {
		return 10;
	}
}
