package ntut.csie.ezScrum.web.action.backlog;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DownloadAction;

import com.sun.tools.javac.code.Attribute.Array;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.support.SessionManager;

public class ShowPrintableTasksAction extends DownloadAction {
	private static Log log = LogFactory.getLog(ShowPrintableStoryAction.class);
	
	protected StreamInfo getStreamInfo(ActionMapping mapping,
	        ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		// get session info
		ProjectObject project = (ProjectObject) SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		
		// get parameter info
		long serialSprintId = Long.parseLong(request.getParameter("sprintID"));
		SprintObject sprint = SprintObject.get(project.getId(), serialSprintId);
		
		String[] selectedSerialTasksId = request.getParameterValues("selects");
		
		ArrayList<TaskObject> selectedTasks = new ArrayList<TaskObject>();
		for (String serialTaskId : selectedSerialTasksId) {
			TaskObject task = TaskObject.get(project.getId(), Long.parseLong(serialTaskId));
			selectedTasks.add(task);
		}
		return null;
	}
}
