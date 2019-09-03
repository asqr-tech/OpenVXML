package org.eclipse.vtp.modules.webservice.ui.properties;

import com.openmethods.openvxml.desktop.model.webservices.wsdl.soap.SoapBindingOperation;

public interface IOperationListener
{
	public void operationChanged(SoapBindingOperation operation);
}
