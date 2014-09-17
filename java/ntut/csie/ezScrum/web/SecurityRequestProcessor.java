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

package ntut.csie.ezScrum.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.tiles.TilesRequestProcessor;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/*
 * 外部程式要呼叫action前都必須先經過這個程式的判定是否可進入
 * 目前是拿來檢查session是否存在是否要轉頁(只針對document.location.href = "./xxx.do" 的action )
 * 而不能拿來替ext widget中的url:"yyy.do" 的action轉頁(因為只是將資料內容轉換而不能轉換頁面)
 * */

public class SecurityRequestProcessor extends TilesRequestProcessor {
    private static Log log = LogFactory.getLog(SecurityRequestProcessor.class);

    protected boolean processRoles(HttpServletRequest request,
        HttpServletResponse response, ActionMapping mapping)
        throws IOException, ServletException {
        log.info("SecurityRequestProcessor Process Role.......");
        
        //允許直接access的動作
        if (mapping.getPath().equals("/logon") ||
        		mapping.getPath().equals("/feedbackDisplay") ||
                mapping.getPath().equals("/logonSubmit") ||
                mapping.getPath().equals("/viewProjectFeed") ||
                mapping.getPath().equals("/viewPersonalFeed") ||
                mapping.getPath().equals("/showBuilderReport") ||
                mapping.getPath().equals("/isLogin") ||
                mapping.getPath().equals("/login") ||
                mapping.getPath().equals("/showIssueDetail") ||
                mapping.getPath().equals("/buildResult") ||
                mapping.getPath().equals("/query") ||
                mapping.getPath().equals("/getFolderList") ||
                mapping.getPath().equals("/fileUpload") ||
                //report issue
                mapping.getPath().equals("/AjaxCreateIssueReprot") ||
                mapping.getPath().equals("/AjaxShowIssueReportBack")||
                mapping.getPath().equals("/showReportIssues")||
                mapping.getPath().equals("/fileUpload") ||
                //add comment
                mapping.getPath().equals("/getCommentInfo")||
                mapping.getPath().equals("/addCommentByTeam")||
                //attachFile
                mapping.getPath().equals("/ajaxAttachFile")||
                //給外部使用者觀看所有回報的問題
                mapping.getPath().equals("/ajaxShowReportIssues")||
                mapping.getPath().equals("/showIssueInformation")||
                mapping.getPath().equals("/viewIssueInfo")||
                mapping.getPath().equals("/ajaxGetIssueInfo")||
                mapping.getPath().equals("/fileDownload")   ||
                mapping.getPath().equals("/validateUserEvent") //如果action是檢查session是否過期則直接讓它過
        		) 
        {
        	log.info("SecurityRequestProcessor True");
            return true;
        }
        
        HttpSession session = request.getSession();
        
        //若有登入的session存在也可以直接access
        if (session.getAttribute("UserSession") != null) {
            return true;
        }
        
        //session不存在時
        //遇到以下頁面轉轉頁
        ActionForward forward = null;
        if( mapping.getPath().equals("/viewList") ||
        		mapping.getPath().equals("/viewProject") ||
        		mapping.getPath().equals("/viewManagement")
        		){
            forward = new ActionForward();
            forward.setName("home");
            forward.setPath("/logon.do");
            forward.setRedirect(true);
        }

        if (forward != null) {
            processForwardConfig(request, response, forward);
        }
        
        //其它沒有session不準存取
        return false;
    }
}
