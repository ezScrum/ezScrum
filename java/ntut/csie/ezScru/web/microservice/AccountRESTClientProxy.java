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
import ntut.csie.ezScrum.web.helper.AccountHelper;
import ntut.csie.ezScrum.web.logic.ProjectLogic;
import ntut.csie.ezScrum.web.support.TranslateUtil;
import ntut.csie.jcis.account.core.LogonException;

public class AccountRESTClientProxy implements IAccountController{
	String baseURL = "http://localhost:8088";
	String token;
	private AccountHelper accountHelper;
	private final String SYSTEM = "system";
	public AccountRESTClientProxy(){
		accountHelper = new AccountHelper();
	}
	public AccountRESTClientProxy(String token){
		accountHelper = new AccountHelper();
		this.token = token;
	}
	
	public String validateUsername(String inputUsername) {
		String responseFromHelper = accountHelper.validateUsername(inputUsername);
		Pattern pattern = Pattern.compile("[0-9a-zA-Z_]*");
		Matcher matcher = pattern.matcher(inputUsername);
		boolean doesMatch = matcher.matches();
		
		AccountRESTClientProxy accountService = new AccountRESTClientProxy(token);
		boolean validateUsername;
		try {
			validateUsername = accountService.checkUsernameIsExist(inputUsername);
			if(doesMatch && !validateUsername && !inputUsername.isEmpty())
				return "true";
			else 
				return "false";
		} catch (IOException e) {
			e.printStackTrace();
			return responseFromHelper;
		}
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

	public AccountObject createAccount(AccountInfo accountInfo) {
		AccountObject accountFromHelper = accountHelper.createAccount(accountInfo);
		JSONObject json = new JSONObject();
		try {
			json.put("username", accountInfo.username)
				.put("password", accountInfo.password)
				.put("nickname", accountInfo.nickName)
				.put("email", accountInfo.email)
				.put("enabled", accountInfo.enable);
		} catch (JSONException e) {
			e.printStackTrace();
			return accountFromHelper;
		}
		
		String requestURL = baseURL + "/accounts/add";
		URL url;
		HttpURLConnection conn;
		int responsecode = 0;
		String responseId = "";
		AccountObject account = null;
		try {
			url = new URL(requestURL);
		
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			conn.setRequestProperty("Authorization", token);
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.write(json.toString().getBytes("UTF-8"));
			responsecode = conn.getResponseCode();
			wr.flush();
			wr.close();
			
			
			
			if(responsecode != 200){
				return accountFromHelper;
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
		} catch (IOException e) {
			e.printStackTrace();
//			return accountFromHelper;
		}
		return account;
		
	}
	
	public AccountObject updateAccount(AccountInfo accountInfo){
		AccountObject accountFromHelper = accountHelper.updateAccount(accountInfo);
		JSONObject json = new JSONObject();
		try {
			json.put("username", accountInfo.username)
//			.put("password", accountInfo.password)
				.put("nickname", accountInfo.nickName)
				.put("email", accountInfo.email)
				.put("enabled", accountInfo.enable);
			
			
			if(accountInfo.password == null || accountInfo.password.isEmpty() || accountInfo.password == "")
				json.put("password", "");
			else 
				json.put("password", accountInfo.password);
		} catch (JSONException e) {
			e.printStackTrace();
			return accountFromHelper;
		}
		String requestURL = baseURL + "/accounts/update/"+accountInfo.id;
		String response = "";
		HttpURLConnection conn;
		URL url;
		int responsecode = 0;
		AccountObject account;
		try {
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
				return accountFromHelper;
			}
//			return "Fail";
		
			InputStream is = conn.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line = null;
			StringBuffer sb = new StringBuffer();
			while ((line = reader.readLine()) != null) {
			    sb.append(line);
			}
			is.close();
			response = sb.toString();
			account = translateToAccount(response);
		} catch (IOException | JSONException e) {
			e.printStackTrace();
			return accountFromHelper;
		}
		return account;
	}
	
	public boolean deleteAccount(long id){
		boolean deleteFromAccountHelper = accountHelper.deleteAccount(id);
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
					return deleteFromAccountHelper;
				InputStream is = conn.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				StringBuffer sb = new StringBuffer();
				sb.append(reader.readLine());
				is.close();
				String response = sb.toString();
				conn.disconnect();
				return Boolean.valueOf(response);
			} catch (IOException e) {
				e.printStackTrace();
				return deleteFromAccountHelper;
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return deleteFromAccountHelper;
		}
	}
	
	public AccountObject getAccountById(long id){
		AccountObject accountFromHelper = accountHelper.getAccountById(id);
		String requestURL = baseURL + "/accounts/getUserById/"+id;
		URL url;
		AccountObject account = null;
		try {
			url = new URL(requestURL);
			HttpURLConnection conn;
			try {
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
				conn.setRequestProperty("Authorization", token);
				conn.setDoOutput(false);
				int responsecode = conn.getResponseCode();
				if(responsecode != 200)
					return accountFromHelper;
				InputStream is = conn.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				String line = null;
				StringBuffer sb = new StringBuffer();
				while ((line = reader.readLine()) != null) {
				    sb.append(line);
				}
				is.close();
				String response = sb.toString();
				account = translateToAccount(response);
			} catch (IOException | JSONException e) {
				e.printStackTrace();
				return accountFromHelper;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return accountFromHelper;
		}
		return account;
	}
	
	@Override
	public String getAssignedProject(long accountId) {
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
	
	@Override
	public AccountObject addAssignedRole(long accountId, long projectId, String scrumRole) {
		AccountObject accountFromHelper = accountHelper.addAssignedRole(accountId, projectId, scrumRole);
		AccountObject account = null;
		
		if(scrumRole.equals("admin")){
			String responseString;
			try {
				responseString = updateAccountSystemRole(accountId, true);
				account = translateToAccount(responseString);
			} catch (IOException | JSONException e) {
				e.printStackTrace();
				return accountFromHelper;
			}
		} else{
			account = getAccountById(accountId);
			account.joinProjectWithScrumRole(projectId, RoleEnum.valueOf(scrumRole));
		}
		return account;
	}
	
	@Override
	public AccountObject removeAssignRole(long accountId, long projectId, String role) {
		AccountObject accountFromHelper = accountHelper.removeAssignRole(accountId, projectId, role);
		AccountObject account = null;
		if (role.equals("admin")) {
			try {
				String responseString = updateAccountSystemRole(accountId, false);
				account = translateToAccount(responseString);
			} catch (IOException | JSONException e) {
				e.printStackTrace();
				return accountFromHelper;
			}
		}else{
			account = getAccountById(accountId);
			account.deleteProjectRole(projectId, RoleEnum.valueOf(role));
		}
		
		return account;
	}
	
	public String getAccountXML(AccountObject account) {
		ArrayList<AccountObject> accounts = new ArrayList<AccountObject>();
		accounts.add(account);
		return getXmlstring(accounts);
	}
	
	@Override
	public String getAccountListXML() {
		// TODO Auto-generated method stub
		String responseFromHelper = accountHelper.getAccountListXML();
		ArrayList<AccountObject> accounts;
		try {
			accounts = getAccounts();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return responseFromHelper;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return responseFromHelper;
		}
		return getXmlstring(accounts);
	}
	@Override
	public String getManagementView(AccountObject account) {
		if(account.isAdmin() == true)
			return "Admin_ManagementView";
		else 
			return "User_ManagementView";
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
	
	public AccountObject confirmAccount(String username, String password) throws Exception{
		
		AccountObject theAccount = null;
		String token = this.Login(username, password);
		if(token == "Fail")
			throw new LogonException(false, false);
		this.setToken(token);
		String account = this.sendGetAccountByUsernamePassword(username, password);
		JSONObject accountJSON = new JSONObject(account);
		boolean checkEnabled = Boolean.valueOf(accountJSON.getString("enabled"));
		if(checkEnabled == false){
			throw new LogonException(false, false);
		}
		theAccount = new AccountObject(Long.valueOf(accountJSON.getString("id")), accountJSON.getString("username"));
		theAccount.setEmail(accountJSON.getString("email"));
		theAccount.setEnable(Boolean.valueOf(accountJSON.getString("enabled")));
		theAccount.setNickName(accountJSON.getString("nickname"));	
		theAccount.setAdmin(Boolean.valueOf(accountJSON.getString("systemrole")));
		theAccount.setToken(token);
		
		accountHelper.confirmAccount(username, password);
		
		return theAccount;
	}
}
