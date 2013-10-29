package ntut.csie.ezScrum.web.action.backlog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.IIssueHistory;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.control.ProductBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.google.gson.Gson;

public class ShowIssueHistoryAction extends PermissionAction {
	private static Log log = LogFactory.getLog(ShowIssueHistoryAction.class);

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

		// get project from session or DB
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");

		// get parameter info
		String issueID = request.getParameter("issueID");
		Long id = -1L;
		if ((issueID != null) && (!issueID.equals("-1"))) {
			id = Long.parseLong(issueID);
		}

		// 用Gson轉換issue為json格式傳出
		ProductBacklogHelper helper = new ProductBacklogHelper(project, session);
		IIssue issue = helper.getIssue(id);
		IssueHistoryUI ihui = new IssueHistoryUI(issue);
		Gson gson = new Gson();
		return new StringBuilder(gson.toJson(ihui));
	}

	private class IssueHistoryUI {
		private long Id = -1L;
		private String Link = "";
		private String Name = "";
		private String IssueType = "";

		private List<IssueHistoryList> IssueHistories = new LinkedList<IssueHistoryList>();

		public IssueHistoryUI(IIssue issue) {
			if (issue != null) {
				this.Id = issue.getIssueID();
				this.Link = issue.getIssueLink();
				this.Name = issue.getSummary();
				this.IssueType = issue.getCategory();

				if (issue.getHistory().size() > 0) {
					for (IIssueHistory history : issue.getIssueHistories()) {
						if (history.getDescription().length() > 0) {
							this.IssueHistories.add(new IssueHistoryList(history));
						}
					}
				}
			}
		}
	}

	private class IssueHistoryList {
		private String Description = "";
		private String HistoryType = "";
		private String ModifiedDate = "";

		public IssueHistoryList(IIssueHistory history) {
			parseDate(history.getModifyDate());
			parseType_Desc(history.getDescription());
		}

		private void parseType_Desc(String desc) {
			String[] token = desc.split(":");
			if (token.length == 2) {
				this.HistoryType = token[0];
				this.Description = token[1];
			} else {
				this.HistoryType = "";
				this.Description = desc;
			}
		}

		private void parseDate(long date) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd-hh:mm:ss");
			Date d = new Date(date);
			this.ModifiedDate = sdf.format(d);
		}
	}
}
