package ntut.csie.ezScrum.restful.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ntut.csie.ezScrum.dao.HistoryDAO;
import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.restful.mobile.service.ProductBacklogWebService;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateRelease;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataInfo.StoryInfo;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.databasEnum.IssueTypeEnum;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.logic.ProductBacklogLogic;
import ntut.csie.ezScrum.web.mapper.ProductBacklogMapper;
import ntut.csie.jcis.account.core.LogonException;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

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
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe(); // 初始化 SQL

		mCP = new CreateProject(mProjectCount);
		mCP.exeCreate(); // 新增一測試專案

		mCR = new CreateRelease(mReleaseCount, mCP);
		mCR.exe();

		mProject = mCP.getAllProjects().get(0);
	}

	@After
	public void tearDown() throws Exception {
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
		mProject = null;
		mConfig = null;
		mProductBacklogHelper = null;
	}

	@Test
	public void testcreateStory() throws LogonException, JSONException {
		String username = "admin";
		String userpwd = "admin";
		String projectName = mProject.getName();

		// Web service 物件
		ProductBacklogWebService productBacklogWebService = new ProductBacklogWebService(username, userpwd, projectName);
		// 建立 ProductBacklog
		mCPB = new CreateProductBacklog(mStoryCount, mEstimate, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		mCPB.exe();

		String TEST_STORY_NAME = "TEST_STORY_";			// Story Name
		String TEST_STORY_VALUE = "50";					// Business Value
		String TEST_STORY_IMP = "100";					// Story importance
		String TEST_STORY_EST = "2";					// Story estimation
		String TEST_STORY_HOW_TO_DEMO = "TEST_STORY_DEMO_";	// How to demo
		String TEST_STORY_NOTES = "TEST_STORY_NOTE_";	    // Story notes
		StoryInfo storyInformation = new StoryInfo
		        (TEST_STORY_NAME,
		         TEST_STORY_IMP,
		         TEST_STORY_EST,
		         TEST_STORY_VALUE,
		         TEST_STORY_HOW_TO_DEMO,
		         TEST_STORY_NOTES, "", "", "", "");
		

		Gson gson = new Gson();
		// call RESTFUL method
		productBacklogWebService.createStory(new JSONObject(gson.toJson(storyInformation)));
	
		// call local method
		IStory[] storyList = (new ProductBacklogLogic(mConfig.getUserSession(), mProject)).getStoriesByFilterType(null);
		assertTrue(storyList.length == mStoryCount + 1);
	}

	@Test
	public void testreadStory() throws LogonException, JSONException {
		String username = "admin";
		String userpwd = "admin";
		String projectName = mProject.getName();

		// Web service 物件
		ProductBacklogWebService productBacklogWebService = new ProductBacklogWebService(username, userpwd, projectName);
		// 建立 ProductBacklog
		mCPB = new CreateProductBacklog(mStoryCount, mEstimate, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		mCPB.exe();

		// call RESTFUL method
		productBacklogWebService.readStory(null);
		// get JSON 
		String response = productBacklogWebService.getRESTFulResponseString();
		// call local method
		IStory[] storyList = (new ProductBacklogLogic(mConfig.getUserSession(), mProject)).getStoriesByFilterType(null);
		// JSON to JSONArray 
		JSONArray storyJSONArray = new JSONArray(response);

		for (int i = 0; i < mStoryCount; i++) {
			JSONObject JSONObject = (JSONObject) storyJSONArray.get(i);
			assertEquals(String.valueOf(storyList[i].getStoryId()), JSONObject.get("id"));
			assertEquals(storyList[i].getName(), JSONObject.get("name"));
			assertEquals(storyList[i].getNotes(), JSONObject.get("notes"));
			assertEquals(storyList[i].getHowToDemo(), JSONObject.get("howToDemo"));
			assertEquals(storyList[i].getImportance(), JSONObject.get("importance"));
			assertEquals(storyList[i].getValue(), JSONObject.get("value"));
			assertEquals(storyList[i].getEstimated(), JSONObject.get("estimation"));
			assertEquals(storyList[i].getStatus(), JSONObject.get("status"));
			assertEquals(storyList[i].getSprintID(), JSONObject.get("sprint"));
			assertEquals(storyList[i].getReleaseID(), JSONObject.get("release"));
			assertEquals(storyList[i].getDescription(), JSONObject.get("description"));
		}

	}

	@Test
	public void testupdateStory() throws LogonException, JSONException, SQLException{
		String username = "admin";
		String userpwd = "admin";
		String projectName = mProject.getName();
		
		// Web service 物件
		ProductBacklogWebService productBacklogWebService = new ProductBacklogWebService(username, userpwd, projectName);
		// 建立 ProductBacklog
		mCPB = new CreateProductBacklog(0, mEstimate, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		mCPB.exe();
		
		String TEST_STORY_NAME = "TEST_STORY_";			// Story Name
		String TEST_STORY_VALUE = "50";					// Business Value
		String TEST_STORY_IMP = "100";					// Story importance
		String TEST_STORY_EST = "2";					// Story estimation
		String TEST_STORY_HOW_TO_DEMO = "TEST_STORY_DEMO_";	// How to demo
		String TEST_STORY_NOTES = "TEST_STORY_NOTE_";	    // Story notes
		String TEST_STORY_DESCRIPTION = "TEST_DESCRIPTION_";// Story description
		
		ProductBacklogMapper productBacklogMapper = new ProductBacklogMapper(mProject, mConfig.getUserSession());

		StoryInfo storyInformation = new StoryInfo
		               (TEST_STORY_NAME,
		                TEST_STORY_IMP,
		                TEST_STORY_EST,
		                TEST_STORY_VALUE,
		                TEST_STORY_HOW_TO_DEMO,
		                TEST_STORY_NOTES,
		                TEST_STORY_DESCRIPTION, "sprint01", "tag01", "release01");
		
		IIssue story = productBacklogMapper.addStory(storyInformation);
		
		mCPB.editStory(story.getIssueID(),
		        TEST_STORY_NAME + "1",
		        TEST_STORY_VALUE,
		        TEST_STORY_IMP,
		        TEST_STORY_EST,
		        TEST_STORY_HOW_TO_DEMO + "1",
		        TEST_STORY_NOTES + "1",
		        true);
		
		story = productBacklogMapper.getIssue(story.getIssueID());
		
		StoryObject storyObject = new StoryObject(story);

		Gson gson = new Gson();
		// call RESTFUL method
		productBacklogWebService.updateStory(new JSONObject(gson.toJson(storyObject)));

		// call local method
		IStory[] storyArray = (new ProductBacklogLogic(mConfig.getUserSession(), mProject)).getStoriesByFilterType(null);
		// assert
		assertEquals(TEST_STORY_NAME + "1", storyArray[0].getName());
	}

	@Test
	public void testdeleteStory() throws LogonException, SQLException {
		String username = "admin";
		String userpwd = "admin";
		String projectName = mProject.getName();

		// Web service 物件
		ProductBacklogWebService productBacklogWebService = new ProductBacklogWebService(username, userpwd, projectName);
		// 建立 ProductBacklog
		mCPB = new CreateProductBacklog(mStoryCount, mEstimate, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		mCPB.exe();

		// call RESTFUL method
		productBacklogWebService.deleteStory(String.valueOf(mCPB.getStoryIds().get(0)));
		// get all story
		IStory[] storyList = (new ProductBacklogLogic(mConfig.getUserSession(), mProject)).getStoriesByFilterType(null);
		// assert
		assertTrue(storyList.length == mStoryCount - 1);
		
		// get History
		HistoryDAO historyDAO = HistoryDAO.getInstance();
		ArrayList<HistoryObject> historyList = new ArrayList<HistoryObject>();
		historyList = historyDAO.getHistoriesByIssue(mCPB.getStoryIds().get(0), IssueTypeEnum.TYPE_STORY);

		// assert
		assertTrue(storyList.length == mStoryCount - 1);
		assertTrue(historyList.size() == 0);
	}

	@Test
	public void testreadStoryByID() throws LogonException, JSONException {
		String username = "admin";
		String userpwd = "admin";
		String projectName = mProject.getName();

		// Web service 物件
		ProductBacklogWebService productBacklogWebService = new ProductBacklogWebService(username, userpwd, projectName);
		// 建立 ProductBacklog
		mCPB = new CreateProductBacklog(mStoryCount, mEstimate, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		mCPB.exe();

		// call RESTFUL method
		productBacklogWebService.readStoryById(mCPB.getStoryIds().get(0));
		// get JSON 
		String response = productBacklogWebService.getRESTFulResponseString();
		// call local method
		IStory[] storyList = (new ProductBacklogLogic(mConfig.getUserSession(), mProject)).getStoriesByFilterType(null);

		JSONObject JSONObject = new JSONObject(response);
		assertEquals(String.valueOf(storyList[0].getStoryId()), JSONObject.get("id"));
		assertEquals(storyList[0].getName(), JSONObject.get("name"));
		assertEquals(storyList[0].getNotes(), JSONObject.get("notes"));
		assertEquals(storyList[0].getHowToDemo(), JSONObject.get("howToDemo"));
		assertEquals(storyList[0].getImportance(), JSONObject.get("importance"));
		assertEquals(storyList[0].getValue(), JSONObject.get("value"));
		assertEquals(storyList[0].getEstimated(), JSONObject.get("estimation"));
		assertEquals(storyList[0].getStatus(), JSONObject.get("status"));
		assertEquals(storyList[0].getSprintID(), JSONObject.get("sprint"));
		assertEquals(storyList[0].getReleaseID(), JSONObject.get("release"));
		assertEquals(storyList[0].getDescription(), JSONObject.get("description"));
	}

	@Test
	public void testgetTagList() throws LogonException, JSONException {
		String username = "admin";
		String userpwd = "admin";
		String projectName = mProject.getName();

		// Web service 物件
		ProductBacklogWebService productBacklogWebService = new ProductBacklogWebService(username, userpwd, projectName);
		// 建立 ProductBacklog
		mCPB = new CreateProductBacklog(mStoryCount, mEstimate, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		mCPB.exe();

		// call RESTFUL method
		productBacklogWebService.readAllTags();
		// get JSON 
		String response = productBacklogWebService.getRESTFulResponseString();
		// call local method
		mProductBacklogHelper = new ProductBacklogHelper(mConfig.getUserSession(), mProject);
		ArrayList<TagObject> iIssueTagList = mProductBacklogHelper.getTagList();
		// JSON to JSONArray 
		JSONArray tagJSONArray = new JSONArray(response);

		for (int i = 0; i < tagJSONArray.length(); i++) {
			JSONObject JSONObject = (JSONObject) tagJSONArray.get(i);
			assertEquals(String.valueOf(iIssueTagList.get(i).getId()), JSONObject.get("id"));
			assertEquals(String.valueOf(iIssueTagList.get(i).getName()), JSONObject.get("tagName"));
		}

	}

	@Test
	public void testGetStoryHistory() throws LogonException, JSONException, SQLException {
		String username = "admin";
		String userpwd = "admin";
		String projectName = mProject.getName();

		// Web service 物件
		ProductBacklogWebService productBacklogWebService = new ProductBacklogWebService(username, userpwd, projectName);
		// 建立 ProductBacklog
		mCPB = new CreateProductBacklog(mStoryCount, mEstimate, mCP, CreateProductBacklog.COLUMN_TYPE_EST);
		mCPB.exe();

		for (int i = 0; i < mStoryCount; i++) {
			// call RESTFUL method
			productBacklogWebService.readStoryHistory(mCPB.getStoryIds().get(i));
			// get JSON 
			String response = productBacklogWebService.getRESTFulResponseString();
			// call local method
			mProductBacklogHelper = new ProductBacklogHelper(mConfig.getUserSession(), mProject);
			List<HistoryObject> iIssuehistorys = mProductBacklogHelper.getIssue(mCPB.getStoryIds().get(i)).getHistories();

			// JSON to JSONObject
			JSONObject historyJSONObject = new JSONObject(response);
			// JSON to JSONArray 
			JSONArray historyJSONArray = historyJSONObject.getJSONArray("storyHistoryList");

			for (int j = 0; j < historyJSONArray.length(); j++) {
				JSONObject JSONObject = historyJSONArray.getJSONObject(j);
				assertEquals(String.valueOf(iIssuehistorys.get(j).getFormattedModifiedTime()), JSONObject.getString("modifyDate"));
				assertEquals(String.valueOf(iIssuehistorys.get(j).getHistoryTypeString()), JSONObject.getString("historyType"));
				assertEquals(String.valueOf(iIssuehistorys.get(j).getDescription()), JSONObject.getString("description"));
			}
		}
	}
}
