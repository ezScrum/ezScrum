package ntut.csie.ezScrum.restful.mobile.support;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import ntut.csie.ezScrum.restful.mobile.util.BurndownChartUtil;

public class ConvertBurndownChart {
	private JSONObject storyBurndownChartList, taskBurndownChartList;
	private JSONArray storyPointArray, taskPointArray;
	public ConvertBurndownChart() throws JSONException {
		//	story burndown chart
		storyBurndownChartList = new JSONObject();
		storyPointArray = new JSONArray();
		storyBurndownChartList.put( BurndownChartUtil.TAG_BURNDOWNCHART, storyPointArray );
		
		//	task burndown chart
		taskBurndownChartList = new JSONObject();
		taskPointArray = new JSONArray();
		taskBurndownChartList.put( BurndownChartUtil.TAG_BURNDOWNCHART, taskPointArray );
	}
	public void convertStoryRealPointMap( LinkedHashMap<Date,Double> storyRealPointMap ) throws JSONException{
		Object [] storyDateArray = storyRealPointMap.keySet().toArray() ;		
		for( int i = 0 ; i < storyDateArray.length ; i ++ ){
			JSONObject storyPoint = new JSONObject();
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			String createDate = dateFormat.format( storyDateArray[ i ]);
			storyPoint.put(BurndownChartUtil.TAG_DATE, createDate);
			Double points = storyRealPointMap.get( storyDateArray[ i ] );
			if( points != null ){    //sprint在進行中，在還沒到的日期下是不會有story point的
				storyPoint.put(BurndownChartUtil.TAG_POINT, points.toString());
			}
			else
			{
				storyPoint.put(BurndownChartUtil.TAG_POINT, "");
			}
			storyPointArray.put(storyPoint);
		}		
	}
	
	public void convertTaskRealPointMap(LinkedHashMap<Date, Double> taskRealPointMap) throws JSONException{
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		for( Map.Entry<Date, Double> entry: taskRealPointMap.entrySet() ){
			JSONObject taskPoint = new JSONObject();
			Date date = entry.getKey();
			Double points = entry.getValue();
			
			String createDate = dateFormat.format(date);
			taskPoint.put(BurndownChartUtil.TAG_DATE, createDate);
			if( points != null ){    //sprint在進行中，在還沒到的日期下是不會有story point的
				taskPoint.put(BurndownChartUtil.TAG_POINT, points.toString());
			}
			else
			{
				taskPoint.put(BurndownChartUtil.TAG_POINT, "");
			}
			taskPointArray.put(taskPoint);
		}
	}
	
	public String getStoryRealPointMapJSONString(){
		return storyBurndownChartList.toString();
	}
	
	public String getTaskRealPointMapJSONString() {
		return taskBurndownChartList.toString();
	}
}
