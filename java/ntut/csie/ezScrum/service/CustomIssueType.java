package ntut.csie.ezScrum.service;

public class CustomIssueType{
	private long typeId;
	private String typeName;
	private boolean isPublic;
	private boolean isKanban;
	
	public CustomIssueType(){
	}
	/* 回傳type在資料庫的id */
	public long getTypeId() {
		return typeId;
	}
	/* 設定type在資料庫的id */
	public void setTypeId(long typeId) {
		this.typeId = typeId;
	}
	/* 回傳type的名稱 */
	public String getTypeName() {
		return typeName;
	}
	/* 設定type的名稱 */
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	/* 設定type是否提供外部人員使用 */
	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
	/* 設定type是否為Kanban所使用 */
	public void setKanban(boolean isKanban) {
		this.isKanban = isKanban;
	}
	/* 回傳這個type是否為公開狀態 */
	public boolean ispublic() {
		return this.isPublic;
	}
	/* 回傳這個type是否為Kanban所使用 */
	public boolean isKanban() {
		return this.isKanban;
	}
	
}
