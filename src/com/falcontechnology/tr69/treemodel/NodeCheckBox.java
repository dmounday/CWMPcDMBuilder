package com.falcontechnology.tr69.treemodel;

public class NodeCheckBox {
	private String text;
	private String fullPath;

	private boolean selected;
	private boolean profileItem;

	public NodeCheckBox(String text, boolean selected) {
		this.text = text;
		this.selected = selected;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean newValue) {
		selected = newValue;
	}

	public String getText() {
		return text;
	}

	public void setText(String newValue) {
		text = newValue;
	}

	public String getFullPath() {
		return fullPath;
	}

	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}

	public boolean isProfileItem() {
		return profileItem;
	}

	public void setProfileItem(boolean profileItem) {
		this.profileItem = profileItem;
	}

	public String toString() {
		return getClass().getName() + "[" + text + "/" + selected + "]";
	}
}
