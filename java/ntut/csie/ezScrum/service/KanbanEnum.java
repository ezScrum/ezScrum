package ntut.csie.ezScrum.service;

public class KanbanEnum {
	final public static String KANBAN = "Kanban";
	
	// Kanban 所使用的 IssueType
	final public static String ISSUETYPE_WORKITEM = "WorkItem";
	// Custom fields - Type + Status + Priority + WorkState + Size + Deadline + Handler
	final public static String FIELD_WORKITEM_TYPE = "Type";
	final public static String FIELD_WORKITEM_STATUS = "Status";
	final public static String FIELD_WORKITEM_PRIORITY = "Priority";
	final public static String FIELD_WORKITEM_WORKSTATE = "WorkState";
	final public static String FIELD_WORKITEM_SIZE = "Size";
	final public static String FIELD_WORKITEM_DEADLINE = "Deadline";
	final public static String FIELD_WORKITEM_HANDLER = "Handler";

	final public static String ISSUETYPE_STATUS = "Status";
	// Custom fields - Limit
	final public static String FIELD_STATUS_LIMIT = "Limit";
	
	// Workitem 的 Status
	final public static String WORKITEM_BACKLOG_STATUS = "Backlog";
	final public static String WORKITEM_LIVE_STATUS = "Live";
	
	// Kanban Role Permission
	final public static String ACCESS_KANBANBACKLOG = "AccessKanbanBacklog";
	final public static String ACCESS_MANAGESTATUS = "AccessManageStatus";
	final public static String ACCESS_KANBANBOARD = "AccessKanbanBoard";
	final public static String ACCESS_KANBANREPORT = "AccessKanbanReport";
	// Kanban file file tag
	//final public static String KANBANROLE_FILE_TAG_PERMISSION = "Permission";
	
	// Workitem 的 WorkState
	public static enum WorkState {
	    New("New"),
	    Assigned("Assigned"),
	    Closed("Closed"),
	    Blocked("Blocked");

	    WorkState(String key_id) {
	        this.key_id = key_id;
	    }

	    private final String key_id;

	    public String toString() {
	        return key_id;
	    }
	}
	
	// Workitem 的 Type
	public static enum Type {
	    UserStory("User Story"),
	    Task("Task");

	    Type(String key_id) {
	        this.key_id = key_id;
	    }

	    private final String key_id;

	    public String toString() {
	        return key_id;
	    }
	}
	
	// Workitem 的 Priotrity
	public static enum Priority {
	    High("3"),
	    Medium("2"),
	    Low("1"),
	    None("0");

	    Priority(String key_id) {
	        this.key_id = key_id;
	    }

	    private final String key_id;

	    public String getKey_id() {
	        return key_id;
	    }
	}
	
	public static KanbanEnum.Priority getPriority(String name){
		if (name.equals(KanbanEnum.Priority.High.toString()))
			return KanbanEnum.Priority.High;
		else if (name.equals(KanbanEnum.Priority.Medium.toString()))
			return KanbanEnum.Priority.Medium;
		else if (name.equals(KanbanEnum.Priority.Medium.toString()))
			return KanbanEnum.Priority.Low;
		else
			return KanbanEnum.Priority.None;
	}
}
