package ntut.csie.ezScrum.issue.sql.service.internal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.IIssueTag;
import ntut.csie.ezScrum.issue.internal.IssueTag;
import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.core.ITSPrefsStorage;
import ntut.csie.ezScrum.issue.sql.service.tool.ISQLControl;

public class MantisTagService extends AbstractMantisService {
	
	public MantisTagService(ISQLControl control, ITSPrefsStorage prefs) {
		setControl(control);
		setPrefs(prefs);
	}
	
	public void initTag(IIssue issue) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("ezscrum_tag_relation");
		valueSet.addLeftJoin("ezscrum_tag_table", "ezscrum_tag_relation.tag_id", "ezscrum_tag_table.id");
		valueSet.addLeftJoin("mantis_bug_table", "ezscrum_tag_relation.story_id", "mantis_bug_table.id");
		valueSet.addEqualCondition("story_id", Long.toString(issue.getIssueID()));
		valueSet.addEqualCondition("ezscrum_tag_table.project_id", "mantis_bug_table.project_id");
		String query = valueSet.getSelectQuery();
		ArrayList<IIssueTag> tags = new ArrayList<IIssueTag>();

		try {
			ResultSet result = getControl().executeQuery(query);
			while (result.next()) {
				IIssueTag tag = new IssueTag();
				tag.setTagId(result.getLong("ezscrum_tag_table.id"));
				tag.setTagName(result.getString("ezscrum_tag_table.name"));
				
				tags.add(tag);
			}
			issue.setTag(tags);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateTag(String tagId, String tagName, String projectName) {
		int projectID = getProjectID(projectName);
		IQueryValueSet valueSet = new MySQLQuerySet();

		// 移除原本tag資訊
		valueSet.addTableName("ezscrum_tag_table");
		valueSet.addEqualCondition("id", tagId);
		valueSet.addEqualCondition("project_id", Long.toString(projectID));
		String query = valueSet.getDeleteQuery();
		getControl().execute(query);

		// 新增修改後的tag資訊
		String newId = addNewTag(tagName, projectName);

		// 更新tag relation table
		// 找出在此專案中的story並將tagId更新
		/*-----------------------------------------------------------
		 *UPDATE `ezscrum_tag_relation` 
		 *set tag_id = 2 where tag_id = 1 
		 *and story_id = (select id from mantis_bug_table where project_id = 1)
		-------------------------------------------------------------*/
		query = "UPDATE `ezscrum_tag_relation` SET tag_id = "
				+ newId
				+ " WHERE tag_id = "
				+ tagId
				+ " and story_id = "
				+ "(select id from mantis_bug_table where project_id = "+projectID+")";
		getControl().execute(query);

	}
	
	// 新增自訂分類標籤
	// 新增完回傳新增後的tag id
	public String addNewTag(String name, String projectName) {
		String newId = "";
		int projectID = getProjectID(projectName);
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("ezscrum_tag_table");
		valueSet.addInsertValue("project_id", Long.toString(projectID));
		valueSet.addInsertValue("name", name);

		// 判斷是否有已存在相同名稱但不同專案之tag
		valueSet.addEqualCondition("name", "'" + name + "'");
		String query = valueSet.getSelectQuery();
		
		try {
			ResultSet result = getControl().executeQuery(query);
			if (result.next()) {
				// 有相同名稱存在則指定id
				valueSet.addInsertValue("id", result.getString("ezscrum_tag_table.id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		query = valueSet.getInsertQuery();
		getControl().execute(query);

		// 取得新增後的tag id
		valueSet.clear();
		valueSet.addTableName("ezscrum_tag_table");
		valueSet.addEqualCondition("project_id", Long.toString(projectID));
		valueSet.addEqualCondition("name", "'" + name + "'");
		query = valueSet.getSelectQuery();

		try {
			ResultSet result = getControl().executeQuery(query);
			if (result.next()) {
				// 有相同名稱存在則指定id
				newId = result.getString("id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return newId;
	}
	
	// 刪除自訂分類標籤
	public void deleteTag(String id, String projectName) {
		int projectID = getProjectID(projectName);
		// 把story中有關此tag的資訊移除
		// fix at 2010.12.01，修改語法使其通過 LEFT JOIN 指令
		String query = "delete Tag_R from `ezscrum_tag_relation` " +
			"AS Tag_R LEFT JOIN `mantis_bug_table` AS Bug_T on Tag_R.story_id=Bug_T.id WHERE Bug_T.project_id="	+ projectID + " and Tag_R.tag_id=" + id;
		getControl().execute(query);

		// 移除Tag資訊
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("ezscrum_tag_table");
		valueSet.addEqualCondition("id", id);
		valueSet.addEqualCondition("project_id", Long.toString(projectID));
		query = valueSet.getDeleteQuery();
		getControl().execute(query);

	}

	public IIssueTag getTagByName(String name, String projectName) {
		int projectID = getProjectID(projectName);
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("ezscrum_tag_table");
		valueSet.addEqualCondition("project_id", Long.toString(projectID));
		valueSet.addEqualCondition("name", "'" + name + "'");
		String query = valueSet.getSelectQuery();
		IIssueTag tag = null;
		try {
			ResultSet result = getControl().executeQuery(query);
			if (result.next()) {
				tag = new IssueTag();
				tag.setTagId(result.getLong("id"));
				tag.setTagName(result.getString("name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return tag;
	}
	
	// 取得自訂分類標籤列表
	public IIssueTag[] getTagList(String projectName) {
		int projectID = getProjectID(projectName);
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("ezscrum_tag_table");
		valueSet.addEqualCondition("project_id", Long.toString(projectID));
		String query = valueSet.getSelectQuery();
		ArrayList<IIssueTag> tags = new ArrayList<IIssueTag>();

		try {
			ResultSet result = getControl().executeQuery(query);
			while (result.next()) {
				IIssueTag tag = new IssueTag();
				tag.setTagId(result.getLong("id"));
				tag.setTagName(result.getString("name"));

				tags.add(tag);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return tags.toArray(new IIssueTag[tags.size()]);
	}

	public boolean isTagExist(String name, String projectName) {
		return (getTagByName(name, projectName) != null);
	}
	
	// 對Story設定自訂分類標籤
	public void addStoryTag(String storyID, String tagID) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("ezscrum_tag_relation");
		valueSet.addEqualCondition("story_id", storyID);
		valueSet.addEqualCondition("tag_id", tagID);
		valueSet.addInsertValue("story_id", storyID);
		valueSet.addInsertValue("tag_id", tagID);
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
	public void removeStoryTag(String storyID, String tagID) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("ezscrum_tag_relation");
		valueSet.addEqualCondition("story_id", storyID);
		if (!tagID.equals("-1"))
			valueSet.addEqualCondition("tag_id", tagID);
		String query = valueSet.getDeleteQuery();
		getControl().execute(query);
	}
}
