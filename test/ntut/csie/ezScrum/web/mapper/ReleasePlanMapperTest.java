package ntut.csie.ezScrum.web.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataInfo.ReleaseInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.ReleaseObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ReleasePlanMapperTest {
	private static Log mlog = LogFactory.getLog(SprintPlanMapperTest.class);
	private CreateProject mCP;
	private CreateSprint mCS;
	private CreateProductBacklog mCPB;

	private int mProjectCount = 1;
	private int mSprintCount = 1;
	private int mStoryCount = 2;

	private ReleasePlanMapper mReleasePlanMapper = null;
	private Configuration mConfig = null;

	@Before
	public void setUp() {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 新增 Project
		mCP = new CreateProject(mProjectCount);
		mCP.exeCreate();

		mCS = new CreateSprint(mSprintCount, mCP);
		mCS.exe();

		// 新增 Story
		mCPB = new CreateProductBacklog(mStoryCount, mCP);
		mCPB.exe();

		// 建立 SprintPlanMapper 物件
		ProjectObject project = mCP.getAllProjects().get(0);
		mReleasePlanMapper = new ReleasePlanMapper(project);

		// ============= release ==============
		ini = null;
		project = null;
	}

	@After
	public void tearDown() {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		// 讓 config 回到 Production 模式
		mConfig.setTestMode(false);
		mConfig.save();

		// ============= release ==============
		ini = null;
		mCP = null;
		mCS = null;
		mCPB = null;
		mReleasePlanMapper = null;
		projectManager = null;
		mConfig = null;
	}

	@Test
	public void testAddRelease() {
		// create release
		ReleaseInfo releaseInfo = new ReleaseInfo();
		releaseInfo.name = "TEST_RELEASE_NAME";
		releaseInfo.description = "TEST_RELEASE_DESCRIPTION";
		releaseInfo.startDate = "2015/06/10";
		releaseInfo.dueDate = "2015/06/24";
		// add release
		long releaseId = mReleasePlanMapper.addRelease(releaseInfo);
		ReleaseObject release = ReleaseObject.get(releaseId);
		
		// assert
		assertTrue(release.getId() > 0);
		assertEquals(release.getProjectId(), mCP.getAllProjects().get(0).getId());
		assertEquals("TEST_RELEASE_NAME", release.getName());
		assertEquals("TEST_RELEASE_DESCRIPTION", release.getDescription());
		assertEquals("2015/06/10", release.getStartDateString());
		assertEquals("2015/06/24", release.getDueDateString());
	}

	@Test
	public void testGetReleases() {
		// TODO
	}

	@Test
	public void testUpdateRelease() {
		// TODO
	}

	@Test
	public void testDeleteRelease() {
		// TODO
	}

	private ReleaseObject createRelease() {
		// create release
		ReleaseInfo releaseInfo = new ReleaseInfo();
		releaseInfo.name = "TEST_RELEASE_NAME";
		releaseInfo.description = "TEST_RELEASE_DESCRIPTION";
		releaseInfo.startDate = "2015/06/10";
		releaseInfo.dueDate = "2015/06/24";
		// add release
		long releaseId = mReleasePlanMapper.addRelease(releaseInfo);
		ReleaseObject release = ReleaseObject.get(releaseId);
		// echo
		mlog.info("Create 1 test Release success.");
		return release;
	}
}
