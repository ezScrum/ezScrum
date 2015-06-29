package ntut.csie.ezScrum.web.action.plan;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class RemoveSprintPlanOfReleasePlanAction extends Action {
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		IProject iProject = (IProject) request.getSession().getAttribute("Project");
		ProjectObject project = new ProjectObject(iProject.getName());
		String releaseId = request.getParameter("ReleaseId");
		String sprintId = request.getParameter("SprintId");

		String result = "";
		try {
			if (releaseId != null && sprintId != null) {
				SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, Long.parseLong(sprintId));
				ArrayList<StoryObject> stories = sprintBacklogLogic.getStories();
				ProductBacklogHelper PBHelper = new ProductBacklogHelper(project);

				for (StoryObject story : stories) {
					PBHelper.dropStoryFromSprint(story.getId());
				}
				result = "<DropSprint><Result>true</Result><Sprint><Id>" + sprintId + "</Id></Sprint></DropSprint>";
			} else {
				result = "<DropSprint><Result>false</Result></DropSprint>";
			}
		} catch (Exception e) {
			result = "<DropSprint><Result>false</Result></DropSprint>";
		}

		try {
			response.setContentType("text/xml; charset=utf-8");
			response.getWriter().write(result);
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}