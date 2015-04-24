package ntut.csie.ezScrum.web.action.plan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
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

public class ShowPrintableReleaseDocxAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		HashMap<String, Float> tatolStoryPoints;
		HashMap<String, ArrayList<StoryObject>> stories;
		LinkedHashMap<Long, ArrayList<TaskObject>> TaskMap;
		
		// get session info
		ProjectObject project = SessionManager.getProjectObject(request);
    	IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
    	
    	// get parameter info
    	String releaseID = request.getParameter("releaseID");
    	String showTask = request.getParameter("showtask");
    	
    	boolean printTask = false;
    	if (showTask != null) {
    		if (showTask.equals("true")) {
    			printTask = true;
    		}
    	}
    	
    	//get release information
    	ReleasePlanHelper RPhelper = new ReleasePlanHelper(project);
		IReleasePlanDesc reDesc = RPhelper.getReleasePlan(releaseID);
		//initial data
		stories = new HashMap<String, ArrayList<StoryObject>>();
		tatolStoryPoints = new HashMap<String, Float>();
		TaskMap = new LinkedHashMap<Long, ArrayList<TaskObject>>();
		//get sprints information of the release(release id) 

    	try {
	    	List<ISprintPlanDesc> sprinDescList = reDesc.getSprintDescList();
	    	if(sprinDescList != null) {
	    		for (ISprintPlanDesc desc : sprinDescList) {
					String sprintID = desc.getID();
					SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, Long.parseLong(sprintID));
					ArrayList<StoryObject> issues = sprintBacklogLogic.getStoriesByImp();
					stories.put(sprintID, issues);

					//the sum of story points
					float total = 0;
					for (StoryObject story : issues) {
						int est = story.getEstimate();
						total = total + est;
					}
					tatolStoryPoints.put(sprintID, total);

					// print task information
					if (printTask) {
						for (StoryObject story : issues) {
							TaskMap.put(story.getId(), story.getTasks());
						}
					}
				}
			}
	       	
	    	
	    	//set attribute in request
	    	request.setAttribute("release", reDesc);
			request.setAttribute("sprints", sprinDescList);
			request.setAttribute("stories", stories);
			request.setAttribute("TaskMap", TaskMap);
			request.setAttribute("tatolStoryPoints", tatolStoryPoints);
			
			ScrumRole sr = new ScrumRoleLogic().getScrumRole(project, session.getAccount());
			
			if (sr.getAccessReleasePlan()) {
				return mapping.findForward("success");
			} else {
				return mapping.findForward("GuestOnly");
			}
    	} catch (Exception e) {
    		return mapping.findForward("GuestOnly");
    	}
	}
}