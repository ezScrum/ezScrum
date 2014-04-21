package ntut.csie.ezScrum.restful.mobile.support;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.IIssueHistory;
import ntut.csie.ezScrum.issue.internal.IssueAttachFile;
import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.restful.mobile.util.SprintBacklogUtil;
import ntut.csie.ezScrum.restful.mobile.util.SprintPlanUtil;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.ezScrum.web.support.Translation;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.google.gson.Gson;

public class ConvertSprintBacklog {
	public ConvertSprintBacklog(){
		
	}
	
	public String readSprintInformationList( ISprintPlanDesc iSprintPlanDescList ) throws JSONException{
		JSONObject sprintPlan = new JSONObject();
		JSONObject sprintProperties = new JSONObject();
		sprintProperties.put( SprintPlanUtil.TAG_ID, iSprintPlanDescList.getID() );	//	id
		sprintProperties.put( SprintPlanUtil.TAG_SPRINTGOAL, iSprintPlanDescList.getGoal() );	//	sprint goal
		sprintProperties.put( SprintPlanUtil.TAG_STARTDATE, iSprintPlanDescList.getStartDate() );	//	start date
		sprintProperties.put( SprintPlanUtil.TAG_DEMODATE, iSprintPlanDescList.getDemoDate() );	//	demo date
		sprintProperties.put( SprintPlanUtil.TAG_INTERVAL, iSprintPlanDescList.getInterval() );	//	interval
		sprintProperties.put( SprintPlanUtil.TAG_MEMBERS, iSprintPlanDescList.getMemberNumber() );	//	members count
		sprintProperties.put( SprintPlanUtil.TAG_HOURSCANCOMMIT, iSprintPlanDescList.getAvailableDays() );	//	== hours can submit
		sprintProperties.put( SprintPlanUtil.TAG_DAILYMEETING, iSprintPlanDescList.getNotes() );
		sprintProperties.put( SprintPlanUtil.TAG_DEMOPLACE, iSprintPlanDescList.getDemoPlace() );
		sprintPlan.put( SprintPlanUtil.TAG_CURRENTSPRINT, sprintProperties );
		return sprintPlan.toString();
	}
	public String readSprintInformationList( List<ISprintPlanDesc> iSprintPlanDescList, int currentSprintID ) throws JSONException{
		JSONObject sprintPlanList = new JSONObject();
		JSONArray sprintPlanArray = new JSONArray();
		sprintPlanList.put( SprintPlanUtil.TAG_CURRENTSPRINTID, currentSprintID );
		sprintPlanList.put( SprintPlanUtil.TAG_SPRINTPLANLIST, sprintPlanArray );
		for( ISprintPlanDesc item : iSprintPlanDescList ){
			JSONObject sprintPlan = new JSONObject();
			JSONObject sprintProperties = new JSONObject();
			sprintProperties.put( SprintPlanUtil.TAG_ID, item.getID() );	//	id
			sprintProperties.put( SprintPlanUtil.TAG_SPRINTGOAL, item.getGoal() );	//	sprint goal
			sprintProperties.put( SprintPlanUtil.TAG_STARTDATE, item.getStartDate() );	//	start date
			sprintProperties.put( SprintPlanUtil.TAG_DEMODATE, item.getDemoDate() );	//	demo date
			sprintProperties.put( SprintPlanUtil.TAG_INTERVAL, item.getInterval() );	//	interval
			sprintProperties.put( SprintPlanUtil.TAG_MEMBERS, item.getMemberNumber() );	//	members count
			sprintProperties.put( SprintPlanUtil.TAG_HOURSCANCOMMIT, item.getAvailableDays() );	//	== hours can submit
			sprintProperties.put( SprintPlanUtil.TAG_DAILYMEETING, item.getNotes() );
			sprintProperties.put( SprintPlanUtil.TAG_DEMOPLACE, item.getDemoPlace() );
			sprintPlan.put( SprintPlanUtil.TAG_SPRINTPLAN, sprintProperties );
			sprintPlanArray.put( sprintPlan );
		}
		return sprintPlanList.toString();
	}
	public String readStoryIDList( SprintBacklogLogic sprintBacklogLogic ) throws JSONException{
		JSONObject storyIDList = new JSONObject();
		JSONArray storyArray = new JSONArray();
		List<IIssue> stroyArray = sprintBacklogLogic.getStories();
		for( IIssue item : stroyArray ){
			JSONObject story = new JSONObject();
			story.put("id", item.getIssueID());
			story.put("point", Integer.parseInt(item.getEstimated()));
			story.put("status", item.getStatus());
			storyArray.put(story);
		}
		storyIDList.put(SprintPlanUtil.TAG_STORYLIST, storyArray );
		return storyIDList.toString();
	}
	
	/**
	 * 轉換 task id list 的 json string
	 * @param storyID
	 * @param taskIIssueList
	 * @return
	 * @throws JSONException
	 */
	public String convertTaskIDList(String storyID, IIssue[] taskIIssueList) throws JSONException {
		JSONObject story = new JSONObject();
		JSONObject storyProperties = new JSONObject();
		JSONArray taskIDList = new JSONArray();
		for( IIssue task: taskIIssueList ){
			taskIDList.put( task.getIssueID() );
		}
		storyProperties.put(SprintBacklogUtil.TAG_ID, storyID);
		storyProperties.put(SprintBacklogUtil.TAG_TASKIDLIST, taskIDList);
		
		story.put(SprintBacklogUtil.TAG_STORY, storyProperties);
		return story.toString();
	}
	
	/**
	 * 轉換 task history 的 json string
	 * @param taskHistoryList
	 * @param remainingHourList
	 * @return
	 * @throws JSONException
	 */
	public String convertTaskHistory(List<IIssueHistory> taskHistoryList, List<String> remainingHourList) throws JSONException {
		JSONObject taskHistory = new JSONObject();
		JSONArray historyItemArray = new JSONArray();
		for( int i = 0; i < taskHistoryList.size(); i++ ){
			IIssueHistory history = taskHistoryList.get(i);
			String reaminHour = remainingHourList.get(i);
			String historyType = history.getFieldName().trim();
			if( !isHandlerIDType(historyType) ){
				String modifyDate = parseDate( history.getModifyDate() );
				HistoryItemInfo historyItemInfo = new HistoryItemInfo( history.getDescription() );
				
				JSONObject historyItem = new JSONObject();
				historyItem.put( SprintBacklogUtil.TAG_MODIFYDATE, modifyDate );
				historyItem.put( SprintBacklogUtil.TAG_HISTORYTYPE, historyItemInfo.getType() );
				historyItem.put( SprintBacklogUtil.TAG_DESCRIPTION, historyItemInfo.getDescription());			
				historyItem.put( SprintBacklogUtil.TAG_REMAINHOUR, reaminHour );
				historyItemArray.put(historyItem);
			}
		}
		taskHistory.put(SprintBacklogUtil.TAG_TASKHISTORYLIST, historyItemArray);
		return taskHistory.toString();
	}
	
	private boolean isHandlerIDType( String historyType ){
		if( historyType.equals("handler_id") ){
			return true;
		}else{
			return false;
		}
	}
	
	private class HistoryItemInfo{
		private String description;
		private String type;
		public HistoryItemInfo( String desc ){
			this.parse( desc );
		}

		private void parse(String desc) {
			String [] token = desc.split(":");
			if ( token.length == 2 ) {
				this.setType( token[0].trim() );
				this.setDescription( token[1].trim() );
			} else {
				this.setType("");
				this.setDescription(desc);
			}
		}
		private void setDescription(String description) {
			this.description = description;
		}
		private void setType(String type) {
			this.type = type;
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
	 * @param taskinformationList
	 * @return
	 * @throws JSONException
	 */
	public String readTaskInformationList(IIssue iTaskInformaion) throws JSONException{
		JSONObject taskInformation = new JSONObject();
		JSONObject taskInformations = new JSONObject();
		JSONArray partnersArray = new JSONArray();
		taskInformations.put( SprintBacklogUtil.TAG_ID, ""+iTaskInformaion.getIssueID() );//加上""使輸出格式從ID變為"ID"
		taskInformations.put( SprintBacklogUtil.TAG_NAME, iTaskInformaion.getSummary() );//getName
		taskInformations.put( SprintBacklogUtil.TAG_HANDLER, iTaskInformaion.getAssignto() );//getHandler
		partnersArray.put( iTaskInformaion.getPartners());
		taskInformations.put(SprintBacklogUtil.TAG_PARTERNERS, partnersArray );	
		taskInformations.put( SprintBacklogUtil.TAG_ESTIMATION, iTaskInformaion.getEstimated() );
		taskInformations.put( SprintBacklogUtil.TAG_REMAINHOUR, iTaskInformaion.getRemains() );	
		taskInformations.put( SprintBacklogUtil.TAG_ACTUALHOUR, iTaskInformaion.getActualHour() );
		taskInformations.put( SprintBacklogUtil.TAG_NOTES, iTaskInformaion.getNotes() );
		
		taskInformation.put( SprintBacklogUtil.TAG_TASKINFORMATION, taskInformations );
		return taskInformation.toString();
	}

	
	/**
	 * 轉換多個 task information 的 json string
	 * @param tasks
	 * @return
	 * @throws JSONException
	 */
	public String readTasksInformationList(IIssue[] tasks) throws JSONException {
		JSONObject taskList = new JSONObject();
		JSONArray taskArray = new JSONArray();
		if (tasks == null)
			return "";
		for (IIssue task : tasks) {
			taskArray.put(readTaskInformationList(task));
		}
		taskList.put(SprintBacklogUtil.TAG_TASKLIST, taskArray);
		return taskList.toString();
	}

	/**
	 * 轉換 Sprint Backlog中的Story及Task成 json string
	 * @param sprintBacklogLogic 
	 * @param taskinformationList
	 * @return
	 * @throws JSONException
	 */
	public String convertStoryTaskInformationList( SprintBacklogLogic sprintBacklogLogic, SprintBacklogMapper sb, String handler ) throws JSONException{
		int storyLength = 0;
		ArrayList<TaskBoard_Story> storyList = new ArrayList<TaskBoard_Story>();
		
		if ( (sb != null) && (sb.getSprintPlanId() > 0) ) {
			List<IIssue> stories = sprintBacklogLogic.getStoriesByImp();	
			Map<Long, IIssue[]> taskMap = sb.getTasksMap(); 
			stories = this.filterStory(stories, taskMap, handler);
			
			for (IIssue story : stories) {
				storyList.add(create_TaskBoard_Story(story, taskMap.get(story.getIssueID())));
			}
			storyLength = stories.size();
		}
		else{ // no sprint exist
			storyLength = 0;
		}
		
		Gson gson = new Gson();
		
		HashMap<String,Object> jsonMap = new HashMap<String,Object>();
		jsonMap.put("success", "true");
		jsonMap.put("Total", storyLength);
		jsonMap.put("Stories",storyList);
		
		return gson.toJson(jsonMap);
	}
	
	// filter story and task by handler name
	private List<IIssue> filterStory(List<IIssue> stories, Map<Long, IIssue[]> taskmap, String filtername) {
		List<IIssue> filterissues = new LinkedList<IIssue>();
		
		// All member, return all story
		if (filtername.equals("ALL") || filtername.length() == 0) {
			return stories;
		} else {
			// filter member name by handler, return the story and task map relation
			for (IIssue story : stories) {
				IIssue[] tasks = taskmap.get(story.getIssueID());
				if (tasks != null) {
					List<IIssue> filtertask = new LinkedList<IIssue>();
					
					for (IIssue task : tasks) {
						if (checkParent(filtername, task.getPartners(), task.getAssignto())) {
							filtertask.add(task);
						}
					}
					
					if (filtertask.size() > 0) {
						// cover new filter map
						taskmap.put(story.getIssueID(), filtertask.toArray(new IIssue[filtertask.size()]));
						filterissues.add(story);
					}
				}
			}
			return filterissues;
//			return filterissues.toArray(new IIssue[filterissues.size()]);
		}
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
	private TaskBoard_Story create_TaskBoard_Story(IIssue story, IIssue[] tasks) {

		TaskBoard_Story TB_Story = new TaskBoard_Story(story);
		
		if(tasks != null)
		{
			for(IIssue task:tasks)
			{
				TB_Story.Tasks.add(new TaskBoard_Task(task));
			}
		}
		return TB_Story;
	}

	// 協助將資料轉換的 Translater
	Translation tr = new Translation();

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

		public TaskBoard_Story(IIssue story) {
			Id = Long.toString(story.getIssueID());
			Name = HandleSpecialChar(story.getSummary());
			Value = story.getValue();
			Estimate = story.getEstimated();
			Importance = story.getImportance();
			Tag = tr.Join(story.getTag(), ",");
			Status = story.getStatus();
			Notes = HandleSpecialChar(story.getNotes());
			HowToDemo = HandleSpecialChar(story.getHowToDemo());
			Release = story.getReleaseID();
			Sprint = story.getSprintID();
			
			Link = story.getIssueLink();
			AttachFileList = getAttachFilePath(story, story.getAttachFile());
			
			if(!AttachFileList.isEmpty())
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

		public TaskBoard_Task(IIssue task) {
			Id = Long.toString(task.getIssueID());
			Name = HandleSpecialChar(task.getSummary());
			Estimate = task.getEstimated();
			RemainHours = task.getRemains();
			Actual = task.getActualHour();
			Handler = task.getAssignto();
			Partners = task.getPartners();
			Status = task.getStatus();
			Notes = HandleSpecialChar(task.getNotes());
			Link = task.getIssueLink();
			AttachFileList = getAttachFilePath(task, task.getAttachFile());
			if(!AttachFileList.isEmpty())
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
		
		public Date getUploadDate(){
			return UploadDate;
		}
	}
	
	// 將 attach file的資訊組成有效的連結
	private ArrayList<TaskBoard_AttachFile> getAttachFilePath(IIssue story, List<IssueAttachFile> list) {
		
		ArrayList<TaskBoard_AttachFile> array = new ArrayList<TaskBoard_AttachFile>();
		for (IssueAttachFile file : list) {
			array.add(new TaskBoard_AttachFile(file.getAttachFileId(), file.getFilename(), "fileDownload.do?projectName="
					+ story.getProjectName() + "&fileID=" + file.getAttachFileId() + "&fileName=" + file.getFilename()
					+ "&fileType=" + file.getFileType(), new Date(file.getDate_added())));
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
