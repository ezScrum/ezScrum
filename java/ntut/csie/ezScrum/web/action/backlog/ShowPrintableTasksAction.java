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

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.core.MakePDFService;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.logic.ScrumRoleLogic;
import ntut.csie.ezScrum.web.support.SessionManager;

public class ShowPrintableTasksAction extends DownloadAction {
	private static Log log = LogFactory.getLog(ShowPrintableStoryAction.class);
	
	protected StreamInfo getStreamInfo(ActionMapping mapping,
	        ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		// get session info
		ProjectObject project = (ProjectObject) SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
				
		String[] getSelectedTaskId = request.getParameterValues("selects");
		
		String[] selectedSerialTasksId = getSelectedTaskId[0].replaceAll("\\[", "").replaceAll("\\]", "").split(",");
		
		System.out.println(123);
		ArrayList<TaskObject> selectedTasks = new ArrayList<TaskObject>();
		for (String serialTaskId : selectedSerialTasksId) {
			System.out.println(serialTaskId + " test \n");
			
			TaskObject task = TaskObject.get(project.getId(), Long.parseLong(serialTaskId));
			selectedTasks.add(task);
			
		}
		System.out.println(456);
		request.setAttribute("Tasks", selectedTasks);
		File file = null;
		
		try {
			//直接嵌入server上的pdf字型擋給系統 
			ServletContext sc = getServlet().getServletContext();
			String ttfPath = sc.getRealPath("") + "/WEB-INF/otherSetting/uming.ttf";

			MakePDFService makePDFService = new MakePDFService();
			file = makePDFService.getTaskFile(ttfPath, selectedTasks);
		} catch(DocumentException de){
			log.debug(de.toString());
		} catch (IOException ioe) {
			log.debug(ioe.toString());
		}
		
		response.setHeader("Content-disposition", "inline; filename=SprintStoryTask.pdf");

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
