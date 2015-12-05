package ntut.csie.ezScrum.restful.dataMigration.support;

import ntut.csie.ezScrum.restful.dataMigration.jsonEnum.HistoryJSONEnum;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;

public class StatusTranslator {
	/**
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
	
	public static int getTaskStatus(String statusName){
		if (statusName.equals(HistoryJSONEnum.TASK_UNPLAN_STATUS_UNCHECK)) {
			return TaskObject.STATUS_UNCHECK;
		} else if (statusName.equals(HistoryJSONEnum.TASK_UNPLAN_STATUS_CHECK)) {
			return TaskObject.STATUS_CHECK;
		} else if (statusName.equals(HistoryJSONEnum.TASK_UNPLAN_STATUS_DONE)) {
			return TaskObject.STATUS_DONE;
		}
		return -1;
	}
}
