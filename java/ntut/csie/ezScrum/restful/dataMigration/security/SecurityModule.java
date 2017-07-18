package ntut.csie.ezScrum.restful.dataMigration.security;

import java.util.ArrayList;

import ntut.csie.ezScrum.dao.AccountDAO;
import ntut.csie.ezScrum.web.dataObject.AccountObject;

public class SecurityModule {
	public static final String USERNAME_HEADER = "username";
	public static final String PASSWORD_HEADER = "password";
	public static final String ADMIN_MD5_USERNAME = "21232f297a57a5a743894a0e4a801fc3";
	public static final String ADMIN_MD5_PASSWORD = "21232f297a57a5a743894a0e4a801fc3";
	
	public static boolean isAccountValid(String md5EncodingUsername, String md5EncodingPassword) {
		ArrayList<AccountObject> allAcocunts = AccountDAO.getInstance().getAllAccounts();
		for (AccountObject account : allAcocunts) {
			String originUsername = account.getUsername();
			String md5EncodingAccountUsername = AccountDAO.getMd5(originUsername);
			String md5EncodingAccountPassword = account.getPassword();
			if (md5EncodingUsername != null 
					&&  md5EncodingPassword != null 
					&& (md5EncodingUsername.equals(md5EncodingAccountUsername)) 
					&& (md5EncodingPassword.equals(md5EncodingAccountPassword))
					&& account.isAdmin()) {
				return true;
			}
		}
		return false;
	}
}
