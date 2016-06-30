package ntut.csie.ezScrum.web.action.backlog;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.control.TaskBoard;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.Translation;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.codehaus.jettison.json.JSONException;

public class AjaxGetSprintIndexInfoAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		ProjectObject project = SessionManager.getProject(request);
		String result = "";
		String sprintIdString = request.getParameter("sprintID");
		long sprintId = -1;
		if (sprintIdString != null && !sprintIdString.isEmpty()) {
			sprintId = Long.parseLong(sprintIdString);
		}
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, sprintId);
		SprintBacklogMapper sprintBacklogMapper = sprintBacklogLogic.getSprintBacklogMapper();
		TaskBoard board = new TaskBoard(sprintBacklogLogic, sprintBacklogMapper);
		
		// 建立 thisSprintStore 的資料
		long currentSprintId = 0;
		long releaseId = 0;
		double initialPoint = 0.0d;
		double currentPoint = 0.0d;
		double initialHours = 0.0d;
		double currentHours = 0.0d;
		String SprintGoal = "";
		String StoryChartUrl = "";
		String TaskChartUrl = "";
		boolean isCurrentSprint=false;
		
		//如果Sprint存在的話，那麼就取出此Sprint的資料以回傳
		if ( (sprintBacklogMapper != null) && (sprintBacklogMapper.getSprintId() > 0) ) {
			currentSprintId = sprintBacklogMapper.getSprintId();
			SprintObject sprint = sprintBacklogMapper.getSprint();
			initialPoint = sprint.getTotalStoryPoints();
			currentPoint = sprint.getStoryUnclosedPoints();
			initialHours = sprint.getTotalTaskPoints();
			currentHours = sprint.getTaskRemainsPoints();
			
			ReleasePlanHelper releasePlanHelper = new ReleasePlanHelper(project);
			releaseId = releasePlanHelper.getReleaseIdBySprintId(currentSprintId);
				
			SprintGoal = sprintBacklogMapper.getSprintGoal();
			
			StoryChartUrl = board.getStoryChartLink();
			TaskChartUrl = board.getTaskChartLink();
			
			if(sprintBacklogMapper.getSprintEndDate().getTime() > (new Date()).getTime())
				isCurrentSprint = true;
		} 
		try {
			result = Translation.translateSprintInfoToJson(
					currentSprintId, initialPoint, currentPoint, initialHours, currentHours, releaseId, SprintGoal,
					StoryChartUrl, TaskChartUrl, isCurrentSprint);
			
			response.setContentType("text/html; charset=utf-8");
			response.getWriter().write(result);
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		return null;
	}
}
