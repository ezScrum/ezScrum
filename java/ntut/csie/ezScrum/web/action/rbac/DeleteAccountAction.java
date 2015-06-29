package ntut.csie.ezScrum.web.action.rbac;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.helper.AccountHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class DeleteAccountAction extends Action {
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		IUserSession userSession = (IUserSession) request.getSession().getAttribute("UserSession");
		long accountId;
		
		try {
			accountId = Long.parseLong(request.getParameter("id"));
		} catch (NumberFormatException e) {
			accountId = 0;
		}

		// 設置 Header 與編碼
		response.setContentType("text/html");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		
		boolean result = (new AccountHelper(userSession)).deleteAccount(accountId);
		
		try {
			response.getWriter().write(String.valueOf(result));
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// 刪除 Session 中關於該使用者的所有專案權限。
		SessionManager.removeScrumRolesMap(request, userSession.getAccount());
		
		return null;
	}
}
