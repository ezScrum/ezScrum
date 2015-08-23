package ntut.csie.ezScrum.iteration.iternal;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.ReleaseObject;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.jcis.core.util.ChartUtil;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

public class ReleaseBoard {
	private final String STORY_CHART_FILE = "StoryBurnDown.png";
	private final String NAME = "ReleaseBoard";

	private final long OneDay = ScrumEnum.DAY_MILLISECOND;
	private ReleaseObject mRelease;
	private LinkedHashMap<Date, Double> mStoryIdealMap;
	private LinkedHashMap<Date, Double> mStoryRealMap;

	private Date m_currentDate = new Date();

	public ReleaseBoard(ReleaseObject release) {
		mRelease = release;
		init();
	}

	// 產生圖表的 map 資料
	// 1. 將 map 資料轉換成 json 字串給 ExtJS
	// 2. 先前版本是可以產生一個 JFreeChart
	private void init() {
		if (mRelease != null) {
			Date startDate = DateUtil.dayFilter(mRelease.getStartDateString());
			Date dueDate = DateUtil.dayFilter(mRelease.getDueDateString());
			int totalDays = (int) ((dueDate.getTime() - startDate.getTime()) / OneDay);

			// ============產生繪製story用的資料=======================
			// 產生理想的線
			mStoryIdealMap = new LinkedHashMap<Date, Double>();

			double[] initPoint = { getStoryCount(), 0.0 };

			// 產生真實預估的線
			mStoryRealMap = new LinkedHashMap<Date, Double>();
			Calendar indexDate = Calendar.getInstance();
			indexDate.setTime(startDate);

			// 計算直線方程式用
			int num = 0;
			// 因為一個 release 可能包含很多個 sprint ，一次顯示所有日期會太多資訊
			// 所以設定區間為每個 sprint 顯示兩筆資料
			int interval_count = mRelease.getSprints().size() * 2;
			int increase_days = (int) (totalDays / interval_count);
			// 每一天的理想與真實點數
			while (indexDate.getTimeInMillis() < m_currentDate.getTime()) {
				if (indexDate.getTimeInMillis() > dueDate
						.getTime())
					break;

				Date key = indexDate.getTime();

				// 理想線直線方程式 y = - (起始點數 / 總天數) * 第幾天 + 起始點數
				mStoryIdealMap.put(key, (((-initPoint[0]) / totalDays) * num)
						+ initPoint[0]);
				mStoryRealMap.put(key,
						getStoryCount() - mRelease.getDoneStoryByDate(key));

				indexDate.add(Calendar.DATE, increase_days);
				num += increase_days;
			}

			// 針對 release 的最後一天作特別處理顯示當天的點數
			indexDate.setTime(dueDate);
			Date key = indexDate.getTime();
			mStoryIdealMap.put(key, 0.0);
			mStoryRealMap.put(key, mRelease.getReleaseAllStoryDone());
		}
	}

	public LinkedHashMap<Date, Double> getStoryIdealPointMap() {
		return this.mStoryIdealMap;
	}

	public LinkedHashMap<Date, Double> getStoryRealPointMap() {
		return this.mStoryRealMap;
	}

	public String getPlanID() {
		return String.valueOf(mRelease.getId());
	}

	public String getPlanName() {
		return mRelease.getName();
	}

	public double getStoryCount() {
		return mRelease.getStories().size();
	}

	public double getUndoneStoryCount() {
		return (getStoryCount() - mRelease.getDoneStoryByDate(new Date()));
	}

	public String getStoryChartLink() {
		ProjectObject project = ProjectObject.get(mRelease.getProjectId());
		IProject iProject = new ProjectMapper().getProjectByID(project.getName());
		// workspace/project/_metadata/TaskBoard/ChartLink
		String chartPath = iProject.getFolder(IProject.METADATA).getFullPath()
				+ File.separator + this.NAME + File.separator + "Plan"
				+ mRelease.getId() + File.separator + STORY_CHART_FILE;

		// 繪圖
		drawGraph(ScrumEnum.STORY_ISSUE_TYPE, chartPath);

		String link = "./Workspace/" + project.getName() + "/"
				+ IProject.METADATA + "/" + NAME + "/Plan" + mRelease.getId()
				+ "/" + STORY_CHART_FILE;

		return link;
	}

	private synchronized void drawGraph(String type, String chartPath) {
		// 設定圖表內容
		Date startDate = DateUtil.dayFilter(mRelease.getStartDateString());
		Date dueDate = DateUtil.dayFilter(mRelease.getDueDateString());
		ChartUtil chartUtil = new ChartUtil(
				"Stories Burndown Chart in Release Plan #"
						+ mRelease.getId(),
						startDate, new Date(dueDate.getTime() + 24 * 3600 * 1000));

		chartUtil.setChartType(ChartUtil.LINECHART);

		// TODO:要新增的data set
		chartUtil.addDataSet("current", mStoryRealMap);
		chartUtil.addDataSet("ideal", mStoryIdealMap);
		chartUtil.setInterval(1);
		chartUtil.setValueAxisLabel("Stories");
		// 依照輸入的順序來呈現顏色
		Color[] colors = { Color.RED, Color.GRAY };
		chartUtil.setColor(colors);

		float[] dashes = { 8f };
		BasicStroke[] strokes = {
				new BasicStroke(1.5f),
				new BasicStroke(1.5f, BasicStroke.CAP_ROUND,
						BasicStroke.JOIN_ROUND, 16f, dashes, 0.f) };
		chartUtil.setStrokes(strokes);

		// 產生圖表
		chartUtil.createChart(chartPath);
	}
}
