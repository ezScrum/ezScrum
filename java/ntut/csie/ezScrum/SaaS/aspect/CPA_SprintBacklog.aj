package ntut.csie.ezScrum.SaaS.aspect;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import ntut.csie.ezScrum.SaaS.PMF;
import ntut.csie.ezScrum.SaaS.action.support.DifferentDataTypeTranslation;
import ntut.csie.ezScrum.SaaS.database.ProjectDataStore;
import ntut.csie.ezScrum.SaaS.database.StoryDataStore;
import ntut.csie.ezScrum.SaaS.database.TagDataStore;
import ntut.csie.ezScrum.SaaS.database.TaskDataStore;
import ntut.csie.ezScrum.SaaS.util.ScrumEnum;
import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.IIssueTag;
import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.issue.internal.Issue;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ITask;
import ntut.csie.ezScrum.iteration.iternal.Story;
import ntut.csie.ezScrum.iteration.iternal.Task;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.jcis.resource.core.IProject;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;


// 有關資料庫物件存取的程式碼 (DataStore)必須做 data nucleus enhance 的動作
public aspect CPA_SprintBacklog {

	private String projectId;
	
	/*
	 *  Sprint Backlog Mapper
	 */	

	// replace: constructor of SprintBacklogMapper(IProject project, IUserSession userSession) 
	pointcut SprintBacklogMapper1PC(IProject project, IUserSession userSession)
	:execution(public SprintBacklogMapper.new(IProject, IUserSession)) && args(project, userSession);
	
	after(IProject project, IUserSession userSession)
	returning:SprintBacklogMapper1PC(project, userSession) {
		System.out.println("replaced by AOP and use after method...SprintBacklogMapper Constructor PC: " + thisJoinPoint);
		
		this.projectId = project.getName();
	}
	
	// replace: constructor of SprintBacklogMapper(IProject project, IUserSession userSession, int sprintId)
	pointcut SprintBacklogMapper2PC(IProject project, IUserSession userSession, int sprintId)
	: execution(SprintBacklogMapper.new(IProject, IUserSession, int)) && args(project, userSession, sprintId);
	
	after(IProject project, IUserSession userSession, int sprintId)
	returning:SprintBacklogMapper2PC(project, userSession, sprintId) {
		System.out.println("replaced by AOP and use after method...SprintBacklogMapper Constructor PC: " + thisJoinPoint);
		
		this.projectId = project.getName();
	}
	
	/**
	 * private method
	 */
	// replace: private method of SprintBacklogMapper.initITSInformation()
	pointcut initITSInformationPC()
	: execution(void SprintBacklogMapper.initITSInformation()) && args();

	void around()
	: initITSInformationPC() {
		System.out.println("replaced by AOP...initITSInformationPC: " + thisJoinPoint);
	}
	
	// replace: public IIssue[] getStoryInSprint(long sprintID)
	pointcut getStoryInSprintPC(long sprintID)
	: execution(IIssue[] SprintBacklogMapper.getStoryInSprint(long)) && args(sprintID);

	IIssue[] around(long sprintID)
	: getStoryInSprintPC(sprintID) {
		System.out.println("replaced by AOP...getStoryInSprintPC: " + thisJoinPoint);
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Query query = pm.newQuery(StoryDataStore.class);
		query.setFilter("projectId == '" + this.projectId +"' && " + "sprintId == '" + String.valueOf(sprintID) +"'");

		@SuppressWarnings("unchecked")
		List<StoryDataStore> result = (List<StoryDataStore>) query.execute();
		List<IStory> stories = new ArrayList<IStory>();
		for(StoryDataStore storyDataStore: result){
			stories.add(this.tranStory(storyDataStore));				
		}
		pm.close();
		
		return stories.toArray(new IStory[stories.size()]);
	}
	
	// replace: public IIssue[] getTaskInStory(long storyID)
	pointcut getTaskInStoryPC(long storyID)
	: execution(IIssue[] SprintBacklogMapper.getTaskInStory(long)) && args(storyID);

	IIssue[] around(long storyID)
	: getTaskInStoryPC(storyID) {
		System.out.println("replaced by AOP...getTaskInStoryPC: " + thisJoinPoint);
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Query query = pm.newQuery(TaskDataStore.class);
		query.setFilter("projectId == '" + this.projectId + "' && " + "parentID == '" + String.valueOf(storyID) + "'");
		@SuppressWarnings("unchecked")
		List<TaskDataStore> result = (List<TaskDataStore>) query.execute();
		List<ITask> tasks = new ArrayList<ITask>();
		for(TaskDataStore task: result){
			tasks.add(this.tranTask(task));
		}
		pm.close();
		return tasks.toArray(new ITask[tasks.size()]);
	}
	
	private IStory tranStory(StoryDataStore storyDS) {
		IIssue issue = new Issue();
		issue.setIssueID(Long.parseLong(storyDS.getStoryId()));
		issue.setSummary(storyDS.getName());
		issue.setIssueLink("");
		issue.setStatus(ITSEnum.getStatus(storyDS.getStatusValue()));
		
		Story story = new Story(issue);
		story.setValue(storyDS.getValue());
		story.setImportance(storyDS.getImportance());
		story.setEstimated(storyDS.getEstimation());
		story.setHowToDemo(storyDS.getHowToDemo());
		story.setNotes(storyDS.getNotes());
		story.setSprintId(storyDS.getSprintId());
		story.setReleaseId(storyDS.getReleaseId());
		story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
		
		List<IIssueTag> tagList = new ArrayList<IIssueTag>();
		for(String tagName:storyDS.getTagsList()){
			TagDataStore tagDS = this.getTagDataStore(tagName);
			if( tagDS != null ){
				tagList.add(DifferentDataTypeTranslation.tranTag(tagDS));
			}
		}
		story.setTag(tagList);
		
		DifferentDataTypeTranslation.tranStoryHistory(story, storyDS);
		
		return story;
	}
	
	private ITask tranTask(TaskDataStore taskDS) {
		Task task = new Task();
		task.setStoryID( String.valueOf(task.getStoryID()) );
		task.setEstimated( taskDS.getEstimation() );
		task.setNotes( taskDS.getNotes() );
		task.setPartners( task.getPartners() );
		task.setCategory(ScrumEnum.TASK_ISSUE_TYPE);
		DifferentDataTypeTranslation.tranTaskHistory(task, taskDS);
		
//		ITask task = new Task(taskDS.getId());
//		task.setName(taskDS.getName());
//		task.setActualHour(taskDS.getActualHour());
//		task.setEstimation(taskDS.getEstimation());
//		task.setHandler(taskDS.getHandler());
//		task.setNotes(taskDS.getNotes());
//		task.setParent(taskDS.getParentID());
//		task.setRemains(taskDS.getRemains());
//		task.setStatusValue(taskDS.getStatusValue());
//		task.setPartners(taskDS.getPartners());
//		this.tranTaskHistory(task, taskDS);
		return task;
	}
	
	/**
	 * ------------------------- Tag -------------------------
	 */
	/**
	 * Get Tag Data Store by TagName
	 * @param tagName
	 * @return
	 */
	private TagDataStore getTagDataStore(String tagName){
		//	get tag data store
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Key projectkey = KeyFactory.createKey(ProjectDataStore.class.getSimpleName(), this.projectId);
		ProjectDataStore projectDS = pm.getObjectById(ProjectDataStore.class, projectkey);
		
		for(TagDataStore tagDS:projectDS.getTags()){
			if( tagDS.getTagName().equals(tagName) ){
				return tagDS;
			}
		}
		return null;
	}
}