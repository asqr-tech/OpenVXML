package org.eclipse.vtp.desktop.editors.core.internal.configuration;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.vtp.desktop.editors.core.configuration.ComponentPropertiesPanel;
import org.eclipse.vtp.desktop.editors.core.configuration.ComponentPropertiesPanelProvider;
import org.eclipse.vtp.desktop.editors.core.configuration.ConnectorPropertiesPanel;

import com.openmethods.openvxml.desktop.model.workflow.design.IDesignComponent;
import com.openmethods.openvxml.desktop.model.workflow.design.IDesignConnector;

public class ConnectorPropertiesPanelProvider implements
		ComponentPropertiesPanelProvider {

	public ConnectorPropertiesPanelProvider() {
	}

	@Override
	public boolean isApplicableFor(IDesignComponent designComponent) {
		return designComponent instanceof IDesignConnector;
	}

	@Override
	public List<ComponentPropertiesPanel> getPropertiesPanels(
			IDesignComponent designComponent) {
		List<ComponentPropertiesPanel> ret = new LinkedList<ComponentPropertiesPanel>();
		IDesignConnector connector = (IDesignConnector) designComponent;
		ConnectorPropertiesPanel connectorPropertiesPanel = new ConnectorPropertiesPanel(
				connector);
		ret.add(connectorPropertiesPanel);
		return ret;
	}

}
