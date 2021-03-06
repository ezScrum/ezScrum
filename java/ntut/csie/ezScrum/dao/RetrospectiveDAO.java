package ntut.csie.ezScrum.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.web.dataObject.RetrospectiveObject;
import ntut.csie.ezScrum.web.dataObject.SerialNumberObject;
import ntut.csie.ezScrum.web.databaseEnum.RetrospectiveEnum;

public class RetrospectiveDAO extends
		AbstractDAO<RetrospectiveObject, RetrospectiveObject> {

	private static RetrospectiveDAO sInstance = null;

	public static RetrospectiveDAO getInstance() {
		if (sInstance == null) {
			sInstance = new RetrospectiveDAO();
		}
		return sInstance;
	}

	@Override
	public long create(RetrospectiveObject retrospective) {
		long currentTime = System.currentTimeMillis();

		IQueryValueSet valueSet = new MySQLQuerySet();
		SerialNumberObject serialNumber = SerialNumberDAO.getInstance().get(retrospective.getProjectId());

		long retorspectiveSerialId = serialNumber.getRetrospectiveId() + 1;

		valueSet.addTableName(RetrospectiveEnum.TABLE_NAME);
		valueSet.addInsertValue(RetrospectiveEnum.SERIAL_ID, retorspectiveSerialId);
		valueSet.addInsertValue(RetrospectiveEnum.NAME, retrospective.getName());
		valueSet.addInsertValue(RetrospectiveEnum.DESCRIPTION, retrospective.getDescription());
		valueSet.addInsertValue(RetrospectiveEnum.TYPE, retrospective.getType());
		valueSet.addInsertValue(RetrospectiveEnum.STATUS, RetrospectiveObject.STATUS_NEW); // 新增後的狀態為new
		valueSet.addInsertValue(RetrospectiveEnum.SPRINT_ID, retrospective.getSprintId());
		valueSet.addInsertValue(RetrospectiveEnum.PROJECT_ID, retrospective.getProjectId());
		valueSet.addInsertValue(RetrospectiveEnum.CREATE_TIME, currentTime);
		valueSet.addInsertValue(RetrospectiveEnum.UPDATE_TIME, currentTime);

		String query = valueSet.getInsertQuery();
		long id = mControl.executeInsert(query);

		serialNumber.setRetrospectiveId(retorspectiveSerialId);
		serialNumber.save();
		return id;
	}

	@Override
	public RetrospectiveObject get(long id) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(RetrospectiveEnum.TABLE_NAME);
		valueSet.addEqualCondition(RetrospectiveEnum.ID, id);

		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);

		RetrospectiveObject retrospective = null;
		try {
			if (result.next()) {
				retrospective = convert(result);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		return retrospective;
	}

	@Override
	public boolean update(RetrospectiveObject retrospective) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(RetrospectiveEnum.TABLE_NAME);
		valueSet.addInsertValue(RetrospectiveEnum.NAME, retrospective.getName());
		valueSet.addInsertValue(RetrospectiveEnum.DESCRIPTION, retrospective.getDescription());
		valueSet.addInsertValue(RetrospectiveEnum.TYPE, retrospective.getType());
		valueSet.addInsertValue(RetrospectiveEnum.SPRINT_ID, retrospective.getSprintId());
		valueSet.addInsertValue(RetrospectiveEnum.STATUS, retrospective.getStatus());
		valueSet.addInsertValue(RetrospectiveEnum.UPDATE_TIME, retrospective.getUpdateTime());
		valueSet.addEqualCondition(RetrospectiveEnum.ID, retrospective.getId());
		String query = valueSet.getUpdateQuery();

		return mControl.executeUpdate(query);
	}

	@Override
	public boolean delete(long id) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(RetrospectiveEnum.TABLE_NAME);
		valueSet.addEqualCondition(RetrospectiveEnum.ID, id);
		String query = valueSet.getDeleteQuery();

		return mControl.executeUpdate(query);
	}
	
	public ArrayList<RetrospectiveObject> getRetrospectivesBySprintId(long sprintId) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(RetrospectiveEnum.TABLE_NAME);
		valueSet.addEqualCondition(RetrospectiveEnum.SPRINT_ID, sprintId);
		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);
		ArrayList<RetrospectiveObject> retrospectives = new ArrayList<>();
		try {
			while (result.next()) {
				retrospectives.add(convert(result));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		return retrospectives;
	}

	public static RetrospectiveObject convert(ResultSet result) throws SQLException{
		RetrospectiveObject retrospective = new RetrospectiveObject(
				result.getLong(RetrospectiveEnum.ID), 
				result.getLong(RetrospectiveEnum.SERIAL_ID),
				result.getLong(RetrospectiveEnum.PROJECT_ID));
		retrospective.setName(result.getString(RetrospectiveEnum.NAME))
				     .setDescription(result.getString(RetrospectiveEnum.DESCRIPTION))
				     .setType(result.getString(RetrospectiveEnum.TYPE))
				     .setStatus(result.getString(RetrospectiveEnum.STATUS))
				     .setSprintId(result.getLong(RetrospectiveEnum.SPRINT_ID))
				     .setProjectId(result.getLong(RetrospectiveEnum.PROJECT_ID))
				     .setCreateTime(result.getLong(RetrospectiveEnum.CREATE_TIME))
				     .setUpdateTime(result.getLong(RetrospectiveEnum.UPDATE_TIME));
		return retrospective;
	}
}
