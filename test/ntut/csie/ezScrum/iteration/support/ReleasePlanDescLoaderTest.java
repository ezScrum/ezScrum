package ntut.csie.ezScrum.iteration.support;

import java.util.List;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.mapper.ReleasePlanMapper;
import ntut.csie.jcis.resource.core.IProject;

public class ReleasePlanDescLoaderTest extends TestCase {
	
	private Configuration configuration = null;
	private CreateProject CP = null;
	private IProject project = null;
	private ReleasePlanMapper loader = null;
	
	public ReleasePlanDescLoaderTest(String method) {
		super(method);
	}
	
	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.save();
		
		super.setUp();
		
		// initial SQL
		InitialSQL init = new InitialSQL(configuration);
		init.exe();
		
		// create project
		this.CP = new CreateProject(1);
		this.CP.exeCreate();
		this.project = this.CP.getProjectList().get(0);
		
		// initial loader
		this.loader = new ReleasePlanMapper(this.project);
		
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
		configuration.save();
		
		// release
		init = null;
		cp = null;
		this.CP = null;
		this.project = null;
		configuration = null;
		super.tearDown();
	}
	
	public void testloadReleasePlan() {
		// 資料尚未建立就去讀取，不會回傳任何資料
		List<IReleasePlanDesc> descs = this.loader.getReleasePlanList();
		assertEquals(descs.size(), 0);
		
		// 建立一筆假資料
		CreateRelease cr = new CreateRelease(2, this.CP);
		cr.exe();
		
		// 會回傳一筆 release
		descs = this.loader.getReleasePlanList();
		assertEquals(descs.size(), 2);
		
		IReleasePlanDesc desc = descs.get(0);
		assertEquals(desc.getID(), "1");
		assertEquals(desc.getName(), cr.TEST_RELEASE_NAME + "1");
		assertEquals(desc.getDescription(), cr.TEST_RELEASE_DESC + "1");
		
		desc = descs.get(1);
		assertEquals(desc.getID(), "2");
		assertEquals(desc.getName(), cr.TEST_RELEASE_NAME + "2");
		assertEquals(desc.getDescription(), cr.TEST_RELEASE_DESC + "2");
	}
	
	public void testloadReleasePlanList() {
		// 資料尚未建立就去讀取，不會回傳任何資料
		List<IReleasePlanDesc> descs = this.loader.getReleasePlanList();
		assertEquals(descs.size(), 0);
		
		// 建立一筆假資料
		CreateRelease cr = new CreateRelease(2, this.CP);
		cr.exe();
		
		// 會回傳一筆 release
		descs = this.loader.getReleasePlanList();
		assertEquals(descs.size(), 2);
		
		IReleasePlanDesc desc = descs.get(0);
		assertEquals(desc.getID(), "1");
		assertEquals(desc.getName(), cr.TEST_RELEASE_NAME + "1");
		assertEquals(desc.getDescription(), cr.TEST_RELEASE_DESC + "1");		
		
		desc = descs.get(1);
		assertEquals(desc.getID(), "2");
		assertEquals(desc.getName(), cr.TEST_RELEASE_NAME + "2");
		assertEquals(desc.getDescription(), cr.TEST_RELEASE_DESC + "2");
	}
}
