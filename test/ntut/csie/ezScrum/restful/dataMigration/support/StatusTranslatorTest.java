package ntut.csie.ezScrum.restful.dataMigration.support;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ntut.csie.ezScrum.web.databaseEnum.StatusEnum;

public class StatusTranslatorTest {
	@Test
	public void testGetStatusByStatusString() {
		assertEquals(-1, StatusTranslator.getStatusByStatusString(""));
		assertEquals(-1, StatusTranslator.getStatusByStatusString(null));
		assertEquals(StatusEnum.NEW, StatusTranslator.getStatusByStatusString("new"));
		assertEquals(StatusEnum.ASSIGNED, StatusTranslator.getStatusByStatusString("assigned"));
		assertEquals(StatusEnum.CLOSED, StatusTranslator.getStatusByStatusString("closed"));
		assertEquals(-1, StatusTranslator.getStatusByStatusString("resolved"));
	}
}
