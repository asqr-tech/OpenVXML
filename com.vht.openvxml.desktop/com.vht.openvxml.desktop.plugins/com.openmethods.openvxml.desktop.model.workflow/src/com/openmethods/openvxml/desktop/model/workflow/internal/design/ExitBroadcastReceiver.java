package com.openmethods.openvxml.desktop.model.workflow.internal.design;

import com.openmethods.openvxml.desktop.model.workflow.design.IExitBroadcastReceiver;

public class ExitBroadcastReceiver implements IExitBroadcastReceiver {
	private String exitPattern = null;

	public ExitBroadcastReceiver(String exitPattern) {
		super();
		this.exitPattern = exitPattern;
	}

	@Override
	public String getExitPattern() {
		return exitPattern;
	}

}
