package org.eclipse.vtp.modules.attacheddata.ui.configuration;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.vtp.desktop.editors.core.configuration.ComponentPropertiesPanel;
import org.eclipse.vtp.desktop.editors.core.configuration.ComponentPropertiesPanelProvider;
import org.eclipse.vtp.desktop.model.interactive.core.IInteractiveProjectAspect;

import com.openmethods.openvxml.desktop.model.workflow.design.IDesignComponent;
import com.openmethods.openvxml.desktop.model.workflow.design.IDesignConnector;

public class ConnectorPropertiesPanelProvider implements
		ComponentPropertiesPanelProvider {

	public ConnectorPropertiesPanelProvider() {
	}

	@Override
	public boolean isApplicableFor(IDesignComponent designComponent) {
		return (designComponent instanceof IDesignConnector)
				&& (designComponent.getDesign().getDocument().getProject()
						.getProjectAspect(IInteractiveProjectAspect.ASPECT_ID) != null);
	}

	@Override
	public List<ComponentPropertiesPanel> getPropertiesPanels(
			IDesignComponent designComponent) {
		List<ComponentPropertiesPanel> ret = new LinkedList<ComponentPropertiesPanel>();
		IDesignConnector connector = (IDesignConnector) designComponent;
		AttachedDataPropertiesPanel attachedDataPropertiesPanel = new AttachedDataPropertiesPanel(
				connector);
		ret.add(attachedDataPropertiesPanel);
		return ret;
	}

}
