/**
 * 
 */
package org.eclipse.vtp.desktop.model.core.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.core.IOpenVXMLProjectAspect;
import org.eclipse.vtp.desktop.model.core.IWorkflowResource;
import org.eclipse.vtp.desktop.model.core.WorkflowCore;
import org.eclipse.vtp.desktop.model.core.event.ReloadObjectDataEvent;
import org.eclipse.vtp.desktop.model.core.spi.IOpenVXMLProjectAspectFactory;
import org.eclipse.vtp.desktop.model.core.spi.OpenVXMLProjectAspect;
import org.eclipse.vtp.framework.util.Guid;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author trip
 *
 */
public class OpenVXMLProject extends WorkflowResource implements
		IOpenVXMLProject, IResourceChangeListener
{
	/** The underlying eclipse project for this project resource. */
	protected IProject project;
	private IOpenVXMLProject parent;
	private String projectId = Guid.createGUID();
	private Map<String, OpenVXMLProjectAspect> aspectsById = 
			new HashMap<String, OpenVXMLProjectAspect>();

	/**
	 * 
	 */
	public OpenVXMLProject(IProject project)
	{
		super();
		this.project = project;
		activateEvents();
		loadBuildPath();
	}
	
	public String getId()
	{
		return projectId;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.internals.VoiceResource#getObjectId()
	 */
	protected String getObjectId()
	{
		return project.getFullPath().toPortableString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IWorkflowResource#getName()
	 */
	@Override
	public String getName()
	{
		return project.getName();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IVoiceProject#getUnderlyingProject()
	 */
	public IProject getUnderlyingProject()
	{
		return project;
	}
	
	public IOpenVXMLProject getParentProject()
	{
		return parent;
	}
	
	public void setParentProject(IOpenVXMLProject parent)
	{
		System.out.println("Begin setting parent: " + parent);
		IOpenVXMLProject toCheck = parent;
		while(toCheck != null)
		{
			if(toCheck.getId().equals(projectId))
			{
				throw new IllegalArgumentException("Cycle detected in project parent structure.");
			}
			toCheck = toCheck.getParentProject();
		}
		System.out.println("Cycle Check Negative");
		this.parent = parent;
		//safely record parent project id
		storeBuildPath();
		//reload all project aspects taking the new parent project into account
		loadBuildPath();
		//store modified project apsect information
		storeBuildPath();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IWorkflowResource#getParent()
	 */
	@Override
	public IWorkflowResource getParent()
	{
		return null;
	}
	
	

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.internal.WorkflowResource#getProject()
	 */
	@Override
	public IOpenVXMLProject getProject()
	{
		return this;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IOpenVXMLProject#addProjectAspect(java.lang.String)
	 */
	@Override
	public synchronized IOpenVXMLProjectAspect addProjectAspect(String aspectId) throws CoreException
	{
		IProjectDescription desc = project.getDescription();
		ProjectAspectRegistry registry = ProjectAspectRegistry.getInstance();
		IOpenVXMLProjectAspectFactory factory = registry.getAspectFactory(aspectId);
		if(factory == null)
		{
			System.err.println("Failed to locate project aspect " + aspectId + " for project " + getName());
			//skip this aspect.  It will be removed when this project meta-data is next saved.
			//TODO Preserve aspect configuration for duplication on next save in case there was a bad update or something
			throw new IllegalArgumentException("Aspect " + aspectId + " not available.");
		}
		List<String> requiredAspects = factory.getRequiredAspects();
		for(String requiredId : requiredAspects)
		{
			if(requiredId != null && getProjectAspect(requiredId) == null)
			{
				//parent aspect has not been loaded yet, wait for next pass
				throw new IllegalArgumentException("Required Aspect " + requiredId + " not available or not applied to this project.");
			}
		}
		factory.createProjectLayout(this, null);
		boolean configurationChanged = factory.configureProject(this, desc, null);
		OpenVXMLProjectAspect aspect = factory.createProjectAspect(this, null);
		aspectsById.put(aspect.getAspectId(), aspect);
		if(configurationChanged)
			project.setDescription(desc, null);
		storeBuildPath();
		return aspect;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IOpenVXMLProject#getProjectAspect(java.lang.String)
	 */
	@Override
	public synchronized IOpenVXMLProjectAspect getProjectAspect(String aspectId)
	{
		return aspectsById.get(aspectId);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IOpenVXMLProject#getProjectAspects()
	 */
	@Override
	public synchronized List<IOpenVXMLProjectAspect> getProjectAspects()
	{
		return new ArrayList<IOpenVXMLProjectAspect>(aspectsById.values());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IOpenVXMLProject#removeProjectAspect(org.eclipse.vtp.desktop.model.core.IOpenVXMLProjectAspect)
	 */
	@Override
	public synchronized void removeProjectAspect(String aspectId) throws CoreException
	{
		OpenVXMLProjectAspect aspect = aspectsById.get(aspectId);
		if(aspect == null)
			throw new IllegalArgumentException("Project aspect not part of this project");
		IProjectDescription desc = project.getDescription();
		if(aspect.removeProjectConfiguration(desc))
			project.setDescription(desc, null);
		aspect.removeProjectLayout();
		aspectsById.remove(aspectId);
		storeBuildPath();
	}

	private synchronized void loadBuildPath()
	{
		System.err.println("Loading Build Path: " + getName());
//		new Exception().printStackTrace();
		try
		{
			IFile buildPath = project.getFile(".buildPath");
			if(!buildPath.exists())
			{
				storeBuildPath();
			}
			DocumentBuilderFactory buildFactory =
				DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = buildFactory.newDocumentBuilder();
			Document doc = builder.parse(buildPath.getContents());
			Element root = doc.getDocumentElement();
			projectId = root.getAttribute("id");
			String parentId = root.getAttribute("parent");
			if(parentId != null && !parentId.equals(""))
			{
				IOpenVXMLProject parent = WorkflowCore.getDefault().getWorkflowModel().getWorkflowProject(parentId);
				if(parent != null)
				{
					this.parent = parent;
				}
			}
			NodeList aspectsList = root.getElementsByTagName("project-aspects");
			if(aspectsList.getLength() > 0)
			{
				Element aspectsElement = (Element)aspectsList.item(0);
				List<Element> aspectsToProcess = new LinkedList<Element>();
				NodeList aspectList = aspectsElement.getChildNodes();
				for(int i = 0; i < aspectList.getLength(); i++)
				{
					Node node = aspectList.item(i);
					if(node.getNodeType() == Node.ELEMENT_NODE && ((Element)node).getTagName().equals("aspect"))
						aspectsToProcess.add((Element)node);
				}

				IProjectDescription desc = project.getDescription();
				ProjectAspectRegistry registry = ProjectAspectRegistry.getInstance();
				boolean loadedAspect = true;
				boolean configurationChanged = false;
				//loop until all aspects have been loaded or we do a full pass
				//without successfully loading a single aspect in case a dependency isn't available
				while(loadedAspect && aspectsToProcess.size() > 0)
				{
					loadedAspect = false;
					List<Element> failedToProcess = new LinkedList<Element>();
					for(Element aspectElement : aspectsToProcess)
					{
						String aspectId = aspectElement.getAttribute("id");
						IOpenVXMLProjectAspectFactory factory = registry.getAspectFactory(aspectId);
						if(factory == null)
						{
							System.err.println("Failed to locate project aspect " + aspectId + " for project " + getName());
							//skip this aspect.  It will be removed when this project meta-data is next saved.
							//TODO Preserve aspect configuration for duplication on next save in case there was a bad update or something
							continue;
						}
						List<String> requiredAspects = factory.getRequiredAspects();
						for(String requiredId : requiredAspects)
						{
							if(requiredId != null && getProjectAspect(requiredId) == null)
							{
								//parent aspect has not been loaded yet, wait for next pass
								failedToProcess.add(aspectElement);
								continue;
							}
						}
						factory.createProjectLayout(this, aspectElement);
						configurationChanged |= factory.configureProject(this, desc, aspectElement);
						OpenVXMLProjectAspect aspect = factory.createProjectAspect(this, aspectElement);
						aspectsById.put(aspect.getAspectId(), aspect);
						loadedAspect = true;
					}
					aspectsToProcess = failedToProcess;
				}
				if(configurationChanged)
					project.setDescription(desc, null);
			}
			project.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public final void storeBuildPath()
	{
		System.out.println("Storing Build Path: " + getName());
		try
		{
			Document document =
				DocumentBuilderFactory.newInstance().newDocumentBuilder()
									  .newDocument();
			Element rootElement =
				document.createElement("workflow-settings");
			document.appendChild(rootElement);
			rootElement.setAttribute("id", projectId);
			if(parent != null)
				rootElement.setAttribute("parent", parent.getId());
			Element aspectsElement = document.createElement("project-aspects");
			rootElement.appendChild(aspectsElement);
			for(OpenVXMLProjectAspect aspect : aspectsById.values())
			{
				Element aspectElement = document.createElement("aspect");
				aspectsElement.appendChild(aspectElement);
				aspectElement.setAttribute("id", aspect.getAspectId());
				aspect.writeConfiguration(aspectElement);
			}
			
			Transformer trans =
				TransformerFactory.newInstance().newTransformer();
			DOMSource source = new DOMSource(document);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(baos);
			trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");
			trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		    trans.transform(source, result);

			IFile buildPath = project.getFile(".buildPath");
			if(!buildPath.exists())
				buildPath.create(new ByteArrayInputStream(baos.toByteArray()), true, null);
			else
				buildPath.setContents(new ByteArrayInputStream(baos.toByteArray()),
					true, false, null);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		catch(FactoryConfigurationError e)
		{
			e.printStackTrace();
		}

		WorkflowCore.getDefault()
						 .postObjectEvent(new ReloadObjectDataEvent(
				getObjectId()));
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	@Override
	public void resourceChanged(IResourceChangeEvent event)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter)
	{
		if(adapter.equals(IResource.class) && adapter.isAssignableFrom(project.getClass()))
		{
			return project;
		}
		if(adapter.equals(IProject.class))
		{
			return project;
		}
		synchronized(aspectsById)
		{
			for(OpenVXMLProjectAspect aspect : aspectsById.values())
			{
				if(adapter.isAssignableFrom(aspect.getClass()))
					return adapter;
			}
		}
		return super.getAdapter(adapter);
	}

	@Override
	public List<IWorkflowResource> getChildren()
	{
		List<IWorkflowResource> children = new LinkedList<IWorkflowResource>();
		for(OpenVXMLProjectAspect aspect : aspectsById.values())
		{
			aspect.getAspectResources(children);
		}
		return children;
	}

}
