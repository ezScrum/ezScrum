package ntut.csie.ezScrum.issue.sql.service.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.mapper.AccountMapper;

public class Configuration {
	private Properties properties;
	public String USER_ID = "admin";	// user ID
	private String BASEDIR_PATH = getBaseDirPath();
	private final String PERFS_FILE_NAME = "ezScrum.ini";
	private final String PERFS_FILE_PATH = BASEDIR_PATH + File.separator + PERFS_FILE_NAME;
	private final String MODE = "Mode";
	private final String SERVER_URL = "ServerUrl";
	private final String SERVICE_PATH = "ServicePath";
	private final String ACCOUNT = "Account";
	private final String PASSWORD = "Password";
	private final String DATABASE_TYPE = "DatabaseType";
	private final String DATABASE_NAME = "DatabaseName";
	private final String TEST = "test";
	
	private String dataPath;	     // 預設的 TestData
	private String workspacePath;	 // 預設的 workspace
	private String initialSQLPath;	 // 預設的初始SQL檔案位置
	
	private ProjectObject mProject;
	private IUserSession m_userSession;

	public Configuration() {
		init();
		init_workspacepath();
	}

	public Configuration(IUserSession userSession) {
		m_userSession = userSession;
		init();
		init_workspacepath();
	}

	public Configuration(ProjectObject project, IUserSession userSession) {
		mProject = project;
		m_userSession = userSession;
		init();
		init_workspacepath();
	}

	/**
	 * Initial Configuration
	 */
	private void init() {
		properties = new Properties();
		try {
			properties.load(new FileInputStream(PERFS_FILE_PATH));
		} catch (IOException e) {
			System.out.println(
					"************ ERROR MESSAGE ************\n\n\n" +
					"Please check \"ezScrum.ini\" file exist.\n\n\n" +
					"***************************************\n\n\n"
			);
			System.exit(0);
		}
	}

	/**
	 * save changes to ini file
	 */
	public void save() {
		List<String> list = new ArrayList<String>();
		BufferedReader bufferedReader = null;
		BufferedWriter bufferedWriter = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(new File(PERFS_FILE_PATH)));

			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				for (String property : properties.stringPropertyNames()) {
					if (line.startsWith(property)) {
						String[] valueStrings = line.split("=");
						if (valueStrings.length > 1 && valueStrings[1].replaceAll("\\s+", "") != properties.getProperty(property)) {
							line = property + " = " + properties.getProperty(property);
						} else if (valueStrings.length == 1) {
							line = property + " = " + properties.getProperty(property);
						}
					}
				}
				list.add(line);
			}

			bufferedWriter = new BufferedWriter(new FileWriter(new File(PERFS_FILE_PATH), false));
			for (String str : list) {
				bufferedWriter.write(str + "\r\n");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				bufferedReader.close();
				bufferedWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void init_workspacepath() {
		if (isRunningTestMode()) {
			// initial test data path
			dataPath = getBaseDirPath() + File.separator + "TestData";
			
			// initial test worksapce path
			workspacePath = dataPath + File.separator + "TestWorkspace";
			
			// initial test upload file
			initialSQLPath = dataPath + File.separator + "InitialData" + File.separator + "initial_bk.sql";
		} else {
			// initial test data path
			dataPath = BASEDIR_PATH + File.separator + "WebContent";
			
			// initial test worksapce path
			workspacePath = dataPath + File.separator + "Workspace";
			
			// initial test upload file
			initialSQLPath = workspacePath + File.separator + "_metadata" + File.separator + "initial_bk.sql";
		}

		// initial workspace property
		System.setProperty("ntut.csie.jcis.resource.WorkspaceRoot", workspacePath);

		// initial account manager property
		System.setProperty("ntut.csie.jcis.accountManager", "ntut.csie.jcis.account.core.internal.XMLAccountManager");

		// initial rolebase.xml path
		System.setProperty("ntut.csie.jcis.accountManager.path", workspacePath + File.separator + "RoleBase.xml");
	}

	public String getProjectName() {
		return mProject.getName();
	}

	public String getServerUrl() {
		return isRunningTestMode() ? properties.getProperty(TEST + "." + SERVER_URL) : properties.getProperty(SERVER_URL);
	}

	public String getWebServicePath() {
		return isRunningTestMode() ? properties.getProperty(TEST + "." + SERVICE_PATH) : properties.getProperty(SERVICE_PATH);
	}

	public String getAccount() {
		if (m_userSession == null)
			return getDBAccount();
		return m_userSession.getAccount().getUsername();
	}

	public String getDBAccount() {
		return isRunningTestMode() ? properties.getProperty(TEST + "." + ACCOUNT) : properties.getProperty(ACCOUNT);
	}

	public String getDBPassword() {
		return isRunningTestMode() ? properties.getProperty(TEST + "." + PASSWORD) : properties.getProperty(PASSWORD);
	}

	public String getDBType() {
		return isRunningTestMode() ? properties.getProperty(TEST + "." + DATABASE_TYPE) : properties.getProperty(DATABASE_TYPE);
	}

	public String getDBName() {
		return isRunningTestMode() ? properties.getProperty(TEST + "." + DATABASE_NAME) : properties.getProperty(DATABASE_NAME);
	}

	public void setServerUrl(String value) {
		properties.setProperty(isRunningTestMode() ? TEST + "." + SERVER_URL : SERVER_URL, value);
	}

	public void setWebServicePath(String value) {
		properties.setProperty(isRunningTestMode() ? TEST + "." + SERVICE_PATH : SERVICE_PATH, value);
	}

	public void setDBAccount(String value) {
		properties.setProperty(isRunningTestMode() ? TEST + "." + ACCOUNT : ACCOUNT, value);
	}

	public void setDBPassword(String value) {
		properties.setProperty(isRunningTestMode() ? TEST + "." + PASSWORD : PASSWORD, value);
	}

	public void setDBType(String value) {
		properties.setProperty(isRunningTestMode() ? TEST + "." + DATABASE_TYPE : DATABASE_TYPE, value);
	}

	public void setDBName(String value) {
		properties.setProperty(isRunningTestMode() ? TEST + "." + DATABASE_NAME : DATABASE_NAME, value);
	}
	
	public void setTestMode(boolean isTest){
		properties.setProperty(MODE, String.valueOf(isTest));
	}
	
	public boolean isRunningTestMode(){
		return Boolean.parseBoolean(properties.getProperty(MODE));
	}
	
	/**
	 * return mock up IUserSession
	 */
	public IUserSession getUserSession() {
		AccountObject theAccount = new AccountMapper().getAccount(USER_ID);
		IUserSession theUserSession = new UserSession(theAccount);
		return theUserSession;
	}
	
	/**
	 * return base dir path
	 */
	public String getBaseDirPath() {
		String basedir = System.getProperty("ntut.csie.jcis.resource.BaseDir");
		if (basedir == null) {
			basedir = System.getProperty("user.dir");
		}
		return basedir;
	}
	
	/**
	 * return TestWorkspace path
	 */
	public String getWorkspacePath() {
		return workspacePath;
	}
	
	/**
	 * return TestData path
	 */
	public String getDataPath() {
		return dataPath;
	}
	
	/**
	 * return InitialSQL file path
	 */
	public String getInitialSQLPath() {
		return initialSQLPath;
	}

}
