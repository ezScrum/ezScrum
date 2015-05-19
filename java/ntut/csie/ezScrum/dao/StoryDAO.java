package ntut.csie.ezScrum.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.web.dataObject.SerialNumberObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.databasEnum.SerialNumberEnum;
import ntut.csie.ezScrum.web.databasEnum.StoryEnum;

public class StoryDAO extends AbstractDAO<StoryObject, StoryObject> {
	private static StoryDAO sInstance = null;

	public static StoryDAO getInstance() {
		if (sInstance == null) {
			sInstance = new StoryDAO();
		}
		return sInstance;
	}

	@Override
	public long create(StoryObject story) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		long currentTime = System.currentTimeMillis();
		SerialNumberObject serialNumber = SerialNumberDAO.getInstance().get(
				story.getProjectId());

		valueSet.addTableName(StoryEnum.TABLE_NAME);
		valueSet.addInsertValue(StoryEnum.SERIAL_ID,
				serialNumber.getStoryId() + 1);
		valueSet.addInsertValue(StoryEnum.NAME, story.getName());
		valueSet.addInsertValue(StoryEnum.STATUS, story.getStatus());
		valueSet.addInsertValue(StoryEnum.ESTIMATE, story.getEstimate());
		valueSet.addInsertValue(StoryEnum.IMPORTANCE, story.getImportance());
		valueSet.addInsertValue(StoryEnum.VALUE, story.getValue());
		valueSet.addInsertValue(StoryEnum.NOTES, story.getNotes());
		valueSet.addInsertValue(StoryEnum.HOW_TO_DEMO, story.getHowToDemo());
		valueSet.addInsertValue(StoryEnum.PROJECT_ID, story.getProjectId());
		valueSet.addInsertValue(StoryEnum.SPRINT_ID, story.getSprintId());
		valueSet.addInsertValue(StoryEnum.CREATE_TIME, currentTime);
		valueSet.addInsertValue(StoryEnum.UPDATE_TIME, currentTime);
		String query = valueSet.getInsertQuery();
		long id = mControl.executeInsert(query);
		serialNumber.setId(SerialNumberEnum.STORY,
				serialNumber.getStoryId() + 1);
		serialNumber.save();
		return id;
	}

	@Override
	public StoryObject get(long id) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(StoryEnum.TABLE_NAME);
		valueSet.addEqualCondition(StoryEnum.ID, id);
		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);
		StoryObject story = null;
		try {
			if (result.next()) {
				story = convert(result);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		return story;
	}

	@Override
	public boolean update(StoryObject story) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(StoryEnum.TABLE_NAME);
		valueSet.addInsertValue(StoryEnum.NAME, story.getName());
		valueSet.addInsertValue(StoryEnum.STATUS, story.getStatus());
		valueSet.addInsertValue(StoryEnum.ESTIMATE, story.getEstimate());
		valueSet.addInsertValue(StoryEnum.IMPORTANCE, story.getImportance());
		valueSet.addInsertValue(StoryEnum.VALUE, story.getValue());
		valueSet.addInsertValue(StoryEnum.NOTES, story.getNotes());
		valueSet.addInsertValue(StoryEnum.HOW_TO_DEMO, story.getHowToDemo());
		valueSet.addInsertValue(StoryEnum.SPRINT_ID, story.getSprintId());
		valueSet.addInsertValue(StoryEnum.UPDATE_TIME, story.getUpdateTime());
		valueSet.addEqualCondition(StoryEnum.ID, story.getId());
		String query = valueSet.getUpdateQuery();
		return mControl.executeUpdate(query);
	}

	@Override
	public boolean delete(long id) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(StoryEnum.TABLE_NAME);
		valueSet.addEqualCondition(StoryEnum.ID, id);
		String query = valueSet.getDeleteQuery();
		return mControl.executeUpdate(query);
	}

	public ArrayList<StoryObject> getStoriesBySprintId(long sprintId) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(StoryEnum.TABLE_NAME);
		valueSet.addEqualCondition(StoryEnum.SPRINT_ID, sprintId);
		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);
		ArrayList<StoryObject> stories = new ArrayList<StoryObject>();
		try {
			while (result.next()) {
				stories.add(convert(result));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		return stories;
	}

	public ArrayList<StoryObject> getStoriesWithNoParent(long projectId) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(StoryEnum.TABLE_NAME);
		valueSet.addEqualCondition(StoryEnum.SPRINT_ID, StoryObject.NO_PARENT);
		valueSet.addEqualCondition(StoryEnum.PROJECT_ID, projectId);
		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);
		ArrayList<StoryObject> stories = new ArrayList<StoryObject>();
		try {
			while (result.next()) {
				stories.add(convert(result));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		return stories;
	}

	public ArrayList<StoryObject> getStoriesByProjectId(long projectId) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(StoryEnum.TABLE_NAME);
		valueSet.addEqualCondition(StoryEnum.PROJECT_ID, projectId);
		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);
		ArrayList<StoryObject> stories = new ArrayList<StoryObject>();
		try {
			while (result.next()) {
				stories.add(convert(result));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		return stories;
	}

	public static StoryObject convert(ResultSet result) throws SQLException {
		StoryObject story = new StoryObject(result.getLong(StoryEnum.ID),
				result.getLong(StoryEnum.SERIAL_ID),
				result.getLong(StoryEnum.PROJECT_ID));
		story.setName(result.getString(StoryEnum.NAME))
				.setStatus(result.getInt(StoryEnum.STATUS))
				.setEstimate(result.getInt(StoryEnum.ESTIMATE))
				.setImportance(result.getInt(StoryEnum.IMPORTANCE))
				.setValue(result.getInt(StoryEnum.VALUE))
				.setNotes(result.getString(StoryEnum.NOTES))
				.setHowToDemo(result.getString(StoryEnum.HOW_TO_DEMO))
				.setSprintId(result.getLong(StoryEnum.SPRINT_ID))
				.setCreateTime(result.getLong(StoryEnum.CREATE_TIME))
				.setUpdateTime(result.getLong(StoryEnum.UPDATE_TIME));
		return story;
	}
}
