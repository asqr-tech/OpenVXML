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
package com.openmethods.openvxml.desktop.model.dependencies.internal;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.vtp.desktop.model.core.IWorkflowResource;
import org.eclipse.vtp.desktop.model.core.event.ObjectRefreshEvent;
import org.eclipse.vtp.desktop.model.core.internal.WorkflowResource;

import com.openmethods.openvxml.desktop.model.dependencies.IDependency;
import com.openmethods.openvxml.desktop.model.dependencies.IDependencySet;
import com.openmethods.openvxml.desktop.model.dependencies.IExternalDependenciesProjectAspect;

/**
 * DependencySet.
 * 
 * @author Lonnie Pryor
 */
public class DependencySet extends WorkflowResource implements IDependencySet {
	/** Comment for project. */
	private final IExternalDependenciesProjectAspect aspect;
	/** Comment for folder. */
	private final IFolder folder;

	/**
	 * Creates a new DependencySet.
	 * 
	 * @param project
	 * @param folder
	 */
	public DependencySet(final IExternalDependenciesProjectAspect aspect,
			final IFolder folder) {
		this.aspect = aspect;
		this.folder = folder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.desktop.core.project.internals.VoiceResource#getObjectId
	 * ()
	 */
	@Override
	protected String getObjectId() {
		return folder.getFullPath().toPortableString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.desktop.core.project.IVoiceResource#getName()
	 */
	@Override
	public String getName() {
		return folder.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.desktop.core.project.IVoiceResource#getParent()
	 */
	@Override
	public IWorkflowResource getParent() {
		return aspect.getHostProject();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.desktop.core.project.IDependencySet#getDependencies()
	 */
	@Override
	public List<IDependency> getDependencies() {
		List<IDependency> results = new LinkedList<IDependency>();
		try {
			IResource[] members = folder.members();
			for (IResource member : members) {
				if (member.getType() == IResource.FILE) {
					results.add(new Dependency(this, (IFile) member));
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.desktop.core.project.IDependencySet#createDependency(
	 * java.lang.String, java.net.URL)
	 */
	@Override
	public IDependency createDependency(String name, InputStream content) {
		IFile file = folder.getFile(name);
		try {
			if (!file.exists()) {
				file.create(content, true, null);
			} else {
				file.setContents(content, true, false, null);
			}
		} catch (CoreException e) {
			e.printStackTrace();
			return null;
		}
		processObjectEvent(new ObjectRefreshEvent(getObjectId()));
		refresh();
		return new Dependency(this, file);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.desktop.core.project.IDependencySet#removeDependency(
	 * org.eclipse.vtp.desktop.core.project.IDependency)
	 */
	@Override
	public void removeDependency(IDependency dependency) {
		IFile file = folder.getFile(dependency.getName());
		try {
			if (file.exists()) {
				file.delete(true, null);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		processObjectEvent(new ObjectRefreshEvent(getObjectId()));
		refresh();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.desktop.model.core.internal.WorkflowResource#getAdapter
	 * (java.lang.Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapterClass) {
		if (IResource.class.isAssignableFrom(adapterClass)
				&& adapterClass.isAssignableFrom(folder.getClass())) {
			return folder;
		}
		if (DependencySet.class.isAssignableFrom(adapterClass)) {
			return this;
		}
		return super.getAdapter(adapterClass);
	}

	@Override
	public List<IWorkflowResource> getChildren() {
		return new LinkedList<IWorkflowResource>(getDependencies());
	}

	@Override
	public IFolder getUnderlyingFolder() {
		return folder;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DependencySet) {
			return folder.equals(((DependencySet) obj).getUnderlyingFolder());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return folder.toString().hashCode();
	}
}
