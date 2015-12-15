package ntut.csie.ezScrum.restful.dataMigration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import ntut.csie.ezScrum.dao.ProjectDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ExportJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ProjectJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ResponseJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.SprintJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.StoryJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.TaskJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.UnplanJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.support.ResponseFactory;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.databaseEnum.ProjectEnum;
import ntut.csie.ezScrum.web.databaseEnum.SprintEnum;
import ntut.csie.ezScrum.web.databaseEnum.StoryEnum;
import ntut.csie.ezScrum.web.databaseEnum.TaskEnum;
import ntut.csie.ezScrum.web.databaseEnum.UnplanEnum;

@Path("dataMigration")
public class IntegratedRESTfulApi {
	private Client mClient;
	private static String BASE_URL = "http://localhost:8080/ezScrum/resource/";

	@POST
	@Path("/projects")
	@Produces(MediaType.APPLICATION_JSON)
	public Response importProjectsJSON(String entity) throws IOException {
		// Get Client
		mClient = ClientBuilder.newClient();
		// Import JSON Data
		JSONObject importDataJSON = null;
		// 檢查JSON format
		try {
			importDataJSON = new JSONObject(entity);
		} catch (JSONException e) {
			return ResponseFactory.getResponse(Response.Status.BAD_REQUEST, ResponseJSONEnum.ERROR_BAD_REQUEST_MEESSAGE, "");
		}
		// Clean all data
		cleanOldEzscrumData();
		// 檢查 Checksum
		// TODO
		// 檢查版本號
		// TODO
		///// 資料擷取 /////
		// Create Accounts
		try {
			JSONArray accountJSONArray = importDataJSON.getJSONArray(ExportJSONEnum.ACCOUNTS);
			// Create Account
			for (int i = 0; i < accountJSONArray.length(); i++) {
				JSONObject accountJSON = accountJSONArray.getJSONObject(i);
				mClient.target(BASE_URL)
				        .path("accounts")
				        .request()
				        .post(Entity.text(accountJSON.toString()));
				// TODO 紀錄結果

			}
		} catch (JSONException e) {
			return ResponseFactory.getResponse(Response.Status.BAD_REQUEST, ResponseJSONEnum.ERROR_BAD_REQUEST_MEESSAGE, "");
		}

		// Create Projects
		JSONArray projectJSONArray = null;
		try {
			projectJSONArray = importDataJSON.getJSONArray(ExportJSONEnum.PROJECTS);
			//// Create Project
			for (int i = 0; i < projectJSONArray.length(); i++) {
				JSONObject projectJSON = projectJSONArray.getJSONObject(i);
				Response response = mClient.target(BASE_URL)
				        .path("projects")
				        .request()
				        .post(Entity.text(projectJSON.toString()));
				// TODO 紀錄結果
				JSONObject responseJSON = new JSONObject(response.readEntity(String.class));
				JSONObject contentJSON = responseJSON.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT);
				long projectId = contentJSON.getLong(ProjectEnum.ID);

				// Update ScrumRoles
				JSONObject scrumRolesJSON = projectJSON.getJSONObject(ProjectJSONEnum.SCRUM_ROLES);
				response = mClient.target(BASE_URL)
				        .path("projects/" + projectId +
				                "/scrumroles")
				        .request()
				        .put(Entity.text(scrumRolesJSON.toString()));

				// Create ProjectRoles
				JSONArray projectRoleJSONArray = projectJSON.getJSONArray(ProjectJSONEnum.PROJECT_ROLES);
				for (int j = 0; j < projectRoleJSONArray.length(); j++) {
					JSONObject projectRoleJSON = projectRoleJSONArray.getJSONObject(j);
					response = mClient.target(BASE_URL)
					        .path("projects/" + projectId +
					                "/projectroles")
					        .request()
					        .post(Entity.text(projectRoleJSON.toString()));
				}

				// Create Tags
				JSONArray tagJSONArray = projectJSON.getJSONArray(ProjectJSONEnum.TAGS);
				for (int j = 0; j < tagJSONArray.length(); j++) {
					JSONObject tagJSON = tagJSONArray.getJSONObject(j);
					response = mClient.target(BASE_URL)
					        .path("projects/" + projectId +
					                "/tags")
					        .request()
					        .post(Entity.text(tagJSON.toString()));
				}

				//// Create Sprints
				JSONArray sprintJSONArray = projectJSON.getJSONArray(ProjectJSONEnum.SPRINTS);
				for (int j = 0; j < sprintJSONArray.length(); j++) {
					JSONObject sprintJSON = sprintJSONArray.getJSONObject(j);
					response = mClient.target(BASE_URL)
					        .path("projects/" + projectId +
					                "/sprints")
					        .request()
					        .post(Entity.text(sprintJSON.toString()));
					responseJSON = new JSONObject(response.readEntity(String.class));
					contentJSON = responseJSON.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT);
					// Get new SprintId
					long sprintId = contentJSON.getLong(SprintEnum.ID);
					// Put new SprintId into JSON
					sprintJSON.put(SprintJSONEnum.ID, sprintId);

					//// Create Stories
					JSONArray storyJSONArray = sprintJSON.getJSONArray(SprintJSONEnum.STORIES);
					for (int k = 0; k < storyJSONArray.length(); k++) {
						JSONObject storyJSON = storyJSONArray.getJSONObject(k);
						response = mClient.target(BASE_URL)
						        .path("projects/" + projectId +
						                "/sprints/" + sprintId +
						                "/stories")
						        .request()
						        .post(Entity.text(storyJSON.toString()));
						responseJSON = new JSONObject(response.readEntity(String.class));
						contentJSON = responseJSON.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT);
						// Get new StoryId
						long storyId = contentJSON.getLong(StoryEnum.ID);
						// Put new StoryId into JSON
						storyJSON.put(SprintJSONEnum.ID, storyId);

						// Add Tag to Story
						JSONArray tagInStoryJSONArray = storyJSON.getJSONArray(StoryJSONEnum.TAGS);
						for (int l = 0; l < tagInStoryJSONArray.length(); l++) {
							JSONObject tagJSON = tagInStoryJSONArray.getJSONObject(l);
							response = mClient.target(BASE_URL)
							        .path("projects/" + projectId +
							                "/sprints/" + sprintId +
							                "/stories/" + storyId +
							                "/tags")
							        .request()
							        .post(Entity.text(tagJSON.toString()));
						}

						// Add AttachFiles to Story
						JSONArray attachFilesInStoryJSONArray = storyJSON.getJSONArray(StoryJSONEnum.ATTACH_FILES);
						for (int l = 0; l < attachFilesInStoryJSONArray.length(); l++) {
							JSONObject attachFileJSON = attachFilesInStoryJSONArray.getJSONObject(l);
							response = mClient.target(BASE_URL)
							        .path("projects/" + projectId +
							                "/sprints/" + sprintId +
							                "/stories/" + storyId +
							                "/attachfiles")
							        .request()
							        .post(Entity.text(attachFileJSON.toString()));
						}

						//// Create Tasks
						JSONArray taskJSONArray = storyJSON.getJSONArray(StoryJSONEnum.TASKS);
						for (int m = 0; m < taskJSONArray.length(); m++) {
							JSONObject taskJSON = taskJSONArray.getJSONObject(m);
							response = mClient.target(BASE_URL)
							        .path("projects/" + projectId +
							                "/sprints/" + sprintId +
							                "/stories/" + storyId +
							                "/tasks")
							        .request()
							        .post(Entity.text(taskJSON.toString()));
							responseJSON = new JSONObject(response.readEntity(String.class));
							contentJSON = responseJSON.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT);
							// Get new TaskId
							long taskId = contentJSON.getLong(TaskEnum.ID);
							// Put new TaskId into JSON
							taskJSON.put(TaskJSONEnum.ID, taskId);

							// Add AttachFiles to Task
							JSONArray attachFilesInTaskJSONArray = taskJSON.getJSONArray(TaskJSONEnum.ATTACH_FILES);
							for (int n = 0; n < attachFilesInTaskJSONArray.length(); n++) {
								JSONObject attachFileJSON = attachFilesInTaskJSONArray.getJSONObject(n);
								response = mClient.target(BASE_URL)
								        .path("projects/" + projectId +
								                "/sprints/" + sprintId +
								                "/stories/" + storyId +
								                "/tasks/" + taskId +
								                "/attachfiles")
								        .request()
								        .post(Entity.text(attachFileJSON.toString()));
							}
						}
					}

					//// Create Unplans
					JSONArray unplanJSONArray = sprintJSON.getJSONArray(SprintJSONEnum.UNPLANS);
					for (int k = 0; k < unplanJSONArray.length(); k++) {
						JSONObject unplanJSON = unplanJSONArray.getJSONObject(k);
						response = mClient.target(BASE_URL)
						        .path("projects/" + projectId +
						                "/sprints/" + sprintId +
						                "/unplans")
						        .request()
						        .post(Entity.text(unplanJSON.toString()));
						responseJSON = new JSONObject(response.readEntity(String.class));
						contentJSON = responseJSON.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT);
						// Get new UnplanId
						long unplanId = contentJSON.getLong(UnplanEnum.ID);
						// Put new UnplanId into JSON
						unplanJSON.put(UnplanEnum.ID, unplanId);
					}

					//// Create Retrospectives
					JSONArray retrospectiveJSONArray = sprintJSON.getJSONArray(SprintJSONEnum.RETROSPECTIVES);
					for (int k = 0; k < retrospectiveJSONArray.length(); k++) {
						JSONObject retrospectiveJSON = retrospectiveJSONArray.getJSONObject(k);
						response = mClient.target(BASE_URL)
						        .path("projects/" + projectId +
						                "/sprints/" + sprintId +
						                "/retrospectives")
						        .request()
						        .post(Entity.text(retrospectiveJSON.toString()));
					}
				}

				//// Create Releases
				JSONArray releaseJSONArray = projectJSON.getJSONArray(ProjectJSONEnum.RELEASES);
				for (int j = 0; j < releaseJSONArray.length(); j++) {
					JSONObject releaseJSON = releaseJSONArray.getJSONObject(j);
					response = mClient.target(BASE_URL)
					        .path("projects/" + projectId +
					                "/releases")
					        .request()
					        .post(Entity.text(releaseJSON.toString()));
				}

				//// Create Dropped Stories
				JSONArray droppedStoryJSONArray = projectJSON.getJSONArray(ProjectJSONEnum.DROPPED_STORIES);
				for (int j = 0; j < droppedStoryJSONArray.length(); j++) {
					JSONObject droppedStoryJSON = droppedStoryJSONArray.getJSONObject(j);
					response = mClient.target(BASE_URL)
					        .path("projects/" + projectId +
					                "/stories")
					        .request()
					        .post(Entity.text(droppedStoryJSON.toString()));
					responseJSON = new JSONObject(response.readEntity(String.class));
					contentJSON = responseJSON.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT);
					// Get new StoryId
					long storyId = contentJSON.getLong(StoryEnum.ID);
					// Put new StoryId into JSON
					droppedStoryJSON.put(StoryJSONEnum.ID, storyId);

					// Add Tag to Story
					JSONArray tagInStoryJSONArray = droppedStoryJSON.getJSONArray(StoryJSONEnum.TAGS);
					for (int k = 0; k < tagInStoryJSONArray.length(); k++) {
						JSONObject tagJSON = tagInStoryJSONArray.getJSONObject(k);
						response = mClient.target(BASE_URL)
						        .path("projects/" + projectId +
						                "/stories/" + storyId +
						                "/tags")
						        .request()
						        .post(Entity.text(tagJSON.toString()));
					}

					// Add AttachFiles to Story
					JSONArray attachFilesInStoryJSONArray = droppedStoryJSON.getJSONArray(StoryJSONEnum.ATTACH_FILES);
					for (int k = 0; k < attachFilesInStoryJSONArray.length(); k++) {
						JSONObject attachFileJSON = attachFilesInStoryJSONArray.getJSONObject(k);
						response = mClient.target(BASE_URL)
						        .path("projects/" + projectId +
						                "/stories/" + storyId +
						                "/attachfiles")
						        .request()
						        .post(Entity.text(attachFileJSON.toString()));
					}

					//// Create Tasks in DroppedStory
					JSONArray taskJSONArray = droppedStoryJSON.getJSONArray(StoryJSONEnum.TASKS);
					for (int k = 0; k < taskJSONArray.length(); k++) {
						JSONObject taskJSON = taskJSONArray.getJSONObject(k);
						response = mClient.target(BASE_URL)
						        .path("projects/" + projectId +
						                "/stories/" + storyId +
						                "/tasks")
						        .request()
						        .post(Entity.text(taskJSON.toString()));
						responseJSON = new JSONObject(response.readEntity(String.class));
						contentJSON = responseJSON.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT);
						// Get new TaskId
						long taskId = contentJSON.getLong(TaskEnum.ID);
						// Put new TaskId into JSON
						taskJSON.put(TaskJSONEnum.ID, taskId);

						// Add AttachFiles to Task
						JSONArray attachFileInTaskJSONArray = taskJSON.getJSONArray(TaskJSONEnum.ATTACH_FILES);
						for (int l = 0; l < attachFileInTaskJSONArray.length(); l++) {
							JSONObject attachFileJSON = attachFileInTaskJSONArray.getJSONObject(l);
							response = mClient.target(BASE_URL)
							        .path("projects/" + projectId +
							                "/stories/" + storyId +
							                "/tasks/" + taskId +
							                "/attachfiles")
							        .request()
							        .post(Entity.text(attachFileJSON.toString()));
						}
					}
				}

				//// Create Dropped Tasks
				JSONArray droppedTaskJSONArray = projectJSON.getJSONArray(ProjectJSONEnum.DROPPED_TASKS);
				for (int j = 0; j < droppedTaskJSONArray.length(); j++) {
					JSONObject taskJSON = droppedTaskJSONArray.getJSONObject(j);
					response = mClient.target(BASE_URL)
					        .path("projects/" + projectId +
					                "/tasks")
					        .request()
					        .post(Entity.text(taskJSON.toString()));
					responseJSON = new JSONObject(response.readEntity(String.class));
					contentJSON = responseJSON.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT);
					// Get new TaskId
					long taskId = contentJSON.getLong(TaskEnum.ID);
					// Put new TaskId into JSON
					taskJSON.put(TaskJSONEnum.ID, taskId);

					// Add AttachFiles to Task
					JSONArray attachFileInTaskJSONArray = taskJSON.getJSONArray(TaskJSONEnum.ATTACH_FILES);
					for (int k = 0; k < attachFileInTaskJSONArray.length(); k++) {
						JSONObject attachFileJSON = attachFileInTaskJSONArray.getJSONObject(k);
						response = mClient.target(BASE_URL)
						        .path("projects/" + projectId +
						                "/tasks/" + taskId +
						                "/attachfiles")
						        .request()
						        .post(Entity.text(attachFileJSON.toString()));
					}
				}

				//// Create Histories
				// Create Histories in Dropped Story
				for (int j = 0; j < droppedStoryJSONArray.length(); j++) {
					JSONObject droppedStoryJSON = droppedStoryJSONArray.getJSONObject(j);
					long droppedStoryId = droppedStoryJSON.getLong(StoryJSONEnum.ID);
					// Delete old Histories
					response = mClient.target(BASE_URL)
					        .path("projects/" + projectId +
					                "/stories/" + droppedStoryId +
					                "/histories")
					        .request()
					        .delete();
					// Add Histories to Story
					JSONArray historyInStoryJSONArray = droppedStoryJSON.getJSONArray(StoryJSONEnum.HISTORIES);
					for (int k = 0; k < historyInStoryJSONArray.length(); k++) {
						JSONObject historyJSON = historyInStoryJSONArray.getJSONObject(k);
						response = mClient.target(BASE_URL)
						        .path("projects/" + projectId +
						                "/stories/" + droppedStoryId +
						                "/histories")
						        .request()
						        .post(Entity.text(historyJSON.toString()));
					}

					// Create Histories in Task
					JSONArray taskJSONArray = droppedStoryJSON.getJSONArray(StoryJSONEnum.TASKS);
					for (int k = 0; k < taskJSONArray.length(); k++) {
						JSONObject taskJSON = taskJSONArray.getJSONObject(k);
						long taskId = taskJSON.getLong(TaskJSONEnum.ID);
						// Delete old Histories
						response = mClient.target(BASE_URL)
						        .path("projects/" + projectId +
						                "/stories/" + droppedStoryId +
						                "/tasks/" + taskId +
						                "/histories")
						        .request()
						        .delete();
						// Add Histories to Story
						JSONArray historyInTaskJSONArray = taskJSON.getJSONArray(TaskJSONEnum.HISTORIES);
						for (int l = 0; l < historyInTaskJSONArray.length(); l++) {
							JSONObject historyJSON = historyInTaskJSONArray.getJSONObject(l);
							response = mClient.target(BASE_URL)
							        .path("projects/" + projectId +
							                "/stories/" + droppedStoryId +
							                "/tasks/" + taskId +
							                "/histories")
							        .request()
							        .post(Entity.text(historyJSON.toString()));
						}
					}
				}

				// Create Histories in Dropped Task
				for (int j = 0; j < droppedTaskJSONArray.length(); j++) {
					JSONObject taskJSON = droppedTaskJSONArray.getJSONObject(j);
					long droppedTaskId = taskJSON.getLong(TaskJSONEnum.ID);
					// Delete old Histories
					response = mClient.target(BASE_URL)
					        .path("projects/" + projectId +
					                "/tasks/" + droppedTaskId +
					                "/histories")
					        .request()
					        .delete();
					// Add Histories to Story
					JSONArray historyInTaskJSONArray = taskJSON.getJSONArray(TaskJSONEnum.HISTORIES);
					for (int k = 0; k < historyInTaskJSONArray.length(); k++) {
						JSONObject historyJSON = historyInTaskJSONArray.getJSONObject(k);
						response = mClient.target(BASE_URL)
						        .path("projects/" + projectId +
						                "/tasks/" + droppedTaskId +
						                "/histories")
						        .request()
						        .post(Entity.text(historyJSON.toString()));
					}
				}

				// Create Histories in Story
				for (int j = 0; j < sprintJSONArray.length(); j++) {
					JSONObject sprintJSON = sprintJSONArray.getJSONObject(j);
					long sprintId = sprintJSON.getLong(SprintJSONEnum.ID);
					JSONArray storyJSONArray = sprintJSON.getJSONArray(SprintJSONEnum.STORIES);
					for (int k = 0; k < storyJSONArray.length(); k++) {
						JSONObject storyJSON = storyJSONArray.getJSONObject(k);
						long storyId = storyJSON.getLong(StoryJSONEnum.ID);
						// Delete old Histories
						response = mClient.target(BASE_URL)
						        .path("projects/" + projectId +
						                "/sprints/" + sprintId +
						                "/stories/" + storyId +
						                "/histories")
						        .request()
						        .delete();
						// Add History to Story
						JSONArray historyInStoryJSONArray = storyJSON.getJSONArray(StoryJSONEnum.HISTORIES);
						for (int l = 0; l < historyInStoryJSONArray.length(); l++) {
							JSONObject historyJSON = historyInStoryJSONArray.getJSONObject(l);
							response = mClient.target(BASE_URL)
							        .path("projects/" + projectId +
							                "/sprints/" + sprintId +
							                "/stories/" + storyId +
							                "/histories")
							        .request()
							        .post(Entity.text(historyJSON.toString()));
						}

						// Create Histories in Task
						JSONArray taskJSONArray = storyJSON.getJSONArray(StoryJSONEnum.TASKS);
						for (int l = 0; l < taskJSONArray.length(); l++) {
							JSONObject taskJSON = taskJSONArray.getJSONObject(l);
							long taskId = taskJSON.getLong(TaskJSONEnum.ID);
							// Delete old Histories
							response = mClient.target(BASE_URL)
							        .path("projects/" + projectId +
							                "/sprints/" + sprintId +
							                "/stories/" + storyId +
							                "/tasks/" + taskId +
							                "/histories")
							        .request()
							        .delete();
							// Add History to Task
							JSONArray historyInTaskJSONArray = taskJSON.getJSONArray(TaskJSONEnum.HISTORIES);
							for (int m = 0; m < historyInTaskJSONArray.length(); m++) {
								JSONObject historyJSON = historyInTaskJSONArray.getJSONObject(m);
								response = mClient.target(BASE_URL)
								        .path("projects/" + projectId +
								                "/sprints/" + sprintId +
								                "/stories/" + storyId +
								                "/tasks/" + taskId +
								                "/histories")
								        .request()
								        .post(Entity.text(historyJSON.toString()));
							}
						}
					}

					// Create Histories in Unplans
					JSONArray unplanJSONArray = sprintJSON.getJSONArray(SprintJSONEnum.UNPLANS);
					for (int k = 0; k < unplanJSONArray.length(); k++) {
						JSONObject unplanJSON = unplanJSONArray.getJSONObject(k);
						long unplanId = unplanJSON.getLong(UnplanJSONEnum.ID);
						// Delete old Histories
						response = mClient.target(BASE_URL)
						        .path("projects/" + projectId +
						                "/sprints/" + sprintId +
						                "/unplans/" + unplanId +
						                "/histories")
						        .request()
						        .delete();
						// Add History to Unplan
						JSONArray historyInUnplanJSONArray = unplanJSON.getJSONArray(UnplanJSONEnum.HISTORIES);
						for (int l = 0; l < historyInUnplanJSONArray.length(); l++) {
							JSONObject historyJSON = historyInUnplanJSONArray.getJSONObject(l);
							response = mClient.target(BASE_URL)
							        .path("projects/" + projectId +
							                "/sprints/" + sprintId +
							                "/unplans/" + unplanId +
							                "/histories")
							        .request()
							        .post(Entity.text(historyJSON.toString()));
						}
					}
				}
			}
		} catch (JSONException e) {
			return ResponseFactory.getResponse(Response.Status.BAD_REQUEST, ResponseJSONEnum.ERROR_BAD_REQUEST_MEESSAGE, "");
		}
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MEESSAGE, "");
	}
	
	private void cleanOldEzscrumData() throws IOException {
		// Get Admin account
		final String ADMIN_USER_NAME = "admin"; 
		AccountObject adminAccount = AccountObject.get(ADMIN_USER_NAME);
		Configuration config = new Configuration();
		config.save();
		InitialSQL ini = new InitialSQL(config);
		ini.exe();
		// delete external files
		deleteAllProject();
		deleteAllAttachFiles();
		adminAccount.save();
	}
	
	private void deleteAllProject() throws IOException {
		final String WORKSPACE_PATH = "./WebContent/Workspace/";
		ArrayList<ProjectObject> projects = ProjectDAO.getInstance().getAllProjects();
		for (ProjectObject project : projects) {
			File file = new File(WORKSPACE_PATH + project.getName());
			FileUtils.deleteDirectory(file);
		}
	}
	
	private void deleteAllAttachFiles() throws IOException {
		final String WORKSPACE_PATH = "./WebContent/Workspace/";
		final String ATTACH_FILE_FOLDER_NAME = "AttachFile";
		File file = new File(WORKSPACE_PATH + ATTACH_FILE_FOLDER_NAME);
		FileUtils.deleteDirectory(file);
	}
}
