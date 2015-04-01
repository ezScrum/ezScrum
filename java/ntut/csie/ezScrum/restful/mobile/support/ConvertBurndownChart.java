package ntut.csie.ezScrum.restful.mobile.support;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import ntut.csie.ezScrum.restful.mobile.util.BurndownChartUtil;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class ConvertBurndownChart {
	private JSONObject mStoryBurndownChartList, mTaskBurndownChartList;
	private JSONArray mStoryPoints, mTaskPoints;

	public ConvertBurndownChart() throws JSONException {
		// story burn down chart
		mStoryBurndownChartList = new JSONObject();
		mStoryPoints = new JSONArray();
		mStoryBurndownChartList.put(BurndownChartUtil.TAG_BURNDOWNCHART,
				mStoryPoints);

		// task burn down chart
		mTaskBurndownChartList = new JSONObject();
		mTaskPoints = new JSONArray();
		mTaskBurndownChartList.put(BurndownChartUtil.TAG_BURNDOWNCHART,
				mTaskPoints);
	}

	public void convertStoryPoint(LinkedHashMap<Date, Double> storyPointMap)
			throws JSONException {
		
		Object[] storyDateArray = storyPointMap.keySet().toArray();
		for (int i = 0; i < storyDateArray.length; i++) {
			JSONObject storyPoint = new JSONObject();
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			String createDate = dateFormat.format(storyDateArray[i]);
			storyPoint.put(BurndownChartUtil.TAG_DATE, createDate);
			Double points = storyPointMap.get(storyDateArray[i]);
			if (points != null) { // sprint在進行中，在還沒到的日期下是不會有story point的
				storyPoint.put(BurndownChartUtil.TAG_POINT, points.toString());
			} else {
				storyPoint.put(BurndownChartUtil.TAG_POINT, "");
			}
			mStoryPoints.put(storyPoint);
		}
	}

	public void convertTaskPoint(LinkedHashMap<Date, Double> taskPointMap)
			throws JSONException {
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		for (Map.Entry<Date, Double> entry : taskPointMap.entrySet()) {
			JSONObject taskPoint = new JSONObject();
			Date date = entry.getKey();
			Double points = entry.getValue();

			String createDate = dateFormat.format(date);
			taskPoint.put(BurndownChartUtil.TAG_DATE, createDate);
			if (points != null) { // sprint在進行中，在還沒到的日期下是不會有story point的
				taskPoint.put(BurndownChartUtil.TAG_POINT, points.toString());
			} else {
				taskPoint.put(BurndownChartUtil.TAG_POINT, "");
			}
			mTaskPoints.put(taskPoint);
		}
	}

	public String getStoryBurndownChartJSONString() {
		return mStoryBurndownChartList.toString();
	}

	public String getTaskBurndownChartJSONString() {
		return mTaskBurndownChartList.toString();
	}
}
