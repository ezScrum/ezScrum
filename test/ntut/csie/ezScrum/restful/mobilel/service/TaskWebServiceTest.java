package ntut.csie.ezScrum.restful.mobilel.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import ntut.csie.ezScrum.dao.TaskDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.restful.mobile.service.TaskWebService;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.databasEnum.TaskEnum;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.google.gson.Gson;

public class TaskWebServiceTest {
	private int mProjectCount = 1;
	private int mSprintCount = 1;
	private int mStoryCount = 5;
	private TaskWebService mTaskWebService;
	private CreateProject mCP;
	private CreateSprint mCS;
	private AddStoryToSprint mASTS;
	private MySQLControl mControl;
	private Configuration mConfiguration;
	
	@Before
	public void setUp() throws Exception{
		mConfiguration = new Configuration();
		mConfiguration.setTestMode(true);
		mConfiguration.save();
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfiguration);
		ini.exe(); 

		// 新增Project
		mCP = new CreateProject(mProjectCount);
		mCP.exeCreate();

		// 新增Sprint
		mCS = new CreateSprint(mSprintCount, mCP);
		mCS.exe();

		// 新增Story
		mASTS = new AddStoryToSprint(mStoryCount, 1, mCS, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		mASTS.exe();
		
		mControl = new MySQLControl(mConfiguration);
		mControl.connect();
		
		// create account
		String TEST_ACCOUNT_NAME = "TEST_ACCOUNT_1";
		String TEST_ACCOUNT_NICKNAME = "TEST_ACCOUNT_NICKNAME_1";
		String TEST_ACCOUNT_PASSWORD = "TEST_ACCOUNT_PASSWORD_1";
		String TEST_ACCOUNT_EMAIL = "TEST_ACCOUNT_EMAIL_1";
		
		AccountObject account = new AccountObject(TEST_ACCOUNT_NAME);
		account.setNickName(TEST_ACCOUNT_NICKNAME);
		account.setPassword(TEST_ACCOUNT_PASSWORD);
		account.setEmail(TEST_ACCOUNT_EMAIL);
		account.setEnable(true);
		account.save();
		account.reload();
		
		// Set up TaskWebService
		mTaskWebService = new TaskWebService(TEST_ACCOUNT_NAME, TEST_ACCOUNT_PASSWORD, String.valueOf(mCP.getAllProjects().get(0).getId()));
	}
	
	@After
	public void tearDown(){
		InitialSQL ini = new InitialSQL(mConfiguration);
		ini.exe(); // 初始化 SQL

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		mConfiguration.setTestMode(false);
		mConfiguration.save();
		
		// release
		mTaskWebService = null;
		mCP = null;
		mCS = null;
		mASTS = null;
		mControl = null;
		mConfiguration = null;
	}
	
	@Test
	public void testGetTasksWithNoParent() throws SQLException, JSONException{
		String TEST_TASK_NAME = "TEST_TASK_1";
		String TEST_TASK_NOTE = "TEST_TASK_NOTE_1";
		TaskObject task = new TaskObject(mCP.getAllProjects().get(0).getId());
		task.setName(TEST_TASK_NAME);
		task.setEstimate(8);
		task.setNotes(TEST_TASK_NOTE);
		task.setHandlerId(2);
		task.setCreateTime(System.currentTimeMillis());
		task.save();
		task.reload();
		
		String actualTaskString = mTaskWebService.getTasksWithNoParent();
		Gson gson = new Gson();
		
		ArrayList<TaskObject> existedTask = new ArrayList<TaskObject>();
		existedTask.add(task);
		
		String expectedTaskJSON = gson.toJson(existedTask);
		Assert.assertEquals(actualTaskString, expectedTaskJSON);
	}
	
	@Test
	public void testCreateTaskInStory() throws JSONException, SQLException{
		long TEST_TASK_ID = 1L;
		String TEST_TASK_NAME = "TEST_TASK_1";
		String TEST_TASK_NOTES = "TEST_TASK_NOTES";
		int TEST_TASK_ESTIMATE = 8;
		int TEST_TASK_REMAIN = 8;
		int TEST_TASK_ACTUAL = 8;
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", TEST_TASK_ID);
		jsonObject.put("name", TEST_TASK_NAME);
		jsonObject.put("estimate", TEST_TASK_ESTIMATE);
		jsonObject.put("remains", TEST_TASK_REMAIN);
		jsonObject.put("notes", TEST_TASK_NOTES);
		jsonObject.put("actual", TEST_TASK_ACTUAL);
		
		long newTaskId = Long.valueOf(mTaskWebService.createTaskInStory(1L, jsonObject.toString()));
		Assert.assertTrue(newTaskId > 0);
		
		// Query
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(TaskEnum.TABLE_NAME);
		valueSet.addEqualCondition(TaskEnum.ID, newTaskId);
		String query = valueSet.getSelectQuery();
		ResultSet resultSet = mControl.executeQuery(query);
		Assert.assertTrue(resultSet.first());
		// assert
		TaskObject taskObject = TaskDAO.convert(resultSet);
		Assert.assertEquals(TEST_TASK_ID, taskObject.getId());
		Assert.assertEquals(TEST_TASK_NAME, taskObject.getName());
		Assert.assertEquals(TEST_TASK_NOTES, taskObject.getNotes());
		Assert.assertEquals(TEST_TASK_ESTIMATE, taskObject.getEstimate());
		Assert.assertEquals(TEST_TASK_REMAIN, taskObject.getRemains());
	}
	
	@Test
	public void testUpdateTask() throws JSONException, SQLException{
		String TEST_TASK_NAME = "TEST_TASK_1";
		String TEST_TASK_NOTE = "TEST_TASK_NOTE_1";
		TaskObject task = new TaskObject(mCP.getAllProjects().get(0).getId());
		task.setName(TEST_TASK_NAME);
		task.setEstimate(8);
		task.setNotes(TEST_TASK_NOTE);
		task.setHandlerId(1);
		task.setCreateTime(System.currentTimeMillis());
		task.setStoryId(1);
		task.save();
		task.reload();
		
		// new task data
		String TEST_TASK_NAME_NEW = "TEST_TASK_1_NEW";
		String TEST_TASK_NOTE_NEW = "TEST_TASK_NOTE_1_NEW";
		String TEST_ACCOUNT_NAME = "TEST_ACCOUNT_1";
		long TEST_TASK_ESTIMATE_NEW = 18;
		long TEST_TASK_REMAIN_NEW = 5;
		long TEST_TASK_ACTUAL_NEW = 5;

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", task.getId());
		jsonObject.put("name", TEST_TASK_NAME_NEW);
		jsonObject.put("estimate", TEST_TASK_ESTIMATE_NEW);
		jsonObject.put("remains", TEST_TASK_REMAIN_NEW);
		jsonObject.put("notes", TEST_TASK_NOTE_NEW);
		jsonObject.put("actual", TEST_TASK_ACTUAL_NEW);
		jsonObject.put("handler", TEST_ACCOUNT_NAME);
		jsonObject.put("partner", "");
		boolean updateStatus = Boolean.valueOf(mTaskWebService.updateTask(jsonObject.toString()));
		
		// assert
		Assert.assertTrue(updateStatus);
		// Query
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(TaskEnum.TABLE_NAME);
		valueSet.addEqualCondition(TaskEnum.ID, task.getId());
		String query = valueSet.getSelectQuery();
		ResultSet resultSet = mControl.executeQuery(query);
		Assert.assertTrue(resultSet.first());
		
		// assert
		TaskObject taskObject = TaskDAO.convert(resultSet);
		Assert.assertEquals(TEST_TASK_NAME_NEW, taskObject.getName());
		Assert.assertEquals(TEST_TASK_NOTE_NEW, taskObject.getNotes());
		Assert.assertEquals(TEST_TASK_ESTIMATE_NEW, taskObject.getEstimate());
		Assert.assertEquals(TEST_TASK_REMAIN_NEW, taskObject.getRemains());
		Assert.assertEquals(TEST_ACCOUNT_NAME, taskObject.getHandler().getUsername());
	}
}
