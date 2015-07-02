package ntut.csie.ezScrum.web.support;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.web.dataObject.AttachFileObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class Translation {

	// for GetEditStoryInfoAction
	public static String translateStoryToXML(StoryObject story) {
		TranslateSpecialChar translateChar = new TranslateSpecialChar();

		StringBuilder responseText = new StringBuilder();
		responseText.append("<ProductBacklog>");
		responseText.append("<Total>1</Total>");
		responseText.append("<Story>");
		responseText.append("<Id>" + story.getId() + "</Id>");
		responseText.append("<Link></Link>");
		responseText.append("<Name>"
				+ translateChar.TranslateXMLChar(story.getName()) + "</Name>");
		responseText.append("<Value>" + story.getValue() + "</Value>");
		responseText.append("<Importance>" + story.getImportance()
				+ "</Importance>");
		responseText.append("<Estimate>" + story.getEstimate() + "</Estimate>");
		responseText.append("<Status>" + story.getStatusString() + "</Status>");
		responseText
				.append("<Notes>"
						+ translateChar.TranslateXMLChar(story.getNotes())
						+ "</Notes>");
		responseText.append("<HowToDemo>"
				+ translateChar.TranslateXMLChar(story.getHowToDemo())
				+ "</HowToDemo>");
		responseText.append("<Release></Release>");
		if (story.getSprintId() == StoryObject.NO_PARENT) {
			responseText.append("<Sprint>None</Sprint>");			
		} else {
			responseText.append("<Sprint>" + story.getSprintId() + "</Sprint>");
		}
		responseText.append("<Tag>"
				+ translateChar.TranslateXMLChar(Join(story.getTags(), ","))
				+ "</Tag>");
		if (story.getAttachFiles().size() == 0)
			responseText.append("<Attach>false</Attach>");
		else
			responseText.append("<Attach>true</Attach>");
		responseText.append("</Story>");
		responseText.append("</ProductBacklog>");

		return responseText.toString();
	}

	public static String translateStoryToJson(StoryObject story) {
		ArrayList<StoryObject> storie = new ArrayList<StoryObject>();
		storie.add(story);
		return translateStoriesToJson(storie);
	}

	// for ShowProductBacklogAction
	public static String translateStoriesToJson(ArrayList<StoryObject> stories) {
		TranslateSpecialChar translateChar = new TranslateSpecialChar();

		JSONObject responseText = new JSONObject();
		try {
			responseText.put("success", true);
			responseText.put("Total", stories.size());
			JSONArray jsonStroies = new JSONArray();
			for (int i = 0; i < stories.size(); i++) {
				JSONObject jsonStory = new JSONObject();

				jsonStory.put("Id", stories.get(i).getId());
				jsonStory.put("Type", "Story");
				jsonStory.put("Name", translateChar.TranslateJSONChar((stories.get(i).getName())));
				jsonStory.put("Value", stories.get(i).getValue());
				jsonStory.put("Estimate", stories.get(i).getEstimate());
				jsonStory.put("Importance", stories.get(i).getImportance());
				jsonStory.put("Tag", translateChar.TranslateJSONChar(Join(stories.get(i).getTags(), ",")));
				jsonStory.put("Status", stories.get(i).getStatusString());
				jsonStory.put("Notes", translateChar.TranslateJSONChar(stories.get(i).getNotes()));
				jsonStory.put("HowToDemo", translateChar.TranslateJSONChar(stories.get(i).getHowToDemo()));
				jsonStory.put("Link", "");
				jsonStory.put("Release", "");
				if (stories.get(i).getSprintId() == StoryObject.NO_PARENT) {
					jsonStory.put("Sprint", "None");
				} else {
					jsonStory.put("Sprint", stories.get(i).getSprintId());				
				}
				jsonStory.put("FilterType", getFilterType(stories.get(i)));

				if (stories.get(i).getAttachFiles().size() == 0)
					jsonStory.put("Attach", false);
				else
					jsonStory.put("Attach", true);

				ArrayList<AttachFileObject> attachFiles = stories.get(i).getAttachFiles();
				JSONArray jsonAttachFiles = new JSONArray();
				for (AttachFileObject attachFile : attachFiles) {
					JSONObject jsonAttachFile = new JSONObject();
					jsonAttachFile.put("IssueId", attachFile.getIssueId());
					jsonAttachFile.put("FileId", attachFile.getId());
					jsonAttachFile.put("FileName", translateChar.TranslateJSONChar(attachFile.getName()));

					// parse Dateformat as Gson Default DateFormat (TaskBoard
					// page)
					DateFormat dateFormat = DateFormat.getDateTimeInstance(
							DateFormat.DEFAULT, DateFormat.DEFAULT);
					Date date = new Date(attachFile.getCreateTime());
					String attachTime = dateFormat.format(date);
					ProjectObject project = ProjectObject.get(stories.get(i)
							.getId());
					jsonAttachFile.put("UploadDate", attachTime);
					jsonAttachFile.put("FilePath",
							"fileDownload.do?projectName=" + project.getName()
									+ "&fileId=" + attachFile.getId()
									+ "&fileName=" + attachFile.getName());
					jsonAttachFiles.put(jsonAttachFile);
				}
				jsonStory.put("AttachFileList", jsonAttachFiles);

				jsonStroies.put(jsonStory);
			}
			responseText.put("Stories", jsonStroies);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return responseText.toString();
	}

	public static String translateTaskToJson(TaskObject task) {
		TranslateSpecialChar translateChar = new TranslateSpecialChar();
		JSONObject responseText = new JSONObject();
		try {
			responseText.put("success", true);
			responseText.put("Total", 1);

			JSONObject jsonTask = new JSONObject();
			jsonTask.put("Id", task.getId());
			jsonTask.put("Name",
					translateChar.TranslateJSONChar((task.getName())));
			jsonTask.put("Value", "");
			jsonTask.put("Estimate", task.getEstimate());
			jsonTask.put("Importance", "");
			jsonTask.put("Tag", "");
			jsonTask.put("Status", task.getStatusString());
			jsonTask.put("Notes",
					translateChar.TranslateJSONChar(task.getNotes()));
			jsonTask.put("HowToDemo", "");
			jsonTask.put("Link", "");
			jsonTask.put("Release", "");
			jsonTask.put("Sprint", "");
			jsonTask.put("FilterType", "");

			if (task.getAttachFiles().size() > 0) {
				jsonTask.put("Attach", true);
			} else {
				jsonTask.put("Attach", false);
			}

			JSONArray attachFiles = new JSONArray();
			for (AttachFileObject attachFile : task.getAttachFiles()) {
				JSONObject attachFileJson = new JSONObject();
				attachFileJson.put("IssueId", attachFile.getIssueId());
				attachFileJson.put("IssueType", attachFile.getIssueTypeStr());
				attachFileJson.put("FileId", attachFile.getId());
				attachFileJson.put("FileName", attachFile.getName());
				attachFileJson.put("FilePath", "fileDownload.do?projectName="
						+ task.getProjectId() + "&fileId=" + attachFile.getId()
						+ "&fileName=" + attachFile.getName());

				DateFormat dateFormat = DateFormat.getDateTimeInstance(
						DateFormat.DEFAULT, DateFormat.DEFAULT);
				Date date = new Date(attachFile.getCreateTime());
				String attachTime = dateFormat.format(date);

				attachFileJson.put("UploadDate", attachTime);
				attachFiles.put(attachFileJson);
			}

			jsonTask.put("AttachFileList", attachFiles);
			responseText.put("Tasks", new JSONArray().put(jsonTask));

			return responseText.toString();
		} catch (JSONException e) {
		}
		return new JSONObject().toString();
	}

	// for Taskboard, CO data, include Handler + Partners
	public static String translateTaskboardIssueToJson(IIssue issue) {
		TranslateSpecialChar translateChar = new TranslateSpecialChar();
		JSONObject responseText = new JSONObject();
		try {
			responseText.put("success", true);

			JSONObject jsonIssue = new JSONObject();
			// 若需要其他欄位請再新增
			jsonIssue.put("Id", issue.getIssueID());
			jsonIssue.put("Link",
					translateChar.TranslateJSONChar(issue.getIssueLink()));
			jsonIssue.put("Name",
					translateChar.TranslateJSONChar((issue.getSummary())));
			jsonIssue.put("Handler", issue.getAssignto());
			jsonIssue.put("Partners", issue.getPartners());
			responseText.put("Issue", jsonIssue);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return responseText.toString();
	}

	public static String translateTaskboardStoryToJson(StoryObject story) {
		JSONObject responseText = new JSONObject();
		try {
			responseText.put("success", true);
			JSONObject jsonIssue = new JSONObject();
			// 若需要其他欄位請再新增
			jsonIssue.put("Id", story.getId());
			jsonIssue.put("Link", "");
			jsonIssue.put("Name", story.getName());
			jsonIssue.put("Estimate", story.getEstimate());
			jsonIssue.put("Handler", "");
			jsonIssue.put("Partners", "");
			responseText.put("Issue", jsonIssue);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return responseText.toString();
	}

	public static String translateTaskboardTaskToJson(TaskObject task) {
		JSONObject responseText = new JSONObject();
		try {
			responseText.put("success", true);
			JSONObject jsonIssue = new JSONObject();
			// 若需要其他欄位請再新增
			jsonIssue.put("Id", task.getId());
			jsonIssue.put("Link", "");
			jsonIssue.put("Name", task.getName());
			jsonIssue.put("Handler", task.getHandler() == null ? "" : task
					.getHandler().getUsername());
			jsonIssue.put("Partners", task.getPartnersUsername());
			responseText.put("Issue", jsonIssue);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return responseText.toString();
	}

	private static String getFilterType(StoryObject story) {

		// status 為 Done
		if (story.getStatus() == StoryObject.STATUS_DONE) {
			return ScrumEnum.DONE;
		}

		// business value 存在 & estimate 存在 & importance 存在
		if (story.getValue() > 0 && story.getEstimate() > 0
				&& story.getImportance() > 0
				&& story.getStatus() == StoryObject.STATUS_UNCHECK) {
			return ScrumEnum.DETAIL;
		}
		// business value 不存在 以及 其他狀況預設為 Backlog
		return ScrumEnum.BACKLOG;
	}

	// for AjaxGetSprintIndexInfoAction
	public static String translateSprintInfoToJson(long currentSprintId,
			double InitialPoint, double CurrentPoint, double InitialHours,
			double CurrentHours, int releaseId, String SprintGoal,
			String StoryChartUrl, String TaskChartUrl, boolean isCurrentSprint)
			throws JSONException {

		TranslateSpecialChar translateChar = new TranslateSpecialChar();
		JSONObject responseText = new JSONObject();
		responseText.put("success", true);
		responseText.put("Total", 1);
		JSONObject sprint = new JSONObject();
		sprint.put("Id", currentSprintId);
		sprint.put(
				"Name",
				"Sprint #"
						+ translateChar.TranslateJSONChar(String
								.valueOf(currentSprintId)));
		sprint.put("InitialPoint", String.valueOf(InitialPoint));
		sprint.put("CurrentPoint", String.valueOf(CurrentPoint));
		sprint.put("InitialHours", String.valueOf(InitialHours));
		sprint.put("CurrentHours", String.valueOf(CurrentHours));
		sprint.put(
				"ReleaseID",
				"Release #"
						+ translateChar.HandleNullString(Integer
								.toString(releaseId)));
		sprint.put("SprintGoal", translateChar.TranslateJSONChar(SprintGoal));
		sprint.put("StoryChartUrl", StoryChartUrl);
		sprint.put("TaskChartUrl", TaskChartUrl);
		sprint.put("IsCurrentSprint", isCurrentSprint);
		responseText.put("Sprint", sprint);

		return responseText.toString();
	}

	// for ShowSprintBacklogAction
	public static String translateSprintBacklogToJson(
			ArrayList<StoryObject> stories, long currentSprintId,
			double currentPoint, double limitedPoint, double taskPoint,
			int releaseId, String sprintGoal) {

		TranslateSpecialChar translateChar = new TranslateSpecialChar();

		JSONObject responseText = new JSONObject();
		try {
			responseText.put("success", true);
			responseText.put("Total", stories.size());

			JSONObject sprint = new JSONObject();
			sprint.put("Id", currentSprintId);
			sprint.put(
					"Name",
					"Sprint #"
							+ translateChar.TranslateJSONChar(String
									.valueOf(currentSprintId)));
			sprint.put("CurrentPoint", currentPoint);
			sprint.put("LimitedPoint", limitedPoint);
			sprint.put("TaskPoint", taskPoint);
			sprint.put(
					"ReleaseID",
					"Release #"
							+ translateChar.HandleNullString(Integer
									.toString(releaseId)));
			sprint.put("SprintGoal", sprintGoal);
			responseText.put("Sprint", sprint);

			JSONArray jsonStories = new JSONArray();
			for (StoryObject story : stories) {
				JSONObject jsonStory = new JSONObject();

				jsonStory.put("Id", story.getId());
				jsonStory.put("Link", "");
				jsonStory.put("Name",
						translateChar.TranslateJSONChar(story.getName()));
				jsonStory.put("Value", story.getValue());
				jsonStory.put("Importance", story.getImportance());
				jsonStory.put("Estimate", story.getEstimate());
				jsonStory.put("Status", story.getStatusString());
				jsonStory.put("Notes",
						translateChar.TranslateJSONChar(story.getNotes()));
				jsonStory.put("Tag", translateChar.TranslateJSONChar(Join(
						story.getTags(), ",")));
				jsonStory.put("HowToDemo",
						translateChar.TranslateJSONChar(story.getHowToDemo()));
				jsonStory.put("Release", "");
				jsonStory.put("Sprint", story.getSprintId());

				if (story.getAttachFiles().size() == 0) {
					jsonStory.put("Attach", false);
				} else {
					jsonStory.put("Attach", true);
				}

				ArrayList<AttachFileObject> files = story.getAttachFiles();
				JSONArray jsonFiles = new JSONArray();
				for (AttachFileObject file : files) {
					JSONObject jsonFile = new JSONObject();
					jsonFile.put("IssueId", file.getIssueId());
					jsonFile.put("FileId", file.getId());
					jsonFile.put("FileName", translateChar
							.TranslateXMLChar(translateChar
									.TranslateJSONChar(file.getName())));
					jsonFile.put(
							"DownloadPath",
							"fileDownload.do?projectName="
									+ ProjectObject.get(story.getProjectId())
											.getName() + "&fileId="
									+ file.getId() + "&fileName="
									+ file.getName());
					jsonFiles.put(jsonFile);
				}
				jsonStory.put("AttachFileList", jsonFiles);

				jsonStories.put(jsonStory);
			}
			responseText.put("Stories", jsonStories);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return responseText.toString();
	}

	// 將 BundownChart Data 轉換成 Json 格式
	public static String translateBurndownChartDataToJson(
			LinkedHashMap<Date, Double> ideal, LinkedHashMap<Date, Double> real) {
		JSONObject responseText = new JSONObject();
		try {
			responseText.put("success", true);

			JSONArray array = new JSONArray();
			DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
			Object[] idealPointArray = ideal.keySet().toArray();

			for (int i = 0; i < idealPointArray.length; i++) {
				JSONObject obj = new JSONObject();
				obj.put("Date", formatter.format(idealPointArray[i]));
				obj.put("IdealPoint", ideal.get(idealPointArray[i]));

				if (real.get(idealPointArray[i]) != null)
					obj.put("RealPoint", real.get(idealPointArray[i]));
				else
					obj.put("RealPoint", "null");

				array.put(obj);
			}
			responseText.put("Points", array);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return responseText.toString();
	}

	public static String Join(List<TagObject> tags, String delimiter) {
		StringBuilder result = new StringBuilder();
		if (!tags.isEmpty()) {
			for (TagObject tag : tags) {
				result.append(tag.getName() + delimiter);
			}
			result.delete(result.length() - delimiter.length(), result.length());
		}
		return result.toString();
	}
}
