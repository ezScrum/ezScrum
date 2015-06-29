/*
 * Copyright (C) 2005 Chin-Yun Hsieh <hsieh@csie.ntut.edu.tw> Yu Chin Cheng <yccheng@csie.ntut.edu.tw> Chien-Tsun Chen <ctchen@ctchen.idv.tw> Tsui-Chen She <kay_sher@hotmail.com> Chia-Hao
 * Wu<chwu2004@gmail.com>
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307,
 * USA.
 */

// Created by MyEclipse Struts
// XSL source (default): platform:/plugin/com.genuitec.eclipse.cross.easystruts.eclipse_3.8.4/xslt/JavaClass.xsl
package ntut.csie.ezScrum.web.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataInfo.ProjectInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.helper.ProjectHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class SaveProjectAction extends Action {
	private static Log log = LogFactory.getLog(SaveProjectAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	        HttpServletResponse response) {
		log.info("save Project!");
		//	取得使用者登入資料
		IUserSession userSession = (IUserSession) request.getSession().getAttribute("UserSession");
		AccountObject account = userSession.getAccount();
		//	取得上一頁的資訊
		String fromPage = request.getParameter("from");

		// 取得所有專案的資訊
		String name = request.getParameter("Name");
		String displayName = request.getParameter("DisplayName");
		String comment = request.getParameter("Comment");
		String manager = request.getParameter("ProjectManager");
		String attachFileSizeString = request.getParameter("AttachFileSize");
		long attachFileSize;
		try {
			attachFileSize = Long.parseLong(attachFileSizeString);
		} catch (Exception e) {
			attachFileSize = 0;
		}
		if (attachFileSize == 0) attachFileSize = 2;
		if (comment == null) comment = "";
		if (manager == null) manager = "";

		// 將參數物件化
		ProjectInfo projectInfo = new ProjectInfo();
		projectInfo.name = name;
		projectInfo.displayName = displayName;
		projectInfo.common = comment;
		projectInfo.manager = manager;
		projectInfo.attachFileSize = attachFileSize;
		
		// 透過 project helper 得到 response text
		ProjectHelper projectHelper = new ProjectHelper();
		String saveProjectXML = projectHelper.getCreateProjectXML(request,
				userSession, fromPage, projectInfo);

		// 設定 response 內容和型態
		try {
			response.setContentType("text/xml; charset=utf-8");
			response.getWriter().write(saveProjectXML);
			response.getWriter().close();
		} catch (IOException e) {
			log.debug("SaveProjectAction.java : response occur IOException. ");
			e.printStackTrace();
		}

		// 刪除 Session 中關於該使用者的所有專案權限。
		SessionManager.removeScrumRolesMap(request, account);

		return null;
	}
}