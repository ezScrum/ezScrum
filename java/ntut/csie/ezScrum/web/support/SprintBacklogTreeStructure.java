package ntut.csie.ezScrum.web.support;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;

public class SprintBacklogTreeStructure {
	// Issue info
		String Type = "";
		String ID = "";
		String Tag = "";
		String Name = "";
		String Handler = "";
		String Value = "";
		String Estimate = "";
		String Importance = "";
		String Status = "";
		String Notes = "";
		String Link = "";
		String SprintID = "";
		String ReleaseID = "";
		
		private ArrayList<Date> dateList = null;
		private HashMap<String, String> dateToHourMap = new HashMap<String, String>();
		
		// Ext-JS attribute
		boolean leaf = false;
		boolean expanded = false;
		String id = "";
		String cls = "folder";
		
		// Task of Story is a leaf
		List<SprintBacklogTreeStructure> children = null;

	public SprintBacklogTreeStructure() {
		// initial empty function
	}

	public SprintBacklogTreeStructure(StoryObject story,
			ArrayList<TaskObject> tasks, ArrayList<Date> dates) {
		Type = ScrumEnum.STORY_ISSUE_TYPE;
		ID = Long.toString(story.getId());
		Name = HandleSpecialChar(story.getName());
		Handler = " ";
		Value = String.valueOf(story.getValue());
		Estimate = String.valueOf(story.getEstimate());
		Importance = String.valueOf(story.getImportance());
		Tag = Translation.Join(story.getTags(), ", ");
		Status = story.getStatusString();
		Notes = HandleSpecialChar(story.getNotes());
		Link = "";
		SprintID = String.valueOf(story.getSprintId());
		ReleaseID = "";
		
		leaf = false;
		expanded = false;
		cls = "folder";
		id = "Story:" + ID; // for ext tree panel to recognize node
		children = new ArrayList<SprintBacklogTreeStructure>();
		// 設定 Sprint 時間
		dateList = dates;
		// 將 child 建構成樹狀
		TranslateTaskSturct(tasks);
	}

	public SprintBacklogTreeStructure(TaskObject task, ArrayList<Date> dates) {
		Type = ScrumEnum.TASK_ISSUE_TYPE;
		ID = String.valueOf(task.getId());
		Name = task.getName();
		Handler = task.getHandler() != null ? task.getHandler().getUsername() : "";
		Estimate = String.valueOf(task.getEstimate());
		Status = task.getStatusString();
		Notes = task.getNotes();
		
		leaf = true;
		cls = "file";
		id = "Task:" + ID; // for ext tree panel to recognize node
		
		children = null;
		dateList = dates;
	}

	// 根據日期設定 Task 的 Remaining Hours
	private void SetRemainsByDate(TaskObject task) {
		String value = "";
		// 使用時間來取得 Remaining Hours 然後用 DateColumns 來當作 Key
		for (int i = 0; dateList.size() > i; i++) {
			// 超過今天日期的則不計算
			if (dateList.get(i).getTime() <= new Date().getTime()) {
				// remain
				value = getTaskRemains(dateList.get(i), task);
			} else {
				value = "";
			}
			// 以日期當作 Key
			// this.dateToHourMap.put(this.currentCols.get(i).GetColumnName(), value);
			// 以 TreeGrid Column 當作 Key
			dateToHourMap.put("Date_" + String.valueOf(i + 1), value);
		}
	}

	// 取得當天 Task 的時數
	private String getTaskRemains(Date date, TaskObject task) {
		double point = 0;
		try {
			point = task.getRemains(date);
		} catch (Exception e) {
			return "0.0";
		}
		return String.valueOf(point);
	}

	// 將 Task 也轉換成 SprintBacklogTreeStructure 結構
	private void TranslateTaskSturct(ArrayList<TaskObject> tasks) {
		if ((tasks != null) && (tasks.size() > 0)) {
			for (TaskObject task : tasks) {
				SprintBacklogTreeStructure taskStructure = new SprintBacklogTreeStructure(
						task, dateList);
				taskStructure.SetRemainsByDate(task);
				children.add(taskStructure);
				taskStructure = null; // release
			}
		} else {
			children = new ArrayList<SprintBacklogTreeStructure>();
		}
		// 主要是判斷前端網頁樹狀結構中空story預設要設為展開(因為 ext 預設是不展開)
		if (children == null || children.size() == 0) {
			expanded = true;
		}
	}

	private String HandleSpecialChar(String str) {
		if (str.contains("\n")) {
			str = str.replaceAll("\n", "<br/>");
		}
		return str;
	}

	/*************** For Test ********************/
	public List<SprintBacklogTreeStructure> GetTasksTreeListForTest() {
		return children;
	}

	public HashMap<String, String> GetDatetoRemainMap() {
		return dateToHourMap;
	}
}
