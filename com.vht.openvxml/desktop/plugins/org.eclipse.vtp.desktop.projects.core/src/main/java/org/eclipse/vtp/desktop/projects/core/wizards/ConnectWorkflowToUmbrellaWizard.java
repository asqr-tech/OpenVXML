package org.eclipse.vtp.desktop.projects.core.wizards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.core.WorkflowCore;
import org.eclipse.vtp.desktop.model.core.internal.OpenVXMLProject;
import org.eclipse.vtp.desktop.model.interactive.core.IUmbrellaProjectAspect;
import org.eclipse.vtp.desktop.projects.core.wizards.ConnectWorkflowToUmbrellaWizard.BrandMapping.Action;

import com.openmethods.openvxml.desktop.model.branding.BrandManager;
import com.openmethods.openvxml.desktop.model.branding.IBrand;
import com.openmethods.openvxml.desktop.model.branding.IBrandingProjectAspect;
import com.openmethods.openvxml.desktop.model.branding.internal.Brand;
import com.openmethods.openvxml.desktop.model.workflow.IDesignDocument;
import com.openmethods.openvxml.desktop.model.workflow.IDesignFolder;
import com.openmethods.openvxml.desktop.model.workflow.IDesignItemContainer;
import com.openmethods.openvxml.desktop.model.workflow.IWorkflowProjectAspect;

public class ConnectWorkflowToUmbrellaWizard extends Wizard
{
	private OpenVXMLProject appProject;
	private UmbrellaPage umbrellaPage;
	private BrandMappingPage brandMappingPage = null;
	private List<IOpenVXMLProject> umbrellaProjects = new ArrayList<IOpenVXMLProject>();

	public ConnectWorkflowToUmbrellaWizard(OpenVXMLProject appProject)
	{
		super();
		this.appProject = appProject;
		List<IOpenVXMLProject> projects = WorkflowCore.getDefault().getWorkflowModel().listWorkflowProjects();
		for(IOpenVXMLProject p : projects)
		{
			if(p.getProjectAspect(IUmbrellaProjectAspect.ASPECT_ID) != null)
				umbrellaProjects.add(p);
		}
		umbrellaPage = new UmbrellaPage();
		addPage(umbrellaPage);
		if(umbrellaProjects.size() > 0)
		{
			brandMappingPage = new BrandMappingPage();
			brandMappingPage.setUmbrellaProject(umbrellaProjects.get(0));
			addPage(brandMappingPage);
		}
	}

	@Override
	public boolean performFinish()
	{
		try
		{
			if(!PlatformUI.getWorkbench().getActiveWorkbenchWindow()
			.getActivePage().saveAllEditors(true))
				return false;
			List<IEditorReference> toClose = new LinkedList<IEditorReference>();
			IEditorReference[] editorRefs = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
			.getActivePage().getEditorReferences();
			for(IEditorReference ref : editorRefs)
			{
				IEditorInput input = ref.getEditorInput();
				if(input instanceof FileEditorInput)
				{
					FileEditorInput fileInput = (FileEditorInput)input;
					IFile file = fileInput.getFile();
					if(file.getProject() != null && file.getProject().equals(appProject.getUnderlyingProject()))
					{
						toClose.add(ref);
					}
				}
			}
			if(!toClose.isEmpty())
			{
				MessageBox confirmDialog = new MessageBox(getShell(), SWT.OK | SWT.CANCEL | SWT.ICON_WARNING);
				StringBuilder buf = new StringBuilder("The following editors will be closed before completing the connection:\r\n");
				for(IEditorReference ref : toClose)
				{
					buf.append(((FileEditorInput)ref.getEditorInput()).getFile().getFullPath().toPortableString());
					buf.append("\r\n");
				}
				confirmDialog.setMessage(buf.toString());
				if(confirmDialog.open() != SWT.OK)
					return false;
				if(!PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().closeEditors(toClose.toArray(new IEditorReference[toClose.size()]), true))
					return false;
			}
			brandMappingPage.applyMappings();
			appProject.setParentProject(brandMappingPage.umbrellaProject);
			return true;
		}
		catch (PartInitException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public class UmbrellaPage extends WizardPage
	{
		private Combo umbrellaCombo;
		
		public UmbrellaPage()
		{
			super("UmbrellaPage", "Select this project's Umbrella", null);
			if(umbrellaProjects.size() < 1)
				setPageComplete(false);
		}

		@Override
		public void createControl(Composite parent)
		{
			Composite comp = new Composite(parent, SWT.NONE);
			if(umbrellaProjects.size() > 0)
			{
				comp.setLayout(new GridLayout(2, false));
				Label umbrellaLabel = new Label(comp, SWT.NONE);
				umbrellaLabel.setText("Umbrella Project");
				umbrellaLabel.setLayoutData(new GridData());
				umbrellaCombo = new Combo(comp, SWT.READ_ONLY | SWT.DROP_DOWN);
				umbrellaCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				for(IOpenVXMLProject p : umbrellaProjects)
				{
					umbrellaCombo.add(p.getName());
				}
				umbrellaCombo.select(0);
				umbrellaCombo.addSelectionListener(new SelectionListener()
				{
					@Override
					public void widgetSelected(SelectionEvent e)
					{
						System.out.println("updating mapping page");
						brandMappingPage.setUmbrellaProject(umbrellaProjects.get(umbrellaCombo.getSelectionIndex()));
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e)
					{
					}
				});
			}
			else
			{
				comp.setLayout(new FillLayout());
				Label noUmbrellaLabel = new Label(comp, SWT.NONE);
				noUmbrellaLabel.setText("There currently are no Umbrella projects in the workspace.  You must create one before connecting a workflow project.");
			}
			setControl(comp);
		}
		
	}
	
	public class BrandMappingPage extends WizardPage
	{
		private IOpenVXMLProject umbrellaProject = null;
		private IBrandingProjectAspect sourceAspect = null;
		private BrandManager sourceManager = null;
		private IBrandingProjectAspect destinationAspect = null;
		private BrandManager destinationManager = null;
		private Map<String, BrandMapping> mappings = new HashMap<String, BrandMapping>();
		private TreeViewer viewer = null; 
		private TargetEditor targetEditor;
		
		public BrandMappingPage()
		{
			super("BrandMapping", "Determine how the original brands are mapped to its new Umbrella project", null);
			sourceAspect = (IBrandingProjectAspect)appProject.getProjectAspect(IBrandingProjectAspect.ASPECT_ID);
			sourceManager = sourceAspect.getBrandManager();
		}
		
		public void setUmbrellaProject(IOpenVXMLProject umbrellaProject)
		{
			this.umbrellaProject = umbrellaProject;
			destinationAspect = (IBrandingProjectAspect)umbrellaProject.getProjectAspect(IBrandingProjectAspect.ASPECT_ID);
			destinationManager = destinationAspect.getBrandManager();
			mappings.clear();
			IBrand sourceDefault = sourceManager.getDefaultBrand();
			mapBrand(sourceDefault);
			if(viewer != null)
			{
				targetEditor.updateBrands();
				viewer.refresh();
			}
		}
		
		private void mapBrand(IBrand sourceBrand)
		{
			IBrand destinationBrand = destinationManager.getBrandById(sourceBrand.getId());
			if(destinationBrand == null)
			{
				destinationBrand = destinationManager.getBrandByPath(sourceBrand.getPath());
			}
			BrandMapping mapping = new BrandMapping(sourceBrand);
			if(destinationBrand != null)
				mapping.replaceWith(destinationBrand);
			else
				mapping.addTo(sourceBrand.getPath());
			mappings.put(sourceBrand.getId(), mapping);
			for(IBrand child : sourceBrand.getChildBrands())
				mapBrand(child);
		}
		
		public void checkMappings()
		{
			List<String> mappedBrands = new LinkedList<String>();
			List<String> newPaths = new LinkedList<String>();
			for(BrandMapping mapping : mappings.values())
			{
				if(mapping.action == Action.ADD)
				{
					if(mapping.destinationName == null)
					{
						setErrorMessage(mapping.sourceBrand.getPath() + " is not configured properly.");
						setPageComplete(false);
						return;
					}
					if(newPaths.contains(mapping.destinationName))
					{
						setErrorMessage(mapping.destinationName + " is being added multiple times.");
						setPageComplete(false);
						return;
					}
					if(destinationManager.getBrandByPath(mapping.destinationName) != null)
					{
						setErrorMessage(mapping.sourceBrand.getPath() + " is mapped to an existing brand.");
						setPageComplete(false);
						return;
					}
					newPaths.add(mapping.destinationName);
				}
				else if(mapping.action == Action.REPLACE)
				{
					if(mapping.destinationBrand == null)
					{
						setErrorMessage(mapping.sourceBrand.getPath() + " is not configured properly.");
						setPageComplete(false);
						return;
					}
					if(mappedBrands.contains(mapping.destinationBrand.getPath()))
					{
						setErrorMessage(mapping.destinationBrand.getPath() + " is referenced multiple times.");
						setPageComplete(false);
						return;
					}
					mappedBrands.add(mapping.destinationBrand.getPath());
				}
			}
			setErrorMessage(null);
			setPageComplete(true);
		}
		
		public void applyMappings()
		{
			//create working copies of all design canvases
			List<IDesignDocument> designs = new LinkedList<IDesignDocument>();
			IWorkflowProjectAspect workflowAspect = (IWorkflowProjectAspect)appProject.getProjectAspect(IWorkflowProjectAspect.ASPECT_ID);
			IDesignItemContainer container = workflowAspect.getDesignRootFolder();
			addWorkingCopies(designs, container);
			for(BrandMapping mapping : mappings.values())
			{
				if(mapping.action == Action.REMOVE)
				{
					mapping.sourceBrand.delete();
				}
				else if(mapping.action == Action.REPLACE)
				{
					if(mapping.destinationBrand != null)
						((Brand)mapping.sourceBrand).setId(mapping.destinationBrand.getId());
				}
			}
			appProject.storeBuildPath();
			for(IDesignDocument design : designs)
			{
				try
				{
					design.commitWorkingCopy();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			for(BrandMapping mapping : mappings.values())
			{
				if(mapping.action == Action.ADD)
				{
					if(mapping.destinationName != null)
					{
						String dest = mapping.destinationName;
						if(dest.startsWith("/"))
							dest = dest.substring(1);
						IBrand target = null;
						String[] parts = dest.split("/");
						for(int i = 0; i < parts.length; i++)
						{
							StringBuilder path = new StringBuilder();
							for(int j = 0; j < i; j++)
							{
								path.append("/");
								path.append(parts[j]);
							}
							path.append("/");
							path.append(parts[i]);
							IBrand cur = destinationManager.getBrandByPath(path.toString());
							if(cur == null)
							{
								cur = new Brand(mapping.sourceBrand.getId(), parts[i]);
								cur.setParent(target);
							}
							target = cur;
						}
						((Brand)target).setId(mapping.sourceBrand.getId());
					}
				}
			}
		}
		
		private void addWorkingCopies(List<IDesignDocument> designs, IDesignItemContainer container)
		{
			for(IDesignDocument d : container.getDesignDocuments())
			{
				d.becomeWorkingCopy(true);
				designs.add(d);
			}
			for(IDesignFolder f : container.getDesignFolders())
			{
				addWorkingCopies(designs, f);
			}
		}

		@Override
		public void createControl(Composite parent)
		{
			Composite comp = new Composite(parent, SWT.NONE);
			comp.setLayout(new FillLayout());
			Tree tree = new Tree(comp, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
			tree.setHeaderVisible(true);
			viewer = new TreeViewer(tree);
			
			TreeColumn sourceColumn = new TreeColumn(tree, SWT.LEFT);
			sourceColumn.setText("Source Brand");
			sourceColumn.setResizable(true);
			sourceColumn.setWidth(200);
			
			TreeViewerColumn actionColumn = new TreeViewerColumn(viewer, SWT.LEFT);
			actionColumn.getColumn().setText("Action");
			actionColumn.getColumn().setResizable(true);
			actionColumn.getColumn().setWidth(100);
			actionColumn.setEditingSupport(new ActionEditor());
			
			TreeViewerColumn targetColumn = new TreeViewerColumn(viewer, SWT.LEFT);
			targetColumn.getColumn().setText("Target Brand");
			targetColumn.getColumn().setResizable(true);
			targetColumn.getColumn().setWidth(200);
			targetColumn.setEditingSupport(targetEditor = new TargetEditor());
			targetEditor.updateBrands();
			
			viewer.setContentProvider(new BrandContentProvider());
			viewer.setLabelProvider(new BrandLabelProvider());
			viewer.setInput(this);
			viewer.expandAll();
			setControl(comp);
		}
		
		public class ActionEditor extends EditingSupport
		{
			ComboBoxCellEditor actionCombo;

			public ActionEditor()
			{
				super(viewer);
				actionCombo = new ComboBoxCellEditor(viewer.getTree(), new String[] {"Remove Brand", "Replace with", "Add to Umbrella"}, SWT.READ_ONLY | SWT.DROP_DOWN);
			}

			@Override
			protected CellEditor getCellEditor(Object element)
			{
				return actionCombo;
			}

			@Override
			protected boolean canEdit(Object element)
			{
				return true;
			}

			@Override
			protected Object getValue(Object element)
			{
				IBrand brand = (IBrand)element;
				BrandMapping mapping = mappings.get(brand.getId());
				switch(mapping.action)
				{
					case REMOVE:
						return 0;
					case REPLACE:
						return 1;
					case ADD:
						return 2;
					default:
						return 0;
				}
			}

			@Override
			protected void setValue(Object element, Object value)
			{
				IBrand brand = (IBrand)element;
				BrandMapping mapping = mappings.get(brand.getId());
				Integer i = (Integer)value;
				switch(i)
				{
					case 0:
						mapping.remove();
						break;
					case 1:
						mapping.replaceWith(null);
						break;
					case 2:
						mapping.addTo(brand.getPath());
						break;
					default:
						mapping.remove();
				}
				viewer.refresh();
				checkMappings();
			}
		}

		public class TargetEditor extends EditingSupport
		{
			ComboBoxCellEditor targetCombo;
			List<IBrand> targetBrands = new ArrayList<IBrand>();
			TextCellEditor addText;

			public TargetEditor()
			{
				super(viewer);
				targetCombo = new ComboBoxCellEditor(viewer.getTree(), new String[] {}, SWT.READ_ONLY | SWT.DROP_DOWN);
				addText = new TextCellEditor(viewer.getTree());
				addText.setValidator(new ICellEditorValidator()
				{
					@Override
					public String isValid(Object value)
					{
						String text = value.toString();
						if(!text.startsWith("/Default"))
						{
							return "Brands must be rooted at the Default brand, e.g. /Default/brandname";
						}
						if(text.endsWith("/"))
							return "Brands must not end with a '/'";
						IBrand b = destinationManager.getBrandByPath(text);
						if(b != null)
							return "Target brand already exists.  You must use Replace with instead of Add to Umbrella.";
						IStructuredSelection sel = (IStructuredSelection)viewer.getSelection();
						IBrand editedBrand = (IBrand)sel.getFirstElement();
						BrandMapping editedMapping = mappings.get(editedBrand.getId());
						if(editedMapping.action == Action.ADD)
						{
							for(BrandMapping m : mappings.values())
							{
								if(m != editedMapping && m.action == Action.ADD)
								{
									if(text.equals(m.destinationName))
										return "A brand with that name is already being added.";
								}
							}
						}
						return null;
					}
				});
				addText.addListener(new ICellEditorListener()
				{

					@Override
					public void applyEditorValue()
					{
						checkMappings();
					}

					@Override
					public void cancelEditor()
					{
						checkMappings();
					}

					@Override
					public void editorValueChanged(boolean oldValidState,
							boolean newValidState)
					{
						if(!newValidState)
							BrandMappingPage.this.setErrorMessage(addText.getErrorMessage());
						else
							BrandMappingPage.this.setErrorMessage(null);
					}
				});
			}
			
			public void updateBrands()
			{
				targetBrands.clear();
				addBrand(destinationManager.getDefaultBrand());
				String[] items = new String[targetBrands.size()];
				for(int i = 0; i < targetBrands.size(); i++)
				{
					StringBuilder buf = new StringBuilder();
					IBrand b = targetBrands.get(i).getParent();
					while(b != null)
					{
						buf.append("   ");
						b = b.getParent();
					}
					buf.append(targetBrands.get(i).getName());
					items[i] = buf.toString();
				}
				targetCombo.setItems(items);
			}
			
			private void addBrand(IBrand brand)
			{
				targetBrands.add(brand);
				for(IBrand child : brand.getChildBrands())
				{
					addBrand(child);
				}
			}

			@Override
			protected CellEditor getCellEditor(Object element)
			{
				IBrand brand = (IBrand)element;
				BrandMapping mapping = mappings.get(brand.getId());
				if(mapping.action == Action.ADD)
					return addText;
				return targetCombo;
			}

			@Override
			protected boolean canEdit(Object element)
			{
				IBrand brand = (IBrand)element;
				BrandMapping mapping = mappings.get(brand.getId());
				return mapping.action != Action.REMOVE;
			}

			@Override
			protected Object getValue(Object element)
			{
				IBrand brand = (IBrand)element;
				BrandMapping mapping = mappings.get(brand.getId());
				if(mapping.action == Action.REPLACE)
				{
					if(mapping.destinationBrand != null)
					{
						for(int i = 0; i < targetBrands.size(); i++)
						{
							IBrand b = targetBrands.get(i);
							if(b.getId().equals(mapping.destinationBrand.getId()))
								return new Integer(i);
						}
					}
					return 0;
				}
				else
					return mapping.destinationName;
			}

			@Override
			protected void setValue(Object element, Object value)
			{
				IBrand brand = (IBrand)element;
				BrandMapping mapping = mappings.get(brand.getId());
				if(mapping.action == Action.REPLACE)
				{
					if(value != null)
					{
						Integer i = (Integer)value;
						mapping.replaceWith(targetBrands.get(i));
					}
				}
				else
					mapping.addTo(value.toString());
				viewer.refresh();
				checkMappings();
			}
		}

		private class BrandContentProvider implements IStructuredContentProvider,
		ITreeContentProvider
		{
		
			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
			 */
			public Object[] getElements(Object inputElement)
			{
				return new Object[] { sourceManager.getDefaultBrand() };
			}
		
			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
			 */
			public void dispose()
			{
			}
		
			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
			 */
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
			{
			}
		
			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
			 */
			public Object[] getChildren(Object parentElement)
			{
				return ((IBrand)parentElement).getChildBrands().toArray();
			}
		
			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
			 */
			public Object getParent(Object element)
			{
				return ((IBrand)element).getParent();
			}
		
			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
			 */
			public boolean hasChildren(Object element)
			{
				return ((IBrand)element).getChildBrands().size() > 0;
			}
		}
		
		private class BrandLabelProvider extends LabelProvider implements ITableLabelProvider
		{
			@Override
			public Image getColumnImage(Object element, int columnIndex)
			{
				return null;
			}

			@Override
			public String getColumnText(Object element, int columnIndex)
			{
				IBrand b = (IBrand)element;
				BrandMapping mapping = mappings.get(b.getId());
				if(columnIndex == 0)
					return b.getName();
				if(columnIndex == 1)
				{
					switch(mapping.action)
					{
						case REMOVE:
							return "Remove Brand";
						case REPLACE:
							return "Replace With";
						case ADD:
							return "Add to Umbrella";
						default:
							return "Remove Brand";
					}
				}
				if(mapping.destinationBrand != null)
					return mapping.destinationBrand.getPath();
				else
				{
					if(mapping.destinationName == null)
						return "";
					return mapping.destinationName;
				}
			}
		
		}

	}
	
	public static class BrandMapping
	{
		private IBrand sourceBrand;
		private Action action = Action.REMOVE;
		private IBrand destinationBrand = null;
		private String destinationName = null;
		
		public BrandMapping(IBrand sourceBrand)
		{
			super();
			this.sourceBrand = sourceBrand;
		}
		
		public IBrand getSourceBrand()
		{
			return sourceBrand;
		}
		
		public Action getAction()
		{
			return action;
		}
		
		public IBrand getDestinationBrand()
		{
			return destinationBrand;
		}
		
		public void replaceWith(IBrand destinationBrand)
		{
			action = Action.REPLACE;
			this.destinationBrand = destinationBrand;
			this.destinationName = null;
		}
		
		public void remove()
		{
			action = Action.REMOVE;
			this.destinationBrand = null;
			this.destinationName = null;
		}
		
		public void addTo(String name)
		{
			action = Action.ADD;
			this.destinationBrand = null;
			this.destinationName = name;
		}
		
		public enum Action
		{
			REMOVE, REPLACE, ADD
		}
	}
	
}
