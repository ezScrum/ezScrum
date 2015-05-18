package ntut.csie.ezScrum.iteration.support;

import static org.junit.Assert.assertEquals;

import java.util.List;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.mapper.ReleasePlanMapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ReleasePlanDescLoaderTest {
	
	private Configuration mConfig = null;
	private CreateProject mCP = null;
	private ProjectObject mProject = null;
	private ReleasePlanMapper mReleasePlanMapper = null;
	
	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		mConfig = new Configuration(new UserSession(AccountObject.get("admin")));
		
		// initial SQL
		InitialSQL init = new InitialSQL(mConfig);
		init.exe();
		
		// create project
		mCP = new CreateProject(1);
		mCP.exeCreate();
		mProject = mCP.getAllProjects().get(0);
		
		// initial loader
		this.mReleasePlanMapper = new ReleasePlanMapper(mProject);
		
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
	public void testloadReleasePlan() {
		// 資料尚未建立就去讀取，不會回傳任何資料
		List<IReleasePlanDesc> descs = mReleasePlanMapper.getReleasePlanList();
		assertEquals(descs.size(), 0);
		
		// 建立一筆假資料
		CreateRelease cr = new CreateRelease(2, mCP);
		cr.exe();
		
		// 會回傳一筆 release
		descs = mReleasePlanMapper.getReleasePlanList();
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
	
	@Test
	public void testloadReleasePlanList() {
		// 資料尚未建立就去讀取，不會回傳任何資料
		List<IReleasePlanDesc> descs = mReleasePlanMapper.getReleasePlanList();
		assertEquals(descs.size(), 0);
		
		// 建立一筆假資料
		CreateRelease cr = new CreateRelease(2, mCP);
		cr.exe();
		
		// 會回傳一筆 release
		descs = this.mReleasePlanMapper.getReleasePlanList();
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
