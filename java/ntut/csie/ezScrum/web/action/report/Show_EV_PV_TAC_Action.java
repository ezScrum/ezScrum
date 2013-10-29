package ntut.csie.ezScrum.web.action.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.control.PerformanceIndexDataMaker;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.support.Translation;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class Show_EV_PV_TAC_Action extends PermissionAction{
	
	// SC/SP and BV setting
	private double BC_SP_Value = 0;
	private double BV_Value = 0;
	
	@Override
	public boolean isValidAction() {
		return true; //TODO
	}

	@Override
	public boolean isXML() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		// get session info
		
		IProject project = (IProject) request.getSession().getAttribute("Project");
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		String bc_sp = project.getProjectDesc().getBC_SP();
		String bv = project.getProjectDesc().getBV();
		
		//假如BC/SP或BV其中有一個資料是null或是空字串則不畫出圖型
		if( ( bc_sp == null || bc_sp.isEmpty() ) || ( bv == null || bv.isEmpty() ) ){
			return new StringBuilder("");
		}
		
		//get BC/SP and BV from project
		BC_SP_Value = Double.parseDouble( project.getProjectDesc().getBC_SP() );
		BV_Value = Double.valueOf( project.getProjectDesc().getBV() );
		
		SprintPlanHelper sprintPlanHelper  = new SprintPlanHelper( project );
		List<ISprintPlanDesc> sprintPlanArray = sprintPlanHelper.loadListPlans();
		
//		List<SprintBacklogMapper> sprintBacklogArrayList = new ArrayList<SprintBacklogMapper>();
//		
//		for( ISprintPlanDesc sprintPlan : sprintPlanArray ){
//			SprintBacklogMapper tempSprintBacklog = new SprintBacklogMapper( project, session, Integer.parseInt( sprintPlan.getID() ) );
//			sprintBacklogArrayList.add( tempSprintBacklog );
//		}
//		
//		PerformanceIndexDataMaker performanceIndexDataMaker = new PerformanceIndexDataMaker(sprintPlanArray,sprintBacklogArrayList);
		
		List<SprintBacklogLogic> sprintBacklogLogicArrayList = new ArrayList<SprintBacklogLogic>();
		
		for( ISprintPlanDesc sprintPlan : sprintPlanArray ){
			SprintBacklogLogic tempSprintBacklogLogic = new SprintBacklogLogic( project, session, sprintPlan.getID() );
			sprintBacklogLogicArrayList.add( tempSprintBacklogLogic );
		}
		
		PerformanceIndexDataMaker performanceIndexDataMaker = new PerformanceIndexDataMaker(sprintPlanArray,sprintBacklogLogicArrayList);
		List<Map.Entry<Integer,Double>> evTupleList = performanceIndexDataMaker.getEarnedValueTupleList(BC_SP_Value);
		List<Map.Entry<Integer,Double>> pviTupleList = performanceIndexDataMaker.getPlanValueTupleList( BC_SP_Value, BV_Value);
		List<Map.Entry<Integer,Double>> tacTupleList = performanceIndexDataMaker.getTotalActualCostTupleList();
		
		Translation tt = new Translation();
		String jsonData = tt.translateEV_PV_TAC_DataToJson(evTupleList, pviTupleList, tacTupleList);
		
		StringBuilder sb = new StringBuilder(jsonData);
		
		return sb;
	}

}
