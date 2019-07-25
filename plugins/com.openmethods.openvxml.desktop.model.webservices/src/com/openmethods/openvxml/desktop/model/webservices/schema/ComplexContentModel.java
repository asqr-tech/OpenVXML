package com.openmethods.openvxml.desktop.model.webservices.schema;


public class ComplexContentModel extends ContentModel
{
	private boolean mixed = false;
	private ElementGroup elementGroup = null;

	public ComplexContentModel(boolean mixed)
	{
		super();
		this.mixed = mixed;
	}
	
	public boolean isMixedContent()
	{
		return mixed;
	}
	
	public void setMixedContent(boolean mixedContent)
	{
		this.mixed = mixedContent;
	}

	public ElementGroup getElementGroup()
	{
		return elementGroup;
	}
	
	public void setElementGroup(ElementGroup elementGroup)
	{
		this.elementGroup = elementGroup;
	}
}
