package ntut.csie.ezScrum.restful.dataMigration.jsonEnum;

import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.databaseEnum.HistoryEnum;

public class HistoryJSONEnum {
	public static final String ISSUE_TYPE = HistoryEnum.ISSUE_TYPE;
	public static final String HISTORY_TYPE = HistoryEnum.HISTORY_TYPE;
	public static final String OLD_VALUE = HistoryEnum.OLD_VALUE;
	public static final String NEW_VALUE = HistoryEnum.NEW_VALUE;
	public static final String CREATE_TIME = HistoryEnum.CREATE_TIME;
	
	// History Type
	public enum HistoryType {
		CREATE(HistoryObject.TYPE_CREATE),
		NAME(HistoryObject.TYPE_NAME),
		ESTIMATE(HistoryObject.TYPE_ESTIMATE),
		REMAINS(HistoryObject.TYPE_REMAIMS),
		ACTUAL(HistoryObject.TYPE_ACTUAL),
		IMPORTANCE(HistoryObject.TYPE_IMPORTANCE),
		VALUE(HistoryObject.TYPE_VALUE),
		ATTACH_FILE(HistoryObject.TYPE_ATTACH_FILE),
		STATUS(HistoryObject.TYPE_STATUS),
		HANDLER(HistoryObject.TYPE_HANDLER),
		SPECIFIC_TIME(HistoryObject.TYPE_SPECIFIC_TIME),
		DROP_TASK(HistoryObject.TYPE_DROP),
		APPEND_TO_SPRINT(HistoryObject.TYPE_APPEND),
		APPEND_TO_STORY(HistoryObject.TYPE_APPEND),
		ADD_TASK(HistoryObject.TYPE_ADD),
		REMOVE_FROM_SPRINT(HistoryObject.TYPE_REMOVE),
		REMOVE_FROM_STORY(HistoryObject.TYPE_REMOVE),
		NOTE(HistoryObject.TYPE_NOTE),
		HOW_TO_DEMO(HistoryObject.TYPE_HOW_TO_DEMO),
		ADD_PARTNER(HistoryObject.TYPE_ADD_PARTNER),
		REMOVE_PARTNER(HistoryObject.TYPE_REMOVE_PARTNER),
		UNPLAN_CHANGE_SPRINT_ID(HistoryObject.TYPE_SPRINT_ID);
		
		private int mIndex;
		private HistoryType(int index){
			mIndex = index;
		}
		public int getIndex(){
			return mIndex;
		}
	}
	
	// Task, Unplan status in new version
	public final static String TASK_UNPLAN_STATUS_UNCHECK = "new";
	public final static String TASK_UNPLAN_STATUS_CHECK = "assigned";
	public final static String TASK_UNPLAN_STATUS_DONE = "closed";
	
	// Story status in new version
	public final static String STORY_STATUS_UNCHECK = "new";
	public final static String STORY_STATUS_DONE = "closed";
	
	// Retrospective status in new version
	public final static String RETROSPECTIVE_STATUS_NEW = "new";
	public final static String RETROSPECTIVE_STATUS_ASSIGNED = "assigned";
	public final static String RETROSPECTIVE_STATUS_CLOSED = "closed";
	public final static String RETROSPECTIVE_STATUS_RESOLVED = "resolved";
}
