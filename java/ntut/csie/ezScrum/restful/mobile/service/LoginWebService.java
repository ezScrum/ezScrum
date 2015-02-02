package ntut.csie.ezScrum.restful.mobile.service;

import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.jcis.account.core.LogonException;

public class LoginWebService {
	private AccountObject Account = null;

	public LoginWebService(String username, String userpwd) throws LogonException {
		this.Account = getAccount(username, userpwd);
	}

	public AccountObject getAccount() {
		return this.Account;
	}

	private AccountObject getAccount(String id, String password) throws LogonException {
		String guest = "guest";
		// ezScrum v1.8
		AccountObject user = null;
		if (!id.equals(guest) && !password.equals(guest)) {
			user = new AccountMapper().confirmAccount(id, password);
		} else {
			user = null;
		}
		return user;
	}
}
