package ntut.csie.ezScrum.web.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.google.gson.JsonObject;

public class ValidateUserEventAction extends Action {
	public ActionForward execute(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) {

		HttpSession session = request.getSession();
		IUserSession userSession = (IUserSession) session.getAttribute("UserSession");

		boolean isUserSessionExisted = false;	//	false:代表Session Out of Time;true:Session Existed。
		boolean isProjectAccess = false;		//	false:代表沒有擁有專案權限。true:代表擁有專案權限。

		//	判斷Session是否已經過期。
		//	沒有過期才需要對Permission進行驗證。
		if (userSession != null) {
			boolean isProjectNeed = checkPageNeedProject(request.getHeader("Referer"));
			//	判斷當前位置是否需要專案資訊。
			if (isProjectNeed) {
				isProjectAccess = checkAccount(request);
			} else {
				isProjectAccess = true;
			}
			isUserSessionExisted = true;
		}
		/*		}else{
					//    由於session已經過期所以其他驗證皆不需要，皆設為false
					isUserSessionExisted = false;
					isProjectAccess = false;
				}*/

		JsonObject json = new JsonObject();
		json.addProperty("IsUserSessionExisted", isUserSessionExisted);
		json.addProperty("IsProjectAccess", isProjectAccess);

		try {
			response.getWriter().write(json.toString());
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 判別目前頁面的位置 false:不需有專案資訊也可進行權限認證 true:需有專案資訊才能進行權限認證
	 * 
	 * @param refererString
	 * @return
	 */
	private boolean checkPageNeedProject(String refererString) {
		if (refererString.contains("logonSubmit.do")) {
			return false;	//	for gae use，當admin或使用者在登入時，不需要有專案資訊。
		} else if (refererString.contains("viewManagement.do")) {
			return false;	//	admin或使用者管理個人資訊或他人資訊時，不需要有專案資訊。
		} else if (refererString.contains("viewList.do")) {
			return false;	//	admin或使用者在觀看專案列表時，不需要有專案資訊。
		} else {
			return true;
		}
	}

	public boolean checkAccount(HttpServletRequest request) {
		HttpSession session = request.getSession();
		ProjectObject project = SessionManager.getProjectObject(request);
		IUserSession userSession = (IUserSession) session.getAttribute("UserSession");
		// 判斷使用者是否為被啟用狀態
		AccountObject account = userSession.getAccount();
		if (!account.getEnable()) {
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

		/*			// 判斷使用者是否為存在於資料庫的使用者
					if ( ! existUser(acc.getID())) {
						return false;
					}*/

		return true;
	}
}
