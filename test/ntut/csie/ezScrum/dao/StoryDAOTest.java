package ntut.csie.ezScrum.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

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
		for (int i = 0; i < 3; i++) {
			StoryObject story = new StoryObject(sProjectId);
			story.setName("TEST_STORY_" + i + 1).setNotes("TEST_NOTE_" + i + 1)
					.setEstimate(i * 2).setImportance(i * 2).setValue(i * 2);
			long storyId = StoryDAO.getInstance().create(story);
			assertNotSame(-1, storyId);
		}

		ArrayList<StoryObject> stories = new ArrayList<StoryObject>();
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(StoryEnum.TABLE_NAME);
		valueSet.addEqualCondition(StoryEnum.PROJECT_ID, sProjectId);
		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);
		while (result.next()) {
			stories.add(StoryDAO.convert(result));
		}

		assertEquals(3, stories.size());
		for (int i = 0; i < 3; i++) {
			assertEquals(i + 1, stories.get(i).getId());
			assertEquals(i + 1, stories.get(i).getSerialId());
			assertEquals("TEST_STORY_" + i + 1, stories.get(i).getName());
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
}
