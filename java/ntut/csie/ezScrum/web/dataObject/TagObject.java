package ntut.csie.ezScrum.web.dataObject;

import ntut.csie.ezScrum.issue.core.IIssueTag;

public class TagObject {
	private String tagID = "";
	private String tagName = "";

	public TagObject() {}
	
	public TagObject(IIssueTag iIssueTag) {
		this.tagID = Long.toString(iIssueTag.getTagId());
		this.tagName = iIssueTag.getTagName();
	}
	
	public String getTagID() {
		return tagID;
	}
	
	public void setTagID(String tagID) {
		this.tagID = tagID;
	}
	
	public String getTagName() {
		return tagName;
	}
	
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	
	public String toString() {
		String str = "tagID: " + tagID + ", tagName: " + tagName;
		return str;
	}
}
