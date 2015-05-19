package ntut.csie.ezScrum.web.action.backlog;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.iternal.IProjectSummaryEnum;
import ntut.csie.ezScrum.web.logic.ScrumRoleLogic;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class ShowSprintInformationAction extends Action {
	public ActionForward execute(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) {

		// get session info
		ProjectObject project = SessionManager.getProjectObject(request);
		IProject iProject = new ProjectMapper().getProjectByID(project.getName());
		IUserSession userSession = (IUserSession) request.getSession().getAttribute("UserSession");

		/*
		 * 原因:由於在ShowSprintInformation.jsp 中的 ${Project.projectDesc.displayName },
		 * 其中
		 * 1. Project是對應request中的key of attribute
		 * 2. projectDesc是對應 Project(class) 的 getProjectDesc(method)
		 * 3. displayName對應 IProjectPreference(class) 的 getDisplayName(method)
		 * 目的:解決開不同分頁瀏覽不同專案時，在Sprint backlog點選Sprint Information顯示正確的sprint information.
		 */
		request.setAttribute(IProjectSummaryEnum.PROJECT, iProject);

		// get parameter info
		long sprintId = Long.parseLong(request.getParameter("sprintID"));
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, sprintId);
		SprintBacklogMapper sprintBacklogMapper = sprintBacklogLogic.getSprintBacklogMapper();
		if (sprintBacklogMapper == null) {
			return mapping.findForward("error");
		}
		sprintId = sprintBacklogMapper.getSprintId();
		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(project, sprintId);
		
		ArrayList<StoryObject> stories = sprintBacklogHelper.getStoriesByImportance();
		
		request.setAttribute("SprintID", sprintBacklogMapper.getSprintId());
		request.setAttribute("Stories", stories);

		request.setAttribute("StoryPoint", sprintBacklogLogic.getTotalStoryPoints());

		SprintPlanHelper spHelper = new SprintPlanHelper(project);
		ISprintPlanDesc plan = spHelper.loadPlan(String.valueOf(sprintBacklogMapper.getSprintId()));
		request.setAttribute("SprintPlan", plan);
		request.setAttribute("Actors", (new ProjectMapper()).getProjectWorkersUsername(project.getId()));
		String sprintPeriod = DateUtil.format(sprintBacklogLogic.getSprintStartWorkDate(),
		        DateUtil._8DIGIT_DATE_1)
		        + " to "
		        + DateUtil.format(sprintBacklogLogic.getSprintEndWorkDate(), DateUtil._8DIGIT_DATE_1);

		request.setAttribute("SprintPeriod", sprintPeriod);

		AccountObject account = userSession.getAccount();
		ScrumRole sr = new ScrumRoleLogic().getScrumRole(project, account);
		if (sr != null && sr.getAccessSprintBacklog()) {
			return mapping.findForward("success");
		} else {
			return mapping.findForward("GuestOnly");
		}
	}
}
