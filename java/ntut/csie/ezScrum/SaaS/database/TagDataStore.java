package ntut.csie.ezScrum.SaaS.database;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;


import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class TagDataStore {
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	@Persistent
	private String tagId;
	@Persistent
	private String tagName;
	
	/**
	 * [待改善]:
	 * 說明Tag, Story, Project
	 * 原始想法:	Project包含多個 TagDataStore, Story包含多個 TagDataStore
	 * 出現問題:
	 * 		Detected attempt to establish ProjectDataStore("p1")/StoryDataStore("1") 
	 * 	as the parent of ProjectDataStore("p1")/TagDataStore("3") 
	 * 	but the entity identified by ProjectDataStore("p1")/TagDataStore("3") is already a child of ProjectDataStore("p1").
	 * 		A parent cannot be established or changed once an object has been persisted.
	 * 目前作法:	Project包含多個 TagDataStore, Story包含多個 TagName，
	 * 			因此在 AddTagToStory, RemoveTagFromStory, deleteTag 這三個功能時，
	 * 			必須以 query 的方式去取得該 Tag 的 TagDdataStore 並使用Loop，以利於刪除和更新Story資訊。
	 */
	
	public TagDataStore(Key key) {
		this.key = key;
	}
	
	public Key getKey() {
		return key;
	}
	
    public void setKey(Key key) {
        this.key = key;
    }
	
	public String getTagId() {
		return tagId;
	}
	public void setTagId(String tagId) {
		this.tagId = tagId;
	}
	public String getTagName() {
		return tagName;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
}
