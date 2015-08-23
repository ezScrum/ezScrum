package ntut.csie.ezScrum.web.action.plan;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.ReleaseObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class RemoveReleasePlanAction extends PermissionAction {
	private static Log log = LogFactory.getLog(RemoveReleasePlanAction.class);

	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessReleasePlan();
	}

	@Override
	public boolean isXML() {
		// html
		return false;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		log.info(" Remove Release Plan. ");

		// get session info
		ProjectObject project = SessionManager.getProjectObject(request);

		// get parameter info
		String releaseIdString = request.getParameter("releaseID");
		long releaseId = Long.parseLong(releaseIdString);
		ReleasePlanHelper releasePlanHelper = new ReleasePlanHelper(project);
		ReleaseObject release = releasePlanHelper.getReleasePlan(releaseId);
		ProductBacklogHelper productBacklogHelper = new ProductBacklogHelper(project);
		SprintPlanHelper sprintPlanHelper = new SprintPlanHelper(project);

		if (release == null) {
			return new StringBuilder("false");
		} else {
			ArrayList<StoryObject> stories = productBacklogHelper
					.getStoriesByRelease(release);
			ArrayList<SprintObject> sprints = release.getSprints();

			for (SprintObject sprint : sprints) {
				sprintPlanHelper.deleteSprint(sprint.getId());
			}

			// 移除 sprint 與底下 Story 的關係
			for (int index = 0; index < stories.size(); index++) {
				if (stories.get(index).getSprintId() > 0) {
					productBacklogHelper.dropStoryFromSprint(stories.get(index).getId());
				}
			}
			// 刪除Release
			releasePlanHelper.deleteReleasePlan(releaseId);

			return new StringBuilder("true");
		}
	}
}
