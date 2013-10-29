package ntut.csie.ezScrum.test.CreateData;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.jcis.account.core.AccountFactory;
import ntut.csie.jcis.account.core.IAccount;
import ntut.csie.jcis.account.core.IAccountManager;
import ntut.csie.jcis.account.core.IActor;

public class ezScrumInfoConfig {

	private static String TestDataPath = "";		// 預設的 TestData
	private static String TestWorkspacePath = "";	// 預設的 worksapce
	private static String TestConfigFile = "";		// 預設的設定檔
	private static String TestInitialSQLPath = "";	// 預設的初始SQL檔案位置

	public String SERVER_URL = "";			// MySQL database 的 IP
	public String SERVER_ACCOUNT = "";		// Access Local DB 的帳號
	public String SERVER_PASSWORD = "";		// Access Local DB 的密碼
	public final String SERVER_PATH = "/mantis/mc/mantisconnect.php";	// ?

	public String APPSERV_USERID = "";		// AppServ 的管理者帳號
	public String APPSERV_PASSWORD = "";	// AppServ 的管理者密碼

	public String DATABASE_TYPE = "";
	public String DATABASE_NAME = "";

	public String USER_ID = "admin";	// user ID
	// public final String USER_PASSWORD = "ilove306"; // user password

	private static Properties props = null;

	public ezScrumInfoConfig() {
		init_workspacepath();	// initial ezScrum info
		init_property();		// initial test property
		setITS_Info();			// initial ITS config info
	}

	private void init_workspacepath() {
		// initial test data path
		this.TestDataPath = getBaseDirPath() + File.separator + "TestData";

		// initial test worksapce path
		this.TestWorkspacePath = this.TestDataPath + File.separator + "TestWorkspace";

		// initial test config file path
		if (System.getProperty("ntut.csie.ezScrum.test.property") == null) {
			// default TestConfig.properties
			this.TestConfigFile = this.TestDataPath + File.separator + "TestConfig.properties";
		} else {
			this.TestConfigFile = System.getProperty("ntut.csie.ezScrum.test.property");
		}

		// initial test upload file
		this.TestInitialSQLPath = this.TestDataPath + File.separator + "InitialData" + File.separator + "initial_bk.sql";
	}

	private void init_property() {
		props = new Properties();
		try {
			props.load(new FileInputStream(this.TestConfigFile));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// initial workspace property
		System.setProperty("ntut.csie.jcis.resource.WorkspaceRoot", this.TestWorkspacePath);

		// initial account manager property
		System.setProperty("ntut.csie.jcis.accountManager", "ntut.csie.jcis.account.core.internal.XMLAccountManager");

		// initial rolebase.xml path
		System.setProperty("ntut.csie.jcis.accountManager.path", this.TestWorkspacePath + File.separator + "RoleBase.xml");
	}

	// 設定專案 ITS info 的 property
	private void setITS_Info() {
		this.SERVER_URL = this.props.getProperty("SERVER_URL");
		this.SERVER_ACCOUNT = this.props.getProperty("SERVER_ACCOUNT");
		this.SERVER_PASSWORD = this.props.getProperty("SERVER_PASSWORD");

		this.APPSERV_USERID = this.props.getProperty("APPSERV_USERID");
		this.APPSERV_PASSWORD = this.props.getProperty("APPSERV_PASSWORD");
		this.DATABASE_TYPE = this.props.getProperty("DATABASE_TYPE");
		this.DATABASE_NAME = this.props.getProperty("DATABASE_NAME");
	}

	/**
	 * return mockup IUserSession
	 */
	public IUserSession getUserSession() {
		IAccount theAccount = null;
		IAccountManager manager = AccountFactory.getManager();
		theAccount = manager.getAccount(USER_ID);
		IUserSession theUserSession = new UserSession(theAccount);
		return theUserSession;
	}

	/**
	 * return base dir path
	 */
	public String getBaseDirPath() {
		String basedir = System.getProperty("ntut.csie.jcis.resource.BaseDir");

		if (basedir == null) {
			basedir = System.getProperty("user.dir").replace('\\', '/');
		}

		return basedir;
	}

	/**
	 * return TestWorkspace path
	 */
	public String getTestWorkspacePath() {
		return this.TestWorkspacePath;
	}

	/**
	 * return TestData path
	 */
	public String getTestDataPath() {
		return this.TestDataPath;
	}

	/**
	 * return InitialSQL file path
	 */
	public String getInitialSQLPath() {
		return this.TestInitialSQLPath;
	}

	/**
	 * setting the user who will get his session
	 */
	public void setUser(String id) {
		USER_ID = id;
	}
}
