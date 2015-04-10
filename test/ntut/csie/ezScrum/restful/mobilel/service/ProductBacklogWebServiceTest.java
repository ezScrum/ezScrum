package ntut.csie.ezScrum.restful.mobilel.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import ntut.csie.ezScrum.dao.HistoryDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.restful.mobile.service.ProductBacklogWebService;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.databasEnum.IssueTypeEnum;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.jcis.account.core.LogonException;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProductBacklogWebServiceTest {
	private int mProjectCount = 1;
	private int mReleaseCount = 1;
	private int mStoryCount = 3;
	private int mEstimate = 90;
	private CreateProject mCP;
	private CreateRelease mCR;
	private CreateProductBacklog mCPB;
	private ProjectObject mProject;
	private Configuration mConfig;
	private ProductBacklogHelper mProductBacklogHelper;

	@Before
	public void setUp() {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 新增一測試專案
		mCP = new CreateProject(mProjectCount);
		mCP.exeCreate();

		mCR = new CreateRelease(mReleaseCount, mCP);
		mCR.exe();
		
		mProject = mCP.getAllProjects().get(0);
		mProductBacklogHelper = new ProductBacklogHelper(mProject);
	}

	@After
	public void tearDown() {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();											

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		
		mConfig.setTestMode(false);
		mConfig.save();

		// release
		mCP = null;
		mCR = null;
		mCPB = null;
		mProject = null;
		mConfig = null;
		mProductBacklogHelper = null;
	}

	@Test
	public void testCreateStory() throws LogonException, JSONException {
		String username = "admin";
		String password = "admin";
		String projectName = mProject.getName();

		// Web service 物件
		ProductBacklogWebService service = new ProductBacklogWebService(username, password, projectName);
		// 建立 ProductBacklog
		mCPB = new CreateProductBacklog(mStoryCount, mEstimate, mCP, "EST");
		mCPB.exe();

		JSONObject storyJson = new JSONObject();
		storyJson
			.put("name", "TEST_STORY")
			.put("importance", 100)
			.put("estimate", 2)
			.put("value", 50)
			.put("how_to_demo", "TEST_STORY_DEMO")
			.put("notes", "TEST_STORY_NOTE")
			.put("status", 1)
			.put("sprint_id", -1)
			.put("tags", "");

		// call RESTFUL method
		service.createStory(storyJson.toString());
	
		// call local method
		ArrayList<StoryObject> stories = mProductBacklogHelper.getStories();
		assertTrue(stories.size() == mStoryCount + 1);
	}

	@Test
	public void testGetStory() throws LogonException, JSONException {
		String username = "admin";
		String password = "admin";
		String projectName = mProject.getName();

		// Web service 物件
		ProductBacklogWebService service = new ProductBacklogWebService(username, password, projectName);
		// 建立 ProductBacklog
		mCPB = new CreateProductBacklog(mStoryCount, mEstimate, mCP, "EST");
		mCPB.exe();
		
		// call RESTFUL method get not existed story
		service.getStory(-1);
		// get JSON 
		String response = service.getRESTFulResponseString();
		assertEquals("{}", response);

		// call RESTFUL method
		service.getStory(1);
		// get JSON 
		response = service.getRESTFulResponseString();
		// call local method
		StoryObject story = mProductBacklogHelper.getStory(1);
		// JSON to JSONObject 
		JSONObject storyJson = new JSONObject(response);

		assertEquals(story.getId(), storyJson.getLong("id"));
		assertEquals(story.getName(), storyJson.getString("name"));
		assertEquals(story.getNotes(), storyJson.getString("notes"));
		assertEquals(story.getHowToDemo(), storyJson.getString("how_to_demo"));
		assertEquals(story.getImportance(), storyJson.getInt("importance"));
		assertEquals(story.getValue(), storyJson.getInt("value"));
		assertEquals(story.getEstimate(), storyJson.getInt("estimate"));
		assertEquals(story.getStatus(), storyJson.getInt("status"));
		assertEquals(story.getSprintId(), storyJson.getLong("sprint_id"));
	}
	
	@Test
	public void testGetStories() throws LogonException, JSONException {
		String username = "admin";
		String password = "admin";
		String projectName = mProject.getName();

		// Web service 物件
		ProductBacklogWebService service = new ProductBacklogWebService(username, password, projectName);
		// 建立 ProductBacklog
		mCPB = new CreateProductBacklog(mStoryCount, mEstimate, mCP, "EST");
		mCPB.exe();

		// call RESTFUL method
		service.getStories();
		// get JSON 
		String response = service.getRESTFulResponseString();
		// call local method
		ArrayList<StoryObject> stories = mProductBacklogHelper.getStories();
		// JSON to JSONObject 
		JSONObject storiesJson = new JSONObject(response);

		for (int i = 0; i < mStoryCount; i++) {
			StoryObject story = stories.get(i);
			JSONObject storyJson = storiesJson.getJSONArray("stories").getJSONObject(i);
			assertEquals(story.getId(), storyJson.getLong("id"));
			assertEquals(story.getName(), storyJson.getString("name"));
			assertEquals(story.getNotes(), storyJson.getString("notes"));
			assertEquals(story.getHowToDemo(), storyJson.getString("how_to_demo"));
			assertEquals(story.getImportance(), storyJson.getInt("importance"));
			assertEquals(story.getValue(), storyJson.getInt("value"));
			assertEquals(story.getEstimate(), storyJson.getInt("estimate"));
			assertEquals(story.getStatus(), storyJson.getInt("status"));
			assertEquals(story.getSprintId(), storyJson.getLong("sprint_id"));
		}
	}

	@Test
	public void testUpdateStory() throws LogonException, JSONException {
		String username = "admin";
		String password = "admin";
		String projectName = mProject.getName();
		
		// Web service 物件
		ProductBacklogWebService service = new ProductBacklogWebService(username, password, projectName);
		// 建立 ProductBacklog
		mCPB = new CreateProductBacklog(0, mEstimate, mCP, "EST");
		mCPB.exe();
		
		JSONObject storyJson = new JSONObject();
		storyJson
			.put("name", "TEST_STORY")
			.put("importance", 100)
			.put("estimate", 2)
			.put("value", 50)
			.put("how_to_demo", "TEST_STORY_DEMO")
			.put("notes", "TEST_STORY_NOTE")
			.put("status", 0)
			.put("sprint_id", -1)
			.put("tags", "QOQ,QAQ");
		service.createStory(storyJson.toString());
		
		// create tags
		TagObject tag1 = new TagObject("QOQ", mProject.getId());
		tag1.save();
		TagObject tag2 = new TagObject("QAQ", mProject.getId());
		tag2.save();
		TagObject tag3 = new TagObject("QWQ", mProject.getId());
		tag3.save();
		
		storyJson = new JSONObject();
		storyJson
			.put("id", 1)
			.put("name", "QQ")
			.put("importance", 90)
			.put("estimate", 8)
			.put("value", 100)
			.put("how_to_demo", "要死啦")
			.put("notes", "改不完拉")
			.put("status", 1)
			.put("sprint_id", 1)
			.put("tags", "QAQ,QWQ");
		
		// call RESTFUL method
		service.updateStory(storyJson.toString());
		// get JSON 
		String response = service.getRESTFulResponseString();
		// JSON to JSONObject 
		storyJson = new JSONObject(response);
		
		// assert
		assertEquals("SUCCESS", storyJson.getString("status"));
	}

	@Test
	public void testDeleteStory() throws LogonException {
		String username = "admin";
		String password = "admin";
		String projectName = mProject.getName();

		// Web service 物件
		ProductBacklogWebService service = new ProductBacklogWebService(username, password, projectName);
		// 建立 ProductBacklog
		mCPB = new CreateProductBacklog(mStoryCount, mEstimate, mCP, "EST");
		mCPB.exe();

		// call RESTFUL method
		service.deleteStory(mCPB.getStoryIds().get(0));
		// get all story
		ArrayList<StoryObject> stories = mProductBacklogHelper.getStories();
		// assert
		assertTrue(stories.size() == mStoryCount - 1);
		
		// get History
		HistoryDAO historyDAO = HistoryDAO.getInstance();
		ArrayList<HistoryObject> histories = new ArrayList<HistoryObject>();
		histories = historyDAO.getHistoriesByIssue(mCPB.getStoryIds().get(0), IssueTypeEnum.TYPE_STORY);

		// assert
		assertEquals(histories.size(), 0);
	}

	@Test
	public void testGetTags() throws LogonException, JSONException {
		String username = "admin";
		String password = "admin";
		String projectName = mProject.getName();

		// Web service 物件
		ProductBacklogWebService service = new ProductBacklogWebService(username, password, projectName);
		// 建立 ProductBacklog
		mCPB = new CreateProductBacklog(mStoryCount, mEstimate, mCP, "EST");
		mCPB.exe();

		// call RESTFUL method
		service.getAllTags();
		// get JSON 
		String response = service.getRESTFulResponseString();
		// call local method
		mProductBacklogHelper = new ProductBacklogHelper(mProject);
		ArrayList<TagObject> tags = mProductBacklogHelper.getTagList();
		// JSON to JSONArray 
		JSONObject responseJson = new JSONObject(response);
		JSONArray tagsJson = responseJson.getJSONArray("tags");

		for (int i = 0; i < tagsJson.length(); i++) {
			JSONObject JSONObject = (JSONObject) tagsJson.get(i);
			assertEquals(String.valueOf(tags.get(i).getId()), JSONObject.get("id"));
			assertEquals(String.valueOf(tags.get(i).getName()), JSONObject.get("tagName"));
		}
	}

	@Test
	public void testGetStoryHistory() throws LogonException, JSONException {
		String username = "admin";
		String password = "admin";
		String projectName = mProject.getName();

		// Web service 物件
		ProductBacklogWebService service = new ProductBacklogWebService(username, password, projectName);
		// 建立 ProductBacklog
		mCPB = new CreateProductBacklog(mStoryCount, mEstimate, mCP, "EST");
		mCPB.exe();

		for (int i = 0; i < mStoryCount; i++) {
			// call RESTFUL method
			service.getStoryHistory(mCPB.getStoryIds().get(i));
			// get JSON 
			String response = service.getRESTFulResponseString();
			// call local method
			mProductBacklogHelper = new ProductBacklogHelper(mProject);
			ArrayList<HistoryObject> histories = mProductBacklogHelper.getStory(mCPB.getStoryIds().get(i)).getHistories();

			// JSON to JSONObject
			JSONObject historyJson = new JSONObject(response);
			// JSON to JSONArray 
			JSONArray historiesJson = historyJson.getJSONArray("histories");

			for (int j = 0; j < historiesJson.length(); j++) {
				JSONObject JSONObject = historiesJson.getJSONObject(j);
				assertEquals(histories.get(j).getIssueId(), JSONObject.getInt("issue_id"));
				assertEquals(histories.get(j).getIssueType(), JSONObject.getInt("issue_type"));
				assertEquals(histories.get(j).getHistoryType(), JSONObject.getInt("type"));
				assertEquals(histories.get(j).getDescription(), JSONObject.getString("description"));
			}
		}
	}
}
