package ntut.csie.ezScrum.web.importer;

import ntut.csie.ezScrum.web.dataObject.ProjectObject;

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
}
