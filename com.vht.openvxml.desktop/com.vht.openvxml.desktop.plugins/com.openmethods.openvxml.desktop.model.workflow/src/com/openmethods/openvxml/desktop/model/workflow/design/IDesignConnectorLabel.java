package com.openmethods.openvxml.desktop.model.workflow.design;

import org.eclipse.swt.graphics.Point;

public interface IDesignConnectorLabel {
	public Point getOffsetPosition();

	public int getAnchorSegment();

	public void adjustPosition(int xoff, int yoff);

	public void setPosition(int segment, int lox, int loy);

	public void resetPosition();
}
