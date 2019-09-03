/**
 * 
 */
package com.openmethods.openvxml.desktop.model.webservices.builders;

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.core.IWorkflowResource;
import org.eclipse.vtp.desktop.model.core.WorkflowCore;

import com.openmethods.openvxml.desktop.model.webservices.IWebserviceDescriptor;
import com.openmethods.openvxml.desktop.model.webservices.IWebserviceProjectAspect;
import com.openmethods.openvxml.desktop.model.webservices.IWebserviceSet;
import com.openmethods.openvxml.desktop.model.webservices.schema.SchemaProblem;
import com.openmethods.openvxml.desktop.model.webservices.wsdl.WSDL;
import com.openmethods.openvxml.desktop.model.webservices.wsdl.WSDLProblem;

/**
 * @author trip
 *
 */
public class WebserviceModelBuilder extends IncrementalProjectBuilder
{
	/**
	 * Constant string containing the builder id
	 */
	public static final String BUILDER_ID =
		"com.openmethods.openvxml.desktop.model.webservices.WebserviceModelBuilder";
	public static final String MARKER_ID = 
		"com.openmethods.openvxml.desktop.model.webservices.wsdlMarker";
	
	/**
	 * 
	 */
	public WebserviceModelBuilder()
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IncrementalProjectBuilder#build(int, java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, @SuppressWarnings("rawtypes") Map args, IProgressMonitor monitor)
	throws CoreException
	{
		System.err.println("building: " + getProject().getName() + " this: " + this);
		//index|build of project
		if(!getProject().isSynchronized(IResource.DEPTH_INFINITE))
		{
			System.err.println("Resource out of synch with filesystem, refreshing");
			getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
			this.needRebuild();
			return getProject().getReferencedProjects();
		}
		IResourceDelta delta = getDelta(getProject());
		if(delta == null)
		{
			fullBuild(monitor);
		}
		else
		{
			incrementalBuild(delta, monitor);
		}
		return getProject().getReferencedProjects();
	}
	
	/**
	 * Performs all tasks required by a full build of the application project.
	 *
	 * @param monitor The progress monitor used to provide user feedback
	 * @throws CoreException If the build encounters an error during execution
	 */
	protected void fullBuild(final IProgressMonitor monitor)
		throws CoreException
	{
		System.err.println("##########doing full build****************************");
		IOpenVXMLProject oproject = WorkflowCore.getDefault().getWorkflowModel().convertToWorkflowProject(getProject());
		IWebserviceProjectAspect aspect = (IWebserviceProjectAspect)oproject.getProjectAspect(IWebserviceProjectAspect.ASPECT_ID);
		IWebserviceSet webservices = aspect.getWebserviceSet();
		for(IWebserviceDescriptor descriptor : webservices.getWebserviceDescriptors())
		{
			IFile resource = descriptor.getUnderlyingFile();
			resource.deleteMarkers(MARKER_ID, true, IResource.DEPTH_INFINITE);
			try
			{
				WSDL wsdl = descriptor.getWSDL();
				List<SchemaProblem> schemaProblems = wsdl.getSchemaProblems();
				List<WSDLProblem> wsdlProblems = wsdl.getWSDLProblems();
				for(SchemaProblem problem : schemaProblems)
				{
					IMarker marker = resource.createMarker(MARKER_ID);
					marker.setAttributes(new String[] {IMarker.MESSAGE, IMarker.LINE_NUMBER, IMarker.LOCATION, IMarker.SEVERITY}, new Object[] {problem.getMessage(), problem.getLineNumber(), "Line " + problem.getLineNumber(), IMarker.SEVERITY_ERROR});
				}
				for(WSDLProblem problem : wsdlProblems)
				{
					IMarker marker = resource.createMarker(MARKER_ID);
					marker.setAttributes(new String[] {IMarker.MESSAGE, IMarker.LINE_NUMBER, IMarker.LOCATION, IMarker.SEVERITY}, new Object[] {problem.getMessage(), problem.getLineNumber(), "Line " + problem.getLineNumber(), IMarker.SEVERITY_ERROR});
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Performs any build tasks required by the resource delta of the application project.
	 *
	 * @param delta The changes to the application project
	 * @param monitor The progress monitor used to provide user feedback
	 * @throws CoreException If the build encounters an error during execution
	 */
	protected void incrementalBuild(IResourceDelta delta,
		IProgressMonitor monitor) throws CoreException
	{
		AddedDeltaVisitor addedVisitor = new AddedDeltaVisitor();
		delta.accept(addedVisitor);
		ChangedDeltaVisitor changedVisitor = new ChangedDeltaVisitor();
		delta.accept(changedVisitor);
	}
	
	/**
	 * This delta visitor is currently a NOOP.  Any resource delta analysis
	 * needed by future incarnations of this builder will be performed here.
	 */
	private class ChangedDeltaVisitor implements IResourceDeltaVisitor
	{
		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) throws CoreException
		{
			IResource resource = delta.getResource();
			if(resource instanceof IFile && delta.getKind() == IResourceDelta.CHANGED)
			{
				IWorkflowResource workflowResource = WorkflowCore.getDefault().getWorkflowModel().convertToWorkflowResource(resource);
				if(workflowResource instanceof IWebserviceDescriptor)
				{
					IWebserviceDescriptor descriptor = (IWebserviceDescriptor)workflowResource;
					resource.deleteMarkers(MARKER_ID, true, IResource.DEPTH_INFINITE);
					try
					{
						WSDL wsdl = descriptor.getWSDL();
						List<SchemaProblem> schemaProblems = wsdl.getSchemaProblems();
						List<WSDLProblem> wsdlProblems = wsdl.getWSDLProblems();
						for(SchemaProblem problem : schemaProblems)
						{
							IMarker marker = resource.createMarker(MARKER_ID);
							marker.setAttributes(new String[] {IMarker.MESSAGE, IMarker.LINE_NUMBER, IMarker.LOCATION, IMarker.SEVERITY}, new Object[] {problem.getMessage(), problem.getLineNumber(), "Line " + problem.getLineNumber(), IMarker.SEVERITY_ERROR});
						}
						for(WSDLProblem problem : wsdlProblems)
						{
							IMarker marker = resource.createMarker(MARKER_ID);
							marker.setAttributes(new String[] {IMarker.MESSAGE, IMarker.LINE_NUMBER, IMarker.LOCATION, IMarker.SEVERITY}, new Object[] {problem.getMessage(), problem.getLineNumber(), "Line " + problem.getLineNumber(), IMarker.SEVERITY_ERROR});
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			return true;
		}
	}
	
	/**
	 * This delta visitor is currently a NOOP.  Any resource delta analysis
	 * needed by future incarnations of this builder will be performed here.
	 */
	private class AddedDeltaVisitor implements IResourceDeltaVisitor
	{
		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) throws CoreException
		{
			IResource resource = delta.getResource();
			if(delta.getKind() == IResourceDelta.ADDED)
			{
				System.out.println("Added Vistor: " + resource);
				System.out.println(delta);
				IWorkflowResource workflowResource = WorkflowCore.getDefault().getWorkflowModel().convertToWorkflowResource(resource);
				if(workflowResource instanceof IWebserviceDescriptor)
				{
					IWebserviceDescriptor descriptor = (IWebserviceDescriptor)workflowResource;
					resource.deleteMarkers(MARKER_ID, true, IResource.DEPTH_INFINITE);
					try
					{
						WSDL wsdl = descriptor.getWSDL();
						List<SchemaProblem> schemaProblems = wsdl.getSchemaProblems();
						List<WSDLProblem> wsdlProblems = wsdl.getWSDLProblems();
						for(SchemaProblem problem : schemaProblems)
						{
							IMarker marker = resource.createMarker(MARKER_ID);
							marker.setAttributes(new String[] {IMarker.MESSAGE, IMarker.LINE_NUMBER, IMarker.LOCATION, IMarker.SEVERITY}, new Object[] {problem.getMessage(), problem.getLineNumber(), "Line " + problem.getLineNumber(), IMarker.SEVERITY_ERROR});
						}
						for(WSDLProblem problem : wsdlProblems)
						{
							IMarker marker = resource.createMarker(MARKER_ID);
							marker.setAttributes(new String[] {IMarker.MESSAGE, IMarker.LINE_NUMBER, IMarker.LOCATION, IMarker.SEVERITY}, new Object[] {problem.getMessage(), problem.getLineNumber(), "Line " + problem.getLineNumber(), IMarker.SEVERITY_ERROR});
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			return true;
		}
	}
	
}
