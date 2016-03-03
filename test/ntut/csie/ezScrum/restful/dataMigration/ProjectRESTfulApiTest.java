package ntut.csie.ezScrum.restful.dataMigration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.ArrayList;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.net.httpserver.HttpServer;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.AccountJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ProjectJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ResponseJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.ScrumRoleJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.TagJSONEnum;
import ntut.csie.ezScrum.restful.dataMigration.support.BaseUrlDistributor;
import ntut.csie.ezScrum.restful.dataMigration.security.SecurityModule;import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.databaseEnum.ProjectEnum;
import ntut.csie.ezScrum.web.databaseEnum.RoleEnum;

public class ProjectRESTfulApiTest extends JerseyTest {
	private Configuration mConfig;
	private CreateProject mCP;
	private CreateAccount mCA;
	private ResourceConfig mResourceConfig;
	private Client mClient;
	private HttpServer mHttpServer;
	private static String BASE_URL = BaseUrlDistributor.TEST_MODE_BASE_URL;
	private URI mBaseUri = URI.create(BASE_URL);

	@Override
	protected Application configure() {
		mResourceConfig = new ResourceConfig(ProjectRESTfulApi.class);
		return mResourceConfig;
	}

	@Before
	public void setUp() {
		// Set Test Mode
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// Create Project
		mCP = new CreateProject(1);
		mCP.exeCreateForDb();

		// Create Account
		mCA = new CreateAccount(2);
		mCA.exe();

		// Start Server
		mHttpServer = JdkHttpServerFactory.createHttpServer(mBaseUri, mResourceConfig, true);

		// Create Client
		mClient = ClientBuilder.newClient();
	}

	@After
	public void tearDown() {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 讓 config 回到 Production 模式
		mConfig.setTestMode(false);
		mConfig.save();

		// Stop Server
		mHttpServer.stop(0);

		// ============= release ==============
		ini = null;
		mCP = null;
		mHttpServer = null;
		mClient = null;
	}

	@Test
	public void testCreateProject() throws JSONException {
		// Test Data
		String projectName = "TEST_PROJECT_NAME";
		String projectDisplayName = "TEST_PROJECT_DISPLAY_NAME";
		String projectComment = "TEST_PROJECT_COMMENT";
		String projectProductOwner = "TEST_PROJECT_PRODUCT_OWNER";
		int projectMaxAttachFileSize = 2;
		long createTime = System.currentTimeMillis();

		JSONObject projectJSON = new JSONObject();
		projectJSON.put(ProjectJSONEnum.NAME, projectName);
		projectJSON.put(ProjectJSONEnum.DISPLAY_NAME, projectDisplayName);
		projectJSON.put(ProjectJSONEnum.COMMENT, projectComment);
		projectJSON.put(ProjectJSONEnum.PRODUCT_OWNER, projectProductOwner);
		projectJSON.put(ProjectJSONEnum.ATTATCH_MAX_SIZE, projectMaxAttachFileSize);
		projectJSON.put(ProjectJSONEnum.CREATE_TIME, createTime);

		// Call '/projects' API
		Response response = mClient.target(BASE_URL)
		        .path("projects")
		        .request()
		        .header(SecurityModule.USERNAME_HEADER, SecurityModule.ADMIN_MD5_USERNAME)
		        .header(SecurityModule.PASSWORD_HEADER, SecurityModule.ADMIN_MD5_PASSWORD)
		        .post(Entity.text(projectJSON.toString()));

		JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
		JSONObject contentJSON = new JSONObject(jsonResponse.getString(ResponseJSONEnum.JSON_KEY_CONTENT));

		// Assert
		assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
		assertTrue(contentJSON.getLong(ProjectEnum.ID) != -1);
		assertEquals(projectName, contentJSON.getString(ProjectEnum.NAME));
		assertEquals(projectDisplayName, contentJSON.getString(ProjectEnum.DISPLAY_NAME));
		assertEquals(projectComment, contentJSON.getString(ProjectEnum.COMMENT));
		assertEquals(projectProductOwner, contentJSON.getString(ProjectEnum.PRODUCT_OWNER));
		assertEquals(projectMaxAttachFileSize, contentJSON.getInt(ProjectEnum.ATTATCH_MAX_SIZE));
		assertEquals(createTime, contentJSON.getLong(ProjectEnum.CREATE_TIME));
	}
	
	@Test
	public void testCreateExistedProject() throws JSONException {
		// Test Data
		String projectName = mCP.getAllProjects().get(0).getName();
		String projectDisplayName = mCP.getAllProjects().get(0).getDisplayName();
		String projectComment = mCP.getAllProjects().get(0).getComment();
		String projectProductOwner = mCP.getAllProjects().get(0).getManager();
		int projectMaxAttachFileSize = (int) mCP.getAllProjects().get(0).getAttachFileSize();
		long createTime = mCP.getAllProjects().get(0).getCreateTime();

		JSONObject projectJSON = new JSONObject();
		projectJSON.put(ProjectJSONEnum.NAME, projectName);
		projectJSON.put(ProjectJSONEnum.DISPLAY_NAME, projectDisplayName);
		projectJSON.put(ProjectJSONEnum.COMMENT, projectComment);
		projectJSON.put(ProjectJSONEnum.PRODUCT_OWNER, projectProductOwner);
		projectJSON.put(ProjectJSONEnum.ATTATCH_MAX_SIZE, projectMaxAttachFileSize);
		projectJSON.put(ProjectJSONEnum.CREATE_TIME, createTime);

		// Call '/projects' API
		Response response = mClient.target(BASE_URL)
		        .path("projects")
		        .request()
		        .header(SecurityModule.USERNAME_HEADER, SecurityModule.ADMIN_MD5_USERNAME)
		        .header(SecurityModule.PASSWORD_HEADER, SecurityModule.ADMIN_MD5_PASSWORD)
		        .post(Entity.text(projectJSON.toString()));
		
		// Assert
		assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void testUpdateScrumRolesInProject() throws JSONException {
		// Test Data
		long projectId = mCP.getAllProjects().get(0).getId();
		String projectName = mCP.getAllProjects().get(0).getName();
		ScrumRole productOwner = new ScrumRole(projectName, ScrumRoleJSONEnum.PRODUCT_OWNER);
		productOwner.setAccessProductBacklog(true);
		productOwner.setAccessSprintPlan(true);
		productOwner.setAccessTaskBoard(false);
		productOwner.setAccessSprintBacklog(true);
		productOwner.setAccessReleasePlan(true);
		productOwner.setAccessRetrospective(false);
		productOwner.setAccessUnplanItem(false);
		productOwner.setAccessReport(true);
		productOwner.setAccessEditProject(true);

		ScrumRole scrumMaster = new ScrumRole(projectName, ScrumRoleJSONEnum.SCRUM_MASTER);
		scrumMaster.setAccessProductBacklog(true);
		scrumMaster.setAccessSprintPlan(true);
		scrumMaster.setAccessTaskBoard(true);
		scrumMaster.setAccessSprintBacklog(true);
		scrumMaster.setAccessReleasePlan(true);
		scrumMaster.setAccessRetrospective(true);
		scrumMaster.setAccessUnplanItem(true);
		scrumMaster.setAccessReport(true);
		scrumMaster.setAccessEditProject(false);

		ScrumRole scrumTeam = new ScrumRole(projectName, ScrumRoleJSONEnum.SCRUM_TEAM);
		scrumTeam.setAccessProductBacklog(false);
		scrumTeam.setAccessSprintPlan(true);
		scrumTeam.setAccessTaskBoard(true);
		scrumTeam.setAccessSprintBacklog(true);
		scrumTeam.setAccessReleasePlan(true);
		scrumTeam.setAccessRetrospective(true);
		scrumTeam.setAccessUnplanItem(true);
		scrumTeam.setAccessReport(true);
		scrumTeam.setAccessEditProject(false);

		ScrumRole stakeholder = new ScrumRole(projectName, ScrumRoleJSONEnum.STAKEHOLDER);
		stakeholder.setAccessProductBacklog(false);
		stakeholder.setAccessSprintPlan(false);
		stakeholder.setAccessTaskBoard(false);
		stakeholder.setAccessSprintBacklog(false);
		stakeholder.setAccessReleasePlan(true);
		stakeholder.setAccessRetrospective(false);
		stakeholder.setAccessUnplanItem(false);
		stakeholder.setAccessReport(true);
		stakeholder.setAccessEditProject(false);

		ScrumRole guest = new ScrumRole(projectName, ScrumRoleJSONEnum.GUEST);
		guest.setAccessProductBacklog(false);
		guest.setAccessSprintPlan(false);
		guest.setAccessTaskBoard(false);
		guest.setAccessSprintBacklog(false);
		guest.setAccessReleasePlan(true);
		guest.setAccessRetrospective(false);
		guest.setAccessUnplanItem(false);
		guest.setAccessReport(true);
		guest.setAccessEditProject(false);
		
		JSONObject scrumRolesJSON = new JSONObject();
		scrumRolesJSON.put(ScrumRoleJSONEnum.PRODUCT_OWNER, productOwner.toJSON());
		scrumRolesJSON.put(ScrumRoleJSONEnum.SCRUM_MASTER, scrumMaster.toJSON());
		scrumRolesJSON.put(ScrumRoleJSONEnum.SCRUM_TEAM, scrumTeam.toJSON());
		scrumRolesJSON.put(ScrumRoleJSONEnum.STAKEHOLDER, stakeholder.toJSON());
		scrumRolesJSON.put(ScrumRoleJSONEnum.GUEST, guest.toJSON());
		
		// Call '/projects/{projectId}/scrumRoles' API
		Response response = mClient.target(BASE_URL)
				.path("projects/" + projectId + "/scrumroles")
				.request()
				.header(SecurityModule.USERNAME_HEADER, SecurityModule.ADMIN_MD5_USERNAME)
		        .header(SecurityModule.PASSWORD_HEADER, SecurityModule.ADMIN_MD5_PASSWORD)
				.put(Entity.text(scrumRolesJSON.toString()));
		
		// Assert Response
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		JSONObject responseJSON = new JSONObject(response.readEntity(String.class));
		JSONObject contentJSON = responseJSON.getJSONObject(ResponseJSONEnum.JSON_KEY_CONTENT);
		String message = responseJSON.getString(ResponseJSONEnum.JSON_KEY_MESSAGE);
		assertEquals(ResponseJSONEnum.SUCCESS_MESSAGE, message);
		// Assert Product Owner permission
		JSONObject productOwnerJSON = contentJSON.getJSONObject(ScrumRoleJSONEnum.PRODUCT_OWNER);
		assertEquals(productOwner.getAccessProductBacklog(), productOwnerJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_PRODUCT_BACKLOG));
		assertEquals(productOwner.getAccessSprintPlan(), productOwnerJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_SPRINT_PLAN));
		assertEquals(productOwner.getAccessTaskBoard(), productOwnerJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_TASKBOARD));
		assertEquals(productOwner.getAccessSprintBacklog(), productOwnerJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_SPRINT_BACKLOG));
		assertEquals(productOwner.getAccessReleasePlan(), productOwnerJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_RELEASE_PLAN));
		assertEquals(productOwner.getAccessRetrospective(), productOwnerJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_RETROSPECTIVE));
		assertEquals(productOwner.getAccessUnplanItem(), productOwnerJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_UNPLAN));
		assertEquals(productOwner.getAccessReport(), productOwnerJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_REPORT));
		assertEquals(productOwner.getAccessEditProject(), productOwnerJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_EDIT_PROJECT));
		
		// Assert Scrum Master permission
		JSONObject scrumMasterJSON = contentJSON.getJSONObject(ScrumRoleJSONEnum.SCRUM_MASTER);
		assertEquals(scrumMaster.getAccessProductBacklog(), scrumMasterJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_PRODUCT_BACKLOG));
		assertEquals(scrumMaster.getAccessSprintPlan(), scrumMasterJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_SPRINT_PLAN));
		assertEquals(scrumMaster.getAccessTaskBoard(), scrumMasterJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_TASKBOARD));
		assertEquals(scrumMaster.getAccessSprintBacklog(), scrumMasterJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_SPRINT_BACKLOG));
		assertEquals(scrumMaster.getAccessReleasePlan(), scrumMasterJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_RELEASE_PLAN));
		assertEquals(scrumMaster.getAccessRetrospective(), scrumMasterJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_RETROSPECTIVE));
		assertEquals(scrumMaster.getAccessUnplanItem(), scrumMasterJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_UNPLAN));
		assertEquals(scrumMaster.getAccessReport(), scrumMasterJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_REPORT));
		assertEquals(scrumMaster.getAccessEditProject(), scrumMasterJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_EDIT_PROJECT));
		
		// Assert Scrum Team permission
		JSONObject scrumTeamJSON = contentJSON.getJSONObject(ScrumRoleJSONEnum.SCRUM_TEAM);
		assertEquals(scrumTeam.getAccessProductBacklog(), scrumTeamJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_PRODUCT_BACKLOG));
		assertEquals(scrumTeam.getAccessSprintPlan(), scrumTeamJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_SPRINT_PLAN));
		assertEquals(scrumTeam.getAccessTaskBoard(), scrumTeamJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_TASKBOARD));
		assertEquals(scrumTeam.getAccessSprintBacklog(), scrumTeamJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_SPRINT_BACKLOG));
		assertEquals(scrumTeam.getAccessReleasePlan(), scrumTeamJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_RELEASE_PLAN));
		assertEquals(scrumTeam.getAccessRetrospective(), scrumTeamJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_RETROSPECTIVE));
		assertEquals(scrumTeam.getAccessUnplanItem(), scrumTeamJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_UNPLAN));
		assertEquals(scrumTeam.getAccessReport(), scrumTeamJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_REPORT));
		assertEquals(scrumTeam.getAccessEditProject(), scrumTeamJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_EDIT_PROJECT));
		
		// Assert Stakeholder permission
		JSONObject stakeholderJSON = contentJSON.getJSONObject(ScrumRoleJSONEnum.STAKEHOLDER);
		assertEquals(stakeholder.getAccessProductBacklog(), stakeholderJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_PRODUCT_BACKLOG));
		assertEquals(stakeholder.getAccessSprintPlan(), stakeholderJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_SPRINT_PLAN));
		assertEquals(stakeholder.getAccessTaskBoard(), stakeholderJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_TASKBOARD));
		assertEquals(stakeholder.getAccessSprintBacklog(), stakeholderJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_SPRINT_BACKLOG));
		assertEquals(stakeholder.getAccessReleasePlan(), stakeholderJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_RELEASE_PLAN));
		assertEquals(stakeholder.getAccessRetrospective(), stakeholderJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_RETROSPECTIVE));
		assertEquals(stakeholder.getAccessUnplanItem(), stakeholderJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_UNPLAN));
		assertEquals(stakeholder.getAccessReport(), stakeholderJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_REPORT));
		assertEquals(stakeholder.getAccessEditProject(), stakeholderJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_EDIT_PROJECT));
		
		// Assert Guest permission
		JSONObject guestJSON = contentJSON.getJSONObject(ScrumRoleJSONEnum.GUEST);
		assertEquals(guest.getAccessProductBacklog(), guestJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_PRODUCT_BACKLOG));
		assertEquals(guest.getAccessSprintPlan(), guestJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_SPRINT_PLAN));
		assertEquals(guest.getAccessTaskBoard(), guestJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_TASKBOARD));
		assertEquals(guest.getAccessSprintBacklog(), guestJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_SPRINT_BACKLOG));
		assertEquals(guest.getAccessReleasePlan(), guestJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_RELEASE_PLAN));
		assertEquals(guest.getAccessRetrospective(), guestJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_RETROSPECTIVE));
		assertEquals(guest.getAccessUnplanItem(), guestJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_UNPLAN));
		assertEquals(guest.getAccessReport(), guestJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_REPORT));
		assertEquals(guest.getAccessEditProject(), guestJSON.getBoolean(ScrumRoleJSONEnum.ACCESS_EDIT_PROJECT));
		
		// Assert Scrum Roles in DB
		ProjectObject project = ProjectObject.get(projectId);
		// Assert Product Owner in DB
		ScrumRole productOwnerInDB = project.getScrumRole(RoleEnum.ProductOwner);
		assertEquals(productOwner.getAccessProductBacklog(), productOwnerInDB.getAccessProductBacklog());
		assertEquals(productOwner.getAccessSprintPlan(), productOwnerInDB.getAccessSprintPlan());
		assertEquals(productOwner.getAccessTaskBoard(), productOwnerInDB.getAccessTaskBoard());
		assertEquals(productOwner.getAccessSprintBacklog(), productOwnerInDB.getAccessSprintBacklog());
		assertEquals(productOwner.getAccessReleasePlan(), productOwnerInDB.getAccessReleasePlan());
		assertEquals(productOwner.getAccessRetrospective(), productOwnerInDB.getAccessRetrospective());
		assertEquals(productOwner.getAccessUnplanItem(), productOwnerInDB.getAccessUnplanItem());
		assertEquals(productOwner.getAccessReport(), productOwnerInDB.getAccessReport());
		assertEquals(productOwner.getAccessEditProject(), productOwnerInDB.getAccessEditProject());
		// Assert Scrum Master in DB
		ScrumRole scrumMasterInDB = project.getScrumRole(RoleEnum.ScrumMaster);
		assertEquals(scrumMaster.getAccessProductBacklog(), scrumMasterInDB.getAccessProductBacklog());
		assertEquals(scrumMaster.getAccessSprintPlan(), scrumMasterInDB.getAccessSprintPlan());
		assertEquals(scrumMaster.getAccessTaskBoard(), scrumMasterInDB.getAccessTaskBoard());
		assertEquals(scrumMaster.getAccessSprintBacklog(), scrumMasterInDB.getAccessSprintBacklog());
		assertEquals(scrumMaster.getAccessReleasePlan(), scrumMasterInDB.getAccessReleasePlan());
		assertEquals(scrumMaster.getAccessRetrospective(), scrumMasterInDB.getAccessRetrospective());
		assertEquals(scrumMaster.getAccessUnplanItem(), scrumMasterInDB.getAccessUnplanItem());
		assertEquals(scrumMaster.getAccessReport(), scrumMasterInDB.getAccessReport());
		assertEquals(scrumMaster.getAccessEditProject(), scrumMasterInDB.getAccessEditProject());
		// Assert Scrum Team in DB
		ScrumRole scrumTeamInDB = project.getScrumRole(RoleEnum.ScrumTeam);
		assertEquals(scrumTeam.getAccessProductBacklog(), scrumTeamInDB.getAccessProductBacklog());
		assertEquals(scrumTeam.getAccessSprintPlan(), scrumTeamInDB.getAccessSprintPlan());
		assertEquals(scrumTeam.getAccessTaskBoard(), scrumTeamInDB.getAccessTaskBoard());
		assertEquals(scrumTeam.getAccessSprintBacklog(), scrumTeamInDB.getAccessSprintBacklog());
		assertEquals(scrumTeam.getAccessReleasePlan(), scrumTeamInDB.getAccessReleasePlan());
		assertEquals(scrumTeam.getAccessRetrospective(), scrumTeamInDB.getAccessRetrospective());
		assertEquals(scrumTeam.getAccessUnplanItem(), scrumTeamInDB.getAccessUnplanItem());
		assertEquals(scrumTeam.getAccessReport(), scrumTeamInDB.getAccessReport());
		assertEquals(scrumTeam.getAccessEditProject(), scrumTeamInDB.getAccessEditProject());
		// Assert Stakeholder in DB
		ScrumRole stakeholderInDB = project.getScrumRole(RoleEnum.Stakeholder);
		assertEquals(stakeholder.getAccessProductBacklog(), stakeholderInDB.getAccessProductBacklog());
		assertEquals(stakeholder.getAccessSprintPlan(), stakeholderInDB.getAccessSprintPlan());
		assertEquals(stakeholder.getAccessTaskBoard(), stakeholderInDB.getAccessTaskBoard());
		assertEquals(stakeholder.getAccessSprintBacklog(), stakeholderInDB.getAccessSprintBacklog());
		assertEquals(stakeholder.getAccessReleasePlan(), stakeholderInDB.getAccessReleasePlan());
		assertEquals(stakeholder.getAccessRetrospective(), stakeholderInDB.getAccessRetrospective());
		assertEquals(stakeholder.getAccessUnplanItem(), stakeholderInDB.getAccessUnplanItem());
		assertEquals(stakeholder.getAccessReport(), stakeholderInDB.getAccessReport());
		assertEquals(stakeholder.getAccessEditProject(), stakeholderInDB.getAccessEditProject());
		// Assert guest in DB
		ScrumRole guestInDB = project.getScrumRole(RoleEnum.Guest);
		assertEquals(guest.getAccessProductBacklog(), guestInDB.getAccessProductBacklog());
		assertEquals(guest.getAccessSprintPlan(), guestInDB.getAccessSprintPlan());
		assertEquals(guest.getAccessTaskBoard(), guestInDB.getAccessTaskBoard());
		assertEquals(guest.getAccessSprintBacklog(), guestInDB.getAccessSprintBacklog());
		assertEquals(guest.getAccessReleasePlan(), guestInDB.getAccessReleasePlan());
		assertEquals(guest.getAccessRetrospective(), guestInDB.getAccessRetrospective());
		assertEquals(guest.getAccessUnplanItem(), guestInDB.getAccessUnplanItem());
		assertEquals(guest.getAccessReport(), guestInDB.getAccessReport());
		assertEquals(guest.getAccessEditProject(), guestInDB.getAccessEditProject());
	}

	@Test
	public void testCreateProjectRoleInProject() throws JSONException {
		// Test Data
		long projectId = mCP.getAllProjects().get(0).getId();
		String projectName = mCP.getAllProjects().get(0).getName();
		String userName = mCA.getAccountList().get(0).getUsername();
		String roleName = "ScrumTeam";

		JSONObject projectRoleJSON = new JSONObject();
		projectRoleJSON.put(AccountJSONEnum.USERNAME, userName);
		projectRoleJSON.put(ScrumRoleJSONEnum.ROLE, roleName);

		// Call '/projects/{projectId}/projectroles' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + projectId +
		              "/projectroles")
		        .request()
		        .header(SecurityModule.USERNAME_HEADER, SecurityModule.ADMIN_MD5_USERNAME)
		        .header(SecurityModule.PASSWORD_HEADER, SecurityModule.ADMIN_MD5_PASSWORD)
		        .post(Entity.text(projectRoleJSON.toString()));

		ArrayList<AccountObject> accounts = ProjectObject.get(projectId).getProjectWorkers();

		// Assert
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(1, accounts.size());
		assertEquals(userName, accounts.get(0).getUsername());
		assertEquals(roleName, accounts.get(0).getProjectRoleMap().get(projectName).getScrumRole().getRoleName());
	}

	@Test
	public void testCreateTagInProject() throws JSONException {
		// Test Data
		long projectId = mCP.getAllProjects().get(0).getId();
		String tagName = "TEST_TAG_NAME";

		JSONObject tagJSON = new JSONObject();
		tagJSON.put(TagJSONEnum.NAME, tagName);

		// Call '/projects/{projectId}/tags' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + projectId +
		              "/tags")
		        .request()
		        .header(SecurityModule.USERNAME_HEADER, SecurityModule.ADMIN_MD5_USERNAME)
		        .header(SecurityModule.PASSWORD_HEADER, SecurityModule.ADMIN_MD5_PASSWORD)
		        .post(Entity.text(tagJSON.toString()));

		ArrayList<TagObject> tags = ProjectObject.get(projectId).getTags();

		// Assert
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(1, tags.size());
		assertEquals(tagName, tags.get(0).getName());
	}
	
	@Test
	public void testCreateExistedTagInProject() throws JSONException {
		// Test Data
		long projectId = mCP.getAllProjects().get(0).getId();
		String tagName = "TEST_TAG_NAME";

		JSONObject tagJSON = new JSONObject();
		tagJSON.put(TagJSONEnum.NAME, tagName);

		// Call '/projects/{projectId}/tags' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + projectId +
		              "/tags")
		        .request()
		        .header(SecurityModule.USERNAME_HEADER, SecurityModule.ADMIN_MD5_USERNAME)
		        .header(SecurityModule.PASSWORD_HEADER, SecurityModule.ADMIN_MD5_PASSWORD)
		        .post(Entity.text(tagJSON.toString()));

		ArrayList<TagObject> tags = ProjectObject.get(projectId).getTags();

		// Assert
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(1, tags.size());
		assertEquals(tagName, tags.get(0).getName());
		
		// Call '/projects/{projectId}/tags' API again
		response = mClient.target(BASE_URL)
		        .path("projects/" + projectId +
		                "/tags")
		        .request()
		        .header(SecurityModule.USERNAME_HEADER, SecurityModule.ADMIN_MD5_USERNAME)
		        .header(SecurityModule.PASSWORD_HEADER, SecurityModule.ADMIN_MD5_PASSWORD)
		        .post(Entity.text(tagJSON.toString()));

		// Assert
		assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void testCreateProject_AccountIsInvalid() throws JSONException {
		String invalidUsername = "test";
		String invalidPassword = "test";
		
		// Test Data
		String projectName = "TEST_PROJECT_NAME";
		String projectDisplayName = "TEST_PROJECT_DISPLAY_NAME";
		String projectComment = "TEST_PROJECT_COMMENT";
		String projectProductOwner = "TEST_PROJECT_PRODUCT_OWNER";
		int projectMaxAttachFileSize = 2;
		long createTime = System.currentTimeMillis();

		JSONObject projectJSON = new JSONObject();
		projectJSON.put(ProjectJSONEnum.NAME, projectName);
		projectJSON.put(ProjectJSONEnum.DISPLAY_NAME, projectDisplayName);
		projectJSON.put(ProjectJSONEnum.COMMENT, projectComment);
		projectJSON.put(ProjectJSONEnum.PRODUCT_OWNER, projectProductOwner);
		projectJSON.put(ProjectJSONEnum.ATTATCH_MAX_SIZE, projectMaxAttachFileSize);
		projectJSON.put(ProjectJSONEnum.CREATE_TIME, createTime);

		// Call '/projects' API
		Response response = mClient.target(BASE_URL)
		        .path("projects")
		        .request()
		        .header(SecurityModule.USERNAME_HEADER, invalidUsername)
		        .header(SecurityModule.PASSWORD_HEADER, invalidPassword)
		        .post(Entity.text(projectJSON.toString()));

		JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
		JSONObject contentJSON = new JSONObject(jsonResponse.getString(ResponseJSONEnum.JSON_KEY_CONTENT));
		String message = jsonResponse.getString(ResponseJSONEnum.JSON_KEY_MESSAGE);

		// Assert
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
		assertEquals(new JSONObject().toString(), contentJSON.toString());
		assertEquals("", message);
	}
	
	@Test
	public void testCreateProject_AccountIsNull() throws JSONException {
		String nullUsername = null;
		String nullPassword = null;
		
		// Test Data
		String projectName = "TEST_PROJECT_NAME";
		String projectDisplayName = "TEST_PROJECT_DISPLAY_NAME";
		String projectComment = "TEST_PROJECT_COMMENT";
		String projectProductOwner = "TEST_PROJECT_PRODUCT_OWNER";
		int projectMaxAttachFileSize = 2;
		long createTime = System.currentTimeMillis();

		JSONObject projectJSON = new JSONObject();
		projectJSON.put(ProjectJSONEnum.NAME, projectName);
		projectJSON.put(ProjectJSONEnum.DISPLAY_NAME, projectDisplayName);
		projectJSON.put(ProjectJSONEnum.COMMENT, projectComment);
		projectJSON.put(ProjectJSONEnum.PRODUCT_OWNER, projectProductOwner);
		projectJSON.put(ProjectJSONEnum.ATTATCH_MAX_SIZE, projectMaxAttachFileSize);
		projectJSON.put(ProjectJSONEnum.CREATE_TIME, createTime);

		// Call '/projects' API
		Response response = mClient.target(BASE_URL)
		        .path("projects")
		        .request()
		        .header(SecurityModule.USERNAME_HEADER, nullUsername)
		        .header(SecurityModule.PASSWORD_HEADER, nullPassword)
		        .post(Entity.text(projectJSON.toString()));

		JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
		JSONObject contentJSON = new JSONObject(jsonResponse.getString(ResponseJSONEnum.JSON_KEY_CONTENT));
		String message = jsonResponse.getString(ResponseJSONEnum.JSON_KEY_MESSAGE);

		// Assert
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
		assertEquals(new JSONObject().toString(), contentJSON.toString());
		assertEquals("", message);
	}
	
	@Test
	public void testCreateProject_AccountIsEmpty() throws JSONException {
		String emptyUsername = "";
		String emptyPassword = "";
		
		// Test Data
		String projectName = "TEST_PROJECT_NAME";
		String projectDisplayName = "TEST_PROJECT_DISPLAY_NAME";
		String projectComment = "TEST_PROJECT_COMMENT";
		String projectProductOwner = "TEST_PROJECT_PRODUCT_OWNER";
		int projectMaxAttachFileSize = 2;
		long createTime = System.currentTimeMillis();

		JSONObject projectJSON = new JSONObject();
		projectJSON.put(ProjectJSONEnum.NAME, projectName);
		projectJSON.put(ProjectJSONEnum.DISPLAY_NAME, projectDisplayName);
		projectJSON.put(ProjectJSONEnum.COMMENT, projectComment);
		projectJSON.put(ProjectJSONEnum.PRODUCT_OWNER, projectProductOwner);
		projectJSON.put(ProjectJSONEnum.ATTATCH_MAX_SIZE, projectMaxAttachFileSize);
		projectJSON.put(ProjectJSONEnum.CREATE_TIME, createTime);

		// Call '/projects' API
		Response response = mClient.target(BASE_URL)
		        .path("projects")
		        .request()
		        .header(SecurityModule.USERNAME_HEADER, emptyUsername)
		        .header(SecurityModule.PASSWORD_HEADER, emptyPassword)
		        .post(Entity.text(projectJSON.toString()));

		JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
		JSONObject contentJSON = new JSONObject(jsonResponse.getString(ResponseJSONEnum.JSON_KEY_CONTENT));
		String message = jsonResponse.getString(ResponseJSONEnum.JSON_KEY_MESSAGE);

		// Assert
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
		assertEquals(new JSONObject().toString(), contentJSON.toString());
		assertEquals("", message);
	}
	
	@Test
	public void testCreateExistedProject_AccountIsInvalid() throws JSONException {
		String invalidUsername = "test";
		String invalidPassword = "test";
		
		// Test Data
		String projectName = mCP.getAllProjects().get(0).getName();
		String projectDisplayName = mCP.getAllProjects().get(0).getDisplayName();
		String projectComment = mCP.getAllProjects().get(0).getComment();
		String projectProductOwner = mCP.getAllProjects().get(0).getManager();
		int projectMaxAttachFileSize = (int) mCP.getAllProjects().get(0).getAttachFileSize();
		long createTime = mCP.getAllProjects().get(0).getCreateTime();

		JSONObject projectJSON = new JSONObject();
		projectJSON.put(ProjectJSONEnum.NAME, projectName);
		projectJSON.put(ProjectJSONEnum.DISPLAY_NAME, projectDisplayName);
		projectJSON.put(ProjectJSONEnum.COMMENT, projectComment);
		projectJSON.put(ProjectJSONEnum.PRODUCT_OWNER, projectProductOwner);
		projectJSON.put(ProjectJSONEnum.ATTATCH_MAX_SIZE, projectMaxAttachFileSize);
		projectJSON.put(ProjectJSONEnum.CREATE_TIME, createTime);

		// Call '/projects' API
		Response response = mClient.target(BASE_URL)
		        .path("projects")
		        .request()
		        .header(SecurityModule.USERNAME_HEADER, invalidUsername)
		        .header(SecurityModule.PASSWORD_HEADER, invalidPassword)
		        .post(Entity.text(projectJSON.toString()));
		
		JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
		JSONObject contentJSON = new JSONObject(jsonResponse.getString(ResponseJSONEnum.JSON_KEY_CONTENT));
		String message = jsonResponse.getString(ResponseJSONEnum.JSON_KEY_MESSAGE);

		// Assert
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
		assertEquals(new JSONObject().toString(), contentJSON.toString());
		assertEquals("", message);
	}
	
	@Test
	public void testCreateExistedProject_AccountIsNull() throws JSONException {
		String nullUsername = null;
		String nullPassword = null;
		
		// Test Data
		String projectName = mCP.getAllProjects().get(0).getName();
		String projectDisplayName = mCP.getAllProjects().get(0).getDisplayName();
		String projectComment = mCP.getAllProjects().get(0).getComment();
		String projectProductOwner = mCP.getAllProjects().get(0).getManager();
		int projectMaxAttachFileSize = (int) mCP.getAllProjects().get(0).getAttachFileSize();
		long createTime = mCP.getAllProjects().get(0).getCreateTime();

		JSONObject projectJSON = new JSONObject();
		projectJSON.put(ProjectJSONEnum.NAME, projectName);
		projectJSON.put(ProjectJSONEnum.DISPLAY_NAME, projectDisplayName);
		projectJSON.put(ProjectJSONEnum.COMMENT, projectComment);
		projectJSON.put(ProjectJSONEnum.PRODUCT_OWNER, projectProductOwner);
		projectJSON.put(ProjectJSONEnum.ATTATCH_MAX_SIZE, projectMaxAttachFileSize);
		projectJSON.put(ProjectJSONEnum.CREATE_TIME, createTime);

		// Call '/projects' API
		Response response = mClient.target(BASE_URL)
		        .path("projects")
		        .request()
		        .header(SecurityModule.USERNAME_HEADER, nullUsername)
		        .header(SecurityModule.PASSWORD_HEADER, nullPassword)
		        .post(Entity.text(projectJSON.toString()));
		
		JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
		JSONObject contentJSON = new JSONObject(jsonResponse.getString(ResponseJSONEnum.JSON_KEY_CONTENT));
		String message = jsonResponse.getString(ResponseJSONEnum.JSON_KEY_MESSAGE);

		// Assert
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
		assertEquals(new JSONObject().toString(), contentJSON.toString());
		assertEquals("", message);
	}
	
	@Test
	public void testCreateExistedProject_AccountIsEmpty() throws JSONException {
		String emptyUsername = "";
		String emptyPassword = "";
		
		// Test Data
		String projectName = mCP.getAllProjects().get(0).getName();
		String projectDisplayName = mCP.getAllProjects().get(0).getDisplayName();
		String projectComment = mCP.getAllProjects().get(0).getComment();
		String projectProductOwner = mCP.getAllProjects().get(0).getManager();
		int projectMaxAttachFileSize = (int) mCP.getAllProjects().get(0).getAttachFileSize();
		long createTime = mCP.getAllProjects().get(0).getCreateTime();

		JSONObject projectJSON = new JSONObject();
		projectJSON.put(ProjectJSONEnum.NAME, projectName);
		projectJSON.put(ProjectJSONEnum.DISPLAY_NAME, projectDisplayName);
		projectJSON.put(ProjectJSONEnum.COMMENT, projectComment);
		projectJSON.put(ProjectJSONEnum.PRODUCT_OWNER, projectProductOwner);
		projectJSON.put(ProjectJSONEnum.ATTATCH_MAX_SIZE, projectMaxAttachFileSize);
		projectJSON.put(ProjectJSONEnum.CREATE_TIME, createTime);

		// Call '/projects' API
		Response response = mClient.target(BASE_URL)
		        .path("projects")
		        .request()
		        .header(SecurityModule.USERNAME_HEADER, emptyUsername)
		        .header(SecurityModule.PASSWORD_HEADER, emptyPassword)
		        .post(Entity.text(projectJSON.toString()));
		
		JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
		JSONObject contentJSON = new JSONObject(jsonResponse.getString(ResponseJSONEnum.JSON_KEY_CONTENT));
		String message = jsonResponse.getString(ResponseJSONEnum.JSON_KEY_MESSAGE);

		// Assert
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
		assertEquals(new JSONObject().toString(), contentJSON.toString());
		assertEquals("", message);
	}
	
	@Test
	public void testCreateProjectRoleInProject_AccountIsInvalid() throws JSONException {
		String invalidUsername = "test";
		String invalidPassword = "test";
		
		// Test Data
		long projectId = mCP.getAllProjects().get(0).getId();
		String userName = mCA.getAccountList().get(0).getUsername();
		String roleName = "ScrumTeam";

		JSONObject projectRoleJSON = new JSONObject();
		projectRoleJSON.put(AccountJSONEnum.USERNAME, userName);
		projectRoleJSON.put(ScrumRoleJSONEnum.ROLE, roleName);

		// Call '/projects/{projectId}/projectroles' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + projectId +
		              "/projectroles")
		        .request()
		        .header(SecurityModule.USERNAME_HEADER, invalidUsername)
		        .header(SecurityModule.PASSWORD_HEADER, invalidPassword)
		        .post(Entity.text(projectRoleJSON.toString()));

		JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
		JSONObject contentJSON = new JSONObject(jsonResponse.getString(ResponseJSONEnum.JSON_KEY_CONTENT));
		String message = jsonResponse.getString(ResponseJSONEnum.JSON_KEY_MESSAGE);

		// Assert
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
		assertEquals(new JSONObject().toString(), contentJSON.toString());
		assertEquals("", message);
	}
	
	@Test
	public void testCreateProjectRoleInProject_AccountIsNull() throws JSONException {
		String nullUsername = null;
		String nullPassword = null;
		
		// Test Data
		long projectId = mCP.getAllProjects().get(0).getId();
		String userName = mCA.getAccountList().get(0).getUsername();
		String roleName = "ScrumTeam";

		JSONObject projectRoleJSON = new JSONObject();
		projectRoleJSON.put(AccountJSONEnum.USERNAME, userName);
		projectRoleJSON.put(ScrumRoleJSONEnum.ROLE, roleName);

		// Call '/projects/{projectId}/projectroles' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + projectId +
		              "/projectroles")
		        .request()
		        .header(SecurityModule.USERNAME_HEADER, nullUsername)
		        .header(SecurityModule.PASSWORD_HEADER, nullPassword)
		        .post(Entity.text(projectRoleJSON.toString()));


		JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
		JSONObject contentJSON = new JSONObject(jsonResponse.getString(ResponseJSONEnum.JSON_KEY_CONTENT));
		String message = jsonResponse.getString(ResponseJSONEnum.JSON_KEY_MESSAGE);

		// Assert
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
		assertEquals(new JSONObject().toString(), contentJSON.toString());
		assertEquals("", message);
	}
	
	@Test
	public void testCreateProjectRoleInProject_AccountIsEmpty() throws JSONException {
		String emptyUsername = "";
		String emptyPassword = "";
		
		// Test Data
		long projectId = mCP.getAllProjects().get(0).getId();
		String userName = mCA.getAccountList().get(0).getUsername();
		String roleName = "ScrumTeam";

		JSONObject projectRoleJSON = new JSONObject();
		projectRoleJSON.put(AccountJSONEnum.USERNAME, userName);
		projectRoleJSON.put(ScrumRoleJSONEnum.ROLE, roleName);

		// Call '/projects/{projectId}/projectroles' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + projectId +
		              "/projectroles")
		        .request()
		        .header(SecurityModule.USERNAME_HEADER, emptyUsername)
		        .header(SecurityModule.PASSWORD_HEADER, emptyPassword)
		        .post(Entity.text(projectRoleJSON.toString()));


		JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
		JSONObject contentJSON = new JSONObject(jsonResponse.getString(ResponseJSONEnum.JSON_KEY_CONTENT));
		String message = jsonResponse.getString(ResponseJSONEnum.JSON_KEY_MESSAGE);

		// Assert
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
		assertEquals(new JSONObject().toString(), contentJSON.toString());
		assertEquals("", message);
	}
	
	@Test
	public void testCreateTagInProject_AccountIsInvalid() throws JSONException {
		String invalidUsername = "test";
		String invalidPassword = "test";
		
		// Test Data
		long projectId = mCP.getAllProjects().get(0).getId();
		String tagName = "TEST_TAG_NAME";

		JSONObject tagJSON = new JSONObject();
		tagJSON.put(TagJSONEnum.NAME, tagName);

		// Call '/projects/{projectId}/tags' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + projectId +
		              "/tags")
		        .request()
		        .header(SecurityModule.USERNAME_HEADER, invalidUsername)
		        .header(SecurityModule.PASSWORD_HEADER, invalidPassword)
		        .post(Entity.text(tagJSON.toString()));

		JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
		JSONObject contentJSON = new JSONObject(jsonResponse.getString(ResponseJSONEnum.JSON_KEY_CONTENT));
		String message = jsonResponse.getString(ResponseJSONEnum.JSON_KEY_MESSAGE);

		// Assert
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
		assertEquals(new JSONObject().toString(), contentJSON.toString());
		assertEquals("", message);
	}
	
	@Test
	public void testCreateTagInProject_AccountIsInvalid_AccountIsNull() throws JSONException {
		String nullUsername = null;
		String nullPassword = null;
		
		// Test Data
		long projectId = mCP.getAllProjects().get(0).getId();
		String tagName = "TEST_TAG_NAME";

		JSONObject tagJSON = new JSONObject();
		tagJSON.put(TagJSONEnum.NAME, tagName);

		// Call '/projects/{projectId}/tags' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + projectId +
		              "/tags")
		        .request()
		        .header(SecurityModule.USERNAME_HEADER, nullUsername)
		        .header(SecurityModule.PASSWORD_HEADER, nullPassword)
		        .post(Entity.text(tagJSON.toString()));

		JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
		JSONObject contentJSON = new JSONObject(jsonResponse.getString(ResponseJSONEnum.JSON_KEY_CONTENT));
		String message = jsonResponse.getString(ResponseJSONEnum.JSON_KEY_MESSAGE);

		// Assert
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
		assertEquals(new JSONObject().toString(), contentJSON.toString());
		assertEquals("", message);
	}
	
	@Test
	public void testCreateTagInProject_AccountIsEmpty() throws JSONException {
		String emptyUsername = "";
		String emptyPassword = "";
		
		// Test Data
		long projectId = mCP.getAllProjects().get(0).getId();
		String tagName = "TEST_TAG_NAME";

		JSONObject tagJSON = new JSONObject();
		tagJSON.put(TagJSONEnum.NAME, tagName);

		// Call '/projects/{projectId}/tags' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + projectId +
		              "/tags")
		        .request()
		        .header(SecurityModule.USERNAME_HEADER, emptyUsername)
		        .header(SecurityModule.PASSWORD_HEADER, emptyPassword)
		        .post(Entity.text(tagJSON.toString()));

		JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
		JSONObject contentJSON = new JSONObject(jsonResponse.getString(ResponseJSONEnum.JSON_KEY_CONTENT));
		String message = jsonResponse.getString(ResponseJSONEnum.JSON_KEY_MESSAGE);

		// Assert
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
		assertEquals(new JSONObject().toString(), contentJSON.toString());
		assertEquals("", message);
	}
	
	@Test
	public void testCreateExistedTagInProject_AccountIsInvalid() throws JSONException {
		String invalidUsername = "test";
		String invalidPassword = "test";
		
		// Test Data
		long projectId = mCP.getAllProjects().get(0).getId();
		String tagName = "TEST_TAG_NAME";

		JSONObject tagJSON = new JSONObject();
		tagJSON.put(TagJSONEnum.NAME, tagName);

		// Call '/projects/{projectId}/tags' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + projectId +
		              "/tags")
		        .request()
		        .header(SecurityModule.USERNAME_HEADER, SecurityModule.ADMIN_MD5_USERNAME)
		        .header(SecurityModule.PASSWORD_HEADER, SecurityModule.ADMIN_MD5_PASSWORD)
		        .post(Entity.text(tagJSON.toString()));

		ArrayList<TagObject> tags = ProjectObject.get(projectId).getTags();

		// Assert
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(1, tags.size());
		assertEquals(tagName, tags.get(0).getName());
		
		// Call '/projects/{projectId}/tags' API again
		response = mClient.target(BASE_URL)
		        .path("projects/" + projectId +
		                "/tags")
		        .request()
		        .header(SecurityModule.USERNAME_HEADER, invalidUsername)
		        .header(SecurityModule.PASSWORD_HEADER, invalidPassword)
		        .post(Entity.text(tagJSON.toString()));

		JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
		JSONObject contentJSON = new JSONObject(jsonResponse.getString(ResponseJSONEnum.JSON_KEY_CONTENT));
		String message = jsonResponse.getString(ResponseJSONEnum.JSON_KEY_MESSAGE);

		// Assert
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
		assertEquals(new JSONObject().toString(), contentJSON.toString());
		assertEquals("", message);
	}
	
	@Test
	public void testCreateExistedTagInProject_AccountIsNull() throws JSONException {
		String nullUsername = null;
		String nullPassword = null;
		
		// Test Data
		long projectId = mCP.getAllProjects().get(0).getId();
		String tagName = "TEST_TAG_NAME";

		JSONObject tagJSON = new JSONObject();
		tagJSON.put(TagJSONEnum.NAME, tagName);

		// Call '/projects/{projectId}/tags' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + projectId +
		              "/tags")
		        .request()
		        .header(SecurityModule.USERNAME_HEADER, SecurityModule.ADMIN_MD5_USERNAME)
		        .header(SecurityModule.PASSWORD_HEADER, SecurityModule.ADMIN_MD5_PASSWORD)
		        .post(Entity.text(tagJSON.toString()));

		ArrayList<TagObject> tags = ProjectObject.get(projectId).getTags();

		// Assert
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(1, tags.size());
		assertEquals(tagName, tags.get(0).getName());
		
		// Call '/projects/{projectId}/tags' API again
		response = mClient.target(BASE_URL)
		        .path("projects/" + projectId +
		                "/tags")
		        .request()
		        .header(SecurityModule.USERNAME_HEADER, nullUsername)
		        .header(SecurityModule.PASSWORD_HEADER, nullPassword)
		        .post(Entity.text(tagJSON.toString()));

		JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
		JSONObject contentJSON = new JSONObject(jsonResponse.getString(ResponseJSONEnum.JSON_KEY_CONTENT));
		String message = jsonResponse.getString(ResponseJSONEnum.JSON_KEY_MESSAGE);

		// Assert
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
		assertEquals(new JSONObject().toString(), contentJSON.toString());
		assertEquals("", message);
	}
	
	@Test
	public void testCreateExistedTagInProject_AccountIsEmpty() throws JSONException {
		String emptyUsername = "";
		String emptyPassword = "";
		
		// Test Data
		long projectId = mCP.getAllProjects().get(0).getId();
		String tagName = "TEST_TAG_NAME";

		JSONObject tagJSON = new JSONObject();
		tagJSON.put(TagJSONEnum.NAME, tagName);

		// Call '/projects/{projectId}/tags' API
		Response response = mClient.target(BASE_URL)
		        .path("projects/" + projectId +
		              "/tags")
		        .request()
		        .header(SecurityModule.USERNAME_HEADER, SecurityModule.ADMIN_MD5_USERNAME)
		        .header(SecurityModule.PASSWORD_HEADER, SecurityModule.ADMIN_MD5_PASSWORD)
		        .post(Entity.text(tagJSON.toString()));

		ArrayList<TagObject> tags = ProjectObject.get(projectId).getTags();

		// Assert
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(1, tags.size());
		assertEquals(tagName, tags.get(0).getName());
		
		// Call '/projects/{projectId}/tags' API again
		response = mClient.target(BASE_URL)
		        .path("projects/" + projectId +
		                "/tags")
		        .request()
		        .header(SecurityModule.USERNAME_HEADER, emptyUsername)
		        .header(SecurityModule.PASSWORD_HEADER, emptyPassword)
		        .post(Entity.text(tagJSON.toString()));

		JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
		JSONObject contentJSON = new JSONObject(jsonResponse.getString(ResponseJSONEnum.JSON_KEY_CONTENT));
		String message = jsonResponse.getString(ResponseJSONEnum.JSON_KEY_MESSAGE);

		// Assert
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
		assertEquals(new JSONObject().toString(), contentJSON.toString());
		assertEquals("", message);
	}
}
