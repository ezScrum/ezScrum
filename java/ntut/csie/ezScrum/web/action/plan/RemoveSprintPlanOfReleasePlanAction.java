package ntut.csie.ezScrum.web.action.plan;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class RemoveSprintPlanOfReleasePlanAction extends Action {
	public ActionForward execute(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) {
		IProject project = (IProject) request.getSession().getAttribute("Project");
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		String releaseId = request.getParameter("ReleaseId");
		String sprintId = request.getParameter("SprintId");

		String result = "";
		try {
			if (releaseId != null && sprintId != null) {
				SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, session, sprintId);
				List<IIssue> stories = sprintBacklogLogic.getStories();
				ProductBacklogHelper PBHelper = new ProductBacklogHelper(session, project);

				for (IIssue story : stories) {
					PBHelper.removeReleaseTagFromIssue(story.getIssueID());
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