package ntut.csie.ezScrum.web.action.rbac;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.codehaus.jettison.json.JSONException;

import ch.ethz.ssh2.crypto.Base64;
import ntut.csie.ezScru.web.microservice.MicroserviceProxy;
import ntut.csie.ezScru.web.microservice.IAccount;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataInfo.AccountInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.support.SessionManager;

/**
 * create & update 都是使用此 Action (admin 專用)
 */
public class ModifyAccountAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) throws JSONException, IOException {

		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
//		AccountHelper accountHelper = new AccountHelper();
		IAccount accountService = new MicroserviceProxy(session.getAccount().getToken());
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
			
		
//			String responseString = accountService.updateAccount(accountInfo);
//			JSONObject accountJSON = new JSONObject(responseString);
//			newAccount = new AccountObject(Long.valueOf(accountJSON.getString("id")), accountJSON.getString("username"));
//			newAccount.setEmail(accountJSON.getString("email"));
//			newAccount.setEnable(Boolean.valueOf(accountJSON.getString("enabled")));
//			newAccount.setNickName(accountJSON.getString("nickname"));	
//			newAccount.setAdmin(Boolean.valueOf(accountJSON.getString("systemrole")));
			newAccount = accountService.updateAccount(accountInfo);
//			newAccount = accountHelper.updateAccount(accountInfo);	
			newAccount.setToken(session.getAccount().getToken());
//			

			// no password information, use the default password
			if ((accountInfo.password == null) || (accountInfo.password.length() == 0) || accountInfo.password.equals("")) {
				// get default password
//				accountInfo.password = newAccount.getPassword();
				accountInfo.password = session.getAccount().getPassword();
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
			
			newAccount = accountService.createAccount(accountInfo);
//			newAccount = accountHelper.createAccount(accountInfo);
//			if(!responseString.contains("Fail")){
//				newAccount = new AccountObject(Long.valueOf(responseString), accountInfo.username);
//				newAccount.setEmail(accountInfo.email)
//						  .setNickName(accountInfo.nickName)
//						  .setEnable(accountInfo.enable)
//						  .setPassword(accountInfo.password);
//			}

		}

		StringBuilder result = new StringBuilder();
		if (newAccount != null) {
			// 刪除 Session 中關於該使用者的所有專案權限。
			SessionManager.removeScrumRolesMap(request, newAccount);
			result.append(accountService.getAccountXML(newAccount));
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
	
//	private String getXmlstring(ArrayList<AccountObject> accounts) {
//		StringBuilder stringBuilder = new StringBuilder();
//		stringBuilder.append("<Accounts>");
//		for (AccountObject account : accounts) {
//			if (account == null) {
//				stringBuilder.append("Account not found.");
//			} else {
//				stringBuilder.append("<AccountInfo>");
//				stringBuilder.append("<ID>").append(account.getId()).append("</ID>");
//				stringBuilder.append("<Account>").append(account.getUsername()).append("</Account>");
//				stringBuilder.append("<Name>").append(account.getNickName()).append("</Name>");
//				stringBuilder.append("<Mail>").append(account.getEmail()).append("</Mail>");
//				stringBuilder.append("<Roles>").append(TranslateUtil.getRolesString(account.getRoles())).append("</Roles>");
//				stringBuilder.append("<Enable>").append(account.getEnable()).append("</Enable>");
//				stringBuilder.append("</AccountInfo>");
//			}
//		}
//		stringBuilder.append("</Accounts>");
//		
//		return stringBuilder.toString();
//	}
}
