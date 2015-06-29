package ntut.csie.ezScrum.web.action.rbac;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.mapper.AccountMapper;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class ShowAccountInfoAction extends Action {
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// 取得要取得帳號資訊的id
		String id = request.getParameter("id");
		AccountObject account = new AccountMapper().getAccount(id);
		
		// write account to XML format
		StringBuilder sb = new StringBuilder();
		sb.append("<Accounts>");
		sb.append("<Account>");
		sb.append("<ID>").append(id).append("</ID>");				
		sb.append("<Name>").append(account.getNickName()).append("</Name>");				
		sb.append("<Mail>").append(account.getEmail()).append("</Mail>");
		sb.append("<Enable>").append(account.getEnable()).append("</Enable>");
		sb.append("</Account>");
	    sb.append("</Accounts>");
	    
	    try {
			response.setContentType("text/xml; charset=utf-8");
			response.getWriter().write(sb.toString());
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
