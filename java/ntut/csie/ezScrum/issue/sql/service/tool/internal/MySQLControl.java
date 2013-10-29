package ntut.csie.ezScrum.issue.sql.service.tool.internal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.io.filefilter.TrueFileFilter;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import ntut.csie.ezScrum.issue.sql.service.tool.ISQLControl;

public class MySQLControl implements ISQLControl {
	String _hostname;
	String _port;
	String _dbname;
	String _user;
	String _password;

	Connection _connection = null;
	DataSource ds = null;

	String[] _keys;

	/** �s�u��Ƴ]�w **/
	public MySQLControl(String hostname, String port, String dbname) {
		// �]�wIP��m , Port , �H�έn�s������Ʈw�W��
		_hostname = hostname;
		_port = port;
		_dbname = dbname;
		loadDriver();
	}

	public void setUser(String user) {
		_user = user;
	}

	public void setPassword(String password) {
		_password = password;
	}

	public void connection() {
		try {
			if (ds == null) {
				ds = ConnectionPoolManager.getInstance().getConnectionPool("com.mysql.jdbc.Driver", getURL(), _user, _password);
			}

			// 只有在Connection為null或者是Connection已經Close的情況下才進行Connection
			if (_connection == null || _connection.isClosed())
				_connection = ds.getConnection();

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			ds = null;
			ConnectionPoolManager.getInstance().RemoveConnectionPool(getURL());
		}
	}

	@Override
	public void connectionToServer() {
		// TODO Auto-generated method stub
		try {
			_connection = DriverManager.getConnection(getServerURL(), _user, _password);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public String[] getKeys() {
		return _keys;
	}
	
	private void setKeys(Statement statement, String query) throws SQLException {
		boolean result = statement.execute(query, Statement.RETURN_GENERATED_KEYS);
		ResultSet keys = statement.getGeneratedKeys();

		// ��X�Ҧ^�Ǫ��۰�Key��
		if (keys.next()) {
			ResultSetMetaData _metadata = keys.getMetaData();
			int columnCount = _metadata.getColumnCount();
			_keys = new String[columnCount];
			for (int i = 1; i <= columnCount; i++) {
				_keys[i - 1] = keys.getString(i);
			}
		}
		else {
			_keys = new String[0];
		}
	}

	// �Ψӧ@�w�]�� , �Y�S���]�wreturn_keys���� , ���w�]��false
	public boolean execute(String query) {
		return execute(query, false);
	}

	// �ΨӰ��椣�|���^�Ǹ�T��SQL��O
	public boolean execute(String query, boolean return_keys) {
		try {
			Statement _statement = _connection.createStatement();
			if (return_keys) {
				setKeys(_statement, query);
			} else {
				return _statement.execute(query);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}

	/**
	 * for insert, update, delete
	 */
	public boolean executeUpdate(String query) {
		return executeUpdate(query, false);
	}
	
	/**
	 * for insert, update, delete
	 */
	public boolean executeUpdate(String query, boolean return_keys) {
		try {
			Statement _statement = _connection.createStatement();
			if (return_keys) {
				setKeys(_statement, query);
			} else {
				return _statement.executeUpdate(query) > 0 ? true : false;
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}

	// �ΨӰ���|���^�Ǫ�SQL��O
	public ResultSet executeQuery(String query) {
		ResultSet result = null;
		try {
			Statement _statement = _connection.createStatement();
			// ����SQL��O , �åB�⵲�G���x�s�_��
			result = _statement.executeQuery(query);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return null;
		}
		return result;
	}

	public void close() {
		try {
			_connection.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	private String getURL() {
		return "jdbc:mysql://" + _hostname + ":" + _port + "/" + _dbname + "?useUnicode=true&characterEncoding=utf8";
	}

	private String getServerURL() {
		return "jdbc:mysql://" + _hostname + ":" + _port;
	}

	private void loadDriver() {
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	@Override
	public java.sql.Connection getconnection() {
		// TODO Auto-generated method stub
		return this._connection;
	}

}
