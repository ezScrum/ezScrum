package ntut.csie.ezScrum.issue.sql.service.internal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.tool.ISQLControl;
import ntut.csie.ezScrum.web.dataInfo.AttachFileInfo;
import ntut.csie.ezScrum.web.dataObject.AttachFileObject;
import ntut.csie.ezScrum.web.databasEnum.AttachFileEnum;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MantisAttachFileService extends AbstractMantisService {
	private static Log log = LogFactory.getLog(MantisAttachFileService.class);

	public MantisAttachFileService(ISQLControl control, Configuration config) {
		setControl(control);
		setConfig(config);
	}

	public void initAttachFile(IIssue issue) {
		ArrayList<AttachFileObject> attachFiles = new ArrayList<AttachFileObject>();
		if (issue.getCategory().toLowerCase().equals("story")) {
			attachFiles = getAttachFileByStoryId(issue.getIssueID());
		} else if(issue.getCategory().toLowerCase().equals("task")) {
			attachFiles = getAttachFileByTaskId(issue.getIssueID());
		}
		
		for (AttachFileObject file: attachFiles) {
			issue.addAttachFile(file);
		}
	}
	
	// for ezScrum 1.8
	public long addAttachFile(AttachFileInfo attachFileInfo) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(AttachFileEnum.TABLE_NAME);
		valueSet.addInsertValue(AttachFileEnum.ISSUE_ID, String.valueOf(attachFileInfo.issueId));
		valueSet.addInsertValue(AttachFileEnum.ISSUE_TYPE, String.valueOf(attachFileInfo.issueType));
		valueSet.addInsertValue(AttachFileEnum.NAME, attachFileInfo.name);
		valueSet.addInsertValue(AttachFileEnum.PATH, attachFileInfo.path);
		valueSet.addInsertValue(AttachFileEnum.CONTENT_TYPE, attachFileInfo.contentType);
		valueSet.addInsertValue(AttachFileEnum.CREATE_TIME, String.valueOf(System.currentTimeMillis()));
		String query = valueSet.getInsertQuery();
		log.info("[SQL] " + query);
		getControl().execute(query, true);
		
		String[] keys = getControl().getKeys();
		System.out.println(keys[0]);
		long newId = Long.parseLong(keys[0]);
		return newId;
	}

	// for ezScrum v1.8
	public AttachFileObject getAttachFile(long fileId) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(AttachFileEnum.TABLE_NAME);
		valueSet.addEqualCondition(AttachFileEnum.ID, Long.toString(fileId));

		String query = valueSet.getSelectQuery();
		log.info("[SQL] " + query);
		return getSelectAttachFile(query).get(0);
	}

	/**
	 * 用 story id 取得 story 底下的 attach file
	 * for ezScrum v1.8
	 */
	public ArrayList<AttachFileObject> getAttachFileByStoryId(long id) {
		//設定SQL
		IQueryValueSet valueSet = new MySQLQuerySet();

		valueSet.addTableName(AttachFileEnum.TABLE_NAME);
		valueSet.addEqualCondition(AttachFileEnum.ISSUE_ID, "'" + id + "'");
		valueSet.addEqualCondition(AttachFileEnum.ISSUE_TYPE, "'" + AttachFileObject.TYPE_STORY + "'");
		valueSet.setOrderBy(AttachFileEnum.CREATE_TIME, MySQLQuerySet.DESC_ORDER);

		//取得sql語法
		String query = valueSet.getSelectQuery();
		log.info("[SQL] " + query);
		return getSelectAttachFile(query);
	}
	
	/**
	 * 用 task id 取得 task 底下的 attach file
	 * for ezScrum v1.8
	 */
	public ArrayList<AttachFileObject> getAttachFileByTaskId(long id) {
		//設定SQL
		IQueryValueSet valueSet = new MySQLQuerySet();

		valueSet.addTableName(AttachFileEnum.TABLE_NAME);
		valueSet.addEqualCondition(AttachFileEnum.ISSUE_ID, "'" + id + "'");
		valueSet.addEqualCondition(AttachFileEnum.ISSUE_TYPE, "'" + AttachFileObject.TYPE_TASK + "'");
		valueSet.setOrderBy(AttachFileEnum.CREATE_TIME, MySQLQuerySet.DESC_ORDER);

		//取得sql語法
		String query = valueSet.getSelectQuery();
		log.info("[SQL] " + query);
		return getSelectAttachFile(query);
	}

	private ArrayList<AttachFileObject> getSelectAttachFile(String query) {
		ArrayList<AttachFileObject> list = new ArrayList<AttachFileObject>();
		try {
			ResultSet result = getControl().executeQuery(query);
			//取得file的資訊
			while (result.next()) {
				AttachFileObject.Builder attachfileBuilder = new AttachFileObject.Builder();
				attachfileBuilder.setAttachFileId(result.getLong(AttachFileEnum.ID));
				attachfileBuilder.setIssueId(result.getLong(AttachFileEnum.ISSUE_ID));
				attachfileBuilder.setIssueType(result.getInt(AttachFileEnum.ISSUE_TYPE));
				attachfileBuilder.setName(result.getString(AttachFileEnum.NAME));
				attachfileBuilder.setPath(result.getString(AttachFileEnum.PATH));
				attachfileBuilder.setCreateTime(result.getLong(AttachFileEnum.CREATE_TIME));
				list.add(attachfileBuilder.build());
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// for ezScrum v1.8
	public void deleteAttachFile(long fileId) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(AttachFileEnum.TABLE_NAME);
		valueSet.addEqualCondition(AttachFileEnum.ID, Long.toString(fileId));
		String query = valueSet.getDeleteQuery();
		log.info("[SQL] " + query);
		getControl().execute(query);
	}
}
