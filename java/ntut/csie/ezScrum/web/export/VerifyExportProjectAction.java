package ntut.csie.ezScrum.web.export;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.simple.JSONObject;

public class VerifyExportProjectAction extends Action {

	@SuppressWarnings("unchecked")
	public ActionForward execute(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) {

		JSONObject json = new JSONObject();
		json.put("isSuccess", true);
		System.out.println("json string: " + json.toJSONString());
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
