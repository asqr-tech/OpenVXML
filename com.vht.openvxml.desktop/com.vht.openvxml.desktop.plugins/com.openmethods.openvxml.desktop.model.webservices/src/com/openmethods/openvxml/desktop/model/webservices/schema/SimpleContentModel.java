package com.openmethods.openvxml.desktop.model.webservices.schema;

public class SimpleContentModel extends ContentModel {
	private SimpleType contentType = null;

	public SimpleContentModel() {
		super();
	}

	public SimpleType getContentType() {
		return contentType;
	}

	public void setContentType(SimpleType contentType) {
		this.contentType = contentType;
	}
}
