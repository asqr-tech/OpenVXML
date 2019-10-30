package org.eclipse.vtp.desktop.model.elements.core.internal.views;

import com.openmethods.openvxml.desktop.model.workflow.design.IDesign;

public interface PalletItemFilter {
	public boolean canBeContainedBy(IDesign design);
}
