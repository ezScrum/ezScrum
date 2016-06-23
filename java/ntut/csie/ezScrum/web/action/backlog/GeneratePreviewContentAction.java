package ntut.csie.ezScrum.web.action.backlog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ntut.csie.ezScrum.issue.mail.service.core.SprintInfoContent;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.support.SessionManager;

public class GeneratePreviewContentAction extends PermissionAction{
	private static Log log = LogFactory.getLog(GeneratePreviewContentAction.class);
	
	@Override
	public boolean isValidAction(){
		return super.getScrumRole().getAccessSprintBacklog();
	}
	@Override 
	public boolean isXML() {
		// XML
		return true;
	}
	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		log.info("Generate Preview Content in GeneratePreviewContentAction.");
		ProjectObject project = SessionManager.getProject(request);
		long serialSprintId;
		String serialSprintIdString = request.getParameter("sprintID");
		if (serialSprintIdString == null || serialSprintIdString.length() == 0) {
			serialSprintId = -1;
		} else {
			serialSprintId = Long.parseLong(serialSprintIdString);
		}
		SprintObject sprint = SprintObject.get(project.getId(), serialSprintId);
		StringBuilder result = new StringBuilder();
		SprintInfoContent sprintInfoContent = new SprintInfoContent();
		result = sprintInfoContent.getResult(sprint, project);
//		String subject = "ezScrum: Sprint "+serialSprintId+" Sprint Info";
//		String sprintGoal = sprint.getGoal();
//		String storyInfo ="";
//		String schedule= "";
//		ArrayList<StoryObject> stories = sprint.getStories();
//		for (StoryObject story : stories) {
//			storyInfo =storyInfo+"	"+story.getName()+"("+story.getEstimate()+")\n";
//		}
//		storyInfo = storyInfo + "Estimated velocity : "+sprint.getTotalStoryPoints()+" story points";
//		schedule += "	 Sprint period :" + sprint.getStartDateString() + " to "+sprint.getDemoDateString()+"\n";
//		schedule += "	 Daily Scrum : "	+ sprint.getDailyInfo()+"\n";
//		schedule += "	 Sprint demo : "+sprint.getDemoDateString()+" "+sprint.getDemoPlace();
//		result.append("<SprintInfo>");
//		result.append("<subject>").append(subject).append("</subject>");
//		result.append("<sprintGoal>").append(sprintGoal).append("</sprintGoal>");
//		result.append("<storyInfo>").append(storyInfo).append("</storyInfo>");
//		result.append("<schedule>").append(schedule).append("</schedule>");
//		result.append("</SprintInfo>");
		return result;
	}
	
}
