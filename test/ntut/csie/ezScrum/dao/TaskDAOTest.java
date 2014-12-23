package ntut.csie.ezScrum.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.databasEnum.TaskEnum;

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
	
	public void testCreate() throws SQLException {
		for (int i = 0; i < 3; i++) {
			TaskObject task = new TaskObject();
			task.setName("TEST_TASK_" + i+1)
				.setNotes("TEST_NOTE_" + i+1)
				.setProjectId(projectId)
				.setEstimate(i*2)
				.setRemains(i*2)
				.setActual(i*2);
			long taskId = TaskDAO.getInstance().create(task);
			assertNotSame(-1, taskId);
		}
		
		// 從 DB 裡取出 task 資料
		ArrayList<TaskObject> tasks = new ArrayList<TaskObject>();
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(TaskEnum.TABLE_NAME);
		valueSet.addEqualCondition(TaskEnum.PROJECT_ID, projectId);
		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);
		while (result.next()) {
			tasks.add(convert(result));
		}
		
		assertEquals(3, tasks.size());
		for (int i = 0; i < 3; i++) {
			assertEquals(i+1, tasks.get(i).getId());
			assertEquals(i+1, tasks.get(i).getSerialId());
			assertEquals("TEST_TASK_" + i+1, tasks.get(i).getName());
			assertEquals("TEST_NOTE_" + i+1, tasks.get(i).getNotes());
			assertEquals(projectId, tasks.get(i).getProjectId());
			assertEquals(-1, tasks.get(i).getStoryId());
			assertEquals(i*2, tasks.get(i).getEstimate());
			assertEquals(i*2, tasks.get(i).getRemains());
			assertEquals(i*2, tasks.get(i).getActual());
			assertNotNull(tasks.get(i).getCreateTime());
			assertNotNull(tasks.get(i).getUpdateTime());
		}
	}
	
//	public void testGet() throws SQLException {
//		// create three task
//		TaskObject task = new TaskObject();
//		task.setName("TEST_TASK_1")
//			.setNotes("TEST_NOTE_1")
//			.setProjectId(projectId)
//			.setStoryId(1)
//			.setEstimate(13)
//			.setRemains(8)
//			.setActual(5);
//		long taskId = TaskDAO.getInstance().create(task);
//		
//		// get task
//		TaskObject theTask = TaskDAO.getInstance().get(taskId);
//		assertEquals(task.getName(), theTask.getName());
//	}
	
	private TaskObject convert(ResultSet result) throws SQLException {
		TaskObject task = new TaskObject(result.getLong(TaskEnum.ID), result.getLong(TaskEnum.SERIAL_ID));
		task.setName(result.getString(TaskEnum.NAME))
			.setHandlerId(result.getLong(TaskEnum.HANDLER_ID))
			.setEstimate(result.getInt(TaskEnum.ESTIMATE))
			.setRemains(result.getInt(TaskEnum.REMAIN))
			.setActual(result.getInt(TaskEnum.ACTUAL))
			.setNotes(result.getString(TaskEnum.NOTES))
			.setProjectId(result.getLong(TaskEnum.PROJECT_ID))
			.setStoryId(result.getLong(TaskEnum.STORY_ID))
			.setCreateTime(result.getLong(TaskEnum.CREATE_TIME))
			.setUpdateTime(result.getLong(TaskEnum.UPDATE_TIME));
		return task;
	}
}
