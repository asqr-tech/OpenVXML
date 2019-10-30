package com.openmethods.openvxml.desktop.model.webservices.wsdl;

public class Port {
	private String name = null;
	private Binding binding = null;

	public Port(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Binding getBinding() {
		return binding;
	}

	public void setBinding(Binding binding) {
		this.binding = binding;
	}
}
