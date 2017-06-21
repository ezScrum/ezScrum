package ntut.csie.ezScrum.web.action.rbac;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ntut.csie.ezScru.web.microservice.AccountRESTClientProxy;
import ntut.csie.ezScru.web.microservice.IAccountController;
import ntut.csie.ezScrum.pic.core.IUserSession;

public class GetAssignedProjectAction extends Action {
	public ActionForward execute(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response)
	        throws Exception {

		long id;
		try {
			id = Long.parseLong(request.getParameter("accountID"));
		} catch (NumberFormatException e) {
			id = 0;
		}
		
		try {
			
			IUserSession userSession = (IUserSession) request.getSession().getAttribute("UserSession");
			String token = userSession.getAccount().getToken();
			IAccountController accountService = new AccountRESTClientProxy(token);
			response.setContentType("text/xml; charset=utf-8");
//			response.getWriter().write(new AccountHelper().getAssignedProject(id));
			response.getWriter().write(accountService.getAssignedProject(id));
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
//	private final String SYSTEM = "system";
//	public String getAssignedProject(AccountObject account){
//		
////		AccountObject account = mAccountMapper.getAccount(accountId);
////		HashMap<String, ProjectRole> rolesMap = mAccountMapper.getProjectRoleList(accountId);
//		HashMap<String, ProjectRole> rolesMap = account.getRoles();
//		List<String> assignedProject = new ArrayList<String>();
//		StringBuilder assignRoleInfo = new StringBuilder();
//		
//		// 取得帳號的Assign資訊
//		assignRoleInfo.append("<AssignRoleInfo>");
//		assignRoleInfo.append("<AccountInfo>");
//		// Account Info
//		assignRoleInfo.append("<ID>").append(account.getId()).append("</ID>");
//		assignRoleInfo.append("<Account>").append(account.getUsername()).append("</Account>");
//		assignRoleInfo.append("<Name>").append(account.getNickName()).append("</Name>");
//		// Assign Roles
//		assignRoleInfo.append("<Roles>");
//		for (Entry<String, ProjectRole> entry : rolesMap.entrySet()) {
//			ScrumRole permission = entry.getValue().getScrumRole();
//			ProjectObject project = entry.getValue().getProject();
//			String resource = permission.getProjectName();
//			String operation = permission.getRoleName();
//			assignRoleInfo.append("<Assigned>")
//			  			  .append("<ResourceId>").append(project.getId()).append("</ResourceId>")
//						  .append("<Resource>").append(resource).append("</Resource>")
//						  .append("<Operation>").append(operation).append("</Operation>")
//						  .append("</Assigned>");
//			assignedProject.add(resource);	// 記錄此 project 為 assigned	
//		}
//		assignRoleInfo.append("</Roles>");
//		
//		// UnAssign Roles
//		ProjectLogic projectLogic = new ProjectLogic();
//		ArrayList<ProjectObject> projects = projectLogic.getProjects();
//		for (ProjectObject project : projects) {
//			String resource = project.getName();
//			// 如果project沒有被assigned權限，則代表為unassigned的project
//			if (!assignedProject.contains(resource)) {
//				assignRoleInfo.append("<Unassigned>")
//				  			  .append("<ResourceId>").append(project.getId()).append("</ResourceId>")
//							  .append("<Resource>").append(resource).append("</Resource>")
//							  .append("</Unassigned>");
//			}
//		}
//		// 判斷是否為administrator
//		if (!assignedProject.contains(this.SYSTEM)) 
//			assignRoleInfo.append("<Unassigned><ResourceId>0</ResourceId><Resource>")
//						  .append(this.SYSTEM)
//						  .append("</Resource></Unassigned>");
//		
//		assignRoleInfo.append("</AccountInfo>");
//		assignRoleInfo.append("</AssignRoleInfo>");
//		
//		return assignRoleInfo.toString();
//	}
}
