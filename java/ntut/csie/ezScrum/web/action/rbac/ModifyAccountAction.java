package ntut.csie.ezScrum.web.action.rbac;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataInfo.AccountInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.helper.AccountHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.ethz.ssh2.crypto.Base64;

/**
 * create & update 都是使用此 Action (admin 專用)
 */
public class ModifyAccountAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) {

		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		AccountHelper accountHelper = new AccountHelper(session);
		AccountObject newAccount = null;
		AccountInfo accountInfo = new AccountInfo();

		// 判斷要 create 還是 update
		String isEdit = request.getParameter("isEdit");

		// update account
		if (Boolean.valueOf(isEdit)) {
			accountInfo.id = Long.parseLong(request.getParameter("id"));
			accountInfo.username = request.getParameter("account");
			accountInfo.password = request.getParameter("passwd");
			accountInfo.email = request.getParameter("mail");
			accountInfo.nickName = request.getParameter("name");
			accountInfo.enable = Boolean.parseBoolean(request.getParameter("enable"));
			
			newAccount = accountHelper.updateAccount(accountInfo);

			// no password information, use the default password
			if ((accountInfo.password == null) || (accountInfo.password.length() == 0) || accountInfo.password.equals("")) {
				// get default password
				accountInfo.password = newAccount.getPassword();
			}

			// 如果更新的是登入者的密碼則更新 session 中屬於插件使用的密碼
			String username = session.getAccount().getUsername();
			if (username.equals(accountInfo.username)) {
				String encodedPassword = new String(Base64.encode(accountInfo.password.getBytes()));
				request.getSession().setAttribute("passwordForPlugin", encodedPassword);
			}
		} else {
			// create account
			accountInfo.username = request.getParameter("account");
			accountInfo.password = request.getParameter("passwd");
			accountInfo.email = request.getParameter("mail");
			accountInfo.nickName = request.getParameter("name");
			accountInfo.enable = Boolean.parseBoolean(request.getParameter("enable"));
			
			newAccount = accountHelper.createAccount(accountInfo);
		}

		StringBuilder result = new StringBuilder();
		if (newAccount != null) {
			// 刪除 Session 中關於該使用者的所有專案權限。
			SessionManager.removeScrumRolesMap(request, newAccount);
			result.append(accountHelper.getAccountXML(newAccount));
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
