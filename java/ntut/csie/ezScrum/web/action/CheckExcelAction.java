package ntut.csie.ezScrum.web.action;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class CheckExcelAction extends Action {

	public ActionForward execute(ActionMapping mapping,ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		//檔案的路徑
		String path = request.getParameter("path");
        //判斷附檔名是否為.csv
		boolean valid =isValid(path);
		// 設置Header與編碼
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");

		try {
			//設定回傳結果
			PrintWriter out = response.getWriter();
			out.print(valid);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private boolean isValid(String path){
		boolean valid = path.endsWith(".xls");
		return valid;
	}
}

