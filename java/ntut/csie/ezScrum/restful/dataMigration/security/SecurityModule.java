package ntut.csie.ezScrum.restful.dataMigration.security;

import ntut.csie.ezScrum.web.dataObject.AccountObject;

public class SecurityModule {
	public static boolean isAccountValid(String username, String password) {
		AccountObject account = AccountObject.get(username);
		final String ADMIN_USERNAME = "admin";
		AccountObject admin = AccountObject.get(ADMIN_USERNAME);
		if (account == null) {
			return false;
		} else {
			String adminPassword = admin.getPassword();
			if ((username.equals(ADMIN_USERNAME))&&(password.equals(adminPassword))) {
				return true;
			}
		}
		return false;
	}
}
