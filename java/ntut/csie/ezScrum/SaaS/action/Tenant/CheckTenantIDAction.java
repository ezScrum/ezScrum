package ntut.csie.ezScrum.SaaS.action.Tenant;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import ntut.csie.ezScrum.SaaS.multitenancy.TenantManager;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class CheckTenantIDAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String id = request.getParameter("id");
		
		// 設置Header與編碼
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");

		try {
			// 將判斷帳號是否有效結果傳給View
			PrintWriter out = response.getWriter();
			out.print(validateAccountID(id));
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 判斷帳號是否已被建立, 建立則傳給View無法新增帳號的訊息 判斷帳號是否為有效的字元, 數字與英文才為有效 true則已存在,false則不存在
	 * 
	 * @param id
	 * @return
	 */
	private String validateAccountID(String id) {

		// 判斷帳號是否符合只有英文+數字的格式
		Pattern p = Pattern.compile("[0-9a-zA-Z_]*");
		Matcher m = p.matcher(id);
		boolean b = m.matches();

		// 若帳號可建立且ID format正確 則回傳true
		TenantManager tm = new TenantManager();
		if (b && !tm.isTenantExist(id) && !id.isEmpty()) {			
			return "true";
		}

		return "false";
	}
}
