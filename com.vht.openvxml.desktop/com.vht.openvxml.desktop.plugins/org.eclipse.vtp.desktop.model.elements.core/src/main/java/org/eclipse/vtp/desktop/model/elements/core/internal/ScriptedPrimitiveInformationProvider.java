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
package org.eclipse.vtp.desktop.model.elements.core.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.elements.core.PrimitiveInformationProvider;
import org.eclipse.vtp.framework.util.XMLWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.openmethods.openvxml.desktop.model.businessobjects.FieldType;
import com.openmethods.openvxml.desktop.model.businessobjects.FieldType.Primitive;
import com.openmethods.openvxml.desktop.model.businessobjects.IBusinessObject;
import com.openmethods.openvxml.desktop.model.businessobjects.IBusinessObjectField;
import com.openmethods.openvxml.desktop.model.businessobjects.IBusinessObjectProjectAspect;
import com.openmethods.openvxml.desktop.model.businessobjects.IBusinessObjectSet;
import com.openmethods.openvxml.desktop.model.businessobjects.internal.BusinessObject;
import com.openmethods.openvxml.desktop.model.businessobjects.internal.BusinessObjectField;
import com.openmethods.openvxml.desktop.model.workflow.design.IDesignElement;
import com.openmethods.openvxml.desktop.model.workflow.design.IDesignElementConnectionPoint;
import com.openmethods.openvxml.desktop.model.workflow.design.Variable;
import com.openmethods.openvxml.desktop.model.workflow.internal.VariableHelper;
import com.openmethods.openvxml.desktop.model.workflow.internal.design.ConnectorRecord;

public class ScriptedPrimitiveInformationProvider extends
		PrimitiveInformationProvider {
	String typeId;
	String typeName;
	boolean acceptsConnectors = false;
	List<ConnectorRecord> connectorRecords = new ArrayList<ConnectorRecord>();
	List<Variable> variables = new ArrayList<Variable>();
	IConfigurationElement configurationElement = null;
	boolean canDelete = true;
	boolean hasConnectors = false;
	List<VarRecord> varRecords = new ArrayList<VarRecord>();
	List<VarPropertySet> varPropertySets = new ArrayList<VarPropertySet>();

	public ScriptedPrimitiveInformationProvider(PrimitiveElement element) {
		super(element);
	}

	public void init(IConfigurationElement configurationElement) {
		acceptsConnectors = Boolean.parseBoolean(configurationElement
				.getAttribute("acceptsConnectors"));
		if (configurationElement.getAttribute("canDelete") != null) {
			canDelete = Boolean.parseBoolean(configurationElement
					.getAttribute("canDelete"));
		}
		typeId = configurationElement.getAttribute("typeid");
		typeName = configurationElement.getAttribute("typename");

		IConfigurationElement exitPointsElement = configurationElement
				.getChildren("exit_points")[0];
		IConfigurationElement[] exitPointElements = exitPointsElement
				.getChildren("exit_point");
		for (IConfigurationElement exitPointElement : exitPointElements) {
			ConnectorRecord cr = new ConnectorRecord(getElement(),
					exitPointElement.getAttribute("name"),
					IDesignElementConnectionPoint.ConnectionPointType
							.valueOf(exitPointElement.getAttribute("type")));
			connectorRecords.add(cr);
			hasConnectors = true;
		}
		IConfigurationElement actionsElement = configurationElement
				.getChildren("variables")[0];
		IConfigurationElement[] actionElements = actionsElement
				.getChildren("variable");
		for (IConfigurationElement actionElement : actionElements) {
			String variableName = actionElement.getAttribute("name");
			String variableType = actionElement.getAttribute("type");
			if (variableType != null) // legacy support
			{
				boolean array = false;
				if (actionElement.getAttribute("array") != null
						&& "true".equalsIgnoreCase(actionElement
								.getAttribute("array"))) // support for old
															// format
				{
					array = true;
				}
				Primitive prim = Primitive.find(variableType);
				if (prim != null) {
					if (array) {
						varPropertySets.add(new VarPropertySet(variableName,
								Primitive.ARRAY.getName(), variableType));
					} else {
						varPropertySets.add(new VarPropertySet(variableName,
								variableType, null));
					}
				} else {
					if (array) {
						varPropertySets.add(new VarPropertySet(variableName,
								Primitive.ARRAY.getName(), variableType));
					} else {
						varPropertySets.add(new VarPropertySet(variableName,
								variableType, null));
					}
				}
			} else {
				String ftTypeName;
				String ftBaseTypeName = null;
				int precision = 1;

				IConfigurationElement[] typeElements = actionElement
						.getChildren("data-type");
				if (typeElements.length > 0) {
					IConfigurationElement element = typeElements[0];
					String typeName = element.getAttribute("type");
					ftTypeName = typeName;
					if (typeName.startsWith("primitive:")) {
						typeName = typeName.substring(10);
						ftTypeName = typeName;
						Primitive type = Primitive.find(typeName);
						if (type.hasBaseType()) {
							String baseTypeName = element
									.getAttribute("base-type");
							if (baseTypeName.startsWith("primitive:")) {
								baseTypeName = baseTypeName.substring(10);
								ftBaseTypeName = baseTypeName;
								Primitive baseType = Primitive
										.find(baseTypeName);
								if (baseType.hasPrecision()) {
									try {
										precision = Integer.parseInt(element
												.getAttribute("precision"));
									} catch (NumberFormatException nfe) {
									}
								}
							} else // business object type
							{
								baseTypeName = baseTypeName.substring(7);
								ftBaseTypeName = baseTypeName;
							}
						}

						if (type.hasPrecision()) {
							try {
								precision = Integer.parseInt(element
										.getAttribute("precision"));
							} catch (NumberFormatException nfe) {
							}
						}
					}

					varPropertySets.add(new VarPropertySet(variableName,
							ftTypeName, ftBaseTypeName, precision));
				}
			}
		}
	}

	@Override
	public ConnectorRecord getConnectorRecord(String recordName) {
		for (int i = 0; i < connectorRecords.size(); i++) {
			ConnectorRecord cr = connectorRecords.get(i);
			if (cr.getName().equals(recordName)) {
				return cr;
			}
		}
		return null;
	}

	@Override
	public List<ConnectorRecord> getConnectorRecords() {
		return connectorRecords;
	}

	@Override
	public List<ConnectorRecord> getConnectorRecords(
			IDesignElementConnectionPoint.ConnectionPointType... types) {
		int flags = IDesignElementConnectionPoint.ConnectionPointType
				.getFlagSet(types);
		if ((flags & 0xFFFFFFF8) != 0) {
			// combination of the available types
			throw new IllegalArgumentException(
					"DesignConnector type must be one of those defined in the class ConnectorRecord.");
		}
		List<ConnectorRecord> ret = new ArrayList<ConnectorRecord>();
		for (int i = 0; i < connectorRecords.size(); i++) {
			ConnectorRecord cr = connectorRecords.get(i);
			if (cr.getType().isSet(flags)) {
				ret.add(cr);
			}
		}
		return ret;
	}

	@Override
	public void readConfiguration(Element configuration) {
	}

	@Override
	public void writeConfiguration(Element configuration) {
	}

	@Override
	public boolean acceptsConnector(IDesignElement origin) {
		return acceptsConnectors;
	}

	public String getTypeId() {
		return typeId;
	}

	public String getTypeName() {
		return typeName;
	}

	@Override
	public boolean hasConnectors() {
		return hasConnectors;
	}

	public void addVariable(String name, FieldType type) {
		Variable v = new Variable(name, type);
		IOpenVXMLProject project = getElement().getDesign().getDocument()
				.getProject();
		IBusinessObjectProjectAspect aspect = (IBusinessObjectProjectAspect) project
				.getProjectAspect(IBusinessObjectProjectAspect.ASPECT_ID);
		VariableHelper.buildObjectFields(v, aspect.getBusinessObjectSet());
		variables.add(v);
	}

	@Override
	public List<Variable> getOutgoingVariables(String exitPoint,
			boolean localOnly) {
		varRecords.clear();
		for (VarPropertySet vps : varPropertySets) {
			varRecords.add(createVarRecord(vps));
		}

		if (variables.size() == 0 && varRecords.size() != 0) {
			for (VarRecord vr : varRecords) {
				addVariable(vr.name, vr.type);
			}
		}
		return variables;
	}

	public void declareBusinessObjects(IBusinessObjectSet businessObjectSet) {
		if (configurationElement != null) {
			IConfigurationElement[] bosArray = configurationElement
					.getChildren("business-objects");
			if (bosArray != null && bosArray.length > 0 && bosArray[0] != null) {
				IConfigurationElement[] boElements = bosArray[0]
						.getChildren("business-object");
				for (IConfigurationElement boElement : boElements) {
					boolean safeToCreate = false;
					String boDefName = boElement.getAttribute("name");

					Map<String, BusinessObjectFieldDefinition> fieldDefs = new HashMap<String, BusinessObjectFieldDefinition>();
					IConfigurationElement[] fieldElements = boElement
							.getChildren("field");
					for (IConfigurationElement fieldElement : fieldElements) {
						String name = fieldElement.getAttribute("name"); // Was
																			// already
																			// an
																			// attribute
						FieldType ft = null;
						String type = fieldElement.getAttribute("type"); // Was
																			// already
																			// an
																			// attribute
						if (fieldElement.getAttribute("object") != null) // legacy
																			// support
						{
							boolean array = Boolean.parseBoolean(fieldElement
									.getAttribute("array")); // Compatible
																// because
																// parseBoolean(null)
																// returns
																// false
							Primitive prim = Primitive.find(type);
							if (prim != null) {
								if (array) {
									ft = new FieldType(Primitive.ARRAY, prim);
								} else {
									ft = new FieldType(prim);
								}
							} else {
								IBusinessObject bo = businessObjectSet
										.getBusinessObject(type);
								if (array) {
									ft = new FieldType(Primitive.ARRAY, bo);
								} else {
									ft = new FieldType(bo);
								}
							}
						} else {
							Primitive prim = Primitive.find(type);
							if (prim != null) {
								if (prim.hasBaseType()) {
									String baseType = fieldElement
											.getAttribute("basetype");
									Primitive basePrim = Primitive
											.find(baseType);
									if (basePrim != null) {
										ft = new FieldType(prim, basePrim);
									} else {
										ft = new FieldType(
												prim,
												businessObjectSet
														.getBusinessObject(baseType));
									}
								} else {
									ft = new FieldType(prim);
								}
							} else {
								IBusinessObject bo = businessObjectSet
										.getBusinessObject(type);
								ft = new FieldType(bo);
							}
						}
						int precision; // Compatible because the null value
										// loads old default
						String precisionText = fieldElement
								.getAttribute("precision");
						if ("double".equals(precisionText)) {
							precision = FieldType.DOUBLE;
						} else if ("absolute".equals(precisionText)) {
							precision = FieldType.ABSOLUTE;
						} else {
							precision = FieldType.SINGLE;
						}
						ft.setPrecision(precision);
						String initialValue = fieldElement
								.getAttribute("initial_value"); // Compatible
																// because null
																// value load
																// old default
						if (initialValue == null) {
							initialValue = "";
						}
						boolean secured = Boolean.parseBoolean(fieldElement
								.getAttribute("secured")); // Compatible because
															// parseBoolean(null)
															// returns false

						fieldDefs.put(fieldElement.getAttribute("name"),
								new BusinessObjectFieldDefinition(name, ft,
										initialValue, secured));
					}

					List<IBusinessObject> existingBOs = businessObjectSet
							.getBusinessObjects();
					if (existingBOs != null && existingBOs.size() > 0) {
						boolean noBoNameMatches = true;
						for (IBusinessObject existingBo : existingBOs) {
							// Is there a name collision?
							if (existingBo.getName()
									.equalsIgnoreCase(boDefName)) {
								noBoNameMatches = false;
								List<IBusinessObjectField> existingBoFields = existingBo
										.getFields();
								boolean fieldsMatch = true;
								// Why is there a name collision?
								// Right number of fields?
								if (fieldDefs.size() == existingBoFields.size()) {
									// Compare each of the fields
									for (IBusinessObjectField existingBof : existingBoFields) {
										// Does the field name match?
										if (fieldDefs.containsKey(existingBof
												.getName())) {
											BusinessObjectFieldDefinition newBof = fieldDefs
													.get(existingBof.getName());
											if (!(existingBof.getDataType()
													.getPrimitiveType() == newBof
													.getType()
													.getPrimitiveType()
													&& (existingBof
															.getDataType()
															.getPrimitiveBaseType() == newBof
															.getType()
															.getPrimitiveType())
													&& (existingBof
															.getDataType()
															.getObjectType() == newBof
															.getType()
															.getObjectType())
													&& existingBof
															.getDataType()
															.getObjectBaseType() == newBof
															.getType()
															.getObjectBaseType()
													&& existingBof
															.getInitialValue()
															.equals(newBof
																	.getInitialValue()) && (existingBof
														.isSecured() == newBof
													.isSecured()))) {
												// there is an attribute of the
												// field that doesn't match
												fieldsMatch = false;
												break;
											}
										} else {
											// there is a field name that
											// doesn't have a match
											fieldsMatch = false;
											break;
										}
									}
								} else {
									// Wrong number of fields
									fieldsMatch = false;
								}

								if (fieldsMatch) {
									// same name, same definition - do nothing
								} else {
									// same name, different definition - warn
									// the user
									// TODO warn the user of the situation
									// TODO offer the user some options along
									// with the notification
									System.out
											.println("There is already a Business Object by that name. Skipping declaration of the new one."); // TODO
																																				// remove
																																				// this
																																				// up
																																				// after
																																				// the
																																				// user
																																				// prompt
																																				// is
																																				// added
								}
							}
						}
						if (noBoNameMatches) {
							// There is no BO definition with this name, so it's
							// safe to create this one
							safeToCreate = true;
						}
					} else {
						// There are no existing BO definitions, so it's safe to
						// create this one
						safeToCreate = true;
					}

					if (safeToCreate) {
						// The name is not taken - create the definition
						BusinessObject bo;
						try {
							bo = (BusinessObject) businessObjectSet
									.createBusinessObject(boDefName);

							Document doc = DocumentBuilderFactory.newInstance()
									.newDocumentBuilder().newDocument();
							Element rootElement = doc
									.createElement("business-object");
							doc.appendChild(rootElement);
							rootElement.setAttribute("id", bo.getId());
							rootElement.setAttribute("name", bo.getName());

							Element fields = rootElement.getOwnerDocument()
									.createElement("fields");
							rootElement.appendChild(fields);

							for (BusinessObjectFieldDefinition bofd : fieldDefs
									.values()) {
								BusinessObjectField bof = new BusinessObjectField(
										bo, bofd.getName(), bofd.getType(),
										bofd.getInitialValue(),
										bofd.isSecured());
								Element fieldElement = fields
										.getOwnerDocument().createElement(
												"field");
								fields.appendChild(fieldElement);
								fieldElement
										.setAttribute("name", bof.getName());
								fieldElement.setAttribute("initialValue",
										bof.getInitialValue());
								fieldElement.setAttribute("secured",
										Boolean.toString(bof.isSecured()));
								bofd.getType().write(fieldElement);
							}

							DOMSource source = new DOMSource(doc);
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							Transformer trans = TransformerFactory
									.newInstance().newTransformer();
							trans.setOutputProperty(OutputKeys.ENCODING,
									"UTF-8");
							trans.transform(source,
									new XMLWriter(baos).toXMLResult());
							bo.write(new ByteArrayInputStream(baos
									.toByteArray()));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			for (VarRecord vr : varRecords) {
				addVariable(vr.name, vr.type);
			}
		}
	}

	private VarRecord createVarRecord(VarPropertySet vps) {
		FieldType ft;

		Primitive type = Primitive.find(vps.typeName);

		if (type.hasBaseType()) {
			String baseTypeName = vps.baseTypeName;
			Primitive baseType = Primitive.find(baseTypeName);
			if (baseType != null) // Should return primitive type if it's not an
									// object; Otherwise null
			{
				ft = new FieldType(type, baseType);
				if (baseType.hasPrecision()) {
					ft.setPrecision(vps.precision);
				}
			} else // business object type
			{
				IOpenVXMLProject project = getElement().getDesign()
						.getDocument().getProject();
				IBusinessObjectProjectAspect aspect = (IBusinessObjectProjectAspect) project
						.getProjectAspect(IBusinessObjectProjectAspect.ASPECT_ID);
				IBusinessObject bo = aspect.getBusinessObjectSet()
						.getBusinessObject(baseTypeName);
				if (bo == null) {
					throw new RuntimeException(
							"Missing business object definition: "
									+ baseTypeName);
				}
				ft = new FieldType(type, bo);
			}
		} else {
			ft = new FieldType(type);
		}

		if (type.hasPrecision()) {
			ft.setPrecision(vps.precision);
		}

		return new VarRecord(vps.variableName, ft);
	}

	private class BusinessObjectFieldDefinition {
		private String name;
		private FieldType type;
		private String initialValue;
		private boolean secured;

		BusinessObjectFieldDefinition(String name, FieldType type,
				String initialValue, boolean secured) {
			this.name = name;
			this.type = type;
			this.initialValue = initialValue;
			this.secured = secured;
		}

		String getName() {
			return name;
		}

		FieldType getType() {
			return type;
		}

		String getInitialValue() {
			return initialValue;
		}

		boolean isSecured() {
			return secured;
		}
	}

	private class VarRecord {
		String name;
		FieldType type;

		public VarRecord(String name, FieldType type) {
			super();
			this.name = name;
			this.type = type;
		}
	}

	private class VarPropertySet {

		String variableName; // name of the variable
		String typeName; // String,Date,Number,Array,Map, ObjectName, etc
		String baseTypeName; // if typeName is Array or Map, then this
								// parameterizes with
								// String,Date,Number,ObjectName, etc
		int precision = 1; // Set 1 as the default

		public VarPropertySet(String variableName, String typeName,
				String baseTypeName) {
			this.variableName = variableName;
			this.typeName = typeName;
			this.baseTypeName = baseTypeName;
		}

		public VarPropertySet(String variableName, String typeName,
				String baseTypeName, int precision) {
			this.variableName = variableName;
			this.typeName = typeName;
			this.baseTypeName = baseTypeName;
			this.precision = precision;
		}
	}
}
