/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006-2007 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.modules.attacheddata.ui.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.vtp.desktop.editors.core.configuration.DesignElementPropertiesPanel;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.eclipse.vtp.modules.attacheddata.ui.ReceiveAttachedDataInformationProvider;

import com.openmethods.openvxml.desktop.model.workflow.design.IDesignElement;
import com.openmethods.openvxml.desktop.model.workflow.design.ObjectDefinition;
import com.openmethods.openvxml.desktop.model.workflow.design.ObjectField;
import com.openmethods.openvxml.desktop.model.workflow.design.Variable;

public class ReceiveAttachedDataPropertiesPanel extends
		DesignElementPropertiesPanel {
	static final String lastString = "LastResult";
	/** The InformationProvider for this particular module */
	ReceiveAttachedDataInformationProvider info = null;
	/** The text field used to set name of this particular module */
	Text nameField;
	TreeViewer lVarTree;
	TreeViewer rVarTree;
	List<Object> variables = new ArrayList<Object>();

	public ReceiveAttachedDataPropertiesPanel(String name, IDesignElement ppe) {
		super(name, ppe);
		info = (ReceiveAttachedDataInformationProvider) ((PrimitiveElement) ppe)
				.getInformationProvider();
		List<Variable> vars = ppe.getDesign().getVariablesFor(ppe);
		outer: for (Variable v : vars) {
			for (int i = 0; i < this.variables.size(); i++) {
				Variable cur = (Variable) this.variables.get(i);
				if (cur.getName().compareToIgnoreCase(v.getName()) > 0) {
					this.variables.add(i, v);
					continue outer;
				}
			}
			this.variables.add(v);
		}
		for (int i = 0; i < this.variables.size(); i++) {
			Variable cur = (Variable) this.variables.get(i);
			if (cur.getName().compareToIgnoreCase(lastString) > 0) {
				this.variables.add(i, lastString);
				break;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.desktop.ui.app.editor.model.ComponentPropertiesPanel#
	 * createControls(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControls(Composite parent) {
		parent.setLayout(new FillLayout());

		Composite comp = new Composite(parent, SWT.NONE);
		comp.setBackground(parent.getBackground());
		comp.setLayout(new GridLayout(2, false));

		Label nameLabel = new Label(comp, SWT.NONE);
		nameLabel.setText("Name: ");
		nameLabel.setBackground(comp.getBackground());
		nameLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
		nameField = new Text(comp, SWT.SINGLE | SWT.BORDER);
		nameField.setText(getElement().getName());
		nameField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_CENTER));

		Composite builderComp = new Composite(comp, SWT.NONE);
		builderComp.setBackground(comp.getBackground());

		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		builderComp.setLayout(new GridLayout(3, false));
		builderComp.setLayoutData(gd);

		Group lGroup = new Group(builderComp, SWT.TITLE);
		lGroup.setBackground(builderComp.getBackground());
		lGroup.setText("Keys Names (String[])");
		lGroup.setLayout(new GridLayout(2, false));
		gd = new GridData(GridData.FILL_BOTH);
		lGroup.setLayoutData(gd);

		final Composite stackComp1 = new Composite(lGroup, SWT.NONE);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		stackComp1.setLayoutData(gd);

		final StackLayout sl1 = new StackLayout();
		stackComp1.setLayout(sl1);
		stackComp1.setBackground(lGroup.getBackground());
		lVarTree = new TreeViewer(stackComp1, SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.BORDER | SWT.SINGLE);
		lVarTree.setContentProvider(new VariableContentProvider());
		lVarTree.setLabelProvider(new VariableLabelProvider());
		lVarTree.setInput(this);
		sl1.topControl = lVarTree.getControl();

		Group rGroup = new Group(builderComp, SWT.TITLE);
		rGroup.setBackground(builderComp.getBackground());
		rGroup.setText("Output Variable (Map)");
		rGroup.setLayout(new GridLayout(2, false));
		gd = new GridData(GridData.FILL_BOTH);
		rGroup.setLayoutData(gd);

		final Composite stackComp2 = new Composite(rGroup, SWT.NONE);
		stackComp2.setBackground(rGroup.getBackground());
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		stackComp2.setLayoutData(gd);

		final StackLayout sl2 = new StackLayout();
		stackComp2.setLayout(sl2);
		rVarTree = new TreeViewer(stackComp2, SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.BORDER | SWT.SINGLE);
		rVarTree.setContentProvider(new VariableContentProvider());
		rVarTree.setLabelProvider(new VariableLabelProvider());
		rVarTree.setInput(this);
		sl2.topControl = rVarTree.getControl();

		ObjectDefinition od = getObjectDefinitionFromVariables(info.getInput());
		StructuredSelection ss = (od == null) ? StructuredSelection.EMPTY
				: new StructuredSelection(od);
		lVarTree.setSelection(ss);

		ObjectDefinition rod = getObjectDefinitionFromVariables(info
				.getOutput());
		StructuredSelection rss = (rod == null) ? StructuredSelection.EMPTY
				: new StructuredSelection(rod);
		rVarTree.setSelection(rss);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.desktop.editors.core.elements.PrimitivePropertiesPanel
	 * #save()
	 */
	@Override
	public void save() {
		getElement().setName(nameField.getText());

		ISelection selection = lVarTree.getSelection();

		if ((selection != null) && !selection.isEmpty()
				&& selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			Object selObj = ss.getFirstElement();

			if (selObj instanceof ObjectDefinition) {
				ObjectDefinition objDef = (ObjectDefinition) selObj;
				info.setInput(objDef.getPath());
			} else if (selObj instanceof String) {
				String selString = (String) selObj;
				if (!selString.equals("LastResult")) {
					info.setInput("LastResult." + selString);
				} else {
					info.setInput(selString);
				}
			} else {
				info.setInput("");
			}
		} else {
			info.setInput("");
		}

		selection = rVarTree.getSelection();

		if ((selection != null) && !selection.isEmpty()
				&& selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			Object selObj = ss.getFirstElement();

			if (selObj instanceof ObjectDefinition) {
				ObjectDefinition objDef = (ObjectDefinition) selObj;
				info.setOutput(objDef.getPath());
			} else if (selObj instanceof String) {
				String selString = (String) selObj;
				if (!selString.equals("LastResult")) {
					info.setOutput("LastResult." + selString);
				} else {
					info.setOutput(selString);
				}
			} else {
				info.setOutput("");
			}
		} else {
			info.setOutput("");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.desktop.model.core.configuration.ComponentPropertiesPanel
	 * #cancel()
	 */
	@Override
	public void cancel() {

	}

	/**
	 * @param name
	 * @return
	 */
	public ObjectDefinition getObjectDefinitionFromVariables(String name) {
		ObjectDefinition ret = null;
		List<Variable> vars = getElement().getDesign().getVariablesFor(
				getElement());

		for (int i = 0; i < vars.size(); i++) {
			String varName = name;
			boolean sub = false;

			if (name.indexOf(".") != -1) {
				varName = name.substring(0, name.indexOf("."));
				sub = true;
			}

			Variable v = vars.get(i);

			if (v.getName().equals(varName)) {
				if (sub) {
					// dig deeper
					ret = getObjectDefinitionFromFields(
							name.substring(name.indexOf(".") + 1), v);
				} else {
					ret = v;

					break;
				}
			}
		}

		return ret;
	}

	/**
	 * @param name
	 * @param parent
	 * @return
	 */
	public ObjectDefinition getObjectDefinitionFromFields(String name,
			ObjectDefinition parent) {
		ObjectDefinition ret = null;
		List<ObjectField> fields = parent.getFields();

		for (int i = 0; i < fields.size(); i++) {
			String varName = name;
			boolean sub = false;

			if (name.indexOf('.') != -1) {
				varName = name.substring(0, name.indexOf("."));
				sub = true;
			}

			ObjectField of = fields.get(i);

			if (of.getName().equals(varName)) {
				if (sub) {
					ret = getObjectDefinitionFromFields(
							name.substring(name.indexOf(".") + 1), of);
				} else {
					ret = of;

					break;
				}
			}
		}

		return ret;
	}

	public class VariableContentProvider implements ITreeContentProvider {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang
		 * .Object)
		 */
		@Override
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof ObjectDefinition) {
				ObjectDefinition v = (ObjectDefinition) parentElement;

				return v.getFields().toArray();
			} else if (parentElement instanceof String
					&& ((String) parentElement).equals(lastString)) {
				return new String[] { "markname", "marktime", "confidence",
						"inputmode", "interpretation", "utterance" };
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang
		 * .Object)
		 */
		@Override
		public Object getParent(Object element) {
			if (element instanceof Variable) {
				return null;
			} else if (element instanceof String) {
				if (((String) element).equals(lastString)) {
					return null;
				}
				return lastString;
			} else {
				return ((ObjectField) element).getParent();
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang
		 * .Object)
		 */
		@Override
		public boolean hasChildren(Object element) {
			if (element instanceof String) {
				if (((String) element).equals(lastString)) {
					return true;
				}
				return false;
			}
			return ((ObjectDefinition) element).getFields().size() > 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(
		 * java.lang.Object)
		 */
		@Override
		public Object[] getElements(Object inputElement) {
			return variables.toArray();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		@Override
		public void dispose() {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse
		 * .jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	public class VariableLabelProvider implements ILabelProvider {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
		 */
		@Override
		public Image getImage(Object element) {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			if (element instanceof String) {
				return (String) element;
			}
			return ((ObjectDefinition) element).getName();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse
		 * .jface.viewers.ILabelProviderListener)
		 */
		@Override
		public void addListener(ILabelProviderListener listener) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
		 */
		@Override
		public void dispose() {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java
		 * .lang.Object, java.lang.String)
		 */
		@Override
		public boolean isLabelProperty(Object element, String property) {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse
		 * .jface.viewers.ILabelProviderListener)
		 */
		@Override
		public void removeListener(ILabelProviderListener listener) {
		}
	}

	@Override
	public void setConfigurationContext(Map<String, Object> values) {
	}

	@Override
	public List<String> getApplicableContexts() {
		return Collections.emptyList();
	}

}
