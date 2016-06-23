package ntut.csie.ezScrum.issue.sql.service.tool.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.internal.TestConnectException;
import ntut.csie.ezScrum.issue.sql.service.tool.ISQLControl;
import ntut.csie.ezScrum.web.databaseEnum.TabalesNameEnum;

public class DatabaseChecker {
	private Configuration mConfig = new Configuration();
	private ISQLControl mControl = new MySQLControl(mConfig);
	private static String INITIATE_SQL_FILE = "initial_bk.sql";
	
	public DatabaseChecker() {
		
	}
	
	public boolean testAndInitDatabase() {
		try {
			System.out.println("Test DB connection...");
			testConnect();

			try {
				System.out.println("Create DB...");
				createDB();
			} catch (Exception e) {
			}

			if (!isAllTableExist()) {
				System.out.println("Create tables...");
				initiateDB();
			}

			return true;
		} catch (Exception exception) {
			System.out
					.println("************** ERROR MESSAGE **************\n\n\n"
							+ "Database connect fail.\n\n"
							+ "Please check database setting in ezScrum.ini is correct.\n\n\n"
							+ "*******************************************\n\n\n");
		}
		return false;
	}
	
	private boolean testServerConnect() throws SQLException {
    	mControl.connectToServer();
		Connection connection = mControl.getconnection();
		try {
			if (connection == null)
				return false;
			else
				return true;
		} finally {
			if (connection != null)
				connection.close();
		}
	}
    
    private void testConnect() throws Exception {
		try {
			if (testServerConnect()) {
				return;
			} else {
				throw new TestConnectException(
						TestConnectException.DATABASE_ERROR);
			}
		} catch (SQLException e) {
			throw new TestConnectException(TestConnectException.CONNECT_ERROR);
		}
	}
    
    private boolean createDB() {
		mControl.connectToServer();
		try {
			String sql = "CREATE DATABASE " + mConfig.getDBName();
			return mControl.execute(sql);
		} finally {
			mControl.close();
		}
	}
    
    private boolean isAllTableExist(ISQLControl checker) {
		boolean isExisting = true;
		List<String> nameList = new ArrayList<String>();

		checker.connect();
		try {
			ResultSet result = checker.executeQuery("SHOW TABLES");
			while (result.next()) {
				nameList.add(result.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			checker.close();
		}
		
		ArrayList<String> tablesName = TabalesNameEnum.getAllTablesName();
		if (nameList.size() != tablesName.size()) {
			return false;
		}
		for (String tableName : nameList) {
			if (!tablesName.contains(tableName)) {
				isExisting = false;
				break;
			}
		}
		return isExisting;
	}
    
    private boolean isAllTableExist() {
		return isAllTableExist(mControl);
	}
    
    private void importSQL(Connection conn, InputStream in) throws SQLException {
		Scanner s = new Scanner(in,"UTF-8");
		s.useDelimiter("(;(\r)?\n)|(--\n)");
		Statement st = null;
		try {
			st = conn.createStatement();
			while (s.hasNext()) {
				String line = s.next();
				if (line.startsWith("/*!") && line.endsWith("*/")) {
					int i = line.indexOf(' ');
					line = line.substring(i + 1, line.length() - " */".length());
				}

				if (line.trim().length() > 0) {
					st.execute(line);
				}
			}
		} finally {
			if (st != null)
				st.close();
			if (s != null)
				s.close();
		}
	}
    
    private boolean initiateDB() throws SQLException {
		mControl.connect();
		Connection connection = mControl.getconnection();
		String defaultFile = mConfig.getWorkspacePath() + File.separator + "_metadata" + File.separator + INITIATE_SQL_FILE;
		try {
			importSQL(connection, new FileInputStream(defaultFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} finally {
			mControl.close();
		}
		return true;
	}
}
