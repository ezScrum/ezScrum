package ntut.csie.ezScrum.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.web.dataObject.SerialNumberObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.databasEnum.IssuePartnerRelationEnum;
import ntut.csie.ezScrum.web.databasEnum.IssueTypeEnum;
import ntut.csie.ezScrum.web.databasEnum.SerialNumberEnum;
import ntut.csie.ezScrum.web.databasEnum.TaskEnum;

public class TaskDAO extends AbstractDAO<TaskObject, TaskObject> {

	private static TaskDAO sInstance = null;

	public static TaskDAO getInstance() {
		if (sInstance == null) {
			sInstance = new TaskDAO();
		}
		return sInstance;
	}

	@Override
	public long create(TaskObject task) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		long currentTime = System.currentTimeMillis();
		SerialNumberObject serialNumber = SerialNumberDAO.getInstance().get(
				task.getProjectId());

		valueSet.addTableName(TaskEnum.TABLE_NAME);
		valueSet.addInsertValue(TaskEnum.SERIAL_ID,
				serialNumber.getTaskId() + 1);
		valueSet.addInsertValue(TaskEnum.NAME, task.getName());
		valueSet.addInsertValue(TaskEnum.HANDLER_ID, task.getHandlerId());
		valueSet.addInsertValue(TaskEnum.ESTIMATE, task.getEstimate());
		valueSet.addInsertValue(TaskEnum.REMAIN, task.getRemains());
		valueSet.addInsertValue(TaskEnum.ACTUAL, task.getActual());
		valueSet.addInsertValue(TaskEnum.NOTES, task.getNotes());
		valueSet.addInsertValue(TaskEnum.STATUS, task.getStatus());
		valueSet.addInsertValue(TaskEnum.PROJECT_ID, task.getProjectId());
		valueSet.addInsertValue(TaskEnum.STORY_ID, task.getStoryId());
		if (task.getCreateTime() > 0) {
			valueSet.addInsertValue(TaskEnum.CREATE_TIME, task.getCreateTime());
			valueSet.addInsertValue(TaskEnum.UPDATE_TIME, task.getCreateTime());
		} else {
			valueSet.addInsertValue(TaskEnum.CREATE_TIME, currentTime);
			valueSet.addInsertValue(TaskEnum.UPDATE_TIME, currentTime);
		}
		String query = valueSet.getInsertQuery();
		long id = mControl.executeInsert(query);

		serialNumber.setId(SerialNumberEnum.TASK, serialNumber.getTaskId() + 1);
		serialNumber.save();

		return id;
	}

	@Override
	public TaskObject get(long id) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(TaskEnum.TABLE_NAME);
		valueSet.addEqualCondition(TaskEnum.ID, id);
		String query = valueSet.getSelectQuery();

		ResultSet result = mControl.executeQuery(query);

		TaskObject task = null;
		try {
			if (result.next()) {
				task = convert(result);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		return task;
	}

	@Override
	public boolean update(TaskObject task) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(TaskEnum.TABLE_NAME);
		valueSet.addInsertValue(TaskEnum.NAME, task.getName());
		valueSet.addInsertValue(TaskEnum.HANDLER_ID, task.getHandlerId());
		valueSet.addInsertValue(TaskEnum.ESTIMATE, task.getEstimate());
		valueSet.addInsertValue(TaskEnum.REMAIN, task.getRemains());
		valueSet.addInsertValue(TaskEnum.ACTUAL, task.getActual());
		valueSet.addInsertValue(TaskEnum.NOTES, task.getNotes());
		valueSet.addInsertValue(TaskEnum.STATUS, task.getStatus());
		valueSet.addInsertValue(TaskEnum.STORY_ID, task.getStoryId());
		valueSet.addInsertValue(TaskEnum.UPDATE_TIME, task.getUpdateTime());
		valueSet.addEqualCondition(TaskEnum.ID, task.getId());
		String query = valueSet.getUpdateQuery();

		return mControl.executeUpdate(query);
	}

	@Override
	public boolean delete(long id) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(TaskEnum.TABLE_NAME);
		valueSet.addEqualCondition(TaskEnum.ID, id);
		String query = valueSet.getDeleteQuery();
		return mControl.executeUpdate(query);
	}

	public ArrayList<TaskObject> getTasksByStoryId(long storyId) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(TaskEnum.TABLE_NAME);
		valueSet.addEqualCondition(TaskEnum.STORY_ID, storyId);
		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);

		ArrayList<TaskObject> tasks = new ArrayList<TaskObject>();
		try {
			while (result.next()) {
				tasks.add(convert(result));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		return tasks;
	}

	/**
	 * Get the tasks which do not have story id.
	 * 
	 * @param projectId
	 * @return All tasks which no parent in this project
	 */
	public ArrayList<TaskObject> getTasksWithNoParent(long projectId) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(TaskEnum.TABLE_NAME);
		valueSet.addEqualCondition(TaskEnum.STORY_ID, TaskObject.NO_PARENT);
		valueSet.addEqualCondition(TaskEnum.PROJECT_ID, projectId);
		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);

		ArrayList<TaskObject> tasks = new ArrayList<TaskObject>();
		try {
			while (result.next()) {
				tasks.add(convert(result));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		
		return tasks;
	}

	public ArrayList<Long> getPartnersId(long taskId) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(IssuePartnerRelationEnum.TABLE_NAME);
		valueSet.addEqualCondition(IssuePartnerRelationEnum.ISSUE_ID,
				Long.toString(taskId));
		valueSet.addEqualCondition(IssuePartnerRelationEnum.ISSUE_TYPE,
				IssueTypeEnum.TYPE_TASK);
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

	public long addPartner(long taskId, long partnerId) {
		long id = -1;
		if (!partnerExists(taskId, partnerId)) {
			IQueryValueSet valueSet = new MySQLQuerySet();
			valueSet.addTableName(IssuePartnerRelationEnum.TABLE_NAME);
			valueSet.addInsertValue(IssuePartnerRelationEnum.ISSUE_ID, taskId);
			valueSet.addInsertValue(IssuePartnerRelationEnum.ACCOUNT_ID,
					partnerId);
			valueSet.addInsertValue(IssuePartnerRelationEnum.ISSUE_TYPE,
					IssueTypeEnum.TYPE_TASK);
			String query = valueSet.getInsertQuery();
			id = mControl.executeInsert(query);
		}

		return id;
	}
	
	public boolean removePartner(long taskId, long partnerId) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(IssuePartnerRelationEnum.TABLE_NAME);
		valueSet.addEqualCondition(IssuePartnerRelationEnum.ISSUE_ID, taskId);
		valueSet.addEqualCondition(IssuePartnerRelationEnum.ACCOUNT_ID, partnerId);
		valueSet.addEqualCondition(IssuePartnerRelationEnum.ISSUE_TYPE, IssueTypeEnum.TYPE_TASK);
		String query = valueSet.getDeleteQuery();
		return mControl.executeUpdate(query);
	}

	public boolean partnerExists(long taskId, long partnerId) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(IssuePartnerRelationEnum.TABLE_NAME);
		valueSet.addEqualCondition(IssuePartnerRelationEnum.ISSUE_TYPE,
				IssueTypeEnum.TYPE_TASK);
		valueSet.addEqualCondition(IssuePartnerRelationEnum.ISSUE_ID, taskId);
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

	public static TaskObject convert(ResultSet result) throws SQLException {
		TaskObject task = new TaskObject(result.getLong(TaskEnum.ID),
				result.getLong(TaskEnum.SERIAL_ID),
				result.getLong(TaskEnum.PROJECT_ID));
		task.setName(result.getString(TaskEnum.NAME))
				.setHandlerId(result.getLong(TaskEnum.HANDLER_ID))
				.setEstimate(result.getInt(TaskEnum.ESTIMATE))
				.setRemains(result.getInt(TaskEnum.REMAIN))
				.setActual(result.getInt(TaskEnum.ACTUAL))
				.setStatus(result.getInt(TaskEnum.STATUS))
				.setNotes(result.getString(TaskEnum.NOTES))
				.setStoryId(result.getLong(TaskEnum.STORY_ID))
				.setCreateTime(result.getLong(TaskEnum.CREATE_TIME))
				.setUpdateTime(result.getLong(TaskEnum.UPDATE_TIME));
		return task;
	}
}
