package ntut.csie.ezScrum.test.CreateData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.dao.HistoryDAO;
import ntut.csie.ezScrum.dao.ProjectDAO;
import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.issue.internal.Issue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.internal.MantisService;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.databasEnum.IssueTypeEnum;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.ezScrum.web.mapper.ProductBacklogMapper;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

public class CreateTask {
	private static Log log = LogFactory.getLog(CreateTask.class);

	private int ProjectCount = 1;
	private int TaskCount = 1;

	private String TEST_TASK_NAME = "TEST_TASK_"; // Task Name
	private String TEST_TASK_NOTE = "TEST_TASK_NOTES_"; // Task Notes
	private int TEST_TASK_EST = 2; // Task estimation
	private long TEST_HANDLER = -1;
	private ArrayList<Long> TEST_PARTNER = new ArrayList<Long>();
	private Date SpecificTime;

	private CreateProject CP;

	// ========================== 為了可以設定 task 而新增下列屬性
	// ===========================
	private boolean AutoSetTask = true;
	private long StoryID = 1;

	private ArrayList<Long> TaskIdList = new ArrayList<Long>();
	private ArrayList<TaskObject> TaskList = new ArrayList<TaskObject>();

	private IUserSession userSession = new UserSession(
			new AccountMapper().getAccount("admin"));

	public CreateTask(int count, int EstValue, long StoryID, CreateProject CP) {
		this.TaskCount = count;
		this.TEST_TASK_EST = EstValue;
		this.StoryID = StoryID;
		this.AutoSetTask = false;
		this.CP = CP;

		Calendar cal = Calendar.getInstance();
		this.SpecificTime = cal.getTime();
	}

	public CreateTask(int count, CreateProject CP) {
		this.TaskCount = count;
		this.ProjectCount = CP.getProjectList().size();
		this.CP = CP;
		Calendar cal = Calendar.getInstance();
		this.SpecificTime = cal.getTime();

		this.AutoSetTask = true;
	}

	public ArrayList<Long> getTaskIDList() {
		return this.TaskIdList;
	}

	public String getDefault_TASK_NAME(int i) {
		return (TEST_TASK_NAME + Integer.toString(i));
	}

	public int getDefault_TASK_EST() {
		return TEST_TASK_EST;
	}

	public String getDefault_TASK_NOTE(int i) {
		return (TEST_TASK_NOTE + Integer.toString(i));
	}

	public List<TaskObject> getTaskList() {
		return this.TaskList;
	}

	public void exe() throws Exception {
		if (this.AutoSetTask) {
			initial_Spint_Story();

			for (int i = 0; i < this.ProjectCount; i++) {
				// 此路徑為開發端的 TestData/MyWorkspace/
				IProject project = this.CP.getProjectList().get(i);

				long Default_storyID = 1;

				for (int j = 0; j < this.TaskCount; j++) {
					String TaskName = getDefault_TASK_NAME(j + 1);
					String TaskNote = getDefault_TASK_NOTE(j + 1);

					TaskObject task = this.addTask(project, TaskName,
							this.TEST_TASK_EST, this.TEST_HANDLER,
							new ArrayList<Long>(), TaskNote, Default_storyID,
							this.SpecificTime);
					this.TaskList.add(task);
					this.TaskIdList.add(task.getId());
					this.log.info("專案 " + project.getName() + " 在 Story ID: "
							+ Default_storyID + " 新增一筆 Task ID: " + task.getId());
				}
			}
		} else {
			for (int j = 0; j < this.TaskCount; j++) {
				IProject project = this.CP.getProjectList().get(0);

				String TaskName = getDefault_TASK_NAME(j + 1);
				String TaskNote = getDefault_TASK_NOTE(j + 1);

				TaskObject task = this.addTask(project, TaskName,
						this.TEST_TASK_EST, this.TEST_HANDLER,
						new ArrayList<Long>(), TaskNote, this.StoryID,
						this.SpecificTime);

				this.TaskList.add(task);
				this.TaskIdList.add(task.getId());
				this.log.info("在 Story ID: " + this.StoryID + " 新增一筆 Task ID: "
						+ task.getId());
			}
		}
	}

	// initial create one sprint and one product backlog
	private void initial_Spint_Story() throws Exception {
		// 新建一個 sprint
		CreateSprint CS = new CreateSprint(1, this.CP);
		CS.exe();

		AddStoryToSprint storyTosprint = new AddStoryToSprint(1, 2, CS,
				this.CP, CreateProductBacklog.TYPE_ESTIMATION);
		storyTosprint.exe(); // 執行 - 將 stories 區分到每個 sprints
	}

	private TaskObject addTask(IProject p, String name, int estimate, long handlerId,
			ArrayList<Long> partners, String notes, long storyId, Date date) {
		long projectId = ProjectDAO.getInstance().get(p.getName()).getId();
		TaskObject task = new TaskObject(projectId);
		task.setName(name).setNotes(notes).setEstimate(estimate)
				.setHandlerId(handlerId).setStoryId(storyId)
				.setCreateTime(date.getTime()).save();
		task.setPartnersId(partners);
		task.save();
		return task;
	}
}
