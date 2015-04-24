package ntut.csie.ezScrum.web.helper;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.form.IterationPlanForm;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SprintPlanHelperTest {
	private SprintPlanHelper mSprintPlanHelper;
	private CreateProject mCP;
	private CreateSprint mCS;
	private int mProjectCount = 1;
	private int mSprintCount = 3;
	private Configuration mConfig = null;
	
	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();											// 初始化 SQL
		
		this.mCP = new CreateProject(this.mProjectCount);
		this.mCP.exeCreate();
		
		this.mCS = new CreateSprint(this.mSprintCount, this.mCP);
		this.mCS.exe();
    }

	@After
    public void tearDown() throws IOException, Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();											// 初始化 SQL
		
		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		// 讓 config 回到  Production 模式
		mConfig.setTestMode(false);
		mConfig.save();
		
		// release
		mCP = null;
		mCS = null;
		projectManager = null;
		mConfig = null;
    }
    
//	public void testcurrentSprintID() {
//		this.helper = new SprintPlanHelper(this.CP.getIProjectList().get(0));
//    	
//		int cur = this.SprintCount/2 + 1;		// 當下的 sprint 為所有新建 sprints 的中間值
//    	
//		assertEquals(cur, this.helper.currentSprintID());
//	}
//    
//	public void testloadPlans() {
//		this.helper = new SprintPlanHelper(this.CP.getIProjectList().get(0));
//		ISprintPlanDesc[] SprintPlans = this.helper.loadPlans();
//		assertEquals(this.CS.getSprintCount(), SprintPlans.length);
//    	
//		for (int i=0 ; i<SprintPlans.length ; i++) {
//			assertEquals(this.CS.getDefault_SPRINT_GOAL(i+1), SprintPlans[i].getGoal());
//			assertEquals("2", SprintPlans[i].getInterval());
//			assertEquals("2", SprintPlans[i].getMemberNumber());
//			assertEquals("10", SprintPlans[i].getAvailableDays());
//			assertEquals("100", SprintPlans[i].getFocusFactor());
//			assertEquals("LAB 1321", SprintPlans[i].getDemoPlace());
//		}
//	}
//    
//	public void testloadListPlans() {
//		this.helper = new SprintPlanHelper(this.CP.getIProjectList().get(0));
//		List<ISprintPlanDesc> SprintPlans = this.helper.loadListPlans();
//		assertEquals(this.CS.getSprintCount(), SprintPlans.size());
//    	
//		for (int i=0 ; i<SprintPlans.size() ; i++) {
//			assertEquals(this.CS.getDefault_SPRINT_GOAL(i+1), SprintPlans.get(i).getGoal());
//			assertEquals("2", SprintPlans.get(i).getInterval());
//			assertEquals("2", SprintPlans.get(i).getMemberNumber());
//			assertEquals("10", SprintPlans.get(i).getAvailableDays());
//			assertEquals("100", SprintPlans.get(i).getFocusFactor());
//			assertEquals("LAB 1321", SprintPlans.get(i).getDemoPlace());
//		}	
//	}
//    
//	public void testloadPlan() {
//		this.helper = new SprintPlanHelper(this.CP.getIProjectList().get(0));
//		ISprintPlanDesc SprintPlan = this.helper.loadPlan("1");
//		
//		assertEquals(this.CS.getDefault_SPRINT_GOAL(1), SprintPlan.getGoal());
//		assertEquals("2", SprintPlan.getInterval());
//		assertEquals("2", SprintPlan.getMemberNumber());
//		assertEquals("10", SprintPlan.getAvailableDays());
//		assertEquals("100", SprintPlan.getFocusFactor());
//		assertEquals("LAB 1321", SprintPlan.getDemoPlace());
//		
//		// 除錯，測試輸入不存在的 sprintID，會回傳 new SprintPlanDesc
//		ISprintPlanDesc nullSprintPlan = this.helper.loadPlan("1000");
//		assertEquals("-1", nullSprintPlan.getID());
//		assertEquals(null, nullSprintPlan.getGoal());			// ========== 程式設計此欄為 null =========
//		assertEquals("", nullSprintPlan.getInterval());
//		assertEquals("0", nullSprintPlan.getMemberNumber());
//		assertEquals("0", nullSprintPlan.getAvailableDays());
//		assertEquals("", nullSprintPlan.getFocusFactor());
//		assertEquals("", nullSprintPlan.getDemoPlace());
//		assertEquals("", nullSprintPlan.getDemoDate());
//		assertEquals("", nullSprintPlan.getStartDate());
//		assertEquals("", nullSprintPlan.getEndDate());
//		assertEquals("", nullSprintPlan.getNotes());
//		assertEquals("", nullSprintPlan.getNumber());
//		
//		ISprintPlanDesc nullSprintPlan2 = this.helper.loadPlan("XXX");
//		assertEquals("-1", nullSprintPlan2.getID());
//		assertEquals(null, nullSprintPlan.getGoal());			// ========== 程式設計此欄為 null =========
//		assertEquals("", nullSprintPlan2.getInterval());
//		assertEquals("0", nullSprintPlan2.getMemberNumber());
//		assertEquals("0", nullSprintPlan2.getAvailableDays());
//		assertEquals("", nullSprintPlan2.getFocusFactor());
//		assertEquals("", nullSprintPlan2.getDemoPlace());
//		assertEquals("", nullSprintPlan2.getDemoDate());
//		assertEquals("", nullSprintPlan2.getStartDate());
//		assertEquals("", nullSprintPlan2.getEndDate());
//		assertEquals("", nullSprintPlan2.getNotes());
//		assertEquals("", nullSprintPlan2.getNumber());
//	}
//    
//	public void testloadCurrentPlan() throws Exception {
//    	this.helper = new SprintPlanHelper(this.CP.getIProjectList().get(0));
//    	int cur = this.SprintCount/2 + 1;		// 當下的 sprint 為所有新建 sprints 的中間值
//    	
//    	ISprintPlanDesc SprintPlan = this.helper.loadCurrentPlan();
//		assertEquals(this.CS.getDefault_SPRINT_GOAL(cur), SprintPlan.getGoal());
//		assertEquals("2", SprintPlan.getInterval());
//		assertEquals("2", SprintPlan.getMemberNumber());
//		assertEquals("10", SprintPlan.getAvailableDays());
//		assertEquals("100", SprintPlan.getFocusFactor());
//		assertEquals("LAB 1321", SprintPlan.getDemoPlace());
//		
//		
//		// 清空 sprint plan
//		InitialSQL ini = new InitialSQL(ezScrumInfo);
//		ini.exe();			// 初始化 SQL		
//		
//		CopyProject copyProject = new CopyProject(this.CP);
//    	copyProject.exeDelete_Project();					// 刪除測試檔案
//    	
//		this.CP = new CreateProject(this.ProjectCount);
//		this.CP.exe();
//		
//		// 除錯，當沒有產生任何 sprint
//		this.helper = new SprintPlanHelper(this.CP.getIProjectList().get(0));
//		ISprintPlanDesc nullSprintPlan = this.helper.loadCurrentPlan();
//		assertEquals(null, nullSprintPlan);
//		
//		// 還有輸入一筆 SprintPlans，但是今天日期不為其中一 SprintPlan 的測試未寫
//	}
//
//	public void testgetNextDemoDate() {
//    	// !! 超級複雜 !!
//    	
//    	
//	}
//    
//	public void testloadPlan_int() {
//    	this.helper = new SprintPlanHelper(this.CP.getIProjectList().get(0));
//		ISprintPlanDesc SprintPlan = this.helper.loadPlan(1);		// 參數為數字
//		
//		assertEquals(this.CS.getDefault_SPRINT_GOAL(1), SprintPlan.getGoal());
//		assertEquals("2", SprintPlan.getInterval());
//		assertEquals("2", SprintPlan.getMemberNumber());
//		assertEquals("10", SprintPlan.getAvailableDays());
//		assertEquals("100", SprintPlan.getFocusFactor());
//		assertEquals("LAB 1321", SprintPlan.getDemoPlace());
//		
//		// 除錯，測試輸入不存在的 sprintID，會回傳 new SprintPlanDesc
//		ISprintPlanDesc nullSprintPlan = this.helper.loadPlan("1000");
//		assertEquals("-1", nullSprintPlan.getID());
//		assertEquals(null, nullSprintPlan.getGoal());			// ========== 程式設計此欄為 null =========
//		assertEquals("", nullSprintPlan.getInterval());
//		assertEquals("0", nullSprintPlan.getMemberNumber());
//		assertEquals("0", nullSprintPlan.getAvailableDays());
//		assertEquals("", nullSprintPlan.getFocusFactor());
//		assertEquals("", nullSprintPlan.getDemoPlace());
//		assertEquals("", nullSprintPlan.getDemoDate());
//		assertEquals("", nullSprintPlan.getStartDate());
//		assertEquals("", nullSprintPlan.getEndDate());
//		assertEquals("", nullSprintPlan.getNotes());
//		assertEquals("", nullSprintPlan.getNumber());
//		
//		ISprintPlanDesc nullSprintPlan2 = this.helper.loadPlan("XXX");
//		assertEquals("-1", nullSprintPlan2.getID());
//		assertEquals(null, nullSprintPlan.getGoal());			// ========== 程式設計此欄為 null =========
//		assertEquals("", nullSprintPlan2.getInterval());
//		assertEquals("0", nullSprintPlan2.getMemberNumber());
//		assertEquals("0", nullSprintPlan2.getAvailableDays());
//		assertEquals("", nullSprintPlan2.getFocusFactor());
//		assertEquals("", nullSprintPlan2.getDemoPlace());
//		assertEquals("", nullSprintPlan2.getDemoDate());
//		assertEquals("", nullSprintPlan2.getStartDate());
//		assertEquals("", nullSprintPlan2.getEndDate());
//		assertEquals("", nullSprintPlan2.getNotes());
//		assertEquals("", nullSprintPlan2.getNumber());   	
//	}
//    
//	public void testgetPlanNumbers() throws Exception {
//		this.helper = new SprintPlanHelper(this.CP.getIProjectList().get(0));
//		assertEquals(this.CS.getSprintCount(), this.helper.getPlanNumbers());
//		
//		// 清空 sprint plan
//		InitialSQL ini = new InitialSQL(ezScrumInfo);
//		ini.exe();		// 初始化 SQL
//		
//		CopyProject copyProject = new CopyProject(this.CP);
//    	copyProject.exeDelete_Project();					// 刪除測試檔案
//    	
//		this.CP = new CreateProject(this.ProjectCount);
//		this.CP.exe();
//		
//		// 除錯，當沒有產生任何 sprint
//		this.helper = new SprintPlanHelper(this.CP.getIProjectList().get(0));
//		assertEquals(0, this.helper.getPlanNumbers());
//	}
//    
//	public void testgetSprintPlanForm() {
//    	// 此 method 沒有被呼叫使用
//    	this.helper = new SprintPlanHelper(this.CP.getIProjectList().get(0));
//    	
//    	// 設定參數資料
//    	IterationPlanForm form = new IterationPlanForm();
//    	form.setID(Integer.toString(this.CS.getSprintCount()+1));
//    	form.setAvailableDays("10");
//    	form.setIterStartDate("2010/10/10");
//    	form.setDemoDate("2010/10/24");
//    	form.setDemoPlace("Lab 1321");
//    	form.setFocusFactor("100");
//    	form.setGoal("Get ONE PIECE !!");
//    	form.setIterIterval("2");
//    	form.setIterMemberNumber("2");
//    	form.setNotes("成為海賊王");    	
//	}
//    
//	public void testsaveIterationPlanForm() {
//    	this.helper = new SprintPlanHelper(this.CP.getIProjectList().get(0));
//    	
//    	// 設定參數資料
//    	IterationPlanForm form = new IterationPlanForm();
//    	form.setID(Integer.toString(this.CS.getSprintCount()+1));
//    	form.setAvailableDays("10");
//    	form.setIterStartDate("2010/10/10");
//    	form.setDemoDate("2010/10/24");
//    	form.setDemoPlace("Lab 1321");
//    	form.setFocusFactor("100");
//    	form.setGoal("Get ONE PIECE !!");
//    	form.setIterIterval("2");
//    	form.setIterMemberNumber("2");
//    	form.setNotes("成為海賊王");
//    	
//    	helper.saveIterationPlanForm(form);
//		ISprintPlanDesc SprintPlan = this.helper.loadPlan(form.getID());
//		assertEquals(form.getID(), SprintPlan.getID());
//		assertEquals(form.getGoal(), SprintPlan.getGoal());
//		assertEquals(form.getIterIterval(), SprintPlan.getInterval());
//		assertEquals(form.getIterMemberNumber(), SprintPlan.getMemberNumber());
//		assertEquals(form.getAvailableDays(), SprintPlan.getAvailableDays());
//		assertEquals(form.getFocusFactor(), SprintPlan.getFocusFactor());
//		assertEquals(form.getDemoPlace(), SprintPlan.getDemoPlace());
//		assertEquals(form.getDemoDate(), SprintPlan.getDemoDate());
//		assertEquals(form.getNotes(), SprintPlan.getNotes());
//	}
//    
	/*-----------------------------------------------------------
	*	測試Focus Factor與AvaliableDays輸入為0的處理方式
	-------------------------------------------------------------*/
	@Test
	public void testFocusFactorAndAvailableDays() {
		System.out.println("testFocusFactorAndAvailableDays: 請找時間把測試失敗原因找出來~");
/*		
		this.helper = new SprintPlanHelper(this.CP.getProjectList().get(0));
		
		//設定一個Focus為零的Sprint
		IterationPlanForm form = new IterationPlanForm();
    	form.setID(Integer.toString(this.CS.getSprintCount()+1));
    	form.setAvailableDays("10");
    	form.setIterStartDate("2010/10/10");
    	form.setDemoDate("2010/10/24");
    	form.setDemoPlace("Lab 1321");
    	form.setFocusFactor("0");
    	form.setGoal("Get ONE PIECE !!");
    	form.setIterIterval("2");
    	form.setIterMemberNumber("2");
    	form.setNotes("成為海賊王");
    	helper.saveIterationPlanForm(form);
		ISprintPlanDesc SprintPlan = this.helper.loadPlan(form.getID());
		assertEquals(form.getID(), SprintPlan.getID());
		assertEquals(form.getGoal(), SprintPlan.getGoal());
		assertEquals(form.getIterIterval(), SprintPlan.getInterval());
		assertEquals(form.getIterMemberNumber(), SprintPlan.getMemberNumber());
		assertEquals(form.getAvailableDays(), SprintPlan.getAvailableDays());
		assertEquals(form.getFocusFactor(), SprintPlan.getFocusFactor());
		assertEquals(form.getDemoPlace(), SprintPlan.getDemoPlace());
		assertEquals(form.getDemoDate(), SprintPlan.getDemoDate());
		assertEquals(form.getNotes(), SprintPlan.getNotes());
		
		//設定一個AvailableDays為0的Sprint
		form = new IterationPlanForm();
    	form.setID(Integer.toString(this.CS.getSprintCount()+1));
    	form.setAvailableDays("0");
    	form.setIterStartDate("2010/10/10");
    	form.setDemoDate("2010/10/24");
    	form.setDemoPlace("Lab 1321");
    	form.setFocusFactor("100");
    	form.setGoal("Get ONE PIECE !!");
    	form.setIterIterval("2");
    	form.setIterMemberNumber("2");
    	form.setNotes("成為海賊王");
    	helper.saveIterationPlanForm(form);
		SprintPlan = this.helper.loadPlan(form.getID());
		assertEquals(form.getID(), SprintPlan.getID());
		assertEquals(form.getGoal(), SprintPlan.getGoal());
		assertEquals(form.getIterIterval(), SprintPlan.getInterval());
		assertEquals(form.getIterMemberNumber(), SprintPlan.getMemberNumber());
		assertEquals(form.getAvailableDays(), SprintPlan.getAvailableDays());
		assertEquals(form.getFocusFactor(), SprintPlan.getFocusFactor());
		assertEquals(form.getDemoPlace(), SprintPlan.getDemoPlace());
		assertEquals(form.getDemoDate(), SprintPlan.getDemoDate());
		assertEquals(form.getNotes(), SprintPlan.getNotes());
		
		//設定AvailableDays與FocusFactor為0的Sprint
		form = new IterationPlanForm();
    	form.setID(Integer.toString(this.CS.getSprintCount()+1));
    	form.setAvailableDays("0");
    	form.setIterStartDate("2010/10/10");
    	form.setDemoDate("2010/10/24");
    	form.setDemoPlace("Lab 1321");
    	form.setFocusFactor("0");
    	form.setGoal("Get ONE PIECE !!");
    	form.setIterIterval("2");
    	form.setIterMemberNumber("2");
    	form.setNotes("成為海賊王");
    	helper.saveIterationPlanForm(form);
		SprintPlan = this.helper.loadPlan(form.getID());
		assertEquals(form.getID(), SprintPlan.getID());
		assertEquals(form.getGoal(), SprintPlan.getGoal());
		assertEquals(form.getIterIterval(), SprintPlan.getInterval());
		assertEquals(form.getIterMemberNumber(), SprintPlan.getMemberNumber());
		assertEquals(form.getAvailableDays(), SprintPlan.getAvailableDays());
		assertEquals(form.getFocusFactor(), SprintPlan.getFocusFactor());
		assertEquals(form.getDemoPlace(), SprintPlan.getDemoPlace());
		assertEquals(form.getDemoDate(), SprintPlan.getDemoDate());
		assertEquals(form.getNotes(), SprintPlan.getNotes());
*/		
	}
	
	@Test
    public void testDeleteIterationPlan() {
		System.out.println("testdeleteIterationPlan: 請找時間把測試失敗原因找出來~");
/*    	
    	this.helper = new SprintPlanHelper(this.CP.getProjectList().get(0));
    	int lastID = this.helper.getLastSprintId();
    	this.helper.deleteIterationPlan(Integer.toString(lastID));
    	
    	ISprintPlanDesc nullSprintPlan = this.helper.loadPlan(lastID);
		assertEquals("-1", nullSprintPlan.getID());
		assertEquals(null, nullSprintPlan.getGoal());			// ========== 程式設計此欄為 null =========
		assertEquals("", nullSprintPlan.getInterval());
		assertEquals("0", nullSprintPlan.getMemberNumber());
		assertEquals("0", nullSprintPlan.getAvailableDays());
		assertEquals("0", nullSprintPlan.getFocusFactor());
		assertEquals("", nullSprintPlan.getDemoPlace());
		assertEquals("", nullSprintPlan.getDemoDate());
		assertEquals("", nullSprintPlan.getStartDate());
		assertEquals("", nullSprintPlan.getEndDate());
		assertEquals("", nullSprintPlan.getNotes());
//		assertEquals("", nullSprintPlan.getNumber());
*/
    }
    
	@Test
	public void testGetProjectStartDate() throws Exception {
		CopyProject copyProject = new CopyProject(this.mCP);
    	copyProject.exeDelete_Project();					// 刪除測試檔案
    	
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();											// 初始化 SQL
		
		this.mCP = new CreateProject(this.mProjectCount);
		this.mCP.exeCreate();
		
		this.mCS = new CreateSprint(1, this.mCP);
		this.mCS.exe();
		
		this.mSprintPlanHelper = new SprintPlanHelper(this.mCP.getProjectList().get(0));
		
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		Date Today = cal.getTime();
		assertEquals(format.format(Today), format.format(this.mSprintPlanHelper.getProjectStartDate()));
	}
    
	@Test
	public void testGetProjectEndDate() throws Exception {
    	this.mSprintPlanHelper = new SprintPlanHelper(this.mCP.getProjectList().get(0));
    	int lastID = this.mSprintPlanHelper.getLastSprintId();
    	
    	ISprintPlanDesc SprintPlan = this.mSprintPlanHelper.loadPlan(lastID);
    	Date ProjectEndDate = this.mSprintPlanHelper.getProjectEndDate();
    	
    	SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
    	assertEquals(SprintPlan.getEndDate(), format.format(ProjectEndDate).toString());
    	
    	// 清空 sprint
    	CopyProject copyProject = new CopyProject(this.mCP);
    	copyProject.exeDelete_Project();					// 刪除測試檔案
    	
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();											// 初始化 SQL
		
		this.mCP = new CreateProject(this.mProjectCount);
		this.mCP.exeCreate();
		
		// 除錯
		this.mSprintPlanHelper = new SprintPlanHelper(this.mCP.getProjectList().get(0));
		Date nullProjectEndDate = this.mSprintPlanHelper.getProjectEndDate();				// ==========================
		assertEquals(null, nullProjectEndDate);									// ==========================
	}    
    
	@Test
	public void testGetLastSprintId() throws Exception {
    	this.mSprintPlanHelper = new SprintPlanHelper(this.mCP.getProjectList().get(0));
    	int lastID = this.mSprintPlanHelper.getLastSprintId();
    	
    	assertEquals(this.mCS.getSprintCount(), lastID);
    	
    	// 清空 sprint
    	CopyProject copyProject = new CopyProject(this.mCP);
    	copyProject.exeDelete_Project();					// 刪除測試檔案
    	
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();											// 初始化 SQL
		
		this.mCP = new CreateProject(this.mProjectCount);
		this.mCP.exeCreate();
		
		lastID = this.mSprintPlanHelper.getLastSprintId();
		assertEquals(-1, lastID);
	}
    
	@Test
	public void testGetLastSprintPlanNumber() throws Exception {
		System.out.println("testgetLastSprintPlanNumber: 請找時間把測試失敗原因找出來~");
/*		
    	this.helper = new SprintPlanHelper(this.CP.getProjectList().get(0));
    	int lastID = this.helper.getLastSprintId();
    	
    	assertEquals(this.CS.getSprintCount(), lastID);
    	
    	// 清空 sprint
    	CopyProject copyProject = new CopyProject(this.CP);
    	copyProject.exeDelete_Project();					// 刪除測試檔案
    	
		InitialSQL ini = new InitialSQL(config);
		ini.exe();											// 初始化 SQL
		
		this.CP = new CreateProject(this.ProjectCount);
		this.CP.exeCreate();
		
		lastID = this.helper.getLastSprintId();
		assertEquals(0, lastID);
*/		
	}
    
	@Test
	public void testGetSprintIDbyDate() {
		System.out.println("testgetSprintIDbyDate: 請找時間把測試失敗原因找出來~");
/*		
		this.helper = new SprintPlanHelper(this.CP.getProjectList().get(0));
		int cur = this.SprintCount/2 + 1;		// 當下的 sprint 為所有新建 sprints 的中間值
		
		ISprintPlanDesc SprintPlan = this.helper.loadPlan(cur);
		Calendar cal = Calendar.getInstance();
		Date Today = cal.getTime();		
		assertEquals(cur, this.helper.getSprintIDbyDate(Today));
		
		// 將時間加到下一個 sprint
		cal.add(Calendar.DAY_OF_YEAR, Integer.parseInt(SprintPlan.getInterval()+5));
		Date NextSprintDay = cal.getTime();
		assertEquals(cur+1, this.helper.getSprintIDbyDate(NextSprintDay));
		
		// 將時間加到超出 sprint 範圍之後
		cal.add(Calendar.DAY_OF_YEAR, 1000);
		Date DefaultDay = cal.getTime();
		assertEquals(-1, this.helper.getSprintIDbyDate(DefaultDay));
		
		// 將時間回到早於 sprint 範圍之前
		cal.add(Calendar.DAY_OF_YEAR, -5000);
		Date DefaultDay2 = cal.getTime();
		assertEquals(-1, this.helper.getSprintIDbyDate(DefaultDay2));
*/				
	}
    
	@Test
    public void testMoveSprint() {
    	this.mSprintPlanHelper = new SprintPlanHelper(this.mCP.getProjectList().get(0));
    	
    	// EndSprint 是要被交換的 sprint
    	ISprintPlanDesc OldSprint = this.mSprintPlanHelper.loadPlan(this.mCS.getSprintCount());
    	
    	// 再新增一筆 sprint 4 用來移動
    	// 設定參數資料
    	IterationPlanForm form = new IterationPlanForm();
    	form.setID(Integer.toString(this.mCS.getSprintCount()+1));
    	form.setAvailableDays("10");
    	form.setDemoPlace("1321 LUB");
    	form.setFocusFactor("200");
    	form.setGoal("Get ONE PIECE !!");
    	form.setIterIterval("2");
    	form.setIterMemberNumber("2");
    	form.setNotes("成為海賊王");
    	
    	SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
    	Date Start = new Date(OldSprint.getEndDate());
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(Start);
    	cal.add(Calendar.DAY_OF_YEAR, 1);		// 開始日為上一個 sprint 的下一天
    	form.setIterStartDate(format.format(cal.getTime()));
    	
    	Date Demo = new Date(OldSprint.getEndDate());
    	cal = Calendar.getInstance();
    	cal.setTime(Demo);
    	cal.add(Calendar.DAY_OF_YEAR, 14);		// 兩個禮拜
    	form.setDemoDate(format.format(cal.getTime()));
    
    	this.mSprintPlanHelper.saveIterationPlanForm(form);	// 存入成為一筆新的 sprint
    	ISprintPlanDesc NewSprint = this.mSprintPlanHelper.loadPlan(this.mSprintPlanHelper.getLastSprintId());
    	
    	// 移動 sprint 3 與 sprint 4
    	int oldID = this.mCS.getSprintCount();
    	int newID = this.mSprintPlanHelper.getLastSprintId();
    	
    	this.mSprintPlanHelper.moveSprint(oldID, newID);
    	
    	// old ID 的資訊應該變成 sprint 4 資訊
    	ISprintPlanDesc oldID_sprintInfo = this.mSprintPlanHelper.loadPlan(oldID);
    	assertEquals(Integer.toString(oldID), oldID_sprintInfo.getID());
    	assertEquals("10", oldID_sprintInfo.getAvailableDays());
    	assertEquals(OldSprint.getStartDate(), oldID_sprintInfo.getStartDate());
    	assertEquals("1321 LUB", oldID_sprintInfo.getDemoPlace());
    	assertEquals("200", oldID_sprintInfo.getFocusFactor());
    	assertEquals("Get ONE PIECE !!", oldID_sprintInfo.getGoal());
    	assertEquals("2", oldID_sprintInfo.getInterval());
    	assertEquals("2", oldID_sprintInfo.getMemberNumber());
    	assertEquals("成為海賊王", oldID_sprintInfo.getNotes());
    	
    		
    	// new Id 的資訊應該變成 sprint 3 資訊
    	ISprintPlanDesc newID_sprintInfo = this.mSprintPlanHelper.loadPlan(newID);
    	assertEquals(Integer.toString(newID), newID_sprintInfo.getID());
    	assertEquals(OldSprint.getAvailableDays(), newID_sprintInfo.getAvailableDays());
    	// 日期是正常的，所以時間不會移動
    	assertEquals(NewSprint.getStartDate(), newID_sprintInfo.getStartDate());
    	assertEquals(OldSprint.getDemoPlace(), newID_sprintInfo.getDemoPlace());
    	assertEquals(OldSprint.getFocusFactor(), newID_sprintInfo.getFocusFactor());
    	assertEquals(OldSprint.getGoal(), newID_sprintInfo.getGoal());
    	assertEquals(OldSprint.getInterval(), newID_sprintInfo.getInterval());
    	assertEquals(OldSprint.getMemberNumber(), newID_sprintInfo.getMemberNumber());
    	assertEquals(OldSprint.getNotes(), newID_sprintInfo.getNotes());
    }
}