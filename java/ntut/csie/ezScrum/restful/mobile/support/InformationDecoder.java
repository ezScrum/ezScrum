package ntut.csie.ezScrum.restful.mobile.support;

import java.io.IOException;
import java.net.URLDecoder;

import ch.ethz.ssh2.crypto.Base64;

public class InformationDecoder {
	private String mDecodeUsername;
	private String mDecodePwd;
	private String mDecodeProjectName;

	public void decode(String username, String password) throws IOException {
		byte[] userName = Base64.decode(username.toCharArray());
		byte[] Pwd = Base64.decode(password.toCharArray());
		setDecodeUsername(new String(userName));
		setDecodePwd(new String(Pwd));
	}

	public void decode(String encodeUsername, String encodePassword,
			String encodeProjectName) throws IOException {
		byte[] userName = Base64.decode(encodeUsername.toCharArray());
		byte[] pwd = Base64.decode(encodePassword.toCharArray());
		setDecodeUsername(new String(userName));
		setDecodePwd(new String(pwd));
		setDecodeProjectName(encodeProjectName);
	}

	public void decodeProjectName(String encodeProjectName) throws IOException {
		String projectName = URLDecoder.decode(encodeProjectName, "UTF-8");
		setDecodeProjectName(projectName);
	}

	private void setDecodeUsername(String decodeUsername) {
		this.mDecodeUsername = decodeUsername;
	}

	public String getDecodeUsername() {
		return mDecodeUsername;
	}

	private void setDecodePwd(String decodePwd) {
		this.mDecodePwd = decodePwd;
	}

	public String getDecodePwd() {
		return mDecodePwd;
	}

	private void setDecodeProjectName(String decodeUrl) {
		this.mDecodeProjectName = decodeUrl;
	}

	public String getDecodeProjectName() {
		return mDecodeProjectName;
	}
}
