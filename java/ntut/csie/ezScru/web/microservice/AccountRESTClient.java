package ntut.csie.ezScru.web.microservice;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.dataInfo.AccountInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.ProjectRole;
import ntut.csie.ezScrum.web.databaseEnum.RoleEnum;
import ntut.csie.ezScrum.web.logic.ProjectLogic;
import ntut.csie.ezScrum.web.support.TranslateUtil;
import ntut.csie.ezScru.web.microservice.ServiceConfiguration;

public class AccountRESTClient{
	
	String baseURL;
	String token;
	private final String SYSTEM = "system";
	
	public AccountRESTClient(){
		try{
			ServiceConfiguration sc = new ServiceConfiguration("AccountManagement");
			baseURL = "http://" + sc.getServiceUrl() + ":" + sc.getServicePort();
		}catch(Exception e){
			baseURL = "http://localhost:8088";
		}
	}
	public AccountRESTClient(String token){
		try{
			ServiceConfiguration sc = new ServiceConfiguration("AccountManagement");
			baseURL = "http://" + sc.getServiceUrl() + ":" + sc.getServicePort();
		}catch(Exception e){
			baseURL = "http://localhost:8088";
		}
		this.token = token;
	}
	public String getToken(){
		return token;
	}
	public void setToken(String token){
		this.token = token;
	}
	
	public AccountObject createAccount(AccountInfo accountInfo) throws IOException {
		JSONObject json = new JSONObject();
		try {
			json.put("username", accountInfo.username)
				.put("password", accountInfo.password)
				.put("nickname", accountInfo.nickName)
				.put("email", accountInfo.email)
				.put("enabled", accountInfo.enable);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IOException();
		}
		String requestURL = baseURL + "/accounts/add";
		URL url;
		HttpURLConnection conn;
		int responsecode = 0;
		String responseId = "";
		AccountObject account = null;
		url = new URL(requestURL);
		
		conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
		conn.setRequestProperty("Authorization", token);
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		wr.write(json.toString().getBytes("UTF-8"));
		wr.flush();
		wr.close();
		responsecode = conn.getResponseCode();
		
		if(responsecode != 200){
//			return accountFromHelper;
			throw new IOException();
		}
		

		InputStream is = conn.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuffer sb = new StringBuffer();
		sb.append(reader.readLine());
		responseId = sb.toString();
		account = new AccountObject(Long.valueOf(responseId), accountInfo.username);
		account.setPassword(accountInfo.password);
		account.setAdmin(false);
		account.setEmail(accountInfo.email);
		account.setNickName(accountInfo.nickName);
		account.setEnable(accountInfo.enable);

		conn.disconnect();
		return account;
	}

	
	public AccountObject updateAccount(AccountInfo accountInfo) throws IOException {
		JSONObject json = new JSONObject();
		try {
			json.put("username", accountInfo.username)
//		.put("password", accountInfo.password)
				.put("nickname", accountInfo.nickName)
				.put("email", accountInfo.email)
				.put("enabled", accountInfo.enable);
			if(accountInfo.password == null || accountInfo.password.isEmpty() || accountInfo.password == "")
				json.put("password", "");
			else 
				json.put("password", accountInfo.password);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IOException();
		}
		
		String requestURL = baseURL + "/accounts/update/"+accountInfo.id;
		String response = "";
		HttpURLConnection conn;
		URL url;
		int responsecode = 0;
		AccountObject account = null;
		url = new URL(requestURL);
		
		conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
		conn.setRequestProperty("Authorization", token);
		conn.setRequestMethod("PUT");
		conn.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		wr.write(json.toString().getBytes("UTF-8"));
		wr.flush();
		wr.close();
		responsecode = conn.getResponseCode();
		if(responsecode != 200){
			throw new ConnectException("Connected fail");
		}
		InputStream is = conn.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line = null;
		StringBuffer sb = new StringBuffer();
		while ((line = reader.readLine()) != null) {
		    sb.append(line);
		}
		is.close();
		response = sb.toString();
		try {
			account = translateToAccount(response);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return account;
	}

	
	public boolean deleteAccount(long id) throws IOException {
		String requestURL = baseURL + "/accounts/delete/"+id;
		URL url;
		url = new URL(requestURL);
		HttpURLConnection conn;
		conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
		conn.setRequestProperty("Authorization", token);
		conn.setDoOutput(true);
		int responsecode = conn.getResponseCode();
		
		if(responsecode != 200){
			throw new ConnectException("Connected fail");
		}
		InputStream is = conn.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuffer sb = new StringBuffer();
		sb.append(reader.readLine());
		is.close();
		String response = sb.toString();
		conn.disconnect();
		return Boolean.valueOf(response);
	}

	
	public String getAssignedProject(long accountId) throws IOException {
		AccountObject account = getAccountById(accountId);
		HashMap<String, ProjectRole> rolesMap = account.getRoles();
		List<String> assignedProject = new ArrayList<String>();
		StringBuilder assignRoleInfo = new StringBuilder();
		
		// 取得帳號的Assign資訊
		assignRoleInfo.append("<AssignRoleInfo>");
		assignRoleInfo.append("<AccountInfo>");
		// Account Info
		assignRoleInfo.append("<ID>").append(account.getId()).append("</ID>");
		assignRoleInfo.append("<Account>").append(account.getUsername()).append("</Account>");
		assignRoleInfo.append("<Name>").append(account.getNickName()).append("</Name>");
		// Assign Roles
		assignRoleInfo.append("<Roles>");
		for (Entry<String, ProjectRole> entry : rolesMap.entrySet()) {
			ScrumRole permission = entry.getValue().getScrumRole();
			ProjectObject project = entry.getValue().getProject();
			String resource = permission.getProjectName();
			String operation = permission.getRoleName();
			assignRoleInfo.append("<Assigned>")
			  			  .append("<ResourceId>").append(project.getId()).append("</ResourceId>")
						  .append("<Resource>").append(resource).append("</Resource>")
						  .append("<Operation>").append(operation).append("</Operation>")
						  .append("</Assigned>");
			assignedProject.add(resource);	// 記錄此 project 為 assigned	
		}
		assignRoleInfo.append("</Roles>");
		
		// UnAssign Roles
		ProjectLogic projectLogic = new ProjectLogic();
		ArrayList<ProjectObject> projects = projectLogic.getProjects();
		for (ProjectObject project : projects) {
			String resource = project.getName();
			// 如果project沒有被assigned權限，則代表為unassigned的project
			if (!assignedProject.contains(resource)) {
				assignRoleInfo.append("<Unassigned>")
				  			  .append("<ResourceId>").append(project.getId()).append("</ResourceId>")
							  .append("<Resource>").append(resource).append("</Resource>")
							  .append("</Unassigned>");
			}
		}
		// 判斷是否為administrator
		if (!assignedProject.contains(this.SYSTEM)) 
			assignRoleInfo.append("<Unassigned><ResourceId>0</ResourceId><Resource>")
						  .append(this.SYSTEM)
						  .append("</Resource></Unassigned>");
		
		assignRoleInfo.append("</AccountInfo>");
		assignRoleInfo.append("</AssignRoleInfo>");
		
		return assignRoleInfo.toString();
	}

	public AccountObject addAssignedRole(long accountId, long projectId, String scrumRole) throws IOException {
		AccountObject account = null;
		
		if(scrumRole.equals("admin")){
			String responseString;
			responseString = updateAccountSystemRole(accountId, true);
			try {
				account = translateToAccount(responseString);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else{
			account = getAccountById(accountId);
			account.joinProjectWithScrumRole(projectId, RoleEnum.valueOf(scrumRole));
		}
		return account;
	}

	
	public AccountObject removeAssignRole(long accountId, long projectId, String role) throws IOException {
		AccountObject account = null;
		if (role.equals("admin")) {
			String responseString = updateAccountSystemRole(accountId, false);
			try {
				account = translateToAccount(responseString);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else{
			account = getAccountById(accountId);
			account.deleteProjectRole(projectId, RoleEnum.valueOf(role));
		}
		return account;
	}

	
	public String getAccountListXML() throws IOException {
		ArrayList<AccountObject> accounts;
		accounts = getAccounts();
		return getXmlstring(accounts);
	}

	
	public String validateUsername(String inputUsername) throws IOException {
		Pattern pattern = Pattern.compile("[0-9a-zA-Z_]*");
		Matcher matcher = pattern.matcher(inputUsername);
		boolean doesMatch = matcher.matches();
		
		boolean validateUsername;
		validateUsername = checkUsernameIsExist(inputUsername);
		if(doesMatch && !validateUsername && !inputUsername.isEmpty())
			return "true";
		else 
			return "false";
	}

	
	public AccountObject confirmAccount(String username, String password) throws IOException {
		AccountObject theAccount = null;
		String token = this.Login(username, password);
		if(token == "Fail")
			throw new IOException();
		this.setToken(token);
		String account = "";
		try {
			account = this.getAccountByUsernamePassword(username, password);
			JSONObject accountJSON = new JSONObject(account);
			boolean checkEnabled = Boolean.valueOf(accountJSON.getString("enabled"));
			if(checkEnabled == false){
				throw new IOException();
			}
			theAccount = new AccountObject(Long.valueOf(accountJSON.getString("id")), accountJSON.getString("username"));
			theAccount.setEmail(accountJSON.getString("email"));
			theAccount.setEnable(Boolean.valueOf(accountJSON.getString("enabled")));
			theAccount.setNickName(accountJSON.getString("nickname"));	
			theAccount.setAdmin(Boolean.valueOf(accountJSON.getString("systemrole")));
			theAccount.setToken(token);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
//			throw new JSONException("account data incorrect");
		}
		return theAccount;
	}

	
	public AccountObject getAccountById(long id) throws IOException {
		String requestURL = baseURL + "/accounts/getUserById/"+id;
		URL url;
		AccountObject account = null;
		url = new URL(requestURL);
		HttpURLConnection conn;
		conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
		conn.setRequestProperty("Authorization", token);
		conn.setDoOutput(false);
		int responsecode = conn.getResponseCode();
		if(responsecode != 200){
			throw new ConnectException("Connected fail");
		}
		InputStream is = conn.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line = null;
		StringBuffer sb = new StringBuffer();
		while ((line = reader.readLine()) != null) {
		    sb.append(line);
		}
		is.close();
		String response = sb.toString();
		try {
			account = translateToAccount(response);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return account;
	}
	
	private AccountObject translateToAccount(String account) throws JSONException{
		JSONObject accountJSON;
		AccountObject newAccount = null;
		accountJSON = new JSONObject(account);
		newAccount = new AccountObject(Long.valueOf(accountJSON.getString("id")), accountJSON.getString("username"));
		newAccount.setEmail(accountJSON.getString("email"));
		newAccount.setEnable(Boolean.valueOf(accountJSON.getString("enabled")));
		newAccount.setNickName(accountJSON.getString("nickname"));	
		newAccount.setAdmin(Boolean.valueOf(accountJSON.getString("systemrole")));
		
		return newAccount;
	}
	
	public ArrayList<AccountObject> getAccounts() throws IOException {
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
		JSONObject accountsJSON;
		try {
			accountsJSON = new JSONObject(response);
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
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
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
				throw new IOException();
			
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
	public String getAccountByUsernamePassword(String username, String password) throws IOException{
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
	public String getAccountXML(AccountObject account) {
		ArrayList<AccountObject> accounts = new ArrayList<AccountObject>();
		accounts.add(account);
		return getXmlstring(accounts);
	}
	public boolean checkConnect() throws IOException{
		String requestURL = baseURL + "/accounts/checkConnect";
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
	
	public String getNotificationSubscriptStatus(Long account_id, String firebaseToken) throws IOException,JSONException{
		String requestURL = baseURL + "/accounts/getNotificationSubscriptStatus";

		JSONObject json = new JSONObject();
		json.put("account_id", account_id.toString());
		json.put("firebaseToken", firebaseToken);
		
		URL url = new URL(requestURL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Authorization", token);
		conn.setDoOutput(true);
		
		OutputStream wr = conn.getOutputStream();
        wr.write(json.toString().getBytes("UTF-8"));
		int responsecode = conn.getResponseCode();
		
		if(responsecode != 200)
			throw new ConnectException("Connected fail");
		InputStream is = conn.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuffer sb = new StringBuffer();
		sb.append(reader.readLine());
		is.close();
		String response = sb.toString();
		return response;
	}
	
	public String subscribeNotification(Long account_id, String firebaseToken) throws IOException,JSONException{
		String requestURL = baseURL + "/accounts/subscribeNotification";
		
		JSONObject json = new JSONObject();
		json.put("account_id", account_id.toString());
		json.put("firebaseToken", firebaseToken);
		
		URL url = new URL(requestURL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Authorization", token);
		conn.setDoOutput(true);
		
		OutputStream wr = conn.getOutputStream();
        wr.write(json.toString().getBytes("UTF-8"));
		int responsecode = conn.getResponseCode();
		
		if(responsecode != 200)
			throw new ConnectException("Connected fail");
		InputStream is = conn.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuffer sb = new StringBuffer();
		sb.append(reader.readLine());
		is.close();
		String response = sb.toString();
		return response;
	}
	
	public String cancelSubscribeNotification(Long account_id, String firebaseToken) throws IOException,JSONException{
		String requestURL = baseURL + "/accounts/cancelSubscribeNotification";
		
		JSONObject json = new JSONObject();
		json.put("account_id", account_id.toString());
		json.put("firebaseToken", firebaseToken);
		
		URL url = new URL(requestURL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Authorization", token);
		conn.setDoOutput(true);
		
		OutputStream wr = conn.getOutputStream();
        wr.write(json.toString().getBytes("UTF-8"));
		int responsecode = conn.getResponseCode();
		
		if(responsecode != 200)
			throw new ConnectException("Connected fail");
		InputStream is = conn.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuffer sb = new StringBuffer();
		sb.append(reader.readLine());
		is.close();
		String response = sb.toString();
		return response;
	}
	
	public String notifyServiceLogout(Long account_id, String firebaseToken) throws IOException,JSONException{
		String requestURL = baseURL + "/accounts/notifyServiceLogout";
		
		JSONObject json = new JSONObject();
		json.put("account_id", account_id.toString());
		json.put("firebaseToken", firebaseToken);
		
		URL url = new URL(requestURL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Authorization", token);
		conn.setDoOutput(true);
		
		OutputStream wr = conn.getOutputStream();
        wr.write(json.toString().getBytes("UTF-8"));
		int responsecode = conn.getResponseCode();
		
		if(responsecode != 200)
			throw new ConnectException("Connected fail");
		InputStream is = conn.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuffer sb = new StringBuffer();
		sb.append(reader.readLine());
		is.close();
		String response = sb.toString();
		return response;
	}
	
	public String sendNotification(ArrayList<Long> accounts_id, String title, String body, String eventSource) throws IOException,JSONException{
		String requestURL = baseURL + "/accounts/sendNotification";
		
		JSONArray array = new JSONArray(accounts_id);
		JSONObject json = new JSONObject();
		json.put("accounts_id", array.toString());
		json.put("title", title);
		json.put("body", body);
		json.put("eventSource", eventSource);
		
		URL url = new URL(requestURL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Authorization", token);
		conn.setDoOutput(true);
		
		OutputStream wr = conn.getOutputStream();
        wr.write(json.toString().getBytes("UTF-8"));
		int responsecode = conn.getResponseCode();
		
		if(responsecode != 200)
			throw new ConnectException("Connected fail");
		InputStream is = conn.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuffer sb = new StringBuffer();
		sb.append(reader.readLine());
		is.close();
		String response = sb.toString();
		return response;
	}
}
