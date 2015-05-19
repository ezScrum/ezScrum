package ntut.csie.ezScrum.restful.mobile.support;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.restful.mobile.util.SprintBacklogUtil;
import ntut.csie.ezScrum.restful.mobile.util.SprintPlanUtil;
import ntut.csie.ezScrum.web.dataObject.AttachFileObject;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.ezScrum.web.support.Translation;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.google.gson.Gson;

public class ConvertSprintBacklog {
	public ConvertSprintBacklog() {

	}

	public String readSprintInformationList(ISprintPlanDesc iSprintPlanDescList)
			throws JSONException {
		JSONObject sprintPlan = new JSONObject();
		JSONObject sprintProperties = new JSONObject();
		sprintProperties
				.put(SprintPlanUtil.TAG_ID, iSprintPlanDescList.getID()); // id
		sprintProperties.put(SprintPlanUtil.TAG_SPRINTGOAL,
				iSprintPlanDescList.getGoal()); // sprint goal
		sprintProperties.put(SprintPlanUtil.TAG_STARTDATE,
				iSprintPlanDescList.getStartDate()); // start date
		sprintProperties.put(SprintPlanUtil.TAG_DEMODATE,
				iSprintPlanDescList.getDemoDate()); // demo date
		sprintProperties.put(SprintPlanUtil.TAG_INTERVAL,
				iSprintPlanDescList.getInterval()); // interval
		sprintProperties.put(SprintPlanUtil.TAG_MEMBERS,
				iSprintPlanDescList.getMemberNumber()); // members count
		sprintProperties.put(SprintPlanUtil.TAG_HOURSCANCOMMIT,
				iSprintPlanDescList.getAvailableDays()); // == hours can submit
		sprintProperties.put(SprintPlanUtil.TAG_DAILYMEETING,
				iSprintPlanDescList.getNotes());
		sprintProperties.put(SprintPlanUtil.TAG_DEMOPLACE,
				iSprintPlanDescList.getDemoPlace());
		sprintPlan.put(SprintPlanUtil.TAG_CURRENTSPRINT, sprintProperties);
		return sprintPlan.toString();
	}

	public String readSprintInformationList(
			List<ISprintPlanDesc> iSprintPlanDescList, int currentSprintID)
			throws JSONException {
		JSONObject sprintPlanList = new JSONObject();
		JSONArray sprintPlanArray = new JSONArray();
		sprintPlanList.put(SprintPlanUtil.TAG_CURRENTSPRINTID, currentSprintID);
		sprintPlanList.put(SprintPlanUtil.TAG_SPRINTPLANLIST, sprintPlanArray);
		for (ISprintPlanDesc item : iSprintPlanDescList) {
			JSONObject sprintPlan = new JSONObject();
			JSONObject sprintProperties = new JSONObject();
			sprintProperties.put(SprintPlanUtil.TAG_ID, item.getID()); // id
			sprintProperties.put(SprintPlanUtil.TAG_SPRINTGOAL, item.getGoal()); // sprint
																					// goal
			sprintProperties.put(SprintPlanUtil.TAG_STARTDATE,
					item.getStartDate()); // start date
			sprintProperties.put(SprintPlanUtil.TAG_DEMODATE,
					item.getDemoDate()); // demo date
			sprintProperties.put(SprintPlanUtil.TAG_INTERVAL,
					item.getInterval()); // interval
			sprintProperties.put(SprintPlanUtil.TAG_MEMBERS,
					item.getMemberNumber()); // members count
			sprintProperties.put(SprintPlanUtil.TAG_HOURSCANCOMMIT,
					item.getAvailableDays()); // == hours can submit
			sprintProperties.put(SprintPlanUtil.TAG_DAILYMEETING,
					item.getNotes());
			sprintProperties.put(SprintPlanUtil.TAG_DEMOPLACE,
					item.getDemoPlace());
			sprintPlan.put(SprintPlanUtil.TAG_SPRINTPLAN, sprintProperties);
			sprintPlanArray.put(sprintPlan);
		}
		return sprintPlanList.toString();
	}

	public String readStoryIDList(SprintBacklogLogic sprintBacklogLogic)
			throws JSONException {
		JSONObject storyIds = new JSONObject();
		JSONArray storyArray = new JSONArray();
		ArrayList<StoryObject> stories = sprintBacklogLogic.getStories();
		for (StoryObject story : stories) {
			JSONObject stroyJson = new JSONObject();
			stroyJson.put("id", story.getId());
			stroyJson.put("point", story.getEstimate());
			stroyJson.put("status", story.getStatusString());
			storyArray.put(story);
		}
		storyIds.put(SprintPlanUtil.TAG_STORYLIST, storyArray);
		return storyIds.toString();
	}

	/**
	 * 轉換 task id list 的 json string
	 * 
	 * @param storyId
	 * @param tasks
	 * @return
	 * @throws JSONException
	 */
	public String convertTaskIdList(long storyId, ArrayList<TaskObject> tasks)
			throws JSONException {
		JSONObject story = new JSONObject();
		JSONObject storyProperties = new JSONObject();
		JSONArray tasksId = new JSONArray();
		for (TaskObject task : tasks) {
			tasksId.put(task.getId());
		}
		storyProperties.put(SprintBacklogUtil.TAG_ID, storyId);
		storyProperties.put(SprintBacklogUtil.TAG_TASKIDLIST, tasksId);

		story.put(SprintBacklogUtil.TAG_STORY, storyProperties);
		return story.toString();
	}

	/**
	 * 轉換 task history 的 json string
	 * 
	 * @param taskHistoryList
	 * @param remainingHourList
	 * @return
	 * @throws JSONException
	 */
	public String convertTaskHistory(List<HistoryObject> taskHistoryList,
			List<String> remainingHourList) throws JSONException {
		JSONObject taskHistory = new JSONObject();
		JSONArray historyItemArray = new JSONArray();
		for (int i = 0; i < taskHistoryList.size(); i++) {
			HistoryObject history = taskHistoryList.get(i);
			String reaminHour = remainingHourList.get(i);
			int historyType = history.getHistoryType();
			if (historyType == HistoryObject.TYPE_HANDLER) {
				String modifyDate = parseDate(history.getCreateTime());
				HistoryItemInfo historyItemInfo = new HistoryItemInfo(
						history.getDescription(), history.getHistoryTypeString());

				JSONObject historyItem = new JSONObject();
				historyItem.put(SprintBacklogUtil.TAG_MODIFYDATE, modifyDate);
				historyItem.put(SprintBacklogUtil.TAG_HISTORYTYPE,
						historyItemInfo.getType());
				historyItem.put(SprintBacklogUtil.TAG_DESCRIPTION,
						historyItemInfo.getDescription());
				historyItem.put(SprintBacklogUtil.TAG_REMAINHOUR, reaminHour);
				historyItemArray.put(historyItem);
			}
		}
		taskHistory
				.put(SprintBacklogUtil.TAG_TASKHISTORYLIST, historyItemArray);
		return taskHistory.toString();
	}

	private class HistoryItemInfo {
		private String description;
		private String type;

		public HistoryItemInfo(String desc, String type) {
			description = desc;
			type = type;
		}

		private void setDescription(String description) {
			description = description;
		}

		private void setType(String type) {
			type = type;
		}

		public String getDescription() {
			return description;
		}

		public String getType() {
			return type;
		}
	}

	/**
	 * 轉換date顯示格式:yyyy/MM/dd-hh:mm:ss
	 * 
	 * @param date
	 * @return
	 */
	private String parseDate(long date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd-hh:mm:ss");
		Date d = new Date(date);

		String modifiedDate = sdf.format(d);
		return modifiedDate;
	}

	/**
	 * 轉換 task information 的 json string
	 * 
	 * @param taskinformationList
	 * @return
	 * @throws JSONException
	 */
	public String readTaskInformationList(TaskObject task)
			throws JSONException {
		JSONObject taskInformation = new JSONObject();
		JSONObject taskInformations = new JSONObject();
		JSONArray partnersArray = new JSONArray();
		taskInformations.put(SprintBacklogUtil.TAG_ID,
				"" + task.getId());// 加上""使輸出格式從ID變為"ID"
		taskInformations.put(SprintBacklogUtil.TAG_NAME,
				task.getName());// getName
		taskInformations.put(SprintBacklogUtil.TAG_HANDLER,
				task.getHandler() != null ? task.getHandler().getUsername() : "");// getHandler
		partnersArray.put(task.getPartners());
		taskInformations.put(SprintBacklogUtil.TAG_PARTERNERS, partnersArray);
		taskInformations.put(SprintBacklogUtil.TAG_ESTIMATION,
				task.getEstimate());
		taskInformations.put(SprintBacklogUtil.TAG_REMAINHOUR,
				task.getRemains());
		taskInformations.put(SprintBacklogUtil.TAG_ACTUALHOUR,
				task.getActual());
		taskInformations.put(SprintBacklogUtil.TAG_NOTES,
				task.getNotes());

		taskInformation.put(SprintBacklogUtil.TAG_TASKINFORMATION,
				taskInformations);
		return taskInformation.toString();
	}

	/**
	 * 轉換多個 task information 的 json string
	 * 
	 * @param tasks
	 * @return
	 * @throws JSONException
	 */
	public String readTasksInformationList(ArrayList<TaskObject> tasks) throws JSONException {
		JSONObject taskList = new JSONObject();
		JSONArray taskArray = new JSONArray();
		if (tasks == null)
			return "";
		for (TaskObject task : tasks) {
			taskArray.put(readTaskInformationList(task));
		}
		taskList.put(SprintBacklogUtil.TAG_TASKLIST, taskArray);
		return taskList.toString();
	}

	/**
	 * 轉換 Sprint Backlog中的Story及Task成 json string
	 * 
	 * @param sprintBacklogLogic
	 * @param taskinformationList
	 * @return
	 * @throws JSONException
	 */
	public String convertStoryTaskInformationList(
			SprintBacklogLogic sprintBacklogLogic, SprintBacklogMapper sb,
			String handler) throws JSONException {
		int storyLength = 0;
		ArrayList<TaskBoard_Story> storyList = new ArrayList<TaskBoard_Story>();

		if ((sb != null) && (sb.getSprintId() > 0)) {
			ArrayList<StoryObject> stories = sprintBacklogLogic.getStoriesByImp();
			Map<Long, ArrayList<TaskObject>> taskMap = getStoryToTasksMap(stories);
			stories = filterStory(stories, taskMap, handler);

			for (StoryObject story : stories) {
				storyList.add(createTaskBoardStory(story,
						taskMap.get(story.getId())));
			}
			storyLength = stories.size();
		} else { // no sprint exist
			storyLength = 0;
		}

		Gson gson = new Gson();

		HashMap<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("success", "true");
		jsonMap.put("Total", storyLength);
		jsonMap.put("Stories", storyList);

		return gson.toJson(jsonMap);
	}

	// filter story and task by handler name
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
	
	private HashMap<Long, ArrayList<TaskObject>> getStoryToTasksMap(ArrayList<StoryObject> stories) {
		HashMap<Long, ArrayList<TaskObject>> storyToTasks = new HashMap<Long, ArrayList<TaskObject>>();
		for (StoryObject story : stories) {
			storyToTasks.put(story.getId(), story.getTasks());
		}
		return storyToTasks;
	}

	// if partner of assignto is equals usename, return it
	private boolean checkParent(String name, String partners, String assignto) {
		String[] parents = partners.split(";");
		for (String p : parents) {
			if (name.compareTo(p) == 0)
				return true;
		}

		if (name.compareTo(assignto) == 0)
			return true;

		return false;
	}

	// 將 tasks 塞到 story裡方便用Gson轉成Json string
	private TaskBoard_Story createTaskBoardStory(StoryObject story, ArrayList<TaskObject> tasks) {

		TaskBoard_Story TB_Story = new TaskBoard_Story(story);

		if (tasks != null) {
			for (TaskObject task : tasks) {
				TB_Story.Tasks.add(new TaskBoard_Task(task));
			}
		}
		return TB_Story;
	}

	// 欲打包成json格式的 story 物件
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

			if (!AttachFileList.isEmpty())
				Attach = "true";
			else
				Attach = "false";

			Tasks = new ArrayList<TaskBoard_Task>();
		}
	}

	// 欲打包成json格式的 task 物件
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
			Name = HandleSpecialChar(task.getName());
			Estimate = String.valueOf(task.getEstimate());
			RemainHours = String.valueOf(task.getRemains());
			Actual = String.valueOf(task.getActual());
			Handler = task.getHandler().getUsername();
			Partners = task.getPartnersUsername();
			Status = task.getStatusString();
			Notes = HandleSpecialChar(task.getNotes());
			Link = "";
			AttachFileList = getAttachFilePath(task, task.getAttachFiles());
			if (!AttachFileList.isEmpty())
				Attach = "true";
			else
				Attach = "false";
		}
	}

	// 供Story/Task記錄attach file的物件
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

	// 將 attach file的資訊組成有效的連結
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

	private String HandleSpecialChar(String str) {
		if (str.contains("\n")) {
			str = str.replaceAll("\n", "<br/>");
		}
		return str;
	}
}
