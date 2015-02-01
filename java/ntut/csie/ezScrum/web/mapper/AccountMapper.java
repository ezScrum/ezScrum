package ntut.csie.ezScrum.web.mapper;

import java.util.ArrayList;
import java.util.HashMap;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataInfo.AccountInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.ProjectRole;
import ntut.csie.ezScrum.web.databasEnum.RoleEnum;
import ntut.csie.jcis.account.core.AccountFactory;
import ntut.csie.jcis.account.core.IAccountManager;
import ntut.csie.jcis.account.core.IPermission;
import ntut.csie.jcis.account.core.IRole;
import ntut.csie.jcis.account.core.LogonException;

public class AccountMapper {
	
	private IUserSession mUserSession = null;
	
	public AccountMapper(IUserSession userSession) {
		mUserSession = userSession;
	}

	public AccountMapper() {}

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
		return AccountObject.getAllAccounts();
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
	
	
	/**
	 * 建立 rolebase 的各專案的 permission
	 * @throws Exception
	 */
	public void createPermission(ProjectObject project) throws Exception {
		String projectName = project.getName();

		IAccountManager am = getManager();
		for (RoleEnum role : RoleEnum.values()) {
			String name = projectName + "_" + role.name();
			IPermission oriPerm = am.getPermission(name);
			if (oriPerm == null) {
				IPermission perm = AccountFactory.createPermission(name,
						projectName, role.name());
				am.addPermission(perm);
				perm = am.getPermission(name);

				// 若 perm 為空代表沒新增成功
				if (perm == null) {
					am.referesh();
					throw new Exception("建立 Permission 失敗!!");
				}

				am.save();
			}
		}
	}
	
	public void createRole(ProjectObject project) throws Exception {
		String projectName = project.getName();

		IAccountManager am = getManager();
		for (RoleEnum role : RoleEnum.values()) {
			String name = projectName + "_" + role.name();
			IRole oriRole = AccountFactory.getManager().getRole(name);
			if (oriRole == null) {
				IRole irole = AccountFactory.createRole(name, name);
				am.addRole(irole);
				ArrayList<String> permissionNameList = new ArrayList<String>();
				permissionNameList.add(name);
				// 加入成功則進行群組成員與 Role 的設置
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
}
