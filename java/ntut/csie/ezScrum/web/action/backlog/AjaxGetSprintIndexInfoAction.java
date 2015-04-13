package ntut.csie.ezScrum.web.action.backlog;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.control.TaskBoard;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
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
		
		ProjectObject project = SessionManager.getProjectObject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		long sprintId = Long.parseLong(request.getParameter("sprintID"));
		
		String result = "";
		
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, sprintId);
		SprintBacklogMapper sprintBacklogMapper = sprintBacklogLogic.getSprintBacklogMapper();
		TaskBoard board = new TaskBoard(sprintBacklogLogic, sprintBacklogMapper);
		
		// 建立 thisSprintStore 的資料
		long currentSprintId = 0;
		int releaseID = 0;
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
			initialPoint = sprintBacklogLogic.getTotalStoryPoints();
			currentPoint = sprintBacklogLogic.getStoryUnclosedPoints();
			initialHours = sprintBacklogLogic.getTaskEstimatePoints();
			currentHours = sprintBacklogLogic.getTaskRemainsPoints();
			
			ReleasePlanHelper rpHelper = new ReleasePlanHelper(project);
			releaseID = Integer.parseInt(rpHelper.getReleaseID(currentSprintId));
				
			SprintGoal = sprintBacklogMapper.getSprintGoal();
			
			StoryChartUrl = board.getStoryChartLink();
			TaskChartUrl = board.getTaskChartLink();
			
			if(sprintBacklogMapper.getSprintEndDate().getTime() > (new Date()).getTime())
				isCurrentSprint = true;
		} 
		try {
			result = Translation.translateSprintInfoToJson(
					currentSprintId, initialPoint, currentPoint, initialHours, currentHours, releaseID, SprintGoal,
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
