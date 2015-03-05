package ntut.csie.ezScrum.dao;

import static org.junit.Assert.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HistoryDAOTest {
	private Configuration mConfig;
	private CreateProject mCP;
	private int mProjectCount = 1;
	private HistoryDAO mHistoryDao = null;
	private MySQLControl mControl = null;

	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		mCP = new CreateProject(mProjectCount);
		mCP.exeCreate();

		mHistoryDao = HistoryDAO.getInstance();
		mControl = new MySQLControl(mConfig);
		mControl.connect();
	}

	@After
	public void tearDown() throws Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		mConfig.setTestMode(false);
		mConfig.save();

		// ============= release ==============
		ini = null;
		mCP = null;
		mConfig = null;
		mHistoryDao = null;
		mControl = null;
	}

	@Test
	public void testAdd() {
		String oldValue = "1";
		String newValue = "0";
		long modifiedTime = System.currentTimeMillis();

		HistoryObject history = new HistoryObject();
		history.setHistoryType(HistoryObject.TYPE_IMPORTANCE)
				.setNewValue(oldValue).setOldValue(newValue)
				.setCreateTime(modifiedTime)
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
				assertEquals(history.getCreateTime(),
						result.getLong(HistoryEnum.CREATE_TIME));
				assertEquals(id, result.getLong(HistoryEnum.ID));
				assertEquals(history.getNewValue(),
						result.getString(HistoryEnum.NEW_VALUE));
			}
		} catch (SQLException e) {
			assertFalse(true);
		}
	}

	@Test
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
			valueSet.addInsertValue(HistoryEnum.CREATE_TIME,
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

	@Test
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
			valueSet.addInsertValue(HistoryEnum.CREATE_TIME,
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

	@Test
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
			valueSet.addInsertValue(HistoryEnum.CREATE_TIME,
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

	@Test
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
			valueSet.addInsertValue(HistoryEnum.CREATE_TIME,
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
