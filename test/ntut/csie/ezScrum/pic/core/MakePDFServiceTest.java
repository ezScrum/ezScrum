package ntut.csie.ezScrum.pic.core;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.test.CreateData.AddStoryToSprint;
import ntut.csie.ezScrum.test.CreateData.AddTaskToStory;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.web.dataObject.TaskObject;

public class MakePDFServiceTest {
	private Configuration mConfig;
	private CreateProject mCP;
	private CreateSprint mCS;
	private AddStoryToSprint mASTS;
	private AddTaskToStory mATTS;
	private MakePDFService mMakePDFService;

	@Before
	public void setUp() throws Exception {
		mMakePDFService = new MakePDFService();

		// initialize database
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// initialize SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		ini = null;

		// create test data
		int PROJECT_COUNT = 1;
		int SPRINT_COUNT = 1;
		int STORY_COUNT = 3;
		int STORY_ESTIMATE = 5;
		int TASK_COUNT = 3;
		int TASK_ESTIMATE = 8;
		String CREATE_PRODUCTBACKLOG_TYPE = "EST";

		mCP = new CreateProject(PROJECT_COUNT);
		mCP.exeCreateForDb();

		mCS = new CreateSprint(SPRINT_COUNT, mCP);
		mCS.exe();

		mASTS = new AddStoryToSprint(STORY_COUNT, STORY_ESTIMATE, mCS, mCP, CREATE_PRODUCTBACKLOG_TYPE);
		mASTS.exe();

		mATTS = new AddTaskToStory(TASK_COUNT, TASK_ESTIMATE, mASTS, mCP);
		mATTS.exe();

	}

	@After
	public void teardown() {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// make config to Production mode
		mConfig.setTestMode(false);
		mConfig.save();

		ini = null;
		mCP = null;
		mCS = null;
		mASTS = null;
		mATTS = null;
	}

	@Test
	public void testGenerateTaskCellContent() {
		long taskId = 1;
		// get task one
		TaskObject task = TaskObject.get(taskId);
		assertEquals(1, task.getId());
		assertEquals("TEST_TASK_1", task.getName());
		assertEquals(1, task.getSerialId());

		String ans = "Task Id # 1" + "\n" + "TEST_TASK_1" + "\n\n\n\n" + "8 hrs";
		assertEquals(ans, mMakePDFService.generateTaskCellContent(task));
	}

	@Test
	public void testGenerateTaskCellContent_WithNullTask() {
		assertEquals("", mMakePDFService.generateTaskCellContent(null));
	}

	@Test
	public void testGetPdfTableWithContent_WithOnlyLeftTask() {
		long taskId = 1;
		TaskObject task = TaskObject.get(taskId);
		try {
			Path currentRelativePath = Paths.get("");
			String s = currentRelativePath.toAbsolutePath().toString();
			String filePath = s + "/WebContent/WEB-INF/otherSetting/uming.ttf";
			BaseFont bfChinese = BaseFont.createFont(filePath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			PdfPTable table = mMakePDFService.getPdfTableWithContent(bfChinese, task, null);
			assertEquals(3, table.getNumberOfColumns());
			assertEquals(3, table.getRow(0).getCells().length);
			// check left column
			String leftColumnCellContent = "[Task Id # 1" + "\n" + "TEST_TASK_1" + "\n\n\n\n" + "8 hrs]";
			assertEquals(leftColumnCellContent, table.getRow(0).getCells()[0].getCompositeElements().get(0).toString());
			// check middle column
			assertEquals(PdfPCell.NO_BORDER, table.getRow(0).getCells()[1].getBorder());
			// check right column
			assertEquals("[]", table.getRow(0).getCells()[2].getCompositeElements().get(0).toString());
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetPdfTableWithContent_WithOnlyRightTask() {
		long taskId = 2;
		TaskObject task = TaskObject.get(taskId);
		try {
			Path currentRelativePath = Paths.get("");
			String s = currentRelativePath.toAbsolutePath().toString();
			String filePath = s + "/WebContent/WEB-INF/otherSetting/uming.ttf";
			BaseFont bfChinese = BaseFont.createFont(filePath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			PdfPTable table = mMakePDFService.getPdfTableWithContent(bfChinese, null, task);
			assertEquals(3, table.getNumberOfColumns());
			assertEquals(3, table.getRow(0).getCells().length);
			String leftColumnCellContent = "[]";
			assertEquals(leftColumnCellContent, table.getRow(0).getCells()[0].getCompositeElements().get(0).toString());
			// check middle column
			assertEquals(PdfPCell.NO_BORDER, table.getRow(0).getCells()[1].getBorder());
			String rightColumnCellContent = "[Task Id # 2" + "\n" + "TEST_TASK_2" + "\n\n\n\n" + "8 hrs]";
			assertEquals(rightColumnCellContent, table.getRow(0).getCells()[2].getCompositeElements().get(0).toString());
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Test
	public void testGetPdfTableWithContent_WithAllTaskNull() {
		try {
			Path currentRelativePath = Paths.get("");
			String s = currentRelativePath.toAbsolutePath().toString();
			String filePath = s + "/WebContent/WEB-INF/otherSetting/uming.ttf";
			BaseFont bfChinese = BaseFont.createFont(filePath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			PdfPTable table = mMakePDFService.getPdfTableWithContent(bfChinese, null, null);
			assertEquals(3, table.getNumberOfColumns());
			assertEquals(3, table.getRow(0).getCells().length);
			String leftColumnCellContent = "[]";
			assertEquals(leftColumnCellContent, table.getRow(0).getCells()[0].getCompositeElements().get(0).toString());
			// check middle column
			assertEquals(PdfPCell.NO_BORDER, table.getRow(0).getCells()[1].getBorder());
			String rightColumnCellContent = "[]";
			assertEquals(rightColumnCellContent, table.getRow(0).getCells()[2].getCompositeElements().get(0).toString());
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetPdfTableWithContent_WithTwoTasks(){
		long taskId = 1;
		long taskId_2 = 2;
		TaskObject task = TaskObject.get(taskId);
		TaskObject task_2 = TaskObject.get(taskId_2);
		try {
			Path currentRelativePath = Paths.get("");
			String s = currentRelativePath.toAbsolutePath().toString();
			String filePath = s + "/WebContent/WEB-INF/otherSetting/uming.ttf";
			BaseFont bfChinese = BaseFont.createFont(filePath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			PdfPTable table = mMakePDFService.getPdfTableWithContent(bfChinese, task, task_2);
			assertEquals(3, table.getNumberOfColumns());
			assertEquals(3, table.getRow(0).getCells().length);
			String leftColumnCellContent = "[Task Id # 1" + "\n" + "TEST_TASK_1" + "\n\n\n\n" + "8 hrs]";
			assertEquals(leftColumnCellContent, table.getRow(0).getCells()[0].getCompositeElements().get(0).toString());
			// check middle column
			assertEquals(PdfPCell.NO_BORDER, table.getRow(0).getCells()[1].getBorder());
			String rightColumnCellContent = "[Task Id # 2" + "\n" + "TEST_TASK_2" + "\n\n\n\n" + "8 hrs]";
			assertEquals(rightColumnCellContent, table.getRow(0).getCells()[2].getCompositeElements().get(0).toString());
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Test
	public void testGenerateCustomPdfPTable() {
		PdfPTable table = mMakePDFService.generateCustomPdfPTable();
		// Assert table style
		assertEquals(100f, table.getWidthPercentage());
		assertEquals(3, table.getNumberOfColumns());
	}
}
