package ntut.csie.ezScrum.iteration.iternal;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;

import org.jdom.Element;

public class Story extends ScrumIssue implements IStory {
	
	private String estimated = "0";
	private String importance = "0";
	private String howToDemo = "";
	private String notes = "";
	private String sprintId = "-1";
	private String releaseId = "-1";
	private boolean processed = false;
	private String value = "";

	public Story(){}
	
	public Story(IIssue issue){
		try {
			this.setIssue(issue);
		} catch (SQLException e) {
		}
	}		
	
	public String getValue(){
		if(processed){
			return this.value;
		}
		return checkValue(getTagValue(ScrumEnum.VALUE), true);
	}
	
	@Override
	public String getEstimated() {
		if(processed)
			return estimated;
		
		return checkValue(getTagValue(ScrumEnum.ESTIMATION), true);
	}
	
	@Override
	public String getImportance() {
		if(processed)
			return importance;
		
		return checkValue(getTagValue(ScrumEnum.IMPORTANCE), true);
	}
	
	@Override
	public String getHowToDemo() {
		if(processed)
			return howToDemo;
		
		return checkValue(getTagValue(ScrumEnum.HOWTODEMO), false);
	}
	
	@Override
	public String getNotes() {
		if(processed)
			return notes;
		
		return checkValue(getTagValue(ScrumEnum.NOTES), false);
	}
	
	@Override
	public String getSprintID() {
		if(processed)
			return sprintId;
		
		return checkValue(getTagValue(ScrumEnum.SPRINT_ID), true);
	}
	
	@Override
	public String getReleaseID() {
		if(processed)
			return releaseId;
		
		return checkValue(getTagValue(ScrumEnum.RELEASE_TAG), true);
	}
	
	@Override
	public String getValueByType(String type){
		if (type.equals(ScrumEnum.ID_ATTR))
			return Long.toString(this.getIssueID());
		else if (type.equals(ScrumEnum.IMPORTANCE))
			return this.getImportance();
		else if (type.equals(ScrumEnum.VALUE))
			return this.getValue();
		else if (type.equals(ScrumEnum.ESTIMATION))
			return this.getEstimated();
		else if (type.equals(ScrumEnum.HOWTODEMO))
			return this.getHowToDemo();
		else if (type.equals(ScrumEnum.NOTES))
			return this.getNotes();
		else if (type.equals(ScrumEnum.STATUS))
			return this.getStatus();
		else if (type.equals(ScrumEnum.STORYNAME))
			return this.getName();
		else if (type.equals(ScrumEnum.RELEASE_TAG))
			return this.getReleaseID();
		else if (type.equals(ScrumEnum.SPRINT_TAG))
			return this.getSprintID();
		
		return null;
	}
	
	public void setTagContent(Element history) {
		mTagRoot = history;
		generateTagReleationHistory();
	}
	
	private String checkValue(String value, boolean isDigital) {
		if (value == null || value.isEmpty()) {
			if (isDigital) {
				return IStory.DIGITAL_BLANK_VALUE;
			} else {
				return IStory.STRING_BLANK_VALUE;
			}
		}
		
		return value;
	}
	
	private void generateTagReleationHistory() {
		Long e = 0l;
		Long i = 0l;
		Long h = 0l;
		Long n = 0l;
		Long s = 0l;
		Long r = 0l;
		Long v = 0l;
		
		@SuppressWarnings("unchecked")
		List<Element> tags = mTagRoot.getChildren();
		for (Element tag : tags) {
			@SuppressWarnings("unchecked")
			List<Element> childTags = tag.getChildren();
			for (Element childTag : childTags) {
				if(tag.getAttributeValue("id") != null)	{
					Long current = Long.parseLong(tag.getAttributeValue("id"));
					if(e < current && childTag.getName() == ScrumEnum.ESTIMATION) {
						estimated = childTag.getText();
						e = current;
					}
					
					if(i < current && childTag.getName() == ScrumEnum.IMPORTANCE) {
						importance = childTag.getText();
						i = current;
					}
					
					if(h < current && childTag.getName() == ScrumEnum.HOWTODEMO) {
						howToDemo = childTag.getText();
						h = current;
					}
					
					if(n < current && childTag.getName() == ScrumEnum.NOTES) {
						notes = childTag.getText();
						n = current;
					}
					
					if(s <= current && childTag.getName() == ScrumEnum.SPRINT_ID) {
						sprintId = childTag.getText();
						s = current;
					}
					
					if(r < current && childTag.getName() == ScrumEnum.RELEASE_TAG) {
						releaseId = childTag.getText();
						r = current;
					}
					if(v < current && childTag.getName() == ScrumEnum.VALUE) {
						value = childTag.getText();
						v = current;
					}
				}
			}
		}
		processed = true;
	}

	@Override
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public void setStoryId(long storyId) {
		this.setIssueID( storyId );
	}

	@Override
	public long getStoryId() {
		return this.getIssueID();
	}

	@Override
	public void setStoryLink(String storyLink) {
		this.setIssueLink(storyLink);
	}

	@Override
	public String getStoryLink() {
		return this.getIssueLink();
	}

	@Override
	public void setSprintId(String sprintId) {
		this.sprintId = sprintId;
	}

	@Override
	public void setReleaseId(String releaseId) {
		this.releaseId = releaseId;
	}

	@Override
	public void setImportance(String importance) {
		this.importance = importance;
	}

	@Override
	public void setEstimated(String estimated) {
		this.estimated = estimated;
	}

	@Override
	public void setHowToDemo(String howToDemo) {
		this.howToDemo = howToDemo;
	}

	@Override
	public void setNotes(String notes) {
		this.notes = notes;
	}

	@Override
	public void setName(String name) {
		this.setSummary(name);
	}
}
