package org.eclipse.vtp.desktop.export.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.vtp.desktop.export.IProjectExporter;
import org.eclipse.vtp.framework.util.ConfigurationDictionary;

public abstract class ProjectExporter implements IProjectExporter,
		Comparable<ProjectExporter> {
	private boolean dirty = false;
	private final Map<String, String> settings = new HashMap<String, String>();

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public void loadSettings(Exporter exporter) {
		settings.putAll(exporter.loadSettings(getProject().getName()));
	}

	public void saveSettings(Exporter exporter) {
		exporter.saveSettings(getProject().getName(), settings);
	}

	@Override
	public String getSetting(String key) {
		return settings.get(key);
	}

	@Override
	public String getSetting(String key, String defaultValue) {
		String value = settings.get(key);
		if (value == null) {
			return defaultValue;
		}
		return value;
	}

	@Override
	public void putSetting(String key, String value) {
		settings.put(key, value);
	}

	@Override
	public void clearSetting(String key) {
		settings.remove(key);
	}

	public Collection<ConfigurationDictionary> getConfigurationDictionaries(
			String uniqueToken) {
		return Collections.emptyList();
	}

	@Override
	public int compareTo(ProjectExporter o) {
		return getProject().getName().compareTo(o.getProject().getName());
	}

	@Override
	public String toString() {
		return getProject().getName();
	}

}
