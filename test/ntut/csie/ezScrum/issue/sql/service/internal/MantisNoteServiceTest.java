package ntut.csie.ezScrum.issue.sql.service.internal;

import java.sql.ResultSet;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.IIssueNote;
import ntut.csie.ezScrum.issue.internal.Issue;
import ntut.csie.ezScrum.issue.internal.IssueNote;
import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.core.ITSPrefsStorage;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.test.CreateData.CopyProject;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.ezScrumInfoConfig;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

import org.jdom.Element;

public class MantisNoteServiceTest extends TestCase {
	private ITSPrefsStorage prefs;
	
	private CreateProject CP;
	private CreateProductBacklog CPB;
	private int ProjectCount = 1;
	private int StoryCount = 10;
	private ezScrumInfoConfig config = new ezScrumInfoConfig();
	
	private MantisService MSservice;
	private MantisIssueService MISservice;
	private MantisNoteService MNService;
	TextParserGeneraterForNote noteTextHelper;
	
	public MantisNoteServiceTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() throws Exception {
		InitialSQL ini = new InitialSQL(config);
		ini.exe();											// 初始化 SQL
		
		// 新增Project
		this.CP = new CreateProject(this.ProjectCount);
		this.CP.exeCreate();
		
		// 建立MantisTagService
		IProject project = this.CP.getProjectList().get(0);
		prefs = new ITSPrefsStorage(project, config.getUserSession());
		this.MSservice = new MantisService(prefs);
		MNService = new MantisNoteService(MSservice.getControl(), prefs);
		MISservice = new MantisIssueService(MSservice.getControl(), prefs);
		
		super.setUp();
		
		// ============= release ==============
		ini = null;
		project = null;
	}
	
	protected void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(config);
		ini.exe();											// 初始化 SQL

		CopyProject copyProject = new CopyProject(this.CP);
    	copyProject.exeDelete_Project();					// 刪除測試檔案
    	
    	// ============= release ==============
    	ini = null;
    	copyProject = null;
    	this.CP = null;
    	this.CPB = null;
    	this.config = null;
    	this.MNService = null;
    	
    	super.tearDown();
	}
	
	public void testGetIssueNotes(){
		this.CPB = new CreateProductBacklog(this.StoryCount, this.CP);
		this.CPB.exe();
		
		// get the issues by creating data
		List<IIssue> issueList = this.CPB.getIssueList();
		this.MSservice.openConnect();

		int index = 0;
		int imp = 200, est = 21, value = 300;
		// override the note info.
		for(IIssue issue : issueList){
			this.addTagElement(issue, Integer.toString(imp + index), Integer.toString(est + index), 
									  Integer.toString(value + index), "demo_"+Integer.toString(index+1),
									  "note_"+Integer.toString(index+1));
			index++;
		}
			
		//assert the issueNote info.
		index = 0;
		for(IIssue issue : issueList){
			// test method
			List<IIssueNote> notes = MNService.getIssueNotes(issue);
			// test method
			for (IIssueNote note : notes) {
				noteTextHelper = new TextParserGeneraterForNote();
				noteTextHelper.parserNoteText(note.getText());
				assertEquals(index+1, note.getIssueID());// 1, 2 .. 
				assertEquals(index+1, note.getNoteID());// 1, 2 ..
				assertEquals("administrator", note.getHandler());// default = 1 (administrator)
				assertEquals(Integer.toString(imp + index), noteTextHelper.getImportance());// 200, 201, 202 ..
				assertEquals(Integer.toString(est + index), noteTextHelper.getEstimation());// 21, 22 , 23 ..
				assertEquals(Integer.toString(value + index), noteTextHelper.getValue());// 300, 301, 302 ..
				assertEquals("demo_" + Integer.toString(index+1), noteTextHelper.getHowToDemo());// demo_1, 2 ..
				assertEquals("note_" + Integer.toString(index+1), noteTextHelper.getNotes());// note_1, 2 ..
			}
			index++;
		}
		// close connection
		this.MSservice.closeConnect();
	}
	
	public void testUpdateBugNote(){
		IProject project = this.CP.getProjectList().get(0);
		List<IIssue> issueList = new LinkedList<IIssue>();
		List<IIssueNote> noteList;
		this.MSservice.openConnect();
		// new 10 issues
		for (int index = 0 ; index < 10 ; index++) {
			IIssue story = new Issue();
			story.setIssueID(index+1);
			story.setSummary("Story_Name_" + Integer.toString(index+1));
			story.setDescription("Story_Desc_" + Integer.toString(index+1));

			story.setProjectID(project.getName());
			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
			
			this.MISservice.newIssue(story);
			issueList.add(story);

			// check the issue's note is null
			noteList = this.MNService.getIssueNotes(issueList.get(index));
			assertEquals(0, noteList.size());
		}
		
		// Test if there is no existing tag in bug note initially, 
		// "insertBugNote" would be called in function "updateBugNote".
		int imp = 201, est = 21, val = 251;
		for(int index = 0; index < issueList.size(); index++){
			String importance = Integer.toString(imp + index);
			String estimation = Integer.toString(est + index);
			String value = Integer.toString(val + index);
			String howToDemo = "demo_"+Integer.toString(index+1);
			String notes = "note_"+Integer.toString(index+1);
			
			// test method : "updateBugNote", which would be called in function "addTagElement".
			this.addTagElement(issueList.get(index), importance, estimation, value,	howToDemo, notes);

			// check the issue note info.
			noteList = this.MNService.getIssueNotes(issueList.get(index));
			assertEquals(1, noteList.size());
			for (IIssueNote note : noteList) {
				noteTextHelper = new TextParserGeneraterForNote();
				noteTextHelper.parserNoteText(note.getText());
				assertEquals(index+1, note.getIssueID());					// 1, 2 .. 
				assertEquals(index+1, note.getNoteID());					// 1, 2 ..
				assertEquals("administrator", note.getHandler());			// default = 1 (administrator)
				assertEquals(importance, noteTextHelper.getImportance());	// 201, 202, 203 ..
				assertEquals(estimation, noteTextHelper.getEstimation());	// 21, 22 , 23 ..
				assertEquals(value, noteTextHelper.getValue());				// 251, 252, 253 ..
				assertEquals(howToDemo, noteTextHelper.getHowToDemo());		// demo_1, 2 ..
				assertEquals(notes, noteTextHelper.getNotes());				// note_1, 2 ..
			}
		}
		// ==============================================================

		
		// Test if there are some tags already exist in bug note initially, 
		// then the "updateBugNote" would update the "JCIS tag" by "appending".
		imp = 301; 
		est = 31;
		val = 351;
		for(int index = 0; index < issueList.size(); index++){
			String importance = Integer.toString(imp + index);
			String estimation = Integer.toString(est + index);
			String value = Integer.toString(val + index);
			String howToDemo = "demo_"+Integer.toString(index+11);
			String notes = "note_"+Integer.toString(index+11);
			
			// test method : "updateBugNote", which would be called in function "addTagElement".
			this.addTagElement(issueList.get(index), importance, estimation, value,	howToDemo, notes);
			
			// check the issue note info.
			noteList = this.MNService.getIssueNotes(issueList.get(index));
			assertEquals(1, noteList.size());// list size still is 1, because the query result didn't separate the note tags 
			for (IIssueNote note : noteList) {
				noteTextHelper = new TextParserGeneraterForNote();
				noteTextHelper.parserNoteText(note.getText());
				assertEquals(index+1, note.getIssueID());					// 1, 2 .. 
				assertEquals(index+1, note.getNoteID());					// 1, 2 ..
				assertEquals("administrator", note.getHandler());			// default = 1 (administrator)
				assertEquals(importance, noteTextHelper.getImportance());	// 301, 302, 303 ..
				assertEquals(estimation, noteTextHelper.getEstimation());	// 31, 32 , 33 ..
				assertEquals(value, noteTextHelper.getValue());				// 351, 352, 353 ..
				assertEquals(howToDemo, noteTextHelper.getHowToDemo());		// demo_1, 2 ..
				assertEquals(notes, noteTextHelper.getNotes());				// note_1, 2 ..
			}
		}
		// close connection
		this.MSservice.closeConnect();
	}
	
	public void testUpdateIssueNote(){
		IProject project = this.CP.getProjectList().get(0);
		List<IIssue> issueList = new LinkedList<IIssue>();
		List<IIssueNote> noteList;
		this.MSservice.openConnect();
		// new 10 issues
		for (int index = 0 ; index < 10 ; index++) {
			IIssue story = new Issue();
			story.setIssueID(index+1);
			story.setSummary("Story_Name_" + Integer.toString(index+1));
			story.setDescription("Story_Desc_" + Integer.toString(index+1));

			story.setProjectID(project.getName());
			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
			
			this.MISservice.newIssue(story);
			issueList.add(story);

			// check the issue's note is null
			noteList = this.MNService.getIssueNotes(issueList.get(index));
			assertEquals(0, noteList.size());
		}
		
		// Test if there is no existing tag in bug note initially, 
		// "insertBugNote" would be called in function "updateIssueNote".
		int imp = 201, est = 21, val = 251;
		for(int index = 0; index < issueList.size(); index++){
			String importance = Integer.toString(imp + index);
			String estimation = Integer.toString(est + index);
			String value = Integer.toString(val + index);
			String howToDemo = "demo_"+Integer.toString(index+1);
			String notes = "note_"+Integer.toString(index+1);

			// generate note text
			noteTextHelper = new TextParserGeneraterForNote();
			String noteText = noteTextHelper.generaterNoteText(importance, estimation, value, howToDemo, notes);
			
			IIssueNote issueNote = new IssueNote();
			issueNote.setText(noteText);
			
			// test method
			this.MNService.updateIssueNote(issueList.get(index), issueNote);
			
			// set the issue's noteList
			noteList = this.MNService.getIssueNotes(issueList.get(index));
			issueList.get(index).setIssueNotes(noteList);
			
			// check the issue note info.
			noteList = this.MNService.getIssueNotes(issueList.get(index));
			assertEquals(1, noteList.size());
			for (IIssueNote note : noteList) {
				noteTextHelper = new TextParserGeneraterForNote();
				noteTextHelper.parserNoteText(note.getText());
				assertEquals(index+1, note.getIssueID());					// 1, 2 .. 
				assertEquals(index+1, note.getNoteID());					// 1, 2 ..
				assertEquals("administrator", note.getHandler());			// default = 1 (administrator)
				assertEquals(importance, noteTextHelper.getImportance());	// 201, 202, 203 ..
				assertEquals(estimation, noteTextHelper.getEstimation());	// 21, 22 , 23 ..
				assertEquals(value, noteTextHelper.getValue());				// 251, 252, 253 ..
				assertEquals(howToDemo, noteTextHelper.getHowToDemo());		// demo_1, 2 ..
				assertEquals(notes, noteTextHelper.getNotes());				// note_1, 2 ..
			}
		}
		// ==============================================================
		
		
		// Test if there are some tags already exist in bug note initially, 
		// then the "updateIssueNote" would update the "JCIS tag" by "overriding".
		imp = 301;
		est = 31;
		val = 351;
		int index = 0;
		for(IIssue issue : issueList){
			String importance = Integer.toString(imp + index);
			String estimation = Integer.toString(est + index);
			String value = Integer.toString(val + index);
			String howToDemo = "update_demo_"+Integer.toString(index+1);
			String notes = "update_note_"+Integer.toString(index+1);
			
			// generate note text
			noteTextHelper = new TextParserGeneraterForNote();
			String noteText = noteTextHelper.generaterNoteText(importance, estimation, value, howToDemo, notes);
			
			// new issueNote
			IIssueNote issueNote = new IssueNote();
			issueNote.setIssueID(issueList.get(index).getIssueID());
			issueNote.setText(noteText);
			issueNote.setHandler("");
			issueNote.setNoteID(issueList.get(index).getIssueID());
			
			// add new issueNote to issue 
			issue.addIssueNote(issueNote);

			// test method
			this.MNService.updateIssueNote(issue, issueNote);
			
			// check the issue note info.
			noteList = this.MNService.getIssueNotes(issueList.get(index));
			assertEquals(1, noteList.size());
			for (IIssueNote note : noteList) {
				noteTextHelper = new TextParserGeneraterForNote();
				noteTextHelper.parserNoteText(note.getText());
				assertEquals(index+1, note.getIssueID());					// 1, 2 .. 
				assertEquals(index+1, note.getNoteID());					// 1, 2 ..
				assertEquals("administrator", note.getHandler());			// default = 1 (administrator)
				assertEquals(importance, noteTextHelper.getImportance());	// 301, 302, 303 ..
				assertEquals(estimation, noteTextHelper.getEstimation());	// 31, 32, 33 ..
				assertEquals(value, noteTextHelper.getValue());				// 351, 352, 353 ..
				assertEquals(howToDemo, noteTextHelper.getHowToDemo());		// update_note_1, 2 ..
				assertEquals(notes, noteTextHelper.getNotes());				// update_note_1, 2 ..
			}
			
			index++;
		}
		// close connection
		this.MSservice.closeConnect();
	}
	
	public void testInsertBugNote(){
		IProject project = this.CP.getProjectList().get(0);
		List<IIssue> issueList = new LinkedList<IIssue>();
		List<IIssueNote> noteList;
		this.MSservice.openConnect();
		// new 10 issues
		for (int index = 0 ; index < 10 ; index++) {
			IIssue story = new Issue();
			story.setIssueID(index+1);
			story.setSummary("Story_Name_" + Integer.toString(index+1));
			story.setDescription("Story_Desc_" + Integer.toString(index+1));

			story.setProjectID(project.getName());
			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
			
			this.MISservice.newIssue(story);
			issueList.add(story);

			// check the issue's note is null
			noteList = this.MNService.getIssueNotes(issueList.get(index));
			assertEquals(0, noteList.size());
		}
		
		// Test insertBugNote, insert new bug note into table 
		int imp = 201, est = 21, val = 251;
		for(int index = 0; index < issueList.size(); index++){
			String importance = Integer.toString(imp + index);
			String estimation = Integer.toString(est + index);
			String value = Integer.toString(val + index);
			String howToDemo = "demo_"+Integer.toString(index+1);
			String notes = "note_"+Integer.toString(index+1);
			
			// generate note text
			noteTextHelper = new TextParserGeneraterForNote();
			String noteText = noteTextHelper.generaterNoteText(importance, estimation, value, howToDemo, notes);
			
			// test method
			this.MNService.insertBugNote(issueList.get(index).getIssueID(), noteText);
			
			// check the issue note info.
			noteList = this.MNService.getIssueNotes(issueList.get(index));
			assertEquals(1, noteList.size());
			for (IIssueNote note : noteList) {
				noteTextHelper = new TextParserGeneraterForNote();
				noteTextHelper.parserNoteText(note.getText());
				assertEquals(index+1, note.getIssueID());					// 1, 2 .. 
				assertEquals(index+1, note.getNoteID());					// 1, 2 ..
				assertEquals("administrator", note.getHandler());			// default = 1 (administrator)
				assertEquals(importance, noteTextHelper.getImportance());	// 201, 202, 203 ..
				assertEquals(estimation, noteTextHelper.getEstimation());	// 21, 22 , 23 ..
				assertEquals(value, noteTextHelper.getValue());				// 251, 252, 253 ..
				assertEquals(howToDemo, noteTextHelper.getHowToDemo());		// demo_1, 2 ..
				assertEquals(notes, noteTextHelper.getNotes());				// note_1, 2 ..
			}
		}
		// close connection
		this.MSservice.closeConnect();
	}
	
	public void testRemoveNote(){
		this.CPB = new CreateProductBacklog(this.StoryCount, this.CP);
		this.CPB.exe();
		
		// get the issues by creating data
		List<IIssue> issueList = this.CPB.getIssueList();
		this.MSservice.openConnect();

		int index = 0;
		int imp = 200, est = 21, value = 300;
		// override the note info.
		for(IIssue issue : issueList){
			this.addTagElement(issue, Integer.toString(imp + index), Integer.toString(est + index), 
									  Integer.toString(value + index), "demo_"+Integer.toString(index+1),
									  "note_"+Integer.toString(index+1));
			index++;
		}
		
		for(IIssue issue : issueList){
			String issueID = Long.toString(issue.getIssueID());
			//test method
			this.MNService.removeNote(issueID);

			// assert no note exist in bugnote table
			IQueryValueSet valueSet = new MySQLQuerySet();
			valueSet.addTableName("mantis_bugnote_table");
			valueSet.addFieldEqualCondition("mantis_bugnote_table.id", issueID);
			String query = valueSet.getSelectQuery();
			ResultSet result = this.MSservice.getControl().executeQuery(query);
			try {
				assertTrue(!result.next());
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

			// assert no note exist in bugnote_text table
			valueSet.clear();
			valueSet.addTableName("mantis_bugnote_text_table");
			valueSet.addFieldEqualCondition("mantis_bugnote_text_table.id", issueID);
			query = valueSet.getSelectQuery();
			result = this.MSservice.getControl().executeQuery(query);
			try {
				assertTrue(!result.next());
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		// close connection
		this.MSservice.closeConnect();
	}
	
	private void addTagElement(IIssue issue, String imp, String est, String value, String howtodemo, String notes) {
		Element history = new Element(ScrumEnum.HISTORY_TAG);
		history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, DateUtil.format(new Date(), DateUtil._16DIGIT_DATE_TIME_2));

		if (imp != null && !imp.equals("")) {
			Element importanceElem = new Element(ScrumEnum.IMPORTANCE);
			int temp = (int) Float.parseFloat(imp);
			importanceElem.setText(temp + "");
			history.addContent(importanceElem);
		}

		if (est != null && !est.equals("")) {
			Element storyPoint = new Element(ScrumEnum.ESTIMATION);
			storyPoint.setText(est);
			history.addContent(storyPoint);
		}
		
		if(value != null && !value.equals("")) {
			Element customValue = new Element(ScrumEnum.VALUE);
			customValue.setText(value);
			history.addContent(customValue);
		}
		
		Element howToDemoElem = new Element(ScrumEnum.HOWTODEMO);
		howToDemoElem.setText(howtodemo);
		history.addContent(howToDemoElem);

		Element notesElem = new Element(ScrumEnum.NOTES);
		notesElem.setText(notes);
		history.addContent(notesElem);
		
		if (history.getChildren().size() > 0) {
			issue.addTagValue(history);
			// test method
			this.MSservice.updateBugNote(issue);
			// test method
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
