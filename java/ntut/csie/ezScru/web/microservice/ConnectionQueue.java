package ntut.csie.ezScru.web.microservice;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import ntut.csie.ezScru.web.microservice.command.ICommand;

public class ConnectionQueue {
	
	private Queue <ICommand> notDoneAction = new LinkedList<ICommand>();
	private String state = "notFinish";
	private AccountRESTClient accountRESTClient;
	Thread thread = new Thread(){
		@Override
		public void run(){
			while(state != "finish"){
				if(connect()){
					while(!notDoneAction.isEmpty()){
						ICommand a = notDoneAction.element();
						try {
							System.out.println("thread run");
							a.Execute();
							notDoneAction.remove();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					state = "finish";
				}
				try {
					sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
		}
	};
	
	public ConnectionQueue(AccountRESTClient accountRESTClient){
		this.accountRESTClient = accountRESTClient;
		state = "notFinish";
	}
	public void queue(ICommand command){
		notDoneAction.add(command);
		if(thread.isAlive()){
			state = "notFinish";
			thread.run();
		}thread.start();
		
	}
	private boolean connect(){
		try {
			return accountRESTClient.checkConnect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
}
