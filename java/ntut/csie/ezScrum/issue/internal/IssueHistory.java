package ntut.csie.ezScrum.issue.internal;

import java.util.Date;

import ntut.csie.ezScrum.issue.core.IIssueHistory;
import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.iteration.core.RelationEnum;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;

public class IssueHistory implements IIssueHistory {

	private String m_fieldName;
	private long m_issueID;
	private long m_modifyDate;
	private String m_newValue;
	private String m_oldValue;
	private int m_type;
	private long m_id;
	public  Date getModifyDateDate()
	{
		return new Date(m_modifyDate);
	}
	@Override
	public long getHistoryID() {
		return m_id;
	}
	
	@Override
	public void setHistoryID(long historyID) {
		m_id = historyID;
	}	
	
	@Override
	public String getFieldName() {
		return m_fieldName;
	}

	@Override
	public long getIssueID() {
		return m_issueID;
	}

	@Override
	public long getModifyDate() {
		return m_modifyDate;
	}

	@Override
	public String getNewValue() {
		return m_newValue;
	}

	@Override
	public String getOldValue() {
		return m_oldValue;
	}

	@Override
	public int getType() {
		return m_type;
	}

	@Override
	public void setFieldName(String fieldName) {
		this.m_fieldName = fieldName;
	}

	@Override
	public void setIssueID(long issueID) {
		this.m_issueID = issueID;
	}

	@Override
	public void setModifyDate(long modifyDate) {
		this.m_modifyDate = modifyDate;
	}

	@Override
	public void setNewValue(String newValue) {
		this.m_newValue = newValue;
	}

	@Override
	public void setOldValue(String oldValue) {
		this.m_oldValue = oldValue;
	}

	@Override
	public void setType(int type) {
		this.m_type = type;
	}

	@Override
	public String getDescription() {
		StringBuffer sb = new StringBuffer();
//		if (getType()==ISSUE_NEW_TYPE)
//			sb.append("New(Issue)");
//		else if (getType()==BUGNOTE_ADD_TYPE)
//			sb.append("Note Added:"+getOldValue());
		if(getType()==ISSUE_NEW_TYPE)
			sb.append(getFieldName());
		else if(getType()==DESCRIPTION_UPDATE_VALUE)
			sb.append(getFieldName());
		else if(getType()==NOTES_UPDATE_VALUE){
			sb.append(getFieldName());
		}
		else if (getType()==RELEATIONSHIP_ADD_TYPE){
			if (getOldValue().equals(PARENT_OLD_VALUE))
				sb.append("Add Task " + getNewValue());
			else if (getOldValue().equals(CHILD_OLD_VALUE))
				sb.append("Append to Story " + getNewValue());
			else if (getOldValue().equals(RelationEnum.IMPLICATIONOF_OLD_VALUE))
				sb.append("Implication of " + getNewValue());
			else if (getOldValue().equals(RelationEnum.TRANSFORMTO_OLD_VALUE))
				sb.append("Transform to " + getNewValue());
			else if (getOldValue().equals(RelationEnum.TRANSFORMBY_OLD_VALUE))
				sb.append("Transform by " + getNewValue());
		}
		else if (getType()==RELEATIONSHIP_DELETE_TYPE){
			if (getOldValue().equals(PARENT_OLD_VALUE))
				sb.append("Drop Task " + getNewValue());
			else if (getOldValue().equals(CHILD_OLD_VALUE))
				sb.append("Remove from Story " + getNewValue());
		}
		else if (getType()==TASK_DELETE_TYPE){
			sb.append("Delete Task " + getNewValue());
		}else if (getFieldName().equals(HANDLER_FIELD_NAME)){
			//TODO:未完成,因不好處,且暫時還不需要
		}else if (getFieldName().equals(STATUS_FIELD_NAME)){
			sb.append("Status : \""+getTaskBoardStatus(getOldValue())+"\" => \""+ getTaskBoardStatus(getNewValue())+"\"");			
		}else if (getFieldName().equals(ScrumEnum.SPRINT_ID)){
			sb.append("Sprint : "+getOldValue()+" => "+ getNewValue());			
		}else if (getFieldName().equals(ScrumEnum.ESTIMATION)){
			sb.append("Estimation : "+getOldValue()+" => "+ getNewValue());			
		}else if (getFieldName().equals(ScrumEnum.IMPORTANCE)){
			sb.append("Importance : "+getOldValue()+" => "+ getNewValue());			
		}else if (getFieldName().equals(ScrumEnum.ACTUALHOUR)){
			sb.append("ActualHour : "+getOldValue()+" => "+ getNewValue());		
		}else{
			sb.append(getFieldName()+" : "+getOldValue()+" => "+ getNewValue());
		}
		return sb.toString();
	}	
	
	private String getTaskBoardStatus(String status){
		if (Integer.parseInt(status) < ITSEnum.ASSIGNED_STATUS)
			return "Not Checked Out";
		if (Integer.parseInt(status) >= ITSEnum.ASSIGNED_STATUS && Integer.parseInt(status) < ITSEnum.CLOSED_STATUS)
			return "Checked Out";
		else
			return "Done";
	}
}
