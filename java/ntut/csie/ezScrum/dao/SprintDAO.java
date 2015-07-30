package ntut.csie.ezScrum.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.web.dataObject.SerialNumberObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.databasEnum.SprintEnum;
import ntut.csie.jcis.core.util.DateUtil;

public class SprintDAO extends AbstractDAO<SprintObject, SprintObject> {

	private static SprintDAO sInstance = null;

	public static SprintDAO getInstance() {
		if (sInstance == null) {
			sInstance = new SprintDAO();
		}
		return sInstance;
	}

	@Override
	public long create(SprintObject sprint) {
		long currentTime = System.currentTimeMillis();

		IQueryValueSet valueSet = new MySQLQuerySet();
		SerialNumberObject serialNumber = SerialNumberDAO.getInstance().get(sprint.getProjectId());

		long sprintId = serialNumber.getSprintId() + 1;
		valueSet.addTableName(SprintEnum.TABLE_NAME);
		valueSet.addInsertValue(SprintEnum.SERIAL_ID, sprintId);
		valueSet.addInsertValue(SprintEnum.GOAL, sprint.getSprintGoal());
		valueSet.addInsertValue(SprintEnum.INTERVAL, sprint.getInterval());
		valueSet.addInsertValue(SprintEnum.MEMBERS, sprint.getMembersAmount());
		valueSet.addInsertValue(SprintEnum.AVAILABLE_HOURS, sprint.getHoursCanCommit());
		valueSet.addInsertValue(SprintEnum.FOCUS_FACTOR, sprint.getFocusFactor());
		valueSet.addInsertValue(SprintEnum.START_DATE, sprint.getStartDateString());
		valueSet.addInsertValue(SprintEnum.DUE_DATE, sprint.getDueDateString());
		valueSet.addInsertValue(SprintEnum.DEMO_DATE, sprint.getDemoDateString());
		valueSet.addInsertValue(SprintEnum.DEMO_PLACE, sprint.getDemoPlace());
		valueSet.addInsertValue(SprintEnum.DAILY_INFO, sprint.getDailyInfo());
		valueSet.addInsertValue(SprintEnum.PROJECT_ID, sprint.getProjectId());
		// if no create time
		if (sprint.getCreateTime() > 0) {
			valueSet.addInsertValue(SprintEnum.CREATE_TIME, sprint.getCreateTime());
			valueSet.addInsertValue(SprintEnum.UPDATE_TIME, sprint.getCreateTime());
		} else {
			valueSet.addInsertValue(SprintEnum.CREATE_TIME, currentTime);
			valueSet.addInsertValue(SprintEnum.UPDATE_TIME, currentTime);
		}

		String query = valueSet.getInsertQuery();
		long id = mControl.executeInsert(query);

		serialNumber.setSprintId(sprintId);
		serialNumber.save();

		return id;
	}

	@Override
	public SprintObject get(long id) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(SprintEnum.TABLE_NAME);
		valueSet.addEqualCondition(SprintEnum.ID, id);

		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);

		SprintObject sprint = null;
		try {
			if (result.next()) {
				sprint = convert(result);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		return sprint;
	}

	@Override
	public boolean update(SprintObject sprint) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(SprintEnum.TABLE_NAME);
		valueSet.addInsertValue(SprintEnum.GOAL, sprint.getSprintGoal());
		valueSet.addInsertValue(SprintEnum.INTERVAL, sprint.getInterval());
		valueSet.addInsertValue(SprintEnum.MEMBERS, sprint.getMembersAmount());
		valueSet.addInsertValue(SprintEnum.AVAILABLE_HOURS, sprint.getHoursCanCommit());
		valueSet.addInsertValue(SprintEnum.FOCUS_FACTOR, sprint.getFocusFactor());
		valueSet.addInsertValue(SprintEnum.START_DATE, sprint.getStartDateString());
		valueSet.addInsertValue(SprintEnum.DUE_DATE, sprint.getDueDateString());
		valueSet.addInsertValue(SprintEnum.DEMO_DATE, sprint.getDemoDateString());
		valueSet.addInsertValue(SprintEnum.DEMO_PLACE, sprint.getDemoPlace());
		valueSet.addInsertValue(SprintEnum.DAILY_INFO, sprint.getDailyInfo());
		valueSet.addInsertValue(SprintEnum.UPDATE_TIME, sprint.getUpdateTime());
		valueSet.addEqualCondition(SprintEnum.ID, sprint.getId());
		String query = valueSet.getUpdateQuery();

		return mControl.executeUpdate(query);
	}

	/**
	 * Update SerialId for SprintObject
	 */
	public boolean updateSerialId(long sprintId, long newSerialNumber) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(SprintEnum.TABLE_NAME);
		valueSet.addInsertValue(SprintEnum.SERIAL_ID, newSerialNumber);
		valueSet.addEqualCondition(SprintEnum.ID, sprintId);
		String query = valueSet.getUpdateQuery();

		return mControl.executeUpdate(query);
	}

	@Override
	public boolean delete(long id) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(SprintEnum.TABLE_NAME);
		valueSet.addEqualCondition(SprintEnum.ID, id);
		String query = valueSet.getDeleteQuery();
		return mControl.executeUpdate(query);
	}

	public ArrayList<SprintObject> getSprintsByProjectId(long projectId) {
		ArrayList<SprintObject> sprints = new ArrayList<SprintObject>();

		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(SprintEnum.TABLE_NAME);
		valueSet.addEqualCondition(SprintEnum.PROJECT_ID, projectId);

		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);

		try {
			while (result.next()) {
				sprints.add(convert(result));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		return sprints;
	}

	public static SprintObject convert(ResultSet result) throws SQLException {
		SprintObject sprint = new SprintObject(result.getLong(SprintEnum.ID),
		        result.getLong(SprintEnum.SERIAL_ID),
		        result.getLong(SprintEnum.PROJECT_ID));
		sprint.setSprintGoal(result.getString(SprintEnum.GOAL))
		        .setInterval(result.getInt(SprintEnum.INTERVAL))
		        .setMembers(result.getInt(SprintEnum.MEMBERS))
		        .setHoursCanCommit(result.getInt(SprintEnum.AVAILABLE_HOURS))
		        .setFocusFactor(result.getInt(SprintEnum.FOCUS_FACTOR))
		        .setStartDate(new SimpleDateFormat(DateUtil._8DIGIT_DATE_1).format(result.getDate(SprintEnum.START_DATE)))
		        .setDueDate(new SimpleDateFormat(DateUtil._8DIGIT_DATE_1).format(result.getDate(SprintEnum.DUE_DATE)))
		        .setDemoDate(new SimpleDateFormat(DateUtil._8DIGIT_DATE_1).format(result.getDate(SprintEnum.DEMO_DATE)))
		        .setDemoPlace(result.getString(SprintEnum.DEMO_PLACE))
		        .setDailyInfo(result.getString(SprintEnum.DAILY_INFO))
		        .setCreateTime(result.getLong(SprintEnum.CREATE_TIME))
		        .setUpdateTime(result.getLong(SprintEnum.UPDATE_TIME));
		return sprint;
	}
}
