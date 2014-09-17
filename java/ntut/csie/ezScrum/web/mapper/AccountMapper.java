package ntut.csie.ezScrum.web.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.control.MantisAccountManager;
import ntut.csie.ezScrum.web.dataObject.ProjectRole;
import ntut.csie.ezScrum.web.dataObject.RoleEnum;
import ntut.csie.ezScrum.web.dataObject.UserInformation;
import ntut.csie.ezScrum.web.dataObject.UserObject;
import ntut.csie.ezScrum.web.sqlService.MySQLService;
import ntut.csie.ezScrum.web.support.TranslateUtil;
import ntut.csie.jcis.account.core.AccountFactory;
import ntut.csie.jcis.account.core.IAccount;
import ntut.csie.jcis.account.core.IAccountManager;
import ntut.csie.jcis.account.core.IActor;
import ntut.csie.jcis.account.core.IPermission;
import ntut.csie.jcis.account.core.IRole;
import ntut.csie.jcis.account.core.LogonException;
import ntut.csie.jcis.resource.core.IProject;

public class AccountMapper {
	private IProject mProject;
	private IUserSession mUserSession;
	//private ITSPrefsStorage mPrefs;
	private Configuration mConfig;
	private MySQLService mService;

	public AccountMapper() {
		//mPrefs = new ITSPrefsStorage();
		//mService = new MySQLService(mPrefs);
		mConfig = new Configuration();
		mService = new MySQLService(mConfig);
	}

	public AccountMapper(IProject project, IUserSession userSession) {
		mProject = project;
		mUserSession = userSession;
		//mPrefs = new ITSPrefsStorage(mProject, mUserSession);
		mConfig = new Configuration(mUserSession);
		mService = new MySQLService(mConfig);
	}

	public UserObject createAccount(UserInformation user) {
		mService.openConnect();
		mService.createAccount(user);
		UserObject account = mService.getAccount(user.getAccount());
		mService.closeConnect();
//		IAccount itsAccount = createAccountToITS(user, roles);	// 當project與role都從外部檔案移到資料庫，就可以刪掉
//		return addRoleFromITS(account, itsAccount);				// 當project與role都從外部檔案移到資料庫，就可以刪掉
		return account;
	}

	/**
	 * 進行編輯帳號的動作，並且將帳號更新角色，in 資料庫 和外部檔案資訊( RoleBase )的部分
	 */
	public UserObject updateAccount(UserInformation user) {
		mService.openConnect();
		mService.updateAccount(user);
		UserObject account = mService.getAccount(user.getAccount());
		mService.closeConnect();
//		IAccount itsAccount = updateAccountToITS(mUserSession, user);	// 當project與role都從外部檔案移到資料庫，就可以刪掉
//		return addRoleFromITS(account, itsAccount);						// 當project與role都從外部檔案移到資料庫，就可以刪掉
		return account;
	}

	/**
	 * 刪除 account
	 */
	public boolean deleteAccount(String id) {
		mService.openConnect();
		boolean result = mService.deleteAccount(id);
		mService.closeConnect();
//		deleteAccountToITS(mUserSession, id);	// 當project與role都從外部檔案移到資料庫，就可以刪掉
		return result;
	}

	public UserObject getAccount(String account) {
		mService.openConnect();
		UserObject user = mService.getAccount(account);
		mService.closeConnect();
//		IAccount itsAccount = getAccountByIdToITS(id);	// 當project與role都從外部檔案移到資料庫，就可以刪掉
//		return addRoleFromITS(account, itsAccount);					// 當project與role都從外部檔案移到資料庫，就可以刪掉
		return user;
	}
	
	public UserObject getAccountById(String id) {
		mService.openConnect();
		UserObject user = mService.getAccountById(id);
		mService.closeConnect();
//		IAccount itsAccount = getAccountByIdToITS(id);	// 當project與role都從外部檔案移到資料庫，就可以刪掉
//		return addRoleFromITS(account, itsAccount);					// 當project與role都從外部檔案移到資料庫，就可以刪掉
		return user;
	}

	public List<UserObject> getAccountList() {
		mService.openConnect();
		List<UserObject> list = mService.getAccountList();
		mService.closeConnect();
//		list = getAccountListToITS();	// 當project與role都從外部檔案移到資料庫，就可以刪掉
		return list;
	}

	public UserObject confirmAccount(String id, String password) throws LogonException {
		mService.openConnect();
		UserObject account = mService.confirmAccount(id, password);
		mService.closeConnect();
		if (account == null) {
			throw new LogonException(false, false);
		} else {
//			IAccount itsAccount = getAccountByIdToITS(id);	// 當project與role都從外部檔案移到資料庫，就可以刪掉
//			return addRoleFromITS(account, itsAccount);		// 當project與role都從外部檔案移到資料庫，就可以刪掉
			return account;
		}
	}

	public HashMap<String, ProjectRole> getProjectRoleList(String id) {
		mService.openConnect();
		HashMap<String, ProjectRole> roles = mService.getProjectRoleList(id);
		mService.closeConnect();
		return roles;
	}
	
	/**
	 * 取得角色在專案中的權限
	 */
	public IPermission getPermission(String project, String role) {
		return getPermissionToITS(project, role);	// 當project與role都從外部檔案移到資料庫，就可以刪掉
	}

	public IPermission getPermission(String role) {
		return getPermissionToITS(role);	// 當project與role都從外部檔案移到資料庫，就可以刪掉
	}

	public void createRole(IProject p) throws Exception {
		createRoleToITS(p);	// 當project與role都從外部檔案移到資料庫，就可以刪掉
	}

	public void removeRole(IUserSession session, IAccount account, String id, List<String> roleList, String res) throws Exception {
		removeRoleToITS(mUserSession, account, id, roleList, res);	// 當project與role都從外部檔案移到資料庫，就可以刪掉
	}
	
	public UserObject removeRoleToDb(String projectId, String accountId, RoleEnum role) {
		mService.openConnect();
		mService.deleteProjectRole(projectId, accountId, role);
		UserObject result = mService.getAccountById(accountId);
		mService.closeConnect();
		return result;
	}
	
	public UserObject removeRoleToDb(String accountId) {
		mService.openConnect();
		mService.deleteSystemRole(accountId);
		UserObject result = mService.getAccountById(accountId);
		mService.closeConnect();
		return result;
	}

	public void addRole(IUserSession session, IAccount account, List<String> roleList, String id, String res, String op) throws Exception {
		addRoleToITS(mUserSession, account, roleList, id, res, op);	// 當project與role都從外部檔案移到資料庫，就可以刪掉
	}
	
	public UserObject addRoleToDb(String projectId, String accountId, RoleEnum role) {
		mService.openConnect();
		mService.createProjectRole(projectId, accountId, role);
		UserObject result = mService.getAccountById(accountId);
		mService.closeConnect();
		return result;
	}

	public UserObject addRoleToDb(String accountId) {
		mService.openConnect();
		mService.createSystemRole(accountId);
		UserObject result = mService.getAccountById(accountId);
		mService.closeConnect();
		return result;
	}
	
	/**
	 * 若帳號可建立且ID format正確 則回傳true
	 */
	public boolean isAccountExist(String id) {
		mService.openConnect();
		UserObject account = mService.getAccount(id);
		mService.closeConnect();
		return account != null;
	}

	/**
	 * TODO: 將外部檔案改成DB
	 */
	public void createPermission(IProject p) throws Exception {
		createPermissionToITS(p);	// 當project與role都從外部檔案移到資料庫，就可以刪掉
	}
	
	/**
	 * 過度期使用，當外部檔案都轉移完畢即可刪掉
	 */	
	@Deprecated
	private IAccount addRoleFromITS(IAccount account, IAccount itsAccount) {
		if (account!= null && itsAccount != null && itsAccount.getRoles() != null) {
			for (IRole role : itsAccount.getRoles()) {
				account.addRole(role);
			}
		}
		return account;
	}
	
	/**
	 * old operation
	 * delete it when project and scrum role is ready
	 */

	private String[] operation = {"ProductOwner", "ScrumMaster", "ScrumTeam", "Stakeholder", "Guest"};

	@Deprecated
	public IAccount createAccountToITS(UserInformation userInformation, String roles) {
		String id = userInformation.getAccount();
		String realName = userInformation.getName();
		String password = userInformation.getPassword();
		String email = userInformation.getEmail();
		String enable = userInformation.getEnable();

		IAccountManager am = getManager();
		// 建立帳號(RoleBase部分)
		IAccount account = AccountFactory.createAccount(id, realName, password, true);
		account.setEmail(email);

		// 預防 null 的發生 -> when?
		if ((enable == null) || (enable.length() == 0)) {
			enable = "true";
		}
		account.setEnable(enable);

		am.addAccount(account);
		account = am.getAccount(id);
		List<String> roleList = TranslateUtil.translateRoleString(roles);

		// 確認已成功加入Assign roles
		if (account != null) {
			am.updateAccountRoles(id, roleList);
			am.save();
		}
		return account;
	}

	/**
	 * 進行編輯帳號的動作，並且將帳號更新角色，in 資料庫 和外部檔案資訊( RoleBase )的部分
	 * 
	 * @param session
	 * @param userInformation
	 * @return
	 */
	@Deprecated
	public IAccount updateAccountToITS(IUserSession session, UserInformation userInformation) {
		this.updateAccountInWorkspace(userInformation);
		IAccount account = this.getAccountByIdToITS(userInformation.getAccount());
		this.updateAccountInDatabase(session, account);
		return account;
	}

	/**
	 * 進行編輯帳號的動作，並且將帳號更新角色 in mantis 資訊的部分
	 * 
	 * @param session
	 * @param updateAccount
	 */
	@Deprecated
	private void updateAccountInDatabase(IUserSession session, IAccount updateAccount) {
		MantisAccountManager accountManager = new MantisAccountManager(session);
		try {
			accountManager.updateUserProfile(updateAccount);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 進行編輯帳號的動作，並且將帳號更新角色 in 外部檔案資訊的部分
	 * 
	 * @param userInformation
	 */
	@Deprecated
	private void updateAccountInWorkspace(UserInformation userInformation) {
		String id = userInformation.getAccount();
		String name = userInformation.getName();
		String pwd = userInformation.getPassword();
		String mail = userInformation.getEmail();
		String enable = userInformation.getEnable();

		IAccountManager am = getManager();
		IAccount updateAccount = am.getAccount(id);

		// 預防 null 的發生
		if ((enable == null) || (enable.length() == 0)) {
			enable = updateAccount.getEnable();
		}

		// no password, use the default password
		if ((pwd == null) || (pwd.length() == 0) || pwd.equals("")) {
			pwd = updateAccount.getPassword(); // get default password
			am.updateAccountData(id, name, pwd, mail, enable, false); // false 不經過Md5編碼
		} else {
			am.updateAccountData(id, name, pwd, mail, enable, true); // true 經過Md5編碼
		}

		am.save();
	}

	/**
	 * 刪除 account in "外部檔案" 和 "資料庫" 資訊的部分
	 */
	@Deprecated
	public void deleteAccountToITS(IUserSession session, String id) {
		this.deleteAccountInDatabase(session, id);
		this.deleteAccountInWorkspace(id);
	}

	/**
	 * 刪除 account in mantis 資訊的部分
	 * 
	 * @param session
	 * @param id
	 */
	@Deprecated
	private void deleteAccountInDatabase(IUserSession session, String id) {
		MantisAccountManager accountManager = new MantisAccountManager(session);
		try {
			accountManager.deleteAccount(getManager(), id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 刪除 account in ezScrum local 的部分
	 * 
	 * @param id
	 */
	@Deprecated
	private void deleteAccountInWorkspace(String id) {
		// 刪除帳號, 包含群組內與Assign的資料
		getManager().removeAccount(id);
		getManager().save();
	}

	/**
	 * 進行帳號更新的動作, 並且將帳號 Assign Roles, 建立完畢執行儲存檔案 in "外部檔案" 和 "資料庫"
	 */
	@Deprecated
	public void addRoleToITS(IUserSession session, IAccount account, List<String> roleList, String id, String res, String op) throws Exception {
		this.updateAccountRoleInDatabase(session, account, res, op);
		this.updateAccountRoleInWorkspace(id, roleList);
	}

	/**
	 * 進行帳號更新的動作, 並且將帳號 Assign Roles, 建立完畢執行儲存檔案 in "資料庫"
	 * 
	 * @param session
	 * @param account
	 * @param res
	 * @param op
	 * @throws Exception
	 */
	@Deprecated
	private void updateAccountRoleInDatabase(IUserSession session, IAccount account, String res, String op) throws Exception {
		MantisAccountManager accountManager = new MantisAccountManager(session);
		accountManager.addReleation(account, res, op);
	}

	/**
	 * 進行帳號更新的動作, 並且將帳號 Assign Roles, 建立完畢執行儲存檔案 in "外部檔案"
	 * 
	 * @param id
	 * @param roleList
	 */
	@Deprecated
	private void updateAccountRoleInWorkspace(String id, List<String> roleList) {
		getManager().updateAccountRoles(id, roleList);
		getManager().save();
	}

	/**
	 * 進行帳號更新的動作, 並且將帳號 Remove Roles, 建立完畢執行儲存檔案 in "外部檔案" 和 "資料庫"
	 * 
	 * @param session
	 * @param account
	 * @param id
	 * @param roleList
	 * @param res
	 * @throws Exception
	 */
	@Deprecated
	public void removeRoleToITS(IUserSession session, IAccount account, String id, List<String> roleList, String res) throws Exception {
		this.removeAccountRoleInDatabase(session, account, res);
		this.removeAccountRoleInWorkspace(id, roleList);
	}

	/**
	 * 進行帳號更新的動作, 並且將帳號 Remove Roles, 建立完畢執行儲存檔案 in "外部檔案"
	 * 
	 * @param id
	 * @param roleList
	 */
	@Deprecated
	private void removeAccountRoleInWorkspace(String id, List<String> roleList) {
		getManager().updateAccountRoles(id, roleList);
		getManager().save();
	}

	/**
	 * 進行帳號更新的動作, 並且將帳號 Remove Roles, 建立完畢執行儲存檔案 in "資料庫"
	 * 
	 * @param session
	 * @param account
	 * @param res
	 * @throws Exception
	 */
	@Deprecated
	private void removeAccountRoleInDatabase(IUserSession session, IAccount account, String res) throws Exception {
		MantisAccountManager accountManager = new MantisAccountManager(session);
		accountManager.removeReleation(account, res);
	}

	/**
	 * 建立 rolebase 的各專案的 permission
	 * 
	 * @throws Exception
	 */
	@Deprecated
	public void createPermissionToITS(IProject p) throws Exception {
		String resource = p.getName();

		IAccountManager am = getManager();
		for (String oper : operation) {
			String name = resource + "_" + oper;
			IPermission oriPerm = am.getPermission(name);
			if (oriPerm == null) {
				IPermission perm = AccountFactory.createPermission(name, resource, oper);
				am.addPermission(perm);
				perm = am.getPermission(name);

				// 若perm為空代表沒新增成功
				if (perm == null) {
					am.referesh();
					throw new Exception("建立Permission失敗!!");
				}

				am.save();
			}
		}
	}

	@Deprecated
	public void createRoleToITS(IProject p) throws Exception {
		String resource = p.getName();

		IAccountManager am = getManager();
		for (String oper : operation) {
			String name = resource + "_" + oper;
			IRole oriRole = AccountFactory.getManager().getRole(name);
			if (oriRole == null) {
				IRole role = AccountFactory.createRole(name, name);
				am.addRole(role);
				List<String> permissionNameList = new ArrayList<String>();
				permissionNameList.add(name);
				// 加入成功則進行群組成員與Role的設置
				if (am.getRole(name) != null) {
					am.updateRolePermission(name, permissionNameList);
				} else {
					throw new Exception("建立Role失敗!!");
				}
				// 儲存檔案
				am.save();
			}
		}
	}

	@Deprecated
	private IAccountManager getManager() {
		return AccountFactory.getManager();
	}

	@Deprecated
	public void releaseManager() {
		AccountFactory.releaseManager();
	}

	/**
	 * 取得角色在專案中的權限
	 * 
	 * @param project
	 * @param role
	 * @return
	 */
	@Deprecated
	public IPermission getPermissionToITS(String project, String role) {
		return getManager().getPermission(project, role);
	}

	@Deprecated
	public IPermission getPermissionToITS(String role) {
		return getManager().getPermission(role);
	}

	/**
	 * 若帳號可建立且ID format正確 則回傳true
	 * 
	 * @param id
	 * @return
	 */
	@Deprecated
	public IAccount getAccountByIdToITS(String id) {
		return getManager().getAccount(id);
	}

	@Deprecated
	public List<IActor> getAccountListToITS() {
		return getManager().getAccountList();
	}
}
