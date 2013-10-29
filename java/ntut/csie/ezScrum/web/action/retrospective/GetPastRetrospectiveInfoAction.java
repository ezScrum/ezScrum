package ntut.csie.ezScrum.web.action.retrospective;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetPastRetrospectiveInfoAction extends Action{
	// --------------------------------------------------------- Instance
	// Variables
	private static Log log = LogFactory.getLog(GetAddNewRetrospectiveInfoAction.class);

	// --------------------------------------------------------- Methods
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		IProject project = (IProject) request.getSession().getAttribute("Project");	

    	SprintPlanHelper spHelper = new SprintPlanHelper(project);
    	List<ISprintPlanDesc> plans = spHelper.loadListPlans();

		StringBuilder sb = new StringBuilder();
		TranslateSpecialChar tsc = new TranslateSpecialChar();
    	
    	if (plans.size() <= 0) {
    		// default data for null sprint backlog  
    		sb.append("<Sprints>" +
		    				"<Sprint>" +
		    					"<Id>0</Id>" +
		    					"<Name>No sprint</Name>" +
		    					"<Start>false</Start>" +
		    					"<Edit>\"0\"</Edit>" +
		    					"<Goal></Goal>" +
		    				"</Sprint>" +
		    			"</Sprints>");
    	} else {
	    	//今天的日期
    		Date today = new Date();
	    	//所有 Sprint 封裝成 XML 給 Ext(ComboBox) 使用
			sb.append("<Sprints>");
			for (ISprintPlanDesc sprint : plans)
			{
				// 若此 Sprint 尚未開始，則過濾掉
				String s = sprint.getStartDate();
				Date startDate = DateUtil.dayFilter(s);
				boolean start = startDate.after(today);
				if(start == true){
					continue;
				}
				
				sb.append("<Sprint>");
				sb.append("<Id>" + sprint.getID() + "</Id>");
				sb.append("<Name>Sprint #" + sprint.getID() + "</Name>");
				
				/*-----------------------------------------------------------
				*	判斷此Sprint是否已經開始了
				-------------------------------------------------------------*/
				sb.append("<Start>");
				sb.append((start?"true":"false"));
				sb.append("</Start>");
				
				
				/*------------------------------------------------------------
				 *  判斷此Sprint是否已過期，是否可以編輯
				 -------------------------------------------------------------*/
				String e = sprint.getEndDate();		// 取得日期為轉換成字串的日期 yyyy/mm/dd
				Date endDate = DateUtil.dayFilter(e);
				SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
				String transToday_String = format.format(today);
				Date transToday_Date = DateUtil.dayFilter(transToday_String);	// 將今日轉成與 endDate 一樣的格式，去除 時分秒
				sb.append("<Edit>");
				boolean end = transToday_Date.after(endDate);	// 今日  (before && equal) 結束日期時為 true
				sb.append( (! end ? "1":"0") );	// 因為是用 after 所以要多加 not 反過來
				sb.append("</Edit>");
				
				
				/*-----------------------------------------------------------
				*	加入Sprint Goal
				-------------------------------------------------------------*/
				sb.append("<Goal>");
				sb.append(tsc.TranslateXMLChar(sprint.getGoal()));
				sb.append("</Goal>");
	
				sb.append("</Sprint>");
			}
			sb.append("</Sprints>");
    	}
			
		try {
			response.setContentType("text/xml; charset=utf-8");
			response.getWriter().write(sb.toString());
			response.getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

}
