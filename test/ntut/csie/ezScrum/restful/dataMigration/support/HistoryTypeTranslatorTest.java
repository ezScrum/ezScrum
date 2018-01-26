package ntut.csie.ezScrum.restful.dataMigration.support;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ntut.csie.ezScrum.web.dataObject.HistoryObject;

public class HistoryTypeTranslatorTest {
	@Test
	public void testGetHistoryType() {
		assertEquals(HistoryObject.TYPE_CREATE, HistoryTypeTranslator.getHistoryType("CREATE"));
		assertEquals(HistoryObject.TYPE_NAME, HistoryTypeTranslator.getHistoryType("NAME"));
		assertEquals(HistoryObject.TYPE_ESTIMATE, HistoryTypeTranslator.getHistoryType("ESTIMATE"));
		assertEquals(HistoryObject.TYPE_REMAIMS, HistoryTypeTranslator.getHistoryType("REMAINS"));
		//assertEquals(HistoryObject.TYPE_ACTUAL, HistoryTypeTranslator.getHistoryType("ACTUAL"));
		assertEquals(HistoryObject.TYPE_IMPORTANCE, HistoryTypeTranslator.getHistoryType("IMPORTANCE"));
		assertEquals(HistoryObject.TYPE_VALUE, HistoryTypeTranslator.getHistoryType("VALUE"));
		assertEquals(HistoryObject.TYPE_ATTACH_FILE, HistoryTypeTranslator.getHistoryType("ATTACH_FILE"));
		assertEquals(HistoryObject.TYPE_STATUS, HistoryTypeTranslator.getHistoryType("STATUS"));
		assertEquals(HistoryObject.TYPE_HANDLER, HistoryTypeTranslator.getHistoryType("HANDLER"));
		assertEquals(HistoryObject.TYPE_SPECIFIC_TIME, HistoryTypeTranslator.getHistoryType("SPECIFIC_TIME"));
		assertEquals(HistoryObject.TYPE_DROP, HistoryTypeTranslator.getHistoryType("DROP_TASK"));
		assertEquals(HistoryObject.TYPE_APPEND, HistoryTypeTranslator.getHistoryType("APPEND_TO_SPRINT"));
		assertEquals(HistoryObject.TYPE_APPEND, HistoryTypeTranslator.getHistoryType("APPEND_TO_STORY"));
		assertEquals(HistoryObject.TYPE_ADD, HistoryTypeTranslator.getHistoryType("ADD_TASK"));
		assertEquals(HistoryObject.TYPE_REMOVE, HistoryTypeTranslator.getHistoryType("REMOVE_FROM_SPRINT"));
		assertEquals(HistoryObject.TYPE_REMOVE, HistoryTypeTranslator.getHistoryType("REMOVE_FROM_STORY"));
		assertEquals(HistoryObject.TYPE_NOTE, HistoryTypeTranslator.getHistoryType("NOTE"));
		assertEquals(HistoryObject.TYPE_HOW_TO_DEMO, HistoryTypeTranslator.getHistoryType("HOW_TO_DEMO"));
		assertEquals(HistoryObject.TYPE_ADD_PARTNER, HistoryTypeTranslator.getHistoryType("ADD_PARTNER"));
		assertEquals(HistoryObject.TYPE_REMOVE_PARTNER, HistoryTypeTranslator.getHistoryType("REMOVE_PARTNER"));
		assertEquals(HistoryObject.TYPE_SPRINT_ID, HistoryTypeTranslator.getHistoryType("UNPLAN_CHANGE_SPRINT_ID"));
	}
}
