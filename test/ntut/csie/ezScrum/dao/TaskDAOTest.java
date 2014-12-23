package ntut.csie.ezScrum.dao;

import java.sql.SQLException;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.TaskObject;

public class TaskDAOTest extends TestCase {
	private MySQLControl mControl = null;
	private Configuration mConfig;
	private CreateProject mCreateProject;
	private int mProjectCount = 2;
	private long projectId;

	public TaskDAOTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.store();

		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		mCreateProject = new CreateProject(mProjectCount);
		mCreateProject.exeCreate();

		mControl = new MySQLControl(mConfig);
		mControl.connection();
		
		projectId = Long.parseLong(mCreateProject.getProjectObjectList().get(0).getId());
		
		super.setUp();
	}

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
		mCreateProject = null;
		mConfig = null;
		mControl = null;

		super.tearDown();
	}
	
	public void testCreate() {
		TaskObject task = new TaskObject();
		task.setName("TEST_TASK_1")
			.setNotes("TEST_NOTE_1")
			.setProjectId(projectId)
			.setStoryId(1)
			.setEstimate(13)
			.setRemains(8)
			.setActual(5);
		long taskId = TaskDAO.getInstance().create(task);
		assertNotSame(-1, taskId);
	}
	
	public void testGet() throws SQLException {
		// create three task
		TaskObject task = new TaskObject();
		task.setName("TEST_TASK_1")
			.setNotes("TEST_NOTE_1")
			.setProjectId(projectId)
			.setStoryId(1)
			.setEstimate(13)
			.setRemains(8)
			.setActual(5);
		long taskId = TaskDAO.getInstance().create(task);
		
		// get task
		TaskObject theTask = TaskDAO.getInstance().get(taskId);
		assertEquals(task.getName(), theTask.getName());
	}
}
