package ntut.csie.ezScrum.web.action.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.control.TaskBoard;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
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
		IProject project = SessionManager.getProject(request);
		IUserSession userSession = (IUserSession) request.getSession().getAttribute("UserSession");

		// get Account, ScrumRole
		AccountObject account = userSession.getAccount();
		
		ScrumRole sr = new ScrumRoleLogic().getScrumRole(project, account);
		AccountLogic accountLogic = new AccountLogic();
		
		// 檢查帳號不通過，提示錯誤頁面		    // 檢查此帳號是否允許操作  action 的權限
		if (!( accountLogic.checkAccount(request) && sr.getAccessTaskBoard() )) {
			return mapping.findForward("permissionDenied");
		}
		
		String sprintID = request.getParameter("sprintID");
		String name = "ALL";
		
		if (request.getParameter("UserID") != null)		name = request.getParameter("UserID");	// 設定參數值
		request.setAttribute("User", name);
		
		SprintPlanHelper spHelper = new SprintPlanHelper(project);

		List<ISprintPlanDesc> plans = spHelper.loadListPlans();
		
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, userSession, null);
		SprintBacklogMapper backlog = sprintBacklogLogic.getSprintBacklogMapper();
		
		// backlog = null 代表取得 sprintBackLog 發生問題，所以進入防錯處理，塞入假資料
		if (backlog != null) {
			List<String> actorList = (new ProjectMapper()).getProjectScrumWorkerList(userSession, project);
			
			actorList.remove(0);
			actorList.add(0, "ALL");
			
			request.setAttribute("ActorList", actorList);
			request.setAttribute("SprintPlans", plans);

			TaskBoard board = null;
			if (sprintID == null) {
				board = new TaskBoard(sprintBacklogLogic, backlog);
			} else {
				ISprintPlanDesc desc = spHelper.loadPlan(sprintID);

				if ( ! desc.getID().equals("-1")) {
					board = new TaskBoard(sprintBacklogLogic, (new SprintBacklogLogic(project, userSession, sprintID)).getSprintBacklogMapper());
					
					// 判斷名字是不是all,如果不是就處理,是全部都 show
					if (name.compareTo("ALL") != 0)		board = filterUser(name, board);
				}
			}

			request.setAttribute("TaskBoard", board);
			request.setAttribute("SprintID", board.getSprintID());
		} else {
			List<String> ActorList = new ArrayList<String>();
			request.setAttribute("ActorList", ActorList);
			request.setAttribute("SprintPlans", plans);
			TaskBoard board = null;
			request.setAttribute("TaskBoard", board);
		}
		
		return mapping.findForward("success");
	}

	private TaskBoard filterUser(String name, TaskBoard board) {
		List<IIssue> storyarray = board.getStories();
		List<IIssue> Storylist = new ArrayList<IIssue>();

		Map<Long, ArrayList<TaskObject>> taskMap = null;
		ArrayList<TaskObject> tasklist = null;

		for (IIssue story : storyarray) {
			taskMap = board.getTaskMap();
			ArrayList<TaskObject> taskarray = taskMap.get(story.getIssueID());
			if (taskarray != null) {
				tasklist = new ArrayList<TaskObject>();
				for (TaskObject task : taskarray) {
					if(task.getHandler() != null){
						if (checkParent(name, task.getPartnersUsername(), task.getHandler().getUsername()))
							tasklist.add(task);
					} else {
						if (checkParent(name, task.getPartnersUsername(), ""))
							tasklist.add(task);
					}
				}
				taskMap.put(story.getIssueID(), tasklist);
				if (tasklist.size() != 0) {
					Storylist.add(story);
				}
			}
		}
		board.setM_taskMap(taskMap);
		board.setM_stories(Storylist);
		return board;
	}

	public boolean checkParent(String name, String partners, String assignto)// 判斷partner或是assignto有沒有欄位符合usename,若有傳回true
	{
		String[] parents = partners.split(";");
		for (String p : parents) {
			if (name.compareTo(p) == 0)
				return true;
		}
		if (name.compareTo(assignto) == 0)
			return true;
		return false;
	}
}
