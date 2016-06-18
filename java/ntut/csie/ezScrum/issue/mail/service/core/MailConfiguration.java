package ntut.csie.ezScrum.issue.mail.service.core;

import java.io.File;
import java.util.Properties;

public class MailConfiguration {
	private Properties properties;
	private String BASEDIR_PATH = getBaseDirPath();
	private final String PERFS_FILE_NAME = "ezScrum_MailSender.ini";
	private final String PERFS_FILE_PATH = BASEDIR_PATH + File.separator + PERFS_FILE_NAME;
	/**
	 * return base dir path
	 */
	public String getBaseDirPath() {
		String basedir = System.getProperty("ntut.csie.jcis.resource.BaseDir");
		if (basedir == null) {
			basedir = System.getProperty("user.dir");
		}
		return basedir;
	}
}
