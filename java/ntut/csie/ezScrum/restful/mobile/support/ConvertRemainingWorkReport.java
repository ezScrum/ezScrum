package ntut.csie.ezScrum.restful.mobile.support;

import java.awt.Color;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.jcis.core.util.ChartUtil;

import org.jfree.chart.JFreeChart;

public class ConvertRemainingWorkReport implements IScrumReport{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String Type="";
	private Map<Date, Integer> m_nonAssignMap = new TreeMap<Date, Integer>();
	private Map<Date, Integer> m_AssignedMap = new TreeMap<Date, Integer>();
	private Map<Date, Integer> m_DoneMap = new TreeMap<Date, Integer>();
	private long chartStartDate;
	private long chartEndDate;
	private int interval;
	private String ProjectName="";
	private String Category="";
	private String SprintID;
	private String NonAssign;
	private String Assigned;
	private String Total;
	private String Done;
	public String getSprintID() {
		return SprintID;
	}
	public void setSprintID(String sprintID) {
		SprintID = sprintID;
	}
	public void setCategory(String category) {
		Category = category;
	}
	public ConvertRemainingWorkReport()
	{
		this.Type=ScrumEnum.REMAININGWORK;
	}
	@Override
	public JFreeChart getChart() {
	ChartUtil chartUtil = new ChartUtil(ProjectName
				+ " Work Activity", new Date(chartStartDate), new Date(chartEndDate));

		chartUtil.setChartType(ChartUtil.AREALINECHART);
		
		chartUtil.addDataSet("Done", this.m_DoneMap);
		chartUtil.addDataSet("Assigned", this.m_AssignedMap);
		chartUtil.addDataSet("non-Assign", this.m_nonAssignMap);
		chartUtil.setInterval(interval);
		chartUtil.setValueAxisLabel("Num. of Tasks ");

		Color[] colors = { Color.GREEN, Color.BLUE, Color.RED };

		chartUtil.setColor(colors);

		// 產生圖表
		
		return chartUtil.getChart();
	}
	@Override
	public String getType() {
		return Type;
	}
	public Map<Date, Integer> getM_nonAssignMap() {
		return m_nonAssignMap;
	}
	public void setM_nonAssignMap(Map<Date, Integer> assignMap) {
		m_nonAssignMap = assignMap;
	}
	public Map<Date, Integer> getM_AssignedMap() {
		return m_AssignedMap;
	}
	public void setM_AssignedMap(Map<Date, Integer> assignedMap) {
		m_AssignedMap = assignedMap;
	}
	public Map<Date, Integer> getM_DoneMap() {
		return m_DoneMap;
	}
	public void setM_DoneMap(Map<Date, Integer> doneMap) {
		m_DoneMap = doneMap;
	}
	public long getChartStartDate() {
		return chartStartDate;
	}
	public void setChartStartDate(long chartStartDate) {
		this.chartStartDate = chartStartDate;
	}
	public long getChartEndDate() {
		return chartEndDate;
	}
	public void setChartEndDate(long chartEndDate) {
		this.chartEndDate = chartEndDate;
	}
	public int getInterval() {
		return interval;
	}
	public void setInterval(int interval) {
		this.interval = interval;
	}
	public String getProjectName() {
		return ProjectName;
	}
	public void setProjectName(String projectName) {
		ProjectName = projectName;
	}
	public String getCategory() {
		return Category;
	}
	public String getNonAssign() {
		return NonAssign;
	}
	public void setNonAssign(String nonAssign) {
		NonAssign = nonAssign;
	}
	public String getAssigned() {
		return Assigned;
	}
	public void setAssigned(String assigned) {
		Assigned = assigned;
	}
	public String getTotal() {
		return Total;
	}
	public void setTotal(String total) {
		Total = total;
	}
	public String getDone() {
		return Done;
	}
	public void setDone(String done) {
		Done = done;
	}
}
