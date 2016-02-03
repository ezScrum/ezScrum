package ntut.csie.ezScrum.restful.dataMigration.support;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;

public class BaseUrlDistributor {
	public static final String BASE_URL = "http://localhost:8080/ezScrum/resource/";
	public static final String TEST_MODE_BASE_URL = "http://localhost:9527/ezScrum/resource/";
	
	public static String getBaseUrl() {
		Configuration configuration = new Configuration();
		if (configuration.isRunningTestMode()) {
			return TEST_MODE_BASE_URL;
		}
		return BASE_URL;
	}
}
