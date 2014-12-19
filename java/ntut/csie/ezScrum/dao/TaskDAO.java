package ntut.csie.ezScrum.dao;

import java.sql.SQLException;

import ntut.csie.ezScrum.web.dataInfo.TaskInfo;
import ntut.csie.ezScrum.web.dataObject.TaskObject;

public class TaskDAO extends AbstractDAO<TaskObject, TaskInfo> {

	@Override
	public long create(TaskInfo infoObject) {
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

}
