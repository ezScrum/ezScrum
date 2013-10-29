package ntut.csie.ezScrum.web.action.plan;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.ezScrum.web.logic.ProductBacklogLogic;
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
		String Release_id = request.getParameter("ReleaseId");
		String sprintID = request.getParameter("SprintId");

		String result = "";	
		try{
			if (Release_id != null && sprintID !=null) {
				ReleasePlanHelper helper = new ReleasePlanHelper(project);
				
				//從Release中將Sprint移除
//				helper.deleteSpritnOfRelease(Release_id, Sprint_id);		// delete sprint plan of the release plan
				
				//將Sprint底下的Story也自此Release移除
//				SprintBacklogMapper sprintBacklog = new SprintBacklogMapper(project,session,Integer.parseInt(Sprint_id));
//				ProductBacklogLogic productBacklogLogic = new ProductBacklogLogic(session,project);
//				List<IIssue> stories =sprintBacklog.getStories();
				SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project,session,sprintID);
				List<IIssue> stories = sprintBacklogLogic.getStories();
				ProductBacklogLogic productBacklogLogic = new ProductBacklogLogic(session,project);
				for(IIssue story:stories){
					productBacklogLogic.removeReleaseTagFromIssue(story.getIssueID()+"");
				}
//				ProductBacklogHelper productBacklog = new ProductBacklogHelper(project,session);
//				IIssue[] stories =sprintBacklog.getStories();
//				for(IIssue story:stories)
//				{
//					productBacklog.removeRelease(story.getIssueID()+"");
//				}
				
				result = "<DropSprint><Result>true</Result><Sprint><Id>" + sprintID + "</Id></Sprint></DropSprint>";
			}else {
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}