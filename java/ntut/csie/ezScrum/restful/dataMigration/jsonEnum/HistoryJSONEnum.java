package ntut.csie.ezScrum.restful.dataMigration.jsonEnum;

public class HistoryJSONEnum {
	public static final String ISSUE_TYPE = "issue_type";
	public static final String HISTORY_TYPE = "type";
	public static final String OLD_VALUE = "old_value";
	public static final String NEW_VALUE = "new_value";
	public static final String CREATE_TIME = "create_time";
	
	// History Type
	public enum HistoryType {
		CREATE,
		NAME,
		ESTIMATE,
		REMAINS,
		ACTUAL,
		IMPORTANCE,
		VALUE,
		ATTACH_FILE,
		STATUS,
		HANDLER,
		SPECIFIC_TIME,
		DROP_TASK,
		APPEND_TO_SPRINT,
		APPEND_TO_STORY,
		ADD_TASK,
		REMOVE_FROM_SPRINT,
		REMOVE_FROM_STORY,
		NOTE,
		HOW_TO_DEMO,
		ADD_PARTNER,
		REMOVE_PARTNER,
		UNPLAN_CHANGE_SPRINT_ID
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
