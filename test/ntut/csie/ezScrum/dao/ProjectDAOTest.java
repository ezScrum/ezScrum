package ntut.csie.ezScrum.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.databasEnum.ProjectEnum;
import ntut.csie.ezScrum.web.databasEnum.RoleEnum;
import ntut.csie.ezScrum.web.databasEnum.ScrumRoleEnum;

public class ProjectDAOTest extends TestCase {
	private MySQLControl mControl = null;
	private Configuration mConfig;

	public ProjectDAOTest(String testMethod) {
		super(testMethod);
	}

	@Before
	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		mControl = new MySQLControl(mConfig);
		mControl.connection();

		super.setUp();
	}

	@After
	protected void tearDown() throws Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(mConfig.getDataPath());

		// ============= release ==============
		ini = null;
		mConfig = null;
		mControl = null;

		super.tearDown();
	}

	@Test
	public void testCreate() {
		// create three test data
		for (int i = 0; i < 3; i++) {
			ProjectObject project = new ProjectObject("TEST_PROJECT_" + i + 1);
			project
			        .setDisplayName("TEST_DISPLATNAME_" + i + 1)
			        .setComment("TEST_COMMON_" + i + 1)
			        .setManager("TEST_MANAGER")
			        .setAttachFileSize(2);
			long projectId = ProjectDAO.getInstance().create(project);
			assertNotSame(-1, projectId);
		}

		// 從 DB 裡取出 project 資料
		ArrayList<ProjectObject> projects = new ArrayList<ProjectObject>();
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(ProjectEnum.TABLE_NAME);
		String query = valueSet.getSelectQuery();
		
		ResultSet result = mControl.executeQuery(query);
		try {
	        while (result.next()) {
	        	projects.add(convertProject(result));
	        }
        } catch (SQLException e) {
	        e.printStackTrace();
        } finally {
        	closeResultSet(result);
        }

		assertEquals(3, projects.size());
		for (int i = 0; i < 3; i++) {
			assertEquals(i + 1, projects.get(i).getId());
			assertEquals("TEST_PROJECT_" + i + 1, projects.get(i).getName());
			assertEquals("TEST_DISPLATNAME_" + i + 1, projects.get(i).getDisplayName());
			assertEquals("TEST_COMMON_" + i + 1, projects.get(i).getComment());
			assertEquals("TEST_MANAGER", projects.get(i).getManager());
			assertEquals(2, projects.get(i).getAttachFileSize());
			assertNotNull(projects.get(i).getCreateTime());
			assertNotNull(projects.get(i).getUpdateTime());
		}
	}
	
	@Test
	public void testCreateWithScrumRole() {
		// create test data
		ProjectObject project = new ProjectObject("TEST_PROJECT_1");
		project
		        .setDisplayName("TEST_DISPLATNAME_1")
		        .setComment("TEST_COMMON_1")
		        .setManager("TEST_MANAGER")
		        .setAttachFileSize(2);
		long projectId = ProjectDAO.getInstance().create(project);
		assertNotSame(-1, projectId);

		project = ProjectDAO.getInstance().get(projectId);
		// 從 DB 裡取出 project 的 Scrum role 資料
		MySQLQuerySet valueSet = new MySQLQuerySet();
		valueSet.addTableName(ScrumRoleEnum.TABLE_NAME);
		valueSet.addEqualCondition(ScrumRoleEnum.PROJECT_ID, projectId);
		String query = valueSet.getSelectQuery();
		
		ResultSet result = mControl.executeQuery(query);
		try {
			if (result.first()) {
				return convertScrumRole(project.getName(), role.name(), result);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
	}

	@Test
	public void testGet() throws SQLException {
		// create three test data
		for (int i = 0; i < 3; i++) {
			ProjectObject project = new ProjectObject("TEST_PROJECT_" + i + 1);
			project
			        .setDisplayName("TEST_DISPLATNAME_" + i + 1)
			        .setComment("TEST_COMMON_" + i + 1)
			        .setManager("TEST_MANAGER")
			        .setAttachFileSize(2);
			long projectId = ProjectDAO.getInstance().create(project);
			assertNotSame(-1, projectId);
		}

		// get project
		ArrayList<ProjectObject> projects = new ArrayList<ProjectObject>();
		for (int i = 0; i < 3; i++) {
			projects.add(ProjectDAO.getInstance().get(i + 1));
		}
		assertEquals(3, projects.size());

		for (int i = 0; i < 3; i++) {
			assertEquals(i + 1, projects.get(i).getId());
			assertEquals("TEST_PROJECT_" + i + 1, projects.get(i).getName());
			assertEquals("TEST_DISPLATNAME_" + i + 1, projects.get(i).getDisplayName());
			assertEquals("TEST_COMMON_" + i + 1, projects.get(i).getComment());
			assertEquals("TEST_MANAGER", projects.get(i).getManager());
			assertEquals(2, projects.get(i).getAttachFileSize());
			assertNotNull(projects.get(i).getCreateTime());
			assertNotNull(projects.get(i).getUpdateTime());
		}
	}

	@Test
	public void testUpdate() throws SQLException {
		// create test data
		ProjectObject project = new ProjectObject("TEST_PROJECT_1");
		project
		        .setDisplayName("TEST_DISPLATNAME_1")
		        .setComment("TEST_COMMON_1")
		        .setManager("TEST_MANAGER")
		        .setAttachFileSize(2);
		long projectId = ProjectDAO.getInstance().create(project);
		assertNotSame(-1, projectId);

		project = ProjectDAO.getInstance().get(projectId);
		project
		        .setDisplayName("含淚寫測試")
		        .setComment("崩潰底霸格")
		        .setManager("QAQ")
		        .setAttachFileSize(3);
		boolean result = ProjectDAO.getInstance().update(project);
		assertEquals(true, result);

		ProjectObject theProject = ProjectDAO.getInstance().get(projectId);
		assertEquals(theProject.getId(), project.getId());
		assertEquals(theProject.getName(), project.getName());
		assertEquals(theProject.getDisplayName(), project.getDisplayName());
		assertEquals(theProject.getComment(), project.getComment());
		assertEquals(theProject.getManager(), project.getManager());
		assertEquals(3, project.getAttachFileSize());
		assertEquals(theProject.getCreateTime(), project.getCreateTime());
		assertNotNull(theProject.getUpdateTime());
	}

	@Test
	public void testDelete() {
		// create three test data
		for (int i = 0; i < 3; i++) {
			ProjectObject project = new ProjectObject("TEST_PROJECT_" + i + 1);
			project
			        .setDisplayName("TEST_DISPLATNAME_" + i + 1)
			        .setComment("TEST_COMMON_" + i + 1)
			        .setManager("TEST_MANAGER")
			        .setAttachFileSize(2);
			long projectId = ProjectDAO.getInstance().create(project);
			assertNotSame(-1, projectId);
		}

		// get project
		ArrayList<ProjectObject> projects = new ArrayList<ProjectObject>();
		for (int i = 0; i < 3; i++) {
			projects.add(ProjectDAO.getInstance().get(i + 1));
		}
		assertEquals(3, projects.size());

		// delete project #2
		boolean success = ProjectDAO.getInstance().delete(projects.get(1).getId());
		assertEquals(true, success);

		// reload projects
		projects.clear();
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(ProjectEnum.TABLE_NAME);
		String query = valueSet.getSelectQuery();
		
		ResultSet result = mControl.executeQuery(query);
		try {
	        while (result.next()) {
	        	projects.add(convertProject(result));
	        }
        } catch (SQLException e) {
	        e.printStackTrace();
        } finally {
        	closeResultSet(result);
        }
		assertEquals(2, projects.size());
	}

	@Test
	public void testGetProjectByName() {
		// create three test data
		for (int i = 0; i < 3; i++) {
			ProjectObject project = new ProjectObject("TEST_PROJECT_" + (i+1));
			project
			        .setDisplayName("TEST_DISPLATNAME_" + (i+1))
			        .setComment("TEST_COMMON_" + (i+1))
			        .setManager("TEST_MANAGER")
			        .setAttachFileSize(2);
			long projectId = ProjectDAO.getInstance().create(project);
			assertNotSame(-1, projectId);
		}

		// get project by name
		ProjectObject theProject = ProjectDAO.getInstance().getProjectByName("TEST_PROJECT_1");

		assertEquals(1, theProject.getId());
		assertEquals("TEST_DISPLATNAME_1", theProject.getDisplayName());
		assertEquals("TEST_COMMON_1", theProject.getComment());
	}

	@Test
	public void testGetProjects() {
		// create three test data
		for (int i = 0; i < 3; i++) {
			ProjectObject project = new ProjectObject("TEST_PROJECT_" + i + 1);
			project
			        .setDisplayName("TEST_DISPLATNAME_" + i + 1)
			        .setComment("TEST_COMMON_" + i + 1)
			        .setManager("TEST_MANAGER")
			        .setAttachFileSize(2);
			long projectId = ProjectDAO.getInstance().create(project);
			assertNotSame(-1, projectId);
		}
		
		ArrayList<ProjectObject> projects = ProjectDAO.getInstance().getProjects();
		
		assertEquals(3, projects.size());
	}

	private ProjectObject convertProject(ResultSet result) {
		ProjectObject project = null;
        try {
	        project = new ProjectObject(result.getLong(ProjectEnum.ID),
	        		result.getString(ProjectEnum.NAME));
	        project
	        .setDisplayName(result.getString(ProjectEnum.DISPLAY_NAME))
	        .setComment(result.getString(ProjectEnum.COMMENT))
	        .setManager(result.getString(ProjectEnum.PRODUCT_OWNER))
	        .setAttachFileSize(result.getLong(ProjectEnum.ATTATCH_MAX_SIZE))
	        .setCreateTime(result.getLong(ProjectEnum.CREATE_TIME))
	        .setUpdateTime(result.getLong(ProjectEnum.UPDATE_TIME));
        } catch (SQLException e) {
	        e.printStackTrace();
        } finally {
        	closeResultSet(result);
        }
		return project;
	}
	
	private ScrumRole convertScrumRole(String projectName, String role, ResultSet result) {
		ScrumRole scrumRole = new ScrumRole(projectName, role);
		scrumRole.setisGuest(RoleEnum.Guest == RoleEnum.valueOf(role));
		try {
	        scrumRole.setAccessProductBacklog(result.getBoolean(ScrumRoleEnum.ACCESS_PRODUCT_BACKLOG));
	        scrumRole.setAccessReleasePlan(result.getBoolean(ScrumRoleEnum.ACCESS_RELEASE_PLAN));
	        scrumRole.setReadReport(result.getBoolean(ScrumRoleEnum.ACCESS_REPORT));
	        scrumRole.setAccessRetrospective(result.getBoolean(ScrumRoleEnum.ACCESS_RETROSPECTIVE));
	        scrumRole.setAccessSprintBacklog(result.getBoolean(ScrumRoleEnum.ACCESS_SPRINT_BACKLOG));
	        scrumRole.setAccessSprintPlan(result.getBoolean(ScrumRoleEnum.ACCESS_SPRINT_PLAN));
	        scrumRole.setAccessUnplannedItem(result.getBoolean(ScrumRoleEnum.ACCESS_UNPLANNED));
	        scrumRole.setAccessTaskBoard(result.getBoolean(ScrumRoleEnum.ACCESS_TASKBOARD));
	        scrumRole.setEditProject(result.getBoolean(ScrumRoleEnum.ACCESS_EDIT_PROJECT));
        } catch (SQLException e) {
	        e.printStackTrace();
        } finally {
        	closeResultSet(result);
        }
		return scrumRole;
	}
	
	private void closeResultSet(ResultSet result) {
		if (result != null) {
			try {
				result.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
