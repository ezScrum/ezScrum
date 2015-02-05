package ntut.csie.ezScrum.test.CreateData;

import java.util.ArrayList;
import java.util.List;

import ntut.csie.ezScrum.web.dataObject.TaskObject;

public class AddTaskToStory {
	private int mTaskCount = 1; // add number of task into story
	private int mStoryCount = 1;
	private int mProjectCount = 1;
	private int mSprintCount = 1;

	private int mEstValue = 1;

	private CreateProject mCP;
	private ArrayList<Long> mTasksId = new ArrayList<Long>();
	private ArrayList<TaskObject> mTasks = new ArrayList<TaskObject>();

	public AddTaskToStory(int count, int EstValue, AddStoryToSprint ASS,
			CreateProject CP) {
		mTaskCount = count;
		mStoryCount = ASS.getStories().size() / ASS.getSprintCount();
		mProjectCount = CP.getProjectList().size();
		mSprintCount = ASS.getSprintCount();

		mEstValue = EstValue;
		mCP = CP;
	}

	public void exe() throws Exception {
		for (int i = 0; i < mProjectCount; i++) {
			// 將 story 區分到每個 sprint 的 story 底下
			// 將task已相同數量的方式加入至story中
			for (int j = 0; j < mSprintCount; j++) {
				for (int k = 0; k < mStoryCount; k++) {
					long StoryID = j * mStoryCount + (k + 1);
					CreateTask CT = new CreateTask(mTaskCount, mEstValue, StoryID, mCP);
					CT.exe();

					mTasks.addAll(CT.getTaskList());
					mTasksId.addAll(CT.getTaskIDList());
				}
			}
		}
	}

	public ArrayList<Long> getTasksId() {
		return mTasksId;
	}

	public ArrayList<TaskObject> getTasks() {
		return mTasks;
	}
}
