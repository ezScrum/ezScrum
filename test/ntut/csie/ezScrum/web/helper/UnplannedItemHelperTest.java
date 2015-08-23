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

		// 新增 Sprint
		mCS = new CreateSprint(1, mCP);
		mCS.exe();
		
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
	public void testGetListXML() {
		// 新增 3 Unplanned
		mCUI = new CreateUnplannedItem(3, mCP, mCS);
		mCUI.exe();
		
		ArrayList<UnplannedObject> unplanneds = mCUI.getUnplanneds();
		TranslateSpecialChar tsc = new TranslateSpecialChar();
		
		
		StringBuilder expectString = new StringBuilder();
		expectString.append("<UnplannedItems><Sprint>")
			.append("<Id>ALL</Id>")
			.append("<Name>Sprint ALL</Name>")
			.append("</Sprint>");
		for (int i = 0; i < unplanneds.size(); i++) {
			expectString.append("<UnplannedItem>");
			expectString.append("<Id>").append(unplanneds.get(i).getId()).append("</Id>");
			expectString.append("<Link></Link>");
			expectString.append("<Name>").append(tsc.TranslateXMLChar(unplanneds.get(i).getName())).append("</Name>");
			expectString.append("<SprintID>").append(unplanneds.get(i).getSprintId()).append("</SprintID>");
			expectString.append("<Estimate>").append(unplanneds.get(i).getEstimate()).append("</Estimate>");
			expectString.append("<Status>").append(unplanneds.get(i).getStatus()).append("</Status>");
			expectString.append("<ActualHour>").append(unplanneds.get(i).getActual()).append("</ActualHour>");
			expectString.append("<Handler>").append(unplanneds.get(i).getHandlerName()).append("</Handler>");
			expectString.append("<Partners>").append(tsc.TranslateXMLChar(unplanneds.get(i).getPartnersUsername())).append("</Partners>");
			expectString.append("<Notes>").append(tsc.TranslateXMLChar(unplanneds.get(i).getNotes())).append("</Notes>");
			expectString.append("</UnplannedItem>");
		}
		expectString.append("</UnplannedItems>");
		
		StringBuilder actualString = mUnplannedHelper.getListXML("ALL");
		assertEquals(expectString.toString(), actualString.toString());
	}
}
