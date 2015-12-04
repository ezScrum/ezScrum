package ntut.csie.ezScrum.restful.dataMigration.support;

import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.HistoryJSONEnum;
import ntut.csie.ezScrum.web.dataObject.StoryObject;

public class StatusTranslator {
	/**
	 * 將 History 
	 * @param statusName from HistoryJSONEnum
	 */
	public static int getStoryStatus(String statusName){
		if (statusName.equals(HistoryJSONEnum.STORY_STATUS_UNCHECK)) {
			return StoryObject.STATUS_UNCHECK;
		} else if (statusName.equals(HistoryJSONEnum.STORY_STATUS_DONE)) {
			return StoryObject.STATUS_DONE;
		}
		return -1;
	}
}
