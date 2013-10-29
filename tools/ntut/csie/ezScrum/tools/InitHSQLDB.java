package ntut.csie.ezScrum.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class InitHSQLDB {
	static String FILE_TABLE_NAME = "File_Table";
	public static void main(String[] args) {
		
		try
		{
			Class.forName("org.hsqldb.jdbcDriver");
			Connection c = DriverManager.getConnection("jdbc:hsqldb:file:./data/default","SA","");
			
			c.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}
}
