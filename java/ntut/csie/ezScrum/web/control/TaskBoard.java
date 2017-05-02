package ntut.csie.ezScrum.web.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
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
	private ArrayList<StoryObject> m_dropedStories;
	private ArrayList<StoryObject> mStories;
	private ArrayList<TaskObject> mTasksInDropStories;
	private ArrayList<TaskObject> mTasks;
	private LinkedHashMap<Date, Double> mDateToStoryIdealPoint;
	private LinkedHashMap<Date, Double> mDateToStoryPoint;
	private LinkedHashMap<Date, Double> mDateToTaskIdealPoint;
	private LinkedHashMap<Date, Double> mDateToTaskRealPoint;
	private Date mCurrentDate = new Date();
	private Date mGeneratedTime = new Date();
	final private long mOneDay = ScrumEnum.DAY_MILLISECOND;
	
	public TaskBoard(SprintBacklogLogic sprintBacklogLogic, SprintBacklogMapper sprintBacklogMapper) {
		mSprintBacklogLogic = sprintBacklogLogic;
		mSprintBacklogMapper = sprintBacklogMapper;
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

	private void init() {
		// 取得目前最新的Story與Task狀態
		ProjectObject project = mSprintBacklogMapper.getProject();

		mStories = mSprintBacklogLogic.getStoriesSortedByImpInSprint();
		m_dropedStories = mSprintBacklogMapper.getDroppedStories();
		mTasks = mSprintBacklogMapper.getTasksInSprint();
		mTasksInDropStories = mSprintBacklogMapper.getTasksInDropStories();
		mDateToStoryIdealPoint = new LinkedHashMap<Date, Double>(); // Story的理想線
		mDateToTaskIdealPoint = new LinkedHashMap<Date, Double>(); // Task的理想線
		mDateToStoryPoint = new LinkedHashMap<Date, Double>(); // Story的真實線
		mDateToTaskRealPoint = new LinkedHashMap<Date, Double>(); // Task的真實線
	}

	public void buildPointMap(String burndownType) {
		if (mSprintBacklogMapper != null) {
			Date sprintStartWorkDate = mSprintBacklogLogic.getSprintStartWorkDate();
			Date sprintEndWorkDate = mSprintBacklogLogic.getSprintEndWorkDate();
			Date sprintEndDate = mSprintBacklogMapper.getSprintEndDate();

			Calendar indexDate = Calendar.getInstance();
			indexDate.setTime(sprintStartWorkDate);
			
			// The sprint have number of day exclude holiday.
			int dayOfSprint = mSprintBacklogLogic.getSprintWorkDays(); 
			double initPoint;
			long today = mCurrentDate.getTime(); // 今天的日期，如果今天已經在EndDate之後，那就設為EndDate
			if (mCurrentDate.getTime() > sprintEndDate.getTime()) {
				// end date 為當日的 00:00:00 ，所以還要加入OneDay，這樣時間才會是 00:00:00 - 23:59:59
				today = sprintEndDate.getTime() + mOneDay;
			}

			if (burndownType.equals("story")) {
				initPoint = getStoryPointByDate(sprintStartWorkDate); // Initial story point
				setStoryPointMap(indexDate, sprintEndWorkDate, initPoint, dayOfSprint, today);
			} else if (burndownType.equals("task")) {
				initPoint = getTaskPointByDate(sprintStartWorkDate); // Initial task point
				setTaskPointMap(indexDate, sprintEndWorkDate, initPoint, dayOfSprint, today);
			}
		}
	}

	private void setStoryPointMap(Calendar indexDate, Date sprintEndWorkDate, double initPoint, double dayOfSprint,
			long today) {
		int sprintDayCount = 0;
		while (indexDate.getTimeInMillis() <= sprintEndWorkDate.getTime()) {
			Date ckeckDate = indexDate.getTime();
			if (!DateUtil.isHoliday(ckeckDate)) {
				mDateToStoryIdealPoint.put(ckeckDate, initPoint - (initPoint / (dayOfSprint -1))* sprintDayCount);
				if (indexDate.getTimeInMillis() < today) {
					mDateToStoryPoint.put(ckeckDate, getStoryPointByDate(ckeckDate));
				} else {
					mDateToStoryPoint.put(ckeckDate, null);
				}
				sprintDayCount++;
			}
			indexDate.add(Calendar.DATE, 1);
		}
		cleanHistories("Story");
	}

	private void setTaskPointMap(Calendar indexDate, Date sprintEndWorkDate, double initPoint, double dayOfSprint,
			long today) {
		int sprintDayCount = 0;
		while (indexDate.getTimeInMillis() <= sprintEndWorkDate.getTime()) {
			Date ckeckDate = indexDate.getTime();
			if (!DateUtil.isHoliday(ckeckDate)) {
				mDateToTaskIdealPoint.put(ckeckDate, initPoint - (initPoint / (dayOfSprint -1)) * sprintDayCount);
				if (indexDate.getTimeInMillis() < today) {
					double i = getTaskPointByDate(ckeckDate);
					mDateToTaskRealPoint.put(ckeckDate, i);
				} else {
					mDateToTaskRealPoint.put(ckeckDate, null);
				}
				sprintDayCount++;
			}
			indexDate.add(Calendar.DATE, 1);
		}
		cleanHistories("Task");
	}
	
	private void cleanHistories(String issueType){
		if(issueType == "Story"){
			for(StoryObject story : mStories){
				story.cleanHistories();
			}
			for(StoryObject story : m_dropedStories){
				story.cleanHistories();
			}
		}
		else if(issueType == "Task"){
			for(TaskObject task : mTasks){
				task.cleanHistories();
			}
			for(TaskObject task : mTasksInDropStories){
				task.cleanHistories();
			}
		}
	}

	private double getStoryPointByDate(Date date) {
		Date endDate = new Date(date.getTime() + mOneDay);
		double storyPoint = 0;
		// Visit all Story
		for (StoryObject story : mStories) {
			if (story.getStatus(endDate) == StoryObject.STATUS_DONE) {
				continue;
			}
			try {
				storyPoint += getStoryPoint(endDate, story);
			} catch (Exception e) {
				continue;
			}
		}
		// Visit all DropStory		
		for (StoryObject story : m_dropedStories) {
			if (story.getStatus(endDate) == StoryObject.STATUS_DONE) {
				continue;
			}
			try {
				storyPoint += getStoryPoint(endDate, story);
			} catch (Exception e) {
				continue;
			}
		}
		return storyPoint;
	}

	private double getTaskPointByDate(Date date) {
		Date endDate = new Date(date.getTime() + mOneDay);
		double taskPoint = 0;
		// Visit all Story
		for (TaskObject task : mTasks) {
			if (task.getStatus(endDate) == TaskObject.STATUS_DONE) {
				continue;
			}
			try {
				taskPoint += getTaskPoint(endDate, task);
			} catch (Exception e) {
				continue;
			}
		}
		// Visit all DropStory
		for (StoryObject story : m_dropedStories) {
			if (story.getStatus(endDate) == StoryObject.STATUS_DONE || 
					!(story.checkVisableByDate(date, mSprintBacklogMapper.getSprintId()))) {
				continue;
			}
			for (TaskObject task : story.getTasks()) {
				if (task.getStatus(endDate) == TaskObject.STATUS_DONE) {
					continue;
				}
				try {
					taskPoint += getTaskPoint(endDate, task);
				} catch (Exception e) {
					continue;
		   	 	}
			}
		}
		return taskPoint;
	}

	private double getStoryPoint(Date date, StoryObject story) throws Exception {
		double point = 0;
		// 確認這個Story在那個時間是否存在
		if (story.checkVisableByDate(date, mSprintBacklogMapper.getSprintId())) {
			try {
				point = story.getStoryPointByDate(date);
			} catch (Exception e) {
				return 0;
			}
		} else {
			// 表示這個Story在當時不存在於這個Sprint裡面
			throw new Exception("this story isn't at this sprint");
		}
		return point;
	}

	private double getTaskPoint(Date date, TaskObject task) throws Exception {
		double point = 0;
		if (task.checkVisableByDate(date, mSprintBacklogMapper.getSprintId())) {
			try {
				point = task.getTaskPointByDate(date);
			} catch (Exception e1) {
				// 如果沒有，那就回傳 0
				return 0;
			}

		} else {
			throw new Exception("this task isn't at this sprint");
		}
		return point;
	}

	private double[] getPointByDate(Date date) {
		double[] point = { 0, 0 };

		// 依照Type取出當天的Story或者是Task來進行計算
		// 因為輸入的日期為當日的0:0:0,但在23:59:59之前也算當日，所以必需多加一日做為當天的計算
		Date endDate = new Date(date.getTime() + mOneDay);

		// 尋訪現有的所有Story
		for (StoryObject story : mStories) {
			// 已經closed的Story就不用算他的點數啦，連Task都省掉了
			if (story.getStatus(endDate) == StoryObject.STATUS_DONE) {
				continue;
			}

			try {
				// 計算Story點數
				point[0] += getStoryPoint(endDate, story);

				// 計算Task點數
				// 取得這個Story底下的Task點數
				ArrayList<TaskObject> tasks = story.getTasks();
				for (TaskObject task : tasks) {
					if (task.getStatus(endDate) == TaskObject.STATUS_DONE) {
						continue;
					}
					point[1] += getTaskPoint(endDate, task);
				}
			} catch (Exception e) {
				// 如果會有Exception表示此時間Story不在此Sprint中，所以getTagValue回傳null乘parseDouble產生exception
				continue;
			}
		}

		// Visit all DropStory
		for (StoryObject story : m_dropedStories) {
			if (story.getStatus(endDate) == StoryObject.STATUS_DONE) {
				continue;
			}

			try {
				// Calculate story Point
				point[0] += getStoryPoint(endDate, story);

				// Calculate task Point

				ArrayList<TaskObject> tasks = story.getTasks();
				for (TaskObject task : tasks) {
					if (task.getStatus(endDate) == TaskObject.STATUS_DONE) {
						continue;
					}
					point[1] += getTaskPoint(endDate, task);
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
		return mSprintBacklogMapper.getSprintId();
	}

	public String getStoryPoint() {
		SprintObject sprint = mSprintBacklogMapper.getSprint();
		if (sprint == null) {
			return "0.0 / 0.0";
		}
		return sprint.getStoryUnclosedPoints() + " / " + sprint.getTotalStoryPoints();
	}

	public String getTaskPoint() {
		SprintObject sprint = mSprintBacklogMapper.getSprint();
		if (sprint == null) {
			return "0.0 / 0.0";
		}
		return sprint.getTaskRemainsPoints() + " / " + sprint.getTotalTaskPoints();
	}

	public String getInitialStoryPoint() {
		SprintObject sprint = mSprintBacklogMapper.getSprint();
		if (sprint == null) {
			return "0.0 / 0.0";
		}
		return (getStoryPointByDate(mSprintBacklogMapper.getSprintStartDate())) + " / " + sprint.getLimitedPoint();
	}

	public String getInitialTaskPoint() {
		return (getTaskPointByDate(mSprintBacklogMapper.getSprintStartDate())) + " / -";
	}

	public ArrayList<StoryObject> getStories() {
		return mStories;
	}

	public void setStories(ArrayList<StoryObject> stories) {
		mStories = stories;
	}

	public String getStoryChartLink() {
		ProjectObject project = mSprintBacklogMapper.getProject();
		// workspace/project/_metadata/TaskBoard/ChartLink
		String chartPath = "./Workspace/" + project.getName() + "/" + IProject.METADATA + "/" + NAME + File.separator
				+ "Sprint" + getSprintId() + File.separator + STORY_CHART_FILE;

		// 繪圖
		drawGraph(ScrumEnum.STORY_ISSUE_TYPE, chartPath, "Story Points");

		String link = "./Workspace/" + project.getName() + "/" + IProject.METADATA + "/" + NAME + "/Sprint"
				+ getSprintId() + "/" + STORY_CHART_FILE;

		return link;
	}

	public String getTaskChartLink() {
		ProjectObject project = mSprintBacklogMapper.getProject();
		// workspace/project/_metadata/TaskBoard/Sprint1/ChartLink
		String chartPath = "./Workspace/" + project.getName() + "/" + IProject.METADATA + "/" + NAME + File.separator
				+ "Sprint" + getSprintId() + File.separator + TASK_CHART_FILE;

		// 繪圖
		drawGraph(ScrumEnum.TASK_ISSUE_TYPE, chartPath, "Remaining Hours");

		String link = "./Workspace/" + project.getName() + "/" + IProject.METADATA + "/" + NAME + "/Sprint"
				+ getSprintId() + "/" + TASK_CHART_FILE;

		return link;
	}

	private synchronized void drawGraph(String type, String chartPath, String Y_axis_value) {
		// 設定圖表內容
		ChartUtil chartUtil = new ChartUtil(
				(type.equals(ScrumEnum.TASK_ISSUE_TYPE) ? "Tasks" : "Stories") + " Burndown Chart in Sprint #"
						+ getSprintId(),
				mSprintBacklogMapper.getSprintStartDate(),
				new Date(mSprintBacklogMapper.getSprintEndDate().getTime() + 24 * 3600 * 1000));

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
		Color[] colors = { Color.RED, Color.GRAY };
		chartUtil.setColor(colors);

		float[] dashes = { 8f };
		BasicStroke[] strokes = { new BasicStroke(1.5f),
				new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 16f, dashes, 0.f) };
		chartUtil.setStrokes(strokes);

		// 產生圖表
		chartUtil.createChart(chartPath);
	}

	public String getGeneratedTime() {
		return DateUtil.format(mGeneratedTime, DateUtil._16DIGIT_DATE_TIME);
	}
}
