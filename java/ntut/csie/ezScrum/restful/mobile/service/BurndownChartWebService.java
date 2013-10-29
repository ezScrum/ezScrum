package ntut.csie.ezScrum.restful.mobile.service;

import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.restful.mobile.support.ConvertBurndownChart;
import ntut.csie.ezScrum.web.control.TaskBoard;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.jcis.account.core.LogonException;

import org.codehaus.jettison.json.JSONException;

public class BurndownChartWebService extends ProjectWebService{
	private TaskBoard taskBoard ;
	public BurndownChartWebService( String username , String userpwd , String projectID , int sprintID ) throws LogonException{
		super(username, userpwd , projectID );
//		SprintBacklogMapper sprintBacklog = new SprintBacklogMapper( super.getProjectList().get( 0 ) , new UserSession( super.getAccount() ) , sprintID ) ;
//		taskBoard = new TaskBoard( sprintBacklog );
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic( super.getProjectList().get( 0 ) , new UserSession( super.getAccount() ) , String.valueOf(sprintID) ) ;
		SprintBacklogMapper sprintBacklogMapper = sprintBacklogLogic.getSprintBacklogMapper();
		taskBoard = new TaskBoard( sprintBacklogLogic, sprintBacklogMapper );
	}
	
	public String getRESTFulStoryRealPointMapResponseString() throws JSONException{
		ConvertBurndownChart convertBurndownChart = new ConvertBurndownChart();
		convertBurndownChart.convertStoryRealPointMap( taskBoard.getstoryRealPointMap() ) ;
		return convertBurndownChart.getStoryRealPointMapJSONString();
	}
	public String getRESTFulTaskRealPointMapResponseString() throws JSONException {
		ConvertBurndownChart convertBurndownChart = new ConvertBurndownChart();
		convertBurndownChart.convertTaskRealPointMap( taskBoard.gettaskRealPointMap() );
		return convertBurndownChart.getTaskRealPointMapJSONString();
	}
}
