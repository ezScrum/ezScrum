package ntut.csie.ezScrum.issue.internal;

public class IssueAttachFile {
//	id 	bug_id 	title 	
//	description 	diskfile 	
//	filename 	folder 	filesize 	
//	file_type 	date_added 	content
	private long attachFileId;
	private long issueID;
	private String title;
	private String description;
	private String folder;
	private String diskfile;//md5
	private String filename;
	private int filesize;
	private String fileType;
	private long date_added;
	//沒有實作content,如有需要請自行實作
	public IssueAttachFile()
	{
		attachFileId=0;
		issueID=0;
		title="";
		description="";
		folder="";
		diskfile="";
		filename="";
		filesize=0;
		fileType="";
		date_added=0;
	}
	public long getAttachFileId() {
		return attachFileId;
	}
	public void setAttachFileId(long attachFileId) {
		this.attachFileId = attachFileId;
	}
	public long getIssueID() {
		return issueID;
	}
	public void setIssueID(long issueID) {
		this.issueID = issueID;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getFolder() {
		return folder;
	}
	public void setFolder(String folder) {
		this.folder = folder;
	}
	public String getDiskfile() {
		return diskfile;
	}
	public void setDiskfile(String diskfile) {
		this.diskfile = diskfile;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public int getFilesize() {
		return filesize;
	}
	public void setFilesize(int filesize) {
		this.filesize = filesize;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public long getDate_added() {
		return date_added;
	}
	public void setDate_added(long date_added) {
		this.date_added = date_added;
	}
}
