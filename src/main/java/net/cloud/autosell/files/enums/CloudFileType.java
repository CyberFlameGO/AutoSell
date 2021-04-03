package net.cloud.autosell.files.enums;

public enum CloudFileType {

	SETTINGS("settings"),
	MESSAGES("messages"),
	PRICES("prices");

	String name;
	
	CloudFileType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

}
