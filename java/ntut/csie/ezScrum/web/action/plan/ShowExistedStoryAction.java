package ntut.csie.ezScrum.web.action.plan;

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
		}catch (NumberFormatException e) {
    		System.out.println("class : ShowExistedStoryAction, method : execute, exception : " + e.toString());
    		NumberError = true;
		}
		
    	if(stories == null){
    		System.out.println("stories == null");
    	}
    	
		if (NumberError || stories == null ) {
			return new StringBuilder("");
		} 

		
//		for(IStory story:stories) {
//			//replace special symbol
//			story.setSummary(story.getSummary());
//			story.setDescription(story.getDescription());
//
//			String title=null;
//			List<IssueAttachFile> files = story.getAttachFile();
//			for(IssueAttachFile file:files){
//				if(title==null)
//					title = file.getFilename();
//				else
//					title = title+"; "+file.getFilename();
//			}
//		}
    	
		StringBuilder sb = new StringBuilder( sprintBacklogHelper.getStoriesInSprintResponseText(stories) );
		
//		TranslateSpecialChar translateSpecialchar = new TranslateSpecialChar();
//		StringBuilder sb = new StringBuilder();
//		sb.append("<ExistingStories>");
//		for(IStory story:stories ){
//			String releaseId = story.getReleaseID();
//			if(releaseId.equals("") || releaseId.equals("0") || releaseId.equals("-1"))
//				releaseId = "None";
//			
//			String sprintId = story.getSprintID();
//			if(sprintId.equals("") || sprintId.equals("0") || sprintId.equals("-1"))
//				sprintId = "None";
//			
//			sb.append("<Story>");
//			sb.append("<Id>" + story.getIssueID() + "</Id>");
//			sb.append("<Link>" + translateSpecialchar.TranslateXMLChar(story.getIssueLink()) + "</Link>");
//			sb.append("<Name>" + translateSpecialchar.TranslateXMLChar(story.getSummary()) + "</Name>");
//			sb.append("<Value>" + story.getValue() + "</Value>");
//			sb.append("<Importance>" + story.getImportance() + "</Importance>");
//			sb.append("<Estimation>" + story.getEstimated() + "</Estimation>");
//			sb.append("<Status>" + story.getStatus() + "</Status>");
//			sb.append("<Notes>" + translateSpecialchar.TranslateXMLChar(story.getNotes()) + "</Notes>");
//			sb.append("<HowToDemo>" + translateSpecialchar.TranslateXMLChar(story.getHowToDemo()) + "</HowToDemo>");
//			sb.append("<Release>" + releaseId + "</Release>");
//			sb.append("<Sprint>" + sprintId + "</Sprint>");
//			sb.append("<Tag>" + translateSpecialchar.TranslateXMLChar(new Translation().Join(story.getTag(), ",")) + "</Tag>");
//			sb.append("</Story>");
//		}
//		sb.append("</ExistingStories>");
		
		return sb;
		
		
//		// get session info
//		IProject project = (IProject) SessionManager.getProject(request);
//		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
//		
//		// get parameter info
//		String sprintID = request.getParameter("sprintID");
//		String releaseID = request.getParameter("releaseID");
//		
//		// ProductBacklog Helper
//    	ProductBacklogHelper helper = new ProductBacklogHelper(project, session);
//    	IStory[] stories = null;
//    	
//    	TranslateSpecialChar translateSpecialchar = new TranslateSpecialChar();
//		
//    	boolean NumberError = false;
//    	// Select from Sprint Backlog
//		if ( (sprintID != null) && (! sprintID.isEmpty()) && (! sprintID.equals("-1")) ) {
//			try {
//				if (Integer.parseInt(sprintID) > 0) {
//					// get release ID by sprint ID
//					ReleasePlanHelper rphelper = new ReleasePlanHelper(project);
//					releaseID = rphelper.getReleaseID(sprintID);
//					// get stories that exist in 
//			    	stories= helper.getAddableStories(sprintID, releaseID);
//				}
//			} catch (NumberFormatException e) {
//				System.out.println("class : ShowExistedStoryAction, method : execute, exception : " + e.toString());
//				NumberError = true;
//			}
//		} else if ( (releaseID != null) && (! releaseID.isEmpty()) && (! releaseID.equals("-1")) ) {
//			
//			// Select from Release Plan
//			// get stories that exist in
//			try {
//				if (Integer.parseInt(releaseID) > 0) {
//					stories= helper.getAddableStories();
//				}
//			} catch (NumberFormatException e) {
//				System.out.println("class : ShowExistedStoryAction, method : execute, exception : " + e.toString());
//				NumberError = true;
//			}
//		} else {
//			NumberError = true;
//		}
//		
//		if (NumberError) {
//			return new StringBuilder("");
//		} 
//		
//		for(IStory story:stories) {
//			//replace special symbol
//			story.setSummary(story.getSummary());
//			story.setDescription(story.getDescription());
//
//			String title=null;
//			List<IssueAttachFile> files = story.getAttachFile();
//			for(IssueAttachFile file:files){
//				if(title==null)
//					title = file.getFilename();
//				else
//					title = title+"; "+file.getFilename();
//			}
//		}
//    	
//		StringBuilder sb = new StringBuilder();
//		sb.append("<ExistingStories>");
//		for(int i = 0; i < stories.length; i++)
//		{
//			String releaseId = stories[i].getReleaseID();
//			if(releaseId.equals("") || releaseId.equals("0") || releaseId.equals("-1"))
//				releaseId = "None";
//			
//			String sprintId = stories[i].getSprintID();
//			if(sprintId.equals("") || sprintId.equals("0") || sprintId.equals("-1"))
//				sprintId = "None";
//			
//			sb.append("<Story>");
//			sb.append("<Id>" + stories[i].getIssueID() + "</Id>");
//			sb.append("<Link>" + translateSpecialchar.TranslateXMLChar(stories[i].getIssueLink()) + "</Link>");
//			sb.append("<Name>" + translateSpecialchar.TranslateXMLChar(stories[i].getName()) + "</Name>");
//			sb.append("<Value>" + stories[i].getValue() + "</Value>");
//			sb.append("<Importance>" + stories[i].getImportance() + "</Importance>");
//			sb.append("<Estimation>" + stories[i].getEstimated() + "</Estimation>");
//			sb.append("<Status>" + stories[i].getStatus() + "</Status>");
//			sb.append("<Notes>" + translateSpecialchar.TranslateXMLChar(stories[i].getNotes()) + "</Notes>");
//			sb.append("<HowToDemo>" + translateSpecialchar.TranslateXMLChar(stories[i].getHowToDemo()) + "</HowToDemo>");
//			sb.append("<Release>" + releaseId + "</Release>");
//			sb.append("<Sprint>" + sprintId + "</Sprint>");
//			sb.append("<Tag>" + translateSpecialchar.TranslateXMLChar(new Translation().Join(stories[i].getTag(), ",")) + "</Tag>");
//			sb.append("</Story>");
//		}
//		sb.append("</ExistingStories>");
//		
//		return sb;
	}
}
