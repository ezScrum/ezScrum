package ntut.csie.ezScru.web.microservice;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import ntut.csie.ezScrum.web.dataInfo.AccountInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.support.TranslateUtil;

public class CallAccountMicroservice {
	String baseURL = "http://localhost:8088";
	String token;
	public CallAccountMicroservice(){
		
	}
	public CallAccountMicroservice(String token){
		this.token = token;
	}
	public void CallService(){
		
	}
	public String Login(String username, String password) throws IOException{
		String requestURL = baseURL + "/login";
		String bodyJSON = "{\"username\":\"" + username + "\", \"password\":\""+ password + "\"}";
		URL url = new URL(requestURL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
		conn.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		wr.write(bodyJSON.getBytes("UTF-8"));
		wr.flush();
		wr.close();
		
		int responseCode = conn.getResponseCode();
		Map<String, List<String>> map = conn.getHeaderFields();

		if(responseCode == 200){
			this.token = conn.getHeaderField("Authorization");
			
			conn.disconnect();
			return token;
		} else {
			conn.disconnect();
			return "Fail";
		}
		
//		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//		String inputLine;
//		StringBuffer response = new StringBuffer();
//
//		while ((inputLine = in.readLine()) != null) {
//			response.append(inputLine);
//		}
//		in.close();

		//print result
		
//		AccRestClient client = new AccRestClient(token);
//		client.getAcountList();
					
//		return response.toString();
	}
	public String getToken(){
		return token;
	}
	
	public void setToken(String token){
		this.token = token;
	}

	public AccountObject getAccountById(long id) throws IOException, JSONException{
		String requestURL = baseURL + "/accounts/getUserById/"+id;
		URL url = new URL(requestURL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
		conn.setRequestProperty("Authorization", token);
		conn.setDoOutput(false);
		int responsecode = conn.getResponseCode();
		InputStream is = conn.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line = null;
		StringBuffer sb = new StringBuffer();
		while ((line = reader.readLine()) != null) {
		    sb.append(line);
		}
		is.close();
		String response = sb.toString();
		JSONObject accountJSON = new JSONObject(response);
		AccountObject account = new AccountObject(id, accountJSON.get("username").toString());
		account.setEmail(accountJSON.get("email").toString());
		account.setEnable(Boolean.valueOf(accountJSON.get("enabled").toString()));
		account.setNickName(accountJSON.get("nickname").toString());
		account.setAdmin(Boolean.valueOf(accountJSON.get("systemrole").toString()));
		return account;
	}
	public String sendGetAccountByUsernamePassword(String username, String password) throws Exception{
		String requestURL = baseURL + "/accounts/getAccount";
		requestURL += "?username="+username+"&password="+password;
		URL url = new URL(requestURL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		//conn.setRequestMethod("GET");conn.setRequestProperty("username", username);
		//conn.setRequestProperty("password", password);
		
		conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
		conn.setRequestProperty("Authorization", token);
		conn.setDoOutput(false);
//		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
//		wr.flush();
//		wr.close();
		
		int responsecode = conn.getResponseCode();
		InputStream is = conn.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line = null;
		StringBuffer sb = new StringBuffer();
		while ((line = reader.readLine()) != null) {
		    sb.append(line);
		}
		is.close();
		String response = sb.toString();
		conn.disconnect();
//		String accountJSON = conn.getResponseMessage().toString();
		return response;
	}
	
	/**
	 * admin get all user from account microservice
	 * @throws IOException 
	 * @throws JSONException 
	 */
	public ArrayList<AccountObject> getAccounts() throws IOException, JSONException{
		String requestURL = baseURL + "/accounts/all";
		URL url = new URL(requestURL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
		conn.setRequestProperty("Authorization", token);
		conn.setDoOutput(false);
		
		int responsecode = conn.getResponseCode();
		
		if(responsecode != 200)
			throw new ConnectException("Connected fail");
		
		InputStream is = conn.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line = null;
		StringBuffer sb = new StringBuffer();
		while ((line = reader.readLine()) != null) {
		    sb.append(line);
		}
		is.close();
		String response = sb.toString();
		conn.disconnect();
		ArrayList<AccountObject> result = new ArrayList();
		JSONObject accountsJSON = new JSONObject(response);
		JSONArray accountsJSONArray = accountsJSON.getJSONArray("accounts");
		for(int i = 0; i < accountsJSONArray.length(); i++){
			JSONObject explrObject = accountsJSONArray.getJSONObject(i);
			AccountObject account = new AccountObject(Long.valueOf(explrObject.get("id").toString()), explrObject.get("username").toString());
			account.setEmail(explrObject.get("email").toString());
			account.setNickName(explrObject.get("username").toString());
			account.setAdmin(Boolean.valueOf(explrObject.get("systemrole").toString()));
			account.setEnable(Boolean.valueOf(explrObject.get("enabled").toString()));
			account.setNickName(explrObject.get("nickname").toString());
			result.add(account);
		}
		return result;
	}
	
	public boolean checkUsernameIsExist(String username) throws IOException{
		String requestURL = baseURL + "/accounts/check?username="+username;
		URL url = new URL(requestURL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
		conn.setRequestProperty("Authorization", token);
		conn.setDoOutput(false);
		int responsecode = conn.getResponseCode();
		
		if(responsecode != 200)
			throw new ConnectException("Connected fail");
		
		InputStream is = conn.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuffer sb = new StringBuffer();
		sb.append(reader.readLine());
		is.close();
		String response = sb.toString();
		conn.disconnect();
		
		return Boolean.valueOf(response);
	}
	
	public String createAccount(AccountInfo accountInfo) throws JSONException, IOException{
		JSONObject json = new JSONObject();
		json.put("username", accountInfo.username)
			.put("password", accountInfo.password)
			.put("nickname", accountInfo.nickName)
			.put("email", accountInfo.email)
			.put("enabled", accountInfo.enable);
		
		String requestURL = baseURL + "/accounts/add";
		URL url = new URL(requestURL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
		conn.setRequestProperty("Authorization", token);
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		wr.write(json.toString().getBytes("UTF-8"));
		int responsecode = conn.getResponseCode();
		wr.flush();
		wr.close();
		
		if(responsecode != 200)
			return "Fail";
		
		InputStream is = conn.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuffer sb = new StringBuffer();
		sb.append(reader.readLine());
		conn.disconnect();
		return sb.toString();
	}
	
	public String updateAccount(AccountInfo accountInfo) throws JSONException, IOException{
		JSONObject json = new JSONObject();
		json.put("username", accountInfo.username)
//			.put("password", accountInfo.password)
			.put("nickname", accountInfo.nickName)
			.put("email", accountInfo.email)
			.put("enabled", accountInfo.enable);
		String requestURL = baseURL + "/accounts/update/"+accountInfo.id;
		
		if(accountInfo.password == null || accountInfo.password.isEmpty() || accountInfo.password == "")
			json.put("password", "");
		else 
			json.put("password", accountInfo.password);

		URL url = new URL(requestURL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
		conn.setRequestProperty("Authorization", token);
		conn.setRequestMethod("PUT");
		conn.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		wr.write(json.toString().getBytes("UTF-8"));
		wr.flush();
		wr.close();
		
		int responsecode = conn.getResponseCode();
		
		if(responsecode != 200)
			return "Fail";
		
		InputStream is = conn.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line = null;
		StringBuffer sb = new StringBuffer();
		while ((line = reader.readLine()) != null) {
		    sb.append(line);
		}
		is.close();
		return sb.toString();
	}
	
	public String updateAccountSystemRole(long id, boolean systemrole) throws IOException{
		String requestURL = baseURL + "/accounts/updateSystemRole/"+id;
		JSONObject json = new JSONObject();
		StringBuffer sb = new StringBuffer();
		try {
			json.put("systemrole", systemrole);
			URL url = new URL(requestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			conn.setRequestProperty("Authorization", token);
			conn.setRequestMethod("PUT");
			conn.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.write(json.toString().getBytes("UTF-8"));
			int responsecode = conn.getResponseCode();
			wr.flush();
			wr.close();
			if(responsecode != 200)
				return "Fail";
			
			InputStream is = conn.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line = null;
			
			while ((line = reader.readLine()) != null) {
			    sb.append(line);
			}
			is.close();
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	public boolean deleteAccount(long id){
		String requestURL = baseURL + "/accounts/delete/"+id;
		URL url;
		try {
			url = new URL(requestURL);
			HttpURLConnection conn;
			try {
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
				conn.setRequestProperty("Authorization", token);
				conn.setDoOutput(true);
				int responsecode = conn.getResponseCode();
				
				if(responsecode != 200)
					return false;
				InputStream is = conn.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				StringBuffer sb = new StringBuffer();
				sb.append(reader.readLine());
				is.close();
				String response = sb.toString();
				conn.disconnect();
				return Boolean.valueOf(response);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public String getAccountXML(AccountObject account) {
		ArrayList<AccountObject> accounts = new ArrayList<AccountObject>();
		accounts.add(account);
		return getXmlstring(accounts);
	}
	
	// ezScrum v1.8
	private String getXmlstring(ArrayList<AccountObject> accounts) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<Accounts>");
		for (AccountObject account : accounts) {
			if (account == null) {
				stringBuilder.append("Account not found.");
			} else {
				stringBuilder.append("<AccountInfo>");
				stringBuilder.append("<ID>").append(account.getId()).append("</ID>");
				stringBuilder.append("<Account>").append(account.getUsername()).append("</Account>");
				stringBuilder.append("<Name>").append(account.getNickName()).append("</Name>");
				stringBuilder.append("<Mail>").append(account.getEmail()).append("</Mail>");
				stringBuilder.append("<Roles>").append(TranslateUtil.getRolesString(account.getRoles())).append("</Roles>");
				stringBuilder.append("<Enable>").append(account.getEnable()).append("</Enable>");
				stringBuilder.append("</AccountInfo>");
			}
		}
		stringBuilder.append("</Accounts>");
			
		return stringBuilder.toString();
	}
	
}
