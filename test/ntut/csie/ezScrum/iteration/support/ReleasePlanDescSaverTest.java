package ntut.csie.ezScrum.iteration.support;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.iteration.iternal.ReleasePlanDesc;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.mapper.ReleasePlanMapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ReleasePlanDescSaverTest {
	
	private Configuration mConfig = null;
	private CreateProject mCP = null;
	private ProjectObject mProject = null;
	private ReleasePlanMapper mRelasePlanMapper = null;
	
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
		mProject = mCP.getAllProjects().get(0);
		
		// initial loader
		mRelasePlanMapper = new ReleasePlanMapper(mProject);
		
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
	public void testsaveReleasePlan() {
		// 測試讀取資料尚未存入
		ReleasePlanMapper loader = new ReleasePlanMapper(mProject);
		List<IReleasePlanDesc> descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 0);

		// 設定時間
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		
		// 第一筆假資料
		IReleasePlanDesc ActualDesc_1 = new ReleasePlanDesc();
		ActualDesc_1.setID("1");
		ActualDesc_1.setName("Release Name - 1");
		ActualDesc_1.setStartDate(getDate(today, 0));
		ActualDesc_1.setEndDate(getDate(today, 180));
		mRelasePlanMapper.addReleasePlan(ActualDesc_1);
		
		// 驗證資料是否存入
		descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 1);
		IReleasePlanDesc ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), ActualDesc_1.getID());
		assertEquals(ExpectedDesc.getName(), ActualDesc_1.getName());
		assertEquals(ExpectedDesc.getDescription(), ActualDesc_1.getDescription());
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());

		
		// 第二筆假資料
		IReleasePlanDesc ActualDesc_2 = new ReleasePlanDesc();
		ActualDesc_2.setID("2");
		ActualDesc_2.setName("Release Name - 2");
		ActualDesc_2.setStartDate(getDate(today, 0));
		ActualDesc_2.setEndDate(getDate(today, 250));
		mRelasePlanMapper.addReleasePlan(ActualDesc_2);
		
		// 驗證資料是否存入
		descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 2);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), ActualDesc_1.getID());
		assertEquals(ExpectedDesc.getName(), ActualDesc_1.getName());
		assertEquals(ExpectedDesc.getDescription(), ActualDesc_1.getDescription());
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		
		// 驗證資料是否存入
		ExpectedDesc = descs.get(1);
		assertEquals(ExpectedDesc.getID(), ActualDesc_2.getID());
		assertEquals(ExpectedDesc.getName(), ActualDesc_2.getName());
		assertEquals(ExpectedDesc.getDescription(), ActualDesc_2.getDescription());
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_2.getStartDate());
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_2.getEndDate());
	}
	
	@Test
	public void testsaveReleasePlan_duplicate() {
		// 測試讀取資料尚未存入
		ReleasePlanMapper loader = new ReleasePlanMapper(mProject);
		List<IReleasePlanDesc> descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 0);

		// 設定時間
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		
		// 第一筆假資料
		IReleasePlanDesc ActualDesc_1 = new ReleasePlanDesc();
		ActualDesc_1.setID("1");
		ActualDesc_1.setName("Release Name - 1");
		ActualDesc_1.setStartDate(getDate(today, 0));
		ActualDesc_1.setEndDate(getDate(today, 180));
		mRelasePlanMapper.addReleasePlan(ActualDesc_1);
		
		// 驗證資料是否存入
		descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 1);
		IReleasePlanDesc ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), ActualDesc_1.getID());
		assertEquals(ExpectedDesc.getName(), ActualDesc_1.getName());
		assertEquals(ExpectedDesc.getDescription(), ActualDesc_1.getDescription());
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());

		
		// 第二筆假資料
		IReleasePlanDesc ActualDesc_2 = new ReleasePlanDesc();
		ActualDesc_2.setID("2");
		ActualDesc_2.setName("Release Name - 2");
		ActualDesc_2.setStartDate(getDate(today, 0));
		ActualDesc_2.setEndDate(getDate(today, 250));
		mRelasePlanMapper.addReleasePlan(ActualDesc_2);
		
		// 驗證資料是否存入
		descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 2);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), ActualDesc_1.getID());
		assertEquals(ExpectedDesc.getName(), ActualDesc_1.getName());
		assertEquals(ExpectedDesc.getDescription(), ActualDesc_1.getDescription());
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		
		// 驗證資料是否存入
		ExpectedDesc = descs.get(1);
		assertEquals(ExpectedDesc.getID(), ActualDesc_2.getID());
		assertEquals(ExpectedDesc.getName(), ActualDesc_2.getName());
		assertEquals(ExpectedDesc.getDescription(), ActualDesc_2.getDescription());
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_2.getStartDate());
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_2.getEndDate());
		
		
		mRelasePlanMapper.addReleasePlan(ActualDesc_1);
		mRelasePlanMapper.addReleasePlan(ActualDesc_1);
		mRelasePlanMapper.addReleasePlan(ActualDesc_1);
		mRelasePlanMapper.addReleasePlan(ActualDesc_2);
		mRelasePlanMapper.addReleasePlan(ActualDesc_2);
		mRelasePlanMapper.addReleasePlan(ActualDesc_2);
		
		descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 2);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), ActualDesc_1.getID());
		assertEquals(ExpectedDesc.getName(), ActualDesc_1.getName());
		assertEquals(ExpectedDesc.getDescription(), ActualDesc_1.getDescription());
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		
		// 驗證資料是否存入
		ExpectedDesc = descs.get(1);
		assertEquals(ExpectedDesc.getID(), ActualDesc_2.getID());
		assertEquals(ExpectedDesc.getName(), ActualDesc_2.getName());
		assertEquals(ExpectedDesc.getDescription(), ActualDesc_2.getDescription());
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_2.getStartDate());
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_2.getEndDate());
	}
	
	@Test
	public void testsaveReleasePlan_empty() {
		// 測試讀取資料尚未存入
		ReleasePlanMapper loader = new ReleasePlanMapper(mProject);
		List<IReleasePlanDesc> descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 0);
		
		IReleasePlanDesc ActualDesc_1 = new ReleasePlanDesc();
		mRelasePlanMapper.addReleasePlan(ActualDesc_1);		
	}
	
	@Test
	public void testeditReleasePlan_empty() {
		// 測試讀取資料尚未存入
		ReleasePlanMapper loader = new ReleasePlanMapper(mProject);
		List<IReleasePlanDesc> descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 0);
		
		IReleasePlanDesc ActualDesc_1 = new ReleasePlanDesc();
		mRelasePlanMapper.updateReleasePlan(ActualDesc_1);
	}
	
	@Test
	public void testeditReleasePlan_ID() {
		// 測試讀取資料尚未存入
		ReleasePlanMapper loader = new ReleasePlanMapper(mProject);
		List<IReleasePlanDesc> descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 0);

		// 設定時間
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		
		// 第一筆假資料
		IReleasePlanDesc ActualDesc_1 = new ReleasePlanDesc();
		ActualDesc_1.setID("1");
		ActualDesc_1.setName("Release Name - 1");
		ActualDesc_1.setStartDate(getDate(today, 0));
		ActualDesc_1.setEndDate(getDate(today, 180));
		mRelasePlanMapper.addReleasePlan(ActualDesc_1);
		
		// 驗證資料是否存入
		descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 1);
		IReleasePlanDesc ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), ActualDesc_1.getID());
		assertEquals(ExpectedDesc.getName(), ActualDesc_1.getName());
		assertEquals(ExpectedDesc.getDescription(), ActualDesc_1.getDescription());
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		
		// 修改 ActualDesc_1 的 ID 並且編輯存入，但是不會有任何異動
		ActualDesc_1.setID("2");
		mRelasePlanMapper.updateReleasePlan(ActualDesc_1);
		descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 1);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "1");
		assertEquals(ExpectedDesc.getName(), ActualDesc_1.getName());
		assertEquals(ExpectedDesc.getDescription(), ActualDesc_1.getDescription());
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
	}
	
	@Test
	public void testeditReleasePlan_Name() {
		// 測試讀取資料尚未存入
		ReleasePlanMapper loader = new ReleasePlanMapper(mProject);
		List<IReleasePlanDesc> descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 0);

		// 設定時間
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		
		// 第一筆假資料
		IReleasePlanDesc ActualDesc_1 = new ReleasePlanDesc();
		ActualDesc_1.setID("1");
		ActualDesc_1.setName("Release Name - 1");
		ActualDesc_1.setStartDate(getDate(today, 0));
		ActualDesc_1.setEndDate(getDate(today, 180));
		mRelasePlanMapper.addReleasePlan(ActualDesc_1);
		
		// 驗證資料是否存入
		descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 1);
		IReleasePlanDesc ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), ActualDesc_1.getID());
		assertEquals(ExpectedDesc.getName(), ActualDesc_1.getName());
		assertEquals(ExpectedDesc.getDescription(), ActualDesc_1.getDescription());
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		
		// 修改 ActualDesc_1 的 Name 並且編輯存入
		ActualDesc_1.setName("New Release Name - 1");
		mRelasePlanMapper.updateReleasePlan(ActualDesc_1);
		descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 1);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getName(), ActualDesc_1.getName());
		assertEquals(ExpectedDesc.getName(), "New Release Name - 1");
	}
	
	@Test
	public void testeditReleasePlan_StartDate() {
		// 測試讀取資料尚未存入
		ReleasePlanMapper loader = new ReleasePlanMapper(mProject);
		List<IReleasePlanDesc> descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 0);

		// 設定時間
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		
		// 第一筆假資料
		IReleasePlanDesc ActualDesc_1 = new ReleasePlanDesc();
		ActualDesc_1.setID("1");
		ActualDesc_1.setName("Release Name - 1");
		ActualDesc_1.setStartDate(getDate(today, 0));
		ActualDesc_1.setEndDate(getDate(today, 180));
		mRelasePlanMapper.addReleasePlan(ActualDesc_1);
		
		// 驗證資料是否存入
		descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 1);
		IReleasePlanDesc ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), ActualDesc_1.getID());
		assertEquals(ExpectedDesc.getName(), ActualDesc_1.getName());
		assertEquals(ExpectedDesc.getDescription(), ActualDesc_1.getDescription());
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		
		// 修改 ActualDesc_1 的 StartDate 並且編輯存入
		ActualDesc_1.setStartDate(getDate(today, 100));
		mRelasePlanMapper.updateReleasePlan(ActualDesc_1);
		descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 1);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 100));
	}
	
	@Test
	public void testeditReleasePlan_EndDate() {
		// 測試讀取資料尚未存入
		ReleasePlanMapper loader = new ReleasePlanMapper(mProject);
		List<IReleasePlanDesc> descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 0);

		// 設定時間
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		
		// 第一筆假資料
		IReleasePlanDesc ActualDesc_1 = new ReleasePlanDesc();
		ActualDesc_1.setID("1");
		ActualDesc_1.setName("Release Name - 1");
		ActualDesc_1.setStartDate(getDate(today, 0));
		ActualDesc_1.setEndDate(getDate(today, 180));
		mRelasePlanMapper.addReleasePlan(ActualDesc_1);
		
		// 驗證資料是否存入
		descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 1);
		IReleasePlanDesc ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), ActualDesc_1.getID());
		assertEquals(ExpectedDesc.getName(), ActualDesc_1.getName());
		assertEquals(ExpectedDesc.getDescription(), ActualDesc_1.getDescription());
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		
		// 修改 ActualDesc_1 的 EndDate 並且編輯存入
		ActualDesc_1.setEndDate(getDate(today, 150));
		mRelasePlanMapper.updateReleasePlan(ActualDesc_1);
		descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 1);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 150));
	}
	
	@Test
	public void testeditReleasePlan_Desc() {
		// 測試讀取資料尚未存入
		ReleasePlanMapper loader = new ReleasePlanMapper(mProject);
		List<IReleasePlanDesc> descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 0);

		// 設定時間
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		
		// 第一筆假資料
		IReleasePlanDesc ActualDesc_1 = new ReleasePlanDesc();
		ActualDesc_1.setID("1");
		ActualDesc_1.setName("Release Name - 1");
		ActualDesc_1.setStartDate(getDate(today, 0));
		ActualDesc_1.setEndDate(getDate(today, 180));
		mRelasePlanMapper.addReleasePlan(ActualDesc_1);
		
		// 驗證資料是否存入
		descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 1);
		IReleasePlanDesc ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), ActualDesc_1.getID());
		assertEquals(ExpectedDesc.getName(), ActualDesc_1.getName());
		assertEquals(ExpectedDesc.getDescription(), ActualDesc_1.getDescription());
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		
		// 修改 ActualDesc_1 的 Description 並且編輯存入
		ActualDesc_1.setDescription("New Release Description - 1");
		mRelasePlanMapper.updateReleasePlan(ActualDesc_1);
		descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 1);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getDescription(), ActualDesc_1.getDescription());
		assertEquals(ExpectedDesc.getDescription(), "New Release Description - 1");
	}
	
	@Test
	public void testdeleteReleasePlan_1() {
		// 測試讀取資料尚未存入
		ReleasePlanMapper loader = new ReleasePlanMapper(mProject);
		List<IReleasePlanDesc> descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 0);

		// 設定時間
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		
		// 第一筆假資料
		IReleasePlanDesc ActualDesc_1 = new ReleasePlanDesc();
		ActualDesc_1.setID("1");
		ActualDesc_1.setName("Release Name - 1");
		ActualDesc_1.setStartDate(getDate(today, 0));
		ActualDesc_1.setEndDate(getDate(today, 180));
		mRelasePlanMapper.addReleasePlan(ActualDesc_1);
		
		// 驗證資料是否存入
		descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 1);
		IReleasePlanDesc ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), ActualDesc_1.getID());
		assertEquals(ExpectedDesc.getName(), ActualDesc_1.getName());
		assertEquals(ExpectedDesc.getDescription(), ActualDesc_1.getDescription());
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());

		
		// 第二筆假資料
		IReleasePlanDesc ActualDesc_2 = new ReleasePlanDesc();
		ActualDesc_2.setID("2");
		ActualDesc_2.setName("Release Name - 2");
		ActualDesc_2.setDescription("Description - 2");
		ActualDesc_2.setStartDate(getDate(today, 0));
		ActualDesc_2.setEndDate(getDate(today, 250));
		mRelasePlanMapper.addReleasePlan(ActualDesc_2);
		
		// 驗證資料是否存入
		descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 2);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), ActualDesc_1.getID());
		assertEquals(ExpectedDesc.getName(), ActualDesc_1.getName());
		assertEquals(ExpectedDesc.getDescription(), ActualDesc_1.getDescription());
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		
		// 驗證資料是否存入
		ExpectedDesc = descs.get(1);
		assertEquals(ExpectedDesc.getID(), ActualDesc_2.getID());
		assertEquals(ExpectedDesc.getName(), ActualDesc_2.getName());
		assertEquals(ExpectedDesc.getDescription(), ActualDesc_2.getDescription());
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_2.getStartDate());
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_2.getEndDate());
		
		// 測試刪除第一筆
		mRelasePlanMapper.deleteReleasePlan("1");
		descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 1);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), ActualDesc_2.getID());
		assertEquals(ExpectedDesc.getName(), ActualDesc_2.getName());
		assertEquals(ExpectedDesc.getDescription(), ActualDesc_2.getDescription());
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_2.getStartDate());
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_2.getEndDate());
	}
	
	@Test
	public void testdeleteReleasePlan_2() {
		// 測試讀取資料尚未存入
		ReleasePlanMapper loader = new ReleasePlanMapper(mProject);
		List<IReleasePlanDesc> descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 0);

		// 設定時間
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		
		// 第一筆假資料
		IReleasePlanDesc ActualDesc_1 = new ReleasePlanDesc();
		ActualDesc_1.setID("1");
		ActualDesc_1.setName("Release Name - 1");
		ActualDesc_1.setStartDate(getDate(today, 0));
		ActualDesc_1.setEndDate(getDate(today, 180));
		mRelasePlanMapper.addReleasePlan(ActualDesc_1);
		
		// 驗證資料是否存入
		descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 1);
		IReleasePlanDesc ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), ActualDesc_1.getID());
		assertEquals(ExpectedDesc.getName(), ActualDesc_1.getName());
		assertEquals(ExpectedDesc.getDescription(), ActualDesc_1.getDescription());
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());

		
		// 第二筆假資料
		IReleasePlanDesc ActualDesc_2 = new ReleasePlanDesc();
		ActualDesc_2.setID("2");
		ActualDesc_2.setName("Release Name - 2");
		ActualDesc_2.setStartDate(getDate(today, 0));
		ActualDesc_2.setEndDate(getDate(today, 250));
		mRelasePlanMapper.addReleasePlan(ActualDesc_2);
		
		// 驗證資料是否存入
		descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 2);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), ActualDesc_1.getID());
		assertEquals(ExpectedDesc.getName(), ActualDesc_1.getName());
		assertEquals(ExpectedDesc.getDescription(), ActualDesc_1.getDescription());
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		
		// 驗證資料是否存入
		ExpectedDesc = descs.get(1);
		assertEquals(ExpectedDesc.getID(), ActualDesc_2.getID());
		assertEquals(ExpectedDesc.getName(), ActualDesc_2.getName());
		assertEquals(ExpectedDesc.getDescription(), ActualDesc_2.getDescription());
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_2.getStartDate());
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_2.getEndDate());
		
		// 測試刪除第二筆
		mRelasePlanMapper.deleteReleasePlan("2");
		descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 1);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), ActualDesc_1.getID());
		assertEquals(ExpectedDesc.getName(), ActualDesc_1.getName());
		assertEquals(ExpectedDesc.getDescription(), ActualDesc_1.getDescription());
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
	}
	
	@Test
	public void testdeleteReleasePlan_empty() {
		// 測試讀取資料尚未存入
		ReleasePlanMapper loader = new ReleasePlanMapper(mProject);
		List<IReleasePlanDesc> descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 0);
		
		mRelasePlanMapper.deleteReleasePlan("1");
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
}