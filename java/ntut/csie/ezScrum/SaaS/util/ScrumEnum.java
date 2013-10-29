package ntut.csie.ezScrum.SaaS.util;

public interface ScrumEnum {
	
	// ==================== Permission ================================
	//	多租戶才有的
	final public static String TENANT_PERMISSION = "system_tenantAdmin";
	//	web
	final public static String CREATEPROJECT_PERMISSION = "system_createProject";
	final public static String ADMINISTRATOR_PERMISSION = "system_admin";
	// ==================== Permission ================================
	
	final public static String ITER_PLAN_FILE = "iterPlan.xml";
	final public static String RELEASE_PLAN_FILE = "releasePlan.xml";

	final public static String PLAN_TAG = "Plan";
	final public static String SPRINT_TAG = "Sprint";
	final public static String START_DATE_TAG = "Start";
	final public static String INTERVAL_TAG = "Interval";
	final public static String NUMBER_MEMBER_TAG ="MemNumber";
	final public static String FOCUSFACTOR_TAG = "FocusFactor";
	final public static String GOAL_TAG = "Goal";
	final public static String AVAILABLE_DAYS_TAG = "AvailableDays";
	final public static String NOTES_TAG = "Notes";
	final public static String DEMO_DATE_TAG = "DemoDate";
	final public static String DEMO_PLACE_TAG = "DemoPlace";	
	final public static String ROOT_TAG = "root";
	final public static String HISTORY_TAG = "JCIS";
	final public static String ID_HISTORY_ATTR = "id";
	final public static String TYPE_HISTORY_ATTR = "type";
	final public static String STRING_BLANK_VALUE = "";
	final public static String DIGITAL_BLANK_VALUE = "0";
	final public static String ID_ATTR = "id";

	// ==================== releasePlan.xml ================================
	final public static String ID_Release_ATTR="id";
	final public static String RELEASE_TAG = "Release";
	final public static String RELEASES = "Releases";
	final public static String RELEASE_NAME = "Name";
	final public static String RELEASE_START_DATE = "StartDate";
	final public static String RELEASE_END_DATE = "EndDate";
	final public static String RELEASE_DESCRIPTION = "Description";
	final public static String RELEASE_SPRINTS_TAG = "Sprints";
	final public static String RELEASE_SPRINT = "Sprint";
	// ==================== releasePlan.xml ================================
	
	final public long DAY_MILLISECOND = 24 * 3600 * 1000 - 1;
	final public int WEEK_DAY = 7;
	
	//資料的欄位
	final public static String SPRINT_ID = "Iteration";
	final public static String VALUE = "Value";
	final public static String ESTIMATION = "Estimation";
	final public static String IMPORTANCE = "Importance";
	final public static String HOWTODEMO = "HowToDemo";
	final public static String NOTES = "Notes";
	final public static String PARTNERS = "Partners";
	final public static String ACTUALHOUR = "ActualHour";
	final public static String SPECIFICTIME="SpecificTime";
	final public static String STATUS="Status";
	final public static String REMAINS = "Remains";
	final public static String HANDLER = "Handler";
	
	//issue's category
	final public static String GOOD_ISSUE_TYPE = "Good";
	final public static String UNPLANNEDITEM_ISSUE_TYPE = "UnplannedItem";
	final public static String IMPROVEMENTS_ISSUE_TYPE = "Improvement";
	final public static String STORY_ISSUE_TYPE = "Story";
	final public static String TASK_ISSUE_TYPE = "Task";
	final public static String BUG_ISSUE_TYPE="Bug";
	final public static String ISSUE_ISSUE_TYPE="Issue";
	final public static String FEATURE_ISSUE_TYPE="Feature";
	final public static String REMAININGWORK="RemainingWork";
	
	final public static String STORYNAME="Name";
	//project's type
	final public static String PROJECT_STATUS = "10";
	final public static String PROJECT_ENABLED = "1";
	final public static String PROJECT_VIEW_STATE = "50";
	final public static String PROJECT_ACCESS_MIN = "10";
	final public static String PROJECT_DEFAULT_ISSUE = "1";
	
	//ScrumRole
	final public static String SYSTEM = "system";
	final public static String SCRUMROLE_ADMIN = "admin";
	final public static String SCRUMROLE_ADMINISTRATOR = "administrator";
	final public static String SCRUMROLE_PRODUCTOWNER = "ProductOwner";
	final public static String SCRUMROLE_SCRUMMASTER = "ScrumMaster";
	final public static String SCRUMROLE_SCRUMTEAM= "ScrumTeam";
	final public static String SCRUMROLE_STAKEHOLDER = "Stakeholder";
	final public static String SCRUMROLE_GUEST = "Guest";
	
	//Scrum Role permission
	final public static String ACCESS_PRODUCTBACKLOG = "AccessProductBacklog";
	final public static String ACCESS_RELEASEPLAN = "AccessReleasePlan";
	final public static String ACCESS_SPRINTPLAN = "AccessSprintPlan";
	final public static String ACCESS_SPRINTBACKLOG = "AccessSprintBacklog";
	final public static String ACCESS_TASKBOARD = "AccessTaskboard";
	final public static String ACCESS_UNPLANNED = "AccessUnplanned";
	final public static String ACCESS_RETROSPECTIVE = "AccessRetrospective";
	final public static String ACCESS_REPORT = "AccessReport";
	final public static String ACCESS_EDITPROJECT = "AccessEditProject";
	//Scrum Role file	
	final public static String SCRUMROLE_FILE = "ScrumRole.xml";
	//Scrum file file tag
	final public static String SCRUMROLE_FILE_TAG_PERMISSION = "Permission";
	
	// product backlog filter type
	final public static String BACKLOG = "BACKLOG";
	final public static String DETAIL = "DETAIL";
	final public static String DONE = "DONE";
	final public static String FILTER_NANE = "STORY_NAME";
	final public static String FILTER_DESCRIPTION = "STORY_DESC";
	final public static String FILTER_HANDLER = "STORY_HANDLER";
	
	//story
	public final static String EMPTY_SPRINT_ID = "-1";
	public final static String EMPTY_RELEASE_ID = "-1";
	public final static String EMPTY_PARENT_ID = "-1";
}