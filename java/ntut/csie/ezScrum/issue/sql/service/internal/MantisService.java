package ntut.csie.ezScrum.issue.sql.service.internal;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.dao.AttachFileDAO;
import ntut.csie.ezScrum.dao.HistoryDAO;
import ntut.csie.ezScrum.dao.TagDAO;
import ntut.csie.ezScrum.dao.TaskDAO;
import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.IIssueNote;
import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.issue.internal.IssueNote;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IITSService;
import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.tool.ISQLControl;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.HSQLControl;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.iteration.support.TranslateSpecialChar;
import ntut.csie.ezScrum.web.dataInfo.AttachFileInfo;
import ntut.csie.ezScrum.web.dataObject.AttachFileObject;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.databasEnum.IssueTypeEnum;
import ntut.csie.jcis.account.core.AccountEnum;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.core.util.XmlFileUtil;
import ntut.csie.jcis.resource.core.IProject;
import ntut.csie.jcis.resource.core.ResourceFacade;
import ntut.csie.jcis.resource.core.internal.Workspace;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

public class MantisService extends AbstractMantisService implements IITSService {
	private static Log log = LogFactory.getLog(MantisService.class);
	private static String INITIATE_SQL_FILE = "initial_bk.sql";
	final public static String ROOT_TAG = "root";

	final private String m_id = "Mantis";
	final private String PORT_SERVICE_MYSQL = "3306";
	private String MANTIS_TABLE_TYPE = "Default";
	private String MANTIS_DB_NAME = "bugtracker";

	private MantisNoteService mNoteService;
	private MantisIssueService mIssueService;

	public MantisService(Configuration config) {
		setConfig(config);

		if (!getConfig().getDBType().equals(""))
			MANTIS_TABLE_TYPE = getConfig().getDBType();

		if (!getConfig().getDBName().equals(""))
			MANTIS_DB_NAME = getConfig().getDBName();

		// =========設定要使用的SQLControl============
		ISQLControl control = null;
		if (MANTIS_TABLE_TYPE.equalsIgnoreCase("MySQL")) {
			control = new MySQLControl(config.getServerUrl(),
					PORT_SERVICE_MYSQL, MANTIS_DB_NAME);
		} else {
			/*-----------------------------------------------------------
			 *	如果是要使用Default SQL的設定，
			 *	那麼ServerUrl的路徑要指到Workspace裡面
			 *	所以要先取得Workspace的路徑
			-------------------------------------------------------------*/
			// 因為Default DB的檔案名稱預設就是ProjectName
			String projectName = config.getProjectName();

			// 如果是Default SQL的話，那麼DB路徑就會被設定為Project底下的資料夾+Project檔案名稱
			// ex. WorkspacePath/ProjectName/ProjectName
			String DBRootPath = new Workspace().getRoot()
					.getProject(projectName).getFullPath().append(projectName)
					.getPathString();

			// 然後剩下的路徑啥就不會管他了
			// ex. ProjectName.h2.db , 所以MANTIS_TABLE_NAME會被完全忽略 ....
			control = new HSQLControl(DBRootPath, PORT_SERVICE_MYSQL,
					projectName);
		}

		control.setUser(config.getDBAccount());
		control.setPassword(config.getDBPassword());
		setControl(control);

	}

	/**
	 * 利用透過MantisConnect及直接access資料庫的方式來實作 因此提供的pm帳號必需要能在Mantis及MySQL上使用
	 */
	public void openConnect() {
		getControl().connect();

		mNoteService = new MantisNoteService(getControl(), getConfig());
		mIssueService = new MantisIssueService(getControl(), getConfig());
	}

	/************************************************************
	 * 執行SQL Script清空資料庫並且重新建立Table
	 *************************************************************/
	public boolean initiateDB() throws SQLException {
		getControl().connect();
		Connection connection = getControl().getconnection();

		String defaultFile = ResourceFacade.getWorkspace().getRoot()
				.getFolder(IProject.METADATA).getFullPath()
				+ "/" + INITIATE_SQL_FILE;
		try {
			TableCreater
					.importSQL(connection, new FileInputStream(defaultFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} finally {
			getControl().close();
		}
		return true;
	}

	/**
	 * 在SQL Server上建立一個資料庫
	 */
	public boolean createDB() {
		ISQLControl controller = getControl();
		controller.connectToServer();
		try {
			String sql = "CREATE DATABASE " + MANTIS_DB_NAME;
			return controller.execute(sql);
		} finally {
			controller.close();
		}
	}

	/**
	 * Test connection and initialize database
	 * 
	 * 1. Test database connection available 
	 * 2. Create database 
	 * 3. Check number of table in database 
	 * 4. Initialize tables in database
	 * 
	 */
	public boolean testAndInitDatabase() {
		try {
			System.out.println("Test DB connection...");
			testConnect();

			try {
				System.out.println("Create DB...");
				createDB();
			} catch (Exception e) {
			}

			if (!isAllTableExist()) {
				System.out.println("Create tables...");
				initiateDB();
			}

			return true;
		} catch (Exception exception) {
			System.out
					.println("************** ERROR MESSAGE **************\n\n\n"
							+ "Database connect fail.\n\n"
							+ "Please check database setting in ezScrum.ini is correct.\n\n\n"
							+ "*******************************************\n\n\n");
		}
		return false;
	}

	/**
	 * 
	 * 測試hostname+port 是否可以正確連線
	 * 
	 */
	public void testConnect() throws Exception {
		try {
			if (testServerConnect()) {
				return;
			} else {
				throw new TestConnectException(
						TestConnectException.DATABASE_ERROR);
			}
		} catch (SQLException e) {
			throw new TestConnectException(TestConnectException.CONNECT_ERROR);
		}

	}

	public boolean isAllTableExist() {
		/*-----------------------------------------------------------
		 *	如果是MySql需要檢查Table是否存在
		-------------------------------------------------------------*/
		if (MANTIS_TABLE_TYPE.equalsIgnoreCase("MySQL")) {
			return TableCreater.isAllTableExist(getControl());

		}
		return true;
	}

	private boolean testServerConnect() throws SQLException {
		getControl().connectToServer();
		Connection connection = getControl().getconnection();
		try {
			if (connection == null)
				return false;
			else
				return true;
		} finally {
			if (connection != null)
				connection.close();
		}
	}

	/**
	 * 關閉連線,主要是關閉SQL的連線 在使用完成一定要關閉連線
	 */
	public void closeConnect() {
		try {
			getControl().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public long newIssue(IIssue issue) {
		long issueId = mIssueService.newIssue(issue);
		
		int issueType = -1;
		if (issue.getIssueType() == IssueTypeEnum.TYPE_UNPLANNED) {
			issueType = IssueTypeEnum.TYPE_UNPLANNED;
		} else if (issue.getIssueType() == IssueTypeEnum.TYPE_STORY) {
			issueType = IssueTypeEnum.TYPE_STORY;
		} else if (issue.getIssueType() == IssueTypeEnum.TYPE_TASK) {
			issueType = IssueTypeEnum.TYPE_TASK;
		} else if (issue.getIssueType() == IssueTypeEnum.TYPE_RETROSPECTIVE) {
			issueType = IssueTypeEnum.TYPE_RETROSPECTIVE;
		}
		
		HistoryObject createHisytory = new HistoryObject(
				issueId, 
				issueType, 
				HistoryObject.TYPE_CREATE,
				"",
				"",
				System.currentTimeMillis());
		createHisytory.save();
		if (issue.getParentId() > 0) {
			HistoryObject appendHistory = new HistoryObject(
					issueId, 
					issueType, 
					HistoryObject.TYPE_APPEND,
					"",
					String.valueOf(issue.getParentId()),
					System.currentTimeMillis());
			appendHistory.save();
		}
		
		return issueId;
	}

//	/************************************************************
//	 * 可以針對 releaseID 與 SprintID 來搜尋 Story 與 Task，並且只取得最新的資料
//	 *************************************************************/
//	public IIssue[] getIssues(String projectName, String category,
//			String releaseId, String sprintId) {
//		return getIssues(projectName, category, releaseId, sprintId, null);
//	}

	/************************************************************
	 * 找出期間限定的Issue
	 *************************************************************/
	public IIssue[] getIssues(String projectName, String category,
			String releaseId, String sprintId, Date startDate, Date endDate) {

		IIssue[] issues = mIssueService.getIssues(projectName, category,
				releaseId, sprintId, startDate, endDate);

		for (IIssue issue : issues) {
			setIssueNote(issue);
			issue.setIssueNotes(mNoteService.getIssueNotes(issue));
			setChildParentRelation(issue);
		}
		return issues;
	}

	/************************************************************
	 * 針對傳入的時間範圍取出這時間的Issue狀態 (for story)
	 *************************************************************/
	public IIssue[] getIssues(String projectName, String category,
			String releaseId, String sprintId, Date date) {
		
		IIssue[] issues = mIssueService.getIssues(projectName, category,
				releaseId, sprintId, date);
		
		for (IIssue issue : issues) {
			setIssueNote(issue);
			issue.setIssueNotes(mNoteService.getIssueNotes(issue));
			
			setChildParentRelation(issue);
		}
		return issues;
	}

	// 當有更新時,若沒有重新建立 Service 的話,連續取得同一個專案會造成只能取到舊資料
	public IIssue[] getIssues(String projectName) {
		IIssue[] issues = mIssueService.getIssues(projectName);
		
		for (IIssue issue : issues) {
			setIssueNote(issue);
			issue.setIssueNotes(mNoteService.getIssueNotes(issue));
			setChildParentRelation(issue);
		}
		return issues;
	}

	public IIssue[] getIssues(String projectName, String category) throws SQLException {
		IIssue[] issues = mIssueService.getIssues(projectName, category);
		
		for (IIssue issue : issues) {
			setIssueNote(issue);
			issue.setIssueNotes(mNoteService.getIssueNotes(issue));
			setChildParentRelation(issue);
		}
		return issues;
	}

	public IIssue getIssue(long issueId) {
		IIssue issue = mIssueService.getIssue(issueId);
		
		if (issue != null) {
			setIssueNote(issue);
			issue.setIssueNotes(mNoteService.getIssueNotes(issue));
			setChildParentRelation(issue);
		}
		return issue;
	}
	
	private void setChildParentRelation(IIssue issue) {
		ArrayList<Long> childrenId = new ArrayList<Long>();
		long parentId = 0;
		
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("mantis_bug_relationship_table");
		valueSet.addEqualCondition("source_bug_id", issue.getIssueID());
		String query = valueSet.getSelectQuery();
		
		ResultSet result = getControl().executeQuery(query);

		try {
			while (result.next()) {
				childrenId.add(result.getLong("destination_bug_id"));
			}
		} catch (SQLException e) {
		}
		
		valueSet.clear();
		valueSet.addTableName("mantis_bug_relationship_table");
		valueSet.addEqualCondition("destination_bug_id", issue.getIssueID());
		query = valueSet.getSelectQuery();
		
		result = getControl().executeQuery(query);
		try {
			while (result.next()) {
				parentId = result.getLong("source_bug_id");
			}
		} catch (SQLException e) {
		}
		
		if (issue.getCategory().equals(ScrumEnum.STORY_ISSUE_TYPE)) {
			childrenId.clear();
			ArrayList<TaskObject> tasks = TaskDAO.getInstance().getTasksByStoryId(issue.getIssueID());
			for (TaskObject task : tasks) {
				childrenId.add(task.getId());
			}
		}
		
		issue.setChildrenId(childrenId);
		issue.setParentId(parentId);
	}

	// =====================有新增標籤時,在這必需要存入Issue Tag中============
	private void setIssueNote(IIssue issue) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("mantis_bugnote_table");
		valueSet.addTableName("mantis_bugnote_text_table");
		valueSet.addFieldEqualCondition("mantis_bugnote_text_table.id",
				"mantis_bugnote_table.bugnote_text_id");
		valueSet.addFieldEqualCondition("mantis_bugnote_table.bug_id",
				Long.toString(issue.getIssueID()));
		String query = valueSet.getSelectQuery();
		// String query = "SELECT date_submitted, note,
		// mantis_bugnote_text_table.id FROM `mantis_bugnote_table` ,
		// `mantis_bugnote_text_table` WHERE mantis_bugnote_text_table.id =
		// mantis_bugnote_table.bugnote_text_id AND mantis_bugnote_table.bug_id
		// ="
		// + issue.getIssueID();
		// Statement stmt;
		try {
			ResultSet result = getControl().executeQuery(query);
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
						log.error(e);
						continue;
					} catch (IOException e) {
						log.error(e);
						continue;
					}
				}

				/***************************************************************
				 * 使用Issue Note記錄全部的note
				 **************************************************************/
				IssueNote issueNote = new IssueNote();
				issueNote.setIssueID(issue.getIssueID());
				issueNote.setText(result.getString("note"));
				issueNote.setHandler(this.getUserName(result
						.getInt("reporter_id")));
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

	public String[] getActors(String projectName, int accessLevel) {
		int projectID = getProjectID(projectName);
		List<String> allUserList = getAllUsers();

		List<String> userList = new ArrayList<String>();
		for (String username : allUserList) {
			if (getProjectAccessLevel(getUserID(username), projectID) >= accessLevel)
				userList.add(username);
		}

		return userList.toArray(new String[userList.size()]);

	}

	public String[] getCategories(String projectName) {
		List<String> categories = new ArrayList<String>();
		int projectID = getProjectID(projectName);

		if (projectID > 0) {
			IQueryValueSet valueSet = new MySQLQuerySet();
			valueSet.addTableName("mantis_project_category_table");
			valueSet.addEqualCondition("project_id",
					Integer.toString(projectID));

			String query = valueSet.getSelectQuery();

			try {
				ResultSet result = getControl().executeQuery(query);
				while (result.next()) {
					categories.add(result.getString("category"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return categories.toArray(new String[categories.size()]);
	}

	public String getServiceID() {
		return m_id;
	}

	public int count(String projectName, String type, Date date) {
		IIssue[] issues = getIssues(projectName);
		if (type.equals(ITSEnum.CLOSED))
			return countDone(issues, date);
		if (type.equals(ITSEnum.RESOLVED))
			return countAssigned(issues, date);
		if (type.equals(ITSEnum.WORKING))
			return countNonAssign(issues, date);
		if (type.equals(ITSEnum.TOTAL))
			return countTotal(issues, date);
		return 0;

	}

	private int countDone(IIssue[] issues, Date date) {
		int count = 0;
		for (IIssue issue : issues) {
			if (issue.getStatusUpdated(ITSEnum.CLOSED_STATUS) == null)
				continue;
			// 一般而言,date都會包含當天,所以在計算時要多加一天
			if (issue.getStatusUpdated(ITSEnum.CLOSED_STATUS).getTime() <= (date
					.getTime() + 24 * 3600 * 1000)) {

				count++;
			}
		}
		return count;
	}

	private int countAssigned(IIssue[] issues, Date date) {
		int count = 0;
		for (IIssue issue : issues) {
			if (issue.getStatusUpdated(ITSEnum.ASSIGNED_STATUS) == null)
				continue;
			// 一般而言,date都會包含當天,所以在計算時要多加一天
			if (issue.getStatusUpdated(ITSEnum.ASSIGNED_STATUS).getTime() <= (date
					.getTime() + 24 * 3600 * 1000))
				count++;
		}
		return count;
	}

	private int countNonAssign(IIssue[] issues, Date date) {
		int count = 0;
		for (IIssue issue : issues) {
			if (issue.getWorkingUpdated() == 0)
				continue;
			// 一般而言,date都會包含當天,所以在計算時要多加一天
			if (issue.getWorkingUpdated() <= (date.getTime() + 24 * 3600 * 1000))
				count++;
		}
		return count;
	}

	private int countTotal(IIssue[] issues, Date date) {
		return issues.length;
	}

	@Override
	public void updateBugNote(IIssue issue) {
		mNoteService.updateBugNote(issue);
	}

	@Override
	public void updateIssueNote(IIssue issue, IIssueNote note) {
		mNoteService.updateIssueNote(issue, note);
	}

	@Override
	public void addRelationship(long sourceId, long targetId, int type,
			Date date) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("mantis_bug_relationship_table");
		valueSet.addInsertValue("source_bug_id", Long.toString(sourceId));
		valueSet.addInsertValue("destination_bug_id", Long.toString(targetId));
		valueSet.addInsertValue("relationship_type", Integer.toString(type));
		String query = valueSet.getInsertQuery();
		getControl().execute(query);
		
		if (type == ITSEnum.PARENT_RELATIONSHIP) {
			HistoryObject addTaskHistory = new HistoryObject(
					sourceId, 
					IssueTypeEnum.TYPE_STORY, 
					HistoryObject.TYPE_ADD,
					"",
					String.valueOf(targetId),
					date.getTime());
			addTaskHistory.save();
			HistoryObject appendedToStoryHistory = new HistoryObject(
					targetId, 
					IssueTypeEnum.TYPE_TASK, 
					HistoryObject.TYPE_APPEND,
					"",
					String.valueOf(sourceId),
					date.getTime());
			appendedToStoryHistory.save();
		}
	}

	@Override
	public void removeRelationship(long sourceId, long targetId, int type) {
		if (type == ITSEnum.PARENT_RELATIONSHIP) {
			IQueryValueSet valueSet = new MySQLQuerySet();
			valueSet.addTableName("mantis_bug_relationship_table");
			valueSet.addEqualCondition("source_bug_id", Long.toString(sourceId));
			valueSet.addEqualCondition("destination_bug_id",
					Long.toString(targetId));
			String query = valueSet.getDeleteQuery();
			// String query = "DELETE FROM `mantis_bug_relationship_table` WHERE
			// source_bug_id = "
			// + sourceID + " AND destination_bug_id = " + targetID;
			getControl().execute(query);
			
			long time = System.currentTimeMillis();
			HistoryObject dropTaskHistory = new HistoryObject(
					sourceId, 
					IssueTypeEnum.TYPE_STORY, 
					HistoryObject.TYPE_DROP,
					"",
					String.valueOf(targetId),
					time);
			dropTaskHistory.save();
			
			HistoryObject removedFromStoryHistory = new HistoryObject(
					targetId, 
					IssueTypeEnum.TYPE_TASK, 
					HistoryObject.TYPE_REMOVE,
					"",
					String.valueOf(sourceId),
					time);
			removedFromStoryHistory.save();
		}
	}

	public void updateHandler(IIssue issue, String handler, Date modifyDate) {
		// 變更人員
		int oldActor = getUserID(issue.getAssignto());

		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("mantis_bug_table");
		valueSet.addInsertValue("handler_id",
				Integer.toString(getUserID(handler)));

		// 若狀態不為 assigned，則改變狀態為 assigned
		int oldStatus = ITSEnum.getStatus(issue.getStatus());
		if (oldStatus != ITSEnum.ASSIGNED_STATUS) {
			valueSet.addInsertValue("status",
					Integer.toString(ITSEnum.ASSIGNED_STATUS));
		}

		valueSet.addEqualCondition("id", Long.toString(issue.getIssueID()));

		String updateQuery = valueSet.getUpdateQuery();

		getControl().execute(updateQuery);
		String newUsername = getUserID(handler) + "";
		String oldActorString = "";
		if (oldActor > 0) {
			oldActorString = String.valueOf(oldActor);
		}
		HistoryObject handlerHistory = new HistoryObject(
				issue.getIssueID(), 
				issue.getIssueType(), 
				HistoryObject.TYPE_HANDLER,
				// issue 沒有 actor 會回傳 0, 如果 actor == 0 則存入空字串
				oldActorString,
				newUsername,
				System.currentTimeMillis());
		handlerHistory.save();
	}

	@Override
	public void updateName(IIssue issue, String name, Date modifyDate) {

		TranslateSpecialChar translateChar = new TranslateSpecialChar();

		// 變更SUMMARY
		String oldSummary = issue.getSummary();
		oldSummary = translateChar.TranslateDBChar(oldSummary);
		name = translateChar.TranslateDBChar(name);

		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("mantis_bug_table");
		valueSet.addInsertValue("summary", name);
		valueSet.addEqualCondition("id", Long.toString(issue.getIssueID()));

		String updateQuery = valueSet.getUpdateQuery();

		getControl().execute(updateQuery);

		// 新增歷史記錄
		HistoryObject nameHistory = new HistoryObject(
				issue.getIssueID(), 
				issue.getIssueType(), 
				HistoryObject.TYPE_NAME,
				oldSummary,
				name,
				modifyDate.getTime());
		nameHistory.save();
	}

	@Override
	public void changeStatusToClosed(long issueID, int resolution,
			String bugNote, Date closeDate) {
		IIssue issue = getIssue(issueID);
		int oldStatus = issue.getStatusValue();

		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("mantis_bug_table");
		valueSet.addInsertValue("status",
				Integer.toString(ITSEnum.CLOSED_STATUS));
		valueSet.addInsertValue("resolution", Integer.toString(resolution));
		valueSet.addEqualCondition("id", Long.toString(issueID));

		String query = valueSet.getUpdateQuery();

		getControl().execute(query);

		// 新增歷史記錄,還有一個resolution的history,因為不是很重要,就暫時沒加入
		HistoryObject statusHistory = new HistoryObject(
				issue.getIssueID(), 
				issue.getIssueType(), 
				HistoryObject.TYPE_STATUS,
				String.valueOf(oldStatus),
				String.valueOf(ITSEnum.CLOSED_STATUS),
				System.currentTimeMillis());
		statusHistory.save();
		if (bugNote != null && !bugNote.equals("")) {
			issue.addIssueNote(bugNote);
			updateBugNote(issue);
		}
	}

	public void insertBugNote(long issueId, String note) {
		IIssue issue = getIssue(issueId);
		mNoteService.insertBugNote(issueId, note);
		HistoryObject statusHistory = new HistoryObject(
				issueId,
				issue.getIssueType(),
				HistoryObject.TYPE_STATUS,
				String.valueOf(ITSEnum.NEW_STATUS),
				String.valueOf(ITSEnum.ASSIGNED_STATUS),
				System.currentTimeMillis());
		statusHistory.save();
	}

	@Override
	public void reopenStatusToAssigned(long issueID, String name,
			String bugNote, Date reopenDate) {
		IIssue issue = getIssue(issueID);
		int oldStatus = issue.getStatusValue();

		// String updateQuery = "UPDATE `mantis_bug_table` SET `status` = '"
		// + ITSEnum.ASSIGNED_STATUS + "', `resolution` = '"
		// + ITSEnum.OPEN_RESOLUTION
		// + "' WHERE `mantis_bug_table`.`id` =" + issueID;

		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("mantis_bug_table");
		valueSet.addInsertValue("summary", name);
		valueSet.addInsertValue("status",
				Integer.toString(ITSEnum.ASSIGNED_STATUS));
		valueSet.addInsertValue("resolution",
				Integer.toString(ITSEnum.OPEN_RESOLUTION));
		valueSet.addEqualCondition("id", Long.toString(issueID));
		String query = valueSet.getUpdateQuery();

		getControl().execute(query);
		// 新增歷史記錄,還有一個resolution的history,因為不是很重要,就暫時沒加入
		HistoryObject statusHistory = new HistoryObject(
				issue.getIssueID(), 
				issue.getIssueType(), 
				HistoryObject.TYPE_STATUS,
				String.valueOf(oldStatus),
				String.valueOf(ITSEnum.ASSIGNED_STATUS),
				reopenDate.getTime());
		statusHistory.save();
		if (bugNote != null && !bugNote.equals("")) {
			Element history = new Element(ScrumEnum.HISTORY_TAG);
			history.setAttribute(ScrumEnum.ID_HISTORY_ATTR,
					DateUtil.format(new Date(), DateUtil._16DIGIT_DATE_TIME_2));

			Element notesElem = new Element(ScrumEnum.NOTES);
			notesElem.setText(bugNote);
			history.addContent(notesElem);

			if (history.getChildren().size() > 0) {
				issue.addTagValue(history);
				// 最後將修改的結果更新至DB
				updateBugNote(issue);
			}
		}

	}

	@Override
	public void resetStatusToNew(long issueId, String name, String bugNote,
			Date resetDate) {
		IIssue issue = getIssue(issueId);
		int oldStatus = issue.getStatusValue();

		// String updateQuery = "UPDATE `mantis_bug_table` SET `status` = '"
		// + ITSEnum.NEW_STATUS + "', `resolution` = '"
		// + ITSEnum.OPEN_RESOLUTION
		// + "', `handler_id` = '0' WHERE `mantis_bug_table`.`id` ="
		// + issueID;
		TranslateSpecialChar translateChar = new TranslateSpecialChar();// accept
																		// special
																		// char
																		// ex: /
																		// '
		name = translateChar.TranslateDBChar(name);

		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("mantis_bug_table");
		valueSet.addInsertValue("summary", name);
		valueSet.addInsertValue("status", Integer.toString(ITSEnum.NEW_STATUS));
		valueSet.addInsertValue("resolution",
				Integer.toString(ITSEnum.OPEN_RESOLUTION));
		valueSet.addInsertValue("handler_id", "0");
		valueSet.addEqualCondition("id", Long.toString(issueId));
		String query = valueSet.getUpdateQuery();

		getControl().execute(query);

		// 新增歷史記錄,還有一個 resolution 的 history ,因為不是很重要,就暫時沒加入
		HistoryObject statusHistory = new HistoryObject(
				issue.getIssueID(), 
				issue.getIssueType(), 
				HistoryObject.TYPE_STATUS,
				String.valueOf(oldStatus),
				String.valueOf(ITSEnum.NEW_STATUS),
				resetDate.getTime());
		statusHistory.save();
		if (bugNote != null && !bugNote.equals("")) {
			Element history = new Element(ScrumEnum.HISTORY_TAG);
			history.setAttribute(ScrumEnum.ID_HISTORY_ATTR,
					DateUtil.format(new Date(), DateUtil._16DIGIT_DATE_TIME_2));

			Element notesElem = new Element(ScrumEnum.NOTES);
			notesElem.setText(bugNote);
			history.addContent(notesElem);

			if (history.getChildren().size() > 0) {
				issue.addTagValue(history);
				// 最後將修改的結果更新至DB
				updateBugNote(issue);
			}
		}
	}

//	public void updateHistoryModifiedDate(long issueID, long historyID,
//			Date date) {
//		if (historyID < Long.parseLong("20000000000000")) {
//			mHistoryService.updateHistoryModifiedDate(historyID, date);
//		} else {
//			mNoteService.updateHistoryModifiedDate(this.getIssue(issueID),
//					historyID, date);
//		}
//	}

	@Override
	public void updateIssueContent(IIssue modifiedIssue) {
		mIssueService.updateIssueContent(modifiedIssue);
		// m_noteService.insertBugNote(modifiedIssue.getIssueID(), modifiedIssue
		// .getIssueNotes().get(0).getText());
		// 未修改與修改後的issue比較,更新history
	}

	public void removeIssue(String ID) {
		IIssue issue = getIssue(Long.parseLong(ID));
		
		// 刪除retrospective issue，分別砍掉issue與note的資料檔
		mIssueService.removeIssue(ID);
		mNoteService.removeNote(ID);
		
		HistoryDAO historyDao = HistoryDAO.getInstance();
		historyDao.deleteByIssue(Long.parseLong(ID), issue.getIssueType());

		// 刪除此Story與其他Sprint or Release的關係
		deleteStoryRelationTable(ID);
	}

	// 刪除story
	public void deleteStory(String ID) {
		IIssue issue = getIssue(Long.parseLong(ID));
		if (issue.getCategory().compareTo(ScrumEnum.STORY_ISSUE_TYPE) == 0) {
			IQueryValueSet valueSet = new MySQLQuerySet();
			valueSet.addTableName("mantis_bug_table");
			valueSet.addEqualCondition("id", ID);
			String query = valueSet.getDeleteQuery();
			getControl().execute(query);

			// 清除StoryRelationTable中有關Stroy的資料
			valueSet.clear();
			valueSet.addTableName("ezscrum_story_relation");
			valueSet.addEqualCondition("storyID", ID);
			query = valueSet.getDeleteQuery();
			getControl().execute(query);
			
			// 刪除History
			HistoryDAO historyDao = HistoryDAO.getInstance();
			historyDao.deleteByIssue(Long.parseLong(ID), issue.getIssueType());
		}
	}

	// 刪除 task
	public void deleteTask(long taskID) {
		IIssue issue = getIssue(taskID);
		
		// delete task，分別砍掉issue與history的資料檔
		String bug_text_id = mIssueService.getBugTextId(taskID);
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("mantis_bug_text_table");
		valueSet.addEqualCondition("id", bug_text_id);
		String query = valueSet.getDeleteQuery();
		getControl().execute(query);
		mIssueService.removeIssue(Long.toString(taskID));
		
		HistoryDAO historyDao = HistoryDAO.getInstance();
		historyDao.deleteByIssue(taskID, issue.getIssueType());
	}

	// 刪除 story 和 task 的關係
	public void deleteRelationship(long storyId, long taskId) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("mantis_bug_relationship_table");
		valueSet.addEqualCondition("source_bug_id", Long.toString(storyId));
		valueSet.addEqualCondition("destination_bug_id", Long.toString(taskId));
		String query = valueSet.getDeleteQuery();
		getControl().execute(query);
	}

	// ezScrum上面新增帳號，新增進mySQL裡
	public void addUser(String name, String password, String email,
			String realName, String access_Level, String cookie_string,
			String createDate, String lastVisitDate) throws Exception {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("mantis_user_table");
		valueSet.addLikeCondition("username", name);
		try {
			String query = valueSet.getSelectQuery();
			ResultSet resultSet = getControl().executeQuery(query);
			valueSet.clear();
			// resultSet.first 如果回傳false代表set內是空的
			if (!resultSet.first()) {
				valueSet.addTableName("mantis_user_table");
				valueSet.addInsertValue(AccountEnum.ACCOUNT_NAME, name);
				valueSet.addInsertValue(AccountEnum.ACCOUNT_EMAIL, email);
				valueSet.addInsertValue(AccountEnum.ACCOUNT_PAASSWORD, password);
				valueSet.addInsertValue(AccountEnum.ACCOUNT_REALNAME, realName);
				valueSet.addInsertValue(AccountEnum.ACCOUNT_ACCESS_LEVEL,
						access_Level);
				valueSet.addInsertValue(AccountEnum.ACCOUNT_COOKIE_STRING,
						cookie_string);
				valueSet.addInsertValue(AccountEnum.ACCOUNT_DATE_CREATED,
						createDate);
				valueSet.addInsertValue(AccountEnum.ACCOUNT_LAST_VISIT,
						lastVisitDate);

				query = valueSet.getInsertQuery();
				getControl().execute(query);
			}

		} catch (Exception e) {
			throw e;
		}
	}

	// 是否存在這個使用者
	public boolean existUser(String name) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("mantis_user_table");
		valueSet.addLikeCondition("username", name);
		valueSet.addLikeCondition("enabled", "1");

		try {
			String query = valueSet.getSelectQuery();
			ResultSet resultSet = getControl().executeQuery(query);
			// resultSet.first 如果回傳false代表set內是空的
			if (!resultSet.first()) {
				return Boolean.FALSE;
			} else {
				return Boolean.TRUE;
			}
		} catch (Exception e) {
			return Boolean.FALSE;
		}
	}

	// 新增User和Project之間的關係，新增到MySql裡
	public void addUserProjectRelation(String projectName, String name,
			String access_Level) throws Exception {
		IQueryValueSet valueSet = new MySQLQuerySet();

		try {
			// 取得User的ID
			valueSet.addTableName("mantis_user_table");
			valueSet.addLikeCondition("username", name);
			String query = valueSet.getSelectQuery();
			ResultSet resultSet = getControl().executeQuery(query);
			resultSet.first();
			String userID = resultSet.getString(AccountEnum.ACCOUNT_ID);
			resultSet.close();

			// 取得Project的ID
			valueSet.clear();
			valueSet.addTableName("mantis_project_table");
			valueSet.addLikeCondition("name", projectName);
			query = valueSet.getSelectQuery();
			resultSet = getControl().executeQuery(query);
			resultSet.first();
			String projectID = resultSet.getString(AccountEnum.ACCOUNT_ID);

			// 新增使用者跟Project的Relation
			valueSet.clear();
			valueSet.addTableName("mantis_project_user_list_table");
			valueSet.addInsertValue("project_id", projectID);
			valueSet.addInsertValue("user_id", userID);
			valueSet.addInsertValue("access_level", access_Level);
			query = valueSet.getInsertQuery();
			getControl().execute(query);
		} catch (Exception e) {
			throw e;
		}
	}

	/*
	 * Jcis上面刪除帳號，刪除mySQL裡的資料
	 * 
	 * 警告:欲刪除使用者帳號 請先刪除User和Project之間的關係 否則會造成找不到User_id而無法砍除User和Project之間的關係
	 */
	public void deleteUser(String userid) throws Exception {
		IQueryValueSet valueSet = new MySQLQuerySet();
		try {
			valueSet.addTableName("mantis_user_table");
			valueSet.addLikeCondition("username", userid);
			String query = valueSet.getDeleteQuery();
			getControl().execute(query);

		} catch (Exception e) {
			throw e;
		}
	}

	// 刪除User和Project之間的關係，刪除MySql裡的資料
	public void deleteUserProjectRelation(String userName, String projectName)
			throws Exception {
		IQueryValueSet valueSet = new MySQLQuerySet();

		try {
			String projectId = "";
			String userId = "";

			// 取得userId
			valueSet.addTableName("mantis_user_table");
			valueSet.addLikeCondition("username", userName);
			String query = valueSet.getSelectQuery();
			ResultSet resultSet = getControl().executeQuery(query);
			if (resultSet.first())
				userId = resultSet.getString(AccountEnum.ACCOUNT_ID);

			// 取得projectId
			valueSet.clear();
			valueSet.addTableName("mantis_project_table");
			valueSet.addLikeCondition("name", projectName);
			query = valueSet.getSelectQuery();
			resultSet = getControl().executeQuery(query);
			if (resultSet.first())
				projectId = resultSet.getString(AccountEnum.ACCOUNT_ID);

			// 刪除user跟project之間的關係
			valueSet.clear();
			valueSet.addTableName("mantis_project_user_list_table");
			valueSet.addLikeCondition("project_id", projectId);
			valueSet.addLikeCondition("user_id", userId);
			query = valueSet.getDeleteQuery();
			getControl().execute(query);

		} catch (Exception e) {
			throw e;
		}
	}

	// 刪除Project所有屬於access_level的使用者
	public void deleteUserProjectRelationByAccessLevel(String projectName,
			String access_level) throws Exception {
		IQueryValueSet valueSet = new MySQLQuerySet();

		try {
			String projectId = "";

			// 取得projectId
			valueSet.clear();
			valueSet.addTableName("mantis_project_table");
			valueSet.addLikeCondition("name", projectName);
			String query = valueSet.getSelectQuery();
			ResultSet resultSet = getControl().executeQuery(query);

			if (resultSet.first())
				projectId = resultSet.getString(AccountEnum.ACCOUNT_ID);

			// 刪除user跟project之間的關係
			valueSet.clear();
			valueSet.addTableName("mantis_project_user_list_table");
			valueSet.addLikeCondition("project_id", projectId);
			// access_level不同，代表的值也不同
			if (access_level.compareTo(AccountEnum.ACCOUNT_ACTOR_ADMIN) == 0)
				valueSet.addLikeCondition("access_level",
						AccountEnum.ACCESS_LEVEL_MANAGER);
			else
				valueSet.addLikeCondition("access_level",
						AccountEnum.ACCESS_LEVEL_VIEWER);

			query = valueSet.getDeleteQuery();
			getControl().execute(query);

		} catch (Exception e) {
			throw new Exception("刪除Mantis權限失敗!");
		}
	}

	// 回傳是否User和任何一個Project有關聯
	public boolean isUserHasRelationByAnyProject(String userName)
			throws Exception {
		IQueryValueSet valueSet = new MySQLQuerySet();

		try {
			// 取得User的ID
			valueSet.addTableName("mantis_user_table");
			valueSet.addLikeCondition("username", userName);
			String query = valueSet.getSelectQuery();
			ResultSet resultSet = getControl().executeQuery(query);
			resultSet.first();
			String userID = resultSet.getString(AccountEnum.ACCOUNT_ID);
			resultSet.close();

			// 新增使用者跟Project的Relation
			valueSet.clear();
			valueSet.addTableName("mantis_project_user_list_table");
			valueSet.addEqualCondition("user_id", userID);
			query = valueSet.getSelectQuery();
			resultSet = getControl().executeQuery(query);
			if (resultSet.first())
				return true;
		} catch (Exception e) {
			throw e;
		}

		return false;
	}

	// 更新user的資料
	public void updateUserProfile(String userID, String realName,
			String password, String email, String enable) throws Exception {
		try {
			IQueryValueSet valueSet = new MySQLQuerySet();
			valueSet.addTableName("mantis_user_table");
			valueSet.addLikeCondition("username", userID);

			valueSet.addInsertValue(AccountEnum.ACCOUNT_EMAIL, email);
			valueSet.addInsertValue(AccountEnum.ACCOUNT_PAASSWORD, password);
			valueSet.addInsertValue(AccountEnum.ACCOUNT_REALNAME, realName);
			valueSet.addInsertValue(AccountEnum.ACCOUNT_ENABLED, enable);
			String query = valueSet.getUpdateQuery();
			getControl().execute(query);

		} catch (Exception e) {
			throw new Exception("更新Mantis資料發生錯誤");
		}
	}

	public void createProject(String ProjectName) throws Exception {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("mantis_project_table");

		valueSet.addInsertValue("name", ProjectName);
		valueSet.addInsertValue("status", ScrumEnum.PROJECT_STATUS);
		valueSet.addInsertValue("enabled", ScrumEnum.PROJECT_ENABLED);
		valueSet.addInsertValue("view_state", ScrumEnum.PROJECT_VIEW_STATE);
		valueSet.addInsertValue("access_min", ScrumEnum.PROJECT_ACCESS_MIN);
		valueSet.addInsertValue("file_path", "");
		valueSet.addInsertValue("description", "");

		String query = valueSet.getInsertQuery();
		try {
			getControl().execute(query);
		} catch (Exception e) {
			throw e;
		}
	}

	public void updateStoryRelationTable(long storyId, String projectName,
			String releaseId, String sprintId, String estimation,
			String importance, Date date) {

		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("ezscrum_story_relation");

		valueSet.addInsertValue("storyID", Long.toString(storyId));
		int projectId = getProjectID(projectName);

		valueSet.addInsertValue("projectID", Integer.toString(projectId));

		if (releaseId != null)
			valueSet.addInsertValue("releaseID", releaseId);

		if (sprintId != null)
			valueSet.addInsertValue("sprintID", sprintId);

		if (estimation != null)
			valueSet.addInsertValue("estimation", estimation);

		if (importance != null)
			valueSet.addInsertValue("importance", estimation);

		// 取得時間
		Timestamp now = new Timestamp(new Date().getTime());

		valueSet.addInsertValue("updateDate", now.toString());
		String query = valueSet.getInsertQuery();

		getControl().execute(query);
	}

	public void deleteStoryRelationTable(String storyID) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("ezscrum_story_relation");
		valueSet.addEqualCondition("storyID", storyID);
		String query = valueSet.getDeleteQuery();
		getControl().execute(query);
	}

	/**
	 * for ezScrum v1.8
	 */
	public long addAttachFile(AttachFileInfo attachFileInfo) {
		// builder
		AttachFileObject.Builder attachFileBuilder = new AttachFileObject.Builder();
		attachFileBuilder.setIssueId(attachFileInfo.issueId);
		attachFileBuilder.setIssueType(attachFileInfo.issueType);
		attachFileBuilder.setContentType(attachFileInfo.contentType);
		attachFileBuilder.setName(attachFileInfo.name);
		attachFileBuilder.setPath(attachFileInfo.path);
		
		// create AttachFileObject
		AttachFileObject attachFile = attachFileBuilder.build();
		long newAttachFileId = AttachFileDAO.getInstance().create(attachFile);
		return newAttachFileId;
	}
	
	/**
	 * for ezScrum v1.8
	 */
	public void deleteAttachFile(long fileId) {
		AttachFileDAO.getInstance().delete(fileId);
	}
	
	public AttachFileObject getAttachFile(long fileId) {
		return AttachFileDAO.getInstance().get(fileId);
	}
	
	@Override
    public long addNewTag(String name, String projectName) {
	    return TagDAO.getInstance().create(new TagObject(name, ProjectObject.get(projectName).getId()));
    }

	@Override
    public void deleteTag(long id, String projectName) {
	    // TODO Auto-generated method stub
    }

	@Override
    public ArrayList<TagObject> getTagList(String projectName) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public void addStoryTag(String storyID, long tagID) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void removeStoryTag(String storyID, long tagID) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public List<IStory> getStorys(String name) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public void updateTag(long tagId, String tagName, String projectName) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public boolean isTagExist(String name, String projectName) {
	    // TODO Auto-generated method stub
	    return false;
    }

	@Override
    public TagObject getTagByName(String name, String projectName) {
	    // TODO Auto-generated method stub
	    return null;
    }
}
