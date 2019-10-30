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
package org.eclipse.vtp.desktop.views.pallet;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.IPage;
import org.eclipse.vtp.desktop.views.Activator;

import com.openmethods.openvxml.desktop.model.workflow.design.IDesign;

public class PalletPage implements IPage, IPropertyChangeListener,
		PalletFocusListener {
	private Composite comp;
	private Pallet pallet;
	private IAdaptable container;

	public PalletPage() {
		super();
		Activator.getDefault().getPreferenceStore()
				.addPropertyChangeListener(this);
		pallet = PalletManager.getDefault().createCurrentPallet();
	}

	/**
	 * @param container
	 */
	public void setContainer(IAdaptable container) {
		if (this.container != null) {
			PalletFocusProvider focusProvider = (PalletFocusProvider) container
					.getAdapter(PalletFocusProvider.class);
			if (focusProvider != null) {
				focusProvider.removeFocusListener(this);
			}
		}
		this.container = container;
		pallet.setContainer((IDesign) container.getAdapter(IDesign.class));
		PalletFocusProvider focusProvider = (PalletFocusProvider) container
				.getAdapter(PalletFocusProvider.class);
		if (focusProvider != null) {
			focusProvider.addFocusListener(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.IPage#createControl(org.eclipse.swt.widgets.Composite
	 * )
	 */
	@Override
	public void createControl(Composite parent) {
		try {
			comp = new Composite(parent, SWT.NONE);
			// comp.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_BLUE));
			comp.setLayout(new FillLayout());
			pallet.createControl(comp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.IPage#dispose()
	 */
	@Override
	public void dispose() {
		Activator.getDefault().getPreferenceStore()
				.removePropertyChangeListener(this);
		pallet.destroy();
		comp.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.IPage#getControl()
	 */
	@Override
	public Control getControl() {
		return comp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.IPage#setActionBars(org.eclipse.ui.IActionBars)
	 */
	@Override
	public void setActionBars(IActionBars actionBars) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.IPage#setFocus()
	 */
	@Override
	public void setFocus() {
		comp.setFocus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse
	 * .jface.util.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals("CurrentPallet")) {
			pallet.destroy();
			// Composite parent = comp.getParent();
			// comp.dispose();
			pallet = PalletManager.getDefault().createCurrentPallet();
			pallet.setContainer((IDesign) container.getAdapter(IDesign.class));
			pallet.createControl(comp);
			comp.layout(true, true);
		}
	}

	@Override
	public void focusChanged(IDesign design) {
		pallet.destroy();
		// Composite parent = comp.getParent();
		// comp.dispose();
		pallet = PalletManager.getDefault().createCurrentPallet();
		pallet.setContainer(design);
		pallet.createControl(comp);
		comp.layout(true, true);
	}

}
