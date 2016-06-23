package ntut.csie.ezScrum.issue.mail.service.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class MailConfiguration {
	private Properties properties;
	private String BASEDIR_PATH = getBaseDirPath();
	private final String PERFS_FILE_NAME = "ezScrum_MailSender.ini";
	private final String ACCOUNT = "Account";
	private final String PASSWORD = "Password";
	private final String Mail_TYPE = "MailType";
	private final String PERFS_FILE_PATH = BASEDIR_PATH + File.separator + PERFS_FILE_NAME;
	public MailConfiguration() {
		init();
	}
	/**
	 * Initial Configuration
	 */
	private void init() {
		properties = new Properties();
		try {
			properties.load(new FileInputStream(PERFS_FILE_PATH));
		} catch (IOException e) {
			System.out.println(
					"************ ERROR MESSAGE ************\n\n\n" +
					"Please check \"ezScrum MailSender File.ini\" file exist.\n\n\n" +
					"***************************************\n\n\n"
			);
			System.exit(0);
		}
	}

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
	public String getAccount() {
		return properties.getProperty(ACCOUNT);
	}
	public String getPassword() {
		return properties.getProperty(PASSWORD);
	}
	public String getMailType() {
		return properties.getProperty(Mail_TYPE);
	}
}
