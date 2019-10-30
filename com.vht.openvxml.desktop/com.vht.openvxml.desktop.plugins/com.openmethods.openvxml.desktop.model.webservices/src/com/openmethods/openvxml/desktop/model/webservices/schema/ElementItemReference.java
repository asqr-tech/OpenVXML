package com.openmethods.openvxml.desktop.model.webservices.schema;

public class ElementItemReference extends ElementItem {
	private ElementItem referencedElementItem = null;

	public ElementItemReference(Schema owner) {
		super(owner, null);
	}

	public void setReferencedElementItem(ElementItem referencedElementItem) {
		this.referencedElementItem = referencedElementItem;
	}

	@Override
	public String getDefaultValue() {
		return referencedElementItem.getDefaultValue();
	}

	@Override
	public String getFixedValue() {
		return referencedElementItem.getFixedValue();
	}

	@Override
	public String getName() {
		return referencedElementItem.getName();
	}

	@Override
	public Type getType() {
		return referencedElementItem.getType();
	}

	@Override
	public boolean hasQualifyOverride() {
		return referencedElementItem.hasQualifyOverride();
	}

	@Override
	public boolean isQualified() {
		return referencedElementItem.isQualified();
	}

	@Override
	public void setDefaultValue(String defaultValue) {
		throw new UnsupportedOperationException(
				"Cannot modify a referenced element directly.");
	}

	@Override
	public void setFixedValue(String fixedValue) {
		throw new UnsupportedOperationException(
				"Cannot modify a referenced element directly.");
	}

	@Override
	public void setQualified(boolean qualified) {
		throw new UnsupportedOperationException(
				"Cannot modify a referenced element directly.");
	}

	@Override
	public void setQualifyOverride(boolean qualifyOverride) {
		throw new UnsupportedOperationException(
				"Cannot modify a referenced element directly.");
	}

	@Override
	public void setType(Type type) {
		throw new UnsupportedOperationException(
				"Cannot modify a referenced element directly.");
	}

}
