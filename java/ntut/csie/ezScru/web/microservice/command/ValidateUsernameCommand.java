package ntut.csie.ezScru.web.microservice.command;

import java.io.IOException;

import ntut.csie.ezScru.web.microservice.AccountRESTClient;

public class ValidateUsernameCommand implements ICommand{
	private AccountRESTClient accountService;
	private String inputUsername;
	public ValidateUsernameCommand(AccountRESTClient accountService, String inputUsername){
		this.accountService = accountService;
		this.inputUsername = inputUsername;
	}
	
	public String validateUsername() throws IOException{
		return accountService.validateUsername(inputUsername);
	}
	
	@Override
	public Object Execute() throws IOException {
		return validateUsername();
	}
}
