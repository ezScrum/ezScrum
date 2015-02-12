package ntut.csie.ezScrum.web.logic;

import javax.servlet.http.HttpServletRequest;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.support.SessionManager;

public class AccountLogic {

	public AccountLogic() {}

	/**
	 * 判斷帳號對於專案的權限
	 * 
	 * @return
	 */
	public boolean checkAccount(HttpServletRequest request) {
		ProjectObject project = SessionManager.getProjectObject(request);
		IUserSession userSession = (IUserSession) request.getSession().getAttribute("UserSession");
		// 判斷使用者是否為被啟用狀態
		AccountObject account = userSession.getAccount();
		if (!account.getEnable()) {
			return false;
		}

		// ezScrum v1.8
		ScrumRole sr = new ScrumRoleLogic().getScrumRole(project, account);
		if (sr == null || sr.isGuest()) {	// 判斷使用者是否為 guest 使用者
			return false;
		} else {
			return true;
		}
	}
}
