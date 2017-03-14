package ntut.csie.ezScrum.web.action.report;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.Translation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class DoneIssueAction extends PermissionAction {
	private static Log log = LogFactory.getLog(DoneIssueAction.class);

	@Override
	public boolean isValidAction() {
		return (super.getScrumRole().getAccessProductBacklog() && (!super.getScrumRole().isGuest()));
	}

	@Override
	public boolean isXML() {
		return false;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) {
		log.info("Done Issue in DoneIssueAction.");

		// get project from session or DB
		ProjectObject project = SessionManager.getProject(request);

		// get parameter info
		long serialId = Long.parseLong(request.getParameter("Id"));
		String name = request.getParameter("Name");
		String notes = request.getParameter("Notes");
		String changeDate = request.getParameter("ChangeDate");
		int actual = Integer.parseInt(request.getParameter("Actualhour"));
		String issueType = request.getParameter("IssueType");
		System.out.println("Id");
		System.out.println(serialId);
		System.out.println("Name");
		System.out.println(name);
		System.out.println("IssueType");
		System.out.println(issueType);
		
		
		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(project);
		StringBuilder result = new StringBuilder("");
		
		if (issueType.equals("Story")) {
			System.out.println("This is Story");
			sprintBacklogHelper.closeStory(serialId, name, notes, changeDate);
			// return done issue 相關相關資訊
			StoryObject story = sprintBacklogHelper.getStory(serialId);
			result.append(Translation.translateTaskboardStoryToJson(story));
		} else if (issueType.equals("Task")) {
			System.out.println("This is Task");
			sprintBacklogHelper.closeTask(project.getId(),serialId, name, notes, actual, changeDate);
			TaskObject task = sprintBacklogHelper.getTask(project.getId(), serialId);
			System.out.println(task.getName());
			System.out.println(task.getSerialId());

			System.out.println(task.getHandlerUsername());
			result.append(Translation.translateTaskboardTaskToJson(task));
		}

		return result;
	}
}
