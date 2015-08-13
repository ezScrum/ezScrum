package ntut.csie.ezScrum.restful.mobile.controller;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import ntut.csie.ezScrum.restful.mobile.service.SprintBacklogWebService;
import ntut.csie.ezScrum.restful.mobile.service.SprintPlanWebService;
import ntut.csie.ezScrum.restful.mobile.support.InformationDecoder;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.jcis.account.core.LogonException;

import org.codehaus.jettison.json.JSONException;

@Path("{projectName}/sprint-backlog/")
public class SprintBacklogWebServiceController {
	private SprintPlanWebService mSprintPlanWebService;
	private SprintBacklogWebService mSprintBacklogWebService;

	/****
	 * 取得所有sprint information
	 * http://IP:8080/ezScrum/web-service/{projectName}/sprint
	 * -backlog/sprintlist?username={userName}&password={password}
	 * 
	 * @return
	 */
	@GET
	@Path("sprintlist")
	@Produces(MediaType.APPLICATION_JSON)
	public String getSprintInfoList(@QueryParam("username") String username,
			@QueryParam("password") String password,
			@PathParam("projectName") String projectName) {
		String jsonString = "";
		InformationDecoder decoder = new InformationDecoder();
		try {
			decoder.decode(username, password, projectName);
			AccountObject user = new AccountObject(decoder.getDecodeUsername());
			user.setPassword(decoder.getDecodePwd());
			mSprintPlanWebService = new SprintPlanWebService(user,
					decoder.getDecodeProjectName());
			jsonString = mSprintPlanWebService.getRESTFulResponseString();
		} catch (IOException e) {
			System.out.println("class: InformationDecoder, "
					+ "method: decode, " + "exception: " + e.toString());
			e.printStackTrace();
		} catch (LogonException e) {
			System.out.println("class: SprintPlanWebService, "
					+ "method: SprintPlanWebService, " + "exception: "
					+ e.toString());
			e.printStackTrace();
		} catch (JSONException e) {
			System.out.println("class: ConvertSprintBacklog, "
					+ "method: readSprintInformationList, " + "exception: "
					+ e.toString());
			e.printStackTrace();
		}
		return jsonString;
	}

	/****
	 * 依照sprint id和 handler id 取得sprint backlog(該sprint的story及底下的task資訊)
	 * http://IP
	 * :8080/ezScrum/web-service/{projectName}/sprint-backlog/{sprintId}/
	 * sprintbacklog?username={userName}&password={password}
	 * 
	 * @return
	 */
	@GET
	@Path("{sprintId}/sprintbacklog")
	@Produces(MediaType.APPLICATION_JSON)
	public String getSprintBacklog(@QueryParam("username") String userName,
			@QueryParam("password") String password,
			@PathParam("projectName") String projectName,
			@PathParam("sprintId") String sprintId) {
		String jsonString = "";
		InformationDecoder decoder = new InformationDecoder();
		try {
			decoder.decode(userName, password, projectName);
			if (sprintId.equals("currentSprint")) {
				mSprintBacklogWebService = new SprintBacklogWebService(
						decoder.getDecodeUsername(), decoder.getDecodePwd(),
						decoder.getDecodeProjectName());
			} else {
				int iteration = Integer.parseInt(sprintId);
				mSprintBacklogWebService = new SprintBacklogWebService(
						decoder.getDecodeUsername(), decoder.getDecodePwd(),
						decoder.getDecodeProjectName(), iteration);
			}

			jsonString = mSprintBacklogWebService.getSprintBacklogJsonString();

		} catch (IOException e) {
			System.out.println("class: InformationDecoder, "
					+ "method: decode, " + "exception: " + e.toString());
			e.printStackTrace();
		} catch (LogonException e) {
			System.out.println("class: SprintPlanWebService, "
					+ "method: SprintPlanWebService, " + "exception: "
					+ e.toString());
			e.printStackTrace();
		} catch (JSONException e) {
			System.out.println("class: ConvertSprintBacklog, "
					+ "method: readSprintInformationList, " + "exception: "
					+ e.toString());
			e.printStackTrace();
		}
		return jsonString;
	}

	/****
	 * 取得current sprint information
	 * http://IP:8080/ezScrum/web-service/{projectName
	 * }/sprint-backlog/current-sprint?username={userName}&password={password}
	 * 
	 * @return
	 */
	@GET
	@Path("current-sprint")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCurrentSprintBacklog(
			@QueryParam("username") String username,
			@QueryParam("password") String password,
			@PathParam("projectName") String projectName) {
		String currentSprintBacklogJsonString = "";
		InformationDecoder decoder = new InformationDecoder();
		try {
			decoder.decode(username, password, projectName);
			AccountObject user = new AccountObject(decoder.getDecodeUsername());
			user.setPassword(decoder.getDecodePwd());
			mSprintPlanWebService = new SprintPlanWebService(user,
					decoder.getDecodeProjectName());
			currentSprintBacklogJsonString = mSprintPlanWebService.getCurrentSprintJsonString();
		} catch (Exception e) {
			System.out.println("class: SprintBacklogWebServiceController, "
					+ "method: getCurrentSprintBacklog, " + "exception: "
					+ e.toString());
			e.printStackTrace();
		}
		return currentSprintBacklogJsonString;
	}

	/****
	 * 取得指定sprint 中所有story id list
	 * http://IP:8080/ezScrum/web-service/{projectName}
	 * /sprint-backlog/{sprintId}/storylist
	 * ?username={userName}&password={password}
	 * 
	 * @param username
	 * @param password
	 * @param projectName
	 * @param sprintId
	 * @return
	 */
	@GET
	@Path("{sprintId}/storylist")
	@Produces(MediaType.APPLICATION_JSON)
	public String getStoriesId(@QueryParam("username") String username,
			@QueryParam("password") String password,
			@PathParam("projectName") String projectName,
			@PathParam("sprintId") String sprintId) {
		String storiesIdJsonString = "";
		InformationDecoder decoder = new InformationDecoder();
		try {
			decoder.decode(username, password, projectName);
			if (sprintId.equals("current")) {
				mSprintBacklogWebService = new SprintBacklogWebService(
						decoder.getDecodeUsername(), decoder.getDecodePwd(),
						decoder.getDecodeProjectName());
			} else {
				int iteration = Integer.parseInt(sprintId);
				mSprintBacklogWebService = new SprintBacklogWebService(
						decoder.getDecodeUsername(), decoder.getDecodePwd(),
						decoder.getDecodeProjectName(), iteration);
			}
			storiesIdJsonString = mSprintBacklogWebService.getStoriesIdJsonStringInSprint();
		} catch (IOException e) {
			System.out.println("class: InformationDecoder, "
					+ "method: decode, " + "exception: " + e.toString());
		} catch (LogonException e) {
			System.out.println("class: SprintBacklogWebServiceController, "
					+ "method: getStoriesId, " + "exception: "
					+ e.toString());
			e.printStackTrace();
		} catch (JSONException e) {
			System.out.println("class: ConvertSprintBacklog, "
					+ "method: readStoryInformationList, " + "exception: "
					+ e.toString());
			e.printStackTrace();
		}
		return storiesIdJsonString;
	}

	/***
	 * 取得單一Story的所有Task id list
	 * http://IP:8080/ezScrum/web-service/{projectName}/sprint
	 * -backlog/{sprintId}/{
	 * storyId}/task-id-list?username={userName}&password={password}
	 * 
	 * @return
	 */
	@GET
	@Path("{sprintId}/{storyId}/task-id-list")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTasksId(@QueryParam("username") String username,
			@QueryParam("password") String password,
			@PathParam("projectName") String projectName,
			@PathParam("sprintId") long sprintId,
			@PathParam("storyId") long storyId) {
		String tasksIdJsonString = "";
		InformationDecoder decoder = new InformationDecoder();
		try {
			decoder.decode(username, password, projectName);
			mSprintBacklogWebService = new SprintBacklogWebService(
					decoder.getDecodeUsername(), decoder.getDecodePwd(),
					decoder.getDecodeProjectName(), sprintId);
			tasksIdJsonString = mSprintBacklogWebService
					.getTasksIdJsonStringInStory(storyId);
		} catch (IOException e) {
			System.out
					.println("Class:SprintBacklogWebServiceController.java, method:getTasksId, exception:IOException, "
							+ e.toString());
			e.printStackTrace();
		} catch (LogonException e) {
			System.out
					.println("Class:SprintBacklogWebServiceController.java, method:getTasksId, exception:LogonException, "
							+ e.toString());
			e.printStackTrace();
		} catch (JSONException e) {
			System.out
					.println("Class:SprintBacklogWebServiceController.java, method:getTasksId, exception:JSONException, "
							+ e.toString());
			e.printStackTrace();
		} finally {
		}
		return tasksIdJsonString;
	}

	/**
	 * 取得單一Task的History
	 * http://IP:8080/ezScrum/web-service/{projectName}/sprint-backlog
	 * /{sprintId}/{taskId}/history?username={userName}&password={password}
	 * 
	 * @param username
	 * @param password
	 * @param projectName
	 * @param sprintId
	 * @param storyId
	 * @param taskId
	 * @return
	 */
	@GET
	@Path("{sprintId}/{taskId}/history")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTaskHistory(@QueryParam("username") String username,
			@QueryParam("password") String password,
			@PathParam("projectName") String projectName,
			@PathParam("sprintId") long sprintId,
			@PathParam("taskId") long taskId) {
		String taskHistoryJsonString = "";
		InformationDecoder decoder = new InformationDecoder();
		try {
			decoder.decode(username, password, projectName);
			mSprintBacklogWebService = new SprintBacklogWebService(
					decoder.getDecodeUsername(), decoder.getDecodePwd(),
					decoder.getDecodeProjectName(), sprintId);
			taskHistoryJsonString = mSprintBacklogWebService
					.getTaskHsitoryJsonString(taskId);
		} catch (IOException e) {
			System.out
					.println("Class:SprintBacklogWebServiceController.java, method:getTaskHistory, exception:IOException, "
							+ e.toString());
			e.printStackTrace();
		} catch (NumberFormatException e) {
			System.out
					.println("Class:SprintBacklogWebServiceController.java, method:getTaskHistory, exception:NumberFormatException, "
							+ e.toString());
			e.printStackTrace();
		} catch (LogonException e) {
			System.out
					.println("Class:SprintBacklogWebServiceController.java, method:getTaskHistory, exception:LogonException, "
							+ e.toString());
			e.printStackTrace();
		} catch (JSONException e) {
			System.out
					.println("Class:SprintBacklogWebServiceController.java, method:getTaskHistory, exception:JSONException, "
							+ e.toString());
			e.printStackTrace();
		} finally {
		}
		return taskHistoryJsonString;
	}

	/****
	 * 取得單一Task的 information
	 * http://IP:8080/ezScrum/web-service/{projectName}/sprint
	 * -backlog/{sprintId}/{taskId}?username={userName}&password={password}
	 * 
	 * @return
	 */
	@GET
	@Path("{sprintId}/{taskId}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTaskInformation(@QueryParam("username") String username,
			@QueryParam("password") String password,
			@PathParam("projectName") String projectName,
			@PathParam("sprintId") long sprintId,
			@PathParam("taskId") long taskId) {
		String jsonString = "";
		InformationDecoder decoder = new InformationDecoder();
		try {
			decoder.decode(username, password, projectName);
			mSprintBacklogWebService = new SprintBacklogWebService(
					decoder.getDecodeUsername(), decoder.getDecodePwd(),
					decoder.getDecodeProjectName(), sprintId);
			jsonString = mSprintBacklogWebService.getTaskJsonString(taskId);
		} catch (IOException e) {
			System.out
					.println("Class:SprintBacklogWebServiceController.java, method:getTaskInformation, exception:IOException, "
							+ e.toString());
			e.printStackTrace();
		} catch (LogonException e) {
			System.out
					.println("Class:SprintBacklogWebServiceController.java, method:getTaskInformation, exception:LogonException, "
							+ e.toString());
			e.printStackTrace();
		} catch (JSONException e) {
			System.out
					.println("Class:SprintBacklogWebServiceController.java, method:getTaskInformation, exception:JSONException, "
							+ e.toString());
			e.printStackTrace();
		} finally {
		}
		return jsonString;
	}

}
