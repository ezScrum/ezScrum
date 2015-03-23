package ntut.csie.ezScrum.web.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.control.TaskBoard;
import ntut.csie.ezScrum.web.dataObject.AttachFileObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.ezScrum.web.support.Translation;
import ntut.csie.jcis.resource.core.IProject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TaskboardHelper {
	private IProject project;
	private IUserSession userSession;
	private SprintBacklogLogic sprintBacklogLogic;
	private SprintBacklogMapper sprintBacklogMapper;
	private String sprintID;

	/**
	 * for web service
	 * 
	 * @param project
	 * @param userSession
	 */
	public TaskboardHelper(IProject project, IUserSession userSession) {
		this.project = project;
		this.userSession = userSession;
		this.sprintBacklogLogic = new SprintBacklogLogic(this.project, this.userSession, null);
		this.sprintBacklogMapper = this.sprintBacklogLogic.getSprintBacklogMapper();
	}

	public TaskboardHelper(IProject project, IUserSession userSession, String sprintID) {
		this.project = project;
		this.userSession = userSession;
		this.sprintID = sprintID;
		this.sprintBacklogLogic = new SprintBacklogLogic(this.project, this.userSession, this.sprintID);
		this.sprintBacklogMapper = this.sprintBacklogLogic.getSprintBacklogMapper();
	}
	
	/**
	 * 傳出Story或Task burndown chart的數值json
	 */
	public String getSprintBurndownChartDataResponseText(String type) {
		String responseText = "";
		int sprintCount = (new SprintPlanHelper(project).loadListPlans()).size();
		// backlog = null 代表沒有Sprint資訊
		if (sprintCount != 0) {
			// Get TaskBoard Data
			TaskBoard taskBoard = new TaskBoard(sprintBacklogLogic, sprintBacklogMapper);
			// Get Sprint Data
			if (taskBoard != null) {
				Translation tans = new Translation();
				if (type.equals("story")) {
					responseText = tans.translateBurndownChartDataToJson(taskBoard.getStoryIdealPointMap(), taskBoard.getStoryRealPointMap());
				} else if (type.equals("task")) {
					responseText = tans.translateBurndownChartDataToJson(taskBoard.getTaskIdealPointMap(), taskBoard.getTaskRealPointMap());
				}
			}
		} else {
			responseText = "{\"Points\":[],\"success\":true}";
		}
		return responseText;
	}

	/**
	 * 將TaskBoard上所需要的SprintInfo包成sprintInfoUI物件以json丟出去
	 */
	public StringBuilder getSprintInfoForTaskBoardText() {
		SprintInfoUI sprintInfoUI = null;
		// 如果Sprint存在的話，那麼就取出此Sprint的資料以回傳
		if ((sprintBacklogMapper != null) && (sprintBacklogMapper.getSprintId() > 0)) {
			int currentSprintID = sprintBacklogMapper.getSprintId();
			double currentPoint = sprintBacklogLogic.getUnclosePoint(ScrumEnum.STORY_ISSUE_TYPE);
			double currentHours = sprintBacklogLogic.getUnclosePoint(ScrumEnum.TASK_ISSUE_TYPE);
			boolean isCurrentSprint = false;
			ReleasePlanHelper rpHelper = new ReleasePlanHelper(project);
			String releaseID = rpHelper.getReleaseID(Integer.toString(currentSprintID));
			if (sprintBacklogMapper.getSprintEndDate().getTime() > (new Date()).getTime()) {
				isCurrentSprint = true;
			}
			sprintInfoUI = new SprintInfoUI(currentSprintID, sprintBacklogMapper.getSprintGoal(), currentPoint, currentHours, releaseID, isCurrentSprint);
		} else {
			sprintInfoUI = new SprintInfoUI();
		}
		Gson gson = new Gson();
		return new StringBuilder(gson.toJson(sprintInfoUI));
	}
	
	/**
	 * 將TaskBoard上所顯示的Story與Task以json方式丟出資料
	 */
	public StringBuilder getTaskBoardStoryTaskListTest(String name) {
		int storyLength = 0;
		ArrayList<TaskBoard_Story> storyList = new ArrayList<TaskBoard_Story>();

		if ((sprintBacklogMapper != null) && (sprintBacklogMapper.getSprintId() > 0)) {
			List<IIssue> stories = sprintBacklogLogic.getStoriesByImp();		// 根據Sprint的importance來取Story
			Map<Long, ArrayList<TaskObject>> taskMap = sprintBacklogMapper.getTasksMap(); 	// 取出Sprint中所有的task
			stories = this.filterStory(stories, taskMap, name);					// filter story

			for (IIssue story : stories) {
				storyList.add(create_TaskBoard_Story(story, taskMap.get(story.getIssueID())));
			}
			storyLength = stories.size();
		}
		else { // no sprint exist
			storyLength = 0;
		}

		Gson gson = new GsonBuilder().disableHtmlEscaping().create();;
		LinkedHashMap<String, Object> jsonMap = new LinkedHashMap<String, Object>();
		jsonMap.put("Stories", storyList);
		jsonMap.put("success", true);
		jsonMap.put("Total", storyLength);
		return new StringBuilder(gson.toJson(jsonMap));
	}
	
	private class SprintInfoUI {
		private int ID = 0;
		private String SprintGoal = "";
		private double CurrentStoryPoint = 0d;
		private double CurrentTaskPoint = 0d;
		private String ReleaseID = "Release None";
		private boolean isCurrentSprint = false;

		public SprintInfoUI() {}

		public SprintInfoUI(int ID, String goal, double sp, double tp, String rid, boolean current) {
			this.ID = ID;
			this.SprintGoal = goal;
			this.CurrentStoryPoint = sp;
			this.CurrentTaskPoint = tp;
			this.ReleaseID = "Release #" + rid;
			this.isCurrentSprint = current;
		}
	}
	
	private List<IIssue> filterStory(List<IIssue> stories, Map<Long, ArrayList<TaskObject>> taskmap, String filtername) {
		List<IIssue> filterissues = new LinkedList<IIssue>();
		// All member, return all story
		if (filtername.equals("ALL") || filtername.length() == 0) {
			return stories;
		} else {
			// filter member name by handler, return the story and task map relation
			for (IIssue story : stories) {
				ArrayList<TaskObject> tasks = taskmap.get(story.getIssueID());
				if (tasks != null) {
					ArrayList<TaskObject> filtertask = new ArrayList<TaskObject>();

					for (TaskObject task : tasks) {
						String handlerUserName = task.getHandler() != null ? task.getHandler().getUsername() : "";
						if (checkParent(filtername, task.getPartnersUsername(), handlerUserName)) {
							filtertask.add(task);
						}
					}

					if (filtertask.size() > 0) {
						// cover new filter map
						taskmap.put(story.getIssueID(), filtertask);
						filterissues.add(story);
					}
				}
			}
			return filterissues;
		}
	}

	// if partner of assignto is equals usename, return it
	public boolean checkParent(String name, String partners, String assignto) {
		String[] parents = partners.split(";");
		for (String p : parents) {
			if (name.compareTo(p) == 0) return true;
		}

		if (name.compareTo(assignto) == 0) return true;

		return false;
	}

	private TaskBoard_Story create_TaskBoard_Story(IIssue story, ArrayList<TaskObject> tasks) {
		TaskBoard_Story TB_Story = new TaskBoard_Story(story);

		if (tasks != null) {
			for (TaskObject task : tasks) {
				TB_Story.Tasks.add(new TaskBoard_Task(task));
			}
		}
		return TB_Story;
	}

	/**
	 * 嚙磊嚙踝蕭嚙瞌嚙踝蕭嚙豎也嚙磅嚙踝蕭嚙緣嚙瞌嚙踝蕭嚙誕堆蕭
	 * Inner Class嚙璀嚙瞎嚙踝蕭峔荓NStory嚙踝蕭Task嚙賞成GSon嚙箠嚙瘡嚙賞換嚙踝蕭嚙踝蕭嚙踝蕭
	 * 
	 * @author OPH
	 * 
	 */
	Translation tr = new Translation();

	private class TaskBoard_Story {
		String Id;
		String Name;
		String Value;
		String Estimate;
		String Importance;
		String Tag;
		String Status;
		String Notes;
		String HowToDemo;
		String Link;
		String Release;
		String Sprint;
		Boolean Attach;
		List<TaskBoard_AttachFile> AttachFileList;
		ArrayList<TaskBoard_Task> Tasks;

		public TaskBoard_Story(IIssue story) {
			Id = Long.toString(story.getIssueID());
			Name = HandleSpecialChar(story.getSummary());
			Value = story.getValue();
			Estimate = story.getEstimated();
			Importance = story.getImportance();
			Tag = tr.Join(story.getTags(), ",");
			Status = story.getStatus();
			Notes = HandleSpecialChar(story.getNotes());
			HowToDemo = HandleSpecialChar(story.getHowToDemo());
			Release = story.getReleaseID();
			Sprint = story.getSprintID();

			Link = story.getIssueLink();
			AttachFileList = getAttachFilePath(story, story.getAttachFiles());

			if (!AttachFileList.isEmpty()) Attach = true;
			else Attach = false;

			Tasks = new ArrayList<TaskBoard_Task>();
		}
	}

	private class TaskBoard_Task {
		String Id;
		String Name;
		String Estimate;
		String RemainHours;
		String HandlerUserName;
		String Notes;
		List<TaskBoard_AttachFile> AttachFileList;
		Boolean Attach;
		String Status;
		String Partners;
		String Link;
		String Actual;
		
		public TaskBoard_Task(TaskObject task) {
			Id = Long.toString(task.getId());
			Name = HandleSpecialChar(task.getName());
			Estimate = String.valueOf(task.getEstimate());
			RemainHours = String.valueOf(task.getRemains());
			Actual = String.valueOf(task.getActual());
			HandlerUserName = task.getHandler() == null ? "" : task.getHandler().getUsername();
			Partners = task.getPartnersUsername();
			Status = task.getStatusString();
			Notes = HandleSpecialChar(task.getNotes());
			Link = "";
			AttachFileList = getAttachFilePath(task, task.getAttachFiles());
			if (!AttachFileList.isEmpty()) Attach = true;
			else Attach = false;
		}
	}

	private class TaskBoard_AttachFile {
		long FileId;
		String FileName;
		String FilePath;
		Date UploadDate;

		public TaskBoard_AttachFile(long id, String name, String path, Date date) {
			FileId = id;
			FileName = name;
			FilePath = path;
			UploadDate = date;
		}

		public Date getUploadDate() {
			return UploadDate;
		}
	}


	private ArrayList<TaskBoard_AttachFile> getAttachFilePath(IIssue story, List<AttachFileObject> list) {

		ArrayList<TaskBoard_AttachFile> array = new ArrayList<TaskBoard_AttachFile>();
		for (AttachFileObject file : list) {
			array.add(new TaskBoard_AttachFile(file.getId(), file.getName(), "fileDownload.do?projectName="
			        + story.getProjectName() + "&fileId=" + file.getId() + "&fileName=" + file.getName()
			        , new Date(file.getCreateTime())));
		}
		return array;
	}
	
	private ArrayList<TaskBoard_AttachFile> getAttachFilePath(TaskObject task, List<AttachFileObject> list) {

		ArrayList<TaskBoard_AttachFile> array = new ArrayList<TaskBoard_AttachFile>();
		for (AttachFileObject file : list) {
			ProjectObject project = ProjectObject.get(task.getProjectId());
			String projectName = project.getName();
			array.add(new TaskBoard_AttachFile(file.getId(), file.getName(), "fileDownload.do?projectName="
			        + projectName + "&fileId=" + file.getId() + "&fileName=" + file.getName()
			        , new Date(file.getCreateTime())));
		}
		return array;
	}

	private String HandleSpecialChar(String str) {
		if (str.contains("\n")) {
			str = str.replaceAll("\n", "<br/>");
		}

		return str;
	}
}
