package org.eclipse.vtp.framework.interactions.voice.services;

public class ExternalServer
{
	private String location;
	private boolean online = true;

	public ExternalServer(String location)
	{
		super();
		this.location = location;
	}

	public String getLocation()
	{
		return location;
	}
	
	public boolean lastStatus()
	{
		return online;
	}
	
	public void setStatus(boolean online)
	{
		this.online = online;
	}
}
