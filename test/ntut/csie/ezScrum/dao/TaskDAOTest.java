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

		projectId = mCreateProject.getProjects().get(0).getId();

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
		// create three test data
		for (int i = 0; i < 3; i++) {
			TaskObject task = new TaskObject(projectId);
			task.setName("TEST_TASK_" + i + 1)
			        .setNotes("TEST_NOTE_" + i + 1)
			        .setEstimate(i * 2)
			        .setRemains(i * 2)
			        .setActual(i * 2);
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
			assertEquals(i + 1, tasks.get(i).getId());
			assertEquals(i + 1, tasks.get(i).getSerialId());
			assertEquals("TEST_TASK_" + i + 1, tasks.get(i).getName());
			assertEquals("TEST_NOTE_" + i + 1, tasks.get(i).getNotes());
			assertEquals(projectId, tasks.get(i).getProjectId());
			assertEquals(-1, tasks.get(i).getStoryId());
			assertEquals(i * 2, tasks.get(i).getEstimate());
			assertEquals(i * 2, tasks.get(i).getRemains());
			assertEquals(i * 2, tasks.get(i).getActual());
			assertNotNull(tasks.get(i).getCreateTime());
			assertNotNull(tasks.get(i).getUpdateTime());
		}
	}

	public void testGet() throws SQLException {
		// create three test data
		for (int i = 0; i < 3; i++) {
			TaskObject task = new TaskObject(projectId);
			task.setName("TEST_TASK_" + i + 1)
			        .setNotes("TEST_NOTE_" + i + 1)
			        .setEstimate(i * 2)
			        .setRemains(i * 2)
			        .setActual(i * 2);
			long taskId = TaskDAO.getInstance().create(task);
			assertNotSame(-1, taskId);
		}

		// get task
		ArrayList<TaskObject> tasks = new ArrayList<TaskObject>();
		for (int i = 0; i < 3; i++) {
			tasks.add(TaskDAO.getInstance().get(i + 1));
		}
		assertEquals(3, tasks.size());

		for (int i = 0; i < 3; i++) {
			assertEquals(i + 1, tasks.get(i).getId());
			assertEquals(i + 1, tasks.get(i).getSerialId());
			assertEquals("TEST_TASK_" + i + 1, tasks.get(i).getName());
			assertEquals("TEST_NOTE_" + i + 1, tasks.get(i).getNotes());
			assertEquals(projectId, tasks.get(i).getProjectId());
			assertEquals(-1, tasks.get(i).getStoryId());
			assertEquals(i * 2, tasks.get(i).getEstimate());
			assertEquals(i * 2, tasks.get(i).getRemains());
			assertEquals(i * 2, tasks.get(i).getActual());
			assertNotNull(tasks.get(i).getCreateTime());
			assertNotNull(tasks.get(i).getUpdateTime());
		}
	}

	public void testUpdate() throws SQLException {
		TaskObject task = new TaskObject(projectId);
		task.setName("TEST_TASK_1")
		        .setNotes("TEST_NOTE_1")
		        .setEstimate(1)
		        .setRemains(2)
		        .setActual(3);
		long taskId = TaskDAO.getInstance().create(task);
		assertNotSame(-1, taskId);

		task = TaskDAO.getInstance().get(taskId);
		task.setName("崩潰惹")
		        .setNotes("含淚寫測試")
		        .setEstimate(8)
		        .setRemains(8)
		        .setActual(8);
		boolean result = TaskDAO.getInstance().update(task);
		assertEquals(true, result);

		TaskObject theTask = TaskDAO.getInstance().get(taskId);
		assertEquals(theTask.getId(), task.getId());
		assertEquals(theTask.getSerialId(), task.getSerialId());
		assertEquals(theTask.getName(), task.getName());
		assertEquals(theTask.getNotes(), task.getNotes());
		assertEquals(theTask.getProjectId(), task.getProjectId());
		assertEquals(theTask.getStoryId(), task.getStoryId());
		assertEquals(theTask.getEstimate(), task.getEstimate());
		assertEquals(theTask.getRemains(), task.getRemains());
		assertEquals(theTask.getActual(), task.getActual());
	}

	public void testDelete() throws SQLException {
		// create three test data
		for (int i = 0; i < 3; i++) {
			TaskObject task = new TaskObject(projectId);
			task.setName("TEST_TASK_" + i + 1)
		        .setNotes("TEST_NOTE_" + i + 1)
		        .setEstimate(i * 2)
		        .setRemains(i * 2)
		        .setActual(i * 2);
			long taskId = TaskDAO.getInstance().create(task);
			assertNotSame(-1, taskId);
		}

		// get tasks
		ArrayList<TaskObject> tasks = new ArrayList<TaskObject>();
		for (int i = 0; i < 3; i++) {
			tasks.add(TaskDAO.getInstance().get(i + 1));
		}
		assertEquals(3, tasks.size());

		// delete task #2
		boolean result = TaskDAO.getInstance().delete(tasks.get(1).getId());
		assertEquals(true, result);

		// reload tasks
		tasks.clear();
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(TaskEnum.TABLE_NAME);
		valueSet.addEqualCondition(TaskEnum.PROJECT_ID, projectId);
		String query = valueSet.getSelectQuery();
		ResultSet resultSet = mControl.executeQuery(query);
		while (resultSet.next()) {
			tasks.add(convert(resultSet));
		}
		assertEquals(2, tasks.size());
	}

	public void testGetTasksByStory() throws SQLException {
		// create three test data
		for (int i = 0; i < 3; i++) {
			TaskObject task = new TaskObject(projectId);
			task.setName("TEST_TASK_" + i + 1)
		        .setNotes("TEST_NOTE_" + i + 1)
		        .setEstimate(i * 2)
		        .setRemains(i * 2)
		        .setActual(i * 2);
			if (i == 1) {
				task.setStoryId(2);
			} else {
				task.setStoryId(1);
			}
			long taskId = TaskDAO.getInstance().create(task);
			assertNotSame(-1, taskId);
		}
		
		// get tasks by story
		ArrayList<TaskObject> tasks = TaskDAO.getInstance().getTasksByStory(1);
		assertEquals(2, tasks.size());
	}
	
	public void testGetWildTasks() throws SQLException {
		// create three test data
		for (int i = 0; i < 3; i++) {
			TaskObject task = new TaskObject(projectId);
			task.setName("TEST_TASK_" + i + 1)
		        .setNotes("TEST_NOTE_" + i + 1)
		        .setEstimate(i * 2)
		        .setRemains(i * 2)
		        .setActual(i * 2);
			if (i == 1) {
				task.setStoryId(2);
			}
			long taskId = TaskDAO.getInstance().create(task);
			assertNotSame(-1, taskId);
		}
		
		// get Wild Tasks
		ArrayList<TaskObject> tasks = TaskDAO.getInstance().getWildTasks(projectId);
		assertEquals(2, tasks.size());
	}

	private TaskObject convert(ResultSet result) throws SQLException {
		TaskObject task = new TaskObject(
		        result.getLong(TaskEnum.ID),
		        result.getLong(TaskEnum.SERIAL_ID),
		        result.getLong(TaskEnum.PROJECT_ID));
		task.setName(result.getString(TaskEnum.NAME))
		        .setHandlerId(result.getLong(TaskEnum.HANDLER_ID))
		        .setEstimate(result.getInt(TaskEnum.ESTIMATE))
		        .setRemains(result.getInt(TaskEnum.REMAIN))
		        .setActual(result.getInt(TaskEnum.ACTUAL))
		        .setNotes(result.getString(TaskEnum.NOTES))
		        .setStoryId(result.getLong(TaskEnum.STORY_ID))
		        .setCreateTime(result.getLong(TaskEnum.CREATE_TIME))
		        .setUpdateTime(result.getLong(TaskEnum.UPDATE_TIME));
		return task;
	}
}
