package ntut.csie.ezScrum.restful.mobile.controller;

import java.io.IOException;

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
import ntut.csie.ezScrum.web.dataObject.UserObject;
import ntut.csie.jcis.account.core.LogonException;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.google.gson.Gson;

@Path("{projectID}/sprint/")
public class SprintPlanWebServiceController {
//	SprintWebService mSprintWebService;
	SprintPlanWebService mSprintPlanWebService;
	
	/**	Create Sprint
	 *  http://IP:8080/ezScrum/web-service/{projectID}/sprint/create?userName={userName}&password={password}
	 * **/
	@POST
	@Path("create")
	@Produces(MediaType.APPLICATION_JSON)
	public String createSprint(@PathParam("projectID") String projectID, 
							   @QueryParam("userName") String userName, 
							   @QueryParam("password") String password, 
							   JSONObject sprintJson) {
		Gson gson = new Gson();
		String responseString = "";
		try{
			SprintObject sprintObject = gson.fromJson(sprintJson.toString(), SprintObject.class);
			InformationDecoder decoder = new InformationDecoder();
			decoder.decode(userName, password, projectID);
			// 使用者帳號
			UserObject userObject = new UserObject();
			userObject.setAccount(decoder.getDecodeUserName());
			userObject.setPassword(decoder.getDecodePwd());
			mSprintPlanWebService = new SprintPlanWebService(userObject, decoder.getDecodeProjectID());
			mSprintPlanWebService.createSprint(sprintObject);
			responseString += mSprintPlanWebService.getRESTFulResponseString();
		} catch (JSONException e) {
			responseString += "JSONException";
			System.out.println(	"class: SprintWebServiceController, " +
								"method: createSprint, " +
								"exception: "+ e.toString() );
			e.printStackTrace();
		} catch (LogonException e) {
			responseString += "LogonException";
			System.out.println(	"class: SprintWebServiceController, " +
								"method: createSprint, " +
								"exception: "+ e.toString() );
			e.printStackTrace();
		} catch (IOException e) {
			responseString += "IOException";
			System.out.println(	"class: SprintWebServiceController, " +
								"method: createSprint, " +
								"api:InformationDecoder, " +
								"exception: "+ e.toString() );
			e.printStackTrace();
		}
		return responseString;
	}

	/**	Delete Sprint
	 * 	http://IP:8080/ezScrum/web-service/{projectID}/sprint/delete/{sprintID}?userName={userName}&password={password}
	 * **/
	@DELETE
	@Path("delete/{sprintID}")
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteSprint(@PathParam("projectID") String projectID, 
							   @PathParam("sprintID") String sprintID, 
							   @QueryParam("userName") String userName, 
							   @QueryParam("password") String password) {
		String responseString = "";
		try{
			InformationDecoder decoder = new InformationDecoder();
			decoder.decode(userName, password, projectID);
			UserObject userObject = new UserObject();
			userObject.setAccount(decoder.getDecodeUserName());
			userObject.setPassword(decoder.getDecodePwd());
			mSprintPlanWebService = new SprintPlanWebService(userObject, decoder.getDecodeProjectID());
			mSprintPlanWebService.deleteSprint(sprintID);
			responseString += mSprintPlanWebService.getRESTFulResponseString();
		} catch (JSONException e) {
			responseString += "JSONException";
			System.out.println(	"class: SprintWebServiceController, " +
								"method: deleteSprint, " +
								"exception: "+ e.toString() );
			e.printStackTrace();
		} catch (LogonException e) {
			responseString += "LogonException";
			System.out.println(	"class: SprintWebServiceController, " +
								"method: deleteSprint, " +
								"exception: "+ e.toString() );
			e.printStackTrace();
		} catch (IOException e) {
			responseString += "IOException";
			System.out.println(	"class: SprintWebServiceController, " +
								"method: deleteSprint, " +
								"api:InformationDecoder, " +
								"exception: "+ e.toString() );
			e.printStackTrace();
		}
		return responseString;
	}	
	
	/**	Update Sprint
	 * 	http://IP:8080/ezScrum/web-service/{projectID}/sprint/update?userName={userName}&password={password}
	 * **/
	@PUT
	@Path("update")
	@Produces(MediaType.APPLICATION_JSON)
	public String updateSprint(@PathParam("projectID") String projectID, 
							   @QueryParam("userName") String userName, 
							   @QueryParam("password") String password, 
							   String sprintJson) {
		Gson gson = new Gson();
		String responseString = "";
		try{
			SprintObject sprint = gson.fromJson(sprintJson, SprintObject.class);
			InformationDecoder decoder = new InformationDecoder();
			decoder.decode(userName, password, projectID);
			UserObject userObject = new UserObject();
			userObject.setAccount(decoder.getDecodeUserName());
			userObject.setPassword(decoder.getDecodePwd());
			mSprintPlanWebService = new SprintPlanWebService(userObject, decoder.getDecodeProjectID());
			mSprintPlanWebService.updateSprint(sprint);
			responseString += mSprintPlanWebService.getRESTFulResponseString();
		} catch (JSONException e) {
			responseString += "JSONException";
			System.out.println(	"class: SprintWebServiceController, " +
								"method: updateSprint, " +
								"exception: "+ e.toString() );
			e.printStackTrace();
		} catch (LogonException e) {
			responseString += "LogonException";
			System.out.println(	"class: SprintWebServiceController, " +
								"method: updateSprint, " +
								"exception: "+ e.toString() );
			e.printStackTrace();
		} catch (IOException e) {
			responseString += "IOException";
			System.out.println(	"class: SprintWebServiceController, " +
								"method: updateSprint, " +
								"api:InformationDecoder, " +
								"exception: "+ e.toString() );
			e.printStackTrace();
		}
		return responseString;
	}

	/****
	 * 取得 project 中所有的 sprint 
	 * http://IP:8080/ezScrum/web-service/{projectID}/sprint/all?userName={userName}&password={password}
	 * @return
	 */
	@GET
	@Path("all")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllSprint(@PathParam("projectID") String projectID,
							   @QueryParam("userName") String userName, 
							   @QueryParam("password") String password) {
		String jsonString = "";
		InformationDecoder decoder = new InformationDecoder();
		UserObject userObject = new UserObject();
		try {
			decoder.decode(userName, password, projectID);
			userObject.setAccount(decoder.getDecodeUserName());
			userObject.setPassword(decoder.getDecodePwd());
			mSprintPlanWebService = new SprintPlanWebService(userObject, decoder.getDecodeProjectID());
			jsonString = mSprintPlanWebService.getAllSprint();
		} catch (LogonException e) {
			System.out.println(	"class: SprintWebServiceController, " + 
								"method: getAllSprint, " + 
								"exception: " + e.toString() );
			e.printStackTrace();
		} catch (JSONException e) {
			System.out.println(	"class: SprintWebServiceController, " + 
								"method: getAllSprint, " + 
								"exception: " + e.toString() );
			e.printStackTrace();
		} catch (IOException e) {
			jsonString += "IOException";
			System.out.println(	"class: SprintWebServiceController, " +
								"method: getAllSprint, " +
								"api:InformationDecoder, " +
								"exception: "+ e.toString() );
			e.printStackTrace();
		}
		return jsonString;
	}


	/****
	 * 取得 project 中所有的 sprint 包含 story 和 task
	 * http://IP:8080/ezScrum/web-service/{projectID}/sprint/{sprintID}/all?userName={userName}&password={password}
	 * @return
	 */
	@GET
	@Path("{sprintID}/all")
	@Produces(MediaType.APPLICATION_JSON)
	public String getSprintWithAllItem(@PathParam("projectID") String projectID, 
									   @PathParam("sprintID") String sprintID, 
									   @QueryParam("userName") String username,
									   @QueryParam("password") String password) {
		String sprintJson = "";
		InformationDecoder decoder = new InformationDecoder();
		try {
			decoder.decode(username, password, projectID);
			UserObject userObject = new UserObject();
			userObject.setAccount(decoder.getDecodeUserName());
			userObject.setPassword(decoder.getDecodePwd());
			mSprintPlanWebService = new SprintPlanWebService(userObject, decoder.getDecodeProjectID());
			sprintJson = mSprintPlanWebService.getSprintWithAllItem(sprintID);
		} catch (IOException e) {
			System.out.println(	"class: SprintWebServiceController, " +
					"method: getSprintWithAllItem, " +
					"api:InformationDecoder, " +
					"exception: "+ e.toString() );
			e.printStackTrace();
		} catch (LogonException e) {
			System.out.println(	"class: SprintWebServiceController, " + 
					"method: getSprintWithAllItem, " + 
					"exception: " + e.toString() );
			e.printStackTrace();
		}
		return sprintJson;
	}
	
}
