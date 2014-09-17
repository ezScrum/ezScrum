/*
 * Copyright (C) 2005 Chin-Yun Hsieh <hsieh@csie.ntut.edu.tw>
 *                    Yu Chin Cheng <yccheng@csie.ntut.edu.tw>
 *                    Chien-Tsun Chen <ctchen@ctchen.idv.tw>
 *                    Tsui-Chen She <kay_sher@hotmail.com>
 *                    Chia-Hao Wu<chwu2004@gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

/*
 * Created on 2005/7/25
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package ntut.csie.ezScrum.web;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import ntut.csie.ezScrum.web.logic.ProjectLogic;
import ntut.csie.jcis.core.ISystemPropertyEnum;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.config.ModuleConfig;

/**
 * @author
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class InitialPlugIn implements org.apache.struts.action.PlugIn {
	private static Log log = LogFactory.getLog(InitialPlugIn.class);
	private InitialConfigManager configManager;


	/**
	 * 
	 */
	public InitialPlugIn() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.struts.action.PlugIn#destroy()
	 */
	public void destroy() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.struts.action.PlugIn#init(org.apache.struts.action.ActionServlet,
	 *      org.apache.struts.config.ModuleConfig)
	 */
	public void init(ActionServlet servlet, ModuleConfig config)
			throws ServletException {
		log.info("*************Plugin Initail***********");

		// 取得Server的路徑
		ServletContext application = servlet.getServletContext();
		String WebRootPath = application.getRealPath("/");
		
		//為了確認路徑正確，所以要檢查WebRootPath的結尾是否有/
		// 修改原先版本為下列，預防 linux 上可能會發生路徑問題
		// 因為原先方式的會產生 xxxxx\ezScrum\/RoleBase.xml 此兩條線的怪異路徑
//		if((!WebRootPath.endsWith("/"))||(!WebRootPath.endsWith("\\")))
//		{
//			WebRootPath = WebRootPath.concat("/");
//		}
		if ( ! WebRootPath.endsWith(File.separator) ) {
			WebRootPath = WebRootPath.concat(File.separator);
		}
		
		
		this.configManager = new InitialConfigManager(WebRootPath);
		// Start Set Properties
		System.setProperty(ISystemPropertyEnum.JOB_SCHEDULER, configManager
				.getConfig(ISystemPropertyEnum.JOB_SCHEDULER));
		
		System.setProperty(ISystemPropertyEnum.APP_PATH, WebRootPath);

		/*-----------------------------------------------------------
		*	如果Workspace Path有被設置過了就不再設置
		-------------------------------------------------------------*/
		String WebRootPath_Workspace = System.getProperty(ISystemPropertyEnum.WORKSPACE_PATH);
		if(WebRootPath_Workspace == null)
		{
			System.setProperty(ISystemPropertyEnum.WORKSPACE_PATH, WebRootPath
					+ "Workspace");
		}
		System.setProperty(ISystemPropertyEnum.LIB_PATH, WebRootPath
				+ "WEB-INF" + File.separator + "lib");
		
		
		//jake, mantis task 616, jcis 632
		System.setProperty(ISystemPropertyEnum.SNAPSHOT_NAME, "MySnapshot");		
		System.setProperty(ISystemPropertyEnum.SNAPSHOT_PATH, WebRootPath
				+ System.getProperty(ISystemPropertyEnum.SNAPSHOT_NAME));
		
		////jake, mantis task 616, jcis 632 end
		System.setProperty(ISystemPropertyEnum.INTEGRATOR_REPORT_XSLPATH,
				WebRootPath + "IntegrationReport.xsl");

		System.setProperty(ISystemPropertyEnum.BUILDER_REPORT_XSLPATH,
				WebRootPath + "xsl");

		System.setProperty(ISystemPropertyEnum.ACCOUT_MANAGER,
				"ntut.csie.jcis.account.core.internal.XMLAccountManager");
		System.setProperty(ISystemPropertyEnum.ACCOUT_MANAGER_PATH, WebRootPath
				+ "RoleBase.xml");

		System.setProperty(ISystemPropertyEnum.WEB_APP_NAME, "ezScrum");
		System.setProperty(ISystemPropertyEnum.REMOTE_MACHINE_LINUX,
				"127.0.0.1");
		System.setProperty(ISystemPropertyEnum.FULL_WEB_APP_NAME,
				"http://localhost:8080/JCIS/");

		// 自動取得web server的ip address
		try {
			System.setProperty(ISystemPropertyEnum.WEB_ROOT_ADDRESS, "http://"
					+ InetAddress.getLocalHost().getHostAddress()
					+ ":8080/ezScrum/");
		} catch (UnknownHostException e) {
			e.printStackTrace();
			String ip = this.configManager
					.getConfig(ISystemPropertyEnum.WEB_ROOT_ADDRESS);
			System.setProperty(ISystemPropertyEnum.WEB_ROOT_ADDRESS, "http://"
					+ ip + ":8080/ezScrum/");
		}

		System.setProperty(ISystemPropertyEnum.RSS_PATH, WebRootPath
				+ File.separator + "Pages" + File.separator + "RSS");
		
		
		// ezScrum system version information
		this.configManager.ezScrumConfigLoad();
		System.setProperty("System_Version",this.configManager.getConfig("System_Version"));
		log.info("ezScrum Version: " + System.getProperty("System_Version"));
		
		// update ezScrum server url
		System.setProperty("System_UpdateURL", this.configManager.getConfig("ezScrum.update.server.url"));
		log.info("ezScrum Update Server: " + System.getProperty("System_UpdateURL"));
		
		// update ezScrum workspace path
		System.setProperty("System_UpdateWorkspace", System.getProperty("user.home") + File.separator + ".ezScrum" + File.separator + "updates");
		log.info("ezScrum Update Download Path: " + System.getProperty("System_UpdateWorkspace"));
		
		// 設定ezScrum啟動時間
		System.setProperty("System_Start_Time",(new Date()).toString());
		log.info("ezScrum Start Time: " + System.getProperty("System_Start_Time"));
		
		// 設定基本檔案複製動作
		ProjectLogic projectLogic = new ProjectLogic();
//		projectLogic.cloneDefaultFile(); ezSCrum v1.8不需要
		
		// =============================================
		// 下面的檢查是為了讓 v1.2 可以相容而所做的檢查
		
		// 檢查專案的角色是否都正確建立於 RoleBase
//		projectLogic.check_default_role(); ezSCrum v1.8不需要
		
		// 檢查各個專案的 ScrumRole 是否有 guest 角色
//		projectLogic.check_role_guest(); ezSCrum v1.8不需要
		// =============================================
		
		/*
		 * ezScrum system information
		 * 由於中文編碼問題，所以不採用此方法
		String systemTeam = this.configManager.getConfig("System_Team");
		String contactInformation = this.configManager.getConfig("contact_information");
		try {
			systemTeam = new String(systemTeam.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		try {
			contactInformation = new String(contactInformation.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.setProperty("System_Team",systemTeam);
		System.setProperty("contact_information",contactInformation);
		*/
		
//		log.info("ntut.csie.jcis.ApplicationRoot=\t"
//				+ System.getProperty("ntut.csie.jcis.ApplicationRoot"));
//		log.info("ntut.csie.jcis.resource.WorkspaceRoot=\t"
//				+ System.getProperty("ntut.csie.jcis.resource.WorkspaceRoot"));
//		log.info("ntut.csie.jcis.LibPath=\t"
//				+ System.getProperty("ntut.csie.jcis.LibPath"));
//		log.info("ntut.csie.jcis.SnapshotName=\t"
//				+ System.getProperty("ntut.csie.jcis.SnapshotName"));
//		log.info("ntut.csie.jcis.SnapshotPath=\t"
//				+ System.getProperty("ntut.csie.jcis.SnapshotPath"));
//		log
//				.info("ntut.csie.jcis.integrationReport.xslPath=\t"
//						+ System
//								.getProperty("ntut.csie.jcis.integrationReport.xslPath"));
//		log.info("ntut.csie.jcis.builderReport.xslPath=\t"
//				+ System.getProperty("ntut.csie.jcis.builderReport.xslPath"));
//		log.info("ntut.csie.jcis.WebAppName=\t"
//				+ System.getProperty("ntut.csie.jcis.WebAppName"));
//		log.info("ntut.csie.jcis.accountManager=\t"
//				+ System.getProperty("ntut.csie.jcis.accountManager"));
//		log.info("ntut.csie.jcis.accountManager.path=\t"
//				+ System.getProperty("ntut.csie.jcis.accountManager.path"));
//
//		System.setProperty(ISystemPropertyEnum.BACKUP_PATH, WebRootPath
//				+ "Backup");
//		log.info("ntut.csie.jcis.UseVirtualEnvironment=\t"
//				+ System.getProperty("ntut.csie.jcis.UseVirtualEnvironment"));
//		log.info("ntut.csie.jcis.VMMSIPAddress=\t" 
//				+ System.getProperty("ntut.csie.jcis.VMMSIPAddress"));
//		log.info("ntut.csie.jcis.temp.workspaceRoot=\t" 
//				+ System.getProperty("ntut.csie.jcis.temp.workspaceRoot"));
//		log.info("ntut.csie.jcis.tempDownloadURL=\t" 
//				+ System.getProperty("ntut.csie.jcis.tempDownloadURL"));
//		log.info("ntut.csie.jcis.UploadPath=\t" 
//				+ System.getProperty("ntut.csie.jcis.UploadPath"));
//		log.info("ntut.csie.jcis.UploadUrl=\t" 
//				+ System.getProperty("ntut.csie.jcis.UploadUrl"));
//		log.info("ntut.csie.jcis.SourceServerPath=\t" 
//				+ System.getProperty("ntut.csie.jcis.SourceServerPath"));
//		log.info("ntut.csie.jcis.SourceServerUrl=\t" 
//				+ System.getProperty("ntut.csie.jcis.SourceServerUrl"));
//		log.info("Create PIC");
//		ProjectInfoCenter.getInstance();
		log.info("*************Plugin Initail END***********");
//		log.info("*************Start Patch***********");
//		Patcher patcher = new Patcher(WebRootPath+"Patch", WebRootPath+"..", WebRootPath+"Backup");
//		patcher.patchAll();
//		log.info("*************Start End***********");
	}
}
