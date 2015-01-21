package ntut.csie.ezScrum.test.CreateData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

public class CheckOutIssue {
	private ArrayList<Long> IssueIDList = new ArrayList<Long>();
	private List<IIssue> issueList;
	private CreateProject CP;
	private Date SetDoneDate = null;
	private Configuration configuration = new Configuration();
	private ProjectMapper projectMapper = new ProjectMapper();

//	public CheckOutIssue(ArrayList<Long> list, CreateProject CP) {
//		this.IssueIDList = list;
//		this.CP = CP;
//	}
	
	public CheckOutIssue(List<IIssue> list, CreateProject CP) {
		this.issueList = list;
		this.CP = CP;
	}

//	public CheckOutIssue(ArrayList<Long> list, CreateProject CP, Date setDate) {
//		this.IssueIDList = list;
//		this.CP = CP;
//		this.SetDoneDate = setDate;
//	}
	
	public CheckOutIssue(List<IIssue> list, CreateProject CP, Date setDate) {
		this.issueList = list;
		this.CP = CP;
		this.SetDoneDate = setDate;
	}

	public void exeReset_Issues() throws Exception {
		IUserSession userSession = configuration.getUserSession();

		for (int i = 0; i < this.CP.getProjectList().size(); i++) {
			String projectName = this.CP.mProjectName + Integer.toString((i + 1)); // TEST_PROJECT_X
//			IProject project = ResourceFacade.getWorkspace().getRoot().getProject(projectName);
			IProject project = this.projectMapper.getProjectByID(projectName);

//			// SprintBacklog SB = new SprintBacklog(project, CreateUserSession());
//			SprintBacklogMapper SB = new SprintBacklogMapper(project, userSession);
			
			SprintBacklogMapper sprintBacklogMapper = (new SprintBacklogLogic(project, userSession, null)).getSprintBacklogMapper();

			for (long ID : this.IssueIDList) {
				if (this.SetDoneDate != null) {
					SimpleDateFormat format = new SimpleDateFormat(DateUtil._16DIGIT_DATE_TIME);
					sprintBacklogMapper.resetTask(ID, "", format.format(this.SetDoneDate), "");
				} else {
					sprintBacklogMapper.resetTask(ID, "", "", "");
				}
				System.out.println("移動 Issue " + ID + " 到 Non Check-out 成功");
			}
		}
	}

	public void exeCheckOut_Issues() throws Exception {
		IUserSession userSession = configuration.getUserSession();

		for (int i = 0; i < this.CP.getProjectList().size(); i++) {
			String projectName = this.CP.mProjectName + Integer.toString((i + 1)); // TEST_PROJECT_X
//			IProject project = ResourceFacade.getWorkspace().getRoot().getProject(projectName);
			IProject project = this.projectMapper.getProjectByID(projectName);

//			// SprintBacklog SB = new SprintBacklog(project, CreateUserSession());
//			SprintBacklogMapper SB = new SprintBacklogMapper(project, userSession);
			
			SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, userSession, null);

			for (long ID : this.IssueIDList) {
				if (this.SetDoneDate != null) {
					SimpleDateFormat format = new SimpleDateFormat(DateUtil._16DIGIT_DATE_TIME);
					sprintBacklogLogic.checkOutTask(ID, configuration.USER_ID, "", "", format.format(this.SetDoneDate), "");
				} else {
					sprintBacklogLogic.checkOutTask(ID, configuration.USER_ID, "", "", "", "");
				}
				System.out.println("移動 Issue " + ID + " 到 Check-out 成功");
			}
		}
	}

	public void exeDone_Issues() throws Exception {
		IUserSession userSession = configuration.getUserSession();
		String handler = userSession.getAccount().getId();

		for (int i = 0; i < this.CP.getProjectList().size(); i++) {
//			String projectName = this.CP.PJ_NAME + Integer.toString((i + 1)); // TEST_PROJECT_X
//			IProject project = ResourceFacade.getWorkspace().getRoot().getProject(projectName);
			IProject project = this.CP.getProjectList().get(i);

//			// SprintBacklog SB = new SprintBacklog(project, CreateUserSession());
//			SprintBacklogMapper SB = new SprintBacklogMapper(project, userSession);
			
			SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, userSession, null);
			SprintBacklogMapper sprintBacklogMapper = sprintBacklogLogic.getSprintBacklogMapper();

			for (IIssue issue : this.issueList) {
				long ID = issue.getIssueID();
				String name = issue.getSummary();
				if (this.SetDoneDate != null) {
					SimpleDateFormat format = new SimpleDateFormat(DateUtil._16DIGIT_DATE_TIME);
					sprintBacklogMapper.getIssue(ID).setAssignto(handler);
					sprintBacklogLogic.doneIssue(ID, name, format.format(this.SetDoneDate), "", "");
				} else {
					sprintBacklogMapper.getIssue(ID).setAssignto(handler);
					sprintBacklogLogic.doneIssue(ID, name, "", "", "");
				}
				System.out.println("移動 Issue " + ID + " 到 Done 成功");
			}
		}
	}
}
