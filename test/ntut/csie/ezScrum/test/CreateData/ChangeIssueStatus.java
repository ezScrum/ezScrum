package ntut.csie.ezScrum.test.CreateData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.jcis.core.util.DateUtil;

public class ChangeIssueStatus {
	private ArrayList<TaskObject> mTasks = new ArrayList<TaskObject>();
	private ArrayList<StoryObject> mStories = new ArrayList<StoryObject>();
	private CreateProject mCP;
	private Date mSetDoneDate = null;
	private Configuration mConfiguration = new Configuration();
	
	public <E> ChangeIssueStatus(ArrayList<E> issues, CreateProject CP) {
		for(Object object : issues){
			if (object instanceof StoryObject) {
				StoryObject story = (StoryObject) object;
				mStories.add(story);
			} else if(object instanceof TaskObject){
				TaskObject task = (TaskObject) object;
				mTasks.add(task);
			}
		}
		mCP = CP;
	}
	
	public void exeCheckOutTasks() {
		IUserSession userSession = mConfiguration.getUserSession();
		String handlerUsername = userSession.getAccount().getUsername();
		ProjectObject project = mCP.getAllProjects().get(0);

		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, -1);

		for (TaskObject task : mTasks) {
			long id = task.getId();
			String name = task.getName();
			String notes = task.getNotes();
			SimpleDateFormat format = new SimpleDateFormat(DateUtil._16DIGIT_DATE_TIME);
			if (mSetDoneDate != null) {
				sprintBacklogLogic.checkOutTask(id, name, handlerUsername, task.getPartnersUsername(), notes, format.format(mSetDoneDate));
			} else {
				sprintBacklogLogic.checkOutTask(id, name, handlerUsername, task.getPartnersUsername(), notes, format.format(new Date()));
			}
			System.out.println("移動 Task " + id + " 到 Check Out 成功");
		}
	}
	
	public void exeCloseTasks() {
		ProjectObject project = mCP.getAllProjects().get(0);

		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, -1);

		for (TaskObject task : mTasks) {
			long id = task.getId();
			String name = task.getName();
			String notes = task.getNotes();
			int actual = task.getActual();
			SimpleDateFormat format = new SimpleDateFormat(DateUtil._16DIGIT_DATE_TIME);
			if (mSetDoneDate != null) {
				sprintBacklogLogic.closeTask(id, name, notes, actual, format.format(mSetDoneDate));
			} else {
				sprintBacklogLogic.closeTask(id, name, notes, actual, format.format(new Date()));
			}
			System.out.println("移動 Task " + id + " 到 Done 成功");
		}
	}
	
	public void exeCloseStories() {
		ProjectObject project = mCP.getAllProjects().get(0);
		SprintBacklogMapper sprintBacklogMapper = new SprintBacklogMapper(project);
		for (StoryObject story : mStories) {
			long id = story.getId();
			String name = story.getName();
			String notes = story.getNotes();
			if (mSetDoneDate != null) {
				sprintBacklogMapper.closeStory(id, name, notes, mSetDoneDate);
			} else {
				sprintBacklogMapper.closeStory(id, name, notes, new Date());
			}
			System.out.println("移動Story " + id + " 到 Done 成功");
		}
	}
}
