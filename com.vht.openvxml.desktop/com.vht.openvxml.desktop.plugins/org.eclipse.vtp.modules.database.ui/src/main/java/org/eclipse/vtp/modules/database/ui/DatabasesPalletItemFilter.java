/**
 * 
 */
package org.eclipse.vtp.modules.database.ui;

import org.eclipse.vtp.desktop.model.elements.core.internal.views.PalletItemFilter;

import com.openmethods.openvxml.desktop.model.databases.IDatabaseProjectAspect;
import com.openmethods.openvxml.desktop.model.workflow.design.IDesign;

/**
 * @author trip
 *
 */
public class DatabasesPalletItemFilter implements PalletItemFilter {

	/**
	 * 
	 */
	public DatabasesPalletItemFilter() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.desktop.model.elements.core.internal.views.PalletItemFilter
	 * #canBeContainedBy(org.eclipse.vtp.desktop.model.core.design.IDesign)
	 */
	@Override
	public boolean canBeContainedBy(IDesign design) {
		return design.getDocument().getProject()
				.getProjectAspect(IDatabaseProjectAspect.ASPECT_ID) != null;
	}

}
