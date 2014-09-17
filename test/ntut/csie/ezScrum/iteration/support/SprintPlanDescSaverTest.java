package ntut.csie.ezScrum.iteration.support;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.iteration.iternal.SprintPlanDesc;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.mapper.SprintPlanMapper;
import ntut.csie.jcis.resource.core.IProject;

public class SprintPlanDescSaverTest extends TestCase {
	
	private Configuration configuration = null;
	private CreateProject CP = null;
	private IProject project = null;
	private SprintPlanMapper saver = null;
	
	public SprintPlanDescSaverTest(String method) {
		super(method);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.store();
		
		// initial SQL
		InitialSQL init = new InitialSQL(configuration);
		init.exe();
		
		// create project
		this.CP = new CreateProject(1);
		this.CP.exeCreate();
		this.project = this.CP.getProjectList().get(0);
		
		// initial loader
		this.saver = new SprintPlanMapper(this.project);
		
		// release
		init = null;
	}
	
	protected void tearDown() throws Exception {
		// initial SQL
		InitialSQL init = new InitialSQL(configuration);
		init.exe();
		
		// copy and delete test project
		CopyProject cp = new CopyProject(this.CP);
		cp.exeDelete_Project();
		
		configuration.setTestMode(false);
		configuration.store();
		
		// release
		init = null;
		cp = null;
		this.CP = null;
		this.project = null;
		configuration = null;
		
		super.tearDown();
	}
	
	public void testsave_empty() {
		// 測試讀取資料尚未存入不會出錯
		ISprintPlanDesc ActualDesc_1 = new SprintPlanDesc();
		this.saver.addSprintPlan(ActualDesc_1);
	}

	public void testsave() {
		// 測試讀取資料尚未存入
		SprintPlanMapper loader = new SprintPlanMapper(this.project);
		List<ISprintPlanDesc> descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 0);

		// 設定時間
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		
		// 第一筆假資料
		SprintPlanDesc ActualDesc_1 = new SprintPlanDesc();
		ActualDesc_1.setID("1");
		ActualDesc_1.setGoal("Sprint Goal - 1");
		ActualDesc_1.setInterval("2");
		ActualDesc_1.setMemberNumber("5");
		ActualDesc_1.setFocusFactor("100");
		ActualDesc_1.setAvailableDays("20");
		ActualDesc_1.setNotes("Sprint Note - 1");
		ActualDesc_1.setStartDate(getDate(today, 0));
		ActualDesc_1.setDemoDate(getDate(today, 14));
		ActualDesc_1.setDemoPlace("Lab 1321");	
		this.saver.addSprintPlan(ActualDesc_1);
		
		// 驗證加入的資料是否正確
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 1);
		ISprintPlanDesc ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "1");
		assertEquals(ExpectedDesc.getGoal(), ActualDesc_1.getGoal());
		assertEquals(ExpectedDesc.getGoal(), "Sprint Goal - 1");
		assertEquals(ExpectedDesc.getInterval(), ActualDesc_1.getInterval());
		assertEquals(ExpectedDesc.getInterval(), "2");
		assertEquals(ExpectedDesc.getMemberNumber(), ActualDesc_1.getMemberNumber());
		assertEquals(ExpectedDesc.getMemberNumber(), "5");
		assertEquals(ExpectedDesc.getFocusFactor(), ActualDesc_1.getFocusFactor());
		assertEquals(ExpectedDesc.getFocusFactor(), "100");
		assertEquals(ExpectedDesc.getAvailableDays(), ActualDesc_1.getAvailableDays());
		assertEquals(ExpectedDesc.getAvailableDays(), "20");
		assertEquals(ExpectedDesc.getNotes(), ActualDesc_1.getNotes());
		assertEquals(ExpectedDesc.getNotes(), "Sprint Note - 1");
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 0));
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 13));
		assertEquals(ExpectedDesc.getDemoDate(), ActualDesc_1.getDemoDate());
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 14));
		assertEquals(ExpectedDesc.getDemoPlace(), ActualDesc_1.getDemoPlace());
		assertEquals(ExpectedDesc.getDemoPlace(), "Lab 1321");
	}
	
	public void testsave_duplicate() {
		// 測試讀取資料尚未存入
		SprintPlanMapper loader = new SprintPlanMapper(this.project);
		List<ISprintPlanDesc> descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 0);

		// 設定時間
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		
		// 第一筆假資料
		ISprintPlanDesc ActualDesc_1 = new SprintPlanDesc();
		ActualDesc_1.setID("1");
		ActualDesc_1.setGoal("Sprint Goal - 1");
		ActualDesc_1.setInterval("2");
		ActualDesc_1.setMemberNumber("5");
		ActualDesc_1.setFocusFactor("100");
		ActualDesc_1.setAvailableDays("20");
		ActualDesc_1.setNotes("Sprint Note - 1");
		ActualDesc_1.setStartDate(getDate(today, 0));
		ActualDesc_1.setDemoDate(getDate(today, 14));
		ActualDesc_1.setDemoPlace("Lab 1321");		
		this.saver.addSprintPlan(ActualDesc_1);
		
		// 驗證加入的資料是否正確
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 1);
		ISprintPlanDesc ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "1");
		assertEquals(ExpectedDesc.getGoal(), ActualDesc_1.getGoal());
		assertEquals(ExpectedDesc.getGoal(), "Sprint Goal - 1");
		assertEquals(ExpectedDesc.getInterval(), ActualDesc_1.getInterval());
		assertEquals(ExpectedDesc.getInterval(), "2");
		assertEquals(ExpectedDesc.getMemberNumber(), ActualDesc_1.getMemberNumber());
		assertEquals(ExpectedDesc.getMemberNumber(), "5");
		assertEquals(ExpectedDesc.getFocusFactor(), ActualDesc_1.getFocusFactor());
		assertEquals(ExpectedDesc.getFocusFactor(), "100");
		assertEquals(ExpectedDesc.getAvailableDays(), ActualDesc_1.getAvailableDays());
		assertEquals(ExpectedDesc.getAvailableDays(), "20");
		assertEquals(ExpectedDesc.getNotes(), ActualDesc_1.getNotes());
		assertEquals(ExpectedDesc.getNotes(), "Sprint Note - 1");
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 0));
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 13));
		assertEquals(ExpectedDesc.getDemoDate(), ActualDesc_1.getDemoDate());
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 14));
		assertEquals(ExpectedDesc.getDemoPlace(), ActualDesc_1.getDemoPlace());
		assertEquals(ExpectedDesc.getDemoPlace(), "Lab 1321");
		
		this.saver.addSprintPlan(ActualDesc_1);
		this.saver.addSprintPlan(ActualDesc_1);
		this.saver.addSprintPlan(ActualDesc_1);
		
		// 驗證加入的資料是否正確
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 1);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "1");
		assertEquals(ExpectedDesc.getGoal(), ActualDesc_1.getGoal());
		assertEquals(ExpectedDesc.getGoal(), "Sprint Goal - 1");
		assertEquals(ExpectedDesc.getInterval(), ActualDesc_1.getInterval());
		assertEquals(ExpectedDesc.getInterval(), "2");
		assertEquals(ExpectedDesc.getMemberNumber(), ActualDesc_1.getMemberNumber());
		assertEquals(ExpectedDesc.getMemberNumber(), "5");
		assertEquals(ExpectedDesc.getFocusFactor(), ActualDesc_1.getFocusFactor());
		assertEquals(ExpectedDesc.getFocusFactor(), "100");
		assertEquals(ExpectedDesc.getAvailableDays(), ActualDesc_1.getAvailableDays());
		assertEquals(ExpectedDesc.getAvailableDays(), "20");
		assertEquals(ExpectedDesc.getNotes(), ActualDesc_1.getNotes());
		assertEquals(ExpectedDesc.getNotes(), "Sprint Note - 1");
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 0));
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 13));
		assertEquals(ExpectedDesc.getDemoDate(), ActualDesc_1.getDemoDate());
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 14));
		assertEquals(ExpectedDesc.getDemoPlace(), ActualDesc_1.getDemoPlace());
		assertEquals(ExpectedDesc.getDemoPlace(), "Lab 1321");		
	}
	
	public void testedit_empty() {
		// 測試讀取資料尚未存入不會出錯
		ISprintPlanDesc ActualDesc_1 = new SprintPlanDesc();
		this.saver.updateSprintPlan(ActualDesc_1);		
	}
	
	public void testedit_ID() {
		// 測試讀取資料尚未存入
		SprintPlanMapper loader = new SprintPlanMapper(this.project);
		List<ISprintPlanDesc> descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 0);

		// 設定時間
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		
		// 第一筆假資料
		ISprintPlanDesc ActualDesc_1 = new SprintPlanDesc();
		ActualDesc_1.setID("1");
		ActualDesc_1.setGoal("Sprint Goal - 1");
		ActualDesc_1.setInterval("2");
		ActualDesc_1.setMemberNumber("5");
		ActualDesc_1.setFocusFactor("100");
		ActualDesc_1.setAvailableDays("20");
		ActualDesc_1.setNotes("Sprint Note - 1");
		ActualDesc_1.setStartDate(getDate(today, 0));
		ActualDesc_1.setDemoDate(getDate(today, 14));
		ActualDesc_1.setDemoPlace("Lab 1321");		
		this.saver.addSprintPlan(ActualDesc_1);
		
		// 驗證加入的資料是否正確
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 1);
		ISprintPlanDesc ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "1");
		assertEquals(ExpectedDesc.getGoal(), ActualDesc_1.getGoal());
		assertEquals(ExpectedDesc.getGoal(), "Sprint Goal - 1");
		assertEquals(ExpectedDesc.getInterval(), ActualDesc_1.getInterval());
		assertEquals(ExpectedDesc.getInterval(), "2");
		assertEquals(ExpectedDesc.getMemberNumber(), ActualDesc_1.getMemberNumber());
		assertEquals(ExpectedDesc.getMemberNumber(), "5");
		assertEquals(ExpectedDesc.getFocusFactor(), ActualDesc_1.getFocusFactor());
		assertEquals(ExpectedDesc.getFocusFactor(), "100");
		assertEquals(ExpectedDesc.getAvailableDays(), ActualDesc_1.getAvailableDays());
		assertEquals(ExpectedDesc.getAvailableDays(), "20");
		assertEquals(ExpectedDesc.getNotes(), ActualDesc_1.getNotes());
		assertEquals(ExpectedDesc.getNotes(), "Sprint Note - 1");
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 0));
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 13));
		assertEquals(ExpectedDesc.getDemoDate(), ActualDesc_1.getDemoDate());
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 14));
		assertEquals(ExpectedDesc.getDemoPlace(), ActualDesc_1.getDemoPlace());
		assertEquals(ExpectedDesc.getDemoPlace(), "Lab 1321");
		
		// 修改 ActualDesc_1 的 ID 並且編輯存入，但是不會有任何異動 
		ActualDesc_1.setID("2");
		this.saver.updateSprintPlan(ActualDesc_1);
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 1);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "1");
		assertEquals(ExpectedDesc.getGoal(), ActualDesc_1.getGoal());
		assertEquals(ExpectedDesc.getGoal(), "Sprint Goal - 1");
		assertEquals(ExpectedDesc.getInterval(), ActualDesc_1.getInterval());
		assertEquals(ExpectedDesc.getInterval(), "2");
		assertEquals(ExpectedDesc.getMemberNumber(), ActualDesc_1.getMemberNumber());
		assertEquals(ExpectedDesc.getMemberNumber(), "5");
		assertEquals(ExpectedDesc.getFocusFactor(), ActualDesc_1.getFocusFactor());
		assertEquals(ExpectedDesc.getFocusFactor(), "100");
		assertEquals(ExpectedDesc.getAvailableDays(), ActualDesc_1.getAvailableDays());
		assertEquals(ExpectedDesc.getAvailableDays(), "20");
		assertEquals(ExpectedDesc.getNotes(), ActualDesc_1.getNotes());
		assertEquals(ExpectedDesc.getNotes(), "Sprint Note - 1");
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 0));
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 13));
		assertEquals(ExpectedDesc.getDemoDate(), ActualDesc_1.getDemoDate());
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 14));
		assertEquals(ExpectedDesc.getDemoPlace(), ActualDesc_1.getDemoPlace());
		assertEquals(ExpectedDesc.getDemoPlace(), "Lab 1321");
	}
	
	public void testedit_Goal() {
		// 測試讀取資料尚未存入
		SprintPlanMapper loader = new SprintPlanMapper(this.project);
		List<ISprintPlanDesc> descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 0);

		// 設定時間
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		
		// 第一筆假資料
		ISprintPlanDesc ActualDesc_1 = new SprintPlanDesc();
		ActualDesc_1.setID("1");
		ActualDesc_1.setGoal("Sprint Goal - 1");
		ActualDesc_1.setInterval("2");
		ActualDesc_1.setMemberNumber("5");
		ActualDesc_1.setFocusFactor("100");
		ActualDesc_1.setAvailableDays("20");
		ActualDesc_1.setNotes("Sprint Note - 1");
		ActualDesc_1.setStartDate(getDate(today, 0));
		ActualDesc_1.setDemoDate(getDate(today, 14));
		ActualDesc_1.setDemoPlace("Lab 1321");		
		this.saver.addSprintPlan(ActualDesc_1);
		
		// 驗證加入的資料是否正確
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 1);
		ISprintPlanDesc ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "1");
		assertEquals(ExpectedDesc.getGoal(), ActualDesc_1.getGoal());
		assertEquals(ExpectedDesc.getGoal(), "Sprint Goal - 1");
		assertEquals(ExpectedDesc.getInterval(), ActualDesc_1.getInterval());
		assertEquals(ExpectedDesc.getInterval(), "2");
		assertEquals(ExpectedDesc.getMemberNumber(), ActualDesc_1.getMemberNumber());
		assertEquals(ExpectedDesc.getMemberNumber(), "5");
		assertEquals(ExpectedDesc.getFocusFactor(), ActualDesc_1.getFocusFactor());
		assertEquals(ExpectedDesc.getFocusFactor(), "100");
		assertEquals(ExpectedDesc.getAvailableDays(), ActualDesc_1.getAvailableDays());
		assertEquals(ExpectedDesc.getAvailableDays(), "20");
		assertEquals(ExpectedDesc.getNotes(), ActualDesc_1.getNotes());
		assertEquals(ExpectedDesc.getNotes(), "Sprint Note - 1");
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 0));
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 13));
		assertEquals(ExpectedDesc.getDemoDate(), ActualDesc_1.getDemoDate());
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 14));
		assertEquals(ExpectedDesc.getDemoPlace(), ActualDesc_1.getDemoPlace());
		assertEquals(ExpectedDesc.getDemoPlace(), "Lab 1321");
		
		// 修改 ActualDesc_1 的 ID 並且編輯存入，但是不會有任何異動 
		ActualDesc_1.setGoal("New Goal - 2");
		this.saver.updateSprintPlan(ActualDesc_1);
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 1);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "1");
		assertEquals(ExpectedDesc.getGoal(), ActualDesc_1.getGoal());
		assertEquals(ExpectedDesc.getGoal(), "New Goal - 2");
		assertEquals(ExpectedDesc.getInterval(), ActualDesc_1.getInterval());
		assertEquals(ExpectedDesc.getInterval(), "2");
		assertEquals(ExpectedDesc.getMemberNumber(), ActualDesc_1.getMemberNumber());
		assertEquals(ExpectedDesc.getMemberNumber(), "5");
		assertEquals(ExpectedDesc.getFocusFactor(), ActualDesc_1.getFocusFactor());
		assertEquals(ExpectedDesc.getFocusFactor(), "100");
		assertEquals(ExpectedDesc.getAvailableDays(), ActualDesc_1.getAvailableDays());
		assertEquals(ExpectedDesc.getAvailableDays(), "20");
		assertEquals(ExpectedDesc.getNotes(), ActualDesc_1.getNotes());
		assertEquals(ExpectedDesc.getNotes(), "Sprint Note - 1");
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 0));
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 13));
		assertEquals(ExpectedDesc.getDemoDate(), ActualDesc_1.getDemoDate());
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 14));
		assertEquals(ExpectedDesc.getDemoPlace(), ActualDesc_1.getDemoPlace());
		assertEquals(ExpectedDesc.getDemoPlace(), "Lab 1321");
	}
	
	public void testedit_Interval() {
		// 測試讀取資料尚未存入
		SprintPlanMapper loader = new SprintPlanMapper(this.project);
		List<ISprintPlanDesc> descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 0);

		// 設定時間
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		
		// 第一筆假資料
		ISprintPlanDesc ActualDesc_1 = new SprintPlanDesc();
		ActualDesc_1.setID("1");
		ActualDesc_1.setGoal("Sprint Goal - 1");
		ActualDesc_1.setInterval("2");
		ActualDesc_1.setMemberNumber("5");
		ActualDesc_1.setFocusFactor("100");
		ActualDesc_1.setAvailableDays("20");
		ActualDesc_1.setNotes("Sprint Note - 1");
		ActualDesc_1.setStartDate(getDate(today, 0));
		ActualDesc_1.setDemoDate(getDate(today, 28));
		ActualDesc_1.setDemoPlace("Lab 1321");		
		this.saver.addSprintPlan(ActualDesc_1);
		
		// 驗證加入的資料是否正確
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 1);
		ISprintPlanDesc ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "1");
		assertEquals(ExpectedDesc.getGoal(), ActualDesc_1.getGoal());
		assertEquals(ExpectedDesc.getGoal(), "Sprint Goal - 1");
		assertEquals(ExpectedDesc.getInterval(), ActualDesc_1.getInterval());
		assertEquals(ExpectedDesc.getInterval(), "2");
		assertEquals(ExpectedDesc.getMemberNumber(), ActualDesc_1.getMemberNumber());
		assertEquals(ExpectedDesc.getMemberNumber(), "5");
		assertEquals(ExpectedDesc.getFocusFactor(), ActualDesc_1.getFocusFactor());
		assertEquals(ExpectedDesc.getFocusFactor(), "100");
		assertEquals(ExpectedDesc.getAvailableDays(), ActualDesc_1.getAvailableDays());
		assertEquals(ExpectedDesc.getAvailableDays(), "20");
		assertEquals(ExpectedDesc.getNotes(), ActualDesc_1.getNotes());
		assertEquals(ExpectedDesc.getNotes(), "Sprint Note - 1");
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 0));
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 13));
		assertEquals(ExpectedDesc.getDemoDate(), ActualDesc_1.getDemoDate());
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 28));
		assertEquals(ExpectedDesc.getDemoPlace(), ActualDesc_1.getDemoPlace());
		assertEquals(ExpectedDesc.getDemoPlace(), "Lab 1321");
		
		// 修改 ActualDesc_1 的 ID 並且編輯存入，但是不會有任何異動 
		ActualDesc_1.setInterval("4");
		this.saver.updateSprintPlan(ActualDesc_1);
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 1);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "1");
		assertEquals(ExpectedDesc.getGoal(), ActualDesc_1.getGoal());
		assertEquals(ExpectedDesc.getGoal(), "Sprint Goal - 1");
		assertEquals(ExpectedDesc.getInterval(), ActualDesc_1.getInterval());
		assertEquals(ExpectedDesc.getInterval(), "4");
		assertEquals(ExpectedDesc.getMemberNumber(), ActualDesc_1.getMemberNumber());
		assertEquals(ExpectedDesc.getMemberNumber(), "5");
		assertEquals(ExpectedDesc.getFocusFactor(), ActualDesc_1.getFocusFactor());
		assertEquals(ExpectedDesc.getFocusFactor(), "100");
		assertEquals(ExpectedDesc.getAvailableDays(), ActualDesc_1.getAvailableDays());
		assertEquals(ExpectedDesc.getAvailableDays(), "20");
		assertEquals(ExpectedDesc.getNotes(), ActualDesc_1.getNotes());
		assertEquals(ExpectedDesc.getNotes(), "Sprint Note - 1");
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 0));
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 27));
		assertEquals(ExpectedDesc.getDemoDate(), ActualDesc_1.getDemoDate());
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 28));
		assertEquals(ExpectedDesc.getDemoPlace(), ActualDesc_1.getDemoPlace());
		assertEquals(ExpectedDesc.getDemoPlace(), "Lab 1321");
	}
	
	public void testedit_MemberNumber() {
		// 測試讀取資料尚未存入
		SprintPlanMapper loader = new SprintPlanMapper(this.project);
		List<ISprintPlanDesc> descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 0);

		// 設定時間
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		
		// 第一筆假資料
		ISprintPlanDesc ActualDesc_1 = new SprintPlanDesc();
		ActualDesc_1.setID("1");
		ActualDesc_1.setGoal("Sprint Goal - 1");
		ActualDesc_1.setInterval("2");
		ActualDesc_1.setMemberNumber("5");
		ActualDesc_1.setFocusFactor("100");
		ActualDesc_1.setAvailableDays("20");
		ActualDesc_1.setNotes("Sprint Note - 1");
		ActualDesc_1.setStartDate(getDate(today, 0));
		ActualDesc_1.setDemoDate(getDate(today, 28));
		ActualDesc_1.setDemoPlace("Lab 1321");		
		this.saver.addSprintPlan(ActualDesc_1);
		
		// 驗證加入的資料是否正確
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 1);
		ISprintPlanDesc ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "1");
		assertEquals(ExpectedDesc.getGoal(), ActualDesc_1.getGoal());
		assertEquals(ExpectedDesc.getGoal(), "Sprint Goal - 1");
		assertEquals(ExpectedDesc.getInterval(), ActualDesc_1.getInterval());
		assertEquals(ExpectedDesc.getInterval(), "2");
		assertEquals(ExpectedDesc.getMemberNumber(), ActualDesc_1.getMemberNumber());
		assertEquals(ExpectedDesc.getMemberNumber(), "5");
		assertEquals(ExpectedDesc.getFocusFactor(), ActualDesc_1.getFocusFactor());
		assertEquals(ExpectedDesc.getFocusFactor(), "100");
		assertEquals(ExpectedDesc.getAvailableDays(), ActualDesc_1.getAvailableDays());
		assertEquals(ExpectedDesc.getAvailableDays(), "20");
		assertEquals(ExpectedDesc.getNotes(), ActualDesc_1.getNotes());
		assertEquals(ExpectedDesc.getNotes(), "Sprint Note - 1");
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 0));
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 13));
		assertEquals(ExpectedDesc.getDemoDate(), ActualDesc_1.getDemoDate());
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 28));
		assertEquals(ExpectedDesc.getDemoPlace(), ActualDesc_1.getDemoPlace());
		assertEquals(ExpectedDesc.getDemoPlace(), "Lab 1321");
		
		// 修改 ActualDesc_1 的 ID 並且編輯存入，但是不會有任何異動 
		ActualDesc_1.setMemberNumber("100");
		this.saver.updateSprintPlan(ActualDesc_1);
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 1);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "1");
		assertEquals(ExpectedDesc.getGoal(), ActualDesc_1.getGoal());
		assertEquals(ExpectedDesc.getGoal(), "Sprint Goal - 1");
		assertEquals(ExpectedDesc.getInterval(), ActualDesc_1.getInterval());
		assertEquals(ExpectedDesc.getInterval(), "2");
		assertEquals(ExpectedDesc.getMemberNumber(), ActualDesc_1.getMemberNumber());
		assertEquals(ExpectedDesc.getMemberNumber(), "100");
		assertEquals(ExpectedDesc.getFocusFactor(), ActualDesc_1.getFocusFactor());
		assertEquals(ExpectedDesc.getFocusFactor(), "100");
		assertEquals(ExpectedDesc.getAvailableDays(), ActualDesc_1.getAvailableDays());
		assertEquals(ExpectedDesc.getAvailableDays(), "20");
		assertEquals(ExpectedDesc.getNotes(), ActualDesc_1.getNotes());
		assertEquals(ExpectedDesc.getNotes(), "Sprint Note - 1");
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 0));
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 13));
		assertEquals(ExpectedDesc.getDemoDate(), ActualDesc_1.getDemoDate());
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 28));
		assertEquals(ExpectedDesc.getDemoPlace(), ActualDesc_1.getDemoPlace());
		assertEquals(ExpectedDesc.getDemoPlace(), "Lab 1321");
	}
	
	public void testedit_FocusFactor() {
		// 測試讀取資料尚未存入
		SprintPlanMapper loader = new SprintPlanMapper(this.project);
		List<ISprintPlanDesc> descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 0);

		// 設定時間
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		
		// 第一筆假資料
		ISprintPlanDesc ActualDesc_1 = new SprintPlanDesc();
		ActualDesc_1.setID("1");
		ActualDesc_1.setGoal("Sprint Goal - 1");
		ActualDesc_1.setInterval("2");
		ActualDesc_1.setMemberNumber("5");
		ActualDesc_1.setFocusFactor("100");
		ActualDesc_1.setAvailableDays("20");
		ActualDesc_1.setNotes("Sprint Note - 1");
		ActualDesc_1.setStartDate(getDate(today, 0));
		ActualDesc_1.setDemoDate(getDate(today, 28));
		ActualDesc_1.setDemoPlace("Lab 1321");		
		this.saver.addSprintPlan(ActualDesc_1);
		
		// 驗證加入的資料是否正確
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 1);
		ISprintPlanDesc ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "1");
		assertEquals(ExpectedDesc.getGoal(), ActualDesc_1.getGoal());
		assertEquals(ExpectedDesc.getGoal(), "Sprint Goal - 1");
		assertEquals(ExpectedDesc.getInterval(), ActualDesc_1.getInterval());
		assertEquals(ExpectedDesc.getInterval(), "2");
		assertEquals(ExpectedDesc.getMemberNumber(), ActualDesc_1.getMemberNumber());
		assertEquals(ExpectedDesc.getMemberNumber(), "5");
		assertEquals(ExpectedDesc.getFocusFactor(), ActualDesc_1.getFocusFactor());
		assertEquals(ExpectedDesc.getFocusFactor(), "100");
		assertEquals(ExpectedDesc.getAvailableDays(), ActualDesc_1.getAvailableDays());
		assertEquals(ExpectedDesc.getAvailableDays(), "20");
		assertEquals(ExpectedDesc.getNotes(), ActualDesc_1.getNotes());
		assertEquals(ExpectedDesc.getNotes(), "Sprint Note - 1");
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 0));
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 13));
		assertEquals(ExpectedDesc.getDemoDate(), ActualDesc_1.getDemoDate());
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 28));
		assertEquals(ExpectedDesc.getDemoPlace(), ActualDesc_1.getDemoPlace());
		assertEquals(ExpectedDesc.getDemoPlace(), "Lab 1321");
		
		// 修改 ActualDesc_1 的 ID 並且編輯存入，但是不會有任何異動 
		ActualDesc_1.setFocusFactor("50");
		this.saver.updateSprintPlan(ActualDesc_1);
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 1);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "1");
		assertEquals(ExpectedDesc.getGoal(), ActualDesc_1.getGoal());
		assertEquals(ExpectedDesc.getGoal(), "Sprint Goal - 1");
		assertEquals(ExpectedDesc.getInterval(), ActualDesc_1.getInterval());
		assertEquals(ExpectedDesc.getInterval(), "2");
		assertEquals(ExpectedDesc.getMemberNumber(), ActualDesc_1.getMemberNumber());
		assertEquals(ExpectedDesc.getMemberNumber(), "5");
		assertEquals(ExpectedDesc.getFocusFactor(), ActualDesc_1.getFocusFactor());
		assertEquals(ExpectedDesc.getFocusFactor(), "50");
		assertEquals(ExpectedDesc.getAvailableDays(), ActualDesc_1.getAvailableDays());
		assertEquals(ExpectedDesc.getAvailableDays(), "20");
		assertEquals(ExpectedDesc.getNotes(), ActualDesc_1.getNotes());
		assertEquals(ExpectedDesc.getNotes(), "Sprint Note - 1");
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 0));
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 13));
		assertEquals(ExpectedDesc.getDemoDate(), ActualDesc_1.getDemoDate());
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 28));
		assertEquals(ExpectedDesc.getDemoPlace(), ActualDesc_1.getDemoPlace());
		assertEquals(ExpectedDesc.getDemoPlace(), "Lab 1321");
	}
	
	public void testedit_AvailableDay() {
		// 測試讀取資料尚未存入
		SprintPlanMapper loader = new SprintPlanMapper(this.project);
		List<ISprintPlanDesc> descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 0);

		// 設定時間
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		
		// 第一筆假資料
		ISprintPlanDesc ActualDesc_1 = new SprintPlanDesc();
		ActualDesc_1.setID("1");
		ActualDesc_1.setGoal("Sprint Goal - 1");
		ActualDesc_1.setInterval("2");
		ActualDesc_1.setMemberNumber("5");
		ActualDesc_1.setFocusFactor("100");
		ActualDesc_1.setAvailableDays("20");
		ActualDesc_1.setNotes("Sprint Note - 1");
		ActualDesc_1.setStartDate(getDate(today, 0));
		ActualDesc_1.setDemoDate(getDate(today, 28));
		ActualDesc_1.setDemoPlace("Lab 1321");		
		this.saver.addSprintPlan(ActualDesc_1);
		
		// 驗證加入的資料是否正確
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 1);
		ISprintPlanDesc ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "1");
		assertEquals(ExpectedDesc.getGoal(), ActualDesc_1.getGoal());
		assertEquals(ExpectedDesc.getGoal(), "Sprint Goal - 1");
		assertEquals(ExpectedDesc.getInterval(), ActualDesc_1.getInterval());
		assertEquals(ExpectedDesc.getInterval(), "2");
		assertEquals(ExpectedDesc.getMemberNumber(), ActualDesc_1.getMemberNumber());
		assertEquals(ExpectedDesc.getMemberNumber(), "5");
		assertEquals(ExpectedDesc.getFocusFactor(), ActualDesc_1.getFocusFactor());
		assertEquals(ExpectedDesc.getFocusFactor(), "100");
		assertEquals(ExpectedDesc.getAvailableDays(), ActualDesc_1.getAvailableDays());
		assertEquals(ExpectedDesc.getAvailableDays(), "20");
		assertEquals(ExpectedDesc.getNotes(), ActualDesc_1.getNotes());
		assertEquals(ExpectedDesc.getNotes(), "Sprint Note - 1");
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 0));
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 13));
		assertEquals(ExpectedDesc.getDemoDate(), ActualDesc_1.getDemoDate());
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 28));
		assertEquals(ExpectedDesc.getDemoPlace(), ActualDesc_1.getDemoPlace());
		assertEquals(ExpectedDesc.getDemoPlace(), "Lab 1321");
		
		// 修改 ActualDesc_1 的 ID 並且編輯存入，但是不會有任何異動 
		ActualDesc_1.setAvailableDays("25");
		this.saver.updateSprintPlan(ActualDesc_1);
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 1);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "1");
		assertEquals(ExpectedDesc.getGoal(), ActualDesc_1.getGoal());
		assertEquals(ExpectedDesc.getGoal(), "Sprint Goal - 1");
		assertEquals(ExpectedDesc.getInterval(), ActualDesc_1.getInterval());
		assertEquals(ExpectedDesc.getInterval(), "2");
		assertEquals(ExpectedDesc.getMemberNumber(), ActualDesc_1.getMemberNumber());
		assertEquals(ExpectedDesc.getMemberNumber(), "5");
		assertEquals(ExpectedDesc.getFocusFactor(), ActualDesc_1.getFocusFactor());
		assertEquals(ExpectedDesc.getFocusFactor(), "100");
		assertEquals(ExpectedDesc.getAvailableDays(), ActualDesc_1.getAvailableDays());
		assertEquals(ExpectedDesc.getAvailableDays(), "25");
		assertEquals(ExpectedDesc.getNotes(), ActualDesc_1.getNotes());
		assertEquals(ExpectedDesc.getNotes(), "Sprint Note - 1");
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 0));
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 13));
		assertEquals(ExpectedDesc.getDemoDate(), ActualDesc_1.getDemoDate());
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 28));
		assertEquals(ExpectedDesc.getDemoPlace(), ActualDesc_1.getDemoPlace());
		assertEquals(ExpectedDesc.getDemoPlace(), "Lab 1321");
	}
	
	public void testedit_Notes() {
		// 測試讀取資料尚未存入
		SprintPlanMapper loader = new SprintPlanMapper(this.project);
		List<ISprintPlanDesc> descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 0);

		// 設定時間
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		
		// 第一筆假資料
		ISprintPlanDesc ActualDesc_1 = new SprintPlanDesc();
		ActualDesc_1.setID("1");
		ActualDesc_1.setGoal("Sprint Goal - 1");
		ActualDesc_1.setInterval("2");
		ActualDesc_1.setMemberNumber("5");
		ActualDesc_1.setFocusFactor("100");
		ActualDesc_1.setAvailableDays("20");
		ActualDesc_1.setNotes("Sprint Note - 1");
		ActualDesc_1.setStartDate(getDate(today, 0));
		ActualDesc_1.setDemoDate(getDate(today, 28));
		ActualDesc_1.setDemoPlace("Lab 1321");		
		this.saver.addSprintPlan(ActualDesc_1);
		
		// 驗證加入的資料是否正確
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 1);
		ISprintPlanDesc ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "1");
		assertEquals(ExpectedDesc.getGoal(), ActualDesc_1.getGoal());
		assertEquals(ExpectedDesc.getGoal(), "Sprint Goal - 1");
		assertEquals(ExpectedDesc.getInterval(), ActualDesc_1.getInterval());
		assertEquals(ExpectedDesc.getInterval(), "2");
		assertEquals(ExpectedDesc.getMemberNumber(), ActualDesc_1.getMemberNumber());
		assertEquals(ExpectedDesc.getMemberNumber(), "5");
		assertEquals(ExpectedDesc.getFocusFactor(), ActualDesc_1.getFocusFactor());
		assertEquals(ExpectedDesc.getFocusFactor(), "100");
		assertEquals(ExpectedDesc.getAvailableDays(), ActualDesc_1.getAvailableDays());
		assertEquals(ExpectedDesc.getAvailableDays(), "20");
		assertEquals(ExpectedDesc.getNotes(), ActualDesc_1.getNotes());
		assertEquals(ExpectedDesc.getNotes(), "Sprint Note - 1");
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 0));
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 13));
		assertEquals(ExpectedDesc.getDemoDate(), ActualDesc_1.getDemoDate());
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 28));
		assertEquals(ExpectedDesc.getDemoPlace(), ActualDesc_1.getDemoPlace());
		assertEquals(ExpectedDesc.getDemoPlace(), "Lab 1321");
		
		// 修改 ActualDesc_1 的 ID 並且編輯存入，但是不會有任何異動 
		ActualDesc_1.setNotes("New Notes - 2");
		this.saver.updateSprintPlan(ActualDesc_1);
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 1);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "1");
		assertEquals(ExpectedDesc.getGoal(), ActualDesc_1.getGoal());
		assertEquals(ExpectedDesc.getGoal(), "Sprint Goal - 1");
		assertEquals(ExpectedDesc.getInterval(), ActualDesc_1.getInterval());
		assertEquals(ExpectedDesc.getInterval(), "2");
		assertEquals(ExpectedDesc.getMemberNumber(), ActualDesc_1.getMemberNumber());
		assertEquals(ExpectedDesc.getMemberNumber(), "5");
		assertEquals(ExpectedDesc.getFocusFactor(), ActualDesc_1.getFocusFactor());
		assertEquals(ExpectedDesc.getFocusFactor(), "100");
		assertEquals(ExpectedDesc.getAvailableDays(), ActualDesc_1.getAvailableDays());
		assertEquals(ExpectedDesc.getAvailableDays(), "20");
		assertEquals(ExpectedDesc.getNotes(), ActualDesc_1.getNotes());
		assertEquals(ExpectedDesc.getNotes(), "New Notes - 2");
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 0));
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 13));
		assertEquals(ExpectedDesc.getDemoDate(), ActualDesc_1.getDemoDate());
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 28));
		assertEquals(ExpectedDesc.getDemoPlace(), ActualDesc_1.getDemoPlace());
		assertEquals(ExpectedDesc.getDemoPlace(), "Lab 1321");
	}

	public void testedit_StartDate() {
		// 測試讀取資料尚未存入
		SprintPlanMapper loader = new SprintPlanMapper(this.project);
		List<ISprintPlanDesc> descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 0);

		// 設定時間
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		
		// 第一筆假資料
		ISprintPlanDesc ActualDesc_1 = new SprintPlanDesc();
		ActualDesc_1.setID("1");
		ActualDesc_1.setGoal("Sprint Goal - 1");
		ActualDesc_1.setInterval("2");
		ActualDesc_1.setMemberNumber("5");
		ActualDesc_1.setFocusFactor("100");
		ActualDesc_1.setAvailableDays("20");
		ActualDesc_1.setNotes("Sprint Note - 1");
		ActualDesc_1.setStartDate(getDate(today, 0));
		ActualDesc_1.setDemoDate(getDate(today, 28));
		ActualDesc_1.setDemoPlace("Lab 1321");		
		this.saver.addSprintPlan(ActualDesc_1);
		
		// 驗證加入的資料是否正確
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 1);
		ISprintPlanDesc ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "1");
		assertEquals(ExpectedDesc.getGoal(), ActualDesc_1.getGoal());
		assertEquals(ExpectedDesc.getGoal(), "Sprint Goal - 1");
		assertEquals(ExpectedDesc.getInterval(), ActualDesc_1.getInterval());
		assertEquals(ExpectedDesc.getInterval(), "2");
		assertEquals(ExpectedDesc.getMemberNumber(), ActualDesc_1.getMemberNumber());
		assertEquals(ExpectedDesc.getMemberNumber(), "5");
		assertEquals(ExpectedDesc.getFocusFactor(), ActualDesc_1.getFocusFactor());
		assertEquals(ExpectedDesc.getFocusFactor(), "100");
		assertEquals(ExpectedDesc.getAvailableDays(), ActualDesc_1.getAvailableDays());
		assertEquals(ExpectedDesc.getAvailableDays(), "20");
		assertEquals(ExpectedDesc.getNotes(), ActualDesc_1.getNotes());
		assertEquals(ExpectedDesc.getNotes(), "Sprint Note - 1");
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 0));
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 13));
		assertEquals(ExpectedDesc.getDemoDate(), ActualDesc_1.getDemoDate());
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 28));
		assertEquals(ExpectedDesc.getDemoPlace(), ActualDesc_1.getDemoPlace());
		assertEquals(ExpectedDesc.getDemoPlace(), "Lab 1321");
		
		// 修改 ActualDesc_1 的 ID 並且編輯存入，但是不會有任何異動 
		ActualDesc_1.setNotes("New Notes - 2");
		ActualDesc_1.setStartDate(getDate(today, 10));
		this.saver.updateSprintPlan(ActualDesc_1);
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 1);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "1");
		assertEquals(ExpectedDesc.getGoal(), ActualDesc_1.getGoal());
		assertEquals(ExpectedDesc.getGoal(), "Sprint Goal - 1");
		assertEquals(ExpectedDesc.getInterval(), ActualDesc_1.getInterval());
		assertEquals(ExpectedDesc.getInterval(), "2");
		assertEquals(ExpectedDesc.getMemberNumber(), ActualDesc_1.getMemberNumber());
		assertEquals(ExpectedDesc.getMemberNumber(), "5");
		assertEquals(ExpectedDesc.getFocusFactor(), ActualDesc_1.getFocusFactor());
		assertEquals(ExpectedDesc.getFocusFactor(), "100");
		assertEquals(ExpectedDesc.getAvailableDays(), ActualDesc_1.getAvailableDays());
		assertEquals(ExpectedDesc.getAvailableDays(), "20");
		assertEquals(ExpectedDesc.getNotes(), ActualDesc_1.getNotes());
		assertEquals(ExpectedDesc.getNotes(), "New Notes - 2");
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 10));
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 23));
		assertEquals(ExpectedDesc.getDemoDate(), ActualDesc_1.getDemoDate());
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 28));
		assertEquals(ExpectedDesc.getDemoPlace(), ActualDesc_1.getDemoPlace());
		assertEquals(ExpectedDesc.getDemoPlace(), "Lab 1321");
	}
	
	public void testedit_DemoDate() {
		// 測試讀取資料尚未存入
		SprintPlanMapper loader = new SprintPlanMapper(this.project);
		List<ISprintPlanDesc> descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 0);

		// 設定時間
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		
		// 第一筆假資料
		ISprintPlanDesc ActualDesc_1 = new SprintPlanDesc();
		ActualDesc_1.setID("1");
		ActualDesc_1.setGoal("Sprint Goal - 1");
		ActualDesc_1.setInterval("2");
		ActualDesc_1.setMemberNumber("5");
		ActualDesc_1.setFocusFactor("100");
		ActualDesc_1.setAvailableDays("20");
		ActualDesc_1.setNotes("Sprint Note - 1");
		ActualDesc_1.setStartDate(getDate(today, 0));
		ActualDesc_1.setDemoDate(getDate(today, 28));
		ActualDesc_1.setDemoPlace("Lab 1321");		
		this.saver.addSprintPlan(ActualDesc_1);
		
		// 驗證加入的資料是否正確
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 1);
		ISprintPlanDesc ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "1");
		assertEquals(ExpectedDesc.getGoal(), ActualDesc_1.getGoal());
		assertEquals(ExpectedDesc.getGoal(), "Sprint Goal - 1");
		assertEquals(ExpectedDesc.getInterval(), ActualDesc_1.getInterval());
		assertEquals(ExpectedDesc.getInterval(), "2");
		assertEquals(ExpectedDesc.getMemberNumber(), ActualDesc_1.getMemberNumber());
		assertEquals(ExpectedDesc.getMemberNumber(), "5");
		assertEquals(ExpectedDesc.getFocusFactor(), ActualDesc_1.getFocusFactor());
		assertEquals(ExpectedDesc.getFocusFactor(), "100");
		assertEquals(ExpectedDesc.getAvailableDays(), ActualDesc_1.getAvailableDays());
		assertEquals(ExpectedDesc.getAvailableDays(), "20");
		assertEquals(ExpectedDesc.getNotes(), ActualDesc_1.getNotes());
		assertEquals(ExpectedDesc.getNotes(), "Sprint Note - 1");
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 0));
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 13));
		assertEquals(ExpectedDesc.getDemoDate(), ActualDesc_1.getDemoDate());
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 28));
		assertEquals(ExpectedDesc.getDemoPlace(), ActualDesc_1.getDemoPlace());
		assertEquals(ExpectedDesc.getDemoPlace(), "Lab 1321");
		
		// 修改 ActualDesc_1 的 ID 並且編輯存入，但是不會有任何異動 
		ActualDesc_1.setNotes("New Notes - 2");
		ActualDesc_1.setStartDate(getDate(today, 10));
		ActualDesc_1.setDemoDate(getDate(today, 30));
		ActualDesc_1.setDemoPlace("去全家 Demo");
		this.saver.updateSprintPlan(ActualDesc_1);
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 1);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "1");
		assertEquals(ExpectedDesc.getGoal(), ActualDesc_1.getGoal());
		assertEquals(ExpectedDesc.getGoal(), "Sprint Goal - 1");
		assertEquals(ExpectedDesc.getInterval(), ActualDesc_1.getInterval());
		assertEquals(ExpectedDesc.getInterval(), "2");
		assertEquals(ExpectedDesc.getMemberNumber(), ActualDesc_1.getMemberNumber());
		assertEquals(ExpectedDesc.getMemberNumber(), "5");
		assertEquals(ExpectedDesc.getFocusFactor(), ActualDesc_1.getFocusFactor());
		assertEquals(ExpectedDesc.getFocusFactor(), "100");
		assertEquals(ExpectedDesc.getAvailableDays(), ActualDesc_1.getAvailableDays());
		assertEquals(ExpectedDesc.getAvailableDays(), "20");
		assertEquals(ExpectedDesc.getNotes(), ActualDesc_1.getNotes());
		assertEquals(ExpectedDesc.getNotes(), "New Notes - 2");
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 10));
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 23));
		assertEquals(ExpectedDesc.getDemoDate(), ActualDesc_1.getDemoDate());
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 30));
		assertEquals(ExpectedDesc.getDemoPlace(), ActualDesc_1.getDemoPlace());
		assertEquals(ExpectedDesc.getDemoPlace(), "去全家 Demo");
	}

	public void testedit_DemoPlace() {
		// 測試讀取資料尚未存入
		SprintPlanMapper loader = new SprintPlanMapper(this.project);
		List<ISprintPlanDesc> descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 0);

		// 設定時間
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		
		// 第一筆假資料
		ISprintPlanDesc ActualDesc_1 = new SprintPlanDesc();
		ActualDesc_1.setID("1");
		ActualDesc_1.setGoal("Sprint Goal - 1");
		ActualDesc_1.setInterval("2");
		ActualDesc_1.setMemberNumber("5");
		ActualDesc_1.setFocusFactor("100");
		ActualDesc_1.setAvailableDays("20");
		ActualDesc_1.setNotes("Sprint Note - 1");
		ActualDesc_1.setStartDate(getDate(today, 0));
		ActualDesc_1.setDemoDate(getDate(today, 28));
		ActualDesc_1.setDemoPlace("Lab 1321");		
		this.saver.addSprintPlan(ActualDesc_1);
		
		// 驗證加入的資料是否正確
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 1);
		ISprintPlanDesc ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "1");
		assertEquals(ExpectedDesc.getGoal(), ActualDesc_1.getGoal());
		assertEquals(ExpectedDesc.getGoal(), "Sprint Goal - 1");
		assertEquals(ExpectedDesc.getInterval(), ActualDesc_1.getInterval());
		assertEquals(ExpectedDesc.getInterval(), "2");
		assertEquals(ExpectedDesc.getMemberNumber(), ActualDesc_1.getMemberNumber());
		assertEquals(ExpectedDesc.getMemberNumber(), "5");
		assertEquals(ExpectedDesc.getFocusFactor(), ActualDesc_1.getFocusFactor());
		assertEquals(ExpectedDesc.getFocusFactor(), "100");
		assertEquals(ExpectedDesc.getAvailableDays(), ActualDesc_1.getAvailableDays());
		assertEquals(ExpectedDesc.getAvailableDays(), "20");
		assertEquals(ExpectedDesc.getNotes(), ActualDesc_1.getNotes());
		assertEquals(ExpectedDesc.getNotes(), "Sprint Note - 1");
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 0));
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 13));
		assertEquals(ExpectedDesc.getDemoDate(), ActualDesc_1.getDemoDate());
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 28));
		assertEquals(ExpectedDesc.getDemoPlace(), ActualDesc_1.getDemoPlace());
		assertEquals(ExpectedDesc.getDemoPlace(), "Lab 1321");
		
		// 修改 ActualDesc_1 的 ID 並且編輯存入，但是不會有任何異動 
		ActualDesc_1.setNotes("New Notes - 2");
		ActualDesc_1.setStartDate(getDate(today, 10));
		ActualDesc_1.setDemoDate(getDate(today, 30));
		this.saver.updateSprintPlan(ActualDesc_1);
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 1);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "1");
		assertEquals(ExpectedDesc.getGoal(), ActualDesc_1.getGoal());
		assertEquals(ExpectedDesc.getGoal(), "Sprint Goal - 1");
		assertEquals(ExpectedDesc.getInterval(), ActualDesc_1.getInterval());
		assertEquals(ExpectedDesc.getInterval(), "2");
		assertEquals(ExpectedDesc.getMemberNumber(), ActualDesc_1.getMemberNumber());
		assertEquals(ExpectedDesc.getMemberNumber(), "5");
		assertEquals(ExpectedDesc.getFocusFactor(), ActualDesc_1.getFocusFactor());
		assertEquals(ExpectedDesc.getFocusFactor(), "100");
		assertEquals(ExpectedDesc.getAvailableDays(), ActualDesc_1.getAvailableDays());
		assertEquals(ExpectedDesc.getAvailableDays(), "20");
		assertEquals(ExpectedDesc.getNotes(), ActualDesc_1.getNotes());
		assertEquals(ExpectedDesc.getNotes(), "New Notes - 2");
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 10));
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 23));
		assertEquals(ExpectedDesc.getDemoDate(), ActualDesc_1.getDemoDate());
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 30));
		assertEquals(ExpectedDesc.getDemoPlace(), ActualDesc_1.getDemoPlace());
		assertEquals(ExpectedDesc.getDemoPlace(), "Lab 1321");
	}	
	
	public void testedit_ActualCost() {
		// 測試讀取資料尚未存入
		SprintPlanMapper loader = new SprintPlanMapper(this.project);
		List<ISprintPlanDesc> descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 0);

		// 設定時間
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		
		// 第一筆假資料
		ISprintPlanDesc ActualDesc_1 = new SprintPlanDesc();
		ActualDesc_1.setID("1");
		ActualDesc_1.setGoal("Sprint Goal - 1");
		ActualDesc_1.setInterval("2");
		ActualDesc_1.setMemberNumber("5");
		ActualDesc_1.setFocusFactor("100");
		ActualDesc_1.setAvailableDays("20");
		ActualDesc_1.setNotes("Sprint Note - 1");
		ActualDesc_1.setStartDate(getDate(today, 0));
		ActualDesc_1.setDemoDate(getDate(today, 28));
		ActualDesc_1.setDemoPlace("Lab 1321");		
		this.saver.addSprintPlan(ActualDesc_1);
		
		// 驗證加入的資料是否正確
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 1);
		ISprintPlanDesc ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "1");
		assertEquals(ExpectedDesc.getGoal(), ActualDesc_1.getGoal());
		assertEquals(ExpectedDesc.getGoal(), "Sprint Goal - 1");
		assertEquals(ExpectedDesc.getInterval(), ActualDesc_1.getInterval());
		assertEquals(ExpectedDesc.getInterval(), "2");
		assertEquals(ExpectedDesc.getMemberNumber(), ActualDesc_1.getMemberNumber());
		assertEquals(ExpectedDesc.getMemberNumber(), "5");
		assertEquals(ExpectedDesc.getFocusFactor(), ActualDesc_1.getFocusFactor());
		assertEquals(ExpectedDesc.getFocusFactor(), "100");
		assertEquals(ExpectedDesc.getAvailableDays(), ActualDesc_1.getAvailableDays());
		assertEquals(ExpectedDesc.getAvailableDays(), "20");
		assertEquals(ExpectedDesc.getNotes(), ActualDesc_1.getNotes());
		assertEquals(ExpectedDesc.getNotes(), "Sprint Note - 1");
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 0));
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 13));
		assertEquals(ExpectedDesc.getDemoDate(), ActualDesc_1.getDemoDate());
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 28));
		assertEquals(ExpectedDesc.getDemoPlace(), ActualDesc_1.getDemoPlace());
		assertEquals(ExpectedDesc.getDemoPlace(), "Lab 1321");
		
		// 修改 ActualDesc_1 的 ID 並且編輯存入，但是不會有任何異動 
		ActualDesc_1.setNotes("New Notes - 2");
		ActualDesc_1.setStartDate(getDate(today, 10));
		ActualDesc_1.setDemoDate(getDate(today, 30));
		ActualDesc_1.setActualCost("300");
		this.saver.updateSprintPlanForActualCost(ActualDesc_1);
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 1);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "1");
		assertEquals(ExpectedDesc.getGoal(), ActualDesc_1.getGoal());
		assertEquals(ExpectedDesc.getGoal(), "Sprint Goal - 1");
		assertEquals(ExpectedDesc.getInterval(), ActualDesc_1.getInterval());
		assertEquals(ExpectedDesc.getInterval(), "2");
		assertEquals(ExpectedDesc.getMemberNumber(), ActualDesc_1.getMemberNumber());
		assertEquals(ExpectedDesc.getMemberNumber(), "5");
		assertEquals(ExpectedDesc.getFocusFactor(), ActualDesc_1.getFocusFactor());
		assertEquals(ExpectedDesc.getFocusFactor(), "100");
		assertEquals(ExpectedDesc.getAvailableDays(), ActualDesc_1.getAvailableDays());
		assertEquals(ExpectedDesc.getAvailableDays(), "20");
		assertEquals(ExpectedDesc.getNotes(), ActualDesc_1.getNotes());
		assertEquals(ExpectedDesc.getNotes(), "New Notes - 2");
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 10));
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 23));
		assertEquals(ExpectedDesc.getDemoDate(), ActualDesc_1.getDemoDate());
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 30));
		assertEquals(ExpectedDesc.getDemoPlace(), ActualDesc_1.getDemoPlace());
		assertEquals(ExpectedDesc.getDemoPlace(), "Lab 1321");
		assertEquals(ExpectedDesc.getActualCost(), "300.0");
	}	
		
	public void testdelete_empty() {
		// 測試讀取資料尚未存入不會出錯
		ISprintPlanDesc ActualDesc_1 = new SprintPlanDesc();
		this.saver.deleteSprintPlan("1");
	}
	
	public void testdelete_1() {
		// 測試讀取資料尚未存入
		SprintPlanMapper loader = new SprintPlanMapper(this.project);
		List<ISprintPlanDesc> descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 0);

		// 設定時間
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		
		// 第一筆假資料
		ISprintPlanDesc ActualDesc_1 = new SprintPlanDesc();
		ActualDesc_1.setID("1");
		ActualDesc_1.setGoal("Sprint Goal - 1");
		ActualDesc_1.setInterval("2");
		ActualDesc_1.setMemberNumber("5");
		ActualDesc_1.setFocusFactor("100");
		ActualDesc_1.setAvailableDays("20");
		ActualDesc_1.setNotes("Sprint Note - 1");
		ActualDesc_1.setStartDate(getDate(today, 0));
		ActualDesc_1.setDemoDate(getDate(today, 14));
		ActualDesc_1.setDemoPlace("Lab 1321");		
		this.saver.addSprintPlan(ActualDesc_1);
		
		// 驗證加入的資料是否正確
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 1);
		ISprintPlanDesc ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "1");
		assertEquals(ExpectedDesc.getGoal(), ActualDesc_1.getGoal());
		assertEquals(ExpectedDesc.getGoal(), "Sprint Goal - 1");
		assertEquals(ExpectedDesc.getInterval(), ActualDesc_1.getInterval());
		assertEquals(ExpectedDesc.getInterval(), "2");
		assertEquals(ExpectedDesc.getMemberNumber(), ActualDesc_1.getMemberNumber());
		assertEquals(ExpectedDesc.getMemberNumber(), "5");
		assertEquals(ExpectedDesc.getFocusFactor(), ActualDesc_1.getFocusFactor());
		assertEquals(ExpectedDesc.getFocusFactor(), "100");
		assertEquals(ExpectedDesc.getAvailableDays(), ActualDesc_1.getAvailableDays());
		assertEquals(ExpectedDesc.getAvailableDays(), "20");
		assertEquals(ExpectedDesc.getNotes(), ActualDesc_1.getNotes());
		assertEquals(ExpectedDesc.getNotes(), "Sprint Note - 1");
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 0));
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 13));
		assertEquals(ExpectedDesc.getDemoDate(), ActualDesc_1.getDemoDate());
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 14));
		assertEquals(ExpectedDesc.getDemoPlace(), ActualDesc_1.getDemoPlace());
		assertEquals(ExpectedDesc.getDemoPlace(), "Lab 1321");
		
		this.saver.addSprintPlan(ActualDesc_1);
		this.saver.addSprintPlan(ActualDesc_1);
		this.saver.addSprintPlan(ActualDesc_1);
		
		// 驗證加入的資料是否正確
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 1);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "1");
		assertEquals(ExpectedDesc.getGoal(), ActualDesc_1.getGoal());
		assertEquals(ExpectedDesc.getGoal(), "Sprint Goal - 1");
		assertEquals(ExpectedDesc.getInterval(), ActualDesc_1.getInterval());
		assertEquals(ExpectedDesc.getInterval(), "2");
		assertEquals(ExpectedDesc.getMemberNumber(), ActualDesc_1.getMemberNumber());
		assertEquals(ExpectedDesc.getMemberNumber(), "5");
		assertEquals(ExpectedDesc.getFocusFactor(), ActualDesc_1.getFocusFactor());
		assertEquals(ExpectedDesc.getFocusFactor(), "100");
		assertEquals(ExpectedDesc.getAvailableDays(), ActualDesc_1.getAvailableDays());
		assertEquals(ExpectedDesc.getAvailableDays(), "20");
		assertEquals(ExpectedDesc.getNotes(), ActualDesc_1.getNotes());
		assertEquals(ExpectedDesc.getNotes(), "Sprint Note - 1");
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 0));
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 13));
		assertEquals(ExpectedDesc.getDemoDate(), ActualDesc_1.getDemoDate());
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 14));
		assertEquals(ExpectedDesc.getDemoPlace(), ActualDesc_1.getDemoPlace());
		assertEquals(ExpectedDesc.getDemoPlace(), "Lab 1321");
		
		// 新加入一筆一樣的資料但是 ID 不一樣
		ActualDesc_1.setID("2");
		this.saver.addSprintPlan(ActualDesc_1);
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 2);
		ExpectedDesc = descs.get(0);
		
		// 測試刪除第二筆
		this.saver.deleteSprintPlan("2");
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 1);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "1");
		assertEquals(ExpectedDesc.getGoal(), ActualDesc_1.getGoal());
		assertEquals(ExpectedDesc.getGoal(), "Sprint Goal - 1");
		assertEquals(ExpectedDesc.getInterval(), ActualDesc_1.getInterval());
		assertEquals(ExpectedDesc.getInterval(), "2");
		assertEquals(ExpectedDesc.getMemberNumber(), ActualDesc_1.getMemberNumber());
		assertEquals(ExpectedDesc.getMemberNumber(), "5");
		assertEquals(ExpectedDesc.getFocusFactor(), ActualDesc_1.getFocusFactor());
		assertEquals(ExpectedDesc.getFocusFactor(), "100");
		assertEquals(ExpectedDesc.getAvailableDays(), ActualDesc_1.getAvailableDays());
		assertEquals(ExpectedDesc.getAvailableDays(), "20");
		assertEquals(ExpectedDesc.getNotes(), ActualDesc_1.getNotes());
		assertEquals(ExpectedDesc.getNotes(), "Sprint Note - 1");
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 0));
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 13));
		assertEquals(ExpectedDesc.getDemoDate(), ActualDesc_1.getDemoDate());
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 14));
		assertEquals(ExpectedDesc.getDemoPlace(), ActualDesc_1.getDemoPlace());
		assertEquals(ExpectedDesc.getDemoPlace(), "Lab 1321");		
	}
	
	public void testdelete_2() {
		// 測試讀取資料尚未存入
		SprintPlanMapper loader = new SprintPlanMapper(this.project);
		List<ISprintPlanDesc> descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 0);

		// 設定時間
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		
		// 第一筆假資料
		ISprintPlanDesc ActualDesc_1 = new SprintPlanDesc();
		ActualDesc_1.setID("1");
		ActualDesc_1.setGoal("Sprint Goal - 1");
		ActualDesc_1.setInterval("2");
		ActualDesc_1.setMemberNumber("5");
		ActualDesc_1.setFocusFactor("100");
		ActualDesc_1.setAvailableDays("20");
		ActualDesc_1.setNotes("Sprint Note - 1");
		ActualDesc_1.setStartDate(getDate(today, 0));
		ActualDesc_1.setDemoDate(getDate(today, 14));
		ActualDesc_1.setDemoPlace("Lab 1321");		
		this.saver.addSprintPlan(ActualDesc_1);
		
		// 驗證加入的資料是否正確
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 1);
		ISprintPlanDesc ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "1");
		assertEquals(ExpectedDesc.getGoal(), ActualDesc_1.getGoal());
		assertEquals(ExpectedDesc.getGoal(), "Sprint Goal - 1");
		assertEquals(ExpectedDesc.getInterval(), ActualDesc_1.getInterval());
		assertEquals(ExpectedDesc.getInterval(), "2");
		assertEquals(ExpectedDesc.getMemberNumber(), ActualDesc_1.getMemberNumber());
		assertEquals(ExpectedDesc.getMemberNumber(), "5");
		assertEquals(ExpectedDesc.getFocusFactor(), ActualDesc_1.getFocusFactor());
		assertEquals(ExpectedDesc.getFocusFactor(), "100");
		assertEquals(ExpectedDesc.getAvailableDays(), ActualDesc_1.getAvailableDays());
		assertEquals(ExpectedDesc.getAvailableDays(), "20");
		assertEquals(ExpectedDesc.getNotes(), ActualDesc_1.getNotes());
		assertEquals(ExpectedDesc.getNotes(), "Sprint Note - 1");
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 0));
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 13));
		assertEquals(ExpectedDesc.getDemoDate(), ActualDesc_1.getDemoDate());
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 14));
		assertEquals(ExpectedDesc.getDemoPlace(), ActualDesc_1.getDemoPlace());
		assertEquals(ExpectedDesc.getDemoPlace(), "Lab 1321");
		
		this.saver.addSprintPlan(ActualDesc_1);
		this.saver.addSprintPlan(ActualDesc_1);
		this.saver.addSprintPlan(ActualDesc_1);
		
		// 驗證加入的資料是否正確
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 1);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "1");
		assertEquals(ExpectedDesc.getGoal(), ActualDesc_1.getGoal());
		assertEquals(ExpectedDesc.getGoal(), "Sprint Goal - 1");
		assertEquals(ExpectedDesc.getInterval(), ActualDesc_1.getInterval());
		assertEquals(ExpectedDesc.getInterval(), "2");
		assertEquals(ExpectedDesc.getMemberNumber(), ActualDesc_1.getMemberNumber());
		assertEquals(ExpectedDesc.getMemberNumber(), "5");
		assertEquals(ExpectedDesc.getFocusFactor(), ActualDesc_1.getFocusFactor());
		assertEquals(ExpectedDesc.getFocusFactor(), "100");
		assertEquals(ExpectedDesc.getAvailableDays(), ActualDesc_1.getAvailableDays());
		assertEquals(ExpectedDesc.getAvailableDays(), "20");
		assertEquals(ExpectedDesc.getNotes(), ActualDesc_1.getNotes());
		assertEquals(ExpectedDesc.getNotes(), "Sprint Note - 1");
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 0));
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 13));
		assertEquals(ExpectedDesc.getDemoDate(), ActualDesc_1.getDemoDate());
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 14));
		assertEquals(ExpectedDesc.getDemoPlace(), ActualDesc_1.getDemoPlace());
		assertEquals(ExpectedDesc.getDemoPlace(), "Lab 1321");

		// 新加入一筆一樣的資料但是 ID 不一樣
		ActualDesc_1.setID("2");
		this.saver.addSprintPlan(ActualDesc_1);
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 2);
		ExpectedDesc = descs.get(0);

		// 測試刪除第一筆
		this.saver.deleteSprintPlan("1");
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 1);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "2");
		assertEquals(ExpectedDesc.getGoal(), ActualDesc_1.getGoal());
		assertEquals(ExpectedDesc.getGoal(), "Sprint Goal - 1");
		assertEquals(ExpectedDesc.getInterval(), ActualDesc_1.getInterval());
		assertEquals(ExpectedDesc.getInterval(), "2");
		assertEquals(ExpectedDesc.getMemberNumber(), ActualDesc_1.getMemberNumber());
		assertEquals(ExpectedDesc.getMemberNumber(), "5");
		assertEquals(ExpectedDesc.getFocusFactor(), ActualDesc_1.getFocusFactor());
		assertEquals(ExpectedDesc.getFocusFactor(), "100");
		assertEquals(ExpectedDesc.getAvailableDays(), ActualDesc_1.getAvailableDays());
		assertEquals(ExpectedDesc.getAvailableDays(), "20");
		assertEquals(ExpectedDesc.getNotes(), ActualDesc_1.getNotes());
		assertEquals(ExpectedDesc.getNotes(), "Sprint Note - 1");
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 0));
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 13));
		assertEquals(ExpectedDesc.getDemoDate(), ActualDesc_1.getDemoDate());
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 14));
		assertEquals(ExpectedDesc.getDemoPlace(), ActualDesc_1.getDemoPlace());
		assertEquals(ExpectedDesc.getDemoPlace(), "Lab 1321");		
	}
	
	public void testmoveSprint_empty() {
		// 測試讀取資料尚未存入不會出錯
		ISprintPlanDesc ActualDesc_1 = new SprintPlanDesc();
		this.saver.moveSprintPlan(1, 2);
		
		SprintPlanMapper loader = new SprintPlanMapper(this.project);
		List<ISprintPlanDesc> descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 0);
	}
	
	public void testmoveSprint_1() {
		// 設定時間
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		
		SprintPlanMapper loader = new SprintPlanMapper(this.project);
		List<ISprintPlanDesc> descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 0);
		
		// 建立五筆資料
		CreateSprint cs = new CreateSprint(5, this.CP);
		cs.exe();
		
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 5);
		
		ISprintPlanDesc ExpectedDesc = null;
		for (int i=0 ; i<descs.size() ; i++) {
			String ID = Integer.toString(i+1);
			ExpectedDesc = descs.get(i);
			assertEquals(ExpectedDesc.getID(), ID);
			assertEquals(ExpectedDesc.getGoal(), cs.TEST_SPRINT_GOAL + ID);
			assertEquals(ExpectedDesc.getInterval(), CreateSprint.SPRINT_INTERVAL);
			assertEquals(ExpectedDesc.getMemberNumber(), CreateSprint.SPRINT_MEMBER);
			assertEquals(ExpectedDesc.getFocusFactor(), CreateSprint.SPRINT_FOCUS_FACTOR);
			assertEquals(ExpectedDesc.getAvailableDays(), CreateSprint.SPRINT_AVAILABLE_DAY);
			assertEquals(ExpectedDesc.getNotes(), cs.TEST_SPRINT_NOTE + ID);
			assertEquals(ExpectedDesc.getStartDate(), getDate(today, i*2*7));
			assertEquals(ExpectedDesc.getEndDate(), getDate(today, (i+1)*2*7-1));
			assertEquals(ExpectedDesc.getDemoDate(), getDate(today, (i+1)*2*7-1));
			assertEquals(ExpectedDesc.getDemoPlace(), CreateSprint.SPRINT_DEMOPLACE);
		}
		
		// 將第二筆移動到第五筆
		this.saver.moveSprintPlan(2, 5);
		
		// 檢查是否 2-5 全部交換
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 5);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "1");
		assertEquals(ExpectedDesc.getGoal(), cs.TEST_SPRINT_GOAL + "1");
		assertEquals(ExpectedDesc.getInterval(), CreateSprint.SPRINT_INTERVAL);
		assertEquals(ExpectedDesc.getMemberNumber(), CreateSprint.SPRINT_MEMBER);
		assertEquals(ExpectedDesc.getFocusFactor(), CreateSprint.SPRINT_FOCUS_FACTOR);
		assertEquals(ExpectedDesc.getAvailableDays(), CreateSprint.SPRINT_AVAILABLE_DAY);
		assertEquals(ExpectedDesc.getNotes(), cs.TEST_SPRINT_NOTE + "1");
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 0));
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 1*2*7-1));
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 1*2*7-1));
		assertEquals(ExpectedDesc.getDemoPlace(), CreateSprint.SPRINT_DEMOPLACE);
		
		ExpectedDesc = descs.get(1);
		assertEquals(ExpectedDesc.getID(), "2");
		assertEquals(ExpectedDesc.getGoal(), cs.TEST_SPRINT_GOAL + "3");
		assertEquals(ExpectedDesc.getInterval(), CreateSprint.SPRINT_INTERVAL);
		assertEquals(ExpectedDesc.getMemberNumber(), CreateSprint.SPRINT_MEMBER);
		assertEquals(ExpectedDesc.getFocusFactor(), CreateSprint.SPRINT_FOCUS_FACTOR);
		assertEquals(ExpectedDesc.getAvailableDays(), CreateSprint.SPRINT_AVAILABLE_DAY);
		assertEquals(ExpectedDesc.getNotes(), cs.TEST_SPRINT_NOTE + "3");
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 1*2*7));
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 2*2*7-1));
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 2*2*7-1));
		assertEquals(ExpectedDesc.getDemoPlace(), CreateSprint.SPRINT_DEMOPLACE);
		
		ExpectedDesc = descs.get(2);
		assertEquals(ExpectedDesc.getID(), "3");
		assertEquals(ExpectedDesc.getGoal(), cs.TEST_SPRINT_GOAL + "4");
		assertEquals(ExpectedDesc.getInterval(), CreateSprint.SPRINT_INTERVAL);
		assertEquals(ExpectedDesc.getMemberNumber(), CreateSprint.SPRINT_MEMBER);
		assertEquals(ExpectedDesc.getFocusFactor(), CreateSprint.SPRINT_FOCUS_FACTOR);
		assertEquals(ExpectedDesc.getAvailableDays(), CreateSprint.SPRINT_AVAILABLE_DAY);
		assertEquals(ExpectedDesc.getNotes(), cs.TEST_SPRINT_NOTE + "4");
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 2*2*7));
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 3*2*7-1));
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 3*2*7-1));
		assertEquals(ExpectedDesc.getDemoPlace(), CreateSprint.SPRINT_DEMOPLACE);
		
		ExpectedDesc = descs.get(3);
		assertEquals(ExpectedDesc.getID(), "4");
		assertEquals(ExpectedDesc.getGoal(), cs.TEST_SPRINT_GOAL + "5");
		assertEquals(ExpectedDesc.getInterval(), CreateSprint.SPRINT_INTERVAL);
		assertEquals(ExpectedDesc.getMemberNumber(), CreateSprint.SPRINT_MEMBER);
		assertEquals(ExpectedDesc.getFocusFactor(), CreateSprint.SPRINT_FOCUS_FACTOR);
		assertEquals(ExpectedDesc.getAvailableDays(), CreateSprint.SPRINT_AVAILABLE_DAY);
		assertEquals(ExpectedDesc.getNotes(), cs.TEST_SPRINT_NOTE + "5");
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 3*2*7));
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 4*2*7-1));
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 4*2*7-1));
		assertEquals(ExpectedDesc.getDemoPlace(), CreateSprint.SPRINT_DEMOPLACE);
		
		ExpectedDesc = descs.get(4);
		assertEquals(ExpectedDesc.getID(), "5");
		assertEquals(ExpectedDesc.getGoal(), cs.TEST_SPRINT_GOAL + "2");
		assertEquals(ExpectedDesc.getInterval(), CreateSprint.SPRINT_INTERVAL);
		assertEquals(ExpectedDesc.getMemberNumber(), CreateSprint.SPRINT_MEMBER);
		assertEquals(ExpectedDesc.getFocusFactor(), CreateSprint.SPRINT_FOCUS_FACTOR);
		assertEquals(ExpectedDesc.getAvailableDays(), CreateSprint.SPRINT_AVAILABLE_DAY);
		assertEquals(ExpectedDesc.getNotes(), cs.TEST_SPRINT_NOTE + "2");
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 4*2*7));
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 5*2*7-1));
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 5*2*7-1));
		assertEquals(ExpectedDesc.getDemoPlace(), CreateSprint.SPRINT_DEMOPLACE);
	}
	
	public void testmoveSprint_2() {
		// 設定時間
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		
		SprintPlanMapper loader = new SprintPlanMapper(this.project);
		List<ISprintPlanDesc> descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 0);
		
		// 建立五筆資料
		CreateSprint cs = new CreateSprint(5, this.CP);
		cs.exe();
		
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 5);
		
		ISprintPlanDesc ExpectedDesc = null;
		for (int i=0 ; i<descs.size() ; i++) {
			String ID = Integer.toString(i+1);
			ExpectedDesc = descs.get(i);
			assertEquals(ExpectedDesc.getID(), ID);
			assertEquals(ExpectedDesc.getGoal(), cs.TEST_SPRINT_GOAL + ID);
			assertEquals(ExpectedDesc.getInterval(), CreateSprint.SPRINT_INTERVAL);
			assertEquals(ExpectedDesc.getMemberNumber(), CreateSprint.SPRINT_MEMBER);
			assertEquals(ExpectedDesc.getFocusFactor(), CreateSprint.SPRINT_FOCUS_FACTOR);
			assertEquals(ExpectedDesc.getAvailableDays(), CreateSprint.SPRINT_AVAILABLE_DAY);
			assertEquals(ExpectedDesc.getNotes(), cs.TEST_SPRINT_NOTE + ID);
			assertEquals(ExpectedDesc.getStartDate(), getDate(today, i*2*7));
			assertEquals(ExpectedDesc.getEndDate(), getDate(today, (i+1)*2*7-1));
			assertEquals(ExpectedDesc.getDemoDate(), getDate(today, (i+1)*2*7-1));
			assertEquals(ExpectedDesc.getDemoPlace(), CreateSprint.SPRINT_DEMOPLACE);
		}
		
		// 將第二筆移動到第五筆
		this.saver.moveSprintPlan(5, 2);
		
		// 檢查是否 2-5 全部交換
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 5);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "1");
		assertEquals(ExpectedDesc.getGoal(), cs.TEST_SPRINT_GOAL + "1");
		assertEquals(ExpectedDesc.getInterval(), CreateSprint.SPRINT_INTERVAL);
		assertEquals(ExpectedDesc.getMemberNumber(), CreateSprint.SPRINT_MEMBER);
		assertEquals(ExpectedDesc.getFocusFactor(), CreateSprint.SPRINT_FOCUS_FACTOR);
		assertEquals(ExpectedDesc.getAvailableDays(), CreateSprint.SPRINT_AVAILABLE_DAY);
		assertEquals(ExpectedDesc.getNotes(), cs.TEST_SPRINT_NOTE + "1");
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 0));
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 1*2*7-1));
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 1*2*7-1));
		assertEquals(ExpectedDesc.getDemoPlace(), CreateSprint.SPRINT_DEMOPLACE);
		
		ExpectedDesc = descs.get(1);
		assertEquals(ExpectedDesc.getID(), "2");
		assertEquals(ExpectedDesc.getGoal(), cs.TEST_SPRINT_GOAL + "5");
		assertEquals(ExpectedDesc.getInterval(), CreateSprint.SPRINT_INTERVAL);
		assertEquals(ExpectedDesc.getMemberNumber(), CreateSprint.SPRINT_MEMBER);
		assertEquals(ExpectedDesc.getFocusFactor(), CreateSprint.SPRINT_FOCUS_FACTOR);
		assertEquals(ExpectedDesc.getAvailableDays(), CreateSprint.SPRINT_AVAILABLE_DAY);
		assertEquals(ExpectedDesc.getNotes(), cs.TEST_SPRINT_NOTE + "5");
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 1*2*7));
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 2*2*7-1));
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 2*2*7-1));
		assertEquals(ExpectedDesc.getDemoPlace(), CreateSprint.SPRINT_DEMOPLACE);
		
		ExpectedDesc = descs.get(2);
		assertEquals(ExpectedDesc.getID(), "3");
		assertEquals(ExpectedDesc.getGoal(), cs.TEST_SPRINT_GOAL + "2");
		assertEquals(ExpectedDesc.getInterval(), CreateSprint.SPRINT_INTERVAL);
		assertEquals(ExpectedDesc.getMemberNumber(), CreateSprint.SPRINT_MEMBER);
		assertEquals(ExpectedDesc.getFocusFactor(), CreateSprint.SPRINT_FOCUS_FACTOR);
		assertEquals(ExpectedDesc.getAvailableDays(), CreateSprint.SPRINT_AVAILABLE_DAY);
		assertEquals(ExpectedDesc.getNotes(), cs.TEST_SPRINT_NOTE + "2");
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 2*2*7));
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 3*2*7-1));
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 3*2*7-1));
		assertEquals(ExpectedDesc.getDemoPlace(), CreateSprint.SPRINT_DEMOPLACE);
		
		ExpectedDesc = descs.get(3);
		assertEquals(ExpectedDesc.getID(), "4");
		assertEquals(ExpectedDesc.getGoal(), cs.TEST_SPRINT_GOAL + "3");
		assertEquals(ExpectedDesc.getInterval(), CreateSprint.SPRINT_INTERVAL);
		assertEquals(ExpectedDesc.getMemberNumber(), CreateSprint.SPRINT_MEMBER);
		assertEquals(ExpectedDesc.getFocusFactor(), CreateSprint.SPRINT_FOCUS_FACTOR);
		assertEquals(ExpectedDesc.getAvailableDays(), CreateSprint.SPRINT_AVAILABLE_DAY);
		assertEquals(ExpectedDesc.getNotes(), cs.TEST_SPRINT_NOTE + "3");
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 3*2*7));
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 4*2*7-1));
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 4*2*7-1));
		assertEquals(ExpectedDesc.getDemoPlace(), CreateSprint.SPRINT_DEMOPLACE);
		
		ExpectedDesc = descs.get(4);
		assertEquals(ExpectedDesc.getID(), "5");
		assertEquals(ExpectedDesc.getGoal(), cs.TEST_SPRINT_GOAL + "4");
		assertEquals(ExpectedDesc.getInterval(), CreateSprint.SPRINT_INTERVAL);
		assertEquals(ExpectedDesc.getMemberNumber(), CreateSprint.SPRINT_MEMBER);
		assertEquals(ExpectedDesc.getFocusFactor(), CreateSprint.SPRINT_FOCUS_FACTOR);
		assertEquals(ExpectedDesc.getAvailableDays(), CreateSprint.SPRINT_AVAILABLE_DAY);
		assertEquals(ExpectedDesc.getNotes(), cs.TEST_SPRINT_NOTE + "4");
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 4*2*7));
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 5*2*7-1));
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 5*2*7-1));
		assertEquals(ExpectedDesc.getDemoPlace(), CreateSprint.SPRINT_DEMOPLACE);
	}
	
	public void testmoveSprint_3() {
		// 設定時間
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		
		SprintPlanMapper loader = new SprintPlanMapper(this.project);
		List<ISprintPlanDesc> descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 0);
		
		// 建立五筆資料
		CreateSprint cs = new CreateSprint(5, this.CP);
		cs.exe();
		
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 5);
		
		ISprintPlanDesc ExpectedDesc = null;
		for (int i=0 ; i<descs.size() ; i++) {
			String ID = Integer.toString(i+1);
			ExpectedDesc = descs.get(i);
			assertEquals(ExpectedDesc.getID(), ID);
			assertEquals(ExpectedDesc.getGoal(), cs.TEST_SPRINT_GOAL + ID);
			assertEquals(ExpectedDesc.getInterval(), CreateSprint.SPRINT_INTERVAL);
			assertEquals(ExpectedDesc.getMemberNumber(), CreateSprint.SPRINT_MEMBER);
			assertEquals(ExpectedDesc.getFocusFactor(), CreateSprint.SPRINT_FOCUS_FACTOR);
			assertEquals(ExpectedDesc.getAvailableDays(), CreateSprint.SPRINT_AVAILABLE_DAY);
			assertEquals(ExpectedDesc.getNotes(), cs.TEST_SPRINT_NOTE + ID);
			assertEquals(ExpectedDesc.getStartDate(), getDate(today, i*2*7));
			assertEquals(ExpectedDesc.getEndDate(), getDate(today, (i+1)*2*7-1));
			assertEquals(ExpectedDesc.getDemoDate(), getDate(today, (i+1)*2*7-1));
			assertEquals(ExpectedDesc.getDemoPlace(), CreateSprint.SPRINT_DEMOPLACE);
		}
		
		// 將第一筆移動到第五筆，可是不能被移動，因為 sprint 已經開始了
		this.saver.moveSprintPlan(5, 1);
		for (int i=0 ; i<descs.size() ; i++) {
			String ID = Integer.toString(i+1);
			ExpectedDesc = descs.get(i);
			assertEquals(ExpectedDesc.getID(), ID);
			assertEquals(ExpectedDesc.getGoal(), cs.TEST_SPRINT_GOAL + ID);
			assertEquals(ExpectedDesc.getInterval(), CreateSprint.SPRINT_INTERVAL);
			assertEquals(ExpectedDesc.getMemberNumber(), CreateSprint.SPRINT_MEMBER);
			assertEquals(ExpectedDesc.getFocusFactor(), CreateSprint.SPRINT_FOCUS_FACTOR);
			assertEquals(ExpectedDesc.getAvailableDays(), CreateSprint.SPRINT_AVAILABLE_DAY);
			assertEquals(ExpectedDesc.getNotes(), cs.TEST_SPRINT_NOTE + ID);
			assertEquals(ExpectedDesc.getStartDate(), getDate(today, i*2*7));
			assertEquals(ExpectedDesc.getEndDate(), getDate(today, (i+1)*2*7-1));
			assertEquals(ExpectedDesc.getDemoDate(), getDate(today, (i+1)*2*7-1));
			assertEquals(ExpectedDesc.getDemoPlace(), CreateSprint.SPRINT_DEMOPLACE);
		}	
	}
	
	public void testmoveSprint_4() {
		// 設定時間
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		
		SprintPlanMapper loader = new SprintPlanMapper(this.project);
		List<ISprintPlanDesc> descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 0);
		
		// 建立五筆資料
		CreateSprint cs = new CreateSprint(5, this.CP);
		cs.exe();
		
		descs = this.getSprintPlanListAndSortById(loader);
		assertEquals(descs.size(), 5);
		
		ISprintPlanDesc ExpectedDesc = null;
		for (int i=0 ; i<descs.size() ; i++) {
			String ID = Integer.toString(i+1);
			ExpectedDesc = descs.get(i);
			assertEquals(ExpectedDesc.getID(), ID);
			assertEquals(ExpectedDesc.getGoal(), cs.TEST_SPRINT_GOAL + ID);
			assertEquals(ExpectedDesc.getInterval(), CreateSprint.SPRINT_INTERVAL);
			assertEquals(ExpectedDesc.getMemberNumber(), CreateSprint.SPRINT_MEMBER);
			assertEquals(ExpectedDesc.getFocusFactor(), CreateSprint.SPRINT_FOCUS_FACTOR);
			assertEquals(ExpectedDesc.getAvailableDays(), CreateSprint.SPRINT_AVAILABLE_DAY);
			assertEquals(ExpectedDesc.getNotes(), cs.TEST_SPRINT_NOTE + ID);
			assertEquals(ExpectedDesc.getStartDate(), getDate(today, i*2*7));
			assertEquals(ExpectedDesc.getEndDate(), getDate(today, (i+1)*2*7-1));
			assertEquals(ExpectedDesc.getDemoDate(), getDate(today, (i+1)*2*7-1));
			assertEquals(ExpectedDesc.getDemoPlace(), CreateSprint.SPRINT_DEMOPLACE);
		}
		
		// 將第一筆移動到第五筆，可是不能被移動，因為 sprint 已經開始了
		this.saver.moveSprintPlan(1, 5);
		for (int i=0 ; i<descs.size() ; i++) {
			String ID = Integer.toString(i+1);
			ExpectedDesc = descs.get(i);
			assertEquals(ExpectedDesc.getID(), ID);
			assertEquals(ExpectedDesc.getGoal(), cs.TEST_SPRINT_GOAL + ID);
			assertEquals(ExpectedDesc.getInterval(), CreateSprint.SPRINT_INTERVAL);
			assertEquals(ExpectedDesc.getMemberNumber(), CreateSprint.SPRINT_MEMBER);
			assertEquals(ExpectedDesc.getFocusFactor(), CreateSprint.SPRINT_FOCUS_FACTOR);
			assertEquals(ExpectedDesc.getAvailableDays(), CreateSprint.SPRINT_AVAILABLE_DAY);
			assertEquals(ExpectedDesc.getNotes(), cs.TEST_SPRINT_NOTE + ID);
			assertEquals(ExpectedDesc.getStartDate(), getDate(today, i*2*7));
			assertEquals(ExpectedDesc.getEndDate(), getDate(today, (i+1)*2*7-1));
			assertEquals(ExpectedDesc.getDemoDate(), getDate(today, (i+1)*2*7-1));
			assertEquals(ExpectedDesc.getDemoPlace(), CreateSprint.SPRINT_DEMOPLACE);
		}			
	}
	
	private String getDate(Date date, int duration) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		Calendar cal_start = Calendar.getInstance();
		Calendar cal_end = Calendar.getInstance();
		cal_start.setTime(date);		// 得到今天的日期
		cal_end.setTime(date);			// 得到今天的日期
		cal_end.add(Calendar.DAY_OF_YEAR, duration);
		
		return format.format(cal_end.getTime());	// get start date
	}
	
	// sort descent by ID
	private List<ISprintPlanDesc> getSprintPlanListAndSortById(SprintPlanMapper srMapper) {		
		List<ISprintPlanDesc> list = srMapper.getSprintPlanList();
		
		List<ISprintPlanDesc> newList = new ArrayList<ISprintPlanDesc>();
		for (ISprintPlanDesc source : list) {
			int index = 0;
			for (ISprintPlanDesc target : newList) {
				// 遞增排序
				if (Integer.parseInt(target.getID()) > Integer.parseInt(source.getID()))
					break;
				index++;
			}
			newList.add(index, source);
		}
		
		return newList;
	}	
	
}