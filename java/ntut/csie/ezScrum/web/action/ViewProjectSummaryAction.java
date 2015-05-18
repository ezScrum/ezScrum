package ntut.csie.ezScrum.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.control.TaskBoard;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.ProjectHelper;
import ntut.csie.ezScrum.web.logic.ScrumRoleLogic;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class ViewProjectSummaryAction extends Action {
	
	private static Log log = LogFactory.getLog(ViewProjectSummaryAction.class);
	private SessionManager mProjectSessionManager = null;

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	        HttpServletResponse response) {
		
		HttpSession session = request.getSession();
		IUserSession userSession = (IUserSession) request.getSession().getAttribute("UserSession");
		ProjectObject project = SessionManager.getProjectObject(request);
		ProjectHelper projectHelper = new ProjectHelper();
		String projectName = request.getParameter("PID");
		log.debug("Parameter=" + projectName);

		// 比對 project name 是否存在
		if (!projectHelper.isProjectExisted(projectName)) {
			return mapping.findForward("error");
		}

		// 判斷 session 中專案是否為空，空的話則建立新的專案於 session 中
		if (project == null) {
			project = projectHelper.getProjectByName(projectName);
			session.setAttribute(projectName, project);
		}

		// 判斷該使用者是否存在於專案中
		if (!projectHelper.isUserExistInProject(project, userSession)) {
			session.removeAttribute(projectName);
			return mapping.findForward("permissionDenied");
		}

		mProjectSessionManager = new SessionManager(request);
		request.setAttribute("projectObject", project);

		// 更新 session 中的資料
		mProjectSessionManager.setProjectObject(request, project);

		// 取得 TaskBoard 資訊
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, -1);
		SprintBacklogMapper sprintBacklogMapper = sprintBacklogLogic.getSprintBacklogMapper();

		TaskBoard board = null;
		if (sprintBacklogMapper != null) {
			board = new TaskBoard(sprintBacklogLogic, sprintBacklogMapper);
			request.setAttribute("TaskBoard", board);
			request.setAttribute("SprintID", board.getSprintId());
		} else {
			request.setAttribute("TaskBoard", board);
			request.setAttribute("SprintID", "null");
		}

		// ezScrum v1.8
		AccountObject account = userSession.getAccount();
		ScrumRole scrumRole = new ScrumRoleLogic().getScrumRole(project, account);

		if (scrumRole != null && scrumRole.isGuest()) {
			request.getSession().setAttribute("isGuest", "true");
			log.info(account.getUsername() + " is a guest, view project: " + project.getName());
			return mapping.findForward("GuestOnly");
		} else {
			request.getSession().setAttribute("isGuest", "false");
			log.info(account.getUsername() + " is not a guest, view project: " + project.getName());
		}

		return mapping.findForward("SummaryView");
	}
}
