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

//Created by MyEclipse Struts
// XSL source (default): platform:/plugin/com.genuitec.eclipse.cross.easystruts.eclipse_3.8.4/xslt/JavaClass.xsl
package ntut.csie.ezScrum.web.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.ITSInformation;
import ntut.csie.ezScrum.web.dataObject.ProjectInformation;
import ntut.csie.ezScrum.web.helper.ProjectHelper;
import ntut.csie.ezScrum.web.logic.ScrumRoleLogic;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.account.core.IAccount;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;



public class SaveProjectAction extends Action {
	private static Log log = LogFactory.getLog(SaveProjectAction.class);
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response){
		log.info("save Project!");
		//	取得使用者登入資料
		IUserSession userSession = (IUserSession) request.getSession().getAttribute("UserSession");
		IAccount account = userSession.getAccount();
		//	取得上一頁的資訊
		String fromPage = request.getParameter("from");

		//	取得所有 ITS 參數資料
		String serverURL = request.getParameter("ServerUrl");
		String serverPath = request.getParameter("ServicePath");
		String dbAccount = request.getParameter("DBAccount");
		String dbPassword = request.getParameter("DBPassword");
		String projectName = request.getParameter("Name");
		String dbType = request.getParameter("SQLType");
		String dbName = request.getParameter("DBName");

		// 取得所有專案的資訊
		String pName = request.getParameter("Name");
		String pDisplayName = request.getParameter("DisplayName");
		String pComment = request.getParameter("Comment");
		String pManager = request.getParameter("ProjectManager");
		String pAttachFileSize = request.getParameter("AttachFileSize");
		
		if (pAttachFileSize == null)	pAttachFileSize = "2";
		if (pComment == null)		pComment = "";
		if (pManager == null)		pManager = "";
		
		//	將參數物件化
		ITSInformation itsInformation = new ITSInformation(serverURL, serverPath, dbAccount, dbPassword, projectName, dbType, dbName);
		ProjectInformation projectInformation = new ProjectInformation(pName, pDisplayName, pComment, pManager, pAttachFileSize);
		
		//	透過project helper得到response text
		ProjectHelper projectHelper = new ProjectHelper();
		String saveProjectXML = projectHelper.getCreateProjectXML( request, userSession, fromPage, itsInformation, projectInformation );
		
		//	設定response 內容和型態
		try {
			response.setContentType("text/xml; charset=utf-8");
			response.getWriter().write(saveProjectXML);
			response.getWriter().close();
    	} catch (IOException e) {
    		log.debug( "SaveProjectAction.java : response occur IOException. " );
			e.printStackTrace();
		}
    	
		//	reset Project<-->ScrumRole map	
//		ScrumRoleManager scrumRoleManager = new ScrumRoleManager();
//		scrumRoleManager.setScrumRoles( account );
		(new ScrumRoleLogic()).setScrumRoles( account );
		
		//	刪除Session中關於該使用者的所有專案權限。
        SessionManager.removeScrumRolesMap(request, account);
		
		return null;
	}
}