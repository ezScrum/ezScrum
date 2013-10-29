package ntut.csie.ezScrum.web.dataObject;

public class ITSInformation {
	// 取得所有 ITS 參數資料
	private String serverURL = "";
	private String serverPath = "";
	private String dbAccount = "";
	private String dbPassword = "";
	private String projectName = "";
	private String dbType = "";
	private String dbName = "";
	
	public ITSInformation( String serverURL, String serverPath, String dbAccount, String dbPassword, String projectName, String dbType, String dbName ){
		this.setServerURL(serverURL);
		this.setServerPath(serverPath);
		this.setDbAccount(dbAccount);
		this.setDbPassword(dbPassword);
		this.setProjectName(projectName);
		this.setDbType(dbType);
		this.setDbName(dbName);
	}

	public void setServerURL(String serverURL) {
		this.serverURL = serverURL;
	}

	public String getServerURL() {
		return serverURL;
	}

	public void setServerPath(String serverPath) {
		this.serverPath = serverPath;
	}

	public String getServerPath() {
		return serverPath;
	}

	public void setDbAccount(String serverAcc) {
		this.dbAccount = serverAcc;
	}

	public String getDbAccount() {
		return dbAccount;
	}

	public void setDbPassword(String serverPwd) {
		this.dbPassword = serverPwd;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getDbName() {
		return dbName;
	}
}
