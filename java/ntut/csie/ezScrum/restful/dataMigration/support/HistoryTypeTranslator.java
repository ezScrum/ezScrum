package ntut.csie.ezScrum.restful.dataMigration.support;

import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.HistoryJSONEnum;

public class HistoryTypeTranslator {
	public static int getHistoryType(String historyTypeString) {
		int historyType = HistoryJSONEnum.HistoryType.valueOf(historyTypeString).getIndex();
		return historyType;
	}
}
