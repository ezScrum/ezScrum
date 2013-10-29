package ntut.csie.ezScrum.web.support;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;

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
	String  id = "";
	String cls = "folder";
	
	// Task of Story is a leaf
	List<SprintBacklogTreeStructure> children = null;
	
	public SprintBacklogTreeStructure() {
		// initial empty function
	}
	
	public SprintBacklogTreeStructure(IIssue story, IIssue[] task, ArrayList<Date> dates) {
		this.Type = ScrumEnum.STORY_ISSUE_TYPE;
		this.ID = Long.toString(story.getIssueID());
		this.Name = HandleSpecialChar(story.getSummary());
		this.Handler = " ";
		this.Value = story.getValue();
		this.Estimate = story.getEstimated();
		this.Importance = story.getImportance();
		this.Tag = new Translation().Join(story.getTag(), ", ");
		this.Status = story.getStatus();
		this.Notes = HandleSpecialChar(story.getNotes());
		this.Link = story.getIssueLink();
		this.SprintID = story.getSprintID();
		this.ReleaseID = story.getReleaseID();
		
		this.leaf = false;
		//this.expanded = true;
		this.cls = "folder";
		this.id = Long.toString(story.getIssueID());
		
		this.children = new ArrayList<SprintBacklogTreeStructure>();

		// 設定 Sprint 時間
		this.dateList = dates;
		// 將 child 建構成樹狀
		TranslaeTaskSturct(task);
	}

	// 根據日期設定 Task 的 Remaining Hours
	private void SetRemainByDate(IIssue task){
		String value = "";
		// 使用時間來取得 Remaining Hours 然後用 DateColumns 來當作 Key
		for (int i = 0; this.dateList.size() > i; i++){
			if (task.getCategory().equals("Task") && 
					this.dateList.get(i).getTime() <= new Date().getTime()) // 超過今天日期的則不計算 remain
				value = getTaskPoint(this.dateList.get(i), task);
			else
				value = "";

			// 以日期當作 Key
			//this.dateToHourMap.put(this.currentCols.get(i).GetColumnName(), value);
			// 以 TreeGrid Column 當作 Key
			this.dateToHourMap.put("Date_" + String.valueOf(i+1), value);
		}
	}
	
	// 取得當天 Task 的時數
	private String getTaskPoint(Date date, IIssue task) {
		double point = 0;
		
		try {
			point = Double.parseDouble(task
					.getTagValue(ScrumEnum.REMAINS, date));
		} catch (Exception e) {
			try {
				// 表示這個Task沒有REMAINS，那麼就取得ESTIMATION八
				point = Double.parseDouble(task.getTagValue(
						ScrumEnum.ESTIMATION, date));
			} catch (Exception e1) {
				// 如果還是沒有，那就回傳 0
				return "0.0";
			}
		}
		return String.valueOf(point);
	}
	
	// 將 Task 也轉換成 SprintBacklogTreeStructure 結構
	private void TranslaeTaskSturct(IIssue[] tasks) {
		if ( (tasks != null) && (tasks.length > 0) ) {
			for (IIssue task : tasks) {
				SprintBacklogTreeStructure c = 
					new SprintBacklogTreeStructure(ScrumEnum.TASK_ISSUE_TYPE,		// type
												Long.toString(task.getIssueID()),	// ID 
												new Translation().Join(task.getTag(), ", "),	// tag
												task.getSummary(),					// bane
												task.getAssignto(),					// handler
												task.getValue(), 					// value
												task.getEstimated(),				// estimate
												task.getImportance(),				// importance
												task.getStatus(), 					// status
												task.getNotes(),					// note
												task.getIssueLink(),				// link
												task.getSprintID(),					// sprint ID
												task.getReleaseID(),				// release ID
												this.dateList,						// Sprint Date
												"file", true, true);		// ext attribute
				c.SetRemainByDate(task);
				this.children.add(c);
				
				c = null;		// release
			}
		} else {
			this.children = new ArrayList<SprintBacklogTreeStructure>();
		}
		//主要是判斷前端網頁樹狀結構中空story預設要設為展開(因為ext預設是不展開)
		if( this.children == null || this.children.size() == 0 ){
			this.expanded = true;
		}
	}
	
	// child 建構用
	private SprintBacklogTreeStructure(String type, String ID, String tag, String name, String handler, 
			             String value, String est, String imp, String status, String note, String link,
			             String sprintID, String releaseID, ArrayList<Date> dates,
			             String icon, boolean ep, boolean leaf) {
		this.Type = type;
		this.ID = ID;
		this.Tag = tag;
		this.Name = HandleSpecialChar(name);
		this.Handler = TranslateNull(handler);
		this.Value = TranslateNull(value);
		this.Estimate = TranslateNull(est);
		this.Importance = TranslateNull(imp);
		this.Status = status;
		this.Notes = HandleSpecialChar(note);
		this.Link = link;
		
		this.leaf = leaf;
		this.cls = icon;
		//this.expanded = ep;
		this.id = ID;
			
		this.children = null;
		this.dateList = dates;
	}
	
	private String HandleSpecialChar(String str) {
		if (str.contains("\n")) {
			str = str.replaceAll("\n", "<br/>");
		}
		
		return str;
	}
	
	private String TranslateNull(String str) {
		// null case
		if ( (str == null) || (str.length() == 0) || (str.isEmpty()) ) {
			return "";
		}
		
		// zero case
		if (str.equals("0")) {
			return "";
		}
	
		return str;
	}
	
	/*************** For Test ********************/ 
	public List<SprintBacklogTreeStructure> GetTasksTreeListForTest(){
		return this.children;
	}
	
	public HashMap<String, String> GetDatetoRemainMap(){
		return this.dateToHourMap;
	}
}
