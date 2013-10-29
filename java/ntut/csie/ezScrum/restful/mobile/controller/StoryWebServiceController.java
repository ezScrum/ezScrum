package ntut.csie.ezScrum.restful.mobile.controller;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import ntut.csie.ezScrum.restful.mobile.service.StoryWebService;
import ntut.csie.ezScrum.restful.mobile.support.InformationDecoder;
import ntut.csie.jcis.account.core.LogonException;

import org.codehaus.jettison.json.JSONException;

@Path("{projectID}/story/")
public class StoryWebServiceController {
	StoryWebService mStoryWebService;

	/****
	 * 建立新的 story
	 * http://IP:8080/ezScrum/web-service/{projectID}/story/create?userName={userName}&password={password}
	 * @return
	 */
	@POST
	@Path("create")
	@Produces(MediaType.APPLICATION_JSON)
	public String createStory(@PathParam("projectID") String projectID, 
							  @QueryParam("userName") String username,
							  @QueryParam("password") String password, 
							  String storyJson) {
		String responseString = "";
		InformationDecoder decoder = new InformationDecoder();
		try {
			decoder.decode(username, password, projectID);
			mStoryWebService = new StoryWebService(decoder.getDecodeUserName(), decoder.getDecodePwd(), decoder.getDecodeProjectID());
			responseString = mStoryWebService.createStory(storyJson);
		} catch (JSONException e) {
			responseString += "JSONException";
			System.out.println(	"class: StoryWebServiceController, " +
								"method: createStory, " +
								"exception: "+ e.toString() );
			e.printStackTrace();
		} catch (LogonException e) {
			responseString += "LogonException";
			System.out.println(	"class: StoryWebServiceController, " +
								"method: createStory, " +
								"exception: "+ e.toString() );
			e.printStackTrace();
		} catch (IOException e) {
			responseString += "IOException";
			e.printStackTrace();
			System.out.println(	"class: StoryWebServiceController, " +
					"method: createStory, " +
					"api:InformationDecoder, " +
					"exception: "+ e.toString() );
		}
		return responseString;
	}

	/****
	 * 更新 story
	 * http://IP:8080/ezScrum/web-service/{projectID}/story/update?userName={userName}&password={password}
	 * @return
	 */
	@PUT
	@Path("update")
	@Produces("application/json")
	public String updateStory(@PathParam("projectID") String projectID, 
							  @QueryParam("userName") String username,
							  @QueryParam("password") String password, 
							  String storyJson) {
		String storyAfterEdit = "";
		InformationDecoder decoder = new InformationDecoder();
		try{
			decoder.decode(username, password, projectID);
			mStoryWebService = new StoryWebService(decoder.getDecodeUserName(), decoder.getDecodePwd(), decoder.getDecodeProjectID());
			storyAfterEdit = mStoryWebService.updateStory(storyJson);
		} catch (LogonException e) {
			storyAfterEdit += "LogonException";
			System.out.println(	"class: ProductBacklogWebServiceController, " +
								"method: updateStory, " +
								"exception: "+ e.toString() );
			e.printStackTrace();
		} catch (IOException e) {
			storyAfterEdit += "IOException";
			System.out.println(	"class: ProductBacklogWebServiceController, " +
								"method: updateStory, " +
								"api:InformationDecoder, " +
								"exception: "+ e.toString() );
			e.printStackTrace();
		}
		return storyAfterEdit;
	}
	
	/****
	 * 取得 story 中所有的 task 
	 * http://IP:8080/ezScrum/web-service/{projectID}/story/{storyID}/tasks?userName={userName}&password={password}
	 * @return
	 */
	@GET
	@Path("{storyID}/tasks")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTaskInStory(@PathParam("projectID") String projectID, 
								 @PathParam("storyID") String storyID, 
								 @QueryParam("userName") String username,
								 @QueryParam("password") String password) {
		String jsonString = "";
		InformationDecoder decoder = new InformationDecoder();
		try {
			decoder.decode(username, password);
			decoder.decodeProjectID(projectID);
			mStoryWebService = new StoryWebService(decoder.getDecodeUserName(), decoder.getDecodePwd(), decoder.getDecodeProjectID());
			jsonString = mStoryWebService.getTaskInStory(storyID);
		} catch (LogonException e) {
			System.out.println(	"class: StoryWebServiceController, " + 
								"method: getTaskInStory, " + 
								"exception: " + e.toString() );
			e.printStackTrace();
		} catch (JSONException e) {
			System.out.println(	"class: StoryWebServiceController, " + 
								"method: getTaskInStory, " + 
								"exception: " + e.toString() );
			e.printStackTrace();
		} catch (IOException e) {
			jsonString += "IOException";
			System.out.println(	"class: StoryWebServiceController, " +
								"method: getTaskInStory, " +
								"api:InformationDecoder, " +
								"exception: "+ e.toString() );
			e.printStackTrace();
		}
		return jsonString;
	}

	/****
	 * 將已經存在的 task 加入 story
	 * http://IP:8080/ezScrum/web-service/{projectID}/story/{storyID}/add-existed-task?userName={userName}&password={password}
	 * @return
	 */
	@POST
	@Path("{storyID}/add-existed-task")
	@Produces(MediaType.APPLICATION_JSON)
	public void addExistedTask(@PathParam("projectID") String projectID, 
								 @PathParam("storyID") String storyID, 
								 @QueryParam("userName") String username,
								 @QueryParam("password") String password,
								 String taskIDsJson) {
		InformationDecoder decoder = new InformationDecoder();
		try {
			decoder.decode(username, password);
			decoder.decodeProjectID(projectID);
			mStoryWebService = new StoryWebService(decoder.getDecodeUserName(), decoder.getDecodePwd(), decoder.getDecodeProjectID());
			mStoryWebService.addExistedTask(storyID, taskIDsJson);
		} catch (LogonException e) {
			System.out.println(	"class: StoryWebServiceController, " + 
								"method: addExistedTask, " + 
								"exception: " + e.toString() );
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(	"class: StoryWebServiceController, " +
								"method: addExistedTask, " +
								"api:InformationDecoder, " +
								"exception: "+ e.toString() );
			e.printStackTrace();
		}
	}
}
