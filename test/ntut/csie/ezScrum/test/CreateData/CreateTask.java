package ntut.csie.ezScrum.test.CreateData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.issue.internal.Issue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IITSService;
import ntut.csie.ezScrum.issue.sql.service.core.ITSServiceFactory;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.IUserSession;
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
	
	private String TEST_TASK_NAME = "TEST_TASK_";			// Task Name
	private String TEST_TASK_DESC = "TEST_TASK_DESC_";		// Task Desc
	private String TEST_TASK_EST = "2";						// Task estimation
	private String TEST_HANDLER = "";
	private String TEST_PARTNER = "";
	private String TEST_TASK_NOTE = "TEST_TASK_NOTES_";		// Task Notes
	private Date SpecificTime;
	
	private CreateProject CP;
	
	// ========================== 為了可以設定 task 而新增下列屬性 ===========================
	private boolean AutoSetTask = true;
	private long StoryID = 1;
	
	private ArrayList<Long> TaskIDList = new ArrayList<Long>();
	private List<IIssue> TaskList = new ArrayList<IIssue>();
	ezScrumInfoConfig config = new ezScrumInfoConfig();
	
	public CreateTask(int count, int EstValue, long StoryID, CreateProject CP) {
		this.TaskCount = count;
		this.TEST_TASK_EST = Integer.toString(EstValue);
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
		return this.TaskIDList;
	}
	
	public String getDefault_TASK_NAME(int i) {
		return (TEST_TASK_NAME + Integer.toString(i));
	}
	
	public String getDefault_TASK_DESC(int i) {
		return (TEST_TASK_DESC+ Integer.toString(i));
	}
	
	public String getDefault_TASK_EST() {
		return TEST_TASK_EST;
	}
	
	public String getDefault_TASK_NOTE(int i) {
		return (TEST_TASK_NOTE+ Integer.toString(i));
	}
	
	public List<IIssue> getTaskList(){
		return this.TaskList;
	}
	
	public void exe() throws Exception {
		IUserSession userSession = (new ezScrumInfoConfig()).getUserSession();
		if (this.AutoSetTask) {
			initial_Spint_Story();
			
			for (int i=0 ; i<this.ProjectCount ; i++) {
//				String projectName = this.CP.PJ_NAME + Integer.toString((i+1));	// TEST_PROJECT_X
//				IProject project = ResourceFacade.getWorkspace().getRoot().getProject(projectName);
				// 此路徑為開發端的   TestData/MyWorkspace/
				IProject project = this.CP.getProjectList().get(i);
				ProductBacklogMapper productBacklogMapper = new ProductBacklogMapper(project, userSession);
				
				long Default_storyID = 1;
				
				for (int j=0 ; j<this.TaskCount ; j++) {
					String TaskName = getDefault_TASK_NAME(j+1);
					String TaskDesc = getDefault_TASK_DESC(j+1);
					String TaskNote = getDefault_TASK_NOTE(j+1);
				
					long TaskID = this.addTask(project, TaskName, TaskDesc, this.TEST_TASK_EST, this.TEST_HANDLER, this.TEST_PARTNER, 
							TaskNote, Default_storyID, this.SpecificTime);
					this.TaskList.add(productBacklogMapper.getIssue(TaskID));
					this.TaskIDList.add(TaskID);
					this.log.info("專案 " + project.getName() + " 在 Story ID: " + Default_storyID + " 新增一筆 Task ID: " + TaskID);
				}	
			}
		} else {
			for (int j=0 ; j<this.TaskCount ; j++) {
				IProject p = this.CP.getProjectList().get(0);
				ProductBacklogMapper productBacklogMapper = new ProductBacklogMapper(p, userSession);
				
				String TaskName = getDefault_TASK_NAME(j+1);
				String TaskDesc = getDefault_TASK_DESC(j+1);
				String TaskNote = getDefault_TASK_NOTE(j+1);
			
				long TaskID = this.addTask(p, TaskName, TaskDesc, this.TEST_TASK_EST, this.TEST_HANDLER, this.TEST_PARTNER, 
						TaskNote, this.StoryID, this.SpecificTime);
				
				this.TaskList.add(productBacklogMapper.getIssue(TaskID));
				this.TaskIDList.add(TaskID);
				this.log.info("在 Story ID: " + this.StoryID + " 新增一筆 Task ID: " + TaskID);
			}
		}
	}
	
	// initial create one sprint and one product backlog
	private void initial_Spint_Story() throws Exception {
		// 新建一個 sprint
		CreateSprint CS = new CreateSprint(1, this.CP);
		CS.exe();
		
		AddStoryToSprint storyTosprint = new AddStoryToSprint(1, 2, CS, this.CP, CreateProductBacklog.TYPE_ESTIMATION);
		storyTosprint.exe();		// 執行 - 將 stories 區分到每個 sprints
	}
	
	private long addTask(IProject p, String name, String description, String estimation,
			String handler, String partners, String notes, long storyID,
			Date date) {
		IIssue task = new Issue();
		task.setProjectID(p.getName());
		task.setSummary(name);
		task.setDescription(description);
		task.setCategory(ScrumEnum.TASK_ISSUE_TYPE);
		
		//ITSPrefsStorage itsPrefs = new ITSPrefsStorage(p, config.getUserSession());
		Configuration configuration = new Configuration(config.getUserSession());
		ITSServiceFactory itsFactory = ITSServiceFactory.getInstance();

		IITSService itsService = itsFactory.getService(configuration);
		itsService.openConnect();
		
		long taskID = itsService.newIssue(task);
		task = itsService.getIssue(taskID);
		
		String actualHour = "0";

		// 利用edit來增加estimation的tag
		// 剛新增Task時Remaining = estimation
		editTask(itsService, task, name, estimation, estimation, handler,
				partners, actualHour, notes, date);

		// 新增關係
		itsService.addRelationship(storyID, task.getIssueID(),
				ITSEnum.PARENT_RELATIONSHIP, date);

		// 因使用暫存的方式來加速存取速度,所以當有變動時則需更新
		itsService.closeConnect();
		return task.getIssueID();
	}
	
	private boolean editTask(IITSService itsService, IIssue task, String Name, String estimation,
			String remains, String handler, String partners, String actualHour,
			String notes, Date modifyDate) {
		// 先變更handler
		modify(itsService, task, Name, handler, modifyDate);

		Element history = new Element(ScrumEnum.HISTORY_TAG);
		// history.setAttribute(IIssue.TYPE_HISTORY_ATTR,
		// IIssue.STORY_TYPE_HSITORY_VALUE);
		history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, DateUtil.format(
				(modifyDate == null ? new Date() : modifyDate),
				DateUtil._16DIGIT_DATE_TIME_2));

		if (estimation != null && !estimation.equals("")) {
			if (!task.getEstimated().equals(estimation)) {
				Element storyPoint = new Element(ScrumEnum.ESTIMATION);
				storyPoint.setText(estimation);
				history.addContent(storyPoint);
			}
		}
		if (remains != null && !remains.equals("")) {
			if (!task.getRemains().equals(remains)) {
				Element remainingPoints = new Element(ScrumEnum.REMAINS);
				remainingPoints.setText(remains);
				history.addContent(remainingPoints);
			}
		}
		if (!task.getPartners().equals(partners)) {
			Element element = new Element(ScrumEnum.PARTNERS);
			element.setText(partners.replaceAll("'", "''"));
			history.addContent(element);
		}
		if (notes != null) {
			if (!task.getNotes().equals(notes)) {
				Element element = new Element(ScrumEnum.NOTES);
				element.setText(notes.replaceAll("'", "''"));
				history.addContent(element);
			}
		}
		if (actualHour != null && !actualHour.equals("")) {
			if (!task.getActualHour().equals(actualHour)) {
				Element element = new Element(ScrumEnum.ACTUALHOUR);
				element.setText(actualHour);
				history.addContent(element);
			}
		}

		if (history.getChildren().size() > 0) {
			task.addTagValue(history);
			// 最後將修改的結果更新至DB
			itsService.updateBugNote(task);
			return true;
		}
		
		return false;
	}
	
	private void modify(IITSService itsService, IIssue task, String Name, String handler,
			Date modifyDate) {
		if (!task.getAssignto().equals(handler)) {
			itsService.updateHandler(task, handler, modifyDate);
		}
		
		if (!task.getSummary().equals(Name) && Name != null) {
			itsService.updateName(task, Name, modifyDate);
		}
	}
}
