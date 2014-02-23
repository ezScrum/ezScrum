package ntut.csie.ezScrum.web.dataObject;

public class ProjectInformation {
	// 取得所有專案的資訊
	private String id;
	private String name = "";
	private String displayName = "";
	private String comment = "";
	private String manager = "";
	private String attachFileSize = "";
	private long createDate;

	public ProjectInformation(String id, String name, String displayName, String comment, String manager, String attachFileSize, long createDate) {
		setId(id);
		setName(name);
		setDisplayName(displayName);
		setComment(comment);
		setManager(manager);
		setAttachFileSize(attachFileSize);
		setCreateDate(createDate);
	}

	public ProjectInformation(String name, String displayName, String comment, String manager, String attachFileSize) {
		setName(name);
		setDisplayName(displayName);
		setComment(comment);
		setManager(manager);
		setAttachFileSize(attachFileSize);
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

	public long getCreateDate() {
		return createDate;
	}

	public void setCreateDate(long createDate) {
		this.createDate = createDate;
	}
}
