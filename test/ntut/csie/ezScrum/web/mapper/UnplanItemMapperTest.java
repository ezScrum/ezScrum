package ntut.csie.ezScrum.web.mapper;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.CreateUnplanItem;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataInfo.UnplanInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.UnplanObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UnplanItemMapperTest {
	private CreateProject mCP;
	private CreateSprint mCS;
	private CreateUnplanItem mCUI;
	private Configuration mConfig;
	private ProjectObject mProject;
	private UnplanItemMapper mUnplanMapper;
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
		mUnplanMapper = new UnplanItemMapper(mProject);
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
    	mUnplanMapper = null;
    	mConfig = null;
//    	mControl = null;
	}
	
	@Test
	public void testGetUnplan() {
		// 新增 1 Unplan
		mCUI = new CreateUnplanItem(1, mCP, mCS);
		mCUI.exe();
		UnplanObject unplan = mCUI.getUnplans().get(0);
		
		assertEquals(1, unplan.getId());
		assertEquals(1, unplan.getSerialId());
		assertEquals("TEST_UNPLAN_1", unplan.getName());
		assertEquals("TEST_UNPLAN_NOTES_1", unplan.getNotes());
		assertEquals(2, unplan.getEstimate());
		assertEquals(2, unplan.getActual());
		assertEquals(1, unplan.getSprintId());
		assertEquals(1, unplan.getProjectId());
	}
	
	@Test
	public void testGetUnplansInSprint() {
		// 新增 3 Unplan
		mCUI = new CreateUnplanItem(3, mCP, mCS);
		mCUI.exe();
		
		ArrayList<UnplanObject> expectUnplans = mCUI.getUnplans();
		ArrayList<UnplanObject> actualUnplans = mUnplanMapper.getUnplansInSprint(sSprintId);
		assertEquals(expectUnplans.size(), actualUnplans.size());
		for (int i = 0; i < 3; i++) {
			UnplanObject actualUnplan = actualUnplans.get(i);
			UnplanObject expectUnplan = expectUnplans.get(i);
			assertEquals(expectUnplan.getId(), actualUnplan.getId());
			assertEquals(expectUnplan.getSerialId(), actualUnplan.getSerialId());
			assertEquals(expectUnplan.getName(), actualUnplan.getName());
			assertEquals(expectUnplan.getNotes(), actualUnplan.getNotes());
			assertEquals(expectUnplan.getEstimate(), actualUnplan.getEstimate());
			assertEquals(expectUnplan.getActual(), actualUnplan.getActual());
			assertEquals(expectUnplan.getSprintId(), actualUnplan.getSprintId());
			assertEquals(expectUnplan.getProjectId(), actualUnplan.getProjectId());
		}
	}
	
	@Test
	public void testGetAllUnplans() {
		// 新增 3 Unplan
		mCUI = new CreateUnplanItem(3, mCP, mCS);
		mCUI.exe();
		
		ArrayList<UnplanObject> expectUnplans = mCUI.getUnplans();
		ArrayList<UnplanObject> actualUnplans = mUnplanMapper.getAllUnplans();
		assertEquals(expectUnplans.size(), actualUnplans.size());
	}
	
	@Test
	public void testAddUnplan() {
		UnplanInfo unplanInfo = new UnplanInfo();
		unplanInfo.name = "TEST_NAME";
		unplanInfo.notes = "TEST_NOTES";
		unplanInfo.estimate = 10;
		unplanInfo.actual = 0;
		
		long id = mUnplanMapper.addUnplan(sProjectId, sSprintId, unplanInfo);
		UnplanObject unplan = UnplanObject.get(id);
		
		assertEquals(unplanInfo.name, unplan.getName());
		assertEquals(unplanInfo.notes, unplan.getNotes());
		assertEquals(unplanInfo.estimate, unplan.getEstimate());
		assertEquals(unplanInfo.estimate, unplan.getActual());
		assertEquals(UnplanObject.STATUS_UNCHECK, unplan.getStatus());
		assertEquals(1, unplan.getSerialId());
		assertEquals(sProjectId, unplan.getProjectId());
		assertEquals(sSprintId, unplan.getSprintId());
		assertEquals(1, unplan.getHistories().size());
	}
	
	@Test
	public void testUpdateUnplan() {
		// Create account
		AccountObject account1 = new AccountObject("account1");
		account1.save();
		AccountObject account2 = new AccountObject("account2");
		account2.save();
		// 新增 1 Unplan
		mCUI = new CreateUnplanItem(1, mCP, mCS);
		mCUI.exe();
		UnplanObject unplan = mCUI.getUnplans().get(0);
		
		assertEquals(1, unplan.getId());
		assertEquals(1, unplan.getSerialId());
		assertEquals("TEST_UNPLAN_1", unplan.getName());
		assertEquals("TEST_UNPLAN_NOTES_1", unplan.getNotes());
		assertEquals(2, unplan.getEstimate());
		assertEquals(2, unplan.getActual());
		assertEquals(sSprintId, unplan.getSprintId());
		assertEquals(sProjectId, unplan.getProjectId());
		
		UnplanInfo unplanInfo = new UnplanInfo();
		unplanInfo.id = unplan.getId();
		unplanInfo.name = "亨利欺負學長";
		unplanInfo.notes = "亨利快衝阿";
		unplanInfo.estimate = 888;
		unplanInfo.actual = 456;
		unplanInfo.sprintId = 2;
		unplanInfo.status = UnplanObject.STATUS_DONE;
		ArrayList<Long> partnersId = new ArrayList<Long>();
		partnersId.add(account1.getId());
		partnersId.add(account2.getId());
		unplanInfo.partnersId = partnersId;
		mUnplanMapper.updateUnplan(unplan.getId(), unplanInfo);
		
		unplan.reload();
		assertEquals(1, unplan.getId());
		assertEquals(1, unplan.getSerialId());
		assertEquals("亨利欺負學長", unplan.getName());
		assertEquals("亨利快衝阿", unplan.getNotes());
		assertEquals(888, unplan.getEstimate());
		assertEquals(456, unplan.getActual());
		assertEquals(2, unplan.getSprintId());
		assertEquals(sProjectId, unplan.getProjectId());
		assertEquals(2, unplan.getPartnersId().size());
		assertEquals(account1.getId(), unplan.getPartnersId().get(0));
		assertEquals(account2.getId(), unplan.getPartnersId().get(1));
	}
	
    @Test
	public void testUpdateUnplan_History() {
		// 新增 1 Unplan
		mCUI = new CreateUnplanItem(1, mCP, mCS);
		mCUI.exe();
		UnplanObject unplan = mCUI.getUnplans().get(0);
		
		UnplanInfo unplanInfo = new UnplanInfo();
		unplanInfo.id = unplan.getId();
		unplanInfo.name = "亨利欺負學長";
		unplanInfo.notes = "亨利快衝阿";
		unplanInfo.estimate = 888;
		unplanInfo.actual = 456;
		unplanInfo.sprintId = 2;
		unplanInfo.status = UnplanObject.STATUS_DONE;
		
		mUnplanMapper.updateUnplan(unplan.getId(), unplanInfo);
		
		unplan.reload();
		assertEquals(1, unplan.getId());
		assertEquals(1, unplan.getSerialId());
		assertEquals("亨利欺負學長", unplan.getName());
		assertEquals("亨利快衝阿", unplan.getNotes());
		assertEquals(888, unplan.getEstimate());
		assertEquals(456, unplan.getActual());
		assertEquals(2, unplan.getSprintId());
		assertEquals(sProjectId, unplan.getProjectId());
		
		// get histories
		ArrayList<HistoryObject> histories = unplan.getHistories();
		// check histories size
		assertEquals(7, histories.size());
		// check 1th history
		assertEquals(HistoryObject.TYPE_CREATE, histories.get(0).getHistoryType());
		assertEquals("Create Unplan #1", histories.get(0).getDescription());
		// check 2th history
		assertEquals(HistoryObject.TYPE_NAME, histories.get(1).getHistoryType());
		assertEquals("\"TEST_UNPLAN_1\" => \"亨利欺負學長\"", histories.get(1).getDescription());
		// check 3th history
		assertEquals(HistoryObject.TYPE_NOTE, histories.get(2).getHistoryType());
		assertEquals("\"TEST_UNPLAN_NOTES_1\" => \"亨利快衝阿\"", histories.get(2).getDescription());
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
	public void testDeleteUnplan() {
		// 新增 3 Unplan
		mCUI = new CreateUnplanItem(3, mCP, mCS);
		mCUI.exe();
		ArrayList<Long> unplansId = mCUI.getUnplansId();
		
		// delete the unplans
		for (long unplanId : unplansId) {
			mUnplanMapper.deleteUnplan(unplanId);
		}
		
		// all unplans should be deleted
		for (long unplanId : unplansId) {
			UnplanObject unplan = UnplanObject.get(unplanId);
			assertEquals(null, unplan);
		}
	}
}
