package ntut.csie.ezScrum.web.action.backlog;

import java.text.NumberFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.iternal.IProjectSummaryEnum;
import ntut.csie.ezScrum.web.logic.ScrumRoleLogic;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.account.core.IAccount;
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
		IProject project = (IProject) SessionManager.getProject(request);
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
		String sprintID = request.getParameter("sprintID");
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, userSession, sprintID);
		SprintBacklogMapper backlog = sprintBacklogLogic.getSprintBacklogMapper();
		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(project, userSession);
		if (backlog == null) {
			return mapping.findForward("error");
		}
		
		List<IIssue> issues = sprintBacklogHelper.getStoriesByImportance();
		
		request.setAttribute("SprintID", backlog.getSprintPlanId());
		request.setAttribute("Stories", issues);

		NumberFormat nf = NumberFormat.getInstance();
		request.setAttribute("StoryPoint", nf.format(sprintBacklogLogic.getCurrentPoint(ScrumEnum.STORY_ISSUE_TYPE)));

		SprintPlanHelper spHelper = new SprintPlanHelper(project);
		ISprintPlanDesc plan = spHelper.loadPlan(backlog.getSprintPlanId());
		request.setAttribute("SprintPlan", plan);
		request.setAttribute("Actors", (new ProjectMapper()).getProjectScrumWorkerList(userSession, project));
		String sprintPeriod = DateUtil.format(sprintBacklogLogic.getSprintStartWorkDate(),
		        DateUtil._8DIGIT_DATE_1)
		        + " to "
		        + DateUtil.format(sprintBacklogLogic.getSprintEndWorkDate(), DateUtil._8DIGIT_DATE_1);

		request.setAttribute("SprintPeriod", sprintPeriod);

		IAccount acc = userSession.getAccount();
		ScrumRole sr = new ScrumRoleLogic().getScrumRole(project, acc);
		if (sr.getAccessSprintBacklog()) {
			return mapping.findForward("success");
		} else {
			return mapping.findForward("GuestOnly");
		}
	}
}
