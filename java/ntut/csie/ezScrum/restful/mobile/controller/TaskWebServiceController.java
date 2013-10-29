package ntut.csie.ezScrum.restful.mobile.controller;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import ntut.csie.ezScrum.restful.mobile.service.TaskWebService;
import ntut.csie.ezScrum.restful.mobile.support.InformationDecoder;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.jcis.account.core.LogonException;

import com.google.gson.Gson;

@Path("{projectID}/task/")
public class TaskWebServiceController {
	TaskWebService mTaskWebService;
	
	/****
	 * 修改 task
	 * 需 post task json string
	 * http://IP:8080/ezScrum/web-service/{projectID}/task/update?userName={userName}&password={password}
	 * @return
	 */
	@POST
	@Path("update")
	@Produces(MediaType.APPLICATION_JSON)
	public String updateTask(@PathParam("projectID") String projectID,
							 @QueryParam("userName") String username,
							 @QueryParam("password") String password, 
							 String taskJson) {
		System.out.println("task json : " + taskJson);
		InformationDecoder decoder = new InformationDecoder();
		try {
			decoder.decode(username, password, projectID);
			mTaskWebService = new TaskWebService(decoder.getDecodeUserName(), decoder.getDecodePwd(), decoder.getDecodeProjectID());
		} catch (IOException e) {
			System.out.println(	"class: TaskWebServiceController, " + 
					"method: updateTask, " + 
					"exception: " + e.toString() );
			e.printStackTrace();
		} catch (LogonException e) {
			System.out.println(	"class: TaskWebServiceController, " + 
					"method: updateTask, " + 
					"exception: " + e.toString() );
			e.printStackTrace();
		}
		return mTaskWebService.updateTask(taskJson);
	}
	
	/****
	 * 取得所有已存在的 task 沒被 assign 到 story 的 task 
	 * http://IP:8080/ezScrum/web-service/{projectID}/task/existed?userName={userName}&password={password}
	 * @return
	 */
	@GET
	@Path("existed")
	@Produces(MediaType.APPLICATION_JSON)
	public String getExistedTask(@PathParam("projectID") String projectID,
								 @QueryParam("userName") String username,
								 @QueryParam("password") String password) {
		String existedTaskListJson = "";
		InformationDecoder decoder = new InformationDecoder();
		try {
			decoder.decode(username, password);
			decoder.decodeProjectID(projectID);
			mTaskWebService = new TaskWebService(decoder.getDecodeUserName(), decoder.getDecodePwd(), decoder.getDecodeProjectID());
			existedTaskListJson = mTaskWebService.getExistedTask();
		} catch (LogonException e) {
			System.out.println(	"class: TaskWebServiceController, " + 
								"method: getTaskInStory, " + 
								"exception: " + e.toString() );
			e.printStackTrace();
		} catch (IOException e) {
			existedTaskListJson += "IOException";
			System.out.println(	"class: TaskWebServiceController, " +
								"method: getTaskInStory, " +
								"api:InformationDecoder, " +
								"exception: "+ e.toString() );
			e.printStackTrace();
		}
		return existedTaskListJson;
	}
	
	/****
	 * 將新的 task 加入 story
	 * http://IP:8080/ezScrum/web-service/{projectID}/task/create/{storyID}?userName={userName}&password={password}
	 * @return
	 */
	@POST
	@Path("create/{storyID}")
	@Produces(MediaType.APPLICATION_JSON)
	public String createTaskInStory(@PathParam("projectID") String projectID, 
									@PathParam("storyID") String storyID, 
									@QueryParam("userName") String username,
									@QueryParam("password") String password, 
									String taskJson) {
		Gson gson = new Gson();
		InformationDecoder decoder = new InformationDecoder();
		String newTaskId = "";
		TaskObject task = gson.fromJson(taskJson, TaskObject.class);
		try {
			decoder.decode(username, password);
			decoder.decodeProjectID(projectID);
			mTaskWebService = new TaskWebService(decoder.getDecodeUserName(), decoder.getDecodePwd(), decoder.getDecodeProjectID());
			newTaskId = mTaskWebService.createTaskInStory(storyID, task);
		} catch (IOException e) {
			System.out.println(	"class: TaskWebServiceController, " +
					"method: createTaskInStory, " +
					"api:InformationDecoder, " +
					"exception: "+ e.toString() );
			e.printStackTrace();
		} catch (LogonException e) {
			System.out.println(	"class: TaskWebServiceController, " + 
					"method: createTaskInStory, " + 
					"exception: " + e.toString() );
			e.printStackTrace();
		}
		return newTaskId;
	}
	
	/****
	 * 刪除 task
	 * http://IP:8080/ezScrum/web-service/{projectID}/task/delete/{taskID}/from/{storyID}?userName={userName}&password={password}
	 * @return
	 */
	@DELETE
	@Path("delete/{taskID}/from/{storyID}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteTask(@PathParam("projectID") String projectID, 
						   @PathParam("taskID") String taskID, 
						   @PathParam("storyID") String storyID, 
						   @QueryParam("userName") String username,
						   @QueryParam("password") String password) {
		InformationDecoder decoder = new InformationDecoder();
		try {
			decoder.decode(username, password);
			decoder.decodeProjectID(projectID);
			mTaskWebService = new TaskWebService(decoder.getDecodeUserName(), decoder.getDecodePwd(), decoder.getDecodeProjectID());
			mTaskWebService.deleteTask(taskID, storyID);
		} catch (IOException e) {
			System.out.println(	"class: TaskWebServiceController, " +
					"method: deleteTask, " +
					"api:InformationDecoder, " +
					"exception: "+ e.toString() );
			e.printStackTrace();
		} catch (LogonException e) {
			System.out.println(	"class: TaskWebServiceController, " + 
					"method: deleteTask, " + 
					"exception: " + e.toString() );
			e.printStackTrace();
		}
	}

	/****
	 * 將 task 從 story 中移除
	 * http://IP:8080/ezScrum/web-service/{projectID}/task/drop/{taskID}/from/{storyID}?userName={userName}&password={password}
	 * @return
	 */
	@DELETE
	@Path("drop/{taskID}/from/{storyID}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void dropTask(@PathParam("projectID") String projectID, 
						 @PathParam("taskID") String taskID, 
						 @PathParam("storyID") String storyID, 
						 @QueryParam("userName") String username,
						 @QueryParam("password") String password) {
		InformationDecoder decoder = new InformationDecoder();
		try {
			decoder.decode(username, password);
			decoder.decodeProjectID(projectID);
			mTaskWebService = new TaskWebService(decoder.getDecodeUserName(), decoder.getDecodePwd(), decoder.getDecodeProjectID());
			mTaskWebService.dropTask(taskID, storyID);
		} catch (IOException e) {
			System.out.println(	"class: TaskWebServiceController, " +
					"method: dropTask, " +
					"api:InformationDecoder, " +
					"exception: "+ e.toString() );
			e.printStackTrace();
		} catch (LogonException e) {
			System.out.println(	"class: TaskWebServiceController, " + 
					"method: dropTask, " + 
					"exception: " + e.toString() );
			e.printStackTrace();
		}
	}
}
