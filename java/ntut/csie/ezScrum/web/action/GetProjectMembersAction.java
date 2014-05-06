package ntut.csie.ezScrum.web.action;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.UserObject;
import ntut.csie.ezScrum.web.helper.ProjectHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetProjectMembersAction extends Action {
	private static Log log = LogFactory.getLog(GetProjectMembersAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) {
		log.info("Get Project Members in GetProjectMembersAction.java");
//		IProject project = (IProject) SessionManager.getProject(request);
		ProjectObject project = (ProjectObject) SessionManager.getProjectObject(request);
//		IUserSession userSession = (IUserSession) request.getSession().getAttribute("UserSession");

		List<UserObject> accounts = new ProjectHelper().getProjectMemberList(project);

		StringBuilder result = new StringBuilder();
		result.append("<Members>");
		if (accounts != null && accounts.size() > 0) {
			for (UserObject acc : accounts) {
				result.append("<Member>");
				result.append("<ID>").append(acc.getId()).append("</ID>");
				result.append("<Account>").append(acc.getAccount()).append("</Account>");
				result.append("<Name>").append(acc.getName()).append("</Name>");
//				result.append("<Role>").append(splitRole(project, acc.getRoles())).append("</Role>");
				result.append("<Role>").append(acc.getRoles().get(project.getName()).getScrumRole().getRoleName()).append("</Role>");
				result.append("<Enable>").append(acc.getEnable()).append("</Enable>");
				result.append("</Member>");
			}
		}
		result.append("</Members>");

		try {
			response.setContentType("text/xml; charset=utf-8");
			response.getWriter().write(result.toString());
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

//	private String splitRole(IProject project, IRole[] roles) {
//		String split_role = "";
//
//		if (roles.length > 0) {
//			for (IRole role : roles) {
//				// 將專案的角色以切字串方式取出
//				String[] token = role.getRoleId().split(project.getName() + "_");
//				if ((token.length == 2) && (token[1].length() > 0)) {
//					// 取得此專案的角色即可
//					split_role = token[1];
//					break;
//				}
//			}
//		}
//
//		return split_role;
//	}
}