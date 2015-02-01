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
		long id = Long.parseLong(request.getParameter("id"));
		String projectName = request.getParameter("projectName");
		String scrumRole = request.getParameter("scrumRole");

		ScrumRoleHelper scrumRoleHelper = new ScrumRoleHelper();

		// default content type
		response.setContentType("text/html; charset=utf-8");

		try {
			response.getWriter().write(scrumRoleHelper.getScrumRolePermissionList(id, projectName, scrumRole));
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
