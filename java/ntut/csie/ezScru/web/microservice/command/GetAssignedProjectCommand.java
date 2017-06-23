package ntut.csie.ezScru.web.microservice.command;

import java.io.IOException;

import org.codehaus.jettison.json.JSONException;

import ntut.csie.ezScru.web.microservice.AccountRESTClient;

public class GetAssignedProjectCommand implements AccountRESTCommand{
	private AccountRESTClient accountService;
	private long accountId;
	GetAssignedProjectCommand(AccountRESTClient accountService, long accountId){
		this.accountService = accountService;
		this.accountId = accountId;
	}
	
	public String getAssignedProject() throws IOException, JSONException{
		return accountService.getAssignedProject(accountId);
	}
	@Override
	public Object Execute() throws Exception {
		return getAssignedProject();
	}

}
