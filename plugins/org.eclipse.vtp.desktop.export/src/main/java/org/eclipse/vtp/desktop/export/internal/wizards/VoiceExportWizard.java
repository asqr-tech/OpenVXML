package org.eclipse.vtp.desktop.export.internal.wizards;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaLibrariesFolder;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaLibrary;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaProject;
import org.eclipse.vtp.desktop.model.interactive.core.InteractiveWorkflowCore;

public class VoiceExportWizard extends Wizard implements IExportWizard
{
	private VoiceSelectionPage voicePage = null;
	private LocationPage locationPage = null;
	private Map<String, IMediaProject> voices = new HashMap<String, IMediaProject>();

	public VoiceExportWizard()
	{
		List<IMediaProject> allProjects = InteractiveWorkflowCore.getDefault().getInteractiveWorkflowModel().listMediaProjects();
		for(IMediaProject voice : allProjects)
		{
			voices.put(voice.getUnderlyingProject().getName(), voice);
		}
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		List<IMediaProject> initialSelection = new ArrayList<IMediaProject>();
		Iterator i = selection.iterator();
		while(i.hasNext())
		{
			Object obj = i.next();
			if(obj instanceof IProject)
			{
				IMediaProject p = voices.get(((IProject)obj).getName());
				if(p != null)
					initialSelection.add(p);
			}
			else if(obj instanceof IMediaProject)
			{
				IMediaProject p = voices.get(((IMediaProject)obj).getName());
				if(p != null)
					initialSelection.add(p);
			}
		}
		voicePage = new VoiceSelectionPage(initialSelection);
		locationPage = new LocationPage();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages()
	{
		addPage(voicePage);
		addPage(locationPage);
	}

	@Override
	public boolean performFinish()
	{
		String path = locationPage.getExportPath();
		List<IMediaProject> toExport = voicePage.getSelectedVoices();
		File basePath = new File(path);
		if(!basePath.exists())
			if(!basePath.mkdirs())
				return false;
		File containerPath = new File(basePath, "voices/");
		if(!containerPath.exists())
			if(!containerPath.mkdirs())
				return false;
		List<String> overwrites = new LinkedList<String>();
		for(IMediaProject voice : toExport)
		{
			File voicePath = new File(containerPath, voice.getName() + "/");
			if(voicePath.exists())
			{
				overwrites.add(voicePath.getAbsolutePath());
			}
		}
		if(!overwrites.isEmpty())
		{
			//confirm delete with user
			StringBuilder buf = new StringBuilder("The following voices already exist and will be overwritten.\r\n");
			for(String p : overwrites)
			{
				buf.append(p).append("\r\n");
			}
			buf.append("Would you like to continue?");
			MessageBox confirmationDialog =
					new MessageBox(Display.getCurrent().getActiveShell(),
						SWT.YES | SWT.NO | SWT.ICON_WARNING);
				confirmationDialog.setMessage(buf.toString());
			int result = confirmationDialog.open();
			if(result != SWT.YES)
			{
				return false;
			}
		}
		for(IMediaProject voice : toExport)
		{
			File voicePath = new File(containerPath, voice.getName() + "/");
			if(voicePath.exists())
			{
				deleteDirectory(voicePath);
			}
			voicePath.mkdirs();
			try
			{
				IMediaLibrariesFolder libraries = voice.getMediaLibrariesFolder();
				for(IMediaLibrary library : libraries.getMediaLibraries())
				{
					IFolder libraryFolder = library.getUnderlyingFolder();
					copyFolder(libraryFolder, voicePath);
				}
				InputStream in = getClass().getClassLoader().getResourceAsStream("index.php");
				File indexFile = new File(voicePath, "index.php");
				FileOutputStream fos = new FileOutputStream(indexFile);
				byte[] buf = new byte[10240];
				int len = in.read(buf);
				while(len != -1)
				{
					fos.write(buf, 0, len);
					len = in.read(buf);
				}
				in.close();
				fos.close();
				in = getClass().getClassLoader().getResourceAsStream("index.jsp");
				indexFile = new File(voicePath, "index.jsp");
				fos = new FileOutputStream(indexFile);
				len = in.read(buf);
				while(len != -1)
				{
					fos.write(buf, 0, len);
					len = in.read(buf);
				}
				in.close();
				fos.close();
				in = getClass().getClassLoader().getResourceAsStream("Default.aspx");
				indexFile = new File(voicePath, "Default.aspx");
				fos = new FileOutputStream(indexFile);
				len = in.read(buf);
				while(len != -1)
				{
					fos.write(buf, 0, len);
					len = in.read(buf);
				}
				in.close();
				fos.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	private void copyFolder(IFolder folder, File path) throws Exception
	{
		File folderPath = new File(path, folder.getName() + "/");
		folderPath.mkdirs();
		for(IResource r : folder.members())
		{
			if(r instanceof IFolder)
				copyFolder((IFolder)r, folderPath);
			else
			{
				File childPath = new File(folderPath, r.getName());
				FileOutputStream fos = new FileOutputStream(childPath);
				IFile f = (IFile)r;
				InputStream in = f.getContents();
				byte[] buf = new byte[10240];
				int len = in.read(buf);
				while(len != -1)
				{
					fos.write(buf);
					len = in.read(buf);
				}
				in.close();
				fos.close();
			}
		}
	}
	
	private void deleteDirectory(File directory)
	{
		File[] children = directory.listFiles();
		for(File child : children)
		{
			if(child.isDirectory())
				deleteDirectory(child);
			else
				child.delete();
		}
		directory.delete();
	}
	
	public class VoiceSelectionPage extends WizardPage implements
	IStructuredContentProvider, ICheckStateListener, SelectionListener
	{
		private List<IMediaProject> initialSelection;
		private CheckboxTableViewer viewer = null;
		private Button selectAll = null;
		private Button deselectAll = null;
		
		public VoiceSelectionPage(List<IMediaProject> initialSelection)
		{
			super("Voice Selection");
			setPageComplete(false);
			this.initialSelection = initialSelection;
		}

		@Override
		public void createControl(Composite parent)
		{
			Composite composite = new Composite(parent, SWT.NONE);
			composite.setLayout(new GridLayout(2, false));
			viewer = CheckboxTableViewer.newCheckList(composite, SWT.BORDER);
			viewer.addCheckStateListener(this);
			viewer.setContentProvider(this);
			viewer.setLabelProvider(new LabelProvider(){
				public String getText(Object element)
				{
					return ((IMediaProject)element).getName();
				}
			});
			viewer.setSorter(new ViewerSorter());
			viewer.setInput(voices);
			viewer.setCheckedElements(initialSelection.toArray());
			viewer.getTable().setLayoutData(
					new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));
			selectAll = new Button(composite, SWT.PUSH);
			selectAll.setText("Select All");
			selectAll.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
			selectAll.addSelectionListener(this);
			deselectAll = new Button(composite, SWT.PUSH);
			deselectAll.setText("Deselect All");
			deselectAll
					.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
			deselectAll.addSelectionListener(this);
			Label extra = new Label(composite, SWT.NONE);
			extra.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
			setControl(composite);
		}
		
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public Object[] getElements(Object inputElement) {
			return voices.values().toArray();
		}

		public void checkStateChanged(CheckStateChangedEvent event)
		{
			setPageComplete(viewer.getCheckedElements().length > 0);
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			if (e.getSource() == selectAll)
			{
				viewer.setCheckedElements(voices.values().toArray());
				setPageComplete(true);
			}
			else if (e.getSource() == deselectAll)
			{
				viewer.setCheckedElements(new Object[0]);
				setPageComplete(false);
			}
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
		
		public List<IMediaProject> getSelectedVoices()
		{
			Object[] checked = viewer.getCheckedElements();
			List<IMediaProject> ret = new ArrayList<IMediaProject>(checked.length);
			for(Object obj : checked)
			{
				ret.add((IMediaProject)obj);
			}
			return ret;
		}
		
	}

	public class LocationPage extends WizardPage
	{
		private Combo locationCombo = null;
		
		public LocationPage()
		{
			super("Export Location");
			setPageComplete(false);
		}

		@Override
		public void createControl(Composite parent)
		{
			Composite comp = new Composite(parent, SWT.NONE);
			comp.setLayout(new GridLayout(2, false));

			Label locationLabel = new Label(comp, SWT.NONE);
			locationLabel.setText("Select a location to export to");
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;
			locationLabel.setLayoutData(gd);
			
			locationCombo = new Combo(comp, SWT.DROP_DOWN | SWT.BORDER);
			locationCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			locationCombo.addModifyListener(new ModifyListener()
			{
				@Override
				public void modifyText(ModifyEvent e)
				{
					setPageComplete(locationCombo.getText() != null &&
									!locationCombo.getText().equals(""));
				}
			});
			Button browseButton = new Button(comp, SWT.PUSH | SWT.BORDER);
			browseButton.setText("Browse");
			browseButton.addSelectionListener(new SelectionListener()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					String path = new DirectoryDialog(getShell()).open();
					if (path == null)
						return;
					locationCombo.setText(path);
					setPageComplete(true);
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e)
				{
				}
			});
			setControl(comp);
		}
		
		public String getExportPath()
		{
			return locationCombo.getText();
		}
	}
}
