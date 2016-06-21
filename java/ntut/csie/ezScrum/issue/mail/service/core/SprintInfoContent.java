package ntut.csie.ezScrum.issue.mail.service.core;

import java.util.ArrayList;

import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;

public class SprintInfoContent {
	public SprintInfoContent(){}
	public StringBuilder getResult(SprintObject sprint, ProjectObject project){
		StringBuilder result = new StringBuilder();
		System.out.println("");
		System.out.println(sprint);
		System.out.println("");
		String subject = "ezScrum: Sprint "+sprint.getSerialId()+" Sprint Info";
		String sprintGoal = "";
		if(sprint.getGoal()!="")
			sprintGoal = sprint.getGoal();
		String storyInfo = getStoryInfo(sprint, project);
		String schedule= getSchedule(sprint);
//		ArrayList<StoryObject> stories = sprint.getStories();
//		for (StoryObject story : stories) {
//			storyInfo =storyInfo+"	"+story.getName()+"("+story.getEstimate()+")\n";
//		}
//		storyInfo = storyInfo + "Estimated velocity : "+sprint.getTotalStoryPoints()+" story points";
//		schedule += "	 Sprint period :" + sprint.getStartDateString() + " to "+sprint.getDemoDateString()+"\n";
//		schedule += "	 Daily Scrum : "	+ sprint.getDailyInfo()+"\n";
//		schedule += "	 Sprint demo : "+sprint.getDemoDateString()+" "+sprint.getDemoPlace();
		result.append("<SprintInfo>");
		result.append("<Total>1</Total>");
		result.append("<Sprint>");
		result.append("<subject>").append(subject).append("</subject>");
		result.append("<sprintGoal>").append(sprintGoal).append("</sprintGoal>");
		result.append("<storyInfo>").append(storyInfo).append("</storyInfo>");
		result.append("<schedule>").append(schedule).append("</schedule>");
		result.append("</Sprint>");
		result.append("</SprintInfo>");
		return result;
	}
	public String getStoryInfo(SprintObject sprint, ProjectObject project){
		String storyInfo ="";
		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(project, sprint.getId());
		// stories sorted by importance
		ArrayList<StoryObject> stories = sprintBacklogHelper.getStoriesSortedByImpInSprint();
		for (StoryObject story : stories) {
			storyInfo = storyInfo+"	"+story.getName()+"("+story.getEstimate()+")\n";
		}
		storyInfo = storyInfo + "Estimated velocity : "+sprint.getTotalStoryPoints()+" story points";
		return storyInfo;
	}
	public String getSchedule(SprintObject sprint){
		String schedule= "";
		schedule += "	 Sprint period :" + sprint.getStartDateString() + " to "+sprint.getDemoDateString()+"\n";
		schedule += "	 Daily Scrum : "	+ sprint.getDailyInfo()+"\n";
		schedule += "	 Sprint demo : "+sprint.getDemoDateString()+" "+sprint.getDemoPlace();
		return schedule;
	}
}
