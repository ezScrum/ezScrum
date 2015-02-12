package ntut.csie.ezScrum.web.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.helper.ProjectHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class ViewListAction extends Action {
	private static Log log = LogFactory.getLog(ViewListAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) {

		// 取得使用者登入資料
		IUserSession userSession = (IUserSession) request.getSession().getAttribute("UserSession");
		AccountObject account = userSession.getAccount();
		log.debug(userSession.getAccount().getUsername());

		// 移除Project information如此一來專案列表就不顯示上次進入的Project ID。
		request.getSession().removeAttribute("Project");

		// 透過project helper得到response text
		ProjectHelper projectHelper = new ProjectHelper();
		String projectListXML = projectHelper.getProjectListXML(account);

		// 設定response 內容和型態
		try {
			response.setContentType("text/xml; charset=utf-8");
			response.getWriter().write(projectListXML);
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
