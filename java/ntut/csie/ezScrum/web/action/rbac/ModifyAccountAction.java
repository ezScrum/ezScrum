package ntut.csie.ezScrum.web.action.rbac;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.UserInformation;
import ntut.csie.ezScrum.web.helper.AccountHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.account.core.IAccount;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.ethz.ssh2.crypto.Base64;

// create & update 都是使用此 Action
public class ModifyAccountAction extends Action {
	// private static Log log = LogFactory.getLog(ModifyAccountAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) {

		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");

		// 取得要新增帳戶的資料
		String id = request.getParameter("id");
		String password = request.getParameter("passwd");
		String email = request.getParameter("mail");
		String realName = request.getParameter("name");
		String enable = request.getParameter("enable");
		String isEdit = request.getParameter("isEdit");

		String roles = "user";

		UserInformation user = new UserInformation(id, realName, password, email, enable);

		AccountHelper ah = new AccountHelper(session);
		IAccount newAccount = null;

		// 更新使用者資訊
		if (Boolean.valueOf(isEdit)) {
			newAccount = ah.updateAccount(user);

			// no password information, use the default password
			if ((password == null) || (password.length() == 0) || password.equals("")) {
				password = newAccount.getPassword();		// get default password
			}

			// 如果更新的是登入者的密碼則更新session中屬於插件使用的密碼
			String userName = session.getAccount().getName();
			if (userName.equals(id)) {
				String encodedPassword = new String(Base64.encode(password.getBytes()));
				request.getSession().setAttribute("passwordForPlugin", encodedPassword);
			}
		} else {
			// 新增帳號
			newAccount = ah.createAccount(user, roles);
		}

		StringBuilder result = new StringBuilder();
		if (newAccount != null) {
			// 刪除Session中關於該使用者的所有專案權限。
			SessionManager.removeScrumRolesMap(request, newAccount);
			result.append(ah.getAccountXML(newAccount));
		} else {
			result.append("{\"success\": false}");
		}

		response.setContentType("text/xml; charset=utf-8");
		try {
			response.getWriter().write(result.toString());
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
