package ntut.csie.ezScrum.web.dataObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ntut.csie.ezScrum.dao.HistoryDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.web.databaseEnum.HistoryEnum;

public class HistoryObjectTest {
	private MySQLControl mControl = null;
	private Configuration mConfig;

	@Before
	public void setUp() {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		mControl = new MySQLControl(mConfig);
		mControl.connect();
	}

	@After
	public void tearDown() {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		// 讓 config 回到  Production 模式
		mConfig.setTestMode(false);
		mConfig.save();

		ini = null;
		mControl = null;
		mConfig = null;
	}

	@Test
	public void testGetFormattedModifiedTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2015, 8, 25, 17, 1, 2);
		long time = calendar.getTime().getTime();
		HistoryObject history = new HistoryObject();
		history.setCreateTime(time);
		assertEquals("2015/09/25-17:01:02", history.getFormattedModifiedTime());
	}

	@Test
	public void testExists() {
		HistoryObject history = new HistoryObject();
		history.setIssueId(10)
		       .setIssueType(1)
		       .setHistoryType(5)
		       .setNewValue("15")
		       .setOldValue("10")
		       .save();
		assertTrue(history.exists());
	}

	@Test
	public void testSave_CreateANewAttachFile() {
		// Test Data
		long issueId = 10;
		int issueType = 1;
		int historyType = 5;
		String oldValue = "10";
		String newValue = "15";
		
		// Build HistoryObject
		HistoryObject history = new HistoryObject();
		history.setIssueId(issueId)
		       .setIssueType(issueType)
		       .setHistoryType(historyType)
		       .setNewValue(newValue)
		       .setOldValue(oldValue)
		       .save();

		long id = history.getId();
		assertTrue(id > 0);

		HistoryObject historyFromDB = HistoryDAO.getInstance().get(id);

		assertEquals(id, historyFromDB.getId());
		assertEquals(issueId, historyFromDB.getIssueId());
		assertEquals(issueType, historyFromDB.getIssueType());
		assertEquals(historyType, historyFromDB.getHistoryType());
		assertEquals(oldValue, historyFromDB.getOldValue());
		assertEquals(newValue, historyFromDB.getNewValue());
	}

	@Test
	public void testReload() {
		// Test Data
		long issueId = 10;
		int issueType = 1;
		int historyType = 5;
		String oldValue = "10";
		String newValue = "15";

		// Build HistoryObject
		HistoryObject history = new HistoryObject();
		history.setIssueId(issueId)
		        .setIssueType(issueType)
		        .setHistoryType(historyType)
		        .setNewValue(newValue)
		        .setOldValue(oldValue)
		        .save();

		long id = history.getId();
		assertTrue(id > 0);

		assertEquals(id, history.getId());
		assertEquals(issueId, history.getIssueId());
		assertEquals(issueType, history.getIssueType());
		assertEquals(historyType, history.getHistoryType());
		assertEquals(oldValue, history.getOldValue());
		assertEquals(newValue, history.getNewValue());
		
		long issueId2 = 11;
		int issueType2 = 2;
		int historyType2 = 8;
		String oldValue2 = "11";
		String newValue2 = "12";
		
		history.setIssueId(issueId2)
		        .setIssueType(issueType2)
		        .setHistoryType(historyType2)
		        .setNewValue(newValue2)
		        .setOldValue(oldValue2);
		
		assertEquals(id, history.getId());
		assertEquals(issueId2, history.getIssueId());
		assertEquals(issueType2, history.getIssueType());
		assertEquals(historyType2, history.getHistoryType());
		assertEquals(oldValue2, history.getOldValue());
		assertEquals(newValue2, history.getNewValue());
		
		history.reload();
		assertEquals(id, history.getId());
		assertEquals(issueId, history.getIssueId());
		assertEquals(issueType, history.getIssueType());
		assertEquals(historyType, history.getHistoryType());
		assertEquals(oldValue, history.getOldValue());
		assertEquals(newValue, history.getNewValue());
	}

	@Test
	public void testToJSON() throws JSONException {
		// Test Data
		long issueId = 10;
		int issueType = 1;
		int historyType = 5;
		String oldValue = "10";
		String newValue = "15";

		// Build HistoryObject
		HistoryObject history = new HistoryObject();
		history.setIssueId(issueId)
		        .setIssueType(issueType)
		        .setHistoryType(historyType)
		        .setNewValue(newValue)
		        .setOldValue(oldValue)
		        .save();
		
		JSONObject historyJson = history.toJSON();
		assertEquals(history.getId(), historyJson.getLong(HistoryEnum.ID));
		assertEquals(history.getIssueId(), historyJson.getLong(HistoryEnum.ISSUE_ID));
		assertEquals(history.getIssueType(), historyJson.getInt(HistoryEnum.ISSUE_TYPE));
		assertEquals(history.getHistoryType(), historyJson.getInt(HistoryEnum.HISTORY_TYPE));
		assertEquals(history.getDescription(), historyJson.getString(HistoryEnum.DESCRIPTION));
		assertEquals(history.getCreateTime(), historyJson.getLong(HistoryEnum.CREATE_TIME));
	}
}
