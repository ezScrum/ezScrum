package ntut.csie.ezScrum.web.action.rbac;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.helper.ScrumRoleHelper;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class UpdateScrumRolePermissionAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String ProjectID = request.getParameter("ProjectID");
		String RoleName = request.getParameter("RoleName");
		String PermissionList = request.getParameter("PermissionList");

		ScrumRoleHelper srh = new ScrumRoleHelper();		
//		Gson gson = new Gson();
//		AccessPermissionUI PermissionUI = gson.fromJson(PermissionList, AccessPermissionUI.class);
		
		// default content type
		response.setContentType("text/html; charset=utf-8");
		
		try {
//			updatePermission(ProjectID, RoleName, PermissionUI);
			srh.updateScrumRolePermission(ProjectID, RoleName, PermissionList);
			response.getWriter().write("true");
			response.getWriter().close();
		} catch (IOException e) {
			response.getWriter().write("false");
			response.getWriter().close();
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * 更新Permission資料
	 */
//	private void updatePermission(String projectID, String scrumRole, AccessPermissionUI permissionUI) throws Exception{
//		ScrumRole scrumrole = new ScrumRole(projectID, scrumRole);
//		
//		if (scrumrole != null) {
//			scrumrole.setAccessProductBacklog(permissionUI.AccessProductBacklog);
//			scrumrole.setAccessReleasePlan(permissionUI.AccessReleasePlan);
//			scrumrole.setAccessSprintPlan(permissionUI.AccessSprintPlan);
//			scrumrole.setAccessSprintBacklog(permissionUI.AccessSprintBacklog);
//			scrumrole.setAccessTaskBoard(permissionUI.AccessTaskboard);
//			scrumrole.setAccessUnplannedItem(permissionUI.AccessUnplanned);
//			scrumrole.setAccessRetrospective(permissionUI.AccessRetrospective);
//			scrumrole.setReadReport(permissionUI.AccessReport);
//			scrumrole.setEditProject(permissionUI.AccessEditProject);			
//			
//			ScrumRoleManager manager = new ScrumRoleManager();
//			manager.update(scrumrole);
//		}
//	}
}
