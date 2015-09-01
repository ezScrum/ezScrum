package ntut.csie.ezScrum.web.action.plan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.ReleaseObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.ezScrum.web.logic.ScrumRoleLogic;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class ShowPrintableReleaseAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		HashMap<String, Float> tatolStoryPoints;
		HashMap<String, ArrayList<StoryObject>> storiesMap;
		LinkedHashMap<Long, ArrayList<TaskObject>> TaskMap;
		
		// get session info
		ProjectObject project = SessionManager.getProjectObject(request);
    	IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
    	
    	// get parameter info
    	String releaseIdString = request.getParameter("releaseID");
    	String showTask = request.getParameter("showtask");
    	long releaseId = Long.parseLong(releaseIdString);
    	boolean printTask = false;
    	if (showTask != null) {
    		if (showTask.equals("true")) {
    			printTask = true;
    		}
    	}
    	
    	//get release information
    	ReleasePlanHelper releasePlanHelper = new ReleasePlanHelper(project);
    	ReleaseObject release = releasePlanHelper.getReleasePlan(releaseId);		
    	//initial data
    	storiesMap = new HashMap<String, ArrayList<StoryObject>>();
    	tatolStoryPoints = new HashMap<String, Float>();
    	TaskMap = new LinkedHashMap<Long, ArrayList<TaskObject>>();
    	//get sprints information of the release(release id) 
    	
    	try {
	    	ArrayList<SprintObject> sprints = release.getSprints();
	    	if(sprints != null) {
	    		for (SprintObject sprint : sprints) {
	    			String sprintId = String.valueOf(sprint.getId());
		    		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, Long.parseLong(sprintId));
		    		ArrayList<StoryObject> stories = sprintBacklogLogic.getStoriesSortedByImpInSprint();
		    		storiesMap.put(sprintId, stories);
	    		
		    		//the sum of story points
					float total = 0;
					for (StoryObject story : stories) {
						int est = story.getEstimate();
						total = total + est;
					}
		    		tatolStoryPoints.put(sprintId, total);
		    		
					// print task information
					if (printTask) {
						for (StoryObject story : stories) {
							TaskMap.put(story.getId(), story.getTasks());
						}
					}
				}
	    	}
	    	
	    	//set attribute in request
	    	request.setAttribute("release", release);
			request.setAttribute("sprints", sprints);
			request.setAttribute("stories", storiesMap);
			request.setAttribute("TaskMap", TaskMap);
			request.setAttribute("tatolStoryPoints", tatolStoryPoints);
			
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