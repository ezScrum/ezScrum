package ntut.csie.ezScrum.web.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.internal.IssueTypeField;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.service.IssueBacklog;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class ShowIssueInformationAction extends Action {
	private static Log log = LogFactory.getLog(ShowIssueInformationAction.class);
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) {
		log.info(" Show Issue Information. ");
		
		IProject project = (IProject) request.getSession().getAttribute("Project");
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		TranslateSpecialChar tsc = new TranslateSpecialChar();

		// 欲查詢的issue id 以及其專案名稱
		String projectName = request.getParameter("projectName");
		String issueId = request.getParameter("issueID");

		// 如果project以及session的資訊是空的 則透過專案名稱抓取資料
		if (project == null & session == null) {
			project = (new ProjectMapper()).getProjectByID(projectName);// 根據專案名稱取得 IProject 物件
			session = new UserSession(null);
		}

		IIssue issue = null;
		ProductBacklogHelper PBHelper = new ProductBacklogHelper(session, project);
		IssueBacklog backlog = new IssueBacklog(project, session);

		if (issueId != null && !issueId.equals("")) {
			long issueID_int = Long.valueOf(issueId);
			issue = PBHelper.getStory(issueID_int);
			if (issue != null) {
				if (backlog.isScrumType(issue)) {
					request.setAttribute("issue", issue);
					request.setAttribute("summary", tsc.TranslateXMLChar(issue.getSummary()));
					request.setAttribute("Notes", tsc.TranslateXMLChar(issue.getNotes()));
					request.setAttribute("description", tsc.TranslateXMLChar(issue.getDescription()));
					request.setAttribute("howToDemo", tsc.TranslateXMLChar(issue.getHowToDemo()));
					return mapping.findForward("scrumIssue");
				}
				else {
					issue = backlog.getIssue(issueID_int);
					request.setAttribute("issue", issue);
					List<IssueTypeField> typeFields = issue.getFields();
					for (IssueTypeField typeField : typeFields) {
						String fieldlName = typeField.getFieldName();
						String fieldValue = tsc.TranslateXMLChar(typeField.getFieldValue());
						request.setAttribute(fieldlName, fieldValue);
					}
					return mapping.findForward("customIssue");
				}
			}
		}
		return mapping.findForward("error");
	}
}
