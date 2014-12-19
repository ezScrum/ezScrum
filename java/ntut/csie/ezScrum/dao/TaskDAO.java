package ntut.csie.ezScrum.dao;

import java.sql.SQLException;
import java.util.ArrayList;

import ntut.csie.ezScrum.web.dataObject.TaskObject;

public class TaskDAO extends AbstractDAO<TaskObject, TaskObject> {
	
	public static TaskDAO getInstance() {
		if (sInstance == null) {
			sInstance = new TaskDAO();
		}
		return (TaskDAO) sInstance;
	}

	@Override
	public long create(TaskObject infoObject) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public TaskObject get(long id) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean update(TaskObject dataObject) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(long id) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public ArrayList<Long> getPartnersId(long taskId) {
		return null;
	}

}
