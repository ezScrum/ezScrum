package ntut.csie.ezScrum.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ntut.csie.ezScru.web.microservice.AccountRESTClientProxy;
import ntut.csie.ezScru.web.microservice.IAccountController;
import ntut.csie.ezScrum.pic.core.IUserSession;

public class ViewProjectManagementAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		IAccountController accountService = new AccountRESTClientProxy();
		return mapping.findForward(accountService.getManagementView(session.getAccount()));
//		return mapping.findForward(new AccountHelper().getManagementView(session.getAccount()));
		
	}
}
