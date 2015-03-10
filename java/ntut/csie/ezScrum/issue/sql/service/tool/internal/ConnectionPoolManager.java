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
			
			/** 最大允許的閒置時間(秒) */
			dataSource.setMaxIdleTime(300);
			/** 對閒置的連線進行Query測試設置(秒) */
			dataSource.setIdleConnectionTestPeriod(1800);
			
			/** Checkin connection時不檢查連線是否有效 */
			dataSource.setTestConnectionOnCheckin(false);
			/** Checkout connection時檢查連線的有效性*/
			dataSource.setTestConnectionOnCheckout(true);
			/** 進行test時使用的 Query設定*/
			dataSource.setPreferredTestQuery("SELECT 1;");
			/** Connection的最大有效時數(秒)*/
			dataSource.setMaxConnectionAge(28800);
			/** Connection checkout 之後的有效時數(毫秒)*/
			dataSource.setCheckoutTimeout(28800000);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		mPoolMap.put(url, dataSource);
		return dataSource;
	}
}
