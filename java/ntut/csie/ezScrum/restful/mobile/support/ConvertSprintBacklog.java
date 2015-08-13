package ntut.csie.ezScrum.restful.mobile.support;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ntut.csie.ezScrum.restful.mobile.util.SprintBacklogUtil;
import ntut.csie.ezScrum.restful.mobile.util.SprintPlanUtil;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.AttachFileObject;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.support.Translation;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.google.gson.Gson;

public class ConvertSprintBacklog {
	
	public ConvertSprintBacklog() {
	}

	public static String getSprintJSONString(SprintObject sprint) throws JSONException {
		JSONObject sprintJson = new JSONObject();
		sprintJson.put(SprintPlanUtil.TAG_ID, sprint.getId());
		sprintJson.put(SprintPlanUtil.TAG_SPRINT_GOAL, sprint.getSprintGoal());
		sprintJson.put(SprintPlanUtil.TAG_START_DATE, sprint.getStartDateString());
		sprintJson.put(SprintPlanUtil.TAG_DEMO_DATE, sprint.getDemoDateString());
		sprintJson.put(SprintPlanUtil.TAG_DUE_DATE, sprint.getDueDateString());
		sprintJson.put(SprintPlanUtil.TAG_INTERVAL, sprint.getInterval());
		sprintJson.put(SprintPlanUtil.TAG_MEMBERS, sprint.getMembersAmount());
		sprintJson.put(SprintPlanUtil.TAG_HOURS_CAN_COMMIT, sprint.getHoursCanCommit());
		sprintJson.put(SprintPlanUtil.TAG_DAILY_MEETING, sprint.getDailyInfo());
		sprintJson.put(SprintPlanUtil.TAG_DEMO_PLACE, sprint.getDemoPlace());
		
		JSONArray storiesJsonArray = new JSONArray();
		for(StoryObject story : sprint.getStories()){
			storiesJsonArray.put(story.toJSON());
		}
		
		sprintJson.put("stories", storiesJsonArray);
		return sprintJson.toString();
	}

	public static String getSprintsJSONString(ArrayList<SprintObject> sprints, long currentSprintId) throws JSONException {
		JSONObject sprintsJson = new JSONObject();
		JSONArray sprintsJsonArray = new JSONArray();
		sprintsJson.put(SprintPlanUtil.TAG_CURRENT_SPRINT_ID, currentSprintId);
		sprintsJson.put(SprintPlanUtil.TAG_SPRINTS, sprintsJsonArray);
		for (SprintObject sprint : sprints) {
			JSONObject sprintJson = new JSONObject();
			sprintJson.put(SprintPlanUtil.TAG_ID, sprint.getId());
			sprintJson.put(SprintPlanUtil.TAG_SPRINT_GOAL, sprint.getSprintGoal());
			sprintJson.put(SprintPlanUtil.TAG_START_DATE, sprint.getStartDateString());
			sprintJson.put(SprintPlanUtil.TAG_DEMO_DATE, sprint.getDemoDateString());
			sprintJson.put(SprintPlanUtil.TAG_DUE_DATE, sprint.getDueDateString());
			sprintJson.put(SprintPlanUtil.TAG_INTERVAL, sprint.getInterval());
			sprintJson.put(SprintPlanUtil.TAG_MEMBERS, sprint.getMembersAmount());
			sprintJson.put(SprintPlanUtil.TAG_HOURS_CAN_COMMIT, sprint.getHoursCanCommit());
			sprintJson.put(SprintPlanUtil.TAG_DAILY_MEETING, sprint.getDailyInfo());
			sprintJson.put(SprintPlanUtil.TAG_DEMO_PLACE, sprint.getDemoPlace());
			sprintsJsonArray.put(sprintJson);
		}
		return sprintsJson.toString();
	}

	public static String getStoriesIdJsonStringInSprint(ArrayList<StoryObject> stories) throws JSONException {
		JSONObject storiesIdJsonString = new JSONObject();
		JSONArray storyArray = new JSONArray();
		for (StoryObject story : stories) {
			JSONObject stroyJson = new JSONObject();
			stroyJson.put("id", story.getId());
			stroyJson.put("point", story.getEstimate());
			stroyJson.put("status", story.getStatusString());
			storyArray.put(stroyJson);
		}
		storiesIdJsonString.put(SprintPlanUtil.TAG_STORIES, storyArray);
		return storiesIdJsonString.toString();
	}

	/**
	 * 轉換 task id list 的 json string
	 * 
	 * @param storyId
	 * @param tasks
	 * @return
	 * @throws JSONException
	 */
	public static String getTasksIdJsonStringInStory(long storyId, ArrayList<TaskObject> tasks) throws JSONException {
		JSONObject story = new JSONObject();
		JSONObject storyProperties = new JSONObject();
		JSONArray tasksId = new JSONArray();
		for (TaskObject task : tasks) {
			tasksId.put(task.getId());
		}
		storyProperties.put(SprintBacklogUtil.TAG_ID, storyId);
		storyProperties.put(SprintBacklogUtil.TAG_TASKSIDL, tasksId);

		story.put(SprintBacklogUtil.TAG_STORY, storyProperties);
		return story.toString();
	}

	/**
	 * 轉換 task history 的 json string
	 * 
	 * @param taskHistories
	 * @param remainingHourList
	 * @return
	 * @throws JSONException
	 */
	public static String getTaskHistoriesJsonString(ArrayList<HistoryObject> taskHistories) throws JSONException {
		JSONObject taskHistoryJson = new JSONObject();
		JSONArray historyJsonArray = new JSONArray();
		for (int i = 0; i < taskHistories.size(); i++) {
			HistoryObject history = taskHistories.get(i);
			JSONObject historyItem = new JSONObject();
			String modifyDate = parseDate(history.getCreateTime());
			historyItem.put(SprintBacklogUtil.TAG_MODIFYDATE, modifyDate);
			historyItem.put(SprintBacklogUtil.TAG_HISTORYTYPE,
					history.getHistoryType());
			historyItem.put(SprintBacklogUtil.TAG_DESCRIPTION,
					history.getDescription());
			historyJsonArray.put(historyItem);
		}
		taskHistoryJson.put(SprintBacklogUtil.TAG_TASKHISTORIES, historyJsonArray);
		return taskHistoryJson.toString();
	}

	/**
	 * 轉換date顯示格式:yyyy/MM/dd-hh:mm:ss
	 * 
	 * @param date
	 * @return
	 */
	private static String parseDate(long date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd-hh:mm:ss");
		Date d = new Date(date);

		String modifiedDate = sdf.format(d);
		return modifiedDate;
	}

	/**
	 * 轉換 task information 的 json string
	 * 
	 * @param task
	 * @return
	 * @throws JSONException
	 */
	public static String getTaskJsonString(TaskObject task) throws JSONException {
		JSONObject taskJsonString = new JSONObject();
		JSONArray partnersArray = new JSONArray();
		taskJsonString.put(SprintBacklogUtil.TAG_ID, task.getId());
		taskJsonString.put(SprintBacklogUtil.TAG_NAME, task.getName());
		taskJsonString.put(SprintBacklogUtil.TAG_HANDLER, task.getHandler() != null ? task.getHandler().getUsername() : "");
		ArrayList<AccountObject> partners = task.getPartners();
		if (!partners.isEmpty()) {
			for (AccountObject partner : partners) {
				JSONObject partnerJson = partner.toJSON();
				partnersArray.put(partnerJson);
			}
		}
		taskJsonString.put(SprintBacklogUtil.TAG_PARTNERS, partnersArray);
		taskJsonString.put(SprintBacklogUtil.TAG_ESTIMATE, task.getEstimate());
		taskJsonString.put(SprintBacklogUtil.TAG_REMAINS, task.getRemains());
		taskJsonString.put(SprintBacklogUtil.TAG_ACTUAL, task.getActual());
		taskJsonString.put(SprintBacklogUtil.TAG_NOTES, task.getNotes());
		return taskJsonString.toString();
	}

	/**
	 * 轉換多個 task information 的 json string
	 * 
	 * @param tasks
	 * @return
	 * @throws JSONException
	 */
	public static String getTasksJsonString(ArrayList<TaskObject> tasks) throws JSONException {
		JSONObject tasksJsonString = new JSONObject();
		JSONArray tasksJsonArray = new JSONArray();
		if (tasks == null)
			return "";
		for (TaskObject task : tasks) {
			JSONObject taskJson = new JSONObject(getTaskJsonString(task));
			tasksJsonArray.put(taskJson);
		}
		tasksJsonString.put(SprintBacklogUtil.TAG_TASKS, tasksJsonArray);
		return tasksJsonString.toString();
	}

	/**
	 * 轉換 Sprint Backlog 中的 Story 及 Task 成 JSON string
	 * 
	 * @param SprintObject
	 * @return
	 * @throws JSONException
	 */
	public static String getSprintBacklogJsonString(SprintObject sprint) throws JSONException {
		if (sprint == null) {
			return "";
		}
		
		ArrayList<StoryObject> stories = sprint.getStories();
		JSONObject sprintBacklogJson = new JSONObject();
		sprintBacklogJson.put("success", "true");
		sprintBacklogJson.put("sprintId", sprint.getId());
		sprintBacklogJson.put("Total", stories.size());
		JSONArray storiesJsonArray = new JSONArray();
		for (StoryObject story : stories) {
			JSONObject storyJson = story.toJSON();
			JSONArray tasksJsonArray = new JSONArray();
			for (TaskObject task : story.getTasks()) {
				JSONObject taskJson = task.toJSON();
				tasksJsonArray.put(taskJson);
			}
			storyJson.put("tasks", tasksJsonArray);
			storiesJsonArray.put(storyJson);
		}
		sprintBacklogJson.put("Stories", storiesJsonArray);
		return sprintBacklogJson.toString();
	}
	
	public String getTaskboardJsonString(SprintObject sprint) {
		ArrayList<TaskBoard_Story> taskboardStories = new ArrayList<TaskBoard_Story>();
		ArrayList<StoryObject> stories = null;
		if (sprint != null) {
			stories = sprint.getStories();
			Map<Long, ArrayList<TaskObject>> taskMap = getStoryToTasksMap(stories);
			for (StoryObject story : stories) {
				taskboardStories.add(createTaskBoardStory(story,
						taskMap.get(story.getId())));
			}
		}

		Gson gson = new Gson();

		HashMap<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("success", "true");
		jsonMap.put("Total", stories.size());
		jsonMap.put("Stories", taskboardStories);

		return gson.toJson(jsonMap);
	}
	
	private static HashMap<Long, ArrayList<TaskObject>> getStoryToTasksMap(ArrayList<StoryObject> stories) {
		HashMap<Long, ArrayList<TaskObject>> storyToTasks = new HashMap<Long, ArrayList<TaskObject>>();
		for (StoryObject story : stories) {
			storyToTasks.put(story.getId(), story.getTasks());
		}
		return storyToTasks;
	}
	
	// 將 tasks 塞到 story 裡方便用 GSON 轉成 JSON string
	private TaskBoard_Story createTaskBoardStory(StoryObject story, ArrayList<TaskObject> tasks) {
		TaskBoard_Story TB_Story = new TaskBoard_Story(story);
		if (tasks != null) {
			for (TaskObject task : tasks) {
				TB_Story.Tasks.add(new TaskBoard_Task(task));
			}
		}
		return TB_Story;
	}

	// 打包 story 物件 for taskboard
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
		String Attach;
		List<TaskBoard_AttachFile> AttachFileList;
		List<TaskBoard_Task> Tasks;

		public TaskBoard_Story(StoryObject story) {
			Id = Long.toString(story.getId());
			Name = story.getName();
			Value = String.valueOf(story.getValue());
			Estimate = String.valueOf(story.getEstimate());
			Importance = String.valueOf(story.getImportance());
			Tag = Translation.Join(story.getTags(), ",");
			Status = story.getStatusString();
			Notes = story.getNotes();
			HowToDemo = story.getHowToDemo();
			Release = "";
			Sprint = String.valueOf(story.getSprintId());

			Link = "";
			AttachFileList = getAttachFilePath(story, story.getAttachFiles());

			if (!AttachFileList.isEmpty())
				Attach = "true";
			else
				Attach = "false";

			Tasks = new ArrayList<TaskBoard_Task>();
		}
	}

	// 打包 task 物件 for taskboard
	private class TaskBoard_Task {
		String Id;
		String Name;
		String Estimate;
		String RemainHours;
		String Handler;
		String Notes;
		List<TaskBoard_AttachFile> AttachFileList;
		String Attach;
		String Status;
		String Partners;
		String Link;
		String Actual;

		public TaskBoard_Task(TaskObject task) {
			Id = String.valueOf(task.getId());
			Name = task.getName();
			Estimate = String.valueOf(task.getEstimate());
			RemainHours = String.valueOf(task.getRemains());
			Actual = String.valueOf(task.getActual());
			Handler = (task.getHandler() == null ? "" : task.getHandler().getUsername());
			Partners = task.getPartnersUsername();
			Status = task.getStatusString();
			Notes = task.getNotes();
			Link = "";
			AttachFileList = getAttachFilePath(task, task.getAttachFiles());
			if (!AttachFileList.isEmpty())
				Attach = "true";
			else
				Attach = "false";
		}
	}

	// 供 Story/Task 記錄 attach file 的物件
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

	// 將 attach file 的資訊組成有效的連結
	private ArrayList<TaskBoard_AttachFile> getAttachFilePath(StoryObject story,
			ArrayList<AttachFileObject> list) {

		ArrayList<TaskBoard_AttachFile> array = new ArrayList<TaskBoard_AttachFile>();
		for (AttachFileObject file : list) {
			array.add(new TaskBoard_AttachFile(file.getId(), file.getName(),
					"fileDownload.do?projectName=" + ProjectObject.get(story.getProjectId()).getName()
							+ "&fileID=" + file.getId() + "&fileName="
							+ file.getName(), new Date(file.getCreateTime())));
		}
		return array;
	}
	
	private ArrayList<TaskBoard_AttachFile> getAttachFilePath(TaskObject task,
			ArrayList<AttachFileObject> list) {

		ArrayList<TaskBoard_AttachFile> array = new ArrayList<TaskBoard_AttachFile>();
		for (AttachFileObject file : list) {
			ProjectObject project = ProjectObject.get(task.getProjectId());
			String projectName = project.getName();
			array.add(new TaskBoard_AttachFile(file.getId(), file.getName(),
					"fileDownload.do?projectName=" + projectName
							+ "&fileID=" + file.getId() + "&fileName="
							+ file.getName(), new Date(file.getCreateTime())));
		}
		return array;
	}
}
