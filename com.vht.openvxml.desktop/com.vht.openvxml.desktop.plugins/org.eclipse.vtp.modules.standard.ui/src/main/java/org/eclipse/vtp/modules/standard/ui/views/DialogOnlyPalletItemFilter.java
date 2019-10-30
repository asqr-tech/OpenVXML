/**
 * 
 */
package org.eclipse.vtp.modules.standard.ui.views;

import org.eclipse.vtp.desktop.model.elements.core.internal.views.PalletItemFilter;

import com.openmethods.openvxml.desktop.model.workflow.design.IDesign;

/**
 * @author trip
 *
 */
public class DialogOnlyPalletItemFilter implements PalletItemFilter
{

	/**
	 * 
	 */
	public DialogOnlyPalletItemFilter()
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.elements.core.internal.views.PalletItemFilter#canBeContainedBy(org.eclipse.vtp.desktop.model.core.design.IDesign)
	 */
	public boolean canBeContainedBy(IDesign design)
	{
		return !design.getDocument().getMainDesign().equals(design);
	}

}
