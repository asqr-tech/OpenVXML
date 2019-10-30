package org.eclipse.vtp.desktop.model.legacy.v4_0To5_0;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.vtp.desktop.model.interactive.voice.natures.VoiceProjectNature5_0;

public class VoiceConverter {

	public VoiceConverter() {
	}

	public void convertVoice(IProject project) {
		try {
			IFolder mediaFilesFolder = project.getFolder("Media Files");
			if (mediaFilesFolder.exists()) {
				IFolder mediaLibrariesFolder = project
						.getFolder("Media Libraries");
				mediaLibrariesFolder.create(true, true, null);
				IFolder defaultLibraryFolder = mediaLibrariesFolder
						.getFolder("Default");
				mediaFilesFolder.move(defaultLibraryFolder.getFullPath(), true,
						null);
				IFile dot = defaultLibraryFolder.getFile(".library");
				dot.create(new ByteArrayInputStream("".getBytes()), true, null);
				IProjectDescription desc = project.getDescription();
				desc.setNatureIds(new String[] { VoiceProjectNature5_0.NATURE_ID });
				project.setDescription(desc, null);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
