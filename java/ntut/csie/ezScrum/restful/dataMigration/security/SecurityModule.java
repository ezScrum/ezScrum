package ntut.csie.ezScrum.restful.dataMigration.security;

import ntut.csie.ezScrum.web.dataObject.AccountObject;

public class SecurityModule {
	public static final String USERNAME_HEADER = "username";
	public static final String PASSWORD_HEADER = "password";
	public static final String ADMIN_MD5_USERNAME = "21232f297a57a5a743894a0e4a801fc3";
	public static final String ADMIN_MD5_PASSWORD = "21232f297a57a5a743894a0e4a801fc3";
	
	public static boolean isAccountValid(String md5EncodingUsername, String md5EncodingPassword) {
		final String ADMIN_USERNAME = "admin";
		final String ADMIN_MD5_ENCODING_USERNAME = "21232f297a57a5a743894a0e4a801fc3";
		AccountObject admin = AccountObject.get(ADMIN_USERNAME);
		String md5AdminEncodingPassword = admin.getPassword();
		if ((md5EncodingUsername.equals(ADMIN_MD5_ENCODING_USERNAME)) && (md5EncodingPassword.equals(md5AdminEncodingPassword))) {
			return true;
		}
		return false;
	}
}
