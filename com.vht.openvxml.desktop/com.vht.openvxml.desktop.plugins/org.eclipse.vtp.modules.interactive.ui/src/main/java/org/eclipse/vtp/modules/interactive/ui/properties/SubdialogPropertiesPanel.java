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
package org.eclipse.vtp.modules.interactive.ui.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.dialogs.PropertyDialog;
import org.eclipse.vtp.desktop.core.dialogs.FramedDialog;
import org.eclipse.vtp.desktop.editors.core.configuration.DesignElementPropertiesPanel;
import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.BrandBinding;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.GenericBindingManager;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.InteractionBinding;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.LanguageBinding;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.NamedBinding;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.PropertyBindingItem;
import org.eclipse.vtp.framework.util.QuickSort;
import org.eclipse.vtp.framework.util.VariableNameValidator;
import org.eclipse.vtp.modules.interactive.ui.SubdialogInformationProvider;
import org.eclipse.vtp.modules.interactive.ui.SubdialogInformationProvider.SubdialogInput;
import org.eclipse.vtp.modules.interactive.ui.SubdialogInformationProvider.SubdialogOutput;
import org.eclipse.vtp.modules.interactive.ui.SubdialogInformationProvider.SubdialogParameter;

import com.openmethods.openvxml.desktop.model.branding.IBrand;
import com.openmethods.openvxml.desktop.model.branding.internal.BrandContext;
import com.openmethods.openvxml.desktop.model.workflow.design.IDesignElement;
import com.openmethods.openvxml.desktop.model.workflow.design.ObjectDefinition;
import com.openmethods.openvxml.desktop.model.workflow.design.ObjectField;
import com.openmethods.openvxml.desktop.model.workflow.design.Variable;
import com.openmethods.openvxml.desktop.model.workflow.internal.VariableHelper;

/**
 * The UI used to configure a Subdialog module.
 * 
 * @author Trip
 */
public class SubdialogPropertiesPanel extends DesignElementPropertiesPanel {
	GenericBindingManager bindingManager;
	IBrand currentBrand;
	/**
	 * A list of all SubdialogParameter objects configured in this Subdialog
	 * module
	 */
	List<SubdialogParameter> urlParameters = new ArrayList<SubdialogParameter>();
	/** A list of all SubdialogInput objects configured in this Subdialog module */
	List<SubdialogInput> inputs = new ArrayList<SubdialogInput>();
	/**
	 * A list of all SubdialogOutput objects configured in this Subdialog module
	 */
	List<SubdialogOutput> outputs = new ArrayList<SubdialogOutput>();
	/**
	 * A text field used to display/change the name of this particular Subdialog
	 * module
	 */
	Text nameField = null;
	/** A Label used to label the Text field for the name of the subdialog */
	Label nameLabel = null;
	Composite container = null;

	Composite destinationContainer = null;
	Combo destinationType = null;
	Composite destinationComp = null;
	StackLayout destinationLayout = null;
	Composite destinationValueComp = null;
	Text destinationValue = null;
	Composite destinationExprComp = null;
	Text destinationExpr = null;
	Composite destinationTreeComp = null;
	TreeViewer destinationTree = null;

	Composite methodContainer = null;
	Combo methodSelectionType = null;
	Composite methodComp = null;
	StackLayout methodLayout = null;
	Composite methodComboComp = null;
	Combo methodCombo = null;
	Composite methodExprComp = null;
	Text methodExpr = null;
	Composite methodTreeComp = null;
	TreeViewer methodTree = null;

	/**
	 * A UI table of all SubdialogInput objects configured in this Subdialog
	 * module
	 */
	TableViewer inputTable = null;
	/**
	 * A UI table of all SubdialogOutput objects configured in this Subdialog
	 * module
	 */
	TableViewer outputTable = null;
	/**
	 * A UI table of all SubdialogParameter objects configured in this Subdialog
	 * module
	 */
	TableViewer urlParamTable = null;
	List<Variable> variables = new ArrayList<Variable>();
	private SubdialogInformationProvider info = null;

	/**
	 * Creates a new SubdialogPropertiesPanel
	 * 
	 * @param name
	 * @param subdialogElement
	 */
	public SubdialogPropertiesPanel(String name, IDesignElement subdialogElement) {
		super(name, subdialogElement);
		PrimitiveElement pe = (PrimitiveElement) subdialogElement;
		bindingManager = (GenericBindingManager) pe
				.getConfigurationManager(GenericBindingManager.TYPE_ID);
		info = (SubdialogInformationProvider) pe.getInformationProvider();
		inputs.addAll(info.getInputs());
		outputs.addAll(info.getOutputs());
		urlParameters.addAll(info.getURLParameters());
		List<Variable> vars = pe.getDesign().getVariablesFor(pe);
		outer: for (Variable v : vars) {
			for (int i = 0; i < variables.size(); i++) {
				if (variables.get(i).getName().compareToIgnoreCase(v.getName()) > 0) {
					variables.add(i, v);
					continue outer;
				}
			}
			variables.add(v);
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
		container = new Composite(parent, SWT.NONE);
		container.setBackground(parent.getBackground());
		container.setLayout(new GridLayout(2, false));
		nameLabel = new Label(container, SWT.NONE);
		nameLabel.setText("Name");
		nameLabel.setBackground(container.getBackground());
		nameLabel.setLayoutData(new GridData());
		nameField = new Text(container, SWT.SINGLE | SWT.BORDER);
		nameField.setText(getElement().getName());
		nameField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		nameField.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				String currentName = nameField.getText().substring(0, e.start)
						+ e.text
						+ nameField.getText(e.end, (nameField.getText()
								.length() - 1));
				if (VariableNameValidator.followsVtpNamingRules(currentName)) {
					nameLabel.setForeground(nameLabel.getDisplay()
							.getSystemColor(SWT.COLOR_BLACK));
					nameField.setForeground(nameField.getDisplay()
							.getSystemColor(SWT.COLOR_BLACK));
					getContainer().setCanFinish(true);
				} else {
					nameLabel.setForeground(nameLabel.getDisplay()
							.getSystemColor(SWT.COLOR_RED));
					nameField.setForeground(nameField.getDisplay()
							.getSystemColor(SWT.COLOR_RED));
					getContainer().setCanFinish(false);
				}
			}
		});
		if (VariableNameValidator.followsVtpNamingRules(nameField.getText())) {
			nameLabel.setForeground(nameLabel.getDisplay().getSystemColor(
					SWT.COLOR_BLACK));
			nameField.setForeground(nameField.getDisplay().getSystemColor(
					SWT.COLOR_BLACK));
			getContainer().setCanFinish(true);
		} else {
			nameLabel.setForeground(nameLabel.getDisplay().getSystemColor(
					SWT.COLOR_RED));
			nameField.setForeground(nameField.getDisplay().getSystemColor(
					SWT.COLOR_RED));
			getContainer().setCanFinish(false);
		}

		Label urlLabel = new Label(container, SWT.NONE);
		urlLabel.setText("URL: ");
		urlLabel.setBackground(container.getBackground());

		GridData gd3 = new GridData();
		gd3.verticalAlignment = SWT.TOP;
		urlLabel.setLayoutData(gd3);

		destinationContainer = new Composite(container, SWT.NONE);
		destinationContainer.setBackground(container.getBackground());
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		destinationContainer.setLayout(gridLayout);
		GridData gd2 = new GridData(SWT.FILL, SWT.FILL, true, true);
		destinationContainer.setLayoutData(gd2);

		destinationType = new Combo(destinationContainer, SWT.DROP_DOWN
				| SWT.READ_ONLY);
		GridData gd4 = new GridData();
		gd4.verticalAlignment = SWT.TOP;
		destinationType.setLayoutData(gd4);
		destinationType.add("Value");
		destinationType.add("Expression");
		destinationType.add("Variable");
		destinationType.select(0);
		destinationType.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				destinationTypeChanged();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		destinationComp = new Composite(destinationContainer, SWT.NONE);
		destinationComp.setBackground(container.getBackground());
		destinationComp.setLayout(destinationLayout = new StackLayout());
		destinationComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		destinationValueComp = new Composite(destinationComp, SWT.NONE);
		destinationValueComp.setBackground(destinationComp.getBackground());
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = layout.marginHeight = 0;
		destinationValueComp.setLayout(layout);
		destinationValue = new Text(destinationValueComp, SWT.SINGLE
				| SWT.BORDER);
		destinationValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		destinationExprComp = new Composite(destinationComp, SWT.NONE);
		destinationExprComp.setBackground(destinationComp.getBackground());
		layout = new GridLayout(1, false);
		layout.marginWidth = layout.marginHeight = 0;
		destinationExprComp.setLayout(layout);
		destinationExpr = new Text(destinationExprComp, SWT.SINGLE | SWT.BORDER);
		destinationExpr.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		destinationTreeComp = new Composite(destinationComp, SWT.NONE);
		destinationTreeComp.setBackground(destinationComp.getBackground());
		FormLayout fl = new FormLayout();
		destinationTreeComp.setLayout(fl);
		destinationTree = new TreeViewer(destinationTreeComp, SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.BORDER | SWT.SINGLE);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0);
		fd.top = new FormAttachment(0);
		fd.right = new FormAttachment(100);
		fd.bottom = new FormAttachment(100);
		fd.height = 10;
		destinationTree.getControl().setLayoutData(fd);
		destinationTree.setContentProvider(new VariableContentProvider());
		destinationTree.setLabelProvider(new VariableLabelProvider());
		destinationTree.setInput(this);
		destinationLayout.topControl = destinationValueComp;
		destinationComp.layout();

		Label methodLabel = new Label(container, SWT.NONE);
		methodLabel.setText("Method: ");
		methodLabel.setBackground(container.getBackground());

		gd3 = new GridData();
		gd3.verticalAlignment = SWT.TOP;
		methodLabel.setLayoutData(gd3);

		methodContainer = new Composite(container, SWT.NONE);
		methodContainer.setBackground(container.getBackground());
		gridLayout = new GridLayout(2, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		methodContainer.setLayout(gridLayout);
		gd2 = new GridData(SWT.FILL, SWT.FILL, true, true);
		methodContainer.setLayoutData(gd2);

		methodSelectionType = new Combo(methodContainer, SWT.DROP_DOWN
				| SWT.READ_ONLY);
		gd4 = new GridData();
		gd4.verticalAlignment = SWT.TOP;
		methodSelectionType.setLayoutData(gd4);
		methodSelectionType.add("Selection");
		methodSelectionType.add("Expression");
		methodSelectionType.add("Variable");
		methodSelectionType.select(0);
		methodSelectionType.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				methodSelectionTypeChanged();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		methodComp = new Composite(methodContainer, SWT.NONE);
		methodComp.setBackground(container.getBackground());
		methodComp.setLayout(methodLayout = new StackLayout());
		methodComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		methodComboComp = new Composite(methodComp, SWT.NONE);
		methodComboComp.setBackground(methodComp.getBackground());
		layout = new GridLayout(1, false);
		layout.marginWidth = layout.marginHeight = 0;
		methodComboComp.setLayout(layout);
		methodCombo = new Combo(methodComboComp, SWT.DROP_DOWN | SWT.READ_ONLY);
		methodCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		methodCombo.add("GET");
		methodCombo.add("POST");
		methodCombo.select(0);
		methodExprComp = new Composite(methodComp, SWT.NONE);
		methodExprComp.setBackground(methodComp.getBackground());
		layout = new GridLayout(1, false);
		layout.marginWidth = layout.marginHeight = 0;
		methodExprComp.setLayout(layout);
		methodExpr = new Text(methodExprComp, SWT.SINGLE | SWT.BORDER);
		methodExpr.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		methodTreeComp = new Composite(methodComp, SWT.NONE);
		methodTreeComp.setBackground(methodComp.getBackground());
		fl = new FormLayout();
		methodTreeComp.setLayout(fl);
		methodTree = new TreeViewer(methodTreeComp, SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.BORDER | SWT.SINGLE);
		fd = new FormData();
		fd.left = new FormAttachment(0);
		fd.top = new FormAttachment(0);
		fd.right = new FormAttachment(100);
		fd.bottom = new FormAttachment(100);
		fd.height = 10;
		methodTree.getControl().setLayoutData(fd);
		methodTree.setContentProvider(new VariableContentProvider());
		methodTree.setLabelProvider(new VariableLabelProvider());
		methodTree.setInput(this);
		methodLayout.topControl = methodComboComp;
		methodComp.layout();

		Group paramsGroup = new Group(container, SWT.NONE);
		paramsGroup.setBackground(container.getBackground());
		paramsGroup.setText("URL Settings");
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		paramsGroup.setLayoutData(gd);
		paramsGroup.setLayout(new GridLayout(2, true));
		urlParamTable = new TableViewer(paramsGroup, SWT.FULL_SELECTION
				| SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
		urlParamTable.getTable().setHeaderVisible(true);
		urlParamTable.getTable().setLinesVisible(true);
		TableColumn paramNameColumn = new TableColumn(urlParamTable.getTable(),
				SWT.NONE);
		paramNameColumn.setText("Name");
		paramNameColumn.setWidth(150);
		TableColumn paramValueColumn = new TableColumn(
				urlParamTable.getTable(), SWT.NONE);
		paramValueColumn.setText("Value");
		paramValueColumn.setWidth(150);
		urlParamTable.setContentProvider(new ParamTableContentProvider());
		urlParamTable.setLabelProvider(new ParamTableLabelProvider());
		urlParamTable.setInput(this);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		gd.heightHint = 90;
		gd.widthHint = 300;
		urlParamTable.getTable().setLayoutData(gd);
		urlParamTable.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				if (!urlParamTable.getSelection().isEmpty()) {
					ParameterValueDialog vd = new ParameterValueDialog(Display
							.getCurrent().getActiveShell());
					SubdialogParameter si = (SubdialogParameter) ((IStructuredSelection) urlParamTable
							.getSelection()).getFirstElement();
					vd.setValue(si);
					if (vd.open() == SWT.OK) {
						urlParamTable.refresh();
					}
				}
			}

		});
		urlParamTable.getControl().addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.DEL
						&& !urlParamTable.getSelection().isEmpty()) {
					SubdialogParameter si = (SubdialogParameter) ((IStructuredSelection) urlParamTable
							.getSelection()).getFirstElement();
					urlParameters.remove(si);
					urlParamTable.refresh();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
		});
		Button addButton = new Button(paramsGroup, SWT.PUSH);
		addButton.setText("Add Parameter");
		gd = new GridData();
		gd.horizontalAlignment = SWT.RIGHT;
		addButton.setLayoutData(gd);
		addButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				ParameterValueDialog vd = new ParameterValueDialog(Display
						.getCurrent().getActiveShell());
				SubdialogParameter si = info.new SubdialogParameter("", 0, "");
				vd.setValue(si);
				if (vd.open() == SWT.OK) {
					urlParameters.add(si);
					urlParamTable.refresh();
				}
			}

		});
		Button deleteButton = new Button(paramsGroup, SWT.PUSH);
		deleteButton.setText("Delete Parameter");
		deleteButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!urlParamTable.getSelection().isEmpty()) {
					SubdialogParameter si = (SubdialogParameter) ((IStructuredSelection) urlParamTable
							.getSelection()).getFirstElement();
					urlParameters.remove(si);
					urlParamTable.refresh();
				}
			}

		});
		gd = new GridData();
		deleteButton.setLayoutData(gd);

		Group inputsGroup = new Group(container, SWT.NONE);
		inputsGroup.setBackground(container.getBackground());
		inputsGroup.setText("Inputs");
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		inputsGroup.setLayoutData(gd);
		inputsGroup.setLayout(new GridLayout(2, true));
		inputTable = new TableViewer(inputsGroup, SWT.FULL_SELECTION
				| SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
		inputTable.getTable().setHeaderVisible(true);
		inputTable.getTable().setLinesVisible(true);
		TableColumn inputNameColumn = new TableColumn(inputTable.getTable(),
				SWT.NONE);
		inputNameColumn.setText("Name");
		inputNameColumn.setWidth(150);
		TableColumn inputValueColumn = new TableColumn(inputTable.getTable(),
				SWT.NONE);
		inputValueColumn.setText("Value");
		inputValueColumn.setWidth(150);
		inputTable.setContentProvider(new InputTableContentProvider());
		inputTable.setLabelProvider(new InputTableLabelProvider());
		inputTable.setInput(this);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		gd.heightHint = 90;
		gd.widthHint = 300;
		inputTable.getTable().setLayoutData(gd);
		inputTable.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				if (!inputTable.getSelection().isEmpty()) {
					InputValueDialog vd = new InputValueDialog(Display
							.getCurrent().getActiveShell());
					SubdialogInput si = (SubdialogInput) ((IStructuredSelection) inputTable
							.getSelection()).getFirstElement();
					vd.setValue(si);
					if (vd.open() == SWT.OK) {
						inputTable.refresh();
					}
				}
			}

		});
		inputTable.getControl().addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.DEL
						&& !inputTable.getSelection().isEmpty()) {
					SubdialogInput si = (SubdialogInput) ((IStructuredSelection) inputTable
							.getSelection()).getFirstElement();
					inputs.remove(si);
					inputTable.refresh();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
		});

		addButton = new Button(inputsGroup, SWT.PUSH);
		addButton.setText("Add Input");
		gd = new GridData();
		gd.horizontalAlignment = SWT.RIGHT;
		addButton.setLayoutData(gd);
		addButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				InputValueDialog vd = new InputValueDialog(Display.getCurrent()
						.getActiveShell());
				SubdialogInput si = info.new SubdialogInput("", 0, "");
				vd.setValue(si);
				if (vd.open() == SWT.OK) {
					inputs.add(si);
					inputTable.refresh();
				}
			}

		});
		deleteButton = new Button(inputsGroup, SWT.PUSH);
		deleteButton.setText("Delete Input");
		deleteButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!inputTable.getSelection().isEmpty()) {
					SubdialogInput si = (SubdialogInput) ((IStructuredSelection) inputTable
							.getSelection()).getFirstElement();
					inputs.remove(si);
					inputTable.refresh();
				}
			}

		});
		gd = new GridData();
		deleteButton.setLayoutData(gd);

		Group outputsGroup = new Group(container, SWT.NONE);
		outputsGroup.setBackground(container.getBackground());
		outputsGroup.setText("Outputs");
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		outputsGroup.setLayoutData(gd);
		outputsGroup.setLayout(new GridLayout(2, true));
		outputTable = new TableViewer(outputsGroup, SWT.FULL_SELECTION
				| SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
		outputTable.getTable().setHeaderVisible(true);
		outputTable.getTable().setLinesVisible(true);
		TableColumn outputNameColumn = new TableColumn(outputTable.getTable(),
				SWT.NONE);
		outputNameColumn.setText("Name");
		outputNameColumn.setWidth(150);
		TableColumn outputValueColumn = new TableColumn(outputTable.getTable(),
				SWT.NONE);
		outputValueColumn.setText("Value");
		outputValueColumn.setWidth(150);
		outputTable.setContentProvider(new OutputTableContentProvider());
		outputTable.setLabelProvider(new OutputTableLabelProvider());
		outputTable.setInput(this);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		gd.heightHint = 90;
		gd.widthHint = 300;
		outputTable.getTable().setLayoutData(gd);
		outputTable.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				if (!outputTable.getSelection().isEmpty()) {
					OutputValueDialog vd = new OutputValueDialog(Display
							.getCurrent().getActiveShell());
					SubdialogOutput so = (SubdialogOutput) ((IStructuredSelection) outputTable
							.getSelection()).getFirstElement();
					vd.setValue(so);
					if (vd.open() == SWT.OK) {
						outputTable.refresh();
					}
				}
			}

		});
		outputTable.getControl().addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.DEL
						&& !outputTable.getSelection().isEmpty()) {
					SubdialogOutput so = (SubdialogOutput) ((IStructuredSelection) outputTable
							.getSelection()).getFirstElement();
					outputs.remove(so);
					outputTable.refresh();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
		});
		addButton = new Button(outputsGroup, SWT.PUSH);
		addButton.setText("Add Output");
		gd = new GridData();
		gd.horizontalAlignment = SWT.RIGHT;
		addButton.setLayoutData(gd);
		addButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				OutputValueDialog vd = new OutputValueDialog(Display
						.getCurrent().getActiveShell());
				SubdialogOutput so = info.new SubdialogOutput("", "");
				vd.setValue(so);
				if (vd.open() == SWT.OK) {
					outputs.add(so);
					outputTable.refresh();
				}
			}

		});
		deleteButton = new Button(outputsGroup, SWT.PUSH);
		deleteButton.setText("Delete Output");
		gd = new GridData();
		deleteButton.setLayoutData(gd);
		deleteButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!outputTable.getSelection().isEmpty()) {
					SubdialogOutput so = (SubdialogOutput) ((IStructuredSelection) outputTable
							.getSelection()).getFirstElement();
					outputs.remove(so);
					outputTable.refresh();
				}
			}

		});
	}

	/**
	 * Sets which controls are visible based on the destination type
	 */
	private void destinationTypeChanged() {
		((FormData) destinationTree.getControl().getLayoutData()).height = 10;
		switch (destinationType.getSelectionIndex()) {
		case 2:
			destinationLayout.topControl = destinationTreeComp;
			((FormData) destinationTree.getControl().getLayoutData()).height = 175;
			break;
		case 1:
			destinationLayout.topControl = destinationExprComp;
			break;
		default:
			destinationLayout.topControl = destinationValueComp;
		}
		destinationTreeComp.layout();
		destinationComp.layout();
		destinationContainer.layout();
		container.layout();
	}

	/**
	 * Sets which controls are visible based on the method selection type
	 */
	private void methodSelectionTypeChanged() {
		((FormData) methodTree.getControl().getLayoutData()).height = 10;
		switch (methodSelectionType.getSelectionIndex()) {
		case 2:
			methodLayout.topControl = methodTreeComp;
			((FormData) methodTree.getControl().getLayoutData()).height = 175;
			break;
		case 1:
			methodLayout.topControl = methodExprComp;
			break;
		default:
			methodLayout.topControl = methodComboComp;
		}
		methodTreeComp.layout();
		methodComp.layout();
		methodContainer.layout();
		container.layout();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.desktop.ui.app.editor.model.ComponentPropertiesPanel#
	 * save()
	 */
	@Override
	public void save() {
		getElement().setName(nameField.getText());
		storeBindings();
		getElement().commitConfigurationChanges(bindingManager);
		info.setInputs(inputs);
		info.setOutputs(outputs);
		info.setURLParameters(urlParameters);
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
		getElement().rollbackConfigurationChanges(bindingManager);
	}

	public class ParamTableContentProvider implements
			IStructuredContentProvider {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(
		 * java.lang.Object)
		 */
		@Override
		public Object[] getElements(Object inputElement) {
			return urlParameters.toArray();
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

	public class ParamTableLabelProvider implements ITableLabelProvider {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java
		 * .lang.Object, int)
		 */
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.
		 * lang.Object, int)
		 */
		@Override
		public String getColumnText(Object element, int columnIndex) {
			SubdialogParameter si = (SubdialogParameter) element;
			if (columnIndex == 0) {
				return si.name;
			}
			return si.value;
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
			return false;
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

	public class InputTableContentProvider implements
			IStructuredContentProvider {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(
		 * java.lang.Object)
		 */
		@Override
		public Object[] getElements(Object inputElement) {
			return inputs.toArray();
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

	public class InputTableLabelProvider implements ITableLabelProvider {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java
		 * .lang.Object, int)
		 */
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.
		 * lang.Object, int)
		 */
		@Override
		public String getColumnText(Object element, int columnIndex) {
			SubdialogInput si = (SubdialogInput) element;
			if (columnIndex == 0) {
				return si.name;
			}
			return si.value;
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
			return false;
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

	public class OutputTableContentProvider implements
			IStructuredContentProvider {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(
		 * java.lang.Object)
		 */
		@Override
		public Object[] getElements(Object inputElement) {
			return outputs.toArray();
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

	public class OutputTableLabelProvider implements ITableLabelProvider {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java
		 * .lang.Object, int)
		 */
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.
		 * lang.Object, int)
		 */
		@Override
		public String getColumnText(Object element, int columnIndex) {
			SubdialogOutput so = (SubdialogOutput) element;
			if (columnIndex == 0) {
				return so.varName;
			}
			return so.valueName;
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
			return false;
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

	/**
	 * A dialog used to configure the properties of a SubdialogInput object.
	 */
	public class InputValueDialog extends FramedDialog {
		/** A text field used to display/modify the name of the variable */
		Text variableNameField;
		TreeViewer variableViewer;
		/** A text field used to display/modify a static value for the variable */
		Text staticValueField;
		/** The SubdialogInput object this dialog will modify */
		SubdialogInformationProvider.SubdialogInput value;
		Color darkBlue;
		Color lightBlue;
		List<ObjectDefinition> vars;
		Composite staticValueComp;
		Composite variableTreeComp;
		Composite valueComp;
		StackLayout valueLayout;
		Combo valueType;
		Label nameLabel;
		/** The button used to dismiss the dialog and keep the changes */
		Button okButton;

		/**
		 * Creates a new InputValueDialog
		 * 
		 * @param shellProvider
		 */
		@SuppressWarnings("unchecked")
		public InputValueDialog(Shell shell) {
			super(shell);
			this.setSideBarSize(40);
			this.setTitle("Select a value");

			List<Variable> unsortedVars = getElement().getDesign()
					.getVariablesFor(getElement());
			if (unsortedVars == null) {
				this.vars = null;
			} else {
				Comparable<Comparer>[] comp = new Comparable[unsortedVars
						.size()];
				for (int b = 0; b < unsortedVars.size(); b++) {
					Object obj = unsortedVars.get(b);
					Comparer compr = new Comparer((ObjectDefinition) obj);
					comp[b] = compr;
				}
				comp = QuickSort.comparableSort(comp);
				this.vars = new ArrayList<ObjectDefinition>();
				for (Comparable<Comparer> element : comp) {
					this.vars.add(((Comparer) element).od);
				}
			}
		}

		/**
		 * Specifies which SubdialogInput object to modify
		 * 
		 * @param value
		 *            - the SubdialogInput object to modify
		 */
		public void setValue(SubdialogInformationProvider.SubdialogInput value) {
			this.value = value;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.vtp.desktop.core.dialogs.FramedDialog#createButtonBar
		 * (org.eclipse.swt.widgets.Composite)
		 */
		@Override
		protected void createButtonBar(Composite parent) {
			parent.setLayout(new GridLayout(1, true));

			Composite buttons = new Composite(parent, SWT.NONE);
			buttons.setBackground(parent.getBackground());

			GridData buttonsData = new GridData(GridData.FILL_BOTH);
			buttonsData.horizontalAlignment = SWT.RIGHT;
			buttons.setLayoutData(buttonsData);

			RowLayout rl = new RowLayout();
			rl.pack = false;
			rl.spacing = 5;
			buttons.setLayout(rl);

			okButton = new Button(buttons, SWT.PUSH);
			okButton.setText("Ok");
			okButton.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					okPressed();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

			final Button cancelButton = new Button(buttons, SWT.PUSH);
			cancelButton.setText("Cancel");
			cancelButton.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					cancelPressed();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

			if (Display.getCurrent().getDismissalAlignment() == SWT.RIGHT) {
				cancelButton.moveAbove(okButton);
			}
			this.getShell().setDefaultButton(okButton);
		}

		/**
		 * Saves any changes made to this object and exits with a return code of
		 * SWT.OK
		 */
		public void okPressed() {
			value.name = variableNameField.getText();
			if (valueType.getSelectionIndex() == 1) {
				value.type = 1;
				if (!variableViewer.getSelection().isEmpty()) {
					value.value = ((ObjectDefinition) ((IStructuredSelection) variableViewer
							.getSelection()).getFirstElement()).getPath();
				} else {
					value.value = "";
				}
			} else {
				value.type = 0;
				value.value = staticValueField.getText();
			}
			this.setReturnCode(SWT.OK);
			close();
		}

		/**
		 * Cancels any changes made to this object and exits with a return code
		 * of SWT.CANCEL
		 */
		public void cancelPressed() {
			this.setReturnCode(SWT.CANCEL);
			close();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.vtp.desktop.core.dialogs.FramedDialog#createDialogContents
		 * (org.eclipse.swt.widgets.Composite)
		 */
		@Override
		protected void createDialogContents(Composite parent) {
			darkBlue = new Color(parent.getDisplay(), 77, 113, 179);
			lightBlue = new Color(parent.getDisplay(), 240, 243, 249);
			parent.addDisposeListener(new DisposeListener() {
				@Override
				public void widgetDisposed(DisposeEvent e) {
					darkBlue.dispose();
					lightBlue.dispose();
				}
			});
			this.setFrameColor(darkBlue);
			this.setSideBarColor(lightBlue);
			parent.setLayout(new GridLayout(2, false));

			nameLabel = new Label(parent, SWT.NONE);
			nameLabel.setText("Input Name");
			nameLabel.setBackground(parent.getBackground());
			GridData gd = new GridData();
			nameLabel.setLayoutData(gd);

			variableNameField = new Text(parent, SWT.SINGLE | SWT.BORDER);
			variableNameField.setText(value.name);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;
			variableNameField.setLayoutData(gd);
			variableNameField.addVerifyListener(new VerifyListener() {
				@Override
				public void verifyText(VerifyEvent e) {
					String currentName = variableNameField.getText().substring(
							0, e.start)
							+ e.text
							+ variableNameField.getText(e.end,
									(variableNameField.getText().length() - 1));
					if (VariableNameValidator
							.followsEcmaNamingRules(currentName)) {
						nameLabel.setForeground(nameLabel.getDisplay()
								.getSystemColor(SWT.COLOR_BLACK));
						variableNameField.setForeground(variableNameField
								.getDisplay().getSystemColor(SWT.COLOR_BLACK));
						okButton.setEnabled(true);
						// TODO check for name collisions
						// for(int b = 0; b < reservedNames.size(); b++)
						// {
						// if(currentName.equals(reservedNames.get(b))) //Is
						// this name taken?
						// {
						// nameLabel.setForeground(nameLabel.getDisplay().getSystemColor(SWT.COLOR_RED));
						// variableNameField.setForeground(variableNameField.getDisplay().getSystemColor(SWT.COLOR_RED));
						// okButton.setEnabled(false);
						// }
						// }
					} else {
						nameLabel.setForeground(nameLabel.getDisplay()
								.getSystemColor(SWT.COLOR_RED));
						variableNameField.setForeground(variableNameField
								.getDisplay().getSystemColor(SWT.COLOR_RED));
						okButton.setEnabled(false);
					}
				}
			});
			okButton.setEnabled(VariableNameValidator
					.followsEcmaNamingRules(variableNameField.getText()));

			valueType = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
			GridData valueTypeGridData = new GridData();
			valueTypeGridData.verticalAlignment = SWT.TOP;
			valueType.setLayoutData(valueTypeGridData);
			valueType.add("Value");
			valueType.add("Variable");
			valueType.select(value.type == 1 ? 1 : 0);
			valueType.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					valueTypeChanged();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

			valueComp = new Composite(parent, SWT.None);
			valueComp.setBackground(parent.getBackground());
			valueComp.setLayout(valueLayout = new StackLayout());
			valueComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			staticValueComp = new Composite(valueComp, SWT.NONE);
			staticValueComp.setBackground(valueComp.getBackground());
			GridLayout layout = new GridLayout(1, false);
			layout.marginWidth = layout.marginHeight = 0;
			staticValueComp.setLayout(layout);

			variableTreeComp = new Composite(valueComp, SWT.NONE);
			variableTreeComp.setBackground(valueComp.getBackground());
			layout = new GridLayout(1, false);
			layout.marginWidth = layout.marginHeight = 0;
			variableTreeComp.setLayout(layout);

			Tree variableTree = new Tree(variableTreeComp, SWT.BORDER
					| SWT.SINGLE | SWT.FULL_SELECTION);
			gd = new GridData(GridData.FILL_BOTH);
			gd.horizontalIndent = 10;
			gd.horizontalSpan = 2;
			variableTree.setLayoutData(gd);
			variableViewer = new TreeViewer(variableTree);
			variableViewer.setContentProvider(new VariableContentProvider());
			variableViewer.setLabelProvider(new VariableLabelProvider());
			variableViewer.setInput(this);

			staticValueField = new Text(staticValueComp, SWT.BORDER
					| SWT.SINGLE);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;
			if (value.type == 1) {
				for (int i = 0; i < vars.size(); i++) {
					Variable v = (Variable) vars.get(i);

					if (v.getName().equals(value.value)) {
						variableViewer.setSelection(new StructuredSelection(v));
					} else if ((value.value != null)
							&& value.value.startsWith(v.getName())) {
						List<ObjectField> objectFields = v.getFields();

						for (int f = 0; f < objectFields.size(); f++) {
							ObjectField of = objectFields.get(f);

							if (of.getPath().equals(value.value)) {
								variableViewer
										.setSelection(new StructuredSelection(
												of));
							}
						}
					}
				}
			}
			staticValueField.setLayoutData(gd);

			if (value.type == 1) {
				valueLayout.topControl = variableTreeComp;
			} else {
				staticValueField.setText((value.value == null) ? ""
						: value.value);
				valueLayout.topControl = staticValueComp;
			}
		}

		/**
		 * Sets which controls are visible based on the value type
		 */
		private void valueTypeChanged() {
			switch (valueType.getSelectionIndex()) {
			case 1:
				valueLayout.topControl = variableTreeComp;
				break;
			default:
				valueLayout.topControl = staticValueComp;
			}
			valueComp.layout();
		}

		public class VariableContentProvider implements ITreeContentProvider {
			@Override
			public Object[] getElements(Object inputElement) {
				return vars.toArray();
			}

			@Override
			public void dispose() {
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}

			@Override
			public Object[] getChildren(Object parentElement) {
				return ((ObjectDefinition) parentElement).getFields().toArray();
			}

			@Override
			public Object getParent(Object element) {
				if (element instanceof Variable) {
					return null;
				} else {
					return ((ObjectField) element).getParent();
				}
			}

			@Override
			public boolean hasChildren(Object element) {
				return true;
			}
		}

		public class VariableLabelProvider extends LabelProvider {
			@Override
			public Image getImage(Object element) {
				return org.eclipse.vtp.desktop.core.Activator.getDefault()
						.getImageRegistry().get("ICON_TINY_SQUARE");
			}

			@Override
			public String getText(Object element) {
				return ((ObjectDefinition) element).getName();
			}
		}
	}

	/**
	 * A dialog used to configure the properties of a SubdialogOutput object.
	 */
	public class OutputValueDialog extends FramedDialog {
		/** A text field used to display/modify the name of the variable */
		Text variableNameField;
		/** A text field used to display/modify a static value for the variable */
		Text staticValueField;
		/** The SubdialogOutput object this dialog will modify */
		SubdialogInformationProvider.SubdialogOutput value;
		Color darkBlue;
		Color lightBlue;
		Button okButton;
		Label nameLabel;
		Label valueLabel;

		/**
		 * Creates a new OutputValueDialog
		 * 
		 * @param shellProvider
		 */
		public OutputValueDialog(Shell shell) {
			super(shell);
			this.setSideBarSize(40);
			this.setTitle("Select a value");
		}

		/**
		 * Specifies which SubdialogOutput object to modify
		 * 
		 * @param value
		 *            - the SubdialogOutput object to modify
		 */
		public void setValue(SubdialogInformationProvider.SubdialogOutput value) {
			this.value = value;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.vtp.desktop.core.dialogs.FramedDialog#createButtonBar
		 * (org.eclipse.swt.widgets.Composite)
		 */
		@Override
		protected void createButtonBar(Composite parent) {
			parent.setLayout(new GridLayout(1, true));

			Composite buttons = new Composite(parent, SWT.NONE);
			buttons.setBackground(parent.getBackground());

			GridData buttonsData = new GridData(GridData.FILL_BOTH);
			buttonsData.horizontalAlignment = SWT.RIGHT;
			buttons.setLayoutData(buttonsData);

			RowLayout rl = new RowLayout();
			rl.pack = false;
			rl.spacing = 5;
			buttons.setLayout(rl);

			okButton = new Button(buttons, SWT.PUSH);
			okButton.setText("Ok");
			okButton.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					okPressed();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

			final Button cancelButton = new Button(buttons, SWT.PUSH);
			cancelButton.setText("Cancel");
			cancelButton.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					cancelPressed();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

			if (Display.getCurrent().getDismissalAlignment() == SWT.RIGHT) {
				cancelButton.moveAbove(okButton);
			}
			this.getShell().setDefaultButton(okButton);
		}

		/**
		 * Saves any changes made to this object and exits with a return code of
		 * SWT.OK
		 */
		public void okPressed() {
			value.varName = variableNameField.getText();
			value.valueName = staticValueField.getText();
			this.setReturnCode(SWT.OK);
			close();
		}

		/**
		 * Cancels any changes made to this object and exits with a return code
		 * of SWT.CANCEL
		 */
		public void cancelPressed() {
			this.setReturnCode(SWT.CANCEL);
			close();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.vtp.desktop.core.dialogs.FramedDialog#createDialogContents
		 * (org.eclipse.swt.widgets.Composite)
		 */
		@Override
		protected void createDialogContents(Composite parent) {
			darkBlue = new Color(parent.getDisplay(), 77, 113, 179);
			lightBlue = new Color(parent.getDisplay(), 240, 243, 249);
			parent.addDisposeListener(new DisposeListener() {
				@Override
				public void widgetDisposed(DisposeEvent e) {
					darkBlue.dispose();
					lightBlue.dispose();
				}
			});
			this.setFrameColor(darkBlue);
			this.setSideBarColor(lightBlue);
			parent.setLayout(new GridLayout(2, false));

			nameLabel = new Label(parent, SWT.NONE);
			nameLabel.setText("Target Variable Name");
			nameLabel.setBackground(parent.getBackground());
			GridData gd = new GridData();
			nameLabel.setLayoutData(gd);

			variableNameField = new Text(parent, SWT.SINGLE | SWT.BORDER);
			variableNameField.setText(value.varName);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;
			variableNameField.setLayoutData(gd);
			variableNameField.addVerifyListener(new VerifyListener() {
				@Override
				public void verifyText(VerifyEvent e) {
					String currentName = variableNameField.getText().substring(
							0, e.start)
							+ e.text
							+ variableNameField.getText(e.end,
									(variableNameField.getText().length() - 1));
					if (VariableNameValidator
							.followsEcmaNamingRules(currentName)) {
						nameLabel.setForeground(nameLabel.getDisplay()
								.getSystemColor(SWT.COLOR_BLACK));
						variableNameField.setForeground(variableNameField
								.getDisplay().getSystemColor(SWT.COLOR_BLACK));
						if (VariableNameValidator
								.followsEcmaNamingRules(staticValueField
										.getText())) {
							okButton.setEnabled(true);
							// TODO check for name collisions
							// for(int b = 0; b < reservedNames.size(); b++)
							// {
							// if(currentName.equals(reservedNames.get(b))) //Is
							// this name taken?
							// {
							// nameLabel.setForeground(nameLabel.getDisplay().getSystemColor(SWT.COLOR_RED));
							// variableNameField.setForeground(variableNameField.getDisplay().getSystemColor(SWT.COLOR_RED));
							// okButton.setEnabled(false);
							// }
							// }
						}
					} else {
						nameLabel.setForeground(nameLabel.getDisplay()
								.getSystemColor(SWT.COLOR_RED));
						variableNameField.setForeground(variableNameField
								.getDisplay().getSystemColor(SWT.COLOR_RED));
						okButton.setEnabled(false);
					}
				}
			});

			valueLabel = new Label(parent, SWT.NONE);
			valueLabel.setText("Subdialog Return Value");
			valueLabel.setBackground(parent.getBackground());
			gd = new GridData();
			valueLabel.setLayoutData(gd);

			staticValueField = new Text(parent, SWT.SINGLE | SWT.BORDER);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;
			staticValueField.setLayoutData(gd);
			staticValueField.setText((value.valueName == null) ? ""
					: value.valueName);
			staticValueField.addVerifyListener(new VerifyListener() {
				@Override
				public void verifyText(VerifyEvent e) {
					String currentName = staticValueField.getText().substring(
							0, e.start)
							+ e.text
							+ staticValueField.getText(e.end, (staticValueField
									.getText().length() - 1));
					if (VariableNameValidator
							.followsEcmaNamingRules(currentName)) {
						valueLabel.setForeground(valueLabel.getDisplay()
								.getSystemColor(SWT.COLOR_BLACK));
						staticValueField.setForeground(staticValueField
								.getDisplay().getSystemColor(SWT.COLOR_BLACK));
						if (VariableNameValidator
								.followsEcmaNamingRules(variableNameField
										.getText())) {
							okButton.setEnabled(true);
						}
					} else {
						valueLabel.setForeground(valueLabel.getDisplay()
								.getSystemColor(SWT.COLOR_RED));
						staticValueField.setForeground(staticValueField
								.getDisplay().getSystemColor(SWT.COLOR_RED));
						okButton.setEnabled(false);
					}
				}
			});
			okButton.setEnabled(VariableNameValidator
					.followsEcmaNamingRules(variableNameField.getText())
					&& VariableNameValidator
							.followsEcmaNamingRules(staticValueField.getText()));

		}

	}

	/**
	 * A dialog used to configure the properties of a SubdialogParameter object.
	 */
	public class ParameterValueDialog extends FramedDialog {
		/** A text field used to display/modify the name of the variable */
		Text variableNameField;
		TreeViewer variableViewer;
		/** A text field used to display/modify a static value for the variable */
		Text staticValueField;
		/** The SubdialogParameter object this dialog will modify */
		SubdialogInformationProvider.SubdialogParameter value;
		Color darkBlue;
		Color lightBlue;
		List<ObjectDefinition> vars;
		Composite staticValueComp;
		Composite variableTreeComp;
		Composite valueComp;
		StackLayout valueLayout;
		Combo valueType;
		Label nameLabel;
		/** The button used to dismiss the dialog and keep the changes */
		Button okButton;

		/**
		 * Creates a new ParameterValueDialog
		 * 
		 * @param shellProvider
		 */
		@SuppressWarnings("unchecked")
		public ParameterValueDialog(Shell shell) {
			super(shell);
			this.setSideBarSize(40);
			this.setTitle("Select a value");

			List<Variable> unsortedVars = getElement().getDesign()
					.getVariablesFor(getElement());
			if (unsortedVars == null) {
				this.vars = null;
			} else {
				Comparable<Comparer>[] comp = new Comparable[unsortedVars
						.size()];
				for (int b = 0; b < unsortedVars.size(); b++) {
					Comparer compr = new Comparer(unsortedVars.get(b));
					comp[b] = compr;
				}
				comp = QuickSort.comparableSort(comp);
				this.vars = new ArrayList<ObjectDefinition>();
				for (Comparable<Comparer> element : comp) {
					this.vars.add(((Comparer) element).od);
				}
			}
		}

		/**
		 * Specifies which SubdialogParameter object to modify
		 * 
		 * @param value
		 *            - the SubdialogParameter object to modify
		 */
		public void setValue(
				SubdialogInformationProvider.SubdialogParameter value) {
			this.value = value;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.vtp.desktop.core.dialogs.FramedDialog#createButtonBar
		 * (org.eclipse.swt.widgets.Composite)
		 */
		@Override
		protected void createButtonBar(Composite parent) {
			parent.setLayout(new GridLayout(1, true));

			Composite buttons = new Composite(parent, SWT.NONE);
			buttons.setBackground(parent.getBackground());

			GridData buttonsData = new GridData(GridData.FILL_BOTH);
			buttonsData.horizontalAlignment = SWT.RIGHT;
			buttons.setLayoutData(buttonsData);

			RowLayout rl = new RowLayout();
			rl.pack = false;
			rl.spacing = 5;
			buttons.setLayout(rl);

			okButton = new Button(buttons, SWT.PUSH);
			okButton.setText("Ok");
			okButton.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					okPressed();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

			final Button cancelButton = new Button(buttons, SWT.PUSH);
			cancelButton.setText("Cancel");
			cancelButton.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					cancelPressed();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

			if (Display.getCurrent().getDismissalAlignment() == SWT.RIGHT) {
				cancelButton.moveAbove(okButton);
			}
			this.getShell().setDefaultButton(okButton);
		}

		/**
		 * Saves any changes made to this object and exits with a return code of
		 * SWT.OK
		 */
		public void okPressed() {
			value.name = variableNameField.getText();
			if (valueType.getSelectionIndex() == 1) {
				value.type = 1;
				if (!variableViewer.getSelection().isEmpty()) {
					value.value = ((ObjectDefinition) ((IStructuredSelection) variableViewer
							.getSelection()).getFirstElement()).getPath();
				} else {
					value.value = "";
				}
			} else {
				value.type = 0;
				value.value = staticValueField.getText();
			}
			this.setReturnCode(SWT.OK);
			close();
		}

		/**
		 * Cancels any changes made to this object and exits with a return code
		 * of SWT.CANCEL
		 */
		public void cancelPressed() {
			this.setReturnCode(SWT.CANCEL);
			close();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.vtp.desktop.core.dialogs.FramedDialog#createDialogContents
		 * (org.eclipse.swt.widgets.Composite)
		 */
		@Override
		protected void createDialogContents(Composite parent) {
			darkBlue = new Color(parent.getDisplay(), 77, 113, 179);
			lightBlue = new Color(parent.getDisplay(), 240, 243, 249);
			parent.addDisposeListener(new DisposeListener() {
				@Override
				public void widgetDisposed(DisposeEvent e) {
					darkBlue.dispose();
					lightBlue.dispose();
				}
			});
			this.setFrameColor(darkBlue);
			this.setSideBarColor(lightBlue);
			parent.setLayout(new GridLayout(2, false));

			nameLabel = new Label(parent, SWT.NONE);
			nameLabel.setText("Parameter Name");
			nameLabel.setBackground(parent.getBackground());
			GridData gd = new GridData();
			nameLabel.setLayoutData(gd);

			variableNameField = new Text(parent, SWT.SINGLE | SWT.BORDER);
			variableNameField.setText(value.name);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;
			variableNameField.setLayoutData(gd);
			variableNameField.addVerifyListener(new VerifyListener() {
				@Override
				public void verifyText(VerifyEvent e) {
					String currentName = variableNameField.getText().substring(
							0, e.start)
							+ e.text
							+ variableNameField.getText(e.end,
									(variableNameField.getText().length() - 1));
					if (VariableNameValidator
							.followsEcmaNamingRules(currentName)) {
						nameLabel.setForeground(nameLabel.getDisplay()
								.getSystemColor(SWT.COLOR_BLACK));
						variableNameField.setForeground(variableNameField
								.getDisplay().getSystemColor(SWT.COLOR_BLACK));
						okButton.setEnabled(true);
						// TODO check for name collisions
						// for(int b = 0; b < reservedNames.size(); b++)
						// {
						// if(currentName.equals(reservedNames.get(b))) //Is
						// this name taken?
						// {
						// nameLabel.setForeground(nameLabel.getDisplay().getSystemColor(SWT.COLOR_RED));
						// variableNameField.setForeground(variableNameField.getDisplay().getSystemColor(SWT.COLOR_RED));
						// okButton.setEnabled(false);
						// }
						// }
					} else {
						nameLabel.setForeground(nameLabel.getDisplay()
								.getSystemColor(SWT.COLOR_RED));
						variableNameField.setForeground(variableNameField
								.getDisplay().getSystemColor(SWT.COLOR_RED));
						okButton.setEnabled(false);
					}
				}
			});
			okButton.setEnabled(VariableNameValidator
					.followsEcmaNamingRules(variableNameField.getText()));

			valueType = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
			GridData valueTypeGridData = new GridData();
			valueTypeGridData.verticalAlignment = SWT.TOP;
			valueType.setLayoutData(valueTypeGridData);
			valueType.add("Value");
			valueType.add("Variable");
			valueType.select(value.type == 1 ? 1 : 0);
			valueType.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					valueTypeChanged();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

			valueComp = new Composite(parent, SWT.None);
			valueComp.setBackground(parent.getBackground());
			valueComp.setLayout(valueLayout = new StackLayout());
			valueComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			staticValueComp = new Composite(valueComp, SWT.NONE);
			staticValueComp.setBackground(valueComp.getBackground());
			GridLayout layout = new GridLayout(1, false);
			layout.marginWidth = layout.marginHeight = 0;
			staticValueComp.setLayout(layout);

			variableTreeComp = new Composite(valueComp, SWT.NONE);
			variableTreeComp.setBackground(valueComp.getBackground());
			layout = new GridLayout(1, false);
			layout.marginWidth = layout.marginHeight = 0;
			variableTreeComp.setLayout(layout);

			Tree variableTree = new Tree(variableTreeComp, SWT.BORDER
					| SWT.SINGLE | SWT.FULL_SELECTION);
			gd = new GridData(GridData.FILL_BOTH);
			gd.horizontalIndent = 10;
			gd.horizontalSpan = 2;
			variableTree.setLayoutData(gd);
			variableViewer = new TreeViewer(variableTree);
			variableViewer.setContentProvider(new VariableContentProvider());
			variableViewer.setLabelProvider(new VariableLabelProvider());
			variableViewer.setInput(this);

			staticValueField = new Text(staticValueComp, SWT.BORDER
					| SWT.SINGLE);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;

			if (value.type == 1) {
				for (int i = 0; i < vars.size(); i++) {
					Variable v = (Variable) vars.get(i);

					if (v.getName().equals(value.value)) {
						variableViewer.setSelection(new StructuredSelection(v));
					} else if ((value.value != null)
							&& value.value.startsWith(v.getName())) {
						List<ObjectField> objectFields = v.getFields();

						for (int f = 0; f < objectFields.size(); f++) {
							ObjectField of = objectFields.get(f);

							if (of.getPath().equals(value.value)) {
								variableViewer
										.setSelection(new StructuredSelection(
												of));
							}
						}
					}
				}
			}

			staticValueField.setLayoutData(gd);

			if (value.type == 1) {
				valueLayout.topControl = variableTreeComp;
			} else {
				staticValueField.setText((value.value == null) ? ""
						: value.value);
				valueLayout.topControl = staticValueComp;
			}
		}

		/**
		 * Sets which controls are visible based on the value type
		 */
		private void valueTypeChanged() {
			switch (valueType.getSelectionIndex()) {
			case 1:
				valueLayout.topControl = variableTreeComp;
				break;
			default:
				valueLayout.topControl = staticValueComp;
			}
			valueComp.layout();
		}

		public class VariableContentProvider implements ITreeContentProvider {
			@Override
			public Object[] getElements(Object inputElement) {
				return vars.toArray();
			}

			@Override
			public void dispose() {
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}

			@Override
			public Object[] getChildren(Object parentElement) {
				return ((ObjectDefinition) parentElement).getFields().toArray();
			}

			@Override
			public Object getParent(Object element) {
				if (element instanceof Variable) {
					return null;
				} else {
					return ((ObjectField) element).getParent();
				}
			}

			@Override
			public boolean hasChildren(Object element) {
				return true;
			}
		}

		public class VariableLabelProvider extends LabelProvider {
			@Override
			public Image getImage(Object element) {
				return org.eclipse.vtp.desktop.core.Activator.getDefault()
						.getImageRegistry().get("ICON_TINY_SQUARE");
			}

			@Override
			public String getText(Object element) {
				return ((ObjectDefinition) element).getName();
			}
		}
	}

	private class Comparer implements Comparable<Comparer> {
		ObjectDefinition od = null;

		/**
		 * @param od
		 */
		public Comparer(ObjectDefinition od) {
			this.od = od;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(Comparer arg0) {
			return od.getName().compareToIgnoreCase(arg0.od.getName());
		}

	}

	@Override
	public void setConfigurationContext(Map<String, Object> values) {
		if (!(currentBrand == null)) {
			storeBindings();
		}

		currentBrand = (IBrand) values.get(BrandContext.CONTEXT_ID);
		if (currentBrand == null) {
			final IOpenVXMLProject project = getElement().getDesign()
					.getDocument().getProject();
			final IProject uproject = project.getUnderlyingProject();
			final Shell shell = this.getContainer().getParentShell();
			Display.getCurrent().asyncExec(new Runnable() {
				@Override
				public void run() {
					MessageBox mb = new MessageBox(shell, SWT.OK | SWT.CANCEL
							| SWT.ICON_ERROR);
					mb.setText("Configuration Problems");
					mb.setMessage("The interaction and language configuration for this project is incomplete.  You will not be able edit the applications effectively until this is resolved.  Would you like to configure this now?");
					if (mb.open() == SWT.OK) {
						Display.getCurrent().asyncExec(new Runnable() {
							@Override
							public void run() {
								PropertyDialog pd = PropertyDialog
										.createDialogOn(
												PlatformUI
														.getWorkbench()
														.getActiveWorkbenchWindow()
														.getShell(),
												"org.eclipse.vtp.desktop.projects.core.appproperties",
												uproject);
								pd.open();
							}
						});
					}
					getContainer().cancelDialog();
				}
			});
			return;
		}
		InteractionBinding interactionBinding = bindingManager
				.getInteractionBinding("");
		NamedBinding namedBinding = interactionBinding
				.getNamedBinding("destination");
		LanguageBinding languageBinding = namedBinding.getLanguageBinding("");
		BrandBinding brandBinding = languageBinding
				.getBrandBinding(currentBrand);
		System.out.println(currentBrand.getId());
		PropertyBindingItem valuePropertyItem = (PropertyBindingItem) brandBinding
				.getBindingItem();
		if (valuePropertyItem == null) {
			System.out.println("Value item is null");
			valuePropertyItem = new PropertyBindingItem();
		}
		System.out.println("VALUE TYPE: " + valuePropertyItem.getValueType());
		if (valuePropertyItem.getValue() != null) {
			if (valuePropertyItem.getValueType().equals(
					PropertyBindingItem.STATIC)) {
				destinationType.select(0);
				destinationValue.setText(valuePropertyItem.getValue());
			} else if (valuePropertyItem.getValueType().equals(
					PropertyBindingItem.EXPRESSION)) {
				destinationType.select(1);
				destinationExpr.setText(valuePropertyItem.getValue());
			} else {
				destinationType.select(2);
				ObjectDefinition od = VariableHelper
						.getObjectDefinitionFromVariables(variables,
								valuePropertyItem.getValue());
				StructuredSelection ss = (od == null) ? StructuredSelection.EMPTY
						: new StructuredSelection(od);
				destinationTree.setSelection(ss);
			}
		}
		destinationTypeChanged();

		// TODO clean up system.out items
		interactionBinding = bindingManager.getInteractionBinding("");
		namedBinding = interactionBinding.getNamedBinding("method");
		languageBinding = namedBinding.getLanguageBinding("");
		brandBinding = languageBinding.getBrandBinding(currentBrand);
		System.out.println(currentBrand.getId());
		valuePropertyItem = (PropertyBindingItem) brandBinding.getBindingItem();
		if (valuePropertyItem == null) {
			System.out.println("Value item is null");
			valuePropertyItem = new PropertyBindingItem();
		}
		System.out.println("VALUE TYPE: " + valuePropertyItem.getValueType());
		if (valuePropertyItem.getValue() != null) {
			if (valuePropertyItem.getValueType().equals(
					PropertyBindingItem.STATIC)) {
				methodSelectionType.select(0);
				methodCombo.select("POST".equalsIgnoreCase(valuePropertyItem
						.getValue()) ? 1 : 0);
			} else if (valuePropertyItem.getValueType().equals(
					PropertyBindingItem.EXPRESSION)) {
				methodSelectionType.select(1);
				methodExpr.setText(valuePropertyItem.getValue());
			} else {
				methodSelectionType.select(2);
				ObjectDefinition od = VariableHelper
						.getObjectDefinitionFromVariables(variables,
								valuePropertyItem.getValue());
				StructuredSelection ss = (od == null) ? StructuredSelection.EMPTY
						: new StructuredSelection(od);
				methodTree.setSelection(ss);
			}
		}
		methodSelectionTypeChanged();
	}

	private void storeBindings() {
		try {
			InteractionBinding interactionBinding = bindingManager
					.getInteractionBinding("");
			NamedBinding namedBinding = interactionBinding
					.getNamedBinding("destination");
			LanguageBinding languageBinding = namedBinding
					.getLanguageBinding("");
			BrandBinding brandBinding = languageBinding
					.getBrandBinding(currentBrand);
			PropertyBindingItem valuePropertyItem = (PropertyBindingItem) brandBinding
					.getBindingItem();
			if (valuePropertyItem == null) {
				valuePropertyItem = new PropertyBindingItem();
			} else {
				valuePropertyItem = (PropertyBindingItem) valuePropertyItem
						.clone();
			}
			switch (destinationType.getSelectionIndex()) {
			case 2:
				ISelection selection = destinationTree.getSelection();
				if ((selection != null) && !selection.isEmpty()
						&& selection instanceof IStructuredSelection) {
					Object selObj = ((IStructuredSelection) selection)
							.getFirstElement();
					if (selObj instanceof ObjectDefinition) {
						valuePropertyItem
								.setVariable(((ObjectDefinition) selObj)
										.getPath());
						break;
					}
				}
				valuePropertyItem.setVariable("");
				break;
			case 1:
				valuePropertyItem.setExpression(destinationExpr.getText());
				break;
			default:
				valuePropertyItem.setStaticValue(destinationValue.getText());
			}
			brandBinding.setBindingItem(valuePropertyItem);

			namedBinding = interactionBinding.getNamedBinding("method");
			languageBinding = namedBinding.getLanguageBinding("");
			brandBinding = languageBinding.getBrandBinding(currentBrand);
			valuePropertyItem = (PropertyBindingItem) brandBinding
					.getBindingItem();
			if (valuePropertyItem == null) {
				valuePropertyItem = new PropertyBindingItem();
			} else {
				valuePropertyItem = (PropertyBindingItem) valuePropertyItem
						.clone();
			}
			switch (methodSelectionType.getSelectionIndex()) {
			case 2:
				ISelection selection = methodTree.getSelection();
				if ((selection != null) && !selection.isEmpty()
						&& selection instanceof IStructuredSelection) {
					Object selObj = ((IStructuredSelection) selection)
							.getFirstElement();
					if (selObj instanceof ObjectDefinition) {
						valuePropertyItem
								.setVariable(((ObjectDefinition) selObj)
										.getPath());
						break;
					}
				}
				valuePropertyItem.setVariable("");
				break;
			case 1:
				valuePropertyItem.setExpression(methodExpr.getText());
				break;
			default:
				valuePropertyItem.setStaticValue(methodCombo
						.getSelectionIndex() == 1 ? "POST" : "GET");
			}
			brandBinding.setBindingItem(valuePropertyItem);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
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

	/**
	 * VariableLabelProvider.
	 * 
	 * @author Lonnie Pryor
	 */
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
}
