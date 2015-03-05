package ntut.csie.ezScrum.service;

import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.internal.AbstractMantisService;
import ntut.csie.ezScrum.issue.sql.service.internal.MantisAttachFileService;
import ntut.csie.ezScrum.issue.sql.service.tool.ISQLControl;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.HSQLControl;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.jcis.resource.core.internal.Workspace;

public class EzTrackService extends AbstractMantisService {
	//private static Log log = LogFactory.getLog(MantisService.class);

	//final private String m_id = "Mantis";
	final private String PORT_SERVICE_MYSQL = "3306";
	final public static String ROOT_TAG = "root";

	private String MANTIS_TABLE_TYPE ;
	private String MANTIS_TABLE_NAME ;
	
	private EzTrackIssueService m_issueService;
	private MantisAttachFileService m_attachFileService;
	
	public EzTrackService(Configuration config) {
		setConfig(config);

		// =========set database type (ex: Mysql, Hsql...etc)=========
		MANTIS_TABLE_TYPE = getConfig().getDBType();
		if (MANTIS_TABLE_TYPE == null || MANTIS_TABLE_TYPE.equals(""))
			MANTIS_TABLE_TYPE = "Default";

		// =========set database name ================================
		MANTIS_TABLE_NAME = getConfig().getDBName();
		if (MANTIS_TABLE_NAME == null || MANTIS_TABLE_NAME.equals(""))
			MANTIS_TABLE_NAME = "bugtracker";

		// =========設定要使用的SQLControl============
		ISQLControl control = null;
		if (MANTIS_TABLE_TYPE.equalsIgnoreCase("MySQL")) {
			control = new MySQLControl(config.getServerUrl(),
					PORT_SERVICE_MYSQL, MANTIS_TABLE_NAME);

		} else {
			/*-----------------------------------------------------------
			 *	如果是要使用Default SQL的設定，
			 *	那麼ServerUrl的路徑要指到Workspace裡面
			 *	所以要先取得Workspace的路徑
			-------------------------------------------------------------*/
			// 因為Default DB的檔案名稱預設就是ProjectName
			String projectName = config.getProjectName();

			// 如果是Default SQL的話，那麼DB路徑就會被設定為Project底下的資料夾+Project檔案名稱
			// ex. WorkspacePath/ProjectName/ProjectName
			String DBRootPath = new Workspace().getRoot().getProject(
					projectName).getFullPath().append(projectName)
					.getPathString();

			// 然後剩下的路徑啥就不會管他了
			// ex. ProjectName.h2.db , 所以MANTIS_TABLE_NAME會被完全忽略 ....
			control = new HSQLControl(DBRootPath, PORT_SERVICE_MYSQL,
					projectName);
		}
		control.setUser(config.getDBAccount());
		control.setPassword(config.getDBPassword());
		setControl(control);
	}
	
	/**
	 * 利用透過MantisConnect及直接access資料庫的方式來實作 因此提供的pm帳號必需要能在Mantis及MySQL上使用
	 */
	public void openConnect() {
		getControl().connect();
		m_issueService = new EzTrackIssueService(getControl(), getConfig());
		m_attachFileService =  new MantisAttachFileService(getControl(), getConfig());
	}
	
	/**
	 * 關閉連線,主要是關閉SQL的連線 在使用完成一定要關閉連線
	 */
	public void closeConnect() {
		try{
			getControl().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 依照 Issue ID 取出 Issue
	 */
	public IIssue getIssue(long issueID) {
		IIssue issue = m_issueService.getIssue(issueID);
		//設定attach file的資訊
		m_attachFileService.initAttachFile(issue);
		return issue;
	}
	
	/**
	 * 依照 project ID 取出所有的Custom Issue Type
	 */
	public List<CustomIssueType> getCustomIssueType(String projectName) {
		List<CustomIssueType> list = m_issueService.getCustomIssueType(projectName);
		return list;
	}
	
	/* 新增一筆 issue type*/
	public CustomIssueType addIssueType(String projectName, String typeName, boolean isPublic) {
		return m_issueService.addIssueType(projectName, typeName, isPublic);
	}
	
	
	
}
