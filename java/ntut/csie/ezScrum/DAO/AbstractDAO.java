package ntut.csie.ezScrum.DAO;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;

public abstract class AbstractDAO<T, K> {
	protected Configuration mConfig;
	protected static Object sInstance = null;
	protected MySQLControl mControl;
	
	protected AbstractDAO() {
		mConfig = new Configuration();
		mControl = new MySQLControl(mConfig.getServerUrl(), "3306", mConfig.getDBAccount());
		mControl.setUser(mConfig.getDBAccount());
		mControl.setPassword(mConfig.getDBPassword());
	}
	
	// create
	abstract public long add(K infoObject);
	// read
	abstract public T get(long id);
	// update
	abstract public boolean edit(T dataObject);
	// delete
	abstract public boolean delete(long id);
}
