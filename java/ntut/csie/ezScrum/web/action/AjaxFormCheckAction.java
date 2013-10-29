package ntut.csie.ezScrum.web.action;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.jcis.core.util.FormCheckUtil;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class AjaxFormCheckAction extends Action {
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		String value = request.getParameter("value");
		String type = request.getParameter("type");
		
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");

		try {
			PrintWriter pw = response.getWriter();
			pw.print(getResult(value, type));
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private boolean getResult(String value, String type) {
		if (type != null && value != null) {
			if (type.equals(FormCheckUtil.DIGITAL))
				return FormCheckUtil.isDigital(value);
			else if (type.equals(FormCheckUtil.EXISTED))
				return FormCheckUtil.isExisted(value);
			else if (type.equals(FormCheckUtil.DATE))
				return FormCheckUtil.isDate(value);
			else if (type.equals(FormCheckUtil.INTEGER))
				return FormCheckUtil.isInteger(value);
			else if (type.equals(FormCheckUtil.LENGTH128))
				return FormCheckUtil.isLength128(value);
			else
				return false;
		}
		
		return false;
	}
}
