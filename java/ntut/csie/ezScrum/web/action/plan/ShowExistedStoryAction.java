package ntut.csie.ezScrum.web.action.plan;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
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
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		
		// get parameter info
		String sprintID = request.getParameter("sprintID");
		String releaseID = request.getParameter("releaseID");
		
		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(project, session, sprintID);
		
		// ProductBacklog Helper
    	List<IStory> stories = null;
    	
    	boolean NumberError = false;
    	// Select from Sprint Backlog
    	try{
    		stories = sprintBacklogHelper.getExistedStories(releaseID);
		} catch (SQLException e) {
			System.out.println("class : ShowExistedStoryAction, method : execute, exception : " + e.toString());
    		NumberError = true;
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
