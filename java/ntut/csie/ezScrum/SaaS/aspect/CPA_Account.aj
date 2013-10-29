/*
 * [AspectJ 語法參考]
 * 
 * 1.取代原有函式的執行(代入與回傳的參數型態保持一致)
 *   pointcut 切點函式名稱(代入參數型態 代入參數名稱)
 *   : execution(回傳參數型態 欲取代的函式名稱(代入參數型態)) && arg(代入參數名稱);
 *   	 
 *   回傳參數型態 around(代入參數型態 代入參數名稱)
 *   : 切點函式名稱(代入參數名稱) {
 *   	// replaced code
 *   }
 *   
 *   ex: 
 *   pointcut replaceFunc(String arg0, String arg1)
 *   : execution(String className.replacedFunc(String, String)) && arg(arg0, arg1);
 *   
 *   String around(String arg0, String arg1)
 *   : replaceFunc(arg0, arg1) {
 *   	System.out.println("我要取代妳的功能: " + thisJoinPoint);
 *   }
 *   
 *   2.有拋出例外的函式: 加在 around 之後 
 *   ex:
 *   	void around() throws Exception:
 */
package ntut.csie.ezScrum.SaaS.aspect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import ntut.csie.ezScrum.SaaS.PMF;
import ntut.csie.ezScrum.SaaS.database.AccountDataStore;
import ntut.csie.ezScrum.SaaS.interfaces.account.Account;
import ntut.csie.ezScrum.SaaS.util.ScrumEnum;
import ntut.csie.ezScrum.SaaS.util.ezScrumUtil;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.UserInformation;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.ezScrum.web.support.TranslateUtil;
import ntut.csie.jcis.account.core.IAccount;
import ntut.csie.jcis.account.core.IAccountManager;
import ntut.csie.jcis.account.core.IActor;
import ntut.csie.jcis.account.core.IPermission;
import ntut.csie.jcis.account.core.IRole;
import ntut.csie.jcis.account.core.LogonException;
import ntut.csie.jcis.account.core.internal.Permission;
import ntut.csie.jcis.account.core.internal.Role;
import ntut.csie.jcis.resource.core.IProject;

import org.datanucleus.exceptions.NucleusObjectNotFoundException;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import javax.jdo.Query;


// 有關資料庫物件存取的程式碼 (DataStore)必須做 data nucleus enhance 的動作
public aspect CPA_Account {
	
	/*
	 * AccountMapper
	 */	

	// replace: constructor of AccountMapper.new()
	pointcut AccountMapperPC() 
	: execution(AccountMapper.new());

	void around() :
		AccountMapperPC() {
		System.out.println("replaced by AOP...AccountMapperPC: " + thisJoinPoint);
	}
	
	//	replace : public IAccount createAccount(UserInformation userInformation, String roles)
	pointcut createAccountPC(UserInformation userInformation, String roles)
	: execution(IAccount AccountMapper.createAccount(UserInformation, String)) && args(userInformation, roles);
	
	IAccount around(UserInformation userInformation, String roles)
	: createAccountPC(userInformation, roles) {
		System.out.println("replaced by AOP...createAccountPC: " + thisJoinPoint);

		String id = userInformation.getId();
		String realName = userInformation.getName();
		String password = userInformation.getPassword();
		String email = userInformation.getEmail();
		String enable = userInformation.getEnable();
		
//		AccountMapperJDO am = new AccountMapperJDO();
//		return am.createAccount(id, realName, password, email, enable, roles);
		
//		IAccountManager am = AccountFactory.getManager();
		
		// 建立帳號(RoleBase部分) 第四個參數 encryption 強制使用 MD5 加密
//		IAccount account = AccountFactory.createAccount(id, realName, password);
		IAccount account = new Account(id, realName, ezScrumUtil.getMd5(password), false);
		account.setEmail(email);
		
		// 預防 null 的發生 -> when?
		if ( (enable == null) || (enable.length() == 0) ) {
			enable = "true";
		}		
		account.setEnable(enable);
		
		this.doAddAccount((Account)account);
//		account = am.getAccount(id);
		account = this.doGetAccountById(id);
		List<String> roleList = TranslateUtil.translateRoleString(roles);
	
		// 確認已成功加入Assign roles
		if (account != null) {
			this.doUpdateAccountRoles(id, roleList);
//			am.updateAccountRoles(id, roleList);
//			am.save();
		}
		return account;
	}	
	
	private void doAddAccount(Account account) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Key key = KeyFactory.createKey(AccountDataStore.class.getSimpleName(), account.getID());
		
		AccountDataStore accountData = new AccountDataStore(key, account.getID(), account.getPassword());
		accountData.setName(account.getName());
		accountData.setEmail(account.getEmail());
		accountData.setEnable(account.getEnable());
		
		// ?
		List<String> permissions = new ArrayList<String>();
		for (int i=0; i<account.getPermissionList().size(); i++) {
			permissions.add(account.getPermissionList().get(i).getPermissionName());
		}
		accountData.setPermissions(permissions);

		try {
			pm.makePersistent(accountData);
		} finally {
			pm.close();
		}		
	}
	
	// roleList = $ProjectName_$ScrumRole
	private void doUpdateAccountRoles(String id, List<String> roleList) {			
		PersistenceManager pm = PMF.get().getPersistenceManager();

		Key key = KeyFactory.createKey(AccountDataStore.class.getSimpleName(), id);
		AccountDataStore accountData = pm.getObjectById(AccountDataStore.class, key);
		
		List<String> newPermission = accountData.getPermissions();		
		if (newPermission == null)
			newPermission = new ArrayList<String>();
		
		newPermission.clear();	// 代入的 roleList 是要完整要加入的清單
    	for (int i=0; i < roleList.size(); i++) {
    		newPermission.add(roleList.get(i));
    	}

    	accountData.setPermissions(newPermission);
		
		try {
			pm.makePersistent(accountData);
		} finally {
			pm.close();
		}				
	}
	
	// replace: public IAccount updateAccount(IUserSession session, UserInformation userInformation)
	pointcut updateAccountPC(IUserSession session, UserInformation userInformation)
	: execution(IAccount AccountMapper.updateAccount(IUserSession, UserInformation)) && args(session, userInformation);
	
	IAccount around(IUserSession session, UserInformation userInformation)
	: updateAccountPC(session, userInformation) {
		System.out.println("replaced by AOP...updateAccountPC: " + thisJoinPoint);
		
		String id = userInformation.getId();
		String name = userInformation.getName();
		String pwd = userInformation.getPassword();
		String mail = userInformation.getEmail();
		String enable = userInformation.getEnable();
		
//		AccountMapperJDO am = new AccountMapperJDO();		
//		return am.updateAccount(id, name, pwd, mail, enable);
		
//		IAccount updateAccount = am.getAccount(id);
		IAccount updateAccount = this.doGetAccountById(id);
		
		// 預防 null 的發生
		if ( (enable == null) || (enable.length() == 0) ) {
//			System.out.println("updateAccount: enable??");
			enable = updateAccount.getEnable();
		}		
		
//		if ( (pwd == null) || (pwd.length()==0) || pwd.equals("") ) {
//			pwd = updateAccount.getPassword();		// get default password
//			am.updateAccountData(id, name, pwd, mail, enable, false);	// false 不經過Md5編碼 and set default password
//		} else {
//			am.updateAccountData(id, name, pwd, mail, enable, true);	// true 經過Md5編碼
//		}
		
		String newPasswd;
		if ( !((pwd == null) || (pwd.length()==0) || pwd.equals("")) ) {
			System.out.println("true 經過Md5編碼");
			newPasswd = ezScrumUtil.getMd5(pwd);	//true 經過Md5編碼
		} else {
			System.out.println("false 不經過Md5編碼 and set default password");
			newPasswd = updateAccount.getPassword();
		}
		
		updateAccount.setEmail(mail);
		updateAccount.setName(name);
		updateAccount.setPassword(newPasswd);
		updateAccount.setEnable(enable);
		this.doUpdateAccount(updateAccount);
		
//		am.save();		
		
//		updateAccount = am.getAccount(id);	// for double check
		updateAccount = this.doGetAccountById(id);	// for double check	
		return updateAccount;
	}
	
	private void doUpdateAccount(IAccount account) {
		PersistenceManager pm = PMF.get().getPersistenceManager();

		Key key = KeyFactory.createKey(AccountDataStore.class.getSimpleName(), account.getID());
		AccountDataStore accountData = pm.getObjectById(AccountDataStore.class, key);
		
		accountData.setPassword(account.getPassword());
		accountData.setName(account.getName());
		accountData.setEmail(account.getEmail());
		accountData.setEnable(account.getEnable());		
		// Permission 不會更新不用處理
		
		try {
			pm.makePersistent(accountData);
		} finally {
			pm.close();
		}		
	}
	
	// replace: public void deleteAccount( IUserSession session, String id )
	pointcut deleteAccountPC(IUserSession session, String id)
	: execution(void AccountMapper.deleteAccount(IUserSession, String)) && args(session, id);
	
	void around(IUserSession session, String id)
	: deleteAccountPC(session, id) {
		System.out.println("replaced by AOP...deleteAccountPC: " + thisJoinPoint);

//		AccountMapperJDO am = new AccountMapperJDO();		
//		am.deleteAccount(id);
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Key key = KeyFactory.createKey(AccountDataStore.class.getSimpleName(), id);
		AccountDataStore accountData = pm.getObjectById(AccountDataStore.class, key);

		try {
			pm.deletePersistent(accountData);
		} finally {
			pm.close();
		}
	}
	
	// replace: public void assignRole_add(IUserSession session, IAccount account, List<String> roleList, String id, String res, String op)
	pointcut assignRole_addPC(IUserSession session, IAccount account, List<String> roleList, String id, String res, String op)
	: execution(void AccountMapper.assignRole_add(IUserSession, IAccount, List<String>, String, String, String)) && args(session, account, roleList, id, res, op);
	
	void around(IUserSession session, IAccount account, List<String> roleList, String id, String res, String op) throws Exception
	: assignRole_addPC(session, account, roleList, id, res, op) {
		System.out.println("replaced by AOP...assignRole_addPC: " + thisJoinPoint);

//		AccountMapperJDO am = new AccountMapperJDO();		
//		am.assignRole_add(id, roleList);
		
		this.doUpdateAccountRoles(id, roleList);
	}

	//	replace:public void assignRole_remove(IUserSession session, IAccount account, String id, List<String> roleList, String res)
	pointcut assignRole_removePC(IUserSession session, IAccount account, String id, List<String> roleList, String res)
	: execution(void AccountMapper.assignRole_remove(IUserSession, IAccount, String, List<String>, String)) && args(session, account, id, roleList, res);
	
	void around(IUserSession session, IAccount account, String id, List<String> roleList, String res) throws Exception
	: assignRole_removePC(session, account, id, roleList, res) {
		System.out.println("replaced by AOP...assignRole_removePC: " + thisJoinPoint);

//		AccountMapperJDO am = new AccountMapperJDO();		
//		am.assignRole_remove(id, roleList);
		
		this.doUpdateAccountRoles(id, roleList);
	}
	
	// replace: public void createPermission(IProject p) throws Exception
	pointcut createPermissionPC(IProject p) 
	: execution(void AccountMapper.createPermission(IProject)) && args(p);
	
	void around(IProject p) throws Exception
	: createPermissionPC(p) {
		System.out.println("replaced by AOP...createPermissionPC: " + thisJoinPoint);

//		AccountMapperJDO am = new AccountMapperJDO();	
//		am.createPermission(p);	
		
//		String resource = p.getName();
//		
//		IAccountManager am = AccountFactory.getManager();
//		AccountManagerTemp accountManagerTemp = new AccountManagerTemp();
//		for (String oper : operation) {
//			String name = resource + "_" + oper;
//			IPermission oriPerm = accountManagerTemp.getPermission(name);;
//			if (oriPerm == null) {
//				IPermission perm = AccountFactory.createPermission(name, resource, resource);
//				IPermission perm = new Permission(name, resource, resource);
//				am.addPermission(perm);
//				perm = am.getPermission(name);	// 若perm為空代表沒新增成功
//				
//				accountManagerTemp.addPermission(perm);
//				perm = accountManagerTemp.getPermission(name);
//			
//				am.save();
//			}
//		}
	}
	
	// replace: public void createRole(IProject p) throws Exception
	pointcut createRolePC(IProject p) 
	: execution(void AccountMapper.createRole(IProject)) && args(p);
	
	void around(IProject p) throws Exception
	: createRolePC(p) {
		System.out.println("replaced by AOP...createRolePC: " + thisJoinPoint);

//		AccountMapperJDO am = new AccountMapperJDO();		
//        try {
//            am.createRole(p);
//        } finally {            
//        }		
		
//		String resource = p.getName();
//		
//		IAccountManager am = AccountFactory.getManager();
//		for (String oper : operation) {
//			String name = resource + "_" + oper;
//			IRole oriRole = am.getRole(name);
//			
//			if (oriRole == null) {
//				IRole role = AccountFactory.createRole(name, name);
//				IRole role = new Role(name, name);
//				am.addRole(role);
//				List<String> permissionNameList = new ArrayList<String>();
//				permissionNameList.add(name);
//				
//				am.save();
//			}
//		}
	}	
	
	/**
	 * ------------ Account Manager -----------
	 */
	
	// replace: public IAccountManager getManager()
	pointcut getManagerPC() 
	: execution(IAccountManager AccountMapper.getManager());
	
	IAccountManager around()
	: getManagerPC() {
		System.out.println("replaced by AOP...getManagerPC: " + thisJoinPoint);

//		AccountMapperJDO am = new AccountMapperJDO();		
//		return am.getManager();
		return null;
	}	
	
	// replace: public boolean isAccountExist(String id)
	pointcut isAccountExistPC(String id) 
	: execution(boolean AccountMapper.isAccountExist(String)) && args(id);
	
	boolean around(String id)
	: isAccountExistPC(id) {
		System.out.println("replaced by AOP...isAccountExistPC: " + thisJoinPoint);

//		AccountMapperJDO am = new AccountMapperJDO();		
//		return am.isAccountExist(id);
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Key key = KeyFactory.createKey(AccountDataStore.class.getSimpleName(), id);
		AccountDataStore accountData;
		try{
			accountData = pm.getObjectById(AccountDataStore.class, key);
		}catch (NucleusObjectNotFoundException ex) {
			accountData = null;
			return false;
		}catch (JDOObjectNotFoundException ex) {
			accountData = null;
			return false;
		}finally{
			pm.close();
		}
		if(accountData != null ){
			return true;
		}else{
			return false;
		}
	}
	
	// replace: public IAccount getAccountById(String id)
	pointcut getAccountByIdPC(String id) 
	: execution(IAccount AccountMapper.getAccountById(String)) && args(id);
	
	IAccount around(String id)
	: getAccountByIdPC(id) {
		System.out.println("replaced by AOP...getAccountByIdPC: " + thisJoinPoint);

//		AccountMapperJDO am = new AccountMapperJDO();		
//		return am.getAccountById(id);
		
		return doGetAccountById(id);
	}

	private IAccount doGetAccountById(String id) {
		Account account = null;
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Query query = pm.newQuery(AccountDataStore.class);
		query.setFilter("id == idParam");
	    query.declareParameters("String idParam");

	    @SuppressWarnings("unchecked")
	    List<AccountDataStore> result = (List<AccountDataStore>)query.execute(id);
	    
	    if (result.size() > 0) {	
	    	AccountDataStore accountData = result.get(0);
//	    	account = AccountFactory.createAccount(accountData.getId(), accountData.getName(), accountData.getPassword());
//	    	account = AccountFactory.createAccount(accountData.getId(), accountData.getName(), ezScrumUtil.getMd5(accountData.getPassword()), true);
	    	account = new Account(accountData.getId(), accountData.getName(), ezScrumUtil.getMd5(accountData.getPassword()), false);
	    	account.setPassword(accountData.getPassword());
	    	account.setEmail(accountData.getEmail());
	    	account.setEnable(accountData.getEnable());	// add
	    	
	    	// Account - Permission 轉換成 Account - Role - Permission - Resource_Operation		
	    	List<String> permList = accountData.getPermissions();	    	 					    	
	    	convertToRBAC(account, permList);
	    } else if (id.equals("admin")) {
	    	// 若未存在 admin 帳號資料則先建立	    	
			String password = ezScrumUtil.getMd5("admin");
			String enable = "true";
			String email = "example@ezScrum.tw";
			
			Key key = KeyFactory.createKey(AccountDataStore.class.getSimpleName(), id);

			try {
				pm.getObjectById(AccountDataStore.class, key);
			} catch (JDOObjectNotFoundException e) {
				System.out.println(">>>>>>>> create tenant admin");
				AccountDataStore accountData = new AccountDataStore(key, id, password);
				accountData.setName(id);
				accountData.setEnable(enable);
				accountData.setEmail(email);
				
				// Permission -> fix later
				List<String> permissions = new ArrayList<String>();
//				permissions.add(AccountManager.ADMINISTRATOR_PERMISSION);
				permissions.add(ScrumEnum.TENANT_PERMISSION);
				accountData.setPermissions(permissions);
				
				pm.makePersistent(accountData);
			}	
			
			// 回傳			
//	    	account = AccountFactory.createAccount(id, id, password);
			account = new Account(id, id, ezScrumUtil.getMd5(password), false);
	    	account.setPassword(password);
	    	account.setEmail(email);
	    	account.setEnable(enable);
	    	
	    	// Permission	    	
		}
	    
		pm.close();
		return account;
	}	
	
	
	
	// replace: public List<IActor> getAccountList()
	pointcut getAccountListPC() 
	: execution(List<IActor> AccountMapper.getAccountList());
	
	List<IActor> around()
	: getAccountListPC() {
		System.out.println("replaced by AOP...getAccountListPC: " + thisJoinPoint);

//		AccountMapperJDO am = new AccountMapperJDO();		
//		return am.doGetAccountList();
		
		ArrayList<IActor> accountList = new ArrayList<IActor>();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Query query = pm.newQuery(AccountDataStore.class);
		//Query query = pm.newQuery("select from AccountDataStore");
		@SuppressWarnings("unchecked")
		List<AccountDataStore> result = (List<AccountDataStore>) query.execute();
		
		for(int i=0; i<result.size(); i++)
		{
			AccountDataStore accountData = result.get(i);
			
//			Account account = AccountFactory.createAccount(accountData.getId(), accountData.getName(), accountData.getPassword());
			Account account = new Account(accountData.getId(), accountData.getName(), ezScrumUtil.getMd5(accountData.getPassword()), false);
			account.setPassword(accountData.getPassword());
	    	account.setEmail(accountData.getEmail());
	    	account.setEnable(accountData.getEnable());	// add
	    	
	    	// Permission: ProjectName_RoleName
	    	List<String> permList = accountData.getPermissions();
	    	convertToRBAC(account, permList);
	    	accountList.add(account);				
		}
			
		pm.close();
		
		return accountList;
	}
	
	/**
	 * Account - Permission 轉換成 Account - Role - Permission - Resource_Operation
	 * @param account
	 * @param permList
	 */
	private void convertToRBAC(Account account, List<String> permList) {
	
//		System.out.println("permList size = " + permList.size());
		
    	for (int i=0; i < permList.size(); i++) {
    		String permName = permList.get(i);    		
//    		System.out.println("get permList[" + i + "] = " + permName);    		
    		if (!permName.contains("_"))	// avoid NG
    			continue;
    		
    		String projectName = permName.substring(0, permName.lastIndexOf('_'));
    		String roleName = permName.substring(permName.lastIndexOf('_')+1, permName.length());
    		
//    		IPermission permission = AccountFactory.createPermission(permName, projectName, roleName);
    		IPermission permission = new Permission(permName, projectName, roleName);
    		IRole role = new Role(permName, roleName);

    		role.addPermission(permission);
    		account.addRole(role);
    		
    		account.addPermission(permission);	// skip RBAC, for GAE use
    	}	
	}
	
	// replace: public IPermission getPermission(String project, String role)
	pointcut getPermissionByProjectAndRolePC(String project, String role)
	: execution(IPermission AccountMapper.getPermission(String, String)) && args(project, role);
	
	IPermission around(String project, String role)
	: getPermissionByProjectAndRolePC(project, role) {
		System.out.println("replaced by AOP...getAccountListPC: " + thisJoinPoint);
//		AccountManagerTemp accountManagerTemp = new AccountManagerTemp();
		IPermission permission = new Permission(project+ "_" + role, project, role);
		return permission;
	}
	
	// replace: public IPermission getPermission( String role )
	pointcut getPermissionPC(String role)
	: execution(IPermission AccountMapper.getPermission(String)) && args( role);
	
	IPermission around(String role)
	: getPermissionPC(role) {
		System.out.println("replaced by AOP...getAccountListPC: " + thisJoinPoint);
//		AccountManagerTemp accountManagerTemp = new AccountManagerTemp();
//		return accountManagerTemp.getPermission(role);
		if (role == ScrumEnum.ADMINISTRATOR_PERMISSION || role == "system_createProject")
		{
			IPermission permission = new Permission(ScrumEnum.ADMINISTRATOR_PERMISSION, "system", "admin");
//			System.out.println("admin getPermission DIY!");
			return permission;			
		}
		
		Map<String, IPermission> m_permissionMap = new HashMap<String, IPermission>();
		IPermission perm = (IPermission) m_permissionMap.get(role);
		
		if (perm == null)
		{
//			System.out.println(">>>>> perm null!! role name = " + name);
			
			String permName = role;
			String projectName = permName.substring(0, permName.lastIndexOf('_'));
			String roleName = permName.substring(permName.lastIndexOf('_')+1, permName.length());
	
			IPermission permission = new Permission(role, projectName, roleName);
//			System.out.println("getPermission DIY!");
			return permission;			
		}		
		else
			return perm;
	}
	
	// replace: public void confirmAccount(String id, String password) throws LogonException
	pointcut confirmAccountPC(String id, String password)
	: execution(void AccountMapper.confirmAccount(String, String)) && args(id, password);
	
	void around(String id, String password) throws LogonException
	: confirmAccountPC(id, password) {
		System.out.println("replaced by AOP...getAccountListPC: " + thisJoinPoint);
		IAccount account = this.doGetAccountById(id);

		if (account == null) {
			throw new LogonException(false, false);
		}
		
		if (account.getEnable().equals("false")) {
			throw new LogonException(true, false);
		}

		String pwMD5 = ezScrumUtil.getMd5(password);
		if (!account.getPassword().equals(pwMD5)) {
			throw new LogonException(true, false);
		}
	}
	
	/*
	 * Others
	 */

	// replace: public IAccountManager getManager()
//	pointcut AFgetManagerPC() 
//	: call(IAccountManager ntut.csie.jcis.account.core.AccountFactory.getManager());
//	
//	IAccountManager around()
//	: AFgetManagerPC() {
//		System.out.println("replaced by AOP...AFgetManagerPC: " + thisJoinPoint);
//
//		AccountMapperJDO am = new AccountMapperJDO();		
//		return am.getManager();
//	}		
	
}