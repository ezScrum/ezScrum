package ntut.csie.ezScru.web.microservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ntut.csie.ezScru.web.microservice.command.AccountRESTCommand;

public class AccountServiceInvoker {
	private List<AccountRESTCommand> commandList = new ArrayList<AccountRESTCommand>();
	public void addAction(AccountRESTCommand command){
		commandList.add(command);
	}
	public Object doCommand() throws IOException{
		Object action = commandList.get(0).Execute();
		commandList.clear();
		return action;
	}
}
