package ntut.csie.ezScrum.restful.mobile.support;

import java.io.IOException;
import java.net.URLDecoder;

import org.apache.commons.codec.binary.Base64;


public class InformationDecoder {
	private String mDecodeUsername;
	private String mDecodePwd;
	private String mDecodeProjectName;

	public void decode(String username, String password) throws IOException {
		byte[] userName = Base64.decodeBase64(username.getBytes());
		byte[] Pwd = Base64.decodeBase64(password.getBytes());
		setDecodeUsername(new String(userName));
		setDecodePwd(new String(Pwd));
	}

	public void decode(String encodeUsername, String encodePassword,
			String encodeProjectName) throws IOException {
		byte[] userName = Base64.decodeBase64(encodeUsername.getBytes());
		byte[] pwd = Base64.decodeBase64(encodePassword.getBytes());
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
