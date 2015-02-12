package ntut.csie.ezScrum.web.action.rbac;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.helper.AccountHelper;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class AddUserAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) {

		long id, projectId;
		String scrumRole;
		try {
			id = Long.parseLong(request.getParameter("id"));
			projectId = Long.parseLong(request.getParameter("resource"));
			scrumRole = request.getParameter("operation");
		} catch (NumberFormatException e) {
			id = 0;
			projectId = 0;
			scrumRole = null;
		}

		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		
		PrintWriter writer;
		try {
			response.setContentType("text/xml; charset=utf-8");
			writer = response.getWriter();
		} catch (IOException e1) {
			return null;
		}
		

		if ((id > 0) && (projectId > 0) && (scrumRole != null) && (session != null)) {
			try {
				AccountHelper accountHelper = new AccountHelper(session);
				AccountObject account = accountHelper.addAssignedRole(id, projectId, scrumRole);

				// 刪除 Session 中關於該使用者的所有專案權限。
				writer.write(accountHelper.getAccountXML(account));
			} catch (IllegalArgumentException e) {
				response.setContentType("application/json; charset=utf-8");
				writer.write("{\"msg\": \"The role not exist\"}");
			} finally {				
				writer.close();
			}
		}
		return null;
	}
}
