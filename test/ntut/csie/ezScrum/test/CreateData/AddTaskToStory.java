package ntut.csie.ezScrum.test.CreateData;

import java.util.ArrayList;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;

public class AddTaskToStory {
	private int EachCount = 1;
	private int StoryCount = 1;
	private int ProjectCount = 1;
	private int SprintCount = 1;

	private int EstValue = 1;

	private CreateProject CP;
	private ArrayList<Long> TaskIDList = new ArrayList<Long>();
	private List<IIssue> TaskList = new ArrayList<IIssue>();

	public AddTaskToStory(int count, int EstValue, AddStoryToSprint ASS, CreateProject CP) {
		this.EachCount = count;
		this.StoryCount = ASS.getIssueList().size() / ASS.getSprintCount();
		this.ProjectCount = CP.getProjectList().size();
		this.SprintCount = ASS.getSprintCount();

		this.EstValue = EstValue;
		this.CP = CP;
	}

	public void exe() throws Exception {
		for (int i = 0; i < this.ProjectCount; i++) {
			// String projectName = this.CP.PJ_NAME + Integer.toString((i+1)); // TEST_PROJECT_X

			// IProject project = ResourceFacade.getWorkspace().getRoot().getProject(projectName);
			// 此路徑為開發端的 TestData/MyWorkspace/

			// 將 story 區分到每個 sprint 的 story 底下
			// 將task已相同數量的方式加入至story中
			for (int j = 0; j < this.SprintCount; j++) {
				for (int k = 0; k < this.StoryCount; k++) {
					long StoryID = j * this.StoryCount + (k + 1);
					CreateTask CT = new CreateTask(this.EachCount, this.EstValue, StoryID, this.CP);
					CT.exe();

					this.TaskList.addAll(CT.getTaskList());
					this.TaskIDList.addAll(CT.getTaskIDList());
				}
			}
		}
	}

	public ArrayList<Long> getTaskIDList() {
		return this.TaskIDList;
	}

	public List<IIssue> getTaskList() {
		return this.TaskList;
	}
}
