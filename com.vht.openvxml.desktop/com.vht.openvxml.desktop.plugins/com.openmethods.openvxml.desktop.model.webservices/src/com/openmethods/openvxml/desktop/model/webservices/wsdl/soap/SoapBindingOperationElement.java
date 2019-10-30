package com.openmethods.openvxml.desktop.model.webservices.wsdl.soap;

import java.util.ArrayList;
import java.util.List;

public class SoapBindingOperationElement {
	private List<SoapHeader> headers = new ArrayList<SoapHeader>();
	private SoapBody body = null;

	public SoapBindingOperationElement() {
		super();
	}

	public List<SoapHeader> getHeaders() {
		return headers;
	}

	public void addHeader(SoapHeader header) {
		headers.add(header);
	}

	public SoapBody getBody() {
		return body;
	}

	public void setBody(SoapBody body) {
		this.body = body;
	}

}
