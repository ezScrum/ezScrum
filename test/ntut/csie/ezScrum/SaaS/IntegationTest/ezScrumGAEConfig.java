package ntut.csie.ezScrum.SaaS.IntegationTest;

import java.io.File;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.jcis.account.core.IAccount;

public class ezScrumGAEConfig {
	
	private final String ADMIN_ACCOUNT = "admin";
	private final String ADMIN_PASSWORD = "admin";
	
	private static String TestDataPath = "";		// 預設的 TestData
	private static String TestWorkspacePath = "";	// 預設的 worksapce
	private static String TestConfigFile = "";		// 預設的設定檔
	
	public String SERVER_URL = "";			// MySQL database 的 IP
	public String SERVER_ACCOUNT = "";		// Access Local DB 的帳號
	public String SERVER_PASSWORD = "";		// Access Local DB 的密碼
	public final String SERVER_PATH = "/mantis/mc/mantisconnect.php";	// ?
	
	public String DATABASE_TYPE = "";		
	public String DATABASE_NAME = "";

	public ezScrumGAEConfig(){
	}
	
	/**
	 * return mockup IUserSession
	 */
	public IUserSession getAdminSession() {	
		IAccount theAccount = null;
		
//		theAccount = new Account(USER_ID);	// assign ID 此種方式產生的account在產生ScrumRole會有問題		
		theAccount = (new AccountMapper()).getAccountById(this.ADMIN_ACCOUNT);
		
		IUserSession theUserSession = new UserSession(theAccount);

		return theUserSession;
	}
	
	public File getWebContentFile(){
		String path = this.getBaseDirPath()+ "/WebContent";
		return new File(path);
	}
	
	public String getServletConfigFilePath(){
		String path = "/WEB-INF/struts-config.xml";
		return path;
	}
	
	/**
	 * return base dir path
	 */
	public String getBaseDirPath() {
		String basedir = System.getProperty("user.dir").replace('\\', '/');
		return basedir;
	}
	
	public String getAdminId(){
		return this.ADMIN_ACCOUNT;
	}
	
	public String getAdminPassword(){
		return this.ADMIN_PASSWORD;
	}
}
