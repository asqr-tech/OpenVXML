package com.openmethods.openvxml.desktop.model.workflow.design;

import org.eclipse.swt.graphics.Point;

public interface IDesignConnectorMidpoint
{
	public Point getPosition();
	
	public void setPosition(int x, int y);
	
	public void adjustPosition(int xoff, int yoff);
	
}
