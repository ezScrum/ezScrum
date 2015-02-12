package ntut.csie.ezScrum.web.action;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.ProjectRole;
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
	        HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.info("Get Project Members in GetProjectMembersAction.java");
		ProjectObject project = (ProjectObject) SessionManager.getProjectObject(request);
		
		List<AccountObject> accounts = null;
		try {
			accounts = new ProjectHelper().getProjectMemberList(project);
		} catch (NullPointerException e) {
			e.printStackTrace();
			response.setStatus(403);
			response.getWriter().write("Can not access this page!");
		}
		

		StringBuilder result = new StringBuilder();
		result.append("<Members>");
		if (accounts != null && accounts.size() > 0) {
			for (AccountObject acc : accounts) {
				result.append("<Member>");
				result.append("<ID>").append(acc.getId()).append("</ID>");
				result.append("<Account>").append(acc.getUsername()).append("</Account>");
				result.append("<Name>").append(acc.getNickName()).append("</Name>");
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
}