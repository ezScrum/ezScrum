package ntut.csie.ezScrum.web.mapper;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.CreateUnplannedItem;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataInfo.UnplannedInfo;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.UnplannedObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UnplannedItemMapperTest {
	private CreateProject mCP;
	private CreateSprint mCS;
	private CreateUnplannedItem mCUI;
	private Configuration mConfig;
	private ProjectObject mProject;
	private UnplannedItemMapper mUnplannedMapper;
	private static long sProjectId;
	private static long sSprintId;

	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 新增 Project
		mCP = new CreateProject(1);
		mCP.exeCreate();

		// 新增 Sprint
		mCS = new CreateSprint(1, mCP);
		mCS.exe();
		
		mProject = mCP.getAllProjects().get(0);
		mUnplannedMapper = new UnplannedItemMapper(mProject);
		sProjectId = mProject.getId();
		sSprintId = mCS.getSprints().get(0).getId();
		
		// 為了使 Story 建立時間與修改時間分開而停下
		Thread.sleep(1000);
		// ============= release ==============
		ini = null;
	}
	
	@After
	public void tearDown() throws Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		
		// 讓 config 回到  Production 模式
		mConfig.setTestMode(false);
		mConfig.save();
    	
    	// ============= release ==============
    	ini = null;
    	mCP = null;
    	mCS = null;
//    	mCUI = null;
    	projectManager = null;
    	mUnplannedMapper = null;
    	mConfig = null;
//    	mControl = null;
	}
	
	@Test
	public void testGetUnplanned() {
		// 新增 1 Unplanned
		mCUI = new CreateUnplannedItem(1, mCP, mCS);
		mCUI.exe();
		UnplannedObject unplanned = mCUI.getUnplanneds().get(0);
		
		assertEquals(1, unplanned.getId());
		assertEquals(1, unplanned.getSerialId());
		assertEquals("TEST_UNPLANNED_1", unplanned.getName());
		assertEquals("TEST_UNPLANNED_NOTES_1", unplanned.getNotes());
		assertEquals(2, unplanned.getEstimate());
		assertEquals(2, unplanned.getActual());
		assertEquals(1, unplanned.getSprintId());
		assertEquals(1, unplanned.getProjectId());
	}
	
	@Test
	public void testGetUnplannedsInSprint() {
		// 新增 3 Unplanned
		mCUI = new CreateUnplannedItem(3, mCP, mCS);
		mCUI.exe();
		
		ArrayList<UnplannedObject> expectUnplanneds = mCUI.getUnplanneds();
		ArrayList<UnplannedObject> actualUnplanneds = mUnplannedMapper.getUnplannedsInSprint(sSprintId);
		assertEquals(expectUnplanneds.size(), actualUnplanneds.size());
		for (int i = 0; i < 3; i++) {
			UnplannedObject actualUnplanned = actualUnplanneds.get(i);
			UnplannedObject expectUnplanned = expectUnplanneds.get(i);
			assertEquals(expectUnplanned.getId(), actualUnplanned.getId());
			assertEquals(expectUnplanned.getSerialId(), actualUnplanned.getSerialId());
			assertEquals(expectUnplanned.getName(), actualUnplanned.getName());
			assertEquals(expectUnplanned.getNotes(), actualUnplanned.getNotes());
			assertEquals(expectUnplanned.getEstimate(), actualUnplanned.getEstimate());
			assertEquals(expectUnplanned.getActual(), actualUnplanned.getActual());
			assertEquals(expectUnplanned.getSprintId(), actualUnplanned.getSprintId());
			assertEquals(expectUnplanned.getProjectId(), actualUnplanned.getProjectId());
		}
	}
	
	@Test
	public void testGetAllUnplanneds() {
		// 新增 3 Unplanned
		mCUI = new CreateUnplannedItem(3, mCP, mCS);
		mCUI.exe();
		
		ArrayList<UnplannedObject> expectUnplanneds = mCUI.getUnplanneds();
		ArrayList<UnplannedObject> actualUnplanneds = mUnplannedMapper.getAllUnplanneds();
		assertEquals(expectUnplanneds.size(), actualUnplanneds.size());
	}
	
	@Test
	public void testAddUnplanned() {
		UnplannedInfo unplannedInfo = new UnplannedInfo();
		unplannedInfo.name = "TEST_NAME";
		unplannedInfo.notes = "TEST_NOTES";
		unplannedInfo.estimate = 10;
		unplannedInfo.actual = 0;
		
		long id = mUnplannedMapper.addUnplanned(sProjectId, sSprintId, unplannedInfo);
		UnplannedObject unplanned = UnplannedObject.get(id);
		
		assertEquals(unplannedInfo.name, unplanned.getName());
		assertEquals(unplannedInfo.notes, unplanned.getNotes());
		assertEquals(unplannedInfo.estimate, unplanned.getEstimate());
		assertEquals(unplannedInfo.estimate, unplanned.getActual());
		assertEquals(UnplannedObject.STATUS_UNCHECK, unplanned.getStatus());
		assertEquals(1, unplanned.getSerialId());
		assertEquals(sProjectId, unplanned.getProjectId());
		assertEquals(sSprintId, unplanned.getSprintId());
		assertEquals(1, unplanned.getHistories().size());
	}
	
	@Test
	public void testUpdateUnplanned() {
		// 新增 1 Unplanned
		mCUI = new CreateUnplannedItem(1, mCP, mCS);
		mCUI.exe();
		UnplannedObject unplanned = mCUI.getUnplanneds().get(0);
		
		assertEquals(1, unplanned.getId());
		assertEquals(1, unplanned.getSerialId());
		assertEquals("TEST_UNPLANNED_1", unplanned.getName());
		assertEquals("TEST_UNPLANNED_NOTES_1", unplanned.getNotes());
		assertEquals(2, unplanned.getEstimate());
		assertEquals(2, unplanned.getActual());
		assertEquals(sSprintId, unplanned.getSprintId());
		assertEquals(sProjectId, unplanned.getProjectId());
		
		UnplannedInfo unplannedInfo = new UnplannedInfo();
		unplannedInfo.id = unplanned.getId();
		unplannedInfo.name = "亨利欺負學長";
		unplannedInfo.notes = "亨利快衝阿";
		unplannedInfo.estimate = 888;
		unplannedInfo.actual = 456;
		unplannedInfo.sprintId = 2;
		unplannedInfo.status = UnplannedObject.STATUS_DONE;
		
		mUnplannedMapper.updateUnplanned(unplanned.getId(), unplannedInfo);
		
		unplanned.reload();
		assertEquals(1, unplanned.getId());
		assertEquals(1, unplanned.getSerialId());
		assertEquals("亨利欺負學長", unplanned.getName());
		assertEquals("亨利快衝阿", unplanned.getNotes());
		assertEquals(888, unplanned.getEstimate());
		assertEquals(456, unplanned.getActual());
		assertEquals(2, unplanned.getSprintId());
		assertEquals(sProjectId, unplanned.getProjectId());
	}
	
    @Test
	public void testUpdateUnplanned_History() {
		// 新增 1 Unplanned
		mCUI = new CreateUnplannedItem(1, mCP, mCS);
		mCUI.exe();
		UnplannedObject unplanned = mCUI.getUnplanneds().get(0);
		
		UnplannedInfo unplannedInfo = new UnplannedInfo();
		unplannedInfo.id = unplanned.getId();
		unplannedInfo.name = "亨利欺負學長";
		unplannedInfo.notes = "亨利快衝阿";
		unplannedInfo.estimate = 888;
		unplannedInfo.actual = 456;
		unplannedInfo.sprintId = 2;
		unplannedInfo.status = UnplannedObject.STATUS_DONE;
		
		mUnplannedMapper.updateUnplanned(unplanned.getId(), unplannedInfo);
		
		unplanned.reload();
		assertEquals(1, unplanned.getId());
		assertEquals(1, unplanned.getSerialId());
		assertEquals("亨利欺負學長", unplanned.getName());
		assertEquals("亨利快衝阿", unplanned.getNotes());
		assertEquals(888, unplanned.getEstimate());
		assertEquals(456, unplanned.getActual());
		assertEquals(2, unplanned.getSprintId());
		assertEquals(sProjectId, unplanned.getProjectId());
		
		// get histories
		ArrayList<HistoryObject> histories = unplanned.getHistories();
		// check histories size
		assertEquals(7, histories.size());
		// check 1th history
		assertEquals(HistoryObject.TYPE_CREATE, histories.get(0).getHistoryType());
		assertEquals("Create Unplanned #1", histories.get(0).getDescription());
		// check 2th history
		assertEquals(HistoryObject.TYPE_NAME, histories.get(1).getHistoryType());
		assertEquals("\"TEST_UNPLANNED_1\" => \"亨利欺負學長\"", histories.get(1).getDescription());
		// check 3th history
		assertEquals(HistoryObject.TYPE_NOTE, histories.get(2).getHistoryType());
		assertEquals("\"TEST_UNPLANNED_NOTES_1\" => \"亨利快衝阿\"", histories.get(2).getDescription());
		// check 4th history
		assertEquals(HistoryObject.TYPE_STATUS, histories.get(3).getHistoryType());
		assertEquals("Not Check Out => Done", histories.get(3).getDescription());
		// check 5th history
		assertEquals(HistoryObject.TYPE_ESTIMATE, histories.get(4).getHistoryType());
		assertEquals("2 => 888", histories.get(4).getDescription());
		// check 6th history
		assertEquals(HistoryObject.TYPE_ACTUAL, histories.get(5).getHistoryType());
		assertEquals("2 => 456", histories.get(5).getDescription());
		// check 7th history
		assertEquals(HistoryObject.TYPE_SPRINT_ID, histories.get(6).getHistoryType());
		assertEquals("Sprint #1 => Sprint #2", histories.get(6).getDescription());
	}
	
	@Test
	public void testDeleteUnplanned() {
		// 新增 3 Unplanned
		mCUI = new CreateUnplannedItem(3, mCP, mCS);
		mCUI.exe();
		ArrayList<Long> unplannedsId = mCUI.getUnplannedsId();
		
		// delete the unplanneds
		for (long unplannedId : unplannedsId) {
			mUnplannedMapper.deleteUnplanned(unplannedId);
		}
		
		// all unplanneds should be deleted
		for (long unplannedId : unplannedsId) {
			UnplannedObject unplanned = UnplannedObject.get(unplannedId);
			assertEquals(null, unplanned);
		}
	}
}
