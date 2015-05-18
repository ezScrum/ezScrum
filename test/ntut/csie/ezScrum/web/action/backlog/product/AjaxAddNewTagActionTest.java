package ntut.csie.ezScrum.web.action.backlog.product;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;
import ntut.csie.jcis.resource.core.IProject;
import servletunit.struts.MockStrutsTestCase;

public class AjaxAddNewTagActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private int mProjectCount = 1;
	private Configuration mConfig;
	private final String mActionPath = "/AjaxAddNewTag";
	
	public AjaxAddNewTagActionTest(String testMethod) {
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
		mConfig = null;
	}
	
	//測試 tag 名稱中含 "," ，會不會顯示 not allowed 的訊息
	public void testAddComma() throws Exception{//comma = ","
		IProject project = mCP.getProjectList().get(0);
		String tag = ",";
		String compareMsg = "<Message>TagName: \",\" is not allowed</Message>";
		
		//設定Session資訊
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		
		addRequestParameter("newTagName", tag);	
		
		actionPerform();
		
		assertTrue(getMockResponse().getWriterBuffer().toString().contains(compareMsg));
	}
	
	//測試加入 DB 中已經存在的tag 名稱，並檢視request訊息要包含XML的轉換
	public void testAddExistTag() throws Exception{
		ProjectObject project = mCP.getAllProjects().get(0);

		String tagDB = "HELLO COOL";
		String tag = "HELLO COOL";
		String compareMsg = "already exist";
		
		//設定Session資訊
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		
		new ProductBacklogHelper(project).addNewTag(tagDB);
		
		addRequestParameter("newTagName", tag);//增加新的tag
		
		actionPerform(); // 執行 action
		verifyNoActionErrors();
		
		//抓取Tag Name already exist的訊息, 並比對字串
		assertTrue(getMockResponse().getWriterBuffer().toString().contains(compareMsg));
	}
	
	//測試新增 tag ，並從 DB 中取出比對
	public void testAddNewTag() throws Exception{
		ProjectObject project = mCP.getAllProjects().get(0);

		List<String> tagList = new ArrayList<String>();
		//tagList.add("&/<>\'\"");
		tagList.add("&");
		tagList.add("/");
		tagList.add("<");
		tagList.add(">");
		tagList.add("TEST");
		
		//設定Session資訊
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		request.getSession().setAttribute("Project", project);
		request.setHeader("Referer", "?PID=" + project.getName());	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session
		
		String expectedResponseText = "";
		String actualResponseText = "";
		
		for(int i = 0; i < tagList.size(); i++) {
			addRequestParameter("newTagName", tagList.get(i));
			actionPerform(); // 執行 action
			verifyNoActionErrors();
			
			expectedResponseText = 
					"<Tags><Result>true</Result>" + 
					"<IssueTag>" + 
					"<Id>" + (i+1) + "</Id>" + 
					"<Name>" + new TranslateSpecialChar().TranslateXMLChar(tagList.get(i)) + "</Name>" + 
					"</IssueTag>" + 
					"</Tags>";
			
			actualResponseText = response.getWriterBuffer().toString();
			assertEquals(expectedResponseText, actualResponseText);
			response.reset();
		}
		
		ArrayList<TagObject> tags = (new ProductBacklogHelper(project)).getTagList();
		
		assertEquals(tags.size(), tagList.size());
		for(int i = 0; i < tags.size(); i++){
			assertEquals(tags.get(i).getName(), tagList.get(i));
		}
		
	}
}
