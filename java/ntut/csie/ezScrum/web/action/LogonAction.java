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
// XSL source (default): platform:/plugin/com.genuitec.eclipse.cross.easystruts.eclipse_3.9.210/xslt/JavaClass.xsl
package ntut.csie.ezScrum.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ntut.csie.ezScrum.pic.core.IUserSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;


/**
 * MyEclipse Struts
 * Creation date: 08-10-2005
 *
 * XDoclet definition:
 * @struts:action validate="true"
 */
public class LogonAction extends Action {
    // --------------------------------------------------------- Instance Variables

    // --------------------------------------------------------- Methods

    /**
     * Method execute
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	//	判斷是否已有使用者登入，如果以有登入，會導頁至該帳號的專案列表
    	if( request.getSession().getAttribute("UserSession") != null ){
    		IUserSession userSessionForIsExisted = (IUserSession) request.getSession().getAttribute("UserSession");
    		 if( userSessionForIsExisted.getAccount() != null ){
    			 return mapping.findForward("existedUserSession");
    	     }
    	}
    	
    	String systemVersion = System.getProperty("System_Version");
//    	String systemTeam = System.getProperty("System_Team");
//    	String contactInformation = System.getProperty("contact_information");
    	HttpSession session = request.getSession();
    	session.setAttribute("SystemVersion", systemVersion);
//    	session.setAttribute("SystemTeam", systemTeam);
//    	session.setAttribute("ContactInformation", contactInformation);
        return mapping.findForward("success");
    }
}
