package ntut.csie.ezScrum.web.importer;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.simple.JSONObject;

public class VerifyImportProjectAction extends Action {

	@SuppressWarnings("unchecked")
	public ActionForward execute(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) {

		JSONObject json = new JSONObject();
		DatabaseVersionChecker databaseVersionChecker = new DatabaseVersionChecker();
		json.put("isDatabaseVersionValid", databaseVersionChecker.isDatabaseVersionValid());
		json.put("report", databaseVersionChecker.getReport());
		// 設定response 內容和型態
		try {
			response.setContentType("text/json; charset=utf-8");
			response.getWriter().write(json.toJSONString());
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
