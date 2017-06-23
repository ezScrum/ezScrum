package ntut.csie.ezScru.web.microservice;

import ntut.csie.ezScrum.web.dataInfo.AccountInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;

public interface IAccountController {
//	public AccountObject Login(String username, String password);
	public AccountObject createAccount(AccountInfo user);
	public AccountObject updateAccount(AccountInfo user);
	public boolean deleteAccount(long id);
	public String getAssignedProject(long accountId);
	public AccountObject addAssignedRole(long accountId, long projectId, String scrumRole);
	public AccountObject removeAssignRole(long accountId, long projectId, String role);
	public String getAccountListXML() ;
	public String  validateUsername(String username);
	public AccountObject confirmAccount(String id, String password) throws Exception;
	public AccountObject getAccountById(long id); 
	public String getAccountXML(AccountObject account);
	public String getManagementView(AccountObject account) ;
}
