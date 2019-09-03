package org.eclipse.vtp.modules.webservice.ui.widgets;

import com.openmethods.openvxml.desktop.model.workflow.design.ObjectDefinition;

public interface ObjectDefinitionFilter
{
	public boolean isApplicable(ObjectDefinition definition);
}
