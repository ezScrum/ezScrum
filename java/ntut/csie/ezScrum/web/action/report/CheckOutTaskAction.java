package ntut.csie.ezScrum.web.action.report;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.dataObject.NotificationObject;
import ntut.csie.ezScrum.web.helper.SprintBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.Translation;
import ntut.csie.jcis.core.util.DateUtil;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class CheckOutTaskAction extends PermissionAction {

	@Override
	public boolean isValidAction() {
		return (super.getScrumRole().getAccessProductBacklog() && (!super.getScrumRole().isGuest()));
	}

	@Override
	public boolean isXML() {
		return false;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) {

		// get project from session or DB
		ProjectObject project = SessionManager.getProject(request);		
		
		// get parameter info
		long taskId = Long.parseLong(request.getParameter("Id"));
		String name = request.getParameter("Name");
		String handler = request.getParameter("Handler");
		String partners = request.getParameter("Partners");
		String notes = request.getParameter("Notes");
		String changeDate = request.getParameter("ChangeDate");
		DateFormat dateFormat = new SimpleDateFormat(DateUtil._16DIGIT_DATE_TIME); // 設定changeDate正確的時間格式
		StringBuilder result = new StringBuilder("");
		SprintBacklogHelper sprintBacklogHelper = new SprintBacklogHelper(project);

		try {
			if (changeDate != null && !changeDate.equals(""))		// 用來檢查ChangeDate的格式是否正確, 若錯誤會丟出ParseException
				dateFormat.parse(changeDate);
			sprintBacklogHelper.checkOutTask(taskId, name, handler, partners, notes, changeDate);
			TaskObject task = sprintBacklogHelper.getTask(taskId);	// return checkout的issue的相關資訊
			result.append(Translation.translateTaskboardTaskToJson(task));
		} catch (ParseException e) {								// ChangeDate格式錯誤
			result.append("fail...非正確日期的參數");
		} catch (NullPointerException e) {							// issue為null
			result.append("fail...issue不存在");
		}
		System.out.println(result);
		
		//Send Notification
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		String accToken = session.getAccount().getToken();
		String sender = session.getAccount().getUsername();
		ArrayList<Long> receiversId = project.getProjectMembersId();
		SendNotification(sender, accToken, receiversId, taskId, handler, project.getName());
		
		return result;
	}
	
	private void SendNotification(String sender, String accToken, ArrayList<Long> receiversId, long taskId, String handler,String projectName){
		Configuration configuration = new Configuration();
		String fromServiceUrl;
		if(configuration.getServerUrl() == "127.0.0.1")
			fromServiceUrl = "localhost";
		else
			fromServiceUrl = configuration.getServerUrl();
		NotificationObject notification = new NotificationObject();
		notification.setSender(sender);
		notification.setAccToken(accToken);
		notification.setReceiversId(receiversId);
		notification.setMessageTitle(handler +" check out Task: " + taskId);
		notification.setMessageBody("In project:" + projectName);
		notification.setFromURL("http://"+fromServiceUrl+":8080/ezScrum/viewProject.do?projectName=" + projectName);
		String result = notification.send();
		System.out.println(result);
	}
}
