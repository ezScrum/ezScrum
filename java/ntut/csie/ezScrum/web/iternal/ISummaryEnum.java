package ntut.csie.ezScrum.web.iternal;

public interface ISummaryEnum {
	//==============DetectorBuilder Configuration Environment================
	//summary configuration file
	public final static String STATISTICS_XML_FILE = "Statistics.xml";
	public final static String SUMMARY_XML_FILE = "summary.xml";
	
	public static String SUMMARIES_TAG = "Summaries";
	public static String SUMMARY_TAG = "Summary";
	
	
	//=========預設值=========
	//----------RULE_TAG[@name=值]-------
	public final static String REMAININGWORK_SUMMARY_NAME = "RemainingWork";
	public final static String VELOCITY_SUMMARY_NAME = "Velocity";
	public final static String BUGRATES_SUMMARY_NAME = "BugRates";
	public final static String QUALITYINDICATOR_SUMMARY_NAME = "QualityIndicator";
	
	//------CONF_TAG[@name=值]-------
	public static String STARTTIME_CONF_NAME = "StartDate";
	public static String INTERVAL_CONF_NAME = "Interval";
	//暫時先不做提供自訂tag的功能
//	public static String ITERATION_TAG_CONF_NAME = "IterationTag";
//	public static String ESTIMATEDTIME_TAG_CONF_NAME = "EstimatedTimeTag";
}
