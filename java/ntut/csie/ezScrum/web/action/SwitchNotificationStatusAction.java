package ntut.csie.ezScrum.web.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ntut.csie.ezScru.web.microservice.MicroserviceProxy;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.AccountObject;

public class SwitchNotificationStatusAction extends Action{
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		AccountObject account = session.getAccount();
		Long account_id = account.getId();
		String firebaseToken = account.getFirebaseToken();
		
		String event = request.getParameter("event");
		String s = "";
		if(event.contains("Subscribe")){
			s = Subscribe(account_id, firebaseToken, account.getToken());
		}
		else if(event.contains("Cancel")){
			s = CancelSubscribe(account_id, firebaseToken, account.getToken());
		}
		else if(event.contains("updateProjectSubscriptStatus")){
			String projectName =  request.getParameter("Id");
			String statusType = request.getParameter("statusType");
			boolean status = request.getParameter("status").equals("true");
			s = UpdateProjectsScriptStatus(account_id, account, projectName, statusType, status);
		}
		response.setContentType("text/html; charset=utf-8");
		
		try {
			response.getWriter().write(s);
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String Subscribe(Long account_id ,String firebaseToken, String token){
		MicroserviceProxy ap = new MicroserviceProxy(token);
		return ap.subscribeNotification(account_id, firebaseToken);
	}
	
	private String CancelSubscribe(Long account_id ,String firebaseToken, String token){
		MicroserviceProxy ap = new MicroserviceProxy(token);
		return ap.cancelSubscribeNotification(account_id, firebaseToken);
	}
	
	private String UpdateProjectsScriptStatus(Long account_id, AccountObject account, String projectName, String statusType, boolean status){
		MicroserviceProxy ap = new MicroserviceProxy(account.getToken());
		String projectsSubscriptStatus = account.getProjectsSubscriptStatus();
		account.updateProjectsSubscriptStatus(projectName, statusType, status);
		String s = ap.updateProjectsScriptStatus(account_id, account.getProjectsSubscriptStatus());
		if(!s.equals("Success")){
			account.setProjectsSubscriptStatus(projectsSubscriptStatus);
		}
		return s;
	}
}
