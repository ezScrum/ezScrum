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
	final private String NAME = ISummaryEnum.REMAININGWORK_SUMMARY_NAME;
	final private long OneDay = 24 * 3600 * 1000;
	private IProject project;
	private Date m_chartStartDate = null;
	private Date m_chartEndDate = null;
	private int m_interval = 1;
	private IITSService IITS;
	private IUserSession session;
	private String category;
	private Configuration config;
	private final static String REMAININGWORK_CHART_FILE1 = "RemainingWork1.png";
	private final static String REMAININGWORK_CHART_FILE2 = "RemainingWork2.png";
	private String ChartPath = "";
	private int assignedQuantity;
	private int totalQuantity;
	private int doneQuantity;
	private int nonAssignQuantity;
	private int sprintID;

	private Map<Date, Integer> m_nonAssignMap = new TreeMap<Date, Integer>();
	private Map<Date, Integer> m_AssignedMap = new TreeMap<Date, Integer>();
	private Map<Date, Integer> m_DoneMap = new TreeMap<Date, Integer>();

	private Date Today = new Date();

	public RemainingWorkReport(IProject project, IUserSession userSession, String category, int sprintid) {
		this.sprintID = sprintid;
		this.project = project;
		this.session = userSession;
		this.config = new Configuration(userSession);
		this.category = category;

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
		this.sprintID = sprintid;
		this.project = project;
		this.session = userSession;
		this.config = new Configuration(userSession);
		this.category = category;

		this.Today = setDate;

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
		assignedQuantity = 0;
		totalQuantity = 0;
		doneQuantity = 0;
		nonAssignQuantity = 0;
		SprintPlanHelper spHelper = new SprintPlanHelper(project);
		// 設計sprint NO.
		// 設計起始時間
		this.m_chartStartDate = spHelper.getProjectStartDate();
		// 設計結束時間
		this.m_chartEndDate = spHelper.getProjectEndDate();
		nowExistReport();
	}

	private void init(int sprintid) {
		assignedQuantity = 0;
		totalQuantity = 0;
		doneQuantity = 0;
		nonAssignQuantity = 0;
		// 設計sprint NO.
		SprintBacklogMapper sprintBacklogMapper = (new SprintBacklogLogic(project, session, String.valueOf(sprintid))).getSprintBacklogMapper();
		// 設定起始時間
		this.m_chartStartDate = sprintBacklogMapper.getSprintStartDate();
		// 設定結束時間
		this.m_chartEndDate = sprintBacklogMapper.getSprintEndDate();
		nowExistReport();

	}

	private void createStoryDataBySprint(int sprintid) {
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, session, String.valueOf(sprintID));
		SprintBacklogMapper backlog = sprintBacklogLogic.getSprintBacklogMapper();
		List<IIssue> stories = sprintBacklogLogic.getStories();
		Map<Long, IIssue[]> TaskMap = backlog.getTasksMap();
		Date timeNode = new Date(this.m_chartStartDate.getTime());
		while (timeNode.getTime() <= this.m_chartEndDate.getTime()) {
			// timeNode為今天日期則要傳入現在的時間或使用者設定的時間
			if ((this.Today.getDate() == timeNode.getDate()) &&
			        (Math.abs(this.Today.getTime() - timeNode.getTime()) <= OneDay)) {
				this.countStroyStatusChange(stories, TaskMap, this.Today);
				break;
			} else {
				this.countStroyStatusChange(stories, TaskMap, timeNode);
			}
			timeNode = new Date(timeNode.getTime() + this.m_interval * OneDay);
		}
	}

	private void countStroyStatusChange(List<IIssue> stories, Map<Long, IIssue[]> taskMap, Date date) {
		int Donecount = 0, AssignCount = 0, NonCount = 0;

		Date dateKey = new Date(date.getTime());

		if (date.getTime() != this.Today.getTime()) {
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
		this.m_nonAssignMap.put(dateKey, NonCount + Donecount + AssignCount);
		this.m_DoneMap.put(dateKey, Donecount);
		this.m_AssignedMap.put(dateKey, AssignCount + Donecount);
		saveQuantity(NonCount, Donecount, AssignCount);

	}

	private void createTaskDataBySprint(int sprintid) {
		SprintBacklogMapper backlog = (new SprintBacklogLogic(project, session, String.valueOf(sprintid))).getSprintBacklogMapper();
		ArrayList<TaskObject> issues = backlog.getAllTasks();
		Date timeNode = new Date(this.m_chartStartDate.getTime());
		while (timeNode.getTime() <= this.m_chartEndDate.getTime()) {
			// timeNode為今天日期則要傳入現在的時間或使用者設定的時間
			if ((this.Today.getDate() == timeNode.getDate()) &&
			        (Math.abs(this.Today.getTime() - timeNode.getTime()) <= OneDay)) {
				this.countStatusChange(issues, this.Today);
				break;
			} else {
				this.countStatusChange(issues, timeNode);
			}
			timeNode = new Date(timeNode.getTime() + this.m_interval * OneDay);
		}
	}

	private void countStatusChange(List<IIssue> issues, Date date) {
		int Donecount = 0, AssignCount = 0, NonCount = 0;

		Date dateKey = new Date(date.getTime());

		if (date.getTime() != this.Today.getTime()) {
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

		this.m_nonAssignMap.put(dateKey, NonCount + Donecount + AssignCount);
		this.m_DoneMap.put(dateKey, Donecount);
		this.m_AssignedMap.put(dateKey, AssignCount + Donecount);
		saveQuantity(NonCount, Donecount, AssignCount);
	}

	private void createData() {
		IITS = ITSServiceFactory.getInstance().getService(
		        ITSEnum.MANTIS_SERVICE_ID, config);
		IITS.openConnect();
		IIssue[] issues = IITS.getIssues(project.getName());
		IITS.closeConnect();
		List<IIssue> temp = new ArrayList<IIssue>();
		for (IIssue issue : issues) {
			if (issue.getCategory().compareTo(category) == 0) temp.add(issue);
		}
		Date timeNode = new Date(this.m_chartStartDate.getTime());
		while (timeNode.getTime() <= this.Today.getTime()) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
			String dateStr = format.format(timeNode);
			Date dateKey;
			try {

				dateKey = format.parse(dateStr);
				this.countStatusChange(temp, dateKey);

			} catch (ParseException e) {
				e.printStackTrace();
			}
			timeNode = new Date(timeNode.getTime() + this.m_interval * OneDay);
		}
	}

	private void saveQuantity(int nonAssign, int done, int Assigned) {
		doneQuantity = done;
		assignedQuantity = Assigned;
		nonAssignQuantity = nonAssign;
		totalQuantity = done + Assigned + nonAssign;
	}

	private void drawGraph() {
		// 閮剖��”�批捆
		ChartUtil chartUtil = new ChartUtil(project.getName()
		        + " Work Activity", this.m_chartStartDate, this.m_chartEndDate);

		chartUtil.setChartType(ChartUtil.AREALINECHART);

		chartUtil.addDataSet("Done", this.m_DoneMap);
		chartUtil.addDataSet("Assigned", this.m_AssignedMap);
		chartUtil.addDataSet("non-Assign", this.m_nonAssignMap);
		chartUtil.setInterval(this.m_interval);
		chartUtil.setValueAxisLabel("Num. of Tasks ");

		Color[] colors = {Color.GREEN, Color.BLUE, Color.RED};

		chartUtil.setColor(colors);

		// �Ｙ��”
		File f = new File(getReportPath());
		chartUtil.createChart(getReportPath());

	}

	private void nowExistReport() {
		String chartPath1 = project.getFolder(IProject.METADATA).getFullPath()
		        + File.separator + this.NAME + File.separator + "Report"
		        + File.separator + REMAININGWORK_CHART_FILE1;
		String chartPath2 = project.getFolder(IProject.METADATA).getFullPath()
		        + File.separator + this.NAME + File.separator + "Report"
		        + File.separator + REMAININGWORK_CHART_FILE2;
		File f1 = new File(chartPath1);
		File f2 = new File(chartPath2);
		if (f1.exists() && f2.exists()) {
			ChartPath = REMAININGWORK_CHART_FILE1;
		} else if (f1.exists()) {
			f1.delete();
			ChartPath = REMAININGWORK_CHART_FILE2;
		} else if (f2.exists()) {
			f2.delete();
			ChartPath = REMAININGWORK_CHART_FILE1;
		} else ChartPath = REMAININGWORK_CHART_FILE1;
	}

	private String getReportPath() {
		// 圖片儲存的真正路徑
		// workspace/project/_metadata/RemainingWork/

		String chartPath = project.getFolder(IProject.METADATA).getFullPath()
		        + File.separator + this.NAME + File.separator + "Report"
		        + File.separator + ChartPath;

		return chartPath;
	}

	public String getRemainingWorkChartPath() {
		// web用的路徑
		String link = "./Workspace/" + project.getName() + "/"
		        + IProject.METADATA + "/" + NAME + "/Report" + "/" + ChartPath;

		return link;
	}

	public IScrumReport getScrumReport() {
		ConvertRemainingWorkReport report = new ConvertRemainingWorkReport();
		report.setChartEndDate(this.m_chartEndDate.getTime());
		report.setChartStartDate(this.m_chartStartDate.getTime());
		report.setInterval(this.m_interval);
		report.setM_AssignedMap(this.m_AssignedMap);
		report.setM_DoneMap(this.m_DoneMap);
		report.setM_nonAssignMap(this.m_nonAssignMap);
		report.setProjectName(this.project.getName());
		report.setAssigned(assignedQuantity + "");
		report.setCategory(category);
		report.setDone(doneQuantity + "");
		report.setTotal(totalQuantity + "");
		report.setNonAssign(nonAssignQuantity + "");
		report.setSprintID(sprintID + "");
		return report;
	}

	public int getAssignedQuantity() {
		return assignedQuantity;
	}

	public int getTotalQuantity() {
		return totalQuantity;
	}

	public int getDoneQuantity() {
		return doneQuantity;
	}

	public int getNonAssignQuantity() {
		return nonAssignQuantity;
	}
}
