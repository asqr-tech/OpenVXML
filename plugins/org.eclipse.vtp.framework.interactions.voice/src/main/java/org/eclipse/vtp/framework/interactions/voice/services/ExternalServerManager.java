package org.eclipse.vtp.framework.interactions.voice.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ExternalServerManager
{
	public static ExternalServerManager getInstance()
	{
		return instance;
	}
	
	private static final ExternalServerManager instance = new ExternalServerManager();
	
	private List<ExternalServer> serverLocations = new ArrayList<ExternalServer>();
	private List<ExternalServerManagerListener> listeners = new LinkedList<ExternalServerManagerListener>();
	private DistributionMethod method = DistributionMethod.FAILOVER;
	private Logging logging = Logging.FIRSTFAILURE;

	private ExternalServerManager()
	{
		try
		{
			InitialContext jndiContext = new InitialContext();
			String mediaServerCountString = (String)jndiContext.lookup("java:comp/env/media-server-count");
			if(mediaServerCountString != null)
			{
				int count = 0;
				try
				{
					count = Integer.parseInt(mediaServerCountString);
				}
				catch (NumberFormatException e)
				{
				}
				for(int i = 1; i < count + 1; i++)
				{
					try
					{
						String serverPrefix = (String)jndiContext.lookup("java:comp/env/media-server-" + i);
						addLocation(serverPrefix);
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
		}
		catch (NamingException e)
		{
			System.out.println("Unable to lookup external media server configuration");
			//e.printStackTrace();
		}
	}
	
	public Logging getLogging()
	{
		return logging;
	}
	
	public void setLogging(Logging logging)
	{
		if(logging == null)
			throw new IllegalArgumentException("Logging cannot be null");
		System.out.println("Setting external media server logging to " + logging.getLevel());
		this.logging = logging;
	}
	
	public void setDistributionMethod(DistributionMethod method)
	{
		System.out.println("Setting external media server distribution method to " + method.getName());
		this.method = method;
	}

	public List<ExternalServer> getLocations()
	{
		if(method == DistributionMethod.FAILOVER)
			synchronized(this)
			{
				return new ArrayList<ExternalServer>(serverLocations);
			}
		else
		{
			List<ExternalServer> random = null;
			synchronized(this)
			{
				random = new ArrayList<ExternalServer>(serverLocations);
			}
			Collections.shuffle(random);
			return random;
		}
	}
	
	public synchronized void clearLocations()
	{
		System.out.println("Clearing current list of external media servers");
		serverLocations.clear();
	}
	
	public synchronized void addLocation(String location)
	{
		System.out.println("Adding external media server: " + location);
		serverLocations.add(new ExternalServer(location));
		fireLocationChange();
	}
	
	private void updateLocations(List<String> locations)
	{
		List<ExternalServer> local = new ArrayList<ExternalServer>();
		for(String location : locations)
		{
			local.add(new ExternalServer(location));
		}
		serverLocations = local;
		fireLocationChange();
		for(String location : locations)
		{
			System.out.println("Adding external media server: " + location);
		}
	}
	
	public synchronized void ensureLocations(List<String> locations)
	{
		if(serverLocations.size() != locations.size())
		{
			updateLocations(locations);
			return;
		}
		for(int i = 0; i < locations.size(); i++)
		{
			boolean found = false;
			for(ExternalServer server : serverLocations)
			{
				if(server.getLocation().equals(locations.get(i)))
				{
					found = true;
					break;
				}
			}
			if(!found)
			{
				updateLocations(locations);
				return;
			}
		}
	}
	
	public void addListener(ExternalServerManagerListener l)
	{
		synchronized(listeners)
		{
			listeners.remove(l);
			listeners.add(l);
		}
	}
	
	public void removeListener(ExternalServerManagerListener l)
	{
		synchronized(listeners)
		{
			listeners.remove(l);
		}
	}
	
	private void fireLocationChange()
	{
		synchronized(listeners)
		{
			for(ExternalServerManagerListener l : listeners)
			{
				l.locationsChanged();
			}
		}
	}
	
	public enum DistributionMethod
	{
		RANDOMIZED("random"),FAILOVER("failover");
		
		private String name;
		
		private DistributionMethod(String name)
		{
			this.name = name;
		}
		
		public String getName()
		{
			return name;
		}
		
		public static DistributionMethod getMethod(String name)
		{
			if("random".equalsIgnoreCase(name) || "balanced".equalsIgnoreCase(name))
				return RANDOMIZED;
			if("failover".equalsIgnoreCase(name))
				return FAILOVER;
			return null;
		}
	}
	
	public enum Logging
	{
		NONE("none"),FIRSTFAILURE("first-failure"),ALWAYS("always");
		
		private String name;
		
		private Logging(String name)
		{
			this.name = name;
		}
		
		public String getLevel()
		{
			return name;
		}
		
		public static Logging byLevel(String name)
		{
			if("none".equalsIgnoreCase(name))
				return NONE;
			if("first-failure".equalsIgnoreCase(name) || "firstfailure".equalsIgnoreCase(name) || "first".equalsIgnoreCase(name))
				return FIRSTFAILURE;
			if("always".equalsIgnoreCase(name) || "all".equalsIgnoreCase(name))
				return ALWAYS;
			return null;
		}
	}
}
