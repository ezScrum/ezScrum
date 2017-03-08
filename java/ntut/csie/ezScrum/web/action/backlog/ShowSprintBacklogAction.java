package ntut.csie.ezScrum.web.action.backlog;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

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
		ProjectObject project = (ProjectObject) SessionManager.getProject(request);
		
		long serialSprintId = Long.parseLong(request.getParameter("sprintID"));
		SprintObject sprint = SprintObject.get(project.getId(), serialSprintId);
		long sprintId = -1;
		if (sprint != null) {
			sprintId = sprint.getId();
		}
		SprintBacklogHelper spintBacklogHelper = new SprintBacklogHelper(project, sprintId);		
		ArrayList<TaskObject> tasks = spintBacklogHelper.getTaskBySprintId(sprintId);
				
		StringBuilder result = new StringBuilder(spintBacklogHelper.getShowSprintBacklogText());
		result.setLength(result.length() - 1);
		result.append(",\"TotalTask\":" +  tasks.size() +"}");
		return result;
	}
}