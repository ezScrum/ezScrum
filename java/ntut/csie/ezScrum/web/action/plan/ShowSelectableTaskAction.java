package ntut.csie.ezScrum.web.action.plan;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

public class ShowSelectableTaskAction extends PermissionAction{
	
	@Override
	public boolean isValidAction() {
		return (super.getScrumRole().getAccessSprintBacklog());
	}

	@Override
	public boolean isXML() {
		// XML
		return true;
	}
	
	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		System.out.println("");
		System.out.println("********Tasks******************");
		System.out.println("");
		// get session info
		ProjectObject project = SessionManager.getProject(request);
		
		// get parameter info
		String serialSprintIdString = request.getParameter("sprintID");
		long serialSprintId;
		
		try{
			serialSprintId = Long.parseLong(serialSprintIdString);
		} catch(NumberFormatException e){
			serialSprintId = -1;
		}
		
		SprintObject sprint = SprintObject.get(project.getId(), serialSprintId);
		long sprintId = -1;
		if (sprint != null) {
			sprintId = sprint.getId();
		} else {
			return new StringBuilder("");
		}
		// Select from Sprint Backlog
		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(project, sprintId);
    	ArrayList<TaskObject> tasks = sprintBacklogHelper.getTaskBySprintId(sprintId);
		StringBuilder sb = new StringBuilder(sprintBacklogHelper.getTasksInSprintResponseText(tasks));
		return sb;
	}
}
