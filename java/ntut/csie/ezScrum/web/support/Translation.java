package ntut.csie.ezScrum.web.support;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.web.dataObject.AttachFileObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;

public class Translation {
	public Translation() { /* empty */}

	// useless
	public String translateStory(IIssue[] stories) {
		TranslateSpecialChar TranslateChar = new TranslateSpecialChar();

		StringBuilder sb = new StringBuilder();
		sb.append("<ProductBacklog>");
		sb.append("<Total>" + stories.length + "</Total>");
		for (int i = 0; i < stories.length; i++) {
			sb.append("<Story>");
			sb.append("<Id>" + stories[i].getIssueID() + "</Id>");
			sb.append("<Link>" + TranslateChar.TranslateXMLChar(stories[i].getIssueLink()) + "</Link>");
			sb.append("<Name>" + TranslateChar.TranslateXMLChar(stories[i].getSummary()) + "</Name>");
			sb.append("<Importance>" + stories[i].getImportance() + "</Importance>");
			sb.append("<Estimate>" + stories[i].getEstimated() + "</Estimate>");
			sb.append("<Status>" + stories[i].getStatus() + "</Status>");
			sb.append("<Notes>" + TranslateChar.TranslateXMLChar(stories[i].getNotes()) + "</Notes>");
			sb.append("<HowToDemo>" + TranslateChar.TranslateXMLChar(stories[i].getHowToDemo()) + "</HowToDemo>");
			sb.append("<Release>" + TranslateChar.HandleNullString(stories[i].getReleaseID()) + "</Release>");
			sb.append("<Sprint>" + TranslateChar.HandleNullString(stories[i].getSprintID()) + "</Sprint>");
			sb.append("<Tag>" + TranslateChar.TranslateXMLChar(Join(stories[i].getTags(), ",")) + "</Tag>");
			if (stories[i].getAttachFiles().size() == 0) sb.append("<Attach>false</Attach>");
			else sb.append("<Attach>true</Attach>");
			sb.append("</Story>");
		}

		sb.append("</ProductBacklog>");

		return sb.toString();
	}

	// for GetEditStoryInfoAction
	public String translateStory(IIssue story) {
		TranslateSpecialChar TranslateChar = new TranslateSpecialChar();

		StringBuilder sb = new StringBuilder();
		sb.append("<ProductBacklog>");
		sb.append("<Total>1</Total>");
		sb.append("<Story>");
		sb.append("<Id>" + story.getIssueID() + "</Id>");
		sb.append("<Link>" + TranslateChar.TranslateXMLChar(story.getIssueLink()) + "</Link>");
		sb.append("<Name>" + TranslateChar.TranslateXMLChar(story.getSummary()) + "</Name>");
		sb.append("<Value>" + story.getValue() + "</Value>");
		sb.append("<Importance>" + story.getImportance() + "</Importance>");
		sb.append("<Estimate>" + story.getEstimated() + "</Estimate>");
		sb.append("<Status>" + story.getStatus() + "</Status>");
		sb.append("<Notes>" + TranslateChar.TranslateXMLChar(story.getNotes()) + "</Notes>");
		sb.append("<HowToDemo>" + TranslateChar.TranslateXMLChar(story.getHowToDemo()) + "</HowToDemo>");
		sb.append("<Release>" + TranslateChar.HandleNullString(story.getReleaseID()) + "</Release>");
		sb.append("<Sprint>" + TranslateChar.HandleNullString(story.getSprintID()) + "</Sprint>");
		sb.append("<Tag>" + TranslateChar.TranslateXMLChar(Join(story.getTags(), ",")) + "</Tag>");
		if (story.getAttachFiles().size() == 0) sb.append("<Attach>false</Attach>");
		else sb.append("<Attach>true</Attach>");
		sb.append("</Story>");
		sb.append("</ProductBacklog>");

		return sb.toString();
	}

	// for ShowScrumIssueAction
	public String translateScrumIssueToJson(List<IIssue> scrumIssues) {
		TranslateSpecialChar TranslateChar = new TranslateSpecialChar();

		JsonObject obj = new JsonObject();

		obj.append("success", true);
		obj.append("Total", scrumIssues.size());

		JsonArray jsonIssues = new JsonArray();
		for (int i = 0; i < scrumIssues.size(); i++) {
			JsonObject jsonIssue = new JsonObject();

			jsonIssue.append("Id", scrumIssues.get(i).getIssueID());
			jsonIssue.append("Link", TranslateChar.TranslateJSONChar(scrumIssues.get(i).getIssueLink()));
			jsonIssue.append("Category", TranslateChar.TranslateJSONChar(scrumIssues.get(i).getCategory()));
			jsonIssue.append("Name", TranslateChar.TranslateJSONChar(scrumIssues.get(i).getSummary()));
			jsonIssue.append("Estimate", scrumIssues.get(i).getEstimated());
			jsonIssue.append("Status", scrumIssues.get(i).getStatus());
			jsonIssue.append("Notes", TranslateChar.TranslateJSONChar(scrumIssues.get(i).getNotes()));
			jsonIssue.append("Sprint", TranslateChar.HandleNullString(scrumIssues.get(i).getSprintID()));
			if (scrumIssues.get(i).getAttachFiles().size() == 0) jsonIssue.append("Attach", "false");
			else jsonIssue.append("Attach", "true");

			ArrayList<AttachFileObject> files = scrumIssues.get(i).getAttachFiles();
			JsonArray jsonFiles = new JsonArray();
			for (AttachFileObject file : files) {
				JsonObject jsonFile = new JsonObject();
				jsonFile.append("IssueId", file.getIssueId());
				jsonFile.append("FileId", file.getId());
				jsonFile.append("FileName", TranslateChar.TranslateXMLChar(TranslateChar.TranslateJSONChar(file.getName())));
				jsonFile.append("DownloadPath", "fileDownload.do?projectName=" + scrumIssues.get(i).getProjectName() 
						+ "&fileId=" + file.getId() + "&fileName=" + file.getName());
				jsonFiles.append(jsonFile);
			}
			jsonIssue.append("AttachFileList", jsonFiles);

			jsonIssues.append(jsonIssue);
		}
		obj.append("ScrumIssues", jsonIssues);

		return obj.toString();
	}

	// 將 Custom issue 轉換成 Json 格式
	public String translateCustomIssueToJson(List<IIssue> customIssues) {
		TranslateSpecialChar TranslateChar = new TranslateSpecialChar();

		JsonObject obj = new JsonObject();
		obj.append("success", true);
		obj.append("Total", customIssues.size());
		JsonArray jsonIssues = new JsonArray();
		for (int i = 0; i < customIssues.size(); i++) {
			JsonObject jsonIssue = new JsonObject();
			jsonIssue.append("Id", customIssues.get(i).getIssueID());
			jsonIssue.append("ProjectName", TranslateChar.TranslateJSONChar(customIssues.get(i).getProjectName()));
			jsonIssue.append("Link", "showIssueInformation.do?projectName=" + customIssues.get(i).getProjectName()
			        + "&" + "issueID=" + customIssues.get(i).getIssueID());
			jsonIssue.append("Category", TranslateChar.TranslateJSONChar(customIssues.get(i).getCategory()));
			jsonIssue.append("Name", TranslateChar.TranslateJSONChar(customIssues.get(i).getSummary()));
			jsonIssue.append("Status", customIssues.get(i).getFieldValue("Status"));
			jsonIssue.append("Priority", customIssues.get(i).getFieldValue("Priority"));

			if (customIssues.get(i).getFieldValue("Handled").equals("True")) jsonIssue.append("Handled", customIssues.get(i).getFieldValue("Handled"));
			else jsonIssue.append("Handled", "False");

			jsonIssue.append("ReportUserName", TranslateChar.TranslateJSONChar(customIssues.get(i).getFieldValue("ReportUserName")));

			// 如果comment為-1則代表為空值，為了不讓ext在expander顯示上資訊，所以將資料替換為null
			String comment = customIssues.get(i).getFieldValue("Comment");
			if (comment == "-1") comment = "";

			jsonIssue.append("Comment", TranslateChar.TranslateJSONChar(comment));

			jsonIssue.append("Email", TranslateChar.TranslateJSONChar(customIssues.get(i).getFieldValue("Email")));
			jsonIssue.append("Description", TranslateChar.TranslateJSONChar(customIssues.get(i).getDescription()));
			jsonIssue.append("Handler", customIssues.get(i).getAssignto());

			if (customIssues.get(i).getAttachFiles().size() == 0) jsonIssue.append("Attach", "false");
			else jsonIssue.append("Attach", "true");

			ArrayList<AttachFileObject> files = customIssues.get(i).getAttachFiles();
			JsonArray jsonFiles = new JsonArray();
			for (AttachFileObject file : files) {
				JsonObject jsonFile = new JsonObject();
				jsonFile.append("IssueId", file.getIssueId());
				jsonFile.append("FileId", file.getId());
				jsonFile.append("FileName", TranslateChar.TranslateXMLChar(TranslateChar.TranslateJSONChar(file.getName())));
				jsonFile.append("DownloadPath", "fileDownload.do?projectName=" + customIssues.get(i).getProjectName()
						+ "&fileId=" + file.getId() + "&fileName=" + file.getName());
				jsonFiles.append(jsonFile);
			}
			jsonIssue.append("AttachFileList", jsonFiles);
			jsonIssues.append(jsonIssue);
		}
		obj.append("CustomIssues", jsonIssues);

		return obj.toString();
	}

	public String translateStoryToJson(IIssue story) {
		IIssue[] data = new IIssue[1];
		data[0] = story;
		return this.translateStoryToJson(data);
	}

	// for ShowProductBacklogAction
	public String translateStoryToJson(IIssue[] stories) {
		TranslateSpecialChar TranslateChar = new TranslateSpecialChar();

		JsonObject obj = new JsonObject();

		obj.append("success", true);
		obj.append("Total", stories.length);

		JsonArray jsonStroies = new JsonArray();
		for (int i = 0; i < stories.length; i++) {
			JsonObject jsonStory = new JsonObject();

			jsonStory.append("Id", stories[i].getIssueID());
			jsonStory.append("Name", TranslateChar.TranslateJSONChar((stories[i].getSummary())));
			jsonStory.append("Value", stories[i].getValue());
			jsonStory.append("Estimate", stories[i].getEstimated());
			jsonStory.append("Importance", stories[i].getImportance());
			jsonStory.append("Tag", TranslateChar.TranslateJSONChar(Join(stories[i].getTags(), ",")));
			jsonStory.append("Status", stories[i].getStatus());
			jsonStory.append("Notes", TranslateChar.TranslateJSONChar(stories[i].getNotes()));
			jsonStory.append("HowToDemo", TranslateChar.TranslateJSONChar(stories[i].getHowToDemo()));
			jsonStory.append("Link", TranslateChar.TranslateJSONChar(stories[i].getIssueLink()));
			jsonStory.append("Release", TranslateChar.HandleNullString(stories[i].getReleaseID()));
			jsonStory.append("Sprint", TranslateChar.HandleNullString(stories[i].getSprintID()));
			jsonStory.append("FilterType", getFilterType(stories[i]));

			if (stories[i].getAttachFiles().size() == 0) jsonStory.append("Attach", false);
			else jsonStory.append("Attach", true);

			ArrayList<AttachFileObject> files = stories[i].getAttachFiles();
			JsonArray jsonFiles = new JsonArray();
			for (AttachFileObject file : files) {
				JsonObject jsonFile = new JsonObject();
				jsonFile.append("IssueId", file.getIssueId());
				jsonFile.append("FileId", file.getId());
				jsonFile.append("FileName", TranslateChar.TranslateJSONChar(file.getName()));

				// parse Dateformat as Gson Default DateFormat (TaskBoard page)嚗� Taskboard ��交��澆�蝯曹�(銝��憪������Gson�瑕撟怨���
				DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT);
				Date date = new Date(file.getCreateTime());
				String attachTime = dateFormat.format(date);
				jsonFile.append("UploadDate", attachTime);
				jsonFile.append("FilePath", "fileDownload.do?projectName=" + stories[i].getProjectName() + "&fileId=" + file.getId()
						+ "&fileName=" + file.getName());
				jsonFiles.append(jsonFile);
			}
			jsonStory.append("AttachFileList", jsonFiles);

			jsonStroies.append(jsonStory);
		}
		obj.append("Stories", jsonStroies);

		return obj.toString();
	}

	// for Taskboard, CO data, include Handler + Partners
	public String translateTaskboardIssueToJson(IIssue issue) {
		TranslateSpecialChar TranslateChar = new TranslateSpecialChar();

		JsonObject obj = new JsonObject();
		obj.append("success", true);

		JsonObject jsonIssue = new JsonObject();
		// 若需要其他欄位請再新增
		jsonIssue.append("Id", issue.getIssueID());
		jsonIssue.append("Link", TranslateChar.TranslateJSONChar(issue.getIssueLink()));
		jsonIssue.append("Name", TranslateChar.TranslateJSONChar((issue.getSummary())));
		jsonIssue.append("Handler", issue.getAssignto());
		jsonIssue.append("Partners", issue.getPartners());

		obj.append("Issue", jsonIssue);

		return obj.toString();
	}
	
	public String translateTaskboardTaskToJson(TaskObject task) {
		JsonObject obj = new JsonObject();
		obj.append("success", true);

		JsonObject jsonIssue = new JsonObject();
		// 若需要其他欄位請再新增
		jsonIssue.append("Id", task.getId());
		jsonIssue.append("Link", "");
		jsonIssue.append("Name", task.getName());
		jsonIssue.append("Handler", task.getHandler() == null ? "" : task.getHandler().getUsername());
		jsonIssue.append("Partners", task.getPartnersUsername());
		obj.append("Issue", jsonIssue);

		return obj.toString();
	}

	private String getFilterType(IIssue story) {

		// status 為 Done
		if (story.getStatus().equals(ITSEnum.S_CLOSED_STATUS)) {
			return ScrumEnum.DONE;
		}

		// business value 存在 & estimate 存在 & importance 存在
		if (((story.getValue() != null) && (!story.getValue().equals("0"))) &&
		        ((story.getEstimated() != null) && (!story.getEstimated().equals("0"))) &&
		        ((story.getImportance() != null) && (!story.getImportance().equals("0")))) {

			// status 為 new
			if (story.getStatus().equals(ITSEnum.S_NEW_STATUS)) {
				return ScrumEnum.DETAIL;
			}
		}

		// business value 不存在 以及 其他狀況預設為 Backlog
		return ScrumEnum.BACKLOG;
	}

	// for AjaxGetSprintIndexInfoAction
	public String translateSprintInfoToJson(int currentSprintID,
	        double InitialPoint, double CurrentPoint,
	        double InitialHours, double CurrentHours,
	        int releaseID, String SprintGoal,
	        String StoryChartUrl, String TaskChartUrl, boolean isCurrentSprint) {

		TranslateSpecialChar TranslateChar = new TranslateSpecialChar();
		JsonObject obj = new JsonObject();
		obj.append("success", true);
		obj.append("Total", 1);
		JsonObject sprint = new JsonObject();
		sprint.append("Id", currentSprintID);
		sprint.append("Name", "Sprint #" + TranslateChar.TranslateJSONChar(String.valueOf(currentSprintID)));
		sprint.append("InitialPoint", String.valueOf(InitialPoint));
		sprint.append("CurrentPoint", String.valueOf(CurrentPoint));
		sprint.append("InitialHours", String.valueOf(InitialHours));
		sprint.append("CurrentHours", String.valueOf(CurrentHours));
		sprint.append("ReleaseID", "Release #" + TranslateChar.HandleNullString(Integer.toString(releaseID)));
		sprint.append("SprintGoal", TranslateChar.TranslateJSONChar(SprintGoal));
		sprint.append("StoryChartUrl", StoryChartUrl);
		sprint.append("TaskChartUrl", TaskChartUrl);
		sprint.append("IsCurrentSprint", isCurrentSprint);
		obj.append("Sprint", sprint);

		return obj.toString();
	}

	// for ShowSprintBacklogAction
	public String translateStoryToJson(List<IIssue> issues, int currentSprintID, double currentPoint, double limitedPoint, double taskPoint, int releaseID, String SprintGoal) {
		TranslateSpecialChar TranslateChar = new TranslateSpecialChar();

		JsonObject obj = new JsonObject();

		obj.append("success", true);
		obj.append("Total", issues.size());

		JsonObject sprint = new JsonObject();
		sprint.append("Id", currentSprintID);
		sprint.append("Name", "Sprint #" + TranslateChar.TranslateJSONChar(String.valueOf(currentSprintID)));
		sprint.append("CurrentPoint", String.valueOf(currentPoint));
		sprint.append("LimitedPoint", String.valueOf(limitedPoint));
		sprint.append("TaskPoint", String.valueOf(taskPoint));
		sprint.append("ReleaseID", "Release #" + TranslateChar.HandleNullString(Integer.toString(releaseID)));

		sprint.append("SprintGoal", SprintGoal);
		obj.append("Sprint", sprint);

		JsonArray jsonStories = new JsonArray();
		for (IIssue issue : issues) {
			JsonObject jsonStory = new JsonObject();

			jsonStory.append("Id", issue.getIssueID());
			jsonStory.append("Link", TranslateChar.TranslateJSONChar(issue.getIssueLink()));
			jsonStory.append("Name", TranslateChar.TranslateJSONChar(issue.getSummary()));
			jsonStory.append("Value", issue.getValue());
			jsonStory.append("Importance", issue.getImportance());
			jsonStory.append("Estimate", issue.getEstimated());
			jsonStory.append("Status", issue.getStatus());
			jsonStory.append("Notes", TranslateChar.TranslateJSONChar(issue.getNotes()));
			jsonStory.append("Tag", TranslateChar.TranslateJSONChar(Join(issue.getTags(), ",")));
			jsonStory.append("HowToDemo", TranslateChar.TranslateJSONChar(issue.getHowToDemo()));
			jsonStory.append("Release", TranslateChar.HandleNullString(issue.getReleaseID()));
			jsonStory.append("Sprint", TranslateChar.HandleNullString(issue.getSprintID()));

			if (issue.getAttachFiles().size() == 0) jsonStory.append("Attach", "false");
			else jsonStory.append("Attach", "true");

			ArrayList<AttachFileObject> files = issue.getAttachFiles();
			JsonArray jsonFiles = new JsonArray();
			for (AttachFileObject file : files) {
				JsonObject jsonFile = new JsonObject();
				jsonFile.append("IssueId", file.getIssueId());
				jsonFile.append("FileId", file.getId());
				jsonFile.append("FileName", TranslateChar.TranslateXMLChar(TranslateChar.TranslateJSONChar(file.getName())));
				jsonFile.append("DownloadPath", "fileDownload.do?projectName=" + issue.getProjectName()
						+ "&fileId=" + file.getId() + "&fileName=" + file.getName());
				jsonFiles.append(jsonFile);
			}
			jsonStory.append("AttachFileList", jsonFiles);

			jsonStories.append(jsonStory);
		}
		obj.append("Stories", jsonStories);
		return obj.toString();
	}

	// 將 Kanban WorkItem 轉換成 Json 格式
	public String translateWorkitemToJson(List<IIssue> items, int typeID) {
		TranslateSpecialChar TranslateChar = new TranslateSpecialChar();

		JsonObject obj = new JsonObject();

		obj.append("success", true);
		obj.append("Total", items.size());

		JsonObject type = new JsonObject();
		type.append("Id", typeID);
		obj.append("IssueType", type);

		JsonArray jsonWorkItems = new JsonArray();
		for (int i = 0; i < items.size(); i++) {
			JsonObject jsonWorkItem = new JsonObject();

			jsonWorkItem.append("Id", items.get(i).getIssueID());
			jsonWorkItem.append("Link", "showIssueInformation.do?issueID=" + items.get(i).getIssueID());
			jsonWorkItem.append("Name", TranslateChar.TranslateJSONChar(items.get(i).getSummary()));
			jsonWorkItem.append("Type", items.get(i).getFieldValue("Type"));
			jsonWorkItem.append("Status", items.get(i).getFieldValue("Status"));
			jsonWorkItem.append("Priority", items.get(i).getFieldValue("Priority"));
			jsonWorkItem.append("WorkState", items.get(i).getFieldValue("WorkState"));
			jsonWorkItem.append("Size", items.get(i).getFieldValue("Size"));
			jsonWorkItem.append("Handler", items.get(i).getAssignto());
			jsonWorkItem.append("Deadline", items.get(i).getFieldValue("Deadline"));
			jsonWorkItem.append("Description", TranslateChar.TranslateJSONChar(items.get(i).getDescription()));

			if (items.get(i).getAttachFiles().size() == 0) jsonWorkItem.append("Attach", "false");
			else jsonWorkItem.append("Attach", "true");

			ArrayList<AttachFileObject> files = items.get(i).getAttachFiles();
			JsonArray jsonFiles = new JsonArray();
			for (AttachFileObject file : files) {
				JsonObject jsonFile = new JsonObject();
				jsonFile.append("IssueId", file.getIssueId());
				jsonFile.append("FileId", file.getId());
				jsonFile.append("FileName", TranslateChar.TranslateXMLChar(TranslateChar.TranslateJSONChar(file.getName())));
				jsonFile.append("DownloadPath", "fileDownload.do?projectName=" + items.get(i).getProjectName()
						+ "&fileId=" + file.getId() + "&fileName=" + file.getName());
				jsonFiles.append(jsonFile);
			}
			jsonWorkItem.append("AttachFileList", jsonFiles);

			jsonWorkItems.append(jsonWorkItem);
		}
		obj.append("WorkItems", jsonWorkItems);

		return obj.toString();
	}

	// 將 Kanban Status 轉換成 Json 格式
	public String translateStatusToJson(List<IIssue> items, int typeID) {
		TranslateSpecialChar TranslateChar = new TranslateSpecialChar();

		JsonObject obj = new JsonObject();

		obj.append("success", true);
		obj.append("Total", items.size());

		JsonObject type = new JsonObject();
		type.append("Id", typeID);
		obj.append("IssueType", type);

		JsonArray jsonStatuses = new JsonArray();
		for (int i = 0; i < items.size(); i++) {
			JsonObject jsonStatus = new JsonObject();
			jsonStatus.append("Id", items.get(i).getIssueID());
			jsonStatus.append("Name", TranslateChar.TranslateJSONChar(items.get(i).getSummary()));
			jsonStatus.append("Description", TranslateChar.TranslateJSONChar(items.get(i).getDescription()));
			jsonStatus.append("Limit", items.get(i).getFieldValue("Limit"));

			jsonStatuses.append(jsonStatus);
		}
		obj.append("Statuses", jsonStatuses);

		return obj.toString();
	}

	// 將 BundownChart Data 轉換成 Json 格式
	public String translateBurndownChartDataToJson(LinkedHashMap<Date, Double> ideal, LinkedHashMap<Date, Double> real) {
		JsonObject json = new JsonObject();
		json.append("success", true);

		JsonArray array = new JsonArray();

		DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");

		Object[] idealPointArray = ideal.keySet().toArray();

		for (int i = 0; i < idealPointArray.length; i++) {
			JsonObject obj = new JsonObject();
			obj.append("Date", formatter.format(idealPointArray[i]));
			obj.append("IdealPoint", ideal.get(idealPointArray[i]));

			if (real.get(idealPointArray[i]) != null) obj.append("RealPoint", real.get(idealPointArray[i]));
			else obj.append("RealPoint", "null");

			array.append(obj);
		}

		json.append("Points", array);

		return json.toString();
	}

	// 將 CPI/SPI 圖表 data 轉成 JSON
	public String translateCPI_SPI_DataToJson(List<Map.Entry<Integer, Double>> cpiTupleList, List<Map.Entry<Integer, Double>> spiTupleList, Double[] idealArray) {
		JsonObject obj = new JsonObject();
		// make sure spiArray and cpiArray with the same sprint amount
		if (cpiTupleList.size() != spiTupleList.size()) {
			obj.append("success", false);
			return obj.toString();
		}
		int sprintAmount = cpiTupleList.size();

		obj.append("success", true);

		// jsonDatas means SPI_CPI Data
		JsonArray jsonDatas = new JsonArray();
		for (int i = 0; i < sprintAmount; i++) { // sprint number since from 1
			JsonObject jsonData = new JsonObject();

			jsonData.append("SprintId", cpiTupleList.get(i).getKey());
			jsonData.append("CPI", cpiTupleList.get(i).getValue());
			jsonData.append("SPI", spiTupleList.get(i).getValue());
			jsonData.append("Ideal", idealArray[i]);

			jsonDatas.append(jsonData);
		}
		obj.append("CPI_SPI_Data", jsonDatas);

		return obj.toString();
	}

	public String translateEV_PV_TAC_DataToJson(List<Map.Entry<Integer, Double>> evTupleList, List<Map.Entry<Integer, Double>> pvTupleList, List<Map.Entry<Integer, Double>> tacTupleList) {
		JsonObject obj = new JsonObject();
		// make sure spiArray and cpiArray with the same sprint amount
		if (evTupleList.size() != pvTupleList.size() || evTupleList.size() != tacTupleList.size() || pvTupleList.size() != tacTupleList.size()) {
			obj.append("success", false);
			return obj.toString();
		}
		int sprintAmount = evTupleList.size();

		obj.append("success", true);

		// jsonDatas means SPI_CPI Data
		JsonArray jsonDatas = new JsonArray();
		for (int i = 0; i < sprintAmount; i++) { // sprint number since from 1
			JsonObject jsonData = new JsonObject();

			jsonData.append("SprintId", evTupleList.get(i).getKey());
			jsonData.append("EV", evTupleList.get(i).getValue());
			jsonData.append("PV", pvTupleList.get(i).getValue());
			jsonData.append("TAC", tacTupleList.get(i).getValue());

			jsonDatas.append(jsonData);
		}
		obj.append("PV_EV_TAC_Data", jsonDatas);

		return obj.toString();
	}

	public String Join(List<TagObject> tags, String delimiter) {
		if (tags.isEmpty()) return "";

		StringBuilder sb = new StringBuilder();

		for (TagObject x : tags)
			sb.append(x.getName() + delimiter);

		sb.delete(sb.length() - delimiter.length(), sb.length());

		return sb.toString();
	}
}

class JsonObject
{
	protected Map<String, Object> _data;

	public JsonObject()
	{
		_data = new LinkedHashMap<String, Object>();
	}

	public void append(String name, String value)
	{
		_data.put(name, "\"" + value + "\"");
	}

	public void append(String name, int value)
	{
		_data.put(name, String.valueOf(value));
	}

	public void append(String name, double value)
	{
		_data.put(name, String.valueOf(value));
	}

	public void append(String name, JsonObject value)
	{
		_data.put(name, value);
	}

	public void append(String name, JsonArray value)
	{
		_data.put(name, value);
	}

	public void append(String name, boolean value)
	{
		if (value) _data.put(name, "true");
		else _data.put(name, "false");
	}

	public void append(String name, long value)
	{
		_data.put(name, String.valueOf(value));
	}

	public String toString()
	{
		Iterator<String> iter = _data.keySet().iterator();
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		while (iter.hasNext())
		{
			String key = iter.next();
			Object obj = _data.get(key);
			sb.append("\"" + key + "\":" + obj.toString() + ",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("}");

		return sb.toString();
	}
}

class JsonArray
{
	private List<Object> _data;

	public JsonArray()
	{
		_data = new ArrayList<Object>();
	}

	public void append(Object obj)
	{
		_data.add(obj);
	}

	public String toString()
	{
		Iterator<Object> iter = _data.iterator();
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		while (iter.hasNext())
		{
			Object obj = iter.next();
			sb.append(obj.toString() + ",");
		}
		if (sb.charAt(sb.length() - 1) == ',') sb.deleteCharAt(sb.length() - 1);
		sb.append("]");

		return sb.toString();
	}
}