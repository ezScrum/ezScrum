package ntut.csie.ezScrum.issue.core;

import java.io.Serializable;
import java.util.Date;
 
/**
 * [1]Relationship Delete [2]Relationship Add [3]assign to someone [4] old
 * Status => new Status [5] Add BugNote [6] New Issue
 * 
 * @param issueID
 * @param fieldName
 *            [1,2,5,6] null [3] handler_id [4] status
 * @param oldValue
 *            [1,2] 2:parent of, 3:child of [3] oldone's id [4] value of old         
 *            status [5] note id [6] 0
 * @param newValue
 *            [1,2] targetID [3] someone's id [4] value of new status [5,6] 0
 * @param type
 *            [1] 19 [2] 18 [3,4] 0 [5] 2 [6] 1
 * @param modifyDate
 *            modified time by action
 */
public interface IIssueHistory extends Serializable{
	final public static String HANDLER_FIELD_NAME = "handler_id";
	final public static String STATUS_FIELD_NAME = "status";
	final public static String SUMMARY="summary";
	final public static String EMPTY_FIELD_NAME = null;

	final public String PARENT_OLD_VALUE = "2";
	final public String CHILD_OLD_VALUE = "3";
	final public String ZERO_OLD_VALUE = "0";
	final public String IMPLICATIONOF_VALUE = "8";
	final public String TRANSFORMTO_VALUE = "9";
	final public String TRANSFORMBY_VALUE = "10";
	
	final public String ZERO_NEW_VALUE = "0";
	 
	final public int DESCRIPTION_UPDATE_VALUE = 6;
	final public int NOTES_UPDATE_VALUE = 3;
	
	final public int TASK_DELETE_TYPE = 20;
	final public int RELEATIONSHIP_DELETE_TYPE = 19;
	final public int RELEATIONSHIP_ADD_TYPE = 18;
	final public int BUGNOTE_ADD_TYPE = 2;
	final public int ISSUE_NEW_TYPE = 1;
	final public int OTHER_TYPE = 0;
	
	//儲存用
	final public Date NOW_MODIFY_DATE = null;
	
	public long getHistoryID();
	public void setHistoryID(long historyID);
	public long getIssueID();
	public void setIssueID(long issueID);
	public String getFieldName();
	public void setFieldName(String fieldName);
	public String getOldValue();
	public void setOldValue(String oldValue);
	public String getNewValue();
	public void setNewValue(String newValue);
	public int getType();
	public void setType(int type);
	public long getModifyDate();
	public void setModifyDate(long modifyDate);	
	public String getDescription();
}