package ntut.csie.ezScru.web.microservice.command;

import java.io.IOException;

import org.codehaus.jettison.json.JSONException;

import ntut.csie.ezScru.web.microservice.AccountRESTClient;
import ntut.csie.ezScrum.web.dataObject.AccountObject;

public class AddAssignedRoleCommand implements AccountRESTCommand{
	private AccountRESTClient accountService;
	private long accountId;
	private long projectId;
	private String scrumRole;
	public AddAssignedRoleCommand(AccountRESTClient accountService, long accountId, long projectId, String scrumRole){
		this.accountId = accountId;
		this.accountService = accountService;
		this.projectId = projectId;
		this.scrumRole = scrumRole;
	}
	
	public AccountObject addAssignedRole() throws IOException{
		return accountService.addAssignedRole(accountId, projectId, scrumRole);
	}
	
	@Override
	public Object Execute() throws IOException {
		return addAssignedRole();
	}

}
