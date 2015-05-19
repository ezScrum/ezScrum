package ntut.csie.ezScrum.test.CreateData;

import java.util.ArrayList;

import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AddStoryToSprint {
	private static Log mlog = LogFactory.getLog(AddStoryToSprint.class);
	private int mStoryCount;
	private int mSprintCount;
	private int mProjectCount;
	private CreateProject mCP;
	private ArrayList<StoryObject> mStories = new ArrayList<StoryObject>();

	/**
	 * Add story to sprint construct
	 * 
	 * @param storyCount
	 * @param columnValue Set estimate or importance's value
	 * @param CS
	 * @param CP
	 * @param columnBeSet Chose estimate or importance to set value, e.g. "EST" or "IMP"
	 * @throws Exception
	 */
	public AddStoryToSprint(int storyCount, int columnValue, CreateSprint CS,
			CreateProject CP, String columnBeSet) {
		mStoryCount = storyCount;
		mProjectCount = CP.getProjectList().size();
		mSprintCount = CS.getSprintCount();
		mCP = CP;
		createStories(columnValue, columnBeSet);
	}

	/**
	 * Add story to sprint construct
	 * 
	 * @param storyCount
	 * @param columnValue Set estimate or importance's value
	 * @param sprintCount
	 * @param CP
	 * @param columnBeSet Chose estimate or importance to set value, e.g. "EST" or "IMP"
	 * @throws Exception
	 */
	public AddStoryToSprint(int storyCount, int columnValue, int sprintCount,
			CreateProject CP, String columnBeSet) {
		mStoryCount = storyCount;
		mProjectCount = CP.getAllProjects().size();
		mSprintCount = sprintCount;
		mCP = CP;
		createStories(columnValue, columnBeSet);
	}

	public int getSprintCount() {
		return mSprintCount;
	}

	public ArrayList<StoryObject> getStories() {
		return mStories;
	}

	public void exe() throws Exception {
		for (int i = 0; i < mProjectCount; i++) {
			ProjectObject project = mCP.getAllProjects().get(i);
			// 對每個 sprint 加入 mStoryCount 個 stories
			for (int sprintIndex = 0; sprintIndex < mSprintCount; sprintIndex++) {
				for (int storyIndex = 0; storyIndex < mStoryCount; storyIndex++) {
					int storyPositionInStories = storyIndex + mStoryCount * sprintIndex;
					long sprintId = sprintIndex + 1;
					StoryObject story = mStories.get(storyPositionInStories);
					story.setSprintId(sprintId).save();
				}
				mlog.info("專案 " + project.getName() + ", 第 " + (sprintIndex + 1)
						+ " 個 sprint 加入 " + mStoryCount + " 個 stories 成功");
			}
		}
	}

	// create new story list
	private void createStories(int columnValue, String columnBeSet) {
		int totalStory = mStoryCount * mSprintCount;
		CreateProductBacklog createStory = new CreateProductBacklog(totalStory,
				columnValue, mCP, columnBeSet);
		createStory.exe();
		mStories = createStory.getStories();
	}
}
