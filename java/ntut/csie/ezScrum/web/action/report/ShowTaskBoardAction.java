package ntut.csie.ezScrum.web.action.report;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.control.TaskBoard;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.logic.AccountLogic;
import ntut.csie.ezScrum.web.logic.ScrumRoleLogic;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class ShowTaskBoardAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		ProjectObject project = SessionManager.getProjectObject(request);
		IUserSession userSession = (IUserSession) request.getSession().getAttribute("UserSession");

		// get Account, ScrumRole
		AccountObject account = userSession.getAccount();

		ScrumRole scrumRole = new ScrumRoleLogic().getScrumRole(project,
				account);
		AccountLogic accountLogic = new AccountLogic();

		// 檢查帳號不通過，提示錯誤頁面 // 檢查此帳號是否允許操作 action 的權限
		if (!(accountLogic.checkAccount(request) && scrumRole
				.getAccessTaskBoard())) {
			return mapping.findForward("permissionDenied");
		}

		String sprintId = request.getParameter("sprintID");
		String username = "ALL";

		if (request.getParameter("UserID") != null) {
			username = request.getParameter("UserID"); // 設定參數值
		}
		request.setAttribute("User", username);

		SprintPlanHelper sprintPlanHelper = new SprintPlanHelper(project);

		List<ISprintPlanDesc> plans = sprintPlanHelper.loadListPlans();

		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project,
				-1);
		SprintBacklogMapper backlog = sprintBacklogLogic
				.getSprintBacklogMapper();

		// backlog = null 代表取得 sprintBackLog 發生問題，所以進入防錯處理，塞入假資料
		if (backlog != null) {
			IProject iProject = new ProjectMapper().getProjectByID(project.getName());
			List<String> actorList = (new ProjectMapper()).getProjectScrumWorkerList(userSession, iProject);

			actorList.remove(0);
			actorList.add(0, "ALL");

			request.setAttribute("ActorList", actorList);
			request.setAttribute("SprintPlans", plans);

			TaskBoard board = null;
			if (sprintId == null) {
				board = new TaskBoard(sprintBacklogLogic, backlog);
			} else {
				ISprintPlanDesc desc = sprintPlanHelper.loadPlan(sprintId);

				if (!desc.getID().equals("-1")) {
					board = new TaskBoard(sprintBacklogLogic,
							(new SprintBacklogLogic(project, Long
									.parseLong(sprintId)))
									.getSprintBacklogMapper());

					// 判斷名字是不是all,如果不是就處理,是全部都 show
					if (!username.equals("ALL")) {
						board = filterUser(username, board);
					}
				}
			}

			request.setAttribute("TaskBoard", board);
			request.setAttribute("SprintID", board.getSprintId());
		} else {
			List<String> ActorList = new ArrayList<String>();
			request.setAttribute("ActorList", ActorList);
			request.setAttribute("SprintPlans", plans);
			TaskBoard board = null;
			request.setAttribute("TaskBoard", board);
		}

		return mapping.findForward("success");
	}

	private TaskBoard filterUser(String username, TaskBoard board) {
		List<StoryObject> oldStories = board.getStories();
		List<StoryObject> newStories = new ArrayList<StoryObject>();
		ArrayList<TaskObject> newTasks = null;

		for (StoryObject story : oldStories) {
			ArrayList<TaskObject> oldTasks = story.getTasks();
			if (!oldTasks.isEmpty()) {
				newTasks = new ArrayList<TaskObject>();
				for (TaskObject task : oldTasks) {
					if (task.getHandler() != null) {
						if (checkParent(username, task.getPartnersUsername(),
								task.getHandler().getUsername())) {
							newTasks.add(task);
						}
					} else {
						if (checkParent(username, task.getPartnersUsername(),
								"")) {
							newTasks.add(task);
						}
					}
				}
				if (!newTasks.isEmpty()) {
					newStories.add(story);
				}
			}
		}
		board.setStories(newStories);
		return board;
	}

	// 判斷 partner 或是 handler username 有沒有欄位符合 usename, 若有傳回true
	public boolean checkParent(String username, String partners,
			String handlerUsername) {
		String[] parents = partners.split(";");
		for (String partnerUsername : parents) {
			if (username.equals(partnerUsername)) {
				return true;
			}
		}
		if (username.equals(handlerUsername)) {
			return true;
		}
		return false;
	}
}
