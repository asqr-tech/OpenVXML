package com.openmethods.openvxml.desktop.model.workflow;

import com.openmethods.openvxml.desktop.model.workflow.design.IDesign;

public interface IDesignDocumentListener
{
	public void dialogDesignAdded(IDesignDocument designDocument, IDesign dialogDesign);
	
	public void dialogDesignRemoved(IDesignDocument designDocument, String dialogId);
}
