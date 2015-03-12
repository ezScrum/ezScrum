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
		mControl.execute(query, true);

		String[] keys = mControl.getKeys();
		long id = Long.parseLong(keys[0]);
		return id;
    }
	
	public long addTag(String name, long projectId) {
		long newId = -1;

		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(TagEnum.TABLE_NAME);
		valueSet.addInsertValue(TagEnum.PROJECT_ID, String.valueOf(projectId));
		valueSet.addInsertValue(TagEnum.NAME, name);
		valueSet.addInsertValue(TagEnum.CREATE_TIME, String.valueOf(System.currentTimeMillis()));
		String query = valueSet.getInsertQuery();
		mControl.execute(query, true);
		
		// get the new record id
		String[] keys = mControl.getKeys();
		newId = Long.parseLong(keys[0]);
		
		return newId;
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
	public boolean update(TagObject dataObject) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(TagEnum.TABLE_NAME);
		valueSet.addEqualCondition(TagEnum.ID, dataObject.getId());
		valueSet.addInsertValue(TagEnum.NAME, dataObject.getName());
		valueSet.addInsertValue(TagEnum.CREATE_TIME, String.valueOf(dataObject.getCreateTime()));
		String query = valueSet.getUpdateQuery();

		return mControl.executeUpdate(query);
	}
	
	public boolean updateTag(long tagId, String newName, long projectId) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(TagEnum.TABLE_NAME);
		valueSet.addInsertValue(TagEnum.NAME, newName);
		valueSet.addEqualCondition(TagEnum.ID, String.valueOf(tagId));
		valueSet.addEqualCondition(TagEnum.PROJECT_ID, Long.toString(projectId));
		String query = valueSet.getUpdateQuery();
		return mControl.execute(query);
	}

	@Override
	public boolean delete(long id) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(TagEnum.TABLE_NAME);
		valueSet.addEqualCondition(TagEnum.ID, id);
		String query = valueSet.getDeleteQuery();
		return mControl.executeUpdate(query);
	}
	
	public void deleteTag(long id, long projectId) {
		// 把story中有關此tag的資訊移除
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(StoryTagRelationEnum.TABLE_NAME);
		valueSet.addEqualCondition(StoryTagRelationEnum.TAG_ID, String.valueOf(id));
		String query = valueSet.getDeleteQuery();
		mControl.execute(query);

		// 移除Tag資訊
		valueSet.clear();
		valueSet.addTableName(TagEnum.TABLE_NAME);
		valueSet.addEqualCondition(TagEnum.ID, String.valueOf(id));
		valueSet.addEqualCondition(TagEnum.PROJECT_ID, Long.toString(projectId));
		query = valueSet.getDeleteQuery();
		mControl.execute(query);
	}
	
	// 對Story設定自訂分類標籤
	public void addStoryTag(String storyId, long tagId) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(StoryTagRelationEnum.TABLE_NAME);
		valueSet.addEqualCondition(StoryTagRelationEnum.STORY_ID, storyId);
		valueSet.addEqualCondition(StoryTagRelationEnum.TAG_ID, String.valueOf(tagId));
		valueSet.addInsertValue(StoryTagRelationEnum.STORY_ID, storyId);
		valueSet.addInsertValue(StoryTagRelationEnum.TAG_ID, String.valueOf(tagId));
		String query = valueSet.getSelectQuery();

		// 如果story對tag關係若不存在則新增一筆關係
		try {
			ResultSet result = mControl.executeQuery(query);
			if (!result.next()) {
				query = valueSet.getInsertQuery();
				mControl.execute(query);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// 移除Story的自訂分類標籤
	public void removeStoryTag(String storyId, long tagId) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(StoryTagRelationEnum.TABLE_NAME);
		valueSet.addEqualCondition(StoryTagRelationEnum.STORY_ID, storyId);
		if (tagId != -1){
			valueSet.addEqualCondition(StoryTagRelationEnum.TAG_ID, String.valueOf(tagId));
		}
		String query = valueSet.getDeleteQuery();
		mControl.executeUpdate(query);
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
	public ArrayList<TagObject> getTagList(long projectId) {
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
	
	public boolean isTagExist(String name, long projectId) {
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
