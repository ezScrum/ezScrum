package ntut.csie.ezScrum.test.CreateData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

public class CheckOutIssue {
	private ArrayList<Long> mIssueIDList = new ArrayList<Long>();
	private List<IIssue> mIssueList;
	private ArrayList<TaskObject> mTasks;
	private CreateProject mCP;
	private Date mSetDoneDate = null;
	private Configuration mConfiguration = new Configuration();
	private ProjectMapper mProjectMapper = new ProjectMapper();

	
	public CheckOutIssue(List<IIssue> list, CreateProject CP) {
		mIssueList = list;
		mCP = CP;
	}
	
	public CheckOutIssue(List<IIssue> list, CreateProject CP, Date setDate) {
		mIssueList = list;
		mCP = CP;
		mSetDoneDate = setDate;
	}
	
	public CheckOutIssue(ArrayList<TaskObject> tasks, CreateProject CP) {
		mTasks = tasks;
		mCP = CP;
	}

	public void exeReset_Issues() throws Exception {
		IUserSession userSession = mConfiguration.getUserSession();

		for (int i = 0; i < mCP.getProjectList().size(); i++) {
			String projectName = mCP.mProjectName + Integer.toString((i + 1)); // TEST_PROJECT_X
//			IProject project = ResourceFacade.getWorkspace().getRoot().getProject(projectName);
			IProject project = mProjectMapper.getProjectByID(projectName);

//			// SprintBacklog SB = new SprintBacklog(project, CreateUserSession());
//			SprintBacklogMapper SB = new SprintBacklogMapper(project, userSession);
			
			SprintBacklogMapper sprintBacklogMapper = (new SprintBacklogLogic(project, userSession, null)).getSprintBacklogMapper();

			for (long ID : mIssueIDList) {
				if (mSetDoneDate != null) {
					SimpleDateFormat format = new SimpleDateFormat(DateUtil._16DIGIT_DATE_TIME);
					sprintBacklogMapper.resetTask(ID, "", format.format(mSetDoneDate), new Date());
				} else {
					sprintBacklogMapper.resetTask(ID, "", "", new Date());
				}
				System.out.println("移動 Issue " + ID + " 到 Non Check-out 成功");
			}
		}
	}

	public void exeCheckOut_Issues() throws Exception {
		IUserSession userSession = mConfiguration.getUserSession();

		for (int i = 0; i < mCP.getProjectList().size(); i++) {
			String projectName = mCP.mProjectName + Integer.toString((i + 1)); // TEST_PROJECT_X
//			IProject project = ResourceFacade.getWorkspace().getRoot().getProject(projectName);
			IProject project = mProjectMapper.getProjectByID(projectName);

//			// SprintBacklog SB = new SprintBacklog(project, CreateUserSession());
//			SprintBacklogMapper SB = new SprintBacklogMapper(project, userSession);
			
			SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, userSession, null);

			for (long ID : mIssueIDList) {
				if (mSetDoneDate != null) {
					SimpleDateFormat format = new SimpleDateFormat(DateUtil._16DIGIT_DATE_TIME);
					sprintBacklogLogic.checkOutTask(ID, mConfiguration.USER_ID, "", "", format.format(mSetDoneDate), "");
				} else {
					sprintBacklogLogic.checkOutTask(ID, mConfiguration.USER_ID, "", "", "", "");
				}
				System.out.println("移動 Issue " + ID + " 到 Check-out 成功");
			}
		}
	}

	// for closing story
	public void exeDone_Issues() throws Exception {
		IUserSession userSession = mConfiguration.getUserSession();
		String handler = userSession.getAccount().getUsername();

		for (int i = 0; i < mCP.getProjectList().size(); i++) {
//			String projectName = CP.PJ_NAME + Integer.toString((i + 1)); // TEST_PROJECT_X
//			IProject project = ResourceFacade.getWorkspace().getRoot().getProject(projectName);
			IProject project = mCP.getProjectList().get(i);

//			// SprintBacklog SB = new SprintBacklog(project, CreateUserSession());
//			SprintBacklogMapper SB = new SprintBacklogMapper(project, userSession);
			
			SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, userSession, null);
			SprintBacklogMapper sprintBacklogMapper = sprintBacklogLogic.getSprintBacklogMapper();

			for (IIssue issue : mIssueList) {
				long ID = issue.getIssueID();
				String name = issue.getSummary();
				SimpleDateFormat format = new SimpleDateFormat(DateUtil._16DIGIT_DATE_TIME);
				if (mSetDoneDate != null) {
					sprintBacklogMapper.getStory(ID).setAssignto(handler);
					sprintBacklogLogic.closeStory(ID, name, format.format(mSetDoneDate));
				} else {
					sprintBacklogMapper.getStory(ID).setAssignto(handler);
					sprintBacklogLogic.closeStory(ID, name, format.format(new Date()));
				}
				System.out.println("移動 Issue " + ID + " 到 Done 成功");
			}
		}
	}
	
	// for closing task
	public void exeDone_Tasks() throws Exception {
		IUserSession userSession = mConfiguration.getUserSession();
		String handler = userSession.getAccount().getUsername();

		for (int i = 0; i < mCP.getProjectList().size(); i++) {
//			String projectName = CP.PJ_NAME + Integer.toString((i + 1)); // TEST_PROJECT_X
//			IProject project = ResourceFacade.getWorkspace().getRoot().getProject(projectName);
			IProject project = mCP.getProjectList().get(i);

//			// SprintBacklog SB = new SprintBacklog(project, CreateUserSession());
//			SprintBacklogMapper SB = new SprintBacklogMapper(project, userSession);
			
			SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, userSession, null);
			SprintBacklogMapper sprintBacklogMapper = sprintBacklogLogic.getSprintBacklogMapper();

			for (TaskObject task : mTasks) {
				long ID = task.getId();
				String name = task.getName();
				String notes = task.getNotes();
				int actual = task.getActual();
				SimpleDateFormat format = new SimpleDateFormat(DateUtil._16DIGIT_DATE_TIME);
				if (mSetDoneDate != null) {
					sprintBacklogMapper.getStory(ID).setAssignto(handler);
					sprintBacklogLogic.closeTask(ID, name, notes, actual, format.format(mSetDoneDate));
				} else {
					sprintBacklogMapper.getStory(ID).setAssignto(handler);
					sprintBacklogLogic.closeTask(ID, name, notes, actual, format.format(new Date()));
				}
				System.out.println("移動 Issue " + ID + " 到 Done 成功");
			}
		}
	}
}
