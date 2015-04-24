package ntut.csie.ezScrum.issue.sql.service.internal;

import static org.junit.Assert.*;
import java.sql.ResultSet;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.IIssueNote;
import ntut.csie.ezScrum.issue.internal.Issue;
import ntut.csie.ezScrum.issue.internal.IssueNote;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;
import org.jdom.Element;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MantisNoteServiceTest {
	private int ProjectCount = 1;
	private int StoryCount = 10;
	private CreateProject mCP;
	private CreateProductBacklog mCPB;
	private Configuration mConfig;
	private MantisService mMantisService;
	private MantisIssueService mMantisIssueService;
	private MantisNoteService mMantisNoteService;
	private TextParserGeneraterForNote mTextParserGeneraterForNote;
	
	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration(new UserSession(AccountObject.get("admin")));
		mConfig.setTestMode(true);
		mConfig.save();
		
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();											
		
		// 新增Project
		mCP = new CreateProject(ProjectCount);
		mCP.exeCreate();
		
		// 建立MantisTagService
		mMantisService = new MantisService(mConfig);
		mMantisNoteService = new MantisNoteService(mMantisService.getControl(), mConfig);
		mMantisIssueService = new MantisIssueService(mMantisService.getControl(), mConfig);
	}
	
	@After
	public void tearDown() throws Exception {
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();											

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
    	
    	mConfig.setTestMode(false);
		mConfig.save();
    	
		// release resource
		mCP = null;
		mCPB = null;
		mConfig = null;
		mMantisService = null;
		mMantisIssueService = null;
		mMantisNoteService = null;
		mTextParserGeneraterForNote = null;
	}
	
	@Test
	public void testUpdateBugNote(){
		IProject project = mCP.getProjectList().get(0);
		List<IIssue> issues = new LinkedList<IIssue>();
		List<IIssueNote> notes;
		mMantisService.openConnect();
		// new 10 issues
		for (int index = 0 ; index < 10 ; index++) {
			IIssue story = new Issue();
			story.setIssueID(index + 1);
			story.setSummary("Story_Name_" + Integer.toString(index + 1));
			story.setDescription("Story_Desc_" + Integer.toString(index + 1));

			story.setProjectID(project.getName());
			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
			
			mMantisIssueService.newIssue(story);
			issues.add(story);

			// check the issue's note is null
			notes = mMantisNoteService.getIssueNotes(issues.get(index));
			assertEquals(0, notes.size());
		}
		
		// Test if there is no existing tag in bug note initially, 
		// "insertBugNote" would be called in function "updateBugNote".
		int imp = 201;
		int est = 21;
		int val = 251;
		for(int index = 0; index < issues.size(); index++){
			String importance = Integer.toString(imp + index);
			String estimation = Integer.toString(est + index);
			String value = Integer.toString(val + index);
			String howToDemo = "demo_"+Integer.toString(index + 1);
			String notesString = "note_"+Integer.toString(index + 1);
			
			// test method : "updateBugNote", which would be called in function "addTagElement".
			addTagElement(issues.get(index), importance, estimation, value,	howToDemo, notesString);

			// check the issue note info.
			notes = mMantisNoteService.getIssueNotes(issues.get(index));
			assertEquals(1, notes.size());
			for (IIssueNote note : notes) {
				mTextParserGeneraterForNote = new TextParserGeneraterForNote();
				mTextParserGeneraterForNote.parserNoteText(note.getText());
				assertEquals(index + 1, note.getIssueID());					// 1, 2 .. 
				assertEquals(index + 1, note.getNoteID());					// 1, 2 ..
				assertEquals("admin", note.getHandler());			// default = 1 (administrator)
				assertEquals(importance, mTextParserGeneraterForNote.getImportance());	// 201, 202, 203 ..
				assertEquals(estimation, mTextParserGeneraterForNote.getEstimation());	// 21, 22 , 23 ..
				assertEquals(value, mTextParserGeneraterForNote.getValue());				// 251, 252, 253 ..
				assertEquals(howToDemo, mTextParserGeneraterForNote.getHowToDemo());		// demo_1, 2 ..
				assertEquals(notesString, mTextParserGeneraterForNote.getNotes());				// note_1, 2 ..
			}
		}
		// ==============================================================

		
		// Test if there are some tags already exist in bug note initially, 
		// then the "updateBugNote" would update the "JCIS tag" by "appending".
		imp = 301; 
		est = 31;
		val = 351;
		for(int index = 0; index < issues.size(); index++){
			String importance = Integer.toString(imp + index);
			String estimation = Integer.toString(est + index);
			String value = Integer.toString(val + index);
			String howToDemo = "demo_"+Integer.toString(index + 11);
			String notesString = "note_"+Integer.toString(index + 11);
			
			// test method : "updateBugNote", which would be called in function "addTagElement".
			addTagElement(issues.get(index), importance, estimation, value,	howToDemo, notesString);
			
			// check the issue note info.
			notes = mMantisNoteService.getIssueNotes(issues.get(index));
			assertEquals(1, notes.size());// list size still is 1, because the query result didn't separate the note tags 
			for (IIssueNote note : notes) {
				mTextParserGeneraterForNote = new TextParserGeneraterForNote();
				mTextParserGeneraterForNote.parserNoteText(note.getText());
				assertEquals(index + 1, note.getIssueID());					// 1, 2 .. 
				assertEquals(index + 1, note.getNoteID());					// 1, 2 ..
				assertEquals("admin", note.getHandler());			// default = 1 (administrator)
				assertEquals(importance, mTextParserGeneraterForNote.getImportance());	// 301, 302, 303 ..
				assertEquals(estimation, mTextParserGeneraterForNote.getEstimation());	// 31, 32 , 33 ..
				assertEquals(value, mTextParserGeneraterForNote.getValue());				// 351, 352, 353 ..
				assertEquals(howToDemo, mTextParserGeneraterForNote.getHowToDemo());		// demo_1, 2 ..
				assertEquals(notesString, mTextParserGeneraterForNote.getNotes());				// note_1, 2 ..
			}
		}
		// close connection
		mMantisService.closeConnect();
	}
	
	@Test
	public void testUpdateIssueNote(){
		IProject project = mCP.getProjectList().get(0);
		List<IIssue> issues = new LinkedList<IIssue>();
		List<IIssueNote> notes;
		mMantisService.openConnect();
		// new 10 issues
		for (int index = 0 ; index < 10 ; index++) {
			IIssue story = new Issue();
			story.setIssueID(index + 1);
			story.setSummary("Story_Name_" + Integer.toString(index+1));
			story.setDescription("Story_Desc_" + Integer.toString(index+1));

			story.setProjectID(project.getName());
			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
			
			mMantisIssueService.newIssue(story);
			issues.add(story);

			// check the issue's note is null
			notes = mMantisNoteService.getIssueNotes(issues.get(index));
			assertEquals(0, notes.size());
		}
		
		// Test if there is no existing tag in bug note initially, 
		// "insertBugNote" would be called in function "updateIssueNote".
		int imp = 201, est = 21, val = 251;
		for(int index = 0; index < issues.size(); index++){
			String importance = Integer.toString(imp + index);
			String estimation = Integer.toString(est + index);
			String value = Integer.toString(val + index);
			String howToDemo = "demo_"+Integer.toString(index + 1);
			String notesString = "note_"+Integer.toString(index + 1);

			// generate note text
			mTextParserGeneraterForNote = new TextParserGeneraterForNote();
			String noteText = mTextParserGeneraterForNote.generaterNoteText(importance, estimation, value, howToDemo, notesString);
			
			IIssueNote issueNote = new IssueNote();
			issueNote.setText(noteText);
			
			// test method
			mMantisNoteService.updateIssueNote(issues.get(index), issueNote);
			
			// set the issue's noteList
			notes = mMantisNoteService.getIssueNotes(issues.get(index));
			issues.get(index).setIssueNotes(notes);
			
			// check the issue note info.
			notes = mMantisNoteService.getIssueNotes(issues.get(index));
			assertEquals(1, notes.size());
			for (IIssueNote note : notes) {
				mTextParserGeneraterForNote = new TextParserGeneraterForNote();
				mTextParserGeneraterForNote.parserNoteText(note.getText());
				assertEquals(index + 1, note.getIssueID());					// 1, 2 .. 
				assertEquals(index + 1, note.getNoteID());					// 1, 2 ..
				assertEquals("admin", note.getHandler());			// default = 1 (administrator)
				assertEquals(importance, mTextParserGeneraterForNote.getImportance());	// 201, 202, 203 ..
				assertEquals(estimation, mTextParserGeneraterForNote.getEstimation());	// 21, 22 , 23 ..
				assertEquals(value, mTextParserGeneraterForNote.getValue());				// 251, 252, 253 ..
				assertEquals(howToDemo, mTextParserGeneraterForNote.getHowToDemo());		// demo_1, 2 ..
				assertEquals(notesString, mTextParserGeneraterForNote.getNotes());				// note_1, 2 ..
			}
		}
		// ==============================================================
		
		
		// Test if there are some tags already exist in bug note initially, 
		// then the "updateIssueNote" would update the "JCIS tag" by "overriding".
		imp = 301;
		est = 31;
		val = 351;
		int index = 0;
		for(IIssue issue : issues){
			String importance = Integer.toString(imp + index);
			String estimation = Integer.toString(est + index);
			String value = Integer.toString(val + index);
			String howToDemo = "update_demo_"+Integer.toString(index+1);
			String notesString = "update_note_"+Integer.toString(index+1);
			
			// generate note text
			mTextParserGeneraterForNote = new TextParserGeneraterForNote();
			String noteText = mTextParserGeneraterForNote.generaterNoteText(importance, estimation, value, howToDemo, notesString);
			
			// new issueNote
			IIssueNote issueNote = new IssueNote();
			issueNote.setIssueID(issues.get(index).getIssueID());
			issueNote.setText(noteText);
			issueNote.setHandler("");
			issueNote.setNoteID(issues.get(index).getIssueID());
			
			// add new issueNote to issue 
			issue.addIssueNote(issueNote);

			// test method
			mMantisNoteService.updateIssueNote(issue, issueNote);
			
			// check the issue note info.
			notes = mMantisNoteService.getIssueNotes(issues.get(index));
			assertEquals(1, notes.size());
			for (IIssueNote note : notes) {
				mTextParserGeneraterForNote = new TextParserGeneraterForNote();
				mTextParserGeneraterForNote.parserNoteText(note.getText());
				assertEquals(index + 1, note.getIssueID());					// 1, 2 .. 
				assertEquals(index + 1, note.getNoteID());					// 1, 2 ..
				assertEquals("admin", note.getHandler());			// default = 1 (administrator)
				assertEquals(importance, mTextParserGeneraterForNote.getImportance());	// 301, 302, 303 ..
				assertEquals(estimation, mTextParserGeneraterForNote.getEstimation());	// 31, 32, 33 ..
				assertEquals(value, mTextParserGeneraterForNote.getValue());				// 351, 352, 353 ..
				assertEquals(howToDemo, mTextParserGeneraterForNote.getHowToDemo());		// update_note_1, 2 ..
				assertEquals(notesString, mTextParserGeneraterForNote.getNotes());				// update_note_1, 2 ..
			}
			
			index++;
		}
		// close connection
		mMantisService.closeConnect();
	}
	
	@Test
	public void testInsertBugNote(){
		IProject project = mCP.getProjectList().get(0);
		List<IIssue> issues = new LinkedList<IIssue>();
		List<IIssueNote> notes;
		mMantisService.openConnect();
		// new 10 issues
		for (int index = 0 ; index < 10 ; index++) {
			IIssue story = new Issue();
			story.setIssueID(index + 1);
			story.setSummary("Story_Name_" + Integer.toString(index+1));
			story.setDescription("Story_Desc_" + Integer.toString(index+1));

			story.setProjectID(project.getName());
			story.setCategory(ScrumEnum.STORY_ISSUE_TYPE);
			
			mMantisIssueService.newIssue(story);
			issues.add(story);

			// check the issue's note is null
			notes = mMantisNoteService.getIssueNotes(issues.get(index));
			assertEquals(0, notes.size());
		}
		
		// Test insertBugNote, insert new bug note into table 
		int imp = 201, est = 21, val = 251;
		for(int index = 0; index < issues.size(); index++){
			String importance = Integer.toString(imp + index);
			String estimation = Integer.toString(est + index);
			String value = Integer.toString(val + index);
			String howToDemo = "demo_"+Integer.toString(index + 1);
			String notesString = "note_"+Integer.toString(index + 1);
			
			// generate note text
			mTextParserGeneraterForNote = new TextParserGeneraterForNote();
			String noteText = mTextParserGeneraterForNote.generaterNoteText(importance, estimation, value, howToDemo, notesString);
			
			// test method
			mMantisNoteService.insertBugNote(issues.get(index).getIssueID(), noteText);
			
			// check the issue note info.
			notes = mMantisNoteService.getIssueNotes(issues.get(index));
			assertEquals(1, notes.size());
			for (IIssueNote note : notes) {
				mTextParserGeneraterForNote = new TextParserGeneraterForNote();
				mTextParserGeneraterForNote.parserNoteText(note.getText());
				assertEquals(index + 1, note.getIssueID());					// 1, 2 .. 
				assertEquals(index + 1, note.getNoteID());					// 1, 2 ..
				assertEquals("admin", note.getHandler());			// default = 1 (administrator)
				assertEquals(importance, mTextParserGeneraterForNote.getImportance());	// 201, 202, 203 ..
				assertEquals(estimation, mTextParserGeneraterForNote.getEstimation());	// 21, 22 , 23 ..
				assertEquals(value, mTextParserGeneraterForNote.getValue());				// 251, 252, 253 ..
				assertEquals(howToDemo, mTextParserGeneraterForNote.getHowToDemo());		// demo_1, 2 ..
				assertEquals(notesString, mTextParserGeneraterForNote.getNotes());				// note_1, 2 ..
			}
		}
		// close connection
		mMantisService.closeConnect();
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
			mMantisService.updateBugNote(issue);
			// test method
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
