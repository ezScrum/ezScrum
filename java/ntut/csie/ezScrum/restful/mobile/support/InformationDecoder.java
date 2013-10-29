package ntut.csie.ezScrum.restful.mobile.support;

import java.io.IOException;
import java.net.URLDecoder;

import ch.ethz.ssh2.crypto.Base64;

public class InformationDecoder {
	private String decodeUserName;
	private String decodePwd;
	private String decodeProjectID;
	public void decode( String username, String password ) throws IOException{
		byte[] userName = Base64.decode( username.toCharArray() );
		byte[] Pwd = Base64.decode( password.toCharArray() );
		setDecodeUserName( new String(userName) );
		setDecodePwd( new String(Pwd) );
	}
	public void decode( String encodeUsername, String encodePassword, String encodeProjectID ) throws IOException{
		byte[] userName = Base64.decode( encodeUsername.toCharArray() );
		byte[] pwd = Base64.decode( encodePassword.toCharArray() );
		setDecodeUserName( new String(userName) );
		setDecodePwd( new String(pwd) );
		setDecodeProjectID( encodeProjectID );
	}
	public void decodeProjectID( String encodeProjectID ) throws IOException{
		String projectID = URLDecoder.decode( encodeProjectID, "UTF-8");
		setDecodeProjectID( projectID );
	}
	private void setDecodeUserName( String decodeUserName ) {
		this.decodeUserName = decodeUserName;
	}
	public String getDecodeUserName() {
		return decodeUserName;
	}
	private void setDecodePwd( String decodePwd ) {
		this.decodePwd = decodePwd;
	}
	public String getDecodePwd() {
		return decodePwd;
	}
	private void setDecodeProjectID( String decodeUrl ) {
		this.decodeProjectID = decodeUrl;
	}
	public String getDecodeProjectID() {
		return decodeProjectID;
	}
}
