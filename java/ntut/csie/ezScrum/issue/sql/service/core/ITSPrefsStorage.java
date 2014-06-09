package ntut.csie.ezScrum.issue.sql.service.core;

import java.util.Map;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.jcis.core.exception.PrefsUtilException;
import ntut.csie.jcis.core.util.PrefsUtil;
import ntut.csie.jcis.resource.core.IProject;
import ntut.csie.jcis.resource.core.ResourceFacade;

@Deprecated
public class ITSPrefsStorage {
	private final String PERFS_FILE_NAME = "its_config.xml";
	private final String PROJECT_FOLDER_NAME = "ezScrum";
	private final String SERVER_URL = "ServerUrl";
	private final String SERVICE_PATH = "ServicePath";
	private final String ACCOUNT = "Account";
	private final String PASSWORD = "Password";
	private final String DATABASE_TYPE = "DatabaseType";
	private final String DATABASE_NAME = "DatabaseName";

	private ProjectObject mProject;
	private PrefsUtil mPrefsUtil;
	private String mProjectPrefsPath;
	private String mSysPrefsPath;

	private Map<String, String> prefsMap;
	private IUserSession m_userSession;

	public ITSPrefsStorage() {
		mPrefsUtil = new PrefsUtil(PrefsUtil.ITS_PROJECT_PACKAGE);
		mSysPrefsPath = ResourceFacade.getWorkspace().getRoot().getFolder(IProject.METADATA).getFullPath() + "/" + PERFS_FILE_NAME;
		mProjectPrefsPath = mSysPrefsPath;
		init();
	}
	
	/**
	 * 若不需要對ITS做修改新增的動作(add issue,add bug note)
	 * 而僅做收集資料動作的話(像在builder中要收集its的資料),
	 * userSession可使用null來使用
	 * 在回傳account及password將都會回傳DB的設定
	 */
	public ITSPrefsStorage(ProjectObject project, IUserSession userSession) {
		mProject = project;
		mPrefsUtil = new PrefsUtil(PrefsUtil.ITS_PROJECT_PACKAGE);
		mSysPrefsPath = ResourceFacade.getWorkspace().getRoot().getFolder(IProject.METADATA).getFullPath() + "/" + PERFS_FILE_NAME;
		mProjectPrefsPath = mSysPrefsPath;
		m_userSession = userSession;
		init();
	}
	
	public ITSPrefsStorage(IProject project, IUserSession userSession) {
//		mProject = project;
		mPrefsUtil = new PrefsUtil(PrefsUtil.ITS_PROJECT_PACKAGE);
		mSysPrefsPath = ResourceFacade.getWorkspace().getRoot().getFolder(IProject.METADATA).getFullPath() + "/" + PERFS_FILE_NAME;
		mProjectPrefsPath = mSysPrefsPath;
		m_userSession = userSession;
		init();
	}

	public String getProjectName() {
		return mProject.getName();
	}

	@SuppressWarnings("unchecked")
	private void init() {
		try {
			prefsMap = mPrefsUtil.getPrefs(mSysPrefsPath, "");
		} catch (PrefsUtilException e) {
			e.printStackTrace();
		}
	}

	public void save() {
		try {
			mPrefsUtil.saveSysDefined(prefsMap, mSysPrefsPath);
		} catch (PrefsUtilException e) {
			e.printStackTrace();
		}
	}

	public String getServerUrl() {
		return prefsMap.get(SERVER_URL);
	}

	public String getWebServicePath() {
		return prefsMap.get(SERVICE_PATH);
	}

	public String getAccount() {
		if (m_userSession == null) return getDBAccount();
		return m_userSession.getAccount().getAccount();
	}

	public String getDBAccount() {
		return prefsMap.get(ACCOUNT);
	}

	public String getDBPassword() {
		return prefsMap.get(PASSWORD);
	}

	public String getDBType() {
		return prefsMap.get(DATABASE_TYPE);
	}

	public String getDBName() {
		return prefsMap.get(DATABASE_NAME);
	}

	public void setServerUrl(String serverUrl) {
		prefsMap.put(SERVER_URL, serverUrl);
	}

	public void setServicePath(String servicePath) {
		prefsMap.put(SERVICE_PATH, servicePath);
	}

	public void setDBAccount(String account) {
		prefsMap.put(ACCOUNT, account);
	}

	public void setDBPassword(String password) {
		prefsMap.put(PASSWORD, password);
	}

	public void setDBType(String type) {
		prefsMap.put(DATABASE_TYPE, type);
	}

	public void setDBName(String name) {
		prefsMap.put(DATABASE_NAME, name);
	}

	public Map<String, String> getPerfsMap() {
		return prefsMap;
	}

}
