package ntut.csie.ezScrum.web.export;

import java.util.Date;

import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.jcis.core.util.DateUtil;

import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class JsonTranslaterTest {
	@Test
	public void testTranslateProject() {
		ProjectObject project = new ProjectObject();
		project.setName("projectName");
		project.setDisplayName("projectDisplayName");
		project.setComment("projectComment");
		project.setManager("ProjectOwner");
		project.setAttachFileSize("2");
		Date date = DateUtil.dayFillter("2015/05/13", DateUtil._8DIGIT_DATE_1);
		project.setCreateDate(date.getTime());
		JSONObject jsonObject = JsonTranslater.translateProject(project);
		Assert.assertEquals("projectName", jsonObject.get("name"));
		Assert.assertEquals("projectDisplayName", jsonObject.get("displayName"));
		Assert.assertEquals("projectComment", jsonObject.get("comment"));
		Assert.assertEquals("ProjectOwner", jsonObject.get("productOwner"));
		Assert.assertEquals("2", jsonObject.get("attachMaxSize"));
		Assert.assertEquals(date.getTime(), jsonObject.get("createTime"));
	}
}
