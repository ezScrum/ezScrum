package ntut.csie.ezScrum.restful.mobile.support;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ntut.csie.ezScrum.restful.mobile.util.SprintBacklogUtil;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class ConvertProductBacklog {
	public ConvertProductBacklog() {
	}

	/****
	 * 確認 Story 是否被新增成功，回傳成敗的 json string
	 * 
	 * @param storyId
	 * @return createStoryResponse
	 */
	public String createStory(Long storyId) {
		JSONObject createStoryResponse = new JSONObject();
		try {
			if (storyId == null) {
				createStoryResponse.put("status", "FAILED");
				createStoryResponse.put("storyId", "NULL");
			} else {
				createStoryResponse.put("status", "SUCCESS");
				createStoryResponse.put("storyId", String.valueOf(storyId));
			}
		} catch (JSONException e) {
			return "{\"status\": \"FAILED\", \"storyId\":\"JSONException\"}";
		}
		return createStoryResponse.toString();
	}

	/****
	 * 讀取 Product Backlog 中指定的 story
	 * 
	 * @param story
	 * @return
	 */
	public String getStory(StoryObject story) {
		if (story != null) {
			return story.toString();
		}
		return "{}";
	}

	/****
	 * 讀取Product Backlog 所有的 story
	 * 
	 * @param stories
	 * @return resultString
	 */
	public String readStoryList(ArrayList<StoryObject> stories) {
		JSONArray jsonStories = new JSONArray();
		String resultString = "";
		JSONObject result = new JSONObject();
		try {
			for (StoryObject story : stories) {
				jsonStories.put(story.toJSON());
			}
			result.put("stories", jsonStories);
			resultString = result.toString();
		} catch (JSONException e) {
			resultString = "{\"stories\":[]}";
		}
		return resultString;
	}

	/****
	 * 更新story 至 Product Backlog
	 * 
	 * @param boolean success
	 * @return updateStoryResponse
	 */
	public String updateStory(boolean success) {
		JSONObject updateStoryResponse = new JSONObject();

		try {
			if (success) {
				updateStoryResponse.put("status", "SUCCESS");
			} else {
				updateStoryResponse.put("status", "FAILED");
			}
		} catch (JSONException e) {
			return "{\"status\": \"FAILED\"}";
		}

		return updateStoryResponse.toString();
	}

	/****
	 * 刪除 story
	 * 
	 * @param storyId
	 * @return deleteStoryResponse
	 */
	public String deleteStory(StoryObject story) {
		JSONObject deleteStoryResponse = new JSONObject();
		boolean isExist = false;
		if (story != null) {
			isExist = true;
		}

		try {
			if (isExist) {
				deleteStoryResponse.put("status", "FAILED");
				deleteStoryResponse.put("storyId", story.getId());
			} else {
				deleteStoryResponse.put("status", "SUCCESS");
			}
		} catch (JSONException e) {
			return "{\"status\":\"FAILED\",\"storyId\":\"" + story.getId()
					+ "\"}";
		}
		return deleteStoryResponse.toString();
	}
	
	/**
	 * 取得 tasks 的 json string
	 * 
	 * @param tags
	 * @return JSON String
	 */
	public String getTasks(ArrayList<TaskObject> tasks) {
		JSONObject taskJson = new JSONObject();
		JSONArray tasksJson = new JSONArray();
		try {
			for (TaskObject task : tasks) {
				tasksJson.put(task.toJSON());
			}
			taskJson.put("tasks", tasksJson);
		} catch (JSONException e) {
			return "{\"tasks\":[]}";
		}
		return taskJson.toString();
	}

	/***
	 * 取得 taglist 的json string
	 * 
	 * @param tags
	 * @return JSON String
	 */
	public String getTagList(ArrayList<TagObject> tags) {
		JSONObject tagJsonObject = new JSONObject();
		JSONArray tagListJsonArray = new JSONArray();
		try {
			for (TagObject tag : tags) {
				tagListJsonArray.put(tag.toJSON());
			}
			tagJsonObject.put("tags", tagListJsonArray);
		} catch (JSONException e) {
			return "{\"tags\": []}";
		}
		return tagJsonObject.toString();
	}

	/***
	 * 取得 story history list 的 json string
	 * 
	 * @param histories
	 * @return String
	 */
	public String getStoryHistory(ArrayList<HistoryObject> histories) {
		JSONObject historyJson = new JSONObject();
		JSONArray historiesJson = new JSONArray();
		try {
			for (HistoryObject history : histories) {
				historiesJson.put(history.toJSON());
			}
			historyJson.put("histories", historiesJson);
		} catch (JSONException e) {
			return "{\"hsitories\":[]}";
		}
		return historyJson.toString();
	}
}