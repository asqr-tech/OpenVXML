package com.openmethods.openvxml.desktop.model.databases;

import org.eclipse.vtp.desktop.model.core.IOpenVXMLProjectAspect;

public interface IDatabaseProjectAspect extends IOpenVXMLProjectAspect {
	public static String ASPECT_ID = "com.openmethods.openvxml.desktop.model.aspect.databasesupport";

	public IDatabaseSet getDatabaseSet();
}
