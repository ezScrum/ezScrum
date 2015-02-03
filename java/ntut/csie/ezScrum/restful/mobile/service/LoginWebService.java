package ntut.csie.ezScrum.restful.mobile.service;

import ntut.csie.ezScrum.dao.AccountDAO;
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
			// 從資料庫拿出來的密碼會是加密過的，所以這邊用長度判斷是否已經經過 MD5 加密，如果長度不符合 32 就用 MD5 hash 過一次
			if (password.length() != 32) {
				password = AccountDAO.getInstance().getMd5(password);
			}
			user = new AccountMapper().confirmAccount(id, password);
		} else {
			user = null;
		}
		return user;
	}
}
