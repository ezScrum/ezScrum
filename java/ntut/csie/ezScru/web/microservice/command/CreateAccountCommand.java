package ntut.csie.ezScru.web.microservice.command;

import java.io.IOException;

import org.codehaus.jettison.json.JSONException;

import ntut.csie.ezScru.web.microservice.AccountRESTClient;
import ntut.csie.ezScrum.web.dataInfo.AccountInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;

public class CreateAccountCommand implements AccountRESTCommand {
	private AccountRESTClient accountService;
	private AccountInfo accountInfo;
	CreateAccountCommand(AccountRESTClient accountService,AccountInfo accountInfo){
		this.accountService = accountService; 
		this.accountInfo = accountInfo;
	}
	public AccountObject addAccount(AccountInfo accountInfo) throws JSONException, IOException {
		return accountService.createAccount(accountInfo);
	}
	@Override
	public Object Execute() throws Exception {
		return addAccount(accountInfo);
	}
	
	
	
}
