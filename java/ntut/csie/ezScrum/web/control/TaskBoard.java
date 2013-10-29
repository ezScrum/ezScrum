package ntut.csie.ezScrum.web.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.jcis.core.util.ChartUtil;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

public class TaskBoard {
	private final String STORY_CHART_FILE = "StoryBurnDown.png";
	private final String TASK_CHART_FILE = "TaskBurnDown.png";
	private final String NAME = "TaskBoard";

	private SprintBacklogMapper sprintBacklogMapper;
	private List<IIssue> m_stories;
	private Map<Long, IIssue[]> m_taskMap;
	private IIssue[] m_dropedStories;
	private Map<Long, IIssue[]> m_dropedTaskMap;
	private LinkedHashMap<Date, Double> m_storyIdealPointMap;
	private LinkedHashMap<Date, Double> m_storyRealPointMap;
	private LinkedHashMap<Date, Double> m_taskIdealPointMap;
	private LinkedHashMap<Date, Double> m_taskRealPointMap;
	private Date m_currentDate = new Date();
	private Date m_generatedTime = new Date();
	private String iteration = "0";
	final private long OneDay = ScrumEnum.DAY_MILLISECOND;

	private SprintBacklogLogic sprintBacklogLogic;

	public TaskBoard(SprintBacklogLogic sprintBacklogLogic, SprintBacklogMapper sprintBacklogMapper) {
		this.sprintBacklogLogic = sprintBacklogLogic;
		this.sprintBacklogMapper = sprintBacklogMapper;
		this.iteration = Integer.toString(sprintBacklogMapper.getSprintPlanId());
		init();
	}

	// ======================= for unit test ===========================
	public LinkedHashMap<Date, Double> getstoryRealPointMap() {
		return this.m_storyRealPointMap;
	}

	public LinkedHashMap<Date, Double> gettaskRealPointMap() {
		return this.m_taskRealPointMap;
	}

	public LinkedHashMap<Date, Double> getstoryIdealPointMap() {
		return this.m_storyIdealPointMap;
	}

	public LinkedHashMap<Date, Double> gettaskIdealPointMap() {
		return this.m_taskIdealPointMap;
	}

	// ======================= for unit test ===========================

	private void init() {
		// 取得目前最新的Story與Task狀態
		m_stories = sprintBacklogLogic.getStoriesByImp();
		m_taskMap = sprintBacklogMapper.getTasksMap();
		// 取得從經被drop掉的Story與其底下的Task
		m_dropedStories = sprintBacklogMapper.getDropedStory();
		m_dropedTaskMap = sprintBacklogMapper.getDropedTaskMap();

		if (sprintBacklogMapper != null) {
			// Sprint的起始與結束日期資訊
			Date iter_Start_Work_Date = this.sprintBacklogLogic.getSprintStartWorkDate();
			Date iter_End_Work_Date = this.sprintBacklogLogic.getSprintEndWorkDate();
			Date iter_End_Date = sprintBacklogMapper.getSprintEndDate();

			Calendar indexDate = Calendar.getInstance();
			indexDate.setTime(iter_Start_Work_Date);
			long endTime = iter_End_Work_Date.getTime();

			m_storyIdealPointMap = new LinkedHashMap<Date, Double>();	// Story的理想線
			m_taskIdealPointMap = new LinkedHashMap<Date, Double>();	// Task的理想線
			m_storyRealPointMap = new LinkedHashMap<Date, Double>();	// Story的真實線
			m_taskRealPointMap = new LinkedHashMap<Date, Double>();		// Task的真實線
			double[] initPoint = getPointByDate(iter_Start_Work_Date);	// 第一天Story與Task的點數
			int dayOfSprint = this.sprintBacklogLogic.getSprintWorkDays();	// 扣除假日後，Sprint的總天數
			int num = 0;	// Sprint中第幾天的Counter
			long today = m_currentDate.getTime();	// 今天的日期，如果今天已經在EndDate之後，那就設為EndDate

			if (m_currentDate.getTime() > iter_End_Date.getTime()) {
				// enddate為當日的 00:00:00 ，所以還要加入OneDay，這樣時間才會是 00:00:00 - 23:59:59
				today = iter_End_Date.getTime() + OneDay;
			}
			// 每一天的理想與真實點數
			while (!(indexDate.getTimeInMillis() > endTime) || indexDate.getTimeInMillis() == endTime) {
				Date key = indexDate.getTime();
				// 扣除假日
				if (!DateUtil.isHoliday(key)) {
					// 記錄Story與Task理想線的點數
					// 理想線直線方程式 y = - (起始點數 / 總天數) * 第幾天 + 起始點數
					m_storyIdealPointMap.put(key, (((-initPoint[0]) / dayOfSprint) * num) + initPoint[0]);
					m_taskIdealPointMap.put(key, (((-initPoint[1]) / dayOfSprint) * num) + initPoint[1]);

					// 記錄Story與Task實際線的點數
					// 只取出今天以前的資料
					if (indexDate.getTimeInMillis() < today) {
						double point[] = getPointByDate(key);
						m_storyRealPointMap.put(key, point[0]);
						m_taskRealPointMap.put(key, point[1]);
					} else {
						m_storyRealPointMap.put(key, null);
						m_taskRealPointMap.put(key, null);
					}
					num++;
				}
				indexDate.add(Calendar.DATE, 1);
			}
		}
	}

	private double getStoryPoint(Date date, IIssue issue) throws Exception {
		double point = 0;
		// 確認這個Story在那個時間是否存在
		String issue_iter = issue.getTagValue("Iteration", date);
		if (issue_iter != null && issue_iter.equals(iteration)) {
			try {
				point = Double.parseDouble(issue.getTagValue(
				        ScrumEnum.ESTIMATION, date));
			} catch (Exception e) {
				// 表示這個Story沒有設定Estimation
				return 0;
			}
		} else {
			// 表示這個Story在當時不存在於這個Sprint裡面
			throw new Exception("this story isn't at this sprint");
		}
		return point;
	}

	private double getTaskPoint(Date date, IIssue task) {
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
				return 0;
			}
		}
		return point;
	}

	private double[] getPointByDate(Date date) {
		double[] point = {0, 0};
		IIssue[] items;

		/************************************************************
		 * 依照Type取出當天的Story或者是Task來進行計算
		 *************************************************************/
		// 因為輸入的日期為當日的0:0:0,但在23:59:59之前也算當日，所以必需多加一日做為當天的計算
		Date dueDate = new Date(date.getTime() + OneDay);

		// 尋訪現有的所有Story
		for (IIssue issue : m_stories) {
			double estimation;

			// 已經closed的Story就不用算他的點數啦，連Task都省掉了
			if (issue.getStatusUpdated(dueDate, ITSEnum.CLOSED_STATUS) != null) {
				continue;
			}

			try {
				/***************
				 * 計算Story點數
				 ***************/
				point[0] += getStoryPoint(dueDate, issue);

				/***************
				 * 計算Task點數
				 ***************/
				// 取得這個Story底下的Task點數
				IIssue[] tmpTasks = m_taskMap.get(issue.getIssueID());
				for (IIssue task : tmpTasks) {
					// 已經closed的task就不用算他的點數啦
					if (task.getStatusUpdated(dueDate, ITSEnum.CLOSED_STATUS) != null) continue;
					point[1] += getTaskPoint(dueDate, task);
				}
			} catch (Exception e) {
				// 如果會有Exception表示此時間Story不在此Sprint中，所以getTagValue回傳null乘parseDouble產生exception
				continue;
			}

		}

		// 尋訪現有的所有Droped Story
		for (IIssue issue : m_dropedStories) {
			double estimation;

			// 已經closed的Story就不用算他的點數啦，連Task都省掉了
			if (issue.getStatusUpdated(dueDate, ITSEnum.CLOSED_STATUS) != null) continue;

			try {
				/***************
				 * 計算Story點數
				 ***************/
				point[0] += getStoryPoint(dueDate, issue);

				/***************
				 * 計算Task點數
				 ***************/
				// 取得這個Story底下的Task點數
				IIssue[] tmpTasks = m_dropedTaskMap.get(issue.getIssueID());
				for (IIssue task : tmpTasks) {
					// 已經closed的task就不用算他的點數啦
					if (task.getStatusUpdated(dueDate, ITSEnum.CLOSED_STATUS) != null) continue;
					point[1] += getTaskPoint(dueDate, task);
				}
			} catch (Exception e) {
				// 如果會有Exception表示此時間Story不在此Sprint中，所以getTagValue回傳null乘parseDouble產生exception
				continue;
			}

		}

		return point;
	}

	public String getSprintGoal() {
		return sprintBacklogMapper.getSprintGoal();
	}

	public int getSprintID() {
		return sprintBacklogMapper.getSprintPlanId();
	}

	public String getStoryPoint() {
		return this.sprintBacklogLogic.getCurrentUnclosePoint(ScrumEnum.STORY_ISSUE_TYPE)
		        + " / "
		        + this.sprintBacklogLogic.getCurrentPoint(ScrumEnum.STORY_ISSUE_TYPE);
		// return this.sprintBacklogMapper
		// .getCurrentUnclosePoint(ScrumEnum.STORY_ISSUE_TYPE)
		// + " / "
		// + this.sprintBacklogMapper.getCurrentPoint(ScrumEnum.STORY_ISSUE_TYPE);
	}

	public String getTaskPoint() {
		return this.sprintBacklogLogic.getCurrentUnclosePoint(ScrumEnum.TASK_ISSUE_TYPE)
		        + " / "
		        + this.sprintBacklogLogic.getCurrentPoint(ScrumEnum.TASK_ISSUE_TYPE);
		// return this.sprintBacklogMapper.getCurrentUnclosePoint(ScrumEnum.TASK_ISSUE_TYPE)
		// + " / "
		// + this.sprintBacklogMapper.getCurrentPoint(ScrumEnum.TASK_ISSUE_TYPE);
	}

	public String getInitialStoryPoint() {
		return (getPointByDate(sprintBacklogMapper.getSprintStartDate())[0]) + " / "
		        + this.sprintBacklogMapper.getLimitedPoint();
	}

	public String getInitialTaskPoint() {
		return (getPointByDate(sprintBacklogMapper.getSprintStartDate())[1]) + " / -";
	}

	public List<IIssue> getStories() {
		return m_stories;
	}

	public Map<Long, IIssue[]> getTaskMap() {
		return m_taskMap;
	}

	public String getStoryChartLink() {
		IProject project = sprintBacklogMapper.getProject();
		// workspace/project/_metadata/TaskBoard/ChartLink
		String chartPath = project.getFolder(IProject.METADATA).getFullPath()
		        + File.separator + this.NAME + File.separator + "Sprint"
		        + this.getSprintID() + File.separator + STORY_CHART_FILE;

		// 繪圖
		drawGraph(ScrumEnum.STORY_ISSUE_TYPE, chartPath, "Story Points");

		String link = "./Workspace/" + project.getName() + "/"
		        + IProject.METADATA + "/" + NAME + "/Sprint"
		        + this.getSprintID() + "/" + STORY_CHART_FILE;

		return link;
	}

	public String getTaskChartLink() {
		IProject project = sprintBacklogMapper.getProject();
		// workspace/project/_metadata/TaskBoard/Sprint1/ChartLink
		String chartPath = project.getFolder(IProject.METADATA).getFullPath()
		        + File.separator + this.NAME + File.separator + "Sprint"
		        + this.getSprintID() + File.separator + TASK_CHART_FILE;

		// 繪圖
		drawGraph(ScrumEnum.TASK_ISSUE_TYPE, chartPath, "Remaining Hours");

		String link = "./Workspace/" + project.getName() + "/"
		        + IProject.METADATA + "/" + NAME + "/Sprint"
		        + this.getSprintID() + "/" + TASK_CHART_FILE;

		return link;
	}

	private synchronized void drawGraph(String type, String chartPath, String Y_axis_value) {
		// 設定圖表內容
		ChartUtil chartUtil = new ChartUtil((type
		        .equals(ScrumEnum.TASK_ISSUE_TYPE) ? "Tasks" : "Stories")
		        + " Burndown Chart in Sprint #" + sprintBacklogMapper.getSprintPlanId(),
		        this.sprintBacklogMapper.getSprintStartDate(), new Date(this.sprintBacklogMapper
		                .getSprintEndDate().getTime() + 24 * 3600 * 1000));

		chartUtil.setChartType(ChartUtil.LINECHART);

		// TODO:要新增的data set
		if (type.equals(ScrumEnum.TASK_ISSUE_TYPE)) {
			chartUtil.addDataSet("current", m_taskRealPointMap);
			chartUtil.addDataSet("ideal", m_taskIdealPointMap);
		} else {
			chartUtil.addDataSet("current", m_storyRealPointMap);
			chartUtil.addDataSet("ideal", m_storyIdealPointMap);
		}
		chartUtil.setInterval(1);
		chartUtil.setValueAxisLabel(Y_axis_value);
		// 依照輸入的順序來呈現顏色
		Color[] colors = {Color.RED, Color.GRAY};
		chartUtil.setColor(colors);

		float[] dashes = {8f};
		BasicStroke[] strokes = {
		        new BasicStroke(1.5f),
		        new BasicStroke(1.5f, BasicStroke.CAP_ROUND,
		                BasicStroke.JOIN_ROUND, 16f, dashes, 0.f)};
		chartUtil.setStrokes(strokes);

		// 產生圖表
		chartUtil.createChart(chartPath);
	}

	public IIssue getItem(long id) {

		List<IIssue> items = this.getStories();
		for (IIssue item : items) {
			if (item.getIssueID() == id) return item;
		}

		items = sprintBacklogMapper.getTasks();
		for (IIssue item : items) {
			if (item.getIssueID() == id) return item;
		}

		return null;
	}

	public Map<Integer, String> getResolutionMap() {
		LinkedHashMap<Integer, String> map = new LinkedHashMap<Integer, String>();
		map.put(ITSEnum.FIXED_RESOLUTION, ITSEnum.S_FIXED_RESOLUTION);
		map.put(ITSEnum.UNABLE_TO_REPRODUCE_RESOLUTION,
		        ITSEnum.S_UNABLE_TO_REPRODUCE_RESOLUTION);
		map.put(ITSEnum.NOT_FIXABLE_RESOLUTION,
		        ITSEnum.S_NOT_FIXABLE_RESOLUTION);
		map.put(ITSEnum.DUPLICATE_RESOLUTION, ITSEnum.S_DUPLICATE_RESOLUTION);
		map.put(ITSEnum.WONT_FIX_RESOLUTION, ITSEnum.S_WONT_FIX_RESOLUTION);
		return map;
	}

	public String getGeneratedTime() {
		return DateUtil.format(m_generatedTime, DateUtil._16DIGIT_DATE_TIME);
	}

	public List<IIssue> getM_stories() {
		return m_stories;
	}

	public void setM_stories(List<IIssue> storylist) {
		this.m_stories = storylist;
	}

	public void setM_taskMap(Map<Long, IIssue[]> map) {
		m_taskMap = map;
	}
}
