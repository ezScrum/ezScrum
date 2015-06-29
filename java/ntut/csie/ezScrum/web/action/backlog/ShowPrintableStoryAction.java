package ntut.csie.ezScrum.web.action.backlog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.core.MakePDFService;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.logic.ScrumRoleLogic;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DownloadAction;

import com.lowagie.text.DocumentException;

public class ShowPrintableStoryAction extends DownloadAction {
	private static Log log = LogFactory.getLog(ShowPrintableStoryAction.class);

	protected StreamInfo getStreamInfo(ActionMapping mapping,
	        ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		// get session info
		ProjectObject project = (ProjectObject) SessionManager.getProjectObject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");

		// get parameter info
		long sprintId = Long.parseLong(request.getParameter("sprintID"));
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, sprintId);
		SprintBacklogMapper backlog = sprintBacklogLogic.getSprintBacklogMapper();
		ArrayList<StoryObject> issues = sprintBacklogLogic.getStoriesByImp();

		request.setAttribute("SprintID", backlog.getSprintId());
		request.setAttribute("Stories", issues);
		File file = null;

		try {
			//直接嵌入server上的pdf字型擋給系統 
			ServletContext sc = getServlet().getServletContext();
			String ttfPath = sc.getRealPath("") + "/WEB-INF/otherSetting/uming.ttf";

			MakePDFService makePDFService = new MakePDFService();
			file = makePDFService.getFile(ttfPath, issues);
		} catch (DocumentException de) {
			log.debug(de.toString());
		} catch (IOException ioe) {
			log.debug(ioe.toString());
		}

		response.setHeader("Content-disposition", "inline; filename=SprintStory.pdf");

		AccountObject account = session.getAccount();
		//		ScrumRole sr = new ScrumRoleManager().getScrumRole(project, acc);
		ScrumRole sr = new ScrumRoleLogic().getScrumRole(project, account);
		if (file == null) {
			throw new Exception(" pdf file is null");
		}
		if (sr.getAccessSprintBacklog()) {
			String contentType = "application/pdf";
			return new FileStreamInfo(contentType, file);
		} else {
			return null;
		}
	}
}
