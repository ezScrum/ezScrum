package ntut.csie.ezScru.web.microservice.command;

import java.io.IOException;

import ntut.csie.ezScru.web.microservice.AccountRESTClient;
import ntut.csie.ezScrum.web.dataObject.AccountObject;

public class RemoveAssignRoleCommand implements AccountRESTCommand{
	private AccountRESTClient accountService;
	private long accountId;
	private long projectId;
	private String role;
	public RemoveAssignRoleCommand(AccountRESTClient accountService, long accountId, long projectId, String role){
		this.accountService = accountService;
		this.accountId = accountId;
		this.projectId = projectId;
		this.role = role;
	}
	public AccountObject removeAssignRole() throws IOException{
		return accountService.removeAssignRole(accountId, projectId, role);
	}
	@Override
	public Object Execute() throws IOException {
		return removeAssignRole();
	}

}
