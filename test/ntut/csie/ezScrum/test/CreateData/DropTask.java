package ntut.csie.ezScrum.test.CreateData;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.jcis.resource.core.IProject;

public class DropTask {
	private Configuration mConfig = new Configuration();
	private CreateProject mCreateProject;
	private long mSprintId = 0;
	private long mTaskId = 0;
	
	public DropTask(CreateProject createProject, long sprintId, long parentId, long taskId) {
		mCreateProject = createProject;
		mSprintId = sprintId;
		mTaskId = taskId;
	}

	public void exe() {
		IProject project = mCreateProject.getProjectList().get(0);
		IUserSession userSession = mConfig.getUserSession();
		SprintBacklogMapper sprintBacklogMapper = new SprintBacklogMapper(project, userSession, mSprintId);
		// remove relation
		sprintBacklogMapper.dropTask(mTaskId);
	}
}
