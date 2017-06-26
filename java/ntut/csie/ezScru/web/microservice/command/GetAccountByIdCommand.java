package ntut.csie.ezScru.web.microservice.command;

import java.io.IOException;

import org.codehaus.jettison.json.JSONException;

import ntut.csie.ezScru.web.microservice.AccountRESTClient;
import ntut.csie.ezScrum.web.dataObject.AccountObject;

public class GetAccountByIdCommand implements AccountRESTCommand{
	private AccountRESTClient accountService;
	private long id;
	
	public GetAccountByIdCommand(AccountRESTClient accountService, long id){
		this.accountService = accountService;
		this.id = id;
	}
	public AccountObject getAccountById() throws IOException{
		return accountService.getAccountById(id);
	}
	
	@Override
	public Object Execute() throws IOException {
		return getAccountById();
	}
}
