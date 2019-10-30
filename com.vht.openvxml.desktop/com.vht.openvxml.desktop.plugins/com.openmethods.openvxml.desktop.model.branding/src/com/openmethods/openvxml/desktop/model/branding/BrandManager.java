/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006-2009 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package com.openmethods.openvxml.desktop.model.branding;

public interface BrandManager {
	public IBrand getDefaultBrand();

	/**
	 * @param name
	 * @return
	 * @deprecated
	 */
	@Deprecated
	public IBrand getBrandByName(String name);

	public IBrand getBrandById(String id);

	public IBrand getBrandByPath(String path);

	public boolean checkBrandName(IBrand parent, String name);

	public void fireBrandIdChanged(IBrand brand, String oldId);

	public void fireBrandNameChanged(IBrand brand, String oldName);

	public void fireBrandParentChanged(IBrand brand, IBrand oldParent);

	public void fireBrandAdded(IBrand brand);

	public void fireBrandRemoved(IBrand brand);

	public void addListener(BrandManagerListener listener);

	public void removeListener(BrandManagerListener listener);

}
