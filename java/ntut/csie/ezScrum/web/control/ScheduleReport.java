package ntut.csie.ezScrum.web.control;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.jcis.core.util.CloseStreamUtil;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.SegmentedTimeline;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.IntervalCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.time.SimpleTimePeriod;

public class ScheduleReport {
	private final long OneDay = 24 * 3600 * 1000;
	private Date m_startDate = new Date();
	private Date m_endDate = new Date();
	private int m_interval = 7;
	private DateTickUnit m_dtu = null;;
	private IProject project = null;
	private IUserSession session;
	private int iteration = -1;
	private String folderName = "ScheduleReport"; 
	private String fileName = "ScheduleReport.png";
	private String chartPath ="";
	private String sprintGoal = "";
	private int size = 0;
	public ScheduleReport(IProject project, IUserSession session, int iteration){
		this.project = project;
		this.session = session;
		this.iteration = iteration;
	}
	
	public ScheduleReport(IProject project, IUserSession session){
		this.project = project;
		this.session = session;
	}
	
	public void generateChart(){
		//gernerate dataset
		IntervalCategoryDataset dataset = createDataset();
		//create chart
		JFreeChart chart = ChartFactory.createGanttChart( 
				"Schedule Report", // chart title 
				"Work Item", // domain axis label 
				"Date", // range axis label 
				dataset, // data 
				true, // include legend 
				true, // tooltips 
				false // urls 
		);
		//設定屬性
		setAttribute(chart);
		//儲存檔案
		saveChart(chart);
	}
	
	public String getPath(){
		String link = "./Workspace/" + project.getName() + "/"
		+ IProject.METADATA + "/" + folderName +"/Sprint"+iteration+"/"+fileName;
		return link;
	}
	
	private IntervalCategoryDataset createDataset(){
//		SprintBacklogMapper sb = null;
//		TaskSeriesCollection collection = null;
//		if(this.iteration!=-1){
//			sb = new SprintBacklogMapper(project, session, iteration);
//		}
//		else{
//			sb = new SprintBacklogMapper(project, session);
//			this.iteration = sb.getSprintPlanId();
//		}
		
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, session, String.valueOf(iteration));
		SprintBacklogMapper sb = sprintBacklogLogic.getSprintBacklogMapper();
		TaskSeriesCollection collection = null;
		if(this.iteration==-1){
			this.iteration = sb.getSprintPlanId();
		}
		
		if(sb!=null){
			//設定抓取iter 時間
			this.m_startDate = sb.getSprintStartDate();
			this.m_endDate = sb.getSprintEndDate();
			this.sprintGoal = sb.getSprintGoal();
			collection = initTaskCollection(sprintBacklogLogic, sb);

		}
		return collection; 
	}
	private TaskSeriesCollection initTaskCollection(SprintBacklogLogic sprintBacklogLogic, SprintBacklogMapper sb){
		TaskSeriesCollection collection = null;
		//塞入資料
//		List<IIssue> stories = sb.getStories();
		List<IIssue> stories = sprintBacklogLogic.getStories();
		this.size = stories.size();
		Map<Long, ArrayList<TaskObject>> taskMap = sb.getTasksMap();
		/** 
		* Creating a task series 
		* And adding planned tasks dates on the series. 
		*/
		TaskSeries seriesOne = new TaskSeries("Story"); 
		collection = new TaskSeriesCollection(); 
		 
		/** 
		* Adding the series to the collection 
		* Holds actual Dates. 
		*/
		collection.add(seriesOne); 
		for(IIssue story: stories){
			String summary = story.getSummary();
			ArrayList<TaskObject> tasks = taskMap.get(story.getIssueID());
			
			//因為Story下面有時候會沒有Task所以如果沒有Task就跳過?
			if (tasks == null)
				continue;
			
			long openDate = 0;
			long doneDate = 0;
			for(TaskObject task: tasks){
				task.getActual();
				long assignedDate = task.getCreateTime();
				long closedDate = task.getDoneTime();
				if(closedDate ==0){
					doneDate = 0;
					break;
				}
				if(openDate==0&&doneDate==0){
					openDate = assignedDate;
					doneDate = closedDate;
				}
				else{
					if(assignedDate < openDate)
						openDate = assignedDate;
					if(closedDate > doneDate)
						doneDate = closedDate;
				}
			}
			SimpleTimePeriod per;
			if(doneDate ==0)
				per = new SimpleTimePeriod(0, 0);
			else 
				per = new SimpleTimePeriod(openDate, doneDate);
			Task work = new Task(summary, per);
			seriesOne.add(work);
		}
		return collection;
	}
	
	private void setAttribute(JFreeChart chart){
		//圖案與文字的間隔
		LegendTitle legend = chart.getLegend();
		legend.setBorder(1, 1, 1, 1);
		
		CategoryPlot plot=chart.getCategoryPlot();
		//設定WorkItem的屬性
		CategoryAxis domainAxis = plot.getDomainAxis(); 
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45); //字體角度
		domainAxis.setTickLabelFont(new Font("新細明體", Font.TRUETYPE_FONT, 12)); //字體
				
		//設定Date的屬性
		DateAxis da = (DateAxis)plot.getRangeAxis(0);
		setDateAxis(da);

	
		//設定實體的顯示名稱
		CategoryItemRenderer render = plot.getRenderer(0);
		DateFormat format =	new SimpleDateFormat("yyyy-MM-dd");
		CategoryItemLabelGenerator generator = new IntervalCategoryItemLabelGenerator("{3} ~ {4}", format);
		render.setBaseItemLabelGenerator(generator);
		render.setBaseItemLabelPaint(Color.BLUE);
		render.setBaseItemLabelsVisible(true);	
		render.setBaseItemLabelFont(new Font("黑體", Font.TRUETYPE_FONT, 8));
		render.setSeriesPaint(0, Color.RED);
	}
	
	private void setDateAxis(DateAxis da){
		da.setVerticalTickLabels(true);
//		da.setTickUnit(new DateTickUnit(DateTickUnit.DAY, 1));
		da.setTickLabelsVisible(true);
		da.setVisible(true);
		da.setDateFormatOverride(new SimpleDateFormat("yyyy-MM-dd"));
		
		/*
		 * 以下為調整日期顯示的間隔
		 */

		int totalDays = (int) ((m_endDate.getTime() - this.m_startDate
				.getTime()) / OneDay);

		//自動產生
			int intervalNum = totalDays / m_interval;
			/**
			 * 這裡有bug by ninja31312
			 * int intervalNum = totalDays / m_interval;
			 * 若 totalDays < m_interval 則 intervalNum等於0
			 * 所以sprint的interval如果挑一個禮拜 
			 * ex:
			 * if start Date 2010/12/20
			 *    end   Date 就是2010/12/26
			 * 26-20 = 6
			 * 所以6/7 = 0
			 * 因此圖會畫不出來丟出exception
			 * 其他13/7 = 1 , 20/7 = 2 ...皆為正常
			 */
			if( intervalNum == 0 ){
				intervalNum = 1;
			}
			if (intervalNum <= 30)
				m_dtu = new DateTickUnit(DateTickUnit.DAY, intervalNum);
			else {
				// if ((intervalNum / 30 + 1) * m_interval > 7)
				m_dtu = new DateTickUnit(DateTickUnit.DAY, m_interval
						* (intervalNum / 30 + 1));
			}

		da.setTickUnit(m_dtu);
		da.setRange(this.m_startDate, this.m_endDate);

		// 去掉六日
		SegmentedTimeline timeLine = SegmentedTimeline
				.newMondayThroughFridayTimeline();
		da.setTimeline(timeLine);

	}
	
	private void saveChart(JFreeChart chart){
		chartPath = project.getFolder(IProject.METADATA).getFullPath()
				+ File.separator + this.folderName 
				+ File.separator + "Sprint"+this.iteration
				+ File.separator + this.fileName;
		
		File f = new File(chartPath);
		
		FileOutputStream fos = null;
		try {
			// 預防資料夾尚未被建起
			File folder = new File(chartPath).getParentFile();
			if (folder != null && !folder.exists())
				folder.mkdirs();

			// 將圖表檔輸出
			fos = new FileOutputStream(chartPath);
			ChartUtilities.writeChartAsPNG(fos, chart, 800, 600);
			fos.close();
			while (!f.exists()) {
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			CloseStreamUtil.close(fos);
		}
	}
	public String getSprintGoal(){
		return this.sprintGoal;
	}
	
	public String getDuration(){
		String startDate = DateUtil.format(this.m_startDate , DateUtil._8DIGIT_DATE_1);
		String endtDate = DateUtil.format(this.m_endDate , DateUtil._8DIGIT_DATE_1);
		String duration = startDate +" ~ "+endtDate;		
		return duration;
	}
	public int getIteration(){
		return this.iteration;
	}
	
	public int getStorySize(){
		return this.size;
	}
}
