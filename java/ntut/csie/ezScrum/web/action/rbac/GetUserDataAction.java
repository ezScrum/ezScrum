package ntut.csie.ezScrum.web.action.rbac;

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

public class GetUserDataAction extends Action {
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		AccountObject account = session.getAccount();
		try {
			// 取得使用者帳號列表
			response.setContentType("text/xml; charset=utf-8");
			String token = session.getAccount().getToken();
			MicroserviceProxy accountService = new MicroserviceProxy(token);
//			AccountObject newAccount = accountService.getAccountByUsernamePassword(account.getUsername(), account.getPassword());
//			newAccount.setPassword(account.getPassword());
			response.getWriter().write(accountService.getAccountXML(account));
//			response.getWriter().write(new accountService.getAccountXML(account));
			response.getWriter().close();
		} catch (IOException e) {
			System.out.println("class : GetUserDataAction, method : execute, exception : " + e.toString());
			e.printStackTrace();
		}
		
		return null;
	}
}
