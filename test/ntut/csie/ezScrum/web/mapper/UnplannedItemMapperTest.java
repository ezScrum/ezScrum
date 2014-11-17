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
		mConfig.store();

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
		mConfig.store();
    	
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
		
		// assert issue history info
		ArrayList<HistoryObject> histories = unplanned.getHistories();
		ArrayList<HashMap<String, String>> excepted = genExceptedData(
				genList("1", "16", "2", "12", "13", "21", "3", "5", "19"),
				genList("Create Unplanned #1",
						"Append to Sprint #1",
						"\"p1s1_TEST_UNPLANNED_1\" => \"快接 task 啦\"",
						"Not Check Out => Check Out",
						"admin",
						"",
						"2 => 6",
						"0 => 6",
						"TEST_UNPLANNED_NOTES_1 => 已哭"));
		
		for (int i = 0; i < histories.size(); i++) {
			assertEquals(issueId, histories.get(i).getIssueId());
			assertEquals(IssueTypeEnum.TYPE_UNPLANNED, histories.get(i).getIssueType());
			assertEquals(excepted.get(i).get("type"), String.valueOf(histories.get(i).getHistoryType()));
			assertEquals(excepted.get(i).get("desc"), histories.get(i).getDescription());
		}
	}
	
	private ArrayList<HashMap<String, String>> genExceptedData(ArrayList<String> typeList, ArrayList<String> descList) {
		ArrayList<HashMap<String, String>> exceptedList = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < typeList.size(); i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("type", typeList.get(i));
			map.put("desc", descList.get(i));
			exceptedList.add(map);
		}
		return exceptedList;
	}
	
	private ArrayList<String> genList(String... args) {
		ArrayList<String> list = new ArrayList<String>();
		for (String arg : args) {
			list.add(arg);
		}
		return list;
	}

}
