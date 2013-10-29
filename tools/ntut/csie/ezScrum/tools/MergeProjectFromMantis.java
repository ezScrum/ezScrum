package ntut.csie.ezScrum.tools;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.tool.ISQLControl;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;

public class MergeProjectFromMantis {
	ISQLControl control = null;
	String dbname = null;
	String ip = "140.124.181.147";
	String port = "3306";
	String account = "user";
	String password = "12345";

	public static void main(String[] args) {
		MergeProjectFromMantis merger = new MergeProjectFromMantis(
				"140.124.181.147", "3306", "bugtracker", "user", "12345");
		merger.showDBTable();
	}

	private MergeProjectFromMantis() {

	}

	public MergeProjectFromMantis(String hostname, String port, String dbname,
			String user, String pass) {
		control = new MySQLControl(hostname, port, dbname);
		control.setUser(user);
		control.setPassword(pass);
	}

	public String[] showDBTable() {
		ArrayList<String> nameList = new ArrayList<String>();

		control.connection();
		try {
			ResultSet result = control.executeQuery("SHOW TABLES");

			int i = 0;
			while (result.next()) {
				nameList.add(result.getString(1));
				i++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		control.close();
		return null;
	}
}
