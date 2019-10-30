package org.eclipse.vtp.desktop.projects.interactive.core.view;

import java.text.Collator;

import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaFile;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaFolder;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaLibrariesFolder;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaLibrary;
import org.eclipse.vtp.desktop.model.interactive.core.IPromptSet;

public class MediaViewerSorter extends ViewerSorter {

	public MediaViewerSorter() {
		// TODO Auto-generated constructor stub
	}

	public MediaViewerSorter(Collator collator) {
		super(collator);
	}

	@Override
	public int category(Object element) {
		if (element instanceof IPromptSet) {
			return 1;
		} else if (element instanceof IMediaLibrariesFolder) {
			return 2;
		} else if (element instanceof IMediaLibrary) {
			if (((IMediaLibrary) element).getName().equals("Default")) {
				return 1;
			}
			return 2;
		} else if (element instanceof IMediaFolder) {
			return 2;
		} else if (element instanceof IMediaFile) {
			return 3;
		}
		return Integer.MAX_VALUE;
	}

}
