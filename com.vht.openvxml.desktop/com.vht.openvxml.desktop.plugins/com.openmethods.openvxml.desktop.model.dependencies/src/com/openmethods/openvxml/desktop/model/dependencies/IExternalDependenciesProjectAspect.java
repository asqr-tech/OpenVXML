package com.openmethods.openvxml.desktop.model.dependencies;

import org.eclipse.vtp.desktop.model.core.IOpenVXMLProjectAspect;

public interface IExternalDependenciesProjectAspect extends
		IOpenVXMLProjectAspect {
	public static String ASPECT_ID = "com.openmethods.openvxml.desktop.model.aspect.dependencies";

	/**
	 * @return The <code>IDependencySet</code> folder resource that contains the
	 *         set of web services defined for this application
	 */
	public IDependencySet getDependencySet();

}
