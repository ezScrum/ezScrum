package ntut.csie.ezScrum.issue.sql.service.internal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.IIssueHistory;
import ntut.csie.ezScrum.issue.internal.IssueHistory;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.tool.ISQLControl;
import ntut.csie.ezScrum.iteration.core.RelationEnum;
import ntut.csie.jcis.core.util.DateUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MantisHistoryService extends AbstractMantisService {
	private static Log log = LogFactory.getLog(MantisHistoryService.class);
	
	public MantisHistoryService(ISQLControl control, Configuration config) {
		setControl(control);
		setConfig(config);
	}

	public void addMantisActionHistory(long issueID, String fieldName,
			String oldValue, String newValue, int type, Date modifyDate) {
		int userID = getUserID(getConfig().getAccount());
		if (userID == -1)
			return;

		IQueryValueSet valueSet = new MySQLQuerySet();

		valueSet.addTableName("mantis_bug_history_table");

		valueSet.addInsertValue("user_id", Integer.toString(userID));
		valueSet.addInsertValue("bug_id", Long.toString(issueID));
		
		if (modifyDate == null) {
			valueSet.addInsertValue("date_modified", new Timestamp(new Date().getTime()).toString());
		} else {
			valueSet.addInsertValue("date_modified", DateUtil.format(modifyDate, DateUtil._16DIGIT_DATE_TIME_MYSQL));
		}
		
		valueSet.addInsertValue("field_name", fieldName);
		valueSet.addInsertValue("old_value", oldValue);
		valueSet.addInsertValue("new_value", newValue);
		valueSet.addInsertValue("type", Integer.toString(type));
		
		String query = valueSet.getInsertQuery();
		log.info("[SQL] " + query);
		getControl().execute(query);
	}

	public void addMantisActionHistory(long issueID, String fieldName,
			int oldValue, long newValue, int type, Date modifyDate) {
		this.addMantisActionHistory(issueID, fieldName, Integer
				.toString(oldValue), Long.toString(newValue), type, modifyDate);
	}

	public void updateHistoryModifiedDate(long historyID, Date date) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("mantis_bug_history_table");
		valueSet.addInsertValue("date_modified", DateUtil.format(date, DateUtil._16DIGIT_DATE_TIME_MYSQL));
		valueSet.addEqualCondition("id", Long.toString(historyID));
		String query = valueSet.getUpdateQuery();
		log.info("[SQL] " + query);
		getControl().execute(query);
	}

	public List<IIssueHistory> getIssueHistory(IIssue issue) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("mantis_bug_history_table");
		valueSet.addEqualCondition("bug_id", Long.toString(issue.getIssueID()));
		valueSet.setOrderBy("date_modified", IQueryValueSet.ASC_ORDER);
		String query = valueSet.getSelectQuery();
		log.info("[SQL] " + query);
		List<IIssueHistory> list = new ArrayList<IIssueHistory>();
		ResultSet result = getControl().executeQuery(query);
		try {
			while (result.next()) {
				try {
					IIssueHistory history = new IssueHistory();
					history.setIssueID(issue.getIssueID());
					history.setHistoryID(result.getLong("id"));
					history.setModifyDate(result.getTimestamp("date_modified")
							.getTime());
					String fieldName = result.getString("field_name");
					int type = result.getInt("type");
					if(fieldName ==null|| fieldName.equals("null")){
						if(type == RelationEnum.ISSUE_NEW_TYPE)
							history.setFieldName("New Issue");
						else if(type == RelationEnum.DESCRIPTION_UPDATE_VALUE)
							history.setFieldName("Description Updated");
						else if(type == RelationEnum.NOTES_UPDATE_VALUE)
							history.setFieldName("Note Edited");
					}
					else
						history.setFieldName(fieldName);
					history.setOldValue(result.getString("old_value"));
					history.setNewValue(result.getString("new_value"));
					history.setType(type);
					list.add(history);
				} catch (Exception e) {
					// 多一層try只是為了防止取得型態錯誤的值
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public void initHistory(IIssue issue) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("mantis_bug_history_table");
		valueSet.addEqualCondition("bug_id", Long.toString(issue.getIssueID()));
		valueSet.setOrderBy("date_modified", IQueryValueSet.ASC_ORDER);
		String query = valueSet.getSelectQuery();
		log.info("[SQL] " + query);
		ResultSet result = getControl().executeQuery(query);
		try {
			while (result.next()) {
				try {
					IssueHistory history = new IssueHistory();
					history.setIssueID(issue.getIssueID());
					history.setHistoryID(result.getLong("id"));
					history.setModifyDate(result.getTimestamp("date_modified")
							.getTime());
					String fieldName = result.getString("field_name");
					int type = result.getInt("type");
					if(fieldName == null|| fieldName.equals("null")){
						if(type == RelationEnum.ISSUE_NEW_TYPE)
							history.setFieldName("New Issue");
						else if(type == RelationEnum.DESCRIPTION_UPDATE_VALUE)
							history.setFieldName("Description Updated");
						else if(type == RelationEnum.NOTES_UPDATE_VALUE)
							history.setFieldName("Note Edited");
						else if(type == RelationEnum.RELEATIONSHIP_ADD_TYPE)
							history.setFieldName("Relationship added ");
						else if(type == RelationEnum.RELEATIONSHIP_DELETE_TYPE)
							history.setFieldName("Relationship deleted ");
						else{
							/*讓沒有對應到的fieldName有個預設的對應值
							 * 否則沒有set fieldName 就是null之後其他程式抓出時要是沒有
							 * 判斷是否為null就直接拿來比對其他字串就會丟出例外而且很難debug
							 * by ninja 20101008
							 */
							history.setFieldName("");
						}
					}
					else
						history.setFieldName(fieldName);
	
					history.setOldValue(result.getString("old_value"));
					history.setNewValue(result.getString("new_value"));
					history.setType(type);
					issue.addIssueHistory(history);
				} catch (Exception e) {
					// 多一層try只是為了防止取得型態錯誤的值
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	//刪除 issue的history
	public void removeHistory(String id) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("mantis_bug_history_table");
		valueSet.addEqualCondition("bug_id", id);
		String query = valueSet.getDeleteQuery();
		log.info("[SQL] " + query);
		getControl().execute(query);		
	}
}