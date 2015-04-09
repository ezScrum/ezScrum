package ntut.csie.ezScrum.restful.mobile.support;

import java.io.IOException;
import java.net.URLDecoder;

import ch.ethz.ssh2.crypto.Base64;

public class InformationDecoder {
	private String decodeUsername;
	private String decodePwd;
	private String decodeProjectName;

	public void decode(String username, String password) throws IOException {
		byte[] userName = Base64.decode(username.toCharArray());
		byte[] Pwd = Base64.decode(password.toCharArray());
		setDecodeUserName(new String(userName));
		setDecodePwd(new String(Pwd));
	}

	public void decode(String encodeUsername, String encodePassword,
			String encodeProjectID) throws IOException {
		byte[] userName = Base64.decode(encodeUsername.toCharArray());
		byte[] pwd = Base64.decode(encodePassword.toCharArray());
		setDecodeUserName(new String(userName));
		setDecodePwd(new String(pwd));
		setDecodeProjectName(encodeProjectID);
	}

	public void decodeProjectName(String encodeProjectName) throws IOException {
		String projectName = URLDecoder.decode(encodeProjectName, "UTF-8");
		setDecodeProjectName(projectName);
	}

	private void setDecodeUserName(String decodeUsername) {
		this.decodeUsername = decodeUsername;
	}

	public String getDecodeUserName() {
		return decodeUsername;
	}

	private void setDecodePwd(String decodePwd) {
		this.decodePwd = decodePwd;
	}

	public String getDecodePwd() {
		return decodePwd;
	}

	private void setDecodeProjectName(String decodeUrl) {
		this.decodeProjectName = decodeUrl;
	}

	public String getDecodeProjectName() {
		return decodeProjectName;
	}
}
