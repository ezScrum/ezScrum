package ntut.csie.ezScru.web.microservice.command;

import java.io.IOException;

import ntut.csie.ezScru.web.microservice.AccountRESTClient;
import ntut.csie.ezScrum.web.dataInfo.AccountInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;

public class CreateAccountCommand implements ICommand {
	private AccountRESTClient accountService;
	private AccountInfo accountInfo;
	public CreateAccountCommand(AccountRESTClient accountService,AccountInfo accountInfo){
		this.accountService = accountService; 
		this.accountInfo = accountInfo;
	}
	public AccountObject addAccount(AccountInfo accountInfo) throws IOException {
		return accountService.createAccount(accountInfo);
	}
	@Override
	public Object Execute() throws IOException {
		return addAccount(accountInfo);
	}
}
