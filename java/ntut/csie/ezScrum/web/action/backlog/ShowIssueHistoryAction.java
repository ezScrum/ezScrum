package ntut.csie.ezScrum.web.action.backlog;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
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
		log.info(" Show Issue History. ");
		
		// get project from session or DB
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		// get parameter info
		String issueId = request.getParameter("issueID");
		String issueType = request.getParameter("issueType");
		long id = -1L;
		if ((issueId != null) && (!issueId.equals("-1"))) {
			id = Long.parseLong(issueId);
		}

		// 用Gson轉換issue為json格式傳出
		ProductBacklogHelper PBHelper = new ProductBacklogHelper(session, project);
		
		IssueHistoryUI ihui = null;
		if (issueType.equals("Task")) {
			TaskObject task = TaskObject.get(id);
			if (task != null) {
				ihui = new IssueHistoryUI(task);
			}
		} else {
			IIssue issue = PBHelper.getStory(id);
			try {
				ihui = new IssueHistoryUI(issue);
			} catch (SQLException e) {
			}
		}
		Gson gson = new Gson();
		return new StringBuilder(gson.toJson(ihui));			
	}

	private class IssueHistoryUI {
		private long Id = -1L;
		private String Link = "";
		private String Name = "";
		private String IssueType = "";

		private List<IssueHistoryList> IssueHistories = new LinkedList<IssueHistoryList>();

		public IssueHistoryUI(IIssue issue) throws SQLException {
			if (issue != null) {
				this.Id = issue.getIssueID();
				this.Link = issue.getIssueLink();
				this.Name = issue.getSummary();
				this.IssueType = issue.getCategory();

				if (issue.getHistories().size() > 0) {
					for (HistoryObject history : issue.getHistories()) {
						if (history.getDescription().length() > 0) {
							this.IssueHistories.add(new IssueHistoryList(history));
						}
					}
				}
			}
		}
		
		public IssueHistoryUI(TaskObject task) {
			this.Id = task.getId();
			this.Link = "";
			this.Name = task.getName();
			this.IssueType = "Task";
			
			for (HistoryObject history : task.getHistories()) {
				this.IssueHistories.add(new IssueHistoryList(history));
			}
		}
	}

	private class IssueHistoryList {
		private String Description = "";
		private String HistoryType = "";
		private String ModifiedDate = "";

		public IssueHistoryList(HistoryObject history) {
			parseDate(history.getCreateTime());
			Description = history.getDescription();
			HistoryType = history.getHistoryTypeString();
		}

		private void parseDate(long date) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd-hh:mm:ss");
			Date d = new Date(date);
			ModifiedDate = sdf.format(d);
		}
	}
}
