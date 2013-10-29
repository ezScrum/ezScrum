package ntut.csie.ezScrum.web.action.rbac;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.helper.ScrumRoleHelper;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetScrumRolePermissionListAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//取得專案及角色
		String projectName = request.getParameter("projectName");
		String scrumRole = request.getParameter("scrumRole");
		
		ScrumRoleHelper srh = new ScrumRoleHelper();	
//		ProjectMapper projectMapper = new ProjectMapper();
//		IProject project = projectMapper.getProjectByID(projectName);
//		
//		Gson gson = new Gson();
//		String result = "";
//		if (project.exists()) {
//			ScrumRoleManager roleManager = new ScrumRoleManager();
//			ScrumRole scrumrole = roleManager.getPermission(projectName, scrumRole);
//			
//			result = gson.toJson(new AccessPermissionUI(scrumrole));
//		} else {
//			result = gson.toJson(new AccessPermissionUI(null));
//		}

		// default content type
		response.setContentType("text/html; charset=utf-8");
		
		try {
//			response.getWriter().write(result);
			response.getWriter().write(srh.getScrumRolePermissionList(projectName, scrumRole));
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
