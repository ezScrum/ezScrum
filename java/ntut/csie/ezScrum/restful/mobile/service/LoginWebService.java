package ntut.csie.ezScrum.restful.mobile.service;

import ntut.csie.ezScrum.web.dataObject.UserObject;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.jcis.account.core.LogonException;

public class LoginWebService {
//	private IAccount Account = null;
	private UserObject Account = null;

	public LoginWebService(String username, String userpwd) throws LogonException {
		this.Account = getAccount(username, userpwd);
	}

	public UserObject getAccount() {
		return this.Account;
	}

	private UserObject getAccount(String id, String password) throws LogonException {
		String guest = "guest";
		//		IAccount theAccount = null;
		//		if( !id.equals(guest) && !Password.equals(guest) ){
		//			IAccountManager manager = AccountFactory.getManager();
		//			manager.confirmAccount(id, Password);
		//			theAccount = manager.getAccount(id);
		//		} else {
		//			theAccount = new Account(id);
		//		}
		//		
		//		return theAccount;
		// ezScrum v1.8
		UserObject user = null;
		if (!id.equals(guest) && !password.equals(guest)) {
			user = new AccountMapper().confirmAccount(id, password);
		} else {
			user = new UserObject(id, id, null, null, null);
		}
		return user;
	}
}
