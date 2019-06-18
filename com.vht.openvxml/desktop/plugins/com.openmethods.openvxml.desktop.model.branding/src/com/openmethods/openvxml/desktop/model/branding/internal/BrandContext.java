/**
 * 
 */
package com.openmethods.openvxml.desktop.model.branding.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.core.configuration.ConfigurationContext;

import com.openmethods.openvxml.desktop.model.branding.IBrand;
import com.openmethods.openvxml.desktop.model.branding.IBrandingProjectAspect;

/**
 * @author trip
 *
 */
public class BrandContext implements ConfigurationContext
{
	public static final String CONTEXT_ID = "com.openmethods.openvxml.desktop.model.branding.brandcontext";
	private static final String CONTEXT_NAME = "Brands";
	private IBrandingProjectAspect brandingAspect = null;

	/**
	 * 
	 */
	public BrandContext()
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.configuration.ConfigurationContext#getId()
	 */
	public String getId()
	{
		return CONTEXT_ID;
	}
	
	public String getName()
	{
		return CONTEXT_NAME;
	}
	
	public void setProject(IOpenVXMLProject project)
	{
		this.brandingAspect = (IBrandingProjectAspect)project.getProjectAspect(IBrandingProjectAspect.ASPECT_ID);
		System.err.println("BrandContext: " + brandingAspect);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.configuration.ConfigurationContext#getLabel(java.lang.Object)
	 */
	public String getLabel(Object obj)
	{
		if(obj instanceof IBrand)
		{
			IBrand brand = (IBrand)obj;
			IBrand parent = brand.getParent();
			int indent = 0;
			while(parent != null)
			{
				indent++;
				parent = parent.getParent();
			}
			StringBuilder builder = new StringBuilder();
			for(int i = 0; i < indent; i++)
			{
				builder.append("   ");
			}
			builder.append(brand.getName());
			return builder.toString();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.configuration.ConfigurationContext#getValues()
	 */
	public List<Object> getValues()
	{
		List<Object> ret = new ArrayList<Object>();
		IBrand defaultBrand = brandingAspect.getBrandManager().getDefaultBrand();
		ret.add(defaultBrand);
		traverseBrands(defaultBrand, ret);
		for(Object obj : ret)
		{
			System.err.println(getLabel(obj));
		}
		return ret;
	}

	private void traverseBrands(IBrand parent, List<Object> brands)
	{
		for(IBrand child : parent.getChildBrands())
		{
			brands.add(child);
			traverseBrands(child, brands);
		}
	}

	public boolean setConfigurationContext(Map<String, Object> values)
	{
		return false;
	}
}
