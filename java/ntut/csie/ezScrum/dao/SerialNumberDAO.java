package ntut.csie.ezScrum.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.web.dataObject.SerialNumberObject;
import ntut.csie.ezScrum.web.databaseEnum.SerialNumberEnum;

public class SerialNumberDAO extends
		AbstractDAO<SerialNumberObject, SerialNumberObject> {

	private static SerialNumberDAO sInstance = null;

	public static SerialNumberDAO getInstance() {
		if (sInstance == null) {
			sInstance = new SerialNumberDAO();
		}
		return sInstance;
	}

	@Override
	public long create(SerialNumberObject serialNumber) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(SerialNumberEnum.TABLE_NAME);
		valueSet.addInsertValue(SerialNumberEnum.PROJECT_ID,
				serialNumber.getProjectId());
		valueSet.addInsertValue(SerialNumberEnum.RELEASE,
				serialNumber.getReleaseId());
		valueSet.addInsertValue(SerialNumberEnum.SPRINT,
				serialNumber.getSprintId());
		valueSet.addInsertValue(SerialNumberEnum.STORY,
				serialNumber.getStoryId());
		valueSet.addInsertValue(SerialNumberEnum.TASK, serialNumber.getTaskId());
		valueSet.addInsertValue(SerialNumberEnum.UNPLANNED,
				serialNumber.getUnplannedId());
		valueSet.addInsertValue(SerialNumberEnum.RETROSPECTIVE,
				serialNumber.getRetrospectiveId());
		String query = valueSet.getInsertQuery();

		return mControl.executeInsert(query);
	}

	@Override
	public SerialNumberObject get(long id) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(SerialNumberEnum.TABLE_NAME);
		valueSet.addEqualCondition(SerialNumberEnum.ID, id);
		String query = valueSet.getSelectQuery();

		ResultSet result = mControl.executeQuery(query);

		SerialNumberObject serialNumber = null;
		try {
			if (result.next()) {
				serialNumber = convert(result);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		return serialNumber;
	}
	
	public SerialNumberObject getByProjectId(long projectId) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(SerialNumberEnum.TABLE_NAME);
		valueSet.addEqualCondition(SerialNumberEnum.PROJECT_ID, projectId);
		String query = valueSet.getSelectQuery();

		ResultSet result = mControl.executeQuery(query);

		SerialNumberObject serialNumber = null;
		try {
			if (result.next()) {
				serialNumber = convert(result);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		return serialNumber;
	}

	@Override
	public boolean update(SerialNumberObject serialNumber) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(SerialNumberEnum.TABLE_NAME);
		valueSet.addInsertValue(SerialNumberEnum.RELEASE,
				serialNumber.getReleaseId());
		valueSet.addInsertValue(SerialNumberEnum.SPRINT,
				serialNumber.getSprintId());
		valueSet.addInsertValue(SerialNumberEnum.STORY,
				serialNumber.getStoryId());
		valueSet.addInsertValue(SerialNumberEnum.TASK, serialNumber.getTaskId());
		valueSet.addInsertValue(SerialNumberEnum.UNPLANNED,
				serialNumber.getUnplannedId());
		valueSet.addInsertValue(SerialNumberEnum.RETROSPECTIVE,
				serialNumber.getRetrospectiveId());
		valueSet.addEqualCondition(SerialNumberEnum.PROJECT_ID,
				serialNumber.getProjectId());
		String query = valueSet.getUpdateQuery();

		return mControl.executeUpdate(query);
	}

	@Override
	public boolean delete(long id) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(SerialNumberEnum.TABLE_NAME);
		valueSet.addEqualCondition(SerialNumberEnum.ID, id);
		String query = valueSet.getDeleteQuery();
		return mControl.executeUpdate(query);
	}
	
	public boolean deleteByProjectId(long projectId) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(SerialNumberEnum.TABLE_NAME);
		valueSet.addEqualCondition(SerialNumberEnum.PROJECT_ID, projectId);
		String query = valueSet.getDeleteQuery();
		return mControl.executeUpdate(query);
	}

	private SerialNumberObject convert(ResultSet result) throws SQLException {
		SerialNumberObject serialNumber = new SerialNumberObject(result.getLong(SerialNumberEnum.ID), result.getLong(SerialNumberEnum.PROJECT_ID));
		serialNumber.setReleaseId(result.getLong(SerialNumberEnum.RELEASE))
				    .setSprintId(result.getLong(SerialNumberEnum.SPRINT))
				    .setStoryId(result.getLong(SerialNumberEnum.STORY))
				    .setTaskId(result.getLong(SerialNumberEnum.TASK))
				    .setUnplannedId(result.getLong(SerialNumberEnum.UNPLANNED))
				    .setRetrospectiveId(result.getLong(SerialNumberEnum.RETROSPECTIVE));
		return serialNumber;
	}
}
