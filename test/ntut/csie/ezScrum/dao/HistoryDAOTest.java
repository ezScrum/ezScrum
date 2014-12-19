package ntut.csie.ezScrum.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.databasEnum.HistoryEnum;
import ntut.csie.ezScrum.web.databasEnum.IssueTypeEnum;

public class HistoryDAOTest extends TestCase {
	private Configuration mConfig;
	private CreateProject mCreateProject;
	private int mProjectCount = 1;
	private HistoryDAO mHistoryDao = null;
	private MySQLControl mControl = null;

	public HistoryDAOTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.store();

		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		mCreateProject = new CreateProject(mProjectCount);
		mCreateProject.exeCreate();

		mHistoryDao = HistoryDAO.getInstance();
		mControl = new MySQLControl(mConfig);
		mControl.connection();

		super.setUp();
	}

	protected void tearDown() throws Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(mConfig.getDataPath());

		mConfig.setTestMode(false);
		mConfig.store();

		// ============= release ==============
		ini = null;
		mCreateProject = null;
		mConfig = null;
		mHistoryDao = null;
		mControl = null;

		super.tearDown();
	}

	public void testAdd() {
		String oldValue = "1";
		String newValue = "0";
		long modifiedTime = System.currentTimeMillis();

		HistoryObject history = new HistoryObject();
		history.setHistoryType(HistoryObject.TYPE_IMPORTANCE)
				.setNewValue(oldValue).setOldValue(newValue)
				.setModifiedTime(modifiedTime)
				.setIssueType(IssueTypeEnum.TYPE_TASK).setIssueId(1);

		long id = mHistoryDao.create(history);

		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(HistoryEnum.TABLE_NAME);
		valueSet.addEqualCondition(HistoryEnum.ISSUE_TYPE,
				IssueTypeEnum.TYPE_TASK);
		valueSet.addEqualCondition(HistoryEnum.ISSUE_ID, 1);
		String query = valueSet.getSelectQuery();

		ResultSet result = mControl.executeQuery(query);
		try {
			if (result.next()) {
				assertEquals(history.getHistoryType(),
						result.getInt(HistoryEnum.HISTORY_TYPE));
				assertEquals(history.getIssueType(),
						result.getInt(HistoryEnum.ISSUE_TYPE));
				assertEquals(history.getIssueId(),
						result.getLong(HistoryEnum.ISSUE_ID));
				assertEquals(history.getNewValue(),
						result.getString(HistoryEnum.NEW_VALUE));
				assertEquals(history.getOldValue(),
						result.getString(HistoryEnum.OLD_VALUE));
				assertEquals(history.getModifiedTime(),
						result.getLong(HistoryEnum.MODIFIED_TIME));
				assertEquals(id, result.getLong(HistoryEnum.ID));
				assertEquals(history.getNewValue(),
						result.getString(HistoryEnum.NEW_VALUE));
			}
		} catch (SQLException e) {
			assertFalse(true);
		}
	}

	public void testGet() throws SQLException {
		long ISSUE_ID = 1;
		ArrayList<Long> idList = new ArrayList<Long>();

		for (int i = 0; i < 5; i++) {
			IQueryValueSet valueSet = new MySQLQuerySet();
			valueSet.addTableName(HistoryEnum.TABLE_NAME);
			valueSet.addInsertValue(HistoryEnum.ISSUE_ID, ISSUE_ID);
			valueSet.addInsertValue(HistoryEnum.ISSUE_TYPE,
					IssueTypeEnum.TYPE_STORY);
			valueSet.addInsertValue(HistoryEnum.HISTORY_TYPE,
					HistoryObject.TYPE_ESTIMATE);
			valueSet.addInsertValue(HistoryEnum.OLD_VALUE, i);
			valueSet.addInsertValue(HistoryEnum.NEW_VALUE, (i + 1));
			valueSet.addInsertValue(HistoryEnum.MODIFIED_TIME,
					System.currentTimeMillis());
			String query = valueSet.getInsertQuery();
			mControl.execute(query, true);

			String[] keys = mControl.getKeys();
			long id = Long.parseLong(keys[0]);

			idList.add(id);
		}

		for (int i = 0; i < 5; i++) {
			HistoryObject history = mHistoryDao.get(idList.get(i));

			assertEquals(HistoryObject.TYPE_ESTIMATE, history.getHistoryType());
			assertEquals(IssueTypeEnum.TYPE_STORY, history.getIssueType());
			assertEquals(ISSUE_ID, history.getIssueId());
			assertEquals(String.valueOf((i + 1)), history.getNewValue());
			assertEquals(String.valueOf(i), history.getOldValue());
			assertEquals(idList.get(i).longValue(), history.getId());
		}
	}

	public void testGetHistoriesByIssue() throws SQLException {
		long ISSUE_ID = 1;

		for (int i = 0; i < 5; i++) {
			IQueryValueSet valueSet = new MySQLQuerySet();
			valueSet.addTableName(HistoryEnum.TABLE_NAME);
			valueSet.addInsertValue(HistoryEnum.ISSUE_ID, ISSUE_ID);
			valueSet.addInsertValue(HistoryEnum.ISSUE_TYPE,
					IssueTypeEnum.TYPE_STORY);
			valueSet.addInsertValue(HistoryEnum.HISTORY_TYPE,
					HistoryObject.TYPE_ESTIMATE);
			valueSet.addInsertValue(HistoryEnum.OLD_VALUE, i);
			valueSet.addInsertValue(HistoryEnum.NEW_VALUE, (i + 1));
			valueSet.addInsertValue(HistoryEnum.MODIFIED_TIME,
					System.currentTimeMillis());
			String query = valueSet.getInsertQuery();
			mControl.execute(query, true);
		}

		ArrayList<HistoryObject> histories = mHistoryDao.getHistoriesByIssue(
				ISSUE_ID, IssueTypeEnum.TYPE_STORY);
		
		assertEquals(5, histories.size());

		for (int i = 0; i < 5; i++) {
			HistoryObject history = histories.get(i);
			assertEquals(HistoryObject.TYPE_ESTIMATE, history.getHistoryType());
			assertEquals(IssueTypeEnum.TYPE_STORY, history.getIssueType());
			assertEquals(ISSUE_ID, history.getIssueId());
			assertEquals(String.valueOf((i + 1)), history.getNewValue());
			assertEquals(String.valueOf(i), history.getOldValue());
		}

		// assert 不存在的 history
		histories = mHistoryDao.getHistoriesByIssue(ISSUE_ID,
				IssueTypeEnum.TYPE_TASK);
		assertEquals(0, histories.size());
	}

	public void testDelete() throws SQLException {
		long ISSUE_ID = 1;
		ArrayList<Long> idList = new ArrayList<Long>();

		for (int i = 0; i < 5; i++) {
			IQueryValueSet valueSet = new MySQLQuerySet();
			valueSet.addTableName(HistoryEnum.TABLE_NAME);
			valueSet.addInsertValue(HistoryEnum.ISSUE_ID, ISSUE_ID);
			valueSet.addInsertValue(HistoryEnum.ISSUE_TYPE,
					IssueTypeEnum.TYPE_STORY);
			valueSet.addInsertValue(HistoryEnum.HISTORY_TYPE,
					HistoryObject.TYPE_ESTIMATE);
			valueSet.addInsertValue(HistoryEnum.OLD_VALUE, i);
			valueSet.addInsertValue(HistoryEnum.NEW_VALUE, (i + 1));
			valueSet.addInsertValue(HistoryEnum.MODIFIED_TIME,
					System.currentTimeMillis());
			String query = valueSet.getInsertQuery();
			mControl.execute(query, true);

			String[] keys = mControl.getKeys();
			long id = Long.parseLong(keys[0]);

			idList.add(id);
		}

		assertEquals(
				5,
				mHistoryDao.getHistoriesByIssue(ISSUE_ID,
						IssueTypeEnum.TYPE_STORY).size());

		for (long id : idList) {
			mHistoryDao.delete(id);
		}

		assertEquals(
				0,
				mHistoryDao.getHistoriesByIssue(ISSUE_ID,
						IssueTypeEnum.TYPE_STORY).size());
	}

	public void testDeleteByIssue() throws SQLException {
		long ISSUE_ID = 1;
		ArrayList<Long> idList = new ArrayList<Long>();

		for (int i = 0; i < 5; i++) {
			IQueryValueSet valueSet = new MySQLQuerySet();
			valueSet.addTableName(HistoryEnum.TABLE_NAME);
			valueSet.addInsertValue(HistoryEnum.ISSUE_ID, ISSUE_ID);
			valueSet.addInsertValue(HistoryEnum.ISSUE_TYPE,
					IssueTypeEnum.TYPE_STORY);
			valueSet.addInsertValue(HistoryEnum.HISTORY_TYPE,
					HistoryObject.TYPE_ESTIMATE);
			valueSet.addInsertValue(HistoryEnum.OLD_VALUE, i);
			valueSet.addInsertValue(HistoryEnum.NEW_VALUE, (i + 1));
			valueSet.addInsertValue(HistoryEnum.MODIFIED_TIME,
					System.currentTimeMillis());
			String query = valueSet.getInsertQuery();
			mControl.execute(query, true);

			String[] keys = mControl.getKeys();
			long id = Long.parseLong(keys[0]);

			idList.add(id);
		}

		assertEquals(
				5,
				mHistoryDao.getHistoriesByIssue(ISSUE_ID,
						IssueTypeEnum.TYPE_STORY).size());

		mHistoryDao
				.deleteByIssue(ISSUE_ID, IssueTypeEnum.TYPE_STORY);

		assertEquals(
				0,
				mHistoryDao.getHistoriesByIssue(ISSUE_ID,
						IssueTypeEnum.TYPE_STORY).size());
	}
}
