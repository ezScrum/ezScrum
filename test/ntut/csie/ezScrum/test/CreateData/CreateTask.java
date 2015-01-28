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

	private int mProjectAmount = 1;
	private int mTaskAmount = 1;

	private String TEST_TASK_NAME = "TEST_TASK_"; // Task Name
	private String TEST_TASK_NOTE = "TEST_TASK_NOTES_"; // Task Notes
	private int TEST_TASK_EST = 2; // Task estimation
	private long TEST_HANDLER = -1;
	private ArrayList<Long> TEST_PARTNERS_ID = new ArrayList<Long>();
	private Date mSpecificTime;

	private CreateProject mCP;

	// ========================== 為了可以設定 task 而新增下列屬性
	// ===========================
	private boolean mDoesAutoSetStoryId = true;
	private long mStoryId = 1;

	private ArrayList<Long> mTasksId = new ArrayList<Long>();
	private ArrayList<TaskObject> mTasks = new ArrayList<TaskObject>();

	public CreateTask(int count, int EstValue, long StoryID, CreateProject CP) {
		mTaskAmount = count;
		TEST_TASK_EST = EstValue;
		mStoryId = StoryID;
		mDoesAutoSetStoryId = false;
		mCP = CP;

		Calendar cal = Calendar.getInstance();
		mSpecificTime = cal.getTime();
	}

	public CreateTask(int count, CreateProject CP) {
		mTaskAmount = count;
		mProjectAmount = CP.getProjectList().size();
		mCP = CP;
		Calendar cal = Calendar.getInstance();
		mSpecificTime = cal.getTime();

		mDoesAutoSetStoryId = true;
	}

	public ArrayList<Long> getTaskIDList() {
		return mTasksId;
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
		return mTasks;
	}

	public void exe() throws Exception {
		if (mDoesAutoSetStoryId) {
			initial_Spint_Story();

			for (int i = 0; i < mProjectAmount; i++) {
				// 此路徑為開發端的 TestData/MyWorkspace/
				IProject project = mCP.getProjectList().get(i);

				long Default_storyID = 1;

				for (int j = 0; j < mTaskAmount; j++) {
					String TaskName = getDefault_TASK_NAME(j + 1);
					String TaskNote = getDefault_TASK_NOTE(j + 1);

					TaskObject task = addTask(project, TaskName,
							TEST_TASK_EST, TEST_HANDLER,
							new ArrayList<Long>(), TaskNote, Default_storyID,
							mSpecificTime);
					mTasks.add(task);
					mTasksId.add(task.getId());
					log.info("專案 " + project.getName() + " 在 Story ID: "
							+ Default_storyID + " 新增一筆 Task ID: " + task.getId());
				}
			}
		} else {
			for (int j = 0; j < mTaskAmount; j++) {
				IProject project = mCP.getProjectList().get(0);

				String TaskName = getDefault_TASK_NAME(j + 1);
				String TaskNote = getDefault_TASK_NOTE(j + 1);

				TaskObject task = addTask(project, TaskName,
						TEST_TASK_EST, TEST_HANDLER,
						new ArrayList<Long>(), TaskNote, mStoryId,
						mSpecificTime);

				mTasks.add(task);
				mTasksId.add(task.getId());
				log.info("在 Story ID: " + mStoryId + " 新增一筆 Task ID: "
						+ task.getId());
			}
		}
	}

	// initial create one sprint and one product backlog
	private void initial_Spint_Story() throws Exception {
		// 新建一個 sprint
		CreateSprint CS = new CreateSprint(1, mCP);
		CS.exe();

		AddStoryToSprint storyTosprint = new AddStoryToSprint(1, 2, CS,
				mCP, CreateProductBacklog.TYPE_ESTIMATION);
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
