package ntut.csie.ezScrum.robust.resource.tool;

import java.io.PrintWriter;

import jxl.write.WritableWorkbook;

public class ResourceManager {
	public static void closeResource(AutoCloseable resource) {
		try {
			if (resource != null) {
				resource.close();
			}
		} catch (Exception e) {
			recordExceptionMessage(e);
		}
	}
	
	public static void closeResource(WritableWorkbook resource) {
		try {
			if (resource != null) {
				resource.close();
			}
		} catch (Exception e) {
			recordExceptionMessage(e);
		}
	}
	
	public static void closeResource(PrintWriter resource) {
		if (resource != null) {
			resource.close();
		}
	}
	
	public static void recordExceptionMessage(Exception exception) {
		exception.printStackTrace();
	}
}
