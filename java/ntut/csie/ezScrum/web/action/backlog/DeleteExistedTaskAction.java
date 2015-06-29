package ntut.csie.ezScrum.web.action.backlog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class DeleteExistedTaskAction extends PermissionAction {
//	private static Log log = LogFactory.getLog(AjaxAddSprintTaskAction.class);
	
	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessSprintBacklog();
	}

	@Override
	public boolean isXML() {
		// HTML
		return false;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		// get session info
		ProjectObject project = SessionManager.getProjectObject(request);
		
		// get parameter info
		long sprintID = Long.parseLong(request.getParameter("sprintID"));
		String[] selectedId = request.getParameterValues("selected");
		long[] tasksId = new long[selectedId.length];
		for( int i = 0; i < selectedId.length; i++ ){
			tasksId[i] = Long.parseLong(selectedId[i]);
		}
		
		SprintBacklogMapper backlog = (new SprintBacklogLogic(project, sprintID)).getSprintBacklogMapper();
		backlog.deleteExistingTask(tasksId);
		return new StringBuilder("");
	}
}
