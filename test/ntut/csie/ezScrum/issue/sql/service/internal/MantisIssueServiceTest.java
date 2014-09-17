package ntut.csie.ezScrum.issue.sql.service.internal;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.internal.Issue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

import org.jdom.Element;

public class MantisIssueServiceTest extends TestCase {
	private CreateProject CP;
	private int ProjectCount = 1;
	private Configuration configuration;
	private MantisIssueService MISservice;
	private MantisService MService;
	
	public MantisIssueServiceTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.store();
		
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();											// 初始化 SQL
		
		// 新增Project
		this.CP = new CreateProject(this.ProjectCount);
		this.CP.exeCreate();
		
		IProject project = this.CP.getProjectList().get(0);
		this.MService = new MantisService(configuration);
		this.MISservice = new MantisIssueService(this.MService.getControl(), configuration);
		
		super.setUp();
		
		// ============= release ==============
		ini = null;
		project = null;
	}
	
	protected void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();											// 初始化 SQL
		
		//再一次關閉SQL連線，以免導致Project無法刪除
		MService.closeConnect();
		
		CopyProject copyProject = new CopyProject(this.CP);
    	copyProject.exeDelete_Project();					// 刪除測試檔案
    	
    	configuration.setTestMode(false);
		configuration.store();
    	
    	// ============= release ==============
    	ini = null;
    	copyProject = null;
    	this.CP = null;
    	this.MISservice = null;
    	this.MService = null;
    	configuration = null;
    	
    	super.tearDown();
	}		
	
	
	public void testnewIssue() {
		IIssue story = new Issue();
		story.setIssueID(1);
		story.setSummary("Story_Name_One");
		story.setDescription("Story_Desc_One");
		// open connection
		this.MService.openConnect();
		addTagElement(story, "100", "10", "200", "demo", "note");

		story.setProjectID(this.CP.getProjectList().get(0).getName());
		story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);

		// test method
		long storyID = this.MISservice.newIssue(story);
		// test method
		assertEquals(storyID, (long)1);
		
		IIssue storyOne = this.MService.getIssue(storyID);
		
//		以下 story 的部分屬性是由 MantisService 私有函數產生,無法作比對		
//		assertEquals(storyOne.getImportance(), "100");
//		assertEquals(storyOne.getEstimated(), "10");
//		assertEquals(storyOne.getValue(), "200");
//		assertEquals(storyOne.getHowToDemo(), "demo");
//		assertEquals(storyOne.getNotes(), "note");
		assertEquals(storyOne.getSummary(), "Story_Name_One");
		assertEquals(storyOne.getDescription(), "Story_Desc_One");
		assertEquals(storyOne.getCategory(), ScrumEnum.STORY_ISSUE_TYPE);
		
		// =====================================================================
		
		IIssue story_2 = new Issue();
		story_2.setIssueID(2);
		story_2.setSummary("Story_Name_Two");
		story_2.setDescription("Story_Desc_Two");
		addTagElement(story_2, "500", "50", "300", "DemoTwo", "NoteTwo");
		story_2.setProjectID(this.CP.getProjectList().get(0).getName());
		story_2.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
		
		// test method
		long storyID_2 = this.MISservice.newIssue(story_2);
		// test method
		assertEquals(storyID_2, (long)2);
		
		IIssue storyTwo = this.MService.getIssue(storyID_2);
		
//		以下 story 的部分屬性是由 MantisService 私有函數產生,無法作比對			
//		assertEquals(storyTwo.getImportance(), "500");
//		assertEquals(storyTwo.getEstimated(), "50");
//		assertEquals(storyTwo.getValue(), "300");
//		assertEquals(storyTwo.getHowToDemo(), "DemoTwo");
//		assertEquals(storyTwo.getNotes(), "NoteTwo");
		assertEquals(storyTwo.getSummary(), "Story_Name_Two");
		assertEquals(storyTwo.getDescription(), "Story_Desc_Two");
		assertEquals(storyTwo.getCategory(), ScrumEnum.STORY_ISSUE_TYPE);
		
		// close connection
		this.MService.closeConnect();
	}
	

	public void testgetIssue() {
		IIssue story_1 = new Issue();
		story_1.setIssueID(1);
		story_1.setSummary("Story_Name_One");
		story_1.setDescription("Story_Desc_One");		
		// open connection
		this.MService.openConnect();
		addTagElement(story_1, "100", "10", "200", "demo", "note");

		story_1.setProjectID(this.CP.getProjectList().get(0).getName());
		story_1.setCategory(ScrumEnum.STORY_ISSUE_TYPE);

		long storyID_1 = this.MISservice.newIssue(story_1);
		
		// test method
		IIssue issue_1 = this.MISservice.getIssue(storyID_1);
		// test method
		
//		can not assert these attributes, because it generated from MantisService private method
//		assertEquals(story_1.getImportance(), issue_1.getImportance());
//		assertEquals(story_1.getEstimated(), issue_1.getEstimated());
//		assertEquals(story_1.getValue(), issue_1.getValue());
//		assertEquals(story_1.getHowToDemo(), issue_1.getHowToDemo());
//		assertEquals(story_1.getNotes(), issue_1.getNotes());
		assertEquals(story_1.getSummary(), issue_1.getSummary());
		assertEquals(story_1.getDescription(), issue_1.getDescription());
		assertEquals(story_1.getCategory(), ScrumEnum.STORY_ISSUE_TYPE);
		
		// close connection
		this.MService.closeConnect();
	}
	
	
	public void testgetProjectIssues() {
		List<IIssue> story_list = new LinkedList<IIssue>();
		
		IProject testProject = this.CP.getProjectList().get(0);
		// new 10 test data
		this.MService.openConnect();
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
			story.setProjectID(testProject.getName());
			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
			
			this.MISservice.newIssue(story);
			story_list.add(story);
		}

		List<IIssue> actual_list = this.MISservice.getProjectIssues(testProject.getName());
		
		for (int i=0 ; i<10 ; i++) {
			IIssue expectedIssue = story_list.get(i);
			IIssue actualIssue = actual_list.get(i);
			
			assertEquals(expectedIssue.getSummary(), actualIssue.getSummary());
			assertEquals(expectedIssue.getDescription(), actualIssue.getDescription());
			assertEquals(expectedIssue.getCategory(), ScrumEnum.STORY_ISSUE_TYPE);
			
//			can not assert these attributes, because it generated from MantisService private method
//			assertEquals(expectedIssue.getImportance(), actualIssue.getImportance());
//			assertEquals(expectedIssue.getEstimated(), actualIssue.getEstimated());
//			assertEquals(expectedIssue.getValue(), actualIssue.getValue());
//			assertEquals(expectedIssue.getHowToDemo(), actualIssue.getHowToDemo());
//			assertEquals(expectedIssue.getNotes(), actualIssue.getNotes());
		}
		
		// close connection
		this.MService.closeConnect();
	}
	
	public void testgetIssues_many_parameter() throws InterruptedException {
		IProject testProject = this.CP.getProjectList().get(0);
		// new 10 test data
		this.MService.openConnect();
		
		IIssue story_1 = new Issue();
		story_1.setIssueID(1);
		story_1.setSummary("Story_Name_1");
		story_1.setDescription("Story_Desc_1");

		addTagElement(story_1, Integer.toString((1)*5), Integer.toString((1)*10), Integer.toString((1)*15), 
							 "Demo_1", "Note_1");
		story_1.setProjectID(testProject.getName());
		story_1.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
		Thread.sleep(1000);
		this.MISservice.newIssue(story_1);								// sprint 0, release 0
		
		
		IIssue story_2 = new Issue();
		story_2.setIssueID(2);
		story_2.setSummary("Story_Name_2");
		story_2.setDescription("Story_Desc_2");

		addTagElement(story_2, Integer.toString((2)*5), Integer.toString((2)*10), Integer.toString((2)*15), 
							 "Demo_2", "Note_2");
		story_2.setProjectID(testProject.getName());
		story_2.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
		this.MISservice.newIssue(story_2);
		Thread.sleep(1000);
		this.addSprintReleaseElement(testProject, story_2, "1", "1");	// sprint 1, release 1
		
		
		IIssue story_3 = new Issue();
		story_3.setIssueID(3);
		story_3.setSummary("Story_Name_3");
		story_3.setDescription("Story_Desc_3");

		addTagElement(story_3, Integer.toString((3)*5), Integer.toString((3)*10), Integer.toString((3)*15), 
							 "Demo_3", "Note_3");
		story_3.setProjectID(testProject.getName());
		story_3.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
		this.MISservice.newIssue(story_3);
		Thread.sleep(1000);
		this.addSprintReleaseElement(testProject, story_3, "1", "1");	// sprint 1, release 1
		
		
		IIssue story_4 = new Issue();
		story_4.setIssueID(4);
		story_4.setSummary("Story_Name_4");
		story_4.setDescription("Story_Desc_4");

		addTagElement(story_4, Integer.toString((4)*5), Integer.toString((4)*10), Integer.toString((4)*15), 
							 "Demo_4", "Note_4");
		story_4.setProjectID(testProject.getName());
		story_4.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
		this.MISservice.newIssue(story_4);
		Thread.sleep(1000);
		this.addSprintReleaseElement(testProject, story_4, "2", "1");	// sprint 2, release 1
		
		
		IIssue story_5 = new Issue();
		story_5.setIssueID(5);
		story_5.setSummary("Story_Name_5");
		story_5.setDescription("Story_Desc_5");

		addTagElement(story_5, Integer.toString((5)*5), Integer.toString((5)*10), Integer.toString((5)*15), 
							 "Demo_5", "Note_5");
		story_5.setProjectID(testProject.getName());
		story_5.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
		this.MISservice.newIssue(story_5);
		this.addSprintReleaseElement(testProject, story_5, "2", "1");	// sprint 2, release 1
		
		
		
		IIssue story_6 = new Issue();
		story_6.setIssueID(6);
		story_6.setSummary("Story_Name_6");
		story_6.setDescription("Story_Desc_6");

		addTagElement(story_6, Integer.toString((6)*5), Integer.toString((6)*10), Integer.toString((6)*15), 
							 "Demo_6", "Note_6");
		story_6.setProjectID(testProject.getName());
		story_6.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
		this.MISservice.newIssue(story_6);
		this.addSprintReleaseElement(testProject, story_6, "3", "2");	// sprint 3, release 2
		
		
		
		IIssue story_7 = new Issue();
		story_7.setIssueID(7);
		story_7.setSummary("Story_Name_7");
		story_7.setDescription("Story_Desc_7");

		addTagElement(story_7, Integer.toString((7)*5), Integer.toString((7)*10), Integer.toString((7)*15), 
							 "Demo_7", "Note_7");
		story_7.setProjectID(testProject.getName());
		story_7.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
		this.MISservice.newIssue(story_7);
		this.addSprintReleaseElement(testProject, story_7, "3", "2");	// sprint 3, release 2
		
		
		
		IIssue story_8 = new Issue();
		story_8.setIssueID(8);
		story_8.setSummary("Story_Name_8");
		story_8.setDescription("Story_Desc_8");

		addTagElement(story_8, Integer.toString((8)*5), Integer.toString((8)*10), Integer.toString((8)*15), 
							 "Demo_8", "Note_8");
		story_8.setProjectID(testProject.getName());
		story_8.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
		this.MISservice.newIssue(story_8);
		this.addSprintReleaseElement(testProject, story_8, "4", "2");	// sprint 4, release 2
		
		
		
		IIssue story_9 = new Issue();
		story_9.setIssueID(9);
		story_9.setSummary("Story_Name_9");
		story_9.setDescription("Story_Desc_9");

		addTagElement(story_9, Integer.toString((9)*5), Integer.toString((9)*10), Integer.toString((9)*15), 
							 "Demo_9", "Note_9");
		story_9.setProjectID(testProject.getName());
		story_9.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
		this.MISservice.newIssue(story_9);
		this.addSprintReleaseElement(testProject, story_9, "4", "2");	// sprint 4, release 2
		
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// test method
		IIssue[] actualIssues = this.MISservice.getIssues(testProject.getName(), ScrumEnum.STORY_ISSUE_TYPE, "0", "0", new Date());
		// test method
		assertEquals(0, actualIssues.length);
		
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// test method 		
		actualIssues = this.MISservice.getIssues(testProject.getName(), ScrumEnum.STORY_ISSUE_TYPE, "1", null, new Date());
		
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
		actualIssues = this.MISservice.getIssues(testProject.getName(), ScrumEnum.STORY_ISSUE_TYPE, "2", null, new Date());
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
		actualIssues = this.MISservice.getIssues(testProject.getName(), ScrumEnum.STORY_ISSUE_TYPE, null, "1", new Date());
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
		actualIssues = this.MISservice.getIssues(testProject.getName(), ScrumEnum.STORY_ISSUE_TYPE, null, "2", new Date());
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
		actualIssues = this.MISservice.getIssues(testProject.getName(), ScrumEnum.STORY_ISSUE_TYPE, null, "3", new Date());
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
		actualIssues = this.MISservice.getIssues(testProject.getName(), ScrumEnum.STORY_ISSUE_TYPE, null, "4", new Date());
		// test method
		assertEquals(2, actualIssues.length);
		assertEquals(8, actualIssues[0].getIssueID());
		assertEquals(9, actualIssues[1].getIssueID());
		
		// close connection
		this.MService.closeConnect();
	}
	
	public void testgetIssues_project() {
		List<IIssue> story_list = new LinkedList<IIssue>();
		
		IProject testProject = this.CP.getProjectList().get(0);
		// new 10 test data
		this.MService.openConnect();
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
			story.setProjectID(testProject.getName());
			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
			
			this.MISservice.newIssue(story);
			story_list.add(story);
		}
		
		// new 10 test task data
		for (int i=10 ; i<20 ; i++) {
			IIssue story = new Issue();
			story.setIssueID(i+1);
			story.setSummary("Task_Name_" + Integer.toString(i+1));
			story.setDescription("Task_Desc_" + Integer.toString(i+1));

			addTagElement(story, Integer.toString((i+1)*5), 
								 Integer.toString((i+1)*10), 
								 Integer.toString((i+1)*15), 
								 "Demo_" + Integer.toString(i+1), 
								 "Note_" + Integer.toString(i+1));
			story.setProjectID(testProject.getName());
			story.setCategory(ScrumEnum.TASK_ISSUE_TYPE);
			
			this.MISservice.newIssue(story);
			story_list.add(story);
		}
		
		// test method
		IIssue[] actual_Issues = this.MISservice.getIssues(testProject.getName());
		// test method
		
		assertEquals(20, actual_Issues.length);
		for (int i=0 ; i<10 ; i++) {
			IIssue expectedIssue = story_list.get(i);
			assertEquals(expectedIssue.getProjectID(), actual_Issues[i].getProjectID());
			assertEquals(expectedIssue.getSummary(), actual_Issues[i].getSummary());
			assertEquals(expectedIssue.getCategory(), ScrumEnum.STORY_ISSUE_TYPE);
		}
		for (int i=10 ; i<20 ; i++) {
			IIssue expectedIssue = story_list.get(i);
			assertEquals(expectedIssue.getProjectID(), actual_Issues[i].getProjectID());
			assertEquals(expectedIssue.getSummary(), actual_Issues[i].getSummary());
			assertEquals(expectedIssue.getCategory(), ScrumEnum.TASK_ISSUE_TYPE);
		}
		
		
		// test method
		actual_Issues = this.MISservice.getIssues("UnExistedProject", ScrumEnum.TASK_ISSUE_TYPE);
		// test method
		assertEquals(0, actual_Issues.length);
		
		// test method
		actual_Issues = this.MISservice.getIssues(testProject.getName(), "UnExistedCategory");
		// test method
		assertEquals(0, actual_Issues.length);
		
		
		// close connection
		this.MService.closeConnect();
	}
	
	public void testgetIssues_project_category() {
		List<IIssue> story_list = new LinkedList<IIssue>();
		
		IProject testProject = this.CP.getProjectList().get(0);
		// new 10 test data
		this.MService.openConnect();
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
			story.setProjectID(testProject.getName());
			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
			
			this.MISservice.newIssue(story);
			story_list.add(story);
		}
		
		// new 10 test task data
		for (int i=10 ; i<20 ; i++) {
			IIssue story = new Issue();
			story.setIssueID(i+1);
			story.setSummary("Task_Name_" + Integer.toString(i+1));
			story.setDescription("Task_Desc_" + Integer.toString(i+1));

			addTagElement(story, Integer.toString((i+1)*5), 
								 Integer.toString((i+1)*10), 
								 Integer.toString((i+1)*15), 
								 "Demo_" + Integer.toString(i+1), 
								 "Note_" + Integer.toString(i+1));
			story.setProjectID(testProject.getName());
			story.setCategory(ScrumEnum.TASK_ISSUE_TYPE);
			
			this.MISservice.newIssue(story);
			story_list.add(story);
		}
		
		// test method
		IIssue[] actual_Issues = this.MISservice.getIssues(testProject.getName(), ScrumEnum.STORY_ISSUE_TYPE);
		// test method
		
		assertEquals(10, actual_Issues.length);
		for (int i=0 ; i<10 ; i++) {
			IIssue expectedIssue = story_list.get(i);
			assertEquals(expectedIssue.getProjectID(), actual_Issues[i].getProjectID());
			assertEquals(expectedIssue.getSummary(), actual_Issues[i].getSummary());
			assertEquals(expectedIssue.getCategory(), ScrumEnum.STORY_ISSUE_TYPE);
		}
		
		
		// test method
		actual_Issues = this.MISservice.getIssues(testProject.getName(), ScrumEnum.TASK_ISSUE_TYPE);
		// test method
		
		assertEquals(10, actual_Issues.length);
		for (int i=0 ; i<10 ; i++) {
			IIssue expectedIssue = story_list.get(i+10);
			assertEquals(expectedIssue.getProjectID(), actual_Issues[i].getProjectID());
			assertEquals(expectedIssue.getSummary(), actual_Issues[i].getSummary());
			assertEquals(expectedIssue.getCategory(), ScrumEnum.TASK_ISSUE_TYPE);
		}
		
		// test method
		actual_Issues = this.MISservice.getIssues("UnExistedProject", ScrumEnum.TASK_ISSUE_TYPE);
		// test method
		assertEquals(0, actual_Issues.length);
		
		// test method
		actual_Issues = this.MISservice.getIssues(testProject.getName(), "UnExistedCategory");
		// test method
		assertEquals(0, actual_Issues.length);
		
		
		// close connection
		this.MService.closeConnect();
	}

	public void testupdateIssueContent() {
//		IIssue story = new Issue();
//		story.setIssueID(1);
//		story.setSummary("Story_Name_One");
//		story.setDescription("Story_Desc_One");
//		story.setAdditional("");
//		// open connection
//		this.MService.openConnect();
//		addTagElement(story, "100", "10", "200", "demo", "note");
//
//		story.setProjectID(this.CP.getProjectList().get(0).getName());
//		story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
//
//		long storyID = this.MISservice.newIssue(story);
//		assertEquals(storyID, (long)1);
//		
//		
//		// change issue data
//		story.setSummary("Story_Name_One_PartTwo");
//		
//		// test method
//		this.MISservice.updateIssueContent(story);
//		// test method
//		IIssue actualIssue = this.MISservice.getIssue(1);
//		assertEquals(story.getSummary(), actualIssue.getSummary());
//		
//		
//		story.setDescription("Story_Desc_One_PartTwo");
//		// test method
//		this.MISservice.updateIssueContent(story);
//		// test method
//		actualIssue = this.MISservice.getIssue(1);
//		assertEquals(story.getDescription(), actualIssue.getDescription());
//		
//		
//		story.setAdditional("testAdditional");
//		// test method
//		this.MISservice.updateIssueContent(story);
//		// test method
//		actualIssue = this.MISservice.getIssue(1);
//		assertEquals(story.getAdditional(), actualIssue.getAdditional());
//		
//		
//		story.setCategory(ScrumEnum.TASK_ISSUE_TYPE);
//		// test method
//		this.MISservice.updateIssueContent(story);
//		// test method
//		actualIssue = this.MISservice.getIssue(1);
//		assertEquals(story.getCategory(), actualIssue.getCategory());
//		
//		
//		story.setAssignto("administrator");
//		// test method
//		this.MISservice.updateIssueContent(story);
//		// test method
//		actualIssue = this.MISservice.getIssue(1);
//		assertEquals(story.getAssignto(), actualIssue.getAssignto());
//		
//		
//		// close connection
//		this.MService.closeConnect();
	}
	
	public void testremoveIssue() {
		List<IIssue> issue_list = new LinkedList<IIssue>();
		
		IProject testProject = this.CP.getProjectList().get(0);
		// new 10 test story data
		this.MService.openConnect();
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
			story.setProjectID(testProject.getName());
			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
			
			this.MISservice.newIssue(story);
			issue_list.add(story);
		}
		
		// test method
		this.MISservice.removeIssue("1");
		// test method
		
		issue_list.remove(0);
		List<IStory> actual_list = this.MISservice.getStorys(testProject.getName());
		assertEquals(9, actual_list.size());
		for (int i=0 ; i<9 ; i++) {
			IIssue expectedIssue = issue_list.get(i);
			IStory actualIssue = actual_list.get(i);
			
			assertEquals(expectedIssue.getProjectID(), actualIssue.getProjectID());
			assertEquals(expectedIssue.getSummary(), actualIssue.getSummary());
			assertEquals(expectedIssue.getCategory(), ScrumEnum.STORY_ISSUE_TYPE);
		}
		
		
		// test method
		this.MISservice.removeIssue("10");
		// test method
		
		issue_list.remove(8);
		actual_list = this.MISservice.getStorys(testProject.getName());
		assertEquals(8, actual_list.size());
		for (int i=0 ; i<8 ; i++) {
			IIssue expectedIssue = issue_list.get(i);
			IStory actualIssue = actual_list.get(i);
			
			assertEquals(expectedIssue.getProjectID(), actualIssue.getProjectID());
			assertEquals(expectedIssue.getSummary(), actualIssue.getSummary());
			assertEquals(expectedIssue.getCategory(), ScrumEnum.STORY_ISSUE_TYPE);
		}
		
		
		// close connection
		this.MService.closeConnect();
	}
	
	public void testgetStorys() {
		List<IIssue> issue_list = new LinkedList<IIssue>();
		
		IProject testProject = this.CP.getProjectList().get(0);
		// new 10 test story data
		this.MService.openConnect();
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
			story.setProjectID(testProject.getName());
			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
			
			this.MISservice.newIssue(story);
			issue_list.add(story);
		}
		
		// new 10 test task data
		for (int i=10 ; i<20 ; i++) {
			IIssue story = new Issue();
			story.setIssueID(i+1);
			story.setSummary("Task_Name_" + Integer.toString(i+1));
			story.setDescription("Task_Desc_" + Integer.toString(i+1));

			addTagElement(story, Integer.toString((i+1)*5), 
								 Integer.toString((i+1)*10), 
								 Integer.toString((i+1)*15), 
								 "Demo_" + Integer.toString(i+1), 
								 "Note_" + Integer.toString(i+1));
			story.setProjectID(testProject.getName());
			story.setCategory(ScrumEnum.TASK_ISSUE_TYPE);
			
			this.MISservice.newIssue(story);
			issue_list.add(story);
		}
		
		// test method
		List<IStory> actual_list = this.MISservice.getStorys(testProject.getName());
		// test method
		
		assertEquals(10, actual_list.size());
		for (int i=0 ; i<10 ; i++) {
			IIssue expectedIssue = issue_list.get(i);
			IStory actualIssue = actual_list.get(i);
			
			assertEquals(expectedIssue.getProjectID(), actualIssue.getProjectID());
			assertEquals(expectedIssue.getSummary(), actualIssue.getSummary());
			assertEquals(expectedIssue.getCategory(), ScrumEnum.STORY_ISSUE_TYPE);
			
//			can not assert these attributes, because it generated from MantisService private method
//			assertEquals(expectedIssue.getImportance(), actualIssue.getImportance());
//			assertEquals(expectedIssue.getEstimated(), actualIssue.getEstimated());
//			assertEquals(expectedIssue.getValue(), actualIssue.getValue());
//			assertEquals(expectedIssue.getHowToDemo(), actualIssue.getHowToDemo());
//			assertEquals(expectedIssue.getNotes(), actualIssue.getNotes());
		}
		
		// close connection
		this.MService.closeConnect();
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
		
		this.MService.updateBugNote(issue);
		this.MService.updateStoryRelationTable(Long.toString(issue.getIssueID()), p.getName(), releaseID, sprintID, "100", "100", new Date());
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
			this.MService.updateBugNote(issue);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}