package ntut.csie.ezScrum.restful.mobile.support;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConvertSprintBacklogTest {
	private CreateProject mCP;
	private CreateSprint mCS;
	private Configuration mConfig = null;
	private ArrayList<SprintObject> mSprints = null;
	
	private int mProjectCount = 1;
	private int mSprintCount = 3;

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
		
		// 新增 Sprint
		mCS = new CreateSprint(mSprintCount, mCP);
		mCS.exe();
		
		mSprints = mCS.getSprints();
		
		// ============= release ==============
		ini = null;
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
		projectManager = null;
		mConfig = null;
		mSprints = null;
	}
	
	@Test
	public void testGetSprintJSONString() throws Exception {
		AddStoryToSprint ASTS = new AddStoryToSprint(3, 3, mCS, mCP, "EST");
		ASTS.exe();
		
		SprintObject sprint = mSprints.get(0);
		String response = ConvertSprintBacklog.getSprintJSONString(sprint);
		
		// assert sprint data
		JSONObject responseJson = new JSONObject(response);
		assertEquals(sprint.getId(), responseJson.getLong("id"));
		assertEquals(sprint.getGoal(), responseJson.getString("sprintGoal"));
		assertEquals(sprint.getStartDateString(), responseJson.getString("startDate"));
		assertEquals(sprint.getDemoDateString(), responseJson.getString("demoDate"));
		assertEquals(sprint.getDueDateString(), responseJson.getString("dueDate"));
		assertEquals(sprint.getInterval(), responseJson.getInt("interval"));
		assertEquals(sprint.getTeamSize(), responseJson.getInt("members"));
		assertEquals(sprint.getAvailableHours(), responseJson.getInt("hoursCanCommit"));
		assertEquals(sprint.getDailyInfo(), responseJson.getString("dailyMeeting"));
		assertEquals(sprint.getDemoPlace(), responseJson.getString("demoPlace"));
		
		// assert story data in sprint
		JSONArray storiesArray = responseJson.getJSONArray("stories");
		assertEquals(3, storiesArray.length());
	}
	
	@Test
	public void testGetSprintsJSONString() throws Exception {
		long currentSprintId = 1;
		String response = ConvertSprintBacklog.getSprintsJSONString(mSprints, currentSprintId);
		
		// assert data
		JSONObject responseJson = new JSONObject(response);
		assertEquals(currentSprintId, responseJson.getLong("currentSprintId"));
		
		// assert sprints data
		JSONArray sprintsJsonArray = responseJson.getJSONArray("sprints");
		for (int i = 0; i < sprintsJsonArray.length(); i++) {
			JSONObject sprintJson = sprintsJsonArray.getJSONObject(i);
			SprintObject sprint = mSprints.get(i);
			
			assertEquals(sprint.getId(), sprintJson.getLong("id"));
			assertEquals(sprint.getGoal(), sprintJson.getString("sprintGoal"));
			assertEquals(sprint.getStartDateString(), sprintJson.getString("startDate"));
			assertEquals(sprint.getDemoDateString(), sprintJson.getString("demoDate"));
			assertEquals(sprint.getDueDateString(), sprintJson.getString("dueDate"));
			assertEquals(sprint.getInterval(), sprintJson.getInt("interval"));
			assertEquals(sprint.getTeamSize(), sprintJson.getInt("members"));
			assertEquals(sprint.getAvailableHours(), sprintJson.getInt("hoursCanCommit"));
			assertEquals(sprint.getDailyInfo(), sprintJson.getString("dailyMeeting"));
			assertEquals(sprint.getDemoPlace(), sprintJson.getString("demoPlace"));
		}
	}
	
	@Test
	public void testGetStoriesIdJsonStringInSprint() throws Exception {
		// create stories in sprint
		AddStoryToSprint ASTS = new AddStoryToSprint(2, 5, mCS, mCP, "EST");
		ASTS.exe();
		ArrayList<StoryObject> stories = mCS.getSprints().get(0).getStories();

		// no sprint id case to get current sprint's data
		String response = ConvertSprintBacklog.getStoriesIdJsonStringInSprint(stories);
		
		// assert data
		JSONObject responseJson = new JSONObject(response);
		JSONArray storiesIdJsonArray = responseJson.getJSONArray("stories");
		assertEquals(2, storiesIdJsonArray.length());
		for (int i = 0; i < storiesIdJsonArray.length(); i++) {
			JSONObject storyJson = storiesIdJsonArray.getJSONObject(i);
			StoryObject story = stories.get(i);
			
			assertEquals(story.getId(), storyJson.getLong("id"));
			assertEquals(story.getEstimate(), storyJson.getInt("point"));
			assertEquals(story.getStatusString(), storyJson.getString("status"));
		}
	}
	
	@Test
	public void testGetTasksIdJsonStringInStory() throws Exception {
		AddStoryToSprint ASTS = new AddStoryToSprint(1, 5, mCS, mCP, "EST");
		ASTS.exe();
		AddTaskToStory ATTS = new AddTaskToStory(5, 5, ASTS, mCP);
		ATTS.exe();
		StoryObject story = ASTS.getStories().get(0);
		ArrayList<TaskObject> tasks = story.getTasks();
		String response = ConvertSprintBacklog.getTasksIdJsonStringInStory(story.getId(), tasks);
		
		// assert data
		JSONObject responseJson = new JSONObject(response);
		JSONObject storyJson = responseJson.getJSONObject("story");
		assertEquals(story.getId(), storyJson.getLong("id"));
		JSONArray tasksIdJsonArray = storyJson.getJSONArray("tasksId");
		for (int i = 0; i < tasksIdJsonArray.length(); i++) {
			long taskId = tasksIdJsonArray.getLong(i);
			assertEquals(tasks.get(i).getId(), taskId);
		}
	}
	
	@Test
	public void testGetTaskHistoriesJsonString() throws Exception {
		TaskObject task = new TaskObject(mCP.getAllProjects().get(0).getId());
		task.setName("TEST_TASK").save();
		ArrayList<HistoryObject> taskHistories = task.getHistories();
		
		// assert data
		String response = ConvertSprintBacklog.getTaskHistoriesJsonString(task.getHistories());
		JSONObject responseJson = new JSONObject(response);
		JSONArray historiesJsonArray = responseJson.getJSONArray("taskHistories");
		assertEquals(taskHistories.size(), historiesJsonArray.length());
		for (int i = 0; i < historiesJsonArray.length(); i++) {
			JSONObject historyJson = historiesJsonArray.getJSONObject(i);
			HistoryObject history = taskHistories.get(i);
			
			assertEquals(parseDate(history.getCreateTime()), historyJson.getString("modifyDate"));
			assertEquals(history.getHistoryType(), historyJson.getInt("historyType"));
			assertEquals(history.getDescription(), historyJson.getString("description"));
		}
	}
	
	@Test
	public void testGetTaskJsonString() throws JSONException {
		TaskObject task = new TaskObject(mCP.getAllProjects().get(0).getId());
		task.setName("TEST_TASK").setEstimate(3).setActual(5)
			.setRemains(8).setNotes("TEST_NOTE").save();
		
		// assert data
		String response = ConvertSprintBacklog.getTaskJsonString(task);
		JSONObject responseJson = new JSONObject(response);
		assertEquals(task.getId(), responseJson.getLong("id"));
		assertEquals(task.getName(), responseJson.getString("name"));
		assertEquals("", responseJson.getString("handler"));
		assertEquals("[]", responseJson.getJSONArray("partners").toString());
		assertEquals(task.getEstimate(), responseJson.getInt("estimate"));
		assertEquals(task.getRemains(), responseJson.getInt("remains"));
		assertEquals(task.getActual(), responseJson.getInt("actual"));
		assertEquals(task.getNotes(), responseJson.getString("notes"));
	}
	
	@Test
	public void testGetTasksJsonString() throws Exception {
		ArrayList<TaskObject> tasks = new ArrayList<TaskObject>();
		for (int i = 0; i < 5; i++) {
			TaskObject task = new TaskObject(mCP.getAllProjects().get(0).getId());
			task.setName("TEST_TASK_" + (i+1)).setEstimate(3*i).setActual(5*i)
				.setRemains(8*i).setNotes("TEST_NOTE_" + (i+1)).save();
			tasks.add(task);
		}
		String response = ConvertSprintBacklog.getTasksJsonString(tasks);
		// assert data
		JSONObject responseJson = new JSONObject(response);
		JSONArray tasksJsonArray = responseJson.getJSONArray("tasks");
		assertEquals(5, tasksJsonArray.length());
		for (int i = 0; i < tasksJsonArray.length(); i++) {
			JSONObject taskJson = tasksJsonArray.getJSONObject(i);
			TaskObject task = tasks.get(i);
			
			assertEquals(task.getId(), taskJson.getLong("id"));
			assertEquals(task.getName(), taskJson.getString("name"));
			assertEquals("{}", taskJson.getString("handler"));
			assertEquals("[]", taskJson.getJSONArray("partners").toString());
			assertEquals(task.getEstimate(), taskJson.getInt("estimate"));
			assertEquals(task.getRemains(), taskJson.getInt("remain"));
			assertEquals(task.getActual(), taskJson.getInt("actual"));
			assertEquals(task.getNotes(), taskJson.getString("notes"));
		}
	}
	
	@Test
	public void testGetSprintBacklogJsonString() throws Exception {
		AddStoryToSprint ASTS = new AddStoryToSprint(3, 5, mCS, mCP, "EST");
		ASTS.exe();
		AddTaskToStory ATTS = new AddTaskToStory(3, 5, ASTS, mCP);
		ATTS.exe();
		long sprintId = 1;
		SprintObject sprint = SprintObject.get(sprintId);
		
		// give incorrect sprint id
		String response = ConvertSprintBacklog.getSprintBacklogJsonString(null);
		assertEquals("", response);
		
		// give correct sprint id
		response = ConvertSprintBacklog.getSprintBacklogJsonString(sprint);
		JSONObject responseJson = new JSONObject(response);
		assertEquals("true", responseJson.getString("success"));
		assertEquals(sprintId, responseJson.getLong("sprintId"));
		assertEquals(3, responseJson.getInt("Total"));
		JSONArray storiesJsonArray = responseJson.getJSONArray("Stories");
		assertEquals(3, storiesJsonArray.length());
		for (int i = 0; i <3; i++) {
			JSONObject storyJson = storiesJsonArray.getJSONObject(i);
			JSONArray tasksJsonArray = storyJson.getJSONArray("tasks");
			assertEquals(3, tasksJsonArray.length());
		}
	}
	
	@Test
	public void testGetTaskboardJsonString() throws Exception {
		AddStoryToSprint ASTS = new AddStoryToSprint(3, 5, mCS, mCP, "EST");
		ASTS.exe();
		AddTaskToStory ATTS = new AddTaskToStory(3, 5, ASTS, mCP);
		ATTS.exe();
		SprintObject sprint = SprintObject.get(1);
		
		// give incorrect sprint id
		String response = ConvertSprintBacklog.getSprintBacklogJsonString(null);
		assertEquals("", response);
		
		// give correct sprint id
		response = ConvertSprintBacklog.getSprintBacklogJsonString(sprint);
		JSONObject responseJson = new JSONObject(response);
		assertEquals("true", responseJson.getString("success"));
		assertEquals(3, responseJson.getInt("Total"));
		JSONArray storiesJsonArray = responseJson.getJSONArray("Stories");
		assertEquals(3, storiesJsonArray.length());
		for (int i = 0; i <3; i++) {
			JSONObject storyJson = storiesJsonArray.getJSONObject(i);
			JSONArray tasksJsonArray = storyJson.getJSONArray("tasks");
			assertEquals(3, tasksJsonArray.length());
		}
	}
	
	private String parseDate(long date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd-hh:mm:ss");
		Date d = new Date(date);
		String modifiedDate = sdf.format(d);
		return modifiedDate;
	}
}
