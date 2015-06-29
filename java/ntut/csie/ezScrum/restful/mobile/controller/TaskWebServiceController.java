package ntut.csie.ezScrum.restful.mobile.controller;

import java.io.IOException;
import java.sql.SQLException;

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
import ntut.csie.jcis.account.core.LogonException;

import org.codehaus.jettison.json.JSONException;

@Path("{projectName}/task/")
public class TaskWebServiceController {
	TaskWebService mTaskWebService;

	/****
	 * 修改 task 需 post task json string
	 * http://IP:8080/ezScrum/web-service/{projectName
	 * }/task/update?username={userName}&password={password}
	 * 
	 * @return
	 */
	@POST
	@Path("update")
	@Produces(MediaType.APPLICATION_JSON)
	public String updateTask(@PathParam("projectName") String projectName,
			@QueryParam("username") String username,
			@QueryParam("password") String password, String taskJson) {
		System.out.println("task json : " + taskJson);
		InformationDecoder decoder = new InformationDecoder();
		String result = "false";
		try {
			decoder.decode(username, password, projectName);
			mTaskWebService = new TaskWebService(decoder.getDecodeUsername(),
					decoder.getDecodePwd(), decoder.getDecodeProjectName());
			result = mTaskWebService.updateTask(taskJson);
		} catch (IOException e) {
			System.out.println("class: TaskWebServiceController, "
					+ "method: updateTask, " + "exception: " + e.toString());
			e.printStackTrace();
		} catch (LogonException e) {
			System.out.println("class: TaskWebServiceController, "
					+ "method: updateTask, " + "exception: " + e.toString());
			e.printStackTrace();
		} catch (JSONException e) {
			System.out.println("class: TaskWebServiceController, "
					+ "method: updateTask, " + "exception: " + e.toString());
			e.printStackTrace();
		}
		return result;
	}

	/****
	 * 取得所有已存在的 task 沒被 assign 到 story 的 task
	 * http://IP:8080/ezScrum/web-service/
	 * {projectName}/task/existed?username={userName}&password={password}
	 * 
	 * @return
	 */
	@GET
	@Path("existed")
	@Produces(MediaType.APPLICATION_JSON)
	public String getExistedTask(@PathParam("projectName") String projectName,
			@QueryParam("username") String username,
			@QueryParam("password") String password) {
		String existedTaskListJson = "";
		InformationDecoder decoder = new InformationDecoder();
		try {
			decoder.decode(username, password);
			decoder.decodeProjectName(projectName);
			mTaskWebService = new TaskWebService(decoder.getDecodeUsername(),
					decoder.getDecodePwd(), decoder.getDecodeProjectName());
			existedTaskListJson = mTaskWebService.getTasksWithNoParent();
		} catch (LogonException e) {
			System.out
					.println("class: TaskWebServiceController, "
							+ "method: getTaskInStory, " + "exception: "
							+ e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			existedTaskListJson += "IOException";
			System.out.println("class: TaskWebServiceController, "
					+ "method: getTaskInStory, " + "api:InformationDecoder, "
					+ "exception: " + e.toString());
			e.printStackTrace();
		} catch (SQLException e) {
			existedTaskListJson += "SQLException";
			System.out.println("class: TaskWebServiceController, "
					+ "method: getTaskInStory, " + "api:InformationDecoder, "
					+ "exception: " + e.toString());
			e.printStackTrace();
		}
		return existedTaskListJson;
	}

	/****
	 * 將新的 task 加入 story
	 * http://IP:8080/ezScrum/web-service/{projectName}/task/create
	 * /{storyId}?username={userName}&password={password}
	 * 
	 * @return
	 */
	@POST
	@Path("create/{storyId}")
	@Produces(MediaType.APPLICATION_JSON)
	public String createTaskInStory(@PathParam("projectName") String projectName,
			@PathParam("storyId") long storyId,
			@QueryParam("username") String username,
			@QueryParam("password") String password, String taskJson) {
		InformationDecoder decoder = new InformationDecoder();
		String newTaskId = "";
		try {
			decoder.decode(username, password);
			decoder.decodeProjectName(projectName);
			mTaskWebService = new TaskWebService(decoder.getDecodeUsername(),
					decoder.getDecodePwd(), decoder.getDecodeProjectName());
			newTaskId = mTaskWebService.createTaskInStory(storyId, taskJson);
		} catch (IOException e) {
			System.out
					.println("class: TaskWebServiceController, "
							+ "method: createTaskInStory, "
							+ "api:InformationDecoder, " + "exception: "
							+ e.toString());
			e.printStackTrace();
		} catch (LogonException e) {
			System.out.println("class: TaskWebServiceController, "
					+ "method: createTaskInStory, " + "exception: "
					+ e.toString());
			e.printStackTrace();
		} catch (JSONException e) {
			System.out.println("class: TaskWebServiceController, "
					+ "method: createTaskInStory, " + "exception: "
					+ e.toString());
			e.printStackTrace();
		}
		return newTaskId;
	}

	/****
	 * 刪除 task
	 * http://IP:8080/ezScrum/web-service/{projectName}/task/delete/{taskId
	 * }/from/{storyId}?username={userName}&password={password}
	 * 
	 * @return
	 */
	@DELETE
	@Path("delete/{taskId}/from/{storyId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteTask(@PathParam("projectName") String projectName,
			@PathParam("taskId") String taskId,
			@PathParam("storyId") String storyId,
			@QueryParam("username") String username,
			@QueryParam("password") String password) {
		InformationDecoder decoder = new InformationDecoder();
		try {
			decoder.decode(username, password);
			decoder.decodeProjectName(projectName);
			mTaskWebService = new TaskWebService(decoder.getDecodeUsername(),
					decoder.getDecodePwd(), decoder.getDecodeProjectName());
			mTaskWebService.deleteTask(taskId, storyId);
		} catch (IOException e) {
			System.out.println("class: TaskWebServiceController, "
					+ "method: deleteTask, " + "api:InformationDecoder, "
					+ "exception: " + e.toString());
			e.printStackTrace();
		} catch (LogonException e) {
			System.out.println("class: TaskWebServiceController, "
					+ "method: deleteTask, " + "exception: " + e.toString());
			e.printStackTrace();
		}
	}

	/****
	 * 將 task 從 story 中移除
	 * http://IP:8080/ezScrum/web-service/{projectName}/task/drop
	 * /{taskId}/from/{storyId}?username={userName}&password={password}
	 * 
	 * @return
	 */
	@DELETE
	@Path("drop/{taskId}/from/{storyId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void dropTask(@PathParam("projectName") String projectName,
			@PathParam("taskId") String taskId,
			@PathParam("storyId") String storyId,
			@QueryParam("username") String username,
			@QueryParam("password") String password) {
		InformationDecoder decoder = new InformationDecoder();
		try {
			decoder.decode(username, password);
			decoder.decodeProjectName(projectName);
			mTaskWebService = new TaskWebService(decoder.getDecodeUsername(),
					decoder.getDecodePwd(), decoder.getDecodeProjectName());
			mTaskWebService.dropTask(taskId, storyId);
		} catch (IOException e) {
			System.out.println("class: TaskWebServiceController, "
					+ "method: dropTask, " + "api:InformationDecoder, "
					+ "exception: " + e.toString());
			e.printStackTrace();
		} catch (LogonException e) {
			System.out.println("class: TaskWebServiceController, "
					+ "method: dropTask, " + "exception: " + e.toString());
			e.printStackTrace();
		}
	}
}
