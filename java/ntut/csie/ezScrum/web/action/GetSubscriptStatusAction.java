package ntut.csie.ezScrum.web.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.google.gson.Gson;

import ntut.csie.ezScru.web.microservice.AccountRESTClientProxy;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.AccountObject;

public class GetSubscriptStatusAction extends Action{
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		AccountObject account = session.getAccount();
		String firebaseToken = request.getParameter("firebaseToken");
		session.getAccount().setFirebaseToken(firebaseToken);
		String notificationStatus = getNotificationStatus(account.getId(), firebaseToken, account.getToken());
		SubscriptInfoUI siui = new SubscriptInfoUI(notificationStatus);
		Gson gson = new Gson();
		
		response.setContentType("text/html; charset=utf-8");
		try {
			response.getWriter().write(gson.toJson(siui));
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private class SubscriptInfoUI{
		private String SubscriptStatus = "";
		public SubscriptInfoUI(String SubscriptStatus){
			this.SubscriptStatus = SubscriptStatus;
		}
	}
	
	private String getNotificationStatus(Long account_id ,String firebaseToken, String token){
		AccountRESTClientProxy ap = new AccountRESTClientProxy(token);
		return ap.getNotificationSubscriptStatus(account_id, firebaseToken);
	}
}
