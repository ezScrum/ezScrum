package ntut.csie.ezScrum.restful.dataMigration;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ExportJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ProjectJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ResponseJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.SprintJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.StoryJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.TaskJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.UnplanJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.security.SecurityModule;
import ntut.csie.ezScrum.restful.dataMigration.support.BaseUrlDistributor;
import ntut.csie.ezScrum.restful.dataMigration.support.ResponseFactory;
import ntut.csie.ezScrum.web.databaseEnum.ProjectEnum;
import ntut.csie.ezScrum.web.databaseEnum.SprintEnum;
import ntut.csie.ezScrum.web.databaseEnum.StoryEnum;
import ntut.csie.ezScrum.web.databaseEnum.TaskEnum;
import ntut.csie.ezScrum.web.databaseEnum.UnplanEnum;

@Path("dataMigration")
public class IntegratedRESTfulApi {
	private Client mClient;
	private String mBaseUrl = BaseUrlDistributor.getBaseUrl();

	@POST
	@Path("/projects")
	@Produces(MediaType.APPLICATION_JSON)
	public Response importProjectsJSON(@HeaderParam(SecurityModule.USERNAME_HEADER) String username,
							           @HeaderParam(SecurityModule.PASSWORD_HEADER) String password,
									   String entity) throws IOException {
		if (!SecurityModule.isAccountValid(username, password)) {
			return ResponseFactory.getResponse(Response.Status.FORBIDDEN, ResponseJSONEnum.ERROR_FORBIDDEN_MESSAGE, "");
		}
		// Get Client
		mClient = ClientBuilder.newClient();
		// Import JSON Data
		JSONObject importDataJSON = null;
		// 檢查JSON format
		try {
			importDataJSON = new JSONObject(entity);
		} catch (JSONException e) {
			return ResponseFactory.getResponse(Response.Status.BAD_REQUEST, ResponseJSONEnum.ERROR_BAD_REQUEST_MESSAGE, "");
		}
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
				Response response = mClient.target(mBaseUrl)
				        .path("accounts")
				        .request()
				        .header(SecurityModule.USERNAME_HEADER, username)
				        .header(SecurityModule.PASSWORD_HEADER, password)
				        .post(Entity.text(accountJSON.toString()));
				response.close();
				// TODO 紀錄結果
			}
		} catch (JSONException e) {
			return ResponseFactory.getResponse(Response.Status.BAD_REQUEST, ResponseJSONEnum.ERROR_BAD_REQUEST_MESSAGE, "");
		}

		// Create Projects
		JSONArray projectJSONArray = null;
		try {
			projectJSONArray = importDataJSON.getJSONArray(ExportJSONEnum.PROJECTS);
			//// Create Project
			for (int i = 0; i < projectJSONArray.length(); i++) {
				JSONObject projectJSON = projectJSONArray.getJSONObject(i);
				// Create Project
				Response response = mClient.target(mBaseUrl)
				        .path("projects")
				        .request()
				        .header(SecurityModule.USERNAME_HEADER, username)
				        .header(SecurityModule.PASSWORD_HEADER, password)
				        .post(Entity.text(projectJSON.toString()));
				// TODO 紀錄結果
				// 處理專案名稱重複的問題
				if (response.getStatus() == Response.Status.CONFLICT.getStatusCode()) {
					String newProjectName = getNewProjectName(projectJSON.getString(ProjectJSONEnum.NAME));
					String newProjectDisplayName = getNewProjectName(projectJSON.getString(ProjectJSONEnum.DISPLAY_NAME));
					projectJSON.put(ProjectJSONEnum.NAME, newProjectName);
					projectJSON.put(ProjectJSONEnum.DISPLAY_NAME, newProjectDisplayName);
					// Create Project
					response = mClient.target(mBaseUrl)
					        .path("projects")
					        .request()
					        .header(SecurityModule.USERNAME_HEADER, username)
					        .header(SecurityModule.PASSWORD_HEADER, password)
					        .post(Entity.text(projectJSON.toString()));
				}
				String responseString = response.readEntity(String.class);
				response.close();
				JSONObject responseJSON = new JSONObject(responseString);
				JSONObject contentJSON = responseJSON.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT);
				long projectId = contentJSON.getLong(ProjectEnum.ID);

				// Update ScrumRoles
				JSONObject scrumRolesJSON = projectJSON.getJSONObject(ProjectJSONEnum.SCRUM_ROLES);
				response = mClient.target(mBaseUrl)
				        .path("projects/" + projectId +
				                "/scrumroles")
				        .request()
				        .header(SecurityModule.USERNAME_HEADER, username)
				        .header(SecurityModule.PASSWORD_HEADER, password)
				        .put(Entity.text(scrumRolesJSON.toString()));
				response.close();
				// Create ProjectRoles
				JSONArray projectRoleJSONArray = projectJSON.getJSONArray(ProjectJSONEnum.PROJECT_ROLES);
				for (int j = 0; j < projectRoleJSONArray.length(); j++) {
					JSONObject projectRoleJSON = projectRoleJSONArray.getJSONObject(j);
					response = mClient.target(mBaseUrl)
					        .path("projects/" + projectId +
					                "/projectroles")
					        .request()
					        .header(SecurityModule.USERNAME_HEADER, username)
					        .header(SecurityModule.PASSWORD_HEADER, password)
					        .post(Entity.text(projectRoleJSON.toString()));
					response.close();
				}

				// Create Tags
				JSONArray tagJSONArray = projectJSON.getJSONArray(ProjectJSONEnum.TAGS);
				for (int j = 0; j < tagJSONArray.length(); j++) {
					JSONObject tagJSON = tagJSONArray.getJSONObject(j);
					response = mClient.target(mBaseUrl)
					        .path("projects/" + projectId +
					                "/tags")
					        .request()
					        .header(SecurityModule.USERNAME_HEADER, username)
					        .header(SecurityModule.PASSWORD_HEADER, password)
					        .post(Entity.text(tagJSON.toString()));
					response.close();
				}

				//// Create Sprints
				JSONArray sprintJSONArray = projectJSON.getJSONArray(ProjectJSONEnum.SPRINTS);
				for (int j = 0; j < sprintJSONArray.length(); j++) {
					JSONObject sprintJSON = sprintJSONArray.getJSONObject(j);
					response = mClient.target(mBaseUrl)
					        .path("projects/" + projectId +
					                "/sprints")
					        .request()
					        .header(SecurityModule.USERNAME_HEADER, username)
					        .header(SecurityModule.PASSWORD_HEADER, password)
					        .post(Entity.text(sprintJSON.toString()));
					responseString = response.readEntity(String.class);
					response.close();
					responseJSON = new JSONObject(responseString);
					contentJSON = responseJSON.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT);
					// Get new SprintId
					long sprintId = contentJSON.getLong(SprintEnum.ID);
					// Put new SprintId into JSON
					sprintJSON.put(SprintJSONEnum.ID, sprintId);

					//// Create Stories
					JSONArray storyJSONArray = sprintJSON.getJSONArray(SprintJSONEnum.STORIES);
					for (int k = 0; k < storyJSONArray.length(); k++) {
						JSONObject storyJSON = storyJSONArray.getJSONObject(k);
						response = mClient.target(mBaseUrl)
						        .path("projects/" + projectId +
						                "/sprints/" + sprintId +
						                "/stories")
						        .request()
						        .header(SecurityModule.USERNAME_HEADER, username)
						        .header(SecurityModule.PASSWORD_HEADER, password)
						        .post(Entity.text(storyJSON.toString()));
						responseString = response.readEntity(String.class);
						response.close();
						responseJSON = new JSONObject(responseString);
						contentJSON = responseJSON.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT);
						// Get new StoryId
						long storyId = contentJSON.getLong(StoryEnum.ID);
						// Put new StoryId into JSON
						storyJSON.put(SprintJSONEnum.ID, storyId);

						// Add Tag to Story
						JSONArray tagInStoryJSONArray = storyJSON.getJSONArray(StoryJSONEnum.TAGS);
						for (int l = 0; l < tagInStoryJSONArray.length(); l++) {
							JSONObject tagJSON = tagInStoryJSONArray.getJSONObject(l);
							response = mClient.target(mBaseUrl)
							        .path("projects/" + projectId +
							                "/sprints/" + sprintId +
							                "/stories/" + storyId +
							                "/tags")
							        .request()
							        .header(SecurityModule.USERNAME_HEADER, username)
							        .header(SecurityModule.PASSWORD_HEADER, password)
							        .post(Entity.text(tagJSON.toString()));
							response.close();
						}

						// Add AttachFiles to Story
						JSONArray attachFilesInStoryJSONArray = storyJSON.getJSONArray(StoryJSONEnum.ATTACH_FILES);
						for (int l = 0; l < attachFilesInStoryJSONArray.length(); l++) {
							JSONObject attachFileJSON = attachFilesInStoryJSONArray.getJSONObject(l);
							response = mClient.target(mBaseUrl)
							        .path("projects/" + projectId +
							                "/sprints/" + sprintId +
							                "/stories/" + storyId +
							                "/attachfiles")
							        .request()
							        .header(SecurityModule.USERNAME_HEADER, username)
							        .header(SecurityModule.PASSWORD_HEADER, password)
							        .post(Entity.text(attachFileJSON.toString()));
							response.close();
						}

						//// Create Tasks
						JSONArray taskJSONArray = storyJSON.getJSONArray(StoryJSONEnum.TASKS);
						for (int m = 0; m < taskJSONArray.length(); m++) {
							JSONObject taskJSON = taskJSONArray.getJSONObject(m);
							response = mClient.target(mBaseUrl)
							        .path("projects/" + projectId +
							                "/sprints/" + sprintId +
							                "/stories/" + storyId +
							                "/tasks")
							        .request()
							        .header(SecurityModule.USERNAME_HEADER, username)
							        .header(SecurityModule.PASSWORD_HEADER, password)
							        .post(Entity.text(taskJSON.toString()));
							responseString = response.readEntity(String.class);
							response.close();
							responseJSON = new JSONObject(responseString);
							contentJSON = responseJSON.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT);
							// Get new TaskId
							long taskId = contentJSON.getLong(TaskEnum.ID);
							// Put new TaskId into JSON
							taskJSON.put(TaskJSONEnum.ID, taskId);

							// Add AttachFiles to Task
							JSONArray attachFilesInTaskJSONArray = taskJSON.getJSONArray(TaskJSONEnum.ATTACH_FILES);
							for (int n = 0; n < attachFilesInTaskJSONArray.length(); n++) {
								JSONObject attachFileJSON = attachFilesInTaskJSONArray.getJSONObject(n);
								response = mClient.target(mBaseUrl)
								        .path("projects/" + projectId +
								                "/sprints/" + sprintId +
								                "/stories/" + storyId +
								                "/tasks/" + taskId +
								                "/attachfiles")
								        .request()
								        .header(SecurityModule.USERNAME_HEADER, username)
								        .header(SecurityModule.PASSWORD_HEADER, password)
								        .post(Entity.text(attachFileJSON.toString()));
								response.close();
							}
						}
					}

					//// Create Unplans
					JSONArray unplanJSONArray = sprintJSON.getJSONArray(SprintJSONEnum.UNPLANS);
					for (int k = 0; k < unplanJSONArray.length(); k++) {
						JSONObject unplanJSON = unplanJSONArray.getJSONObject(k);
						response = mClient.target(mBaseUrl)
						        .path("projects/" + projectId +
						                "/sprints/" + sprintId +
						                "/unplans")
						        .request()
						        .header(SecurityModule.USERNAME_HEADER, username)
						        .header(SecurityModule.PASSWORD_HEADER, password)
						        .post(Entity.text(unplanJSON.toString()));
						responseString = response.readEntity(String.class);
						response.close();
						responseJSON = new JSONObject(responseString);
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
						response = mClient.target(mBaseUrl)
						        .path("projects/" + projectId +
						                "/sprints/" + sprintId +
						                "/retrospectives")
						        .request()
						        .header(SecurityModule.USERNAME_HEADER, username)
						        .header(SecurityModule.PASSWORD_HEADER, password)
						        .post(Entity.text(retrospectiveJSON.toString()));
						response.close();
					}
				}

				//// Create Releases
				JSONArray releaseJSONArray = projectJSON.getJSONArray(ProjectJSONEnum.RELEASES);
				for (int j = 0; j < releaseJSONArray.length(); j++) {
					JSONObject releaseJSON = releaseJSONArray.getJSONObject(j);
					response = mClient.target(mBaseUrl)
					        .path("projects/" + projectId +
					                "/releases")
					        .request()
					        .header(SecurityModule.USERNAME_HEADER, username)
					        .header(SecurityModule.PASSWORD_HEADER, password)
					        .post(Entity.text(releaseJSON.toString()));
					response.close();
				}

				//// Create Dropped Stories
				JSONArray droppedStoryJSONArray = projectJSON.getJSONArray(ProjectJSONEnum.DROPPED_STORIES);
				for (int j = 0; j < droppedStoryJSONArray.length(); j++) {
					JSONObject droppedStoryJSON = droppedStoryJSONArray.getJSONObject(j);
					response = mClient.target(mBaseUrl)
					        .path("projects/" + projectId +
					                "/stories")
					        .request()
					        .header(SecurityModule.USERNAME_HEADER, username)
					        .header(SecurityModule.PASSWORD_HEADER, password)
					        .post(Entity.text(droppedStoryJSON.toString()));
					responseString = response.readEntity(String.class);
					response.close();
					responseJSON = new JSONObject(responseString);
					contentJSON = responseJSON.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT);
					// Get new StoryId
					long storyId = contentJSON.getLong(StoryEnum.ID);
					// Put new StoryId into JSON
					droppedStoryJSON.put(StoryJSONEnum.ID, storyId);

					// Add Tag to Story
					JSONArray tagInStoryJSONArray = droppedStoryJSON.getJSONArray(StoryJSONEnum.TAGS);
					for (int k = 0; k < tagInStoryJSONArray.length(); k++) {
						JSONObject tagJSON = tagInStoryJSONArray.getJSONObject(k);
						response = mClient.target(mBaseUrl)
						        .path("projects/" + projectId +
						                "/stories/" + storyId +
						                "/tags")
						        .request()
						        .header(SecurityModule.USERNAME_HEADER, username)
						        .header(SecurityModule.PASSWORD_HEADER, password)
						        .post(Entity.text(tagJSON.toString()));
						response.close();
					}

					// Add AttachFiles to Story
					JSONArray attachFilesInStoryJSONArray = droppedStoryJSON.getJSONArray(StoryJSONEnum.ATTACH_FILES);
					for (int k = 0; k < attachFilesInStoryJSONArray.length(); k++) {
						JSONObject attachFileJSON = attachFilesInStoryJSONArray.getJSONObject(k);
						response = mClient.target(mBaseUrl)
						        .path("projects/" + projectId +
						                "/stories/" + storyId +
						                "/attachfiles")
						        .request()
						        .header(SecurityModule.USERNAME_HEADER, username)
						        .header(SecurityModule.PASSWORD_HEADER, password)
						        .post(Entity.text(attachFileJSON.toString()));
						response.close();
					}

					//// Create Tasks in DroppedStory
					JSONArray taskJSONArray = droppedStoryJSON.getJSONArray(StoryJSONEnum.TASKS);
					for (int k = 0; k < taskJSONArray.length(); k++) {
						JSONObject taskJSON = taskJSONArray.getJSONObject(k);
						response = mClient.target(mBaseUrl)
						        .path("projects/" + projectId +
						                "/stories/" + storyId +
						                "/tasks")
						        .request()
						        .header(SecurityModule.USERNAME_HEADER, username)
						        .header(SecurityModule.PASSWORD_HEADER, password)
						        .post(Entity.text(taskJSON.toString()));
						responseString = response.readEntity(String.class);
						response.close();
						responseJSON = new JSONObject(responseString);
						contentJSON = responseJSON.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT);
						// Get new TaskId
						long taskId = contentJSON.getLong(TaskEnum.ID);
						// Put new TaskId into JSON
						taskJSON.put(TaskJSONEnum.ID, taskId);

						// Add AttachFiles to Task
						JSONArray attachFileInTaskJSONArray = taskJSON.getJSONArray(TaskJSONEnum.ATTACH_FILES);
						for (int l = 0; l < attachFileInTaskJSONArray.length(); l++) {
							JSONObject attachFileJSON = attachFileInTaskJSONArray.getJSONObject(l);
							response = mClient.target(mBaseUrl)
							        .path("projects/" + projectId +
							                "/stories/" + storyId +
							                "/tasks/" + taskId +
							                "/attachfiles")
							        .request()
							        .header(SecurityModule.USERNAME_HEADER, username)
							        .header(SecurityModule.PASSWORD_HEADER, password)
							        .post(Entity.text(attachFileJSON.toString()));
							response.close();
						}
					}
				}

				//// Create Dropped Tasks
				JSONArray droppedTaskJSONArray = projectJSON.getJSONArray(ProjectJSONEnum.DROPPED_TASKS);
				for (int j = 0; j < droppedTaskJSONArray.length(); j++) {
					JSONObject taskJSON = droppedTaskJSONArray.getJSONObject(j);
					response = mClient.target(mBaseUrl)
					        .path("projects/" + projectId +
					                "/tasks")
					        .request()
					        .header(SecurityModule.USERNAME_HEADER, username)
					        .header(SecurityModule.PASSWORD_HEADER, password)
					        .post(Entity.text(taskJSON.toString()));
					responseString = response.readEntity(String.class);
					response.close();
					responseJSON = new JSONObject(responseString);
					contentJSON = responseJSON.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT);
					// Get new TaskId
					long taskId = contentJSON.getLong(TaskEnum.ID);
					// Put new TaskId into JSON
					taskJSON.put(TaskJSONEnum.ID, taskId);

					// Add AttachFiles to Task
					JSONArray attachFileInTaskJSONArray = taskJSON.getJSONArray(TaskJSONEnum.ATTACH_FILES);
					for (int k = 0; k < attachFileInTaskJSONArray.length(); k++) {
						JSONObject attachFileJSON = attachFileInTaskJSONArray.getJSONObject(k);
						response = mClient.target(mBaseUrl)
						        .path("projects/" + projectId +
						                "/tasks/" + taskId +
						                "/attachfiles")
						        .request()
						        .header(SecurityModule.USERNAME_HEADER, username)
						        .header(SecurityModule.PASSWORD_HEADER, password)
						        .post(Entity.text(attachFileJSON.toString()));
						response.close();
					}
				}

				//// Create Histories
				// Create Histories in Dropped Story
				for (int j = 0; j < droppedStoryJSONArray.length(); j++) {
					JSONObject droppedStoryJSON = droppedStoryJSONArray.getJSONObject(j);
					long droppedStoryId = droppedStoryJSON.getLong(StoryJSONEnum.ID);
					// Delete old Histories
					response = mClient.target(mBaseUrl)
					        .path("projects/" + projectId +
					                "/stories/" + droppedStoryId +
					                "/histories")
					        .request()
					        .header(SecurityModule.USERNAME_HEADER, username)
					        .header(SecurityModule.PASSWORD_HEADER, password)
					        .delete();
					response.close();
					// Add Histories to Story
					JSONArray historyInStoryJSONArray = droppedStoryJSON.getJSONArray(StoryJSONEnum.HISTORIES);
					for (int k = 0; k < historyInStoryJSONArray.length(); k++) {
						JSONObject historyJSON = historyInStoryJSONArray.getJSONObject(k);
						response = mClient.target(mBaseUrl)
						        .path("projects/" + projectId +
						                "/stories/" + droppedStoryId +
						                "/histories")
						        .request()
						        .header(SecurityModule.USERNAME_HEADER, username)
						        .header(SecurityModule.PASSWORD_HEADER, password)
						        .post(Entity.text(historyJSON.toString()));
						response.close();
					}

					// Create Histories in Task
					JSONArray taskJSONArray = droppedStoryJSON.getJSONArray(StoryJSONEnum.TASKS);
					for (int k = 0; k < taskJSONArray.length(); k++) {
						JSONObject taskJSON = taskJSONArray.getJSONObject(k);
						long taskId = taskJSON.getLong(TaskJSONEnum.ID);
						// Delete old Histories
						response = mClient.target(mBaseUrl)
						        .path("projects/" + projectId +
						                "/stories/" + droppedStoryId +
						                "/tasks/" + taskId +
						                "/histories")
						        .request()
						        .header(SecurityModule.USERNAME_HEADER, username)
						        .header(SecurityModule.PASSWORD_HEADER, password)
						        .delete();
						response.close();
						// Add Histories to Story
						JSONArray historyInTaskJSONArray = taskJSON.getJSONArray(TaskJSONEnum.HISTORIES);
						for (int l = 0; l < historyInTaskJSONArray.length(); l++) {
							JSONObject historyJSON = historyInTaskJSONArray.getJSONObject(l);
							response = mClient.target(mBaseUrl)
							        .path("projects/" + projectId +
							                "/stories/" + droppedStoryId +
							                "/tasks/" + taskId +
							                "/histories")
							        .request()
							        .header(SecurityModule.USERNAME_HEADER, username)
							        .header(SecurityModule.PASSWORD_HEADER, password)
							        .post(Entity.text(historyJSON.toString()));
							response.close();
						}
					}
				}

				// Create Histories in Dropped Task
				for (int j = 0; j < droppedTaskJSONArray.length(); j++) {
					JSONObject taskJSON = droppedTaskJSONArray.getJSONObject(j);
					long droppedTaskId = taskJSON.getLong(TaskJSONEnum.ID);
					// Delete old Histories
					response = mClient.target(mBaseUrl)
					        .path("projects/" + projectId +
					                "/tasks/" + droppedTaskId +
					                "/histories")
					        .request()
					        .header(SecurityModule.USERNAME_HEADER, username)
					        .header(SecurityModule.PASSWORD_HEADER, password)
					        .delete();
					response.close();
					// Add Histories to Story
					JSONArray historyInTaskJSONArray = taskJSON.getJSONArray(TaskJSONEnum.HISTORIES);
					for (int k = 0; k < historyInTaskJSONArray.length(); k++) {
						JSONObject historyJSON = historyInTaskJSONArray.getJSONObject(k);
						response = mClient.target(mBaseUrl)
						        .path("projects/" + projectId +
						                "/tasks/" + droppedTaskId +
						                "/histories")
						        .request()
						        .header(SecurityModule.USERNAME_HEADER, username)
						        .header(SecurityModule.PASSWORD_HEADER, password)
						        .post(Entity.text(historyJSON.toString()));
						response.close();
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
						response = mClient.target(mBaseUrl)
						        .path("projects/" + projectId +
						                "/sprints/" + sprintId +
						                "/stories/" + storyId +
						                "/histories")
						        .request()
						        .header(SecurityModule.USERNAME_HEADER, username)
						        .header(SecurityModule.PASSWORD_HEADER, password)
						        .delete();
						response.close();
						// Add History to Story
						JSONArray historyInStoryJSONArray = storyJSON.getJSONArray(StoryJSONEnum.HISTORIES);
						for (int l = 0; l < historyInStoryJSONArray.length(); l++) {
							JSONObject historyJSON = historyInStoryJSONArray.getJSONObject(l);
							response = mClient.target(mBaseUrl)
							        .path("projects/" + projectId +
							                "/sprints/" + sprintId +
							                "/stories/" + storyId +
							                "/histories")
							        .request()
							        .header(SecurityModule.USERNAME_HEADER, username)
							        .header(SecurityModule.PASSWORD_HEADER, password)
							        .post(Entity.text(historyJSON.toString()));
							response.close();
						}

						// Create Histories in Task
						JSONArray taskJSONArray = storyJSON.getJSONArray(StoryJSONEnum.TASKS);
						for (int l = 0; l < taskJSONArray.length(); l++) {
							JSONObject taskJSON = taskJSONArray.getJSONObject(l);
							long taskId = taskJSON.getLong(TaskJSONEnum.ID);
							// Delete old Histories
							response = mClient.target(mBaseUrl)
							        .path("projects/" + projectId +
							                "/sprints/" + sprintId +
							                "/stories/" + storyId +
							                "/tasks/" + taskId +
							                "/histories")
							        .request()
							        .header(SecurityModule.USERNAME_HEADER, username)
							        .header(SecurityModule.PASSWORD_HEADER, password)
							        .delete();
							response.close();
							// Add History to Task
							JSONArray historyInTaskJSONArray = taskJSON.getJSONArray(TaskJSONEnum.HISTORIES);
							for (int m = 0; m < historyInTaskJSONArray.length(); m++) {
								JSONObject historyJSON = historyInTaskJSONArray.getJSONObject(m);
								response = mClient.target(mBaseUrl)
								        .path("projects/" + projectId +
								                "/sprints/" + sprintId +
								                "/stories/" + storyId +
								                "/tasks/" + taskId +
								                "/histories")
								        .request()
								        .header(SecurityModule.USERNAME_HEADER, username)
								        .header(SecurityModule.PASSWORD_HEADER, password)
								        .post(Entity.text(historyJSON.toString()));
								response.close();
							}
						}
					}

					// Create Histories in Unplans
					JSONArray unplanJSONArray = sprintJSON.getJSONArray(SprintJSONEnum.UNPLANS);
					for (int k = 0; k < unplanJSONArray.length(); k++) {
						JSONObject unplanJSON = unplanJSONArray.getJSONObject(k);
						long unplanId = unplanJSON.getLong(UnplanJSONEnum.ID);
						// Delete old Histories
						response = mClient.target(mBaseUrl)
						        .path("projects/" + projectId +
						                "/sprints/" + sprintId +
						                "/unplans/" + unplanId +
						                "/histories")
						        .request()
						        .header(SecurityModule.USERNAME_HEADER, username)
						        .header(SecurityModule.PASSWORD_HEADER, password)
						        .delete();
						response.close();
						// Add History to Unplan
						JSONArray historyInUnplanJSONArray = unplanJSON.getJSONArray(UnplanJSONEnum.HISTORIES);
						for (int l = 0; l < historyInUnplanJSONArray.length(); l++) {
							JSONObject historyJSON = historyInUnplanJSONArray.getJSONObject(l);
							response = mClient.target(mBaseUrl)
							        .path("projects/" + projectId +
							                "/sprints/" + sprintId +
							                "/unplans/" + unplanId +
							                "/histories")
							        .request()
							        .header(SecurityModule.USERNAME_HEADER, username)
							        .header(SecurityModule.PASSWORD_HEADER, password)
							        .post(Entity.text(historyJSON.toString()));
							response.close();
						}
					}
				}
			}
			mClient.close();
		} catch (JSONException e) {
			return ResponseFactory.getResponse(Response.Status.BAD_REQUEST, ResponseJSONEnum.ERROR_BAD_REQUEST_MESSAGE, "");
		}
		return ResponseFactory.getResponse(Response.Status.OK, ResponseJSONEnum.SUCCESS_MESSAGE, "");
	}
	
	private String getNewProjectName(String projectName) {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("_yyyyMMddHHmmss");
		return projectName + simpleDateFormat.format(calendar.getTime());
	}
}
