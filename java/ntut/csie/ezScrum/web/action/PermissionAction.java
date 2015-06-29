package ntut.csie.ezScrum.web.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.logic.AccountLogic;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public abstract class PermissionAction extends Action {
	private static Log log = LogFactory.getLog(PermissionAction.class);

	private ScrumRole sr;

	// 讓繼承的˙concrete 自行判斷權限
	public abstract boolean isValidAction();

	// 設定輸出字串型態（xml or html）
	public abstract boolean isXML();

	// 讓繼承的 concrete 回傳要回覆的訊息
	public abstract StringBuilder getResponse(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		ProjectObject project = (ProjectObject) SessionManager
				.getProjectObject(request);
		IUserSession userSession = (IUserSession) request.getSession()
				.getAttribute("UserSession");
		AccountObject account = userSession.getAccount();
		sr = SessionManager.getScrumRole(request, project, account);

		StringBuilder result = null;

		// default content type
		response.setContentType("text/html; charset=utf-8");

		AccountLogic accountLogic = new AccountLogic();
		// 檢查帳號不通過，提示錯誤頁面 // 檢查此帳號是否允許操作 concrete action 的權限
		if (accountLogic.checkAccount(request) && isValidAction()) {
			result = new StringBuilder(getResponse(mapping, form, request,
					response));
			if (isXML()) {
				// 由 concrete action 決定要回傳的訊息為 XML or Json
				response.setContentType("text/xml; charset=utf-8");
			}
		}

		// 此為錯誤權限存取，回傳權限錯誤的資訊給頁面提示使用者
		if (result == null) {
			result = new StringBuilder(
					"{\"PermissionAction\":{\"ActionCheck\":\"false\", \"Id\":0}}");
			log.info("Account " + account.getUsername() + " access deny.");
		}

		try {
			response.getWriter().write(result.toString());
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public ScrumRole getScrumRole() {
		return sr;
	}
}