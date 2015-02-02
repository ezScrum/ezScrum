package ntut.csie.ezScrum.web.action.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.google.gson.Gson;
import com.sshtools.common.ui.SessionManager;

public class GetIssueStatusByIssueIDAction extends PermissionAction {
	private static Log log = LogFactory
			.getLog(GetIssueStatusByIssueIDAction.class);

	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessTaskBoard();
	}

	@Override
	public boolean isXML() {
		// html
		return false;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		String issueStage = null;
		IProject project = (IProject) request.getSession().getAttribute(
				"Project");
		IUserSession userSession = (IUserSession) request.getSession()
				.getAttribute("UserSession");

		String sprintID = request.getParameter("sprintID");// sprintID
		int sprintIDInt = Integer.parseInt(sprintID);

		long issueID = Long.parseLong(request.getParameter("issueID"));// issueID

		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project,
				userSession, sprintID);
		SprintBacklogMapper sprintBacklogMapper = sprintBacklogLogic
				.getSprintBacklogMapper();
		try {
			// retrieve stories
			List<IIssue> stories = sprintBacklogLogic.getStoriesByImp();
			// retrieve storyID task map
			Map<Long, ArrayList<TaskObject>> taskMap = sprintBacklogMapper
					.getTasksMap();

			SprintPlanHelper sph = new SprintPlanHelper(project);
			// get taskBoardStageMap to query status name
			Map<Integer, String> taskBoardStageMap = sph.loadPlan(sprintIDInt)
					.getTaskBoardStageMap();

			// traverse every task in every story to find issueID, not efficient
			// but worked now.
			for (IIssue story : stories) {
				// every story is a root
				if (story.getIssueID() == issueID) {
					// story query map to get status name by status id(called
					// value)
					String storyStage = taskBoardStageMap.get(story
							.getStatusValue());
					issueStage = storyStage;
					break;
				} else { // every task is a story child
					ArrayList<TaskObject> tasks = taskMap.get(story
							.getIssueID());
					for (TaskObject task : tasks) {
						if (task.getId() == issueID) {
							String taskStage = task.getStatusString();
							issueStage = taskStage;
							break;
						}
					}
				}
			}

			Gson gson = new Gson();

			issueStage = gson.toJson(issueStage);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}

		return new StringBuilder(issueStage);// to fit return type;
	}
}
