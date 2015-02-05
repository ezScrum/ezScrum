package ntut.csie.ezScrum.web.mapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.CreateUnplannedItem;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.databasEnum.IssueTypeEnum;
import ntut.csie.jcis.resource.core.IProject;

public class UnplannedItemMapperTest extends TestCase {
	private CreateProject mCreateProject;
	private CreateSprint mCreateSprint;
	private CreateUnplannedItem mCreateUnplanned; 
	private Configuration mConfig;
	private MySQLControl mControl;
	private IProject mProject;
	private UnplannedItemMapper mUnplannedMapper;

	public UnplannedItemMapperTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws InterruptedException {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		mControl = new MySQLControl(mConfig);
		mControl.connection();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 新增 Project
		mCreateProject = new CreateProject(1);
		mCreateProject.exeCreate();

		// 新增 Sprint
		mCreateSprint = new CreateSprint(1, mCreateProject);
		mCreateSprint.exe();
		
		// 新增 Unplanned
		mCreateUnplanned = new CreateUnplannedItem(1, mCreateProject, mCreateSprint);
		mCreateUnplanned.exe();

		mProject = mCreateProject.getProjectList().get(0);
		mUnplannedMapper = new UnplannedItemMapper(mProject, mConfig.getUserSession());
		
		// 為了使 Story 建立時間與修改時間分開而停下
		Thread.sleep(1000);
		// ============= release ==============
		ini = null;
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
		mConfig.save();
    	
    	// ============= release ==============
    	ini = null;
    	mCreateProject = null;
    	projectManager = null;
    	mConfig = null;
    	
    	super.tearDown();
	}
	
	public void testUpdate_History() throws SQLException {
		long issueId = mCreateUnplanned.getIdList().get(0);
		String name = "快接 task 啦";
		String handler = "admin";
		String partners = "Sam, Jay";
		String estimate = "6";
		String actualHour = "6";
		String notes = "已哭";
		String sprintId = mCreateSprint.getSprintIDList().get(0);
		Date date = new Date(System.currentTimeMillis());
		mUnplannedMapper.update(issueId, name, handler, ITSEnum.S_ASSIGNED_STATUS,
				partners, estimate, actualHour, notes, sprintId, date);
		// assert issue info
		IIssue unplanned = mUnplannedMapper.getById(issueId);
		assertEquals(issueId, unplanned.getIssueID());
		assertEquals(name, unplanned.getSummary());
		assertEquals(handler, unplanned.getAssignto());
		assertEquals(partners, unplanned.getPartners());
		assertEquals(estimate, unplanned.getEstimated());
		assertEquals(actualHour, unplanned.getActualHour());
		assertEquals(notes, unplanned.getNotes());
		assertEquals(sprintId, unplanned.getSprintID());
		// get histories
		ArrayList<HistoryObject> histories = unplanned.getHistories();
		/*
		 * expected histories size = 9
		 * expected 1th history type = 1, description = "Create Unplanned #1"
		 * expected 2th history type = 16, description = "Append to Sprint #1"
		 * expected 3th history type = 2, description = "\"p1s1_TEST_UNPLANNED_1\" => \"快接 task 啦\""
		 * expected 4th history type = 12, description = "Not Check Out => Check Out"
		 * expected 5th history type = 13, description = "admin"
		 * expected 6th history type = 21, description = ""
		 * expected 7th history type = 3, description = "2 => 6"
		 * expected 8th history type = 5, description = "0 => 6"
		 * expected 9th history type = 19, description = "\"TEST_UNPLANNED_NOTES_1\" => \"已哭\""
		 */
		// check histories size
		assertEquals(9, histories.size());
		// check 1th history
		assertEquals(1, histories.get(0).getHistoryType());
		assertEquals("Create Unplanned #1", histories.get(0).getDescription());
		// check 2th history
		assertEquals(16, histories.get(1).getHistoryType());
		assertEquals("Append to Sprint #1", histories.get(1).getDescription());
//		// check 3th history
		assertEquals(2, histories.get(2).getHistoryType());
		assertEquals("\"p1s1_TEST_UNPLANNED_1\" => \"快接 task 啦\"", histories.get(2).getDescription());
		// check 4th history
		assertEquals(12, histories.get(3).getHistoryType());
		assertEquals("Not Check Out => Check Out", histories.get(3).getDescription());
		// check 5th history
		assertEquals(13, histories.get(4).getHistoryType());
		assertEquals("admin", histories.get(4).getDescription());
		// check 6th history
		assertEquals(21, histories.get(5).getHistoryType());
		assertEquals("", histories.get(5).getDescription());
		// check 7th history
		assertEquals(3, histories.get(6).getHistoryType());
		assertEquals("2 => 6", histories.get(6).getDescription());
		// check 8th history
		assertEquals(5, histories.get(7).getHistoryType());
		assertEquals("0 => 6", histories.get(7).getDescription());
		// check 9th history
		assertEquals(19, histories.get(8).getHistoryType());
		assertEquals("\"TEST_UNPLANNED_NOTES_1\" => \"已哭\"", histories.get(8).getDescription());
	}
}
