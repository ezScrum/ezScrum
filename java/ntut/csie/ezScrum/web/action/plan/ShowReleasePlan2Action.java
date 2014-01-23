package ntut.csie.ezScrum.web.action.plan;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class ShowReleasePlan2Action extends PermissionAction {
	private static Log log = LogFactory.getLog(ShowReleasePlan2Action.class);
	
	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessReleasePlan();
	}

	@Override
	public boolean isXML() {
		// html
		return false;
	}
	
	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		// get session info
		IProject project = (IProject) SessionManager.getProject(request);
//		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
    	
		ReleasePlanHelper RPhelper = new ReleasePlanHelper(project);
		SprintPlanHelper SPhelper = new SprintPlanHelper(project);
		
		List<IReleasePlanDesc> releaseDescs = RPhelper.loadReleasePlansList();
		List<IReleasePlanDesc> ListReleaseDescs = RPhelper.sortStartDate(releaseDescs);
	
    	StringBuilder result = new StringBuilder(RPhelper.setJSon(ListReleaseDescs, SPhelper));
    	
		return result;
	}	
	
//	private List<IReleasePlanDesc> sortStartDate(List<IReleasePlanDesc> releaseDescs){
//		List<IReleasePlanDesc> ListReleaseDescs = new ArrayList<IReleasePlanDesc>();
//		// ListReleaseDescs 依照 StartDate 排序
//		for (IReleasePlanDesc desc : releaseDescs) {
//			Date addDate = DateUtil.dayFilter(desc.getStartDate());			// 要新增的 Date
//			
//			if (ListReleaseDescs.size() > 0) {
//				int index = 0;
//				for (index=0 ; index<ListReleaseDescs.size() ; index++) {
//					IReleasePlanDesc Desc = ListReleaseDescs.get(index);		// 目前要被比對的 relase
//					Date cmpDate = DateUtil.dayFilter(Desc.getStartDate());		// 要被比對的 Date
//					if ( addDate.compareTo(cmpDate) < 0 ) {
//						break;
//					}
//				}
//				ListReleaseDescs.add(index, desc);
//			} else {
//				ListReleaseDescs.add(desc);
//			}
//		}
//		return ListReleaseDescs;
//	}
	
//	private String setJSon(List<IReleasePlanDesc> ListReleaseDescs, SprintPlanHelper SPhelper){
//		TranslateSpecialChar tsc = new TranslateSpecialChar();
//		
//		String tree = "";
//		tree += "[";
//		int i = 0;
//		for(IReleasePlanDesc des: ListReleaseDescs){
//			if(i==0)
//				tree+="{";
//			else
//				tree+=",{";
//			tree+="Type:\'Release\',";
//			tree+="ID:\'"+des.getID()+"\',";
//			tree+="Name:\'"+tsc.TranslateJSONChar(des.getName())+"\',";
//			tree+="StartDate:\'"+des.getStartDate()+"\',";
//			tree+="EndDate:\'"+des.getEndDate()+"\',";
//			tree+="Description:\'"+ tsc.TranslateJSONChar(des.getDescription()) +"\',";
//			
//			if(des.getSprintDescList()!=null&&des.getSprintDescList().size()!=0){
//				tree+= "expanded: true,";
//				tree+="iconCls:\'task-folder\',";
//
//				tree+= "children:[";
//				tree+= setSprintToJSon(des, SPhelper);
//				tree+= "]";
//			}
//			else{
//				tree+="leaf: true";
//			}
//			tree+= "}";
//			i++;
//		}
//		//[{task:\'Project: Shopping\',duration:13.25,user:\'Tommy Maintz\',leaf: true}]
//	
//		tree += "]";
//		return tree;
//	}
	
//	//透過release des將sprint的資訊寫成JSon
//	private String setSprintToJSon (IReleasePlanDesc IRDesc, SprintPlanHelper SPhelper){
//		TranslateSpecialChar tsc = new TranslateSpecialChar();
//		
//		String sprintTree="";
//		if (IRDesc.getSprintDescList() != null) {				// 有 sprint 資訊，則抓取 sprint 的 xml 資料
//			int i=0;
//			// 將資訊設定成 JSon 輸出格式
//			for (ISprintPlanDesc desc : IRDesc.getSprintDescList()) {
//				if(i==0)
//					sprintTree+="{";
//				else
//					sprintTree+=",{";
//				sprintTree+="Type:\'Sprint\',";
//				sprintTree+="ID:\'"+desc.getID()+"\',";
//				sprintTree+="Name:\'"+tsc.TranslateJSONChar(desc.getGoal())+"\',";
//				sprintTree+="StartDate:\'"+desc.getStartDate()+"\',";
//				sprintTree+="EndDate:\'"+desc.getEndDate()+"\',";
//				sprintTree+="Interval:\'"+desc.getInterval()+"\',";
//				sprintTree+="Description:\' \',";
//				sprintTree+="iconCls:\'task\',";
//				sprintTree+="leaf: true";
//				sprintTree+= "}";
//				i++;
//			}
//		}
//		return sprintTree;
//	}
	
}