package ntut.csie.ezScrum.issue.core;

public class ITSEnum {
	final public static String MANTIS_SERVICE_ID = "mantis";
	
	final public static String WORKING = "working";
	final public static String RESOLVED = "resolved";
	final public static String CLOSED = "closed";
	final public static String TOTAL = "total";
	
	final public static String DONE="done";
	final public static String NONASSIGN="nonAssign";
	final public static String ASSIGNED="assigned";
		
	//產生Table的SQL指令
	final public static String EZSCRUM_STORY_RELATION = "ezscrum_story_relation";
	final public static String EZSCRUM_TAG_RELATION = "ezscrum_tag_relation";
	final public static String EZSCRUM_TAG_TABLE = "ezscrum_tag_table";
	
	// ezTrack additional table
	final public static String EZTRACK_COMBOFIELD = "eztrack_combofield";
	final public static String EZTRACK_ISSUERELATION = "eztrack_issuerelation";
	final public static String EZTRACK_ISSUETYPE = "eztrack_issuetype";
	final public static String EZTRACK_REPORT = "eztrack_report";
	final public static String EZTRACK_TYPEFIELD = "eztrack_typefield";
	final public static String EZTRACK_TYPEFIELDVALUE = "eztrack_typefieldvalue";
	
	// ezKanban additional table
	final public static String EZKANBAN_STATUSORDER = "ezkanban_statusorder";
	
	// Dod tool additional table
	final public static String DOD_COMMIT_LOG = "commit_log";
	final public static String DOD_COMMIT_STORY_RELATION = "commit_story_relation";
	final public static String DOD_QUERY = "query";
	final public static String DOD_BUILDRESULT = "buildresult";
	
	// for integer
	final public static int VIEWER_ACCESS_LEVEL = 10;
	final public static int REPORTER_ACCESS_LEVEL = 25;
	final public static int UPDATER_ACCESS_LEVEL = 40;
	final public static int DEVELOPER_ACCESS_LEVEL = 55;
	final public static int MANAGER_ACCESS_LEVEL = 70;
	final public static int ADMINISTRATOR_ACCESS_LEVEL = 90;

	final public static int PUBLIC_VIEW_STATUS = 10;
	final public static int PRIVATE_VIEW_STATUS = 50;

	final public static int NONE_PRIORITY = 10;
	final public static int LOW_PRIORITY = 20;
	final public static int NORMAL_PRIORITY = 30;
	final public static int HIGH_PRIORITY = 40;
	final public static int URGENT_PRIORITY = 50;
	final public static int IMMEDIATE_PRIORITY = 60;

	final public static int FEATURE_SEVERITY = 10;
	final public static int TRIVIAL_SEVERITY = 20;
	final public static int TEXT_SEVERITY = 30;
	final public static int TWEAK_SEVERITY = 40;
	final public static int MINOR_SEVERITY = 50;
	final public static int MAJOR_SEVERITY = 60;
	final public static int CRASH_SEVERITY = 70;
	final public static int BLOCK_SEVERITY = 80;

	final public static int ALWAYS_REPRODUCIBILITY = 10;
	final public static int SOMETIMES_REPRODUCIBILITY = 30;
	final public static int RANDOM_REPRODUCIBILITY = 50;
	final public static int HAVE_NOT_TIRED_REPRODUCIBILITY = 70;
	final public static int UNABLE_TO_REPRODUCE_REPRODUCIBILITY = 90;
	final public static int NA_REPRODUCIBILITY = 100;

	final public static int NEW_STATUS = 10;
	final public static int FEEDBACK_STATUS = 20;
	final public static int ACKNOWLEDGED_STATUS = 30;
	final public static int CONFIRMED_STATUS = 40;
	final public static int ASSIGNED_STATUS = 50;
	final public static int RESOLVED_STATUS = 80;
	final public static int CLOSED_STATUS = 90;

	final public static int OPEN_RESOLUTION = 10;
	final public static int FIXED_RESOLUTION = 20;
	final public static int REOPENED_RESOLUTION = 30;
	final public static int UNABLE_TO_REPRODUCE_RESOLUTION = 40;
	final public static int NOT_FIXABLE_RESOLUTION = 50;
	final public static int DUPLICATE_RESOLUTION = 60;
	final public static int NO_CHANGE_REQUIRED_RESOLUTION = 70;
	final public static int SUSPENDED_RESOLUTION = 80;
	final public static int WONT_FIX_RESOLUTION = 90;

	// for string
	final public static String S_VIEWER_ACCESS_LEVEL = "viewer";
	final public static String S_REPORTER_ACCESS_LEVEL = "reporter";
	final public static String S_UPDATER_ACCESS_LEVEL = "updater";
	final public static String S_DEVELOPER_ACCESS_LEVEL = "developer";
	final public static String S_MANAGER_ACCESS_LEVEL = "manager";
	final public static String S_ADMINISTRATOR_ACCESS_LEVEL = "administrator";

	final public static String S_PUBLIC_VIEW_STATUS = "public";
	final public static String S_PRIVATE_VIEW_STATUS = "private";

	final public static String S_NONE_PRIORITY = "none";
	final public static String S_LOW_PRIORITY = "low";
	final public static String S_NORMAL_PRIORITY = "normal";
	final public static String S_HIGH_PRIORITY = "high";
	final public static String S_URGENT_PRIORITY = "urgent";
	final public static String S_IMMEDIATE_PRIORITY = "immediate";

	final public static String S_FEATURE_SEVERITY = "feature";
	final public static String S_TRIVIAL_SEVERITY = "trivial";
	final public static String S_TEXT_SEVERITY = "text";
	final public static String S_TWEAK_SEVERITY = "tweak";
	final public static String S_MINOR_SEVERITY = "minor";
	final public static String S_MAJOR_SEVERITY = "major";
	final public static String S_CRASH_SEVERITY = "crash";
	final public static String S_BLOCK_SEVERITY = "block";

	final public static String S_ALWAYS_REPRODUCIBILITY = "always";
	final public static String S_SOMETIMES_REPRODUCIBILITY = "sometimes";
	final public static String S_RANDOM_REPRODUCIBILITY = "random";
	final public static String S_HAVE_NOT_TIRED_REPRODUCIBILITY = "have not tired";
	final public static String S_UNABLE_TO_REPRODUCE_REPRODUCIBILITY = "unable to reproduce";
	final public static String S_NA_REPRODUCIBILITY = "N/A";

	final public static String S_NEW_STATUS = "new";
	final public static String S_FEEDBACK_STATUS = "feedback";
	final public static String S_ACKNOWLEDGED_STATUS = "acknowledged";
	final public static String S_CONFIRMED_STATUS = "confirmed";
	final public static String S_ASSIGNED_STATUS = "assigned";
	final public static String S_RESOLVED_STATUS = "resolved";
	final public static String S_CLOSED_STATUS = "closed";

	final public static String S_OPEN_RESOLUTION = "open";
	final public static String S_FIXED_RESOLUTION = "fixed";
	final public static String S_REOPENED_RESOLUTION = "reopened";
	final public static String S_UNABLE_TO_REPRODUCE_RESOLUTION = "unable to reproduce";
	final public static String S_NOT_FIXABLE_RESOLUTION = "not fixable";
	final public static String S_DUPLICATE_RESOLUTION = "duplicate";
	final public static String S_NO_CHANGE_REQUIRED_RESOLUTION = "no change required";
	final public static String S_SUSPENDED_RESOLUTION = "suspended";
	final public static String S_WONT_FIX_RESOLUTION = "won't fix";
	
	final public static String T_GOOD = "Good";
	final public static String T_IMPROVEMENT = "Improvement";
	

	final public static int PARENT_RELATIONSHIP = 2;
	final public static String S_PARENT_RELATIONSHIP = "parent of";
	
	//===========對應的method==============
	public static String getAccessLevel(int level){
		if (level == VIEWER_ACCESS_LEVEL)
			return S_VIEWER_ACCESS_LEVEL;
		else if (level == REPORTER_ACCESS_LEVEL)
			return S_REPORTER_ACCESS_LEVEL;
		else if (level == UPDATER_ACCESS_LEVEL)
			return S_UPDATER_ACCESS_LEVEL;
		else if (level == DEVELOPER_ACCESS_LEVEL)
			return S_DEVELOPER_ACCESS_LEVEL;
		else if (level == MANAGER_ACCESS_LEVEL)
			return S_MANAGER_ACCESS_LEVEL;
		else if (level == ADMINISTRATOR_ACCESS_LEVEL)
			return S_ADMINISTRATOR_ACCESS_LEVEL;
		return "";		
	}
	
	public static int getAccessLevel(String level){
		if (level.equals(S_VIEWER_ACCESS_LEVEL))
			return VIEWER_ACCESS_LEVEL;
		else if (level.equals(S_REPORTER_ACCESS_LEVEL))
			return REPORTER_ACCESS_LEVEL;
		else if (level.equals(S_UPDATER_ACCESS_LEVEL))
			return UPDATER_ACCESS_LEVEL;
		else if (level.equals(S_DEVELOPER_ACCESS_LEVEL))
			return DEVELOPER_ACCESS_LEVEL;
		else if (level.equals(S_MANAGER_ACCESS_LEVEL))
			return MANAGER_ACCESS_LEVEL;
		else if (level.equals(S_ADMINISTRATOR_ACCESS_LEVEL))
			return ADMINISTRATOR_ACCESS_LEVEL;
		return -1;		
	}
	
	public static String getViewStatus(int status){
		if (status == PUBLIC_VIEW_STATUS)
			return S_PUBLIC_VIEW_STATUS;
		else if (status == PRIVATE_VIEW_STATUS)
			return S_PRIVATE_VIEW_STATUS;
		return "";		
	}
	
	public static int getViewStatus(String status){
		if (status.equals(S_PUBLIC_VIEW_STATUS))
			return PUBLIC_VIEW_STATUS;
		else if (status.equals(S_PRIVATE_VIEW_STATUS))
			return PRIVATE_VIEW_STATUS;
		return -1;	
	}
	
	public static String getPriority(int priority){
		if (priority == NONE_PRIORITY)
			return S_NONE_PRIORITY;
		else if (priority == LOW_PRIORITY)
			return S_LOW_PRIORITY;
		else if (priority == NORMAL_PRIORITY)
			return S_NORMAL_PRIORITY;
		else if (priority == HIGH_PRIORITY)
			return S_HIGH_PRIORITY;
		else if (priority == URGENT_PRIORITY)
			return S_URGENT_PRIORITY;
		else if (priority == IMMEDIATE_PRIORITY)
			return S_IMMEDIATE_PRIORITY;
		return "";		
	}
	
	public static int getPriority(String priority){
		if (priority.equals(S_NONE_PRIORITY))
			return NONE_PRIORITY;
		else if (priority.equals(S_LOW_PRIORITY))
			return LOW_PRIORITY;
		else if (priority.equals(S_NORMAL_PRIORITY))
			return NORMAL_PRIORITY;
		else if (priority.equals(S_HIGH_PRIORITY))
			return HIGH_PRIORITY;
		else if (priority.equals(S_URGENT_PRIORITY))
			return URGENT_PRIORITY;
		else if (priority.equals(S_IMMEDIATE_PRIORITY))
			return IMMEDIATE_PRIORITY;
		return -1;		
	}	
	
	public static String getSeverity(int severity){
		if (severity == FEATURE_SEVERITY)
			return S_FEATURE_SEVERITY;
		else if (severity == TRIVIAL_SEVERITY)
			return S_TRIVIAL_SEVERITY;
		else if (severity == TEXT_SEVERITY)
			return S_TEXT_SEVERITY;
		else if (severity == TWEAK_SEVERITY)
			return S_TWEAK_SEVERITY;
		else if (severity == MINOR_SEVERITY)
			return S_MINOR_SEVERITY;
		else if (severity == MAJOR_SEVERITY)
			return S_MAJOR_SEVERITY;
		else if (severity == CRASH_SEVERITY)
			return S_CRASH_SEVERITY;
		else if (severity == BLOCK_SEVERITY)
			return S_BLOCK_SEVERITY;
		return "";		
	}
	
	public static int getSeverity(String severity){
		if (severity.equals(S_FEATURE_SEVERITY))
			return FEATURE_SEVERITY;
		else if (severity.equals(S_TRIVIAL_SEVERITY))
			return TRIVIAL_SEVERITY;
		else if (severity.equals(S_TEXT_SEVERITY))
			return TEXT_SEVERITY;
		else if (severity.equals(S_TWEAK_SEVERITY))
			return TWEAK_SEVERITY;
		else if (severity.equals(S_MINOR_SEVERITY))
			return MINOR_SEVERITY;
		else if (severity.equals(S_MAJOR_SEVERITY))
			return MAJOR_SEVERITY;
		else if (severity.equals(S_CRASH_SEVERITY))
			return CRASH_SEVERITY;
		else if (severity.equals(S_BLOCK_SEVERITY))
			return BLOCK_SEVERITY;
		return -1;		
	}
	
	public static String getReproducibility(int reproducibility){
		if (reproducibility == ALWAYS_REPRODUCIBILITY)
			return S_ALWAYS_REPRODUCIBILITY;
		else if (reproducibility == SOMETIMES_REPRODUCIBILITY)
			return S_SOMETIMES_REPRODUCIBILITY;
		else if (reproducibility == RANDOM_REPRODUCIBILITY)
			return S_RANDOM_REPRODUCIBILITY;
		else if (reproducibility == HAVE_NOT_TIRED_REPRODUCIBILITY)
			return S_HAVE_NOT_TIRED_REPRODUCIBILITY;
		else if (reproducibility == UNABLE_TO_REPRODUCE_REPRODUCIBILITY)
			return S_UNABLE_TO_REPRODUCE_REPRODUCIBILITY;
		else if (reproducibility == NA_REPRODUCIBILITY)
			return S_NA_REPRODUCIBILITY;
		return "";		
	}
	
	public static int getReproducibility(String reproducibility){
		if (reproducibility.equals(S_ALWAYS_REPRODUCIBILITY))
			return ALWAYS_REPRODUCIBILITY;
		else if (reproducibility.equals(S_SOMETIMES_REPRODUCIBILITY))
			return SOMETIMES_REPRODUCIBILITY;
		else if (reproducibility.equals(S_RANDOM_REPRODUCIBILITY))
			return RANDOM_REPRODUCIBILITY;
		else if (reproducibility.equals(S_HAVE_NOT_TIRED_REPRODUCIBILITY))
			return HAVE_NOT_TIRED_REPRODUCIBILITY;
		else if (reproducibility.equals(S_UNABLE_TO_REPRODUCE_REPRODUCIBILITY))
			return UNABLE_TO_REPRODUCE_REPRODUCIBILITY;
		else if (reproducibility.equals(S_NA_REPRODUCIBILITY))
			return NA_REPRODUCIBILITY;
		return -1;		
	}

	public static String getStatus(int status){
		if (status == NEW_STATUS)
			return S_NEW_STATUS;
		else if (status == FEEDBACK_STATUS)
			return S_FEEDBACK_STATUS;
		else if (status == ACKNOWLEDGED_STATUS)
			return S_ACKNOWLEDGED_STATUS;
		else if (status == CONFIRMED_STATUS)
			return S_CONFIRMED_STATUS;
		else if (status == ASSIGNED_STATUS)
			return S_ASSIGNED_STATUS;
		else if (status == RESOLVED_STATUS)
			return S_RESOLVED_STATUS;
		else if (status == CLOSED_STATUS)
			return S_CLOSED_STATUS;
		else // get status of Task Board plug-in Work Stage
			return String.valueOf(status);
	}
	
	public static int getStatus(String status){
		if (status.equals(S_NEW_STATUS))
			return NEW_STATUS;
		else if (status.equals(S_FEEDBACK_STATUS))
			return FEEDBACK_STATUS;
		else if (status.equals(S_ACKNOWLEDGED_STATUS))
			return ACKNOWLEDGED_STATUS;
		else if (status.equals(S_CONFIRMED_STATUS))
			return CONFIRMED_STATUS;
		else if (status.equals(S_ASSIGNED_STATUS))
			return ASSIGNED_STATUS;
		else if (status.equals(S_RESOLVED_STATUS))
			return RESOLVED_STATUS;
		else if (status.equals(S_CLOSED_STATUS))
			return CLOSED_STATUS;
		return -1;		
	}
	
	public static String getResolution(int resolution){
		if (resolution == OPEN_RESOLUTION)
			return S_OPEN_RESOLUTION;
		else if (resolution == FIXED_RESOLUTION)
			return S_FIXED_RESOLUTION;
		else if (resolution == REOPENED_RESOLUTION)
			return S_REOPENED_RESOLUTION;
		else if (resolution == UNABLE_TO_REPRODUCE_RESOLUTION)
			return S_UNABLE_TO_REPRODUCE_RESOLUTION;
		else if (resolution == NOT_FIXABLE_RESOLUTION)
			return S_NOT_FIXABLE_RESOLUTION;
		else if (resolution == DUPLICATE_RESOLUTION)
			return S_DUPLICATE_RESOLUTION;
		else if (resolution == NO_CHANGE_REQUIRED_RESOLUTION)
			return S_NO_CHANGE_REQUIRED_RESOLUTION;
		else if (resolution == SUSPENDED_RESOLUTION)
			return S_SUSPENDED_RESOLUTION;
		else if (resolution == WONT_FIX_RESOLUTION)
			return S_WONT_FIX_RESOLUTION;
		return "";		
	}
	
	public static int getResolution(String resolution){
		if (resolution.equals(S_OPEN_RESOLUTION))
			return OPEN_RESOLUTION;
		else if (resolution.equals(S_FIXED_RESOLUTION))
			return FIXED_RESOLUTION;
		else if (resolution.equals(S_REOPENED_RESOLUTION))
			return REOPENED_RESOLUTION;
		else if (resolution.equals(S_UNABLE_TO_REPRODUCE_RESOLUTION))
			return UNABLE_TO_REPRODUCE_RESOLUTION;
		else if (resolution.equals(S_NOT_FIXABLE_RESOLUTION))
			return NOT_FIXABLE_RESOLUTION;
		else if (resolution.equals(S_DUPLICATE_RESOLUTION))
			return DUPLICATE_RESOLUTION;
		else if (resolution.equals(S_NO_CHANGE_REQUIRED_RESOLUTION))
			return NO_CHANGE_REQUIRED_RESOLUTION;
		else if (resolution.equals(S_SUSPENDED_RESOLUTION))
			return SUSPENDED_RESOLUTION;
		else if (resolution.equals(S_WONT_FIX_RESOLUTION))
			return WONT_FIX_RESOLUTION;
		return -1;		
	}
	
	public static int getRelationship(String relationship){
		if (relationship.equals(S_PARENT_RELATIONSHIP))
			return PARENT_RELATIONSHIP;		
		return -1;		
	}
	

	public static String getRelationship(int relationship){
		if (relationship == PARENT_RELATIONSHIP)
			return S_PARENT_RELATIONSHIP;		
		return "";		
	}
	
}
