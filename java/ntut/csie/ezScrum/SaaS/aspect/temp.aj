package ntut.csie.ezScrum.SaaS.aspect;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.InitialPlugIn;
import ntut.csie.ezScrum.web.action.backlog.ShowPrintableStoryAction;
import ntut.csie.ezScrum.web.action.other.AjaxGetCustomIssueTypeAction;
import ntut.csie.ezScrum.web.action.report.AjaxGetTaskBoardDescriptionAction;
import ntut.csie.ezScrum.web.action.report.AjaxGetTaskBoardStoryTaskListByGuest;
import ntut.csie.ezScrum.web.action.report.GetSprintBurndownChartDataAction;
import ntut.csie.ezScrum.web.control.TaskBoard;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.actions.DownloadAction.StreamInfo;
import org.apache.struts.config.ModuleConfig;

public aspect temp {

	/*
	*	由於還沒重構完成，所以先取代 Action
	*/	
	
	// replace: public StringBuilder AjaxGetHandlerListAction.getResponse(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	pointcut showPrintableStoryActionPC(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	: execution(StreamInfo ShowPrintableStoryAction.getStreamInfo(ActionMapping, ActionForm, HttpServletRequest, HttpServletResponse)) && args(mapping, form, request, response);
	
	StreamInfo around(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception
	: showPrintableStoryActionPC(mapping, form, request, response) {
		System.out.println("replaced by AOP...showPrintableStoryActionPC: " + thisJoinPoint);
		response.setHeader("Content-disposition", "inline; filename=SprintStory.pdf");
		throw new Exception(" pdf file is null");
	}
	
	// replace: public StringBuilder AjaxGetHandlerListAction.getResponse(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	pointcut ajaxGetTaskBoardDescriptionActionPC(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	: execution(StringBuilder AjaxGetTaskBoardDescriptionAction.getResponse(ActionMapping, ActionForm, HttpServletRequest, HttpServletResponse)) && args(mapping, form, request, response);
	
	StringBuilder around(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	: ajaxGetTaskBoardDescriptionActionPC(mapping, form, request, response) {
		System.out.println("replaced by AOP...ajaxGetTaskBoardDescriptionActionPC: " + thisJoinPoint);
		
		StringBuilder result = new StringBuilder();
		result.append("{\"ID\":\"0\",\"SprintGoal\":\"\",\"Current_Story_Undone_Total_Point\":\"\",\"Current_Task_Undone_Total_Point\":\"\"}");
		return result;
	}
	
	// replace: public StringBuilder AjaxGetHandlerListAction.getResponse(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
//	pointcut ajaxGetHandlerListActionPC(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
//	: execution(StringBuilder AjaxGetHandlerListAction.getResponse(ActionMapping, ActionForm, HttpServletRequest, HttpServletResponse)) && args(mapping, form, request, response);
//	
//	StringBuilder around(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
//	: ajaxGetHandlerListActionPC(mapping, form, request, response) {
//		System.out.println("replaced by AOP...ajaxGetHandlerListActionPC: " + thisJoinPoint);
//		
//		StringBuilder result = new StringBuilder();
//		result.append("<Handlers><Result>success</Result>");
//			result.append("<Handler>");
//			result.append("<Name></Name>");
//			result.append("</Handler>");
//		result.append("</Handlers>");
//		
//		return result;
//	}
	
//	// replace: public ActionForward AjaxGetSprintBacklogDateInfoAction.execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
//	pointcut ajaxGetSprintBacklogDateInfoActionPC(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
//	: execution(ActionForward AjaxGetSprintBacklogDateInfoAction.execute(ActionMapping, ActionForm, HttpServletRequest, HttpServletResponse)) && args(mapping, form, request, response);
//	
//	ActionForward around(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
//	: ajaxGetSprintBacklogDateInfoActionPC(mapping, form, request, response) {
//		System.out.println("replaced by AOP...ajaxGetSprintBacklogDateInfoActionPC: " + thisJoinPoint);
//		
//		String result = "";
//		response.setContentType("text/html; charset=utf-8");
//		try {
//			response.getWriter().write(result);
//			response.getWriter().close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		return null;
//	}
	
	// replace: public ActionForward AjaxGetCustomIssueTypeAction.execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	pointcut ajaxGetCustomIssueTypeActionPC(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	: execution(ActionForward AjaxGetCustomIssueTypeAction.execute(ActionMapping, ActionForm, HttpServletRequest, HttpServletResponse)) && args(mapping, form, request, response);
	
	ActionForward around(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	: ajaxGetCustomIssueTypeActionPC(mapping, form, request, response) {
		System.out.println("replaced by AOP...ajaxGetCustomIssueTypeActionPC: " + thisJoinPoint);
		
		String result = "";
		try{
			StringBuilder sb = new StringBuilder();
			sb.append("<Root>");
			sb.append("<Result>Success</Result>");
			sb.append("<IssueType>");
			sb.append("<TypeId></TypeId>");
			sb.append("<TypeName></TypeName>");
			sb.append("<IsPublic></IsPublic>");
			sb.append("</IssueType>");
			sb.append("</Root>");
			result = sb.toString();
		} catch(Exception e){
			e.printStackTrace();
		}
		try {
			response.setContentType("text/xml; charset=utf-8");
			response.getWriter().write(result);
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	// replace: public ActionForward GetSprintBurndownChartDataAction.execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	pointcut getSprintBurndownChartDataActionPC(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	: execution(ActionForward GetSprintBurndownChartDataAction.execute(ActionMapping, ActionForm, HttpServletRequest, HttpServletResponse)) && args(mapping, form, request, response);
	
	ActionForward around(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	: getSprintBurndownChartDataActionPC(mapping, form, request, response) {
		System.out.println("replaced by AOP...getSprintBurndownChartDataActionPC: " + thisJoinPoint);
		
		String result  = 
			"{" +
				"\"Points\":[]," +
				"\"success\":true" +
			"}";
		
		try {
			response.setContentType("text/html; charset=utf-8");
			response.getWriter().write(result);
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	// for User
	// replace: public ActionForward AjaxGetTaskBoardStoryTaskListByGuest.execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) 
	pointcut AjaxGetTaskBoardStoryTaskListByGuestPC(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	: execution(ActionForward AjaxGetTaskBoardStoryTaskListByGuest.execute(ActionMapping, ActionForm, HttpServletRequest, HttpServletResponse)) && args(mapping, form, request, response);
	
	ActionForward around(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	: AjaxGetTaskBoardStoryTaskListByGuestPC(mapping, form, request, response) {
		System.out.println("replaced by AOP...AjaxGetTaskBoardStoryTaskListByGuestPC: " + thisJoinPoint);		
		
		try {
//			response.setContentType("text/html; charset=utf-8");
			response.getWriter().write("");
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}		
		
	/*
	 * Stapler & PlugIn relative
	 */
	
	// 有可能會寫入檔案,必須轉移至資料庫
	// replace: public void InitialPlugIn.init(ActionServlet servlet, ModuleConfig config) throws ServletException
	pointcut InitialPlugInPC(ActionServlet servlet, ModuleConfig config) 
	: execution(void InitialPlugIn.init(ActionServlet, ModuleConfig)) && args(servlet, config);

	void around(ActionServlet servlet, ModuleConfig config) throws ServletException
	: InitialPlugInPC(servlet, config) {
		System.out.println("replaced by AOP...InitialPlugInPC: " + thisJoinPoint);
		
		System.out.println("*************Plugin Initail***********");
		proceed(servlet, config);
		System.out.println("*************Plugin Initail END***********");		
	}	
	
	// Stapler NG 原因是它做了寫入檔案的動作
	
	pointcut allEzScrumRootPC() 
	: execution(public void ntut.csie.ezScrum.stapler.EzScrumRoot..*(..));

	void around () 
	: allEzScrumRootPC() {
		System.out.println("replaced by AOP...: <<Stapler>> allEzScrumRootPC" + thisJoinPoint);
	}	
	
	pointcut EzScrumDefaultPluginPC() 
	: execution(ntut.csie.ezScrum.stapler.EzScrumDefaultPlugin.new());

	void around () 
	: EzScrumDefaultPluginPC() {
		System.out.println("replaced by AOP...: <<Stapler>> EzScrumDefaultPluginPC" + thisJoinPoint);
	}	
	
	
    // replace: public String getToolbarPluginStringList()
	pointcut getToolbarPluginStringListPC() 
	: execution(String ntut.csie.ezScrum.stapler.ProductBacklog.getToolbarPluginStringList());

	String around () 
	: getToolbarPluginStringListPC() {
		System.out.println("replaced by AOP...: <<Plugin>> getToolbarPluginStringListPC" + thisJoinPoint);
		return "";
	}
	
    // replace: public String getToolbarPluginStringList()
	pointcut getBoardPluginPC() 
	: execution(String ntut.csie.ezScrum.stapler.TaskBoard.getBoardPlugin());

	String around () 
	: getBoardPluginPC() {
		System.out.println("replaced by AOP...: <<Plugin>> getBoardPluginPC" + thisJoinPoint);
		return "";
	}	

	// TaskBoard 尚未重構 fix later
	
	// replace: constructor of TaskBoard.new()
	pointcut NewTaskBoardPC(SprintBacklogLogic sprintBacklogLogic, SprintBacklogMapper sprintBacklogMapper)
	: execution(TaskBoard.new(SprintBacklogLogic, SprintBacklogMapper)) && args(sprintBacklogLogic, sprintBacklogMapper);

	void around(SprintBacklogLogic sprintBacklogLogic, SprintBacklogMapper sprintBacklogMapper)
	: NewTaskBoardPC(sprintBacklogLogic, sprintBacklogMapper) {
		System.out.println("replaced by AOP...NewTaskBoardPC: " + thisJoinPoint);	
	}	
	
	// View Project call it
	// replace: public int getSprintID()
	pointcut TaskBoard_getSprintID_PC() 
	: execution(public int TaskBoard.getSprintID());

	int around () 
	: TaskBoard_getSprintID_PC() {
		System.out.println("replaced by AOP...: <<TaskBoard>> TaskBoard_getSprintID_PC" + thisJoinPoint);
		return 0;
	}	
	
	
}