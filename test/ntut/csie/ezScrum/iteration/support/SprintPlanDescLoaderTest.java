package ntut.csie.ezScrum.iteration.support;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;
import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.ezScrumInfoConfig;
import ntut.csie.ezScrum.web.mapper.SprintPlanMapper;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

public class SprintPlanDescLoaderTest extends TestCase {
	
	private ezScrumInfoConfig config = new ezScrumInfoConfig();
	private CreateProject CP = null;
	private IProject project = null;
	private SprintPlanMapper loader = null;
	
	public SprintPlanDescLoaderTest(String method) {
		super(method);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		
		// initial SQL
		InitialSQL init = new InitialSQL(config);
		init.exe();
		
		// create project
		this.CP = new CreateProject(1);
		this.CP.exeCreate();
		this.project = this.CP.getProjectList().get(0);
		
		// initial loader
		this.loader = new SprintPlanMapper(this.project);
		
		// release
		init = null;
	}
	
	protected void tearDown() throws Exception {
		// initial SQL
		InitialSQL init = new InitialSQL(config);
		init.exe();
		
		// copy and delete test project
		CopyProject cp = new CopyProject(this.CP);
		cp.exeDelete_Project();
		
		// release
		init = null;
		cp = null;
		this.CP = null;
		this.config = null;
		this.project = null;
		
		super.tearDown();
	}

	public void testload() {
		// 設定時間
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		
		CreateSprint cs = new CreateSprint(5, this.CP);
		cs.exe();
		
		List<ISprintPlanDesc> descs = this.loader.getSprintPlanList();
		assertEquals(descs.size(), 5);
		
		ISprintPlanDesc ExpectedDesc = null;
		for (int i=0 ; i<descs.size() ; i++) {
			String ID = Integer.toString(i+1);
			ExpectedDesc = descs.get(i);
			assertEquals(ExpectedDesc.getID(), ID);
			assertEquals(ExpectedDesc.getGoal(), cs.TEST_SPRINT_GOAL + ID);
			assertEquals(ExpectedDesc.getInterval(), cs.SPRINT_INTERVAL);
			assertEquals(ExpectedDesc.getMemberNumber(), cs.SPRINT_MEMBER);
			assertEquals(ExpectedDesc.getFocusFactor(), cs.SPRINT_FOCUS_FACTOR);
			assertEquals(ExpectedDesc.getAvailableDays(), cs.SPRINT_AVAILABLE_DAY);
			assertEquals(ExpectedDesc.getNotes(), cs.TEST_SPRINT_NOTE + ID);
			assertEquals(ExpectedDesc.getStartDate(), getDate(today, i*2*7));
			assertEquals(ExpectedDesc.getEndDate(), getDate(today, (i+1)*2*7-1));
			assertEquals(ExpectedDesc.getDemoDate(), getDate(today, (i+1)*2*7-1));
			assertEquals(ExpectedDesc.getDemoPlace(), cs.SPRINT_DEMOPLACE);
		}
		
		// 修改 ID，測試是否 sort by ID
		// ID 1 與 ID 5 交換，由於 saver 不能對 ID 作 edit，所以只好先砍掉再加回去
		SprintPlanMapper saver = new SprintPlanMapper(project);
		ExpectedDesc = descs.get(0);
		ExpectedDesc.setID("5");
		saver.deleteSprintPlan("5");
		saver.addSprintPlan(ExpectedDesc);
		
		ExpectedDesc = descs.get(4);
		ExpectedDesc.setID("1");
		saver.deleteSprintPlan("1");
		saver.addSprintPlan(ExpectedDesc);
		
		descs = this.loader.getSprintPlanList();	// sort by Id
		descs = this.sortById(descs);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "1");
		assertEquals(ExpectedDesc.getGoal(), cs.TEST_SPRINT_GOAL + "5");
		assertEquals(ExpectedDesc.getInterval(), cs.SPRINT_INTERVAL);
		assertEquals(ExpectedDesc.getMemberNumber(), cs.SPRINT_MEMBER);
		assertEquals(ExpectedDesc.getFocusFactor(), cs.SPRINT_FOCUS_FACTOR);
		assertEquals(ExpectedDesc.getAvailableDays(), cs.SPRINT_AVAILABLE_DAY);
		assertEquals(ExpectedDesc.getNotes(), cs.TEST_SPRINT_NOTE + "5");
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 4*2*7));
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 5*2*7-1));
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 5*2*7-1));
		assertEquals(ExpectedDesc.getDemoPlace(), cs.SPRINT_DEMOPLACE);
		
		ExpectedDesc = descs.get(4);
		assertEquals(ExpectedDesc.getID(), "5");
		assertEquals(ExpectedDesc.getGoal(), cs.TEST_SPRINT_GOAL + "1");
		assertEquals(ExpectedDesc.getInterval(), cs.SPRINT_INTERVAL);
		assertEquals(ExpectedDesc.getMemberNumber(), cs.SPRINT_MEMBER);
		assertEquals(ExpectedDesc.getFocusFactor(), cs.SPRINT_FOCUS_FACTOR);
		assertEquals(ExpectedDesc.getAvailableDays(), cs.SPRINT_AVAILABLE_DAY);
		assertEquals(ExpectedDesc.getNotes(), cs.TEST_SPRINT_NOTE + "1");
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 0*2*7));
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 1*2*7-1));
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 1*2*7-1));
		assertEquals(ExpectedDesc.getDemoPlace(), cs.SPRINT_DEMOPLACE);
	}
	
	public void testListload() {
		// 設定時間
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		
		CreateSprint cs = new CreateSprint(5, this.CP);
		cs.exe();
		
		List<ISprintPlanDesc> descs = this.loader.getSprintPlanList();	// sort by StartDate
		descs = this.sortByStartDate(descs);				
		assertEquals(descs.size(), 5);
		
		ISprintPlanDesc ExpectedDesc = null;
		for (int i=0 ; i<descs.size() ; i++) {
			String ID = Integer.toString(i+1);
			ExpectedDesc = descs.get(i);
			assertEquals(ExpectedDesc.getID(), ID);
			assertEquals(ExpectedDesc.getGoal(), cs.TEST_SPRINT_GOAL + ID);
			assertEquals(ExpectedDesc.getInterval(), cs.SPRINT_INTERVAL);
			assertEquals(ExpectedDesc.getMemberNumber(), cs.SPRINT_MEMBER);
			assertEquals(ExpectedDesc.getFocusFactor(), cs.SPRINT_FOCUS_FACTOR);
			assertEquals(ExpectedDesc.getAvailableDays(), cs.SPRINT_AVAILABLE_DAY);
			assertEquals(ExpectedDesc.getNotes(), cs.TEST_SPRINT_NOTE + ID);
			assertEquals(ExpectedDesc.getStartDate(), getDate(today, i*2*7));
			assertEquals(ExpectedDesc.getEndDate(), getDate(today, (i+1)*2*7-1));
			assertEquals(ExpectedDesc.getDemoDate(), getDate(today, (i+1)*2*7-1));
			assertEquals(ExpectedDesc.getDemoPlace(), cs.SPRINT_DEMOPLACE);
		}
		
		// 修改 ID，測試是否 sort by ID
		// ID 1 與 ID 5 交換，由於 saver 不能對 ID 作 edit，所以只好先砍掉再加回去
		SprintPlanMapper saver = new SprintPlanMapper(project);
		ExpectedDesc = descs.get(0);
		ExpectedDesc.setID("5");
		saver.deleteSprintPlan("5");
		saver.addSprintPlan(ExpectedDesc);
		
		ExpectedDesc = descs.get(4);
		ExpectedDesc.setID("1");
		saver.deleteSprintPlan("1");
		saver.addSprintPlan(ExpectedDesc);
		
		descs = this.loader.getSprintPlanList();	// sort by StartDate
		descs = this.sortByStartDate(descs);
		// 只有換 ID 不對會 listsort 影響
		for (int i=1 ; i<descs.size()-1 ; i++) {
			String ID = Integer.toString(i+1);
			ExpectedDesc = descs.get(i);
			assertEquals(ExpectedDesc.getID(), ID);
			assertEquals(ExpectedDesc.getGoal(), cs.TEST_SPRINT_GOAL + ID);
			assertEquals(ExpectedDesc.getInterval(), cs.SPRINT_INTERVAL);
			assertEquals(ExpectedDesc.getMemberNumber(), cs.SPRINT_MEMBER);
			assertEquals(ExpectedDesc.getFocusFactor(), cs.SPRINT_FOCUS_FACTOR);
			assertEquals(ExpectedDesc.getAvailableDays(), cs.SPRINT_AVAILABLE_DAY);
			assertEquals(ExpectedDesc.getNotes(), cs.TEST_SPRINT_NOTE + ID);
			assertEquals(ExpectedDesc.getStartDate(), getDate(today, i*2*7));
			assertEquals(ExpectedDesc.getEndDate(), getDate(today, (i+1)*2*7-1));
			assertEquals(ExpectedDesc.getDemoDate(), getDate(today, (i+1)*2*7-1));
			assertEquals(ExpectedDesc.getDemoPlace(), cs.SPRINT_DEMOPLACE);
		}
		
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "5");
		assertEquals(ExpectedDesc.getGoal(), cs.TEST_SPRINT_GOAL + "1");
		assertEquals(ExpectedDesc.getInterval(), cs.SPRINT_INTERVAL);
		assertEquals(ExpectedDesc.getMemberNumber(), cs.SPRINT_MEMBER);
		assertEquals(ExpectedDesc.getFocusFactor(), cs.SPRINT_FOCUS_FACTOR);
		assertEquals(ExpectedDesc.getAvailableDays(), cs.SPRINT_AVAILABLE_DAY);
		assertEquals(ExpectedDesc.getNotes(), cs.TEST_SPRINT_NOTE + "1");
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 0*2*7));
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 1*2*7-1));
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 1*2*7-1));
		assertEquals(ExpectedDesc.getDemoPlace(), cs.SPRINT_DEMOPLACE);
		
		ExpectedDesc = descs.get(4);
		assertEquals(ExpectedDesc.getID(), "1");
		assertEquals(ExpectedDesc.getGoal(), cs.TEST_SPRINT_GOAL + "5");
		assertEquals(ExpectedDesc.getInterval(), cs.SPRINT_INTERVAL);
		assertEquals(ExpectedDesc.getMemberNumber(), cs.SPRINT_MEMBER);
		assertEquals(ExpectedDesc.getFocusFactor(), cs.SPRINT_FOCUS_FACTOR);
		assertEquals(ExpectedDesc.getAvailableDays(), cs.SPRINT_AVAILABLE_DAY);
		assertEquals(ExpectedDesc.getNotes(), cs.TEST_SPRINT_NOTE + "5");
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 4*2*7));
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 5*2*7-1));
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 5*2*7-1));
		assertEquals(ExpectedDesc.getDemoPlace(), cs.SPRINT_DEMOPLACE);
	}
	
	public void testloadbyID_1() {
		// 設定時間
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		
		CreateSprint cs = new CreateSprint(5, this.CP);
		cs.exe();

		List<ISprintPlanDesc> descs = this.loader.getSprintPlanList();	// sort by StartDate
		assertEquals(descs.size(), 5);
		
		ISprintPlanDesc ExpectedDesc = null;
		for (int i=0 ; i<descs.size() ; i++) {
			String ID = Integer.toString(i+1);
			ExpectedDesc = this.loader.getSprintPlan(ID);
			assertEquals(ExpectedDesc.getID(), ID);
			assertEquals(ExpectedDesc.getGoal(), cs.TEST_SPRINT_GOAL + ID);
			assertEquals(ExpectedDesc.getInterval(), cs.SPRINT_INTERVAL);
			assertEquals(ExpectedDesc.getMemberNumber(), cs.SPRINT_MEMBER);
			assertEquals(ExpectedDesc.getFocusFactor(), cs.SPRINT_FOCUS_FACTOR);
			assertEquals(ExpectedDesc.getAvailableDays(), cs.SPRINT_AVAILABLE_DAY);
			assertEquals(ExpectedDesc.getNotes(), cs.TEST_SPRINT_NOTE + ID);
			assertEquals(ExpectedDesc.getStartDate(), getDate(today, i*2*7));
			assertEquals(ExpectedDesc.getEndDate(), getDate(today, (i+1)*2*7-1));
			assertEquals(ExpectedDesc.getDemoDate(), getDate(today, (i+1)*2*7-1));
			assertEquals(ExpectedDesc.getDemoPlace(), cs.SPRINT_DEMOPLACE);
		}
		
		ExpectedDesc = this.loader.getSprintPlan("6");
		assertEquals(ExpectedDesc.getID(), "-1");
		assertEquals(ExpectedDesc.getMemberNumber(), "0");
		assertEquals(ExpectedDesc.getFocusFactor(), "0");
		assertEquals(ExpectedDesc.getAvailableDays(), "0");
		assertEquals(ExpectedDesc.getGoal(), "");
		assertEquals(ExpectedDesc.getInterval(), "");
		assertEquals(ExpectedDesc.getNotes(), "");
		assertEquals(ExpectedDesc.getStartDate(), "");
		assertEquals(ExpectedDesc.getDemoDate(), "");
		assertEquals(ExpectedDesc.getEndDate(), "");
		assertEquals(ExpectedDesc.getDemoPlace(), "");
		
		ExpectedDesc = this.loader.getSprintPlan("-1");
		assertEquals(ExpectedDesc.getID(), "-1");
		assertEquals(ExpectedDesc.getMemberNumber(), "0");
		assertEquals(ExpectedDesc.getFocusFactor(), "0");
		assertEquals(ExpectedDesc.getAvailableDays(), "0");
		assertEquals(ExpectedDesc.getGoal(), "");
		assertEquals(ExpectedDesc.getInterval(), "");
		assertEquals(ExpectedDesc.getNotes(), "");
		assertEquals(ExpectedDesc.getStartDate(), "");
		assertEquals(ExpectedDesc.getDemoDate(), "");
		assertEquals(ExpectedDesc.getEndDate(), "");
		assertEquals(ExpectedDesc.getDemoPlace(), "");
	}
	
	public void testloadbyID_2() {
		// 設定時間
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		
		CreateSprint cs = new CreateSprint(5, this.CP);
		cs.exe();

		List<ISprintPlanDesc> descs = this.loader.getSprintPlanList();	// sort by StartDate
		assertEquals(descs.size(), 5);
		
		ISprintPlanDesc ExpectedDesc = null;
		for (int i=0 ; i<descs.size() ; i++) {
			String ID = Integer.toString(i+1);
			ExpectedDesc = this.loader.getSprintPlan(ID);
			assertEquals(ExpectedDesc.getID(), ID);
			assertEquals(ExpectedDesc.getGoal(), cs.TEST_SPRINT_GOAL + ID);
			assertEquals(ExpectedDesc.getInterval(), cs.SPRINT_INTERVAL);
			assertEquals(ExpectedDesc.getMemberNumber(), cs.SPRINT_MEMBER);
			assertEquals(ExpectedDesc.getFocusFactor(), cs.SPRINT_FOCUS_FACTOR);
			assertEquals(ExpectedDesc.getAvailableDays(), cs.SPRINT_AVAILABLE_DAY);
			assertEquals(ExpectedDesc.getNotes(), cs.TEST_SPRINT_NOTE + ID);
			assertEquals(ExpectedDesc.getStartDate(), getDate(today, i*2*7));
			assertEquals(ExpectedDesc.getEndDate(), getDate(today, (i+1)*2*7-1));
			assertEquals(ExpectedDesc.getDemoDate(), getDate(today, (i+1)*2*7-1));
			assertEquals(ExpectedDesc.getDemoPlace(), cs.SPRINT_DEMOPLACE);
		}
		
		ExpectedDesc = this.loader.getSprintPlan("6");
		assertEquals(ExpectedDesc.getID(), "-1");
		assertEquals(ExpectedDesc.getMemberNumber(), "0");
		assertEquals(ExpectedDesc.getFocusFactor(), "0");
		assertEquals(ExpectedDesc.getAvailableDays(), "0");
		assertEquals(ExpectedDesc.getGoal(), "");
		assertEquals(ExpectedDesc.getInterval(), "");
		assertEquals(ExpectedDesc.getNotes(), "");
		assertEquals(ExpectedDesc.getStartDate(), "");
		assertEquals(ExpectedDesc.getDemoDate(), "");
		assertEquals(ExpectedDesc.getEndDate(), "");
		assertEquals(ExpectedDesc.getDemoPlace(), "");
		
		ExpectedDesc = this.loader.getSprintPlan("-1");
		assertEquals(ExpectedDesc.getID(), "-1");
		assertEquals(ExpectedDesc.getMemberNumber(), "0");
		assertEquals(ExpectedDesc.getFocusFactor(), "0");
		assertEquals(ExpectedDesc.getAvailableDays(), "0");
		assertEquals(ExpectedDesc.getGoal(), "");
		assertEquals(ExpectedDesc.getInterval(), "");
		assertEquals(ExpectedDesc.getNotes(), "");
		assertEquals(ExpectedDesc.getStartDate(), "");
		assertEquals(ExpectedDesc.getDemoDate(), "");
		assertEquals(ExpectedDesc.getEndDate(), "");
		assertEquals(ExpectedDesc.getDemoPlace(), "");
		
		ExpectedDesc = this.loader.getSprintPlan("aaa");
		assertEquals(ExpectedDesc.getID(), "-1");
		assertEquals(ExpectedDesc.getMemberNumber(), "0");
		assertEquals(ExpectedDesc.getFocusFactor(), "0");
		assertEquals(ExpectedDesc.getAvailableDays(), "0");
		assertEquals(ExpectedDesc.getGoal(), "");
		assertEquals(ExpectedDesc.getInterval(), "");
		assertEquals(ExpectedDesc.getNotes(), "");
		assertEquals(ExpectedDesc.getStartDate(), "");
		assertEquals(ExpectedDesc.getDemoDate(), "");
		assertEquals(ExpectedDesc.getEndDate(), "");
		assertEquals(ExpectedDesc.getDemoPlace(), "");
	}
	
	/*
	 * 此部分要移到 SprintPlanHelperTest 去測試
	 */
//	public void testgetSprintIDbyDate() {
//		// 設定時間
//		Calendar cal = Calendar.getInstance();
//		
//		CreateSprint cs = new CreateSprint(5, this.CP);
//		cs.exe();
//
//		List<ISprintPlanDesc> descs = this.loader.getSprintPlanList();
//		assertEquals(descs.size(), 5);
//		
//		int ID = this.loader.getSprintIDbyDate(cal.getTime());
//		assertEquals(ID, 1);
//		
//		cal.add(Calendar.DAY_OF_YEAR, 0*2*7);
//		ID = this.loader.getSprintIDbyDate(cal.getTime());
//		assertEquals(ID, 1);
//		cal.add(Calendar.DAY_OF_YEAR, 1*2*7-1);
//		ID = this.loader.getSprintIDbyDate(cal.getTime());
//		assertEquals(ID, 1);
//		
//		cal.add(Calendar.DAY_OF_YEAR, 1);
//		ID = this.loader.getSprintIDbyDate(cal.getTime());
//		assertEquals(ID, 2);
//		cal.add(Calendar.DAY_OF_YEAR, 1*2*7-1);
//		ID = this.loader.getSprintIDbyDate(cal.getTime());
//		assertEquals(ID, 2);
//		
//		cal.add(Calendar.DAY_OF_YEAR, 1);
//		ID = this.loader.getSprintIDbyDate(cal.getTime());
//		assertEquals(ID, 3);
//		cal.add(Calendar.DAY_OF_YEAR, 1*2*7-1);
//		ID = this.loader.getSprintIDbyDate(cal.getTime());
//		assertEquals(ID, 3);
//		
//		cal.add(Calendar.DAY_OF_YEAR, 1);
//		ID = this.loader.getSprintIDbyDate(cal.getTime());
//		assertEquals(ID, 4);
//		cal.add(Calendar.DAY_OF_YEAR, 1*2*7-1);
//		ID = this.loader.getSprintIDbyDate(cal.getTime());
//		assertEquals(ID, 4);
//		
//		cal.add(Calendar.DAY_OF_YEAR, 1);
//		ID = this.loader.getSprintIDbyDate(cal.getTime());
//		assertEquals(ID, 5);
//		cal.add(Calendar.DAY_OF_YEAR, 1*2*7-1);
//		ID = this.loader.getSprintIDbyDate(cal.getTime());
//		assertEquals(ID, 5);
//		
//		
//		// out of bound
//		cal.add(Calendar.DAY_OF_YEAR, -5*2*7);
//		ID = this.loader.getSprintIDbyDate(cal.getTime());
//		assertEquals(ID, -1);
//		cal.add(Calendar.DAY_OF_YEAR, 6*2*7);
//		ID = this.loader.getSprintIDbyDate(cal.getTime());
//		assertEquals(ID, -1);
//	}

	private String getDate(Date date, int duration) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		Calendar cal_start = Calendar.getInstance();
		Calendar cal_end = Calendar.getInstance();
		cal_start.setTime(date);		// 得到今天的日期
		cal_end.setTime(date);			// 得到今天的日期
		cal_end.add(Calendar.DAY_OF_YEAR, duration);
		
		return format.format(cal_end.getTime());	// get start date
	}
	
	/*
	 * from Helper
	 */

	// sort by StartDate
	private List<ISprintPlanDesc> sortByStartDate(List<ISprintPlanDesc> list) {
		List<ISprintPlanDesc> newList = new ArrayList<ISprintPlanDesc>();
		for (ISprintPlanDesc source : list) {
			Date addDate = DateUtil.dayFilter(source.getStartDate());		// 要新增的 Date
			int index = 0;
			for (ISprintPlanDesc target : newList) {
				Date cmpDate = DateUtil.dayFilter(target.getStartDate());	// 要被比對的 Date
				if ( addDate.compareTo(cmpDate) < 0 ) {
					break;
				}
				index++;
			}
			newList.add(index, source);
		}		
		
		return newList;
	}

	// sort descent by ID
	private List<ISprintPlanDesc> sortById(List<ISprintPlanDesc> list) {
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