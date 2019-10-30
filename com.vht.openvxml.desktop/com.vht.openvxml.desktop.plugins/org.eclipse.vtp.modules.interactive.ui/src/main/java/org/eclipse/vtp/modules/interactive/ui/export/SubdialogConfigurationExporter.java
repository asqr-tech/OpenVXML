package org.eclipse.vtp.modules.interactive.ui.export;

import org.eclipse.vtp.desktop.export.IConfigurationExporter;
import org.eclipse.vtp.desktop.export.IDefinitionBuilder;
import org.eclipse.vtp.desktop.export.IFlowElement;
import org.eclipse.vtp.desktop.model.interactive.core.content.ContentLoadingManager;
import org.eclipse.vtp.framework.interactions.core.configurations.ExternalReferenceConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.MediaConfiguration;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class SubdialogConfigurationExporter implements IConfigurationExporter {
	public SubdialogConfigurationExporter() {
	}

	@Override
	public void exportConfiguration(IFlowElement flowElement,
			Element actionElement) {
		ExternalReferenceConfiguration config = new ExternalReferenceConfiguration(
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
		list = ((Element) custom.getElementsByTagNameNS(uri, "params").item(0))
				.getElementsByTagNameNS(uri, "param");
		for (int i = 0; i < list.getLength(); ++i) {
			Element elem = (Element) list.item(i);
			config.setURLParameter(elem.getAttribute("name"),
					"1".equals(elem.getAttribute("type")),
					elem.getAttribute("value"));
		}
		list = ((Element) custom.getElementsByTagNameNS(uri, "outputs").item(0))
				.getElementsByTagNameNS(uri, "output");
		for (int i = 0; i < list.getLength(); ++i) {
			Element elem = (Element) list.item(i);
			config.setOutput(elem.getAttribute("name"),
					elem.getAttribute("value"));
		}
		Element configElement = actionElement.getOwnerDocument()
				.createElementNS(
						IDefinitionBuilder.NAMESPACE_URI_INTERACTIONS_CORE,
						"interactions:external-reference"); //$NON-NLS-1$
		config.save(configElement);
		actionElement.appendChild(configElement);
	}

	@Override
	public String getActionId(IFlowElement flowElement) {
		return "org.eclipse.vtp.framework.interactions.core.actions.external-reference";
	}

	@Override
	public String getDefaultPath(IFlowElement flowElement) {
		return "Continue";
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
