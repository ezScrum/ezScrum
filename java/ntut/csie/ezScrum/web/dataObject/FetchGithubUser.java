package ntut.csie.ezScrum.web.dataObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import com.google.gson.Gson;

//import demo.pojo.GithubPojo;


public class FetchGithubUser {

	private static final long serialVersionUID = 1L;
	private static final String clientID = "4180e319672bde2d0e7e";
	private static final String clientSecret = "f3a703cdd08cc97e18f53d0b4343acf0f509050d";
	private static final String redirectURI = "http://localhost:8080/ezScrum/";
	private static final String appName = "ezScrum";
	private String accessToken = null;
	private String userEmail = "";
	
	public String fetch(String code) throws IOException{
		accessToken = this.getToken(code);
		System.out.println(accessToken);
		userEmail = this.getEmail(accessToken);
		return this.userEmail;
	}

	private String getToken(String code) throws IOException{
		String token = "";	
		String outputString = "";
		try{
		URL url = new URL("https://github.com/login/oauth/access_token?client_id="
								+ clientID + "&redirect_uri=" + redirectURI
								+ "&client_secret=" + clientSecret + "&code=" +code);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setConnectTimeout(20000);
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null) {
			outputString= outputString + line;
		}
		
		System.out.println(outputString);
		if (outputString.indexOf("access_token") != -1) {
			token = outputString.substring(13,outputString.indexOf("&")); }
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return token;
	
	}
	private String getEmail(String token) throws IOException{

		String email = "";	
		try{ 

		String outputString = "";	
		URL url = new URL("https://api.github.com/user");
		HttpURLConnection myURLConnection = (HttpURLConnection) url.openConnection();
		myURLConnection.setRequestProperty("Authorization", "token "+ accessToken);
		myURLConnection.setRequestProperty("User-Agent", appName);
		myURLConnection.setRequestMethod("GET");
		myURLConnection.setUseCaches(false);
		myURLConnection.setDoInput(true);
		myURLConnection.setDoOutput(true);
		myURLConnection.setConnectTimeout(7000);

		outputString = "";
		String line;
		BufferedReader reader = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));
		while ((line = reader.readLine()) != null) {
			outputString = outputString + line;
		}

		if (outputString.indexOf("private_emails") != -1) {
			email = outputString.substring(13,outputString.indexOf("&"));
		}
		reader.close();
		
		System.out.println(outputString);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return email;
	}
}

