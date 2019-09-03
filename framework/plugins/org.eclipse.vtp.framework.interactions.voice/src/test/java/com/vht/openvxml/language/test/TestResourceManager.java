package com.vht.openvxml.language.test;

import org.eclipse.vtp.framework.interactions.core.media.IResourceManager;

public class TestResourceManager implements IResourceManager {

	@Override
	public boolean isFileResource(String fullFilePath) {
		if(fullFilePath.contains(".wav")){
			return true;
		}
		return false;
	}

	@Override
	public boolean isDirectoryResource(String fullDirectoryPath) {
		return false;
	}

	@Override
	public String[] listResources(String fullDirectoryPath) {
		return null;
	}

	@Override
	public boolean hasMediaLibrary(String libraryId) {
		return false;
	}

}
