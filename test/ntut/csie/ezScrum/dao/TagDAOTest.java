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
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.databasEnum.TagEnum;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TagDAOTest {
	private MySQLControl mControl = null;
	private Configuration mConfig;
	private CreateProject mCP;
	private int mProjectCount = 1;
	private long mProjectId;
	
	@Before
	public void setUp(){
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		mCP = new CreateProject(mProjectCount);
		mCP.exeCreate();

		mControl = new MySQLControl(mConfig);
		mControl.connect();

		mProjectId = mCP.getAllProjects().get(0).getId();
	}
	
	@After
	public void tearDown(){
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
	public void testCreate() throws SQLException{
		// Tag Name
		String tagName = "TEST_TAG_NAME_1";
		// create TagObject
		TagObject tag = new TagObject(tagName, mProjectId);
		// create test data
		long tagId = TagDAO.getInstance().create(tag);
		// assert
		assertNotSame(-1, tagId);
		
		// 從資料庫撈出資料檢查
		ArrayList<TagObject> tagList = new ArrayList<TagObject>();
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(TagEnum.TABLE_NAME);
		valueSet.addEqualCondition(TagEnum.ID, tagId);
		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);
		while (result.next()) {
			tagList.add(TagDAO.convert(result));
		}

		assertEquals(1, tagList.size());
		assertEquals(tagId, tagList.get(0).getId());
		assertEquals(tagName, tagList.get(0).getName());
	}
	
	@Test
	public void testGet(){
		// create Tag
		String tagName = "TEST_TAG_NAME_1";
		TagObject tag = createTag(tagName);
		long tagId = tag.getId();
		
		// get
		ArrayList<TagObject> tagList = new ArrayList<TagObject>();
		tagList.add(TagDAO.getInstance().get(tagId));
		assertEquals(1, tagList.size());
		assertEquals(tagId, tagList.get(0).getId());
		assertEquals(tagName, tagList.get(0).getName());
	}
	
	@Test
	public void testUpdate() throws SQLException{
		// new data
		String newTagName = "TEST_TAG_NAME_1_new";
		// create Tag
		String tagName = "TEST_TAG_NAME_1";
		TagObject tag = createTag(tagName);
		long tagId = tag.getId();
		
		// Update TagObject
		tag.setName(newTagName);
		boolean updateStatus = TagDAO.getInstance().update(tag);
		assertTrue(updateStatus);
		
		// 從資料庫取出資料檢查
		ArrayList<TagObject> tagList = new ArrayList<TagObject>();
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(TagEnum.TABLE_NAME);
		valueSet.addEqualCondition(TagEnum.ID, tagId);
		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);
		while (result.next()) {
			tagList.add(TagDAO.convert(result));
		}

		assertEquals(1, tagList.size());
		assertEquals(tagId, tagList.get(0).getId());
		assertEquals(newTagName, tagList.get(0).getName());
	}
	
	@Test
	public void testDelete() throws SQLException{
		// create Tag
		String tagName = "TEST_TAG_NAME_1";
		TagObject tag = createTag(tagName);
		long tagId = tag.getId();
		
		// delete tag
		boolean deleteStatus = TagDAO.getInstance().delete(tagId);
		assertTrue(deleteStatus);
		
		// 從資料庫取出資料檢查
		ArrayList<TagObject> tagList = new ArrayList<TagObject>();
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(TagEnum.TABLE_NAME);
		valueSet.addEqualCondition(TagEnum.ID, tagId);
		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);
		while (result.next()) {
			tagList.add(TagDAO.convert(result));
		}

		assertEquals(0, tagList.size());
	}
	
	@Test
	public void testGetTagInProjectByName(){
		// create Tag
		String tagName = "TEST_TAG_NAME_1";
		createTag(tagName);
		
		// getTagByName
		TagObject tag = TagDAO.getInstance().getTagInProjectByName(mProjectId, tagName);
		// assert
		assertNotNull(tag);
		assertEquals(tagName, tag.getName());
	}
	
	@Test
	public void testGetTagsByProjectId(){
		// Tag Name
		String tagName = "TEST_TAG_NAME_";
		// create 3 TagObject
		for (int i = 0; i < 3; i++) {
			TagObject tag = new TagObject(tagName + i, mProjectId);
			// create test data
			long tagId = TagDAO.getInstance().create(tag);
			// assert
			assertNotSame(-1, tagId);
		}

		// getTagList
		ArrayList<TagObject> tags = TagDAO.getInstance().getTagsByProjectId(mProjectId);
		// assert
		assertEquals(3, tags.size());
		
		for (int i = 0; i < 3; i++) {
			assertEquals(tagName + i, tags.get(i).getName());
			assertEquals(mProjectId, tags.get(i).getProjectId());
		}
	}
	
	@Test
	public void testGetTagsByStoryId(){
		StoryObject story = new StoryObject(mProjectId);
		story.save();
		// Tag Name
		String tagName = "TEST_TAG_NAME_";
		// create 3 TagObject
		for (int i = 0; i < 3; i++) {
			TagObject tag = new TagObject(tagName + i, mProjectId);
			// create test data
			long tagId = TagDAO.getInstance().create(tag);
			// assert
			assertNotSame(-1, tagId);
		}
		assertEquals(0, TagDAO.getInstance().getTagsByStoryId(story.getId()).size());
		story.addTag(2);
		story.save();
		ArrayList<TagObject> tags = TagDAO.getInstance().getTagsByStoryId(story.getId());
		assertEquals(1, tags.size());
		assertEquals("TEST_TAG_NAME_1", tags.get(0).getName());
	}
	
	private TagObject createTag(String tagName) {
		// create TagObject
		TagObject tag = new TagObject(tagName, mProjectId);
		// create test data
		long tagId = TagDAO.getInstance().create(tag);
		assertNotSame(-1, tagId);
		return TagDAO.getInstance().get(tagId);
	}
}
