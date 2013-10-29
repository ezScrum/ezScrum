package ntut.csie.ezScrum.pic.internal;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.UserInformation;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.jcis.account.core.IAccount;
import ntut.csie.jcis.account.core.LogonException;
import ntut.csie.jcis.resource.core.IProject;

public class ProjectInfoCenter {
	private static ProjectInfoCenter m_pic = null;

	private ProjectInfoCenter() {

	}

	public static ProjectInfoCenter getInstance() {
		if (m_pic == null) {
			m_pic = new ProjectInfoCenter();
		}

		return m_pic;
	}

	/**
	 * 進行登入, 若帳號密碼為guest則不進行檢查
	 */
	public IUserSession login(String id, String password) throws LogonException {
		IAccount theAccount = null;
		AccountMapper accountMapper = new AccountMapper();
		theAccount = accountMapper.confirmAccount(id, password);
		IUserSession theUserSession = new UserSession(theAccount);
		return theUserSession;
	}
}
