package org.eclipse.vtp.desktop.model.interactive.core;

import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;

public interface IMediaLibrariesFolder extends IMediaObjectContainer {
	/**
	 * @return The root folder for all media resources of this persona project
	 */
	public List<IMediaLibrary> getMediaLibraries() throws CoreException;

	public IMediaLibrary getMediaLibrary(String name);

	public IFolder getUnderlyingFolder();

}
