package ntut.csie.ezScrum.web.action.backlog;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.control.TaskBoard;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.Translation;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class AjaxGetSprintIndexInfoAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		String sprintID = request.getParameter("sprintID");
		
		String result = "";
		
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, session, sprintID);
		SprintBacklogMapper sprintBacklogMapper = sprintBacklogLogic.getSprintBacklogMapper();
		TaskBoard board = new TaskBoard(sprintBacklogLogic, sprintBacklogMapper);
		
		// 建立 thisSprintStore 的資料
		int currentSprintID = 0;
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
			currentSprintID = sprintBacklogMapper.getSprintId();
			initialPoint = sprintBacklogLogic.getStoryPoint(ScrumEnum.STORY_ISSUE_TYPE);
			currentPoint = sprintBacklogLogic.getUnclosePoint(ScrumEnum.STORY_ISSUE_TYPE);
			initialHours = sprintBacklogLogic.getStoryPoint(ScrumEnum.TASK_ISSUE_TYPE);
			currentHours = sprintBacklogLogic.getUnclosePoint(ScrumEnum.TASK_ISSUE_TYPE);
			
			ReleasePlanHelper rpHelper = new ReleasePlanHelper(project);
			releaseID = Integer.parseInt(rpHelper.getReleaseID(Integer.toString(currentSprintID)));
				
			SprintGoal = sprintBacklogMapper.getSprintGoal();
			
			StoryChartUrl = board.getStoryChartLink();
			TaskChartUrl = board.getTaskChartLink();
			
			if(sprintBacklogMapper.getSprintEndDate().getTime() > (new Date()).getTime())
				isCurrentSprint = true;
		} 
		
		result = Translation.translateSprintInfoToJson(
				currentSprintID, initialPoint, currentPoint, initialHours, currentHours, releaseID, SprintGoal,
				StoryChartUrl, TaskChartUrl,isCurrentSprint);
		try {
			response.setContentType("text/html; charset=utf-8");
			response.getWriter().write(result);
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
