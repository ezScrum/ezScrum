package ntut.csie.ezScrum.restful.dataMigration.support;

import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.HistoryJSONEnum;
import ntut.csie.ezScrum.web.databaseEnum.StatusEnum;

public class StatusTranslator {
	public static int getStatusByStatusString(String statusString) {
		if (statusString == null || statusString.isEmpty()) {
			return -1;
		}
		if (statusString.equals(HistoryJSONEnum.STATUS_NEW)) {
			return StatusEnum.NEW;
		} else if (statusString.equals(HistoryJSONEnum.STATUS_ASSIGNED)) {
			return StatusEnum.ASSIGNED;
		} else if (statusString.equals(HistoryJSONEnum.STATUS_CLOSED)) {
			return StatusEnum.CLOSED;
		}
		return -1;
	}
}
