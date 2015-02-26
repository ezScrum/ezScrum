package ntut.csie.ezScrum.issue.sql.service.tool.internal;

import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class ConnectionPoolManager {
	private static ConnectionPoolManager mInstance = null;
	private Map<String, DataSource> mPoolMap = null;

	private ConnectionPoolManager() {
		mPoolMap = new HashMap<String, DataSource>();
	}

	public static ConnectionPoolManager getInstance() {
		if (mInstance == null) {
			mInstance = new ConnectionPoolManager();
		}
		return mInstance;
	}

	public DataSource getConnectionPool(String driverClass, String url, String account, String password) {
		DataSource dataSource = mPoolMap.get(url);
		if (dataSource == null)
			dataSource = createDataSource(driverClass, url, account, password);
		return dataSource;
	}

	public void RemoveConnectionPool(String url) {
		mPoolMap.remove(url);
	}

	private DataSource createDataSource(String driverClass, String url, String account, String password) {
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		try {
			dataSource.setDriverClass(driverClass);
			dataSource.setJdbcUrl(url);
			dataSource.setUser(account);
			dataSource.setPassword(password);

			dataSource.setMinPoolSize(10);
			dataSource.setMaxPoolSize(35);

			dataSource.setAcquireIncrement(0);
			dataSource.setMaxStatements(0);
			dataSource.setMaxIdleTime(300);
			dataSource.setIdleConnectionTestPeriod(30);
			
			dataSource.setTestConnectionOnCheckin(false);
			dataSource.setTestConnectionOnCheckout(true);
			dataSource.setPreferredTestQuery("SELECT 1;");
			dataSource.setMaxConnectionAge(8 * 24 * 60 * 60);
			dataSource.setCheckoutTimeout(8 * 24 * 60 * 60);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		mPoolMap.put(url, dataSource);
		return dataSource;
	}
}
