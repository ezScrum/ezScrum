package ntut.csie.ezScrum.restful.mobile.service;

import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.jcis.account.core.LogonException;

public class LoginWebService {
//	private IAccount Account = null;
	private AccountObject Account = null;

	public LoginWebService(String username, String userpwd) throws LogonException {
		this.Account = getAccount(username, userpwd);
	}

	public AccountObject getAccount() {
		return this.Account;
	}

	private AccountObject getAccount(String id, String password) throws LogonException {
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
		AccountObject user = null;
		if (!id.equals(guest) && !password.equals(guest)) {
			user = new AccountMapper().confirmAccount(id, password);
		} else {
			user = new AccountObject(id, id, null, null, null);
		}
		return user;
	}
}
