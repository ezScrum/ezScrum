package ntut.csie.ezScrum.web.action.backlog;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.core.MakePDFService;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.logic.ScrumRoleLogic;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.account.core.IAccount;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DownloadAction;

import com.lowagie.text.DocumentException;


public class ShowPrintableStoryAction extends DownloadAction  {
	private static Log log = LogFactory.getLog(ShowPrintableStoryAction.class);
	
	protected StreamInfo getStreamInfo(ActionMapping mapping,
			ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		// get session info
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession userSession = (IUserSession) request.getSession().getAttribute("UserSession");

		// get parameter info
		String sprintID = request.getParameter("sprintID");
		
//		SprintBacklogMapper backlog = null;
//		try{
//			backlog = (new SprintBacklogLogic(project, userSession, sprintID)).getSprintBacklogMapper();
//		}catch (Exception e) {
//		}
//		
////		SprintBacklog backlog = null;
////		try {
////			if (sprintID==null||sprintID.equals(""))    	
////				backlog = new SprintBacklog(project,userSession);
////			else
////				backlog = new SprintBacklog(project,userSession,Integer.parseInt(sprintID));
////		} catch (Exception e){
////			//return mapping.findForward("error");
////		}
//
//		List<IIssue> issues = backlog.getStories();
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, userSession, sprintID);
		SprintBacklogMapper backlog = sprintBacklogLogic.getSprintBacklogMapper();
		List<IIssue> issues = sprintBacklogLogic.getStoriesByImp();

		request.setAttribute("SprintID", backlog.getSprintPlanId());
		request.setAttribute("Stories", issues) ;
		File file = null;
		
		try {
			//直接嵌入server上的pdf字型擋給系統 
			ServletContext sc = getServlet().getServletContext();
			String ttfPath = sc.getRealPath("")+"/WEB-INF/otherSetting/uming.ttf";

			MakePDFService makePDFService = new MakePDFService();
			file = makePDFService.getFile(ttfPath, issues);
		} catch (DocumentException de) {
			log.debug(de.toString());
		} catch (IOException ioe) {
			log.debug(ioe.toString());
		}

		response.setHeader("Content-disposition", "inline; filename=SprintStory.pdf");
		
		IAccount acc = userSession.getAccount();
//		ScrumRole sr = new ScrumRoleManager().getScrumRole(project, acc);
		ScrumRole sr = new ScrumRoleLogic().getScrumRole(project, acc);
		if( file == null ){
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
