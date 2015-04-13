package ntut.csie.ezScrum.web.action.plan;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class ShowExistedStoryAction extends PermissionAction {
//	private static Log log = LogFactory.getLog(ShowExistedStoryAction.class);
	
	@Override
	public boolean isValidAction() {
		return (super.getScrumRole().getAccessReleasePlan() && 
				super.getScrumRole().getAccessSprintBacklog());
	}

	@Override
	public boolean isXML() {
		// XML
		return true;
	}
	
	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		// get session info
		IProject project = SessionManager.getProject(request);
		
		// get parameter info
		String sprintID = request.getParameter("sprintID");
		
		String releaseID = request.getParameter("releaseID");
		
		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(new ProjectObject(project.getName()), Long.parseLong(sprintID));
		
		// ProductBacklog Helper
    	ArrayList<StoryObject> stories = null;
    	
    	boolean NumberError = false;
    	// Select from Sprint Backlog
    	try{
    		stories = sprintBacklogHelper.getExistingStories();
		} catch (NumberFormatException e) {
			System.out.println("class : ShowExistedStoryAction, method : execute, exception : " + e.toString());
    		NumberError = true;
		}
		
		if (NumberError || stories == null ) {
			return new StringBuilder("");
		} 
		
		StringBuilder sb = new StringBuilder( sprintBacklogHelper.getStoriesInSprintResponseText(stories) );
		
		return sb;
	}
}
