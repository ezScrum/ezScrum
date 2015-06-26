package ntut.csie.ezScrum.web.action.rbac;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataInfo.AccountInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.helper.AccountHelper;
import ntut.csie.ezScrum.web.mapper.AccountMapper;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.ethz.ssh2.crypto.Base64;

/**
 * update account info (非 admin account 專用)
 */
public class UpdateAccountAction extends Action {
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");

		AccountInfo accountInfo = new AccountInfo();
		try {
			accountInfo.id = Long.parseLong(request.getParameter("id"));
			accountInfo.username = request.getParameter("account");
			accountInfo.password = request.getParameter("passwd");
			accountInfo.nickName = request.getParameter("name");
			accountInfo.email = request.getParameter("mail");
			accountInfo.enable = Boolean.parseBoolean(request.getParameter("enable"));
		} catch (NumberFormatException e) {
			accountInfo.id = 0;
		}
		
		AccountHelper accountHelper = new AccountHelper(session);
		AccountMapper accountMapper = new AccountMapper();
		AccountObject oldAccount = accountMapper.getAccount(accountInfo.id);
		AccountObject newAccount = accountHelper.updateAccount(accountInfo);

		//	no password, use the default password
		if ((accountInfo.password == null) || (accountInfo.password.length() == 0) || accountInfo.password.equals("")) {
			accountInfo.password = oldAccount.getPassword();
		}

		//	如果更新的是登入者的密碼則更新 session 中屬於插件使用的密碼
		if (session.getAccount().getUsername().equals(accountInfo.username)) {
			String encodedPassword = new String(Base64.encode(accountInfo.password.getBytes()));
			request.getSession().setAttribute("passwordForPlugin", encodedPassword);
		}

		//	目前改了密碼之後並未強制使用者登出,可改良以避免一些問題
		AccountObject sessionAccount = session.getAccount();

		sessionAccount.setNickName(newAccount.getNickName());
		sessionAccount.setEmail(newAccount.getEmail());
		sessionAccount.setPassword(newAccount.getPassword());	// 應該是下次登入才生效,但存取專案資料是比對新的密碼

		try {
			response.setContentType("text/xml; charset=utf-8");
			response.getWriter().write(accountHelper.getAccountXML(newAccount));
			response.getWriter().close();
		} catch (IOException e) {
			System.out.println("class : UpdateAccountAction, method : execute, exception : " + e.toString());
			e.printStackTrace();
		}

		return null;
	}
}
