package ntut.csie.ezScrum.issue.sql.service.internal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.tool.ISQLControl;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.databasEnum.StoryTagRelationEnum;
import ntut.csie.ezScrum.web.databasEnum.TagEnum;

public class MantisTagService extends AbstractMantisService {

	public MantisTagService(ISQLControl control, Configuration config) {
		setControl(control);
		setConfig(config);
	}

	public void initTag(IIssue issue) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(TagEnum.TABLE_NAME);
		valueSet.addLeftJoin(StoryTagRelationEnum.TABLE_NAME,
				TagEnum.TABLE_NAME + "." + TagEnum.ID,
				StoryTagRelationEnum.TABLE_NAME + "."
						+ StoryTagRelationEnum.TAG_ID);
		valueSet.addEqualCondition(StoryTagRelationEnum.STORY_ID, String.valueOf(issue.getIssueID()));

		String query = valueSet.getSelectQuery();
		ArrayList<TagObject> tags = new ArrayList<TagObject>();
		System.out.println(query);
		try {
			ResultSet result = getControl().executeQuery(query);
			while (result.next()) {
				TagObject tag = new TagObject();
				tag.setId(result.getLong(TagEnum.ID));
				tag.setName(result.getString(TagEnum.NAME));

				tags.add(tag);
			}
			issue.setTag(tags);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// for ezScrum 1.8
	public void updateTag(long tagId, String tagName, String projectName) {
		long projectID = getProjectId(projectName);
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(TagEnum.TABLE_NAME);
		valueSet.addInsertValue(TagEnum.NAME, tagName);
		valueSet.addEqualCondition(TagEnum.ID, String.valueOf(tagId));
		valueSet.addEqualCondition(TagEnum.PROJECT_ID, Long.toString(projectID));
		String query = valueSet.getUpdateQuery();
		getControl().execute(query);
	}

	// 新增自訂分類標籤
	// 新增完回傳新增後的tag id
	// for ezScrum 1.8
	public long addNewTag(String name, String projectName) {
		long newId = -1;
		int projectId = getProjectId(projectName);

		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(TagEnum.TABLE_NAME);
		valueSet.addInsertValue(TagEnum.PROJECT_ID, String.valueOf(projectId));
		valueSet.addInsertValue(TagEnum.NAME, name);
		valueSet.addInsertValue(TagEnum.CREATE_TIME,
				String.valueOf(System.currentTimeMillis()));
		String query = valueSet.getInsertQuery();

		getControl().execute(query);

		valueSet.clear();
		valueSet.addTableName(TagEnum.TABLE_NAME);
		valueSet.addEqualCondition(TagEnum.PROJECT_ID,
				String.valueOf(projectId));
		valueSet.addTextFieldEqualCondition(TagEnum.NAME, name);
		valueSet.setOrderBy(TagEnum.CREATE_TIME, MySQLQuerySet.DESC_ORDER);
		query = valueSet.getSelectQuery();

		ResultSet result = getControl().executeQuery(query);
		try {
			if (result.next()) {
				newId = result.getLong(TagEnum.ID);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return newId;
	}

	// 刪除自訂分類標籤
	// for ezScrum 1.8
	public void deleteTag(long id, String projectName) {
		long projectId = getProjectId(projectName);
		// 把story中有關此tag的資訊移除
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(StoryTagRelationEnum.TABLE_NAME);
		valueSet.addEqualCondition(StoryTagRelationEnum.TAG_ID,
				String.valueOf(id));
		String query = valueSet.getDeleteQuery();
		System.out.println(query);
		getControl().execute(query);

		// 移除Tag資訊
		valueSet.clear();
		valueSet.addTableName(TagEnum.TABLE_NAME);
		valueSet.addEqualCondition(TagEnum.ID, String.valueOf(id));
		valueSet.addEqualCondition(TagEnum.PROJECT_ID, Long.toString(projectId));
		query = valueSet.getDeleteQuery();
		System.out.println(query);
		getControl().execute(query);
	}

	// for ezScrum 1.8
	public TagObject getTagByName(String name, String projectName) {
		int projectID = getProjectID(projectName);
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(TagEnum.TABLE_NAME);
		valueSet.addEqualCondition(TagEnum.PROJECT_ID, Long.toString(projectID));
		valueSet.addTextFieldEqualCondition(TagEnum.NAME, name);
		String query = valueSet.getSelectQuery();
		TagObject tag = null;
		try {
			ResultSet result = getControl().executeQuery(query);
			if (result.next()) {
				tag = new TagObject();
				tag.setId(result.getLong(TagEnum.ID));
				tag.setName(result.getString(TagEnum.NAME));
				tag.setProjectId(result.getLong(TagEnum.PROJECT_ID));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return tag;
	}

	// 取得自訂分類標籤列表
	// for ezScrum 1.8
	public ArrayList<TagObject> getTagList(String projectName) {
		ArrayList<TagObject> tags = new ArrayList<TagObject>();
		int projectId = getProjectID(projectName);

		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(TagEnum.TABLE_NAME);
		valueSet.addEqualCondition(TagEnum.PROJECT_ID,
				String.valueOf(projectId));
		String query = valueSet.getSelectQuery();

		try {
			ResultSet result = getControl().executeQuery(query);
			while (result.next()) {
				TagObject tag = new TagObject();
				tag.setId(result.getLong(TagEnum.ID));
				tag.setName(result.getString(TagEnum.NAME));
				tag.setProjectId(result.getLong(TagEnum.PROJECT_ID));

				tags.add(tag);
			}
		} catch (SQLException e) {
		}

		return tags;
	}

	// for ezScrum 1.8
	public boolean isTagExist(String name, String projectName) {
		return (getTagByName(name, projectName) != null);
	}

	// 對Story設定自訂分類標籤
	// for ezScrum 1.8
	public void addStoryTag(String storyID, long tagID) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(StoryTagRelationEnum.TABLE_NAME);
		valueSet.addEqualCondition(StoryTagRelationEnum.STORY_ID, storyID);
		valueSet.addEqualCondition(StoryTagRelationEnum.TAG_ID,
				String.valueOf(tagID));
		valueSet.addInsertValue(StoryTagRelationEnum.STORY_ID, storyID);
		valueSet.addInsertValue(StoryTagRelationEnum.TAG_ID,
				String.valueOf(tagID));
		String query = valueSet.getSelectQuery();

		// 如果story對tag關係若不存在則新增一筆關係
		try {
			ResultSet result = getControl().executeQuery(query);
			if (!result.next()) {
				query = valueSet.getInsertQuery();
				getControl().execute(query);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 移除Story的自訂分類標籤
	// for ezScrum 1.8
	public void removeStoryTag(String storyID, long tagID) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(StoryTagRelationEnum.TABLE_NAME);
		valueSet.addEqualCondition(StoryTagRelationEnum.STORY_ID, storyID);
		if (tagID != -1)
			valueSet.addEqualCondition(StoryTagRelationEnum.TAG_ID,
					String.valueOf(tagID));
		String query = valueSet.getDeleteQuery();
		getControl().execute(query);
	}
}
