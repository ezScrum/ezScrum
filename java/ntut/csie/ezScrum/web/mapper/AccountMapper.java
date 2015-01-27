package ntut.csie.ezScrum.web.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.control.MantisAccountManager;
import ntut.csie.ezScrum.web.dataInfo.AccountInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectRole;
import ntut.csie.ezScrum.web.databasEnum.RoleEnum;
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
	private Configuration mConfig;
//	private MySQLService mService;

	public AccountMapper() {
		mConfig = new Configuration();
//		mService = new MySQLService(mConfig);
	}

	public AccountMapper(IProject project, IUserSession userSession) {
		mProject = project;
		mUserSession = userSession;
		mConfig = new Configuration(mUserSession);
//		mService = new MySQLService(mConfig);
	}

	public AccountObject createAccount(AccountInfo accountInfo) {
		AccountObject account = new AccountObject(accountInfo.userName);
		account.setEmail(accountInfo.email).setPassword(accountInfo.password)
				.setNickName(accountInfo.nickName).setEnable(true).save();
		return account;
	}

	/**
	 * Get account by userName
	 * 
	 * @param userName
	 * @return AccountObject
	 */
	public AccountObject getAccount(String userName) {
		AccountObject user = AccountObject.get(userName);
		return user;
	}

	/**
	 * Get account by account id
	 * 
	 * @param id
	 * @return AccountObject
	 */
	public AccountObject getAccount(long id) {
		AccountObject user = AccountObject.get(id);
		return user;
	}

	/**
	 * Get all account in DB
	 * 
	 * @return AccountObject list
	 */
	public ArrayList<AccountObject> getAccounts() {
		return AccountObject.getAccounts();
	}
	
	/**
	 * Update account info use DAO
	 * 
	 * @param accountInfo
	 * @return AccountObject
	 */
	public AccountObject updateAccount(AccountInfo accountInfo) {
		AccountObject account = AccountObject.get(accountInfo.id);
		account.setEmail(accountInfo.email)
				.setPassword(accountInfo.password)
				.setNickName(accountInfo.nickName)
				.setEnable(accountInfo.enable)
				.save();
		return account;
	}

	/**
	 * Delete account by id
	 * 
	 * @param id
	 * @return boolean
	 */
	public boolean deleteAccount(long id) {
		AccountObject account = AccountObject.get(id);
		boolean result = account.delete();
		return result;
	}

	/**
	 * Use username and password to get account
	 * 
	 * @param username
	 * @param password
	 * @return AccountObject
	 * @throws LogonException
	 */
	public AccountObject confirmAccount(String username, String password) throws LogonException {
		AccountObject account = AccountObject.confirmAccount(username, password);
		if (account == null) {
			throw new LogonException(false, false);
		} else {
			return account;
		}
	}

	/**
	 * 取出 account 的在 project 的 role 權限列表
	 * 
	 * @param id account id
	 * @return 權限列表
	 */
	public HashMap<String, ProjectRole> getProjectRoleList(long id) {
		AccountObject account = AccountObject.get(id);
		return account.getProjectRoleMap();
	}

	/**
	 * Create a project's role for account
	 * 
	 * @param projectId
	 * @param accountId
	 * @param role
	 * @return AccountObject
	 */
	public AccountObject addProjectRole(long projectId, long accountId,
			RoleEnum role) {
		AccountObject account = AccountObject.get(accountId);
		account.createProjectRole(projectId, role);
		return account;
	}
	
	/**
	 * Create a system's role for account
	 * 
	 * @param accountId
	 * @return AccountObject
	 */
	public AccountObject addSystemRole(long accountId) {
		AccountObject account = AccountObject.get(accountId);
		account.createSystemRole();
		return account;
	}
	
	/**
	 * Remove a project's role for account
	 * 
	 * @param projectId
	 * @param accountId
	 * @param role
	 * @return AccountObject
	 */
	public AccountObject removeProjectRole(long projectId, long accountId,
			RoleEnum role) {
		AccountObject account = AccountObject.get(accountId);
		account.deleteProjectRole(projectId, role);
		return account;
	}

	/**
	 * Remove a system's role for account
	 * 
	 * @param accountId
	 * @return AccountObject
	 */
	public AccountObject removeSystemRole(long accountId) {
		AccountObject account = AccountObject.get(accountId);
		account.deleteSystemRole();
		return account;
	}
	
	/**
	 * 判斷 userName 是否已被建立且格式是否正確
	 * 
	 * @param userName
	 * @return boolean
	 */
	public boolean isAccountExist(String userName) {
		AccountObject account = AccountObject.get(userName);
		return account != null;
	}
	
	public void createRole(IProject p) throws Exception {
		createRoleToITS(p); // 當project與role都從外部檔案移到資料庫，就可以刪掉
	}

	/**
	 * TODO: 將外部檔案改成DB
	 */
	public void createPermission(IProject p) throws Exception {
		createPermissionToITS(p); // 當project與role都從外部檔案移到資料庫，就可以刪掉
	}

	private String[] operation = { "ProductOwner", "ScrumMaster", "ScrumTeam",
			"Stakeholder", "Guest" };

	/**
	 * 進行帳號更新的動作, 並且將帳號 Assign Roles, 建立完畢執行儲存檔案 in "外部檔案" 和 "資料庫"
	 */
	@Deprecated
	public void addRoleToITS(IUserSession session, IAccount account,
			List<String> roleList, String id, String res, String op)
			throws Exception {
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
	private void updateAccountRoleInDatabase(IUserSession session,
			IAccount account, String res, String op) throws Exception {
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
	public void removeRoleToITS(IUserSession session, IAccount account,
			String id, List<String> roleList, String res) throws Exception {
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
	private void removeAccountRoleInDatabase(IUserSession session,
			IAccount account, String res) throws Exception {
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
				IPermission perm = AccountFactory.createPermission(name,
						resource, oper);
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
}
