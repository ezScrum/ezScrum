package ntut.csie.ezScrum.web.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.service.IssueBacklog;
import ntut.csie.ezScrum.web.control.ProductBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.Translation;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AjaxDeleteFileAction extends PermissionAction {
	private static Log log = LogFactory.getLog(AjaxDeleteFileAction.class);
	
	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessProductBacklog();
	}

	@Override
	public boolean isXML() {
		// html
		return false;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		// 取得刪除file前需要的資料
		// get project from session or DB
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");

		// get parameter info
		String entryPoint = request.getParameter("entryPoint");
		long issueID = Integer.parseInt(request.getParameter("issueId"));
		long fileID = Long.parseLong(request.getParameter("fileId"));
		
		StringBuilder result = new StringBuilder("{\"success\":false}");
		
		// 透過file的 id 刪除attach file
		ProductBacklogHelper pbHelper = new ProductBacklogHelper(project,session);
		pbHelper.deleteAttachFile(fileID);
		
		// 如果是在CustomIssue的頁面attach file的話，則translate custom issue的json
		if(entryPoint!=null && entryPoint.equals("CustomIssue")) {
			IssueBacklog backlog = new IssueBacklog(project, session);
			IIssue issue = backlog.getIssue(issueID);
			List<IIssue> list = new ArrayList<IIssue>();
			list.add(issue);
			result = new StringBuilder(new Translation().translateCustomIssueToJson(list));
		} else {
			IIssue issue = pbHelper.getIssue(issueID);
			result = new StringBuilder(new Translation().translateStoryToJson(issue));
		}

		return result;
	}
}