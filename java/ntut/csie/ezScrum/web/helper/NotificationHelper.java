package ntut.csie.ezScrum.web.helper;

import java.util.ArrayList;

import ntut.csie.ezScru.web.microservice.AccountRESTClientProxy;
import ntut.csie.ezScrum.web.dataObject.AccountObject;

public class NotificationHelper {

	public String Subscribe(Long account_id ,String firebaseToken, String token){
		AccountRESTClientProxy ap = new AccountRESTClientProxy(token);
		return ap.subscribeNotification(account_id, firebaseToken);
	}
	
	public String CancelSubscribe(Long account_id ,String firebaseToken, String token){
		AccountRESTClientProxy ap = new AccountRESTClientProxy(token);
		return ap.cancelSubscribeNotification(account_id, firebaseToken);
	}
	
	public String GetSubscribeStatus(Long account_id ,String firebaseToken, String token){
		AccountRESTClientProxy ap = new AccountRESTClientProxy(token);
		return ap.getNotificationSubscriptStatus(account_id, firebaseToken);
	}
	
	public String NotifyLogout(Long account_id ,String firebaseToken, String token){
		AccountRESTClientProxy ap = new AccountRESTClientProxy(token);
		return ap.notifyServiceLogout(account_id, firebaseToken);
	}
	
	public String SendMessage(AccountObject sender,String event, ArrayList<Long> recipients_id, long taskId,String projectName){
		//TODO get url and port
		
		String title = sender.getUsername() +" reset Task: " + taskId;
		String body = "In project:" + projectName;
		String eventSource = "https://"+":8080/ezScrum/viewProject.do?projectName=" + projectName;
		
		AccountRESTClientProxy ap = new AccountRESTClientProxy(sender.getToken());
		return ap.sendNotification(sender.getId(), recipients_id, title, body, eventSource);
	}
	
	public String UpdateProjectSubscribe(String projectsSubscribeStatus){
		
		return "";
	}
}
