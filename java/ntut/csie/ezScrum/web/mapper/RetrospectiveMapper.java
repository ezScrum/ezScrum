package ntut.csie.ezScrum.web.mapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.IIssueNote;
import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.issue.internal.Issue;
import ntut.csie.ezScrum.issue.internal.IssueNote;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IITSService;
import ntut.csie.ezScrum.issue.sql.service.core.ITSServiceFactory;
import ntut.csie.ezScrum.iteration.core.IScrumIssue;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.iteration.iternal.ScrumIssue;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.core.util.XmlFileUtil;

import org.jdom.Element;

public class RetrospectiveMapper {
	private ProjectObject mProject;
	private ITSServiceFactory mITSFactory;
	private Configuration mConfiguration;
	private IUserSession mUserSession;	
	
	public RetrospectiveMapper(ProjectObject project, IUserSession userSession) {
		mProject = project;
		mUserSession = userSession;
		// 初始ITS的設定
		mITSFactory = ITSServiceFactory.getInstance();
		mConfiguration = new Configuration(mUserSession);
	}
	
	// from helper: addIssue
	public long add(String name, String description, String sprintID, String type) {		
		IITSService itsService = mITSFactory.getService(ITSEnum.MANTIS_SERVICE_ID, mConfiguration);
		itsService.openConnect();
		IIssue issue = new Issue();

		issue.setProjectID(mProject.getName());
		issue.setSummary(name);
		issue.setDescription(description);
		issue.setCategory(type);

		long issueID = itsService.newIssue(issue);

		issue = itsService.getIssue(issueID);
		itsService.closeConnect();

		createNote(issue, sprintID);			
		return issue.getIssueID();
	}
	
	// from helper: getIssue
	public IIssue getById(long id) {
		IITSService itsService = mITSFactory.getService(ITSEnum.MANTIS_SERVICE_ID, mConfiguration);
		itsService.openConnect();
		IIssue issue = itsService.getIssue(id);
		itsService.closeConnect();

		if (issue.getCategory().equals(ScrumEnum.GOOD_ISSUE_TYPE) || issue.getCategory().equals(ScrumEnum.IMPROVEMENTS_ISSUE_TYPE)||issue.getCategory().equals(ScrumEnum.UNPLANNEDITEM_ISSUE_TYPE))
			return issue;

		return null;
	}	

	// from helper: getRetrospectiveList
	public List<IScrumIssue> getList(String type) throws SQLException {
		IITSService itsService = mITSFactory.getService(ITSEnum.MANTIS_SERVICE_ID, mConfiguration);
		itsService.openConnect();
		IIssue[] issues = itsService.getIssues(mProject.getName(), type);
		List<IScrumIssue> list = new ArrayList<IScrumIssue>();

		for (IIssue issue : issues) {
			list.add(new ScrumIssue(issue));
		}

		itsService.closeConnect();
		return list;	
	}		
	
	// from helper: modifyContent
	public void update(long issueID, String name, String description, String sprintID, String type, String status) {
		IIssue issue = this.getById(issueID);

		issue.setSummary(name);
		issue.setDescription(description);
		if(type!=null)
		issue.setCategory(type);
		if(status!=null)
		issue.setStatus(status);

		IITSService itsService = mITSFactory.getService(ITSEnum.MANTIS_SERVICE_ID, mConfiguration);
		itsService.openConnect();

		itsService.updateIssueContent(issue);

		itsService.closeConnect();

		createNote(issue, sprintID);
	}

	// from helper: delete
	public void delete(String issueID) {
		IITSService itsService = mITSFactory.getService(ITSEnum.MANTIS_SERVICE_ID, mConfiguration);
		
		itsService.openConnect();
		itsService.removeIssue(issueID);
		itsService.closeConnect();		
	}		

	private void createNote(IIssue issue, String sprintID) {

		String lastSprintID = issue.getTagValue(ScrumEnum.SPRINT_ID);
		if (lastSprintID != null && lastSprintID.equals(sprintID))
			return;

		Element history = new Element(ScrumEnum.HISTORY_TAG);
		history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, DateUtil.format(new Date(), DateUtil._16DIGIT_DATE_TIME_2));

		// iteration node
		Element iteration = new Element(ScrumEnum.SPRINT_ID);
		iteration.setText(sprintID);
		history.addContent(iteration);

		issue.addTagValue(history);

		// ===============記錄至db中=============

		String updateContext = XmlFileUtil.getXmlString(issue.getTagContentRoot().getChildren());

		List<IIssueNote> notes = issue.getIssueNotes();

		IIssueNote note = null;
		for (IIssueNote dbNote : notes) {
			if (dbNote.getText().contains("<JCIS") && !dbNote.getText().contains("<JCIS:")) {
				dbNote.setText(updateContext);
				dbNote.setModifiedDate(new Date().getTime());
				note = dbNote;
				break;
			}
		}

		if (note == null) {
			note = new IssueNote();
			note.setText(updateContext);
		}

		// 最後將修改的結果更新至DB
		updateTagValue(issue, note);
	}	
	
	private void updateTagValue(IIssue issue, IIssueNote note) {
		IITSService itsService = mITSFactory.getService(ITSEnum.MANTIS_SERVICE_ID, mConfiguration);
		itsService.openConnect();
		itsService.updateIssueNote(issue, note);
		itsService.closeConnect();
	}
	
}
