package ntut.csie.ezScru.web.microservice.command;

import java.io.IOException;

import ntut.csie.ezScru.web.microservice.AccountRESTClient;
import ntut.csie.ezScrum.web.dataObject.AccountObject;

public class AddAssignedRoleCommand implements ICommand{
	private AccountRESTClient accountService;
	private long accountId;
	private long projectId;
	public AddAssignedRoleCommand(AccountRESTClient accountService, long accountId, long projectId){
		this.accountId = accountId;
		this.accountService = accountService;
		this.projectId = projectId;
	}
	
	public AccountObject addAssignedRole() throws IOException{
		return accountService.addSystemRole(accountId, projectId);
	}
	
	@Override
	public Object Execute() throws IOException {
		return addAssignedRole();
	}

}
