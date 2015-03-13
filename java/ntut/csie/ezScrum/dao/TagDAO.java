package ntut.csie.ezScrum.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.databasEnum.StoryTagRelationEnum;
import ntut.csie.ezScrum.web.databasEnum.TagEnum;

public class TagDAO extends AbstractDAO<TagObject, TagObject>{
	private static TagDAO sInstance = null;
	
	public static TagDAO getInstance(){
		if(sInstance == null){
			sInstance = new TagDAO();
		}
		return sInstance;
	}

	@Override
    public long create(TagObject tag) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(TagEnum.TABLE_NAME);
		valueSet.addInsertValue(TagEnum.NAME, tag.getName());
		valueSet.addInsertValue(TagEnum.PROJECT_ID, tag.getProjectId());
		valueSet.addInsertValue(TagEnum.CREATE_TIME, System.currentTimeMillis());
		String query = valueSet.getInsertQuery();
		long id = mControl.executeInsert(query);
		return id;
    }

	@Override
    public TagObject get(long id) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(TagEnum.TABLE_NAME);
		valueSet.addEqualCondition(TagEnum.ID, id);
		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);
		TagObject tagObject = null;
		try {
			if (result.next()) {
				tagObject = convertTag(result);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResultSet(result);
		}
		return tagObject;
    }

	@Override
	public boolean update(TagObject tag) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(TagEnum.TABLE_NAME);
		valueSet.addEqualCondition(TagEnum.ID, tag.getId());
		valueSet.addInsertValue(TagEnum.NAME, tag.getName());
		String query = valueSet.getUpdateQuery();
		return mControl.executeUpdate(query);
	}

	@Override
	public boolean delete(long id) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(TagEnum.TABLE_NAME);
		valueSet.addEqualCondition(TagEnum.ID, id);
		String query = valueSet.getDeleteQuery();
		return mControl.executeUpdate(query);
	}
	
	public TagObject getTagByName(String name, long projectId) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(TagEnum.TABLE_NAME);
		valueSet.addEqualCondition(TagEnum.PROJECT_ID, Long.toString(projectId));
		valueSet.addTextFieldEqualCondition(TagEnum.NAME, name);
		String query = valueSet.getSelectQuery();
		TagObject tag = null;
		try {
			ResultSet result = mControl.executeQuery(query);
			if (result.next()) {
				tag = new TagObject(result.getLong(TagEnum.ID), result.getString(TagEnum.NAME));
				tag.setProjectId(result.getLong(TagEnum.PROJECT_ID));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tag;
	}
	
	// 取得自訂分類標籤列表
	public ArrayList<TagObject> getTagsByProject(long projectId) {
		ArrayList<TagObject> tags = new ArrayList<TagObject>();

		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(TagEnum.TABLE_NAME);
		valueSet.addEqualCondition(TagEnum.PROJECT_ID, String.valueOf(projectId));
		String query = valueSet.getSelectQuery();

		try {
			ResultSet result = mControl.executeQuery(query);
			while (result.next()) {
				TagObject tag = new TagObject(result.getLong(TagEnum.ID), result.getString(TagEnum.NAME));
				tag.setProjectId(result.getLong(TagEnum.PROJECT_ID));
				tags.add(tag);
			}
		} catch (SQLException e) {}
		return tags;
	}
	
	public boolean exist(String name, long projectId) {
		return (getTagByName(name, projectId) != null);
	}
	
	public TagObject convertTag(ResultSet result) throws SQLException {
		long id = result.getLong(TagEnum.ID);
		String tagName = result.getString(TagEnum.NAME);
		long projectId = result.getLong(TagEnum.PROJECT_ID);
		long createTime = result.getLong(TagEnum.CREATE_TIME);
		
		TagObject tagObject = new TagObject(id, tagName);
		tagObject.setProjectId(projectId)
		         .setCreateTime(createTime);
		return tagObject;
	}
}
