package org.eclipse.vtp.framework.common;

public class ScriptingException extends RuntimeException {
	private final String title;
	private final String description;

	public ScriptingException(String title, String description, Throwable arg0) {
		super(arg0);
		this.title = title;
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String getMessage() {
		return "Scripting error: " + title + " - " + description;
	}

	public String getTitle() {
		return title;
	}

}
