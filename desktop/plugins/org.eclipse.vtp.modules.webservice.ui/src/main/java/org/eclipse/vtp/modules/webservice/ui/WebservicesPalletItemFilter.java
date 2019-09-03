/**
 * 
 */
package org.eclipse.vtp.modules.webservice.ui;

import org.eclipse.vtp.desktop.model.elements.core.internal.views.PalletItemFilter;

import com.openmethods.openvxml.desktop.model.webservices.IWebserviceProjectAspect;
import com.openmethods.openvxml.desktop.model.workflow.design.IDesign;

/**
 * @author trip
 *
 */
public class WebservicesPalletItemFilter implements PalletItemFilter
{

	/**
	 * 
	 */
	public WebservicesPalletItemFilter()
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.elements.core.internal.views.PalletItemFilter#canBeContainedBy(org.eclipse.vtp.desktop.model.core.design.IDesign)
	 */
	public boolean canBeContainedBy(IDesign design)
	{
		return design.getDocument().getProject().getProjectAspect(IWebserviceProjectAspect.ASPECT_ID) != null;
	}

}
