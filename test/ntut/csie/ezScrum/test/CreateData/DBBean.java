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
	private static Log log = LogFactory.getLog(DBBean.class);
	
	private static String connectionDriver = "com.mysql.jdbc.Driver";
	private static String connectionURL;
	private static String userid;
	private static String userpw;
	
	private Connection conn = null;
	private Statement stat = null;
	private ResultSet rs = null;
	
	public DBBean(String IP, String UserId , String UserPw, String DBName) {
		this.connectionURL = "jdbc:mysql://" + IP + ":3306/" + DBName + "?userUnicode=true";
		this.userid = UserId;
		this.userpw = UserPw;
	}
	
	public Connection getConnectionFromODBC() throws Exception {
		try {	
			Class.forName(connectionDriver);
			this.conn = DriverManager.getConnection(connectionURL , userid , userpw);
		} catch (ClassNotFoundException cnfe) {
			this.log.error("method : getConnectionFromODBC, ClassNotFoundException : " + cnfe.toString());
		} catch (SQLException sqle) {
			this.log.error("method : getConnectionFromODBC, SQLException : " + sqle.toString());
		}
		
		if (conn == null) {
			this.log.error("Can't get Connection from ODBC Exception");
			throw new Exception("Can't get Connection from ODBC Exception");
		}
		
	 	return conn;
	}
	
	public boolean doSQL(List<String> Ins) throws Exception {
		this.conn = getConnectionFromODBC();
		this.stat = this.conn.createStatement();
		
		boolean isSuccess = false;
		for (String Str : Ins) {
			if(Str.equals("DELETE `mantis_user_table`;")){
				deleteUserTableInformation(this.conn, this.stat, "mantis_user_table");
			}else{
				this.stat.execute(Str);
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
		this.conn = getConnectionFromODBC();
		this.stat = this.conn.createStatement();
		ResultSet rs = null;
		for (String Str : Ins) {
			this.log.info("[SQL ins ] = " + Str);
			rs = this.stat.executeQuery(Str);
		}
		
		return rs;
	}

	// Close
	public void close() {
		try {
			if(rs != null) rs.close();		
			if(stat != null) stat.close();
			if(conn != null) conn.close();
		} catch(Exception e) {
			e.printStackTrace();
			this.log.error("SQL connection close error");
		}
	}
}