/*
 * Copyright (C) 2005 Chin-Yun Hsieh <hsieh@csie.ntut.edu.tw>
 * Yu Chin Cheng <yccheng@csie.ntut.edu.tw>
 * Chien-Tsun Chen <ctchen@ctchen.idv.tw>
 * Tsui-Chen She <kay_sher@hotmail.com>
 * Chia-Hao Wu<chwu2004@gmail.com>
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

// Created by MyEclipse Struts
// XSL source (default): platform:/plugin/com.genuitec.eclipse.cross.easystruts.eclipse_3.9.210/xslt/JavaClass.xsl
package ntut.csie.ezScrum.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.internal.ProjectInfoCenter;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.Person;
import ntut.csie.ezScrum.web.dataObject.User;
import ntut.csie.ezScrum.web.dataObject.ezScrumAdmin;
import ntut.csie.ezScrum.web.form.LogonForm;
import ntut.csie.ezScrum.web.logic.ProjectLogic;
import ntut.csie.ezScrum.web.support.AccessPermissionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.ethz.ssh2.crypto.Base64;

/**
 * MyEclipse Struts
 * Creation date: 09-08-2005
 * 
 * XDoclet definition:
 * 
 * @struts:action validate="true"
 */
public class LogonSubmitAction extends Action {
	private static Log log = LogFactory.getLog(LogonSubmitAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		LogonForm logonForm = (LogonForm) form;

		// 取得登入的使用者帳號密碼
		String userId = logonForm.getUserId();
		String password = logonForm.getPassword();

		log.debug("LogonForm.UserID=" + userId);

		// 建立User Session
		IUserSession userSession = ProjectInfoCenter.getInstance().login(userId, password);

		String encodedPassword = new String(Base64.encode(password.getBytes()));

		// 設定權限資訊
		AccessPermissionManager.setupPermission(request, userSession);

		// 設定User Session
		request.getSession().setAttribute("UserSession", userSession);

		// 為了要讓插件中可以使用session的中使用者的密碼，所以將原本利用MD5加密的密碼轉換成利用Base64加密。如此加密的密碼才可逆
		request.getSession().setAttribute("passwordForPlugin", encodedPassword);

		ProjectLogic projectLogic = new ProjectLogic();
		projectLogic.cloneDefaultFile();

		Person person = this.getPerson(userSession.getAccount());
		
		return mapping.findForward(person.getForwardName());
	}

	private Person getPerson(AccountObject account) {
		if (account.getRoles().get("system") != null) {
			return new ezScrumAdmin();
		} else {
			return new User();
		}
	}
}
