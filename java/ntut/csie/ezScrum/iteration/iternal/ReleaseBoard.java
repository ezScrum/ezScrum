package ntut.csie.ezScrum.iteration.iternal;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.jcis.core.util.ChartUtil;
import ntut.csie.jcis.resource.core.IProject;

public class ReleaseBoard {
	private final String STORY_CHART_FILE = "StoryBurnDown.png";
	private final String NAME = "ReleaseBoard";

	private final long OneDay = ScrumEnum.DAY_MILLISECOND;
	private double m_storyCount;
	private ReleaseBacklog m_backlog;
	private LinkedHashMap<Date, Double> m_storyIdealMap;
	private LinkedHashMap<Date, Double> m_storyRealMap;

	private Date m_currentDate = new Date();

	public ReleaseBoard(ReleaseBacklog backlog) {
		m_backlog = backlog;
		m_storyCount = 0;
		init();
	}

	// 產生圖表的 map 資料
	// 1. 將 map 資料轉換成 json 字串給 ExtJS 
	// 2. 先前版本是可以產生一個 JFreeChart
	private void init() {
		if(m_backlog!=null){
			int totalDays = (int) ((m_backlog.getEndDate().getTime() - m_backlog.getStartDate().getTime()) / OneDay);
			m_storyCount = Double.parseDouble(String.valueOf(m_backlog.getStoryCount()));
	
			// ============產生繪製story用的資料=======================
			// 產生理想的線
			m_storyIdealMap = new LinkedHashMap<Date, Double>();
			
			double[] initPoint = { m_storyCount, 0.0};
	
			// 產生真實預估的線
			m_storyRealMap = new LinkedHashMap<Date, Double>();
			Calendar indexDate = Calendar.getInstance();
			indexDate.setTime(m_backlog.getStartDate());
			
			// 計算直線方程式用
			int num = 0;
			// 因為一個 release 可能包含很多個 sprint ，一次顯示所有日期會太多資訊
			// 所以設定區間為每個 sprint 顯示兩筆資料
			int interval_count = m_backlog.getSprintPlanCounts() * 2;
			int increase_days = (int) (totalDays/interval_count);
			// 每一天的理想與真實點數
			while (indexDate.getTimeInMillis() < m_currentDate.getTime()) {
				if (indexDate.getTimeInMillis() > m_backlog.getEndDate().getTime())
					break;
				
				Date key = indexDate.getTime();

				// 理想線直線方程式 y = - (起始點數 / 總天數) * 第幾天  + 起始點數
				m_storyIdealMap.put(key, (((-initPoint[0]) / totalDays) * num) + initPoint[0]);
				m_storyRealMap.put(key, m_storyCount - m_backlog.getDoneStoryByDate(key));
				
				indexDate.add(Calendar.DATE, increase_days);
				num += increase_days;
			}
			
			// 針對 release 的最後一天作特別處理顯示當天的點數
			indexDate.setTime(m_backlog.getEndDate());
			Date key = indexDate.getTime();
			m_storyIdealMap.put(key, 0.0);
			m_storyRealMap.put(key, m_backlog.getReleaseAllStoryDone());
		}
	}
	
	public LinkedHashMap<Date, Double> getStoryIdealPointMap() {
		return this.m_storyIdealMap;
	}
	
	public LinkedHashMap<Date, Double> getStoryRealPointMap() {
		return this.m_storyRealMap;
	}

	public String getPlanID(){
		return m_backlog.getID();
	}
	
	public String getPlanName(){
		return m_backlog.getName();
	}
	
	public double getStoryCount(){
		return m_storyCount;
	}
	
	public double getUndoneStoryCount(){
		return (m_storyCount - m_backlog.getDoneStoryByDate(new Date()));
	}

	public String getStoryChartLink() {
		IProject project = m_backlog.getProject();
		// workspace/project/_metadata/TaskBoard/ChartLink
		String chartPath = project.getFolder(IProject.METADATA).getFullPath()
				+ File.separator + this.NAME + File.separator + "Plan" + m_backlog.getID() + File.separator
				+ STORY_CHART_FILE;

		// 繪圖
		drawGraph(ScrumEnum.STORY_ISSUE_TYPE, chartPath);

		String link = "./Workspace/" + project.getName() + "/"
				+ IProject.METADATA + "/" + NAME + "/Plan" + m_backlog.getID() + "/" + STORY_CHART_FILE;

		return link;
	}

	private synchronized void drawGraph(String type, String chartPath) {
		// 設定圖表內容
		ChartUtil chartUtil = new ChartUtil(
				"Stories Burndown Chart in Release Plan #" + m_backlog.getID(), 
				this.m_backlog.getStartDate(), 
				new Date(this.m_backlog.getEndDate().getTime()+24*3600*1000));

		chartUtil.setChartType(ChartUtil.LINECHART);

		// TODO:要新增的data set
		chartUtil.addDataSet("current", m_storyRealMap);
		chartUtil.addDataSet("ideal", m_storyIdealMap);
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

