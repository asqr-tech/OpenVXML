package org.eclipse.vtp.modules.interactive.ui.export;

import org.eclipse.vtp.desktop.export.IConfigurationExporter;
import org.eclipse.vtp.desktop.export.IDefinitionBuilder;
import org.eclipse.vtp.desktop.export.IFlowElement;
import org.eclipse.vtp.desktop.model.interactive.core.content.ContentLoadingManager;
import org.eclipse.vtp.framework.interactions.core.configurations.MediaConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.SubmitConfiguration;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class SubmitConfigurationExporter implements IConfigurationExporter {
	public SubmitConfigurationExporter() {
	}

	@Override
	public void exportConfiguration(IFlowElement flowElement,
			Element actionElement) {
		SubmitConfiguration config = new SubmitConfiguration(
				ContentLoadingManager.getInstance());
		config.setName(flowElement.getName());
		String uri = "http://www.eclipse.org/vtp/namespaces/config"; //$NON-NLS-1$
		Element custom = (Element) flowElement.getConfiguration()
				.getElementsByTagNameNS(uri, "custom-config").item(0); //$NON-NLS-1$
		MediaConfiguration mediaBindings = flowElement.loadMediaBindings("");
		config.setMediaConfiguration(mediaBindings);
		NodeList list = ((Element) custom.getElementsByTagNameNS(uri, "inputs")
				.item(0)).getElementsByTagNameNS(uri, "input");
		for (int i = 0; i < list.getLength(); ++i) {
			Element elem = (Element) list.item(i);
			config.setInput(elem.getAttribute("name"),
					"1".equals(elem.getAttribute("type")),
					elem.getAttribute("value"));
		}
		Element configElement = actionElement.getOwnerDocument()
				.createElementNS(
						IDefinitionBuilder.NAMESPACE_URI_INTERACTIONS_CORE,
						"interactions:submit"); //$NON-NLS-1$
		config.save(configElement);
		actionElement.appendChild(configElement);
	}

	@Override
	public String getActionId(IFlowElement flowElement) {
		return "org.eclipse.vtp.framework.interactions.core.actions.submit";
	}

	@Override
	public String getDefaultPath(IFlowElement flowElement) {
		return null;
	}

	@Override
	public String translatePath(IFlowElement flowElement, String uiPath) {
		return uiPath;
	}

	@Override
	public String getTargetId(IFlowElement flowElement,
			Element afterTransitionElement) {
		return flowElement.getDefaultTargetId(afterTransitionElement);
	}

	@Override
	public boolean isEntryPoint(IFlowElement flowElement) {
		return false;
	}
}
