package ntut.csie.ezScrum.issue.sql.service.tool.internal;

import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class ConnectionPoolManager {
	private static ConnectionPoolManager instance = null;
	private Map<String,DataSource> poolMap;
	private ConnectionPoolManager()
	{
		poolMap = new HashMap<String,DataSource>();
	}
	
	public static ConnectionPoolManager getInstance()
	{
		if(instance == null)
			instance = new ConnectionPoolManager();
		
		return instance;
	}
	
	public DataSource getConnectionPool(String driverClass,String url,String account,String password)
	{
		DataSource ds = poolMap.get(url);
		if(ds == null)
			ds = createDataSource(driverClass,url,account,password);
		
		return ds;
	}

	public void RemoveConnectionPool(String url)
	{
		poolMap.remove(url);
	}
	
	private DataSource createDataSource(String driverClass,String url,String account,String password)
	{
		ComboPooledDataSource ds = new ComboPooledDataSource();
		try
		{
			ds.setDriverClass(driverClass);
			ds.setJdbcUrl(url);
			ds.setUser(account);
			ds.setPassword(password);
			
			ds.setMinPoolSize(10);
			ds.setMaxPoolSize(35);
			
			ds.setAcquireIncrement(0);
			ds.setMaxStatements(0);
			ds.setMaxIdleTime(300);
			ds.setIdleConnectionTestPeriod(100);
		}
		catch(PropertyVetoException e)
		{
			e.printStackTrace();
		}
		poolMap.put(url, ds);
		return ds;
	}
}
