package ntut.csie.ezScrum.web.dataObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import ntut.csie.ezScrum.dao.RetrospectiveDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.databasEnum.RetrospectiveEnum;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RetrospectiveObjectTest {
	private MySQLControl mControl = null;
	private Configuration mConfig = null;
	private CreateProject mCP = null;
	private CreateSprint mCS = null;
	private final int mPROJECT_COUNT = 1;
	private final int mSPRINT_COUNT = 2;
	
	@Before
	public void setUp() {
		// set ini test mode 
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		mControl = new MySQLControl(mConfig);
		mControl.connect();
		
		// create a project
		mCP = new CreateProject(mPROJECT_COUNT);
		mCP.exeCreate();
		
		// create sprint
		mCS = new CreateSprint(mSPRINT_COUNT, mCP);
		mCS.exe();
	}
	
	@After
	public void tearDown() {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		
		mConfig.setTestMode(false);
		mConfig.save();
		
		mConfig = null;
		mControl = null;
		mCP = null;
		mCS = null;
		ini = null;
		projectManager = null;
	}

	@Test
	public void testSaveCreate() {
		// test data 
		String name = "TEST_RETROSPECTIVE_NAME";
		String description = "TEST_RETROSPECTIVE_DESCRIPTION";
		long sprintId = mCS.getSprintsId().get(0);
		long projectId = mCP.getAllProjects().get(0).getId();
		int type = RetrospectiveObject.TYPE_GOOD;
		
		
		RetrospectiveObject retrospective = new RetrospectiveObject(projectId);
		retrospective.setName(name)
					 .setSprintId(sprintId)
					 .setDescription(description)
					 .setType(type)
					 .save();
		
		// Get RetrospectiveObject By DAO
		RetrospectiveObject retrospectiveCreated = RetrospectiveDAO.getInstance().get(retrospective.getId());
		
		assertEquals(name, retrospectiveCreated.getName());
		assertEquals(description, retrospectiveCreated.getDescription());
		assertEquals(type, retrospectiveCreated.getType());
		assertEquals(projectId, retrospectiveCreated.getProjectId());
		assertEquals(sprintId, retrospectiveCreated.getSprintId());
		assertEquals(RetrospectiveObject.STATUS_NEW, retrospectiveCreated.getStatus());
	}
	
	@Test
	public void testSaveUpdate() {
		// test data 
		String name = "TEST_RETROSPECTIVE_NAME";
		String description = "TEST_RETROSPECTIVE_DESCRIPTION";
		long sprintId = mCS.getSprintsId().get(0);
		long projectId = mCP.getAllProjects().get(0).getId();
		int type = RetrospectiveObject.TYPE_GOOD;
		
		String nameUpdate = "TEST_RETROSPECTIVE_NAME_Update";
		String descriptionUpdate = "TEST_RETROSPECTIVE_DESCRIPTION_Update";
		long sprintIdUpdate = mCS.getSprintsId().get(1);
		int statusUpdate = RetrospectiveObject.STATUS_ASSIGNED;
		int typeUpdate = RetrospectiveObject.TYPE_IMPROVEMENT;

		// create retrospective
		RetrospectiveObject retrospective = new RetrospectiveObject(projectId);
		retrospective.setName(name)
		             .setSprintId(sprintId)
				     .setDescription(description)
				     .setType(type)
				     .save();
		
		// assert 有沒有新增成功
		assertNotSame(-1, retrospective.getId());
		
		// Update retrospective
		retrospective.setName(nameUpdate)
					 .setSprintId(sprintIdUpdate)
					 .setDescription(descriptionUpdate)
					 .setType(typeUpdate)
					 .setStatus(statusUpdate)
					 .save();
		
		assertEquals(nameUpdate, retrospective.getName());
		assertEquals(descriptionUpdate, retrospective.getDescription());
		assertEquals(typeUpdate, retrospective.getType());
		assertEquals(sprintIdUpdate, retrospective.getSprintId());
		assertEquals(statusUpdate, retrospective.getStatus());
	}
	
	@Test
	public void testDelete() {
		// test data
		String name = "TEST_RETROSPECTIVE_NAME";
		String description = "TEST_RETROSPECTIVE_DESCRIPTION";
		long sprintId = mCS.getSprintsId().get(0);
		long projectId = mCP.getAllProjects().get(0).getId();
		int type = RetrospectiveObject.TYPE_GOOD;
		
		// create retrospective
		RetrospectiveObject retrospective = new RetrospectiveObject(projectId);
		retrospective.setName(name)
				     .setSprintId(sprintId)
					 .setDescription(description)
					 .setType(type)
					 .save();
		
		// assert 有沒有新增成功
		assertNotSame(-1, retrospective.getId());
		
		// delete
		boolean isDeleteSuccess = retrospective.delete();
		
		assertTrue(isDeleteSuccess);
		
		// Get RetrospectiveObject By DAO
		RetrospectiveObject retrospectiveCreated = RetrospectiveDAO.getInstance().get(retrospective.getId());
		
		assertNull(retrospectiveCreated);
	}
	
	@Test
	public void testToJSON() throws JSONException {
		// test data
		String name = "TEST_RETROSPECTIVE_NAME";
		String description = "TEST_RETROSPECTIVE_DESCRIPTION";
		long sprintId = mCS.getSprintsId().get(0);
		long projectId = mCP.getAllProjects().get(0).getId();
		int type = RetrospectiveObject.TYPE_GOOD;

		// create retrospective
		RetrospectiveObject retrospective = new RetrospectiveObject(projectId);
		retrospective.setName(name)
		             .setSprintId(sprintId)
		             .setDescription(description)
		             .setType(type)
		             .save();
		
		// Get JSON
		JSONObject retrospectiveJson = retrospective.toJSON();
		
		// Assert
		assertEquals(retrospective.getId(), retrospectiveJson.get(RetrospectiveEnum.ID));
		assertEquals(name, retrospectiveJson.get(RetrospectiveEnum.NAME));
		assertEquals(description, retrospectiveJson.get(RetrospectiveEnum.DESCRIPTION));
		assertEquals(sprintId, retrospectiveJson.get(RetrospectiveEnum.SPRINT_ID));
		assertEquals(type, retrospectiveJson.get(RetrospectiveEnum.TYPE));
		assertEquals(RetrospectiveObject.STATUS_NEW, retrospectiveJson.get(RetrospectiveEnum.STATUS));
		assertEquals(projectId, retrospectiveJson.get(RetrospectiveEnum.PROJECT_ID));
		
	}
	
	@Test
	public void testGetTypeString() {
		long projectId = mCP.getAllProjects().get(0).getId();
		RetrospectiveObject retrospective = new RetrospectiveObject(projectId);
		retrospective.setType(RetrospectiveObject.TYPE_GOOD);
		assertEquals("Good", retrospective.getTypeString());
		retrospective.setType(RetrospectiveObject.TYPE_IMPROVEMENT);
		assertEquals("Improvement", retrospective.getTypeString());
	}
	
	@Test
	public void testGetStatusString() {
		long projectId = mCP.getAllProjects().get(0).getId();
		RetrospectiveObject retrospective = new RetrospectiveObject(projectId);
		retrospective.setStatus(RetrospectiveObject.STATUS_NEW);
		assertEquals("new", retrospective.getStatusString());
		retrospective.setStatus(RetrospectiveObject.STATUS_ASSIGNED);
		assertEquals("assigned", retrospective.getStatusString());
		retrospective.setStatus(RetrospectiveObject.STATUS_RESOLVED);
		assertEquals("resolved", retrospective.getStatusString());
		retrospective.setStatus(RetrospectiveObject.STATUS_CLOSED);
		assertEquals("closed", retrospective.getStatusString());
	}
	
	@Test
	public void testGetTypeByTypeString() {
		assertEquals(RetrospectiveObject.TYPE_GOOD, RetrospectiveObject.getTypeByTypeString("Good"));
		assertEquals(RetrospectiveObject.TYPE_IMPROVEMENT, RetrospectiveObject.getTypeByTypeString("Improvement"));
	}
	
	@Test
	public void testGetStatusByStatusString() {
		assertEquals(RetrospectiveObject.STATUS_NEW, RetrospectiveObject.getStatusByStatusString("new"));
		assertEquals(RetrospectiveObject.STATUS_ASSIGNED, RetrospectiveObject.getStatusByStatusString("assigned"));
		assertEquals(RetrospectiveObject.STATUS_RESOLVED, RetrospectiveObject.getStatusByStatusString("resolved"));
		assertEquals(RetrospectiveObject.STATUS_CLOSED, RetrospectiveObject.getStatusByStatusString("closed"));
	}
}
