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
import ntut.csie.ezScrum.web.dataObject.ProjectObject;

public class Configuration {
	private Properties properties;
	private final String PERFS_FILE_NAME = "ezScrum.ini";
	private final String PERFS_FILE_PATH = System.getProperty("user.dir") + "/" + PERFS_FILE_NAME;
	private final String SERVER_URL = "ServerUrl";
	private final String SERVICE_PATH = "ServicePath";
	private final String ACCOUNT = "Account";
	private final String PASSWORD = "Password";
	private final String DATABASE_TYPE = "DatabaseType";
	private final String DATABASE_NAME = "DatabaseName";
	private final String TEST = "test";
	
	private boolean isTest = false;
	private ProjectObject mProject;
	private IUserSession m_userSession;

	public Configuration() {
		properties = new Properties();
		init();
	}
	
	/**
	 * a test Configuration
	 * @param isTest ? True : False
	 */
	public Configuration(boolean isTest) {
		this.isTest = true;
		properties = new Properties();
		init();
	}

	public Configuration(IUserSession userSession) {
		m_userSession = userSession;
		properties = new Properties();
		init();
	}
	
	public Configuration(IUserSession userSession, boolean isTest) {
		this.isTest = true;
		m_userSession = userSession;
		properties = new Properties();
		init();
	}

	public Configuration(ProjectObject project, IUserSession userSession) {
		mProject = project;
		m_userSession = userSession;
		properties = new Properties();
		init();
	}

	/**
	 * Initial Configuration
	 */
	private void init() {
		try {
			properties.load(new FileInputStream(PERFS_FILE_PATH));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * save changes to ini file
	 */
	public void store() {
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

	public String getProjectName() {
		return mProject.getName();
	}

	public String getServerUrl() {
		return isTest ? properties.getProperty(TEST + "." + SERVER_URL) : properties.getProperty(SERVER_URL);
	}

	public String getWebServicePath() {
		return isTest ? properties.getProperty(TEST + "." + SERVICE_PATH) : properties.getProperty(SERVICE_PATH);
	}

	public String getAccount() {
		if (m_userSession == null)
			return getDBAccount();
		return m_userSession.getAccount().getAccount();
	}

	public String getDBAccount() {
		return isTest ? properties.getProperty(TEST + "." + ACCOUNT) : properties.getProperty(ACCOUNT);
	}

	public String getDBPassword() {
		return isTest ? properties.getProperty(TEST + "." + PASSWORD) : properties.getProperty(PASSWORD);
	}

	public String getDBType() {
		return isTest ? properties.getProperty(TEST + "." + DATABASE_TYPE) : properties.getProperty(DATABASE_TYPE);
	}

	public String getDBName() {
		return isTest ? properties.getProperty(TEST + "." + DATABASE_NAME) : properties.getProperty(DATABASE_NAME);
	}

	public void setServerUrl(String value) {
		properties.setProperty(SERVER_URL, value);
	}

	public void setWebServicePath(String value) {
		properties.setProperty(SERVICE_PATH, value);
	}

	public void setDBAccount(String value) {
		properties.setProperty(ACCOUNT, value);
	}

	public void setDBPassword(String value) {
		properties.setProperty(PASSWORD, value);
	}

	public void setDBType(String value) {
		properties.setProperty(DATABASE_TYPE, value);
	}

	public void setDBName(String value) {
		properties.setProperty(DATABASE_NAME, value);
	}

	public boolean isTest() {
		return isTest;
	}

}
