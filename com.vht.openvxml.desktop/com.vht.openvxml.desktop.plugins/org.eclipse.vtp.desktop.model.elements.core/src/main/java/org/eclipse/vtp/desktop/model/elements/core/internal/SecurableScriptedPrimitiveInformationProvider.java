package org.eclipse.vtp.desktop.model.elements.core.internal;

import org.w3c.dom.Element;

import com.openmethods.openvxml.desktop.model.workflow.design.ISecurableElement;

public class SecurableScriptedPrimitiveInformationProvider extends
		ScriptedPrimitiveInformationProvider implements ISecurableElement {
	boolean secured = false;

	public SecurableScriptedPrimitiveInformationProvider(
			PrimitiveElement element) {
		super(element);
	}

	@Override
	public boolean isSecured() {
		return secured;
	}

	@Override
	public void setSecured(boolean secured) {
		this.secured = secured;
	}

	@Override
	public void readConfiguration(Element configuration) {
		secured = Boolean.parseBoolean(configuration.getAttribute("secured"));
	}

	@Override
	public void writeConfiguration(Element configuration) {
		configuration.setAttribute("secured", Boolean.toString(secured));
	}

}
