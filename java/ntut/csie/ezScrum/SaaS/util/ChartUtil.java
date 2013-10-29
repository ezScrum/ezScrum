package ntut.csie.ezScrum.SaaS.util;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SegmentedTimeline;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.TimePeriodValuesCollection;
import org.jfree.data.xy.XYDataset;

public class ChartUtil {
	public final static String BARCHART = "BarChart";
	public final static String LINECHART = "LineChart";
	public final static String AREALINECHART = "AreaLineChart";

	private final long OneDay = 24 * 3600 * 1000;

	private Map<String, Map<Date, ? extends Number>> m_dataSetMap = new LinkedHashMap<String, Map<Date, ? extends Number>>();
	private Date m_startDate = DateUtil.getNowDate();
	private Date m_endDate = DateUtil.getNowDate();
	private int m_interval = 7;
	@SuppressWarnings("unused")
	private int m_markerValue = 0;
	@SuppressWarnings("unused")
	private String m_markerLabel = "";
	private Map<Date, Date> m_dateMarkerMap = new TreeMap<Date, Date>();
	private boolean m_markerFlag = false;
	private String m_title = "";
	private JFreeChart m_chart;
	private String m_chartType = BARCHART;
	private String m_valueAxisLabel = "";
	//private Color[] m_colors = null;
	private boolean m_visualMarkerLabel = false;
	//private Color m_markerColor = Color.DARK_GRAY;
	//private Stroke[] m_strokes = null;
	private DateTickUnit m_dtu = null;
	private boolean m_lineShapeVisible = true;
	@SuppressWarnings("unused")
	private int width = 400;
	@SuppressWarnings("unused")
	private int height = 300;

	public void setSize(int w, int h) {
		width = w;
		height = h;
	}

	public ChartUtil(String title) {
		this.m_title = title;
	}

	public ChartUtil(String title, Date start, Date end) {
		this.m_title = title;
		m_startDate = start;
		m_endDate = end;
	}

	public JFreeChart getChart() {
		drow();
		return m_chart;
	}

	private void drow() {
		// ��b
		final DateAxis domainAxis = new DateAxis("Date");
		domainAxis.setVerticalTickLabels(true);
		domainAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);

		int totalDays = (int) ((m_endDate.getTime() - this.m_startDate
				.getTime()) / OneDay);

		// �վ�����ܪ����j
		// �Y����null�h�N��۰ʲ���
		if (m_dtu == null) {
			int intervalNum = totalDays / m_interval;

			if (intervalNum <= 30)
				m_dtu = new DateTickUnit(DateTickUnit.DAY, m_interval);
			else {
				// if ((intervalNum / 30 + 1) * m_interval > 7)
				m_dtu = new DateTickUnit(DateTickUnit.DAY, m_interval
						* (intervalNum / 30 + 1));
				// else
				// m_dtu = new DateTickUnit(DateTickUnit.DAY, 7);
			}
		}

		domainAxis.setTickUnit(m_dtu);
		domainAxis.setRange(this.m_startDate, this.m_endDate);

		// �h������
		SegmentedTimeline timeLine = SegmentedTimeline
				.newMondayThroughFridayTimeline();
		domainAxis.setTimeline(timeLine);

		// �a�b
		final NumberAxis rangeAxis = new NumberAxis(this.m_valueAxisLabel);
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// ���1
		final XYDataset data = createBarDataset();

		XYPlot plot;

		if (this.m_chartType.equals(ChartUtil.LINECHART)) {
			// ��u��
			final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
			renderer.setBaseShapesVisible(m_lineShapeVisible);
			plot = new XYPlot(data, domainAxis, rangeAxis, renderer);
		} else if (this.m_chartType.equals(ChartUtil.AREALINECHART)) {
			// �϶���
			final XYAreaRenderer renderer = new XYAreaRenderer();
			plot = new XYPlot(data, domainAxis, rangeAxis, renderer);
		} else {
			// ����
			final XYBarRenderer renderer = new XYBarRenderer();
			renderer.setDrawBarOutline(true);
			plot = new XYPlot(data, domainAxis, rangeAxis, renderer);
		}

		// ���2
		// final XYDataset data1 = createLineDataset();

		// ��u��
		// final XYLineAndShapeRenderer renderer1 = new
		// XYLineAndShapeRenderer();
		// renderer1.setBaseShapesVisible(true);
		// renderer1.setBaseShapesFilled(false);

		// �b��u�I�W���ͼƭ�
		// renderer1.setBaseItemLabelGenerator(new
		// StandardXYItemLabelGenerator());
		// renderer1.setBaseItemLabelsVisible(true);

		// plot.setDataset(1, data1);
		// plot.setRenderer(1, renderer1);

		plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
		plot.setOrientation(PlotOrientation.VERTICAL);

		// �аO�W�X�]�w���q
//		if (this.m_markerFlag) {
//			setMarker(plot);
//			ValueMarker valueMarker = new ValueMarker(this.m_markerValue);
//			valueMarker.setStroke(new BasicStroke(1.5f));
//			valueMarker.setPaint(Color.GREEN);
//			valueMarker.setLabel(m_markerLabel);
//			valueMarker.setLabelPaint(Color.BLACK);
//			valueMarker.setLabelTextAnchor(TextAnchor.HALF_ASCENT_LEFT);
//			plot.addRangeMarker(valueMarker);
//		}

		// ��wø�s���C��
//		if (this.m_colors != null) {
//			int index = 0;
//			XYItemRenderer renderer = plot.getRenderer();
//			for (Color color : this.m_colors)
//				renderer.setSeriesPaint(index++, color);
//		}

		// ��w�u��ø�s
//		if (this.m_strokes != null) {
//			XYItemRenderer renderer = plot.getRenderer();
//			for (int i = 0; i < this.m_strokes.length; i++) {
//				renderer.setSeriesStroke(i, m_strokes[i]);
//			}
//		}

		m_chart = new JFreeChart(this.m_title, JFreeChart.DEFAULT_TITLE_FONT,
				plot, true);
	}

	public void createChart(String path) {
		drow();
		outputToFile(path);
	}

	private void outputToFile(String path) {
		// mark IO operation 
//		File f = new File(path);
//		
//		FileOutputStream fos = null;
//		try {
//			// �w����Ƨ��|���Q�ذ_
//			File folder = new File(path).getParentFile();
//			if (folder != null && !folder.exists())
//				folder.mkdirs();
//
//			// �N�Ϫ��ɿ�X��=>PPQA\result\SVNCommitFrequentlyChart.png
//			fos = new FileOutputStream(path);
//			ChartUtilities.writeChartAsPNG(fos, m_chart, width, height);
//			fos.close();
//			while (!f.exists()) {
//			}
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			CloseStreamUtil.close(fos);
//		}
	}

	private XYDataset createBarDataset() {
		final TimePeriodValuesCollection result = new TimePeriodValuesCollection();
		Set<String> nameSet = this.m_dataSetMap.keySet();

		for (String name : nameSet) {
			final TimePeriodValues series = new TimePeriodValues(name);

			Map<Date, ? extends Number> map = m_dataSetMap.get(name);

			Iterator<Date> ir = map.keySet().iterator();

			while (ir.hasNext()) {
				Date key = ir.next();
				series.add(new SimpleTimePeriod(key, new Date(key.getTime()
						+ (m_interval * OneDay))), map.get(key));
			}

			result.addSeries(series);
		}
		return result;
	}

	@SuppressWarnings("unused")
	private void setMarker(XYPlot plot) {
		Iterator<Date> ir = m_dateMarkerMap.keySet().iterator();
		int index = 1;
		while (ir.hasNext()) {

			Date key = ir.next();

			final Marker marker = new IntervalMarker(key.getTime(),
					m_dateMarkerMap.get(key).getTime());

			if (this.m_visualMarkerLabel)
				marker.setLabel("#" + index);
			// marker.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
			// marker.setLabelTextAnchor(TextAnchor.TOP_LEFT);

			marker.setAlpha(0.3f);
			//marker.setPaint(this.m_markerColor);

			plot.addDomainMarker(marker);
			index++;
		}
	}

	public void addDataSet(String name, Map<Date, ? extends Number> map) {
		m_dataSetMap.put(name, map);
	}

	public boolean isMarker() {
		return m_markerFlag;
	}

	public void setMarker(boolean marker) {
		m_markerFlag = marker;
	}

	public void setInterval(int interval) {
		this.m_interval = interval;
	}

	public void setMarkValue(int markValue) {
		this.m_markerValue = markValue;
	}

	public void setMarkerMap(Map<Date, Date> map) {
		this.m_dateMarkerMap = map;
	}

	public void setValueMarkerLabel(String label) {
		this.m_markerLabel = label;
	}

	public void setChartType(String type) {
		this.m_chartType = type;
	}

	public void setValueAxisLabel(String label) {
		this.m_valueAxisLabel = label;
	}

//	public void setColor(Color[] colors) {
//		this.m_colors = colors;
//	}

	public void setVisualMarkerLabel(boolean visual) {
		this.m_visualMarkerLabel = visual;
	}

//	public void setMarkerColor(Color color) {
//		this.m_markerColor = color;
//	}
//
//	public void setStrokes(Stroke[] stroke) {
//		this.m_strokes = stroke;
//	}

	public void setLabelInterval(int days) {
		m_dtu = new DateTickUnit(DateTickUnit.DAY, days);
	}

	public void setShapesVisible(boolean visible) {
		m_lineShapeVisible = visible;
	}
}
