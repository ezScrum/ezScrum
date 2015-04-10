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

@Path("{projectName}/burndown-chart/")
public class BurndownChartWebServiceController {
	private BurndownChartWebService burndownChartWebService;

	/**
	 * 取得指定sprint 的 story burndown chart
	 * //http://IP:8080/ezScrum/web-service/{projectName
	 * }/burndown-chart/{sprintId
	 * }/story-burndown-chart?username={userName}&password={password}
	 * 
	 * @param username
	 * @param password
	 * @param projectName
	 * @param sprintId
	 * @return
	 */
	@GET
	@Path("{sprintId}/story-burndown-chart")
	@Produces(MediaType.APPLICATION_JSON)
	public String getStoryBurndownChart(
			@QueryParam("username") String username,
			@QueryParam("password") String password,
			@PathParam("projectName") String projectName,
			@PathParam("sprintId") String sprintId) {
		String storyPointsJsonString = "";
		InformationDecoder decoder = new InformationDecoder();
		try {
			decoder.decode(username, password, projectName);
			burndownChartWebService = new BurndownChartWebService(decoder.getDecodeUsername(),
					decoder.getDecodePwd(), decoder.getDecodeProjectName(),
					Integer.parseInt(sprintId));
			storyPointsJsonString = burndownChartWebService
					.getRESTFulStoryPointMapResponseString();
		} catch (IOException e) {
			System.out.println("class: InformationDecoder, "
					+ "method: decode, " + "exception: " + e.toString());
		} catch (LogonException e) {
			System.out.println("class: BurndownChartWebServiceController, "
					+ "method: getSprintBacklogList, " + "exception: "
					+ e.toString());
			e.printStackTrace();
		} catch (JSONException e) {
			System.out.println("class: ConvertBurndownChart, "
					+ "method: getStoryRealPointMapJSONString, "
					+ "exception: " + e.toString());
			e.printStackTrace();
		}
		return storyPointsJsonString;
	}

	/**
	 * 取得單一Sprint Task BurndownChart
	 * http://IP:8080/ezScrum/web-service/{projectName
	 * }/burndown-chart/{sprintId}
	 * /task-burndown-chart?username={userName}&password={password}
	 * 
	 * @param username
	 * @param password
	 * @param projectName
	 * @param sprintId
	 * @return
	 */
	@GET
	@Path("{sprintId}/task-burndown-chart")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTaskBurndownChart(@QueryParam("username") String username,
			@QueryParam("password") String password,
			@PathParam("projectName") String projectName,
			@PathParam("sprintId") String sprintId) {
		String taskPointsJsonString = "";
		InformationDecoder decoder = new InformationDecoder();
		try {
			decoder.decode(username, password, projectName);
			burndownChartWebService = new BurndownChartWebService(decoder.getDecodeUsername(),
					decoder.getDecodePwd(), decoder.getDecodeProjectName(),
					Integer.parseInt(sprintId));
			taskPointsJsonString = burndownChartWebService.getRESTFulTaskPointMapResponseString();
		} catch (IOException e) {
			System.out.println("class: InformationDecoder, "
					+ "method: decode, " + "exception: " + e.toString());
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (LogonException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			System.out
					.println("class: BurndownChartWebServiceController, "
							+ "method: getTaskBurndownChart and then getRESTFulTaskRealPointMapResponseString, "
							+ "exception: " + e.toString());
			e.printStackTrace();
		}
		return taskPointsJsonString;
	}
}
