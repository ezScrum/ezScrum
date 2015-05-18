package ntut.csie.ezScrum.web.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ntut.csie.ezScrum.web.control.TaskBoard;
import ntut.csie.ezScrum.web.dataObject.AttachFileObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.ezScrum.web.support.Translation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TaskBoardHelper {
	private ProjectObject mProject;
	private SprintBacklogLogic mSprintBacklogLogic;
	private SprintBacklogMapper mSprintBacklogMapper;
	private long mSprintId;

	/**
	 * for web service
	 * 
	 * @param project
	 * @param userSession
	 */
	public TaskBoardHelper(ProjectObject project) {
		mProject = project;
		mSprintBacklogMapper = mSprintBacklogLogic.getSprintBacklogMapper();
	}

	public TaskBoardHelper(ProjectObject project, long sprintId) {
		mProject = project;
		mSprintId = sprintId;
		mSprintBacklogLogic = new SprintBacklogLogic(mProject, mSprintId);
		mSprintBacklogMapper = mSprintBacklogLogic.getSprintBacklogMapper();
	}
	
	/**
	 * 傳出Story或Task burndown chart的數值json
	 */
	public String getSprintBurndownChartDataResponseText(String type) {
		String responseText = "";
		int sprintCount = (new SprintPlanHelper(mProject).loadListPlans()).size();
		// backlog = null 代表沒有Sprint資訊
		if (sprintCount != 0) {
			// Get TaskBoard Data
			TaskBoard taskBoard = new TaskBoard(mSprintBacklogLogic, mSprintBacklogMapper);
			// Get Sprint Data
			if (taskBoard != null) {
				if (type.equals("story")) {
					responseText = Translation.translateBurndownChartDataToJson(taskBoard.getStoryIdealPointMap(), taskBoard.getStoryRealPointMap());
				} else if (type.equals("task")) {
					responseText = Translation.translateBurndownChartDataToJson(taskBoard.getTaskIdealPointMap(), taskBoard.getTaskRealPointMap());
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
		if ((mSprintBacklogMapper != null) && (mSprintBacklogMapper.getSprintId() > 0)) {
			long currentSprintID = mSprintBacklogMapper.getSprintId();
			double currentPoint = mSprintBacklogLogic.getStoryUnclosedPoints();
			double currentHours = mSprintBacklogLogic.getTaskRemainsPoints();
			boolean isCurrentSprint = false;
			ReleasePlanHelper releasePlanHelper = new ReleasePlanHelper(mProject);
			String releaseID = releasePlanHelper.getReleaseID(currentSprintID);
			if (mSprintBacklogMapper.getSprintEndDate().getTime() > (new Date()).getTime()) {
				isCurrentSprint = true;
			}
			sprintInfoUI = new SprintInfoUI(currentSprintID, mSprintBacklogMapper.getSprintGoal(), currentPoint, currentHours, releaseID, isCurrentSprint);
		} else {
			sprintInfoUI = new SprintInfoUI();
		}
		Gson gson = new Gson();
		return new StringBuilder(gson.toJson(sprintInfoUI));
	}
	
	/**
	 * 將TaskBoard上所顯示的Story與Task以json方式丟出資料
	 * 
	 * @param filterName the handler's name you want to filter
	 */
	public StringBuilder getTaskBoardStoryTaskListText(String filterName) {
		int storyLength = 0;
		ArrayList<TaskBoard_Story> storyList = new ArrayList<TaskBoard_Story>();

		if ((mSprintBacklogMapper != null) && (mSprintBacklogMapper.getSprintId() > 0)) {
			ArrayList<StoryObject> stories = mSprintBacklogLogic.getStoriesByImp();		// 根據Sprint的importance來取Story
			HashMap<Long, ArrayList<TaskObject>> storyToTasks = getStoryToTasksMap(stories);
			stories = filterStory(stories, storyToTasks, filterName);					// filter story

			for (StoryObject story : stories) {
				storyList.add(createTaskBoardStory(story, storyToTasks.get(story.getId())));
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
		private long ID = 0;
		private String SprintGoal = "";
		private double CurrentStoryPoint = 0d;
		private double CurrentTaskPoint = 0d;
		private String ReleaseID = "Release None";
		private boolean isCurrentSprint = false;

		public SprintInfoUI() {}

		public SprintInfoUI(long id, String goal, double sp, double tp, String rid, boolean current) {
			ID = id;
			SprintGoal = goal;
			CurrentStoryPoint = sp;
			CurrentTaskPoint = tp;
			ReleaseID = "Release #" + rid;
			isCurrentSprint = current;
		}
	}
	
	private ArrayList<StoryObject> filterStory(ArrayList<StoryObject> stories, Map<Long, ArrayList<TaskObject>> storyToTasksMap, String filtername) {
		ArrayList<StoryObject> filterStories = new ArrayList<StoryObject>();
		// All member, return all story
		if (filtername.equals("ALL") || filtername.length() == 0) {
			return stories;
		} else {
			// filter member name by handler, return the story and task map relation
			for (StoryObject story : stories) {
				ArrayList<TaskObject> tasks = storyToTasksMap.get(story.getId());
				if (tasks != null) {
					ArrayList<TaskObject> filteredTasks = new ArrayList<TaskObject>();

					for (TaskObject task : tasks) {
						String handlerUserName = task.getHandler() != null ? task.getHandler().getUsername() : "";
						if (checkParent(filtername, task.getPartnersUsername(), handlerUserName)) {
							filteredTasks.add(task);
						}
					}

					if (filteredTasks.size() > 0) {
						// cover new filter map
						storyToTasksMap.put(story.getId(), filteredTasks);
						filterStories.add(story);
					}
				}
			}
			return filterStories;
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

	private TaskBoard_Story createTaskBoardStory(StoryObject story, ArrayList<TaskObject> tasks) {
		TaskBoard_Story TB_Story = new TaskBoard_Story(story);

		if (tasks != null) {
			for (TaskObject task : tasks) {
				TB_Story.Tasks.add(new TaskBoard_Task(task));
			}
		}
		return TB_Story;
	}

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

		public TaskBoard_Story(StoryObject story) {
			Id = String.valueOf(story.getId());
			Name = HandleSpecialChar(story.getName());
			Value = String.valueOf(story.getValue());
			Estimate = String.valueOf(story.getEstimate());
			Importance = String.valueOf(story.getImportance());
			Tag = Translation.Join(story.getTags(), ",");
			Status = story.getStatusString();
			Notes = HandleSpecialChar(story.getNotes());
			HowToDemo = HandleSpecialChar(story.getHowToDemo());
			Release = "";
			Sprint = String.valueOf(story.getSprintId());

			Link = "";
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

	private ArrayList<TaskBoard_AttachFile> getAttachFilePath(StoryObject story, List<AttachFileObject> list) {

		ArrayList<TaskBoard_AttachFile> array = new ArrayList<TaskBoard_AttachFile>();
		for (AttachFileObject file : list) {
			array.add(new TaskBoard_AttachFile(file.getId(), file.getName(), "fileDownload.do?projectName="
			        + ProjectObject.get(story.getProjectId()).getName() + "&fileId=" + file.getId() + "&fileName=" + file.getName()
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
	
	private HashMap<Long, ArrayList<TaskObject>> getStoryToTasksMap(ArrayList<StoryObject> stories) {
		HashMap<Long, ArrayList<TaskObject>> storyToTasks = new HashMap<Long, ArrayList<TaskObject>>();
		for (StoryObject story : stories) {
			storyToTasks.put(story.getId(), story.getTasks());
		}
		return storyToTasks;
	}
}
