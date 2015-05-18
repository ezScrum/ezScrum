package ntut.csie.ezScrum.web.action.report;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.control.TaskBoard;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.ezScrum.web.support.SessionManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import com.google.gson.Gson;

public class AjaxGetTaskBoardDescriptionAction extends PermissionAction {
	private static Log log = LogFactory.getLog(AjaxGetTaskBoardDescriptionAction.class);

	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessTaskBoard();
	}

	@Override
	public boolean isXML() {
		// html
		return false;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		log.info(" Get Task Board Description. In Project Summary Page.");
		ProjectObject project = SessionManager.getProjectObject(request);
		
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, -1);
		SprintBacklogMapper sprintBacklogMapper = sprintBacklogLogic.getSprintBacklogMapper();
		
		TaskBoard board = null;
		if (sprintBacklogMapper != null) {
			board = new TaskBoard(sprintBacklogLogic, sprintBacklogMapper);
		}
		TaskBoardUI tbui = new TaskBoardUI(board);
		return new StringBuilder( (new Gson()).toJson(tbui) );
	}
	
	private class TaskBoardUI {
		private String ID = "0";
		private String SprintGoal = "";
		private String Current_Story_Undone_Total_Point = "";
		private String Current_Task_Undone_Total_Point = "";
		
		public TaskBoardUI(TaskBoard tb) {
			if (tb != null) {
				this.SprintGoal = tb.getSprintGoal();
				this.Current_Story_Undone_Total_Point = tb.getStoryPoint();
				this.Current_Task_Undone_Total_Point = tb.getTaskPoint();
			}
		}
	}
}
