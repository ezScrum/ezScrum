package ntut.csie.ezScrum.iteration.support;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;
import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.iteration.iternal.ReleasePlanDesc;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.ezScrumInfoConfig;
import ntut.csie.ezScrum.web.mapper.ReleasePlanMapper;
import ntut.csie.jcis.resource.core.IProject;

public class ReleasePlanDescSaverTest extends TestCase {
	
	private ezScrumInfoConfig config = new ezScrumInfoConfig();
	private CreateProject CP = null;
	private IProject project = null;
	private ReleasePlanMapper saver = null;
	
	public ReleasePlanDescSaverTest(String method) {
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
		this.saver = new ReleasePlanMapper(this.project);
		
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
	
	public void testsaveReleasePlan() {
		// 測試讀取資料尚未存入
		ReleasePlanMapper loader = new ReleasePlanMapper(this.project);
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
		this.saver.addReleasePlan(ActualDesc_1);
		
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
		this.saver.addReleasePlan(ActualDesc_2);
		
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
	
	public void testsaveReleasePlan_duplicate() {
		// 測試讀取資料尚未存入
		ReleasePlanMapper loader = new ReleasePlanMapper(this.project);
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
		this.saver.addReleasePlan(ActualDesc_1);
		
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
		this.saver.addReleasePlan(ActualDesc_2);
		
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
		
		
		this.saver.addReleasePlan(ActualDesc_1);
		this.saver.addReleasePlan(ActualDesc_1);
		this.saver.addReleasePlan(ActualDesc_1);
		this.saver.addReleasePlan(ActualDesc_2);
		this.saver.addReleasePlan(ActualDesc_2);
		this.saver.addReleasePlan(ActualDesc_2);
		
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
	
	public void testsaveReleasePlan_empty() {
		// 測試讀取資料尚未存入
		ReleasePlanMapper loader = new ReleasePlanMapper(this.project);
		List<IReleasePlanDesc> descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 0);
		
		IReleasePlanDesc ActualDesc_1 = new ReleasePlanDesc();
		this.saver.addReleasePlan(ActualDesc_1);		
	}
	
	public void testeditReleasePlan_empty() {
		// 測試讀取資料尚未存入
		ReleasePlanMapper loader = new ReleasePlanMapper(this.project);
		List<IReleasePlanDesc> descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 0);
		
		IReleasePlanDesc ActualDesc_1 = new ReleasePlanDesc();
		this.saver.updateReleasePlan(ActualDesc_1);
	}
	
	public void testeditReleasePlan_ID() {
		// 測試讀取資料尚未存入
		ReleasePlanMapper loader = new ReleasePlanMapper(this.project);
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
		this.saver.addReleasePlan(ActualDesc_1);
		
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
		this.saver.updateReleasePlan(ActualDesc_1);
		descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 1);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), "1");
		assertEquals(ExpectedDesc.getName(), ActualDesc_1.getName());
		assertEquals(ExpectedDesc.getDescription(), ActualDesc_1.getDescription());
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
	}
	
	public void testeditReleasePlan_Name() {
		// 測試讀取資料尚未存入
		ReleasePlanMapper loader = new ReleasePlanMapper(this.project);
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
		this.saver.addReleasePlan(ActualDesc_1);
		
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
		this.saver.updateReleasePlan(ActualDesc_1);
		descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 1);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getName(), ActualDesc_1.getName());
		assertEquals(ExpectedDesc.getName(), "New Release Name - 1");
	}
	
	public void testeditReleasePlan_StartDate() {
		// 測試讀取資料尚未存入
		ReleasePlanMapper loader = new ReleasePlanMapper(this.project);
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
		this.saver.addReleasePlan(ActualDesc_1);
		
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
		this.saver.updateReleasePlan(ActualDesc_1);
		descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 1);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getStartDate(), getDate(today, 100));
	}
	
	public void testeditReleasePlan_EndDate() {
		// 測試讀取資料尚未存入
		ReleasePlanMapper loader = new ReleasePlanMapper(this.project);
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
		this.saver.addReleasePlan(ActualDesc_1);
		
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
		this.saver.updateReleasePlan(ActualDesc_1);
		descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 1);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
		assertEquals(ExpectedDesc.getEndDate(), getDate(today, 150));
	}
	
	public void testeditReleasePlan_Desc() {
		// 測試讀取資料尚未存入
		ReleasePlanMapper loader = new ReleasePlanMapper(this.project);
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
		this.saver.addReleasePlan(ActualDesc_1);
		
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
		this.saver.updateReleasePlan(ActualDesc_1);
		descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 1);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getDescription(), ActualDesc_1.getDescription());
		assertEquals(ExpectedDesc.getDescription(), "New Release Description - 1");
	}
	
	public void testdeleteReleasePlan_1() {
		// 測試讀取資料尚未存入
		ReleasePlanMapper loader = new ReleasePlanMapper(this.project);
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
		this.saver.addReleasePlan(ActualDesc_1);
		
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
		this.saver.addReleasePlan(ActualDesc_2);
		
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
		this.saver.deleteReleasePlan("1");
		descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 1);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), ActualDesc_2.getID());
		assertEquals(ExpectedDesc.getName(), ActualDesc_2.getName());
		assertEquals(ExpectedDesc.getDescription(), ActualDesc_2.getDescription());
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_2.getStartDate());
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_2.getEndDate());
	}
	
	public void testdeleteReleasePlan_2() {
		// 測試讀取資料尚未存入
		ReleasePlanMapper loader = new ReleasePlanMapper(this.project);
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
		this.saver.addReleasePlan(ActualDesc_1);
		
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
		this.saver.addReleasePlan(ActualDesc_2);
		
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
		this.saver.deleteReleasePlan("2");
		descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 1);
		ExpectedDesc = descs.get(0);
		assertEquals(ExpectedDesc.getID(), ActualDesc_1.getID());
		assertEquals(ExpectedDesc.getName(), ActualDesc_1.getName());
		assertEquals(ExpectedDesc.getDescription(), ActualDesc_1.getDescription());
		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
	}
	
	public void testdeleteReleasePlan_empty() {
		// 測試讀取資料尚未存入
		ReleasePlanMapper loader = new ReleasePlanMapper(this.project);
		List<IReleasePlanDesc> descs = loader.getReleasePlanList();
		assertEquals(descs.size(), 0);
		
		this.saver.deleteReleasePlan("1");
	}
	
//	public void testdeleteSpritnOfRelease() {
//		// 測試讀取資料尚未存入
//		ReleasePlanMapper loader = new ReleasePlanMapper(this.project);
//		List<IReleasePlanDesc> descs = loader.loadReleasePlan();
//		assertEquals(descs.size(), 0);
//
//		// 設定時間
//		Calendar cal = Calendar.getInstance();
//		Date today = cal.getTime();
//		
//		// 第一筆假資料
//		IReleasePlanDesc ActualDesc_1 = new ReleasePlanDesc();
//		ActualDesc_1.setID("1");
//		ActualDesc_1.setName("Release Name - 1");
//		ActualDesc_1.setDescription("Description - 1");
//		ActualDesc_1.setStartDate(getDate(today, 0));
//		ActualDesc_1.setEndDate(getDate(today, 180));
//		this.saver.saveReleasePlan(ActualDesc_1);
//		
//		// 驗證資料是否存入
//		descs = loader.loadReleasePlan();
//		assertEquals(descs.size(), 1);
//		IReleasePlanDesc ExpectedDesc = descs.get(0);
//		assertEquals(ExpectedDesc.getID(), ActualDesc_1.getID());
//		assertEquals(ExpectedDesc.getName(), ActualDesc_1.getName());
//		assertEquals(ExpectedDesc.getDescription(), ActualDesc_1.getDescription());
//		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
//		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
//		
//		// 測試刪除不存在的 sprint
//		this.saver.deleteSpritnOfRelease("1", "2");
//		descs = loader.loadReleasePlan();
//		assertEquals(descs.size(), 1);
//		ExpectedDesc = descs.get(0);
//		
//		
//		// 第二筆假資料
//		IReleasePlanDesc ActualDesc_2 = new ReleasePlanDesc();
//		ActualDesc_2.setID("2");
//		ActualDesc_2.setName("Release Name - 2");
//		ActualDesc_2.setStartDate(getDate(today, 0));
//		ActualDesc_2.setEndDate(getDate(today, 250));
//		this.saver.saveReleasePlan(ActualDesc_2);
//		
//		// 驗證資料是否存入
//		descs = loader.loadReleasePlan();
//		assertEquals(descs.size(), 2);
//		ExpectedDesc = descs.get(0);
//		assertEquals(ExpectedDesc.getID(), ActualDesc_1.getID());
//		assertEquals(ExpectedDesc.getName(), ActualDesc_1.getName());
//		assertEquals(ExpectedDesc.getDescription(), ActualDesc_1.getDescription());
//		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_1.getStartDate());
//		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_1.getEndDate());
//		
//		// 驗證資料是否存入
//		ExpectedDesc = descs.get(1);
//		assertEquals(ExpectedDesc.getID(), ActualDesc_2.getID());
//		assertEquals(ExpectedDesc.getName(), ActualDesc_2.getName());
//		assertEquals(ExpectedDesc.getDescription(), ActualDesc_2.getDescription());
//		assertEquals(ExpectedDesc.getStartDate(), ActualDesc_2.getStartDate());
//		assertEquals(ExpectedDesc.getEndDate(), ActualDesc_2.getEndDate());
//		
//		
//		// 測試刪除存在的 sprint
//		this.saver.deleteSpritnOfRelease("1", "2");
//		descs = loader.loadReleasePlan();
//		assertEquals(descs.size(), 1);
//		ExpectedDesc = descs.get(0);
//	}
	
//	public void testdeleteSpritnOfRelease_empty1() {
//		// 測試讀取資料尚未存入
//		ReleasePlanMapper loader = new ReleasePlanMapper(this.project);
//		List<IReleasePlanDesc> descs = loader.loadReleasePlan();
//		assertEquals(descs.size(), 0);
//		
//		this.saver.deleteSpritnOfRelease("1", "2");		
//	}
	
//	public void testdeleteSpritnOfRelease_empty2() {
//		// 測試讀取資料尚未存入
//		ReleasePlanMapper loader = new ReleasePlanMapper(this.project);
//		List<IReleasePlanDesc> descs = loader.loadReleasePlan();
//		assertEquals(descs.size(), 0);
//		
//		this.saver.deleteSpritnOfRelease("?", null);		
//	}
	
//	public void testdeleteSpritnOfRelease_empty3() {
//		// 測試讀取資料尚未存入
//		ReleasePlanMapper loader = new ReleasePlanMapper(this.project);
//		List<IReleasePlanDesc> descs = loader.loadReleasePlan();
//		assertEquals(descs.size(), 0);
//		
//		this.saver.deleteSpritnOfRelease(null, "?");		
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
}