package ntut.csie.ezScrum.restful.mobile.service;

import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.jcis.account.core.LogonException;

public class LoginWebService {
	private AccountObject mAccount = null;

	public LoginWebService(String username, String userpwd) throws LogonException {
		mAccount = getAccount(username, userpwd);
	}

	public AccountObject getAccount() {
		return mAccount;
	}

	private AccountObject getAccount(String username, String password) throws LogonException {
		String guest = "guest";
		// ezScrum v1.8
		AccountObject user = null;
		if (!username.equals(guest) && !password.equals(guest)) {
			user = new AccountMapper().confirmAccount(username, password);
		} else {
			user = null;
		}
		return user;
	}
}
