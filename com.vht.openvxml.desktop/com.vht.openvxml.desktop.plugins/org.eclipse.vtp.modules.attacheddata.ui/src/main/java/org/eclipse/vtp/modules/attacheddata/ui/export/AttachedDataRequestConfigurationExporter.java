package org.eclipse.vtp.modules.attacheddata.ui.export;

import org.eclipse.vtp.desktop.export.IConfigurationExporter;
import org.eclipse.vtp.desktop.export.IDefinitionBuilder;
import org.eclipse.vtp.desktop.export.IFlowElement;
import org.eclipse.vtp.framework.interactions.core.configurations.InteractionsConstants;
import org.eclipse.vtp.framework.interactions.core.configurations.MetaDataRequestConfiguration;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class AttachedDataRequestConfigurationExporter implements
		IConfigurationExporter, InteractionsConstants {

	public AttachedDataRequestConfigurationExporter() {
	}

	@Override
	public void exportConfiguration(IFlowElement flowElement,
			Element actionElement) {
		String uri = "http://www.eclipse.org/vtp/namespaces/config";
		NodeList metadataRequestNodeList = ((Element) flowElement
				.getConfiguration()
				.getElementsByTagNameNS(uri, "custom-config").item(0))
				.getElementsByTagNameNS(uri, "meta-data-request");
		if (metadataRequestNodeList.getLength() > 0) {
			Element metadataRequestElement = (Element) metadataRequestNodeList
					.item(0);
			MetaDataRequestConfiguration config = new MetaDataRequestConfiguration();
			config.setInput(metadataRequestElement.getAttribute("input"));
			config.setOutput(metadataRequestElement.getAttribute("output"));
			Element configElement = actionElement.getOwnerDocument()
					.createElementNS(
							IDefinitionBuilder.NAMESPACE_URI_INTERACTIONS_CORE,
							"common:meta-data-request");
			config.save(configElement);
			actionElement.appendChild(configElement);
		}
	}

	@Override
	public String getActionId(IFlowElement flowElement) {
		return "org.eclipse.vtp.framework.interactions.core.actions.meta-data-request";
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
