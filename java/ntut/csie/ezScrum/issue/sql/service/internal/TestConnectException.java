package ntut.csie.ezScrum.issue.sql.service.internal;

public class TestConnectException extends Exception {
	public static String CONNECT_ERROR = "Connect Error";
	public static String TABLE_ERROR = "Table Error";
	public static String DATABASE_ERROR = "Database Error";

	private String type = "";

	public TestConnectException(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
