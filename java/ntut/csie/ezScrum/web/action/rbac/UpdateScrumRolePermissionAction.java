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

		String id = request.getParameter("ID");
		String projectID = request.getParameter("ProjectID");
		String roleName = request.getParameter("RoleName");
		String permissionList = request.getParameter("PermissionList");

		ScrumRoleHelper srh = new ScrumRoleHelper();

		// default content type
		response.setContentType("text/html; charset=utf-8");

		try {
			srh.updateScrumRolePermission(id, projectID, roleName, permissionList);
			response.getWriter().write("true");
			response.getWriter().close();
		} catch (IOException e) {
			response.getWriter().write("false");
			response.getWriter().close();
			e.printStackTrace();
		}

		return null;
	}
}
