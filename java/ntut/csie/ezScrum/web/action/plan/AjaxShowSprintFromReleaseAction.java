package ntut.csie.ezScrum.web.action.plan;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;
import ntut.csie.jcis.core.util.DateUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class AjaxShowSprintFromReleaseAction extends Action {
	private static Log log = LogFactory.getLog(AjaxShowSprintFromReleaseAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException {

		ProjectObject project = SessionManager.getProjectObject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		
		String ReleaseID = request.getParameter("Rid");

		ReleasePlanHelper RPhelper = new ReleasePlanHelper(project);
		SprintPlanHelper SPhelper = new SprintPlanHelper(project);
		
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
	
		PrintWriter pw = response.getWriter();
		
		TranslateSpecialChar tsc = new TranslateSpecialChar();
		
		String XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
				
		XML += "<sprints id=\""+ReleaseID+"\">\n";	
			// sprint 資訊
			if (ReleaseID != null) {
				IReleasePlanDesc IRDesc = RPhelper.getReleasePlan(ReleaseID);
				
				if (IRDesc.getSprintDescList() != null) {				// 有 sprint 資訊，則抓取 sprint 的 xml 資料
					List<ISprintPlanDesc> list_ISPdesc = new ArrayList<ISprintPlanDesc>();
					List<ISprintPlanDesc> list = IRDesc.getSprintDescList();	// 讀取 release plan 包含的 sprint plan 編號
					
					// list_ISPdesc 依照 StartDate 排序
					for (int i=0 ; i<list.size() ; i++) {
						ISprintPlanDesc ISPdesc = SPhelper.loadPlan(list.get(i).getID());	// 根據編號去讀取 sprint plan 資訊
						Date addDate = DateUtil.dayFilter(ISPdesc.getStartDate());	// 要新增的 Date
						
						int index = 0;
						for (ISprintPlanDesc desc : list_ISPdesc) {
							ISprintPlanDesc ispDesc = list_ISPdesc.get(index);			// 目前要被比對的 sprint
							Date cmpDate = DateUtil.dayFilter(ispDesc.getStartDate());	// 要被比對的 Date
							if ( addDate.compareTo(cmpDate) < 0 ) {
								break;
							}
							index++;
						}
						list_ISPdesc.add(index, ISPdesc);
					}
					
					// 將資訊設定成 XML 輸出格式
					for (ISprintPlanDesc desc : list_ISPdesc) {
						XML += "\t<sprint id=\"" + desc.getID() + "\">\n";
							
							XML += "\t\t<goal>" + tsc.TranslateXMLChar(desc.getGoal()) + "</goal>\n";
							XML += "\t\t<startdate>" + desc.getStartDate() + "</startdate>\n";
							XML += "\t\t<interval>" + desc.getInterval() + "</interval>\n";
							XML += "\t\t<members>" + desc.getMemberNumber() + "</members>\n";
							if (desc.getDemoDate() == null || desc.getDemoDate() == "") {
								XML += "\t\t<demodate>" + "No Plan!" + "</demodate>\n";
							} else {
								XML += "\t\t<demodate>" + desc.getDemoDate() + "</demodate>\n";
							}
							XML += "\t\t<avaible>" + desc.getAvailableDays() + "</avaible>\n";
							XML += "\t\t<backlogURL>" +	"./showSprintBacklog.do?sprintID=" + desc.getID() + "</backlogURL>\n";
	
						XML += "\t</sprint>\n";
					}
				}
			}
		
		XML += "</sprints>";

		pw.print(new String(XML.getBytes("UTF-8"), "UTF-8"));
		pw.close();

		return null;
	}
}
