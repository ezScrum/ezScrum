package ntut.csie.ezScrum.restful.dataMigration.security;

import ntut.csie.ezScrum.dao.AccountDAO;
import ntut.csie.ezScrum.web.dataObject.AccountObject;

public class SecurityModule {
	public static boolean checkAccount(String username, String password) {
		AccountObject account = AccountDAO.getInstance().get(username);

		if (account == null) {
			return false;
		} else {
			String checkPassword = account.getPassword();
			if (password.equals(checkPassword)) {
				return true;
			}
		}
		return false;
	}
}
