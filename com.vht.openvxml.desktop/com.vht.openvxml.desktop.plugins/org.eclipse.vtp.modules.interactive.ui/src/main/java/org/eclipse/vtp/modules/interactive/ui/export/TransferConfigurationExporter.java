package org.eclipse.vtp.modules.interactive.ui.export;

import org.eclipse.vtp.desktop.export.IConfigurationExporter;
import org.eclipse.vtp.desktop.export.IDefinitionBuilder;
import org.eclipse.vtp.desktop.export.IFlowElement;
import org.eclipse.vtp.framework.interactions.core.configurations.MediaConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.TransferMessageConfiguration;
import org.w3c.dom.Element;

public class TransferConfigurationExporter implements IConfigurationExporter {
	public TransferConfigurationExporter() {
	}

	@Override
	public void exportConfiguration(IFlowElement flowElement,
			Element actionElement) {
		MediaConfiguration mediaBindings = flowElement.loadMediaBindings("");
		TransferMessageConfiguration config = new TransferMessageConfiguration();
		config.setType(mediaBindings.getPropertyConfiguration("type"));
		config.setDestination(mediaBindings
				.getPropertyConfiguration("destination"));
		Element configElement = actionElement.getOwnerDocument()
				.createElementNS(
						IDefinitionBuilder.NAMESPACE_URI_INTERACTIONS_CORE,
						"interactions:transfer-message"); //$NON-NLS-1$
		config.save(configElement);
		actionElement.appendChild(configElement);
	}

	@Override
	public String getActionId(IFlowElement flowElement) {
		return "org.eclipse.vtp.framework.interactions.core.actions.transfer-message";
	}

	@Override
	public String getDefaultPath(IFlowElement flowElement) {
		return null;
	}

	@Override
	public String translatePath(IFlowElement flowElement, String uiPath) {
		return null;
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
