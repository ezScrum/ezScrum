package ntut.csie.ezScrum.restful.dataMigration.support;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;

public class StatusTranslatorTest {
	@Test
	public void testGetStoryStatus() {
		assertEquals(-1, StatusTranslator.getStoryStatus(""));
		assertEquals(-1, StatusTranslator.getStoryStatus(null));
		assertEquals(StoryObject.STATUS_UNCHECK, StatusTranslator.getStoryStatus("new"));
		assertEquals(-1, StatusTranslator.getStoryStatus("assigned"));
		assertEquals(StoryObject.STATUS_DONE, StatusTranslator.getStoryStatus("closed"));
		assertEquals(-1, StatusTranslator.getStoryStatus("resolved"));
	}
	
	@Test
	public void testGetTaskStatus() {
		assertEquals(-1, StatusTranslator.getTaskStatus(""));
		assertEquals(-1, StatusTranslator.getTaskStatus(null));
		assertEquals(TaskObject.STATUS_UNCHECK, StatusTranslator.getTaskStatus("new"));
		assertEquals(TaskObject.STATUS_CHECK, StatusTranslator.getTaskStatus("assigned"));
		assertEquals(TaskObject.STATUS_DONE, StatusTranslator.getTaskStatus("closed"));
		assertEquals(-1, StatusTranslator.getTaskStatus("resolved"));
	}
}
