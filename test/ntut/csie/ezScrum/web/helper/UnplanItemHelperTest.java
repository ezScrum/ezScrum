package ntut.csie.ezScrum.web.helper;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.CreateUnplanItem;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.UnplanObject;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UnplanItemHelperTest {

	private CreateProject mCP;
	private CreateSprint mCS;
	private CreateUnplanItem mCUI;
	private UnplanItemHelper mUnplanHelper;
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
		mUnplanHelper = new UnplanItemHelper(mProject);

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
		ArrayList<UnplanObject> unplans = new ArrayList<UnplanObject>();
		String selectSprint = "-1";

		String expected = genXML(selectSprint, unplans);
		String actualed = mUnplanHelper.getListXML(selectSprint).toString();
		assertEquals(expected, actualed);
	}

	@Test
	public void testGetListXML_DefaultSelectSprint() {
		ArrayList<UnplanObject> unplans = null;
		long sprintId;
		String selectSprint = "";

		// create 2 Sprint
		mCS = new CreateSprint(2, mCP);
		mCS.exe();

		// create 2 Unplan in every sprint
		mCUI = new CreateUnplanItem(2, mCP, mCS);
		mCUI.exe();

		SprintPlanHelper sprintPlanHelper = new SprintPlanHelper(mProject);
		sprintId = sprintPlanHelper.getCurrentSprint().getId();
		unplans = mUnplanHelper.getUnplansInSprint(sprintId);
		selectSprint = String.valueOf(sprintId);

		String expected = genXML(selectSprint, unplans);
		String actualed = mUnplanHelper.getListXML("").toString();
		assertEquals(expected, actualed);
	}

	@Test
	public void testGetListXML_SelectOneSprint() {
		ArrayList<UnplanObject> unplans = null;
		long sprintId;
		String selectSprint;

		// create 2 Sprint
		mCS = new CreateSprint(2, mCP);
		mCS.exe();

		// create 2 Unplan in every sprint
		mCUI = new CreateUnplanItem(2, mCP, mCS);
		mCUI.exe();

		// case (I) select sprint #1

		sprintId = mCS.getSprintsId().get(0);
		unplans = mUnplanHelper.getUnplansInSprint(sprintId);
		selectSprint = String.valueOf(sprintId);

		String expected = genXML(selectSprint, unplans);
		String actualed = mUnplanHelper.getListXML(selectSprint).toString();
		assertEquals(expected, actualed);

		// case (II) select sprint #2

		sprintId = mCS.getSprintsId().get(1);
		unplans = mUnplanHelper.getUnplansInSprint(sprintId);
		selectSprint = String.valueOf(sprintId);

		expected = genXML(selectSprint, unplans);
		actualed = mUnplanHelper.getListXML(selectSprint).toString();
		assertEquals(expected, actualed);
	}

	@Test
	public void testGetListXML_ALL() {
		// create 1 Sprint
		mCS = new CreateSprint(1, mCP);
		mCS.exe();
		// create 3 Unplan
		mCUI = new CreateUnplanItem(3, mCP, mCS);
		mCUI.exe();

		ArrayList<UnplanObject> unplans = mCUI.getUnplans();
		String selectSprint = "ALL";

		String expected = genXML(selectSprint, unplans);
		String actualed = mUnplanHelper.getListXML(selectSprint).toString();
		assertEquals(expected, actualed);
	}

	private String genXML(String selectSprint, ArrayList<UnplanObject> unplans) {
		StringBuilder result = new StringBuilder();
		result.append("<UnplannedItems><Sprint>")
		        .append("<Id>").append(selectSprint).append("</Id>")
		        .append("<Name>Sprint ").append(selectSprint).append("</Name>")
		        .append("</Sprint>");
		for (UnplanObject unplan : unplans) {
			result.append("<UnplannedItem>")
			        .append("<Id>").append(unplan.getId()).append("</Id>")
			        .append("<Link></Link>")
			        .append("<Name>").append(TranslateSpecialChar.TranslateXMLChar(unplan.getName())).append("</Name>")
			        .append("<SprintID>").append(unplan.getSprintId()).append("</SprintID>")
			        .append("<Estimate>").append(unplan.getEstimate()).append("</Estimate>")
			        .append("<Status>").append(unplan.getStatusString()).append("</Status>")
			        .append("<ActualHour>").append(unplan.getActual()).append("</ActualHour>")
			        .append("<Handler>").append(unplan.getHandlerName()).append("</Handler>")
			        .append("<Partners>").append(unplan.getPartnersUsername()).append("</Partners>")
			        .append("<Notes>").append(TranslateSpecialChar.TranslateXMLChar(unplan.getNotes())).append("</Notes>")
			        .append("</UnplannedItem>");
		}
		result.append("</UnplannedItems>");
		return result.toString();
	}
}
