package ntut.csie.ezScrum.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.web.dataObject.SerialNumberObject;
import ntut.csie.ezScrum.web.dataObject.UnplanObject;
import ntut.csie.ezScrum.web.databaseEnum.IssuePartnerRelationEnum;
import ntut.csie.ezScrum.web.databaseEnum.IssueTypeEnum;
import ntut.csie.ezScrum.web.databaseEnum.UnplanEnum;

/**
 * @author AllenHuang 2015/08/21
 */

public class UnplanDAO extends AbstractDAO<UnplanObject, UnplanObject> {

	private static UnplanDAO sInstance = null;
	
	public static UnplanDAO getInstance() {
		if (sInstance == null) {
			sInstance = new UnplanDAO();
		}
		return sInstance;
	}
	
	@Override
    public long create(UnplanObject unplan) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		long currentTime = System.currentTimeMillis();
		SerialNumberObject serialNumber = SerialNumberDAO.getInstance().getByProjectId(
				unplan.getProjectId());

		long unplanId = serialNumber.getUnplanId() + 1;
		valueSet.addTableName(UnplanEnum.TABLE_NAME);
		valueSet.addInsertValue(UnplanEnum.SERIAL_ID, unplanId);
		valueSet.addInsertValue(UnplanEnum.NAME, unplan.getName());
		valueSet.addInsertValue(UnplanEnum.HANDLER_ID, unplan.getHandlerId());
		valueSet.addInsertValue(UnplanEnum.ESTIMATE, unplan.getEstimate());
		valueSet.addInsertValue(UnplanEnum.ACTUAL, unplan.getActual());
		valueSet.addInsertValue(UnplanEnum.NOTES, unplan.getNotes());
		valueSet.addInsertValue(UnplanEnum.STATUS, unplan.getStatus());
		valueSet.addInsertValue(UnplanEnum.PROJECT_ID, unplan.getProjectId());
		valueSet.addInsertValue(UnplanEnum.SPRINT_ID, unplan.getSprintId());
		if (unplan.getCreateTime() > 0) {
			valueSet.addInsertValue(UnplanEnum.CREATE_TIME, unplan.getCreateTime());
			valueSet.addInsertValue(UnplanEnum.UPDATE_TIME, unplan.getCreateTime());
		} else {
			valueSet.addInsertValue(UnplanEnum.CREATE_TIME, currentTime);
			valueSet.addInsertValue(UnplanEnum.UPDATE_TIME, currentTime);
		}
		String query = valueSet.getInsertQuery();
		long id = mControl.executeInsert(query);

		serialNumber.setUnplanId(unplanId);
		serialNumber.save();
		
	    return id;
    }

	@Override
    public UnplanObject get(long id) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(UnplanEnum.TABLE_NAME);
		valueSet.addEqualCondition(UnplanEnum.ID, id);
		String query = valueSet.getSelectQuery();

		ResultSet result = mControl.executeQuery(query);

		UnplanObject unplan = null;
		try {
			if (result.next()) {
				unplan = convert(result);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		return unplan;
    }
	
    public UnplanObject get(long projectId, long serialId) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(UnplanEnum.TABLE_NAME);
		valueSet.addEqualCondition(UnplanEnum.PROJECT_ID, projectId);
		valueSet.addEqualCondition(UnplanEnum.SERIAL_ID, serialId);
		String query = valueSet.getSelectQuery();

		ResultSet result = mControl.executeQuery(query);

		UnplanObject unplan = null;
		try {
			if (result.next()) {
				unplan = convert(result);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		return unplan;
    }

	@Override
    public boolean update(UnplanObject unplan) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(UnplanEnum.TABLE_NAME);
		valueSet.addInsertValue(UnplanEnum.NAME, unplan.getName());
		valueSet.addInsertValue(UnplanEnum.HANDLER_ID, unplan.getHandlerId());
		valueSet.addInsertValue(UnplanEnum.ESTIMATE, unplan.getEstimate());
		valueSet.addInsertValue(UnplanEnum.ACTUAL, unplan.getActual());
		valueSet.addInsertValue(UnplanEnum.NOTES, unplan.getNotes());
		valueSet.addInsertValue(UnplanEnum.STATUS, unplan.getStatus());
		valueSet.addInsertValue(UnplanEnum.SPRINT_ID, unplan.getSprintId());
		valueSet.addInsertValue(UnplanEnum.UPDATE_TIME, unplan.getUpdateTime());
		valueSet.addEqualCondition(UnplanEnum.ID, unplan.getId());
		String query = valueSet.getUpdateQuery();

		return mControl.executeUpdate(query);
    }

	@Override
    public boolean delete(long id) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(UnplanEnum.TABLE_NAME);
		valueSet.addEqualCondition(UnplanEnum.ID, id);
		String query = valueSet.getDeleteQuery();
		return mControl.executeUpdate(query);
    }

	public ArrayList<UnplanObject> getUnplanBySprintId(long sprintId) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(UnplanEnum.TABLE_NAME);
		valueSet.addEqualCondition(UnplanEnum.SPRINT_ID, sprintId);
		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);

		ArrayList<UnplanObject> unplans = new ArrayList<UnplanObject>();
		try {
			while (result.next()) {
				unplans.add(convert(result));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		return unplans;
	}
	
	/**
	 * Get all unplans.
	 * 
	 * @param projectId
	 * @return All unplans which in this project
	 */
	public ArrayList<UnplanObject> getUnplansByProjectId(long projectId) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(UnplanEnum.TABLE_NAME);
		valueSet.addEqualCondition(UnplanEnum.PROJECT_ID, projectId);
		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);

		ArrayList<UnplanObject> unplans = new ArrayList<UnplanObject>();
		try {
			while (result.next()) {
				unplans.add(convert(result));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		
		return unplans;
	}
	
	public ArrayList<Long> getPartnersId(long unplanId) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(IssuePartnerRelationEnum.TABLE_NAME);
		valueSet.addEqualCondition(IssuePartnerRelationEnum.ISSUE_ID,
				Long.toString(unplanId));
		valueSet.addEqualCondition(IssuePartnerRelationEnum.ISSUE_TYPE,
				IssueTypeEnum.TYPE_UNPLAN);
		valueSet.setOrderBy(IssuePartnerRelationEnum.ID, IQueryValueSet.ASC_ORDER);
		String query = valueSet.getSelectQuery();

		ArrayList<Long> partnersId = new ArrayList<Long>();
		ResultSet result = mControl.executeQuery(query);
		try {
			while (result.next()) {
				partnersId.add(result
						.getLong(IssuePartnerRelationEnum.ACCOUNT_ID));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		return partnersId;
	}
	
	public long addPartner(long unplanId, long partnerId) {
		long id = -1;
		if (!partnerExists(unplanId, partnerId)) {
			IQueryValueSet valueSet = new MySQLQuerySet();
			valueSet.addTableName(IssuePartnerRelationEnum.TABLE_NAME);
			valueSet.addInsertValue(IssuePartnerRelationEnum.ISSUE_ID, unplanId);
			valueSet.addInsertValue(IssuePartnerRelationEnum.ACCOUNT_ID,
					partnerId);
			valueSet.addInsertValue(IssuePartnerRelationEnum.ISSUE_TYPE,
					IssueTypeEnum.TYPE_UNPLAN);
			String query = valueSet.getInsertQuery();
			id = mControl.executeInsert(query);
		}

		return id;
	}
	
	public boolean removePartner(long unplanId, long partnerId) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(IssuePartnerRelationEnum.TABLE_NAME);
		valueSet.addEqualCondition(IssuePartnerRelationEnum.ISSUE_ID, unplanId);
		valueSet.addEqualCondition(IssuePartnerRelationEnum.ACCOUNT_ID, partnerId);
		valueSet.addEqualCondition(IssuePartnerRelationEnum.ISSUE_TYPE, IssueTypeEnum.TYPE_UNPLAN);
		String query = valueSet.getDeleteQuery();
		return mControl.executeUpdate(query);
	}

	public boolean partnerExists(long unplanId, long partnerId) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(IssuePartnerRelationEnum.TABLE_NAME);
		valueSet.addEqualCondition(IssuePartnerRelationEnum.ISSUE_TYPE,
				IssueTypeEnum.TYPE_UNPLAN);
		valueSet.addEqualCondition(IssuePartnerRelationEnum.ISSUE_ID, unplanId);
		valueSet.addEqualCondition(IssuePartnerRelationEnum.ACCOUNT_ID,
				partnerId);
		String query = valueSet.getSelectQuery();

		int size = 0;
		ResultSet result = mControl.executeQuery(query);
		try {
			while (result.next()) {
				size++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		if (size > 0) {
			return true;
		}
		return false;
	}
	
	public static UnplanObject convert(ResultSet result) throws SQLException {
		UnplanObject unplan = new UnplanObject(result.getLong(UnplanEnum.ID),
				result.getLong(UnplanEnum.SERIAL_ID),
				result.getLong(UnplanEnum.PROJECT_ID));
		unplan.setName(result.getString(UnplanEnum.NAME))
				.setHandlerId(result.getLong(UnplanEnum.HANDLER_ID))
				.setEstimate(result.getInt(UnplanEnum.ESTIMATE))
				.setActual(result.getInt(UnplanEnum.ACTUAL))
				.setStatus(result.getInt(UnplanEnum.STATUS))
				.setNotes(result.getString(UnplanEnum.NOTES))
				.setSprintId(result.getLong(UnplanEnum.SPRINT_ID))
				.setCreateTime(result.getLong(UnplanEnum.CREATE_TIME))
				.setUpdateTime(result.getLong(UnplanEnum.UPDATE_TIME));
		return unplan;
	}
}
