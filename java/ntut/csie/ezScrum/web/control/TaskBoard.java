package ntut.csie.ezScrum.web.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.jcis.core.util.ChartUtil;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

public class TaskBoard {
	private final String STORY_CHART_FILE = "StoryBurnDown.png";
	private final String TASK_CHART_FILE = "TaskBurnDown.png";
	private final String NAME = "TaskBoard";

	private SprintBacklogMapper mSprintBacklogMapper;
	private SprintBacklogLogic mSprintBacklogLogic;
	private List<StoryObject> mStories;
	private ArrayList<StoryObject> mDroppedStories;
	private LinkedHashMap<Date, Double> mDateToStoryIdealPoint;
	private LinkedHashMap<Date, Double> mDateToStoryPoint;
	private LinkedHashMap<Date, Double> mDateToTaskIdealPoint;
	private LinkedHashMap<Date, Double> mDateToTaskRealPoint;
	private Date mCurrentDate = new Date();
	private Date mGeneratedTime = new Date();
	private long mSprintId = 1;
	final private long mOneDay = ScrumEnum.DAY_MILLISECOND;

	public TaskBoard(SprintBacklogLogic sprintBacklogLogic, SprintBacklogMapper sprintBacklogMapper) {
		mSprintBacklogLogic = sprintBacklogLogic;
		mSprintBacklogMapper = sprintBacklogMapper;
		mSprintId = sprintBacklogMapper.getSprintId();
		init();
	}

	// ======================= for unit test ===========================
	public LinkedHashMap<Date, Double> getStoryRealPointMap() {
		return mDateToStoryPoint;
	}

	public LinkedHashMap<Date, Double> getTaskRealPointMap() {
		return mDateToTaskRealPoint;
	}

	public LinkedHashMap<Date, Double> getStoryIdealPointMap() {
		return mDateToStoryIdealPoint;
	}

	public LinkedHashMap<Date, Double> getTaskIdealPointMap() {
		return mDateToTaskIdealPoint;
	}

	// ======================= for unit test ===========================

	private void init() {
		// 取得目前最新的Story與Task狀態
		mStories = mSprintBacklogLogic.getStoriesByImp();
		// 取得從經被drop掉的Story與其底下的Task
		mDroppedStories = mSprintBacklogMapper.getDroppedStories();
		if (mSprintBacklogMapper != null) {
			// Sprint的起始與結束日期資訊
			Date iter_Start_Work_Date = mSprintBacklogLogic.getSprintStartWorkDate();
			Date iter_End_Work_Date = mSprintBacklogLogic.getSprintEndWorkDate();
			Date iter_End_Date = mSprintBacklogMapper.getSprintEndDate();

			Calendar indexDate = Calendar.getInstance();
			indexDate.setTime(iter_Start_Work_Date);
			long endTime = iter_End_Work_Date.getTime();

			mDateToStoryIdealPoint = new LinkedHashMap<Date, Double>();	// Story的理想線
			mDateToTaskIdealPoint = new LinkedHashMap<Date, Double>();	// Task的理想線
			mDateToStoryPoint = new LinkedHashMap<Date, Double>();	// Story的真實線
			mDateToTaskRealPoint = new LinkedHashMap<Date, Double>();		// Task的真實線
			double[] initPoint = getPointByDate(iter_Start_Work_Date);	// 第一天Story與Task的點數
			int dayOfSprint = mSprintBacklogLogic.getSprintWorkDays();	// 扣除假日後，Sprint的總天數
			int num = 0;	// Sprint中第幾天的Counter
			long today = mCurrentDate.getTime();	// 今天的日期，如果今天已經在EndDate之後，那就設為EndDate

			if (mCurrentDate.getTime() > iter_End_Date.getTime()) {
				// enddate為當日的 00:00:00 ，所以還要加入OneDay，這樣時間才會是 00:00:00 - 23:59:59
				today = iter_End_Date.getTime() + mOneDay;
			}
			// 每一天的理想與真實點數
			while (!(indexDate.getTimeInMillis() > endTime) || indexDate.getTimeInMillis() == endTime) {
				Date key = indexDate.getTime();
				// 扣除假日
				if (!DateUtil.isHoliday(key)) {
					// 記錄Story與Task理想線的點數
					// 理想線直線方程式 y = - (起始點數 / 總天數) * 第幾天 + 起始點數
					mDateToStoryIdealPoint.put(key, (((-initPoint[0]) / dayOfSprint) * num) + initPoint[0]);
					mDateToTaskIdealPoint.put(key, (((-initPoint[1]) / dayOfSprint) * num) + initPoint[1]);

					// 記錄Story與Task實際線的點數
					// 只取出今天以前的資料
					if (indexDate.getTimeInMillis() < today) {
						double point[] = getPointByDate(key);
						mDateToStoryPoint.put(key, point[0]);
						mDateToTaskRealPoint.put(key, point[1]);
					} else {
						mDateToStoryPoint.put(key, null);
						mDateToTaskRealPoint.put(key, null);
					}
					num++;
				}
				indexDate.add(Calendar.DATE, 1);
			}
		}
	}

	private double getStoryPoint(Date date, StoryObject story) throws Exception {
		double point = 0;
		// 確認這個Story在那個時間是否存在
		if (story.getSprintId() == mSprintId) {
			point = story.getEstimate();
		} 
		else {
			// 表示這個Story在當時不存在於這個Sprint裡面
			throw new Exception("this story isn't at this sprint");
		}
		return point;
	}

	private double getTaskPoint(Date date, TaskObject task) {
		double point = 0;

		try {
			point = task.getRemains(date);
		} 
		catch (Exception e) {
			try {
				// 表示這個Task沒有REMAINS，那麼就取得ESTIMATION
				point = task.getEstimate();
			} catch (Exception e1) {
				// 如果還是沒有，那就回傳 0
				return 0;
			}
		}
		return point;
	}

	private double[] getPointByDate(Date date) {
		double[] point = {0, 0};

		/************************************************************
		 * 依照Type取出當天的Story或者是Task來進行計算
		 *************************************************************/
		// 因為輸入的日期為當日的0:0:0,但在23:59:59之前也算當日，所以必需多加一日做為當天的計算
		Date dueDate = new Date(date.getTime() + mOneDay);

		// 尋訪現有的所有Story
		for (StoryObject story : mStories) {
			// 已經closed的Story就不用算他的點數啦，連Task都省掉了
			if (story.getStatus() == StoryObject.STATUS_DONE) {
				continue;
			}

			try {
				/***************
				 * 計算Story點數
				 ***************/
				point[0] += getStoryPoint(dueDate, story);

				/***************
				 * 計算Task點數
				 ***************/
				// 取得這個Story底下的Task點數
				ArrayList<TaskObject> tasks = story.getTasks();
				for (TaskObject task : tasks) {
					if(task.getStatus() == TaskObject.STATUS_DONE) {
						continue;
					}
					point[1] += getTaskPoint(dueDate, task);
				}
			} catch (Exception e) {
				// 如果會有Exception表示此時間Story不在此Sprint中，所以getTagValue回傳null乘parseDouble產生exception
				continue;
			}
		}

		// 尋訪現有的所有Droped Story
		for (StoryObject story : mDroppedStories) {
			// 已經closed的Story就不用算他的點數啦，連Task都省掉了
			if (story.getStatus() == StoryObject.STATUS_DONE) {
				continue;
			}
			try {
				/***************
				 * 計算Story點數
				 ***************/
				point[0] += getStoryPoint(dueDate, story);

				/***************
				 * 計算Task點數
				 ***************/
				// 取得這個Story底下的Task點數
				ArrayList<TaskObject> tasks = story.getTasks();
				for (TaskObject task : tasks) {
					// 已經closed的task就不用算他的點數啦
					if (task.getStatus(dueDate) == TaskObject.STATUS_DONE)
						continue;
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
		return mSprintBacklogMapper.getSprintGoal();
	}

	public long getSprintId() {
		return mSprintId;
	}

	public String getStoryPoint() {
		return mSprintBacklogLogic.getStoryUnclosedPoints()
		        + " / "
		        + mSprintBacklogLogic.getTotalStoryPoints();
	}

	public String getTaskPoint() {
		return mSprintBacklogLogic.getTaskRemainsPoints()
		        + " / "
		        + mSprintBacklogLogic.getTaskEstimatePoints();
	}

	public String getInitialStoryPoint() {
		return (getPointByDate(mSprintBacklogMapper.getSprintStartDate())[0]) + " / "
		        + mSprintBacklogMapper.getLimitedPoint();
	}

	public String getInitialTaskPoint() {
		return (getPointByDate(mSprintBacklogMapper.getSprintStartDate())[1]) + " / -";
	}

	public List<StoryObject> getStories() {
		return mStories;
	}

	public void setStories(List<StoryObject> stories) {
		mStories = stories;
	}

	public String getStoryChartLink() {
		IProject project = mSprintBacklogMapper.getProject();
		// workspace/project/_metadata/TaskBoard/ChartLink
		String chartPath = project.getFolder(IProject.METADATA).getFullPath()
		        + File.separator + NAME + File.separator + "Sprint"
		        + getSprintId() + File.separator + STORY_CHART_FILE;

		// 繪圖
		drawGraph(ScrumEnum.STORY_ISSUE_TYPE, chartPath, "Story Points");

		String link = "./Workspace/" + project.getName() + "/"
		        + IProject.METADATA + "/" + NAME + "/Sprint"
		        + getSprintId() + "/" + STORY_CHART_FILE;

		return link;
	}

	public String getTaskChartLink() {
		IProject project = mSprintBacklogMapper.getProject();
		// workspace/project/_metadata/TaskBoard/Sprint1/ChartLink
		String chartPath = project.getFolder(IProject.METADATA).getFullPath()
		        + File.separator + NAME + File.separator + "Sprint"
		        + getSprintId() + File.separator + TASK_CHART_FILE;

		// 繪圖
		drawGraph(ScrumEnum.TASK_ISSUE_TYPE, chartPath, "Remaining Hours");

		String link = "./Workspace/" + project.getName() + "/"
		        + IProject.METADATA + "/" + NAME + "/Sprint"
		        + getSprintId() + "/" + TASK_CHART_FILE;

		return link;
	}

	private synchronized void drawGraph(String type, String chartPath, String Y_axis_value) {
		// 設定圖表內容
		ChartUtil chartUtil = new ChartUtil((type
		        .equals(ScrumEnum.TASK_ISSUE_TYPE) ? "Tasks" : "Stories")
		        + " Burndown Chart in Sprint #" + getSprintId(),
		        mSprintBacklogMapper.getSprintStartDate(), new Date(mSprintBacklogMapper
		                .getSprintEndDate().getTime() + 24 * 3600 * 1000));

		chartUtil.setChartType(ChartUtil.LINECHART);

		// TODO:要新增的data set
		if (type.equals(ScrumEnum.TASK_ISSUE_TYPE)) {
			chartUtil.addDataSet("current", mDateToTaskRealPoint);
			chartUtil.addDataSet("ideal", mDateToTaskIdealPoint);
		} else {
			chartUtil.addDataSet("current", mDateToStoryPoint);
			chartUtil.addDataSet("ideal", mDateToStoryIdealPoint);
		}
		chartUtil.setInterval(1);
		chartUtil.setValueAxisLabel(Y_axis_value);
		// 依照輸入的順序來呈現顏色
		Color[] colors = {Color.RED, Color.GRAY};
		chartUtil.setColor(colors);

		float[] dashes = {8f};
		BasicStroke[] strokes = {
		        new BasicStroke(1.5f),
		        new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 16f, dashes, 0.f)};
		chartUtil.setStrokes(strokes);

		// 產生圖表
		chartUtil.createChart(chartPath);
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
		return DateUtil.format(mGeneratedTime, DateUtil._16DIGIT_DATE_TIME);
	}
}
