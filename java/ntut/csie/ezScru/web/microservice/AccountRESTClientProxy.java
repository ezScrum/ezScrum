package ntut.csie.ezScru.web.microservice;

import java.io.IOException;

import org.codehaus.jettison.json.JSONException;

import ntut.csie.ezScrum.web.dataInfo.AccountInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.helper.AccountHelper;

public class AccountRESTClientProxy implements IAccountController{
	
	String token;
	private AccountHelper accountHelper;
	private AccountRESTClient accountRESTClient;
	public AccountRESTClientProxy(){
		accountHelper = new AccountHelper();
		accountRESTClient = new AccountRESTClient();
	}
	public AccountRESTClientProxy(String token){
		accountHelper = new AccountHelper();
		accountRESTClient = new AccountRESTClient(token);
		this.token = token;
	}
	
	public String validateUsername(String inputUsername) {
		String responseFromHelper = accountHelper.validateUsername(inputUsername);
		String response;
		try {
			response = accountRESTClient.validateUsername(inputUsername);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return responseFromHelper;
		}
		return response;
	}
	
	public String getToken(){
		return token;
	}
	
	public void setToken(String token){
		this.token = token;
		accountRESTClient.setToken(token);
	}

	public AccountObject createAccount(AccountInfo accountInfo) {
		AccountObject accountFromHelper = accountHelper.createAccount(accountInfo);
		AccountObject account = null;
		try {
			account = accountRESTClient.createAccount(accountInfo);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return accountFromHelper;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return accountFromHelper;
		}
		return account;
		
	}
	
	public AccountObject updateAccount(AccountInfo accountInfo){
		AccountObject accountFromHelper = accountHelper.updateAccount(accountInfo);
		AccountObject account = null;
		try {
			account = accountRESTClient.updateAccount(accountInfo);
		} catch (JSONException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return accountFromHelper;
		}
		return account;
	}
	
	public boolean deleteAccount(long id){
		boolean deleteFromAccountHelper = accountHelper.deleteAccount(id);
		boolean checkDelete = false;
		try {
			checkDelete = accountRESTClient.deleteAccount(id);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return deleteFromAccountHelper;
		}
		return checkDelete;
	}
	
	public AccountObject getAccountById(long id){
		AccountObject accountFromHelper = accountHelper.getAccountById(id);
		AccountObject account = null;
		try {
			account = accountRESTClient.getAccountById(id);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return accountFromHelper;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return accountFromHelper;
		}
		return account;
	}
	
	@Override
	public String getAssignedProject(long accountId) {
		String assignedProjectFromHelper = accountHelper.getAssignedProject(accountId);
		String assingedProject;
		try {
			assingedProject = accountRESTClient.getAssignedProject(accountId);
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return assignedProjectFromHelper;
		}
		return assingedProject;
	}

	@Override
	public AccountObject addAssignedRole(long accountId, long projectId, String scrumRole) {
		AccountObject accountFromHelper = accountHelper.addAssignedRole(accountId, projectId, scrumRole);
		AccountObject account = null;
		try {
			account = accountRESTClient.addAssignedRole(accountId, projectId, scrumRole);
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return accountFromHelper;
		}
		return account;
	}
	
	@Override
	public AccountObject removeAssignRole(long accountId, long projectId, String role) {
		AccountObject accountFromHelper = accountHelper.removeAssignRole(accountId, projectId, role);
		AccountObject account = null;
		
		try {
			account = accountRESTClient.removeAssignRole(accountId, projectId, role);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return accountFromHelper;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return accountFromHelper;
		}
		return account;
	}
	
	public String getAccountXML(AccountObject account) {
		return accountHelper.getAccountXML(account);
	}
	
	@Override
	public String getAccountListXML() {
		String responseFromHelper = accountHelper.getAccountListXML();
		String response;
		try {
			response = accountRESTClient.getAccountListXML();
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return responseFromHelper;
		}
		return response;
	}
	
	public String getManagementView(AccountObject account) {
		if(account.isAdmin() == true)
			return "Admin_ManagementView";
		else 
			return "User_ManagementView";
	}
	
	public AccountObject confirmAccount(String username, String password) throws Exception{
		
		AccountObject theAccount = accountRESTClient.confirmAccount(username, password);
		
//		AccountObject accountFromHelper = accountHelper.confirmAccount(username, password);
		
		return theAccount;
	}
	public String getAccountByUsernamePassword(String username, String password) throws Exception{
		String response = accountRESTClient.getAccountByUsernamePassword(username, password);
		return response;
	}
}
