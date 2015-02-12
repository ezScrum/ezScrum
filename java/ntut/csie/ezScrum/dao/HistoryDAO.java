package ntut.csie.ezScrum.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.databasEnum.HistoryEnum;
import ntut.csie.ezScrum.web.databasEnum.IssuePartnerRelationEnum;

public class HistoryDAO extends AbstractDAO<HistoryObject, HistoryObject> {

	private static HistoryDAO sInstance = null;
	
	public static HistoryDAO getInstance() {
		if (sInstance == null) {
			sInstance = new HistoryDAO();
		}
		return sInstance;
	}

	@Override
	public long create(HistoryObject historyInfo) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(HistoryEnum.TABLE_NAME);
		valueSet.addInsertValue(HistoryEnum.ISSUE_ID, historyInfo.getIssueId());
		valueSet.addInsertValue(HistoryEnum.ISSUE_TYPE,
				historyInfo.getIssueType());
		valueSet.addInsertValue(HistoryEnum.HISTORY_TYPE,
				historyInfo.getHistoryType());
		valueSet.addInsertValue(HistoryEnum.OLD_VALUE, historyInfo.getOldValue());
		valueSet.addInsertValue(HistoryEnum.NEW_VALUE, historyInfo.getNewValue());
		valueSet.addInsertValue(HistoryEnum.CREATE_TIME,
				historyInfo.getCreateTime());
		String query = valueSet.getInsertQuery();

		return mControl.executeInsert(query);
	}

	@Override
	public HistoryObject get(long id) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(HistoryEnum.TABLE_NAME);
		valueSet.addEqualCondition(HistoryEnum.ID, id);
		String query = valueSet.getSelectQuery();

		ResultSet result = mControl.executeQuery(query);

		HistoryObject history = null;
		try {
			if (result.next()) {
				history = convert(result);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return history;
	}

	@Override
	public boolean update(HistoryObject object) {
		return false;
	}

	@Override
	public boolean delete(long id) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(HistoryEnum.TABLE_NAME);
		valueSet.addEqualCondition(HistoryEnum.ID, id);
		String query = valueSet.getDeleteQuery();
		return mControl.execute(query);
	}

	public ArrayList<HistoryObject> getHistoriesByIssue(long issueId,
			int issueType) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(HistoryEnum.TABLE_NAME);
		valueSet.addEqualCondition(HistoryEnum.ISSUE_ID, issueId);
		valueSet.addEqualCondition(HistoryEnum.ISSUE_TYPE, issueType);
		valueSet.setOrderBy(HistoryEnum.CREATE_TIME, MySQLQuerySet.ASC_ORDER);
		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);
		ArrayList<HistoryObject> histories = new ArrayList<HistoryObject>();
		try {
			while (result.next()) {
				histories.add(convert(result));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		return histories;
	}
	
	public boolean deleteByIssue(long issueId, int issueType) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(HistoryEnum.TABLE_NAME);
		valueSet.addEqualCondition(HistoryEnum.ISSUE_ID, issueId);
		valueSet.addEqualCondition(HistoryEnum.ISSUE_TYPE, issueType);
		String query = valueSet.getDeleteQuery();
		return mControl.execute(query);
	}

	private HistoryObject convert(ResultSet result) throws SQLException {
		HistoryObject history = new HistoryObject();
		history.setId(result.getLong(HistoryEnum.ID))
				.setIssueId(result.getLong(HistoryEnum.ISSUE_ID))
				.setIssueType(result.getInt(HistoryEnum.ISSUE_TYPE))
				.setHistoryType(result.getInt(HistoryEnum.HISTORY_TYPE))
				.setOldValue(result.getString(HistoryEnum.OLD_VALUE))
				.setNewValue(result.getString(HistoryEnum.NEW_VALUE))
				.setCreateTime(result.getLong(HistoryEnum.CREATE_TIME));
		return history;
	}
}
