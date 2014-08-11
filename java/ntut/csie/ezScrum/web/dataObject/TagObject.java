package ntut.csie.ezScrum.web.dataObject;

import ntut.csie.ezScrum.issue.core.IIssueTag;

public class TagObject {
	private long id, projectId;
	
	private String tagName = "";
	

	public TagObject() {}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public long getProjectId() {
		return projectId;
	}
	
	public void setProjectId(long id) {
		this.projectId = id;
	}
	
	public String getName() {
		return tagName;
	}
	
	public void setName(String tagName) {
		this.tagName = tagName;
	}
	
	public String toString() {
		String str = "tagID: " + id + ", tagName: " + tagName;
		return str;
	}
}
