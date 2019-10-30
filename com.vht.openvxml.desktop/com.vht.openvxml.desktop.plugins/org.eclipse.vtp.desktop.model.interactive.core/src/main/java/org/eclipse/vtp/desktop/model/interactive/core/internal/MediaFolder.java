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
package org.eclipse.vtp.desktop.model.interactive.core.internal;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaContainer;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaFile;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaFolder;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaObject;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaResource;

/**
 * This is a concrete implementation of <code>IMediaFilesFolder</code> and
 * provides the default behavior of that interface.
 *
 * @author Trip Gilman
 * @version 2.0
 */
public class MediaFolder extends MediaResource implements IMediaFolder {
	private static final String HASHPREFIX = "MEDIAFOLDER";
	/**
	 * The eclipse folder resource this media folder represents.
	 */
	IFolder folder;

	/**
	 * Creates a new <code>MediaFolder</code> in the provided container with the
	 * given eclipse folder resource.
	 *
	 * @param container
	 *            The parent media container
	 * @param folder
	 *            The eclipse folder resource this media folder represents
	 */
	public MediaFolder(IMediaContainer container, IFolder folder) {
		super(container, folder);
		this.folder = folder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.desktop.core.project.IMediaContainer#listMediaResources()
	 */
	@Override
	public List<IMediaResource> listMediaResources() throws CoreException {
		List<IMediaResource> ret = new ArrayList<IMediaResource>();
		IResource[] res = folder.members();

		for (IResource re : res) {
			if (re.getName().startsWith(".")) {
				continue;
			}

			if (re instanceof IFolder) {
				IFolder f = (IFolder) re;
				ret.add(new MediaFolder(this, f));
			} else {
				IFile f = (IFile) re;
				IMediaFile mf = MediaTypeManager.getInstance().createMediaFile(
						this, f);
				if (mf != null) {
					ret.add(mf);
				}
			}
		}

		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.desktop.core.project.IMediaContainer#getMediaFolder(java
	 * .lang.String)
	 */
	@Override
	public IMediaFolder getMediaFolder(String name) {
		IFolder f = folder.getFolder(name);

		return new MediaFolder(this, f);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.desktop.core.project.IMediaContainer#getMediaFile(java
	 * .lang.String)
	 */
	@Override
	public IMediaFile getMediaFile(String name) {
		IFile f = folder.getFile(name);
		IMediaFile mf = MediaTypeManager.getInstance().createMediaFile(this, f);
		return mf;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.desktop.core.project.IMediaContainer#create(java.lang
	 * .String)
	 */
	@Override
	public IMediaFile create(String name) throws CoreException {
		return create(name, new ByteArrayInputStream(new byte[0]), null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.desktop.core.project.IMediaContainer#create(java.lang
	 * .String, java.io.InputStream, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IMediaFile create(String name, InputStream source,
			IProgressMonitor monitor) throws CoreException {
		IFile file = folder.getFile(name);
		IMediaFile mf = MediaTypeManager.getInstance().createMediaFile(this,
				file);
		if (mf != null) {
			file.create(source, false, null);
		}
		return mf;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.desktop.core.project.IMediaContainer#makeDirectory(java
	 * .lang.String)
	 */
	@Override
	public IMediaFolder makeDirectory(String name) throws CoreException {
		IFolder f = folder.getFolder(name);
		f.create(false, true, null);

		return new MediaFolder(this, f);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object o) {
		try {
			return listMediaResources().toArray();
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return new Object[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.model.IWorkbenchAdapter#getImageDescriptor(java.lang.Object
	 * )
	 */
	public ImageDescriptor getImageDescriptor(Object object) {
		return null;
	}

	@Override
	public List<IMediaObject> getChildren() {
		try {
			return new LinkedList<IMediaObject>(this.listMediaResources());
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

	@Override
	public IFolder getUnderlyingFolder() {
		return folder;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapterClass) {
		if (adapterClass.isAssignableFrom(IFolder.class)) {
			return folder;
		}
		return super.getAdapter(adapterClass);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MediaFolder) {
			return folder.equals(((MediaFolder) obj).getUnderlyingFolder());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (HASHPREFIX + folder.toString()).hashCode();
	}
}
