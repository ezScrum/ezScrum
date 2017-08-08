package ntut.csie.ezScru.web.microservice.command;

import java.io.IOException;

import org.codehaus.jettison.json.JSONException;

import ntut.csie.ezScru.web.microservice.AccountRESTClient;

public class GetAccountListXMLCommand implements ICommand{
	private AccountRESTClient accountService;

	public GetAccountListXMLCommand(AccountRESTClient accountService){
		this.accountService = accountService;
	}
	
	public String getAccountListXML() throws IOException {
		return accountService.getAccountListXML();
	}
	
	@Override
	public Object Execute() throws IOException {
		return getAccountListXML();
	}
}
