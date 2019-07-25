package com.openmethods.openvxml.desktop.model.webservices.wsdl;

import com.openmethods.openvxml.desktop.model.webservices.schema.ElementItem;

public class ElementPart extends Part
{
	private ElementItem elementItem = null;

	public ElementPart(String name)
	{
		super(name);
	}

	public ElementItem getElementItem()
	{
		return elementItem;
	}
	
	public void setElementItem(ElementItem elementItem)
	{
		this.elementItem = elementItem;
	}
}
