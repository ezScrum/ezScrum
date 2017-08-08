package ntut.csie.ezScru.web.microservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ntut.csie.ezScru.web.microservice.command.ICommand;

public class Invoker {
	private List<ICommand> commandList = new ArrayList<ICommand>();
	public void addAction(ICommand command){
		commandList.add(command);
	}
	public Object doCommand() throws IOException{
		Object action = commandList.get(0).Execute();
		commandList.clear();
		return action;
	}
}
