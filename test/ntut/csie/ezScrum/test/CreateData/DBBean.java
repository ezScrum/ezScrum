package ntut.csie.ezScrum.test.CreateData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DBBean {
	private static Log mlog = LogFactory.getLog(DBBean.class);
	private static String mConnectionDriver = "com.mysql.jdbc.Driver";
	private static String mConnectionURL;
	private static String mUserId;
	private static String mUserPassword;
	private Connection mConnection = null;
	private Statement mStatement = null;
	private ResultSet mResultSet = null;
	
	public DBBean(String IP, String UserId , String UserPwd, String DBName) {
		mConnectionURL = "jdbc:mysql://" + IP + ":3306/" + DBName + "?userUnicode=true";
		mUserId = UserId;
		mUserPassword = UserPwd;
	}
	
	public Connection getConnectionFromODBC() throws Exception {
		try {	
			Class.forName(mConnectionDriver);
			mConnection = DriverManager.getConnection(mConnectionURL , mUserId , mUserPassword);
		} catch (ClassNotFoundException cnfe) {
			mlog.error("method : getConnectionFromODBC, ClassNotFoundException : " + cnfe.toString());
		} catch (SQLException sqle) {
			mlog.error("method : getConnectionFromODBC, SQLException : " + sqle.toString());
		}
		
		if (mConnection == null) {
			mlog.error("Can't get Connection from ODBC Exception");
			throw new Exception("Can't get Connection from ODBC Exception");
		}
		
	 	return mConnection;
	}
	
	public boolean doSQL(List<String> Ins) throws Exception {
		mConnection = getConnectionFromODBC();
		mStatement = mConnection.createStatement();
		
		boolean isSuccess = false;
		for (String Str : Ins) {
			if(Str.equals("DELETE `mantis_user_table`;")){
				deleteUserTableInformation(mConnection, mStatement, "mantis_user_table");
			}else{
				mStatement.execute(Str);
			}
		}
		
		return isSuccess;
	}
	
	/**
	 * 刪除 User 資料表的資訊
	 * @param conn
	 * @param stmt
	 * @param tableName
	 * @throws SQLException
	 */
	private void deleteUserTableInformation(Connection conn, Statement stmt, String tableName) throws SQLException{
		String deleteAccountSql = "DELETE FROM " + tableName + " WHERE username <> \"admin\" ";
		stmt.execute(deleteAccountSql);
	}
	
	// 執行query, 並儲存回傳資料
	public ResultSet doSQLtest(List<String> Ins) throws Exception {
		mConnection = getConnectionFromODBC();
		mStatement = mConnection.createStatement();
		ResultSet rs = null;
		for (String Str : Ins) {
			mlog.info("[SQL ins ] = " + Str);
			rs = mStatement.executeQuery(Str);
		}
		
		return rs;
	}

	// Close
	public void close() {
		try {
			if(mResultSet != null) mResultSet.close();		
			if(mStatement != null) mStatement.close();
			if(mConnection != null) mConnection.close();
		} catch(Exception e) {
			e.printStackTrace();
			mlog.error("SQL connection close error");
		}
	}
}