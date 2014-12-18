package ntut.csie.ezScrum.iteration.iternal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssueHistory;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ITask;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;

public class Task extends ScrumIssue implements ITask {
	private String estimation;
	private String notes;
	private String partner;
	private String storyID;
	private String partners;
	
	@Override
	public String getEstimated() {
		String value = this.checkValue(getTagValue(ScrumEnum.ESTIMATION), true);
		return value;
	}
			
	@Override
	public String getNotes() {
		String value = this.checkValue(getTagValue(ScrumEnum.NOTES), false);
		return value;
	}
	
	@Override
	public String getPartners() {
		String value = this.checkValue(getTagValue(ScrumEnum.PARTNERS), false);
		return value;
	}

	@Override
	public String getSpecificTime() {
		String value = this.checkValue(getTagValue(ScrumEnum.SPECIFICTIME), false);
		return value;
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

	@Override
	public long getStoryID() {
		if(getParentId() != 0)
			return getParentId();
		return -1;
	}

	@Override
	public void setEstimated(String estimation) {
		this.estimation = estimation;
	}

	@Override
	public void setNotes(String notes) {
		this.notes = notes;
	}

	@Override
	public void setPartners(String partners) {
		this.partners = partners;
	}

	@Override
	public void setStoryID(String storyID) {
		this.storyID = storyID;
	}
}
