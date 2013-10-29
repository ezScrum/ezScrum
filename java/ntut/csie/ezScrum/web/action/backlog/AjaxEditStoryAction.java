package ntut.csie.ezScrum.web.action.backlog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AjaxEditStoryAction extends PermissionAction {
	private static Log log = LogFactory.getLog(AjaxEditStoryAction.class);
	
	@Override
	public boolean isValidAction() {
		return (super.getScrumRole().getAccessProductBacklog() && 
				super.getScrumRole().getAccessTaskBoard());
	}

	@Override
	public boolean isXML() {
		// html
		return false;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		log.info("Edit Story in AjaxEditStoryAction.");
		
		// get session info
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		
		// get parameter info
		long id = Long.parseLong(request.getParameter("issueID"));
		String Name = request.getParameter("Name");
		String importances = request.getParameter("Importance");
		String estimated = request.getParameter("Estimation");
		String value = request.getParameter("Value");
		String howToDemo = request.getParameter("HowToDemo");
		String notes = request.getParameter("Notes");
//		ITSPrefsStorage prefs = new ITSPrefsStorage(project, session);
//		String mantisUrl = prefs.getServerUrl();
		
		ProductBacklogHelper productBacklogHelper = new ProductBacklogHelper(session, project);
		IIssue issue = productBacklogHelper.editStory(id, Name, value, importances, estimated, howToDemo, notes);
		StringBuilder result = productBacklogHelper.translateStoryToJson(issue);
		
		return result;
		
//		ProductBacklogHelper helper = new ProductBacklogHelper(project, session);
//		helper.edit(id,Name,value,importances, estimated, howToDemo, notes);
//
//		IIssue issue = helper.getIssue(id);
//		
//		StringBuilder result = new StringBuilder("");
//		result.append(new Translation(mantisUrl).translateStoryToJson(issue));
//		result.append(new Translation().translateStoryToJson(issue));
//		
//		return result;
	}
}
