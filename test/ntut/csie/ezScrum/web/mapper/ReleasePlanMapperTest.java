package ntut.csie.ezScrum.web.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
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

	private int mProjectCount = 1;

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
		assertEquals(release.getProjectId(), mCP.getAllProjects().get(0)
				.getId());
		assertEquals("TEST_RELEASE_NAME", release.getName());
		assertEquals("TEST_RELEASE_DESCRIPTION", release.getDescription());
		assertEquals("2015/06/10", release.getStartDateString());
		assertEquals("2015/06/24", release.getDueDateString());
	}

	@Test
	public void testGetReleases() {
		// create releases
		long projectId = mCP.getAllProjects().get(0).getId();
		ReleaseObject release1 = new ReleaseObject(projectId);
		release1.setName("TEST_RELEASE_NAME_1")
		        .setStartDate("2015/06/10")
		        .setDueDate("2015/06/24")
		        .save();
		ReleaseObject release2 = new ReleaseObject(projectId);
		release2.setName("TEST_RELEASE_NAME_2")
		        .setStartDate("2015/07/10")
		        .setDueDate("2015/07/24")
		        .save();
		ReleaseObject release3 = new ReleaseObject(projectId);
		release3.setName("TEST_RELEASE_NAME_3")
		        .setStartDate("2015/08/10")
		        .setDueDate("2015/08/24")
		        .save();
		
		ArrayList<ReleaseObject> releases = mReleasePlanMapper.getReleases();
		// assert
		assertEquals(3, releases.size());
		// release 1
		assertEquals(release1.getId(), releases.get(0).getId());
		assertEquals(release1.getName(), releases.get(0).getName());
		assertEquals(release1.getStartDateString(), releases.get(0).getStartDateString());
		assertEquals(release1.getDueDateString(), releases.get(0).getDueDateString());
		// release 2
		assertEquals(release2.getId(), releases.get(1).getId());
		assertEquals(release2.getName(), releases.get(1).getName());
		assertEquals(release2.getStartDateString(), releases.get(1).getStartDateString());
		assertEquals(release2.getDueDateString(), releases.get(1).getDueDateString());
		// release 3
		assertEquals(release3.getId(), releases.get(2).getId());
		assertEquals(release3.getName(), releases.get(2).getName());
		assertEquals(release3.getStartDateString(), releases.get(2).getStartDateString());
		assertEquals(release3.getDueDateString(), releases.get(2).getDueDateString());
	}

	@Test
	public void testUpdateRelease() {
		ReleaseObject release = createRelease();
		
		ReleaseInfo releaseInfo = new ReleaseInfo();
		releaseInfo.id = release.getId();
		releaseInfo.name = "TEST_RELEASE_NAME_UPDATE";
		releaseInfo.description = "TEST_RELEASE_DESCRIPTION_UPDATE";
		releaseInfo.startDate = "2015/07/10";
		releaseInfo.dueDate = "2015/07/24";
		
		mReleasePlanMapper.updateRelease(releaseInfo);
		release = ReleaseObject.get(release.getId());
		assertEquals("TEST_RELEASE_NAME_UPDATE", release.getName());
		assertEquals("TEST_RELEASE_DESCRIPTION_UPDATE", release.getDescription());
		assertEquals("2015/07/10", release.getStartDateString());
		assertEquals("2015/07/24", release.getDueDateString());
	}

	@Test
	public void testDeleteRelease() {
		ReleaseObject release = createRelease();
		long releaseId = release.getId();
		assertTrue(releaseId > 0);
		// delete release
		mReleasePlanMapper.deleteRelease(releaseId);
		// get release
		ReleaseObject releaseFromDB = ReleaseObject.get(releaseId);
		assertNull(releaseFromDB);
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
