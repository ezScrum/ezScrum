package ntut.csie.ezScrum.web.action.backlog;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.iternal.IProjectSummaryEnum;
import ntut.csie.ezScrum.web.logic.ScrumRoleLogic;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.core.util.DateUtil;

public class ShowSprintInformationAction extends Action {
	public ActionForward execute(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) {

		// get session info
		ProjectObject project = SessionManager.getProject(request);
		IUserSession userSession = (IUserSession) request.getSession().getAttribute("UserSession");

		/*
		 * 原因:由於在ShowSprintInformation.jsp 中的 ${Project.projectDesc.displayName },
		 * 其中
		 * 1. Project是對應request中的key of attribute
		 * 2. projectDesc是對應 Project(class) 的 getProjectDesc(method)
		 * 3. displayName對應 IProjectPreference(class) 的 getDisplayName(method)
		 * 目的:解決開不同分頁瀏覽不同專案時，在Sprint backlog點選Sprint Information顯示正確的sprint information.
		 */
		request.setAttribute(IProjectSummaryEnum.PROJECT, project);
 
		// get parameter info
		long serialSprintId = Long.parseLong(request.getParameter("sprintID"));
		SprintObject tempSprint = SprintObject.get(project.getId(), serialSprintId);
		long sprintId = -1;
		if (tempSprint != null) {
			sprintId = tempSprint.getId();
		}
		if (sprintId <= 0) {
			return mapping.findForward("error");
		}
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, sprintId);
		SprintBacklogMapper sprintBacklogMapper = sprintBacklogLogic.getSprintBacklogMapper();
		serialSprintId = sprintBacklogMapper.getSprintId();
		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(project, serialSprintId);
		ArrayList<StoryObject> stories = sprintBacklogHelper.getStoriesSortedByImpInSprint();
		SprintObject sprint = sprintBacklogMapper.getSprint();
		
		request.setAttribute("SprintID", serialSprintId);
		request.setAttribute("Stories", stories);

		request.setAttribute("StoryPoint", (int) sprint.getTotalStoryPoints());

		
		request.setAttribute("SprintPlan", sprint);
		request.setAttribute("Actors", (new ProjectMapper()).getProjectWorkersUsername(project.getId()));
		String sprintPeriod = sprint.getStartDateString() + " to " + sprint.getDueDateString();
		request.setAttribute("SprintPeriod", sprintPeriod);

		AccountObject account = userSession.getAccount();
		ScrumRole scrumRole = new ScrumRoleLogic().getScrumRole(project, account);
		if (scrumRole != null && scrumRole.getAccessSprintBacklog()) {
			return mapping.findForward("success");
		} else {
			return mapping.findForward("GuestOnly");
		}
	}
}
