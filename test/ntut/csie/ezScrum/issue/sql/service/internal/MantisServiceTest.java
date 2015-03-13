package ntut.csie.ezScrum.issue.sql.service.internal;

import static org.junit.Assert.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import ntut.csie.ezScrum.dao.HistoryDAO;
import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.IIssueNote;
import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.issue.internal.Issue;
import ntut.csie.ezScrum.issue.internal.IssueNote;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateTag;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataInfo.AttachFileInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.AttachFileObject;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.databasEnum.IssueTypeEnum;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;
import org.jdom.Element;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MantisServiceTest {
	private int mProjectCount = 1;
	private int mStoryCount = 1;
	private CreateProject mCP;
	private IProject mProject;
	private Configuration mConfig;
	private MantisService mMantisService;

	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration(new UserSession(AccountObject.get("admin")));
		mConfig.setTestMode(true);
		mConfig.save();
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 新增Project
		mCP = new CreateProject(mProjectCount);
		mCP.exeCreate();

		mProject = mCP.getProjectList().get(0);
		mMantisService = new MantisService(mConfig);
	}

	@After
	public void tearDown() throws Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 再一次確認SQL 連線已經關閉
		mMantisService.closeConnect();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(mConfig.getDataPath());

		mConfig.setTestMode(false);
		mConfig.save();

		// release resource
		mCP = null;
		mProject = null;
		mConfig = null;
		mMantisService = null;
	}

	@Test
	public void testNewIssue() {
		mMantisService.openConnect();
		
		IIssue story = new Issue();
		story.setIssueID(1);
		story.setSummary("Story_Name_One");
		story.setDescription("Story_Desc_One");
		story.setProjectID(mProject.getName());
		story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
		long storyId = mMantisService.newIssue(story);
		assertEquals(storyId, (long) 1);

		IIssue storyOne = mMantisService.getIssue(storyId);
		assertEquals(storyOne.getSummary(), "Story_Name_One");
		assertEquals(storyOne.getDescription(), "Story_Desc_One");
		assertEquals(storyOne.getProjectName(), mProject.getName());
		assertEquals(storyOne.getIssueType(), IssueTypeEnum.TYPE_STORY);

		// =====================================================================

		IIssue story_2 = new Issue();
		story_2.setIssueID(2);
		story_2.setSummary("Story_Name_Two");
		story_2.setDescription("Story_Desc_Two");
		story_2.setProjectID(mProject.getName());
		story_2.setCategory(ScrumEnum.STORY_ISSUE_TYPE);

		long storyId_2 = mMantisService.newIssue(story_2);
		assertEquals(storyId_2, (long) 2);

		IIssue storyTwo = mMantisService.getIssue(storyId_2);
		assertEquals(storyTwo.getSummary(), "Story_Name_Two");
		assertEquals(storyTwo.getDescription(), "Story_Desc_Two");
		assertEquals(storyTwo.getProjectName(), mProject.getName());
		assertEquals(storyTwo.getIssueType(), IssueTypeEnum.TYPE_STORY);

		mMantisService.closeConnect();
	}

	@Test
	public void testGetIssues_many_parameter() {
		mMantisService.openConnect();
		
		ArrayList<IIssue> stories = new ArrayList<IIssue>();
		// new 10 test data
		for (int i = 0; i < 10; i++) {
			IIssue story = new Issue();
			story.setIssueID(i + 1);
			story.setSummary("Story_Name_" + Integer.toString(i + 1));
			story.setDescription("Story_Desc_" + Integer.toString(i + 1));
			story.setProjectID(mProject.getName());
			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
			
			mMantisService.newIssue(story);
			stories.add(story);
		}

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		
		IIssue[] actualIssues = mMantisService.getIssues(mProject.getName(), ScrumEnum.STORY_ISSUE_TYPE, null, null, calendar.getTime());

		assertEquals(10, actualIssues.length);
		for (int i = 0; i < 10; i++) {
			IIssue expectedIssue = stories.get(i);
			assertEquals(expectedIssue.getEstimated(), actualIssues[i].getEstimated());
			assertEquals(expectedIssue.getHowToDemo(), actualIssues[i].getHowToDemo());
			assertEquals(expectedIssue.getImportance(), actualIssues[i].getImportance());
			assertEquals(expectedIssue.getNotes(), actualIssues[i].getNotes());
			assertEquals(expectedIssue.getProjectID(), actualIssues[i].getProjectID());
			assertEquals(expectedIssue.getSummary(), actualIssues[i].getSummary());
			assertEquals(expectedIssue.getIssueType(), IssueTypeEnum.TYPE_STORY);
		}
		mMantisService.closeConnect();
	}

	@Test
	public void testGetIssues_project() {
		mMantisService.openConnect();
		
		ArrayList<IIssue> stories = new ArrayList<IIssue>();
		for (int i = 0; i < 10; i++) {
			IIssue story = new Issue();
			story.setIssueID(i + 1);
			story.setSummary("Story_Name_" + Integer.toString(i + 1));
			story.setDescription("Story_Desc_" + Integer.toString(i + 1));
			story.setProjectID(mProject.getName());
			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);

			mMantisService.newIssue(story);
			stories.add(story);
		}

		IIssue[] actualIssues = mMantisService.getIssues(mProject.getName());

		assertEquals(10, actualIssues.length);
		for (int i = 0; i < 10; i++) {
			IIssue expectedIssue = stories.get(i);
			assertEquals(expectedIssue.getEstimated(), actualIssues[i].getEstimated());
			assertEquals(expectedIssue.getHowToDemo(), actualIssues[i].getHowToDemo());
			assertEquals(expectedIssue.getImportance(), actualIssues[i].getImportance());
			assertEquals(expectedIssue.getNotes(), actualIssues[i].getNotes());
			assertEquals(expectedIssue.getProjectID(), actualIssues[i].getProjectID());
			assertEquals(expectedIssue.getSummary(), actualIssues[i].getSummary());
			assertEquals(expectedIssue.getIssueType(), IssueTypeEnum.TYPE_STORY);
		}

		// create issue in not exist project
		for (int i = 10; i < 20; i++) {
			IIssue story = new Issue();
			story.setIssueID(i + 1);
			story.setSummary("Story_Name_" + Integer.toString(i + 1));
			story.setDescription("Story_Desc_" + Integer.toString(i + 1));
			story.setProjectID("NotExistedProject");
			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);

			mMantisService.newIssue(story);
			stories.add(story);
		}

		actualIssues = mMantisService.getIssues(mProject.getName());

		assertEquals(10, actualIssues.length);
		for (int i = 0; i < 10; i++) {
			IIssue expectedIssue = stories.get(i);
			assertEquals(expectedIssue.getEstimated(), actualIssues[i].getEstimated());
			assertEquals(expectedIssue.getHowToDemo(), actualIssues[i].getHowToDemo());
			assertEquals(expectedIssue.getImportance(), actualIssues[i].getImportance());
			assertEquals(expectedIssue.getNotes(), actualIssues[i].getNotes());
			assertEquals(expectedIssue.getProjectID(), actualIssues[i].getProjectID());
			assertEquals(expectedIssue.getSummary(), actualIssues[i].getSummary());
			assertEquals(expectedIssue.getIssueType(), IssueTypeEnum.TYPE_STORY);
		}
		mMantisService.closeConnect();
	}

	@Test
	public void testGetIssues_project_category() throws SQLException {
		mMantisService.openConnect();
		
		ArrayList<IIssue> stories = new ArrayList<IIssue>();
		for (int i = 0; i < 10; i++) {
			IIssue story = new Issue();
			story.setIssueID(i + 1);
			story.setSummary("Story_Name_" + Integer.toString(i + 1));
			story.setDescription("Story_Desc_" + Integer.toString(i + 1));
			story.setProjectID(mProject.getName());
			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);

			mMantisService.newIssue(story);
			stories.add(story);
		}

		// new 10 test task data
		for (int i = 10; i < 20; i++) {
			IIssue story = new Issue();
			story.setIssueID(i + 1);
			story.setSummary("Task_Name_" + Integer.toString(i + 1));
			story.setDescription("Task_Desc_" + Integer.toString(i + 1));
			story.setProjectID(mProject.getName());
			story.setCategory(ScrumEnum.TASK_ISSUE_TYPE);

			mMantisService.newIssue(story);
			stories.add(story);
		}

		IIssue[] actualIssues = mMantisService.getIssues(mProject.getName(), ScrumEnum.STORY_ISSUE_TYPE);

		assertEquals(10, actualIssues.length);
		for (int i = 0; i < 10; i++) {
			IIssue expectedIssue = stories.get(i);
			assertEquals(expectedIssue.getProjectID(), actualIssues[i].getProjectID());
			assertEquals(expectedIssue.getSummary(), actualIssues[i].getSummary());
			assertEquals(expectedIssue.getIssueType(), IssueTypeEnum.TYPE_STORY);
		}

		actualIssues = mMantisService.getIssues(mProject.getName(), ScrumEnum.TASK_ISSUE_TYPE);
		assertEquals(10, actualIssues.length);
		for (int i = 0; i < 10; i++) {
			IIssue expectedIssue = stories.get(i + 10);
			assertEquals(expectedIssue.getProjectID(), actualIssues[i].getProjectID());
			assertEquals(expectedIssue.getSummary(), actualIssues[i].getSummary());
			assertEquals(expectedIssue.getIssueType(), IssueTypeEnum.TYPE_TASK);
		}

		actualIssues = mMantisService.getIssues("UnExistedProject", ScrumEnum.TASK_ISSUE_TYPE);
		assertEquals(0, actualIssues.length);

		actualIssues = mMantisService.getIssues(mProject.getName(), "UnExistedCategory");
		assertEquals(0, actualIssues.length);

		mMantisService.closeConnect();
	}

	@Test
	public void testGetIssue_ID() {
		mMantisService.openConnect();
		
		IIssue story1 = new Issue();
		story1.setIssueID(1);
		story1.setSummary("Story_Name_One");
		story1.setDescription("Story_Desc_One");
		story1.setProjectID(mProject.getName());
		story1.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
		addTagElement(story1, "100", "10", "200", "demo", "note");
		long storyId1 = mMantisService.newIssue(story1);

		IIssue issue1 = mMantisService.getIssue(storyId1);
		assertEquals(1, issue1.getIssueID());
		assertEquals("Story_Name_One", issue1.getSummary());
		assertEquals("Story_Desc_One", issue1.getDescription());
		assertEquals("100", issue1.getImportance());
		assertEquals("10", issue1.getEstimated());
		assertEquals("200", issue1.getValue());
		assertEquals("demo", issue1.getHowToDemo());
		assertEquals("note", issue1.getNotes());

		IIssue error_issue = mMantisService.getIssue(-1);
		assertEquals(null, error_issue);

		mMantisService.closeConnect();
	}

	@Test
	public void testUpdateIssueContent() {
		mMantisService.openConnect();
		
		IIssue story = new Issue();
		story.setIssueID(1);
		story.setSummary("Story_Name_One");
		story.setDescription("Story_Desc_One");
		story.setAdditional("");
		story.setProjectID(mProject.getName());
		story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
		addTagElement(story, "100", "10", "200", "demo", "note");
		long storyId = mMantisService.newIssue(story);
		assertEquals(storyId, (long) 1);

		// change issue data
		story.setSummary("Story_Name_One_PartTwo");

		mMantisService.updateIssueContent(story);
		IIssue actualIssue = mMantisService.getIssue(1);
		assertEquals(story.getSummary(), actualIssue.getSummary());

		story.setDescription("Story_Desc_One_PartTwo");
		mMantisService.updateIssueContent(story);
		actualIssue = mMantisService.getIssue(1);
		assertEquals(story.getDescription(), actualIssue.getDescription());

		story.setAdditional("testAdditional");
		mMantisService.updateIssueContent(story);
		actualIssue = mMantisService.getIssue(1);
		assertEquals(story.getAdditional(), actualIssue.getAdditional());

		story.setCategory(ScrumEnum.TASK_ISSUE_TYPE);
		mMantisService.updateIssueContent(story);
		actualIssue = mMantisService.getIssue(1);
		assertEquals(story.getCategory(), actualIssue.getCategory());

		story.setAssignto("admin");
		mMantisService.updateIssueContent(story);
		actualIssue = mMantisService.getIssue(1);
		assertEquals(story.getAssignto(), actualIssue.getAssignto());

		mMantisService.closeConnect();
	}

	@Test
	public void testRemoveIssue() throws SQLException {
		mMantisService.openConnect();
		
		ArrayList<IIssue> issues = new ArrayList<IIssue>();
		// new 10 test story data
		for (int i = 0; i < 10; i++) {
			IIssue story = new Issue();
			story.setIssueID(i + 1);
			story.setSummary("Story_Name_" + Integer.toString(i + 1));
			story.setDescription("Story_Desc_" + Integer.toString(i + 1));
			story.setProjectID(mProject.getName());
			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
			addTagElement(story, Integer.toString((i + 1) * 5),
			        Integer.toString((i + 1) * 10),
			        Integer.toString((i + 1) * 15),
			        "Demo_" + Integer.toString(i + 1),
			        "Note_" + Integer.toString(i + 1));
			
			mMantisService.newIssue(story);
			issues.add(story);
		}

		mMantisService.removeIssue("1");
		issues.remove(0);
		ArrayList<IStory> actualList = mMantisService.getStorys(mProject.getName());
		assertEquals(9, actualList.size());
		assertEquals(0, HistoryDAO.getInstance().getHistoriesByIssue(1, IssueTypeEnum.TYPE_STORY).size());
		
		for (int i = 0; i < 9; i++) {
			IIssue expectedIssue = issues.get(i);
			IStory actualIssue = actualList.get(i);
			assertEquals(expectedIssue.getProjectID(), actualIssue.getProjectID());
			assertEquals(expectedIssue.getSummary(), actualIssue.getSummary());
			assertEquals(expectedIssue.getIssueType(), IssueTypeEnum.TYPE_STORY);
		}

		mMantisService.removeIssue("10");

		issues.remove(8);
		actualList = mMantisService.getStorys(mProject.getName());
		assertEquals(8, actualList.size());
		for (int i = 0; i < 8; i++) {
			IIssue expectedIssue = issues.get(i);
			IStory actualIssue = actualList.get(i);
			assertEquals(expectedIssue.getProjectID(), actualIssue.getProjectID());
			assertEquals(expectedIssue.getSummary(), actualIssue.getSummary());
			assertEquals(expectedIssue.getIssueType(), IssueTypeEnum.TYPE_STORY);
		}
		mMantisService.closeConnect();
	}

	@Test
	public void testGetStories() {
		mMantisService.openConnect();
		
		ArrayList<IIssue> issues = new ArrayList<IIssue>();
		// new 10 test story data
		for (int i = 0; i < 10; i++) {
			IIssue story = new Issue();
			story.setIssueID(i + 1);
			story.setSummary("Story_Name_" + Integer.toString(i + 1));
			story.setDescription("Story_Desc_" + Integer.toString(i + 1));
			story.setProjectID(mProject.getName());
			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
			addTagElement(story, Integer.toString((i + 1) * 5),
			        Integer.toString((i + 1) * 10),
			        Integer.toString((i + 1) * 15),
			        "Demo_" + Integer.toString(i + 1),
			        "Note_" + Integer.toString(i + 1));
			
			mMantisService.newIssue(story);
			issues.add(story);
		}

		// new 10 test task data
		for (int i = 10; i < 20; i++) {
			IIssue story = new Issue();
			story.setIssueID(i + 1);
			story.setSummary("Task_Name_" + Integer.toString(i + 1));
			story.setDescription("Task_Desc_" + Integer.toString(i + 1));
			story.setProjectID(mProject.getName());
			story.setCategory(ScrumEnum.TASK_ISSUE_TYPE);
			addTagElement(story, Integer.toString((i + 1) * 5),
			        Integer.toString((i + 1) * 10),
			        Integer.toString((i + 1) * 15),
			        "Demo_" + Integer.toString(i + 1),
			        "Note_" + Integer.toString(i + 1));
			
			mMantisService.newIssue(story);
			issues.add(story);
		}

		ArrayList<IStory> actualList = mMantisService.getStorys(mProject.getName());
		assertEquals(10, actualList.size());
		
		for (int i = 0; i < 10; i++) {
			IIssue expectedIssue = issues.get(i);
			IStory actualIssue = actualList.get(i);
			assertEquals(expectedIssue.getProjectID(), actualIssue.getProjectID());
			assertEquals(expectedIssue.getSummary(), actualIssue.getSummary());
			assertEquals(expectedIssue.getIssueType(), IssueTypeEnum.TYPE_STORY);
			assertEquals(expectedIssue.getImportance(), actualIssue.getImportance());
			assertEquals(expectedIssue.getEstimated(), actualIssue.getEstimated());
			assertEquals(expectedIssue.getValue(), actualIssue.getValue());
			assertEquals(expectedIssue.getHowToDemo(), actualIssue.getHowToDemo());
			assertEquals(expectedIssue.getNotes(), actualIssue.getNotes());
		}
		mMantisService.closeConnect();
	}

	private void addTagElement(IIssue issue, String imp, String est, String value, String howtodemo, String notes) {
		Element history = new Element(ScrumEnum.HISTORY_TAG);
		history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, DateUtil.format(new Date(), DateUtil._16DIGIT_DATE_TIME_2));

		if (imp != null && !imp.equals("")) {
			Element importanceElem = new Element(ScrumEnum.IMPORTANCE);
			int temp = (int) Float.parseFloat(imp);
			importanceElem.setText(temp + "");
			history.addContent(importanceElem);
		}

		if (est != null && !est.equals("")) {
			Element storyPoint = new Element(ScrumEnum.ESTIMATION);
			storyPoint.setText(est);
			history.addContent(storyPoint);
		}

		if (value != null && !value.equals("")) {
			Element customValue = new Element(ScrumEnum.VALUE);
			customValue.setText(value);
			history.addContent(customValue);
		}

		Element howToDemoElem = new Element(ScrumEnum.HOWTODEMO);
		howToDemoElem.setText(howtodemo);
		history.addContent(howToDemoElem);

		Element notesElem = new Element(ScrumEnum.NOTES);
		notesElem.setText(notes);
		history.addContent(notesElem);

		if (history.getChildren().size() > 0) {
			issue.addTagValue(history);
			mMantisService.updateBugNote(issue);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// ===========================================================
	// ====================== for tagService =====================
	// ===========================================================
	// 測試對象: getIssues(String projectName, String category, String releaseID, String sprintID, Date date)
	@Test
	public void testInitTag_many_parameter() {
		mMantisService.openConnect();
		
		ArrayList<IIssue> issueList = new ArrayList<IIssue>();
		int dataCount = 10;
		CreateTag CT = new CreateTag(dataCount, mCP);
		CT.exe();
		// new 10 test story data
		for (int listIndex = 0; listIndex < dataCount; listIndex++) {
			IIssue story = new Issue();
			story.setIssueID(listIndex + 1);
			story.setSummary("Story_Name_" + Integer.toString(listIndex + 1));
			story.setProjectID(mProject.getName());
			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);

			mMantisService.newIssue(story);
			issueList.add(story);

			for (int tagIndex = 0; tagIndex < dataCount; tagIndex++) {
				// attach tag to story
				mMantisService.addStoryTag(String.valueOf(listIndex + 1), tagIndex + 1);
			}
		}

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);

		IIssue[] issueSet = mMantisService.getIssues(mProject.getName(), "ScrumEnum.STORY_ISSUE_TYPE", null, null, calendar.getTime());

		// assert the storyTags' id, name correct or not.
		for (int issueIndex = 0; issueIndex < issueSet.length; issueIndex++) {
			for (int tagIndex = 0; tagIndex < dataCount; tagIndex++) {
				TagObject actualTag = issueSet[issueIndex].getTags().get(tagIndex);
				assertEquals((long) (tagIndex + 1), actualTag.getId());
				assertEquals("Test_TAG_" + Integer.toString(tagIndex), actualTag.getName());
			}
		}
		mMantisService.closeConnect();
	}

	// 測試對象: getIssues(String projectName, String category)
	@Test
	public void testInitTag_projectName_category() throws SQLException {
		mMantisService.openConnect();
		
		ArrayList<IIssue> issueList = new ArrayList<IIssue>();
		int dataCount = 10;
		CreateTag CT = new CreateTag(dataCount, mCP);
		CT.exe();
		// new 10 test story data
		for (int listIndex = 0; listIndex < dataCount; listIndex++) {
			IIssue story = new Issue();
			story.setIssueID(listIndex + 1);
			story.setSummary("Story_Name_" + Integer.toString(listIndex + 1));
			story.setProjectID(mProject.getName());
			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);

			mMantisService.newIssue(story);
			issueList.add(story);

			for (int tagIndex = 0; tagIndex < dataCount; tagIndex++) {
				// attach tag to story
				mMantisService.addStoryTag(String.valueOf(listIndex + 1), tagIndex + 1);
			}
		}

		IIssue[] issueSet = mMantisService.getIssues(mProject.getName(), "ScrumEnum.STORY_ISSUE_TYPE");

		// assert the storyTags' id, name correct or not.
		for (int issueIndex = 0; issueIndex < issueSet.length; issueIndex++) {
			for (int tagIndex = 0; tagIndex < dataCount; tagIndex++) {
				TagObject actualTag = issueSet[issueIndex].getTags().get(tagIndex);
				assertEquals((long) (tagIndex + 1), actualTag.getId());
				assertEquals("TEST_TAG_" + Integer.toString(tagIndex + 1), actualTag.getName());
			}
		}
		mMantisService.closeConnect();
	}

	// 測試對象: getIssue(long issueID)
	@Test
	public void testInitTag_ID() {
		mMantisService.openConnect();
		
		ArrayList<IIssue> issueList = new ArrayList<IIssue>();
		int dataCount = 10;
		CreateTag CT = new CreateTag(dataCount, mCP);
		CT.exe();
		// new 10 test story data
		for (int listIndex = 0; listIndex < dataCount; listIndex++) {
			IIssue story = new Issue();
			story.setIssueID(listIndex + 1);
			story.setSummary("Story_Name_" + Integer.toString(listIndex + 1));
			story.setProjectID(mProject.getName());
			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);

			mMantisService.newIssue(story);
			issueList.add(story);

			for (int tagIndex = 0; tagIndex < dataCount; tagIndex++) {
				// attach tag to story
				mMantisService.addStoryTag(String.valueOf(listIndex + 1), tagIndex + 1);
			}
		}

		// assert the storyTags' id, name correct or not.
		for (int issueIndex = 0; issueIndex < dataCount; issueIndex++) {
			IIssue issue = mMantisService.getIssue((long) (issueIndex + 1));
			for (int tagIndex = 0; tagIndex < dataCount; tagIndex++) {
				TagObject actualTag = issue.getTags().get(tagIndex);
				assertEquals((long) (tagIndex + 1), actualTag.getId());
				assertEquals("TEST_TAG_" + Integer.toString(tagIndex + 1), actualTag.getName());
			}
		}
		mMantisService.closeConnect();
	}

	// 測試對象: getStorys(String projectName)
	@Test
	public void testInitTag_projectName() {
		mMantisService.openConnect();
		
		ArrayList<IIssue> issueList = new ArrayList<IIssue>();
		int dataCount = 10;
		CreateTag CT = new CreateTag(dataCount, mCP);
		CT.exe();
		// new 10 test story data
		for (int listIndex = 0; listIndex < dataCount; listIndex++) {
			IIssue story = new Issue();
			story.setIssueID(listIndex + 1);
			story.setSummary("Story_Name_" + Integer.toString(listIndex + 1));
			story.setProjectID(mProject.getName());
			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);

			mMantisService.newIssue(story);
			issueList.add(story);

			for (int tagIndex = 0; tagIndex < dataCount; tagIndex++) {
				// attach tag to story
				mMantisService.addStoryTag(String.valueOf(listIndex + 1), tagIndex + 1);
			}
		}

		ArrayList<IStory> stories = mMantisService.getStorys(mProject.getName());

		// assert the storyTags' id, name correct or not.
		for (int i = 0; i < stories.size(); i++) {
			for (int tagIndex = 0; tagIndex < dataCount; tagIndex++) {
				TagObject actualTag = stories.get(i).getTags().get(tagIndex);
				assertEquals((long) (tagIndex + 1), actualTag.getId());
				assertEquals("TEST_TAG_" + Integer.toString(tagIndex + 1), actualTag.getName());
			}
		}
		mMantisService.closeConnect();
	}

	// 測試對象: deleteStory(String ID)
	@Test
	public void testRemoveStoryTag_story() {
		mMantisService.openConnect();
		
		ArrayList<IIssue> issueList = new ArrayList<IIssue>();
		int dataCount = 10;
		CreateTag CT = new CreateTag(dataCount, mCP);
		CT.exe();
		// new 10 test story data
		for (int listIndex = 0; listIndex < dataCount; listIndex++) {
			IIssue story = new Issue();
			story.setIssueID(listIndex + 1);
			story.setSummary("Story_Name_" + Integer.toString(listIndex + 1));
			story.setProjectID(mProject.getName());
			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);

			mMantisService.newIssue(story);
			issueList.add(story);

			for (int i = 0; i < dataCount; i++) {
				// attach tag to story
				mMantisService.addStoryTag(String.valueOf(listIndex + 1), i + 1);
			}
		}

		// check tag 筆數正確與否
		IIssue[] issueSet = mMantisService.getIssues(mProject.getName());
		assertEquals(dataCount, issueSet.length);

		// check story 有正確delete
		for (int i = 0; i < issueList.size(); i++) {
			mMantisService.deleteStory(Long.toString(issueList.get(i).getIssueID()));
			issueSet = mMantisService.getIssues(mProject.getName());
			assertEquals(dataCount - (i + 1), issueSet.length);
		}

		// assert no story exist
		String query = "SELECT * FROM `mantis_bug_table`";
		ResultSet result = mMantisService.getControl().executeQuery(query);
		try {
			assertTrue(!result.next());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// assert the relation between tag and story has been gone.
		query = "SELECT * FROM `ezscrum_tag_relation`";
		result = mMantisService.getControl().executeQuery(query);
		try {
			assertTrue(!result.next());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		mMantisService.closeConnect();
	}

	// 測試對象: removeStoryTag(String storyID, String tagID)
	@Test
	public void testRemoveStoryTag_storyTag() {
		mMantisService.openConnect();
		
		ArrayList<IIssue> issueList = new ArrayList<IIssue>();
		int dataCount = 10;
		CreateTag CT = new CreateTag(dataCount, mCP);
		CT.exe();
		// new 10 test story data
		for (int listIndex = 0; listIndex < dataCount; listIndex++) {
			IIssue story = new Issue();
			story.setIssueID(listIndex + 1);
			story.setSummary("Story_Name_" + Integer.toString(listIndex + 1));
			story.setProjectID(mProject.getName());
			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);

			mMantisService.newIssue(story);
			issueList.add(story);

			for (int tagIndex = 0; tagIndex < dataCount; tagIndex++) {
				// attach tag to story
				mMantisService.addStoryTag(String.valueOf(listIndex + 1), tagIndex + 1);
			}
		}
		// check tag 筆數正確
		ArrayList<TagObject> tagList = mMantisService.getTagList(mProject.getName());
		assertEquals(dataCount, tagList.size());

		// remove tags which attach to story
		for (int issueIndex = 0; issueIndex < dataCount; issueIndex++) {
			for (int tagIndex = 0; tagIndex < dataCount; tagIndex++) {
				mMantisService.removeStoryTag(Integer.toString(issueIndex + 1), tagIndex + 1);
			}
		}

		// check relation table 中找不到任何 relation
		String query = "SELECT * FROM `ezscrum_tag_relation`";
		ResultSet result = mMantisService.getControl().executeQuery(query);
		try {
			assertTrue(!result.next());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		mMantisService.closeConnect();
	}

	// test add new tag in project
	@Test
	public void testAddNewTag() {
		mMantisService.openConnect();
		
		int tagCount = 10;
		String tagName = "TEST_TAG_";
		// check add tag 之前，tag list 為空
		ArrayList<TagObject> tagList = mMantisService.getTagList(mProject.getName());
		assertEquals(0, tagList.size());

		// new 10 test tag data
		for (int tagIndex = 0; tagIndex < tagCount; tagIndex++) {
			mMantisService.addNewTag(tagName + Integer.toString(tagIndex + 1), mProject.getName());
		}

		// check add tag 之後，tag 筆數正確
		tagList = mMantisService.getTagList(mProject.getName());
		assertEquals(tagCount, tagList.size());

		// check tag info. 正確
		for (int tagIndex = 0; tagIndex < tagCount; tagIndex++) {
			assertEquals((long) (tagIndex + 1), tagList.get(tagIndex).getId());
			assertEquals(tagName + Integer.toString(tagIndex + 1), tagList.get(tagIndex).getName());
		}
		mMantisService.closeConnect();
	}

	@Test
	public void testDeleteTag() {
		mMantisService.openConnect();

		int tagCount = 10;
		String tagName = "TEST_TAG_";
		
		for (int tagIndex = 0; tagIndex < tagCount; tagIndex++) {
			mMantisService.addNewTag(tagName + Integer.toString(tagIndex + 1), mProject.getName());
		}
		// check add tag 之後，tag list 筆數正確
		ArrayList<TagObject> tagList = mMantisService.getTagList(mProject.getName());
		assertEquals(tagCount, tagList.size());

		// delete tag for each
		for (int tagIndex = 0; tagIndex < tagCount; tagIndex++) {
			mMantisService.deleteTag(tagIndex + 1, mProject.getName());
			tagList = mMantisService.getTagList(mProject.getName());
			// check tag list size
			assertEquals(tagCount - (tagIndex + 1), tagList.size());
		}

		// check resultSet 中找不到 delete 掉的 tag
		String query = "SELECT * FROM `ezscrum_tag_table`";
		ResultSet result = mMantisService.getControl().executeQuery(query);
		try {
			assertTrue(!result.next());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		mMantisService.closeConnect();
	}

	@Test
	public void testGetTagList() {
		mMantisService.openConnect();
		
		// check add tag 之前, tag 數為0
		ArrayList<TagObject> tagList = mMantisService.getTagList(mProject.getName());
		assertEquals(0, tagList.size());

		// new 10 test tag data
		int tagCount = 10;
		String tagName = "TEST_TAG_";
		for (int tagIndex = 0; tagIndex < tagCount; tagIndex++) {
			mMantisService.addNewTag(tagName + Integer.toString(tagIndex + 1), mProject.getName());
		}

		// check add tag 後, 數量正確
		tagList = mMantisService.getTagList(mProject.getName());
		assertEquals(tagCount, tagList.size());

		// check tag Info. 正確與否
		for (int tagIndex = 0; tagIndex < tagCount; tagIndex++) {
			assertEquals((long) (tagIndex + 1), tagList.get(tagIndex).getId());
			assertEquals(tagName + Integer.toString(tagIndex + 1), tagList.get(tagIndex).getName());
		}
		mMantisService.closeConnect();
	}

	@Test
	public void testAddStoryTag() {
		mMantisService.openConnect();
		
		ArrayList<IIssue> issueList = new ArrayList<IIssue>();
		int dataCount = 10;
		CreateTag CT = new CreateTag(dataCount, mCP);
		CT.exe();
		// new 10 test story data
		for (int listIndex = 0; listIndex < dataCount; listIndex++) {
			IIssue story = new Issue();
			story.setIssueID(listIndex + 1);
			story.setSummary("Story_Name_" + Integer.toString(listIndex + 1));
			story.setProjectID(mProject.getName());
			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);

			mMantisService.newIssue(story);
			issueList.add(story);

			for (int tagIndex = 0; tagIndex < dataCount; tagIndex++) {
				// attach tag to story
				mMantisService.addStoryTag(String.valueOf(listIndex + 1), tagIndex + 1);
			}
		}

		// check tag 有正確 attach to story
		for (int issueIndex = 0; issueIndex < dataCount; issueIndex++) {
			// check the relation between story & tag
			String query = "SELECT * FROM `ezscrum_tag_relation` WHERE STORY_ID = " + (issueIndex + 1);
			ResultSet result = mMantisService.getControl().executeQuery(query);
			int tagIndex = 0;
			try {
				while (result.next()) {
					assertEquals(tagIndex + 1, result.getInt("ezscrum_tag_relation.tag_id"));
					tagIndex++;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		mMantisService.closeConnect();
	}

	@Test
	public void testUpdateTag() {
		mMantisService.openConnect();
		
		int tagCount = 10;
		String tagName = "TEST_TAG_";
		for (int tagIndex = 0; tagIndex < tagCount; tagIndex++) {
			mMantisService.addNewTag(tagName + Integer.toString(tagIndex + 1), mProject.getName());
		}

		// update tag name. service 作法為 delete 舊的 tag, 新增一筆update name 的 tag
		for (int tagIndex = 0; tagIndex < tagCount; tagIndex++) {
			mMantisService.updateTag(tagIndex + 1,
			        "UPDATE_TAG_" + Integer.toString(tagIndex + 1), mProject.getName());
		}

		// check update tag 後, 筆數正確
		ArrayList<TagObject> tagList = mMantisService.getTagList(mProject.getName());
		assertEquals(tagCount, tagList.size());

		// check update tag 後, tag info. correct or not.
		for (int tagIndex = 10; tagIndex < tagCount; tagIndex++) {
			assertEquals((long) (tagIndex + 1), tagList.get(tagIndex).getId());
			assertEquals("UPDATE_TAG_" + Integer.toString(tagIndex + 1), tagList.get(tagIndex).getName());
		}
		mMantisService.closeConnect();
	}

	@Test
	public void testGetTagByName() {
		mMantisService.openConnect();
		
		int tagCount = 10;
		String tagName = "TEST_TAG_";
		for (int tagIndex = 0; tagIndex < tagCount; tagIndex++) {
			mMantisService.addNewTag(tagName + Integer.toString(tagIndex + 1), mProject.getName());
		}

		TagObject issueTag;
		for (int tagIndex = 0; tagIndex < tagCount; tagIndex++) {
			String expectedTagName = tagName + Integer.toString(tagIndex + 1);
			issueTag = mMantisService.getTagByName(expectedTagName, mProject.getName());
			// check get 的 tag info. 正確與否
			assertEquals((long) (tagIndex + 1), issueTag.getId());
			assertEquals(expectedTagName, issueTag.getName());
		}
		mMantisService.closeConnect();
	}

	@Test
	public void testIsTagExist() {
		mMantisService.openConnect();

		int tagCount = 10;
		String tagName = "TEST_TAG_";
		for (int tagIndex = 0; tagIndex < tagCount; tagIndex++) {
			mMantisService.addNewTag(tagName + Integer.toString(tagIndex + 1), mProject.getName());
		}

		// check tag exist
		for (int tagIndex = 0; tagIndex < tagCount; tagIndex++) {
			String expectedTagName = tagName + Integer.toString(tagIndex + 1);
			assertTrue(mMantisService.isTagExist(expectedTagName, mProject.getName()));
		}
		mMantisService.closeConnect();
	}
	
	@Test
	public void testGetIssues() {
		mMantisService.openConnect();
		
		ArrayList<IIssue> stories = new ArrayList<IIssue>();
		
		for (int i = 0; i < 5; i++) {
			IIssue story = new Issue();
			story.setIssueID(i + 1);
			story.setSummary("Story_Name_" + i + 1);
			story.setDescription("Story_Desc_" + i + 1);
			story.setProjectID(mProject.getName());
			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
			
			mMantisService.newIssue(story);
			stories.add(story);
		}

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		IIssue[] issues = mMantisService.getIssues(mProject.getName(), ScrumEnum.STORY_ISSUE_TYPE, null, null, calendar.getTime());

		for (int i = 0; i < 5; i++) {
			IIssue actualIssue = stories.get(i);
			IIssue expectedIssue = issues[i];
			assertEquals(actualIssue.getEstimated(), expectedIssue.getEstimated());
			assertEquals(actualIssue.getHowToDemo(), expectedIssue.getHowToDemo());
			assertEquals(actualIssue.getImportance(), expectedIssue.getImportance());
			assertEquals(actualIssue.getNotes(), expectedIssue.getNotes());
			assertEquals(actualIssue.getProjectID(), expectedIssue.getProjectID());
			assertEquals(actualIssue.getSummary(), expectedIssue.getSummary());
			assertEquals(IssueTypeEnum.TYPE_STORY, expectedIssue.getIssueType());
		}
		mMantisService.closeConnect();
	}

	@Test
	public void testAddRelationship_history_1() throws SQLException {
		mMantisService.openConnect();
		
		IIssue story = new Issue();
		story.setIssueID(1);
		story.setSummary("Story_Name_One");
		story.setDescription("Story_Desc_One");
		story.setProjectID(mProject.getName());
		story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
		long storyId = mMantisService.newIssue(story);
		assertEquals(storyId, (long) 1);

		IIssue storyOne = mMantisService.getIssue(storyId);
		assertEquals(storyOne.getSummary(), "Story_Name_One");
		assertEquals(storyOne.getDescription(), "Story_Desc_One");
		assertEquals(storyOne.getIssueType(), IssueTypeEnum.TYPE_STORY);

		ArrayList<HistoryObject> histories = storyOne.getHistories();
		assertEquals(histories.get(0).getDescription(), "Create Story #" + storyId);
		assertEquals(histories.get(0).getIssueId(), storyId);
		assertEquals(histories.get(0).getIssueType(), IssueTypeEnum.TYPE_STORY);
		assertEquals(histories.get(0).getNewValue(), "");
		assertEquals(histories.get(0).getOldValue(), "");
		assertEquals(histories.get(0).getHistoryType(), HistoryObject.TYPE_CREATE);

		IIssue task = new Issue();
		task.setIssueID(2);
		task.setSummary("Task_Name_One");
		task.setDescription("Task_Desc_One");
		task.setProjectID(mProject.getName());
		task.setCategory(ScrumEnum.TASK_ISSUE_TYPE);
		long taskId = mMantisService.newIssue(task);
		assertEquals(taskId, (long) 2);

		IIssue taskOne = mMantisService.getIssue(taskId);
		assertEquals(taskOne.getSummary(), "Task_Name_One");
		assertEquals(taskOne.getDescription(), "Task_Desc_One");
		assertEquals(taskOne.getIssueType(), IssueTypeEnum.TYPE_TASK);

		histories = taskOne.getHistories();
		assertEquals(histories.get(0).getDescription(), "Create Task #" + taskId);
		assertEquals(histories.get(0).getIssueId(), taskId);
		assertEquals(histories.get(0).getIssueType(), IssueTypeEnum.TYPE_TASK);
		assertEquals(histories.get(0).getNewValue(), "");
		assertEquals(histories.get(0).getOldValue(), "");
		assertEquals(histories.get(0).getHistoryType(), HistoryObject.TYPE_CREATE);

		Date date = new Date();
		date.setTime(date.getTime() + 1000);
		
		mMantisService.addRelationship(storyId, taskId, ITSEnum.PARENT_RELATIONSHIP, date);

		storyOne = mMantisService.getIssue(storyId);
		assertEquals(storyOne.getSummary(), "Story_Name_One");
		assertEquals(storyOne.getDescription(), "Story_Desc_One");
		assertEquals(storyOne.getIssueType(), IssueTypeEnum.TYPE_STORY);

		histories = storyOne.getHistories();
		assertEquals(histories.get(0).getDescription(), "Create Story #" + storyId);
		assertEquals(histories.get(0).getNewValue(), "");
		assertEquals(histories.get(0).getOldValue(), "");
		assertEquals(histories.get(0).getHistoryType(), HistoryObject.TYPE_CREATE);
		
		assertEquals(histories.get(1).getDescription(), "Add Task #" + taskId);
		assertEquals(histories.get(1).getNewValue(), String.valueOf(taskId));
		assertEquals(histories.get(1).getOldValue(), "");
		assertEquals(histories.get(1).getHistoryType(), HistoryObject.TYPE_ADD);

		taskOne = mMantisService.getIssue(taskId);
		assertEquals(taskOne.getSummary(), "Task_Name_One");
		assertEquals(taskOne.getDescription(), "Task_Desc_One");
		assertEquals(taskOne.getIssueType(), IssueTypeEnum.TYPE_TASK);
		
		histories = taskOne.getHistories();
		assertEquals(histories.get(0).getDescription(), "Create Task #" + taskId);
		assertEquals(histories.get(0).getNewValue(), "");
		assertEquals(histories.get(0).getOldValue(), "");
		assertEquals(histories.get(0).getHistoryType(), HistoryObject.TYPE_CREATE);
		
		assertEquals(histories.get(1).getDescription(), "Append to Story #" + storyId);
		assertEquals(histories.get(1).getNewValue(), String.valueOf(storyId));
		assertEquals(histories.get(1).getOldValue(), "");
		assertEquals(histories.get(1).getHistoryType(), HistoryObject.TYPE_APPEND);

		mMantisService.closeConnect();
	}

	@Test
	public void testAddRelationship_history_2() throws SQLException {
		mMantisService.openConnect();
		
		IIssue story = new Issue();
		story.setIssueID(1);
		story.setSummary("Story_Name_One");
		story.setDescription("Story_Desc_One");
		story.setProjectID(mProject.getName());
		story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
		long storyId = mMantisService.newIssue(story);
		assertEquals(storyId, (long) 1);

		IIssue storyOne = mMantisService.getIssue(storyId);
		assertEquals(storyOne.getSummary(), "Story_Name_One");
		assertEquals(storyOne.getDescription(), "Story_Desc_One");
		assertEquals(storyOne.getIssueType(), IssueTypeEnum.TYPE_STORY);

		ArrayList<HistoryObject> histories = storyOne.getHistories();
		assertEquals(histories.size(), 1);
		assertEquals(histories.get(0).getDescription(), "Create Story #" + storyId);
		assertEquals(histories.get(0).getNewValue(), "");
		assertEquals(histories.get(0).getOldValue(), "");
		assertEquals(histories.get(0).getHistoryType(), HistoryObject.TYPE_CREATE);

		IIssue task = new Issue();
		task.setIssueID(2);
		task.setSummary("Task_Name_One");
		task.setDescription("Task_Desc_One");
		task.setProjectID(mProject.getName());
		task.setCategory(ScrumEnum.TASK_ISSUE_TYPE);
		long taskId = mMantisService.newIssue(task);
		assertEquals(taskId, (long) 2);

		IIssue taskOne = mMantisService.getIssue(taskId);
		assertEquals(taskOne.getSummary(), "Task_Name_One");
		assertEquals(taskOne.getDescription(), "Task_Desc_One");
		assertEquals(taskOne.getIssueType(), IssueTypeEnum.TYPE_TASK);

		histories = taskOne.getHistories();
		assertEquals(histories.size(), 1);
		assertEquals(histories.get(0).getDescription(), "Create Task #" + taskId);
		assertEquals(histories.get(0).getNewValue(), "");
		assertEquals(histories.get(0).getOldValue(), "");
		assertEquals(histories.get(0).getHistoryType(), HistoryObject.TYPE_CREATE);

		Date date = new Date();
		date.setTime(date.getTime() + 1000);
		mMantisService.addRelationship(storyId, taskId, 5, date);

		storyOne = mMantisService.getIssue(storyId);
		assertEquals(storyOne.getSummary(), "Story_Name_One");
		assertEquals(storyOne.getDescription(), "Story_Desc_One");
		assertEquals(storyOne.getIssueType(), IssueTypeEnum.TYPE_STORY);

		histories = storyOne.getHistories();
		assertEquals(histories.size(), 1);
		assertEquals(histories.get(0).getDescription(), "Create Story #" + storyId);
		assertEquals(histories.get(0).getNewValue(), "");
		assertEquals(histories.get(0).getOldValue(), "");
		assertEquals(histories.get(0).getHistoryType(), HistoryObject.TYPE_CREATE);

		mMantisService.closeConnect();
	}

	@Test
	public void testRemoveRelationship_history() throws SQLException {
		mMantisService.openConnect();
		
		IIssue story = new Issue();
		story.setIssueID(1);
		story.setSummary("Story_Name_One");
		story.setDescription("Story_Desc_One");
		story.setProjectID(mProject.getName());
		story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
		long storyId = mMantisService.newIssue(story);
		assertEquals(storyId, (long) 1);

		IIssue storyOne = mMantisService.getIssue(storyId);
		assertEquals(storyOne.getSummary(), "Story_Name_One");
		assertEquals(storyOne.getDescription(), "Story_Desc_One");
		assertEquals(storyOne.getIssueType(), IssueTypeEnum.TYPE_STORY);

		ArrayList<HistoryObject> histories = storyOne.getHistories();
		assertEquals(histories.get(0).getDescription(), "Create Story #" + storyId);
		assertEquals(histories.get(0).getNewValue(), "");
		assertEquals(histories.get(0).getOldValue(), "");
		assertEquals(histories.get(0).getHistoryType(), HistoryObject.TYPE_CREATE);

		IIssue task = new Issue();
		task.setIssueID(2);
		task.setSummary("Task_Name_One");
		task.setDescription("Task_Desc_One");
		task.setProjectID(mProject.getName());
		task.setCategory(ScrumEnum.TASK_ISSUE_TYPE);
		long taskId = mMantisService.newIssue(task);
		assertEquals(taskId, (long) 2);

		IIssue taskOne = mMantisService.getIssue(taskId);
		assertEquals(taskOne.getSummary(), "Task_Name_One");
		assertEquals(taskOne.getDescription(), "Task_Desc_One");
		assertEquals(taskOne.getIssueType(), IssueTypeEnum.TYPE_TASK);

		histories = taskOne.getHistories();
		assertEquals(histories.get(0).getDescription(), "Create Task #" + taskId);
		assertEquals(histories.get(0).getNewValue(), "");
		assertEquals(histories.get(0).getOldValue(), "");
		assertEquals(histories.get(0).getHistoryType(), HistoryObject.TYPE_CREATE);

		Date date = new Date();
		date.setTime(date.getTime() + 1000);
		mMantisService.addRelationship(storyId, taskId, ITSEnum.PARENT_RELATIONSHIP, date);

		storyOne = mMantisService.getIssue(storyId);
		assertEquals(storyOne.getSummary(), "Story_Name_One");
		assertEquals(storyOne.getDescription(), "Story_Desc_One");
		assertEquals(storyOne.getIssueType(), IssueTypeEnum.TYPE_STORY);

		histories = storyOne.getHistories();
		assertEquals(histories.get(0).getDescription(), "Create Story #" + storyId);
		assertEquals(histories.get(0).getNewValue(), "");
		assertEquals(histories.get(0).getOldValue(), "");
		assertEquals(histories.get(0).getHistoryType(), HistoryObject.TYPE_CREATE);
		
		assertEquals(histories.get(1).getDescription(), "Add Task #" + taskId);
		assertEquals(histories.get(1).getNewValue(), String.valueOf(taskId));
		assertEquals(histories.get(1).getOldValue(), "");
		assertEquals(histories.get(1).getHistoryType(), HistoryObject.TYPE_ADD);
		
		taskOne = mMantisService.getIssue(taskId);
		assertEquals(taskOne.getSummary(), "Task_Name_One");
		assertEquals(taskOne.getDescription(), "Task_Desc_One");
		assertEquals(taskOne.getIssueType(), IssueTypeEnum.TYPE_TASK);
		
		histories = taskOne.getHistories();
		assertEquals(histories.get(0).getDescription(), "Create Task #" + taskId);
		assertEquals(histories.get(0).getNewValue(), "");
		assertEquals(histories.get(0).getOldValue(), "");
		assertEquals(histories.get(0).getHistoryType(), HistoryObject.TYPE_CREATE);
		
		assertEquals(histories.get(1).getDescription(), "Append to Story #" + storyId);
		assertEquals(histories.get(1).getNewValue(), String.valueOf(storyId));
		assertEquals(histories.get(1).getOldValue(), "");
		assertEquals(histories.get(1).getHistoryType(), HistoryObject.TYPE_APPEND);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mMantisService.removeRelationship(storyId, taskId, ITSEnum.PARENT_RELATIONSHIP);

		storyOne = mMantisService.getIssue(storyId);
		assertEquals(storyOne.getSummary(), "Story_Name_One");
		assertEquals(storyOne.getDescription(), "Story_Desc_One");
		assertEquals(storyOne.getIssueType(), IssueTypeEnum.TYPE_STORY);
		
		histories = storyOne.getHistories();
		assertEquals(histories.get(0).getDescription(), "Create Story #" + storyId);
		assertEquals(histories.get(0).getNewValue(), "");
		assertEquals(histories.get(0).getOldValue(), "");
		assertEquals(histories.get(0).getHistoryType(), HistoryObject.TYPE_CREATE);
		
		assertEquals(histories.get(1).getDescription(), "Add Task #" + taskId);
		assertEquals(histories.get(1).getNewValue(), String.valueOf(taskId));
		assertEquals(histories.get(1).getOldValue(), "");
		assertEquals(histories.get(1).getHistoryType(), HistoryObject.TYPE_ADD);
		
		assertEquals(histories.get(2).getDescription(), "Drop Task #" + taskId);
		assertEquals(histories.get(2).getNewValue(), String.valueOf(taskId));
		assertEquals(histories.get(2).getOldValue(), "");
		assertEquals(histories.get(2).getHistoryType(), HistoryObject.TYPE_DROP);

		taskOne = mMantisService.getIssue(taskId);
		assertEquals(taskOne.getSummary(), "Task_Name_One");
		assertEquals(taskOne.getDescription(), "Task_Desc_One");
		assertEquals(taskOne.getIssueType(), IssueTypeEnum.TYPE_TASK);
		
		histories = taskOne.getHistories();
		assertEquals(histories.get(0).getDescription(), "Create Task #" + taskId);
		assertEquals(histories.get(0).getNewValue(), "");
		assertEquals(histories.get(0).getOldValue(), "");
		assertEquals(histories.get(0).getHistoryType(), HistoryObject.TYPE_CREATE);
		
		assertEquals(histories.get(1).getDescription(), "Append to Story #" + storyId);
		assertEquals(histories.get(1).getNewValue(), String.valueOf(storyId));
		assertEquals(histories.get(1).getOldValue(), "");
		assertEquals(histories.get(1).getHistoryType(), HistoryObject.TYPE_APPEND);
		
		assertEquals(histories.get(2).getDescription(), "Remove from Story #" + storyId);
		assertEquals(histories.get(2).getNewValue(), String.valueOf(storyId));
		assertEquals(histories.get(2).getOldValue(), "");
		assertEquals(histories.get(2).getHistoryType(), HistoryObject.TYPE_REMOVE);

		mMantisService.closeConnect();
	}

	@Test
	public void testUpdateName_history() throws SQLException {
		IIssue task = new Issue();
		task.setIssueID(1);
		task.setSummary("Task_Name_One");
		task.setDescription("Task_Desc_One");
		task.setProjectID(mCP.getProjectList().get(0).getName());
		task.setCategory(ScrumEnum.TASK_ISSUE_TYPE);

		// open connection
		mMantisService.openConnect();

		long taskID = mMantisService.newIssue(task);
		assertEquals(taskID, (long) 1);

		IIssue taskOne = mMantisService.getIssue(taskID);
		assertEquals(taskOne.getSummary(), "Task_Name_One");
		assertEquals(taskOne.getDescription(), "Task_Desc_One");
		assertEquals(taskOne.getCategory(), ScrumEnum.TASK_ISSUE_TYPE);

		List<HistoryObject> histories = taskOne.getHistories();
		assertEquals(histories.get(0).getDescription(), "Create Task #1");
		assertEquals(histories.get(0).getNewValue(), "");
		assertEquals(histories.get(0).getOldValue(), "");
		assertEquals(histories.get(0).getHistoryType(), HistoryObject.TYPE_CREATE);

		Date d = new Date();
		d.setTime(d.getTime() + 1000);
		// test method
		mMantisService.updateName(task, "NEW_NAME", new Date());
		// test method

		taskOne = mMantisService.getIssue(taskID);
		assertEquals(taskOne.getSummary(), "NEW_NAME");
		assertEquals(taskOne.getDescription(), "Task_Desc_One");
		assertEquals(taskOne.getCategory(), ScrumEnum.TASK_ISSUE_TYPE);
		histories = taskOne.getHistories();
		assertEquals(histories.get(0).getDescription(), "Create Task #1");
		assertEquals(histories.get(0).getNewValue(), "");
		assertEquals(histories.get(0).getOldValue(), "");
		assertEquals(histories.get(0).getHistoryType(), HistoryObject.TYPE_CREATE);
		
		assertEquals(histories.get(1).getDescription(), "\"Task_Name_One\" => \"NEW_NAME\"");
		assertEquals(histories.get(1).getNewValue(), "NEW_NAME");
		assertEquals(histories.get(1).getOldValue(), "Task_Name_One");
		assertEquals(histories.get(1).getHistoryType(), HistoryObject.TYPE_NAME);

		d.setTime(d.getTime() + 1000);
		// test method
		mMantisService.updateName(taskOne, "NEW_NAME2", d);
		// test method

		taskOne = mMantisService.getIssue(taskID);
		assertEquals(taskOne.getSummary(), "NEW_NAME2");
		assertEquals(taskOne.getDescription(), "Task_Desc_One");
		assertEquals(taskOne.getCategory(), ScrumEnum.TASK_ISSUE_TYPE);
		histories = taskOne.getHistories();
		assertEquals(histories.get(0).getDescription(), "Create Task #1");
		assertEquals(histories.get(0).getNewValue(), "");
		assertEquals(histories.get(0).getOldValue(), "");
		assertEquals(histories.get(0).getHistoryType(), HistoryObject.TYPE_CREATE);
		
		assertEquals(histories.get(1).getDescription(), "\"Task_Name_One\" => \"NEW_NAME\"");
		assertEquals(histories.get(1).getNewValue(), "NEW_NAME");
		assertEquals(histories.get(1).getOldValue(), "Task_Name_One");
		assertEquals(histories.get(1).getHistoryType(), HistoryObject.TYPE_NAME);
		
		assertEquals(histories.get(2).getDescription(), "\"NEW_NAME\" => \"NEW_NAME2\"");
		assertEquals(histories.get(2).getNewValue(), "NEW_NAME2");
		assertEquals(histories.get(2).getOldValue(), "NEW_NAME");
		assertEquals(histories.get(2).getHistoryType(), HistoryObject.TYPE_NAME);

		// close connection
		mMantisService.closeConnect();
	}

	@Test
	public void testgetAttachFile() {
		// ================ set initial data =======================
		CreateProductBacklog CPB = new CreateProductBacklog(mStoryCount, mCP);
		CPB.exe();
		
		long storyId = CPB.getStories().get(0).getId();
		AttachFileInfo attachFileInfo = new AttachFileInfo();
        attachFileInfo.issueId = storyId;
        attachFileInfo.name = "TESTFILE.txt";
        attachFileInfo.path = "/abc/def/" + attachFileInfo.name;
        attachFileInfo.issueType = IssueTypeEnum.TYPE_STORY;
        attachFileInfo.contentType = "text/pain";
        attachFileInfo.projectName = mProject.getName();
		
		// ================ set initial data =======================
		mMantisService.openConnect();
		long fileId = mMantisService.addAttachFile(attachFileInfo);
		AttachFileObject attachFileObject = mMantisService.getAttachFile(fileId);
		mMantisService.closeConnect();

		assertEquals(attachFileObject.getId(), fileId);
		assertEquals(attachFileObject.getIssueId(), attachFileInfo.issueId);
		assertEquals(attachFileObject.getName(), attachFileInfo.name);
		assertEquals(attachFileObject.getPath(), attachFileInfo.path);
		assertEquals(attachFileObject.getContentType(), attachFileInfo.contentType);

		// ============= release ==============
		mProject = null;
		attachFileInfo = null;
		attachFileObject = null;
	}
	
	/**
	 * 主要測試getIssue方法中 mAttachFileService.initAttachFile(issue);對getIssue造成的效果是否有效
	 */
	@Test
	public void testGetIssue_AboutAttachFile() {
		// new issue data
		long issueId = 1;
		ArrayList<Long> fileIdList = new ArrayList<Long>();
		IIssue story = new Issue();
		story.setIssueID(issueId);
		story.setSummary("Story_Name_One");
		story.setDescription("Story_Desc_One");
		story.setProjectID(mCP.getProjectList().get(0).getName());
		story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);

		mMantisService.openConnect();
		mMantisService.newIssue(story);
		IIssue storyOne = mMantisService.getIssue(issueId);
		assertEquals(storyOne.getAttachFiles().size(), 0);
		
		// add three file to story
		int attachFileCount = 3;
		ArrayList<AttachFileInfo> attachFileInfolist = new ArrayList<AttachFileInfo>();
		for(int i = 1; i <= attachFileCount; i++) {
			AttachFileInfo attachFileInfo = new AttachFileInfo();
	        attachFileInfo.issueId = story.getIssueID();
	        attachFileInfo.name = "TESTFILE_" + i + ".txt";
	        attachFileInfo.contentType = "text/pain";
	        attachFileInfo.path = "/abc/def/TESTFILE_" + i + ".txt";
	        attachFileInfo.projectName = mProject.getName();
	        fileIdList.add(mMantisService.addAttachFile(attachFileInfo));
	        attachFileInfolist.add(attachFileInfo);
		}
		
		// storyOne這裡要refresh一次，因為已經是dirty data
		storyOne = mMantisService.getIssue(issueId);
		ArrayList<AttachFileObject> attachFileObjects = storyOne.getAttachFiles();
		mMantisService.closeConnect();
		
		assertEquals(attachFileObjects.size(), attachFileCount);
		for(int i = 0; i < attachFileCount; i++) {
	        assertEquals((long)fileIdList.get(i), attachFileObjects.get(i).getId());
	        assertEquals(attachFileInfolist.get(i).name, attachFileObjects.get(i).getName());
	        assertEquals(attachFileInfolist.get(i).issueId, attachFileObjects.get(i).getIssueId());
	        assertEquals(attachFileInfolist.get(i).issueType, attachFileObjects.get(i).getIssueType());
	        assertEquals(attachFileInfolist.get(i).contentType, attachFileObjects.get(i).getContentType());
	        assertEquals(attachFileInfolist.get(i).path, attachFileObjects.get(i).getPath());
		}
	}

	/**
	 * 主要測試getIssues方法中 p.s.這裡是複數s mAttachFileService.initAttachFile(issue);對getIssues造成的效果是否有效
	 * @throws SQLException 
	 */
	@Test
	public void testGetIssues_AboutAttachFile() throws SQLException {
		ArrayList<IIssue> storyList = new ArrayList<IIssue>();
		AttachFileInfo attachFileInfo = new AttachFileInfo();
		long fileId = 0;
		assertEquals(storyList.size(), 0);
		
		mMantisService.openConnect();
		for (int i = 0; i < 3; i++) {
			IIssue story = new Issue();
			story.setIssueID(i + 1);
			story.setSummary("Story_Name_" + (i + 1));
			story.setDescription("Story_Desc_" + (i + 1));
			addTagElement(story, Integer.toString((i + 1) * 5), Integer.toString((i + 1) * 10),
			        Integer.toString((i + 1) * 15), "Demo_" + (i + 1), "Note_" + (i + 1));
			story.setProjectID(mProject.getName());
			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
			mMantisService.newIssue(story);
			// 第二筆資料附加測試檔案
			if (i == 1) {
		        attachFileInfo.issueId = story.getIssueID();
		        attachFileInfo.name = "TESTFILE.txt";
		        attachFileInfo.contentType = "text/pain";
		        attachFileInfo.path = "/abc/def/TESTFILE.txt";
		        attachFileInfo.projectName = mProject.getName();
		        fileId = mMantisService.addAttachFile(attachFileInfo);
			}
		}
		
		// refresh dirty story
		storyList.addAll(Arrays.asList(mMantisService.getIssues(mProject.getName(), ScrumEnum.STORY_ISSUE_TYPE)));
		AttachFileObject attachFileObject = mMantisService.getAttachFile(fileId);
		mMantisService.closeConnect();
		
		assertEquals(storyList.size(), 3);
		// 第一個story不存在任何檔案
		assertEquals(storyList.get(0).getAttachFiles().size(), 0);
		// 第二個story存在一個測試檔案檔案
		assertEquals(storyList.get(1).getAttachFiles().size(), 1);
		assertEquals(fileId, attachFileObject.getId());
		assertEquals(attachFileInfo.name, attachFileObject.getName());
		assertEquals(attachFileInfo.path, attachFileObject.getPath());
		assertEquals(attachFileInfo.issueId, attachFileObject.getIssueId());
		assertEquals(attachFileInfo.issueType, attachFileObject.getIssueType());
		// 第三個story不存在任何檔案
		assertEquals(storyList.get(2).getAttachFiles().size(), 0);
	}

	/**
	 * 主要測試getStorys方法中 mAttachFileService.initAttachFile(story);對getStorys造成的效果是否有效
	 */
	@Test
	public void testGetStories_AboutAttachFile() {
		ArrayList<IStory> storyList = new ArrayList<IStory>();
		AttachFileInfo attachFileInfo = new AttachFileInfo();
		long fileId = 0;
		assertEquals(storyList.size(), 0);
		
		mMantisService.openConnect();
		for (int i = 0; i < 3; i++) {
			IIssue story = new Issue();
			story.setIssueID(i + 1);
			story.setSummary("Story_Name_" + i + 1);
			story.setDescription("Story_Desc_" + i + 1);
			addTagElement(story, Integer.toString((i + 1) * 5), Integer.toString((i + 1) * 10),
			        Integer.toString((i + 1) * 15), "Demo_" + i + 1, "Note_" + i + 1);
			story.setProjectID(mProject.getName());
			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
			mMantisService.newIssue(story);
			// 第二筆資料附加測試檔案
			if (i == 1) {
				attachFileInfo.issueId = story.getIssueID();
		        attachFileInfo.name = "TESTFILE.txt";
		        attachFileInfo.contentType = "text/pain";
		        attachFileInfo.path = "/abc/def/TESTFILE.txt";
		        attachFileInfo.projectName = mProject.getName();
		        fileId = mMantisService.addAttachFile(attachFileInfo);
			}
		}
		
		// refresh dirty story
		storyList = mMantisService.getStorys(mProject.getName());
		AttachFileObject attachFileObject = mMantisService.getAttachFile(fileId);
		mMantisService.closeConnect();
		
		// 第一個story不存在任何檔案
		assertEquals(storyList.get(0).getAttachFiles().size(), 0);
		// 第二個story存在一個測試檔案檔案
		assertEquals(storyList.get(1).getAttachFiles().size(), 1);
		assertEquals(fileId, attachFileObject.getId());
		assertEquals(attachFileInfo.name, attachFileObject.getName());
		assertEquals(attachFileInfo.path, attachFileObject.getPath());
		assertEquals(attachFileInfo.issueId, attachFileObject.getIssueId());
		assertEquals(attachFileInfo.issueType, attachFileObject.getIssueType());
		// 第三個story不存在任何檔案
		assertEquals(storyList.get(2).getAttachFiles().size(), 0);
	}

	/**
	 * 主要測試getAttachFile方法中 mAttachFileService.getAttachFile(fileID);對getAttachFile造成的效果是否有效
	 */
	@Test
	public void testGetAttachFile_AboutAttachFile() {
		long fileId;
		IIssue story = new Issue();
		story.setIssueID(1);
		story.setProjectID(mProject.getName());
		story.setSummary("Story_Name_One");
		story.setDescription("Story_Desc_One");
		story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
		mMantisService.openConnect();
		long storyId = mMantisService.newIssue(story);
		
		AttachFileInfo attachFileInfo = new AttachFileInfo();
		attachFileInfo.issueId = storyId;
        attachFileInfo.name = "TESTFILE.txt";
        attachFileInfo.contentType = "text/pain";
        attachFileInfo.path = "/abc/def/TESTFILE.txt";
        attachFileInfo.projectName = mProject.getName();
        
		fileId = mMantisService.addAttachFile(attachFileInfo);
		// refresh dirty story
		story = mMantisService.getIssue(storyId);
		AttachFileObject attachFileObject = mMantisService.getAttachFile(fileId);
		mMantisService.closeConnect();
		
		assertEquals(fileId, attachFileObject.getId());
		assertEquals(attachFileInfo.name, attachFileObject.getName());
		assertEquals(attachFileInfo.path, attachFileObject.getPath());
		assertEquals(attachFileInfo.issueId, attachFileObject.getIssueId());
		assertEquals(attachFileInfo.issueType, attachFileObject.getIssueType());
	}

	/**
	 * for noteService
	 */
	// test : getIssues(String projectName, String category, String releaseID, String sprintID, Date startDate, Date endDate)
	@Test
	public void testGetIssues_Date_AboutGetIssueNotes() {
		IProject project = mCP.getProjectList().get(0);
		mMantisService.openConnect();
		MantisNoteService MNService = new MantisNoteService(mMantisService.getControl(), mConfig);
		TextParserGeneraterForNote noteTextHelper;
		List<IIssue> issueList = new LinkedList<IIssue>();

		// new 10 issues
		int dataCount = 10;
		for (int index = 0; index < dataCount; index++) {
			IIssue story = new Issue();
			story.setIssueID(index + 1);
			story.setSummary("Story_Name_" + Integer.toString(index + 1));
			story.setDescription("Story_Desc_" + Integer.toString(index + 1));

			story.setProjectID(project.getName());
			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);

			mMantisService.newIssue(story);
			issueList.add(story);
		}

		// insert issues' notes
		int imp = 201, est = 21, val = 251;
		for (int index = 0; index < issueList.size(); index++) {
			String importance = Integer.toString(imp + index);
			String estimation = Integer.toString(est + index);
			String value = Integer.toString(val + index);
			String howToDemo = "demo_" + Integer.toString(index + 1);
			String notes = "note_" + Integer.toString(index + 1);

			// generate note text
			noteTextHelper = new TextParserGeneraterForNote();
			String noteText = noteTextHelper.generaterNoteText(importance, estimation, value, howToDemo, notes);

			// test method
			MNService.insertBugNote(issueList.get(index).getIssueID(), noteText);
		}

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);

		// test method
		IIssue[] issueSet = mMantisService.getIssues(project.getName(), ScrumEnum.STORY_ISSUE_TYPE, null, null, calendar.getTime());
		// test method

		// assert the issues' notes info.
		for (int index = 0; index < issueSet.length; index++) {
			String importance = Integer.toString(imp + index);
			String estimation = Integer.toString(est + index);
			String value = Integer.toString(val + index);
			String howToDemo = "demo_" + Integer.toString(index + 1);
			String notes = "note_" + Integer.toString(index + 1);
			for (IIssueNote note : issueSet[index].getIssueNotes()) {
				noteTextHelper = new TextParserGeneraterForNote();
				noteTextHelper.parserNoteText(note.getText());
				assertEquals(index + 1, note.getIssueID());					// 1, 2 ..
				assertEquals(index + 1, note.getNoteID());					// 1, 2 ..
				// assertEquals("administrator", note.getHandler()); // default = 1 (administrator)
				assertEquals("admin", note.getHandler());			// default = 1 (administrator)
				assertEquals(importance, noteTextHelper.getImportance());	// 201, 202, 203 ..
				assertEquals(estimation, noteTextHelper.getEstimation());	// 21, 22 , 23 ..
				assertEquals(value, noteTextHelper.getValue());				// 251, 252, 253 ..
				assertEquals(howToDemo, noteTextHelper.getHowToDemo());		// demo_1, 2 ..
				assertEquals(notes, noteTextHelper.getNotes());				// note_1, 2 ..
			}
		}
		// close connection
		mMantisService.closeConnect();
	}

	@Test
	public void testGetIssue_IssueID_AboutGetIssueNotes() {
		IProject project = mCP.getProjectList().get(0);
		mMantisService.openConnect();
		MantisNoteService MNService = new MantisNoteService(mMantisService.getControl(), mConfig);
		TextParserGeneraterForNote noteTextHelper;
		List<IIssue> issueList = new LinkedList<IIssue>();

		// new 10 issues
		int dataCount = 10;
		for (int index = 0; index < dataCount; index++) {
			IIssue story = new Issue();
			story.setIssueID(index + 1);
			story.setSummary("Story_Name_" + Integer.toString(index + 1));
			story.setDescription("Story_Desc_" + Integer.toString(index + 1));

			story.setProjectID(project.getName());
			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);

			mMantisService.newIssue(story);
			issueList.add(story);
		}

		// insert issues' notes
		int imp = 201, est = 21, val = 251;
		for (int index = 0; index < issueList.size(); index++) {
			String importance = Integer.toString(imp + index);
			String estimation = Integer.toString(est + index);
			String value = Integer.toString(val + index);
			String howToDemo = "demo_" + Integer.toString(index + 1);
			String notes = "note_" + Integer.toString(index + 1);

			// generate note text
			noteTextHelper = new TextParserGeneraterForNote();
			String noteText = noteTextHelper.generaterNoteText(importance, estimation, value, howToDemo, notes);

			// test method
			MNService.insertBugNote(issueList.get(index).getIssueID(), noteText);
		}

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);

		// assert the issues' notes info.
		for (int index = 0; index < dataCount; index++) {
			long issueID = (long) (index + 1);
			// test method
			List<IIssueNote> noteList = mMantisService.getIssue(issueID).getIssueNotes();
			// test method

			String importance = Integer.toString(imp + index);
			String estimation = Integer.toString(est + index);
			String value = Integer.toString(val + index);
			String howToDemo = "demo_" + Integer.toString(index + 1);
			String notes = "note_" + Integer.toString(index + 1);

			for (IIssueNote note : noteList) {
				noteTextHelper = new TextParserGeneraterForNote();
				noteTextHelper.parserNoteText(note.getText());
				assertEquals(index + 1, note.getIssueID());					// 1, 2 ..
				assertEquals(index + 1, note.getNoteID());					// 1, 2 ..
				// assertEquals("administrator", note.getHandler()); // default = 1 (administrator)
				assertEquals("admin", note.getHandler());			// default = 1 (administrator)
				assertEquals(importance, noteTextHelper.getImportance());	// 201, 202, 203 ..
				assertEquals(estimation, noteTextHelper.getEstimation());	// 21, 22 , 23 ..
				assertEquals(value, noteTextHelper.getValue());				// 251, 252, 253 ..
				assertEquals(howToDemo, noteTextHelper.getHowToDemo());		// demo_1, 2 ..
				assertEquals(notes, noteTextHelper.getNotes());				// note_1, 2 ..
			}
		}
		// close connection
		mMantisService.closeConnect();
	}

	@Test
	public void testUpdateBugNote() {
		IProject project = mCP.getProjectList().get(0);
		mMantisService.openConnect();
		MantisNoteService MNService = new MantisNoteService(mMantisService.getControl(), mConfig);
		TextParserGeneraterForNote noteTextHelper;

		List<IIssue> issueList = new LinkedList<IIssue>();
		List<IIssueNote> noteList;

		// new 10 issues
		for (int index = 0; index < 10; index++) {
			IIssue story = new Issue();
			story.setIssueID(index + 1);
			story.setSummary("Story_Name_" + Integer.toString(index + 1));
			story.setDescription("Story_Desc_" + Integer.toString(index + 1));

			story.setProjectID(project.getName());
			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);

			mMantisService.newIssue(story);
			issueList.add(story);

			// check the issue's note is null
			noteList = MNService.getIssueNotes(issueList.get(index));
			assertEquals(0, noteList.size());
		}

		// Test if there is no existing tag in bug note initially,
		// "insertBugNote" would be called in function "updateBugNote".
		int imp = 201, est = 21, val = 251;
		for (int index = 0; index < issueList.size(); index++) {
			String importance = Integer.toString(imp + index);
			String estimation = Integer.toString(est + index);
			String value = Integer.toString(val + index);
			String howToDemo = "demo_" + Integer.toString(index + 1);
			String notes = "note_" + Integer.toString(index + 1);

			// test method : "updateBugNote", which would be called in function "addTagElement".
			addTagElement(issueList.get(index), importance, estimation, value, howToDemo, notes);

			// check the issue note info.
			noteList = MNService.getIssueNotes(issueList.get(index));
			assertEquals(1, noteList.size());
			for (IIssueNote note : noteList) {
				noteTextHelper = new TextParserGeneraterForNote();
				noteTextHelper.parserNoteText(note.getText());
				assertEquals(index + 1, note.getIssueID());					// 1, 2 ..
				assertEquals(index + 1, note.getNoteID());					// 1, 2 ..
				// assertEquals("administrator", note.getHandler()); // default = 1 (administrator)
				assertEquals("admin", note.getHandler());			// default = 1 (administrator)
				assertEquals(importance, noteTextHelper.getImportance());	// 201, 202, 203 ..
				assertEquals(estimation, noteTextHelper.getEstimation());	// 21, 22 , 23 ..
				assertEquals(value, noteTextHelper.getValue());				// 251, 252, 253 ..
				assertEquals(howToDemo, noteTextHelper.getHowToDemo());		// demo_1, 2 ..
				assertEquals(notes, noteTextHelper.getNotes());				// note_1, 2 ..
			}
		}
		// ==============================================================

		// Test if there are some tags already exist in bug note initially,
		// then the "updateBugNote" would update the "JCIS tag" by "appending".
		imp = 301;
		est = 31;
		val = 351;
		for (int index = 0; index < issueList.size(); index++) {
			String importance = Integer.toString(imp + index);
			String estimation = Integer.toString(est + index);
			String value = Integer.toString(val + index);
			String howToDemo = "demo_" + Integer.toString(index + 11);
			String notes = "note_" + Integer.toString(index + 11);

			// test method : "updateBugNote", which would be called in function "addTagElement".
			addTagElement(issueList.get(index), importance, estimation, value, howToDemo, notes);

			// check the issue note info.
			noteList = MNService.getIssueNotes(issueList.get(index));
			assertEquals(1, noteList.size());// list size still is 1, because the query result didn't separate the note tags
			for (IIssueNote note : noteList) {
				noteTextHelper = new TextParserGeneraterForNote();
				noteTextHelper.parserNoteText(note.getText());
				assertEquals(index + 1, note.getIssueID());					// 1, 2 ..
				assertEquals(index + 1, note.getNoteID());					// 1, 2 ..
				// assertEquals("administrator", note.getHandler()); // default = 1 (administrator)
				assertEquals("admin", note.getHandler());			// default = 1 (administrator)
				assertEquals(importance, noteTextHelper.getImportance());	// 301, 302, 303 ..
				assertEquals(estimation, noteTextHelper.getEstimation());	// 31, 32 , 33 ..
				assertEquals(value, noteTextHelper.getValue());				// 351, 352, 353 ..
				assertEquals(howToDemo, noteTextHelper.getHowToDemo());		// demo_1, 2 ..
				assertEquals(notes, noteTextHelper.getNotes());				// note_1, 2 ..
			}
		}
		// close connection
		mMantisService.closeConnect();

		// release
		project = null;
		MNService = null;
	}

	@Test
	public void testUpdateIssueNote() {
		IProject project = mCP.getProjectList().get(0);
		mMantisService.openConnect();
		MantisNoteService MNService = new MantisNoteService(mMantisService.getControl(), mConfig);
		TextParserGeneraterForNote noteTextHelper;

		List<IIssue> issueList = new LinkedList<IIssue>();
		List<IIssueNote> noteList;

		// new 10 issues
		for (int index = 0; index < 10; index++) {
			IIssue story = new Issue();
			story.setIssueID(index + 1);
			story.setSummary("Story_Name_" + Integer.toString(index + 1));
			story.setDescription("Story_Desc_" + Integer.toString(index + 1));

			story.setProjectID(project.getName());
			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);

			mMantisService.newIssue(story);
			issueList.add(story);

			// check the issue's note is null
			noteList = MNService.getIssueNotes(issueList.get(index));
			assertEquals(0, noteList.size());
		}

		// Test if there is no existing tag in bug note initially,
		// "insertBugNote" would be called in function "updateIssueNote".
		int imp = 201, est = 21, val = 251;
		for (int index = 0; index < issueList.size(); index++) {
			String importance = Integer.toString(imp + index);
			String estimation = Integer.toString(est + index);
			String value = Integer.toString(val + index);
			String howToDemo = "demo_" + Integer.toString(index + 1);
			String notes = "note_" + Integer.toString(index + 1);

			// generate note text
			noteTextHelper = new TextParserGeneraterForNote();
			String noteText = noteTextHelper.generaterNoteText(importance, estimation, value, howToDemo, notes);

			IIssueNote issueNote = new IssueNote();
			issueNote.setText(noteText);

			// test method
			MNService.updateIssueNote(issueList.get(index), issueNote);

			// set the issue's noteList
			noteList = MNService.getIssueNotes(issueList.get(index));
			issueList.get(index).setIssueNotes(noteList);

			// check the issue note info.
			noteList = MNService.getIssueNotes(issueList.get(index));
			assertEquals(1, noteList.size());
			for (IIssueNote note : noteList) {
				noteTextHelper = new TextParserGeneraterForNote();
				noteTextHelper.parserNoteText(note.getText());
				assertEquals(index + 1, note.getIssueID());					// 1, 2 ..
				assertEquals(index + 1, note.getNoteID());					// 1, 2 ..
				// assertEquals("administrator", note.getHandler()); // default = 1 (administrator)
				assertEquals("admin", note.getHandler());			// default = 1 (administrator)
				assertEquals(importance, noteTextHelper.getImportance());	// 201, 202, 203 ..
				assertEquals(estimation, noteTextHelper.getEstimation());	// 21, 22 , 23 ..
				assertEquals(value, noteTextHelper.getValue());				// 251, 252, 253 ..
				assertEquals(howToDemo, noteTextHelper.getHowToDemo());		// demo_1, 2 ..
				assertEquals(notes, noteTextHelper.getNotes());				// note_1, 2 ..
			}
		}
		// ==============================================================

		// Test if there are some tags already exist in bug note initially,
		// then the "updateIssueNote" would update the "JCIS tag" by "overriding".
		imp = 301;
		est = 31;
		val = 351;
		int index = 0;
		for (IIssue issue : issueList) {
			String importance = Integer.toString(imp + index);
			String estimation = Integer.toString(est + index);
			String value = Integer.toString(val + index);
			String howToDemo = "update_demo_" + Integer.toString(index + 1);
			String notes = "update_note_" + Integer.toString(index + 1);

			// generate note text
			noteTextHelper = new TextParserGeneraterForNote();
			String noteText = noteTextHelper.generaterNoteText(importance, estimation, value, howToDemo, notes);

			// new issueNote
			IIssueNote issueNote = new IssueNote();
			issueNote.setIssueID(issueList.get(index).getIssueID());
			issueNote.setText(noteText);
			issueNote.setHandler("");
			issueNote.setNoteID(issueList.get(index).getIssueID());

			// add new issueNote to issue
			issue.addIssueNote(issueNote);

			// test method
			MNService.updateIssueNote(issue, issueNote);

			// check the issue note info.
			noteList = MNService.getIssueNotes(issueList.get(index));
			assertEquals(1, noteList.size());
			for (IIssueNote note : noteList) {
				noteTextHelper = new TextParserGeneraterForNote();
				noteTextHelper.parserNoteText(note.getText());
				assertEquals(index + 1, note.getIssueID());					// 1, 2 ..
				assertEquals(index + 1, note.getNoteID());					// 1, 2 ..
				// assertEquals("administrator", note.getHandler()); // default = 1 (administrator)
				assertEquals("admin", note.getHandler());			// default = 1 (administrator)
				assertEquals(importance, noteTextHelper.getImportance());	// 301, 302, 303 ..
				assertEquals(estimation, noteTextHelper.getEstimation());	// 31, 32, 33 ..
				assertEquals(value, noteTextHelper.getValue());				// 351, 352, 353 ..
				assertEquals(howToDemo, noteTextHelper.getHowToDemo());		// update_note_1, 2 ..
				assertEquals(notes, noteTextHelper.getNotes());				// update_note_1, 2 ..
			}

			index++;
		}
		// close connection
		mMantisService.closeConnect();

		// release
		MNService = null;
	}

	@Test
	public void testInsertBugNote() {
		IProject project = mCP.getProjectList().get(0);
		mMantisService.openConnect();
		MantisNoteService MNService = new MantisNoteService(mMantisService.getControl(), mConfig);
		TextParserGeneraterForNote noteTextHelper;

		List<IIssue> issueList = new LinkedList<IIssue>();
		List<IIssueNote> noteList;

		// new 10 issues
		for (int index = 0; index < 10; index++) {
			IIssue story = new Issue();
			story.setIssueID(index + 1);
			story.setSummary("Story_Name_" + Integer.toString(index + 1));
			story.setDescription("Story_Desc_" + Integer.toString(index + 1));

			story.setProjectID(project.getName());
			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);

			mMantisService.newIssue(story);
			issueList.add(story);

			// check the issue's note is null
			noteList = MNService.getIssueNotes(issueList.get(index));
			assertEquals(0, noteList.size());
		}

		// Test insertBugNote, insert new bug note into table
		int imp = 201, est = 21, val = 251;
		for (int index = 0; index < issueList.size(); index++) {
			String importance = Integer.toString(imp + index);
			String estimation = Integer.toString(est + index);
			String value = Integer.toString(val + index);
			String howToDemo = "demo_" + Integer.toString(index + 1);
			String notes = "note_" + Integer.toString(index + 1);

			// generate note text
			noteTextHelper = new TextParserGeneraterForNote();
			String noteText = noteTextHelper.generaterNoteText(importance, estimation, value, howToDemo, notes);

			// test method
			MNService.insertBugNote(issueList.get(index).getIssueID(), noteText);

			// check the issue note info.
			noteList = MNService.getIssueNotes(issueList.get(index));
			assertEquals(1, noteList.size());
			for (IIssueNote note : noteList) {
				noteTextHelper = new TextParserGeneraterForNote();
				noteTextHelper.parserNoteText(note.getText());
				assertEquals(index + 1, note.getIssueID());					// 1, 2 ..
				assertEquals(index + 1, note.getNoteID());					// 1, 2 ..
				// assertEquals("administrator", note.getHandler()); // default = 1 (administrator)
				assertEquals("admin", note.getHandler());			// default = 1 (administrator)
				assertEquals(importance, noteTextHelper.getImportance());	// 201, 202, 203 ..
				assertEquals(estimation, noteTextHelper.getEstimation());	// 21, 22 , 23 ..
				assertEquals(value, noteTextHelper.getValue());				// 251, 252, 253 ..
				assertEquals(howToDemo, noteTextHelper.getHowToDemo());		// demo_1, 2 ..
				assertEquals(notes, noteTextHelper.getNotes());				// note_1, 2 ..
			}
		}
		// close connection
		mMantisService.closeConnect();

		// release
		MNService = null;
	}

	@Test
	public void testRemoveNote() {
		mMantisService.openConnect();
		MantisNoteService MNService = new MantisNoteService(mMantisService.getControl(), mConfig);

		// new 10 issues
		int storyCount = 10;
		CreateProductBacklog CPB = new CreateProductBacklog(storyCount, mCP);
		CPB.exe();

		// get the issues by creating data
		List<IIssue> issueList = CPB.getIssueList();

		int index = 0;
		int imp = 200, est = 21, value = 300;
		// override the note info.
		for (IIssue issue : issueList) {
			addTagElement(issue, Integer.toString(imp + index), Integer.toString(est + index),
			        Integer.toString(value + index), "demo_" + index + 1, "note_" + index + 1);
			index++;
		}

		for (IIssue issue : issueList) {
			String issueID = Long.toString(issue.getIssueID());
			// test method
			MNService.removeNote(issueID);

			// assert no note exist in bugnote table
			IQueryValueSet valueSet = new MySQLQuerySet();
			valueSet.addTableName("mantis_bugnote_table");
			valueSet.addFieldEqualCondition("mantis_bugnote_table.id", issueID);
			String query = valueSet.getSelectQuery();
			ResultSet result = mMantisService.getControl().executeQuery(query);
			try {
				assertTrue(!result.next());
			} catch (Exception e) {
				e.printStackTrace();
			}

			// assert no note exist in bugnote_text table
			valueSet.clear();
			valueSet.addTableName("mantis_bugnote_text_table");
			valueSet.addFieldEqualCondition("mantis_bugnote_text_table.id", issueID);
			query = valueSet.getSelectQuery();
			result = mMantisService.getControl().executeQuery(query);
			try {
				assertTrue(!result.next());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// close connection
		mMantisService.closeConnect();

		// release
		MNService = null;
		CPB = null;
	}
}