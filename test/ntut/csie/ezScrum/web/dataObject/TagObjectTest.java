package ntut.csie.ezScrum.web.dataObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TagObjectTest {
	private Configuration mConfig = null;
	private CreateProject mCP = null;
	private final static int mPROJECT_COUNT = 1;
	private long mProjectId = -1;
	
	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		mCP = new CreateProject(mPROJECT_COUNT);
		mCP.exeCreate();
		
		mProjectId = mCP.getAllProjects().get(0).getId();
	}

	@After
	public void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		// 讓 config 回到  Production 模式
		mConfig.setTestMode(false);
		mConfig.save();
		
		mConfig = null;
		mCP = null;
	}
	
	@Test
	public void testSave_CreateANewTag() {
		TagObject tag = new TagObject("TEST_NAME", mProjectId);
		tag.save();
		assertEquals(1, tag.getId());
		assertEquals("TEST_NAME", tag.getName());
		assertEquals(1, tag.getProjectId());
	}
	
	@Test
	public void testSave_UpdateTag() {
		TagObject tag = new TagObject("TEST_NAME", mProjectId);
		tag.save();
		assertEquals(1, tag.getId());
		assertEquals("TEST_NAME", tag.getName());
		assertEquals(1, tag.getProjectId());
		tag.setName("TEST_NEW_NAME");
		tag.save();
		assertEquals(1, tag.getId());
		assertEquals("TEST_NEW_NAME", tag.getName());
		assertEquals(1, tag.getProjectId());
	}
	
	@Test
	public void testReload() {
		// test data
		String tagName = "TEST_TAG_NAME_1";
		String tagNewName = "TEST_TAG_NAME_1_new";
		
		// create tag
		TagObject tag = new TagObject(tagName, mProjectId);
		tag.save();
		
		assertEquals(tagName, tag.getName());
		assertEquals(mProjectId, tag.getProjectId());
		
		// set value but don't save
		tag.setName(tagNewName);
		assertEquals(tagNewName, tag.getName());
		
		// reload
		tag.reload();
		assertEquals(tagName, tag.getName());
		
	}
	
	@Test
	public void testDelete() {
		// test data
		String tagName = "TEST_TAG_NAME_1";

		// create tag
		TagObject tag = new TagObject(tagName, mProjectId);
		tag.save();
		
		assertEquals(tagName, tag.getName());
		assertEquals(mProjectId, tag.getProjectId());
		
		// delete tag
		boolean deleteStatus = tag.delete();
		assertTrue(deleteStatus);
		assertEquals(-1, tag.getId());
	}
}
