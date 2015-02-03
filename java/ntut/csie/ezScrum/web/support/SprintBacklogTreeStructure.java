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
	private String mType = "";
	private String mIssueId = "";
	private String mTag = "";
	private String mName = "";
	private String mHandler = "";
	private String mValue = "";
	private String mEstimate = "";
	private String mImportance = "";
	private String mStatus = "";
	private String mNotes = "";
	private String mLink = "";
	private String mSprintID = "";
	private String mReleaseID = "";
	private ArrayList<Date> mDates = null;
	private HashMap<String, String> mDateToHourMap = new HashMap<String, String>();

	// Ext-JS attribute
	private boolean mLeaf = false;
	private boolean mExpanded = false;
	private String mId = "";
	private String mCls = "folder";

	// Task of Story is a leaf
	private List<SprintBacklogTreeStructure> mChildren = null;

	public SprintBacklogTreeStructure() {
		// initial empty function
	}

	public SprintBacklogTreeStructure(IIssue story,
			ArrayList<TaskObject> tasks, ArrayList<Date> dates) {
		mType = ScrumEnum.STORY_ISSUE_TYPE;
		mIssueId = Long.toString(story.getIssueID());
		mName = HandleSpecialChar(story.getSummary());
		mHandler = " ";
		mValue = story.getValue();
		mEstimate = story.getEstimated();
		mImportance = story.getImportance();
		mTag = new Translation().Join(story.getTags(), ", ");
		mStatus = story.getStatus();
		mNotes = HandleSpecialChar(story.getNotes());
		mLink = story.getIssueLink();
		mSprintID = story.getSprintID();
		mReleaseID = story.getReleaseID();
		mLeaf = false;
		// expanded = true;
		mCls = "folder";
		mId = Long.toString(story.getIssueID());
		mChildren = new ArrayList<SprintBacklogTreeStructure>();
		// 設定 Sprint 時間
		mDates = dates;
		// 將 child 建構成樹狀
		TranslaeTaskSturct(tasks);
	}

	public SprintBacklogTreeStructure(TaskObject task, ArrayList<Date> dates) {
		mType = ScrumEnum.TASK_ISSUE_TYPE;
		mIssueId = String.valueOf(task.getId());
		mName = task.getName();
		mHandler = task.getHandler().getUsername();
		mEstimate = String.valueOf(task.getEstimate());
		mStatus = task.getStatusString();
		mNotes = task.getNotes();
		mLeaf = true;
		mCls = "file";
		mDates = dates;
	}
	
	// 根據日期設定 Task 的 Remaining Hours
		private void SetRemainByDate(TaskObject task) {
			String value = "";
			// 使用時間來取得 Remaining Hours 然後用 DateColumns 來當作 Key
			for (int i = 0; mDates.size() > i; i++) {
				// 超過今天日期的則不計算
				if (mDates.get(i).getTime() <= new Date().getTime()) {
					// remain
					value = getTaskPoint(mDates.get(i), task);
				}
				else {
					value = "";
				}
				// 以日期當作 Key
				// dateToHourMap.put(currentCols.get(i).GetColumnName(),
				// value);
				// 以 TreeGrid Column 當作 Key
				mDateToHourMap.put("Date_" + String.valueOf(i + 1), value);
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
						task, mDates);

				taskStructure.SetRemainByDate(task);
				mChildren.add(taskStructure);
				taskStructure = null; // release
			}
		} else {
			mChildren = new ArrayList<SprintBacklogTreeStructure>();
		}
		// 主要是判斷前端網頁樹狀結構中空story預設要設為展開(因為ext預設是不展開)
		if (mChildren == null || mChildren.size() == 0) {
			mExpanded = true;
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
		return mChildren;
	}

	public HashMap<String, String> GetDatetoRemainMap() {
		return mDateToHourMap;
	}
}
