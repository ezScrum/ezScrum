package ntut.csie.ezScrum.test.CreateData;

import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;

public class DropTask {
	private CreateProject mCP;
	private long mSprintId = 0;
	private long mTaskId = 0;
	
	public DropTask(CreateProject createProject, long sprintId, long parentId, long taskId) {
		mCP = createProject;
		mSprintId = sprintId;
		mTaskId = taskId;
	}

	public void exe() {
		ProjectObject project = mCP.getAllProjects().get(0);
		SprintBacklogMapper sprintBacklogMapper = new SprintBacklogMapper(project, mSprintId);
		sprintBacklogMapper.dropTask(mTaskId);
	}
}
