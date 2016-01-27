package ntut.csie.ezScrum.web.action.backlog.product;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import ntut.csie.ezScrum.dao.HistoryDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.databaseEnum.IssueTypeEnum;
import servletunit.struts.MockStrutsTestCase;

public class AjaxDeleteStoryActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private Configuration mConfig;
	private ProjectObject mProject;
	private final String mAction = "/ajaxDeleteStory";
	
	public AjaxDeleteStoryActionTest(String testName) {
		super(testName);
	}
	
	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		mCP = new CreateProject(1);
		mCP.exeCreateForDb(); // 新增一測試專案
		mProject = mCP.getAllProjects().get(0);
		
		super.setUp();
		
		// ================ set action info ========================
		setContextDirectory( new File(mConfig.getBaseDirPath()+ "/WebContent") );
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo( mAction );
		
		ini = null;
	}

	protected void tearDown() throws IOException, Exception {
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		mConfig.setTestMode(false);
		mConfig.save();

		super.tearDown();
		
		ini = null;
		mCP = null;
		mConfig = null;
	}
	
	public void testDeleteStory() throws SQLException{
		int storyCount = 2;
		CreateProductBacklog CPB = new CreateProductBacklog(storyCount, mCP);
		CPB.exe();
		
		// ================ set request info ========================
		String projectName = mProject.getName();
		request.setHeader("Referer", "?projectName=" + projectName);
		String issueID = String.valueOf(CPB.getStoryIds().get(1));
		addRequestParameter("issueID", issueID);
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		
		// ================ 執行 action ======================
		actionPerform();
		
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		//	assert response text
		String expectedResponseText =
			"{\"success\":true, \"Total\":1, \"Stories\":[{\"Id\":"+ issueID +"}]}";
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText, actualResponseText);
		
		// assert history should be delete also
		HistoryDAO historyDAO = HistoryDAO.getInstance();
		ArrayList<HistoryObject> historyList_1 = new ArrayList<HistoryObject>();
		historyList_1 = historyDAO.getHistoriesByIssue(CPB.getStoryIds().get(1), IssueTypeEnum.TYPE_STORY);
		assertTrue(historyList_1.size() == 0);
	}
}
