package ntut.csie.ezScrum.iteration.support;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.mapper.SprintPlanMapper;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SprintPlanDescLoaderTest {
	
	private Configuration mConfig = null;
	private CreateProject mCP = null;
	private IProject mProject = null;
	private SprintPlanMapper mSprintPlanMapper = null;
	
	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		// initial SQL
		InitialSQL init = new InitialSQL(mConfig);
		init.exe();
		
		// create project
		mCP = new CreateProject(1);
		mCP.exeCreate();
		mProject = mCP.getProjectList().get(0);
		
		// initial loader
		mSprintPlanMapper = new SprintPlanMapper(mProject);
		
		// release
		init = null;
	}
	
	@After
	public void tearDown() throws Exception {
		// initial SQL
		InitialSQL init = new InitialSQL(mConfig);
		init.exe();
		
		// copy and delete test project
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		
		mConfig.setTestMode(false);
		mConfig.save();
		
		// release
		init = null;
		mCP = null;
		mProject = null;
		mConfig = null;
	}

	@Test
	public void testload() {
		// 設定時間
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		
		CreateSprint cs = new CreateSprint(5, mCP);
		cs.exe();
		
		List<ISprintPlanDesc> descs = mSprintPlanMapper.getSprintPlanList();
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
		
		// 修改 ID，測試是否 sort by ID
		// ID 1 與 ID 5 交換，由於 saver 不能對 ID 作 edit，所以只好先砍掉再加回去
		SprintPlanMapper saver = new SprintPlanMapper(mProject);
		ExpectedDesc = descs.get(0);
		ExpectedDesc.setID("5");
		saver.deleteSprintPlan("5");
		saver.addSprintPlan(ExpectedDesc);
		
		ExpectedDesc = descs.get(4);
		ExpectedDesc.setID("1");
		saver.deleteSprintPlan("1");
		saver.addSprintPlan(ExpectedDesc);
		
		descs = mSprintPlanMapper.getSprintPlanList();	// sort by Id
		descs = sortById(descs);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "1");
		assertEquals(ExpectedDesc.getGoal(), cs.TEST_SPRINT_GOAL + "5");
		assertEquals(ExpectedDesc.getInterval(), CreateSprint.SPRINT_INTERVAL);
		assertEquals(ExpectedDesc.getMemberNumber(), CreateSprint.SPRINT_MEMBER);
		assertEquals(ExpectedDesc.getFocusFactor(), CreateSprint.SPRINT_FOCUS_FACTOR);
		assertEquals(ExpectedDesc.getAvailableDays(), CreateSprint.SPRINT_AVAILABLE_DAY);
		assertEquals(ExpectedDesc.getNotes(), cs.TEST_SPRINT_NOTE + "5");
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 4*2*7));
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 5*2*7-1));
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 5*2*7-1));
		assertEquals(ExpectedDesc.getDemoPlace(), CreateSprint.SPRINT_DEMOPLACE);
		
		ExpectedDesc = descs.get(4);
		assertEquals(ExpectedDesc.getID(), "5");
		assertEquals(ExpectedDesc.getGoal(), cs.TEST_SPRINT_GOAL + "1");
		assertEquals(ExpectedDesc.getInterval(), CreateSprint.SPRINT_INTERVAL);
		assertEquals(ExpectedDesc.getMemberNumber(), CreateSprint.SPRINT_MEMBER);
		assertEquals(ExpectedDesc.getFocusFactor(), CreateSprint.SPRINT_FOCUS_FACTOR);
		assertEquals(ExpectedDesc.getAvailableDays(), CreateSprint.SPRINT_AVAILABLE_DAY);
		assertEquals(ExpectedDesc.getNotes(), cs.TEST_SPRINT_NOTE + "1");
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 0*2*7));
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 1*2*7-1));
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 1*2*7-1));
		assertEquals(ExpectedDesc.getDemoPlace(), CreateSprint.SPRINT_DEMOPLACE);
	}
	
	@Test
	public void testListload() {
		// 設定時間
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		
		CreateSprint cs = new CreateSprint(5, mCP);
		cs.exe();
		
		List<ISprintPlanDesc> descs = mSprintPlanMapper.getSprintPlanList();	// sort by StartDate
		descs = sortByStartDate(descs);				
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
		
		// 修改 ID，測試是否 sort by ID
		// ID 1 與 ID 5 交換，由於 saver 不能對 ID 作 edit，所以只好先砍掉再加回去
		SprintPlanMapper saver = new SprintPlanMapper(mProject);
		ExpectedDesc = descs.get(0);
		ExpectedDesc.setID("5");
		saver.deleteSprintPlan("5");
		saver.addSprintPlan(ExpectedDesc);
		
		ExpectedDesc = descs.get(4);
		ExpectedDesc.setID("1");
		saver.deleteSprintPlan("1");
		saver.addSprintPlan(ExpectedDesc);
		
		descs = mSprintPlanMapper.getSprintPlanList();	// sort by StartDate
		descs = sortByStartDate(descs);
		// 只有換 ID 不對會 listsort 影響
		for (int i=1 ; i<descs.size()-1 ; i++) {
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
		
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "5");
		assertEquals(ExpectedDesc.getGoal(), cs.TEST_SPRINT_GOAL + "1");
		assertEquals(ExpectedDesc.getInterval(), CreateSprint.SPRINT_INTERVAL);
		assertEquals(ExpectedDesc.getMemberNumber(), CreateSprint.SPRINT_MEMBER);
		assertEquals(ExpectedDesc.getFocusFactor(), CreateSprint.SPRINT_FOCUS_FACTOR);
		assertEquals(ExpectedDesc.getAvailableDays(), CreateSprint.SPRINT_AVAILABLE_DAY);
		assertEquals(ExpectedDesc.getNotes(), cs.TEST_SPRINT_NOTE + "1");
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 0*2*7));
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 1*2*7-1));
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 1*2*7-1));
		assertEquals(ExpectedDesc.getDemoPlace(), CreateSprint.SPRINT_DEMOPLACE);
		
		ExpectedDesc = descs.get(4);
		assertEquals(ExpectedDesc.getID(), "1");
		assertEquals(ExpectedDesc.getGoal(), cs.TEST_SPRINT_GOAL + "5");
		assertEquals(ExpectedDesc.getInterval(), CreateSprint.SPRINT_INTERVAL);
		assertEquals(ExpectedDesc.getMemberNumber(), CreateSprint.SPRINT_MEMBER);
		assertEquals(ExpectedDesc.getFocusFactor(), CreateSprint.SPRINT_FOCUS_FACTOR);
		assertEquals(ExpectedDesc.getAvailableDays(), CreateSprint.SPRINT_AVAILABLE_DAY);
		assertEquals(ExpectedDesc.getNotes(), cs.TEST_SPRINT_NOTE + "5");
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 4*2*7));
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 5*2*7-1));
		assertEquals(ExpectedDesc.getDemoDate(), getDate(today, 5*2*7-1));
		assertEquals(ExpectedDesc.getDemoPlace(), CreateSprint.SPRINT_DEMOPLACE);
	}
	
	@Test
	public void testloadbyID_1() {
		// 設定時間
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		
		CreateSprint cs = new CreateSprint(5, mCP);
		cs.exe();

		List<ISprintPlanDesc> descs = mSprintPlanMapper.getSprintPlanList();	// sort by StartDate
		assertEquals(descs.size(), 5);
		
		ISprintPlanDesc ExpectedDesc = null;
		for (int i=0 ; i<descs.size() ; i++) {
			String ID = Integer.toString(i+1);
			ExpectedDesc = mSprintPlanMapper.getSprintPlan(ID);
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
		
		ExpectedDesc = mSprintPlanMapper.getSprintPlan("6");
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
		
		ExpectedDesc = mSprintPlanMapper.getSprintPlan("-1");
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
	
	@Test
	public void testloadbyID_2() {
		// 設定時間
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		
		CreateSprint cs = new CreateSprint(5, mCP);
		cs.exe();

		List<ISprintPlanDesc> descs = mSprintPlanMapper.getSprintPlanList();	// sort by StartDate
		assertEquals(descs.size(), 5);
		
		ISprintPlanDesc ExpectedDesc = null;
		for (int i=0 ; i<descs.size() ; i++) {
			String ID = Integer.toString(i+1);
			ExpectedDesc = mSprintPlanMapper.getSprintPlan(ID);
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
		
		ExpectedDesc = mSprintPlanMapper.getSprintPlan("6");
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
		
		ExpectedDesc = mSprintPlanMapper.getSprintPlan("-1");
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
		
		ExpectedDesc = mSprintPlanMapper.getSprintPlan("aaa");
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