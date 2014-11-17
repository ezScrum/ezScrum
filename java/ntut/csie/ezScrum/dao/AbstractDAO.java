package ntut.csie.ezScrum.dao;

import java.sql.SQLException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;

public abstract class AbstractDAO<T, K> {
	protected Configuration mConfig;
	protected static Object sInstance = null;
	protected MySQLControl mControl;
	
	protected AbstractDAO() {
		mConfig = new Configuration();
		mControl = new MySQLControl(mConfig);
		mControl.connection();
	}
	
	// create
	abstract public long add(K infoObject);
	// read
	abstract public T get(long id)  throws SQLException;
	// update
	abstract public boolean edit(T dataObject);
	// delete
	abstract public boolean delete(long id);
}
