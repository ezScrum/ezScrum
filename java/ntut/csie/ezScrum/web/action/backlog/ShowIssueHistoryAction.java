package ntut.csie.ezScrum.web.action.backlog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.dataObject.UnplannedObject;

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

		// get parameter info
		long issueId = Long.parseLong(request.getParameter("issueID"));
		String issueType = request.getParameter("issueType");

		// 用 Gson 轉換 issue 為 json 格式傳出
		IssueHistoryUI ihui = null;
		if (issueType.equals("Task")) {
			TaskObject task = TaskObject.get(issueId);
			if (task != null) {
				ihui = new IssueHistoryUI(task);
			}
		} else if (issueType.equals("Story")){
			StoryObject story = StoryObject.get(issueId);
			if (story != null) {
				ihui = new IssueHistoryUI(story);
			}
		} else {
			UnplannedObject unplanned = UnplannedObject.get(issueId);
			if (unplanned != null) {
				ihui = new IssueHistoryUI(unplanned);
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
		
		public IssueHistoryUI(UnplannedObject unplanned) {
			if (unplanned != null) {
				Id = unplanned.getId();
				Link = "";
				Name = unplanned.getName();
				IssueType = "Unplanned";

				if (unplanned.getHistories().size() > 0) {
					for (HistoryObject history : unplanned.getHistories()) {
						if (history.getDescription().length() > 0) {
							IssueHistories.add(new IssueHistoryList(history));
						}
					}
				}
			}
		}

		public IssueHistoryUI(StoryObject story) {
			if (story != null) {
				Id = story.getId();
				Link = "";
				Name = story.getName();
				IssueType = "Story";

				if (story.getHistories().size() > 0) {
					for (HistoryObject history : story.getHistories()) {
						if (history.getDescription().length() > 0) {
							IssueHistories.add(new IssueHistoryList(history));
						}
					}
				}
			}
		}

		public IssueHistoryUI(TaskObject task) {
			Id = task.getId();
			Link = "";
			Name = task.getName();
			IssueType = "Task";

			for (HistoryObject history : task.getHistories()) {
				IssueHistories.add(new IssueHistoryList(history));
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
