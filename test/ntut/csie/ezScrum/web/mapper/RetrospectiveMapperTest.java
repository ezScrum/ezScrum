package ntut.csie.ezScrum.web.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.web.dataInfo.RetrospectiveInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.RetrospectiveObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RetrospectiveMapperTest {
	private ProjectObject mProject = null;
	private SprintObject mSprint = null;
	private RetrospectiveMapper mRetrospectiveMapper = null;
	private Configuration mConfig = null;

	@Before
	public void setUp() {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// initial SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// create project
		mProject = new ProjectObject("TEST_PROJECT");
		mProject.save();
		
		// create sprint
		mSprint = new SprintObject(mProject.getId());
		mSprint.save();

		// create retrospective mapper
		mRetrospectiveMapper = new RetrospectiveMapper(mProject);

		// ============= release ==============
		ini = null;
	}

	@After
	public void tearDown() {
		// initial SQL
		InitialSQL initialSQL = new InitialSQL(mConfig);
		initialSQL.exe();

		// delete external files
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		// set normal mode
		mConfig.setTestMode(false);
		mConfig.save();

		// ============= release ==============
		initialSQL = null;
		mProject = null;
		mSprint = null;
		mRetrospectiveMapper = null;
		projectManager = null;
		mConfig = null;
	}
	
	@Test
	public void testAddRetrospective() {
		RetrospectiveInfo retrospectiveInfo = new RetrospectiveInfo();
		retrospectiveInfo.name = "TEST_RETROSPECTIVE_NAME";
		retrospectiveInfo.description = "TEST_RETROSPECTIVE_DESCRIPTION";
		retrospectiveInfo.type = RetrospectiveObject.TYPE_GOOD;
		retrospectiveInfo.status = RetrospectiveObject.STATUS_NEW;
		retrospectiveInfo.sprintId = mSprint.getId();
		long retrospectiveId = mRetrospectiveMapper.addRetrospective(retrospectiveInfo);
		assertTrue(retrospectiveId > 0);
		RetrospectiveObject retrospective = RetrospectiveObject.get(retrospectiveId);
		assertNotNull(retrospective);
		assertEquals(mProject.getId(), retrospective.getProjectId());
		assertEquals(mSprint.getId(), retrospective.getSprintId());
		assertEquals("TEST_RETROSPECTIVE_NAME", retrospective.getName());
		assertEquals("TEST_RETROSPECTIVE_DESCRIPTION", retrospective.getDescription());
		assertEquals(RetrospectiveObject.TYPE_GOOD, retrospective.getType());
		assertEquals(RetrospectiveObject.STATUS_NEW, retrospective.getStatus());
	}
	
	@Test
	public void testuUpdateRetrospective() {
		RetrospectiveObject retrospective = new RetrospectiveObject(mProject.getId());
		retrospective.setName("TEST_RETROSPECTIVE_NAME")
		             .setDescription("TEST_RETROSPECTIVE_DESCRIPTION")
		             .setType(RetrospectiveObject.TYPE_GOOD)
		             .setStatus(RetrospectiveObject.STATUS_NEW)
		             .setSprintId(99)
		             .save();
		long retrospectiveId = retrospective.getId();
		assertTrue(retrospectiveId > 0);
		RetrospectiveInfo retrospectiveInfo = new RetrospectiveInfo();
		retrospectiveInfo.id = retrospectiveId;
		retrospectiveInfo.name = "TEST_RETROSPECTIVE_NAME_UPDATE";
		retrospectiveInfo.description = "TEST_RETROSPECTIVE_DESCRIPTION_UPDATE";
		retrospectiveInfo.type = RetrospectiveObject.TYPE_IMPROVEMENT;
		retrospectiveInfo.status = RetrospectiveObject.STATUS_RESOLVED;
		retrospectiveInfo.sprintId = mSprint.getId();
		mRetrospectiveMapper.updateRetrospective(retrospectiveInfo);
		retrospective = RetrospectiveObject.get(retrospectiveId);
		assertEquals(mProject.getId(), retrospective.getProjectId());
		assertEquals(mSprint.getId(), retrospective.getSprintId());
		assertEquals("TEST_RETROSPECTIVE_NAME_UPDATE", retrospective.getName());
		assertEquals("TEST_RETROSPECTIVE_DESCRIPTION_UPDATE", retrospective.getDescription());
		assertEquals(RetrospectiveObject.TYPE_IMPROVEMENT, retrospective.getType());
		assertEquals(RetrospectiveObject.STATUS_RESOLVED, retrospective.getStatus());
	}
	
	@Test
	public void testDeleteRetrospective() {
		RetrospectiveObject retrospective = new RetrospectiveObject(mProject.getId());
		retrospective.setName("TEST_RETROSPECTIVE_NAME")
		             .setDescription("TEST_RETROSPECTIVE_DESCRIPTION")
		             .setType(RetrospectiveObject.TYPE_GOOD)
		             .setStatus(RetrospectiveObject.STATUS_NEW)
		             .setSprintId(99)
		             .save();
		long retrospectiveId = retrospective.getId();
		assertTrue(retrospectiveId > 0);
		mRetrospectiveMapper.deleteRetrospective(retrospectiveId);
		retrospective = RetrospectiveObject.get(retrospectiveId);
		assertNull(retrospective);
	}
}
