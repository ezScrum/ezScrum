package ntut.csie.ezScrum.web.action.backlog.product;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.core.IIssueTag;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateTag;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.ezScrumInfoConfig;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;
import servletunit.struts.MockStrutsTestCase;

public class AjaxGetTagListActionTest extends MockStrutsTestCase {
	private CreateProject CP;
	private CreateTag CT;
	private int ProjectCount = 1;
	private ezScrumInfoConfig config = new ezScrumInfoConfig();
	private final String ACTION_PATH = "/AjaxGetTagList";
	
	public AjaxGetTagListActionTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() throws Exception {
		InitialSQL ini = new InitialSQL(config);
		ini.exe();											// 初始化 SQL
		
		// 新增Project
		this.CP = new CreateProject(this.ProjectCount);
		this.CP.exeCreate();
		
		this.CT = new CreateTag(2, this.CP);
		this.CT.exe();
		
		
		super.setUp();
		
		// 設定讀取的struts-config檔案路徑
		setContextDirectory(new File(config.getBaseDirPath() + "/WebContent")); 
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(this.ACTION_PATH);
		
		// ============= release ==============
		ini = null;
	}
	
	protected void tearDown() throws IOException, Exception {
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(config);
		ini.exe();
		
		//	刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(this.config.getTestDataPath());

		super.tearDown();
		
		ini = null;
		projectManager = null;
		this.CP = null;
		this.CT = null;
	}
	
	public void testGetAllTagList(){
		// ================ set request info ========================
		request.setHeader("Referer", "?PID=" + this.CP.getProjectList().get(0).getName());
		
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", config.getUserSession());
		// ================ 執行 action ======================
		actionPerform();
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		//	assert response text
		ArrayList<IIssueTag> tags = this.CT.getTagList();
		StringBuilder sb = new StringBuilder();
		sb.append("<TagList><Result>success</Result>");

		for(IIssueTag tag: tags){
			sb.append("<IssueTag>");
			sb.append("<Id>" + tag.getTagId() + "</Id>");
			sb.append("<Name>" + new TranslateSpecialChar().TranslateXMLChar(tag.getTagName()) + "</Name>");
			sb.append("</IssueTag>");
		}
		sb.append("</TagList>");
		
		String actualResponsetext = this.response.getWriterBuffer().toString();
		assertEquals(sb.toString(), actualResponsetext);
	}
}
