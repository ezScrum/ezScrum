package ntut.csie.ezScrum.web.action.rbac;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.helper.AccountHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.account.core.IAccount;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class AddUserAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) throws Exception {

		long id = Long.parseLong(request.getParameter("id"));
		long projectId = Long.parseLong(request.getParameter("resource"));
		String role = request.getParameter("operation");

		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");

		if ((id > 0) && (projectId > 0) && (role != null) && (session != null)) {
			try {
				AccountHelper accountHelper = new AccountHelper(session);
				AccountObject account = accountHelper.assignRole_add(id, projectId, role);

				// 刪除Session中關於該使用者的所有專案權限。
				response.setContentType("text/xml; charset=utf-8");
				response.getWriter().write(accountHelper.getAccountXML(account));
				response.getWriter().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
