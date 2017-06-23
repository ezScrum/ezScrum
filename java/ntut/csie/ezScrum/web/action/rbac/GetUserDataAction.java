package ntut.csie.ezScrum.web.action.rbac;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.codehaus.jettison.json.JSONObject;

import ntut.csie.ezScru.web.microservice.AccountRESTClientProxy;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.AccountObject;

public class GetUserDataAction extends Action {
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		AccountObject account = session.getAccount();
		try {
			// 取得使用者帳號列表
			response.setContentType("text/xml; charset=utf-8");
			String token = session.getAccount().getToken();
			AccountRESTClientProxy accountService = new AccountRESTClientProxy(token);
			String responseString = accountService.getAccountByUsernamePassword(account.getUsername(), account.getPassword());
			JSONObject accountJSON = new JSONObject(responseString);
			AccountObject newAccount = new AccountObject(Long.valueOf(accountJSON.getString("id")), accountJSON.getString("username"));
			newAccount.setEmail(accountJSON.getString("email"));
			newAccount.setAdmin(Boolean.valueOf(accountJSON.getString("systemrole")));
			newAccount.setNickName(accountJSON.getString("nickname"));
			newAccount.setEnable(Boolean.valueOf(accountJSON.getString("enabled")));
			newAccount.setPassword(account.getPassword());
			response.getWriter().write(accountService.getAccountXML(newAccount));
//			response.getWriter().write(new AccountHelper().getAccountXML(account));
			response.getWriter().close();
		} catch (IOException e) {
			System.out.println("class : GetUserDataAction, method : execute, exception : " + e.toString());
			e.printStackTrace();
		}
		
		return null;
	}
}
