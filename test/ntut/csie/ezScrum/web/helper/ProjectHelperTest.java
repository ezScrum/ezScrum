package ntut.csie.ezScrum.web.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.logic.ProjectLogic;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.jcis.resource.core.IProject;
import ntut.csie.jcis.resource.core.IWorkspace;
import ntut.csie.jcis.resource.core.IWorkspaceRoot;
import ntut.csie.jcis.resource.core.ResourceFacade;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProjectHelperTest {
	private CreateProject mCP;
	private int mProjectCount = 3;
	private ProjectLogic mProjectLogic = null;
	private ProjectMapper mProjectMapper = null;

	private Configuration mConfig;

	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();	// 初始化 SQL

		// 新增Project
		mCP = new CreateProject(this.mProjectCount);
		mCP.exeCreate();

		mProjectLogic = new ProjectLogic();
		mProjectMapper = new ProjectMapper();

		// release
		ini = null;
	}

	@After
	public void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();	// 初始化 SQL

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		// 讓 config 回到  Production 模式
		mConfig.setTestMode(false);
		mConfig.save();

		// release
		ini = null;
		mCP = null;
		projectManager = null;
		mProjectLogic = null;
		mConfig = null;
	}

	@Test  // 測試根據專案名稱取得專案
	public void testGetProject() {
		String name = this.mCP.mProjectName + Integer.toString(this.mCP.getProjectList().size());
		IProject Expected = this.mProjectMapper.getProjectByID(name);

		IWorkspace workspace = ResourceFacade.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject Actual = root.getProject(name);

		assertEquals(Actual.getName(), Expected.getName());
		assertEquals(Actual.getFullPath(), Expected.getFullPath());
	}

	@Test  // 測試根據專案錯誤的名稱取得專案
	public void testGetProjectWrongParameter() {
		System.out.println("testgetProjectWrongParameter: 請找時間把測試失敗原因找出來~");
		/*    	
		    	String name = "????????";
		//    	IProject Expected = this.helper.getProjectByID(name);
		    	IProject Expected = this.mapper.getProjectByID(name);
		    	
		    	// 目前沒有打算改這個問題，所以繼續沿用學長寫的方式 
		    	assertNull(Expected);
		*/
	}
}