package ntut.csie.ezScrum.web.dataObject;

import java.util.ArrayList;
import java.util.HashMap;

import ntut.csie.ezScrum.dao.AccountDAO;
import ntut.csie.ezScrum.web.databasEnum.AccountEnum;
import ntut.csie.ezScrum.web.databasEnum.ProjectRoleEnum;
import ntut.csie.ezScrum.web.databasEnum.RoleEnum;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class AccountObject implements IBaseObject {
	private final static int DEFAULT_VALUE = -1;

	private long mId = DEFAULT_VALUE;
	private String mUsername = "";
	private String mPassword = "";
	private String mEmail = "";
	private String mNickName = "";
	private boolean mEnable = false;
	private long mCreateTime = DEFAULT_VALUE;
	private long mUpdateTime = DEFAULT_VALUE;

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

	public long getId() {
		return mId;
	}
	
	public long getCreateTime() {
		return mCreateTime;
	}

	public AccountObject setCreateTime(long createTime) {
		mCreateTime = createTime;
		return this;
	}

	public long getUpdateTime() {
		return mUpdateTime;
	}

	public AccountObject setUpdateTime(long updateTime) {
		mUpdateTime = updateTime;
		return this;
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
		HashMap<String, ProjectRole> roles = AccountDAO.getInstance().getProjectRoleMap(mId);
		if (roles == null) {
			roles = new HashMap<String, ProjectRole>();
		}
		return roles; 
	}

	/**
	 * Get account by account id
	 * 
	 * @param id
	 *            account id
	 * @return AccountObject
	 */
	public static AccountObject get(long id) {
		return AccountDAO.getInstance().get(id);
	}

	/**
	 * Get account by account user name
	 * 
	 * @param username
	 *            account user name
	 * @return AccountObject
	 */
	public static AccountObject get(String username) {
		return AccountDAO.getInstance().get(username);
	}

	/**
	 * get accounts in ezScrum
	 * 
	 * @return AccountObject list
	 */
	public static ArrayList<AccountObject> getAllAccounts() {
		return AccountDAO.getInstance().getAllAccounts();
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
		// 從資料庫拿出來的密碼會是加密過的，所以這邊用長度判斷是否已經經過 MD5 加密，如果長度不符合 32 就用 MD5 hash 過一次
		if (password.length() != 32) {
			password = AccountDAO.getInstance().getMd5(password);
		}
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
		if (exists()) {
			doUpdate();
		} else {
			doCreate();
		}
	}

	@Override
	public void reload() {
		if (exists()) {
			AccountObject account = AccountDAO.getInstance().get(mId);
			resetData(account);
		}
	}

	private boolean exists() {
		AccountObject account = AccountDAO.getInstance().get(mId);
		return account != null;
	}

	private void resetData(AccountObject account) {
		mId = account.getId();
		mUsername = account.getUsername();
		mPassword = account.getPassword();
		mEmail = account.getEmail();
		mNickName = account.getNickName();
		mEnable = account.getEnable();
		mCreateTime = account.getCreateTime();
		mUpdateTime = account.getUpdateTime();
	}

	private void doCreate() {
		mId = AccountDAO.getInstance().create(this);
		reload();
	}

	private void doUpdate() {
		AccountDAO.getInstance().update(this);
	}

	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject account = new JSONObject();

		account.put(AccountEnum.ID, mId)
		       .put(AccountEnum.USERNAME, mUsername)
		       .put(AccountEnum.NICK_NAME, mNickName)
			   .put(AccountEnum.EMAIL, mEmail)
			   .put(AccountEnum.ENABLE, mEnable)
			   .put(AccountEnum.CREATE_TIME, mCreateTime)
			   .put(AccountEnum.UPDATE_TIME, mUpdateTime);
		return account;
	}
}
