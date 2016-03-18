package ntut.csie.ezScrum.web.action.plan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.ReleaseObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.logic.ScrumRoleLogic;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.support.SessionManager;

public class ShowPrintableReleaseAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		HashMap<Long, Integer> totalStoryPoints;
		HashMap<String, ArrayList<StoryObject>> storiesMap;
		LinkedHashMap<Long, ArrayList<TaskObject>> TaskMap;
		
		// get session info
		ProjectObject project = SessionManager.getProject(request);
    	IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
    	
    	// get parameter info
    	String serialReleaseIdString = request.getParameter("releaseID");
    	String showTask = request.getParameter("showtask");
    	long serialReleaseId = Long.parseLong(serialReleaseIdString);
    	boolean printTask = false;
    	if (showTask != null) {
    		if (showTask.equals("true")) {
    			printTask = true;
    		}
    	}
    	
    	//get release information
    	ReleaseObject release = ReleaseObject.get(project.getId(), serialReleaseId);
    	//initial data
    	storiesMap = new HashMap<String, ArrayList<StoryObject>>();
    	totalStoryPoints = new HashMap<Long, Integer>();
    	TaskMap = new LinkedHashMap<Long, ArrayList<TaskObject>>();
    	//get sprints information of the release(release id) 
    	
    	try {
	    	ArrayList<SprintObject> sprints = release.getSprints();
	    	if(sprints != null) {
	    		for (SprintObject sprint : sprints) {
	    			long serialSprintId = sprint.getSerialId();
		    		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, sprint.getId());
		    		ArrayList<StoryObject> stories = sprintBacklogLogic.getStoriesSortedByImpInSprint();
		    		storiesMap.put(String.valueOf(serialSprintId), stories);
	    		
		    		//the sum of story points
		    		int total = 0;
					for (StoryObject story : stories) {
						int estimate = story.getEstimate();
						total += estimate;
					}
					totalStoryPoints.put(serialSprintId, total);
		    		
					// print task information
					if (printTask) {
						for (StoryObject story : stories) {
							TaskMap.put(story.getSerialId(), story.getTasks());
						}
					}
				}
	    	}
	    	
	    	//set attribute in request
	    	request.setAttribute("release", release);
			request.setAttribute("sprints", sprints);
			request.setAttribute("stories", storiesMap);
			request.setAttribute("TaskMap", TaskMap);
			request.setAttribute("totalStoryPoints", totalStoryPoints);
			
			ScrumRole scrumRole = new ScrumRoleLogic().getScrumRole(project, session.getAccount());
			if (scrumRole.getAccessReleasePlan()) {
				return mapping.findForward("success");
			} else {
				return mapping.findForward("GuestOnly");
			}
    	} catch (Exception e) {
    		return mapping.findForward("GuestOnly");
    	}
	}
}