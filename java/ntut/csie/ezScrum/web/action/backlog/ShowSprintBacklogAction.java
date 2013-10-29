package ntut.csie.ezScrum.web.action.backlog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class ShowSprintBacklogAction extends PermissionAction {
	private static Log log = LogFactory.getLog(ShowSprintBacklogAction.class);

	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessSprintBacklog();
	}

	@Override
	public boolean isXML() {
		return false;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		log.info("Show Sprint Backlog in ShowSprintBacklogAction.");
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession userSession = (IUserSession) request.getSession().getAttribute("UserSession");

		String sprintID = request.getParameter("sprintID");
		StringBuilder result = new StringBuilder(new SprintBacklogHelper(project, userSession, sprintID).getShowSprintBacklogText());
		return result;
		
//		SprintBacklogMapper sprintBacklogMapper = (new SprintBacklogLogic(project, userSession)).getSprintBacklogMapper(sprintID);
//
//		// 建立 thisSprintStore 的資料
//		List<IIssue> issues = null;
//		int currentSprintID = 0;
//		int releaseID = 0;
//		double currentPoint = 0.0d;
//		double limitedPoint = 0.0d;
//		double taskPoint = 0.0d;
//		String SprintGoal = "";
//		if ( (sprintBacklogMapper != null) && (sprintBacklogMapper.getSprintPlanId() > 0) ) {
//			// 存在一 current sprint
//			issues = sprintBacklogMapper.getStoriesByImp();
//			currentSprintID = sprintBacklogMapper.getSprintPlanId();
//			currentPoint = sprintBacklogMapper.getCurrentPoint(ScrumEnum.STORY_ISSUE_TYPE);
//			limitedPoint = sprintBacklogMapper.getLimitedPoint();
//			taskPoint = sprintBacklogMapper.getCurrentPoint(ScrumEnum.TASK_ISSUE_TYPE);
//				
//			ReleasePlanHelper rpHelper = new ReleasePlanHelper(project);
//			releaseID = Integer.parseInt(rpHelper.getReleaseID(Integer.toString(currentSprintID)));
//				
//			SprintGoal = sprintBacklogMapper.getSprintGoal();
//				
//			result = new Translation().translateStoryToJson(issues, currentSprintID, currentPoint, limitedPoint, taskPoint, releaseID, SprintGoal);
//		} else {
////			issues = new IIssue[0];
//			issues = new ArrayList<IIssue>();
//			result = new Translation().translateStoryToJson(issues, currentSprintID, currentPoint, limitedPoint, taskPoint, releaseID, SprintGoal);
//		}
//		
//		return (new StringBuilder()).append(result);
//		return null;
	}

//	public ActionForward execute(ActionMapping mapping, ActionForm form,
//			HttpServletRequest request, HttpServletResponse response) {
//		log.info("Show Sprint Backlog in ShowSprintBacklogAction.");
//		IProject project = (IProject) SessionManager.getProject(request);
//		IUserSession userSession = (IUserSession) request.getSession().getAttribute("UserSession");
//
//		// get Account, ScrumRole
//		IAccount acc = userSession.getAccount();
//		ScrumRole sr = SessionManager.getScrumRole(request, project, acc);
////		MantisAccountMapper accountHelper = new MantisAccountMapper( project, session);
////		
////		// 檢查帳號不通過，提示錯誤頁面		    // 檢查此帳號是否允許操作  action 的權限
////		if (!( accountHelper.CheckAccount(request) && sr.getAccessSprintBacklog() )) {
////			return mapping.findForward("permissionDenied");
////		}
//		
//		AccountLogic accountLogic = new AccountLogic();
//		
//		// 檢查帳號不通過，提示錯誤頁面		    // 檢查此帳號是否允許操作  action 的權限
//		if (!( accountLogic.checkAccount(request) && sr.getAccessSprintBacklog() )) {
//			return mapping.findForward("permissionDenied");
//		}
//
////		ITSPrefsStorage prefs = new ITSPrefsStorage(project, userSession);
////		String mantisUrl = prefs.getServerUrl();
//		String result = "";
//		String sprintID = request.getParameter("sprintID");
//		SprintBacklog backlog = null;
//		
//		try {
//			// sprint 不存在，回傳最近的一個 sprint 或 空的 sprint
//			if (sprintID == null || sprintID.equals("")) {
//				backlog = new SprintBacklog(project, userSession);
//			} else {
//				backlog = new SprintBacklog(project, userSession, Integer.parseInt(sprintID));
//			}
//		} catch (Exception e) {
//			backlog = null;
//			// 已經處理過不必輸出 Exception
//			//log.info("class: ShowSprintBacklogAction, method: execute, exception: " + e.toString());
//		}
//
//		// 建立 thisSprintStore 的資料
//		IIssue[] issues = null;
//		int currentSprintID = 0;
//		int releaseID = 0;
//		double currentPoint = 0.0d;
//		double limitedPoint = 0.0d;
//		double taskPoint = 0.0d;
//		String SprintGoal = "";
//		if ( (backlog != null) && (backlog.getIteration() > 0) ) {
//			// 存在一 current sprint
//			issues = backlog.getStoriesByImp();
//			currentSprintID = backlog.getIteration();
//			currentPoint = backlog.getCurrentPoint(ScrumEnum.STORY_ISSUE_TYPE);
//			limitedPoint = backlog.getLimitedPoint();
//			taskPoint = backlog.getCurrentPoint(ScrumEnum.TASK_ISSUE_TYPE);
//				
//			ReleasePlanHelper rpHelper = new ReleasePlanHelper(project);
//			releaseID = Integer.parseInt(rpHelper.getReleaseID(Integer.toString(currentSprintID)));
//				
//			SprintGoal = backlog.getSprintGoal();
//				
////			result = new Translation(mantisUrl).translateStoryToJson(
////						issues, currentSprintID, currentPoint,
////						limitedPoint, taskPoint, releaseID, SprintGoal);
//			result = new Translation().translateStoryToJson(
//					issues, currentSprintID, currentPoint,
//					limitedPoint, taskPoint, releaseID, SprintGoal);
//		} else {
//			issues = new IIssue[0];
////			result = new Translation(mantisUrl).translateStoryToJson(
////					issues, currentSprintID, currentPoint,
////					limitedPoint, taskPoint, releaseID, SprintGoal);
//			result = new Translation().translateStoryToJson(
//					issues, currentSprintID, currentPoint,
//					limitedPoint, taskPoint, releaseID, SprintGoal);
//		}
//		
//		response.setContentType("text/html; charset=utf-8");
//		try {
//			response.getWriter().write(result);
//			LogFactory.getLog(SecurityRequestProcessor.class).debug("Current Time : " + new Date().toString());
//			response.getWriter().close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
}