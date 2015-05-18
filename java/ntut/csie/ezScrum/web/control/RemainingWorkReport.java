package ntut.csie.ezScrum.web.control;

import java.awt.Color;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IITSService;
import ntut.csie.ezScrum.issue.sql.service.core.ITSServiceFactory;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.restful.mobile.support.ConvertRemainingWorkReport;
import ntut.csie.ezScrum.restful.mobile.support.IScrumReport;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.iternal.ISummaryEnum;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.jcis.core.util.ChartUtil;
import ntut.csie.jcis.resource.core.IProject;

public class RemainingWorkReport {
	final private String mNAME = ISummaryEnum.REMAININGWORK_SUMMARY_NAME;
	final private long mOneDay = 24 * 3600 * 1000;
	private ProjectObject mProject;
	private Date mChartStartDate = null;
	private Date mChartEndDate = null;
	private int mInterval = 1;
	private IITSService mIITS;
	private String mCategory;
	private Configuration mConfiguration;
	private final static String REMAININGWORK_CHART_FILE1 = "RemainingWork1.png";
	private final static String REMAININGWORK_CHART_FILE2 = "RemainingWork2.png";
	private String mChartPath = "";
	private int mAssignedQuantity;
	private int mTotalQuantity;
	private int mDoneQuantity;
	private int mNonAssignQuantity;
	private long mSprintId;
	private Map<Date, Integer> mNonAssignMap = new TreeMap<Date, Integer>();
	private Map<Date, Integer> mAssignedMap = new TreeMap<Date, Integer>();
	private Map<Date, Integer> mDoneMap = new TreeMap<Date, Integer>();
	private Date mToday = new Date();

	public RemainingWorkReport(ProjectObject project, IUserSession userSession, String category, long sprintid) {
		mSprintId = sprintid;
		mProject = project;
		mConfiguration = new Configuration(userSession);
		mCategory = category;
		// 如果category==task或story,就依sprint來取資料,若是其他則show出所有的資料
		if (mCategory.equals(ScrumEnum.TASK_ISSUE_TYPE)) {
			init(mSprintId);
			createTaskDataBySprint();
		} else if (mCategory.equals(ScrumEnum.STORY_ISSUE_TYPE)) {
			init(mSprintId);
			createStoryDataBySprint();
		} else {
			init();
			createIssueData();
		}
		drawGraph();
	}

	public RemainingWorkReport(ProjectObject project, IUserSession userSession, String category, long sprintid, Date setDate) {
		mSprintId = sprintid;
		mProject = project;
		mConfiguration = new Configuration(userSession);
		mCategory = category;
		mToday = setDate;
		// 如果category==task或story,就依sprint來取資料,若是其他則show出所有的資料
		if (mCategory.equals(ScrumEnum.TASK_ISSUE_TYPE)) {
			init(mSprintId);
			createTaskDataBySprint();
		} else if (mCategory.equals(ScrumEnum.STORY_ISSUE_TYPE)) {
			init(mSprintId);
			createStoryDataBySprint();
		} else {
			init();
			createIssueData();
		}
		drawGraph();
	}

	private void init() {
		mAssignedQuantity = 0;
		mTotalQuantity = 0;
		mDoneQuantity = 0;
		mNonAssignQuantity = 0;
		SprintPlanHelper spHelper = new SprintPlanHelper(mProject);
		// 設計sprint NO.
		// 設計起始時間
		mChartStartDate = spHelper.getProjectStartDate();
		// 設計結束時間
		mChartEndDate = spHelper.getProjectEndDate();
		nowExistReport();
	}

	private void init(long sprintId) {
		mAssignedQuantity = 0;
		mTotalQuantity = 0;
		mDoneQuantity = 0;
		mNonAssignQuantity = 0;
		// 設計sprint NO.
		SprintBacklogMapper sprintBacklogMapper = (new SprintBacklogLogic(mProject, sprintId)).getSprintBacklogMapper();
		// 設定起始時間
		mChartStartDate = sprintBacklogMapper.getSprintStartDate();
		// 設定結束時間
		mChartEndDate = sprintBacklogMapper.getSprintEndDate();
		nowExistReport();
	}

	private void createTaskDataBySprint() {
		SprintBacklogMapper sprintBacklogMapper = (new SprintBacklogLogic(mProject, mSprintId)).getSprintBacklogMapper();
		ArrayList<TaskObject> tasks = sprintBacklogMapper.getAllTasks();
		Date timeStamp = new Date(mChartStartDate.getTime());
		while (timeStamp.getTime() <= mChartEndDate.getTime()) {
			// timeNode為今天日期則要傳入現在的時間或使用者設定的時間
			if ((mToday.getDate() == timeStamp.getDate()) &&
			        (Math.abs(mToday.getTime() - timeStamp.getTime()) <= mOneDay)) {
				countTaskStatusChange(tasks, mToday);
				break;
			} else {
				countTaskStatusChange(tasks, timeStamp);
			}
			timeStamp = new Date(timeStamp.getTime() + mInterval * mOneDay);
		}
	}

	private void countTaskStatusChange(ArrayList<TaskObject> tasks, Date date) {
		int doneCount = 0, assignCount = 0, nonCount = 0;
		if (date.getTime() != mToday.getTime()) {
			// 當日期不為當天時，要做處理
			SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
			String dateFormat = format.format(date);
			try {
				date = format.parse(dateFormat);	// 去除分秒格式
			} catch (ParseException e) {
				e.printStackTrace();
			}
			date = new Date(date.getTime() + 24 * 3599999);		// 當天日期加上 23:59:59，這樣計算出來的報表才是當日所有
		}
		if (tasks != null) {
			for (TaskObject task : tasks) {
				switch (task.getStatus(date)) {
					case TaskObject.STATUS_UNCHECK:
						nonCount++;
						break;
					case TaskObject.STATUS_CHECK:
						assignCount++;
						break;
					case TaskObject.STATUS_DONE:
						doneCount++;
						break;
					default:
						break;
				}
			}
		}
		Date dateKey = new Date(date.getTime());
		mNonAssignMap.put(dateKey, nonCount + doneCount + assignCount);
		mDoneMap.put(dateKey, doneCount);
		mAssignedMap.put(dateKey, assignCount + doneCount);
		saveQuantity(nonCount, doneCount, assignCount);
	}
	
	private void createStoryDataBySprint() {
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(mProject, mSprintId);
		List<StoryObject> stories = sprintBacklogLogic.getStories();
		Date timeStamp = new Date(mChartStartDate.getTime());
		while (timeStamp.getTime() <= mChartEndDate.getTime()) {
			// timeNode為今天日期則要傳入現在的時間或使用者設定的時間
			if ((mToday.getDate() == timeStamp.getDate()) &&
			        (Math.abs(mToday.getTime() - timeStamp.getTime()) <= mOneDay)) {
				countStoryStatusChange(stories, mToday);
				break;
			} else {
				countStoryStatusChange(stories, timeStamp);
			}
			timeStamp = new Date(timeStamp.getTime() + mInterval * mOneDay);
		}
	}

	private void countStoryStatusChange(List<StoryObject> stories, Date date) {
		int doneCount = 0, assignCount = 0, nonCount = 0;
		if (date.getTime() != mToday.getTime()) {
			// 當日期不為當天時，要做處理
			SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
			String dateFormat = format.format(date);
			try {
				date = format.parse(dateFormat);	// 去除分秒格式
			} catch (ParseException e) {
				e.printStackTrace();
			}
			date = new Date(date.getTime() + 24 * 3599999);		// 當天日期加上 23:59:59，這樣計算出來的報表才是當日所有
		}
		boolean flag = false;
		for (StoryObject story : stories) {
			ArrayList<TaskObject> tasks = story.getTasks();
			// skip the story that without any task
			if (tasks == null) {
				nonCount++; // story count +1
				continue;
			}
			for (TaskObject task : tasks) {
				if (story.getStatus(date) == StoryObject.STATUS_DONE) {
					doneCount++;
					flag = true;
					break;
				} else if (task.getStatus(date) == TaskObject.STATUS_CHECK
				        || task.getStatus(date) == TaskObject.STATUS_DONE) {
					assignCount++;
					flag = true;
					break;
				}
			}
			if (!flag) nonCount++;
			flag = false;
		}
		Date dateKey = new Date(date.getTime());
		mNonAssignMap.put(dateKey, nonCount + doneCount + assignCount);
		mDoneMap.put(dateKey, doneCount);
		mAssignedMap.put(dateKey, assignCount + doneCount);
		saveQuantity(nonCount, doneCount, assignCount);
	}
	
	private void createIssueData() {
		mIITS = ITSServiceFactory.getInstance().getService(
		        ITSEnum.MANTIS_SERVICE_ID, mConfiguration);
		mIITS.openConnect();
		IIssue[] issues = mIITS.getIssues(mProject.getName());
		mIITS.closeConnect();
		List<IIssue> tempIssues = new ArrayList<IIssue>();
		for (IIssue issue : issues) {
			if (issue.getCategory().equals(mCategory)) {
				tempIssues.add(issue);
			}
		}
		Date timeStamp = new Date(mChartStartDate.getTime());
		while (timeStamp.getTime() <= mToday.getTime()) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
			String dateString = format.format(timeStamp);
			Date date;
			try {
				date = format.parse(dateString);
				countIssueStatusChange(tempIssues, date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			timeStamp = new Date(timeStamp.getTime() + mInterval * mOneDay);
		}
	}
	
	private void countIssueStatusChange(List<IIssue> issues, Date date) {
		int doneCount = 0, assignCount = 0, nonCount = 0;
		if (date.getTime() != mToday.getTime()) {
			// 當日期不為當天時，要做處理
			SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
			String dateFormat = format.format(date);
			try {
				date = format.parse(dateFormat);	// 去除分秒格式
			} catch (ParseException e) {
				e.printStackTrace();
			}
			date = new Date(date.getTime() + 24 * 3599999);		// 當天日期加上 23:59:59，這樣計算出來的報表才是當日所有
		}
		if (issues != null) {
			for (IIssue issue : issues) {
				switch (issue.getDateStatus(date)) {
					case ITSEnum.NEW_STATUS:
						nonCount++;
						break;
					case ITSEnum.ASSIGNED_STATUS:
						assignCount++;
						break;
					case ITSEnum.CLOSED_STATUS:
						doneCount++;
						break;
					default:
						break;
				}
			}
		}
		Date dateKey = new Date(date.getTime());
		mNonAssignMap.put(dateKey, nonCount + doneCount + assignCount);
		mDoneMap.put(dateKey, doneCount);
		mAssignedMap.put(dateKey, assignCount + doneCount);
		saveQuantity(nonCount, doneCount, assignCount);
	}

	private void saveQuantity(int nonAssign, int done, int Assigned) {
		mDoneQuantity = done;
		mAssignedQuantity = Assigned;
		mNonAssignQuantity = nonAssign;
		mTotalQuantity = done + Assigned + nonAssign;
	}

	private void drawGraph() {
		// 設定圖表內容
		ChartUtil chartUtil = new ChartUtil(mProject.getName()
		        + " Work Activity", mChartStartDate, mChartEndDate);
		chartUtil.setChartType(ChartUtil.AREALINECHART);
		chartUtil.addDataSet("Done", mDoneMap);
		chartUtil.addDataSet("Assigned", mAssignedMap);
		chartUtil.addDataSet("non-Assign", mNonAssignMap);
		chartUtil.setInterval(mInterval);
		chartUtil.setValueAxisLabel("Num. of Tasks ");
		Color[] colors = {Color.GREEN, Color.BLUE, Color.RED};
		chartUtil.setColor(colors);
		// 產生圖表
		File f = new File(getReportPath());
		chartUtil.createChart(getReportPath());
	}

	private void nowExistReport() {
		IProject mIProject = new ProjectMapper().getProjectByID(mProject.getName());
		String chartPath1 = mIProject.getFolder(IProject.METADATA).getFullPath()
		        + File.separator + mNAME + File.separator + "Report"
		        + File.separator + REMAININGWORK_CHART_FILE1;
		String chartPath2 = mIProject.getFolder(IProject.METADATA).getFullPath()
		        + File.separator + mNAME + File.separator + "Report"
		        + File.separator + REMAININGWORK_CHART_FILE2;
		File f1 = new File(chartPath1);
		File f2 = new File(chartPath2);
		if (f1.exists() && f2.exists()) {
			mChartPath = REMAININGWORK_CHART_FILE1;
		} else if (f1.exists()) {
			f1.delete();
			mChartPath = REMAININGWORK_CHART_FILE2;
		} else if (f2.exists()) {
			f2.delete();
			mChartPath = REMAININGWORK_CHART_FILE1;
		} else mChartPath = REMAININGWORK_CHART_FILE1;
	}

	private String getReportPath() {
		IProject mIProject = new ProjectMapper().getProjectByID(mProject.getName());
		// 圖片儲存的真正路徑
		// workspace/project/_metadata/RemainingWork/
		String chartPath = mIProject.getFolder(IProject.METADATA).getFullPath()
		        + File.separator + mNAME + File.separator + "Report"
		        + File.separator + mChartPath;
		return chartPath;
	}

	public String getRemainingWorkChartPath() {
		// web用的路徑
		String link = "./Workspace/" + mProject.getName() + "/"
		        + IProject.METADATA + "/" + mNAME + "/Report" + "/" + mChartPath;
		return link;
	}

	public IScrumReport getScrumReport() {
		ConvertRemainingWorkReport report = new ConvertRemainingWorkReport();
		report.setChartEndDate(mChartEndDate.getTime());
		report.setChartStartDate(mChartStartDate.getTime());
		report.setInterval(mInterval);
		report.setM_AssignedMap(mAssignedMap);
		report.setM_DoneMap(mDoneMap);
		report.setM_nonAssignMap(mNonAssignMap);
		report.setProjectName(mProject.getName());
		report.setAssigned(mAssignedQuantity + "");
		report.setCategory(mCategory);
		report.setDone(mDoneQuantity + "");
		report.setTotal(mTotalQuantity + "");
		report.setNonAssign(mNonAssignQuantity + "");
		report.setSprintID(mSprintId + "");
		return report;
	}

	public int getAssignedQuantity() {
		return mAssignedQuantity;
	}

	public int getTotalQuantity() {
		return mTotalQuantity;
	}

	public int getDoneQuantity() {
		return mDoneQuantity;
	}

	public int getNonAssignQuantity() {
		return mNonAssignQuantity;
	}
}
