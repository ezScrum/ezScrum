package ntut.csie.ezScrum.issue.sql.service.tool.internal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import ntut.csie.ezScrum.issue.sql.service.tool.ISQLControl;

public abstract class AbstractSQLControl implements ISQLControl{
	String _hostname;
	String _port;
	String _dbname;
	String _user;
	String _password;
	
	Connection _connection = null;
	
	String[] _keys;
	/**連線資料設定**/
	public void init(String hostname , String port , String dbname , String driverName)
	{
		//設定IP位置 , Port , 以及要連結的資料庫名稱
		_hostname = hostname;
		_port = port;
		_dbname = dbname;
		loadDriver(driverName);
	}
	public void setUser(String user)
	{
		_user = user;
	}
	public void setPassword(String password)
	{
		_password = password;
	}

	
	public void connect()
	{
		 try
		 {
			 _connection = DriverManager.getConnection(getURL(),_user,_password);
		 }
		 catch(SQLException e)
		 {
			 System.out.println(e.getMessage());
		 }
	}
	
	@Override
	public void connectToServer() {
		// TODO Auto-generated method stub
		 try
		 {
			 _connection = DriverManager.getConnection(getServerURL(),_user,_password);
		 }
		 catch(SQLException e)
		 {
			 System.out.println(e.getMessage());
		 }
	}
	
	
	public String[] getKeys()
	{
		return _keys;
	}
	//用來作預設值 , 若沒有設定return_keys的話 , 那預設為false
	public boolean execute(String query)
	{
		return execute(query,false);
	}
	//用來執行不會有回傳資訊的SQL指令
	public boolean execute(String query,boolean return_keys)
	{
		try
		{
			Statement _statement = _connection.createStatement();
			if(return_keys)
			{
				boolean result = _statement.execute(query,Statement.RETURN_GENERATED_KEYS);
				ResultSet keys = _statement.getGeneratedKeys();
				
				//取出所回傳的自動Key值
				if(keys.next())
				{
					ResultSetMetaData _metadata = keys.getMetaData();
					int columnCount = _metadata.getColumnCount();
					_keys = new String[columnCount];
					for(int i = 1;i<=columnCount;i++)
					{
						_keys[i-1] = keys.getString(i);
					}
				}
				else
				{
					_keys = new String[0];
				}
			}
			else
			{
				return _statement.execute(query);
			}
		}
		catch(SQLException e)
		{
			System.out.println(e.getMessage());
		}
		return false;
	}
	//用來執行會有回傳的SQL指令
	public ResultSet executeQuery(String query)
	{
		ResultSet result = null;
		try
		{
			Statement _statement = _connection.createStatement();
			//執行SQL指令 , 並且把結果給儲存起來
			result = _statement.executeQuery(query);
		}
		catch(SQLException e)
		{
			System.out.println(e.getMessage());
			return null;
		}
		return result;
	}
	public void close()
	{
		try{
			_connection.close();
		}
		catch(SQLException e)
		{
			System.out.println(e.getMessage());
		}
	}

	
	protected abstract String getURL();
	protected abstract String getServerURL();

	private void loadDriver(String driverName)
	{
		try
		{
			Class.forName("driverName");
		}
		catch(Exception e)
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
