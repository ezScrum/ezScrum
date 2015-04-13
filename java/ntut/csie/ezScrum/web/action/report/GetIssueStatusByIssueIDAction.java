package ntut.csie.ezScrum.web.action.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.google.gson.Gson;

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
		ProjectObject project = SessionManager.getProjectObject(request);

		String sprintId = request.getParameter("sprintID");// sprintID
		long sprintIdLong = Long.parseLong(sprintId);
		int sprintIdInt = Integer.parseInt(sprintId);

		long issueID = Long.parseLong(request.getParameter("issueID"));// issueID

		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, sprintIdLong);
		try {
			// retrieve stories
			List<StoryObject> stories = sprintBacklogLogic.getStoriesByImp();

			SprintPlanHelper sph = new SprintPlanHelper(project);
			// get taskBoardStageMap to query status name
			Map<Integer, String> taskBoardStageMap = sph.loadPlan(sprintIdInt)
					.getTaskBoardStageMap();

			// traverse every task in every story to find issueID, not efficient
			// but worked now.
			for (StoryObject story : stories) {
				// every story is a root
				if (story.getId() == issueID) {
					// story query map to get status name by status id(called
					// value)
					String storyStage = taskBoardStageMap.get(story.getStatusString());
					issueStage = storyStage;
					break;
				} else { // every task is a story child
					ArrayList<TaskObject> tasks = story.getTasks();
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
