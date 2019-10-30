package com.openmethods.openvxml.desktop.model.workflow.internal;

import java.util.List;

import com.openmethods.openvxml.desktop.model.businessobjects.FieldType;
import com.openmethods.openvxml.desktop.model.businessobjects.FieldType.Primitive;
import com.openmethods.openvxml.desktop.model.businessobjects.IBusinessObject;
import com.openmethods.openvxml.desktop.model.businessobjects.IBusinessObjectField;
import com.openmethods.openvxml.desktop.model.businessobjects.IBusinessObjectSet;
import com.openmethods.openvxml.desktop.model.workflow.design.ObjectDefinition;
import com.openmethods.openvxml.desktop.model.workflow.design.ObjectField;
import com.openmethods.openvxml.desktop.model.workflow.design.Variable;

public class VariableHelper {
	/**
	 * @param name
	 * @param variableType
	 * @param array
	 * @return
	 */
	public static Variable constructVariable(String name,
			IBusinessObjectSet objectSet, FieldType type) {
		Variable v = new Variable(name, type);
		buildObjectFields(v, objectSet);
		return v;
	}

	/**
	 * @param def
	 * @param businessObjectSet
	 */
	public static void buildObjectFields(ObjectDefinition def,
			IBusinessObjectSet businessObjectSet) {
		if (def.getType().isObject()) {
			// lookup business object
			List<IBusinessObject> bos = businessObjectSet.getBusinessObjects();
			for (IBusinessObject ibo : bos) {
				if (ibo.getName().equals(def.getType().getName())) {
					List<IBusinessObjectField> fields = ibo.getFields();
					for (IBusinessObjectField ibof : fields) {
						buildObjectFields(def.addField(new ObjectField(ibof
								.getName(), ibof.getDataType(), false)),
								businessObjectSet);
					}
				}
			}
		} else if (def.getType().getPrimitiveType() == Primitive.ARRAY) {
			def.addField(new ObjectField("numberOfItems", FieldType.NUMBER,
					true));
		} else if (def.getType().getPrimitiveType() == Primitive.STRING) {
			def.addField(new ObjectField("length", FieldType.NUMBER, true));
		}
	}

	public static ObjectDefinition getObjectDefinitionFromVariables(
			List<Variable> vars, String name) {
		ObjectDefinition ret = null;
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
	public static ObjectDefinition getObjectDefinitionFromFields(String name,
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
}
