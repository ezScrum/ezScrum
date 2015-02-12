package ntut.csie.ezScrum.issue.sql.service.internal;

import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.internal.Issue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;
import org.jdom.Element;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MantisIssueServiceTest {
	private int mProjectCount = 1;
	private CreateProject mCP;
	private Configuration mConfig;
	private MantisIssueService mMantisIssueService;
	private MantisService mMantisService;
	
	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();											
		
		// 新增Project
		mCP = new CreateProject(mProjectCount);
		mCP.exeCreate();
		
		mMantisService = new MantisService(mConfig);
		mMantisIssueService = new MantisIssueService(mMantisService.getControl(), mConfig);
	}
	
	@After
	public void tearDown() throws Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();											
		
		//再一次關閉SQL連線，以免導致Project無法刪除
		mMantisService.closeConnect();
		
		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
    	
    	mConfig.setTestMode(false);
		mConfig.save();
    	
		// release resource
		mCP = null;
		mConfig = null;
		mMantisIssueService = null;
		mMantisService = null;
	}		
	
	@Test
	public void testnewIssue() {
		IIssue story = new Issue();
		story.setIssueID(1);
		story.setSummary("Story_Name_One");
		story.setDescription("Story_Desc_One");
		// open connection
		mMantisService.openConnect();
		addTagElement(story, "100", "10", "200", "demo", "note");

		story.setProjectID(mCP.getProjectList().get(0).getName());
		story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);

		// test method
		long storyID = mMantisIssueService.newIssue(story);
		// test method
		assertEquals(storyID, (long)1);
		
		IIssue storyOne = mMantisService.getIssue(storyID);
		assertEquals(storyOne.getSummary(), "Story_Name_One");
		assertEquals(storyOne.getDescription(), "Story_Desc_One");
		assertEquals(storyOne.getCategory(), ScrumEnum.STORY_ISSUE_TYPE);
		
		// =====================================================================
		
		IIssue story2 = new Issue();
		story2.setIssueID(2);
		story2.setSummary("Story_Name_Two");
		story2.setDescription("Story_Desc_Two");
		addTagElement(story2, "500", "50", "300", "DemoTwo", "NoteTwo");
		story2.setProjectID(mCP.getProjectList().get(0).getName());
		story2.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
		
		// test method
		long storyId2 = mMantisIssueService.newIssue(story2);
		// test method
		assertEquals(storyId2, (long)2);
		
		IIssue storyTwo = mMantisService.getIssue(storyId2);
		assertEquals(storyTwo.getSummary(), "Story_Name_Two");
		assertEquals(storyTwo.getDescription(), "Story_Desc_Two");
		assertEquals(storyTwo.getCategory(), ScrumEnum.STORY_ISSUE_TYPE);
		
		// close connection
		mMantisService.closeConnect();
	}
	
	@Test
	public void testgetIssue() {
		IIssue story = new Issue();
		story.setIssueID(1);
		story.setSummary("Story_Name_One");
		story.setDescription("Story_Desc_One");		
		// open connection
		mMantisService.openConnect();
		addTagElement(story, "100", "10", "200", "demo", "note");

		story.setProjectID(mCP.getProjectList().get(0).getName());
		story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);

		long storyId1 = mMantisIssueService.newIssue(story);
		
		// test method
		IIssue issue = mMantisIssueService.getIssue(storyId1);
		// test method
		assertEquals(story.getSummary(), issue.getSummary());
		assertEquals(story.getDescription(), issue.getDescription());
		assertEquals(story.getCategory(), ScrumEnum.STORY_ISSUE_TYPE);
		
		// close connection
		mMantisService.closeConnect();
	}
	
	@Test
	public void testgetProjectIssues() {
		List<IIssue> issues = new LinkedList<IIssue>();
		
		IProject project = mCP.getProjectList().get(0);
		// new 10 test data
		mMantisService.openConnect();
		for (int i=0 ; i<10 ; i++) {
			IIssue story = new Issue();
			story.setIssueID(i+1);
			story.setSummary("Story_Name_" + Integer.toString(i+1));
			story.setDescription("Story_Desc_" + Integer.toString(i+1));

			addTagElement(story, Integer.toString((i+1)*5), 
								 Integer.toString((i+1)*10), 
								 Integer.toString((i+1)*15), 
								 "Demo_" + Integer.toString(i+1), 
								 "Note_" + Integer.toString(i+1));
			story.setProjectID(project.getName());
			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
			
			mMantisIssueService.newIssue(story);
			issues.add(story);
		}

		List<IIssue> actualIssues = mMantisIssueService.getProjectIssues(project.getName());
		
		for (int i=0 ; i<10 ; i++) {
			IIssue expectedIssue = issues.get(i);
			IIssue actualIssue = actualIssues.get(i);
			
			assertEquals(expectedIssue.getSummary(), actualIssue.getSummary());
			assertEquals(expectedIssue.getDescription(), actualIssue.getDescription());
			assertEquals(expectedIssue.getCategory(), ScrumEnum.STORY_ISSUE_TYPE);
		}
		
		// close connection
		mMantisService.closeConnect();
	}
	
	@Test
	public void testgetIssues_many_parameter() throws InterruptedException {
		IProject project = mCP.getProjectList().get(0);
		// new 10 test data
		mMantisService.openConnect();
		
		IIssue story1 = new Issue();
		story1.setIssueID(1);
		story1.setSummary("Story_Name_1");
		story1.setDescription("Story_Desc_1");

		addTagElement(story1, Integer.toString((1)*5), Integer.toString((1)*10), Integer.toString((1)*15), 
							 "Demo_1", "Note_1");
		story1.setProjectID(project.getName());
		story1.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
		Thread.sleep(1000);
		mMantisIssueService.newIssue(story1);								// sprint 0, release 0
		
		
		IIssue story2 = new Issue();
		story2.setIssueID(2);
		story2.setSummary("Story_Name_2");
		story2.setDescription("Story_Desc_2");

		addTagElement(story2, Integer.toString((2)*5), Integer.toString((2)*10), Integer.toString((2)*15), 
							 "Demo_2", "Note_2");
		story2.setProjectID(project.getName());
		story2.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
		mMantisIssueService.newIssue(story2);
		Thread.sleep(1000);
		addSprintReleaseElement(project, story2, "1", "1");	// sprint 1, release 1
		
		
		IIssue story3 = new Issue();
		story3.setIssueID(3);
		story3.setSummary("Story_Name_3");
		story3.setDescription("Story_Desc_3");

		addTagElement(story3, Integer.toString((3)*5), Integer.toString((3)*10), Integer.toString((3)*15), 
							 "Demo_3", "Note_3");
		story3.setProjectID(project.getName());
		story3.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
		mMantisIssueService.newIssue(story3);
		Thread.sleep(1000);
		addSprintReleaseElement(project, story3, "1", "1");	// sprint 1, release 1
		
		
		IIssue story4 = new Issue();
		story4.setIssueID(4);
		story4.setSummary("Story_Name_4");
		story4.setDescription("Story_Desc_4");

		addTagElement(story4, Integer.toString((4)*5), Integer.toString((4)*10), Integer.toString((4)*15), 
							 "Demo_4", "Note_4");
		story4.setProjectID(project.getName());
		story4.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
		mMantisIssueService.newIssue(story4);
		Thread.sleep(1000);
		addSprintReleaseElement(project, story4, "2", "1");	// sprint 2, release 1
		
		
		IIssue story5 = new Issue();
		story5.setIssueID(5);
		story5.setSummary("Story_Name_5");
		story5.setDescription("Story_Desc_5");

		addTagElement(story5, Integer.toString((5)*5), Integer.toString((5)*10), Integer.toString((5)*15), 
							 "Demo_5", "Note_5");
		story5.setProjectID(project.getName());
		story5.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
		mMantisIssueService.newIssue(story5);
		addSprintReleaseElement(project, story5, "2", "1");	// sprint 2, release 1
		
		
		
		IIssue story6 = new Issue();
		story6.setIssueID(6);
		story6.setSummary("Story_Name_6");
		story6.setDescription("Story_Desc_6");

		addTagElement(story6, Integer.toString((6)*5), Integer.toString((6)*10), Integer.toString((6)*15), 
							 "Demo_6", "Note_6");
		story6.setProjectID(project.getName());
		story6.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
		mMantisIssueService.newIssue(story6);
		addSprintReleaseElement(project, story6, "3", "2");	// sprint 3, release 2
		
		
		
		IIssue story7 = new Issue();
		story7.setIssueID(7);
		story7.setSummary("Story_Name_7");
		story7.setDescription("Story_Desc_7");

		addTagElement(story7, Integer.toString((7)*5), Integer.toString((7)*10), Integer.toString((7)*15), 
							 "Demo_7", "Note_7");
		story7.setProjectID(project.getName());
		story7.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
		mMantisIssueService.newIssue(story7);
		addSprintReleaseElement(project, story7, "3", "2");	// sprint 3, release 2
		
		
		
		IIssue story8 = new Issue();
		story8.setIssueID(8);
		story8.setSummary("Story_Name_8");
		story8.setDescription("Story_Desc_8");

		addTagElement(story8, Integer.toString((8)*5), Integer.toString((8)*10), Integer.toString((8)*15), 
							 "Demo_8", "Note_8");
		story8.setProjectID(project.getName());
		story8.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
		mMantisIssueService.newIssue(story8);
		addSprintReleaseElement(project, story8, "4", "2");	// sprint 4, release 2
		
		
		
		IIssue story9 = new Issue();
		story9.setIssueID(9);
		story9.setSummary("Story_Name_9");
		story9.setDescription("Story_Desc_9");

		addTagElement(story9, Integer.toString((9)*5), Integer.toString((9)*10), Integer.toString((9)*15), 
							 "Demo_9", "Note_9");
		story9.setProjectID(project.getName());
		story9.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
		mMantisIssueService.newIssue(story9);
		addSprintReleaseElement(project, story9, "4", "2");	// sprint 4, release 2
		
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// test method
		IIssue[] actualIssues = mMantisIssueService.getIssues(project.getName(), ScrumEnum.STORY_ISSUE_TYPE, "0", "0", new Date());
		// test method
		assertEquals(0, actualIssues.length);
		
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// test method 		
		actualIssues = mMantisIssueService.getIssues(project.getName(), ScrumEnum.STORY_ISSUE_TYPE, "1", null, new Date());
		
		// assert: 若測試失敗,請檢查 DB table "ezscrum_story_relation" 是否漏掉estimation與importance欄位
		assertEquals(4, actualIssues.length);
		assertEquals(2, actualIssues[0].getIssueID());
		assertEquals(3, actualIssues[1].getIssueID());
		assertEquals(4, actualIssues[2].getIssueID());
		assertEquals(5, actualIssues[3].getIssueID());
		
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// test method
		actualIssues = mMantisIssueService.getIssues(project.getName(), ScrumEnum.STORY_ISSUE_TYPE, "2", null, new Date());
		Arrays.sort(actualIssues, new ComparatorByIssueID());
		
		// test method
		assertEquals(4, actualIssues.length);
		assertEquals(6, actualIssues[0].getIssueID());
		assertEquals(7, actualIssues[1].getIssueID());
		assertEquals(8, actualIssues[2].getIssueID());
		assertEquals(9, actualIssues[3].getIssueID());
		
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// test method
		actualIssues = mMantisIssueService.getIssues(project.getName(), ScrumEnum.STORY_ISSUE_TYPE, null, "1", new Date());
		// test method
		assertEquals(2, actualIssues.length);
		assertEquals(2, actualIssues[0].getIssueID());
		assertEquals(3, actualIssues[1].getIssueID());
		
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// test method
		actualIssues = mMantisIssueService.getIssues(project.getName(), ScrumEnum.STORY_ISSUE_TYPE, null, "2", new Date());
		// test method
		assertEquals(2, actualIssues.length);
		assertEquals(4, actualIssues[0].getIssueID());
		assertEquals(5, actualIssues[1].getIssueID());
		
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// test method
		actualIssues = mMantisIssueService.getIssues(project.getName(), ScrumEnum.STORY_ISSUE_TYPE, null, "3", new Date());
		// test method
		assertEquals(2, actualIssues.length);
		assertEquals(6, actualIssues[0].getIssueID());
		assertEquals(7, actualIssues[1].getIssueID());
		
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// test method
		actualIssues = mMantisIssueService.getIssues(project.getName(), ScrumEnum.STORY_ISSUE_TYPE, null, "4", new Date());
		// test method
		assertEquals(2, actualIssues.length);
		assertEquals(8, actualIssues[0].getIssueID());
		assertEquals(9, actualIssues[1].getIssueID());
		
		// close connection
		mMantisService.closeConnect();
	}
	
	@Test
	public void testgetIssues_project() {
		List<IIssue> stories = new LinkedList<IIssue>();
		
		IProject testProject = mCP.getProjectList().get(0);
		// new 10 test data
		mMantisService.openConnect();
		for (int i = 0 ; i < 10 ; i++) {
			IIssue story = new Issue();
			story.setIssueID(i + 1);
			story.setSummary("Story_Name_" + Integer.toString(i+1));
			story.setDescription("Story_Desc_" + Integer.toString(i+1));

			addTagElement(story, Integer.toString((i + 1) * 5), 
								 Integer.toString((i + 1) * 10), 
								 Integer.toString((i + 1) * 15), 
								 "Demo_" + Integer.toString(i+1), 
								 "Note_" + Integer.toString(i+1));
			story.setProjectID(testProject.getName());
			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
			
			mMantisIssueService.newIssue(story);
			stories.add(story);
		}
		
		// new 10 test task data
		for (int i = 10 ; i < 20 ; i++) {
			IIssue story = new Issue();
			story.setIssueID(i + 1);
			story.setSummary("Task_Name_" + Integer.toString(i + 1));
			story.setDescription("Task_Desc_" + Integer.toString(i + 1));

			addTagElement(story, Integer.toString((i + 1) * 5), 
								 Integer.toString((i + 1) * 10), 
								 Integer.toString((i + 1) * 15), 
								 "Demo_" + Integer.toString(i + 1), 
								 "Note_" + Integer.toString(i + 1));
			story.setProjectID(testProject.getName());
			story.setCategory(ScrumEnum.TASK_ISSUE_TYPE);
			
			mMantisIssueService.newIssue(story);
			stories.add(story);
		}
		
		// test method
		IIssue[] actual_Issues = mMantisIssueService.getIssues(testProject.getName());
		// test method
		
		assertEquals(20, actual_Issues.length);
		for (int i = 0 ; i < 10 ; i++) {
			IIssue expectedIssue = stories.get(i);
			assertEquals(expectedIssue.getProjectID(), actual_Issues[i].getProjectID());
			assertEquals(expectedIssue.getSummary(), actual_Issues[i].getSummary());
			assertEquals(expectedIssue.getCategory(), ScrumEnum.STORY_ISSUE_TYPE);
		}
		for (int i = 10 ; i < 20 ; i++) {
			IIssue expectedIssue = stories.get(i);
			assertEquals(expectedIssue.getProjectID(), actual_Issues[i].getProjectID());
			assertEquals(expectedIssue.getSummary(), actual_Issues[i].getSummary());
			assertEquals(expectedIssue.getCategory(), ScrumEnum.TASK_ISSUE_TYPE);
		}
		
		
		// test method
		actual_Issues = mMantisIssueService.getIssues("UnExistedProject", ScrumEnum.TASK_ISSUE_TYPE);
		// test method
		assertEquals(0, actual_Issues.length);
		
		// test method
		actual_Issues = mMantisIssueService.getIssues(testProject.getName(), "UnExistedCategory");
		// test method
		assertEquals(0, actual_Issues.length);
		
		
		// close connection
		mMantisService.closeConnect();
	}
	
	@Test
	public void testgetIssues_project_category() {
		List<IIssue> story_list = new LinkedList<IIssue>();
		
		IProject testProject = mCP.getProjectList().get(0);
		// new 10 test data
		mMantisService.openConnect();
		for (int i = 0 ; i < 10 ; i++) {
			IIssue story = new Issue();
			story.setIssueID(i + 1);
			story.setSummary("Story_Name_" + Integer.toString(i + 1));
			story.setDescription("Story_Desc_" + Integer.toString(i + 1));

			addTagElement(story, Integer.toString((i + 1) * 5), 
								 Integer.toString((i + 1) * 10), 
								 Integer.toString((i + 1) * 15), 
								 "Demo_" + Integer.toString(i + 1), 
								 "Note_" + Integer.toString(i + 1));
			story.setProjectID(testProject.getName());
			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
			
			mMantisIssueService.newIssue(story);
			story_list.add(story);
		}
		
		// new 10 test task data
		for (int i = 10 ; i < 20 ; i++) {
			IIssue story = new Issue();
			story.setIssueID(i + 1);
			story.setSummary("Task_Name_" + Integer.toString(i + 1));
			story.setDescription("Task_Desc_" + Integer.toString(i + 1));

			addTagElement(story, Integer.toString((i + 1) * 5), 
								 Integer.toString((i + 1) * 10), 
								 Integer.toString((i + 1) * 15), 
								 "Demo_" + Integer.toString(i + 1), 
								 "Note_" + Integer.toString(i + 1));
			story.setProjectID(testProject.getName());
			story.setCategory(ScrumEnum.TASK_ISSUE_TYPE);
			
			mMantisIssueService.newIssue(story);
			story_list.add(story);
		}
		
		// test method
		IIssue[] actual_Issues = mMantisIssueService.getIssues(testProject.getName(), ScrumEnum.STORY_ISSUE_TYPE);
		// test method
		
		assertEquals(10, actual_Issues.length);
		for (int i = 0 ; i < 10 ; i++) {
			IIssue expectedIssue = story_list.get(i);
			assertEquals(expectedIssue.getProjectID(), actual_Issues[i].getProjectID());
			assertEquals(expectedIssue.getSummary(), actual_Issues[i].getSummary());
			assertEquals(expectedIssue.getCategory(), ScrumEnum.STORY_ISSUE_TYPE);
		}
		
		
		// test method
		actual_Issues = mMantisIssueService.getIssues(testProject.getName(), ScrumEnum.TASK_ISSUE_TYPE);
		// test method
		
		assertEquals(10, actual_Issues.length);
		for (int i = 0 ; i < 10 ; i++) {
			IIssue expectedIssue = story_list.get(i + 10);
			assertEquals(expectedIssue.getProjectID(), actual_Issues[i].getProjectID());
			assertEquals(expectedIssue.getSummary(), actual_Issues[i].getSummary());
			assertEquals(expectedIssue.getCategory(), ScrumEnum.TASK_ISSUE_TYPE);
		}
		
		// test method
		actual_Issues = mMantisIssueService.getIssues("UnExistedProject", ScrumEnum.TASK_ISSUE_TYPE);
		// test method
		assertEquals(0, actual_Issues.length);
		
		// test method
		actual_Issues = mMantisIssueService.getIssues(testProject.getName(), "UnExistedCategory");
		// test method
		assertEquals(0, actual_Issues.length);
		
		
		// close connection
		mMantisService.closeConnect();
	}
	
	@Test
	public void testremoveIssue() {
		List<IIssue> issue_list = new LinkedList<IIssue>();
		
		IProject testProject = mCP.getProjectList().get(0);
		// new 10 test story data
		mMantisService.openConnect();
		for (int i = 0 ; i < 10 ; i++) {
			IIssue story = new Issue();
			story.setIssueID(i + 1);
			story.setSummary("Story_Name_" + Integer.toString(i + 1));
			story.setDescription("Story_Desc_" + Integer.toString(i + 1));

			addTagElement(story, Integer.toString((i + 1) * 5), 
								 Integer.toString((i + 1) * 10), 
								 Integer.toString((i + 1) * 15), 
								 "Demo_" + Integer.toString(i + 1), 
								 "Note_" + Integer.toString(i + 1));
			story.setProjectID(testProject.getName());
			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
			
			mMantisIssueService.newIssue(story);
			issue_list.add(story);
		}
		
		// test method
		mMantisIssueService.removeIssue("1");
		// test method
		
		issue_list.remove(0);
		List<IStory> actual_list = mMantisIssueService.getStorys(testProject.getName());
		assertEquals(9, actual_list.size());
		for (int i = 0 ; i < 9 ; i++) {
			IIssue expectedIssue = issue_list.get(i);
			IStory actualIssue = actual_list.get(i);
			
			assertEquals(expectedIssue.getProjectID(), actualIssue.getProjectID());
			assertEquals(expectedIssue.getSummary(), actualIssue.getSummary());
			assertEquals(expectedIssue.getCategory(), ScrumEnum.STORY_ISSUE_TYPE);
		}
		
		
		// test method
		mMantisIssueService.removeIssue("10");
		// test method
		
		issue_list.remove(8);
		actual_list = mMantisIssueService.getStorys(testProject.getName());
		assertEquals(8, actual_list.size());
		for (int i = 0 ; i < 8 ; i++) {
			IIssue expectedIssue = issue_list.get(i);
			IStory actualIssue = actual_list.get(i);
			
			assertEquals(expectedIssue.getProjectID(), actualIssue.getProjectID());
			assertEquals(expectedIssue.getSummary(), actualIssue.getSummary());
			assertEquals(expectedIssue.getCategory(), ScrumEnum.STORY_ISSUE_TYPE);
		}
		
		
		// close connection
		mMantisService.closeConnect();
	}
	
	@Test
	public void testgetStorys() {
		List<IIssue> issue_list = new LinkedList<IIssue>();
		
		IProject testProject = mCP.getProjectList().get(0);
		// new 10 test story data
		mMantisService.openConnect();
		for (int i = 0 ; i < 10 ; i++) {
			IIssue story = new Issue();
			story.setIssueID(i + 1);
			story.setSummary("Story_Name_" + Integer.toString(i + 1));
			story.setDescription("Story_Desc_" + Integer.toString(i + 1));

			addTagElement(story, Integer.toString((i + 1) * 5), 
								 Integer.toString((i + 1) * 10), 
								 Integer.toString((i + 1) * 15), 
								 "Demo_" + Integer.toString(i + 1), 
								 "Note_" + Integer.toString(i + 1));
			story.setProjectID(testProject.getName());
			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
			
			mMantisIssueService.newIssue(story);
			issue_list.add(story);
		}
		
		// new 10 test task data
		for (int i=10 ; i<20 ; i++) {
			IIssue story = new Issue();
			story.setIssueID(i + 1);
			story.setSummary("Task_Name_" + Integer.toString(i + 1));
			story.setDescription("Task_Desc_" + Integer.toString(i + 1));

			addTagElement(story, Integer.toString((i + 1) * 5), 
								 Integer.toString((i + 1) * 10), 
								 Integer.toString((i + 1) * 15), 
								 "Demo_" + Integer.toString(i + 1), 
								 "Note_" + Integer.toString(i + 1));
			story.setProjectID(testProject.getName());
			story.setCategory(ScrumEnum.TASK_ISSUE_TYPE);
			
			mMantisIssueService.newIssue(story);
			issue_list.add(story);
		}
		
		// test method
		List<IStory> actual_list = mMantisIssueService.getStorys(testProject.getName());
		// test method
		
		assertEquals(10, actual_list.size());
		for (int i = 0 ; i < 10 ; i++) {
			IIssue expectedIssue = issue_list.get(i);
			IStory actualIssue = actual_list.get(i);
			
			assertEquals(expectedIssue.getProjectID(), actualIssue.getProjectID());
			assertEquals(expectedIssue.getSummary(), actualIssue.getSummary());
			assertEquals(expectedIssue.getCategory(), ScrumEnum.STORY_ISSUE_TYPE);
		}
		
		// close connection
		mMantisService.closeConnect();
	}
	
	private void addSprintReleaseElement(IProject p, IIssue issue, String sprintID, String releaseID) {
		// history node
		Element history = new Element(ScrumEnum.HISTORY_TAG);

		Date current = new Date();
		String dateTime = DateUtil.format(current, DateUtil._16DIGIT_DATE_TIME_2);
		history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, dateTime);

		// iteration node
		Element iteration = new Element(ScrumEnum.SPRINT_ID);
		iteration.setText(sprintID);
		history.addContent(iteration);
		issue.addTagValue(history);
		
		mMantisService.updateBugNote(issue);
		mMantisService.updateStoryRelationTable(issue.getIssueID(), p.getName(), releaseID, sprintID, "100", "100", new Date());
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
		
		if(value != null && !value.equals("")) {
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
}