package ntut.csie.ezScrum.restful.mobile.service;

import ntut.csie.ezScrum.restful.mobile.support.ConvertBurndownChart;
import ntut.csie.ezScrum.web.control.TaskBoard;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.jcis.account.core.LogonException;

import org.codehaus.jettison.json.JSONException;

public class BurndownChartWebService extends ProjectWebService {
	private TaskBoard taskBoard;

	public BurndownChartWebService(String username, String password,
			String projectName, long sprintId) throws LogonException {
		super(username, password, projectName);
		
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(super
				.getAllProjects().get(0), sprintId);
		SprintBacklogMapper sprintBacklogMapper = sprintBacklogLogic
				.getSprintBacklogMapper();
		taskBoard = new TaskBoard(sprintBacklogLogic, sprintBacklogMapper);
	}

	public String getRESTFulStoryPointMapResponseString()
			throws JSONException {
		
		ConvertBurndownChart convertBurndownChart = new ConvertBurndownChart();
		convertBurndownChart.convertStoryPoint(taskBoard
				.getStoryRealPointMap());
		return convertBurndownChart.getStoryBurndownChartJSONString();
	}

	public String getRESTFulTaskPointMapResponseString()
			throws JSONException {
		
		ConvertBurndownChart convertBurndownChart = new ConvertBurndownChart();
		convertBurndownChart.convertTaskPoint(taskBoard
				.getTaskRealPointMap());
		return convertBurndownChart.getTaskBurndownChartJSONString();
	}
}
