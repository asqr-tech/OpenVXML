package com.openmethods.openvxml.desktop.model.webservices.schema;

public class ComplexType extends Type
{
	private ContentModel contentModel = null;
	
	public ComplexType(Schema owner, String name)
	{
		super(owner, name);
	}

	public ComplexType(Schema owner, String namespace, String name)
	{
		super(owner, namespace, name);
	}

	public ContentModel getContentModel()
	{
		return contentModel;
	}
	
	public void setContentModel(ContentModel contentModel)
	{
		this.contentModel = contentModel;
	}
}
