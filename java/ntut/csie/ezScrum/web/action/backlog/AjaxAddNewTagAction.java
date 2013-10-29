package ntut.csie.ezScrum.web.action.backlog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.helper.ProductBacklogHelper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class AjaxAddNewTagAction extends PermissionAction {
	private static Log log = LogFactory.getLog(AjaxAddNewTagAction.class);
	
	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessProductBacklog();
	}

	@Override
	public boolean isXML() {
		// XML
		return true;
	}
	
	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		log.info("Add New Tag in AjaxAddNewTagAction.");
		// get session info 
		IProject project = (IProject) SessionManager.getProject(request);
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		
		// get parameter info
		String newTagName = request.getParameter("newTagName");
		
		StringBuilder result = (new ProductBacklogHelper(session, project)).getAddNewTagResponsetext(newTagName);
		return result;
		
//		ProductBacklogHelper PBHelper = new ProductBacklogHelper(project, session);
//		String original_tagname = NewTagName;
//		
//		NewTagName = new TranslateSpecialChar().TranslateDBChar(NewTagName);
//		
//		StringBuilder result = new StringBuilder("");
//		//先將"\","'"轉換, 判斷DB裡是否存在
//		if(NewTagName.contains(",")) {
//			result = new StringBuilder("<Tags><Result>false</Result><Message>TagName: \",\" is not allowed</Message></Tags>");
//		} else if(PBHelper.isTagExist(NewTagName)) {
//			//轉換"&", "<", ">", """, 通過XML語法
//			//因為"\","'"對xml沒影響, 所以使用original(未轉換)
//			NewTagName = new TranslateSpecialChar().TranslateXMLChar(original_tagname); 
//			result = new StringBuilder("<Tags><Result>false</Result><Message>Tag Name : " + NewTagName + " already exist</Message></Tags>");
//		} else {
//			PBHelper.addNewTag(NewTagName);
//			
//			IIssueTag tag = PBHelper.getTagByName(NewTagName);
//			
//			result.append("<Tags><Result>true</Result>");
//			result.append("<IssueTag>");
//			result.append("<Id>" + tag.getTagId() + "</Id>");
//			result.append("<Name>" + new TranslateSpecialChar().TranslateXMLChar(tag.getTagName()) + "</Name>");
//			result.append("</IssueTag>");
//			result.append("</Tags>");
//		}
//
//		return result;
	}
}
