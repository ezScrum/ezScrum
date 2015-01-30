package ntut.csie.ezScrum.web.support;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
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

	public SprintBacklogTreeStructure(IIssue story,
			ArrayList<TaskObject> tasks, ArrayList<Date> dates) {
		this.Type = ScrumEnum.STORY_ISSUE_TYPE;
		this.ID = Long.toString(story.getIssueID());
		this.Name = HandleSpecialChar(story.getSummary());
		this.Handler = " ";
		this.Value = story.getValue();
		this.Estimate = story.getEstimated();
		this.Importance = story.getImportance();
		this.Tag = new Translation().Join(story.getTags(), ", ");
		this.Status = story.getStatus();
		this.Notes = HandleSpecialChar(story.getNotes());
		this.Link = story.getIssueLink();
		this.SprintID = story.getSprintID();
		this.ReleaseID = story.getReleaseID();

		this.leaf = false;
		// this.expanded = true;
		this.cls = "folder";
		this.id = Long.toString(story.getIssueID());

		this.children = new ArrayList<SprintBacklogTreeStructure>();

		// 設定 Sprint 時間
		this.dateList = dates;
		// 將 child 建構成樹狀
		TranslaeTaskSturct(tasks);
	}

	public SprintBacklogTreeStructure(TaskObject task, ArrayList<Date> dates) {
		this.Type = ScrumEnum.TASK_ISSUE_TYPE;
		this.ID = String.valueOf(task.getId());
		this.Name = task.getName();
		this.Handler = task.getHandler().getUsername();
		this.Estimate = String.valueOf(task.getEstimate());
		this.Status = task.getStatusString();
		this.Notes = task.getNotes();
		this.leaf = true;
		this.cls = "file";
		this.dateList = dates;
	}
	
	// 根據日期設定 Task 的 Remaining Hours
		private void SetRemainByDate(TaskObject task) {
			String value = "";
			// 使用時間來取得 Remaining Hours 然後用 DateColumns 來當作 Key
			for (int i = 0; this.dateList.size() > i; i++) {
				if (this.dateList.get(i).getTime() <= new Date().getTime()) // 超過今天日期的則不計算															// remain
					value = getTaskPoint(this.dateList.get(i), task);
				else
					value = "";

				// 以日期當作 Key
				// this.dateToHourMap.put(this.currentCols.get(i).GetColumnName(),
				// value);
				// 以 TreeGrid Column 當作 Key
				this.dateToHourMap.put("Date_" + String.valueOf(i + 1), value);
			}
		}

	// 取得當天 Task 的時數
	private String getTaskPoint(Date date, TaskObject task) {
		double point = 0;
		try {
			point = task.getRemains(date);
		} catch (Exception e) {
			return "0.0";
		}
		return String.valueOf(point);
	}

	// 將 Task 也轉換成 SprintBacklogTreeStructure 結構
	private void TranslaeTaskSturct(ArrayList<TaskObject> tasks) {
		if ((tasks != null) && (tasks.size() > 0)) {
			for (TaskObject task : tasks) {
				SprintBacklogTreeStructure taskStructure = new SprintBacklogTreeStructure(
						task, this.dateList);

				taskStructure.SetRemainByDate(task);
				this.children.add(taskStructure);

				taskStructure = null; // release
			}
		} else {
			this.children = new ArrayList<SprintBacklogTreeStructure>();
		}
		// 主要是判斷前端網頁樹狀結構中空story預設要設為展開(因為ext預設是不展開)
		if (this.children == null || this.children.size() == 0) {
			this.expanded = true;
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
		return this.children;
	}

	public HashMap<String, String> GetDatetoRemainMap() {
		return this.dateToHourMap;
	}
}
