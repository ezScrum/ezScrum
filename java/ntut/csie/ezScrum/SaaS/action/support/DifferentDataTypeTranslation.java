package ntut.csie.ezScrum.SaaS.action.support;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.SaaS.database.StoryDataStore;
import ntut.csie.ezScrum.SaaS.database.TagDataStore;
import ntut.csie.ezScrum.SaaS.database.TaskDataStore;
import ntut.csie.ezScrum.issue.core.IIssueTag;
import ntut.csie.ezScrum.issue.internal.IssueHistory;
import ntut.csie.ezScrum.issue.internal.IssueTag;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ITask;
import ntut.csie.ezScrum.iteration.core.RelationEnum;
import ntut.csie.ezScrum.web.support.ParseXMLString;
import ntut.csie.jcis.core.util.DateUtil;

public class DifferentDataTypeTranslation {
	
	/**
	 * 轉換Tag Data Store to IIssueTag
	 * 1. Tag Data Store:GAE
	 * 2. IIssueTag:Web前端輸出
	 * @param tagDS
	 * @return
	 */
	public static IIssueTag tranTag(TagDataStore tagDS ){
		IIssueTag issueTag = new IssueTag();
		issueTag.setTagId(Long.valueOf(tagDS.getTagId()));
		issueTag.setTagName(tagDS.getTagName());
		return issueTag;
	}
	
	
	public static void tranStoryHistory(IStory story, StoryDataStore storyDS) {
		List<String> histories = storyDS.getHistorylist();
		for (String DBhistory : histories) {
			try {
				if (!DBhistory.isEmpty()) {
					ParseXMLString StringXML = new ParseXMLString(DBhistory);
					
					IssueHistory history = new IssueHistory();
					history.setIssueID(story.getStoryId());
					history.setHistoryID(Integer.valueOf(StringXML.getCharacterDataFromElement("id")));
					Date modifyDate = DateUtil.parse(StringXML.getCharacterDataFromElement("date_modified"), DateUtil._16DIGIT_DATE_TIME_MYSQL);
					history.setModifyDate(modifyDate.getTime());
					String fieldName = StringXML.getCharacterDataFromElement("field_name");
					int type = Integer.valueOf(StringXML.getCharacterDataFromElement("type"));
					if (fieldName == null || fieldName.equals("null")) {
						if (type == RelationEnum.ISSUE_NEW_TYPE)
							history.setFieldName("New Story");
						else if (type == RelationEnum.DESCRIPTION_UPDATE_VALUE)
							history.setFieldName("Description Updated");
						else if (type == RelationEnum.NOTES_UPDATE_VALUE)
							history.setFieldName("Note Edited");
						else if (type == RelationEnum.RELEATIONSHIP_ADD_TYPE)
							history.setFieldName("Relationship added ");
						else if (type == RelationEnum.RELEATIONSHIP_DELETE_TYPE)
							history.setFieldName("Relationship deleted ");
						else {
							/*
							 * 讓沒有對應到的fieldName有個預設的對應值 否則沒有set fieldName
							 * 就是null之後其他程式抓出時要是沒有
							 * 判斷是否為null就直接拿來比對其他字串就會丟出例外而且很難debug by ninja
							 * 20101008
							 */
							history.setFieldName("");
						}
					} else
						history.setFieldName(fieldName);

					history.setOldValue(StringXML.getCharacterDataFromElement("old_value"));
					history.setNewValue(StringXML.getCharacterDataFromElement("new_value"));
					history.setType(type);
					story.addIssueHistory(history);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void tranTaskHistory(ITask task, TaskDataStore taskDS) {
		List<String> histories = taskDS.getHistorylist();
		for (String DBhistory : histories) {
			try {
				if (!DBhistory.isEmpty()) {
					ParseXMLString StringXML = new ParseXMLString(DBhistory);
					
					IssueHistory history = new IssueHistory();
					history.setIssueID(task.getIssueID());
					history.setHistoryID(Integer.valueOf(StringXML.getCharacterDataFromElement("id")));
					Date modifyDate = DateUtil.parse(StringXML.getCharacterDataFromElement("date_modified"), DateUtil._16DIGIT_DATE_TIME_MYSQL);
					history.setModifyDate(modifyDate.getTime());
					String fieldName = StringXML.getCharacterDataFromElement("field_name");
					int type = Integer.valueOf(StringXML.getCharacterDataFromElement("type"));
					if (fieldName == null || fieldName.equals("null")) {
						if (type == RelationEnum.ISSUE_NEW_TYPE)
							history.setFieldName("New Task");
						else if (type == RelationEnum.DESCRIPTION_UPDATE_VALUE)
							history.setFieldName("Description Updated");
						else if (type == RelationEnum.NOTES_UPDATE_VALUE)
							history.setFieldName("Note Edited");
						else if (type == RelationEnum.RELEATIONSHIP_ADD_TYPE)
							history.setFieldName("Relationship added ");
						else if (type == RelationEnum.RELEATIONSHIP_DELETE_TYPE)
							history.setFieldName("Relationship deleted ");
						else {
							/*
							 * 讓沒有對應到的fieldName有個預設的對應值 否則沒有set fieldName
							 * 就是null之後其他程式抓出時要是沒有
							 * 判斷是否為null就直接拿來比對其他字串就會丟出例外而且很難debug by ninja
							 * 20101008
							 */
							history.setFieldName("");
						}
					} else
						history.setFieldName(fieldName);

					history.setOldValue(StringXML.getCharacterDataFromElement("old_value"));
					history.setNewValue(StringXML.getCharacterDataFromElement("new_value"));
					history.setType(type);
					task.addIssueHistory(history);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String tranHistoryXML(String fieldName, String oldValue, String newValue, int type, Date modifyDate){
		/// <History>
		// <user_id></user_id>
		// <date_modified></date_modified>
		// <field_name></field_name>
		// <old_value></old_value>
		// <new_value></new_value>
		// <type></type>
		// </History>
		StringBuilder sb = new StringBuilder();
		sb.append("<Histories><HistoryList><History");
		sb.append(" id=\"" + "0" + "\"");
		if (modifyDate == null) {
			sb.append(" date_modified=\"" + new Timestamp(new Date().getTime()).toString() + "\"");
		} else {
			sb.append(" date_modified=\"" + DateUtil.format(modifyDate, DateUtil._16DIGIT_DATE_TIME_MYSQL) + "\"");
		}
		sb.append(" field_name=\"" + fieldName + "\"");
		sb.append(" old_value=\"" + oldValue.replace("\"", "\\\"") + "\"");
		sb.append(" new_value=\"" + newValue.replace("\"", "\\\"") + "\"");
		sb.append(" type=\"" + type + "\"/>");
		sb.append("</HistoryList>");	
		sb.append("</Histories>");
		return sb.toString();
	}
}
