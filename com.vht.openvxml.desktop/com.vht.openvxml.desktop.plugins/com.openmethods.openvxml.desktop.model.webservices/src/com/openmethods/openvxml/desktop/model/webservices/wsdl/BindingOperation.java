package com.openmethods.openvxml.desktop.model.webservices.wsdl;

public class BindingOperation {
	private Operation operation = null;

	public BindingOperation(Operation operation) {
		super();
		this.operation = operation;
	}

	public Operation getOperation() {
		return operation;
	}
}
