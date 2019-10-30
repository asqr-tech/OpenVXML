/**
 * 
 */
package org.eclipse.vtp.desktop.model.core.internal;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.core.IWorkflowModel;
import org.eclipse.vtp.desktop.model.core.IWorkflowResource;
import org.eclipse.vtp.desktop.model.core.IWorkflowResourceContainer;
import org.eclipse.vtp.desktop.model.core.natures.WorkflowProjectNature5_0;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author trip
 *
 */
public class WorkflowModel implements IWorkflowModel {
	private OpenVXMLProjectFactory factory = new OpenVXMLProjectFactory();
	private Map<String, IOpenVXMLProject> projectsById = new HashMap<String, IOpenVXMLProject>();
	private Map<String, IOpenVXMLProject> projectsByRaw = new HashMap<String, IOpenVXMLProject>();

	/**
	 * 
	 */
	public WorkflowModel() {
	}

	public void init() {
		loadMissingProjects();
	}

	private void loadMissingProjects() {
		List<ProjectInfo> projectInfoList = new LinkedList<ProjectInfo>();
		IProject[] rawProjects = ResourcesPlugin.getWorkspace().getRoot()
				.getProjects();
		for (IProject project : rawProjects) {
			System.out.println(project.getFullPath().toPortableString());
			if (projectsByRaw.get(project.getFullPath().toPortableString()) != null) {
				System.out.println("already loaded skipping");
				continue;
			}
			try {
				String[] natureIds = project.getDescription().getNatureIds();
				for (String natureId : natureIds) {
					if (natureId.equals(WorkflowProjectNature5_0.NATURE_ID)) {
						try {
							IFile buildPath = project.getFile(".buildPath");
							DocumentBuilderFactory buildFactory = DocumentBuilderFactory
									.newInstance();
							DocumentBuilder builder = buildFactory
									.newDocumentBuilder();
							Document doc = builder.parse(buildPath
									.getContents());
							Element root = doc.getDocumentElement();
							String projectId = root.getAttribute("id");
							String parentId = root.getAttribute("parent");
							System.out.println("Parsed buildpath: " + projectId
									+ " " + parentId);
							projectInfoList.add(new ProjectInfo(project,
									projectId, parentId));
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						break;
					}
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		boolean converted = true;
		while (converted) {
			List<ProjectInfo> toProcess = new LinkedList<ProjectInfo>(
					projectInfoList);
			converted = false;
			for (ProjectInfo info : toProcess) {
				if (info.parentId == null || info.parentId.equals("")) {
					System.out.println("Null parentId: converting");
					IOpenVXMLProject convertedProject = factory
							.convertToWorkflowProject(info.project);
					projectsById.put(info.projectId, convertedProject);
					projectsByRaw.put(info.project.getFullPath()
							.toPortableString(), convertedProject);
					System.out.println("mapped "
							+ info.project.getFullPath().toPortableString());
					projectInfoList.remove(info);
					converted = true;
				} else {
					IOpenVXMLProject parentProject = projectsById
							.get(info.parentId);
					if (parentProject != null) {
						IOpenVXMLProject convertedProject = factory
								.convertToWorkflowProject(info.project);
						projectsById.put(info.projectId, convertedProject);
						projectsByRaw.put(info.project.getFullPath()
								.toPortableString(), convertedProject);
						System.out
								.println("mapped "
										+ info.project.getFullPath()
												.toPortableString());
						projectInfoList.remove(info);
						converted = true;
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.desktop.model.core.IWorkflowModel#convertToWorkflowProject
	 * (org.eclipse.core.resources.IProject)
	 */
	@Override
	public IOpenVXMLProject convertToWorkflowProject(IProject project) {
		IOpenVXMLProject convertedProject = projectsByRaw.get(project
				.getFullPath().toPortableString());
		if (convertedProject == null) {
			loadMissingProjects();
			return projectsByRaw.get(project.getFullPath().toPortableString());
		}
		return convertedProject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.desktop.model.core.IWorkflowModel#createWorkflowProject
	 * (java.lang.String)
	 */
	@Override
	public IOpenVXMLProject createWorkflowProject(String natureId, String name) {
		return factory.createWorkflowProject(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.desktop.model.core.IWorkflowModel#getWorkflowProject(
	 * java.lang.String)
	 */
	@Override
	public IOpenVXMLProject getWorkflowProject(String id) {
		IOpenVXMLProject convertedProject = projectsById.get(id);
		if (convertedProject == null) {
			loadMissingProjects();
			return projectsById.get(id);
		}
		return convertedProject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.desktop.model.core.IWorkflowModel#isWorkflowProject(org
	 * .eclipse.core.resources.IProject)
	 */
	@Override
	public boolean isWorkflowProject(IProject project) {
		return projectsByRaw.containsKey(project.getFullPath()
				.toPortableString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.desktop.model.core.IWorkflowModel#listWorkflowProjects()
	 */
	@Override
	public List<IOpenVXMLProject> listWorkflowProjects() {
		loadMissingProjects();
		return new LinkedList<IOpenVXMLProject>(projectsById.values());
	}

	@Override
	public IWorkflowResource convertToWorkflowResource(IResource resource) {
		if (resource instanceof IProject && ((IProject) resource).isOpen()
				&& isWorkflowProject((IProject) resource)) {
			return convertToWorkflowProject((IProject) resource);
		}
		IProject project = resource.getProject();
		if (project != null && isWorkflowProject(project)) {
			IOpenVXMLProject workflowProject = convertToWorkflowProject(project);
			List<IResource> containers = new LinkedList<IResource>();
			containers.add(resource);
			IContainer container = resource.getParent();
			while (container != null && !container.equals(project)) {
				containers.add(0, container);
				container = container.getParent();
			}
			return locateWorkflowResource(workflowProject, containers);
		}
		return null;
	}

	private IWorkflowResource locateWorkflowResource(
			IWorkflowResourceContainer parentResource, List<IResource> path) {
		IResource resource = path.remove(0);
		for (IWorkflowResource child : parentResource.getChildren()) {
			IResource adaptedResource = (IResource) child
					.getAdapter(IResource.class);
			if (adaptedResource != null && adaptedResource.equals(resource)) {
				if (path.isEmpty()) {
					return child;
				}
				if (child instanceof IWorkflowResourceContainer) {
					return locateWorkflowResource(
							(IWorkflowResourceContainer) child, path);
				}
				return null;
			}
		}
		return null;
	}

	private class ProjectInfo {
		IProject project;
		String projectId;
		String parentId;

		public ProjectInfo(IProject project, String projectId, String parentId) {
			this.project = project;
			this.projectId = projectId;
			this.parentId = parentId;
		}
	}
}
