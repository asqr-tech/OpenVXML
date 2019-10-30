package org.eclipse.vtp.desktop.model.elements.core.export;

import org.eclipse.vtp.desktop.export.IConfigurationExporter;
import org.eclipse.vtp.desktop.export.IFlowElement;
import org.eclipse.vtp.desktop.export.IFlowModel;
import org.w3c.dom.Element;

public class DialogElementConfigurationExporter implements
		IConfigurationExporter {
	public DialogElementConfigurationExporter() {
	}

	@Override
	public void exportConfiguration(IFlowElement flowElement,
			Element actionElement) {
	}

	@Override
	public String getActionId(IFlowElement flowElement) {
		return null;
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
		IFlowModel dialogModel = flowElement.getModel().getDefinitionBuilder()
				.getDialogModel(flowElement.getId());
		return dialogModel == null ? null
				: dialogModel.getEntries().isEmpty() ? null : dialogModel
						.getEntries().iterator().next()
						.getTargetID(afterTransitionElement);
	}

	@Override
	public boolean isEntryPoint(IFlowElement flowElement) {
		return false;
	}
}
