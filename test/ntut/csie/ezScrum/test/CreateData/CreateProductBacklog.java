package ntut.csie.ezScrum.test.CreateData;

import java.util.ArrayList;

import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CreateProductBacklog {
	private static Log mlog = LogFactory.getLog(CreateProductBacklog.class);
	private int mStoryCount = 1;
	private CreateProject mCP = null;
	private String mTestStoryName = "TEST_STORY_";
	private String mTestStoryHowToDemo = "TEST_STORY_DEMO_";
	private String mTestStoryNotes = "TEST_STORY_NOTE_";
	private int mTestStoryValue = 50;
	private int mTestStoryImp = 100;
	private int mTestStoryEst = 2;
	private ArrayList<StoryObject> mStories = new ArrayList<StoryObject>();
	private ArrayList<Long> mStoryIds = new ArrayList<Long>();

	// ========================== 為了可以設定 story 而新增下列屬性 ===========================
	public static final String COLUMN_TYPE_EST = "EST";
	public static final String COLUMN_TYPE_IMP = "IMP";
	private int mColumnValue = 1;
	private boolean mAutoSetStory = true;
	private String mColumnBeSet;

	public CreateProductBacklog() {
	}

	public CreateProductBacklog(int storyCount, CreateProject cp) {
		mStoryCount = storyCount;
		mCP = cp;
		mAutoSetStory = true;
	}

	public CreateProductBacklog(int storyCount, int columnValue, CreateProject CP, String columnBeSet) {
		mStoryCount = storyCount;
		mCP = CP;
		mColumnValue = columnValue;
		mAutoSetStory = false;
		mColumnBeSet = columnBeSet;
	}

	public void exe() {
		if (mAutoSetStory) {
			for (int i = 0; i < mCP.getAllProjects().size(); i++) {
				ProjectObject project = mCP.getAllProjects().get(i);

				for (int j = 0; j < mStoryCount; j++) {
					StoryObject story = new StoryObject(project.getId());
					story
						.setName(mTestStoryName + j + 1)
						.setNotes(mTestStoryNotes + j + 1)
						.setEstimate(mTestStoryEst)
						.setImportance(mTestStoryImp)
						.setValue(mTestStoryValue)
						.setHowToDemo(mTestStoryHowToDemo + j + 1)
						.save();
					mStories.add(story);
					mStoryIds.add(story.getId());
				}
				mlog.info("Project " + project.getName() + " create " + mStoryCount + " Stories success.");
			}
		} else {
			if (mColumnBeSet.equals(COLUMN_TYPE_EST)) {
				for (int i = 0; i < mCP.getAllProjects().size(); i++) {
					ProjectObject project = mCP.getAllProjects().get(i);
					
					for (int j = 0; j < mStoryCount; j++) {
						StoryObject story = new StoryObject(project.getId());
						story
							.setName(mTestStoryName + j + 1)
							.setNotes(mTestStoryNotes + j + 1)
							.setEstimate(mColumnValue)
							.setImportance(mTestStoryImp)
							.setValue(mTestStoryValue)
							.setHowToDemo(mTestStoryHowToDemo + j + 1)
							.save();
						mStories.add(story);
						mStoryIds.add(story.getId());
					}
					mlog.info("Assign Estimate Value");
					mlog.info("Project " + project.getName() + " create " + mStoryCount + " Stories success");
				}
			} else if (mColumnBeSet.equals(COLUMN_TYPE_IMP)) {
				for (int i = 0; i < mCP.getAllProjects().size(); i++) {
					ProjectObject project = mCP.getAllProjects().get(i);

					for (int j = 0; j < mStoryCount; j++) {
						StoryObject story = new StoryObject(project.getId());
						story
							.setName(mTestStoryName + j + 1)
							.setNotes(mTestStoryNotes + j + 1)
							.setEstimate(mTestStoryEst)
							.setImportance(mColumnValue)
							.setValue(mTestStoryValue)
							.setHowToDemo(mTestStoryHowToDemo + j + 1)
							.save();
						mStories.add(story);
						mStoryIds.add(story.getId());
					}
					mlog.info("Assign Importance Value");
					mlog.info("Project " + project.getName() + " create " + mStoryCount + " Stories success");
				}
			} else {
				mlog.info("Create Product Backlog Type error.");
			}
		}
	}

	public void createBacklogStory(ProjectObject project, int value, int importance, int estimate) {
		int index = mStoryIds.size();
		StoryObject story = new StoryObject(project.getId());
		story
			.setName(mTestStoryName + index + 1)
			.setNotes(mTestStoryNotes + index + 1)
			.setEstimate(estimate)
			.setImportance(importance)
			.setValue(value)
			.setHowToDemo(mTestStoryHowToDemo + index + 1)
			.save();
		mStories.add(story);
		mStoryIds.add(story.getId());
	}

	public ArrayList<StoryObject> getStories() {
		return mStories;
	}

	public ArrayList<Long> getStoryIds() {
		return mStoryIds;
	}
}
