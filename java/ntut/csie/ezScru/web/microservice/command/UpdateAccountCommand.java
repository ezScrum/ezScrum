package ntut.csie.ezScru.web.microservice.command;

import java.io.IOException;

import ntut.csie.ezScru.web.microservice.AccountRESTClient;
import ntut.csie.ezScrum.web.dataInfo.AccountInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;

public class UpdateAccountCommand implements AccountRESTCommand{
	private AccountRESTClient accountService;
	private AccountInfo accountInfo;
	public UpdateAccountCommand(AccountRESTClient accountService, AccountInfo accountInfo){
		this.accountService = accountService;
		this.accountInfo = accountInfo;
	}
	public AccountObject updateAccount() throws IOException{
		return accountService.updateAccount(accountInfo);
		
	}
	@Override
	public Object Execute() throws IOException {
		return updateAccount();
	}

}
