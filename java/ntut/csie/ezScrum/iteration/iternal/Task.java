package ntut.csie.ezScrum.iteration.iternal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssueHistory;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ITask;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;

public class Task extends ScrumIssue implements ITask {
	private String estimation;
	private String notes;
	private String partner;
	private String storyID;
	private String partners;
	
	@Override
	public String getEstimated() {
//		String value = getTagValue(ScrumEnum.ESTIMATION);
//		if (value == null)
//			return ScrumEnum.DIGITAL_BLANK_VALUE;
		String value = this.checkValue(getTagValue(ScrumEnum.ESTIMATION), true);
		return value;
	}
			
	
	@Override
	public String getNotes() {
//		String value = getTagValue(ScrumEnum.NOTES);
//		if (value == null)
//			return ScrumEnum.STRING_BLANK_VALUE;
		String value = this.checkValue(getTagValue(ScrumEnum.NOTES), false);
		return value;
	}
	
	@Override
	public String getPartners() {
//		String value = getTagValue(ScrumEnum.PARTNERS);
//		if (value == null)
//			return ScrumEnum.STRING_BLANK_VALUE;
		String value = this.checkValue(getTagValue(ScrumEnum.PARTNERS), false);
		return value;
	}

	@Override
	public String getSpecificTime() {
//		String value = getTagValue(ScrumEnum.SPECIFICTIME);
//		if (value == null)
//			return ScrumEnum.STRING_BLANK_VALUE;
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
	public List<Long> getParentsID() {
		// return m_parnets;
		return this.getParentsID(new Date());
	}
	
	@Override
	public List<Long> getParentsID(Date date) {
		ArrayList<Long> list = new ArrayList<Long>();
		for (IIssueHistory history : getHistory()) {
			if (history.getModifyDate() > date.getTime())
				break;
			if (history.getType() == IIssueHistory.RELEATIONSHIP_ADD_TYPE
					&& history.getOldValue().equals(IIssueHistory.CHILD_OLD_VALUE))
				list.add(Long.parseLong(history.getNewValue()));
			else if (history.getType() == IIssueHistory.RELEATIONSHIP_DELETE_TYPE
					&& history.getOldValue().equals(IIssueHistory.CHILD_OLD_VALUE)) {
				list.remove(Long.parseLong(history.getNewValue()));
			}
		}
		return list;
	}


	@Override
	public long getStoryID() {
//		if(this.getParentsID()!=null)
//		return this.getParentsID().get(0);
//		return -1;
		if(this.getParentsID()!=null)
		return this.getParentsID().get(0);
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
