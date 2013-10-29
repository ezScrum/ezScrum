package ntut.csie.ezScrum.web.action.plan;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AjaxShowStoryFromSprintAction extends PermissionAction {
	private static Log log = LogFactory.getLog(AjaxShowStoryFromSprintAction.class);
	
	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessReleasePlan();
	}

	@Override
	public boolean isXML() {
		return true;
	}
	
	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		log.info("Show Story From Sprint in AjaxShowStoryFromSprintAction.");
		
		// get project from session or DB
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
	
		// get parameter info
		String SprintID = request.getParameter("Sid");
		
		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(project, session, SprintID);
		List<IIssue> stories = sprintBacklogHelper.getStoriesByImportance();
		
		return sprintBacklogHelper.getStoriesInSprintResponseText(stories);		
		
//		SprintBacklogHelper sbh = new SprintBacklogHelper();
//		return sbh.showStoryFromSprint(project, session, SprintID);		
		
//		SprintBacklog backlog = new SprintBacklog(project, session, Integer.parseInt(SprintID));
//		
//		IIssue[] stories = backlog.getStories();
//		
//		stories = sortStory(stories);
//		double point = backlog.getCurrentPoint(ScrumEnum.STORY_ISSUE_TYPE);
//		
//		StringBuilder sb = new StringBuilder();
//		TranslateSpecialChar tsc = new TranslateSpecialChar();
//		sb.append("<ExistingStories>");
//
//	
//		for(int i = 0; i < stories.length; i++)
//		{
//			String releaseId = stories[i].getReleaseID();
//			if(releaseId.equals("") || releaseId.equals("0") || releaseId.equals("-1"))
//				releaseId = "None";
//			
//			String sprintId = stories[i].getSprintID();
//			if(sprintId.equals("") || sprintId.equals("0") || sprintId.equals("-1"))
//				sprintId = "None";
//			sb.append("<Story>");
//			sb.append("<Id>" + stories[i].getIssueID() + "</Id>");
//			sb.append("<Link>" + tsc.TranslateXMLChar(stories[i].getIssueLink()) + "</Link>");
//			sb.append("<Name>" + tsc.TranslateXMLChar(stories[i].getSummary())+ "</Name>");
//			sb.append("<Value>" + stories[i].getValue()+"</Value>");
//			sb.append("<Importance>" + stories[i].getImportance() + "</Importance>");
//			sb.append("<Estimation>" + stories[i].getEstimated() + "</Estimation>");
//			sb.append("<Status>" + stories[i].getStatus() + "</Status>");
//			sb.append("<Notes>" + tsc.TranslateXMLChar(stories[i].getNotes()) + "</Notes>");
//			sb.append("<HowToDemo>" + tsc.TranslateXMLChar(stories[i].getHowToDemo()) + "</HowToDemo>");
//			sb.append("<Release>" + releaseId + "</Release>");
//			sb.append("<Sprint>" + sprintId + "</Sprint>");
//			sb.append("<Tag>" + tsc.TranslateXMLChar(new Translation().Join(stories[i].getTag(), ",")) + "</Tag>");
//			sb.append("</Story>");
//		}
//		sb.append("</ExistingStories>");
//		
//		return sb;
	}
	
	// sort story information by importance
//	private IIssue[] sortStory(IIssue[] issues) {
//		List<IIssue> list = new ArrayList();
//	
//		for (IIssue issue : issues) {
//			int index = 0;
//			for (index=0 ; index<list.size() ; index++) {
//				if ( Integer.parseInt(issue.getImportance()) > Integer.parseInt(list.get(index).getImportance()) ) {
//					break;
//				}
//			}
//			list.add(index, issue);
//		}
//	
//		return list.toArray(new IIssue[list.size()]);
//	}
}
