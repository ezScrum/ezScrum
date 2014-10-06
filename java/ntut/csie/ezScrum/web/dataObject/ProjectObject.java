package ntut.csie.ezScrum.web.dataObject;

import ntut.csie.ezScrum.web.databasEnum.ProjectEnum;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class ProjectObject {
	// 取得所有專案的資訊
	private String id;
	private String name = "";
	private String displayName = "";
	private String comment = "";
	private String manager = "";
	private String attachFileSize = "";
	private String pid = "";
	private long createDate;

	public ProjectObject(String id, String name, String displayName, String comment, String manager, String attachFileSize, long createDate) {
		setId(id);
		setName(name);
		setDisplayName(displayName);
		setComment(comment);
		setManager(manager);
		setAttachFileSize(attachFileSize);
		setCreateDate(createDate);
		setPid(name);
	}

	public ProjectObject(String name, String displayName, String comment, String manager, String attachFileSize) {
		setName(name);
		setPid(name);
		setDisplayName(displayName);
		setComment(comment);
		setManager(manager);
		setAttachFileSize(attachFileSize);
	}

	public ProjectObject() {
    }

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getComment() {
		return comment;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public String getManager() {
		return manager;
	}

	public void setAttachFileSize(String attachFileSize) {
		this.attachFileSize = attachFileSize;
	}

	public String getAttachFileSize() {
		return attachFileSize;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getPid() {
		return pid;
	}
	
	public void setPid(String pid) {
		this.pid = pid;
	}

	public long getCreateDate() {
		return createDate;
	}

	public void setCreateDate(long createDate) {
		this.createDate = createDate;
	}
	
	public JSONObject toJSON() throws JSONException {
		JSONObject object = new JSONObject();
		object
			.put(ProjectEnum.NAME, name)
			.put(ProjectEnum.ID, id)
			.put(ProjectEnum.COMMENT, comment)
			.put(ProjectEnum.PID, pid)
			.put(ProjectEnum.CREATE_TIME, createDate)
			.put(ProjectEnum.PRODUCT_OWNER, manager)
			.put(ProjectEnum.ATTATCH_MAX_SIZE, attachFileSize);
		return object;
	}
}
