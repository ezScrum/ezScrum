package ntut.csie.ezScrum.updater;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import jd.util.Executer;

public class Updater {

	private static Logger logger;
	private static String START_JAR = "start.jar";

	public static void main(String[] args) {

		// 建立Logger
		try {
			logger = Logger.getLogger("jd.util.Executer");
			FileHandler handler = new FileHandler("restarter.log", false);
			logger.addHandler(handler);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			// waits while start.jar exixts and cannot be overwritten
			while (new File(START_JAR).exists()
					&& !new File(START_JAR).canWrite()) {
				logger.severe("Wait for start.jar terminating");
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			// Restart Server

			Executer exec;
			String exePath = new File("App.bat").getAbsolutePath();
			exec = new Executer(exePath);
			exec.setLogger(logger);
			exec.setRunin(new File(".").getAbsolutePath());
			exec.setWaitTimeout(0);
			exec.start();

			Thread.sleep(5000);
			System.exit(0);
		} catch (Throwable e) {
			logger.severe(getStackTrace(e));
		}
	}

	/**
	 * Returns the stacktrace of a Thorwable
	 * 
	 * @param thrown
	 * @return
	 */
	public static String getStackTrace(Throwable thrown) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		thrown.printStackTrace(pw);
		pw.close();
		return sw.toString();
	}
}
