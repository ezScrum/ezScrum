package ntut.csie.ezScrum.restful.dataMigration.support;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.dataObject.UnplanObject;

public class ResourceFinderTest {
	private Configuration mConfig;

	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
	}

	@After
	public void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		// 讓 config 回到 Production 模式
		mConfig.setTestMode(false);
		mConfig.save();

		mConfig = null;
	}
	
	@Test
	public void testFindProject() {
		ProjectObject project = new ProjectObject("Project1");
		project.save();
		ResourceFinder resourceFinder = new ResourceFinder();
		assertNull(resourceFinder.findProject(-1));
		assertNotNull(resourceFinder.findProject(project.getId()));
	}
	
	@Test
	public void testFindSprint() {
		ProjectObject project = new ProjectObject("Project1");
		project.save();
		SprintObject sprint = new SprintObject(project.getId());
		sprint.save();
		ResourceFinder resourceFinder = new ResourceFinder();
		assertNotNull(resourceFinder.findProject(project.getId()));
		assertNull(resourceFinder.findSprint(-1));
		assertNotNull(resourceFinder.findSprint(sprint.getId()));
	}
	
	@Test
	public void testFindStory() {
		ProjectObject project = new ProjectObject("Project1");
		project.save();
		SprintObject sprint = new SprintObject(project.getId());
		sprint.save();
		StoryObject story = new StoryObject(project.getId());
		story.setSprintId(sprint.getId()).save();
		ResourceFinder resourceFinder = new ResourceFinder();
		assertNotNull(resourceFinder.findProject(project.getId()));
		assertNotNull(resourceFinder.findSprint(sprint.getId()));
		assertNull(resourceFinder.findStory(-1));
		assertNotNull(resourceFinder.findStory(story.getId()));
	}
	
	@Test
	public void testFindTask() {
		ProjectObject project = new ProjectObject("Project1");
		project.save();
		SprintObject sprint = new SprintObject(project.getId());
		sprint.save();
		StoryObject story = new StoryObject(project.getId());
		story.setSprintId(sprint.getId()).save();
		TaskObject task = new TaskObject(project.getId());
		task.setStoryId(story.getId()).save();
		ResourceFinder resourceFinder = new ResourceFinder();
		assertNotNull(resourceFinder.findProject(project.getId()));
		assertNotNull(resourceFinder.findSprint(sprint.getId()));
		assertNotNull(resourceFinder.findStory(story.getId()));
		assertNull(resourceFinder.findTask(-1));
		assertNotNull(resourceFinder.findTask(task.getId()));
	}
	
	@Test
	public void testFindTaskInDroppedStory() {
		ProjectObject project = new ProjectObject("Project1");
		project.save();
		StoryObject story = new StoryObject(project.getId());
		story.save();
		TaskObject task = new TaskObject(project.getId());
		task.setStoryId(story.getId()).save();
		ResourceFinder resourceFinder = new ResourceFinder();
		assertNotNull(resourceFinder.findProject(project.getId()));
		assertNotNull(resourceFinder.findDroppedStory(story.getId()));
		assertNull(resourceFinder.findTaskInDroppedStory(-1));
		assertNotNull(resourceFinder.findTaskInDroppedStory(task.getId()));
	}
	
	@Test
	public void testFindUnplan() {
		ProjectObject project = new ProjectObject("Project1");
		project.save();
		SprintObject sprint = new SprintObject(project.getId());
		sprint.save();
		UnplanObject unplan = new UnplanObject(sprint.getId(), project.getId());
		unplan.save();
		ResourceFinder resourceFinder = new ResourceFinder();
		assertNotNull(resourceFinder.findProject(project.getId()));
		assertNotNull(resourceFinder.findSprint(sprint.getId()));
		assertNull(resourceFinder.findUnplan(-1));
		assertNotNull(resourceFinder.findUnplan(unplan.getId()));
	}
	
	@Test
	public void testFindDroppedStory() {
		ProjectObject project = new ProjectObject("Project1");
		project.save();
		StoryObject story = new StoryObject(project.getId());
		story.save();
		ResourceFinder resourceFinder = new ResourceFinder();
		assertNotNull(resourceFinder.findProject(project.getId()));
		assertNull(resourceFinder.findDroppedStory(-1));
		assertNotNull(resourceFinder.findDroppedStory(story.getId()));
	}
	
	@Test
	public void testFindDroppedTask() {
		ProjectObject project = new ProjectObject("Project1");
		project.save();
		TaskObject task = new TaskObject(project.getId());
		task.save();
		ResourceFinder resourceFinder = new ResourceFinder();
		assertNotNull(resourceFinder.findProject(project.getId()));
		assertNull(resourceFinder.findDroppedTask(-1));
		assertNotNull(resourceFinder.findDroppedTask(task.getId()));
	}
}
