package com.openmethods.openvxml.desktop.model.webservices.wsdl.soap;

import java.util.ArrayList;
import java.util.List;

import com.openmethods.openvxml.desktop.model.webservices.wsdl.BindingOperation;
import com.openmethods.openvxml.desktop.model.webservices.wsdl.Operation;

public class SoapBindingOperation extends BindingOperation
{
	private String style = null;
	private String soapAction = "";
	private SoapBindingOperationElement input = null;
	private SoapBindingOperationElement output = null;
	private List<SoapFault> faults = new ArrayList<SoapFault>();

	public SoapBindingOperation(Operation operation)
	{
		super(operation);
	}

	public String getStyle()
	{
		return style;
	}
	
	public void setStyle(String style)
	{
		this.style = style;
	}
	
	public String getSoapAction()
	{
		return soapAction;
	}
	
	public void setSoapAction(String soapAction)
	{
		this.soapAction = soapAction;
	}
	
	public SoapBindingOperationElement getInput()
	{
		return input;
	}
	
	public void setInput(SoapBindingOperationElement input)
	{
		this.input = input;
	}
	
	public SoapBindingOperationElement getOutput()
	{
		return output;
	}
	
	public void setOutput(SoapBindingOperationElement output)
	{
		this.output = output;
	}

	public List<SoapFault> getFaults()
	{
		return faults;
	}
	
	public void addFault(SoapFault fault)
	{
		faults.add(fault);
	}
}
