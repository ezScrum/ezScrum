package ntut.csie.ezScrum.iteration.iternal;

import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.issue.sql.service.core.IITSService;
import ntut.csie.ezScrum.issue.sql.service.core.ITSPrefsStorage;
import ntut.csie.ezScrum.issue.sql.service.core.ITSServiceFactory;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.jcis.resource.core.IProject;

public class MantisProjectManager {
	private IProject m_project;
	private IUserSession m_userSession;
	
	private ITSServiceFactory m_itsFactory;
	private ITSPrefsStorage m_itsPrefs;
	
	public MantisProjectManager(IProject project, IUserSession userSession) {
		m_project = project;
		m_userSession = userSession;
		
		//初始ITS的設定
		m_itsFactory = ITSServiceFactory.getInstance();
		m_itsPrefs = new ITSPrefsStorage(m_project, m_userSession);		
	}
	
	public void CreateProject(String ProjectName) throws Exception {
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID,m_itsPrefs);
		itsService.openConnect();
		
		try {
			itsService.createProject(ProjectName);			
		} catch (Exception e) {
			throw e;
		}
		
		itsService.closeConnect();
		
		/* ezKanban所使用
		EzTrackService etsService = m_itsFactory.getEzTracService(m_itsPrefs);
		etsService.openConnect();
		// 建立 ezKanban 所使用的 IssueType (params: ProjectName, IssueType, IsPublic, isKanban)
		etsService.createDefaultIssueTypeData(ProjectName, KanbanEnum.ISSUETYPE_WORKITEM, 0, 1);
		etsService.createDefaultIssueTypeData(ProjectName, KanbanEnum.ISSUETYPE_STATUS, 0, 1);
		// 建立 Workitem 欄位
		int typeID = etsService.getIssueTypeIDByName(ProjectName, KanbanEnum.ISSUETYPE_WORKITEM);
		etsService.createDefaultIssueFieldData(typeID, KanbanEnum.FIELD_WORKITEM_STATUS, IssueTypeField.Category.Number.toString());
		etsService.createDefaultIssueFieldData(typeID, KanbanEnum.FIELD_WORKITEM_TYPE, IssueTypeField.Category.Combo.toString());
		etsService.createDefaultIssueFieldData(typeID, KanbanEnum.FIELD_WORKITEM_PRIORITY, IssueTypeField.Category.Combo.toString());
		etsService.createDefaultIssueFieldData(typeID, KanbanEnum.FIELD_WORKITEM_WORKSTATE, IssueTypeField.Category.Combo.toString());
		etsService.createDefaultIssueFieldData(typeID, KanbanEnum.FIELD_WORKITEM_SIZE, IssueTypeField.Category.Number.toString());
		etsService.createDefaultIssueFieldData(typeID, KanbanEnum.FIELD_WORKITEM_DEADLINE, IssueTypeField.Category.Date.toString());
		etsService.createDefaultIssueFieldData(typeID, KanbanEnum.FIELD_WORKITEM_HANDLER, IssueTypeField.Category.Textbox.toString());
		// 找出 Priority 欄位的 ID
		int priorityFieldID = 0;
		int typeFieldID = 0;
		int workStateFieldID = 0;
		List<IssueTypeField> fields = etsService.getIssueTypeFields(ProjectName, typeID);
		for (IssueTypeField field : fields){
			if (field.getFieldName().equals(KanbanEnum.FIELD_WORKITEM_PRIORITY) && 
					field.getFieldCategory().equals(IssueTypeField.Category.Combo)){
				priorityFieldID = field.getFieldID();
			}
			if (field.getFieldName().equals(KanbanEnum.FIELD_WORKITEM_TYPE) && 
					field.getFieldCategory().equals(IssueTypeField.Category.Combo)){
				typeFieldID = field.getFieldID();
			}
			if (field.getFieldName().equals(KanbanEnum.FIELD_WORKITEM_WORKSTATE) && 
					field.getFieldCategory().equals(IssueTypeField.Category.Combo)){
				workStateFieldID = field.getFieldID();
			}
		}
		// 建立 Priority Combox 的選項
		etsService.createDefaultComboFieldData(priorityFieldID, KanbanEnum.Priority.High.toString());
		etsService.createDefaultComboFieldData(priorityFieldID, KanbanEnum.Priority.Medium.toString());
		etsService.createDefaultComboFieldData(priorityFieldID, KanbanEnum.Priority.Low.toString());
		// 建立 Type Combox 的選項
		etsService.createDefaultComboFieldData(typeFieldID, KanbanEnum.Type.UserStory.toString());
		etsService.createDefaultComboFieldData(typeFieldID, KanbanEnum.Type.Task.toString());
		// 建立 WorkState Combox 的選項
		etsService.createDefaultComboFieldData(workStateFieldID, KanbanEnum.WorkState.New.toString());
		etsService.createDefaultComboFieldData(workStateFieldID, KanbanEnum.WorkState.Assigned.toString());
		etsService.createDefaultComboFieldData(workStateFieldID, KanbanEnum.WorkState.Closed.toString());
		etsService.createDefaultComboFieldData(workStateFieldID, KanbanEnum.WorkState.Blocked.toString());
		
		// 建立 Status 欄位
		typeID = etsService.getIssueTypeIDByName(ProjectName, KanbanEnum.ISSUETYPE_STATUS);
		etsService.createDefaultIssueFieldData(typeID, KanbanEnum.FIELD_STATUS_LIMIT, IssueTypeField.Category.Number.toString());
		IssueTypeField limitField = etsService.getIssueTypeFields(ProjectName, typeID).get(0);
		// 建立 Workitem Status : Backlog + Live
		IIssue issue = new Issue();
		issue.setProjectID(ProjectName);
		limitField.setFieldValue("");
		issue.addField(limitField);
		// Backlog
		issue.setSummary(KanbanEnum.WORKITEM_BACKLOG_STATUS);
		etsService.newIssue(typeID, issue);
		// Live
		issue.setSummary(KanbanEnum.WORKITEM_LIVE_STATUS);
		etsService.newIssue(typeID, issue);
		
		etsService.closeConnect(); */
		
	}
}