package ntut.csie.ezScrum.web.importer;

import java.util.ArrayList;

import ntut.csie.ezScrum.web.dataObject.ProjectObject;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class JsonParserTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testParseProject() {
		long createTime = System.currentTimeMillis();
		JSONObject projectJson = new JSONObject();
		projectJson.put(JsonParser.PROJECT_CREATE_TIME, createTime);
		projectJson.put(JsonParser.PROJECT_ATTACH_MAX_SIZE, 2L);
		projectJson.put(JsonParser.PROJECT_PRODUCT_OWNER, "Jay");
		projectJson.put(JsonParser.PROJECT_NAME, "Project1");
		projectJson.put(JsonParser.PROJECT_COMMENT, "The first project");
		projectJson.put(JsonParser.PROJECT_DISPLAY_NAME, "Project1 Display");
		ProjectObject project = JsonParser.parseProject(projectJson);
		Assert.assertEquals(createTime, project.getCreateTime());
		Assert.assertEquals(2, project.getAttachFileSize());
		Assert.assertEquals("Jay", project.getManager());
		Assert.assertEquals("Project1", project.getName());
		Assert.assertEquals("The first project", project.getComment());
		Assert.assertEquals("Project1 Display", project.getDisplayName());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testParseProjects() {
		long createTime = System.currentTimeMillis();
		JSONObject projectJson1 = new JSONObject();
		projectJson1.put(JsonParser.PROJECT_CREATE_TIME, createTime);
		projectJson1.put(JsonParser.PROJECT_ATTACH_MAX_SIZE, 2L);
		projectJson1.put(JsonParser.PROJECT_PRODUCT_OWNER, "Jay");
		projectJson1.put(JsonParser.PROJECT_NAME, "Project1");
		projectJson1.put(JsonParser.PROJECT_COMMENT, "The first project");
		projectJson1.put(JsonParser.PROJECT_DISPLAY_NAME, "Project1 Display");
		
		JSONObject projectJson2 = new JSONObject();
		projectJson2.put(JsonParser.PROJECT_CREATE_TIME, createTime);
		projectJson2.put(JsonParser.PROJECT_ATTACH_MAX_SIZE, 2L);
		projectJson2.put(JsonParser.PROJECT_PRODUCT_OWNER, "Henry");
		projectJson2.put(JsonParser.PROJECT_NAME, "Project2");
		projectJson2.put(JsonParser.PROJECT_COMMENT, "The second project");
		projectJson2.put(JsonParser.PROJECT_DISPLAY_NAME, "Project2 Display");
		
		JSONArray jsonArray = new JSONArray();
		jsonArray.add(projectJson1);
		jsonArray.add(projectJson2);
		
		ArrayList<ProjectObject> projects = JsonParser.parseProjects(jsonArray);
		Assert.assertEquals(createTime, projects.get(0).getCreateTime());
		Assert.assertEquals(2, projects.get(0).getAttachFileSize());
		Assert.assertEquals("Jay", projects.get(0).getManager());
		Assert.assertEquals("Project1", projects.get(0).getName());
		Assert.assertEquals("The first project", projects.get(0).getComment());
		Assert.assertEquals("Project1 Display", projects.get(0).getDisplayName());
		
		Assert.assertEquals(createTime, projects.get(1).getCreateTime());
		Assert.assertEquals(2, projects.get(1).getAttachFileSize());
		Assert.assertEquals("Henry", projects.get(1).getManager());
		Assert.assertEquals("Project2", projects.get(1).getName());
		Assert.assertEquals("The second project", projects.get(1).getComment());
		Assert.assertEquals("Project2 Display", projects.get(1).getDisplayName());
	}
}
