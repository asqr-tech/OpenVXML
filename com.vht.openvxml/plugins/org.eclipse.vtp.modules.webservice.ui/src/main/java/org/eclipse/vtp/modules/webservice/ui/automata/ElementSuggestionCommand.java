package org.eclipse.vtp.modules.webservice.ui.automata;

import com.openmethods.openvxml.desktop.model.webservices.schema.ElementItem;

public class ElementSuggestionCommand extends SuggestionCommand
{
	private ElementItem element;

	public ElementSuggestionCommand(ElementItem element)
	{
		super();
		this.element = element;
	}

	public ElementItem getElement()
	{
		return element;
	}
}
