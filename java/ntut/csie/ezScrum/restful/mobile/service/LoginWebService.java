package ntut.csie.ezScrum.restful.mobile.service;

import ntut.csie.jcis.account.core.AccountFactory;
import ntut.csie.jcis.account.core.IAccount;
import ntut.csie.jcis.account.core.IAccountManager;
import ntut.csie.jcis.account.core.LogonException;
import ntut.csie.jcis.account.core.internal.Account;

public class LoginWebService {
	private IAccount Account = null;
	
	public LoginWebService(String username, String userpwd) throws LogonException {
		this.Account = getAccount(username, userpwd);
	}
	
	public IAccount getAccount() {
		return this.Account;
	}

	private IAccount getAccount(String id, String Password) throws LogonException {
		String guest = "guest";
		IAccount theAccount = null;
		if( !id.equals(guest) && !Password.equals(guest) ){
			IAccountManager manager = AccountFactory.getManager();
			manager.confirmAccount(id, Password);
			theAccount = manager.getAccount(id);
		} else {
			theAccount = new Account(id);
		}
		
		return theAccount;
	}
}
