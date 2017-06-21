package ntut.csie.ezScrum.web.action.rbac;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ntut.csie.ezScru.web.microservice.AccountRESTClientProxy;
import ntut.csie.ezScru.web.microservice.IAccountController;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.AccountObject;

public class AddUserAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) {

		long id, projectId;
		String scrumRole = "";
		try {
			id = Long.parseLong(request.getParameter("id"));
			projectId = Long.parseLong(request.getParameter("resource"));
			scrumRole = request.getParameter("operation");
		} catch (Exception e) {
			id = 0;
			projectId = 0;
			scrumRole = "";
		} finally {
			if (scrumRole == null) {
				scrumRole = "";
			}
		}

		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		
		PrintWriter writer;
		try {
			response.setContentType("text/xml; charset=utf-8");
			writer = response.getWriter();
		} catch (IOException e1) {
			return null;
		}
		
		boolean isAdmin = projectId == 0 && scrumRole.equals("admin");
		boolean hasPermission = id > 0 && projectId > 0 && !scrumRole.equals("") && session != null;

		if (isAdmin || hasPermission) {
			try {
				IAccountController accountService = new AccountRESTClientProxy();
				AccountObject account = accountService.addAssignedRole(id, projectId, scrumRole);
				writer.write(accountService.getAccountXML(account));
//				AccountHelper accountHelper = new AccountHelper();
//				AccountObject account = accountHelper.addAssignedRole(id, projectId, scrumRole);
//				writer.write(accountHelper.getAccountXML(account));
//				IUserSession userSession = (IUserSession) request.getSession().getAttribute("UserSession");
//				String token = userSession.getAccount().getToken();
//				AccountRESTClientProxy accountService = new AccountRESTClientProxy(token);
//				AccountObject account = null;
//				
//				if(isAdmin){
//					try {
//						String responseString = accountService.updateAccountSystemRole(id, true);
//						try {
//							JSONObject accountJSON = new JSONObject(responseString);
//							account = new AccountObject(Long.valueOf(accountJSON.getString("id")), accountJSON.getString("username"));
//							account.setEmail(accountJSON.getString("email"));
//							account.setEnable(Boolean.valueOf(accountJSON.getString("enabled")));
//							account.setNickName(accountJSON.getString("nickname"));	
//							account.setAdmin(Boolean.valueOf(accountJSON.getString("systemrole")));
//						} catch (JSONException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				 
//				}else{
//					account = accountService.getAccountById(id);
//					account.joinProjectWithScrumRole(projectId, RoleEnum.valueOf(scrumRole));
//				}
			} catch (IllegalArgumentException e) {
				response.setContentType("application/json; charset=utf-8");
				writer.write("{\"msg\": \"The role not exist\"}");
			} finally {
				writer.close();
			}
		}
		return null;
	}
}
