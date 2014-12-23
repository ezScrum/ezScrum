package ntut.csie.ezScrum.dao;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.SerialNumberObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;

public class TaskDAOTest extends TestCase {
	private MySQLControl mControl = null;
	private Configuration mConfig;
	private CreateProject mCreateProject;
	private int mProjectCount = 2;
	private TaskDAO mTaskDao = null;
	private SerialNumberObject mSerialNumber;
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

		mTaskDao = TaskDAO.getInstance();
		mControl = new MySQLControl(mConfig);
		mControl.connection();
		
		projectId = Long.parseLong(mCreateProject.getProjectObjectList().get(0).getId());
		mSerialNumber = SerialNumberDAO.getInstance().get(projectId);
		
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
		mTaskDao = null;
		mSerialNumber = null;
		mControl = null;

		super.tearDown();
	}
	
	public void testAdd() {
		TaskObject task = new TaskObject();
		task.setName("TEST_TASK_1")
			.setNotes("TEST_NOTE_1")
			.setProjectId(projectId)
			.setStoryId(1)
			.setEstimate(13)
			.setRemains(8)
			.setActual(5);
		long taskId = mTaskDao.create(task);
		assertNotSame(-1, taskId);
	}
}
