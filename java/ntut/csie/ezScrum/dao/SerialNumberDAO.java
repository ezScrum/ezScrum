package ntut.csie.ezScrum.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.web.dataObject.SerialNumberObject;
import ntut.csie.ezScrum.web.databasEnum.SerialNumberEnum;

public class SerialNumberDAO extends AbstractDAO<SerialNumberObject, SerialNumberObject> {

	public static SerialNumberDAO getInstance() {
		if (sInstance == null) {
			sInstance = new SerialNumberDAO();
		}
		return (SerialNumberDAO) sInstance;
	}
	
	@Override
    public long create(SerialNumberObject serialNumber) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(SerialNumberEnum.TABLE_NAME);
		valueSet.addInsertValue(SerialNumberEnum.PROJECT_ID, serialNumber.getProjectId());
		valueSet.addInsertValue(SerialNumberEnum.RELEASE, serialNumber.getReleaseId());
		valueSet.addInsertValue(SerialNumberEnum.SPRINT, serialNumber.getSprintId());
		valueSet.addInsertValue(SerialNumberEnum.STORY, serialNumber.getStoryId());
		valueSet.addInsertValue(SerialNumberEnum.TASK, serialNumber.getTaskId());
		valueSet.addInsertValue(SerialNumberEnum.UNPLANNED, serialNumber.getUnplannedId());
		valueSet.addInsertValue(SerialNumberEnum.RETROSPECTIVE, serialNumber.getRetrospectiveId());
		String query = valueSet.getInsertQuery();
		
		mControl.execute(query, true);
		
		String[] keys = mControl.getKeys();
		long id = Long.parseLong(keys[0]);
		
	    return id;
    }

	@Override
    public SerialNumberObject get(long projectId) throws SQLException {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(SerialNumberEnum.TABLE_NAME);
		valueSet.addEqualCondition(SerialNumberEnum.PROJECT_ID, projectId);
		String query = valueSet.getSelectQuery();

		ResultSet result = mControl.executeQuery(query);
		
		SerialNumberObject serialNumber = null;
		if (result.next()) {
			serialNumber = convert(result);
		}
		return serialNumber;
    }

	@Override
    public boolean update(SerialNumberObject serialNumber) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(SerialNumberEnum.TABLE_NAME);
		valueSet.addInsertValue(SerialNumberEnum.RELEASE, serialNumber.getReleaseId());
		valueSet.addInsertValue(SerialNumberEnum.SPRINT, serialNumber.getSprintId());
		valueSet.addInsertValue(SerialNumberEnum.STORY, serialNumber.getStoryId());
		valueSet.addInsertValue(SerialNumberEnum.TASK, serialNumber.getTaskId());
		valueSet.addInsertValue(SerialNumberEnum.UNPLANNED, serialNumber.getUnplannedId());
		valueSet.addInsertValue(SerialNumberEnum.RETROSPECTIVE, serialNumber.getRetrospectiveId());
		valueSet.addEqualCondition(SerialNumberEnum.PROJECT_ID, serialNumber.getProjectId());
		String query = valueSet.getUpdateQuery();
		
		return mControl.execute(query);
    }
	
	public boolean updateByColumn(String updateColumn, SerialNumberObject serialnumber) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(SerialNumberEnum.TABLE_NAME);
		valueSet.addInsertValue(updateColumn, serialnumber.getId(updateColumn));
		valueSet.addEqualCondition(SerialNumberEnum.PROJECT_ID, Long.toString(serialnumber.getProjectId()));
		String query = valueSet.getUpdateQuery();
		return mControl.execute(query);
	}

	@Override
    public boolean delete(long id) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(SerialNumberEnum.TABLE_NAME);
		valueSet.addEqualCondition(SerialNumberEnum.ID, id);
		String query = valueSet.getDeleteQuery();
		return mControl.execute(query);
    }

	private SerialNumberObject convert(ResultSet result) throws SQLException {
		SerialNumberObject serialNumber = new SerialNumberObject();
		serialNumber.setId(result.getLong(SerialNumberEnum.ID))
					.setProjectId(result.getLong(SerialNumberEnum.PROJECT_ID))
	        		.setReleaseId(result.getLong(SerialNumberEnum.RELEASE))
		        	.setSprintId(result.getLong(SerialNumberEnum.SPRINT))
		        	.setStoryId(result.getLong(SerialNumberEnum.STORY))
		        	.setTaskId(result.getLong(SerialNumberEnum.TASK))
		        	.setUnplannedId(result.getLong(SerialNumberEnum.UNPLANNED))
		        	.setRetrospectiveId(result.getLong(SerialNumberEnum.RETROSPECTIVE));;
		return serialNumber;
	}
}
