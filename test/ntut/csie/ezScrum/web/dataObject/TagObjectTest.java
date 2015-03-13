package ntut.csie.ezScrum.web.dataObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import ntut.csie.ezScrum.dao.TaskDAO;
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
//		TaskObject task = new TaskObject(1);
//		task.setName("TEST_NAME").setNotes("TEST_NOTES").setEstimate(10)
//			.setActual(0);
//		task.save();
//
//		assertEquals(1, task.getId());
//		assertEquals(1, task.getSerialId());
//		assertEquals("TEST_NAME", task.getName());
//		assertEquals("TEST_NOTES", task.getNotes());
//		assertEquals(10, task.getEstimate());
//		assertEquals(10, task.getRemains());
//		assertEquals(0, task.getActual());
	}
	
	@Test
	public void testSave_UpdateTag() {
//		TaskObject task = new TaskObject(1);
//		task.setName("TEST_NAME").setNotes("TEST_NOTES").setEstimate(10)
//			.setActual(0);
//		task.save();
//
//		assertEquals(1, task.getId());
//		assertEquals(1, task.getSerialId());
//		assertEquals("TEST_NAME", task.getName());
//		assertEquals("TEST_NOTES", task.getNotes());
//		assertEquals(10, task.getEstimate());
//		assertEquals(10, task.getRemains());
//		assertEquals(0, task.getActual());
//
//		task.setName("TEST_NAME2").setNotes("TEST_NOTES2").setEstimate(3)
//			.setRemains(5).setActual(1);
//		task.save();
//
//		assertEquals(1, task.getId());
//		assertEquals(1, task.getSerialId());
//		assertEquals("TEST_NAME2", task.getName());
//		assertEquals("TEST_NOTES2", task.getNotes());
//		assertEquals(3, task.getEstimate());
//		assertEquals(5, task.getRemains());
//		assertEquals(1, task.getActual());
	}
	
	@Test
	public void testReload() {
//		TaskObject task = new TaskObject(1);
//		task.setName("TEST_NAME").setNotes("TEST_NOTES").setEstimate(10).setActual(0);
//		task.save();
//
//		assertEquals(1, task.getId());
//		assertEquals(1, task.getSerialId());
//		assertEquals("TEST_NAME", task.getName());
//		assertEquals("TEST_NOTES", task.getNotes());
//		assertEquals(10, task.getEstimate());
//		assertEquals(10, task.getRemains());
//		assertEquals(0, task.getActual());
//
//		task.setName("TEST_NAME2").setNotes("TEST_NOTES2").setEstimate(5).setRemains(3).setActual(1);
//
//		assertEquals(1, task.getId());
//		assertEquals(1, task.getSerialId());
//		assertEquals("TEST_NAME2", task.getName());
//		assertEquals("TEST_NOTES2", task.getNotes());
//		assertEquals(5, task.getEstimate());
//		assertEquals(3, task.getRemains());
//		assertEquals(1, task.getActual());
//
//		try {
//			task.reload();
//
//			assertEquals(1, task.getId());
//			assertEquals(1, task.getSerialId());
//			assertEquals("TEST_NAME", task.getName());
//			assertEquals("TEST_NOTES", task.getNotes());
//			assertEquals(10, task.getEstimate());
//			assertEquals(10, task.getRemains());
//			assertEquals(0, task.getActual());
//		} catch (Exception e) {
//		}
	}
	
	@Test
	public void testDelete() {
//		TaskObject task = new TaskObject(1);
//		task.setName("TEST_NAME").setNotes("TEST_NOTES").setEstimate(10).setActual(0);
//		task.save();
//
//		assertEquals(1, task.getId());
//		assertEquals(1, task.getSerialId());
//		assertEquals("TEST_NAME", task.getName());
//		assertEquals("TEST_NOTES", task.getNotes());
//		assertEquals(10, task.getEstimate());
//		assertEquals(10, task.getRemains());
//		assertEquals(0, task.getActual());
//
//		boolean deleteSuccess = task.delete();
//
//		assertTrue(deleteSuccess);
//		assertEquals(-1, task.getId());
//		assertEquals(-1, task.getSerialId());
//		assertEquals(null, TaskDAO.getInstance().get(1));
	}
}
