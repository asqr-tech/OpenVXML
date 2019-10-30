package com.openmethods.openvxml.desktop.model.webservices.wsdl;

import com.openmethods.openvxml.desktop.model.webservices.schema.Type;

public class TypedPart extends Part {
	private Type type = null;

	public TypedPart(String name) {
		super(name);
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
}
