package ntut.csie.ezScrum.web.action.backlog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class ShowSprintBacklogAction extends PermissionAction {
	private static Log log = LogFactory.getLog(ShowSprintBacklogAction.class);

	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessSprintBacklog();
	}

	@Override
	public boolean isXML() {
		return false;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		log.info("Show Sprint Backlog in ShowSprintBacklogAction.");
		ProjectObject project = (ProjectObject) SessionManager.getProjectObject(request);

		long sprintId = Long.parseLong(request.getParameter("sprintID"));
		StringBuilder result = new StringBuilder(new SprintBacklogHelper(project, sprintId).getShowSprintBacklogText());
		return result;
	}
}