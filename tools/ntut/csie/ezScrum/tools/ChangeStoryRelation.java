package ntut.csie.ezScrum.tools;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.IIssueNote;
import ntut.csie.ezScrum.issue.core.IIssueTag;
import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.issue.internal.Issue;
import ntut.csie.ezScrum.issue.internal.IssueNote;
import ntut.csie.ezScrum.issue.internal.IssueTag;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.internal.MantisIssueService;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.issue.sql.service.tool.ISQLControl;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.logic.ProjectLogic;
import ntut.csie.jcis.account.core.LogonException;
import ntut.csie.jcis.core.util.XmlFileUtil;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;
import org.jdom.Document;
import org.jdom.JDOMException;

public class ChangeStoryRelation {
	private MantisIssueService m_issueService;

	private String MANTIS_TABLE_MYSQL = "bugtracker";
	private String PORT_SERVICE_MYSQL = "3306";
	private String ROOT_TAG = "root";
	
	private String STORY_RELATION_TABLE = "ezscrum_story_relation";

	private IIssue[] m_issues = null;
	// 暫時硬寫的資料庫路徑
	public String SERVER_URL = "140.124.181.83";
	public String SERVER_PATH = "/mantis/mc/mantisconnect.php";
	public String USER = "root";
	public String PASS = "csie1234";
	public String PROJECT_NAME = "Robustness and Dependability";

	/*-----------------------------------------------------------
	 *	產生Table的SQL指令
	-------------------------------------------------------------*/
	String createStoryRelationTable = "CREATE TABLE `ezscrum_story_relation` ("
			+ "`id` int(8) NOT NULL auto_increment,"
			+ "`storyID` int(10) unsigned NOT NULL,"
			+ "`projectID` int(10) unsigned NOT NULL,"
			+ "`releaseID` int(10) default NULL,"
			+ "`sprintID` int(10) default NULL,"
			+ "`estimation` int(8) default NULL,"
			+ "`importance` int(8) default NULL,"
			+ "`updateDate` timestamp NULL default CURRENT_TIMESTAMP,"
			+ "PRIMARY KEY  (`id`),"
			+ "KEY `estimation` (`estimation`,`importance`),"
			+ "KEY `updateDate` (`sprintID`,`projectID`,`storyID`,`updateDate`)"
			+ ") ENGINE=MyISAM DEFAULT CHARSET=utf8";

	String createTagRelationTable = "CREATE TABLE `ezscrum_tag_relation` ("
			+ "`tag_id` int(10) NOT NULL," + "`story_id` int(10) NOT NULL,"
			+ "KEY `tag_id` (`tag_id`)," + "KEY `story_id` (`story_id`)"
			+ ") ENGINE=MyISAM DEFAULT CHARSET=utf8";

	String createTagTable = "CREATE TABLE `ezscrum_tag_table` ("
			+ "`id` int(10) unsigned NOT NULL auto_increment,"
			+ "`project_id` int(10) NOT NULL,"
			+ "`name` varchar(100) NOT NULL," 
			+ " CONSTRAINT pk_tag_proejct PRIMARY KEY (id, project_id)"
			+ ") ENGINE=MyISAM DEFAULT CHARSET=utf8";
	

	/************************************************************
	 * 
	 * 控制整個資料庫更新流程的地方
	 * 
	 *************************************************************/
	public void updateDataBase(IUserSession session) {
		String account = "";
		String pass = "";
		String hostname = "";
		/*-----------------------------------------------------------
		 *	取出所有Project的ITS設定，一個Project一個的去更新
		-------------------------------------------------------------*/
		ProjectLogic helper = new ProjectLogic();
//		IProject[] projects = helper.getAllProjects();
		List<IProject> projects = helper.getAllProjects();
		MultiMap map = new MultiHashMap();
		MultiMap projectMap = new MultiHashMap();
		/*-----------------------------------------------------------
		 *   統計一下現在的Project有用到哪幾台Mantis Server，如果有兩台以上就跟他說老子不幹啦
		-------------------------------------------------------------*/
		for (IProject project : projects) {
			Configuration config = new Configuration(session);
			map.put(config.getServerUrl(), config);
			projectMap.put(config.getServerUrl(), project);
		}

		/*-----------------------------------------------------------
		 *	然後檢查每台Mantis Server是不是有那個Index Table了
		-------------------------------------------------------------*/
		Set keys = map.keySet();
		for (Object key : keys) {
			Collection formColl = (Collection) map.get(key);
			
			Configuration[] forms = (Configuration[])formColl.toArray(new Configuration[formColl.size()]);
			
			Configuration form = forms[0];
			//如果有建立Table的話，那就繼續呼叫createIndexTable
//			if (createUpdateTable(form.getServerUrl(), PORT_SERVICE_MYSQL,
//					MANTIS_TABLE_MYSQL, form.getDBAccount(), form
//							.getDBPassword())) {
			if(true){
				ISQLControl control = new MySQLControl(form.getServerUrl(), PORT_SERVICE_MYSQL, MANTIS_TABLE_MYSQL);
				control.setUser(form.getDBAccount());
				control.setPassword(form.getDBPassword());
				
				control.connection();
				
				/*-----------------------------------------------------------
				*	對每個Project建立Index
				-------------------------------------------------------------*/
				Collection<?> projectColl = (Collection<?>) projectMap.get(key);
				IProject[] tmp = (IProject[])projectColl.toArray(new IProject[projectColl.size()]);
				for(IProject project:tmp)
				{
					createIndexTable(project.getName(),control);
				}
				control.close();
			}
			
			modifyTagTable(form.getServerUrl(), PORT_SERVICE_MYSQL,
					MANTIS_TABLE_MYSQL, form.getDBAccount(), form
							.getDBPassword(), projects);
			
		}
	}

	/*-----------------------------------------------------------
	 *	檢查現在這個資料庫裡面是否有存在需要更新的Table
	-------------------------------------------------------------*/
	public boolean createUpdateTable(String hostname, String port,
			String dbname, String user, String pass) {
		boolean create = false;

		ISQLControl checker = new MySQLControl(hostname, port, dbname);
		checker.setUser(user);
		checker.setPassword(pass);

		ArrayList<String> nameList = new ArrayList<String>();

		checker.connection();

		try {
			ResultSet result = checker.executeQuery("SHOW TABLES");

			int i = 0;
			while (result.next()) {
				nameList.add(result.getString(1));
				i++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		/*-----------------------------------------------------------
		 *	檢查Table是否存在
		-------------------------------------------------------------*/
		// ezscrum_relation_story
		if (!nameList.contains(STORY_RELATION_TABLE)) {
			// 如果Table不存在的話，就建立他
			checker.execute(createStoryRelationTable);
			create = true;
		}

		// ezscrum_tag_relation
		if (!nameList.contains("ezscrum_tag_relation")) {
			checker.execute(createTagRelationTable);
		}

		// ezscrum_tag_table
		if (!nameList.contains("ezscrum_tag_table")) {
			checker.execute(createTagTable);
		}

		checker.close();

		return create;
	}
	
	/*-----------------------------------------------------------
	 *	檢查現在這個資料庫裡面是否有存在需要更新的Tag Table
	-------------------------------------------------------------*/
	public void modifyTagTable(String hostname, String port,
			String dbname, String user, String pass, List<IProject> projects) {
		boolean modify = false;

		ISQLControl checker = new MySQLControl(hostname, port, dbname);
		checker.setUser(user);
		checker.setPassword(pass);

		checker.connection();


		/*-----------------------------------------------------------
		 *	檢查project_id欄位是否存在
		-------------------------------------------------------------*/
		try {
			ResultSet result = checker.executeQuery("SHOW FIELDS FROM ezscrum_tag_table LIKE 'project_id'");
			
			if (!result.next()) 
				modify = true;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("modify: " + modify);
		/*-----------------------------------------------------------
		 *	project_id不存在則更新資料表
		-------------------------------------------------------------*/
		if (modify)
		{
			System.out.println("================ modifyTagTable ====================");
			// 取得已存在資料
			IIssueTag[] tagList = getTagList(checker);
			
			// 刪除ezscrum_tag_table資料表
			checker.execute("DROP TABLE ezscrum_tag_table");
			System.out.println("Delete Old Table");
			
			// 建立新的ezscrum_tag_table資料表
			checker.execute(createTagTable);
			System.out.println("Create New Table");
			
			// 將資料依據專案個數備份並回存
			// 也就是每個專案都擁有相同的tag
			for(IProject project:projects)
			{
				int projectID = getProjectID(project.getName(),checker);
				
				for (int i = 0; i < tagList.length; i++)
				{
					
					checker.execute("INSERT INTO ezscrum_tag_table VALUES('" 
							+ tagList[i].getTagId() 
							+ "','" 
							+ projectID
							+ "','"
							+ tagList[i].getTagName()
							+ "')") ;
					System.out.println("Insert New Data");
				}
			}				
		}

		checker.close();
	}

	public IIssue[] getIssues(String projectName, String releaseID,
			String sprintID) {
		IIssue[] resultIssue = null;

		IQueryValueSet valueSet = new MySQLQuerySet();
		// 將 Table串連起來
		valueSet.addTableName("mantis_project_table");
		valueSet.joinTableName("mantis_bug_table",
				"mantis_bug_table.project_id = mantis_project_table.id");
		valueSet.joinTableName("ezscrum_story_relation",
				"ezscrum_story_relation.storyID = mantis_bug_table.id");
		valueSet.joinTableName("mantis_bug_text_table",
				"mantis_bug_table.bug_text_id = mantis_bug_text_table.id");
		// 設定搜尋條件
		if (releaseID != null)
			valueSet.addEqualCondition("ezscrum_story_relation.releaseID",
					releaseID);
		if (sprintID != null)
			valueSet.addEqualCondition("ezscrum_story_relation.sprintID",
					sprintID);

//		System.out.println(valueSet.getSelectQuery());
		return resultIssue;
	}

	public IIssue[] getIssues(String projectName,ISQLControl control) {
		IIssue[] issues = queryIssues(projectName,control);
		// 建立歷史紀錄
		for (IIssue issue : issues) {
			setIssueTag(issue,control);
			issue.setIssueNotes(getIssueNotes(issue,control));
			// System.out.println("ID="+issue.getIssueID()+"Sprint="+issue.getSprintID());
		}
		return issues;
	}
	
	private IIssueTag[] getTagList(ISQLControl control) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("ezscrum_tag_table");
		String query = valueSet.getSelectQuery();
		ArrayList<IIssueTag> tags = new ArrayList<IIssueTag>();
		
		try {
			ResultSet result = control.executeQuery(query);
			while (result.next()) {
				IIssueTag tag = new IssueTag();
				tag.setTagId(result.getLong("id"));
				tag.setTagName(result.getString("name"));

				tags.add(tag);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return tags.toArray(new IIssueTag[tags.size()]);
	}

	// get user session
	public IUserSession CreateUserSession() throws LogonException {
//		IAccount theAccount = null;
//		theAccount = new Account("admin");
//		IUserSession theUserSession = new UserSession(theAccount);

//		return theUserSession;
		// ezScrum v1.8
		AccountObject user = new AccountObject();
		user.setAccount("admin");
		return new UserSession(user);
	}
	/**
	 * 建立Index Table
	 */
	public void createIndexTable(String projectName,ISQLControl control) {
		IIssue[] issues = getIssues(projectName,control);
		IQueryValueSet valueSet = new MySQLQuerySet();
		int projectID = getProjectID(projectName,control);

		for (IIssue issue : issues) {

			// 取出必要得Issue資訊
			Map<Date, String> releaseMap = issue.getTagValueList("Release");
			Map<Date, String> iterationMap = issue.getTagValueList("Iteration");
			Map<Date, String> estimationMap = issue
					.getTagValueList("Estimation");
			Map<Date, String> importanceMap = issue
					.getTagValueList("Importance");

			// 合併所有的修改時間
			HashSet<Date> mergeKeys = new HashSet<Date>();
			mergeKeys.addAll(releaseMap.keySet());
			mergeKeys.addAll(iterationMap.keySet());
			mergeKeys.addAll(estimationMap.keySet());
			mergeKeys.addAll(importanceMap.keySet());

			// 將同時間修改的資料放置在一起
			MultiMap map = new MultiHashMap();

			categoryTag(map, releaseMap);
			putNullValue(map, mergeKeys, releaseMap.keySet());

			categoryTag(map, iterationMap);
			putNullValue(map, mergeKeys, iterationMap.keySet());

			categoryTag(map, estimationMap);
			putNullValue(map, mergeKeys, estimationMap.keySet());

			categoryTag(map, importanceMap);
			putNullValue(map, mergeKeys, importanceMap.keySet());

			// Map裡面存放資料的順序為release,iteration,estimation,importance
			// 將此issue的歷史資料一筆筆將資料輸入
			Set<Date> keys = new TreeSet(map.keySet());
			for (Date key : keys) {
				Timestamp tmp = new Timestamp(key.getTime());
				valueSet.clear();
				valueSet.addTableName(STORY_RELATION_TABLE);
				valueSet.addInsertValue("storyID", Long.toString(issue
						.getIssueID()));
				valueSet.addInsertValue("projectID", Integer
						.toString(projectID));

				Collection coll = (Collection) map.get(key);

				String result[] = new String[4];
				String rowNames[] = { "releaseID", "sprintID", "estimation",
						"importance" };

				int i = 0;
				for (Object s : coll) {
					result[i] = (String) s;
					i++;
				}

				for (i = 0; i < 4; i++) {
					if (result[i] == null || result[i].isEmpty())
						continue;
					else
						valueSet.addInsertValue(rowNames[i], result[i]);
				}

				valueSet.addInsertValue("updateDate", tmp.toString());

//				System.out.println(valueSet.getInsertQuery());
				control.execute(valueSet.getInsertQuery());
			}
		}
	}

	private void ignoreSprintMove() {

	}

	private void putNullValue(MultiMap map, Set<Date> mergeKeys, Set<Date> keys) {
		HashSet<Date> tmp = new HashSet<Date>();
		tmp.addAll(mergeKeys);
		tmp.removeAll(keys);

		for (Date key : tmp) {
			map.put(key, null);
		}
	}

	private void categoryTag(MultiMap map, Map<Date, String> resource) {
		// 將同時間修改的資料放置在一起
		Set<Date> tmp = resource.keySet();
		String value = null;
		for (Date key : tmp) {
			value = resource.get(key);
			map.put(key, value);
		}
	}


	/*----------------以下為不可視之物，因為都是我copy來的--------------------------------------*/
	/***************************************************************************************/
	/*
	 * Edit by py2k at 2009/8/4
	 */
	public IIssue[] queryIssues(String projectName,ISQLControl control) {

		int projectID = getProjectID(projectName,control);
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
		IIssue[] issues = setIssue(projectName, query , control);
		m_issues = issues;
		return m_issues;
	}

	protected int getProjectID(String projectName,ISQLControl control) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("mantis_project_table");
		valueSet.addLikeCondition("name", projectName);
		String query = valueSet.getSelectQuery();
		// String query = "SELECT `id` FROM `mantis_project_table` WHERE `name`
		// LIKE '"
		// + projectName + "'";
		// Statement stmt;
		try {
			// stmt = conn.createStatement();
			// ResultSet result = stmt.executeQuery(query);
			ResultSet result = control.executeQuery(query);
			int projectID = -1;
			if (result.next())
				projectID = result.getInt("id");
			return projectID;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	private IIssue[] setIssue(String projectName, String query,ISQLControl control) {
		ArrayList<IIssue> issues = new ArrayList<IIssue>();
		try {
			ResultSet result = control.executeQuery(query);
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
					issue.setAssignto("");
				issue.setIssueID(result.getLong("id"));
				// issue.setIssueLink(HTTP_SERVICE + getPrefs().getServerUrl()
				// + MANTIS_VIEW_LINK + issue.getIssueID());
				issue.setStatus(ITSEnum.getStatus(result.getInt("status")));
				issue.setSubmittedDate(result.getTimestamp("date_submitted")
						.getTime());
				// issue.setReporter(getUserName(result.getInt("reporter_id")));
				// 工作開始日期等同submit的日期
				issue.setWorkingUpdated(result.getTimestamp("date_submitted")
						.getTime());
				issue.setDescription(result
						.getString("mantis_bug_text_table.description"));
				issue.setAdditional(result.getString("additional_information"));
				issue.setProjectID(projectName);
				// 加入列表
				issues.add(issue);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return issues.toArray(new IIssue[issues.size()]);
	}

	private void setIssueTag(IIssue issue,ISQLControl control) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("mantis_bugnote_table");
		valueSet.addTableName("mantis_bugnote_text_table");
		valueSet.addFieldEqualCondition("mantis_bugnote_text_table.id",
				"mantis_bugnote_table.bugnote_text_id");
		valueSet.addFieldEqualCondition("mantis_bugnote_table.bug_id", Long
				.toString(issue.getIssueID()));
		String query = valueSet.getSelectQuery();
		// String query = "SELECT date_submitted, note,
		// mantis_bugnote_text_table.id FROM `mantis_bugnote_table` ,
		// `mantis_bugnote_text_table` WHERE mantis_bugnote_text_table.id =
		// mantis_bugnote_table.bugnote_text_id AND mantis_bugnote_table.bug_id
		// ="
		// + issue.getIssueID();
		// Statement stmt;
		try {
			// stmt = conn.createStatement();
			// ResultSet result = stmt.executeQuery(query);
			ResultSet result = control.executeQuery(query);
			while (result.next()) {
				String note = result.getString("note");

				// 只要note中有包含JCIS的tag就算是記錄的地方
				if (note.contains("<JCIS") && !note.contains("<JCIS:")) {
					note = "<" + ROOT_TAG + ">" + note + "</" + ROOT_TAG + ">";
					Document doc;
					try {
						doc = XmlFileUtil.LoadXmlString(note);
						issue.setTagContent(doc.getRootElement());
					} catch (JDOMException e) {
						e.printStackTrace();
						continue;
					} catch (IOException e) {
						e.printStackTrace();
						continue;
					}
				}

				/***************************************************************
				 * 使用Issue Note記錄全部的note
				 **************************************************************/
				IssueNote issueNote = new IssueNote();
				issueNote.setIssueID(issue.getIssueID());
				issueNote.setText(result.getString("note"));
				issueNote.setHandler("");
				issueNote.setNoteID(result.getLong("id"));
				issueNote.setSubmittedDate(result
						.getTimestamp("date_submitted").getTime());
				issueNote.setModifiedDate(result.getTimestamp("last_modified")
						.getTime());

				issue.addIssueNote(issueNote);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<IIssueNote> getIssueNotes(IIssue issue,ISQLControl control) {
		List<IIssueNote> notes = new ArrayList<IIssueNote>();
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("mantis_bugnote_table");
		valueSet.addTableName("mantis_bugnote_text_table");
		valueSet.addFieldEqualCondition("mantis_bugnote_text_table.id",
				"mantis_bugnote_table.bugnote_text_id");
		valueSet.addFieldEqualCondition("mantis_bugnote_table.bug_id", Long
				.toString(issue.getIssueID()));
		String query = valueSet.getSelectQuery();
		ResultSet result = control.executeQuery(query);
		try {
			while (result.next()) {
				IIssueNote note = new IssueNote();
				note.setIssueID(issue.getIssueID());
				note.setNoteID(result.getLong("mantis_bugnote_text_table.id"));
				note.setText(result.getString("note"));
				note.setSubmittedDate(result.getTimestamp("date_submitted")
						.getTime());
				note.setModifiedDate(result.getTimestamp("last_modified")
						.getTime());
				note.setHandler("");
				notes.add(note);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return notes;
	}

	/*-----------------------------------------------------------
	 *	測試用的Main Function
	-------------------------------------------------------------*/
//	public static void main(String[] args) {
//		ChangeStoryRelation change = new ChangeStoryRelation();
//		change.openConnect();
//		change.createIndexTable("Robustness and Dependability");
//		change.closeConnect();
//	}

}
