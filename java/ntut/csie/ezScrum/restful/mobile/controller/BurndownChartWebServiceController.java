package ntut.csie.ezScrum.restful.mobile.controller;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import ntut.csie.ezScrum.restful.mobile.service.BurndownChartWebService;
import ntut.csie.ezScrum.restful.mobile.support.InformationDecoder;
import ntut.csie.jcis.account.core.LogonException;

import org.codehaus.jettison.json.JSONException;

@Path("{projectID}/burndown-chart/")
public class BurndownChartWebServiceController {
	private BurndownChartWebService bcws ;
	
	/**
	 * 取得指定sprint 的 story burndown chart
	 * //http://IP:8080/ezScrum/web-service/{projectID}/burndown-chart/{sprintID}/story-burndown-chart?userName={userName}&password={password}
	 * @param userName
	 * @param password
	 * @param projectID
	 * @param sprintID
	 * @return
	 */
	@GET
	@Path("{sprintID}/story-burndown-chart")
	@Produces(MediaType.APPLICATION_JSON)
	public String getStoryBurndownChart( @QueryParam("userName") String userName,
										 @QueryParam("password") String password,
										 @PathParam("projectID") String projectID,
										 @PathParam("sprintID")  String sprintID ){
		String storyPointsJsonString = "";
		InformationDecoder decoder = new InformationDecoder();
		try {
			decoder.decode( userName, password, projectID );
			this.bcws = new BurndownChartWebService( decoder.getDecodeUserName(), 
													 decoder.getDecodePwd(), 
													 decoder.getDecodeProjectID() ,
													 Integer.parseInt( sprintID ) ) ;
			storyPointsJsonString = this.bcws.getRESTFulStoryPointMapResponseString();
		} catch (IOException e) {
			System.out.println(	"class: InformationDecoder, " + 
								"method: decode, " + 
								"exception: " + e.toString() );
		} catch (LogonException e) {
			System.out.println(	"class: BurndownChartWebServiceController, " + 
								"method: getSprintBacklogList, " + 
								"exception: " + e.toString() );
			e.printStackTrace();
		} catch (JSONException e) {
			System.out.println(	"class: ConvertBurndownChart, " + 
								"method: getStoryRealPointMapJSONString, " + 
								"exception: " + e.toString() );
			e.printStackTrace();
		}
		return storyPointsJsonString;
	}
	
	/**
	 * 取得單一Sprint Task BurndownChart
	 * http://IP:8080/ezScrum/web-service/{projectID}/burndown-chart/{sprintID}/task-burndown-chart?userName={userName}&password={password}
	 * @param userName
	 * @param password
	 * @param projectID
	 * @param sprintID
	 * @return
	 */
	@GET
	@Path("{sprintID}/task-burndown-chart")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTaskBurndownChart(	@QueryParam("userName") String userName,
										@QueryParam("password") String password,
										@PathParam("projectID") String projectID,
										@PathParam("sprintID")  String sprintID ){
		String taskPointsJsonString = "";
		InformationDecoder decoder = new InformationDecoder();
		try {
			decoder.decode( userName, password, projectID );
			this.bcws = new BurndownChartWebService( decoder.getDecodeUserName(),
													 decoder.getDecodePwd(),
													 decoder.getDecodeProjectID(),
													 Integer.parseInt( sprintID ) );
			taskPointsJsonString = this.bcws.getRESTFulTaskPointMapResponseString();
		} catch (IOException e) {
			System.out.println(	"class: InformationDecoder, " + 
								"method: decode, " + 
								"exception: " + e.toString() );
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LogonException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			System.out.println(	"class: BurndownChartWebServiceController, " + 
								"method: getTaskBurndownChart and then getRESTFulTaskRealPointMapResponseString, " + 
								"exception: " + e.toString() );
			e.printStackTrace();
		}
		return taskPointsJsonString;
	}
}
