package ntut.csie.ezScrum.web.action.rbac;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.codehaus.jettison.json.JSONException;

import ntut.csie.ezScru.web.microservice.CallAccountMicroservice;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.SecurityRequestProcessor;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.support.TranslateUtil;

public class GetAccountListAction extends Action {
	public ActionForward execute(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) throws JSONException {
		try {
			// 取得帳號列表
			IUserSession userSession = (IUserSession) request.getSession().getAttribute("UserSession");
			response.setContentType("text/xml; charset=utf-8");
			CallAccountMicroservice accountService = new CallAccountMicroservice(userSession.getAccount().getToken());
			ArrayList<AccountObject> accounts = accountService.getAccounts();
//			response.getWriter().write(new AccountHelper().getAccountListXML());
			response.getWriter().write(getXmlstring(accounts));
			LogFactory.getLog(SecurityRequestProcessor.class).debug("Current Time : " + new Date().toString());
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	private String getXmlstring(ArrayList<AccountObject> accounts) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<Accounts>");
		for (AccountObject account : accounts) {
			if (account == null) {
				stringBuilder.append("Account not found.");
			} else {
				stringBuilder.append("<AccountInfo>");
				stringBuilder.append("<ID>").append(account.getId()).append("</ID>");
				stringBuilder.append("<Account>").append(account.getUsername()).append("</Account>");
				stringBuilder.append("<Name>").append(account.getNickName()).append("</Name>");
				stringBuilder.append("<Mail>").append(account.getEmail()).append("</Mail>");
				stringBuilder.append("<Roles>").append(TranslateUtil.getRolesString(account.getRoles())).append("</Roles>");
				stringBuilder.append("<Enable>").append(account.getEnable()).append("</Enable>");
				stringBuilder.append("</AccountInfo>");
			}
		}
		stringBuilder.append("</Accounts>");
		
		return stringBuilder.toString();
	}
}
