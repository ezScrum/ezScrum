package ntut.csie.ezScrum.web.action.retrospective;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;
import ntut.csie.jcis.core.util.DateUtil;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetPastRetrospectiveInfoAction extends Action {
	// --------------------------------------------------------- Instance
	// Variables

	// --------------------------------------------------------- Methods
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		ProjectObject project = SessionManager.getProjectObject(request);

		SprintPlanHelper sprintPlanHelper = new SprintPlanHelper(project);
		ArrayList<SprintObject> sprints = sprintPlanHelper.getSprints();

		StringBuilder stringBuilder = new StringBuilder();
		TranslateSpecialChar translateSpecialChar = new TranslateSpecialChar();

		if (sprints.isEmpty()) {
			// default data for null sprint backlog
			stringBuilder.append("<Sprints>" + "<Sprint>" + "<Id>0</Id>"
					+ "<Name>No sprint</Name>" + "<Start>false</Start>"
					+ "<Edit>\"0\"</Edit>" + "<Goal></Goal>" + "</Sprint>"
					+ "</Sprints>");
		} else {
			// 今天的日期
			Date today = new Date();
			// 所有 Sprint 封裝成 XML 給 Ext(ComboBox) 使用
			stringBuilder.append("<Sprints>");
			for (SprintObject sprint : sprints) {
				// 若此 Sprint 尚未開始，則過濾掉
				String startDateString = sprint.getStartDateString();
				Date startDate = DateUtil.dayFilter(startDateString);
				boolean start = startDate.after(today);
				if (start == true) {
					continue;
				}

				stringBuilder.append("<Sprint>");
				stringBuilder.append("<Id>" + String.valueOf(sprint.getId()) + "</Id>");
				stringBuilder.append("<Name>Sprint #" + String.valueOf(sprint.getId()) + "</Name>");

				/*-----------------------------------------------------------
				 *	判斷此Sprint是否已經開始了
				-------------------------------------------------------------*/
				stringBuilder.append("<Start>");
				stringBuilder.append((start ? "true" : "false"));
				stringBuilder.append("</Start>");

				/*------------------------------------------------------------
				 *  判斷此Sprint是否已過期，是否可以編輯
				 -------------------------------------------------------------*/
				String endDateString = sprint.getDueDateString(); // 取得日期為轉換成字串的日期
																	// yyyy/mm/dd
				Date endDate = DateUtil.dayFilter(endDateString);
				SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
				String transToday_String = format.format(today);
				Date transToday_Date = DateUtil.dayFilter(transToday_String); // 將今日轉成與
																				// endDate
																				// 一樣的格式，去除
																				// 時分秒
				stringBuilder.append("<Edit>");
				boolean end = transToday_Date.after(endDate); // 今日 (before &&
																// equal) 結束日期時為
																// true
				stringBuilder.append((!end ? "1" : "0")); // 因為是用 after 所以要多加
															// not 反過來
				stringBuilder.append("</Edit>");

				/*-----------------------------------------------------------
				 *	加入Sprint Goal
				-------------------------------------------------------------*/
				stringBuilder.append("<Goal>");
				stringBuilder.append(translateSpecialChar.TranslateXMLChar(sprint.getGoal()));
				stringBuilder.append("</Goal>");
				stringBuilder.append("</Sprint>");
			}
			stringBuilder.append("</Sprints>");
		}

		try {
			response.setContentType("text/xml; charset=utf-8");
			response.getWriter().write(stringBuilder.toString());
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

}