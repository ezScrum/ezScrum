package ntut.csie.ezScrum.web.action.backlog;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AddExistedStoryAction extends PermissionAction {
	
	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessSprintBacklog();
	}

	@Override
	public boolean isXML() {
		// html
		return false;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		// get session info
		ProjectObject project = SessionManager.getProjectObject(request);
//		IProject iProject = (IProject) SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		
		// get parameter info
		String[] issueIDs = request.getParameterValues("selects");
		String sprintId = request.getParameter("sprintID");
		String releaseID = request.getParameter("releaseID");
		
		ArrayList<Long> list = new ArrayList<Long>();
		for (String issueID : issueIDs) {
			list.add(Long.parseLong(issueID));
		}
		
		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(project, session, sprintId);
		sprintBacklogHelper.addExistingStory(list, releaseID);
		
		return new StringBuilder("");
	}
}
