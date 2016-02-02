package ntut.csie.ezScrum.web.helper;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.CreateUnplanItem;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.UnplanObject;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;

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
		mCP.exeCreateForDb();

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
		String actualed = mUnplanHelper.getListXML(selectSprint).toString();
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

	private String genXML(String sprintIdString, ArrayList<UnplanObject> unplans) {
		// write stories to XML format
		StringBuilder result = new StringBuilder();

		result.append("<UnplannedItems><Sprint>")
			.append("<Id>").append(sprintIdString).append("</Id>")
			.append("<Name>Sprint ").append(sprintIdString).append("</Name>")
			.append("</Sprint>");
		for (UnplanObject unplan : unplans) {
			result.append("<UnplannedItem>");
			result.append("<Id>").append(unplan.getId()).append("</Id>");
			result.append("<Link></Link>");
			result.append("<Name>").append(TranslateSpecialChar.TranslateXMLChar(unplan.getName())).append("</Name>");
			result.append("<SprintID>").append(unplan.getSprintId()).append("</SprintID>");
			result.append("<Estimate>").append(unplan.getEstimate()).append("</Estimate>");
			result.append("<Status>").append(unplan.getStatusString()).append("</Status>");
			result.append("<ActualHour>").append(unplan.getActual()).append("</ActualHour>");
			result.append("<Handler>").append(unplan.getHandlerName()).append("</Handler>");
			result.append("<Partners>").append(unplan.getPartnersUsername()).append("</Partners>");
			result.append("<Notes>").append(TranslateSpecialChar.TranslateXMLChar(unplan.getNotes())).append("</Notes>");
			result.append("</UnplannedItem>");
		}
		result.append("</UnplannedItems>");

		return result.toString();
	}
}
