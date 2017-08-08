package ntut.csie.ezScru.web.microservice.command;

import java.io.IOException;

import ntut.csie.ezScru.web.microservice.AccountRESTClient;
import ntut.csie.ezScrum.web.dataObject.AccountObject;

public class RemoveAssignRoleCommand implements ICommand{
	private AccountRESTClient accountService;
	private long accountId;
	private long projectId;
	public RemoveAssignRoleCommand(AccountRESTClient accountService, long accountId, long projectId){
		this.accountService = accountService;
		this.accountId = accountId;
		this.projectId = projectId;
	}
	public AccountObject removeAssignRole() throws IOException{
		return accountService.removeAssignRole(accountId, projectId);
	}
	@Override
	public Object Execute() throws IOException {
		return removeAssignRole();
	}

}
