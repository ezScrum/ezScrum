package ntut.csie.ezScrum.web.action.report;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
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
		ProjectObject project = SessionManager.getProjectObject(request);

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
		return result;
	}
}
