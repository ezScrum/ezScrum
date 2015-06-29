package ntut.csie.ezScrum.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.web.dataObject.AttachFileObject;
import ntut.csie.ezScrum.web.databasEnum.AttachFileEnum;
import ntut.csie.ezScrum.web.databasEnum.IssueTypeEnum;

public class AttachFileDAO extends AbstractDAO<AttachFileObject, AttachFileObject>{

	private static AttachFileDAO sInstance = null;
	
	public static AttachFileDAO getInstance() {
		if (sInstance == null) {
			sInstance = new AttachFileDAO();
		}
		return sInstance;
	}
	
	@Override
	public long create(AttachFileObject attachFile) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(AttachFileEnum.TABLE_NAME);
		valueSet.addInsertValue(AttachFileEnum.ISSUE_ID, String.valueOf(attachFile.getIssueId()));
		valueSet.addInsertValue(AttachFileEnum.ISSUE_TYPE, String.valueOf(attachFile.getIssueType()));
		valueSet.addInsertValue(AttachFileEnum.NAME, attachFile.getName());
		valueSet.addInsertValue(AttachFileEnum.PATH, attachFile.getPath());
		valueSet.addInsertValue(AttachFileEnum.CONTENT_TYPE, attachFile.getContentType());
		valueSet.addInsertValue(AttachFileEnum.CREATE_TIME, String.valueOf(System.currentTimeMillis()));
		String query = valueSet.getInsertQuery();

		return mControl.executeInsert(query);
	}

	@Override
	public AttachFileObject get(long id) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(AttachFileEnum.TABLE_NAME);
		valueSet.addEqualCondition(AttachFileEnum.ID, id);

		String query = valueSet.getSelectQuery();
		return getSelectAttachFiles(query).get(0);
	}
	
	/**
	 * 用 story id 取得 story 底下所有的 attach file
	 * for ezScrum v1.8
	 */
	public ArrayList<AttachFileObject> getAttachFilesByStoryId(long id) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(AttachFileEnum.TABLE_NAME);
		valueSet.addEqualCondition(AttachFileEnum.ISSUE_ID, Long.toString(id));
		valueSet.addEqualCondition(AttachFileEnum.ISSUE_TYPE, Integer.toString(IssueTypeEnum.TYPE_STORY));
		valueSet.setOrderBy(AttachFileEnum.CREATE_TIME, MySQLQuerySet.ASC_ORDER);

		String query = valueSet.getSelectQuery();
		return getSelectAttachFiles(query);
	}
	
	/**
	 * 用 task id 取得 task 底下所有的 attach file
	 * for ezScrum v1.8
	 */
	public ArrayList<AttachFileObject> getAttachFilesByTaskId(long id) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(AttachFileEnum.TABLE_NAME);
		valueSet.addEqualCondition(AttachFileEnum.ISSUE_ID, Long.toString(id));
		valueSet.addEqualCondition(AttachFileEnum.ISSUE_TYPE, "'" + IssueTypeEnum.TYPE_TASK + "'");
		valueSet.setOrderBy(AttachFileEnum.CREATE_TIME, MySQLQuerySet.ASC_ORDER);

		String query = valueSet.getSelectQuery();
		return getSelectAttachFiles(query);
	}

	@Override
	public boolean update(AttachFileObject dataObject) {
		return false;
	}

	@Override
	public boolean delete(long id) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(AttachFileEnum.TABLE_NAME);
		valueSet.addEqualCondition(AttachFileEnum.ID, id);
		String query = valueSet.getDeleteQuery();
		return mControl.execute(query);
	}
	
	private ArrayList<AttachFileObject> getSelectAttachFiles(String query) {
		ArrayList<AttachFileObject> list = new ArrayList<AttachFileObject>();
		try {
			ResultSet result = mControl.executeQuery(query);
			while (result.next()) {
				AttachFileObject.Builder attachfileBuilder = new AttachFileObject.Builder();
				attachfileBuilder.setAttachFileId(result.getLong(AttachFileEnum.ID));
				attachfileBuilder.setIssueId(result.getLong(AttachFileEnum.ISSUE_ID));
				attachfileBuilder.setIssueType(result.getInt(AttachFileEnum.ISSUE_TYPE));
				attachfileBuilder.setName(result.getString(AttachFileEnum.NAME));
				attachfileBuilder.setPath(result.getString(AttachFileEnum.PATH));
				attachfileBuilder.setContentType(result.getString(AttachFileEnum.CONTENT_TYPE));
				attachfileBuilder.setCreateTime(result.getLong(AttachFileEnum.CREATE_TIME));
				list.add(attachfileBuilder.build());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

}
