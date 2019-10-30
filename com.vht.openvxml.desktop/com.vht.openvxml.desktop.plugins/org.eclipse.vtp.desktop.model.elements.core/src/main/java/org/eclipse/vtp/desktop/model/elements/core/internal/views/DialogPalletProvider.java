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
package org.eclipse.vtp.desktop.model.elements.core.internal.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.vtp.desktop.model.elements.core.Activator;
import org.eclipse.vtp.desktop.model.elements.core.internal.DialogElementFactory;
import org.eclipse.vtp.desktop.model.elements.core.internal.DialogElementManager;
import org.eclipse.vtp.desktop.views.pallet.PalletItem;
import org.eclipse.vtp.desktop.views.pallet.PalletItemObserver;
import org.eclipse.vtp.desktop.views.pallet.PalletItemProvider;

import com.openmethods.openvxml.desktop.model.workflow.design.IDesign;

public class DialogPalletProvider implements PalletItemProvider,
		Activator.LocalDialogListener {
	List<PalletItem> primitiveItems = new ArrayList<PalletItem>();
	List<PalletItemObserver> observers = new ArrayList<PalletItemObserver>();

	public DialogPalletProvider() {
		super();
		Activator.getDefault().addListener(this);
		loadItems();
	}

	private void loadItems() {
		IConfigurationElement[] primitiveExtensions = Platform
				.getExtensionRegistry().getConfigurationElementsFor(
						DialogElementManager.dialogExtensionPointId);
		for (IConfigurationElement primitiveExtension : primitiveExtensions) {
			String id = primitiveExtension.getAttribute("id");
			String name = primitiveExtension.getAttribute("name");
			primitiveItems.add(new PalletItem(name, null,
					new DialogElementFactory(), id) {
				@Override
				public boolean canBeContainedBy(IDesign design) {
					return design.equals(design.getDocument().getMainDesign());
				}
			});
		}
		List<Activator.LocalDialogRecord> localDialogs = Activator.getDefault()
				.listLocalDialogs();
		for (Activator.LocalDialogRecord record : localDialogs) {
			primitiveItems.add(new PalletItem(record.getName(), null,
					new DialogElementFactory(), record.getId()) {
				@Override
				public boolean canBeContainedBy(IDesign design) {
					return design.equals(design.getDocument().getMainDesign());
				}
			});
		}
	}

	@Override
	public String getName() {
		return "Dialogs";
	}

	@Override
	public List<PalletItem> getPalletItems() {
		return primitiveItems;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.desktop.views.pallet.PalletItemProvider#getRanking()
	 */
	@Override
	public int getRanking() {
		return 20;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.desktop.views.pallet.PalletItemProvider#createMenu(
	 * org.eclipse.jface.action.IMenuManager,
	 * org.eclipse.vtp.desktop.views.pallet.PalletItem[])
	 */
	@Override
	public void createMenu(final IAdaptable container, IMenuManager manager,
			final PalletItem[] selectedItems) {
		if (selectedItems.length == 1
				&& Activator.getDefault().getLocalDialog(
						(String) selectedItems[0].getData()) != null) {
			manager.add(new Action("Delete Dialog...") {
				@Override
				public void run() {
					Activator.getDefault().removeLocalDialog(
							(String) selectedItems[0].getData());
				}
			});
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.desktop.views.pallet.PalletItemProvider#
	 * addPalletItemObserver(
	 * org.eclipse.vtp.desktop.views.pallet.PalletItemObserver)
	 */
	@Override
	public void addPalletItemObserver(PalletItemObserver observer) {
		observers.remove(observer);
		observers.add(observer);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.desktop.views.pallet.PalletItemProvider#
	 * removePalletItemObserver(
	 * org.eclipse.vtp.desktop.views.pallet.PalletItemObserver)
	 */
	@Override
	public void removePalletItemObserver(PalletItemObserver observer) {
		observers.remove(observer);

	}

	public void fireChange() {
		for (PalletItemObserver observer : observers) {
			observer.palletItemsChanged();
		}
	}

	@Override
	public void localDialogsChanged() {
		primitiveItems.clear();
		loadItems();
		fireChange();
	}

}
