package ntut.csie.ezScrum.web.action.backlog.product;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.core.IIssueTag;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateTag;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;
import servletunit.struts.MockStrutsTestCase;

public class AjaxGetTagListActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateTag mCT;
	private int mProjectCount = 1;
	private Configuration mConfig;
	private final String mActionPath = "/AjaxGetTagList";
	
	public AjaxGetTagListActionTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();											// 初始化 SQL
		
		// 新增Project
		mCP = new CreateProject(mProjectCount);
		mCP.exeCreate();
		
		mCT = new CreateTag(2, mCP);
		mCT.exe();
		
		
		super.setUp();
		
		// 設定讀取的struts-config檔案路徑
		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent")); 
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(mActionPath);
		
		// ============= release ==============
		ini = null;
	}
	
	protected void tearDown() throws IOException, Exception {
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		//	刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		
		mConfig.setTestMode(false);
		mConfig.save();

		super.tearDown();
		
		ini = null;
		projectManager = null;
		mCP = null;
		mCT = null;
		mConfig = null;
	}
	
	public void testGetAllTagList(){
		// ================ set request info ========================
		request.setHeader("Referer", "?PID=" + mCP.getProjectList().get(0).getName());
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		// ================ 執行 action ======================
		actionPerform();
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		//	assert response text
		ArrayList<TagObject> tags = mCT.getTagList();
		StringBuilder sb = new StringBuilder();
		sb.append("<TagList><Result>success</Result>");

		for(TagObject tag: tags){
			sb.append("<IssueTag>");
			sb.append("<Id>" + tag.getId() + "</Id>");
			sb.append("<Name>" + new TranslateSpecialChar().TranslateXMLChar(tag.getName()) + "</Name>");
			sb.append("</IssueTag>");
		}
		sb.append("</TagList>");
		
		String actualResponsetext = response.getWriterBuffer().toString();
		assertEquals(sb.toString(), actualResponsetext);
	}
}
