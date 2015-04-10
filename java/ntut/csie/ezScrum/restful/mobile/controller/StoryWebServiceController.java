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

@Path("{projectName}/story/")
public class StoryWebServiceController {
	StoryWebService mStoryWebService;

	/****
	 * 建立新的 story
	 * http://IP:8080/ezScrum/web-service/{projectName}/story/create?userName
	 * ={userName}&password={password}
	 * 
	 * @return
	 */
	@POST
	@Path("create")
	@Produces(MediaType.APPLICATION_JSON)
	public String createStory(@PathParam("projectName") String projectName,
			@QueryParam("username") String username,
			@QueryParam("password") String password, String storyJson) {
		String responseString = "";
		InformationDecoder decoder = new InformationDecoder();
		try {
			decoder.decode(username, password, projectName);
			mStoryWebService = new StoryWebService(decoder.getDecodeUsername(),
					decoder.getDecodePwd(), decoder.getDecodeProjectName());
			responseString = mStoryWebService.createStory(storyJson);
		} catch (JSONException e) {
			responseString += "JSONException";
			System.out.println("class: StoryWebServiceController, "
					+ "method: createStory, " + "exception: " + e.toString());
			e.printStackTrace();
		} catch (LogonException e) {
			responseString += "LogonException";
			System.out.println("class: StoryWebServiceController, "
					+ "method: createStory, " + "exception: " + e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			responseString += "IOException";
			e.printStackTrace();
			System.out.println("class: StoryWebServiceController, "
					+ "method: createStory, " + "api:InformationDecoder, "
					+ "exception: " + e.toString());
		}
		return responseString;
	}

	/****
	 * 更新 story
	 * http://IP:8080/ezScrum/web-service/{projectName}/story/update?userName
	 * ={userName}&password={password}
	 * 
	 * @return
	 */
	@PUT
	@Path("update")
	@Produces(MediaType.APPLICATION_JSON)
	public String updateStory(@PathParam("projectName") String projectName,
			@QueryParam("username") String username,
			@QueryParam("password") String password, String storyJson) {
		String storyAfterEdit = "";
		InformationDecoder decoder = new InformationDecoder();
		try {
			decoder.decode(username, password, projectName);
			mStoryWebService = new StoryWebService(decoder.getDecodeUsername(),
					decoder.getDecodePwd(), decoder.getDecodeProjectName());
			storyAfterEdit = mStoryWebService.updateStory(storyJson);
		} catch (LogonException e) {
			storyAfterEdit += "LogonException";
			System.out.println("class: ProductBacklogWebServiceController, "
					+ "method: updateStory, " + "exception: " + e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			storyAfterEdit += "IOException";
			System.out.println("class: ProductBacklogWebServiceController, "
					+ "method: updateStory, " + "api:InformationDecoder, "
					+ "exception: " + e.toString());
			e.printStackTrace();
		} catch (JSONException e) {
			storyAfterEdit += "JSONException";
			System.out.println("class: ProductBacklogWebServiceController, "
					+ "method: updateStory, " + "api:InformationDecoder, "
					+ "exception: " + e.toString());
			e.printStackTrace();
		}
		return storyAfterEdit;
	}

	/****
	 * 取得 story 中所有的 task
	 * http://IP:8080/ezScrum/web-service/{projectName}/story/{storyID
	 * }/tasks?username={userName}&password={password}
	 * 
	 * @return
	 */
	@GET
	@Path("{storyId}/tasks")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTasksInStory(@PathParam("projectName") String projectName,
			@PathParam("storyId") long storyId,
			@QueryParam("username") String username,
			@QueryParam("password") String password) {
		String jsonString = "";
		InformationDecoder decoder = new InformationDecoder();
		try {
			decoder.decode(username, password);
			decoder.decodeProjectName(projectName);
			mStoryWebService = new StoryWebService(decoder.getDecodeUsername(),
					decoder.getDecodePwd(), decoder.getDecodeProjectName());
			jsonString = mStoryWebService.getTasksInStory(storyId);
		} catch (LogonException e) {
			System.out
					.println("class: StoryWebServiceController, "
							+ "method: getTaskInStory, " + "exception: "
							+ e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			jsonString += "IOException";
			System.out.println("class: StoryWebServiceController, "
					+ "method: getTaskInStory, " + "api:InformationDecoder, "
					+ "exception: " + e.toString());
			e.printStackTrace();
		}
		return jsonString;
	}

	/****
	 * 將已經存在的 task 加入 story
	 * http://IP:8080/ezScrum/web-service/{projectName}/story/
	 * {storyID}/add-existed-task?username={userName}&password={password}
	 * 
	 * @return
	 */
	@POST
	@Path("{storyId}/add-existed-task")
	@Produces(MediaType.APPLICATION_JSON)
	public void addExistedTask(@PathParam("projectName") String projectName,
			@PathParam("storyId") long storyId,
			@QueryParam("username") String username,
			@QueryParam("password") String password, String tasksIdJson) {
		InformationDecoder decoder = new InformationDecoder();
		try {
			decoder.decode(username, password);
			decoder.decodeProjectName(projectName);
			mStoryWebService = new StoryWebService(decoder.getDecodeUsername(),
					decoder.getDecodePwd(), decoder.getDecodeProjectName());
			mStoryWebService.addExistedTask(storyId, tasksIdJson);
		} catch (LogonException e) {
			System.out
					.println("class: StoryWebServiceController, "
							+ "method: addExistedTask, " + "exception: "
							+ e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("class: StoryWebServiceController, "
					+ "method: addExistedTask, " + "api:InformationDecoder, "
					+ "exception: " + e.toString());
			e.printStackTrace();
		}
	}
}
