package ntut.csie.ezScrum.issue.sql.service.internal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.issue.internal.Issue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.tool.ISQLControl;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.iteration.iternal.Story;
import ntut.csie.ezScrum.iteration.support.TranslateSpecialChar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MantisIssueService extends AbstractMantisService {

	private static Log log = LogFactory.getLog(MantisIssueService.class);
	
	final private String HTTP_SERVICE = "http://";
	final private String MANTIS_VIEW_LINK = "/ezScrum/showIssueInformation.do?";
	final private String ATTRIBURE_ISSUEID = "issueID=";
	
	// Table Name
	final private String STORY_RELATION_TABLE = "ezscrum_story_relation";

//	private IIssue[] m_issues;
//	private String m_currentProjectName = "";

	public MantisIssueService(ISQLControl control, Configuration config) {
		setControl(control);
		setConfig(config);
	}

	/************************************************************
	 * 建立一個新Issue
	 *************************************************************/
	public long newIssue(IIssue issue) {

		IQueryValueSet valueSet = new MySQLQuerySet();
		TranslateSpecialChar translateChar = new TranslateSpecialChar();

		/*
		 * 先在資料庫之中增加一筆Bug Text，之後在增加Issue的時候才有值可以給Issue建立關聯
		 */
		valueSet.addTableName("mantis_bug_text_table");
		valueSet.addInsertValue("description", issue.getDescription());
		valueSet.addInsertValue("additional_information", issue.getAdditional());
		valueSet.addInsertValue("steps_to_reproduce", "");
		String query = valueSet.getInsertQuery();
		getControl().execute(query, true);

		// 取得SQL自動回傳的Key值
		String[] keys = getControl().getKeys();
		int bug_text_id = Integer.parseInt(keys[0]);

		/********** 建立Issue本體 *******************************************/
		valueSet.clear();
		valueSet.addTableName("mantis_bug_table");
		valueSet.addInsertValue("project_id", Integer.toString(getProjectID(issue.getProjectID())));
		valueSet.addInsertValue("reporter_id", Integer
				.toString(getUserID(getConfig().getAccount())));
		valueSet.addInsertValue("category", issue.getCategory());

		// 取得現在時間
		Timestamp now = new Timestamp(new Date().getTime());

		valueSet.addInsertValue("date_submitted", now.toString());
		valueSet.addInsertValue("last_updated", now.toString());
		// 將此Issue其關聯的bug_text_id輸入儲存
		valueSet.addInsertValue("bug_text_id", Long.toString(bug_text_id));
		if (!issue.getSeverity().isEmpty())
			valueSet.addInsertValue("severity", ITSEnum.getSeverity(issue
					.getSeverity())
					+ "");
		if (!issue.getReproducibility().isEmpty())
			valueSet.addInsertValue("reproducibility", ITSEnum
					.getReproducibility(issue.getReproducibility())
					+ "");
		if (!issue.getPriority().isEmpty())
			valueSet.addInsertValue("priority", ITSEnum.getPriority(issue
					.getPriority())
					+ "");
		issue.setSummary(translateChar.TranslateDBChar(issue.getSummary()));
		valueSet.addInsertValue("summary", issue.getSummary());

		query = valueSet.getInsertQuery();
		getControl().execute(query, true);
		long newIssueID = Integer.parseInt(getControl().getKeys()[0]);
		/********* 新Issue建立完畢 ******************************************/

		// 如果此Issue不是Task的話，就在Story Relation的Table建立一筆關聯資料
		if (!issue.getCategory().equals(ScrumEnum.TASK_ISSUE_TYPE)) {
			valueSet.clear();
			valueSet.addTableName("ezscrum_story_relation");

			valueSet.addInsertValue("storyID", Long.toString(newIssueID));
			valueSet.addInsertValue("projectID", Integer.toString(this
					.getProjectID(issue.getProjectID())));

			query = valueSet.getInsertQuery();
			getControl().execute(query);
		}

		// 回傳剛新增bug的id
		return newIssueID;
	}

	/************************************************************
	 * 依照Issue ID取出Issue
	 *************************************************************/
	public IIssue getIssue(long issueID) {
		IIssue issue = null;

		IQueryValueSet valueSet = new MySQLQuerySet();
		// 訂定查詢table名稱
		valueSet.addTableName("mantis_bug_table");
		valueSet.addTableName("mantis_bug_text_table");
		// 訂定查詢的條件
		valueSet.addFieldEqualCondition("mantis_bug_table.bug_text_id",
				"mantis_bug_text_table.id");
		valueSet.addFieldEqualCondition("mantis_bug_table.id", Long
				.toString(issueID));
		// 產生查詢的query
		String query = valueSet.getSelectQuery();
		try {
			ResultSet result = getControl().executeQuery(query);
			if (result.next()) {
				issue = new Issue();
				issue.setCategory(result.getString("category"));
				issue.setReproducibility(ITSEnum.getReproducibility(result
						.getInt("reproducibility")));
				issue.setSeverity(ITSEnum
						.getSeverity(result.getInt("severity")));
				issue.setPriority(ITSEnum
						.getPriority(result.getInt("priority")));
				issue.setSummary(result.getString("summary"));
				issue.setViewState(ITSEnum.getViewStatus(result
						.getInt("view_state")));
				int handlerID = result.getInt("handler_id");
				if (handlerID > 0)
					issue.setAssignto(getUserName(handlerID));
				issue.setIssueID(result.getLong("id"));
				issue.setIssueLink(MANTIS_VIEW_LINK + ATTRIBURE_ISSUEID + issue.getIssueID());
				issue.setStatus(ITSEnum.getStatus(result.getInt("status")));
				issue.setSubmittedDate(result.getTimestamp("date_submitted")
						.getTime());
				String project_id = result.getString("project_id");
				if(project_id!=null && !project_id.equals("")){
					issue.setProjectID(getProjectName(Integer.parseInt(project_id)));
					issue.setProjectName(getProjectName(Integer.parseInt(project_id)));
				}

				issue.setReporter(getUserName(result.getInt("reporter_id")));
				// 工作開始日期等同submit的日期
				issue.setWorkingUpdated(result.getTimestamp("date_submitted")
						.getTime());
				issue.setDescription(result.getString("description"));
				issue.setAdditional(result.getString("additional_information"));

			}

			return issue;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<IIssue> getProjectIssues(String projectName) {
		ArrayList<IIssue> issues = new ArrayList<IIssue>();

		int projectID = getProjectID(projectName);

		IQueryValueSet valueSet = new MySQLQuerySet();
		// 訂定查詢table名稱
		valueSet.addTableName("mantis_bug_table");
		valueSet.addTableName("mantis_bug_text_table");
		// 訂定查詢的條件
		valueSet.addFieldEqualCondition("mantis_bug_table.bug_text_id",
				"mantis_bug_text_table.id");
		valueSet.addEqualCondition("project_id", Integer.toString(projectID));
		// 產生查詢的query
		String query = valueSet.getSelectQuery();

		try {
			ResultSet result = getControl().executeQuery(query);
			while (result.next()) {
				IIssue issue = new Issue();
				issue.setCategory(result.getString("category"));
				issue.setReproducibility(ITSEnum.getReproducibility(result
						.getInt("reproducibility")));
				issue.setSeverity(ITSEnum
						.getSeverity(result.getInt("severity")));
				issue.setPriority(ITSEnum
						.getPriority(result.getInt("priority")));
				issue.setSummary(result.getString("summary"));
				issue.setViewState(ITSEnum.getViewStatus(result
						.getInt("view_state")));
				int handlerID = result.getInt("handler_id");
				if (handlerID > 0)
					issue.setAssignto(getUserName(handlerID));
				issue.setIssueID(result.getLong("id"));
				issue.setIssueLink(MANTIS_VIEW_LINK + ATTRIBURE_ISSUEID + issue.getIssueID());
				issue.setStatus(ITSEnum.getStatus(result.getInt("status")));
				issue.setSubmittedDate(result.getTimestamp("date_submitted")
						.getTime());

				// 工作開始日期等同submit的日期
				issue.setWorkingUpdated(result.getTimestamp("date_submitted")
						.getTime());
				issue.setDescription(result
						.getString("mantis_bug_text_table.description"));
				issue.setAdditional(result.getString("additional_information"));
				issue.setProjectID(projectName);
				issue.setProjectName(projectName);
				// 加入列表
				issues.add(issue);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return issues;
	}

/************************************************************
	 * 讓getIssues的動作可以用Sprint ID與Release ID來作篩選 Add by OPH 2009.12
	 * explain SELECT * FROM `mantis_bug_table` 
	 * join mantis_bug_text_table on mantis_bug_table.bug_text_id = mantis_bug_text_table.id 
	 * join ezscrum_story_relation on ezscrum_story_relation.storyID = mantis_bug_table.id 
	 * join (SELECT storyID,max(updateDate) as max_date FROM `ezscrum_story_relation` 
	 * WHERE 
	 * `projectID` = 3 
	 * AND sprintID is not NULL 
	 * AND `updateDate` < '2009-05-28 23:59:59.999' 
	 * group by storyID) sub_Select on 
	 * sub_Select.storyID = ezscrum_story_relation.storyID 
	 * AND ezscrum_story_relation.updateDate = sub_Select.max_date 
	 * WHERE `category` = 'Story' 
	 * and sprintID = 21
	 * 
	 *************************************************************/
	public IIssue[] getIssues(String projectName, String category,
			String releaseID, String sprintID, Date date) {
		return getIssues(projectName, category, releaseID, sprintID, null, date);
	}

	public IIssue[] getIssues(String projectName, String category,
			String releaseID, String sprintID, Date startDate, Date endDate) {
		IIssue[] resultIssues = null;

		IQueryValueSet valueSet = new MySQLQuerySet();
		// 將 Table串連起來
		valueSet.addTableName("mantis_bug_table");
		valueSet.joinTableName("mantis_bug_text_table", "mantis_bug_table.bug_text_id = mantis_bug_text_table.id");
		valueSet.joinTableName("ezscrum_story_relation", "ezscrum_story_relation.storyID = mantis_bug_table.id");

		// 設定搜尋條件，有關SprintID與ReleaseID與StoryID的搜尋全部在subSelect中進行
		IQueryValueSet subValueSet = new MySQLQuerySet();
		subValueSet.addResultRow("storyID");
		subValueSet.addResultRow("max(updateDate) as max_date");
		subValueSet.addResultRow("max(id) as max_id");
		subValueSet.addTableName("ezscrum_story_relation");

		if (releaseID != null) {
			subValueSet.addEqualCondition("releaseID", releaseID);
		}
		
		if (sprintID != null) {
			if (sprintID.equals("*")) {
				// Drop Story 原本只判斷 sprintID 為 0 則視為"可能是"被 Drop 掉的 Story
				// 但是，此 Story 如果被加入其他 Sprint 時，則此條件無法正確查詢出
				// 所以，此 case 只是為了 Drop Story 的查詢而修改的!!
				valueSet.addBigCondition("sprintID", "-1");
			} else {
				subValueSet.addNotNullCondition("sprintID");
				valueSet.addEqualCondition("sprintID", sprintID);
			}
		}
		
		if (projectName != null) {
			subValueSet.addEqualCondition("projectID", Integer.toString(getProjectID(projectName)));
		}

		// 唯一因為只有mantis_bug_table才有的東西，所以只能加在valueSet的搜尋裡面
		if (category != null) {
			valueSet.addEqualCondition("category", "'" + category + "'");
		}
		
		/*
		 * 如果有設定要取得哪天的資料的話，就將時間加入篩選項目
		 */
		if (startDate != null) {
			Timestamp tmp = new Timestamp(startDate.getTime());
			subValueSet.addBigCondition("updateDate", "'" + tmp.toString() + "'");
		}

		if (endDate != null) {
			Timestamp tmp = new Timestamp(endDate.getTime());
			subValueSet.addLessCondition("updateDate", "'" + tmp.toString()	+ "'");
		}
		
		// 幫這個subSelect作一個別名，讓後面可以取用他的row，然後加個Group By
		String subQuery = "(" + subValueSet.getSelectQuery() + " group by storyID) sub_Select";

		// 然後把這個subSelect加到join裡面
		valueSet.joinTableName(subQuery,
			"sub_Select.storyID = ezscrum_story_relation.storyID AND ezscrum_story_relation.updateDate = sub_Select.max_date AND ezscrum_story_relation.id = sub_Select.max_id");

		String query = valueSet.getSelectQuery();
		resultIssues = setIssue(projectName, query);
		return resultIssues;
	}

	public IIssue[] getIssues(String projectName) {
		int projectID = getProjectID(projectName);
		IQueryValueSet valueSet = new MySQLQuerySet();
		// 訂定查詢table名稱
		valueSet.addTableName("mantis_bug_table");
		valueSet.addTableName("mantis_bug_text_table");
		// 訂定查詢的條件
		valueSet.addFieldEqualCondition("mantis_bug_table.bug_text_id",
				"mantis_bug_text_table.id");
		valueSet.addEqualCondition("project_id", Integer.toString(projectID));
		// 產生查詢的query
		String query = valueSet.getSelectQuery();
		IIssue[] issues = setIssue(projectName, query);
//		m_issues = issues;
//		m_currentProjectName = projectName;
//		return m_issues;
		return issues;
	}

	public IIssue[] getIssues(String projectName, String category) {
		int projectID = getProjectID(projectName);
		IQueryValueSet valueSet = new MySQLQuerySet();
		// 訂定查詢table名稱
		valueSet.addTableName("mantis_bug_table");
		valueSet.addTableName("mantis_bug_text_table");

		// 訂定查詢的條件
		valueSet.addFieldEqualCondition("mantis_bug_table.bug_text_id", "mantis_bug_text_table.id");
		valueSet.addEqualCondition("project_id", Integer.toString(projectID));
		valueSet.addEqualCondition("category", "'" + category + "'");

		// 產生查詢的query
		String query = valueSet.getSelectQuery();
		log.info("[SQL] " + query);
		IIssue[] issues = setIssue(projectName, query);		
		return issues;
	}

	// 暫時做先做一半的欄位
	public void updateIssueContent(IIssue modifiedIssue) {
		HashMap<String, String> map = new HashMap<String, String>();
		IIssue issue = this.getIssue(modifiedIssue.getIssueID());

		if ( ! issue.getSummary().equals(modifiedIssue.getSummary()))
			map.put("summary", modifiedIssue.getSummary());
		
		if ( ! issue.getCategory().equals(modifiedIssue.getCategory()))
			map.put("category", modifiedIssue.getCategory());
		
		if ( ! issue.getStatus().equals(modifiedIssue.getStatus()))
			map.put("status", Integer.toString(ITSEnum.getStatus(modifiedIssue.getStatus())));
		
		if ( ! issue.getAssignto().equals(modifiedIssue.getAssignto()))
			map.put("handler_id", Integer.toString(getUserID(modifiedIssue.getAssignto())));
		
		if ( ! issue.getSeverity().equals(modifiedIssue.getSeverity()))
			map.put("severity", Integer.toString(ITSEnum.getSeverity(modifiedIssue.getSeverity())));
		
		if ( ! issue.getReproducibility().equals(modifiedIssue.getReproducibility()))
			map.put("reproducibility", Integer.toString(ITSEnum.getReproducibility(modifiedIssue.getReproducibility())));
		
		if ( ! issue.getPriority().equals(modifiedIssue.getPriority()))
			map.put("priority", Integer.toString(ITSEnum.getPriority(modifiedIssue.getPriority())));

//		if ( ! issue.getAssignto().equals(modifiedIssue.getPriority()))
//			map.put("handler_id", Integer.toString(getUserID(modifiedIssue.getAssignto())));


		IQueryValueSet valueSet = new MySQLQuerySet();
		if (map.keySet().size() > 0) {
			valueSet.addTableName("mantis_bug_table");
			for (String key : map.keySet()) {
				valueSet.addInsertValue(key, map.get(key));
			}
			
			valueSet.addFieldEqualCondition("mantis_bug_table.id", Long.toString(modifiedIssue.getIssueID()));
			String query = valueSet.getUpdateQuery();
			log.info("[SQL] " + query);
			getControl().execute(query);
		}

		String bug_text_id = getBugTextId(modifiedIssue.getIssueID());
		valueSet = new MySQLQuerySet();
		valueSet.addTableName("mantis_bug_text_table");
		
		if ( ! issue.getAdditional().equals(modifiedIssue.getAdditional()) || ! issue.getDescription().equals(modifiedIssue.getDescription())) {
			valueSet.addInsertValue("additional_information", modifiedIssue.getAdditional());
			valueSet.addInsertValue("description",modifiedIssue.getDescription());
			valueSet.addFieldEqualCondition("mantis_bug_text_table.id", bug_text_id);
			String query = valueSet.getUpdateQuery();
			log.info("[SQL] " + query);
			getControl().execute(query);
		}
	}

	// 刪除 retrospecitve 的issue ，將 data base裡的資料刪除
	public void removeIssue(String id) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("mantis_bug_table");
		valueSet.addEqualCondition("id", id);
		String query = valueSet.getDeleteQuery();
		log.info("[SQL] " + query);
		getControl().execute(query);
	}

	public ArrayList<IStory> getStorys(String projectName) {
		int projectId = getProjectID(projectName);
		IQueryValueSet valueSet = new MySQLQuerySet();
		// 訂定查詢table名稱
		valueSet.addTableName("mantis_bug_table");
		valueSet.addTableName("mantis_bug_text_table");

		// 訂定查詢的條件
		valueSet.addFieldEqualCondition("mantis_bug_table.bug_text_id",	"mantis_bug_text_table.id");
		valueSet.addEqualCondition("project_id", Integer.toString(projectId));
		valueSet.addEqualCondition("category", "'" + ScrumEnum.STORY_ISSUE_TYPE	+ "'");

		// 產生查詢的query
		String query = valueSet.getSelectQuery();
		log.info("[SQL] " + query);
		ArrayList<IStory> issues = new ArrayList<IStory>();
		try {
			ResultSet result = getControl().executeQuery(query);
			while (result.next()) {

				IStory issue = new Story();
				issue.setSummary(result.getString("summary"));
				issue.setIssueID(result.getLong("id"));
				issue.setIssueLink(MANTIS_VIEW_LINK + ATTRIBURE_ISSUEID + issue.getIssueID());
				issue.setStatus(ITSEnum.getStatus(result.getInt("status")));
				issue.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
				issue.setProjectID(projectName);
				issue.setProjectName(projectName);
				// 加入列表
				issues.add(issue);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return issues;
	}
	
	private IIssue[] setIssue(String projectName, String query) {
		ArrayList<IIssue> issues = new ArrayList<IIssue>();
		Hashtable<String, String> ht = new Hashtable<String, String>();
		
		try {
			log.info("[SQL] " + query);
			ResultSet result = getControl().executeQuery(query);
			
			while (result != null && result.next()) {
				IIssue issue = new Issue();
				issue.setCategory(result.getString("category"));
				issue.setReproducibility(ITSEnum.getReproducibility(result
						.getInt("reproducibility")));
				issue.setSeverity(ITSEnum
						.getSeverity(result.getInt("severity")));
				issue.setPriority(ITSEnum
						.getPriority(result.getInt("priority")));
				issue.setSummary(result.getString("summary"));
				issue.setViewState(ITSEnum.getViewStatus(result
						.getInt("view_state")));
				int handlerID = result.getInt("handler_id");
				if (handlerID > 0)
					issue.setAssignto(getUserName(handlerID));
				issue.setIssueID(result.getLong("MANTIS_BUG_TABLE.ID"));
				issue.setIssueLink(MANTIS_VIEW_LINK + ATTRIBURE_ISSUEID + issue.getIssueID());
				issue.setStatus(ITSEnum.getStatus(result.getInt("status")));
				issue.setSubmittedDate(result.getTimestamp("date_submitted")
						.getTime());
				issue.setReporter(getUserName(result.getInt("reporter_id")));
				// 工作開始日期等同submit的日期
				issue.setWorkingUpdated(result.getTimestamp("date_submitted")
						.getTime());
				issue.setDescription(result
						.getString("mantis_bug_text_table.description"));
				issue.setAdditional(result.getString("additional_information"));
				issue.setProjectID(projectName);
				issue.setProjectName(projectName);
				// 加入列表
				
				if (ht.get(Long.toString(issue.getIssueID())) == null) {
					issues.add(issue);
					ht.put(Long.toString(issue.getIssueID()), "true");
				}
			}
		} catch (SQLException e) {
			log.info("[SQL exception] " + e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			log.info("[Exception] " + e.toString());
			e.printStackTrace();
		} finally {
			ht = null;
		}

		return issues.toArray(new IIssue[issues.size()]);
	}
	
	// return BugText ID by issue ID
	public String getBugTextId(long issueID) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("mantis_bug_table");
		valueSet.addFieldEqualCondition("mantis_bug_table.id", Long.toString(issueID));
		String query = valueSet.getSelectQuery();
		
		try {
			log.info("[SQL] " + query);
			ResultSet result = getControl().executeQuery(query);
			if (result.next()) {
				return result.getString("bug_text_id");
			}
		} catch (Exception e) {
			log.info("[SQL exception] " + e.toString());
		}
		
		return null;
	}
}