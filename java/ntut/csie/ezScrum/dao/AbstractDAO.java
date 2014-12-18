package ntut.csie.ezScrum.dao;

import java.sql.SQLException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;

/**
 * The abstract base DAO class could be extended by any concrete DAO class.
 * 
 * @author samhuang 2014/12/18
 * 
 * @param <T>
 *            DAO Get 回來的資料型態 ex：TaskObject
 * @param <K>
 *            Add 時要傳入的資料型態 ex: HistoryObject, TaskInfo
 */
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
	abstract public long create(K infoObject);

	// read
	abstract public T get(long id) throws SQLException;

	// update
	abstract public boolean update(T dataObject);

	// delete
	abstract public boolean delete(long id);
}