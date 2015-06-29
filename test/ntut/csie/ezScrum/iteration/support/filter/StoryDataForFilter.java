package ntut.csie.ezScrum.iteration.support.filter;

import java.util.ArrayList;

import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;

public class StoryDataForFilter {
	// for filter Backlog、Detail、Done
	private long mProjectId = 1;
	private ArrayList<StoryObject> mStories = null;
	private ArrayList<StoryObject> mStoriesInfo = null;
	private ArrayList<TaskObject> mTasksInfo = null;
	
	public StoryDataForFilter() {
		mStories = new ArrayList<StoryObject>();

		for (int i = 0; i < 10; i++) {
			StoryObject story = new StoryObject(mProjectId);
			story.save();
			mStories.add(story);
		}
		init();
	}
	
	public StoryDataForFilter(String info) {
		mStoriesInfo = new ArrayList<StoryObject>();
		mTasksInfo = new ArrayList<TaskObject>();

		for (int i = 0; i < 10; i++) {
			// add story
			StoryObject story = new StoryObject(mProjectId);
			story.save();
			mStoriesInfo.add(story);
			// add task
			TaskObject task = new TaskObject(mProjectId);
			task.save();
			mTasksInfo.add(task);
		}
		initStory(info);
		initTask(info);
	}
	
	public ArrayList<StoryObject> getStorirs() {
		return mStories;
	}
	
	public ArrayList<StoryObject> getStorirsByInfo() {
		return mStoriesInfo;
	}
	
	public ArrayList<TaskObject> getTasksByInfo() {
		return mTasksInfo;
	}
	private void init() {
		// for backlogged, total = 5
		for (int i = 0; i < 5; i++) {
			mStories.get(i).setEstimate(0)
			        .setImportance(0)
			        .setValue(0)
			        .setStatus(StoryObject.STATUS_UNCHECK)
			        .save();
		}
		
		// for detailed, total = 2
		for (int i = 5; i < 7; i++) {
			mStories.get(i).setEstimate(10)
			        .setImportance(10)
			        .setValue(10)
			        .setStatus(StoryObject.STATUS_UNCHECK)
			        .save();
		}
		
		// for done, total = 3
		for (int i = 7; i < 10; i++) {
			mStories.get(i).setEstimate(5)
			        .setImportance(100)
			        .setValue(100)
			        .setStatus(StoryObject.STATUS_DONE)
			        .save();
		}
	}
	
	private void initStory(String info) {
		// summary contains info
		mStoriesInfo.get(0).setName(info).save();
		mStoriesInfo.get(1).setName(info + "_Story_Test").save();
		mStoriesInfo.get(2).setName("Story_Test_" + info + "_Story_Test").save();
		mStoriesInfo.get(3).setName("Story_Test_" + info).save();
		mStoriesInfo.get(4).setName(info + info + info).save();
		
		mStoriesInfo.get(5).setName("").save();
		mStoriesInfo.get(6).setName("").save();
		mStoriesInfo.get(7).setName("").save();
		mStoriesInfo.get(8).setName("").save();
		mStoriesInfo.get(9).setName("").save();
		
		// description contains info
		mStoriesInfo.get(0).setNotes("").save();
		mStoriesInfo.get(1).setNotes("").save();
		mStoriesInfo.get(2).setNotes("").save();
		mStoriesInfo.get(3).setNotes("").save();
		mStoriesInfo.get(4).setNotes("").save();
		
		mStoriesInfo.get(5).setNotes(info).save();
		mStoriesInfo.get(6).setNotes(info + info + info).save();
		mStoriesInfo.get(7).setNotes(info + "_Story_Test").save();
		mStoriesInfo.get(8).setNotes("Story_Test_" + info + "_Story_Test").save();
		mStoriesInfo.get(9).setNotes("Story_Test_" + info).save();
	}
	
	private void initTask(String info) {	
		// summary contains info
		mTasksInfo.get(0).setName(info).save();
		mTasksInfo.get(1).setName(info + "_Story_Test").save();
		mTasksInfo.get(2).setName("Story_Test_" + info + "_Story_Test").save();
		mTasksInfo.get(3).setName("Story_Test_" + info).save();
		mTasksInfo.get(4).setName(info + info + info).save();
		
		mTasksInfo.get(5).setName("").save();
		mTasksInfo.get(6).setName("").save();
		mTasksInfo.get(7).setName("").save();
		mTasksInfo.get(8).setName("").save();
		mTasksInfo.get(9).setName("").save();

		// description contains info
		mTasksInfo.get(0).setNotes("").save();
		mTasksInfo.get(1).setNotes("").save();
		mTasksInfo.get(2).setNotes("").save();
		mTasksInfo.get(3).setNotes("").save();
		mTasksInfo.get(4).setNotes("").save();
		
		mTasksInfo.get(5).setNotes(info).save();
		mTasksInfo.get(6).setNotes(info + info + info).save();
		mTasksInfo.get(7).setNotes(info + "_Story_Test").save();
		mTasksInfo.get(8).setNotes("Story_Test_" + info + "_Story_Test").save();
		mTasksInfo.get(9).setNotes("Story_Test_" + info).save();
		
		// create accounts
		AccountObject account = new AccountObject("Yoman");
		account.save();

		AccountObject account2 = new AccountObject("Waterman");
		account2.save();

		AccountObject account3 = new AccountObject("Ironman");
		account3.save();

		AccountObject account4 = new AccountObject(info);
		account4.save();

		AccountObject account5 = new AccountObject("superman");
		account5.save();

		AccountObject account6 = new AccountObject("noman");
		account6.save();

		// handlers contains info
		mTasksInfo.get(0).setHandlerId(account.getId()).save();
		mTasksInfo.get(1).setHandlerId(account2.getId()).save();
		mTasksInfo.get(2).setHandlerId(account3.getId()).save();
		mTasksInfo.get(3).setHandlerId(account4.getId()).save();
		mTasksInfo.get(4).setHandlerId(account4.getId()).save();
		mTasksInfo.get(5).setHandlerId(account4.getId()).save();
		mTasksInfo.get(6).setHandlerId(account4.getId()).save();
		mTasksInfo.get(7).setHandlerId(account4.getId()).save();
		mTasksInfo.get(8).setHandlerId(account5.getId()).save();
		mTasksInfo.get(8).setHandlerId(account6.getId()).save();
	}
}