/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006-2009 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.desktop.editors.themes.mantis;

import org.eclipse.vtp.desktop.editors.themes.core.CanvasFrame;
import org.eclipse.vtp.desktop.editors.themes.core.ConnectorFrame;
import org.eclipse.vtp.desktop.editors.themes.core.ElementFrame;
import org.eclipse.vtp.desktop.editors.themes.core.Theme;

import com.openmethods.openvxml.desktop.model.workflow.design.IDesign;
import com.openmethods.openvxml.desktop.model.workflow.design.IDesignConnector;
import com.openmethods.openvxml.desktop.model.workflow.design.IDesignElement;

/**
 * The main entry point for the Mantis theme. This class acts as a factory for
 * all the Mantis theme frame types.
 * 
 * @author trip
 */
public class MantisTheme implements Theme {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.desktop.editors.core.theme.Theme#createCanvasFrame(org
	 * .eclipse.vtp.desktop.editors.core.model.UICanvas)
	 */
	@Override
	public CanvasFrame createCanvasFrame(IDesign canvas) {
		return new MantisCanvasFrame(canvas);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.desktop.editors.core.theme.Theme#createConnectorFrame
	 * (org.eclipse.vtp.desktop.editors.core.theme.ElementFrame,
	 * org.eclipse.vtp.desktop.editors.core.theme.ElementFrame,
	 * org.eclipse.vtp.desktop.editors.core.model.UIConnector)
	 */
	@Override
	public ConnectorFrame createConnectorFrame(ElementFrame source,
			ElementFrame destination, IDesignConnector connector) {
		return new MantisConnectorFrame(source, destination, connector);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.desktop.editors.core.theme.Theme#createElementFrame(org
	 * .eclipse.vtp.desktop.editors.core.model.UIElement)
	 */
	@Override
	public ElementFrame createElementFrame(IDesignElement element) {
		return new MantisElementFrame(element);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.desktop.editors.core.theme.Theme#getName()
	 */
	@Override
	public String getName() {
		return "Mantis";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.desktop.editors.core.theme.Theme#getId()
	 */
	@Override
	public String getId() {
		return "org.eclipse.vtp.desktop.editors.themes.mantis";
	}

}
