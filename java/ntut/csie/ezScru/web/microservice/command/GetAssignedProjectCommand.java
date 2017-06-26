package ntut.csie.ezScru.web.microservice.command;

import java.io.IOException;

import ntut.csie.ezScru.web.microservice.AccountRESTClient;

public class GetAssignedProjectCommand implements AccountRESTCommand{
	private AccountRESTClient accountService;
	private long accountId;
	public GetAssignedProjectCommand(AccountRESTClient accountService, long accountId){
		this.accountService = accountService;
		this.accountId = accountId;
	}
	
	public String getAssignedProject() throws IOException{
		return accountService.getAssignedProject(accountId);
	}
	@Override
	public Object Execute() throws IOException {
		return getAssignedProject();
	}

}
