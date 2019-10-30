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
package org.eclipse.vtp.framework.interactions.core.services;

import org.eclipse.vtp.framework.common.IBrand;
import org.eclipse.vtp.framework.common.IBrandSelection;
import org.eclipse.vtp.framework.common.IScriptable;
import org.eclipse.vtp.framework.common.IStringObject;
import org.eclipse.vtp.framework.core.ISessionContext;
import org.eclipse.vtp.framework.interactions.core.IInteractionTypeSelection;
import org.eclipse.vtp.framework.interactions.core.ILanguageSelection;
import org.eclipse.vtp.framework.interactions.core.IMediaLibrarySelection;
import org.eclipse.vtp.framework.interactions.core.media.IMediaProvider;
import org.eclipse.vtp.framework.interactions.core.media.IMediaProviderRegistry;
import org.eclipse.vtp.framework.interactions.core.media.IResourceManager;

/**
 * Implementation of {@link ILanguageSelection}.
 * 
 * @author Lonnie Pryor
 */
public class MediaLibrarySelection implements IMediaLibrarySelection,
		IScriptable {
	/** The session context. */
	private final ISessionContext context;
	private ILanguageSelection languageSelection;
	/** The currently selected interaction type. */
	private final IInteractionTypeSelection interactionTypeSelection;
	private IBrandSelection brandSelection;
	private IMediaProviderRegistry mediaProviderRegistry;

	/**
	 * Creates a new LanguageSelection.
	 * 
	 * @param context
	 *            The session context.
	 * @param languageRegistry
	 *            The language registry.
	 */
	public MediaLibrarySelection(ISessionContext context,
			ILanguageSelection languageSelection,
			IBrandSelection brandSelection,
			IInteractionTypeSelection interactionTypeSelection,
			IMediaProviderRegistry mediaProviderRegistry) {
		this.context = context;
		this.languageSelection = languageSelection;
		this.interactionTypeSelection = interactionTypeSelection;
		this.brandSelection = brandSelection;
		this.mediaProviderRegistry = mediaProviderRegistry;
	}

	@Override
	public String getSelectedMediaLibrary() {
		String interactionTypeID = interactionTypeSelection
				.getSelectedInteractionType().getId();
		String languageID = languageSelection.getSelectedLanguage();
		IBrand brand = brandSelection.getSelectedBrand();
		context.info("Brand: " + brand.getId() + "Interaction Type: "
				+ interactionTypeID + "Language: " + languageID);
		String mediaProviderId = mediaProviderRegistry.lookupMediaProviderID(
				brand.getId(), interactionTypeID, languageID);
		IMediaProvider mediaProvider = mediaProviderRegistry
				.getMediaProvider(mediaProviderId);
		IResourceManager resourceManager = mediaProvider.getResourceManager();
		String id = (String) context.getAttribute("library.selection"); //$NON-NLS-1$
		if (id == null || id.equals("")) {
			id = (String) context.getInheritedAttribute("library.selection");
		}
		if (id == null || id.equals("") || !resourceManager.hasMediaLibrary(id)) {
			id = "Default";
		}
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.ILanguageSelection#
	 * setSelectedLanguage(java.lang.String)
	 */
	@Override
	public boolean setSelectedMediaLibrary(String libraryId) {
		context.info("Setting media library to: "
				+ (libraryId == null ? "Default" : libraryId));
		if (libraryId == null) {
			context.clearAttribute("library.selection"); //$NON-NLS-1$
			return true;
		}
		String interactionTypeID = interactionTypeSelection
				.getSelectedInteractionType().getId();
		String languageID = languageSelection.getSelectedLanguage();
		IBrand brand = brandSelection.getSelectedBrand();
		String mediaProviderId = mediaProviderRegistry.lookupMediaProviderID(
				brand.getId(), interactionTypeID, languageID);
		IMediaProvider mediaProvider = mediaProviderRegistry
				.getMediaProvider(mediaProviderId);
		IResourceManager resourceManager = mediaProvider.getResourceManager();
		if (resourceManager.hasMediaLibrary(libraryId)) {
			context.setAttribute("library.selection", libraryId);
			return true;
		} else {
			context.info("Media library "
					+ (libraryId == null ? "Default" : libraryId)
					+ " not found.  Library not modified.");
		}
		return false;
	}

	@Override
	public boolean clearEntry(String name) {
		if ("value".equals(name)) {
			setSelectedMediaLibrary(null);
			return true;
		}
		return false;
	}

	@Override
	public boolean clearItem(int index) {
		return false;
	}

	@Override
	public Object getEntry(String name) {
		if ("value".equals(name)) {
			return getSelectedMediaLibrary();
		}
		return null;
	}

	@Override
	public String[] getFunctionNames() {
		return new String[] {};
	}

	@Override
	public Object getItem(int index) {
		return null;
	}

	@Override
	public String getName() {
		return "SelectedMediaLibrary";
	}

	@Override
	public String[] getPropertyNames() {
		return new String[] { "value" };
	}

	@Override
	public boolean hasEntry(String name) {
		return "value".equals(name);
	}

	@Override
	public boolean hasItem(int index) {
		return false;
	}

	@Override
	public boolean hasValue() {
		return true;
	}

	@Override
	public Object invokeFunction(String name, Object[] arguments) {
		return null;
	}

	@Override
	public boolean setEntry(String name, Object value) {
		if ("value".equals(name)) {
			String val = "";
			if (value instanceof String) {
				val = (String) value;
			} else if (value instanceof IStringObject) {
				val = ((IStringObject) value).getValue();
			}
			return setSelectedMediaLibrary(val);
		}
		return false;
	}

	@Override
	public boolean setItem(int index, Object value) {
		return false;
	}

	@Override
	public Object toValue() {
		return getSelectedMediaLibrary();
	}

	@Override
	public boolean isMutable() {
		return false;
	}
}
