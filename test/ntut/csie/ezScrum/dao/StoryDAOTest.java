package ntut.csie.ezScrum.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
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
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.databasEnum.StoryEnum;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StoryDAOTest {
	private MySQLControl mControl = null;
	private Configuration mConfig;
	private CreateProject mCP;
	private int mProjectCount = 2;
	private static long sProjectId;

	@Before
	public void setUp() {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		mCP = new CreateProject(mProjectCount);
		mCP.exeCreate();

		mControl = new MySQLControl(mConfig);
		mControl.connect();

		sProjectId = mCP.getAllProjects().get(0).getId();
	}

	@After
	public void tearDown() {
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
		mControl = null;
	}

	@Test
	public void testCreate() throws SQLException {
		// create three test data
		for (int i = 0; i < 3; i++) {
			StoryObject story = new StoryObject(sProjectId);
			story.setName("TEST_STORY_" + i + 1)
					.setNotes("TEST_NOTE_" + i + 1)
					.setEstimate(i * 2)
					.setImportance(i * 2)
					.setValue(i * 2);
			long storyId = StoryDAO.getInstance().create(story);
			assertNotSame(-1, storyId);
		}

		// 從 DB 裡取出 story 資料
		ArrayList<StoryObject> stories = new ArrayList<StoryObject>();
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(StoryEnum.TABLE_NAME);
		valueSet.addEqualCondition(StoryEnum.PROJECT_ID, sProjectId);
		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);
		while (result.next()) {
			stories.add(StoryDAO.convert(result));
		}
		closeResultSet(result);

		assertEquals(3, stories.size());
		for (int i = 0; i < 3; i++) {
			assertEquals(i + 1, stories.get(i).getId());
			assertEquals(i + 1, stories.get(i).getSerialId());
			assertEquals("TEST_STORY_" + i + 1, stories.get(i).getName());
			assertEquals(StoryObject.STATUS_UNCHECK, stories.get(i).getStatus());
			assertEquals("TEST_NOTE_" + i + 1, stories.get(i).getNotes());
			assertEquals(sProjectId, stories.get(i).getProjectId());
			assertEquals(-1, stories.get(i).getSprintId());
			assertEquals(i * 2, stories.get(i).getEstimate());
			assertEquals(i * 2, stories.get(i).getImportance());
			assertEquals(i * 2, stories.get(i).getValue());
			assertNotNull(stories.get(i).getCreateTime());
			assertNotNull(stories.get(i).getUpdateTime());
		}
	}

	@Test
	public void testGet() {
		// create three test data
		for (int i = 0; i < 3; i++) {
			StoryObject story = new StoryObject(sProjectId);
			story.setName("TEST_STORY_" + i + 1)
					.setNotes("TEST_NOTE_" + i + 1)
					.setEstimate(i * 2)
					.setImportance(i * 2)
					.setValue(i * 2);
			long storyId = StoryDAO.getInstance().create(story);
			assertNotSame(-1, storyId);
		}
		
		// get stories
		ArrayList<StoryObject> stories = new ArrayList<StoryObject>();
		for (int i = 0; i < 3; i++) {
			stories.add(StoryDAO.getInstance().get(i + 1));
		}
		assertEquals(3, stories.size());
		
		for (int i = 0; i < 3; i++) {
			assertEquals(i + 1, stories.get(i).getId());
			assertEquals(i + 1, stories.get(i).getSerialId());
			assertEquals("TEST_STORY_" + i + 1, stories.get(i).getName());
			assertEquals(StoryObject.STATUS_UNCHECK, stories.get(i).getStatus());
			assertEquals("TEST_NOTE_" + i + 1, stories.get(i).getNotes());
			assertEquals(sProjectId, stories.get(i).getProjectId());
			assertEquals(-1, stories.get(i).getSprintId());
			assertEquals(i * 2, stories.get(i).getEstimate());
			assertEquals(i * 2, stories.get(i).getImportance());
			assertEquals(i * 2, stories.get(i).getValue());
			assertNotNull(stories.get(i).getCreateTime());
			assertNotNull(stories.get(i).getUpdateTime());
		}
	}
	
	@Test
	public void testUpdate() {
		StoryObject story = new StoryObject(sProjectId);
		story.setName("TEST_STORY_1")
		        .setNotes("TEST_NOTE_1")
		        .setEstimate(1)
		        .setImportance(2)
		        .setValue(3);
		long storyId = StoryDAO.getInstance().create(story);
		assertNotSame(-1, storyId);

		story = StoryDAO.getInstance().get(storyId);
		story.setName("崩潰惹")
		        .setNotes("含淚寫測試")
		        .setEstimate(8)
		        .setImportance(8)
		        .setValue(8)
		        .setStatus(StoryObject.STATUS_DONE);
		boolean result = StoryDAO.getInstance().update(story);
		assertEquals(true, result);

		StoryObject theStory = StoryDAO.getInstance().get(storyId);
		assertEquals(theStory.getId(), story.getId());
		assertEquals(theStory.getSerialId(), story.getSerialId());
		assertEquals(theStory.getName(), story.getName());
		assertEquals(StoryObject.STATUS_DONE, story.getStatus());
		assertEquals(theStory.getNotes(), story.getNotes());
		assertEquals(theStory.getProjectId(), story.getProjectId());
		assertEquals(theStory.getSprintId(), story.getSprintId());
		assertEquals(theStory.getEstimate(), story.getEstimate());
		assertEquals(theStory.getImportance(), story.getImportance());
		assertEquals(theStory.getValue(), story.getValue());
		assertNotNull(theStory.getCreateTime());
		assertNotNull(theStory.getUpdateTime());
	}
	
	@Test
	public void testDelete() throws SQLException {
		// create three test data
		for (int i = 0; i < 3; i++) {
			StoryObject story = new StoryObject(sProjectId);
			story.setName("TEST_STORY_" + i + 1)
					.setNotes("TEST_NOTE_" + i + 1)
					.setEstimate(i * 2)
					.setImportance(i * 2)
					.setValue(i * 2);
			long storyId = StoryDAO.getInstance().create(story);
			assertNotSame(-1, storyId);
		}
		
		// get stories
		ArrayList<StoryObject> stories = new ArrayList<StoryObject>();
		for (int i = 0; i < 3; i++) {
			stories.add(StoryDAO.getInstance().get(i + 1));
		}
		assertEquals(3, stories.size());
		
		// delete story #2
		boolean result = StoryDAO.getInstance().delete(stories.get(1).getId());
		assertTrue(result);
		
		// reload stories
		stories.clear();
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(StoryEnum.TABLE_NAME);
		valueSet.addEqualCondition(StoryEnum.PROJECT_ID, sProjectId);
		String query = valueSet.getSelectQuery();
		ResultSet resultSet = mControl.executeQuery(query);
		while (resultSet.next()) {
			stories.add(StoryDAO.convert(resultSet));
		}
		closeResultSet(resultSet);
		assertEquals(2, stories.size());
	}
	
	@Test
	public void testGetStoriesBySprintId() {
		// create three test data
		for (int i = 0; i < 3; i++) {
			StoryObject story = new StoryObject(sProjectId);
			story.setName("TEST_STORY_" + i + 1)
					.setNotes("TEST_NOTE_" + i + 1)
					.setEstimate(i * 2)
					.setImportance(i * 2)
					.setValue(i * 2);
			if (i == 1) {
				story.setSprintId(2);
			} else {
				story.setSprintId(1);
			}
			long storyId = StoryDAO.getInstance().create(story);
			assertNotSame(-1, storyId);
		}
		
		// get stories by sprint id
		ArrayList<StoryObject> stories = StoryDAO.getInstance().getStoriesBySprintId(1);
		assertEquals(2, stories.size());
	}
	
	@Test
	public void testGetStoriesWithNoParent() {
		// create three test data
		for (int i = 0; i < 3; i++) {
			StoryObject story = new StoryObject(sProjectId);
			story.setName("TEST_STORY_" + i + 1)
					.setNotes("TEST_NOTE_" + i + 1)
					.setEstimate(i * 2)
					.setImportance(i * 2)
					.setValue(i * 2);
			if (i == 1) {
				story.setSprintId(2);
			}
			long storyId = StoryDAO.getInstance().create(story);
			assertNotSame(-1, storyId);
		}
		
		// get wild stories
		ArrayList<StoryObject> stories = StoryDAO.getInstance().getStoriesWithNoParent(sProjectId);
		assertEquals(2, stories.size());
	}
	
	@Test
	public void testGetStoriesByProjectId() {
		// create three test data
		for (int i = 0; i < 3; i++) {
			StoryObject story = new StoryObject(sProjectId);
			story.setName("TEST_STORY_" + i + 1)
					.setNotes("TEST_NOTE_" + i + 1)
					.setEstimate(i * 2)
					.setImportance(i * 2)
					.setValue(i * 2);
			long storyId = StoryDAO.getInstance().create(story);
			assertNotSame(-1, storyId);
		}
		
		// get all stories
		ArrayList<StoryObject> stories = StoryDAO.getInstance().getStoriesByProjectId(sProjectId);
		assertEquals(3, stories.size());
	}
	
	@Test
	public void testConvert() throws SQLException {
		String TEST_NAME = "TEST_NAME";
		String TEST_NOTES = "TEST_NOTES";
		String TEST_HOW_TO_DEMO = "TEST_HOW_TO_DEMO";
		long TEST_SERIAL_NUMBER = 99;
		long TEST_ESTIMATE = 0;
		long TEST_IMPORTANCE = 1;
		long TEST_VALUE = 3;
		long TEST_STATUS = 1;
		long TEST_PROJECT_ID = 4;
		long TEST_SPRINT_ID = 5;
		long TEST_CREATE_TIME = System.currentTimeMillis();
		
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(StoryEnum.TABLE_NAME);
		valueSet.addInsertValue(StoryEnum.SERIAL_ID, TEST_SERIAL_NUMBER);
		valueSet.addInsertValue(StoryEnum.NAME, TEST_NAME);
		valueSet.addInsertValue(StoryEnum.STATUS, TEST_STATUS);
		valueSet.addInsertValue(StoryEnum.ESTIMATE, TEST_ESTIMATE);
		valueSet.addInsertValue(StoryEnum.IMPORTANCE, TEST_IMPORTANCE);
		valueSet.addInsertValue(StoryEnum.VALUE, TEST_VALUE);
		valueSet.addInsertValue(StoryEnum.NOTES, TEST_NOTES);
		valueSet.addInsertValue(StoryEnum.HOW_TO_DEMO, TEST_HOW_TO_DEMO);
		valueSet.addInsertValue(StoryEnum.PROJECT_ID, TEST_PROJECT_ID);
		valueSet.addInsertValue(StoryEnum.SPRINT_ID, TEST_SPRINT_ID);
		valueSet.addInsertValue(StoryEnum.CREATE_TIME, TEST_CREATE_TIME);
		valueSet.addInsertValue(StoryEnum.UPDATE_TIME, TEST_CREATE_TIME);
		String query = valueSet.getInsertQuery();
		
		long id = mControl.executeInsert(query);
		valueSet.clear();
		valueSet.addTableName(StoryEnum.TABLE_NAME);
		valueSet.addEqualCondition(StoryEnum.ID, id);
		query = valueSet.getSelectQuery();
		
		ResultSet result = mControl.executeQuery(query);
		result.next();
		StoryObject actual = StoryDAO.convert(result);
		closeResultSet(result);
		
		assertEquals(id, actual.getId());
		assertEquals(TEST_SERIAL_NUMBER, actual.getSerialId());
		assertEquals(TEST_NAME, actual.getName());
		assertEquals(TEST_STATUS, actual.getStatus());
		assertEquals(TEST_NOTES, actual.getNotes());
		assertEquals(TEST_PROJECT_ID, actual.getProjectId());
		assertEquals(TEST_SPRINT_ID, actual.getSprintId());
		assertEquals(TEST_ESTIMATE, actual.getEstimate());
		assertEquals(TEST_IMPORTANCE, actual.getImportance());
		assertEquals(TEST_VALUE, actual.getValue());
		assertEquals(TEST_CREATE_TIME, actual.getCreateTime());
		assertEquals(TEST_CREATE_TIME, actual.getUpdateTime());
	}
	
	private void closeResultSet(ResultSet result) {
		if (result != null) {
			try {
				result.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}			
		}
	}
}
