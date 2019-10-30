package com.openmethods.openvxml.desktop.model.webservices;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.vtp.desktop.model.core.IWorkflowResource;

import com.openmethods.openvxml.desktop.model.webservices.wsdl.WSDL;

public interface IWebserviceDescriptor extends IWorkflowResource {
	public WSDL getWSDL() throws Exception;

	/**
	 * @return The parent dependency set of this dependency
	 */
	@Override
	public IWebserviceSet getParent();

	/**
	 * Determines if this dependency exists.
	 *
	 * @return <code>true</code> if the dependency exists, <code>false</code>
	 *         otherwise
	 */
	public boolean exists();

	/**
	 * Removes this dependency from the project structure.
	 *
	 * @throws CoreException
	 *             If an error occured during the deletion
	 */
	public void delete() throws CoreException;

	public IFile getUnderlyingFile();
}
