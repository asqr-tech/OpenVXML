package org.eclipse.vtp.desktop.editors.core.configuration;

import java.util.List;

import com.openmethods.openvxml.desktop.model.workflow.design.IDesignComponent;

public interface ComponentPropertiesPanelProvider
{
	public boolean isApplicableFor(IDesignComponent designComponent);
	
	public List<ComponentPropertiesPanel> getPropertiesPanels(IDesignComponent designComponent);
}
