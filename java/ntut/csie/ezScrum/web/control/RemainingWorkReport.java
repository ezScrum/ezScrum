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
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.iternal.ISummaryEnum;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.jcis.core.util.ChartUtil;
import ntut.csie.jcis.resource.core.IProject;

public class RemainingWorkReport {
	final private String mNAME = ISummaryEnum.REMAININGWORK_SUMMARY_NAME;
	final private long mOneDay = 24 * 3600 * 1000;
	private IProject mProject;
	private Date mChartStartDate = null;
	private Date mChartEndDate = null;
	private int mInterval = 1;
	private IITSService mIITS;
	private IUserSession mUserSession;
	private String mCategory;
	private Configuration mConfiguration;
	private final static String REMAININGWORK_CHART_FILE1 = "RemainingWork1.png";
	private final static String REMAININGWORK_CHART_FILE2 = "RemainingWork2.png";
	private String mChartPath = "";
	private int mAssignedQuantity;
	private int mTotalQuantity;
	private int mDoneQuantity;
	private int mNonAssignQuantity;
	private int mSprintId;
	private Map<Date, Integer> mNonAssignMap = new TreeMap<Date, Integer>();
	private Map<Date, Integer> mAssignedMap = new TreeMap<Date, Integer>();
	private Map<Date, Integer> mDoneMap = new TreeMap<Date, Integer>();
	private Date mToday = new Date();

	public RemainingWorkReport(IProject project, IUserSession userSession, String category, int sprintid) {
		mSprintId = sprintid;
		mProject = project;
		mUserSession = userSession;
		mConfiguration = new Configuration(userSession);
		mCategory = category;
		// 如果category==task或story,就依sprint來取資料,若是其他則show出所有的資料
		if (category.compareTo(ScrumEnum.TASK_ISSUE_TYPE) == 0) {
			init(sprintid);
			createTaskDataBySprint(sprintid);
		} else if (category.compareTo(ScrumEnum.STORY_ISSUE_TYPE) == 0) {
			init(sprintid);
			createStoryDataBySprint(sprintid);
		} else {
			init();
			createData();
		}
		drawGraph();
	}

	public RemainingWorkReport(IProject project, IUserSession userSession, String category, int sprintid, Date setDate) {
		mSprintId = sprintid;
		mProject = project;
		mUserSession = userSession;
		mConfiguration = new Configuration(userSession);
		mCategory = category;
		mToday = setDate;
		// 如果category==task或story,就依sprint來取資料,若是其他則show出所有的資料
		if (category.compareTo(ScrumEnum.TASK_ISSUE_TYPE) == 0) {
			init(sprintid);
			createTaskDataBySprint(sprintid);
		} else if (category.compareTo(ScrumEnum.STORY_ISSUE_TYPE) == 0) {
			init(sprintid);
			createStoryDataBySprint(sprintid);
		} else {
			init();
			createData();
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

	private void init(int sprintid) {
		mAssignedQuantity = 0;
		mTotalQuantity = 0;
		mDoneQuantity = 0;
		mNonAssignQuantity = 0;
		// 設計sprint NO.
		SprintBacklogMapper sprintBacklogMapper = (new SprintBacklogLogic(mProject, mUserSession, String.valueOf(sprintid))).getSprintBacklogMapper();
		// 設定起始時間
		mChartStartDate = sprintBacklogMapper.getSprintStartDate();
		// 設定結束時間
		mChartEndDate = sprintBacklogMapper.getSprintEndDate();
		nowExistReport();
	}

	private void createStoryDataBySprint(int sprintid) {
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(mProject, mUserSession, String.valueOf(mSprintId));
		SprintBacklogMapper backlog = sprintBacklogLogic.getSprintBacklogMapper();
		List<IIssue> stories = sprintBacklogLogic.getStories();
		Map<Long, IIssue[]> TaskMap = backlog.getTasksMap();
		Date timeNode = new Date(mChartStartDate.getTime());
		while (timeNode.getTime() <= mChartEndDate.getTime()) {
			// timeNode為今天日期則要傳入現在的時間或使用者設定的時間
			if ((mToday.getDate() == timeNode.getDate()) &&
			        (Math.abs(mToday.getTime() - timeNode.getTime()) <= mOneDay)) {
				countStroyStatusChange(stories, TaskMap, mToday);
				break;
			} else {
				countStroyStatusChange(stories, TaskMap, timeNode);
			}
			timeNode = new Date(timeNode.getTime() + mInterval * mOneDay);
		}
	}

	private void countStroyStatusChange(List<IIssue> stories, Map<Long, IIssue[]> taskMap, Date date) {
		int Donecount = 0, AssignCount = 0, NonCount = 0;
		Date dateKey = new Date(date.getTime());
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
		for (IIssue story : stories) {
			IIssue[] tasks = taskMap.get(story.getIssueID());
			// skip the story that without any task
			if (tasks == null) {
				NonCount++; // story count +1
				continue;
			}
			for (IIssue task : tasks) {
				if (story.getDateStatus(date) == ITSEnum.CLOSED_STATUS) {
					Donecount++;
					flag = true;
					break;
				} else if (task.getDateStatus(date) == ITSEnum.ASSIGNED_STATUS
				        || task.getDateStatus(date) == ITSEnum.CLOSED_STATUS) {
					AssignCount++;
					flag = true;
					break;
				}
			}
			if (!flag) NonCount++;
			flag = false;
		}
		mNonAssignMap.put(dateKey, NonCount + Donecount + AssignCount);
		mDoneMap.put(dateKey, Donecount);
		mAssignedMap.put(dateKey, AssignCount + Donecount);
		saveQuantity(NonCount, Donecount, AssignCount);

	}

	private void createTaskDataBySprint(int sprintid) {
		SprintBacklogMapper backlog = (new SprintBacklogLogic(mProject, mUserSession, String.valueOf(sprintid))).getSprintBacklogMapper();
		ArrayList<TaskObject> issues = backlog.getAllTasks();
		Date timeNode = new Date(mChartStartDate.getTime());
		while (timeNode.getTime() <= mChartEndDate.getTime()) {
			// timeNode為今天日期則要傳入現在的時間或使用者設定的時間
			if ((mToday.getDate() == timeNode.getDate()) &&
			        (Math.abs(mToday.getTime() - timeNode.getTime()) <= mOneDay)) {
				countStatusChange(issues, mToday);
				break;
			} else {
				countStatusChange(issues, timeNode);
			}
			timeNode = new Date(timeNode.getTime() + mInterval * mOneDay);
		}
	}

	private void countStatusChange(List<IIssue> issues, Date date) {
		int Donecount = 0, AssignCount = 0, NonCount = 0;
		Date dateKey = new Date(date.getTime());
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
						NonCount++;
						break;
					case ITSEnum.ASSIGNED_STATUS:
						AssignCount++;
						break;
					case ITSEnum.CLOSED_STATUS:
						Donecount++;
						break;
					default:
						break;
				}
			}
		}
		mNonAssignMap.put(dateKey, NonCount + Donecount + AssignCount);
		mDoneMap.put(dateKey, Donecount);
		mAssignedMap.put(dateKey, AssignCount + Donecount);
		saveQuantity(NonCount, Donecount, AssignCount);
	}

	private void createData() {
		mIITS = ITSServiceFactory.getInstance().getService(
		        ITSEnum.MANTIS_SERVICE_ID, mConfiguration);
		mIITS.openConnect();
		IIssue[] issues = mIITS.getIssues(mProject.getName());
		mIITS.closeConnect();
		List<IIssue> temp = new ArrayList<IIssue>();
		for (IIssue issue : issues) {
			if (issue.getCategory().compareTo(mCategory) == 0) temp.add(issue);
		}
		Date timeNode = new Date(mChartStartDate.getTime());
		while (timeNode.getTime() <= mToday.getTime()) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
			String dateStr = format.format(timeNode);
			Date dateKey;
			try {

				dateKey = format.parse(dateStr);
				countStatusChange(temp, dateKey);

			} catch (ParseException e) {
				e.printStackTrace();
			}
			timeNode = new Date(timeNode.getTime() + mInterval * mOneDay);
		}
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
		String chartPath1 = mProject.getFolder(IProject.METADATA).getFullPath()
		        + File.separator + mNAME + File.separator + "Report"
		        + File.separator + REMAININGWORK_CHART_FILE1;
		String chartPath2 = mProject.getFolder(IProject.METADATA).getFullPath()
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
		// 圖片儲存的真正路徑
		// workspace/project/_metadata/RemainingWork/
		String chartPath = mProject.getFolder(IProject.METADATA).getFullPath()
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
