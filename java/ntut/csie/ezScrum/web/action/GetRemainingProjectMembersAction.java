package ntut.csie.ezScrum.web.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetRemainingProjectMembersAction extends Action {
	private static Log log = LogFactory.getLog(GetRemainingProjectMembersAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) {
		log.info("Get Remaining Project Members in GetRemainingProjectMembersAction.java");

		// get project member list
		ProjectObject project = SessionManager.getProjectObject(request);
		IUserSession userSession = (IUserSession) request.getSession().getAttribute("UserSession");
		
		// ezScrum v1.8
		// get total member list
		ArrayList<AccountObject> accounts = new ProjectMapper().getProjectMembers(project.getId());
		List<AccountObject> accountList = (new AccountMapper()).getAccounts();
		StringBuilder result = new StringBuilder();
		result.append("<Members>");
		for (AccountObject member : accountList) {
			if (!accounts.contains(member) && !member.getUsername().equals("admin")) {
				result.append("<Member>");
				result.append("<ID>").append(member.getUsername()).append("</ID>");
				result.append("<Name>").append(member.getNickName()).append("</Name>");
				result.append("<Role>").append(member.getRoles().get(project.getName()).getScrumRole().getRoleName()).append("</Role>");
				result.append("<Enable>").append(member.getEnable()).append("</Enable>");
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
