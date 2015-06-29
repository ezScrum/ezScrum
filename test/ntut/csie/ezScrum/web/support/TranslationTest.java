package ntut.csie.ezScrum.web.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.ChangeIssueStatus;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.CreateTask;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataInfo.StoryInfo;
import ntut.csie.ezScrum.web.dataInfo.TaskInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TranslationTest {
	private CreateProject mCP;
	private CreateSprint mCS;
	private CreateTask mCT;
	private CreateProductBacklog mCPB;
	private int mProjectCount = 1;
	private int mStoryCount = 10;
	private int mTaskCount = 2;
	private Configuration mConfig = null;
	private ProjectObject mProject;

	@Before
	public void setUp() throws Exception {
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
		mCS = new CreateSprint(1, mCP);
		mCS.exe();

		// 新增 Story
		mCPB = new CreateProductBacklog(mStoryCount, mCP);
		mCPB.exe();
		
		// 新增Task
		mCT = new CreateTask(mTaskCount, mCP);
		mCT.exe();

		mProject = mCP.getAllProjects().get(0);

		// 為了不讓 SQL 跑太快而沒有正確更新值進去
		Thread.sleep(500);

		// ============= release ==============
		ini = null;
	}

	@After
	public void tearDown() throws Exception {
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
		mCT = null;
		mCPB = null;
		mConfig = null;
		mProject = null;
	}

	@Test
	public void testTranslateStoryToXML_WithAllZero() {
		ProjectObject project = ProjectObject.get(mProject.getName());
		ProductBacklogHelper productBacklogHelper = new ProductBacklogHelper(project);
		
		for (int i = 0; i < mCPB.getStories().size(); i++) {
			// Story Data
			StoryObject story = mCPB.getStories().get(i);
			StoryInfo storyInfo = new StoryInfo();
			storyInfo.id = story.getId();
			storyInfo.name = "0";
			storyInfo.estimate = 0;
			storyInfo.value = 0;
			storyInfo.importance = 0;
			storyInfo.howToDemo = "0";
			storyInfo.sprintId = 0;
			storyInfo.notes = "0";
			storyInfo.status = 0;
			storyInfo.tags = "0";
			story = productBacklogHelper.updateStory(storyInfo.id, storyInfo);
			
			// Expected Response Text
			StringBuilder expectedText = new StringBuilder();
			expectedText.append("<ProductBacklog>");
			expectedText.append("<Total>1</Total>");
			expectedText.append("<Story>");
			expectedText.append("<Id>" + story.getId() + "</Id>");
			expectedText.append("<Link></Link>");
			expectedText.append("<Name>0</Name>");
			expectedText.append("<Value>0</Value>");
			expectedText.append("<Importance>0</Importance>");
			expectedText.append("<Estimate>0</Estimate>");
			expectedText.append("<Status>new</Status>");
			expectedText.append("<Notes>0</Notes>");
			expectedText.append("<HowToDemo>0</HowToDemo>");
			expectedText.append("<Release></Release>");
			expectedText.append("<Sprint>0</Sprint>");
			expectedText.append("<Tag></Tag>");
			expectedText.append("<Attach>false</Attach>");
			expectedText.append("</Story>");
			expectedText.append("</ProductBacklog>");

			String actualText = Translation.translateStoryToXML(story);
			assertEquals(expectedText.toString(), actualText);
		}
	}
	
	@Test
	public void testTranslateStoryToXML() {
		ProjectObject project = ProjectObject.get(mProject.getName());
		ProductBacklogHelper productBacklogHelper = new ProductBacklogHelper(project);
		
		for (int i = 0; i < mCPB.getStories().size(); i++) {
			// Tag data
			String tagName = "TEST_TAG_NAME";
			// create tag
			TagObject tag = new TagObject(tagName, project.getId());
			tag.save();
			// Story Data
			StoryObject story = mCPB.getStories().get(i);
			StoryInfo storyInfo = new StoryInfo();
			storyInfo.id = story.getId();
			storyInfo.name = "TEST_STORY_1_NAME";
			storyInfo.estimate = 10;
			storyInfo.value = 8;
			storyInfo.importance = 96;
			storyInfo.howToDemo = "TEST_STORY_HOWTODEMO";
			storyInfo.sprintId = 1;
			storyInfo.notes = "TEST_STORY_NOTES";
			storyInfo.status = 1;
			storyInfo.tags = tag.getName();
			story = productBacklogHelper.updateStory(storyInfo.id, storyInfo);
			
			// Expected Response Text
			StringBuilder expectedText = new StringBuilder();
			expectedText.append("<ProductBacklog>");
			expectedText.append("<Total>1</Total>");
			expectedText.append("<Story>");
			expectedText.append("<Id>" + story.getId() + "</Id>");
			expectedText.append("<Link></Link>");
			expectedText.append("<Name>" + storyInfo.name + "</Name>");
			expectedText.append("<Value>" + storyInfo.value + "</Value>");
			expectedText.append("<Importance>" + storyInfo.importance + "</Importance>");
			expectedText.append("<Estimate>" + storyInfo.estimate + "</Estimate>");
			expectedText.append("<Status>closed</Status>");
			expectedText.append("<Notes>" + storyInfo.notes + "</Notes>");
			expectedText.append("<HowToDemo>" + storyInfo.howToDemo + "</HowToDemo>");
			expectedText.append("<Release></Release>");
			expectedText.append("<Sprint>" + storyInfo.sprintId + "</Sprint>");
			expectedText.append("<Tag>" + tag.getName() + "</Tag>");
			expectedText.append("<Attach>false</Attach>");
			expectedText.append("</Story>");
			expectedText.append("</ProductBacklog>");

			String actualText = Translation.translateStoryToXML(story);
			assertEquals(expectedText.toString(), actualText);
		}
	}
	
	@Test
	public void testTranslateStoryToJson() {
		ProjectObject project = ProjectObject.get(mProject.getName());
		ProductBacklogHelper productBacklogHelper = new ProductBacklogHelper(project);
		
		for (int i = 0; i < mCPB.getStories().size(); i++) {
			// Tag data
			String tagName = "TEST_TAG_NAME";
			// create tag
			TagObject tag = new TagObject(tagName, project.getId());
			tag.save();
			// Story Data
			StoryObject story = mCPB.getStories().get(i);
			StoryInfo storyInfo = new StoryInfo();
			storyInfo.id = story.getId();
			storyInfo.name = "TEST_STORY_1_NAME";
			storyInfo.estimate = 10;
			storyInfo.value = 8;
			storyInfo.importance = 96;
			storyInfo.howToDemo = "TEST_STORY_HOWTODEMO";
			storyInfo.sprintId = 1;
			storyInfo.notes = "TEST_STORY_NOTES";
			storyInfo.status = StoryObject.STATUS_UNCHECK;
			storyInfo.tags = tag.getName();
			story = productBacklogHelper.updateStory(storyInfo.id, storyInfo);

			String actualText = Translation.translateStoryToJson(story);
			assertTrue(actualText.contains("\"success\":true"));
			assertTrue(actualText.contains("\"Total\":1"));
			assertTrue(actualText.contains("\"Id\":" + story.getId()));
			assertTrue(actualText.contains("\"Name\":\"" + storyInfo.name + "\""));
			assertTrue(actualText.contains("\"Value\":" + story.getValue()));
			assertTrue(actualText.contains("\"Estimate\":" + story.getEstimate()));
			assertTrue(actualText.contains("\"Importance\":" + story.getImportance()));
			assertTrue(actualText.contains("\"Tag\":\"" + story.getTags().get(0).getName() + "\""));
			assertTrue(actualText.contains("\"Status\":\"" + story.getStatusString() + "\""));
			assertTrue(actualText.contains("\"Notes\":\"" + story.getNotes() + "\""));
			assertTrue(actualText.contains("\"HowToDemo\":\"" + story.getHowToDemo() + "\""));
			assertTrue(actualText.contains("\"Sprint\":" + story.getSprintId()));
			assertTrue(actualText.contains("\"FilterType\":\"DETAIL\""));
			assertTrue(actualText.contains("\"Attach\":false"));
		}
	}
	
	// 測試是否有將 FilterType 加入 Story 的屬性之一
	@Test
	public void testTranslateStoryToJson2() throws Exception {
		ProjectObject project = ProjectObject.get(mProject.getName());
		ProductBacklogHelper productBacklogHelper = new ProductBacklogHelper(project);
		ArrayList<StoryObject> stories = new ArrayList<StoryObject>();

		// initial data
		for (int i = 0; i < 10; i++) {
			StoryInfo storyInfo = new StoryInfo();
			storyInfo.id = mCPB.getStories().get(i).getId();
			storyInfo.name = "0";
			storyInfo.estimate = 0;
			storyInfo.value = 0;
			storyInfo.importance = 0;
			storyInfo.howToDemo = "0";
			storyInfo.sprintId = 0;
			storyInfo.notes = "TEST_NOTES";
			productBacklogHelper.updateStory(storyInfo.id, storyInfo);

			StoryObject story = mCPB.getStories().get(i);
			story.reload();
			assertEquals(0, story.getEstimate());
			assertEquals(0, story.getImportance());
			assertEquals(0, story.getValue());
		}

		StringBuilder actualSB = new StringBuilder();
		for (int i = 0; i < 10; i++) {
			StoryObject story = mCPB.getStories().get(i);
			story.reload();
			stories.add(story);
			actualSB = new StringBuilder();
			actualSB.append(Translation.translateStoryToJson(stories
					.get(i)));
			assertFalse(actualSB.toString().contains("DONE"));
			assertFalse(actualSB.toString().contains("DETAIL"));
			assertTrue(actualSB.toString().contains("BACKLOG"));
		}

		ArrayList<StoryObject> doneStories = new ArrayList<StoryObject>();
		for (int i = 0; i < 4; i++) {
			doneStories.add(stories.get(i));
		}
		
		ChangeIssueStatus CIS = new ChangeIssueStatus(doneStories, mCP);
		// 將前四筆狀態 done
		CIS.exeCloseStories();
		
		for (int i = 0; i < 10; i++) {
			StoryObject story = stories.get(i);
			story.reload();
		}

		// 驗證 done 狀態
		for (int i = 0; i < 4; i++) {
			StoryObject story = stories.get(i);
			actualSB = new StringBuilder();
			actualSB.append(Translation.translateStoryToJson(story));
			assertTrue(actualSB.toString().contains("DONE"));
			assertFalse(actualSB.toString().contains("DETAIL"));
			assertFalse(actualSB.toString().contains("BACKLOG"));
		}

		// 驗證 backlog 狀態
		for (int i = 4; i < 9; i++) {
			StoryObject story = stories.get(i);
			actualSB = new StringBuilder();
			actualSB.append(Translation.translateStoryToJson(story));
			System.out.println(actualSB);
			assertFalse(actualSB.toString().contains("DONE"));
			assertFalse(actualSB.toString().contains("DETAIL"));
			assertTrue(actualSB.toString().contains("BACKLOG"));
		}

		// 驗證 detail 狀態
		for (int i = 0; i < 10; i++) {
			StoryObject story = stories.get(i);
			actualSB = new StringBuilder();
			actualSB.append(Translation.translateStoryToJson(story));
			assertFalse(actualSB.toString().contains("DETAIL"));
		}

		// 將 4 - 5 改成 detail (目前判斷是 value / estimation / importance 這三者皆要有值才算是)
		StoryInfo storyInfo = new StoryInfo();
		storyInfo.id = stories.get(4).getId();
		storyInfo.name = "";
		storyInfo.estimate = 1;
		storyInfo.value = 1;
		storyInfo.importance = 1;
		storyInfo.howToDemo = "";
		storyInfo.sprintId = 0;
		productBacklogHelper.updateStory(storyInfo.id, storyInfo);

		storyInfo.id = stories.get(5).getId();
		productBacklogHelper.updateStory(storyInfo.id, storyInfo);
		
		for (int i = 0; i < 10; i++) {
			StoryObject story = stories.get(i);
			story.reload();
		}

		// 驗證 done 狀態
		for (int i = 0; i < 4; i++) {
			StoryObject story = stories.get(i);
			actualSB = new StringBuilder();
			actualSB.append(Translation.translateStoryToJson(story));
			assertTrue(actualSB.toString().contains("DONE"));
			assertFalse(actualSB.toString().contains("DETAIL"));
			assertFalse(actualSB.toString().contains("BACKLOG"));
		}

		// 驗證 detail 狀態
		for (int i = 4; i < 6; i++) {
			StoryObject story = stories.get(i);
			actualSB = new StringBuilder();
			actualSB.append(Translation.translateStoryToJson(story));
			assertFalse(actualSB.toString().contains("DONE"));
			assertTrue(actualSB.toString().contains("DETAIL"));
			assertFalse(actualSB.toString().contains("BACKLOG"));
		}

		// 驗證 backlog 狀態
		for (int i = 7; i < 10; i++) {
			StoryObject story = stories.get(i);
			actualSB = new StringBuilder();
			actualSB.append(Translation.translateStoryToJson(story));
			assertFalse(actualSB.toString().contains("DONE"));
			assertFalse(actualSB.toString().contains("DETAIL"));
			assertTrue(actualSB.toString().contains("BACKLOG"));
		}
	}
	
	@Test
	public void testTranslateStoriesToJson() throws JSONException {
		// Test Data
		String TEST_STORY_NAME = "TEST_STORY_NAME";
		int TEST_STORY_ESTIMATE = 10;
		int TEST_STORY_IMPORTANCE = 96;
		String TEST_STORY_HOWTODEMO = "TEST_STORY_HOWTODEMO";
		String TEST_STORY_NOTES = "TEST_STORY_NOTES";
		int TEST_STORY_VALUE = 8;
		int TEST_STORY_SPRINTID = -1;
		int TEST_STORY_STATUS = StoryObject.STATUS_UNCHECK;
		// Tag data
		String tagName = "TEST_TAG_NAME";

		ProjectObject project = ProjectObject.get(mProject.getName());
		ProductBacklogHelper productBacklogHelper = new ProductBacklogHelper(project);
		ArrayList<StoryObject> stories = new ArrayList<StoryObject>();

		// setting stories
		for (int i = 0; i < mCPB.getStories().size(); i++) {
			// create tag
			TagObject tag = new TagObject(tagName, project.getId());
			tag.save();
			// Story Data
			StoryObject story = mCPB.getStories().get(i);
			StoryInfo storyInfo = new StoryInfo();
			storyInfo.id = story.getId();
			storyInfo.name = TEST_STORY_NAME + i;
			storyInfo.estimate = TEST_STORY_ESTIMATE;
			storyInfo.value = TEST_STORY_VALUE;
			storyInfo.importance = TEST_STORY_IMPORTANCE;
			storyInfo.howToDemo = TEST_STORY_HOWTODEMO;
			storyInfo.sprintId = TEST_STORY_SPRINTID;
			storyInfo.notes = TEST_STORY_NOTES;
			storyInfo.status = TEST_STORY_STATUS;
			storyInfo.tags = tag.getName();
			story = productBacklogHelper.updateStory(storyInfo.id, storyInfo);
			stories.add(story);
		}

		// call translateStoriesToJson
		String actualText = Translation.translateStoriesToJson(stories);
		// get JSON
		JSONObject storiesJSON = new JSONObject(actualText);
		assertTrue(storiesJSON.toString().contains("\"success\":true"));
		assertTrue(storiesJSON.toString().contains("\"Total\":10"));

		JSONArray storiesJSONArray = storiesJSON.getJSONArray("Stories");

		for (int i = 0; i < storiesJSONArray.length(); i++) {
			JSONObject storyJson = storiesJSONArray.getJSONObject(i);
			assertTrue(storyJson.toString().contains("\"Id\":" + stories.get(i).getId()));
			assertTrue(storyJson.toString().contains("\"Name\":\"" + TEST_STORY_NAME + i + "\""));
			assertTrue(storyJson.toString().contains("\"Value\":" + TEST_STORY_VALUE));
			assertTrue(storyJson.toString().contains("\"Estimate\":" + TEST_STORY_ESTIMATE));
			assertTrue(storyJson.toString().contains("\"Importance\":" + TEST_STORY_IMPORTANCE));
			assertTrue(storyJson.toString().contains("\"Tag\":\"" + tagName + "\""));
			assertTrue(storyJson.toString().contains("\"Status\":\"new\""));
			assertTrue(storyJson.toString().contains("\"Notes\":\"" + TEST_STORY_NOTES + "\""));
			assertTrue(storyJson.toString().contains("\"HowToDemo\":\"" + TEST_STORY_HOWTODEMO + "\""));
			assertTrue(storyJson.toString().contains("\"Sprint\":\"None\""));
			assertTrue(storyJson.toString().contains("\"FilterType\":\"DETAIL\""));
			assertTrue(storyJson.toString().contains("\"Attach\":false"));
		}

	}
	
	@Test
	public void testTranslateTaskToJson() {
		ProjectObject project = ProjectObject.get(mProject.getName());
		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(project);

		for (int i = 0; i < mCT.getTaskList().size(); i++) {
			// Task Data
			TaskObject task = mCT.getTaskList().get(i);
			TaskInfo taskInfo = new TaskInfo();
			taskInfo.taskId = task.getId();
			taskInfo.name = "TEST_TASK_NAME_" + i;
			taskInfo.notes = "TEST_TASK_NOTES";
			taskInfo.estimate = 13;
			taskInfo.actual = 13;
			taskInfo.remains = 13;
			taskInfo.projectId = project.getId();
			taskInfo.status = TaskObject.STATUS_UNCHECK;
			sprintBacklogHelper.updateTask(taskInfo, "", "");
			task = sprintBacklogHelper.getTask(taskInfo.taskId);
			
			String actualText = Translation.translateTaskToJson(task);
			assertTrue(actualText.contains("\"success\":true"));
			assertTrue(actualText.contains("\"Total\":1"));
			assertTrue(actualText.contains("\"Id\":" + task.getId()));
			assertTrue(actualText.contains("\"Name\":\"" + taskInfo.name + "\""));
			assertTrue(actualText.contains("\"Estimate\":" + task.getEstimate()));
			assertTrue(actualText.contains("\"Status\":\"" + task.getStatusString() + "\""));
			assertTrue(actualText.contains("\"Notes\":\"" + task.getNotes() + "\""));
			assertTrue(actualText.contains("\"Attach\":false"));
		}
	}
	
	@Test
	public void testTranslateTaskboardStoryToJson() {
		ProjectObject project = ProjectObject.get(mProject.getName());
		ProductBacklogHelper productBacklogHelper = new ProductBacklogHelper(project);
		
		for (int i = 0; i < mCPB.getStories().size(); i++) {
			// Story Data
			StoryObject story = mCPB.getStories().get(i);
			StoryInfo storyInfo = new StoryInfo();
			storyInfo.id = story.getId();
			storyInfo.name = "TEST_STORY_1_NAME" + i;
			storyInfo.estimate = 10;
			storyInfo.value = 8;
			storyInfo.importance = 96;
			storyInfo.howToDemo = "TEST_STORY_HOWTODEMO";
			storyInfo.sprintId = 1;
			storyInfo.notes = "TEST_STORY_NOTES";
			storyInfo.status = StoryObject.STATUS_UNCHECK;
			story = productBacklogHelper.updateStory(storyInfo.id, storyInfo);

			String actualText = Translation.translateTaskboardStoryToJson(story);
			assertTrue(actualText.contains("\"success\":true"));
			assertTrue(actualText.contains("\"Id\":" + story.getId()));
			assertTrue(actualText.contains("\"Name\":\"" + storyInfo.name + "\""));
			assertTrue(actualText.contains("\"Estimate\":" + storyInfo.estimate));
		}
		
	}

	@Test
	public void testTranslateTaskboardTaskToJson() {
		ProjectObject project = ProjectObject.get(mProject.getName());
		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(project);
		
		// Test Account
		String TEST_ACCOUNT_NAME = "TEST_ACCOUNT_NAME";
		AccountObject account = new AccountObject(TEST_ACCOUNT_NAME);
		account.save();
		
		for (int i = 0; i < mCT.getTaskList().size(); i++) {
			// Task Data
			TaskObject task = mCT.getTaskList().get(i);
			TaskInfo taskInfo = new TaskInfo();
			taskInfo.taskId = task.getId();
			taskInfo.name = "TEST_TASK_NAME_" + i;
			taskInfo.notes = "TEST_TASK_NOTES";
			taskInfo.estimate = 13;
			taskInfo.actual = 13;
			taskInfo.remains = 13;
			taskInfo.projectId = project.getId();
			taskInfo.status = TaskObject.STATUS_UNCHECK;
			sprintBacklogHelper.updateTask(taskInfo, TEST_ACCOUNT_NAME, "");
			task = sprintBacklogHelper.getTask(taskInfo.taskId);
			
			String actualText = Translation.translateTaskboardTaskToJson(task);
			assertTrue(actualText.contains("\"success\":true"));
			assertTrue(actualText.contains("\"Id\":" + task.getId()));
			assertTrue(actualText.contains("\"Name\":\"" + taskInfo.name + "\""));
			assertTrue(actualText.contains("\"Handler\":\"" + TEST_ACCOUNT_NAME + "\""));
			assertTrue(actualText.contains("\"Partners\":"));
		}
	}
	
	@Test
	public void testTranslateSprintInfoToJson() throws JSONException {
		String actualText = Translation.translateSprintInfoToJson(mCS.getSprintsId().get(0), 13, 13, 25, 25, 1, "SprintGoal", "Story", "Task", true);
		// Assert
		assertTrue(actualText.contains("\"success\":true"));
		assertTrue(actualText.contains("\"Total\":1"));
		assertTrue(actualText.contains("\"Id\":" + mCS.getSprintsId().get(0)));
		assertTrue(actualText.contains("\"Name\":" + "\"Sprint #" + mCS.getSprintsId().get(0) + "\""));
		assertTrue(actualText.contains("\"InitialPoint\":\"13.0\""));
		assertTrue(actualText.contains("\"CurrentPoint\":\"13.0\""));
		assertTrue(actualText.contains("\"InitialHours\":\"25.0\""));
		assertTrue(actualText.contains("\"CurrentHours\":\"25.0\""));
		assertTrue(actualText.contains("\"ReleaseID\":" + "\"Release #1\""));
		assertTrue(actualText.contains("\"SprintGoal\":\"SprintGoal\""));
		assertTrue(actualText.contains("\"StoryChartUrl\":\"Story\""));
		assertTrue(actualText.contains("\"TaskChartUrl\":\"Task\""));
		assertTrue(actualText.contains("\"IsCurrentSprint\":true"));
	}
	
	@Test
	public void testTranslateSprintBacklogToJson() throws JSONException {
		int currentSprintId = 1;
		double currentPoint = 2;
		double limitedPoint = 3;
		double taskPoint = 4;
		int releaseId = 5;
		String sprintGoal = "Hello Sprint Goal";
		String result =  Translation.translateSprintBacklogToJson(mCPB.getStories(), currentSprintId, currentPoint, limitedPoint, taskPoint, releaseId, sprintGoal);
	
		JSONObject actualJson = new JSONObject(result);
		assertEquals(mCPB.getStories().size(), actualJson.getInt("Total"));
		assertTrue(actualJson.getBoolean("success"));
		JSONObject sprint = actualJson.getJSONObject("Sprint");
		assertEquals(currentSprintId, sprint.getInt("Id"));
		assertEquals("Sprint #1", sprint.getString("Name"));
		assertEquals(currentPoint, sprint.getDouble("CurrentPoint"));
		assertEquals(limitedPoint, sprint.getDouble("LimitedPoint"));
		assertEquals(taskPoint, sprint.getDouble("TaskPoint"));
		assertEquals("Release #" + releaseId, sprint.getString("ReleaseID"));
		assertEquals(sprintGoal, sprint.getString("SprintGoal"));
		
		JSONArray stories = actualJson.getJSONArray("Stories");
		for (int i = 0; i < mCPB.getStories().size(); i++) {
			int storyIndex = i + 1;
			JSONObject story = stories.getJSONObject(i);
			assertEquals(storyIndex, story.getInt("Id"));
			assertEquals("", story.getString("Link"));
			assertEquals("TEST_STORY_" + storyIndex,
					story.getString("Name"));
			assertEquals(50, story.getInt("Value"));
			assertEquals(100, story.getInt("Importance"));
			assertEquals(2, story.getInt("Estimate"));
			assertEquals("new", story.getString("Status"));
			assertEquals("TEST_STORY_NOTE_" + storyIndex,
					story.getString("Notes"));
			assertEquals("", story.getString("Tag"));
			assertEquals("TEST_STORY_DEMO_" + storyIndex,
					story.getString("HowToDemo"));
			assertEquals("", story.getString("Release"));
			assertEquals(-1, story.getLong("Sprint"));
			assertEquals(false, story.getBoolean("Attach"));
			JSONArray attachFiles = story.getJSONArray("AttachFileList");
			assertEquals(0, attachFiles.length());
		}
	}
	
	@Test
	public void testTranslateBurndownChartDataToJson() throws JSONException {
		LinkedHashMap<Date, Double> dateToStoryIdealPoint = new LinkedHashMap<Date, Double>();	// Story的理想線
		LinkedHashMap<Date, Double> dateToStoryPoint = new LinkedHashMap<Date, Double>();	// Story的真實線
		Date date1 = DateUtil.dayFillter("2015/04/06-13:14:01", DateUtil._8DIGIT_DATE_1);
		Date date2 = DateUtil.dayFillter("2015/04/07-10:14:01", DateUtil._8DIGIT_DATE_1);
		Date date3 = DateUtil.dayFillter("2015/04/08-15:14:01", DateUtil._8DIGIT_DATE_1);
		Date date4 = DateUtil.dayFillter("2015/04/09-16:14:01", DateUtil._8DIGIT_DATE_1);
		Date date5 = DateUtil.dayFillter("2015/04/10-12:14:01", DateUtil._8DIGIT_DATE_1);
		dateToStoryIdealPoint.put(date1, 10d);
		dateToStoryIdealPoint.put(date2, 7.5d);
		dateToStoryIdealPoint.put(date3, 5d);
		dateToStoryIdealPoint.put(date4, 2.5d);
		dateToStoryIdealPoint.put(date5, 0d);
		dateToStoryPoint.put(date1, 10d);
		dateToStoryPoint.put(date2, 10d);
		dateToStoryPoint.put(date3, 10d);
		dateToStoryPoint.put(date4, 5d);
		dateToStoryPoint.put(date5, 5d);
		String result = Translation.translateBurndownChartDataToJson(dateToStoryIdealPoint, dateToStoryPoint);
		JSONObject actualJson = new JSONObject(result);
		assertTrue(actualJson.getBoolean("success"));
		JSONArray points = actualJson.getJSONArray("Points");
		// day 1
		JSONObject pointOfDay1 = points.getJSONObject(0);
		assertEquals("2015/04/06", pointOfDay1.getString("Date"));
		assertEquals(10d, pointOfDay1.getDouble("IdealPoint"));
		assertEquals(10d, pointOfDay1.getDouble("RealPoint"));
		// day 2
		JSONObject pointOfDay2 = points.getJSONObject(1);
		assertEquals("2015/04/07", pointOfDay2.getString("Date"));
		assertEquals(7.5d, pointOfDay2.getDouble("IdealPoint"));
		assertEquals(10d, pointOfDay2.getDouble("RealPoint"));
		// day 3 
		JSONObject pointOfDay3 = points.getJSONObject(2);
		assertEquals("2015/04/08", pointOfDay3.getString("Date"));
		assertEquals(5d, pointOfDay3.getDouble("IdealPoint"));
		assertEquals(10d, pointOfDay3.getDouble("RealPoint"));
		// day 4
		JSONObject pointOfDay4 = points.getJSONObject(3);
		assertEquals("2015/04/09", pointOfDay4.getString("Date"));
		assertEquals(2.5d, pointOfDay4.getDouble("IdealPoint"));
		assertEquals(5d, pointOfDay4.getDouble("RealPoint"));
		// day 5
		JSONObject pointOfDay5 = points.getJSONObject(4);
		assertEquals("2015/04/10", pointOfDay5.getString("Date"));
		assertEquals(0d, pointOfDay5.getDouble("IdealPoint"));
		assertEquals(5d, pointOfDay5.getDouble("RealPoint"));
	}
	
	@Test
	public void testTranslateBurndownChartDataToJson_WithEmptyData() throws JSONException {
		LinkedHashMap<Date, Double> dateToStoryIdealPoint = new LinkedHashMap<Date, Double>();	// Story的理想線
		LinkedHashMap<Date, Double> dateToStoryPoint = new LinkedHashMap<Date, Double>();	// Story的真實線
		String result = Translation.translateBurndownChartDataToJson(dateToStoryIdealPoint, dateToStoryPoint);
		JSONObject actualJson = new JSONObject(result);
		assertTrue(actualJson.getBoolean("success"));
		JSONArray points = actualJson.getJSONArray("Points");
		assertEquals(0, points.length());
	}
	
	@Test
	public void testJoin() {
		ArrayList<TagObject> tags = new ArrayList<TagObject>();
		assertEquals("", Translation.Join(tags, ";"));
	}
	
	@Test
	public void testJoin_WithMoreTags() {
		ArrayList<TagObject> tags = new ArrayList<TagObject>();
		for (int i = 1; i <= 5; i++) {
			TagObject tag = new TagObject("Tag" + String.valueOf(i), mProject.getId());
			tags.add(tag);
		}
		assertEquals("Tag1;Tag2;Tag3;Tag4;Tag5", Translation.Join(tags, ";"));
	}
}