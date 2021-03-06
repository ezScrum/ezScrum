package ntut.csie.ezScrum.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.web.dataObject.SerialNumberObject;
import ntut.csie.ezScrum.web.dataObject.UnplannedObject;
import ntut.csie.ezScrum.web.databasEnum.UnplannedEnum;
import ntut.csie.ezScrum.web.databaseEnum.IssuePartnerRelationEnum;
import ntut.csie.ezScrum.web.databaseEnum.IssueTypeEnum;

/**
 * @author AllenHuang 2015/08/21
 */

public class UnplannedDAO extends AbstractDAO<UnplannedObject, UnplannedObject> {

	private static UnplannedDAO sInstance = null;
	
	public static UnplannedDAO getInstance() {
		if (sInstance == null) {
			sInstance = new UnplannedDAO();
		}
		return sInstance;
	}
	
	@Override
    public long create(UnplannedObject unplanned) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		long currentTime = System.currentTimeMillis();
		SerialNumberObject serialNumber = SerialNumberDAO.getInstance().get(
				unplanned.getProjectId());

		long unplannedId = serialNumber.getUnplannedId() + 1;
		valueSet.addTableName(UnplannedEnum.TABLE_NAME);
		valueSet.addInsertValue(UnplannedEnum.SERIAL_ID, unplannedId);
		valueSet.addInsertValue(UnplannedEnum.NAME, unplanned.getName());
		valueSet.addInsertValue(UnplannedEnum.HANDLER_ID, unplanned.getHandlerId());
		valueSet.addInsertValue(UnplannedEnum.ESTIMATE, unplanned.getEstimate());
		valueSet.addInsertValue(UnplannedEnum.ACTUAL, unplanned.getActual());
		valueSet.addInsertValue(UnplannedEnum.NOTES, unplanned.getNotes());
		valueSet.addInsertValue(UnplannedEnum.STATUS, unplanned.getStatus());
		valueSet.addInsertValue(UnplannedEnum.PROJECT_ID, unplanned.getProjectId());
		valueSet.addInsertValue(UnplannedEnum.SPRINT_ID, unplanned.getSprintId());
		if (unplanned.getCreateTime() > 0) {
			valueSet.addInsertValue(UnplannedEnum.CREATE_TIME, unplanned.getCreateTime());
			valueSet.addInsertValue(UnplannedEnum.UPDATE_TIME, unplanned.getCreateTime());
		} else {
			valueSet.addInsertValue(UnplannedEnum.CREATE_TIME, currentTime);
			valueSet.addInsertValue(UnplannedEnum.UPDATE_TIME, currentTime);
		}
		String query = valueSet.getInsertQuery();
		long id = mControl.executeInsert(query);

		serialNumber.setUnplannedId(unplannedId);
		serialNumber.save();
		
	    return id;
    }

	@Override
    public UnplannedObject get(long id) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(UnplannedEnum.TABLE_NAME);
		valueSet.addEqualCondition(UnplannedEnum.ID, id);
		String query = valueSet.getSelectQuery();

		ResultSet result = mControl.executeQuery(query);

		UnplannedObject unplanned = null;
		try {
			if (result.next()) {
				unplanned = convert(result);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		return unplanned;
    }

	@Override
    public boolean update(UnplannedObject unplanned) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(UnplannedEnum.TABLE_NAME);
		valueSet.addInsertValue(UnplannedEnum.NAME, unplanned.getName());
		valueSet.addInsertValue(UnplannedEnum.HANDLER_ID, unplanned.getHandlerId());
		valueSet.addInsertValue(UnplannedEnum.ESTIMATE, unplanned.getEstimate());
		valueSet.addInsertValue(UnplannedEnum.ACTUAL, unplanned.getActual());
		valueSet.addInsertValue(UnplannedEnum.NOTES, unplanned.getNotes());
		valueSet.addInsertValue(UnplannedEnum.STATUS, unplanned.getStatus());
		valueSet.addInsertValue(UnplannedEnum.SPRINT_ID, unplanned.getSprintId());
		valueSet.addInsertValue(UnplannedEnum.UPDATE_TIME, unplanned.getUpdateTime());
		valueSet.addEqualCondition(UnplannedEnum.ID, unplanned.getId());
		String query = valueSet.getUpdateQuery();

		return mControl.executeUpdate(query);
    }

	@Override
    public boolean delete(long id) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(UnplannedEnum.TABLE_NAME);
		valueSet.addEqualCondition(UnplannedEnum.ID, id);
		String query = valueSet.getDeleteQuery();
		return mControl.executeUpdate(query);
    }

	public ArrayList<UnplannedObject> getUnplannedBySprintId(long sprintId) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(UnplannedEnum.TABLE_NAME);
		valueSet.addEqualCondition(UnplannedEnum.SPRINT_ID, sprintId);
		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);

		ArrayList<UnplannedObject> unplanneds = new ArrayList<UnplannedObject>();
		try {
			while (result.next()) {
				unplanneds.add(convert(result));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		return unplanneds;
	}
	
	/**
	 * Get all unplanneds.
	 * 
	 * @param projectId
	 * @return All unplanneds which in this project
	 */
	public ArrayList<UnplannedObject> getUnplannedsByProjectId(long projectId) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(UnplannedEnum.TABLE_NAME);
		valueSet.addEqualCondition(UnplannedEnum.PROJECT_ID, projectId);
		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);

		ArrayList<UnplannedObject> unplanneds = new ArrayList<UnplannedObject>();
		try {
			while (result.next()) {
				unplanneds.add(convert(result));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		
		return unplanneds;
	}
	
	public ArrayList<Long> getPartnersId(long unplannedId) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(IssuePartnerRelationEnum.TABLE_NAME);
		valueSet.addEqualCondition(IssuePartnerRelationEnum.ISSUE_ID,
				Long.toString(unplannedId));
		valueSet.addEqualCondition(IssuePartnerRelationEnum.ISSUE_TYPE,
				IssueTypeEnum.TYPE_UNPLANNED);
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
	
	public long addPartner(long unplannedId, long partnerId) {
		long id = -1;
		if (!partnerExists(unplannedId, partnerId)) {
			IQueryValueSet valueSet = new MySQLQuerySet();
			valueSet.addTableName(IssuePartnerRelationEnum.TABLE_NAME);
			valueSet.addInsertValue(IssuePartnerRelationEnum.ISSUE_ID, unplannedId);
			valueSet.addInsertValue(IssuePartnerRelationEnum.ACCOUNT_ID,
					partnerId);
			valueSet.addInsertValue(IssuePartnerRelationEnum.ISSUE_TYPE,
					IssueTypeEnum.TYPE_UNPLANNED);
			String query = valueSet.getInsertQuery();
			id = mControl.executeInsert(query);
		}

		return id;
	}
	
	public boolean removePartner(long unplannedId, long partnerId) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(IssuePartnerRelationEnum.TABLE_NAME);
		valueSet.addEqualCondition(IssuePartnerRelationEnum.ISSUE_ID, unplannedId);
		valueSet.addEqualCondition(IssuePartnerRelationEnum.ACCOUNT_ID, partnerId);
		valueSet.addEqualCondition(IssuePartnerRelationEnum.ISSUE_TYPE, IssueTypeEnum.TYPE_UNPLANNED);
		String query = valueSet.getDeleteQuery();
		return mControl.executeUpdate(query);
	}

	public boolean partnerExists(long unplannedId, long partnerId) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(IssuePartnerRelationEnum.TABLE_NAME);
		valueSet.addEqualCondition(IssuePartnerRelationEnum.ISSUE_TYPE,
				IssueTypeEnum.TYPE_UNPLANNED);
		valueSet.addEqualCondition(IssuePartnerRelationEnum.ISSUE_ID, unplannedId);
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
	
	public static UnplannedObject convert(ResultSet result) throws SQLException {
		UnplannedObject unplanned = new UnplannedObject(result.getLong(UnplannedEnum.ID),
				result.getLong(UnplannedEnum.SERIAL_ID),
				result.getLong(UnplannedEnum.PROJECT_ID));
		unplanned.setName(result.getString(UnplannedEnum.NAME))
				.setHandlerId(result.getLong(UnplannedEnum.HANDLER_ID))
				.setEstimate(result.getInt(UnplannedEnum.ESTIMATE))
				.setActual(result.getInt(UnplannedEnum.ACTUAL))
				.setStatus(result.getInt(UnplannedEnum.STATUS))
				.setNotes(result.getString(UnplannedEnum.NOTES))
				.setSprintId(result.getLong(UnplannedEnum.SPRINT_ID))
				.setCreateTime(result.getLong(UnplannedEnum.CREATE_TIME))
				.setUpdateTime(result.getLong(UnplannedEnum.UPDATE_TIME));
		return unplanned;
	}
}
