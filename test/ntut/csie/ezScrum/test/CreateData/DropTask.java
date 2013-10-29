package ntut.csie.ezScrum.test.CreateData;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.jcis.resource.core.IProject;

public class DropTask {
	private ezScrumInfoConfig config = new ezScrumInfoConfig();
	private CreateProject CP;
	private int sprintID = 0;
	private int issueID = 0;
	private int parentID = 0;
	
	public DropTask(CreateProject CP, int sprintID, int parentID, int issueID) {
		this.CP = CP;
		this.sprintID = sprintID;
		this.issueID = issueID;
		this.parentID = parentID;
	}

	public void exe() {
		IProject project = this.CP.getProjectList().get(0);
		IUserSession userSession = config.getUserSession();
		SprintBacklogMapper sprintBacklogMapper = new SprintBacklogMapper(project, userSession, sprintID);
		// remove relation
		sprintBacklogMapper.removeTask(issueID, parentID);
	}
}
