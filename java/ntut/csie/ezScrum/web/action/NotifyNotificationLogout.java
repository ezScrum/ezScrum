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

public class NotifyNotificationLogout extends Action{
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		AccountObject account = session.getAccount();
		String firebaseToken = account.getFirebaseToken();
		String token = account.getToken();
		String s = NotifyLogout(account.getId(), firebaseToken, token);
		MicroserviceProxy ap = new MicroserviceProxy(token);
		response.setContentType("text/html; charset=utf-8");
		
		try {
			response.getWriter().write(s);
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	private String NotifyLogout(Long account_id, String firebaseToken, String token){
		AccountRESTClientProxy ap = new AccountRESTClientProxy(token);
		return ap.notifyServiceLogout(account_id, firebaseToken);
	}
}
