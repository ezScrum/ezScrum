package ntut.csie.ezScrum.restful.mobile.support;

import java.io.Serializable;

import org.jfree.chart.JFreeChart;

public interface IScrumReport extends Serializable{
public JFreeChart getChart();
public String getType();
}
