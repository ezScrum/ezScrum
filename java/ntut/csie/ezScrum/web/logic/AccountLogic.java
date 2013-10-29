package ntut.csie.ezScrum.web.logic;

import javax.servlet.http.HttpServletRequest;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.account.core.IAccount;
import ntut.csie.jcis.resource.core.IProject;

public class AccountLogic {

	public AccountLogic() {}

	/**
	 * 判斷帳號對於專案的權限
	 * 
	 * @return
	 */
	public boolean checkAccount(HttpServletRequest request) {
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession userSession = (IUserSession) request.getSession().getAttribute("UserSession");
		// 判斷使用者是否為被啟用狀態
		IAccount account = userSession.getAccount();
		if (!account.getEnable().equals("true")) {
			return false;
		}
		ScrumRole sr = SessionManager.getScrumRole(request, project, account);

		if (sr == null) {
			return false;
		}

		// 判斷使用者是否為 guest 使用者
		if (sr.isGuest()) {
			return false;
		}

		// 判斷使用者是否為 admin 使用者
		if (sr.isAdmin()) {
			return true;
		}

		// 判斷使用者是否為存在於資料庫的使用者
		ProjectLogic projectLogic = new ProjectLogic();
		if (!(projectLogic.userIsExistedInProject(project, userSession))) {
			return false;
		}
		// if ( ! existUser(acc.getID())) {
		// return false;
		// }

		return true;
	}
}
