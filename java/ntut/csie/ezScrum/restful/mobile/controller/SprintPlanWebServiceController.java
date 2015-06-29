package ntut.csie.ezScrum.restful.mobile.controller;

import java.io.IOException;
import java.sql.SQLException;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import ntut.csie.ezScrum.restful.mobile.service.SprintPlanWebService;
import ntut.csie.ezScrum.restful.mobile.support.InformationDecoder;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.jcis.account.core.LogonException;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.google.gson.Gson;

@Path("{projectName}/sprint/")
public class SprintPlanWebServiceController {
	SprintPlanWebService mSprintPlanWebService;

	/**
	 * Create Sprint
	 * http://IP:8080/ezScrum/web-service/{projectName}/sprint/create
	 * ?username={userName}&password={password}
	 * **/
	@POST
	@Path("create")
	@Produces(MediaType.APPLICATION_JSON)
	public String createSprint(@PathParam("projectName") String projectName,
			@QueryParam("username") String username,
			@QueryParam("password") String password, JSONObject sprintJson) {
		Gson gson = new Gson();
		String responseString = "";
		try {
			SprintObject sprintObject = gson.fromJson(sprintJson.toString(), SprintObject.class);
			InformationDecoder decoder = new InformationDecoder();
			decoder.decode(username, password, projectName);
			// 使用者帳號
			AccountObject account = new AccountObject(decoder.getDecodeUsername());
			account.setPassword(decoder.getDecodePwd());
			mSprintPlanWebService = new SprintPlanWebService(account, decoder.getDecodeProjectName());
			mSprintPlanWebService.createSprint(sprintObject);
			responseString += mSprintPlanWebService.getRESTFulResponseString();
		} catch (JSONException e) {
			responseString += "JSONException";
			System.out.println("class: SprintWebServiceController, "
					+ "method: createSprint, " + "exception: " + e.toString());
			e.printStackTrace();
		} catch (LogonException e) {
			responseString += "LogonException";
			System.out.println("class: SprintWebServiceController, "
					+ "method: createSprint, " + "exception: " + e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			responseString += "IOException";
			System.out.println("class: SprintWebServiceController, "
					+ "method: createSprint, " + "api:InformationDecoder, "
					+ "exception: " + e.toString());
			e.printStackTrace();
		}
		return responseString;
	}

	/**
	 * Delete Sprint
	 * http://IP:8080/ezScrum/web-service/{projectName}/sprint/delete
	 * /{sprintId}?username={userName}&password={password}
	 * **/
	@DELETE
	@Path("delete/{sprintId}")
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteSprint(@PathParam("projectName") String projectName,
			@PathParam("sprintId") String sprintId,
			@QueryParam("username") String username,
			@QueryParam("password") String password) {
		String responseString = "";
		try {
			InformationDecoder decoder = new InformationDecoder();
			decoder.decode(username, password, projectName);
			AccountObject userObject = new AccountObject(decoder.getDecodeUsername());
			userObject.setPassword(decoder.getDecodePwd());
			mSprintPlanWebService = new SprintPlanWebService(userObject,
					decoder.getDecodeProjectName());
			mSprintPlanWebService.deleteSprint(sprintId);
			responseString += mSprintPlanWebService.getRESTFulResponseString();
		} catch (JSONException e) {
			responseString += "JSONException";
			System.out.println("class: SprintWebServiceController, "
					+ "method: deleteSprint, " + "exception: " + e.toString());
			e.printStackTrace();
		} catch (LogonException e) {
			responseString += "LogonException";
			System.out.println("class: SprintWebServiceController, "
					+ "method: deleteSprint, " + "exception: " + e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			responseString += "IOException";
			System.out.println("class: SprintWebServiceController, "
					+ "method: deleteSprint, " + "api:InformationDecoder, "
					+ "exception: " + e.toString());
			e.printStackTrace();
		}
		return responseString;
	}

	/**
	 * Update Sprint
	 * http://IP:8080/ezScrum/web-service/{projectName}/sprint/update
	 * ?username={userName}&password={password}
	 * **/
	@PUT
	@Path("update")
	@Produces(MediaType.APPLICATION_JSON)
	public String updateSprint(@PathParam("projectName") String projectName,
			@QueryParam("username") String username,
			@QueryParam("password") String password, String sprintJson) {
		Gson gson = new Gson();
		String responseString = "";
		try {
			SprintObject sprint = gson.fromJson(sprintJson, SprintObject.class);
			InformationDecoder decoder = new InformationDecoder();
			decoder.decode(username, password, projectName);
			AccountObject userObject = new AccountObject(decoder.getDecodeUsername());
			userObject.setPassword(decoder.getDecodePwd());
			mSprintPlanWebService = new SprintPlanWebService(userObject,
					decoder.getDecodeProjectName());
			mSprintPlanWebService.updateSprint(sprint);
			responseString += mSprintPlanWebService.getRESTFulResponseString();
		} catch (JSONException e) {
			responseString += "JSONException";
			System.out.println("class: SprintWebServiceController, "
					+ "method: updateSprint, " + "exception: " + e.toString());
			e.printStackTrace();
		} catch (LogonException e) {
			responseString += "LogonException";
			System.out.println("class: SprintWebServiceController, "
					+ "method: updateSprint, " + "exception: " + e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			responseString += "IOException";
			System.out.println("class: SprintWebServiceController, "
					+ "method: updateSprint, " + "api:InformationDecoder, "
					+ "exception: " + e.toString());
			e.printStackTrace();
		}
		return responseString;
	}

	/****
	 * 取得 project 中所有的 sprint
	 * http://IP:8080/ezScrum/web-service/{projectName}/sprint
	 * /all?username={userName}&password={password}
	 * 
	 * @return
	 */
	@GET
	@Path("all")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllSprint(@PathParam("projectName") String projectName,
			@QueryParam("username") String username,
			@QueryParam("password") String password) {
		String jsonString = "";
		InformationDecoder decoder = new InformationDecoder();
		try {
			decoder.decode(username, password, projectName);
			AccountObject userObject = new AccountObject(decoder.getDecodeUsername());
			userObject.setPassword(decoder.getDecodePwd());
			mSprintPlanWebService = new SprintPlanWebService(userObject,
					decoder.getDecodeProjectName());
			jsonString = mSprintPlanWebService.getAllSprint();
		} catch (LogonException e) {
			System.out.println("class: SprintWebServiceController, "
					+ "method: getAllSprint, " + "exception: " + e.toString());
			e.printStackTrace();
		} catch (JSONException e) {
			System.out.println("class: SprintWebServiceController, "
					+ "method: getAllSprint, " + "exception: " + e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			jsonString += "IOException";
			System.out.println("class: SprintWebServiceController, "
					+ "method: getAllSprint, " + "api:InformationDecoder, "
					+ "exception: " + e.toString());
			e.printStackTrace();
		}
		return jsonString;
	}

	/****
	 * 取得 project 中所有的 sprint 包含 story 和 task
	 * http://IP:8080/ezScrum/web-service/
	 * {projectName}/sprint/{sprintId}/all?username={userName}&password={password}
	 * 
	 * @return
	 */
	@GET
	@Path("{sprintId}/all")
	@Produces(MediaType.APPLICATION_JSON)
	public String getSprintWithAllItem(
			@PathParam("projectName") String projectName,
			@PathParam("sprintId") String sprintId,
			@QueryParam("username") String username,
			@QueryParam("password") String password) {
		String sprintJson = "";
		InformationDecoder decoder = new InformationDecoder();
		try {
			decoder.decode(username, password, projectName);
			AccountObject userObject = new AccountObject(decoder.getDecodeUsername());
			userObject.setPassword(decoder.getDecodePwd());
			mSprintPlanWebService = new SprintPlanWebService(userObject,
					decoder.getDecodeProjectName());
			sprintJson = mSprintPlanWebService.getSprintWithAllItem(sprintId);
		} catch (IOException e) {
			System.out
					.println("class: SprintWebServiceController, "
							+ "method: getSprintWithAllItem, "
							+ "api:InformationDecoder, " + "exception: "
							+ e.toString());
			e.printStackTrace();
		} catch (LogonException e) {
			System.out.println("class: SprintWebServiceController, "
					+ "method: getSprintWithAllItem, " + "exception: "
					+ e.toString());
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("class: SprintWebServiceController, "
					+ "method: getSprintWithAllItem, " + "exception: "
					+ e.toString());
			e.printStackTrace();
		}
		return sprintJson;
	}

	/****
	 * 取得 project 中所有的 sprint 包含 story 和 task
	 * http://IP:8080/ezScrum/web-service/
	 * {projectName}/sprint/all/all?username={userName}&password={password}
	 * 
	 * @return
	 */
	@GET
	@Path("/all/all")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllSprintWithAllItem(
			@PathParam("projectName") String projectName,
			@QueryParam("username") String username,
			@QueryParam("password") String password) {
		String sprintJson = "";
		InformationDecoder decoder = new InformationDecoder();
		try {
			decoder.decode(username, password, projectName);
			AccountObject userObject = new AccountObject(decoder.getDecodeUsername());
			userObject.setPassword(decoder.getDecodePwd());
			mSprintPlanWebService = new SprintPlanWebService(userObject,
					decoder.getDecodeProjectName());
			sprintJson = mSprintPlanWebService.getSprintWithAllItem();
		} catch (IOException e) {
			System.out
					.println("class: SprintWebServiceController, "
							+ "method: getSprintWithAllItem, "
							+ "api:InformationDecoder, " + "exception: "
							+ e.toString());
			e.printStackTrace();
		} catch (LogonException e) {
			System.out.println("class: SprintWebServiceController, "
					+ "method: getSprintWithAllItem, " + "exception: "
					+ e.toString());
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("class: SprintWebServiceController, "
					+ "method: getSprintWithAllItem, " + "exception: "
					+ e.toString());
			e.printStackTrace();
		}
		return sprintJson;
	}

}
