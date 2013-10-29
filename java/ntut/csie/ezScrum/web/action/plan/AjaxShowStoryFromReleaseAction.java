package ntut.csie.ezScrum.web.action.plan;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.control.ProductBacklogHelper;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AjaxShowStoryFromReleaseAction extends PermissionAction {
	private static Log log = LogFactory.getLog(AjaxShowStoryFromReleaseAction.class);
	
	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessReleasePlan();
	}

	@Override
	public boolean isXML() {
		// XML
		return true;
	}
	
	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		// get session info
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
	
		ReleasePlanHelper planHelper = new ReleasePlanHelper(project);
		ProductBacklogHelper productHelper = new ProductBacklogHelper(project, session);
		
		// get parameter info
		String R_ID = request.getParameter("Rid");
		
		//取得ReleasePlan
		IReleasePlanDesc plan = planHelper.getReleasePlan(R_ID);
		
		IStory[] storyList = productHelper.getStoriesByRelease(plan);
		return planHelper.showStoryFromReleae(project, R_ID, storyList);
		
//		ReleaseBacklog releaseBacklog;
//		try {
//			releaseBacklog = new ReleaseBacklog(project, plan, 
//					productHelper.getStoriesByRelease(plan));
//		}
//		catch (Exception e) {
//			// TODO: handle exception
//			releaseBacklog = null;
//		}	
				
//		if (R_ID != null) {			
//			IIssue[] stories = releaseBacklog.getStory();
//			stories = sortStory(stories);
//			
//			// write stories to XML format
//			StringBuilder sb = new StringBuilder();
//			sb.append("<ExistingStories>");			
//			for(int i = 0; i < stories.length; i++)
//			{
//				String releaseId = stories[i].getReleaseID();
//				if(releaseId.equals("") || releaseId.equals("0") || releaseId.equals("-1"))
//					releaseId = "None";
//				
//				String sprintId = stories[i].getSprintID();
//				if(sprintId.equals("") || sprintId.equals("0") || sprintId.equals("-1"))
//					sprintId = "None";
//				sb.append("<Story>");
//				sb.append("<Id>" + stories[i].getIssueID() + "</Id>");
//				sb.append("<Link>" + replaceStr(stories[i].getIssueLink()) + "</Link>");
//				sb.append("<Name>" + replaceStr(stories[i].getSummary())+ "</Name>");
//				sb.append("<Value>" + stories[i].getValue()+"</Value>");
//				sb.append("<Importance>" + stories[i].getImportance() + "</Importance>");
//				sb.append("<Estimation>" + stories[i].getEstimated() + "</Estimation>");
//				sb.append("<Status>" + stories[i].getStatus() + "</Status>");
//				sb.append("<Notes>" + replaceStr(stories[i].getNotes()) + "</Notes>");
//				sb.append("<HowToDemo>" + replaceStr(stories[i].getHowToDemo()) + "</HowToDemo>");
//				sb.append("<Release>" + releaseId + "</Release>");
//				sb.append("<Sprint>" + sprintId + "</Sprint>");
//				sb.append("<Tag>" + replaceStr(Join(stories[i].getTag(), ",")) + "</Tag>");
//				sb.append("</Story>");
//			}
//			sb.append("</ExistingStories>");
//			
//			return sb;
//		} else {
//			return null;
//		}
		
	}
	
//	// sort story information by importance
//	private IIssue[] sortStory(IIssue[] issues) {
//		List<IIssue> list = new ArrayList();
//	
//		for (IIssue issue : issues) {
//			int index = 0;
//			for (index=0 ; index<list.size() ; index++) {
//				if ( Integer.parseInt(issue.getImportance()) > Integer.parseInt(list.get(index).getImportance()) ) {
//					break;
//				}
//			}
//			list.add(index, issue);
//		}
//	
//		return list.toArray(new IIssue[list.size()]);
//	}
//	public String Join(List<IIssueTag> tags, String delimiter)
//	{
//	    if (tags.isEmpty())
//		return "";
//	 
//	    StringBuilder sb = new StringBuilder();
//	 
//	    for (IIssueTag x : tags)
//	    	sb.append(x.getTagName() + delimiter);
//	 
//	    sb.delete(sb.length()-delimiter.length(), sb.length());
//	 
//	    return sb.toString();
//	}
//
//	private String replaceStr(String str) {
//		if (str != null) {
//			if (str.contains("&")) {
//				str = str.replaceAll("&", "&amp;");
//			}
//			
//			if (str.contains("\"")) {
//				str = str.replaceAll("\"", "&quot;");
//			}
//			
//			if (str.contains("<")) {
//				str = str.replaceAll("<", "&lt;");
//			}
//			
//			if (str.contains(">")) {
//				str = str.replaceAll(">", "&gt;");
//			}
//		}
//		
//		return str;
//	}
}
