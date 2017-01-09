package ntut.csie.ezScrum.web.action.backlog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DownloadAction;

import com.lowagie.text.DocumentException;

import ntut.csie.ezScrum.dao.ProjectDAO;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.core.MakePDFService;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.logic.ScrumRoleLogic;
import ntut.csie.ezScrum.web.support.SessionManager;

public class ShowPrintableStoriesInProductBacklogAction extends DownloadAction {
	private static Log log = LogFactory.getLog(ShowPrintableStoryAction.class);

	@Override
	protected StreamInfo getStreamInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// get session info
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		
		String projectName = request.getParameter("projectname");
		ProjectDAO projectDAO = new ProjectDAO();
		ProjectObject project = projectDAO.get(projectName);
		
		String selectedSerialStoriesId = request.getParameter("selectedStoriesId");

		String[] selectedSerialStoryId = selectedSerialStoriesId.split(",");
		
		ProductBacklogHelper productbackloghelper = new ProductBacklogHelper(project);
		
		ArrayList<StoryObject> stories= new ArrayList<StoryObject>();
		
		for (String storySerialId : selectedSerialStoryId) {
				stories.add(productbackloghelper.getStory(project.getId(), Long.valueOf(storySerialId).longValue()));
		}
		
		File file = null;

		try {
			//直接嵌入server上的pdf字型擋給系統 
			ServletContext sc = getServlet().getServletContext();
			String ttfPath = sc.getRealPath("") + "/WEB-INF/otherSetting/uming.ttf";

			MakePDFService makePDFService = new MakePDFService();
			file = makePDFService.getFile(ttfPath, stories);
		} catch (DocumentException de) {
			log.debug(de.toString());
		} catch (IOException de) {
			log.debug(de.toString());
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
