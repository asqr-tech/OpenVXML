package com.openmethods.openvxml.platforms.genesys.vxml;

import java.io.IOException;

import org.eclipse.vtp.framework.interactions.voice.vxml.Action;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class Receive extends Action {
	// private String contentType = null;
	// private String body = null;
	// private String nameList = null;
	private String maxtime = "10s";
	private String namespace = NAMESPACE_URI_VXML;
	private boolean gvpPrefix = false;

	public Receive() {
		super();
	}

	// public String getContentType()
	// {
	// return contentType;
	// }
	//
	// public void setContentType(String contentType)
	// {
	// this.contentType = contentType;
	// }
	//
	// public String getBody()
	// {
	// return body;
	// }
	//
	// public void setBody(String body)
	// {
	// this.body = body;
	// }
	//
	// public String getNameList()
	// {
	// return nameList;
	// }
	//
	// public void setNameList(String nameList)
	// {
	// this.nameList = nameList;
	// }

	public void setMaxtime(String maxtime) {
		this.maxtime = maxtime;
	}

	public String getMaxtime() {
		return maxtime;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public void setGvpPrefix(boolean gvpPrefix) {
		this.gvpPrefix = gvpPrefix;
	}

	@Override
	public void writeWidget(ContentHandler outputHandler)
			throws NullPointerException, SAXException {
		AttributesImpl attributes = new AttributesImpl();
		writeAttributes(attributes);
		if (gvpPrefix) {
			outputHandler.startElement(null, "gvp:receive", "gvp:receive",
					attributes);
			outputHandler.endElement(null, "gvp:receive", "gvp:receive");

		} else {
			outputHandler.startElement(namespace, "receive", "receive",
					attributes);
			outputHandler.endElement(namespace, "receive", "receive");

		}
	}

	/**
	 * @param out
	 * @throws IOException
	 */
	protected void writeAttributes(AttributesImpl attributes) {
		// § super.writeAttributes(attributes);
		// if(!NAMESPACE_URI_VXML.equals(namespace))
		// writeAttribute(attributes, null, null, "xmlns", TYPE_CDATA,
		// namespace);
		writeAttribute(attributes, null, null, "maxtime", TYPE_CDATA, maxtime);
		// if(contentType != null)
		// writeAttribute(attributes, null, null, "contenttype", TYPE_CDATA,
		// contentType);
		// if(body != null)
		// {
		// writeAttribute(attributes, null, null, "body", TYPE_CDATA, body);
		// }
		// else
		// writeAttribute(attributes, null, null, "nameList", TYPE_CDATA,
		// nameList);
	}

}
