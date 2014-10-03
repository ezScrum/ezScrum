package ntut.csie.ezScrum.issue.sql.service.internal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.IIssueHistory;
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
import ntut.csie.ezScrum.test.TextParserGeneraterForNote;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateTag;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataInfo.AttachFileInfo;
import ntut.csie.ezScrum.web.dataObject.AttachFileObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

import org.jdom.Element;

public class MantisServiceTest extends TestCase {
	private CreateProject CP;
	private int ProjectCount = 1;
	private int StoryCount = 1;
	private IProject project;
	private Configuration configuration;
	private IUserSession userSession = new UserSession(new AccountMapper().getAccount("admin"));

	private MantisService MSservice;

	public MantisServiceTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		configuration = new Configuration(userSession);
		configuration.setTestMode(true);
		configuration.store();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();

		// 新增Project
		this.CP = new CreateProject(this.ProjectCount);
		this.CP.exeCreate();

		project = this.CP.getProjectList().get(0);
		this.MSservice = new MantisService(configuration);

		super.setUp();

		// ============= release ==============
		ini = null;
	}

	protected void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();											// 初始化 SQL

		// 再一次確認SQL 連線已經關閉
		MSservice.closeConnect();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(configuration.getDataPath());

		configuration.setTestMode(false);
		configuration.store();

		// ============= release ==============
		ini = null;
		this.CP = null;
		this.MSservice = null;
		configuration = null;

		super.tearDown();
	}

//	public void testnewIssue() {
//		IIssue story = new Issue();
//		story.setIssueID(1);
//		story.setSummary("Story_Name_One");
//		story.setDescription("Story_Desc_One");
//		// open connection
//		this.MSservice.openConnect();
//
//		story.setProjectID(this.CP.getProjectList().get(0).getName());
//		story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
//
//		// test method
//		long storyID = this.MSservice.newIssue(story);
//		// test method
//		assertEquals(storyID, (long) 1);
//
//		IIssue storyOne = this.MSservice.getIssue(storyID);
//		assertEquals(storyOne.getSummary(), "Story_Name_One");
//		assertEquals(storyOne.getDescription(), "Story_Desc_One");
//		assertEquals(storyOne.getCategory(), ScrumEnum.STORY_ISSUE_TYPE);
//
//		// =====================================================================
//
//		IIssue story_2 = new Issue();
//		story_2.setIssueID(2);
//		story_2.setSummary("Story_Name_Two");
//		story_2.setDescription("Story_Desc_Two");
//		story_2.setProjectID(this.CP.getProjectList().get(0).getName());
//		story_2.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
//
//		// test method
//		long storyID_2 = this.MSservice.newIssue(story_2);
//		// test method
//		assertEquals(storyID_2, (long) 2);
//
//		IIssue storyTwo = this.MSservice.getIssue(storyID_2);
//		assertEquals(storyTwo.getSummary(), "Story_Name_Two");
//		assertEquals(storyTwo.getDescription(), "Story_Desc_Two");
//		assertEquals(storyTwo.getCategory(), ScrumEnum.STORY_ISSUE_TYPE);
//
//		// close connection
//		this.MSservice.closeConnect();
//	}
//
//	public void testgetIssues_many_parameter() {
//		List<IIssue> story_list = new LinkedList<IIssue>();
//
//		IProject testProject = this.CP.getProjectList().get(0);
//		// new 10 test data
//		this.MSservice.openConnect();
//		for (int i = 0; i < 10; i++) {
//			IIssue story = new Issue();
//			story.setIssueID(i + 1);
//			story.setSummary("Story_Name_" + Integer.toString(i + 1));
//			story.setDescription("Story_Desc_" + Integer.toString(i + 1));
//
//			addTagElement(story, Integer.toString((i + 1) * 5),
//			        Integer.toString((i + 1) * 10),
//			        Integer.toString((i + 1) * 15),
//			        "Demo_" + Integer.toString(i + 1),
//			        "Note_" + Integer.toString(i + 1));
//			story.setProjectID(testProject.getName());
//			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
//
//			this.MSservice.newIssue(story);
//			story_list.add(story);
//		}
//
//		Calendar calendar = Calendar.getInstance();
//		calendar.add(Calendar.DATE, 1);
//		// test method
//		IIssue[] actual_Issues = this.MSservice.getIssues(testProject.getName(), ScrumEnum.STORY_ISSUE_TYPE, null, null, calendar.getTime());
//		// test method
//
//		assertEquals(10, actual_Issues.length);
//		for (int i = 0; i < 10; i++) {
//			IIssue expectedIssue = story_list.get(i);
//			assertEquals(expectedIssue.getEstimated(), actual_Issues[i].getEstimated());
//			assertEquals(expectedIssue.getHowToDemo(), actual_Issues[i].getHowToDemo());
//			assertEquals(expectedIssue.getImportance(), actual_Issues[i].getImportance());
//			assertEquals(expectedIssue.getNotes(), actual_Issues[i].getNotes());
//			assertEquals(expectedIssue.getProjectID(), actual_Issues[i].getProjectID());
//			assertEquals(expectedIssue.getSummary(), actual_Issues[i].getSummary());
//			assertEquals(expectedIssue.getCategory(), ScrumEnum.STORY_ISSUE_TYPE);
//		}
//
//		// close connection
//		this.MSservice.closeConnect();
//	}
//
//	public void testgetIssues_project() {
//		List<IIssue> story_list = new LinkedList<IIssue>();
//
//		IProject testProject = this.CP.getProjectList().get(0);
//		// new 10 test data
//		this.MSservice.openConnect();
//		for (int i = 0; i < 10; i++) {
//			IIssue story = new Issue();
//			story.setIssueID(i + 1);
//			story.setSummary("Story_Name_" + Integer.toString(i + 1));
//			story.setDescription("Story_Desc_" + Integer.toString(i + 1));
//
//			addTagElement(story, Integer.toString((i + 1) * 5),
//			        Integer.toString((i + 1) * 10),
//			        Integer.toString((i + 1) * 15),
//			        "Demo_" + Integer.toString(i + 1),
//			        "Note_" + Integer.toString(i + 1));
//			story.setProjectID(testProject.getName());
//			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
//
//			this.MSservice.newIssue(story);
//			story_list.add(story);
//		}
//
//		// test method
//		IIssue[] actual_Issues = this.MSservice.getIssues(testProject.getName());
//		// test method
//
//		assertEquals(10, actual_Issues.length);
//		for (int i = 0; i < 10; i++) {
//			IIssue expectedIssue = story_list.get(i);
//			assertEquals(expectedIssue.getEstimated(), actual_Issues[i].getEstimated());
//			assertEquals(expectedIssue.getHowToDemo(), actual_Issues[i].getHowToDemo());
//			assertEquals(expectedIssue.getImportance(), actual_Issues[i].getImportance());
//			assertEquals(expectedIssue.getNotes(), actual_Issues[i].getNotes());
//			assertEquals(expectedIssue.getProjectID(), actual_Issues[i].getProjectID());
//			assertEquals(expectedIssue.getSummary(), actual_Issues[i].getSummary());
//			assertEquals(expectedIssue.getCategory(), ScrumEnum.STORY_ISSUE_TYPE);
//		}
//
//		for (int i = 10; i < 20; i++) {
//			IIssue story = new Issue();
//			story.setIssueID(i + 1);
//			story.setSummary("Story_Name_" + Integer.toString(i + 1));
//			story.setDescription("Story_Desc_" + Integer.toString(i + 1));
//
//			addTagElement(story, Integer.toString((i + 1) * 5),
//			        Integer.toString((i + 1) * 10),
//			        Integer.toString((i + 1) * 15),
//			        "Demo_" + Integer.toString(i + 1),
//			        "Note_" + Integer.toString(i + 1));
//			story.setProjectID("NotExistedProject");
//			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
//
//			this.MSservice.newIssue(story);
//			story_list.add(story);
//		}
//
//		// test method
//		actual_Issues = this.MSservice.getIssues(testProject.getName());
//		// test method
//
//		assertEquals(10, actual_Issues.length);
//		for (int i = 0; i < 10; i++) {
//			IIssue expectedIssue = story_list.get(i);
//			assertEquals(expectedIssue.getEstimated(), actual_Issues[i].getEstimated());
//			assertEquals(expectedIssue.getHowToDemo(), actual_Issues[i].getHowToDemo());
//			assertEquals(expectedIssue.getImportance(), actual_Issues[i].getImportance());
//			assertEquals(expectedIssue.getNotes(), actual_Issues[i].getNotes());
//			assertEquals(expectedIssue.getProjectID(), actual_Issues[i].getProjectID());
//			assertEquals(expectedIssue.getSummary(), actual_Issues[i].getSummary());
//			assertEquals(expectedIssue.getCategory(), ScrumEnum.STORY_ISSUE_TYPE);
//		}
//
//		// close connection
//		this.MSservice.closeConnect();
//	}
//
//	public void testgetIssues_project_category() {
//		List<IIssue> story_list = new LinkedList<IIssue>();
//
//		IProject testProject = this.CP.getProjectList().get(0);
//		// new 10 test data
//		this.MSservice.openConnect();
//		for (int i = 0; i < 10; i++) {
//			IIssue story = new Issue();
//			story.setIssueID(i + 1);
//			story.setSummary("Story_Name_" + Integer.toString(i + 1));
//			story.setDescription("Story_Desc_" + Integer.toString(i + 1));
//
//			addTagElement(story, Integer.toString((i + 1) * 5),
//			        Integer.toString((i + 1) * 10),
//			        Integer.toString((i + 1) * 15),
//			        "Demo_" + Integer.toString(i + 1),
//			        "Note_" + Integer.toString(i + 1));
//			story.setProjectID(testProject.getName());
//			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
//
//			this.MSservice.newIssue(story);
//			story_list.add(story);
//		}
//
//		// new 10 test task data
//		for (int i = 10; i < 20; i++) {
//			IIssue story = new Issue();
//			story.setIssueID(i + 1);
//			story.setSummary("Task_Name_" + Integer.toString(i + 1));
//			story.setDescription("Task_Desc_" + Integer.toString(i + 1));
//
//			addTagElement(story, Integer.toString((i + 1) * 5),
//			        Integer.toString((i + 1) * 10),
//			        Integer.toString((i + 1) * 15),
//			        "Demo_" + Integer.toString(i + 1),
//			        "Note_" + Integer.toString(i + 1));
//			story.setProjectID(testProject.getName());
//			story.setCategory(ScrumEnum.TASK_ISSUE_TYPE);
//
//			this.MSservice.newIssue(story);
//			story_list.add(story);
//		}
//
//		// test method
//		IIssue[] actual_Issues = this.MSservice.getIssues(testProject.getName(), ScrumEnum.STORY_ISSUE_TYPE);
//		// test method
//
//		assertEquals(10, actual_Issues.length);
//		for (int i = 0; i < 10; i++) {
//			IIssue expectedIssue = story_list.get(i);
//			assertEquals(expectedIssue.getProjectID(), actual_Issues[i].getProjectID());
//			assertEquals(expectedIssue.getSummary(), actual_Issues[i].getSummary());
//			assertEquals(expectedIssue.getCategory(), ScrumEnum.STORY_ISSUE_TYPE);
//		}
//
//		// test method
//		actual_Issues = this.MSservice.getIssues(testProject.getName(), ScrumEnum.TASK_ISSUE_TYPE);
//		// test method
//
//		assertEquals(10, actual_Issues.length);
//		for (int i = 0; i < 10; i++) {
//			IIssue expectedIssue = story_list.get(i + 10);
//			assertEquals(expectedIssue.getProjectID(), actual_Issues[i].getProjectID());
//			assertEquals(expectedIssue.getSummary(), actual_Issues[i].getSummary());
//			assertEquals(expectedIssue.getCategory(), ScrumEnum.TASK_ISSUE_TYPE);
//		}
//
//		// test method
//		actual_Issues = this.MSservice.getIssues("UnExistedProject", ScrumEnum.TASK_ISSUE_TYPE);
//		// test method
//		assertEquals(0, actual_Issues.length);
//
//		// test method
//		actual_Issues = this.MSservice.getIssues(testProject.getName(), "UnExistedCategory");
//		// test method
//		assertEquals(0, actual_Issues.length);
//
//		// close connection
//		this.MSservice.closeConnect();
//	}
//
//	public void testgetIssue_ID() {
//		IIssue story_1 = new Issue();
//		story_1.setIssueID(1);
//		story_1.setSummary("Story_Name_One");
//		story_1.setDescription("Story_Desc_One");
//		// open connection
//		this.MSservice.openConnect();
//		addTagElement(story_1, "100", "10", "200", "demo", "note");
//
//		story_1.setProjectID(this.CP.getProjectList().get(0).getName());
//		story_1.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
//
//		long storyID_1 = this.MSservice.newIssue(story_1);
//
//		// test method
//		IIssue issue_1 = this.MSservice.getIssue(storyID_1);
//		// test method
//		assertEquals(1, issue_1.getIssueID());
//		assertEquals("Story_Name_One", issue_1.getSummary());
//		assertEquals("Story_Desc_One", issue_1.getDescription());
//		assertEquals("100", issue_1.getImportance());
//		assertEquals("10", issue_1.getEstimated());
//		assertEquals("200", issue_1.getValue());
//		assertEquals("demo", issue_1.getHowToDemo());
//		assertEquals("note", issue_1.getNotes());
//
//		// test method
//		IIssue error_issue = this.MSservice.getIssue(-1);
//		// test method
//		assertEquals(1, issue_1.getIssueID());
//		assertEquals("Story_Name_One", issue_1.getSummary());
//		assertEquals("Story_Desc_One", issue_1.getDescription());
//		assertEquals("100", issue_1.getImportance());
//		assertEquals("10", issue_1.getEstimated());
//		assertEquals("200", issue_1.getValue());
//		assertEquals("demo", issue_1.getHowToDemo());
//		assertEquals("note", issue_1.getNotes());
//		assertEquals(null, error_issue);
//
//		// close connection
//		this.MSservice.closeConnect();
//	}
//
//	public void testupdateIssueContent() {
//		IIssue story = new Issue();
//		story.setIssueID(1);
//		story.setSummary("Story_Name_One");
//		story.setDescription("Story_Desc_One");
//		story.setAdditional("");
//		// open connection
//		this.MSservice.openConnect();
//		addTagElement(story, "100", "10", "200", "demo", "note");
//
//		story.setProjectID(this.CP.getProjectList().get(0).getName());
//		story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
//
//		long storyID = this.MSservice.newIssue(story);
//		assertEquals(storyID, (long) 1);
//
//		// change issue data
//		story.setSummary("Story_Name_One_PartTwo");
//
//		// test method
//		this.MSservice.updateIssueContent(story);
//		// test method
//		IIssue actualIssue = this.MSservice.getIssue(1);
//		assertEquals(story.getSummary(), actualIssue.getSummary());
//
//		story.setDescription("Story_Desc_One_PartTwo");
//		// test method
//		this.MSservice.updateIssueContent(story);
//		// test method
//		actualIssue = this.MSservice.getIssue(1);
//		assertEquals(story.getDescription(), actualIssue.getDescription());
//
//		story.setAdditional("testAdditional");
//		// test method
//		this.MSservice.updateIssueContent(story);
//		// test method
//		actualIssue = this.MSservice.getIssue(1);
//		assertEquals(story.getAdditional(), actualIssue.getAdditional());
//
//		story.setCategory(ScrumEnum.TASK_ISSUE_TYPE);
//		// test method
//		this.MSservice.updateIssueContent(story);
//		// test method
//		actualIssue = this.MSservice.getIssue(1);
//		assertEquals(story.getCategory(), actualIssue.getCategory());
//
//		// story.setAssignto("administrator");
//		story.setAssignto("admin");
//		// test method
//		this.MSservice.updateIssueContent(story);
//		// test method
//		actualIssue = this.MSservice.getIssue(1);
//		assertEquals(story.getAssignto(), actualIssue.getAssignto());
//
//		// close connection
//		this.MSservice.closeConnect();
//	}
//
//	public void testremoveIssue() {
//		List<IIssue> issue_list = new LinkedList<IIssue>();
//
//		IProject testProject = this.CP.getProjectList().get(0);
//		// new 10 test story data
//		this.MSservice.openConnect();
//		for (int i = 0; i < 10; i++) {
//			IIssue story = new Issue();
//			story.setIssueID(i + 1);
//			story.setSummary("Story_Name_" + Integer.toString(i + 1));
//			story.setDescription("Story_Desc_" + Integer.toString(i + 1));
//
//			addTagElement(story, Integer.toString((i + 1) * 5),
//			        Integer.toString((i + 1) * 10),
//			        Integer.toString((i + 1) * 15),
//			        "Demo_" + Integer.toString(i + 1),
//			        "Note_" + Integer.toString(i + 1));
//			story.setProjectID(testProject.getName());
//			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
//
//			this.MSservice.newIssue(story);
//			issue_list.add(story);
//		}
//
//		// test method
//		this.MSservice.removeIssue("1");
//		// test method
//
//		issue_list.remove(0);
//		List<IStory> actual_list = this.MSservice.getStorys(testProject.getName());
//		assertEquals(9, actual_list.size());
//		for (int i = 0; i < 9; i++) {
//			IIssue expectedIssue = issue_list.get(i);
//			IStory actualIssue = actual_list.get(i);
//
//			assertEquals(expectedIssue.getProjectID(), actualIssue.getProjectID());
//			assertEquals(expectedIssue.getSummary(), actualIssue.getSummary());
//			assertEquals(expectedIssue.getCategory(), ScrumEnum.STORY_ISSUE_TYPE);
//		}
//
//		// test method
//		this.MSservice.removeIssue("10");
//		// test method
//
//		issue_list.remove(8);
//		actual_list = this.MSservice.getStorys(testProject.getName());
//		assertEquals(8, actual_list.size());
//		for (int i = 0; i < 8; i++) {
//			IIssue expectedIssue = issue_list.get(i);
//			IStory actualIssue = actual_list.get(i);
//
//			assertEquals(expectedIssue.getProjectID(), actualIssue.getProjectID());
//			assertEquals(expectedIssue.getSummary(), actualIssue.getSummary());
//			assertEquals(expectedIssue.getCategory(), ScrumEnum.STORY_ISSUE_TYPE);
//		}
//
//		// close connection
//		this.MSservice.closeConnect();
//	}
//
//	public void testgetStorys() {
//		List<IIssue> issue_list = new LinkedList<IIssue>();
//
//		IProject testProject = this.CP.getProjectList().get(0);
//		// new 10 test story data
//		this.MSservice.openConnect();
//		for (int i = 0; i < 10; i++) {
//			IIssue story = new Issue();
//			story.setIssueID(i + 1);
//			story.setSummary("Story_Name_" + Integer.toString(i + 1));
//			story.setDescription("Story_Desc_" + Integer.toString(i + 1));
//
//			addTagElement(story, Integer.toString((i + 1) * 5),
//			        Integer.toString((i + 1) * 10),
//			        Integer.toString((i + 1) * 15),
//			        "Demo_" + Integer.toString(i + 1),
//			        "Note_" + Integer.toString(i + 1));
//			story.setProjectID(testProject.getName());
//			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
//
//			this.MSservice.newIssue(story);
//			issue_list.add(story);
//		}
//
//		// new 10 test task data
//		for (int i = 10; i < 20; i++) {
//			IIssue story = new Issue();
//			story.setIssueID(i + 1);
//			story.setSummary("Task_Name_" + Integer.toString(i + 1));
//			story.setDescription("Task_Desc_" + Integer.toString(i + 1));
//
//			addTagElement(story, Integer.toString((i + 1) * 5),
//			        Integer.toString((i + 1) * 10),
//			        Integer.toString((i + 1) * 15),
//			        "Demo_" + Integer.toString(i + 1),
//			        "Note_" + Integer.toString(i + 1));
//			story.setProjectID(testProject.getName());
//			story.setCategory(ScrumEnum.TASK_ISSUE_TYPE);
//
//			this.MSservice.newIssue(story);
//			issue_list.add(story);
//		}
//
//		// test method
//		List<IStory> actual_list = this.MSservice.getStorys(testProject.getName());
//		// test method
//
//		assertEquals(10, actual_list.size());
//		for (int i = 0; i < 10; i++) {
//			IIssue expectedIssue = issue_list.get(i);
//			IStory actualIssue = actual_list.get(i);
//
//			assertEquals(expectedIssue.getProjectID(), actualIssue.getProjectID());
//			assertEquals(expectedIssue.getSummary(), actualIssue.getSummary());
//			assertEquals(expectedIssue.getCategory(), ScrumEnum.STORY_ISSUE_TYPE);
//			assertEquals(expectedIssue.getImportance(), actualIssue.getImportance());
//			assertEquals(expectedIssue.getEstimated(), actualIssue.getEstimated());
//			assertEquals(expectedIssue.getValue(), actualIssue.getValue());
//			assertEquals(expectedIssue.getHowToDemo(), actualIssue.getHowToDemo());
//			assertEquals(expectedIssue.getNotes(), actualIssue.getNotes());
//		}
//
//		// close connection
//		this.MSservice.closeConnect();
//	}
//
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
			this.MSservice.updateBugNote(issue);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
//
//	// ===========================================================
//	// ====================== for tagService =====================
//	// ===========================================================
//	// 測試對象: getIssues(String projectName, String category, String releaseID, String sprintID, Date date)
//	public void testInitTag_many_parameter() {
//		List<IIssue> issueList = new LinkedList<IIssue>();
//		IProject testProject = this.CP.getProjectList().get(0);
//		// new 10 test tag data
//		int dataCount = 10;
//		CreateTag CT = new CreateTag(dataCount, this.CP);
//		CT.exe();
//		// new 10 test story data
//		this.MSservice.openConnect();
//		for (int listIndex = 0; listIndex < dataCount; listIndex++) {
//			IIssue story = new Issue();
//			story.setIssueID(listIndex + 1);
//			story.setSummary("Story_Name_" + Integer.toString(listIndex + 1));
//			story.setProjectID(testProject.getName());
//			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
//
//			this.MSservice.newIssue(story);
//			issueList.add(story);
//
//			for (int tagIndex = 0; tagIndex < dataCount; tagIndex++) {
//				// attach tag to story
//				this.MSservice.addStoryTag(String.valueOf(listIndex + 1), tagIndex + 1);
//			}
//		}
//
//		Calendar calendar = Calendar.getInstance();
//		calendar.add(Calendar.DATE, 1);
//
//		// test method
//		IIssue[] issueSet = this.MSservice.getIssues(testProject.getName(), "ScrumEnum.STORY_ISSUE_TYPE", null, null, calendar.getTime());
//		// test method
//
//		// assert the storyTags' id, name correct or not.
//		for (int issueIndex = 0; issueIndex < issueSet.length; issueIndex++) {
//			for (int tagIndex = 0; tagIndex < dataCount; tagIndex++) {
//				TagObject actualTag = issueSet[issueIndex].getTags().get(tagIndex);
//				assertEquals((long) (tagIndex + 1), actualTag.getId());
//				assertEquals("Test_TAG_" + Integer.toString(tagIndex), actualTag.getName());
//			}
//		}
//		// close connection
//		this.MSservice.closeConnect();
//	}
//
//	// 測試對象: getIssues(String projectName, String category)
//	public void testInitTag_projectName_category() {
//		List<IIssue> issueList = new LinkedList<IIssue>();
//		IProject testProject = this.CP.getProjectList().get(0);
//		// new 10 test tag data
//		int dataCount = 10;
//		CreateTag CT = new CreateTag(dataCount, this.CP);
//		CT.exe();
//		// new 10 test story data
//		this.MSservice.openConnect();
//		for (int listIndex = 0; listIndex < dataCount; listIndex++) {
//			IIssue story = new Issue();
//			story.setIssueID(listIndex + 1);
//			story.setSummary("Story_Name_" + Integer.toString(listIndex + 1));
//			story.setProjectID(testProject.getName());
//			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
//
//			this.MSservice.newIssue(story);
//			issueList.add(story);
//
//			for (int tagIndex = 0; tagIndex < dataCount; tagIndex++) {
//				// attach tag to story
//				this.MSservice.addStoryTag(String.valueOf(listIndex + 1), tagIndex + 1);
//			}
//		}
//
//		// test method
//		IIssue[] issueSet = this.MSservice.getIssues(testProject.getName(), "ScrumEnum.STORY_ISSUE_TYPE");
//		// test method
//
//		// assert the storyTags' id, name correct or not.
//		for (int issueIndex = 0; issueIndex < issueSet.length; issueIndex++) {
//			for (int tagIndex = 0; tagIndex < dataCount; tagIndex++) {
//				TagObject actualTag = issueSet[issueIndex].getTags().get(tagIndex);
//				assertEquals((long) (tagIndex + 1), actualTag.getId());
//				assertEquals("TEST_TAG_" + Integer.toString(tagIndex + 1), actualTag.getName());
//			}
//		}
//		// close connection
//		this.MSservice.closeConnect();
//	}
//
//	// 測試對象: getIssue(long issueID)
//	public void testInitTag_ID() {
//		List<IIssue> issueList = new LinkedList<IIssue>();
//		IProject testProject = this.CP.getProjectList().get(0);
//		// new 10 test tag data
//		int dataCount = 10;
//		CreateTag CT = new CreateTag(dataCount, this.CP);
//		CT.exe();
//		// new 10 test story data
//		this.MSservice.openConnect();
//		for (int listIndex = 0; listIndex < dataCount; listIndex++) {
//			IIssue story = new Issue();
//			story.setIssueID(listIndex + 1);
//			story.setSummary("Story_Name_" + Integer.toString(listIndex + 1));
//			story.setProjectID(testProject.getName());
//			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
//
//			this.MSservice.newIssue(story);
//			issueList.add(story);
//
//			for (int tagIndex = 0; tagIndex < dataCount; tagIndex++) {
//				// attach tag to story
//				this.MSservice.addStoryTag(String.valueOf(listIndex + 1), tagIndex + 1);
//			}
//		}
//
//		// assert the storyTags' id, name correct or not.
//		for (int issueIndex = 0; issueIndex < dataCount; issueIndex++) {
//			// test method
//			IIssue issue = this.MSservice.getIssue((long) (issueIndex + 1));
//			// test method
//			for (int tagIndex = 0; tagIndex < dataCount; tagIndex++) {
//				TagObject actualTag = issue.getTags().get(tagIndex);
//				assertEquals((long) (tagIndex + 1), actualTag.getId());
//				assertEquals("TEST_TAG_" + Integer.toString(tagIndex + 1), actualTag.getName());
//			}
//		}
//		// close connection
//		this.MSservice.closeConnect();
//	}
//
//	// 測試對象: getStorys(String projectName)
//	public void testInitTag_projectName() {
//		List<IIssue> issueList = new LinkedList<IIssue>();
//		IProject testProject = this.CP.getProjectList().get(0);
//		// new 10 test tag data
//		int dataCount = 10;
//		CreateTag CT = new CreateTag(dataCount, this.CP);
//		CT.exe();
//		// new 10 test story data
//		this.MSservice.openConnect();
//		for (int listIndex = 0; listIndex < dataCount; listIndex++) {
//			IIssue story = new Issue();
//			story.setIssueID(listIndex + 1);
//			story.setSummary("Story_Name_" + Integer.toString(listIndex + 1));
//			story.setProjectID(testProject.getName());
//			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
//
//			this.MSservice.newIssue(story);
//			issueList.add(story);
//
//			for (int tagIndex = 0; tagIndex < dataCount; tagIndex++) {
//				// attach tag to story
//				this.MSservice.addStoryTag(String.valueOf(listIndex + 1), tagIndex + 1);
//			}
//		}
//
//		// test method
//		List<IStory> storyList = this.MSservice.getStorys(testProject.getName());
//		// test method
//
//		// assert the storyTags' id, name correct or not.
//		for (int storyIndex = 0; storyIndex < storyList.size(); storyIndex++) {
//			for (int tagIndex = 0; tagIndex < dataCount; tagIndex++) {
//				TagObject actualTag = storyList.get(storyIndex).getTags().get(tagIndex);
//				assertEquals((long) (tagIndex + 1), actualTag.getId());
//				assertEquals("TEST_TAG_" + Integer.toString(tagIndex + 1), actualTag.getName());
//			}
//		}
//		// close connection
//		this.MSservice.closeConnect();
//	}
//
//	// 測試對象: deleteStory(String ID)
//	public void testRemoveStoryTag_story() {
//		List<IIssue> issueList = new LinkedList<IIssue>();
//		IProject testProject = this.CP.getProjectList().get(0);
//		// new 10 test tag data
//		int dataCount = 10;
//		CreateTag CT = new CreateTag(dataCount, this.CP);
//		CT.exe();
//		// new 10 test story data
//		this.MSservice.openConnect();
//		for (int listIndex = 0; listIndex < dataCount; listIndex++) {
//			IIssue story = new Issue();
//			story.setIssueID(listIndex + 1);
//			story.setSummary("Story_Name_" + Integer.toString(listIndex + 1));
//			story.setProjectID(testProject.getName());
//			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
//
//			this.MSservice.newIssue(story);
//			issueList.add(story);
//
//			for (int tagIndex = 0; tagIndex < dataCount; tagIndex++) {
//				// attach tag to story
//				this.MSservice.addStoryTag(String.valueOf(listIndex + 1), tagIndex + 1);
//			}
//		}
//
//		// check tag 筆數正確與否
//		IIssue[] issueSet = this.MSservice.getIssues(testProject.getName());
//		assertEquals(dataCount, issueSet.length);
//
//		// check story 有正確delete
//		for (int issueIndex = 0; issueIndex < issueList.size(); issueIndex++) {
//			// test method
//			this.MSservice.deleteStory(Long.toString(issueList.get(issueIndex).getIssueID()));
//			// test method
//			issueSet = this.MSservice.getIssues(testProject.getName());
//			assertEquals(dataCount - (issueIndex + 1), issueSet.length);
//		}
//
//		// assert no story exist
//		String query = "SELECT * FROM `mantis_bug_table`";
//		ResultSet result = MSservice.getControl().executeQuery(query);
//		try {
//			assertTrue(!result.next());
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//
//		// assert the relation between tag and story has been gone.
//		query = "SELECT * FROM `ezscrum_tag_relation`";
//		result = MSservice.getControl().executeQuery(query);
//		try {
//			assertTrue(!result.next());
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//
//		// close connection
//		this.MSservice.closeConnect();
//	}
//
//	// 測試對象: removeStoryTag(String storyID, String tagID)
//	public void testRemoveStoryTag_storyTag() {
//		List<IIssue> issueList = new LinkedList<IIssue>();
//		IProject testProject = this.CP.getProjectList().get(0);
//		// new 10 test tag data
//		int dataCount = 10;
//		CreateTag CT = new CreateTag(dataCount, this.CP);
//		CT.exe();
//		// new 10 test story data
//		this.MSservice.openConnect();
//		for (int listIndex = 0; listIndex < dataCount; listIndex++) {
//			IIssue story = new Issue();
//			story.setIssueID(listIndex + 1);
//			story.setSummary("Story_Name_" + Integer.toString(listIndex + 1));
//			story.setProjectID(testProject.getName());
//			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
//
//			this.MSservice.newIssue(story);
//			issueList.add(story);
//
//			for (int tagIndex = 0; tagIndex < dataCount; tagIndex++) {
//				// attach tag to story
//				this.MSservice.addStoryTag(String.valueOf(listIndex + 1), tagIndex + 1);
//			}
//		}
//		// check tag 筆數正確
//		ArrayList<TagObject> tagList = this.MSservice.getTagList(testProject.getName());
//		assertEquals(dataCount, tagList.size());
//
//		// remove tags which attach to story
//		for (int issueIndex = 0; issueIndex < dataCount; issueIndex++) {
//			for (int tagIndex = 0; tagIndex < dataCount; tagIndex++) {
//				// test method
//				this.MSservice.removeStoryTag(Integer.toString(issueIndex + 1), tagIndex + 1);
//				// test method
//			}
//		}
//
//		// check relation table 中找不到任何 relation
//		String query = "SELECT * FROM `ezscrum_tag_relation`";
//		ResultSet result = this.MSservice.getControl().executeQuery(query);
//		try {
//			assertTrue(!result.next());
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//
//		// close connection
//		this.MSservice.closeConnect();
//	}
//
//	// test add new tag in project
//	public void testAddNewTag() {
//		IProject testProject = this.CP.getProjectList().get(0);
//		int tagCount = 10;
//		String tagName = "TEST_TAG_";
//		this.MSservice.openConnect();
//		// check add tag 之前，tag list 為空
//		ArrayList<TagObject> tagList = this.MSservice.getTagList(testProject.getName());
//		assertEquals(0, tagList.size());
//
//		// new 10 test tag data
//		for (int tagIndex = 0; tagIndex < tagCount; tagIndex++) {
//			// test method
//			this.MSservice.addNewTag(tagName + Integer.toString(tagIndex + 1), testProject.getName());
//			// test method
//		}
//
//		// check add tag 之後，tag 筆數正確
//		tagList = this.MSservice.getTagList(testProject.getName());
//		assertEquals(tagCount, tagList.size());
//
//		// check tag info. 正確
//		for (int tagIndex = 0; tagIndex < tagCount; tagIndex++) {
//			assertEquals((long) (tagIndex + 1), tagList.get(tagIndex).getId());
//			assertEquals(tagName + Integer.toString(tagIndex + 1), tagList.get(tagIndex).getName());
//		}
//		// close connection
//		this.MSservice.closeConnect();
//	}
//
//	public void testDeleteTag() {
//		// =========== (for MySQL) 測試刪除tag ===========
//		// =========== Local DB 不吃tagService.deleteTag的SQL指令，有sytax error 的訊息 =========
//		IProject testProject = this.CP.getProjectList().get(0);
//		// new 10 test tag data
//		int tagCount = 10;
//		String tagName = "TEST_TAG_";
//		this.MSservice.openConnect();
//		for (int tagIndex = 0; tagIndex < tagCount; tagIndex++) {
//			this.MSservice.addNewTag(tagName + Integer.toString(tagIndex + 1), testProject.getName());
//		}
//		// check add tag 之後，tag list 筆數正確
//		ArrayList<TagObject> tagList = this.MSservice.getTagList(testProject.getName());
//		assertEquals(tagCount, tagList.size());
//
//		// delete tag for each
//		for (int tagIndex = 0; tagIndex < tagCount; tagIndex++) {
//			// test method
//			this.MSservice.deleteTag(tagIndex + 1, testProject.getName());
//			// test method
//			tagList = this.MSservice.getTagList(testProject.getName());
//			// check tag list size
//			assertEquals(tagCount - (tagIndex + 1), tagList.size());
//		}
//
//		// check resultSet 中找不到 delete 掉的 tag
//		String query = "SELECT * FROM `ezscrum_tag_table`";
//		ResultSet result = this.MSservice.getControl().executeQuery(query);
//		try {
//			assertTrue(!result.next());
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//
//		// close connection
//		this.MSservice.closeConnect();
//	}
//
//	public void testGetTagList() {
//		IProject testProject = this.CP.getProjectList().get(0);
//		this.MSservice.openConnect();
//		// check add tag 之前, tag 數為0
//		// test method
//		ArrayList<TagObject> tagList = this.MSservice.getTagList(testProject.getName());
//		// test method
//		assertEquals(0, tagList.size());
//
//		// new 10 test tag data
//		int tagCount = 10;
//		String tagName = "TEST_TAG_";
//		for (int tagIndex = 0; tagIndex < tagCount; tagIndex++) {
//			this.MSservice.addNewTag(tagName + Integer.toString(tagIndex + 1), testProject.getName());
//		}
//
//		// check add tag 後, 數量正確
//		// test method
//		tagList = this.MSservice.getTagList(testProject.getName());
//		// test method
//		assertEquals(tagCount, tagList.size());
//
//		// check tag Info. 正確與否
//		for (int tagIndex = 0; tagIndex < tagCount; tagIndex++) {
//			assertEquals((long) (tagIndex + 1), tagList.get(tagIndex).getId());
//			assertEquals(tagName + Integer.toString(tagIndex + 1), tagList.get(tagIndex).getName());
//		}
//		// close connection
//		this.MSservice.closeConnect();
//	}
//
//	public void testAddStoryTag() {
//		List<IIssue> issueList = new LinkedList<IIssue>();
//		IProject testProject = this.CP.getProjectList().get(0);
//		// new 10 test tag data
//		int dataCount = 10;
//		CreateTag CT = new CreateTag(dataCount, this.CP);
//		CT.exe();
//		// new 10 test story data
//		this.MSservice.openConnect();
//		for (int listIndex = 0; listIndex < dataCount; listIndex++) {
//			IIssue story = new Issue();
//			story.setIssueID(listIndex + 1);
//			story.setSummary("Story_Name_" + Integer.toString(listIndex + 1));
//			story.setProjectID(testProject.getName());
//			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
//
//			this.MSservice.newIssue(story);
//			issueList.add(story);
//
//			for (int tagIndex = 0; tagIndex < dataCount; tagIndex++) {
//				// attach tag to story
//				// test method
//				this.MSservice.addStoryTag(String.valueOf(listIndex + 1), tagIndex + 1);
//				// test method
//			}
//		}
//
//		// check tag 有正確 attach to story
//		for (int issueIndex = 0; issueIndex < dataCount; issueIndex++) {
//			// check the relation between story & tag
//			String query = "SELECT * FROM `ezscrum_tag_relation` WHERE STORY_ID = " + (issueIndex + 1);
//			ResultSet result = this.MSservice.getControl().executeQuery(query);
//			int tagIndex = 0;
//			try {
//				while (result.next()) {
//					assertEquals(tagIndex + 1, result.getInt("ezscrum_tag_relation.tag_id"));
//					tagIndex++;
//				}
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//		// close connection
//		this.MSservice.closeConnect();
//	}
//
//	public void testUpdateTag() {
//		IProject testProject = this.CP.getProjectList().get(0);
//		this.MSservice.openConnect();
//		// new 10 test tag data
//		int tagCount = 10;
//		String tagName = "TEST_TAG_";
//		for (int tagIndex = 0; tagIndex < tagCount; tagIndex++) {
//			this.MSservice.addNewTag(tagName + Integer.toString(tagIndex + 1), testProject.getName());
//		}
//
//		// update tag name. service 作法為 delete 舊的 tag, 新增一筆update name 的 tag
//		for (int tagIndex = 0; tagIndex < tagCount; tagIndex++) {
//			// test method
//			this.MSservice.updateTag(tagIndex + 1,
//			        "UPDATE_TAG_" + Integer.toString(tagIndex + 1), testProject.getName());
//			// test method
//		}
//
//		// check update tag 後, 筆數正確
//		ArrayList<TagObject> tagList = this.MSservice.getTagList(testProject.getName());
//		assertEquals(tagCount, tagList.size());
//
//		// check update tag 後, tag info. correct or not.
//		for (int tagIndex = 10; tagIndex < tagCount; tagIndex++) {
//			assertEquals((long) (tagIndex + 1), tagList.get(tagIndex).getId());
//			assertEquals("UPDATE_TAG_" + Integer.toString(tagIndex + 1), tagList.get(tagIndex).getName());
//		}
//		// close connection
//		this.MSservice.closeConnect();
//	}
//
//	public void testGetTagByName() {
//		IProject testProject = this.CP.getProjectList().get(0);
//		this.MSservice.openConnect();
//		// new 10 test tag data
//		int tagCount = 10;
//		String tagName = "TEST_TAG_";
//		for (int tagIndex = 0; tagIndex < tagCount; tagIndex++) {
//			this.MSservice.addNewTag(tagName + Integer.toString(tagIndex + 1), testProject.getName());
//		}
//
//		TagObject issueTag;
//		for (int tagIndex = 0; tagIndex < tagCount; tagIndex++) {
//			String expectedTagName = tagName + Integer.toString(tagIndex + 1);
//			// test method
//			issueTag = this.MSservice.getTagByName(expectedTagName, testProject.getName());
//			// test method
//
//			// check get 的 tag info. 正確與否
//			assertEquals((long) (tagIndex + 1), issueTag.getId());
//			assertEquals(expectedTagName, issueTag.getName());
//		}
//		// close connection
//		this.MSservice.closeConnect();
//	}
//
//	public void testIsTagExist() {
//		IProject testProject = this.CP.getProjectList().get(0);
//		this.MSservice.openConnect();
//		// new 10 test tag data
//		int tagCount = 10;
//		String tagName = "TEST_TAG_";
//		for (int tagIndex = 0; tagIndex < tagCount; tagIndex++) {
//			this.MSservice.addNewTag(tagName + Integer.toString(tagIndex + 1), testProject.getName());
//		}
//
//		// check tag exist
//		for (int tagIndex = 0; tagIndex < tagCount; tagIndex++) {
//			String expectedTagName = tagName + Integer.toString(tagIndex + 1);
//			// test method
//			assertTrue(this.MSservice.isTagExist(expectedTagName, testProject.getName()));
//			// test method
//		}
//
//		// close connection
//		this.MSservice.closeConnect();
//	}
//
//	public void testnewIssue_history() {
//		IIssue story = new Issue();
//		story.setIssueID(1);
//		story.setSummary("Story_Name_One");
//		story.setDescription("Story_Desc_One");
//		// open connection
//		this.MSservice.openConnect();
//
//		story.setProjectID(this.CP.getProjectList().get(0).getName());
//		story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
//
//		// test method
//		long storyID = this.MSservice.newIssue(story);
//		// test method
//		assertEquals(storyID, (long) 1);
//
//		IIssue storyOne = this.MSservice.getIssue(storyID);
//		assertEquals(storyOne.getSummary(), "Story_Name_One");
//		assertEquals(storyOne.getDescription(), "Story_Desc_One");
//		assertEquals(storyOne.getCategory(), ScrumEnum.STORY_ISSUE_TYPE);
//
//		List<IIssueHistory> histories = storyOne.getHistory();
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//
//		// close connection
//		this.MSservice.closeConnect();
//	}
//
//	public void testgetIssues_history_1() {
//		List<IIssue> story_list = new LinkedList<IIssue>();
//
//		IProject testProject = this.CP.getProjectList().get(0);
//		// new 10 test data
//		this.MSservice.openConnect();
//		for (int i = 0; i < 5; i++) {
//			IIssue story = new Issue();
//			story.setIssueID(i + 1);
//			story.setSummary("Story_Name_" + Integer.toString(i + 1));
//			story.setDescription("Story_Desc_" + Integer.toString(i + 1));
//
//			addTagElement(story, Integer.toString((i + 1) * 5),
//			        Integer.toString((i + 1) * 10),
//			        Integer.toString((i + 1) * 15),
//			        "Demo_" + Integer.toString(i + 1),
//			        "Note_" + Integer.toString(i + 1));
//			story.setProjectID(testProject.getName());
//			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
//
//			this.MSservice.newIssue(story);
//			story_list.add(story);
//		}
//
//		Calendar calendar = Calendar.getInstance();
//		calendar.add(Calendar.DATE, 1);
//		// test method
//		IIssue[] actual_Issues = this.MSservice.getIssues(testProject.getName(), ScrumEnum.STORY_ISSUE_TYPE, null, null, calendar.getTime());
//		// test method
//
//		assertEquals(5, actual_Issues.length);
//		for (int i = 0; i < 5; i++) {
//			IIssue expectedIssue = story_list.get(i);
//			assertEquals(expectedIssue.getEstimated(), actual_Issues[i].getEstimated());
//			assertEquals(expectedIssue.getHowToDemo(), actual_Issues[i].getHowToDemo());
//			assertEquals(expectedIssue.getImportance(), actual_Issues[i].getImportance());
//			assertEquals(expectedIssue.getNotes(), actual_Issues[i].getNotes());
//			assertEquals(expectedIssue.getProjectID(), actual_Issues[i].getProjectID());
//			assertEquals(expectedIssue.getSummary(), actual_Issues[i].getSummary());
//			assertEquals(expectedIssue.getCategory(), ScrumEnum.STORY_ISSUE_TYPE);
//		}
//
//		// setting issuehistory
//		this.MSservice.addHistory((long) 1, "status", "0", "50");
//		this.MSservice.addHistory((long) 1, "Sprint", "-1", "1");
//		this.MSservice.addHistory((long) 1, "handler_id", "0", "1");
//
//		this.MSservice.addHistory((long) 2, "handler_id", "0", "1");
//		this.MSservice.addHistory((long) 2, "status", "0", "50");
//		this.MSservice.addHistory((long) 2, "Sprint", "-1", "1");
//
//		this.MSservice.addHistory((long) 3, "status", "0", "50");
//		this.MSservice.addHistory((long) 3, "handler_id", "0", "1");
//		this.MSservice.addHistory((long) 3, "Sprint", "-1", "1");
//
//		this.MSservice.addHistory((long) 4, "Sprint", "-1", "1");
//		this.MSservice.addHistory((long) 4, "Sprint", "1", "0");
//		this.MSservice.addHistory((long) 4, "Sprint", "0", "2");
//
//		this.MSservice.addHistory((long) 5, "status", "0", "50");
//		this.MSservice.addHistory((long) 5, "Sprint", "0", "1");
//		this.MSservice.addHistory((long) 5, "Sprint", "1", "-1");
//		this.MSservice.addHistory((long) 5, "Sprint", "-1", "2");
//
//		// test method
//		actual_Issues = this.MSservice.getIssues(testProject.getName(), ScrumEnum.STORY_ISSUE_TYPE, null, null, calendar.getTime());
//		// test method
//		assertEquals(5, actual_Issues.length);
//		for (int i = 0; i < 5; i++) {
//			IIssue expectedIssue = story_list.get(i);
//			assertEquals(expectedIssue.getEstimated(), actual_Issues[i].getEstimated());
//			assertEquals(expectedIssue.getHowToDemo(), actual_Issues[i].getHowToDemo());
//			assertEquals(expectedIssue.getImportance(), actual_Issues[i].getImportance());
//			assertEquals(expectedIssue.getNotes(), actual_Issues[i].getNotes());
//			assertEquals(expectedIssue.getProjectID(), actual_Issues[i].getProjectID());
//			assertEquals(expectedIssue.getSummary(), actual_Issues[i].getSummary());
//			assertEquals(expectedIssue.getCategory(), ScrumEnum.STORY_ISSUE_TYPE);
//		}
//
//		IIssue issue_1 = this.MSservice.getIssue(1);
//		List<IIssueHistory> histories = issue_1.getHistory();
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//		assertEquals(histories.get(1).getFieldName(), "status");
//		assertEquals(histories.get(1).getNewValue(), "50");
//		assertEquals(histories.get(1).getOldValue(), "0");
//		assertEquals(histories.get(1).getType(), 0);
//		assertEquals(histories.get(2).getFieldName(), "Sprint");
//		assertEquals(histories.get(2).getNewValue(), "1");
//		assertEquals(histories.get(2).getOldValue(), "-1");
//		assertEquals(histories.get(2).getType(), 0);
//		assertEquals(histories.get(3).getFieldName(), "handler_id");
//		assertEquals(histories.get(3).getNewValue(), "1");
//		assertEquals(histories.get(3).getOldValue(), "0");
//		assertEquals(histories.get(3).getType(), 0);
//
//		IIssue issue_2 = this.MSservice.getIssue(2);
//		histories = issue_2.getHistory();
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//		assertEquals(histories.get(1).getFieldName(), "handler_id");
//		assertEquals(histories.get(1).getNewValue(), "1");
//		assertEquals(histories.get(1).getOldValue(), "0");
//		assertEquals(histories.get(1).getType(), 0);
//		assertEquals(histories.get(2).getFieldName(), "status");
//		assertEquals(histories.get(2).getNewValue(), "50");
//		assertEquals(histories.get(2).getOldValue(), "0");
//		assertEquals(histories.get(2).getType(), 0);
//		assertEquals(histories.get(3).getFieldName(), "Sprint");
//		assertEquals(histories.get(3).getNewValue(), "1");
//		assertEquals(histories.get(3).getOldValue(), "-1");
//		assertEquals(histories.get(3).getType(), 0);
//
//		IIssue issue_3 = this.MSservice.getIssue(3);
//		histories = issue_3.getHistory();
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//		assertEquals(histories.get(1).getFieldName(), "status");
//		assertEquals(histories.get(1).getNewValue(), "50");
//		assertEquals(histories.get(1).getOldValue(), "0");
//		assertEquals(histories.get(1).getType(), 0);
//		assertEquals(histories.get(2).getFieldName(), "handler_id");
//		assertEquals(histories.get(2).getNewValue(), "1");
//		assertEquals(histories.get(2).getOldValue(), "0");
//		assertEquals(histories.get(2).getType(), 0);
//		assertEquals(histories.get(3).getFieldName(), "Sprint");
//		assertEquals(histories.get(3).getNewValue(), "1");
//		assertEquals(histories.get(3).getOldValue(), "-1");
//		assertEquals(histories.get(3).getType(), 0);
//
//		IIssue issue_4 = this.MSservice.getIssue(4);
//		histories = issue_4.getHistory();
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//		assertEquals(histories.get(1).getFieldName(), "Sprint");
//		assertEquals(histories.get(1).getNewValue(), "1");
//		assertEquals(histories.get(1).getOldValue(), "-1");
//		assertEquals(histories.get(1).getType(), 0);
//		assertEquals(histories.get(2).getFieldName(), "Sprint");
//		assertEquals(histories.get(2).getNewValue(), "0");
//		assertEquals(histories.get(2).getOldValue(), "1");
//		assertEquals(histories.get(2).getType(), 0);
//		assertEquals(histories.get(3).getFieldName(), "Sprint");
//		assertEquals(histories.get(3).getNewValue(), "2");
//		assertEquals(histories.get(3).getOldValue(), "0");
//		assertEquals(histories.get(3).getType(), 0);
//
//		IIssue issue_5 = this.MSservice.getIssue(5);
//		histories = issue_5.getHistory();
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//		assertEquals(histories.get(1).getFieldName(), "status");
//		assertEquals(histories.get(1).getNewValue(), "50");
//		assertEquals(histories.get(1).getOldValue(), "0");
//		assertEquals(histories.get(1).getType(), 0);
//		assertEquals(histories.get(2).getFieldName(), "Sprint");
//		assertEquals(histories.get(2).getNewValue(), "1");
//		assertEquals(histories.get(2).getOldValue(), "0");
//		assertEquals(histories.get(2).getType(), 0);
//		assertEquals(histories.get(3).getFieldName(), "Sprint");
//		assertEquals(histories.get(3).getNewValue(), "-1");
//		assertEquals(histories.get(3).getOldValue(), "1");
//		assertEquals(histories.get(3).getType(), 0);
//		assertEquals(histories.get(4).getFieldName(), "Sprint");
//		assertEquals(histories.get(4).getNewValue(), "2");
//		assertEquals(histories.get(4).getOldValue(), "-1");
//		assertEquals(histories.get(4).getType(), 0);
//
//		// close connection
//		this.MSservice.closeConnect();
//	}
//
//	public void testaddHistory() {
//		IIssue story = new Issue();
//		story.setIssueID(1);
//		story.setSummary("Story_Name_One");
//		story.setDescription("Story_Desc_One");
//		// open connection
//		this.MSservice.openConnect();
//
//		story.setProjectID(this.CP.getProjectList().get(0).getName());
//		story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
//
//		long storyID = this.MSservice.newIssue(story);
//		assertEquals(storyID, (long) 1);
//
//		IIssue storyOne = this.MSservice.getIssue(storyID);
//		assertEquals(storyOne.getSummary(), "Story_Name_One");
//		assertEquals(storyOne.getDescription(), "Story_Desc_One");
//		assertEquals(storyOne.getCategory(), ScrumEnum.STORY_ISSUE_TYPE);
//
//		List<IIssueHistory> histories = storyOne.getHistory();
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//
//		// test method
//		this.MSservice.addHistory((long) 1, "status", "0", "50");
//		// test method
//		storyOne = this.MSservice.getIssue(storyID);
//		histories = storyOne.getHistory();
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//		assertEquals(histories.get(1).getFieldName(), "status");
//		assertEquals(histories.get(1).getNewValue(), "50");
//		assertEquals(histories.get(1).getOldValue(), "0");
//		assertEquals(histories.get(1).getType(), 0);
//
//		// test method
//		this.MSservice.addHistory((long) 1, "Sprint", "0", "1");
//		// test method
//		storyOne = this.MSservice.getIssue(storyID);
//		histories = storyOne.getHistory();
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//		assertEquals(histories.get(1).getFieldName(), "status");
//		assertEquals(histories.get(1).getNewValue(), "50");
//		assertEquals(histories.get(1).getOldValue(), "0");
//		assertEquals(histories.get(1).getType(), 0);
//		assertEquals(histories.get(2).getFieldName(), "Sprint");
//		assertEquals(histories.get(2).getNewValue(), "1");
//		assertEquals(histories.get(2).getOldValue(), "0");
//		assertEquals(histories.get(2).getType(), 0);
//
//		// close connection
//		this.MSservice.closeConnect();
//	}
//
//	public void testaddRelationship_history_1() {
//		IIssue story = new Issue();
//		story.setIssueID(1);
//		story.setSummary("Story_Name_One");
//		story.setDescription("Story_Desc_One");
//		// open connection
//		this.MSservice.openConnect();
//		story.setProjectID(this.CP.getProjectList().get(0).getName());
//		story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
//		long storyID = this.MSservice.newIssue(story);
//		assertEquals(storyID, (long) 1);
//
//		IIssue storyOne = this.MSservice.getIssue(storyID);
//		assertEquals(storyOne.getSummary(), "Story_Name_One");
//		assertEquals(storyOne.getDescription(), "Story_Desc_One");
//		assertEquals(storyOne.getCategory(), ScrumEnum.STORY_ISSUE_TYPE);
//
//		List<IIssueHistory> histories = storyOne.getHistory();
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//
//		IIssue task = new Issue();
//		task.setIssueID(2);
//		task.setSummary("Task_Name_One");
//		task.setDescription("Task_Desc_One");
//		task.setProjectID(this.CP.getProjectList().get(0).getName());
//		task.setCategory(ScrumEnum.TASK_ISSUE_TYPE);
//		long taskID = this.MSservice.newIssue(task);
//		assertEquals(taskID, (long) 2);
//
//		IIssue taskOne = this.MSservice.getIssue(taskID);
//		assertEquals(taskOne.getSummary(), "Task_Name_One");
//		assertEquals(taskOne.getDescription(), "Task_Desc_One");
//		assertEquals(taskOne.getCategory(), ScrumEnum.TASK_ISSUE_TYPE);
//
//		histories = taskOne.getHistory();
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//
//		Date d = new Date();
//		d.setTime(d.getTime() + 1000);
//		// test method
//		this.MSservice.addRelationship(storyID, taskID, ITSEnum.PARENT_RELATIONSHIP, d);
//		// test method
//
//		storyOne = this.MSservice.getIssue(storyID);
//		assertEquals(storyOne.getSummary(), "Story_Name_One");
//		assertEquals(storyOne.getDescription(), "Story_Desc_One");
//		assertEquals(storyOne.getCategory(), ScrumEnum.STORY_ISSUE_TYPE);
//
//		histories = storyOne.getHistory();
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//		assertEquals(histories.get(1).getFieldName(), "Relationship added ");
//		assertEquals(histories.get(1).getNewValue(), "2");
//		assertEquals(histories.get(1).getOldValue(), "2");
//		assertEquals(histories.get(1).getType(), 18);		// meaningless
//
//		taskOne = this.MSservice.getIssue(taskID);
//		assertEquals(taskOne.getSummary(), "Task_Name_One");
//		assertEquals(taskOne.getDescription(), "Task_Desc_One");
//		assertEquals(taskOne.getCategory(), ScrumEnum.TASK_ISSUE_TYPE);
//		histories = taskOne.getHistory();
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//		assertEquals(histories.get(1).getFieldName(), "Relationship added ");
//		assertEquals(histories.get(1).getNewValue(), "1");
//		assertEquals(histories.get(1).getOldValue(), "3");
//		assertEquals(histories.get(1).getType(), 18);		// meaningless
//
//		// close connection
//		this.MSservice.closeConnect();
//	}
//
//	public void testaddRelationship_history_2() {
//		IIssue story = new Issue();
//		story.setIssueID(1);
//		story.setSummary("Story_Name_One");
//		story.setDescription("Story_Desc_One");
//		// open connection
//		this.MSservice.openConnect();
//		story.setProjectID(this.CP.getProjectList().get(0).getName());
//		story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
//		long storyID = this.MSservice.newIssue(story);
//		assertEquals(storyID, (long) 1);
//
//		IIssue storyOne = this.MSservice.getIssue(storyID);
//		assertEquals(storyOne.getSummary(), "Story_Name_One");
//		assertEquals(storyOne.getDescription(), "Story_Desc_One");
//		assertEquals(storyOne.getCategory(), ScrumEnum.STORY_ISSUE_TYPE);
//
//		List<IIssueHistory> histories = storyOne.getHistory();
//		assertEquals(histories.size(), 1);
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//
//		IIssue task = new Issue();
//		task.setIssueID(2);
//		task.setSummary("Task_Name_One");
//		task.setDescription("Task_Desc_One");
//		task.setProjectID(this.CP.getProjectList().get(0).getName());
//		task.setCategory(ScrumEnum.TASK_ISSUE_TYPE);
//		long taskID = this.MSservice.newIssue(task);
//		assertEquals(taskID, (long) 2);
//
//		IIssue taskOne = this.MSservice.getIssue(taskID);
//		assertEquals(taskOne.getSummary(), "Task_Name_One");
//		assertEquals(taskOne.getDescription(), "Task_Desc_One");
//		assertEquals(taskOne.getCategory(), ScrumEnum.TASK_ISSUE_TYPE);
//
//		histories = taskOne.getHistory();
//		assertEquals(histories.size(), 1);
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//
//		Date d = new Date();
//		d.setTime(d.getTime() + 1000);
//		// test method
//		this.MSservice.addRelationship(storyID, taskID, 5, d);
//		// test method
//
//		storyOne = this.MSservice.getIssue(storyID);
//		assertEquals(storyOne.getSummary(), "Story_Name_One");
//		assertEquals(storyOne.getDescription(), "Story_Desc_One");
//		assertEquals(storyOne.getCategory(), ScrumEnum.STORY_ISSUE_TYPE);
//
//		histories = storyOne.getHistory();
//		assertEquals(histories.size(), 1);
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//
//		// close connection
//		this.MSservice.closeConnect();
//	}
//
//	public void testremoveRelationship_history() {
//		IIssue story = new Issue();
//		story.setIssueID(1);
//		story.setSummary("Story_Name_One");
//		story.setDescription("Story_Desc_One");
//		// open connection
//		this.MSservice.openConnect();
//		story.setProjectID(this.CP.getProjectList().get(0).getName());
//		story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
//		long storyID = this.MSservice.newIssue(story);
//		assertEquals(storyID, (long) 1);
//
//		IIssue storyOne = this.MSservice.getIssue(storyID);
//		assertEquals(storyOne.getSummary(), "Story_Name_One");
//		assertEquals(storyOne.getDescription(), "Story_Desc_One");
//		assertEquals(storyOne.getCategory(), ScrumEnum.STORY_ISSUE_TYPE);
//
//		List<IIssueHistory> histories = storyOne.getHistory();
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//
//		IIssue task = new Issue();
//		task.setIssueID(2);
//		task.setSummary("Task_Name_One");
//		task.setDescription("Task_Desc_One");
//		task.setProjectID(this.CP.getProjectList().get(0).getName());
//		task.setCategory(ScrumEnum.TASK_ISSUE_TYPE);
//		long taskID = this.MSservice.newIssue(task);
//		assertEquals(taskID, (long) 2);
//
//		IIssue taskOne = this.MSservice.getIssue(taskID);
//		assertEquals(taskOne.getSummary(), "Task_Name_One");
//		assertEquals(taskOne.getDescription(), "Task_Desc_One");
//		assertEquals(taskOne.getCategory(), ScrumEnum.TASK_ISSUE_TYPE);
//
//		histories = taskOne.getHistory();
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//
//		Date d = new Date();
//		d.setTime(d.getTime() + 1000);
//		this.MSservice.addRelationship(storyID, taskID, ITSEnum.PARENT_RELATIONSHIP, d);
//
//		storyOne = this.MSservice.getIssue(storyID);
//		assertEquals(storyOne.getSummary(), "Story_Name_One");
//		assertEquals(storyOne.getDescription(), "Story_Desc_One");
//		assertEquals(storyOne.getCategory(), ScrumEnum.STORY_ISSUE_TYPE);
//
//		histories = storyOne.getHistory();
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//		assertEquals(histories.get(1).getFieldName(), "Relationship added ");
//		assertEquals(histories.get(1).getNewValue(), "2");
//		assertEquals(histories.get(1).getOldValue(), "2");
//		assertEquals(histories.get(1).getType(), 18);		// meaningless
//
//		taskOne = this.MSservice.getIssue(taskID);
//		assertEquals(taskOne.getSummary(), "Task_Name_One");
//		assertEquals(taskOne.getDescription(), "Task_Desc_One");
//		assertEquals(taskOne.getCategory(), ScrumEnum.TASK_ISSUE_TYPE);
//		histories = taskOne.getHistory();
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//		assertEquals(histories.get(1).getFieldName(), "Relationship added ");
//		assertEquals(histories.get(1).getNewValue(), "1");
//		assertEquals(histories.get(1).getOldValue(), "3");
//		assertEquals(histories.get(1).getType(), 18);		// meaningless
//
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		// test method
//		this.MSservice.removeRelationship(storyID, taskID, ITSEnum.PARENT_RELATIONSHIP);
//		// test method
//
//		storyOne = this.MSservice.getIssue(storyID);
//		assertEquals(storyOne.getSummary(), "Story_Name_One");
//		assertEquals(storyOne.getDescription(), "Story_Desc_One");
//		assertEquals(storyOne.getCategory(), ScrumEnum.STORY_ISSUE_TYPE);
//		histories = storyOne.getHistory();
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//		assertEquals(histories.get(1).getFieldName(), "Relationship added ");
//		assertEquals(histories.get(1).getNewValue(), "2");
//		assertEquals(histories.get(1).getOldValue(), "2");
//		assertEquals(histories.get(1).getType(), 18);		// meaningless
//		assertEquals(histories.get(2).getFieldName(), "Relationship deleted ");
//		assertEquals(histories.get(2).getNewValue(), "2");
//		assertEquals(histories.get(2).getOldValue(), "2");
//		assertEquals(histories.get(2).getType(), 19);		// meaningless
//
//		taskOne = this.MSservice.getIssue(taskID);
//		assertEquals(taskOne.getSummary(), "Task_Name_One");
//		assertEquals(taskOne.getDescription(), "Task_Desc_One");
//		assertEquals(taskOne.getCategory(), ScrumEnum.TASK_ISSUE_TYPE);
//		histories = taskOne.getHistory();
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//		assertEquals(histories.get(1).getFieldName(), "Relationship added ");
//		assertEquals(histories.get(1).getNewValue(), "1");
//		assertEquals(histories.get(1).getOldValue(), "3");
//		assertEquals(histories.get(1).getType(), 18);		// meaningless
//		assertEquals(histories.get(2).getFieldName(), "Relationship deleted ");
//		assertEquals(histories.get(2).getNewValue(), "1");
//		assertEquals(histories.get(2).getOldValue(), "3");
//		assertEquals(histories.get(2).getType(), 19);		// meaningless
//
//		// close connection
//		this.MSservice.closeConnect();
//	}
//
//	public void testupdateHandler_history() {
//		IIssue task = new Issue();
//		task.setIssueID(1);
//		task.setSummary("Task_Name_One");
//		task.setDescription("Task_Desc_One");
//		task.setProjectID(this.CP.getProjectList().get(0).getName());
//		task.setCategory(ScrumEnum.TASK_ISSUE_TYPE);
//
//		// open connection
//		this.MSservice.openConnect();
//
//		long taskID = this.MSservice.newIssue(task);
//		assertEquals(taskID, (long) 1);
//
//		IIssue taskOne = this.MSservice.getIssue(taskID);
//		assertEquals(taskOne.getSummary(), "Task_Name_One");
//		assertEquals(taskOne.getDescription(), "Task_Desc_One");
//		assertEquals(taskOne.getCategory(), ScrumEnum.TASK_ISSUE_TYPE);
//
//		List<IIssueHistory> histories = taskOne.getHistory();
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//
//		Date d = new Date();
//		d.setTime(d.getTime() + 1000);
//		// test method
//		this.MSservice.updateHandler(taskOne, "administrator", d);
//		// test method
//
//		int adminId = this.MSservice.getUserID("administrator");
//
//		taskOne = this.MSservice.getIssue(taskID);
//		assertEquals(taskOne.getSummary(), "Task_Name_One");
//		assertEquals(taskOne.getDescription(), "Task_Desc_One");
//		assertEquals(taskOne.getCategory(), ScrumEnum.TASK_ISSUE_TYPE);
//		histories = taskOne.getHistory();
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//		assertEquals(histories.get(1).getFieldName(), "handler_id");
//		assertEquals(histories.get(1).getNewValue(), Integer.toString(adminId));
//		assertEquals(histories.get(1).getOldValue(), "0");
//		assertEquals(histories.get(1).getType(), 0);
//		assertEquals(histories.get(2).getFieldName(), "status");
//		assertEquals(histories.get(2).getNewValue(), "50");
//		assertEquals(histories.get(2).getOldValue(), "10");
//		assertEquals(histories.get(2).getType(), 0);
//
//		d.setTime(d.getTime() + 1000);
//		// 測試塞入一樣狀態的 history，但是會發生日期無法塞進去，所以只好暫停一下進廣告
//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		// test method
//		this.MSservice.updateHandler(taskOne, "administrator", d);
//		// test method
//
//		adminId = this.MSservice.getUserID("administrator");
//
//		taskOne = this.MSservice.getIssue(taskID);
//		assertEquals(taskOne.getSummary(), "Task_Name_One");
//		assertEquals(taskOne.getDescription(), "Task_Desc_One");
//		assertEquals(taskOne.getCategory(), ScrumEnum.TASK_ISSUE_TYPE);
//		histories = taskOne.getHistory();
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//		assertEquals(histories.get(1).getFieldName(), "handler_id");
//		assertEquals(histories.get(1).getNewValue(), Integer.toString(adminId));
//		assertEquals(histories.get(1).getOldValue(), "0");
//		assertEquals(histories.get(1).getType(), 0);
//		assertEquals(histories.get(2).getFieldName(), "status");
//		assertEquals(histories.get(2).getNewValue(), "50");
//		assertEquals(histories.get(2).getOldValue(), "10");
//		assertEquals(histories.get(2).getType(), 0);
//		assertEquals(histories.get(3).getFieldName(), "handler_id");
//		assertEquals(histories.get(3).getNewValue(), Integer.toString(adminId));
//		assertEquals(histories.get(3).getOldValue(), Integer.toString(adminId));
//		assertEquals(histories.get(3).getType(), 0);
//
//		// close connection
//		this.MSservice.closeConnect();
//	}
//
//	public void testupdateName_history() {
//		IIssue task = new Issue();
//		task.setIssueID(1);
//		task.setSummary("Task_Name_One");
//		task.setDescription("Task_Desc_One");
//		task.setProjectID(this.CP.getProjectList().get(0).getName());
//		task.setCategory(ScrumEnum.TASK_ISSUE_TYPE);
//
//		// open connection
//		this.MSservice.openConnect();
//
//		long taskID = this.MSservice.newIssue(task);
//		assertEquals(taskID, (long) 1);
//
//		IIssue taskOne = this.MSservice.getIssue(taskID);
//		assertEquals(taskOne.getSummary(), "Task_Name_One");
//		assertEquals(taskOne.getDescription(), "Task_Desc_One");
//		assertEquals(taskOne.getCategory(), ScrumEnum.TASK_ISSUE_TYPE);
//
//		List<IIssueHistory> histories = taskOne.getHistory();
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//
//		Date d = new Date();
//		d.setTime(d.getTime() + 1000);
//		// test method
//		this.MSservice.updateName(task, "NEW_NAME", new Date());
//		// test method
//
//		taskOne = this.MSservice.getIssue(taskID);
//		assertEquals(taskOne.getSummary(), "NEW_NAME");
//		assertEquals(taskOne.getDescription(), "Task_Desc_One");
//		assertEquals(taskOne.getCategory(), ScrumEnum.TASK_ISSUE_TYPE);
//		histories = taskOne.getHistory();
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//		assertEquals(histories.get(1).getFieldName(), "summary");
//		assertEquals(histories.get(1).getNewValue(), "NEW_NAME");
//		assertEquals(histories.get(1).getOldValue(), "Task_Name_One");
//		assertEquals(histories.get(1).getType(), 0);
//
//		// 一樣的名稱再設定一次
//		d.setTime(d.getTime() + 1000);
//		// test method
//		this.MSservice.updateName(task, "NEW_NAME", d);
//		// test method
//
//		taskOne = this.MSservice.getIssue(taskID);
//		assertEquals(taskOne.getSummary(), "NEW_NAME");
//		assertEquals(taskOne.getDescription(), "Task_Desc_One");
//		assertEquals(taskOne.getCategory(), ScrumEnum.TASK_ISSUE_TYPE);
//		histories = taskOne.getHistory();
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//		assertEquals(histories.get(1).getFieldName(), "summary");
//		assertEquals(histories.get(1).getNewValue(), "NEW_NAME");
//		assertEquals(histories.get(1).getOldValue(), "Task_Name_One");
//		assertEquals(histories.get(1).getType(), 0);
//		assertEquals(histories.get(2).getFieldName(), "summary");
//		assertEquals(histories.get(2).getNewValue(), "NEW_NAME");
//		assertEquals(histories.get(2).getOldValue(), "Task_Name_One");
//		assertEquals(histories.get(2).getType(), 0);
//
//		// close connection
//		this.MSservice.closeConnect();
//	}
//
//	public void testchangeStatusToClosed_hisotry() {
//		IIssue task = new Issue();
//		task.setIssueID(1);
//		task.setSummary("Task_Name_One");
//		task.setDescription("Task_Desc_One");
//		task.setProjectID(this.CP.getProjectList().get(0).getName());
//		task.setCategory(ScrumEnum.TASK_ISSUE_TYPE);
//
//		// open connection
//		this.MSservice.openConnect();
//
//		long taskID = this.MSservice.newIssue(task);
//		assertEquals(taskID, (long) 1);
//
//		IIssue taskOne = this.MSservice.getIssue(taskID);
//		assertEquals(taskOne.getSummary(), "Task_Name_One");
//		assertEquals(taskOne.getDescription(), "Task_Desc_One");
//		assertEquals(taskOne.getCategory(), ScrumEnum.TASK_ISSUE_TYPE);
//
//		List<IIssueHistory> histories = taskOne.getHistory();
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//
//		Date d = new Date();
//		d.setTime(d.getTime() + 1000);
//		this.MSservice.updateHandler(taskOne, "administrator", d);
//
//		int adminId = this.MSservice.getUserID("administrator");
//
//		taskOne = this.MSservice.getIssue(taskID);
//		assertEquals(taskOne.getSummary(), "Task_Name_One");
//		assertEquals(taskOne.getDescription(), "Task_Desc_One");
//		assertEquals(taskOne.getCategory(), ScrumEnum.TASK_ISSUE_TYPE);
//		histories = taskOne.getHistory();
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//		assertEquals(histories.get(1).getFieldName(), "handler_id");
//		assertEquals(histories.get(1).getNewValue(), Integer.toString(adminId));
//		assertEquals(histories.get(1).getOldValue(), "0");
//		assertEquals(histories.get(1).getType(), 0);
//		assertEquals(histories.get(2).getFieldName(), "status");
//		assertEquals(histories.get(2).getNewValue(), "50");
//		assertEquals(histories.get(2).getOldValue(), "10");
//		assertEquals(histories.get(2).getType(), 0);
//
//		d.setTime(d.getTime() + 1000);
//		// 測試塞入一樣狀態的 history，但是會發生日期無法塞進去，所以只好暫停一下進廣告
//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		this.MSservice.updateHandler(taskOne, "administrator", d);
//
//		adminId = this.MSservice.getUserID("administrator");
//
//		taskOne = this.MSservice.getIssue(taskID);
//		assertEquals(taskOne.getSummary(), "Task_Name_One");
//		assertEquals(taskOne.getDescription(), "Task_Desc_One");
//		assertEquals(taskOne.getCategory(), ScrumEnum.TASK_ISSUE_TYPE);
//		histories = taskOne.getHistory();
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//		assertEquals(histories.get(1).getFieldName(), "handler_id");
//		assertEquals(histories.get(1).getNewValue(), Integer.toString(adminId));
//		assertEquals(histories.get(1).getOldValue(), "0");
//		assertEquals(histories.get(1).getType(), 0);
//		assertEquals(histories.get(2).getFieldName(), "status");
//		assertEquals(histories.get(2).getNewValue(), "50");
//		assertEquals(histories.get(2).getOldValue(), "10");
//		assertEquals(histories.get(2).getType(), 0);
//		assertEquals(histories.get(3).getFieldName(), "handler_id");
//		assertEquals(histories.get(3).getNewValue(), Integer.toString(adminId));
//		assertEquals(histories.get(3).getOldValue(), Integer.toString(adminId));
//		assertEquals(histories.get(3).getType(), 0);
//
//		d.setTime(d.getTime() + 1000);
//		this.MSservice.changeStatusToClosed(taskID, ITSEnum.FIXED_RESOLUTION, "Task_New_Note", d);
//		taskOne = this.MSservice.getIssue(taskID);
//		assertEquals(taskOne.getSummary(), "Task_Name_One");
//		assertEquals(taskOne.getDescription(), "Task_Desc_One");
//		assertEquals(taskOne.getCategory(), ScrumEnum.TASK_ISSUE_TYPE);
//		histories = taskOne.getHistory();
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//		assertEquals(histories.get(1).getFieldName(), "handler_id");
//		assertEquals(histories.get(1).getNewValue(), Integer.toString(adminId));
//		assertEquals(histories.get(1).getOldValue(), "0");
//		assertEquals(histories.get(1).getType(), 0);
//		assertEquals(histories.get(2).getFieldName(), "status");
//		assertEquals(histories.get(2).getNewValue(), "50");
//		assertEquals(histories.get(2).getOldValue(), "10");
//		assertEquals(histories.get(2).getType(), 0);
//		assertEquals(histories.get(3).getFieldName(), "handler_id");
//		assertEquals(histories.get(3).getNewValue(), Integer.toString(adminId));
//		assertEquals(histories.get(3).getOldValue(), Integer.toString(adminId));
//		assertEquals(histories.get(3).getType(), 0);
//		assertEquals(histories.get(4).getFieldName(), "status");
//		assertEquals(histories.get(4).getNewValue(), "90");
//		assertEquals(histories.get(4).getOldValue(), "50");
//		assertEquals(histories.get(4).getType(), 0);
//
//		// close connection
//		this.MSservice.closeConnect();
//	}
//
//	public void testinsertBugNote_hisotry() {
//		IIssue task = new Issue();
//		task.setIssueID(1);
//		task.setSummary("Task_Name_One");
//		task.setDescription("Task_Desc_One");
//		task.setProjectID(this.CP.getProjectList().get(0).getName());
//		task.setCategory(ScrumEnum.TASK_ISSUE_TYPE);
//
//		// open connection
//		this.MSservice.openConnect();
//
//		long taskID = this.MSservice.newIssue(task);
//		assertEquals(taskID, (long) 1);
//
//		IIssue taskOne = this.MSservice.getIssue(taskID);
//		assertEquals(taskOne.getSummary(), "Task_Name_One");
//		assertEquals(taskOne.getDescription(), "Task_Desc_One");
//		assertEquals(taskOne.getCategory(), ScrumEnum.TASK_ISSUE_TYPE);
//
//		List<IIssueHistory> histories = taskOne.getHistory();
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//
//		// 日期無法塞進去，所以只好暫停一下進廣告
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//
//		// test method
//		this.MSservice.insertBugNote(taskID, "");
//		// test method
//
//		taskOne = this.MSservice.getIssue(taskID);
//		assertEquals(taskOne.getSummary(), "Task_Name_One");
//		assertEquals(taskOne.getDescription(), "Task_Desc_One");
//		assertEquals(taskOne.getCategory(), ScrumEnum.TASK_ISSUE_TYPE);
//		assertEquals(taskOne.getNotes(), "");
//
//		histories = taskOne.getHistory();
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//		assertEquals(histories.get(1).getFieldName(), "");
//		assertEquals(histories.get(1).getNewValue(), "0");
//		assertEquals(histories.get(1).getOldValue(), "1");
//		assertEquals(histories.get(1).getType(), 2);
//
//		// close connection
//		this.MSservice.closeConnect();
//	}
//
//	public void testreopenStatusToAssigned_hisotry() {
//		IIssue task = new Issue();
//		task.setIssueID(1);
//		task.setSummary("Task_Name_One");
//		task.setDescription("Task_Desc_One");
//		task.setProjectID(this.CP.getProjectList().get(0).getName());
//		task.setCategory(ScrumEnum.TASK_ISSUE_TYPE);
//
//		// open connection
//		this.MSservice.openConnect();
//
//		long taskID = this.MSservice.newIssue(task);
//		assertEquals(taskID, (long) 1);
//
//		IIssue taskOne = this.MSservice.getIssue(taskID);
//		assertEquals(taskOne.getSummary(), "Task_Name_One");
//		assertEquals(taskOne.getDescription(), "Task_Desc_One");
//		assertEquals(taskOne.getCategory(), ScrumEnum.TASK_ISSUE_TYPE);
//
//		List<IIssueHistory> histories = taskOne.getHistory();
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//
//		Date d = new Date();
//		d.setTime(d.getTime() + 1000);
//		// test method
//		this.MSservice.reopenStatusToAssigned(taskID, task.getSummary(), "Note", d);
//		// test method
//
//		taskOne = this.MSservice.getIssue(taskID);
//		assertEquals(taskOne.getSummary(), "Task_Name_One");
//		assertEquals(taskOne.getDescription(), "Task_Desc_One");
//		assertEquals(taskOne.getCategory(), ScrumEnum.TASK_ISSUE_TYPE);
//
//		histories = taskOne.getHistory();
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//		assertEquals(histories.get(1).getFieldName(), IIssueHistory.STATUS_FIELD_NAME);
//		assertEquals(histories.get(1).getNewValue(), "50");
//		assertEquals(histories.get(1).getOldValue(), "10");
//		assertEquals(histories.get(1).getType(), 0);
//
//		// close connection
//		this.MSservice.closeConnect();
//	}
//
//	public void testresetStatusToNew_hisotry() {
//		IIssue task = new Issue();
//		task.setIssueID(1);
//		task.setSummary("Task_Name_One");
//		task.setDescription("Task_Desc_One");
//		task.setProjectID(this.CP.getProjectList().get(0).getName());
//		task.setCategory(ScrumEnum.TASK_ISSUE_TYPE);
//
//		// open connection
//		this.MSservice.openConnect();
//
//		long taskID = this.MSservice.newIssue(task);
//		assertEquals(taskID, (long) 1);
//
//		IIssue taskOne = this.MSservice.getIssue(taskID);
//		assertEquals(taskOne.getSummary(), "Task_Name_One");
//		assertEquals(taskOne.getDescription(), "Task_Desc_One");
//		assertEquals(taskOne.getCategory(), ScrumEnum.TASK_ISSUE_TYPE);
//
//		List<IIssueHistory> histories = taskOne.getHistory();
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//
//		Date d = new Date();
//		d.setTime(d.getTime() + 1000);
//		this.MSservice.reopenStatusToAssigned(taskID, task.getSummary(), "Note", d);
//
//		taskOne = this.MSservice.getIssue(taskID);
//		assertEquals(taskOne.getSummary(), "Task_Name_One");
//		assertEquals(taskOne.getDescription(), "Task_Desc_One");
//		assertEquals(taskOne.getCategory(), ScrumEnum.TASK_ISSUE_TYPE);
//
//		histories = taskOne.getHistory();
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//		assertEquals(histories.get(1).getFieldName(), IIssueHistory.STATUS_FIELD_NAME);
//		assertEquals(histories.get(1).getNewValue(), "50");
//		assertEquals(histories.get(1).getOldValue(), "10");
//		assertEquals(histories.get(1).getType(), 0);
//
//		d.setTime(d.getTime() + 1000);
//		// test method
//		this.MSservice.resetStatusToNew(taskID, task.getSummary(), "Note", d);
//		// test method
//
//		taskOne = this.MSservice.getIssue(taskID);
//		assertEquals(taskOne.getSummary(), "Task_Name_One");
//		assertEquals(taskOne.getDescription(), "Task_Desc_One");
//		assertEquals(taskOne.getCategory(), ScrumEnum.TASK_ISSUE_TYPE);
//
//		histories = taskOne.getHistory();
//		assertEquals(histories.get(0).getFieldName(), "New Issue");
//		assertEquals(histories.get(0).getNewValue(), "0");
//		assertEquals(histories.get(0).getOldValue(), "0");
//		assertEquals(histories.get(0).getType(), 1);
//		assertEquals(histories.get(1).getFieldName(), IIssueHistory.STATUS_FIELD_NAME);
//		assertEquals(histories.get(1).getNewValue(), "50");
//		assertEquals(histories.get(1).getOldValue(), "10");
//		assertEquals(histories.get(1).getType(), 0);
//		assertEquals(histories.get(2).getFieldName(), IIssueHistory.STATUS_FIELD_NAME);
//		assertEquals(histories.get(2).getNewValue(), "10");
//		assertEquals(histories.get(2).getOldValue(), "50");
//		assertEquals(histories.get(2).getType(), 0);
//
//		// close connection
//		this.MSservice.closeConnect();
//	}
//
//	public void testupdateHistoryModifiedDate_history() {
//		IIssue task = new Issue();
//		task.setIssueID(1);
//		task.setSummary("Task_Name_One");
//		task.setDescription("Task_Desc_One");
//		task.setProjectID(this.CP.getProjectList().get(0).getName());
//		task.setCategory(ScrumEnum.TASK_ISSUE_TYPE);
//
//		// open connection
//		this.MSservice.openConnect();
//
//		long taskID = this.MSservice.newIssue(task);
//		assertEquals(taskID, (long) 1);
//
//		Date d = new Date();
//		d.setTime(1000);
//		// test method
//		this.MSservice.updateHistoryModifiedDate(taskID, 1, d);
//		// test method
//
//		IIssue taskOne = this.MSservice.getIssue(taskID);
//		assertEquals(taskOne.getSummary(), "Task_Name_One");
//		assertEquals(taskOne.getDescription(), "Task_Desc_One");
//		assertEquals(taskOne.getCategory(), ScrumEnum.TASK_ISSUE_TYPE);
//
//		List<IIssueHistory> histories = taskOne.getHistory();
//		assertEquals(histories.get(0).getModifyDate(), d.getTime());
//	}
//
//	public void testremoveIssue_history() {
//		IIssue task = new Issue();
//		task.setIssueID(1);
//		task.setSummary("Task_Name_One");
//		task.setDescription("Task_Desc_One");
//		task.setProjectID(this.CP.getProjectList().get(0).getName());
//		task.setCategory(ScrumEnum.TASK_ISSUE_TYPE);
//
//		// open connection
//		this.MSservice.openConnect();
//
//		long taskID = this.MSservice.newIssue(task);
//		assertEquals(taskID, (long) 1);
//
//		Date d = new Date();
//		d.setTime(1000);
//		this.MSservice.updateHistoryModifiedDate(taskID, 1, d);
//
//		IIssue taskOne = this.MSservice.getIssue(taskID);
//		assertEquals(taskOne.getSummary(), "Task_Name_One");
//		assertEquals(taskOne.getDescription(), "Task_Desc_One");
//		assertEquals(taskOne.getCategory(), ScrumEnum.TASK_ISSUE_TYPE);
//
//		List<IIssueHistory> histories = taskOne.getHistory();
//		assertEquals(histories.get(0).getModifyDate(), d.getTime());
//
//		// test method
//		this.MSservice.removeIssue(Long.toString(taskID));
//		// test method
//
//		taskOne = this.MSservice.getIssue(taskID);
//		assertEquals(taskOne, null);
//	}

//	public void testgetAttachFile() {
//		// ================ set initial data =======================
//		CreateProductBacklog CPB = new CreateProductBacklog(this.StoryCount, this.CP);
//		CPB.exe();
//		
//		long issueId = CPB.getIssueList().get(0).getIssueID();
//		AttachFileInfo attachFileInfo = new AttachFileInfo();
//        attachFileInfo.issueId = issueId;
//        attachFileInfo.name = "TESTFILE.txt";
//        attachFileInfo.path = "/abc/def/" + attachFileInfo.name;
//        attachFileInfo.issueType = AttachFileObject.TYPE_STORY;
//        attachFileInfo.contentType = "text/pain";
//        attachFileInfo.projectName = project.getName();
//		
//		// ================ set initial data =======================
//		this.MSservice.openConnect();
//		long fileId = MSservice.addAttachFile(attachFileInfo);
//		AttachFileObject attachFileObject = MSservice.getAttachFile(fileId);
//		this.MSservice.closeConnect();
//
//		assertEquals(attachFileObject.getId(), fileId);
//		assertEquals(attachFileObject.getIssueId(), attachFileInfo.issueId);
//		assertEquals(attachFileObject.getName(), attachFileInfo.name);
//		assertEquals(attachFileObject.getPath(), attachFileInfo.path);
//		assertEquals(attachFileObject.getContentType(), attachFileInfo.contentType);
//
//		// ============= release ==============
//		project = null;
//		attachFileInfo = null;
//		attachFileObject = null;
//	}
//	
	/**
	 * 主要測試getIssue方法中 mAttachFileService.initAttachFile(issue);對getIssue造成的效果是否有效
	 */
//	public void testGetIssue_AboutAttachFile() {
//		// new issue data
//		long issueId = 1;
//		ArrayList<Long> fileIdList = new ArrayList<Long>();
//		IIssue story = new Issue();
//		story.setIssueID(issueId);
//		story.setSummary("Story_Name_One");
//		story.setDescription("Story_Desc_One");
//		story.setProjectID(this.CP.getProjectList().get(0).getName());
//		story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
//
//		this.MSservice.openConnect();
//		this.MSservice.newIssue(story);
//		IIssue storyOne = this.MSservice.getIssue(issueId);
//		assertEquals(storyOne.getAttachFiles().size(), 0);
//		
//		// add three file to story
//		int attachFileCount = 3;
//		ArrayList<AttachFileInfo> attachFileInfolist = new ArrayList<AttachFileInfo>();
//		for(int i = 1; i <= attachFileCount; i++) {
//			AttachFileInfo attachFileInfo = new AttachFileInfo();
//	        attachFileInfo.issueId = story.getIssueID();
//	        attachFileInfo.name = "TESTFILE_" + i + ".txt";
//	        attachFileInfo.contentType = "text/pain";
//	        attachFileInfo.path = "/abc/def/TESTFILE_" + i + ".txt";
//	        attachFileInfo.projectName = project.getName();
//	        fileIdList.add(this.MSservice.addAttachFile(attachFileInfo));
//	        attachFileInfolist.add(attachFileInfo);
//		}
//		
//		// storyOne這裡要refresh一次，因為已經是dirty data
//		storyOne = this.MSservice.getIssue(issueId);
//		ArrayList<AttachFileObject> attachFileObjects = storyOne.getAttachFiles();
//		this.MSservice.closeConnect();
//		
//		assertEquals(attachFileObjects.size(), attachFileCount);
//		for(int i = 0; i < attachFileCount; i++) {
//	        assertEquals((long)fileIdList.get(i), attachFileObjects.get(i).getId());
//	        assertEquals(attachFileInfolist.get(i).name, attachFileObjects.get(i).getName());
//	        assertEquals(attachFileInfolist.get(i).issueId, attachFileObjects.get(i).getIssueId());
//	        assertEquals(attachFileInfolist.get(i).issueType, attachFileObjects.get(i).getIssueType());
//	        assertEquals(attachFileInfolist.get(i).contentType, attachFileObjects.get(i).getContentType());
//	        assertEquals(attachFileInfolist.get(i).path, attachFileObjects.get(i).getPath());
//		}
//	}

	/**
	 * 主要測試getIssues方法中 p.s.這裡是複數s mAttachFileService.initAttachFile(issue);對getIssues造成的效果是否有效
	 */
//	public void testGetIssues_AboutAttachFile() {
//		ArrayList<IIssue> storyList = new ArrayList<IIssue>();
//		AttachFileInfo attachFileInfo = new AttachFileInfo();
//		long fileId = 0;
//		assertEquals(storyList.size(), 0);
//		
//		this.MSservice.openConnect();
//		for (int i = 0; i < 3; i++) {
//			IIssue story = new Issue();
//			story.setIssueID(i + 1);
//			story.setSummary("Story_Name_" + (i + 1));
//			story.setDescription("Story_Desc_" + (i + 1));
//			addTagElement(story, Integer.toString((i + 1) * 5), Integer.toString((i + 1) * 10),
//			        Integer.toString((i + 1) * 15), "Demo_" + (i + 1), "Note_" + (i + 1));
//			story.setProjectID(project.getName());
//			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
//			this.MSservice.newIssue(story);
//			// 第二筆資料附加測試檔案
//			if (i == 1) {
//		        attachFileInfo.issueId = story.getIssueID();
//		        attachFileInfo.name = "TESTFILE.txt";
//		        attachFileInfo.contentType = "text/pain";
//		        attachFileInfo.path = "/abc/def/TESTFILE.txt";
//		        attachFileInfo.projectName = project.getName();
//		        fileId = this.MSservice.addAttachFile(attachFileInfo);
//			}
//		}
//		
//		// refresh dirty story
//		storyList.addAll(Arrays.asList(this.MSservice.getIssues(project.getName(), ScrumEnum.STORY_ISSUE_TYPE)));
//		AttachFileObject attachFileObject = this.MSservice.getAttachFile(fileId);
//		this.MSservice.closeConnect();
//		
//		assertEquals(storyList.size(), 3);
//		// 第一個story不存在任何檔案
//		assertEquals(storyList.get(0).getAttachFiles().size(), 0);
//		// 第二個story存在一個測試檔案檔案
//		assertEquals(storyList.get(1).getAttachFiles().size(), 1);
//		assertEquals(fileId, attachFileObject.getId());
//		assertEquals(attachFileInfo.name, attachFileObject.getName());
//		assertEquals(attachFileInfo.path, attachFileObject.getPath());
//		assertEquals(attachFileInfo.issueId, attachFileObject.getIssueId());
//		assertEquals(attachFileInfo.issueType, attachFileObject.getIssueType());
//		// 第三個story不存在任何檔案
//		assertEquals(storyList.get(2).getAttachFiles().size(), 0);
//	}

	/**
	 * 主要測試getStorys方法中 mAttachFileService.initAttachFile(story);對getStorys造成的效果是否有效
	 */
	public void testGetStories_AboutAttachFile() {
		ArrayList<IStory> storyList = new ArrayList<IStory>();
		AttachFileInfo attachFileInfo = new AttachFileInfo();
		long fileId = 0;
		assertEquals(storyList.size(), 0);
		
		this.MSservice.openConnect();
		for (int i = 0; i < 3; i++) {
			IIssue story = new Issue();
			story.setIssueID(i + 1);
			story.setSummary("Story_Name_" + i + 1);
			story.setDescription("Story_Desc_" + i + 1);
			addTagElement(story, Integer.toString((i + 1) * 5), Integer.toString((i + 1) * 10),
			        Integer.toString((i + 1) * 15), "Demo_" + i + 1, "Note_" + i + 1);
			story.setProjectID(project.getName());
			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
			this.MSservice.newIssue(story);
			// 第二筆資料附加測試檔案
			if (i == 1) {
				attachFileInfo.issueId = story.getIssueID();
		        attachFileInfo.name = "TESTFILE.txt";
		        attachFileInfo.contentType = "text/pain";
		        attachFileInfo.path = "/abc/def/TESTFILE.txt";
		        attachFileInfo.projectName = project.getName();
		        fileId = this.MSservice.addAttachFile(attachFileInfo);
			}
		}
		
		// refresh dirty story
		storyList = (ArrayList<IStory>) this.MSservice.getStorys(project.getName());
		AttachFileObject attachFileObject = this.MSservice.getAttachFile(fileId);
		this.MSservice.closeConnect();
		
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
	public void testGetAttachFile_AboutAttachFile() {
		long fileId;
		IIssue story = new Issue();
		story.setIssueID(1);
		story.setSummary("Story_Name_One");
		story.setDescription("Story_Desc_One");
		story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
		this.MSservice.openConnect();
		System.out.println("aasd123123");
		long storyId = this.MSservice.newIssue(story);
		
		System.out.println("aasd");
		AttachFileInfo attachFileInfo = new AttachFileInfo();
		attachFileInfo.issueId = storyId;
        attachFileInfo.name = "TESTFILE.txt";
        attachFileInfo.contentType = "text/pain";
        attachFileInfo.path = "/abc/def/TESTFILE.txt";
        attachFileInfo.projectName = project.getName();
        
		System.out.println("id = " + storyId);
		fileId = this.MSservice.addAttachFile(attachFileInfo);
		// refresh dirty story
		story = this.MSservice.getIssue(storyId);
		AttachFileObject attachFileObject = this.MSservice.getAttachFile(fileId);
		this.MSservice.closeConnect();
		
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
//	public void testGetIssues_Date_AboutGetIssueNotes() {
//		IProject project = this.CP.getProjectList().get(0);
//		this.MSservice.openConnect();
//		MantisNoteService MNService = new MantisNoteService(this.MSservice.getControl(), configuration);
//		TextParserGeneraterForNote noteTextHelper;
//		List<IIssue> issueList = new LinkedList<IIssue>();
//
//		// new 10 issues
//		int dataCount = 10;
//		for (int index = 0; index < dataCount; index++) {
//			IIssue story = new Issue();
//			story.setIssueID(index + 1);
//			story.setSummary("Story_Name_" + Integer.toString(index + 1));
//			story.setDescription("Story_Desc_" + Integer.toString(index + 1));
//
//			story.setProjectID(project.getName());
//			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
//
//			this.MSservice.newIssue(story);
//			issueList.add(story);
//		}
//
//		// insert issues' notes
//		int imp = 201, est = 21, val = 251;
//		for (int index = 0; index < issueList.size(); index++) {
//			String importance = Integer.toString(imp + index);
//			String estimation = Integer.toString(est + index);
//			String value = Integer.toString(val + index);
//			String howToDemo = "demo_" + Integer.toString(index + 1);
//			String notes = "note_" + Integer.toString(index + 1);
//
//			// generate note text
//			noteTextHelper = new TextParserGeneraterForNote();
//			String noteText = noteTextHelper.generaterNoteText(importance, estimation, value, howToDemo, notes);
//
//			// test method
//			MNService.insertBugNote(issueList.get(index).getIssueID(), noteText);
//		}
//
//		Calendar calendar = Calendar.getInstance();
//		calendar.add(Calendar.DATE, 1);
//
//		// test method
//		IIssue[] issueSet = this.MSservice.getIssues(project.getName(), ScrumEnum.STORY_ISSUE_TYPE, null, null, calendar.getTime());
//		// test method
//
//		// assert the issues' notes info.
//		for (int index = 0; index < issueSet.length; index++) {
//			String importance = Integer.toString(imp + index);
//			String estimation = Integer.toString(est + index);
//			String value = Integer.toString(val + index);
//			String howToDemo = "demo_" + Integer.toString(index + 1);
//			String notes = "note_" + Integer.toString(index + 1);
//			for (IIssueNote note : issueSet[index].getIssueNotes()) {
//				noteTextHelper = new TextParserGeneraterForNote();
//				noteTextHelper.parserNoteText(note.getText());
//				assertEquals(index + 1, note.getIssueID());					// 1, 2 ..
//				assertEquals(index + 1, note.getNoteID());					// 1, 2 ..
//				// assertEquals("administrator", note.getHandler()); // default = 1 (administrator)
//				assertEquals("admin", note.getHandler());			// default = 1 (administrator)
//				assertEquals(importance, noteTextHelper.getImportance());	// 201, 202, 203 ..
//				assertEquals(estimation, noteTextHelper.getEstimation());	// 21, 22 , 23 ..
//				assertEquals(value, noteTextHelper.getValue());				// 251, 252, 253 ..
//				assertEquals(howToDemo, noteTextHelper.getHowToDemo());		// demo_1, 2 ..
//				assertEquals(notes, noteTextHelper.getNotes());				// note_1, 2 ..
//			}
//		}
//		// close connection
//		this.MSservice.closeConnect();
//	}
//
//	public void testGetIssue_IssueID_AboutGetIssueNotes() {
//		IProject project = this.CP.getProjectList().get(0);
//		this.MSservice.openConnect();
//		MantisNoteService MNService = new MantisNoteService(this.MSservice.getControl(), configuration);
//		TextParserGeneraterForNote noteTextHelper;
//		List<IIssue> issueList = new LinkedList<IIssue>();
//
//		// new 10 issues
//		int dataCount = 10;
//		for (int index = 0; index < dataCount; index++) {
//			IIssue story = new Issue();
//			story.setIssueID(index + 1);
//			story.setSummary("Story_Name_" + Integer.toString(index + 1));
//			story.setDescription("Story_Desc_" + Integer.toString(index + 1));
//
//			story.setProjectID(project.getName());
//			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
//
//			this.MSservice.newIssue(story);
//			issueList.add(story);
//		}
//
//		// insert issues' notes
//		int imp = 201, est = 21, val = 251;
//		for (int index = 0; index < issueList.size(); index++) {
//			String importance = Integer.toString(imp + index);
//			String estimation = Integer.toString(est + index);
//			String value = Integer.toString(val + index);
//			String howToDemo = "demo_" + Integer.toString(index + 1);
//			String notes = "note_" + Integer.toString(index + 1);
//
//			// generate note text
//			noteTextHelper = new TextParserGeneraterForNote();
//			String noteText = noteTextHelper.generaterNoteText(importance, estimation, value, howToDemo, notes);
//
//			// test method
//			MNService.insertBugNote(issueList.get(index).getIssueID(), noteText);
//		}
//
//		Calendar calendar = Calendar.getInstance();
//		calendar.add(Calendar.DATE, 1);
//
//		// assert the issues' notes info.
//		for (int index = 0; index < dataCount; index++) {
//			long issueID = (long) (index + 1);
//			// test method
//			List<IIssueNote> noteList = this.MSservice.getIssue(issueID).getIssueNotes();
//			// test method
//
//			String importance = Integer.toString(imp + index);
//			String estimation = Integer.toString(est + index);
//			String value = Integer.toString(val + index);
//			String howToDemo = "demo_" + Integer.toString(index + 1);
//			String notes = "note_" + Integer.toString(index + 1);
//
//			for (IIssueNote note : noteList) {
//				noteTextHelper = new TextParserGeneraterForNote();
//				noteTextHelper.parserNoteText(note.getText());
//				assertEquals(index + 1, note.getIssueID());					// 1, 2 ..
//				assertEquals(index + 1, note.getNoteID());					// 1, 2 ..
//				// assertEquals("administrator", note.getHandler()); // default = 1 (administrator)
//				assertEquals("admin", note.getHandler());			// default = 1 (administrator)
//				assertEquals(importance, noteTextHelper.getImportance());	// 201, 202, 203 ..
//				assertEquals(estimation, noteTextHelper.getEstimation());	// 21, 22 , 23 ..
//				assertEquals(value, noteTextHelper.getValue());				// 251, 252, 253 ..
//				assertEquals(howToDemo, noteTextHelper.getHowToDemo());		// demo_1, 2 ..
//				assertEquals(notes, noteTextHelper.getNotes());				// note_1, 2 ..
//			}
//		}
//		// close connection
//		this.MSservice.closeConnect();
//	}
//
//	public void testUpdateBugNote() {
//		IProject project = this.CP.getProjectList().get(0);
//		this.MSservice.openConnect();
//		MantisNoteService MNService = new MantisNoteService(this.MSservice.getControl(), configuration);
//		TextParserGeneraterForNote noteTextHelper;
//
//		List<IIssue> issueList = new LinkedList<IIssue>();
//		List<IIssueNote> noteList;
//
//		// new 10 issues
//		for (int index = 0; index < 10; index++) {
//			IIssue story = new Issue();
//			story.setIssueID(index + 1);
//			story.setSummary("Story_Name_" + Integer.toString(index + 1));
//			story.setDescription("Story_Desc_" + Integer.toString(index + 1));
//
//			story.setProjectID(project.getName());
//			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
//
//			this.MSservice.newIssue(story);
//			issueList.add(story);
//
//			// check the issue's note is null
//			noteList = MNService.getIssueNotes(issueList.get(index));
//			assertEquals(0, noteList.size());
//		}
//
//		// Test if there is no existing tag in bug note initially,
//		// "insertBugNote" would be called in function "updateBugNote".
//		int imp = 201, est = 21, val = 251;
//		for (int index = 0; index < issueList.size(); index++) {
//			String importance = Integer.toString(imp + index);
//			String estimation = Integer.toString(est + index);
//			String value = Integer.toString(val + index);
//			String howToDemo = "demo_" + Integer.toString(index + 1);
//			String notes = "note_" + Integer.toString(index + 1);
//
//			// test method : "updateBugNote", which would be called in function "addTagElement".
//			this.addTagElement(issueList.get(index), importance, estimation, value, howToDemo, notes);
//
//			// check the issue note info.
//			noteList = MNService.getIssueNotes(issueList.get(index));
//			assertEquals(1, noteList.size());
//			for (IIssueNote note : noteList) {
//				noteTextHelper = new TextParserGeneraterForNote();
//				noteTextHelper.parserNoteText(note.getText());
//				assertEquals(index + 1, note.getIssueID());					// 1, 2 ..
//				assertEquals(index + 1, note.getNoteID());					// 1, 2 ..
//				// assertEquals("administrator", note.getHandler()); // default = 1 (administrator)
//				assertEquals("admin", note.getHandler());			// default = 1 (administrator)
//				assertEquals(importance, noteTextHelper.getImportance());	// 201, 202, 203 ..
//				assertEquals(estimation, noteTextHelper.getEstimation());	// 21, 22 , 23 ..
//				assertEquals(value, noteTextHelper.getValue());				// 251, 252, 253 ..
//				assertEquals(howToDemo, noteTextHelper.getHowToDemo());		// demo_1, 2 ..
//				assertEquals(notes, noteTextHelper.getNotes());				// note_1, 2 ..
//			}
//		}
//		// ==============================================================
//
//		// Test if there are some tags already exist in bug note initially,
//		// then the "updateBugNote" would update the "JCIS tag" by "appending".
//		imp = 301;
//		est = 31;
//		val = 351;
//		for (int index = 0; index < issueList.size(); index++) {
//			String importance = Integer.toString(imp + index);
//			String estimation = Integer.toString(est + index);
//			String value = Integer.toString(val + index);
//			String howToDemo = "demo_" + Integer.toString(index + 11);
//			String notes = "note_" + Integer.toString(index + 11);
//
//			// test method : "updateBugNote", which would be called in function "addTagElement".
//			this.addTagElement(issueList.get(index), importance, estimation, value, howToDemo, notes);
//
//			// check the issue note info.
//			noteList = MNService.getIssueNotes(issueList.get(index));
//			assertEquals(1, noteList.size());// list size still is 1, because the query result didn't separate the note tags
//			for (IIssueNote note : noteList) {
//				noteTextHelper = new TextParserGeneraterForNote();
//				noteTextHelper.parserNoteText(note.getText());
//				assertEquals(index + 1, note.getIssueID());					// 1, 2 ..
//				assertEquals(index + 1, note.getNoteID());					// 1, 2 ..
//				// assertEquals("administrator", note.getHandler()); // default = 1 (administrator)
//				assertEquals("admin", note.getHandler());			// default = 1 (administrator)
//				assertEquals(importance, noteTextHelper.getImportance());	// 301, 302, 303 ..
//				assertEquals(estimation, noteTextHelper.getEstimation());	// 31, 32 , 33 ..
//				assertEquals(value, noteTextHelper.getValue());				// 351, 352, 353 ..
//				assertEquals(howToDemo, noteTextHelper.getHowToDemo());		// demo_1, 2 ..
//				assertEquals(notes, noteTextHelper.getNotes());				// note_1, 2 ..
//			}
//		}
//		// close connection
//		this.MSservice.closeConnect();
//
//		// release
//		project = null;
//		MNService = null;
//	}
//
//	public void testUpdateIssueNote() {
//		IProject project = this.CP.getProjectList().get(0);
//		this.MSservice.openConnect();
//		MantisNoteService MNService = new MantisNoteService(this.MSservice.getControl(), configuration);
//		TextParserGeneraterForNote noteTextHelper;
//
//		List<IIssue> issueList = new LinkedList<IIssue>();
//		List<IIssueNote> noteList;
//
//		// new 10 issues
//		for (int index = 0; index < 10; index++) {
//			IIssue story = new Issue();
//			story.setIssueID(index + 1);
//			story.setSummary("Story_Name_" + Integer.toString(index + 1));
//			story.setDescription("Story_Desc_" + Integer.toString(index + 1));
//
//			story.setProjectID(project.getName());
//			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
//
//			MSservice.newIssue(story);
//			issueList.add(story);
//
//			// check the issue's note is null
//			noteList = MNService.getIssueNotes(issueList.get(index));
//			assertEquals(0, noteList.size());
//		}
//
//		// Test if there is no existing tag in bug note initially,
//		// "insertBugNote" would be called in function "updateIssueNote".
//		int imp = 201, est = 21, val = 251;
//		for (int index = 0; index < issueList.size(); index++) {
//			String importance = Integer.toString(imp + index);
//			String estimation = Integer.toString(est + index);
//			String value = Integer.toString(val + index);
//			String howToDemo = "demo_" + Integer.toString(index + 1);
//			String notes = "note_" + Integer.toString(index + 1);
//
//			// generate note text
//			noteTextHelper = new TextParserGeneraterForNote();
//			String noteText = noteTextHelper.generaterNoteText(importance, estimation, value, howToDemo, notes);
//
//			IIssueNote issueNote = new IssueNote();
//			issueNote.setText(noteText);
//
//			// test method
//			MNService.updateIssueNote(issueList.get(index), issueNote);
//
//			// set the issue's noteList
//			noteList = MNService.getIssueNotes(issueList.get(index));
//			issueList.get(index).setIssueNotes(noteList);
//
//			// check the issue note info.
//			noteList = MNService.getIssueNotes(issueList.get(index));
//			assertEquals(1, noteList.size());
//			for (IIssueNote note : noteList) {
//				noteTextHelper = new TextParserGeneraterForNote();
//				noteTextHelper.parserNoteText(note.getText());
//				assertEquals(index + 1, note.getIssueID());					// 1, 2 ..
//				assertEquals(index + 1, note.getNoteID());					// 1, 2 ..
//				// assertEquals("administrator", note.getHandler()); // default = 1 (administrator)
//				assertEquals("admin", note.getHandler());			// default = 1 (administrator)
//				assertEquals(importance, noteTextHelper.getImportance());	// 201, 202, 203 ..
//				assertEquals(estimation, noteTextHelper.getEstimation());	// 21, 22 , 23 ..
//				assertEquals(value, noteTextHelper.getValue());				// 251, 252, 253 ..
//				assertEquals(howToDemo, noteTextHelper.getHowToDemo());		// demo_1, 2 ..
//				assertEquals(notes, noteTextHelper.getNotes());				// note_1, 2 ..
//			}
//		}
//		// ==============================================================
//
//		// Test if there are some tags already exist in bug note initially,
//		// then the "updateIssueNote" would update the "JCIS tag" by "overriding".
//		imp = 301;
//		est = 31;
//		val = 351;
//		int index = 0;
//		for (IIssue issue : issueList) {
//			String importance = Integer.toString(imp + index);
//			String estimation = Integer.toString(est + index);
//			String value = Integer.toString(val + index);
//			String howToDemo = "update_demo_" + Integer.toString(index + 1);
//			String notes = "update_note_" + Integer.toString(index + 1);
//
//			// generate note text
//			noteTextHelper = new TextParserGeneraterForNote();
//			String noteText = noteTextHelper.generaterNoteText(importance, estimation, value, howToDemo, notes);
//
//			// new issueNote
//			IIssueNote issueNote = new IssueNote();
//			issueNote.setIssueID(issueList.get(index).getIssueID());
//			issueNote.setText(noteText);
//			issueNote.setHandler("");
//			issueNote.setNoteID(issueList.get(index).getIssueID());
//
//			// add new issueNote to issue
//			issue.addIssueNote(issueNote);
//
//			// test method
//			MNService.updateIssueNote(issue, issueNote);
//
//			// check the issue note info.
//			noteList = MNService.getIssueNotes(issueList.get(index));
//			assertEquals(1, noteList.size());
//			for (IIssueNote note : noteList) {
//				noteTextHelper = new TextParserGeneraterForNote();
//				noteTextHelper.parserNoteText(note.getText());
//				assertEquals(index + 1, note.getIssueID());					// 1, 2 ..
//				assertEquals(index + 1, note.getNoteID());					// 1, 2 ..
//				// assertEquals("administrator", note.getHandler()); // default = 1 (administrator)
//				assertEquals("admin", note.getHandler());			// default = 1 (administrator)
//				assertEquals(importance, noteTextHelper.getImportance());	// 301, 302, 303 ..
//				assertEquals(estimation, noteTextHelper.getEstimation());	// 31, 32, 33 ..
//				assertEquals(value, noteTextHelper.getValue());				// 351, 352, 353 ..
//				assertEquals(howToDemo, noteTextHelper.getHowToDemo());		// update_note_1, 2 ..
//				assertEquals(notes, noteTextHelper.getNotes());				// update_note_1, 2 ..
//			}
//
//			index++;
//		}
//		// close connection
//		this.MSservice.closeConnect();
//
//		// release
//		MNService = null;
//	}
//
//	public void testInsertBugNote() {
//		IProject project = this.CP.getProjectList().get(0);
//		this.MSservice.openConnect();
//		MantisNoteService MNService = new MantisNoteService(this.MSservice.getControl(), configuration);
//		TextParserGeneraterForNote noteTextHelper;
//
//		List<IIssue> issueList = new LinkedList<IIssue>();
//		List<IIssueNote> noteList;
//
//		// new 10 issues
//		for (int index = 0; index < 10; index++) {
//			IIssue story = new Issue();
//			story.setIssueID(index + 1);
//			story.setSummary("Story_Name_" + Integer.toString(index + 1));
//			story.setDescription("Story_Desc_" + Integer.toString(index + 1));
//
//			story.setProjectID(project.getName());
//			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
//
//			MSservice.newIssue(story);
//			issueList.add(story);
//
//			// check the issue's note is null
//			noteList = MNService.getIssueNotes(issueList.get(index));
//			assertEquals(0, noteList.size());
//		}
//
//		// Test insertBugNote, insert new bug note into table
//		int imp = 201, est = 21, val = 251;
//		for (int index = 0; index < issueList.size(); index++) {
//			String importance = Integer.toString(imp + index);
//			String estimation = Integer.toString(est + index);
//			String value = Integer.toString(val + index);
//			String howToDemo = "demo_" + Integer.toString(index + 1);
//			String notes = "note_" + Integer.toString(index + 1);
//
//			// generate note text
//			noteTextHelper = new TextParserGeneraterForNote();
//			String noteText = noteTextHelper.generaterNoteText(importance, estimation, value, howToDemo, notes);
//
//			// test method
//			MNService.insertBugNote(issueList.get(index).getIssueID(), noteText);
//
//			// check the issue note info.
//			noteList = MNService.getIssueNotes(issueList.get(index));
//			assertEquals(1, noteList.size());
//			for (IIssueNote note : noteList) {
//				noteTextHelper = new TextParserGeneraterForNote();
//				noteTextHelper.parserNoteText(note.getText());
//				assertEquals(index + 1, note.getIssueID());					// 1, 2 ..
//				assertEquals(index + 1, note.getNoteID());					// 1, 2 ..
//				// assertEquals("administrator", note.getHandler()); // default = 1 (administrator)
//				assertEquals("admin", note.getHandler());			// default = 1 (administrator)
//				assertEquals(importance, noteTextHelper.getImportance());	// 201, 202, 203 ..
//				assertEquals(estimation, noteTextHelper.getEstimation());	// 21, 22 , 23 ..
//				assertEquals(value, noteTextHelper.getValue());				// 251, 252, 253 ..
//				assertEquals(howToDemo, noteTextHelper.getHowToDemo());		// demo_1, 2 ..
//				assertEquals(notes, noteTextHelper.getNotes());				// note_1, 2 ..
//			}
//		}
//		// close connection
//		this.MSservice.closeConnect();
//
//		// release
//		MNService = null;
//	}
//
//	public void testRemoveNote() {
//		this.MSservice.openConnect();
//		MantisNoteService MNService = new MantisNoteService(this.MSservice.getControl(), configuration);
//
//		// new 10 issues
//		int storyCount = 10;
//		CreateProductBacklog CPB = new CreateProductBacklog(storyCount, this.CP);
//		CPB.exe();
//
//		// get the issues by creating data
//		List<IIssue> issueList = CPB.getIssueList();
//
//		int index = 0;
//		int imp = 200, est = 21, value = 300;
//		// override the note info.
//		for (IIssue issue : issueList) {
//			this.addTagElement(issue, Integer.toString(imp + index), Integer.toString(est + index),
//			        Integer.toString(value + index), "demo_" + index + 1, "note_" + index + 1);
//			index++;
//		}
//
//		for (IIssue issue : issueList) {
//			String issueID = Long.toString(issue.getIssueID());
//			// test method
//			MNService.removeNote(issueID);
//
//			// assert no note exist in bugnote table
//			IQueryValueSet valueSet = new MySQLQuerySet();
//			valueSet.addTableName("mantis_bugnote_table");
//			valueSet.addFieldEqualCondition("mantis_bugnote_table.id", issueID);
//			String query = valueSet.getSelectQuery();
//			ResultSet result = this.MSservice.getControl().executeQuery(query);
//			try {
//				assertTrue(!result.next());
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//			// assert no note exist in bugnote_text table
//			valueSet.clear();
//			valueSet.addTableName("mantis_bugnote_text_table");
//			valueSet.addFieldEqualCondition("mantis_bugnote_text_table.id", issueID);
//			query = valueSet.getSelectQuery();
//			result = this.MSservice.getControl().executeQuery(query);
//			try {
//				assertTrue(!result.next());
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		// close connection
//		this.MSservice.closeConnect();
//
//		// release
//		MNService = null;
//		CPB = null;
//	}
}