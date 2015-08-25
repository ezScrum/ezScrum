package ntut.csie.ezScrum.web.helper;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.CreateUnplannedItem;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.UnplannedObject;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UnplannedItemHelperTest {

	private CreateProject mCP;
	private CreateSprint mCS;
	private CreateUnplannedItem mCUI;
	private UnplannedItemHelper mUnplannedHelper;
	private Configuration mConfig;
	private ProjectObject mProject;

	@Before
	public void setUp() {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 新增 Project
		mCP = new CreateProject(1);
		mCP.exeCreate();

		mProject = mCP.getAllProjects().get(0);
		mUnplannedHelper = new UnplannedItemHelper(mProject);

		// release
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

		mConfig.setTestMode(false);
		mConfig.save();

		// release
		ini = null;
		mCP = null;
		mCS = null;
		mConfig = null;
	}

	@Test
	public void testGetListXML_ThereIsNoAnySprint() {
		ArrayList<UnplannedObject> unplanneds = new ArrayList<UnplannedObject>();
		String selectSprint = "-1";

		String expected = genXML(selectSprint, unplanneds);
		String actualed = mUnplannedHelper.getListXML(selectSprint).toString();
		assertEquals(expected, actualed);
	}

	@Test
	public void testGetListXML_DefaultSelectSprint() {
		ArrayList<UnplannedObject> unplanneds = null;
		long sprintId;
		String selectSprint = "";

		// create 2 Sprint
		mCS = new CreateSprint(2, mCP);
		mCS.exe();

		// create 2 Unplanned in every sprint
		mCUI = new CreateUnplannedItem(2, mCP, mCS);
		mCUI.exe();

		SprintPlanHelper sprintPlanHelper = new SprintPlanHelper(mProject);
		sprintId = sprintPlanHelper.getCurrentSprint().getId();
		unplanneds = mUnplannedHelper.getUnplannedsInSprint(sprintId);
		selectSprint = String.valueOf(sprintId);

		String expected = genXML(selectSprint, unplanneds);
		String actualed = mUnplannedHelper.getListXML("").toString();
		assertEquals(expected, actualed);
	}

	@Test
	public void testGetListXML_SelectOneSprint() {
		ArrayList<UnplannedObject> unplanneds = null;
		long sprintId;
		String selectSprint;

		// create 2 Sprint
		mCS = new CreateSprint(2, mCP);
		mCS.exe();

		// create 2 Unplanned in every sprint
		mCUI = new CreateUnplannedItem(2, mCP, mCS);
		mCUI.exe();

		// case (I) select sprint #1

		sprintId = mCS.getSprintsId().get(0);
		unplanneds = mUnplannedHelper.getUnplannedsInSprint(sprintId);
		selectSprint = String.valueOf(sprintId);

		String expected = genXML(selectSprint, unplanneds);
		String actualed = mUnplannedHelper.getListXML(selectSprint).toString();
		assertEquals(expected, actualed);

		// case (II) select sprint #2

		sprintId = mCS.getSprintsId().get(1);
		unplanneds = mUnplannedHelper.getUnplannedsInSprint(sprintId);
		selectSprint = String.valueOf(sprintId);

		expected = genXML(selectSprint, unplanneds);
		actualed = mUnplannedHelper.getListXML(selectSprint).toString();
		assertEquals(expected, actualed);
	}

	@Test
	public void testGetListXML_ALL() {
		// create 1 Sprint
		mCS = new CreateSprint(1, mCP);
		mCS.exe();
		// create 3 Unplanned
		mCUI = new CreateUnplannedItem(3, mCP, mCS);
		mCUI.exe();

		ArrayList<UnplannedObject> unplanneds = mCUI.getUnplanneds();
		String selectSprint = "ALL";

		String expected = genXML(selectSprint, unplanneds);
		String actualed = mUnplannedHelper.getListXML(selectSprint).toString();
		assertEquals(expected, actualed);
	}

	private String genXML(String selectSprint, ArrayList<UnplannedObject> unplanneds) {
		StringBuilder result = new StringBuilder();
		result.append("<UnplannedItems><Sprint>")
		        .append("<Id>").append(selectSprint).append("</Id>")
		        .append("<Name>Sprint ").append(selectSprint).append("</Name>")
		        .append("</Sprint>");
		for (UnplannedObject unplanned : unplanneds) {
			result.append("<UnplannedItem>")
			        .append("<Id>").append(unplanned.getId()).append("</Id>")
			        .append("<Link></Link>")
			        .append("<Name>").append(TranslateSpecialChar.TranslateXMLChar(unplanned.getName())).append("</Name>")
			        .append("<SprintID>").append(unplanned.getSprintId()).append("</SprintID>")
			        .append("<Estimate>").append(unplanned.getEstimate()).append("</Estimate>")
			        .append("<Status>").append(unplanned.getStatusString()).append("</Status>")
			        .append("<ActualHour>").append(unplanned.getActual()).append("</ActualHour>")
			        .append("<Handler>").append(unplanned.getHandlerName()).append("</Handler>")
			        .append("<Partners>").append(unplanned.getPartnersUsername()).append("</Partners>")
			        .append("<Notes>").append(TranslateSpecialChar.TranslateXMLChar(unplanned.getNotes())).append("</Notes>")
			        .append("</UnplannedItem>");
		}
		result.append("</UnplannedItems>");
		return result.toString();
	}
}
