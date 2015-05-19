package ntut.csie.ezScrum.web.action.plan;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class NotYetEndReleaseForMoveStory extends PermissionAction {
	private static Log log = LogFactory.getLog(ShowReleasePlan2Action.class);

	@Override
	public boolean isValidAction() {
		// 因為move story一個給release plan頁面使用另外一個給sprint backlog使用所以要有兩個其中之一個權限才能使用
		boolean pass = (super.getScrumRole().getAccessReleasePlan() || super
				.getScrumRole().getAccessSprintBacklog());
		return pass;
	}

	@Override
	public boolean isXML() {
		// html
		return false;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		// get session info
		ProjectObject project = SessionManager.getProjectObject(request);
		ReleasePlanHelper RPhelper = new ReleasePlanHelper(project);
		StringBuilder result = new StringBuilder(setReleaseToJSon(RPhelper));
		return result;
	}

	// 透過release helper將sprint的資訊寫成JSon
	private String setReleaseToJSon(ReleasePlanHelper RPhelper) {
		Date today = new Date();
		StringBuilder sprintTree = new StringBuilder("");
		
		IReleasePlanDesc[] descs = RPhelper.loadReleasePlans(); 
		if (descs != null) { // 有 sprint 資訊，則抓取 sprint 的 xml 資料
			sprintTree.append("[");
			// 將資訊設定成 JSon 輸出格式 sprint已經開始的無法被移動
			for (int i=0 ; i<descs.length ; i++) {
				// 尚未開始的sprint
				if (stringToDate(descs[i].getEndDate()).compareTo(today) >= 0) {
					sprintTree.append("{ID:\'" + descs[i].getID() + "\'");
					sprintTree.append(", Goal:\'" + descs[i].getName() + "\'" + "},");
				}
			}
			
			sprintTree.delete(sprintTree.length()-1, sprintTree.length());	// 去除最後的逗號
			sprintTree.append("]");
		}

		return sprintTree.toString();
	}

	private Date stringToDate(String dateString) {// 將時間字串轉乘Date物件
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = null;
		try {
			date = simpleDateFormat.parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}
}
