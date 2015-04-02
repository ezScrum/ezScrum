package ntut.csie.ezScrum.web.support;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
	public Translation() {}

	// 將 Custom issue 轉換成 Json 格式
	public String translateCustomIssueToJson(List<IIssue> customIssues) {
		TranslateSpecialChar translateChar = new TranslateSpecialChar();

		JSONObject responseText = new JSONObject();
		try {
			responseText.put("success", true);
			responseText.put("Total", customIssues.size());
			JSONArray issueArray = new JSONArray();
			for (int i = 0; i < customIssues.size(); i++) {
				JSONObject issue = new JSONObject();
				issue.put("Id", customIssues.get(i).getIssueID());
				issue.put("ProjectName", translateChar.TranslateJSONChar(customIssues.get(i).getProjectName()));
				issue.put("Link", "showIssueInformation.do?projectName=" + customIssues.get(i).getProjectName()
						+ "&" + "issueID=" + customIssues.get(i).getIssueID());
				issue.put("Category", translateChar.TranslateJSONChar(customIssues.get(i).getCategory()));
				issue.put("Name", translateChar.TranslateJSONChar(customIssues.get(i).getSummary()));
				issue.put("Status", customIssues.get(i).getFieldValue("Status"));
				issue.put("Priority", customIssues.get(i).getFieldValue("Priority"));
				
				if (customIssues.get(i).getFieldValue("Handled").equals("True")) issue.put("Handled", customIssues.get(i).getFieldValue("Handled"));
				else issue.put("Handled", "False");
				
				issue.put("ReportUserName", translateChar.TranslateJSONChar(customIssues.get(i).getFieldValue("ReportUserName")));
				
				// 如果comment為-1則代表為空值，為了不讓ext在expander顯示上資訊，所以將資料替換為null
				String comment = customIssues.get(i).getFieldValue("Comment");
				if (comment == "-1") comment = "";
				
				issue.put("Comment", translateChar.TranslateJSONChar(comment));
				
				issue.put("Email", translateChar.TranslateJSONChar(customIssues.get(i).getFieldValue("Email")));
				issue.put("Description", translateChar.TranslateJSONChar(customIssues.get(i).getDescription()));
				issue.put("Handler", customIssues.get(i).getAssignto());
				
				if (customIssues.get(i).getAttachFiles().size() == 0) issue.put("Attach", "false");
				else issue.put("Attach", "true");
				
				ArrayList<AttachFileObject> attachFiles = customIssues.get(i).getAttachFiles();
				JSONArray jsonAttachFiles = new JSONArray();
				for (AttachFileObject file : attachFiles) {
					JSONObject attachFile = new JSONObject();
					attachFile.put("IssueId", file.getIssueId());
					attachFile.put("FileId", file.getId());
					attachFile.put("FileName", translateChar.TranslateXMLChar(translateChar.TranslateJSONChar(file.getName())));
					attachFile.put("DownloadPath", "fileDownload.do?projectName=" + customIssues.get(i).getProjectName()
							+ "&fileId=" + file.getId() + "&fileName=" + file.getName());
					jsonAttachFiles.put(attachFile);
				}
				issue.put("AttachFileList", jsonAttachFiles);
				issueArray.put(issue);
			}
			responseText.put("CustomIssues", issueArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return responseText.toString();
	}

	// for GetEditStoryInfoAction
	public String translateStoryToXML(StoryObject story) {
		TranslateSpecialChar translateChar = new TranslateSpecialChar();

		StringBuilder responseText = new StringBuilder();
		responseText.append("<ProductBacklog>");
		responseText.append("<Total>1</Total>");
		responseText.append("<Story>");
		responseText.append("<Id>" + story.getId() + "</Id>");
		responseText.append("<Link></Link>");
		responseText.append("<Name>" + translateChar.TranslateXMLChar(story.getName()) + "</Name>");
		responseText.append("<Value>" + story.getValue() + "</Value>");
		responseText.append("<Importance>" + story.getImportance() + "</Importance>");
		responseText.append("<Estimate>" + story.getEstimate() + "</Estimate>");
		responseText.append("<Status>" + story.getStatus() + "</Status>");
		responseText.append("<Notes>" + translateChar.TranslateXMLChar(story.getNotes()) + "</Notes>");
		responseText.append("<HowToDemo>" + translateChar.TranslateXMLChar(story.getHowToDemo()) + "</HowToDemo>");
		responseText.append("<Release></Release>");
		responseText.append("<Sprint>" + story.getSprintId() + "</Sprint>");
		responseText.append("<Tag>" + translateChar.TranslateXMLChar(Join(story.getTags(), ",")) + "</Tag>");
		if (story.getAttachFiles().size() == 0) responseText.append("<Attach>false</Attach>");
		else responseText.append("<Attach>true</Attach>");
		responseText.append("</Story>");
		responseText.append("</ProductBacklog>");

		return responseText.toString();
	}

	public String translateStoryToJson(StoryObject story) {
		ArrayList<StoryObject> storie = new ArrayList<StoryObject>();
		storie.add(story);
		return translateStoriesToJson(storie);
	}

	// for ShowProductBacklogAction
	public String translateStoriesToJson(ArrayList<StoryObject> stories) {
		TranslateSpecialChar translateChar = new TranslateSpecialChar();

		JSONObject responseText = new JSONObject();
		try {
			responseText.put("success", true);
			responseText.put("Total", stories.size());
			JSONArray jsonStroies = new JSONArray();
			for (int i = 0; i < stories.size(); i++) {
				JSONObject jsonStory = new JSONObject();
				
				jsonStory.put("Id", stories.get(i).getId());
				jsonStory.put("Name", translateChar.TranslateJSONChar((stories.get(i).getName())));
				jsonStory.put("Value", stories.get(i).getValue());
				jsonStory.put("Estimate", stories.get(i).getEstimate());
				jsonStory.put("Importance", stories.get(i).getImportance());
				jsonStory.put("Tag", translateChar.TranslateJSONChar(Join(stories.get(i).getTags(), ",")));
				jsonStory.put("Status", stories.get(i).getStatus());
				jsonStory.put("Notes", translateChar.TranslateJSONChar(stories.get(i).getNotes()));
				jsonStory.put("HowToDemo", translateChar.TranslateJSONChar(stories.get(i).getHowToDemo()));
				jsonStory.put("Link", "");
				jsonStory.put("Release", "");
				jsonStory.put("Sprint", stories.get(i).getSprintId());
				jsonStory.put("FilterType", getFilterType(stories.get(i)));
				
				if (stories.get(i).getAttachFiles().size() == 0) jsonStory.put("Attach", false);
				else jsonStory.put("Attach", true);
				
				ArrayList<AttachFileObject> attachFiles = stories.get(i).getAttachFiles();
				JSONArray jsonAttachFiles = new JSONArray();
				for (AttachFileObject attachFile : attachFiles) {
					JSONObject jsonAttachFile = new JSONObject();
					jsonAttachFile.put("IssueId", attachFile.getIssueId());
					jsonAttachFile.put("FileId", attachFile.getId());
					jsonAttachFile.put("FileName", translateChar.TranslateJSONChar(attachFile.getName()));
					
					// parse Dateformat as Gson Default DateFormat (TaskBoard page)
					DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT);
					Date date = new Date(attachFile.getCreateTime());
					String attachTime = dateFormat.format(date);
					ProjectObject project = ProjectObject.get(stories.get(i).getId());
					jsonAttachFile.put("UploadDate", attachTime);
					jsonAttachFile.put("FilePath", "fileDownload.do?projectName=" + project.getName() + "&fileId=" + attachFile.getId() + "&fileName=" + attachFile.getName());
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
	
	public String translateTaskToJson(TaskObject task) {
		TranslateSpecialChar translateChar = new TranslateSpecialChar();
		JSONObject responseText = new JSONObject();
		try {
			responseText.put("success", true);
			responseText.put("Total", 1);
			
			JSONObject jsonTask = new JSONObject();
			jsonTask.put("Id", task.getId());
			jsonTask.put("Name", translateChar.TranslateJSONChar((task.getName())));
			jsonTask.put("Value", "");
			jsonTask.put("Estimate", task.getEstimate());
			jsonTask.put("Importance", "");
			jsonTask.put("Tag", "");
			jsonTask.put("Status", task.getStatusString());
			jsonTask.put("Notes", translateChar.TranslateJSONChar(task.getNotes()));
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
				attachFileJson.put("FilePath", "fileDownload.do?projectName=" + task.getProjectId() + "&fileId=" + attachFile.getId()
						+ "&fileName=" + attachFile.getName());
				
				DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT);
				Date date = new Date(attachFile.getCreateTime());
				String attachTime = dateFormat.format(date);
				
				attachFileJson.put("UploadDate", attachTime);
				attachFiles.put(attachFileJson);
			}
			
			jsonTask.put("AttachFileList", attachFiles);
			responseText.put("Stories", new JSONArray().put(jsonTask));
			
			return responseText.toString();
		} catch (JSONException e) {
		}
		return new JSONObject().toString();
	}

	// for Taskboard, CO data, include Handler + Partners
	public String translateTaskboardIssueToJson(IIssue issue) {
		TranslateSpecialChar translateChar = new TranslateSpecialChar();
		JSONObject responseText = new JSONObject();
		try {
			responseText.put("success", true);
			
			JSONObject jsonIssue = new JSONObject();
			// 若需要其他欄位請再新增
			jsonIssue.put("Id", issue.getIssueID());
			jsonIssue.put("Link", translateChar.TranslateJSONChar(issue.getIssueLink()));
			jsonIssue.put("Name", translateChar.TranslateJSONChar((issue.getSummary())));
			jsonIssue.put("Handler", issue.getAssignto());
			jsonIssue.put("Partners", issue.getPartners());
			responseText.put("Issue", jsonIssue);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return responseText.toString();
	}
	
	public String translateTaskboardTaskToJson(TaskObject task) {
		JSONObject responseText = new JSONObject();
		try {
			responseText.put("success", true);
			JSONObject jsonIssue = new JSONObject();
			// 若需要其他欄位請再新增
			jsonIssue.put("Id", task.getId());
			jsonIssue.put("Link", "");
			jsonIssue.put("Name", task.getName());
			jsonIssue.put("Handler", task.getHandler() == null ? "" : task.getHandler().getUsername());
			jsonIssue.put("Partners", task.getPartnersUsername());
			responseText.put("Issue", jsonIssue);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return responseText.toString();
	}

	private String getFilterType(StoryObject storyObject) {

		// status 為 Done
		if (storyObject.getStatus() == StoryObject.STATUS_DONE) {
			return ScrumEnum.DONE;
		}

		// business value 存在 & estimate 存在 & importance 存在
		if (((!(storyObject.getValue() == 0))) &&
		    ((!(storyObject.getEstimate() == 0))) &&
		    ((!(storyObject.getImportance() == 0)))) {

			// status 為 new
			if (storyObject.getStatus() == StoryObject.NO_PARENT) {
				return ScrumEnum.DETAIL;
			}
		}

		// business value 不存在 以及 其他狀況預設為 Backlog
		return ScrumEnum.BACKLOG;
	}

	// for AjaxGetSprintIndexInfoAction
	public String translateSprintInfoToJson(int currentSprintId,
	        double InitialPoint, double CurrentPoint,
	        double InitialHours, double CurrentHours,
	        int releaseId, String SprintGoal,
	        String StoryChartUrl, String TaskChartUrl, boolean isCurrentSprint) throws JSONException {

		TranslateSpecialChar translateChar = new TranslateSpecialChar();
		JSONObject responseText = new JSONObject();
		responseText.put("success", true);
		responseText.put("Total", 1);
		JSONObject sprint = new JSONObject();
		sprint.put("Id", currentSprintId);
		sprint.put("Name", "Sprint #" + translateChar.TranslateJSONChar(String.valueOf(currentSprintId)));
		sprint.put("InitialPoint", String.valueOf(InitialPoint));
		sprint.put("CurrentPoint", String.valueOf(CurrentPoint));
		sprint.put("InitialHours", String.valueOf(InitialHours));
		sprint.put("CurrentHours", String.valueOf(CurrentHours));
		sprint.put("ReleaseID", "Release #" + translateChar.HandleNullString(Integer.toString(releaseId)));
		sprint.put("SprintGoal", translateChar.TranslateJSONChar(SprintGoal));
		sprint.put("StoryChartUrl", StoryChartUrl);
		sprint.put("TaskChartUrl", TaskChartUrl);
		sprint.put("IsCurrentSprint", isCurrentSprint);
		responseText.put("Sprint", sprint);

		return responseText.toString();
	}

	// for ShowSprintBacklogAction
	public String translateSprintBacklogToJson(ArrayList<StoryObject> stories,
			int currentSprintId, double currentPoint, double limitedPoint,
			double taskPoint, int releaseID, String SprintGoal) {
		
		TranslateSpecialChar translateChar = new TranslateSpecialChar();

		JSONObject responseText = new JSONObject();
		try {
			responseText.put("success", true);
			responseText.put("Total", stories.size());
			
			JSONObject sprint = new JSONObject();
			sprint.put("Id", currentSprintId);
			sprint.put("Name", "Sprint #" + translateChar.TranslateJSONChar(String.valueOf(currentSprintId)));
			sprint.put("CurrentPoint", String.valueOf(currentPoint));
			sprint.put("LimitedPoint", String.valueOf(limitedPoint));
			sprint.put("TaskPoint", String.valueOf(taskPoint));
			sprint.put("ReleaseID", "Release #" + translateChar.HandleNullString(Integer.toString(releaseID)));
			sprint.put("SprintGoal", SprintGoal);
			responseText.put("Sprint", sprint);
			
			JSONArray jsonStories = new JSONArray();
			for (StoryObject story : stories) {
				JSONObject jsonStory = new JSONObject();
				
				jsonStory.put("Id", story.getId());
				jsonStory.put("Link", "");
				jsonStory.put("Name", translateChar.TranslateJSONChar(story.getName()));
				jsonStory.put("Value", story.getValue());
				jsonStory.put("Importance", story.getImportance());
				jsonStory.put("Estimate", story.getEstimate());
				jsonStory.put("Status", story.getStatusString());
				jsonStory.put("Notes", translateChar.TranslateJSONChar(story.getNotes()));
				jsonStory.put("Tag", translateChar.TranslateJSONChar(Join(story.getTags(), ",")));
				jsonStory.put("HowToDemo", translateChar.TranslateJSONChar(story.getHowToDemo()));
				jsonStory.put("Release", "");
				jsonStory.put("Sprint", story.getSprintId());
				
				if (story.getAttachFiles().size() == 0) jsonStory.put("Attach", "false");
				else jsonStory.put("Attach", "true");
				
				ArrayList<AttachFileObject> files = story.getAttachFiles();
				JSONArray jsonFiles = new JSONArray();
				for (AttachFileObject file : files) {
					JSONObject jsonFile = new JSONObject();
					jsonFile.put("IssueId", file.getIssueId());
					jsonFile.put("FileId", file.getId());
					jsonFile.put("FileName", translateChar.TranslateXMLChar(translateChar.TranslateJSONChar(file.getName())));
					jsonFile.put("DownloadPath", "fileDownload.do?projectName=" + ProjectObject.get(story.getProjectId()).getName()
							+ "&fileId=" + file.getId() + "&fileName=" + file.getName());
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

	// 將 Kanban WorkItem 轉換成 Json 格式
	public String translateWorkitemToJson(List<IIssue> items, int typeId) {
		TranslateSpecialChar translateChar = new TranslateSpecialChar();

		JSONObject responseText = new JSONObject();
		try {
			responseText.put("success", true);
			responseText.put("Total", items.size());
			
			JSONObject type = new JSONObject();
			type.put("Id", typeId);
			responseText.put("IssueType", type);
			
			JSONArray jsonWorkItems = new JSONArray();
			for (int i = 0; i < items.size(); i++) {
				JSONObject jsonWorkItem = new JSONObject();
				
				jsonWorkItem.put("Id", items.get(i).getIssueID());
				jsonWorkItem.put("Link", "showIssueInformation.do?issueID=" + items.get(i).getIssueID());
				jsonWorkItem.put("Name", translateChar.TranslateJSONChar(items.get(i).getSummary()));
				jsonWorkItem.put("Type", items.get(i).getFieldValue("Type"));
				jsonWorkItem.put("Status", items.get(i).getFieldValue("Status"));
				jsonWorkItem.put("Priority", items.get(i).getFieldValue("Priority"));
				jsonWorkItem.put("WorkState", items.get(i).getFieldValue("WorkState"));
				jsonWorkItem.put("Size", items.get(i).getFieldValue("Size"));
				jsonWorkItem.put("Handler", items.get(i).getAssignto());
				jsonWorkItem.put("Deadline", items.get(i).getFieldValue("Deadline"));
				jsonWorkItem.put("Description", translateChar.TranslateJSONChar(items.get(i).getDescription()));
				
				if (items.get(i).getAttachFiles().size() == 0) jsonWorkItem.put("Attach", "false");
				else jsonWorkItem.put("Attach", "true");
				
				ArrayList<AttachFileObject> files = items.get(i).getAttachFiles();
				JSONArray jsonFiles = new JSONArray();
				for (AttachFileObject file : files) {
					JSONObject jsonFile = new JSONObject();
					jsonFile.put("IssueId", file.getIssueId());
					jsonFile.put("FileId", file.getId());
					jsonFile.put("FileName", translateChar.TranslateXMLChar(translateChar.TranslateJSONChar(file.getName())));
					jsonFile.put("DownloadPath", "fileDownload.do?projectName=" + items.get(i).getProjectName()
							+ "&fileId=" + file.getId() + "&fileName=" + file.getName());
					jsonFiles.put(jsonFile);
				}
				jsonWorkItem.put("AttachFileList", jsonFiles);
				jsonWorkItems.put(jsonWorkItem);
			}
			responseText.put("WorkItems", jsonWorkItems);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return responseText.toString();
	}

	// 將 Kanban Status 轉換成 Json 格式
	public String translateStatusToJson(List<IIssue> items, int typeId) {
		TranslateSpecialChar translateChar = new TranslateSpecialChar();

		JSONObject responseText = new JSONObject();
		try {
			responseText.put("success", true);
			responseText.put("Total", items.size());
			
			JSONObject type = new JSONObject();
			type.put("Id", typeId);
			responseText.put("IssueType", type);
			
			JSONArray jsonStatuses = new JSONArray();
			for (int i = 0; i < items.size(); i++) {
				JSONObject jsonStatus = new JSONObject();
				jsonStatus.put("Id", items.get(i).getIssueID());
				jsonStatus.put("Name", translateChar.TranslateJSONChar(items.get(i).getSummary()));
				jsonStatus.put("Description", translateChar.TranslateJSONChar(items.get(i).getDescription()));
				jsonStatus.put("Limit", items.get(i).getFieldValue("Limit"));
				
				jsonStatuses.put(jsonStatus);
			}
			responseText.put("Statuses", jsonStatuses);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return responseText.toString();
	}

	// 將 BundownChart Data 轉換成 Json 格式
	public String translateBurndownChartDataToJson(LinkedHashMap<Date, Double> ideal, LinkedHashMap<Date, Double> real) {
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
				
				if (real.get(idealPointArray[i]) != null) obj.put("RealPoint", real.get(idealPointArray[i]));
				else obj.put("RealPoint", "null");
				
				array.put(obj);
			}
			responseText.put("Points", array);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return responseText.toString();
	}

	// 將 CPI/SPI 圖表 data 轉成 JSON
	public String translateCPI_SPI_DataToJson(List<Map.Entry<Integer, Double>> cpiTupleList,
			List<Map.Entry<Integer, Double>> spiTupleList, Double[] idealArray) {
		
		JSONObject responseText = new JSONObject();
		
		try {
			// make sure spiArray and cpiArray with the same sprint amount
			if (cpiTupleList.size() != spiTupleList.size()) {
				responseText.put("success", false);
				return responseText.toString();
			}
			int sprintAmount = cpiTupleList.size();

			responseText.put("success", true);

			// jsonDatas means SPI_CPI Data
			JSONArray jsonDatas = new JSONArray();
			for (int i = 0; i < sprintAmount; i++) { // sprint number since from 1
				JSONObject jsonData = new JSONObject();

				jsonData.put("SprintId", cpiTupleList.get(i).getKey());
				jsonData.put("CPI", cpiTupleList.get(i).getValue());
				jsonData.put("SPI", spiTupleList.get(i).getValue());
				jsonData.put("Ideal", idealArray[i]);

				jsonDatas.put(jsonData);
			}
			responseText.put("CPI_SPI_Data", jsonDatas);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return responseText.toString();
	}

	public String translateEV_PV_TAC_DataToJson(List<Map.Entry<Integer, Double>> evTupleList,
			List<Map.Entry<Integer, Double>> pvTupleList,
			List<Map.Entry<Integer, Double>> tacTupleList) {
		
		JSONObject obj = new JSONObject();
		
		try {
			// make sure spiArray and cpiArray with the same sprint amount
			if (evTupleList.size() != pvTupleList.size() || evTupleList.size() != tacTupleList.size() || pvTupleList.size() != tacTupleList.size()) {
				obj.put("success", false);
				return obj.toString();
			}
			int sprintAmount = evTupleList.size();

			obj.put("success", true);

			// jsonDatas means SPI_CPI Data
			JSONArray jsonDatas = new JSONArray();
			for (int i = 0; i < sprintAmount; i++) { // sprint number since from 1
				JSONObject jsonData = new JSONObject();

				jsonData.put("SprintId", evTupleList.get(i).getKey());
				jsonData.put("EV", evTupleList.get(i).getValue());
				jsonData.put("PV", pvTupleList.get(i).getValue());
				jsonData.put("TAC", tacTupleList.get(i).getValue());

				jsonDatas.put(jsonData);
			}
			obj.put("PV_EV_TAC_Data", jsonDatas);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj.toString();
	}

	public String Join(List<TagObject> tags, String delimiter) {
		if (tags.isEmpty()) return "";

		StringBuilder text = new StringBuilder();

		for (TagObject x : tags)
			text.append(x.getName() + delimiter);

		text.delete(text.length() - delimiter.length(), text.length());

		return text.toString();
	}
}
