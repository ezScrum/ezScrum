package ntut.csie.ezScrum.web.dataObject;

import java.util.ArrayList;
import java.util.HashMap;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import ntut.csie.ezScrum.dao.AccountDAO;
import ntut.csie.ezScrum.web.databasEnum.AccountEnum;
import ntut.csie.ezScrum.web.databasEnum.RoleEnum;

public class AccountObject implements IBaseObject {
	private final static int DEFAULT_VALUE = -1;

	private long mId = DEFAULT_VALUE;
	private String mUsername = "";
	private String mPassword = "";
	private String mEmail = "";
	private String mNickName = "";
	private boolean mEnable = false;
	private HashMap<String, ProjectRole> mRoles = null;

	public AccountObject(long id, String username) {
		mUsername = username;
		mId = id;
	}

	public AccountObject(String username) {
		if (username != null && username != "") {
			mUsername = username;
		} else {
			throw new RuntimeException();
		}
	}

	public String toString() {
		try {
			return toJSON().toString();
		} catch (JSONException e) {
			return "JSON Exception";
		}
	}
	
	/**
	 * JSON 少 project role 的資料 (待補!!!)
	 * 
	 * @return JSONObject
	 */
	public JSONObject toJSON() throws JSONException {
		JSONObject account = new JSONObject();
		
		account.put(AccountEnum.ID, mId)
		.put(AccountEnum.USERNAME, mUsername)
		.put(AccountEnum.PASSWORD, mPassword)
		.put(AccountEnum.EMAIL, mEmail)
		.put(AccountEnum.ENABLE, mEnable)
		.put("project_role", "");
		
		return account;
	}

	/**
	 * JSON 少 project role 的資料 (待補!!!)
	 * 
	 * @return JSONObject
	 */
	public JSONObject toJSON() throws JSONException {
		JSONObject account = new JSONObject();

		account.put(AccountEnum.ID, mId).put(AccountEnum.USERNAME, mUsername)
				.put(AccountEnum.PASSWORD, mPassword)
				.put(AccountEnum.EMAIL, mEmail)
				.put(AccountEnum.NICK_NAME, mNickName)
				.put(AccountEnum.ENABLE, mEnable).put("project_role", "");

		return account;
	}

	public long getId() {
		return mId;
	}

	public String getPassword() {
		return mPassword;
	}

	public AccountObject setPassword(String password) {
		mPassword = password;
		return this;
	}

	public String getUsername() {
		return mUsername;
	}

	public String getEmail() {
		return mEmail;
	}

	public AccountObject setEmail(String email) {
		mEmail = email;
		return this;
	}

	public String getNickName() {
		return mNickName;
	}

	public AccountObject setNickName(String nickName) {
		mNickName = nickName;
		return this;
	}

	public boolean getEnable() {
		return mEnable;
	}

	public AccountObject setEnable(boolean enable) {
		mEnable = enable;
		return this;
	}

	public HashMap<String, ProjectRole> getRoles() {
	    return AccountDAO.getInstance().getProjectRoleMap(mId);
    }

	/**
	 * Get account by account id
	 * 
	 * @param id account id
	 * @return AccountObject
	 */
	public static AccountObject get(long id) {
		return AccountDAO.getInstance().get(id);
	}
	
	/**
	 * Get account by account user name
	 * 
	 * @param username account user name
	 * @return AccountObject
	 */
	public static AccountObject get(String username) {
		return AccountDAO.getInstance().get(username);
	}

	/**
	 * Get project all accounts
	 * 
	 * @return AccountObject list
	 */
	/**
	 * Get project all accounts
	 * 
	 * @return AccountObject list
	 */
	public static ArrayList<AccountObject> getAccounts() {
		return AccountDAO.getInstance().getAccounts();
	}

	/**
	 * Create map about user and role in each attend project
	 * 
	 * @param projectId
	 * @param role
	 * @return isCreateSuccess
	 */
	public boolean createProjectRole(long projectId, RoleEnum role) {
		return AccountDAO.getInstance().createProjectRole(projectId, mId, role);
	}
	
	/**
	 * Get account access mapping each attend project
	 * 
	 * @return account access map <"Project name", "Project role">
	 */
	public HashMap<String, ProjectRole> getProjectRoleMap() {
		return AccountDAO.getInstance().getProjectRoleMap(mId);
	}
	
	/**
	 * Delete account's role in project
	 * 
	 * @param projectId
	 * @param role
	 * @return isDeleteSuccess
	 */
	public boolean deleteProjectRole(long projectId, RoleEnum role) {
		return AccountDAO.getInstance().deleteProjectRole(projectId, mId, role);
	}
	
	/**
	 * Create project system role
	 * 
	 * @return isCreateSuccess
	 */
	public boolean createSystemRole() {
		return AccountDAO.getInstance().createSystemRole(mId);
	}
	
	/**
	 * 藉由 account id 判斷是否取出專案下的管理者帳號
	 * 
	 * @return admin account's project role
	 */
	public ProjectRole getSystemRole() {
		return AccountDAO.getInstance().getSystemRole(mId);
	}

	/**
	 * Delete account's system role in project
	 * 
	 * @return isDeleteSuccess
	 */
	public boolean deleteSystemRole() {
		return AccountDAO.getInstance().deleteSystemRole(mId);
	}
	
	/**
	 * Use username and password to get account
	 * 
	 * @param username
	 * @param password
	 * @return AccountObject
	 */
	public static AccountObject confirmAccount(String username, String password) {
		return AccountDAO.getInstance().confirmAccount(username, password);
	}
	
	@Override
	public boolean delete() {
		boolean success = AccountDAO.getInstance().delete(mId);
		if (success) {
			mId = DEFAULT_VALUE;
		}
		return success;
	}

	@Override
	public void save() {
		if (recordExists()) {
			doUpdate();
		} else {
			doCreate();
		}
	}

	@Override
	public void reload() throws Exception {
		if (recordExists()) {
			AccountObject account = AccountDAO.getInstance().get(mId);
			if (account != null) {
				resetData(account);
			}
		}
	}

	private boolean recordExists() {
		return mId > 0;
	}

	private void resetData(AccountObject account) {
		mId = account.getId();
		mUsername = account.getUsername();
		mPassword = account.getPassword();
		mEmail = account.getEmail();
		mNickName = account.getNickName();
		mEnable = account.getEnable();
		mRoles = null;
	}

	private void doCreate() {
		mId = AccountDAO.getInstance().create(this);
		try {
			reload();
		} catch (Exception e) {
		}
	}

	private void doUpdate() {
		AccountDAO.getInstance().update(this);
	}
}
