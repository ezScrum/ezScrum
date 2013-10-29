package ntut.csie.ezScrum.web.action.backlog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class GetEditStoryInfoAction extends PermissionAction {
	private static Log log = LogFactory.getLog(GetEditStoryInfoAction.class);
	
	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessProductBacklog();
	}

	@Override
	public boolean isXML() {
		// XML
		return true;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		log.info("Get Edit Story Information in GetEditStoryInfoAction.");
		
		// get session info
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		
		// get parameter info
		long issueID = Long.parseLong(request.getParameter("issueID"));
		StringBuilder result = (new ProductBacklogHelper(session, project)).getEditStoryInformationResponseText(issueID);
		
		return result;
		
//    	ProductBacklogHelper helper = new ProductBacklogHelper(project,session);
//		IIssue issue = helper.getIssue(issueID);
//		
//		StringBuilder result = new StringBuilder("");
//		result.append(new Translation().translateStory(issue));
//		
//		return result;
	}
}