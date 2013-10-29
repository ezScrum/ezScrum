package ntut.csie.ezScrum.web.action.report;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.helper.ProjectHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AjaxGetPartnerListAction extends PermissionAction {
	private static Log log = LogFactory.getLog(AjaxGetPartnerListAction.class);
	
	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessTaskBoard();
	}

	@Override
	public boolean isXML() {
		// XML
		return true;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		log.info("Get Partner List in AjaxGetPartnerListAction.java");
		// get project from session or DB
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession userSession = (IUserSession) request.getSession().getAttribute("UserSession");
		
		List<String> actors = new ProjectHelper().getProjectScrumWorkerList(userSession, project);
		StringBuilder result = new StringBuilder();
		result.append("<Partners><Result>success</Result>");

		for(int i = 1; i < actors.size(); i++) {
				result.append("<Partner>")
					  .append("<Name>").append(actors.get(i)).append("</Name>")
					  .append("</Partner>");
		}
		result.append("</Partners>");
		return result;
	}
}
