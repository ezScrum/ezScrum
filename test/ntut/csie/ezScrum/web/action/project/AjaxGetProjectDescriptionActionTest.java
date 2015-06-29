package ntut.csie.ezScrum.web.action.project;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.AddUserToRole;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import servletunit.struts.MockStrutsTestCase;

public class AjaxGetProjectDescriptionActionTest extends MockStrutsTestCase {
	private int mProjectCount = 1;
	private int mAccountCount = 1;
	private final String mActionPathGetProjectDescription = "/GetProjectDescription";
	private Configuration mConfig;
	private CreateProject mCP;
	private CreateAccount mCA;

	public AjaxGetProjectDescriptionActionTest(String testMethod) {
		super(testMethod);
	}

	private void setRequestPathInformation(String actionPath) {
		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));		// 設定讀取的 struts-config 檔案路徑
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(actionPath);
	}

	/**
	 * clean previous action info
	 */
	/*	private void cleanActionInformation(){
			clearRequestParameters();
			response.reset();
		}*/

	/**
	 * 取得一般使用者的UserSession
	 * 
	 * @param account
	 * @return
	 */
	private IUserSession getUserSession(AccountObject account) {
		IUserSession userSession = new UserSession(account);
		return userSession;
	}

	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 新增Project
		mCP = new CreateProject(mProjectCount);
		mCP.exeCreate();

		// 新增使用者
		mCA = new CreateAccount(mAccountCount);
		mCA.exe();

		super.setUp();

		setRequestPathInformation(mActionPathGetProjectDescription);
	}

	protected void tearDown() throws Exception {
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		//	刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		
		mConfig.setTestMode(false);
		mConfig.save();

		super.tearDown();

		// release
		mConfig = null;
		mCP = null;
		mCA = null;
	}

	/**
	 * 測試admin登入專案後，是否能取得正確的專案資訊。 response text :
	 * {"ID":"0","ProjectName":"TEST_PROJECT_1","ProjectDisplayName":"TEST_PROJECT_1","Commnet":"This is Test Project - 1","ProjectManager":"Project_Manager_1"
	 * ,"AttachFileSize":"2","ProjectCreateDate":"2013/04/15-21:55:19"}
	 */
	public void testAdminAjaxGetProjectDescriptionAction() {
		ProjectObject project = mCP.getAllProjects().get(0);
		String projectID = project.getName();

		// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + projectID);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ======================
		//	assert response text
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
		String actualResponseText = response.getWriterBuffer().toString();
		StringBuilder expectResponseText = new StringBuilder();
		expectResponseText.append("{")
		        .append("\"ID\":\"").append(project.getId()).append("\",")
		        .append("\"ProjectName\":\"").append(project.getName()).append("\",")
		        .append("\"ProjectDisplayName\":\"").append(project.getDisplayName()).append("\",")
		        .append("\"Commnet\":\"").append(project.getComment()).append("\",")
		        .append("\"ProjectManager\":\"").append(project.getManager()).append("\",")
		        .append("\"AttachFileSize\":\"").append(project.getAttachFileSize()).append("\",")
		        .append("\"ProjectCreateDate\":\"").append(dateFormat.format(project.getCreateTime())).append("\"")
		        .append("}");
		assertEquals(expectResponseText.toString(), actualResponseText);
	}

	/**
	 * 測試一般使用者在沒有加入該專案下，是否會回傳權限不足的警告訊息。 response text:{"PermissionAction":{"ActionCheck":"false", "Id":0}}
	 */
	public void testUserAjaxGetProjectDescriptionAction_NotInProject() {
		String projectID = mCP.getProjectList().get(0).getName();
		AccountObject account = mCA.getAccountList().get(0);

		// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + projectID);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", getUserSession(account));

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ======================
		//	assert response text
		String actualResponseText = response.getWriterBuffer().toString();
		String expectResponseText = "{\"PermissionAction\":{\"ActionCheck\":\"false\", \"Id\":0}}";
		assertEquals(expectResponseText, actualResponseText);
	}

	/**
	 * 測試一般使用者登入專案後，是否能取得正確的專案資訊。 response text :
	 * {"ID":"0","ProjectName":"TEST_PROJECT_1","ProjectDisplayName":"TEST_PROJECT_1","Commnet":"This is Test Project - 1","ProjectManager":"Project_Manager_1"
	 * ,"AttachFileSize":"2","ProjectCreateDate":"2013/04/15-21:55:19"}
	 */
	public void testUserAjaxGetProjectDescriptionAction_InProject() {
		ProjectObject project = mCP.getAllProjects().get(0);
		String projectID = project.getName();
		AccountObject account = mCA.getAccountList().get(0);

		AddUserToRole addUserToRole = new AddUserToRole(mCP, mCA);
		addUserToRole.exe_ST();

		// ================ set URL parameter ========================
		request.setHeader("Referer", "?PID=" + projectID);	// SessionManager 會對URL的參數作分析 ,未帶入此參數無法存入session

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", getUserSession(new AccountMapper().getAccount(account.getUsername())));

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ======================
		//	assert response text
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
		String actualResponseText = response.getWriterBuffer().toString();
		StringBuilder expectResponseText = new StringBuilder();
		expectResponseText.append("{")
		        .append("\"ID\":\"").append(project.getId()).append("\",")
		        .append("\"ProjectName\":\"").append(project.getName()).append("\",")
		        .append("\"ProjectDisplayName\":\"").append(project.getDisplayName()).append("\",")
		        .append("\"Commnet\":\"").append(project.getComment()).append("\",")
		        .append("\"ProjectManager\":\"").append(project.getManager()).append("\",")
		        .append("\"AttachFileSize\":\"").append(project.getAttachFileSize()).append("\",")
		        .append("\"ProjectCreateDate\":\"").append(dateFormat.format(project.getCreateTime())).append("\"")
		        .append("}");
		assertEquals(expectResponseText.toString(), actualResponseText);
	}
}
