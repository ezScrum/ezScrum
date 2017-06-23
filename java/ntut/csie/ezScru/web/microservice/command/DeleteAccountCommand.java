package ntut.csie.ezScru.web.microservice.command;

import java.io.IOException;

import ntut.csie.ezScru.web.microservice.AccountRESTClient;

public class DeleteAccountCommand implements AccountRESTCommand{
	private AccountRESTClient accountService;
	private long id;
	DeleteAccountCommand(AccountRESTClient accountService, long id){
		this.accountService = accountService;
		this.id = id;
	}
	public Boolean deleteAccount() throws IOException{
		return accountService.deleteAccount(id);
	}
	@Override
	public Object Execute() throws Exception {
		return deleteAccount();
	}

}
