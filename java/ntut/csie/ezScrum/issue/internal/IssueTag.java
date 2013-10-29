package ntut.csie.ezScrum.issue.internal;

import ntut.csie.ezScrum.issue.core.IIssueTag;
public class IssueTag implements IIssueTag {
	private long tagId;
	private String tagName;
	public IssueTag()
	{
		tagName = "";
	}
	public long getTagId() {
		return tagId;
	}
	public void setTagId(long tagId) {
		this.tagId = tagId;
	}
	public String getTagName() {
		return tagName;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
}
