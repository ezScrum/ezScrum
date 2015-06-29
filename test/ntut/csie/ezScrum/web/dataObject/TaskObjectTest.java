package ntut.csie.ezScrum.web.dataObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.dao.AttachFileDAO;
import ntut.csie.ezScrum.dao.TaskDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.databasEnum.AccountEnum;
import ntut.csie.ezScrum.web.databasEnum.IssueTypeEnum;
import ntut.csie.ezScrum.web.databasEnum.TaskEnum;
import ntut.csie.jcis.core.util.DateUtil;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author samhuang 2014/12/23
 */
public class TaskObjectTest {
	private Configuration mConfig = null;
	private CreateProject mCP = null;
	private final static int mPROJECT_COUNT = 1;
	private long mProjectId = -1;

	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		mCP = new CreateProject(mPROJECT_COUNT);
		mCP.exeCreate();
		
		mProjectId = mCP.getAllProjects().get(0).getId();
	}

	@After
	public void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		// 讓 config 回到  Production 模式
		mConfig.setTestMode(false);
		mConfig.save();
		
		mConfig = null;
		mCP = null;
	}

	@Test
	public void testSetPartnersId() {
		long TEST_TASK_ID = 1;
		long projectId = mCP.getAllProjects().get(0).getId();
		// create a task
		TaskObject task = new TaskObject(projectId);
		task.setName("TEST_NAME").setEstimate(10).setActual(0);
		task.save();
		// before testSetPartnersId
		List<Long> partnersId = TaskDAO.getInstance().getPartnersId(TEST_TASK_ID);
		assertEquals(0, partnersId.size());
		// set one partner
		ArrayList<Long> testPartnersId = new ArrayList<Long>();
		testPartnersId.add(1L);
		task.setPartnersId(testPartnersId);
		// testSetPartnersId_withOnePartner
		partnersId.clear();
		partnersId = TaskDAO.getInstance().getPartnersId(TEST_TASK_ID);
		assertEquals(1, partnersId.size());
		assertEquals(1L, partnersId.get(0));
	}

	@Test
	public void testSetPartnersId_WithTwoPartners() {
		long TEST_TASK_ID = 1;
		// create a task
		TaskObject task = new TaskObject(mProjectId);
		task.setName("TEST_NAME").setEstimate(10).setActual(0);
		task.save();
		// before testSetPartnersId
		List<Long> partnersId = TaskDAO.getInstance().getPartnersId(TEST_TASK_ID);
		assertEquals(0, partnersId.size());
		// set two partners
		ArrayList<Long> testPartnersId = new ArrayList<Long>();
		testPartnersId.add(1L);
		testPartnersId.add(2L);
		task.setPartnersId(testPartnersId);
		// testSetPartnersId_withTwoPartners
		partnersId.clear();
		partnersId = TaskDAO.getInstance().getPartnersId(TEST_TASK_ID);
		assertEquals(2, partnersId.size());
		assertEquals(1L, partnersId.get(0));
		assertEquals(2L, partnersId.get(1));
	}

	@Test
	public void testSetPartnersId_WithTwoPartnersAddOneAndRemoveOnePartner() {
		long TEST_TASK_ID = 1;
		// create a task
		TaskObject task = new TaskObject(mProjectId);
		task.setName("TEST_NAME").setEstimate(10).setActual(0);
		task.save();
		// set two partners
		ArrayList<Long> oldPartnersId = new ArrayList<Long>();
		oldPartnersId.add(1L);
		oldPartnersId.add(2L);
		task.setPartnersId(oldPartnersId);
		// testSetPartnersId_withTwoPartnersAddOneAndRemoveOnePartner
		ArrayList<Long> newPartnersId = new ArrayList<Long>();
		newPartnersId.add(2L);
		newPartnersId.add(3L);
		task.setPartnersId(newPartnersId);
		List<Long> partnersId = TaskDAO.getInstance().getPartnersId(TEST_TASK_ID);
		partnersId.clear();
		partnersId = TaskDAO.getInstance().getPartnersId(TEST_TASK_ID);
		assertEquals(2, partnersId.size());
		assertEquals(2L, partnersId.get(0));
		assertEquals(3L, partnersId.get(1));
	}

	@Test
	public void testAddPartner() {
		long TEST_TASK_ID = 1;
		// create a task
		TaskObject task = new TaskObject(mProjectId);
		task.setName("TEST_NAME").setEstimate(10).setActual(0);
		task.save();
		// check status before add partner
		List<Long> partnersId = TaskDAO.getInstance().getPartnersId(TEST_TASK_ID);
		assertEquals(0, partnersId.size());
		// testAddPartner
		task.addPartner(1);
		partnersId.clear();
		partnersId = TaskDAO.getInstance().getPartnersId(TEST_TASK_ID);
		assertEquals(1, partnersId.size());
		assertEquals(1L, partnersId.get(0));
	}

	@Test
	public void testAddPartner_WithExistPartner() {
		long TEST_TASK_ID = 1;
		// create a task
		TaskObject task = new TaskObject(mProjectId);
		task.setName("TEST_NAME").setEstimate(10).setActual(0);
		task.save();
		task.addPartner(1);
		// check status before test
		List<Long> partnersId = TaskDAO.getInstance().getPartnersId(TEST_TASK_ID);
		assertEquals(1, partnersId.size());
		assertEquals(1L, partnersId.get(0));
		// testAddPartner_withExistPartner
		task.addPartner(1);
		partnersId.clear();
		partnersId = TaskDAO.getInstance().getPartnersId(TEST_TASK_ID);
		assertEquals(1, partnersId.size());
		assertEquals(1L, partnersId.get(0));
	}

	@Test
	public void testRemovePartner() {
		long TEST_TASK_ID = 1;
		// create a task
		TaskObject task = new TaskObject(mProjectId);
		task.setName("TEST_NAME").setEstimate(10).setActual(0);
		task.save();
		// add a partner
		TaskDAO.getInstance().addPartner(TEST_TASK_ID, 1);
		// check status before test
		List<Long> partnersId = TaskDAO.getInstance().getPartnersId(TEST_TASK_ID);
		assertEquals(1, partnersId.size());
		assertEquals(1L, partnersId.get(0));
		// testRemovePartner
		task.removePartner(1);
		partnersId.clear();
		partnersId = TaskDAO.getInstance().getPartnersId(TEST_TASK_ID);
		assertEquals(0, partnersId.size());
	}

	@Test
	public void testRemovePartner_WithTwoPartners() {
		long TEST_TASK_ID = 1;
		// create a task
		TaskObject task = new TaskObject(mProjectId);
		task.setName("TEST_NAME").setEstimate(10).setActual(0);
		task.save();
		// add two partners
		TaskDAO.getInstance().addPartner(TEST_TASK_ID, 1);
		TaskDAO.getInstance().addPartner(TEST_TASK_ID, 2);
		// check status before test
		List<Long> partnersId = TaskDAO.getInstance().getPartnersId(TEST_TASK_ID);
		assertEquals(2, partnersId.size());
		assertEquals(1L, partnersId.get(0));
		assertEquals(2L, partnersId.get(1));
		// testRemovePartner_withTwoPartners
		task.removePartner(1);
		partnersId.clear();
		partnersId = TaskDAO.getInstance().getPartnersId(TEST_TASK_ID);
		assertEquals(1, partnersId.size());
		assertEquals(2L, partnersId.get(0));
	}

	@Test
	public void testGetHandler_UnassignHandler() {
		// create a task
		TaskObject task = new TaskObject(mProjectId);
		task.setName("TEST_NAME").setEstimate(10).setActual(0);
		task.save();
		// testGetHandler
		assertEquals(null, task.getHandler());
	}

	@Test
	public void testGetHandler() {
		String USERNAME = "test_username";
		String PASSWORD = "test_password";
		boolean ENABLE = true;
		// create a account
		AccountObject account = new AccountObject(USERNAME);
		account.setPassword(PASSWORD).setEnable(ENABLE);
		account.save();
		// create a task
		TaskObject task = new TaskObject(mProjectId);
		task.setName("TEST_NAME").setEstimate(10).setActual(0)
			.setHandlerId(account.getId());
		task.save();
		// testGetHandler
		assertEquals(account.getId(), task.getHandler().getId());
	}

	@Test
	public void testGetPartnersId() {
		long TEST_TASK_ID = 1;
		// create a task
		TaskObject task = new TaskObject(mProjectId);
		task.setName("TEST_NAME").setEstimate(10).setActual(0);
		task.save();
		// check status before get partners id
		List<Long> partnersId = task.getPartnersId();
		assertEquals(0, partnersId.size());
		// add a partner
		TaskDAO.getInstance().addPartner(TEST_TASK_ID, 1);
		// testGetPartnersId
		partnersId.clear();
		partnersId = task.getPartnersId();
		assertEquals(1, partnersId.size());
		assertEquals(1L, partnersId.get(0));
	}

	@Test
	public void testGetPartnersId_WithTwoPartners() {
		long TEST_TASK_ID = 1;
		// create a task
		TaskObject task = new TaskObject(mProjectId);
		task.setName("TEST_NAME").setEstimate(10).setActual(0);
		task.save();
		// check status before get partners id
		List<Long> partnersId = task.getPartnersId();
		assertEquals(0, partnersId.size());
		// add two partners
		TaskDAO.getInstance().addPartner(TEST_TASK_ID, 1);
		TaskDAO.getInstance().addPartner(TEST_TASK_ID, 2);
		// testGetPartnersId_withTwoPartners
		partnersId.clear();
		partnersId = task.getPartnersId();
		assertEquals(2, partnersId.size());
		assertEquals(1L, partnersId.get(0));
		assertEquals(2L, partnersId.get(1));
	}

	@Test
	public void testGetPartners() {
		long TEST_TASK_ID = 1;
		String USERNAME = "test_username";
		String PASSWORD = "test_password";
		boolean ENABLE = true;
		// create a task
		TaskObject task = new TaskObject(mProjectId);
		task.setName("TEST_NAME").setEstimate(10).setActual(0);
		task.save();
		// before add a partner
		assertEquals(0, task.getPartners().size());
		// create a account
		AccountObject account = new AccountObject(USERNAME);
		account.setPassword(PASSWORD).setEnable(ENABLE);
		account.save();
		// add partner
		TaskDAO.getInstance().addPartner(TEST_TASK_ID, account.getId());
		// testGetPartners
		assertEquals(1, task.getPartners().size());
		assertEquals(account.getId(), task.getPartners().get(0).getId());
	}

	@Test
	public void testGetPartners_WithTwoPartners() {
		long TEST_TASK_ID = 1;
		String FIRST_USERNAME = "test_first_username";
		String SECOND_USERNAME = "test_second_username";
		String PASSWORD = "test_password";
		boolean ENABLE = true;
		// create a task
		TaskObject task = new TaskObject(mProjectId);
		task.setName("TEST_NAME").setEstimate(10).setActual(0);
		task.save();
		// before add partners
		assertEquals(0, task.getPartners().size());
		// create first account
		AccountObject firstAccount = new AccountObject(FIRST_USERNAME);
		firstAccount.setPassword(PASSWORD).setEnable(ENABLE);
		firstAccount.save();
		// create first account
		AccountObject secondAccount = new AccountObject(SECOND_USERNAME);
		secondAccount.setPassword(PASSWORD).setEnable(ENABLE);
		secondAccount.save();
		// add two partners
		TaskDAO.getInstance().addPartner(TEST_TASK_ID, firstAccount.getId());
		TaskDAO.getInstance().addPartner(TEST_TASK_ID, secondAccount.getId());
		// testGetPartners
		assertEquals(2, task.getPartners().size());
		assertEquals(firstAccount.getId(), task.getPartners().get(0).getId());
		assertEquals(secondAccount.getId(), task.getPartners().get(1).getId());
	}

	@Test
	public void testGetPartnersUsername() {
		long TEST_TASK_ID = 1;
		String USERNAME = "test_username";
		String PASSWORD = "test_password";
		boolean ENABLE = true;
		// create a task
		TaskObject task = new TaskObject(mProjectId);
		task.setName("TEST_NAME").setEstimate(10).setActual(0);
		task.save();
		// before add a partner get partners username
		assertEquals("", task.getPartnersUsername());
		// create a account
		AccountObject account = new AccountObject(USERNAME);
		account.setPassword(PASSWORD).setEnable(ENABLE);
		account.save();
		// add partner
		TaskDAO.getInstance().addPartner(TEST_TASK_ID, account.getId());
		// testGetPartnersUsername
		assertEquals("test_username", task.getPartnersUsername());
	}

	@Test
	public void testGetPartnersUsername_WithTwoPartners() {
		long TEST_TASK_ID = 1;
		String FIRST_USERNAME = "test_first_username";
		String SECOND_USERNAME = "test_second_username";
		String PASSWORD = "test_password";
		boolean ENABLE = true;
		// create a task
		TaskObject task = new TaskObject(mProjectId);
		task.setName("TEST_NAME").setEstimate(10).setActual(0);
		task.save();
		// before add partners
		assertEquals("", task.getPartnersUsername());
		// create first account
		AccountObject firstAccount = new AccountObject(FIRST_USERNAME);
		firstAccount.setPassword(PASSWORD).setEnable(ENABLE);
		firstAccount.save();
		// create first account
		AccountObject secondAccount = new AccountObject(SECOND_USERNAME);
		secondAccount.setPassword(PASSWORD).setEnable(ENABLE);
		secondAccount.save();
		// add two partners
		TaskDAO.getInstance().addPartner(TEST_TASK_ID, firstAccount.getId());
		TaskDAO.getInstance().addPartner(TEST_TASK_ID, secondAccount.getId());
		// testGetPartnersUsername_withTwoPartners
		assertEquals("test_first_username;test_second_username", task.getPartnersUsername());
	}

	@Test
	public void testGetHistories() {
		String TEST_NAME = "TEST_NAME", TEST_NAME_NEW = "TEST_NAME_NEW";
		String TEST_NOTE = "TEST_NOTE", TEST_NOTE_NEW = "TEST_NOTE_NEW";
		int TEST_ESTIMATE = 1, TEST_ESTIMATE_NEW = 2;
		int TEST_ACTUAL = 3, TEST_ACTUAL_NEW = 4;
		int TEST_REMAIN = 5, TEST_REMAIN_NEW = 6;
		int TEST_STATUS = TaskObject.STATUS_UNCHECK, TEST_STATUS_NEW = TaskObject.STATUS_DONE;
		long TEST_HANDLER = 1;
		long TEST_STORY_ID = 2;

		TaskObject task = new TaskObject(1);
		task.setName(TEST_NAME).setNotes(TEST_NOTE).setEstimate(TEST_ESTIMATE)
				.setActual(TEST_ACTUAL).setRemains(TEST_REMAIN)
				.setStatus(TEST_STATUS).save();

		task.setName(TEST_NAME_NEW).setNotes(TEST_NOTE_NEW)
				.setEstimate(TEST_ESTIMATE_NEW).setActual(TEST_ACTUAL_NEW)
				.setRemains(TEST_REMAIN_NEW).setStatus(TEST_STATUS_NEW)
				.setHandlerId(TEST_HANDLER).setStoryId(TEST_STORY_ID).save();

		assertEquals(9, task.getHistories().size());
	}

	@Test
	public void testGetHistories_WithSetNoParent() {
		String TEST_NAME = "TEST_NAME";
		String TEST_NOTE = "TEST_NOTE";
		int TEST_ESTIMATE = 1;
		int TEST_ACTUAL = 3;
		int TEST_REMAIN = 5;
		int TEST_STATUS = TaskObject.STATUS_UNCHECK;
		long TEST_STORY_ID = 2;
		long TEST_STORY_ID_NO_PARENT = -1;

		TaskObject task = new TaskObject(1);
		task.setName(TEST_NAME).setNotes(TEST_NOTE).setEstimate(TEST_ESTIMATE)
				.setActual(TEST_ACTUAL).setRemains(TEST_REMAIN)
				.setStatus(TEST_STATUS).setStoryId(TEST_STORY_ID).save();

		task.setStoryId(TEST_STORY_ID_NO_PARENT).save();

		assertEquals(3, task.getHistories().size());
		assertEquals(HistoryObject.TYPE_CREATE, task.getHistories().get(0).getHistoryType());
		assertEquals(HistoryObject.TYPE_APPEND, task.getHistories().get(1).getHistoryType());
		assertEquals(HistoryObject.TYPE_REMOVE, task.getHistories().get(2).getHistoryType());
	}

	@Test
	public void testGetAttachFiles_OneFile() {
		String TEST_NAME = "TEST_NAME";
		String TEST_NOTE = "TEST_NOTE";
		int TEST_ESTIMATE = 0;
		int TEST_ACTUAL = 1;
		int TEST_REMAIN = 2;
		long TEST_PROJECT = 1;
		long TEST_STORY_ID = 5;

		// the task will be added attach files
		TaskObject task = new TaskObject(TEST_PROJECT);
		task.setName(TEST_NAME).setNotes(TEST_NOTE).setEstimate(TEST_ESTIMATE)
				.setActual(TEST_ACTUAL).setRemains(TEST_REMAIN)
				.setStatus(TaskObject.STATUS_DONE).setStoryId(TEST_STORY_ID)
				.save();

		// create a attach file
		String TEST_FILE_NAME = "TEST_FILE_NAME";
		String TEST_FILE_PATH = "/TEST_PATH";
		String TEST_FILE_CONTENT_TYPE = "jpg";
		long TEST_CREATE_TIME = System.currentTimeMillis();

		AttachFileObject.Builder builder = new AttachFileObject.Builder();
		builder.setContentType(TEST_FILE_CONTENT_TYPE).setIssueId(task.getId())
				.setIssueType(IssueTypeEnum.TYPE_TASK).setName(TEST_FILE_NAME)
				.setPath(TEST_FILE_PATH).setCreateTime(TEST_CREATE_TIME);
		AttachFileObject attachFile = builder.build();
		AttachFileDAO.getInstance().create(attachFile);

		assertEquals(1, task.getAttachFiles().size());
	}

	@Test
	public void testGetAttachFiles_TwoFiles() {
		String TEST_NAME = "TEST_NAME";
		String TEST_NOTE = "TEST_NOTE";
		int TEST_ESTIMATE = 0;
		int TEST_ACTUAL = 1;
		int TEST_REMAIN = 2;
		long TEST_PROJECT = 1;
		long TEST_STORY_ID = 5;

		// the task will be added attach files
		TaskObject task = new TaskObject(TEST_PROJECT);
		task.setName(TEST_NAME).setNotes(TEST_NOTE).setEstimate(TEST_ESTIMATE)
				.setActual(TEST_ACTUAL).setRemains(TEST_REMAIN)
				.setStatus(TaskObject.STATUS_DONE).setStoryId(TEST_STORY_ID)
				.save();

		// first attach file
		String TEST_FILE_NAME = "TEST_FILE_NAME";
		String TEST_FILE_PATH = "/TEST_PATH";
		String TEST_FILE_CONTENT_TYPE = "jpg";
		long TEST_CREATE_TIME = System.currentTimeMillis();

		AttachFileObject.Builder builder = new AttachFileObject.Builder();
		builder.setContentType(TEST_FILE_CONTENT_TYPE).setIssueId(task.getId())
				.setIssueType(IssueTypeEnum.TYPE_TASK).setName(TEST_FILE_NAME)
				.setPath(TEST_FILE_PATH).setCreateTime(TEST_CREATE_TIME);
		AttachFileObject attachFile = builder.build();
		AttachFileDAO.getInstance().create(attachFile);

		String TEST_FILE2_NAME = "TEST_FILE_NAME";
		String TEST_FILE2_PATH = "/TEST_PATH";
		String TEST_FILE2_CONTENT_TYPE = "jpg";

		// second attach file
		AttachFileObject.Builder builder2 = new AttachFileObject.Builder();
		builder2.setContentType(TEST_FILE2_CONTENT_TYPE)
				.setIssueId(task.getId()).setIssueType(IssueTypeEnum.TYPE_TASK)
				.setName(TEST_FILE2_NAME).setPath(TEST_FILE2_PATH)
				.setCreateTime(TEST_CREATE_TIME);
		AttachFileObject attachFile2 = builder2.build();
		AttachFileDAO.getInstance().create(attachFile2);

		assertEquals(2, task.getAttachFiles().size());
	}

	@Test
	public void testToJSON() throws JSONException {
		// init handler and partner, and setup a task
		String TEST_USERNAME = "TEST_ACCOUNT";
		String TEST_EMAIL = "TEST@ezscrum.tw";
		String TEST_ACCOUNT_NAME = "ACCOUNT_NAME";
		String TEST_ACCOUNT_PW = "123123123";
		AccountObject handler = new AccountObject(TEST_USERNAME);
		handler.setEmail(TEST_EMAIL).setNickName(TEST_ACCOUNT_NAME)
				.setPassword(TEST_ACCOUNT_PW).save();

		String TEST_PARTNER_USERNAME = "TEST_PAERTNER_ACCOUNT";
		String TEST_PARTNER_EMAIL = "PARTNER@ezscrum.tw";
		String TEST_PARTNER_NAME = "TEST_PAERTNER_NAME";
		AccountObject partner = new AccountObject(TEST_PARTNER_USERNAME);
		partner.setEmail(TEST_PARTNER_EMAIL).setNickName(TEST_PARTNER_NAME)
				.setPassword(TEST_ACCOUNT_PW).save();

		String TEST_NAME = "TEST_NAME";
		String TEST_NOTE = "TEST_NOTE";
		int TEST_ESTIMATE = 0;
		int TEST_ACTUAL = 1;
		int TEST_REMAIN = 2;
		long TEST_PROJECT = 1;
		long TEST_STORY_ID = 5;

		TaskObject task = new TaskObject(TEST_PROJECT);
		task.setName(TEST_NAME).setNotes(TEST_NOTE).setEstimate(TEST_ESTIMATE)
				.setActual(TEST_ACTUAL).setRemains(TEST_REMAIN)
				.setStatus(TaskObject.STATUS_DONE)
				.setHandlerId(handler.getId()).setStoryId(TEST_STORY_ID).save();
		ArrayList<Long> partners = new ArrayList<Long>();
		partners.add(partner.getId());
		task.addPartner(partner.getId());

		// assert json object
		JSONObject json = task.toJSON();
		assertEquals(TEST_NAME, json.getString(TaskEnum.NAME));
		assertEquals(TEST_NOTE, json.getString(TaskEnum.NOTES));
		assertEquals(TEST_ESTIMATE, json.getInt(TaskEnum.ESTIMATE));
		assertEquals(TEST_ACTUAL, json.getInt(TaskEnum.ACTUAL));
		assertEquals(TEST_ESTIMATE, json.getInt(TaskEnum.REMAIN));
		assertEquals(TEST_PROJECT, json.getInt(TaskEnum.PROJECT_ID));
		assertEquals(TEST_STORY_ID, json.getInt(TaskEnum.STORY_ID));

		JSONObject handlerJson = json.getJSONObject(TaskEnum.HANDLER);
		assertEquals(handler.getId(), handlerJson.getLong(AccountEnum.ID));
		assertEquals(handler.getUsername(), handlerJson.getString(AccountEnum.USERNAME));
		assertEquals(handler.getEmail(), handlerJson.getString(AccountEnum.EMAIL));
		assertEquals(handler.getNickName(), handlerJson.getString(AccountEnum.NICK_NAME));

		JSONObject partnerJosn = json.getJSONArray("partners").getJSONObject(0);
		assertEquals(partner.getId(), partnerJosn.getLong(AccountEnum.ID));
		assertEquals(partner.getUsername(), partnerJosn.getString(AccountEnum.USERNAME));
		assertEquals(partner.getEmail(), partnerJosn.getString(AccountEnum.EMAIL));
		assertEquals(partner.getNickName(), partnerJosn.getString(AccountEnum.NICK_NAME));
	}

	/**
	 * 測試新增一個 task
	 */
	@Test
	public void testSave_CreateANewTask() {
		TaskObject task = new TaskObject(1);
		task.setName("TEST_NAME").setNotes("TEST_NOTES").setEstimate(10)
			.setActual(0);
		task.save();

		assertEquals(1, task.getId());
		assertEquals(1, task.getSerialId());
		assertEquals("TEST_NAME", task.getName());
		assertEquals("TEST_NOTES", task.getNotes());
		assertEquals(10, task.getEstimate());
		assertEquals(10, task.getRemains());
		assertEquals(0, task.getActual());
	}

	/**
	 * 測試一個已存在的 task
	 */
	@Test
	public void testSave_UpdateTask() {
		TaskObject task = new TaskObject(1);
		task.setName("TEST_NAME").setNotes("TEST_NOTES").setEstimate(10)
			.setActual(0);
		task.save();

		assertEquals(1, task.getId());
		assertEquals(1, task.getSerialId());
		assertEquals("TEST_NAME", task.getName());
		assertEquals("TEST_NOTES", task.getNotes());
		assertEquals(10, task.getEstimate());
		assertEquals(10, task.getRemains());
		assertEquals(0, task.getActual());

		task.setName("TEST_NAME2").setNotes("TEST_NOTES2").setEstimate(3)
			.setRemains(5).setActual(1);
		task.save();

		assertEquals(1, task.getId());
		assertEquals(1, task.getSerialId());
		assertEquals("TEST_NAME2", task.getName());
		assertEquals("TEST_NOTES2", task.getNotes());
		assertEquals(3, task.getEstimate());
		assertEquals(5, task.getRemains());
		assertEquals(1, task.getActual());
	}

	@Test
	public void testDelete() {
		TaskObject task = new TaskObject(1);
		task.setName("TEST_NAME").setNotes("TEST_NOTES").setEstimate(10).setActual(0);
		task.save();

		assertEquals(1, task.getId());
		assertEquals(1, task.getSerialId());
		assertEquals("TEST_NAME", task.getName());
		assertEquals("TEST_NOTES", task.getNotes());
		assertEquals(10, task.getEstimate());
		assertEquals(10, task.getRemains());
		assertEquals(0, task.getActual());

		boolean deleteSuccess = task.delete();

		assertTrue(deleteSuccess);
		assertEquals(-1, task.getId());
		assertEquals(-1, task.getSerialId());
		assertEquals(null, TaskDAO.getInstance().get(1));
	}

	@Test
	public void testReload() {
		TaskObject task = new TaskObject(1);
		task.setName("TEST_NAME").setNotes("TEST_NOTES").setEstimate(10).setActual(0);
		task.save();

		assertEquals(1, task.getId());
		assertEquals(1, task.getSerialId());
		assertEquals("TEST_NAME", task.getName());
		assertEquals("TEST_NOTES", task.getNotes());
		assertEquals(10, task.getEstimate());
		assertEquals(10, task.getRemains());
		assertEquals(0, task.getActual());

		task.setName("TEST_NAME2").setNotes("TEST_NOTES2").setEstimate(5).setRemains(3).setActual(1);

		assertEquals(1, task.getId());
		assertEquals(1, task.getSerialId());
		assertEquals("TEST_NAME2", task.getName());
		assertEquals("TEST_NOTES2", task.getNotes());
		assertEquals(5, task.getEstimate());
		assertEquals(3, task.getRemains());
		assertEquals(1, task.getActual());

		try {
			task.reload();

			assertEquals(1, task.getId());
			assertEquals(1, task.getSerialId());
			assertEquals("TEST_NAME", task.getName());
			assertEquals("TEST_NOTES", task.getNotes());
			assertEquals(10, task.getEstimate());
			assertEquals(10, task.getRemains());
			assertEquals(0, task.getActual());
		} catch (Exception e) {
		}
	}

	@Test
	public void testGetStatus_WithSpecificDate() {
		// create a task
		TaskObject task = new TaskObject(mProjectId);
		task.setName("TEST_NAME").setEstimate(10).setActual(0);
		task.save();
		// check task status before test
		Date specificDate = DateUtil.dayFillter("2015/02/04", DateUtil._8DIGIT_DATE_1);
		assertEquals(TaskObject.STATUS_UNCHECK, task.getStatus(specificDate));
		// create a check task out history
		Date changeStatusDate = DateUtil.dayFillter("2015/02/04-13:14:01", DateUtil._16DIGIT_DATE_TIME);
		HistoryObject history = new HistoryObject();
		history.setIssueId(task.getId()).setIssueType(IssueTypeEnum.TYPE_TASK)
				.setHistoryType(HistoryObject.TYPE_STATUS)
				.setOldValue(String.valueOf(task.getStatus()))
				.setNewValue(String.valueOf(TaskObject.STATUS_CHECK))
				.setCreateTime(changeStatusDate.getTime());
		history.save();
		// check task status after add a history
		assertEquals(TaskObject.STATUS_CHECK, task.getStatus(specificDate));
	}
	
	@Test
	public void testGetStatus_WithSpecificDateChangeStatusTwoTimes() {
		// create a task
		TaskObject task = new TaskObject(mProjectId);
		task.setName("TEST_NAME").setEstimate(10).setActual(0);
		task.save();
		// check task status before test
		Date specificDate = DateUtil.dayFillter("2015/02/04", DateUtil._8DIGIT_DATE_1);
		assertEquals(TaskObject.STATUS_UNCHECK, task.getStatus(specificDate));
		// create check task out history1
		Date changeStatusDate1 = DateUtil.dayFillter("2015/02/04-13:14:01", DateUtil._16DIGIT_DATE_TIME);
		HistoryObject history1 = new HistoryObject();
		history1.setIssueId(task.getId()).setIssueType(IssueTypeEnum.TYPE_TASK)
				.setHistoryType(HistoryObject.TYPE_STATUS)
				.setOldValue(String.valueOf(task.getStatus()))
				.setNewValue(String.valueOf(TaskObject.STATUS_CHECK))
				.setCreateTime(changeStatusDate1.getTime());
		history1.save();
		// create check task out history2
		Date changeStatusDate2 = DateUtil.dayFillter("2015/02/04-16:14:01", DateUtil._16DIGIT_DATE_TIME);
		HistoryObject history2 = new HistoryObject();
		history2.setIssueId(task.getId()).setIssueType(IssueTypeEnum.TYPE_TASK)
				.setHistoryType(HistoryObject.TYPE_STATUS)
				.setOldValue(String.valueOf(task.getStatus()))
				.setNewValue(String.valueOf(TaskObject.STATUS_DONE))
				.setCreateTime(changeStatusDate2.getTime());
		history2.save();
		// check task status after add two change status histories
		assertEquals(TaskObject.STATUS_DONE, task.getStatus(specificDate));
	}

	/*
	 * 之後要拔掉,為了符合目前的IIssue
	 */
	@Test
	public void testGetStatusString() {
		// create a task
		TaskObject task = new TaskObject(mProjectId);
		task.setName("TEST_NAME").setEstimate(10).setActual(0);
		task.save();
		// check task status before test
		assertEquals("new", task.getStatusString());
		// check task status after check it out
		task.setStatus(TaskObject.STATUS_CHECK);
		assertEquals("assigned", task.getStatusString());
		// check task status after close it
		task.setStatus(TaskObject.STATUS_DONE);
		assertEquals("closed", task.getStatusString());
		// check task status after reopen it
		task.setStatus(TaskObject.STATUS_UNCHECK);
		assertEquals("new", task.getStatusString());
	}

	@Test
	public void testGetDoneTime() {
		// create a task
		TaskObject task = new TaskObject(mProjectId);
		task.setName("TEST_NAME").setEstimate(10).setActual(0);
		task.save();
		// check task status before test
		assertEquals(0, task.getDoneTime());
		// create a close task history
		Date specificDate = DateUtil.dayFillter("2015/02/04-13:15:01", DateUtil._16DIGIT_DATE_TIME);
		HistoryObject history = new HistoryObject();
		history.setIssueId(task.getId()).setIssueType(IssueTypeEnum.TYPE_TASK)
				.setHistoryType(HistoryObject.TYPE_STATUS)
				.setOldValue(String.valueOf(task.getStatus()))
				.setNewValue(String.valueOf(TaskObject.STATUS_DONE))
				.setCreateTime(specificDate.getTime());
		history.save();
		// check task status after close task
		assertEquals(specificDate.getTime(), task.getDoneTime());
	}

	@Test
	public void testGetDoneTime_WithTwoDoneHistoriesInSameDay() {
		// create a task
		TaskObject task = new TaskObject(mProjectId);
		task.setName("TEST_NAME").setEstimate(10).setActual(0);
		task.save();
		// check task status before test
		assertEquals(0, task.getDoneTime());
		// create a close task history
		Date specificDate1 = DateUtil.dayFillter("2015/02/04-13:15:01", DateUtil._16DIGIT_DATE_TIME);
		HistoryObject history1 = new HistoryObject();
		history1.setIssueId(task.getId()).setIssueType(IssueTypeEnum.TYPE_TASK)
				.setHistoryType(HistoryObject.TYPE_STATUS)
				.setOldValue(String.valueOf(task.getStatus()))
				.setNewValue(String.valueOf(TaskObject.STATUS_DONE))
				.setCreateTime(specificDate1.getTime());
		history1.save();
		// create a close task history
		Date specificDate2 = DateUtil.dayFillter("2015/02/04-13:15:02", DateUtil._16DIGIT_DATE_TIME);
		HistoryObject history2 = new HistoryObject();
		history2.setIssueId(task.getId()).setIssueType(IssueTypeEnum.TYPE_TASK)
				.setHistoryType(HistoryObject.TYPE_STATUS)
				.setOldValue(String.valueOf(task.getStatus()))
				.setNewValue(String.valueOf(TaskObject.STATUS_DONE))
				.setCreateTime(specificDate2.getTime());
		history2.save();
		// check task status after close task
		assertEquals(specificDate2.getTime(), task.getDoneTime());
	}

	@Test
	public void testGetDoneTime_WithTwoDoneHistoriesInDifferentDay() {
		// create a task
		TaskObject task = new TaskObject(mProjectId);
		task.setName("TEST_NAME").setEstimate(10).setActual(0);
		task.save();
		// check task status before test
		assertEquals(0, task.getDoneTime());
		// create a close task history
		Date specificDate1 = DateUtil.dayFillter("2015/02/04-13:15:01", DateUtil._16DIGIT_DATE_TIME);
		HistoryObject history1 = new HistoryObject();
		history1.setIssueId(task.getId()).setIssueType(IssueTypeEnum.TYPE_TASK)
				.setHistoryType(HistoryObject.TYPE_STATUS)
				.setOldValue(String.valueOf(task.getStatus()))
				.setNewValue(String.valueOf(TaskObject.STATUS_DONE))
				.setCreateTime(specificDate1.getTime());
		history1.save();
		// create a close task history
		Date specificDate2 = DateUtil.dayFillter("2015/02/06-09:05:01", DateUtil._16DIGIT_DATE_TIME);
		HistoryObject history2 = new HistoryObject();
		history2.setIssueId(task.getId()).setIssueType(IssueTypeEnum.TYPE_TASK)
				.setHistoryType(HistoryObject.TYPE_STATUS)
				.setOldValue(String.valueOf(task.getStatus()))
				.setNewValue(String.valueOf(TaskObject.STATUS_DONE))
				.setCreateTime(specificDate2.getTime());
		history2.save();
		// check task status after close task
		assertEquals(specificDate2.getTime(), task.getDoneTime());
	}

	@Test
	public void testGetRemains_WithSpecificDate() {
		// create a task
		TaskObject task = new TaskObject(mProjectId);
		task.setName("TEST_NAME").setEstimate(10).setActual(0);
		task.save();
		// check task status before test
		Date specificDate = DateUtil.dayFillter("2015/02/04", DateUtil._8DIGIT_DATE_1);
		assertEquals(10, task.getRemains(specificDate));
		// create a update task remains history
		Date changeStatusDate = DateUtil.dayFillter("2015/02/04-13:14:01", DateUtil._16DIGIT_DATE_TIME);
		HistoryObject history = new HistoryObject();
		history.setIssueId(task.getId()).setIssueType(IssueTypeEnum.TYPE_TASK)
				.setHistoryType(HistoryObject.TYPE_REMAIMS)
				.setOldValue(String.valueOf(task.getRemains()))
				.setNewValue(String.valueOf(5))
				.setCreateTime(changeStatusDate.getTime());
		history.save();
		// check task remains after add a update task remains history
		assertEquals(5, task.getRemains(specificDate));
	}
	
	@Test
	public void testGetRemains_WithSpecificDateChangeRemainsTwoTimes() {
		// create a task
		TaskObject task = new TaskObject(mProjectId);
		task.setName("TEST_NAME").setEstimate(10).setActual(0);
		task.save();
		// check task status before test
		Date specificDate = DateUtil.dayFillter("2015/02/04", DateUtil._8DIGIT_DATE_1);
		assertEquals(10, task.getRemains(specificDate));
		// create a update task remains history1
		Date changeRemainsDate1 = DateUtil.dayFillter("2015/02/04-13:14:01", DateUtil._16DIGIT_DATE_TIME);
		HistoryObject history1 = new HistoryObject();
		history1.setIssueId(task.getId()).setIssueType(IssueTypeEnum.TYPE_TASK)
				.setHistoryType(HistoryObject.TYPE_REMAIMS)
				.setOldValue(String.valueOf(task.getRemains()))
				.setNewValue(String.valueOf(5))
				.setCreateTime(changeRemainsDate1.getTime());
		history1.save();
		// create a update task remains history2
		Date changeRemainsDate2 = DateUtil.dayFillter("2015/02/04-16:14:01", DateUtil._16DIGIT_DATE_TIME);
		HistoryObject history2 = new HistoryObject();
		history2.setIssueId(task.getId()).setIssueType(IssueTypeEnum.TYPE_TASK)
				.setHistoryType(HistoryObject.TYPE_REMAIMS)
				.setOldValue(String.valueOf(5))
				.setNewValue(String.valueOf(8))
				.setCreateTime(changeRemainsDate2.getTime());
		history2.save();
		// check task remains after add two change remains histories
		assertEquals(8, task.getRemains(specificDate));
	}
	
	@Test
	public void testGetRemains_WithChangeRemainsInFiveDates() {
		// create a task
		TaskObject task = new TaskObject(mProjectId);
		task.setName("TEST_NAME").setEstimate(10).setActual(0);
		task.save();
		// check task status before test
		Date firstDate = DateUtil.dayFillter("2015/02/04", DateUtil._8DIGIT_DATE_1);
		assertEquals(10, task.getRemains(firstDate));
		// create a update task remains history1
		Date changeRemainsDate1 = DateUtil.dayFillter("2015/02/05", DateUtil._8DIGIT_DATE_1);
		HistoryObject history1 = new HistoryObject();
		history1.setIssueId(task.getId()).setIssueType(IssueTypeEnum.TYPE_TASK)
				.setHistoryType(HistoryObject.TYPE_REMAIMS)
				.setOldValue(String.valueOf(10))
				.setNewValue(String.valueOf(9))
				.setCreateTime(changeRemainsDate1.getTime());
		history1.save();
		// create a update task remains history2
		Date changeRemainsDate2 = DateUtil.dayFillter("2015/02/06", DateUtil._8DIGIT_DATE_1);
		HistoryObject history2 = new HistoryObject();
		history2.setIssueId(task.getId()).setIssueType(IssueTypeEnum.TYPE_TASK)
				.setHistoryType(HistoryObject.TYPE_REMAIMS)
				.setOldValue(String.valueOf(9))
				.setNewValue(String.valueOf(8))
				.setCreateTime(changeRemainsDate2.getTime());
		history2.save();
		// create a update task remains history3
		Date changeRemainsDate3 = DateUtil.dayFillter("2015/02/07", DateUtil._8DIGIT_DATE_1);
		HistoryObject history3 = new HistoryObject();
		history3.setIssueId(task.getId()).setIssueType(IssueTypeEnum.TYPE_TASK)
				.setHistoryType(HistoryObject.TYPE_REMAIMS)
				.setOldValue(String.valueOf(8))
				.setNewValue(String.valueOf(7))
				.setCreateTime(changeRemainsDate3.getTime());
		history3.save();
		// create a update task remains history4
		Date changeRemainsDate4 = DateUtil.dayFillter("2015/02/08", DateUtil._8DIGIT_DATE_1);
		HistoryObject history4 = new HistoryObject();
		history4.setIssueId(task.getId()).setIssueType(IssueTypeEnum.TYPE_TASK)
				.setHistoryType(HistoryObject.TYPE_REMAIMS)
				.setOldValue(String.valueOf(7))
				.setNewValue(String.valueOf(6))
				.setCreateTime(changeRemainsDate4.getTime());
		history4.save();
		// check task remains after add five change remains histories
		assertEquals(10, task.getRemains(firstDate));
		assertEquals(9, task.getRemains(changeRemainsDate1));
		assertEquals(8, task.getRemains(changeRemainsDate2));
		assertEquals(7, task.getRemains(changeRemainsDate3));
		assertEquals(6, task.getRemains(changeRemainsDate4));
	}
	
	@Test
	public void testGetRemains_WithChangeRemainsThreeTimesInSixDates() {
		// create a task
		TaskObject task = new TaskObject(mProjectId);
		task.setName("TEST_NAME").setEstimate(13).setActual(0);
		task.save();
		// check task status before test
		assertEquals(13, task.getRemains(DateUtil.dayFillter("2015/02/01", DateUtil._8DIGIT_DATE_1)));
		// create a update task remains history1
		Date changeRemainsDate1 = DateUtil.dayFillter("2015/02/03", DateUtil._8DIGIT_DATE_1);
		HistoryObject history1 = new HistoryObject();
		history1.setIssueId(task.getId()).setIssueType(IssueTypeEnum.TYPE_TASK)
				.setHistoryType(HistoryObject.TYPE_REMAIMS)
				.setOldValue(String.valueOf(13))
				.setNewValue(String.valueOf(8))
				.setCreateTime(changeRemainsDate1.getTime());
		history1.save();
		// create a update task remains history2
		Date changeRemainsDate2 = DateUtil.dayFillter("2015/02/05", DateUtil._8DIGIT_DATE_1);
		HistoryObject history2 = new HistoryObject();
		history2.setIssueId(task.getId()).setIssueType(IssueTypeEnum.TYPE_TASK)
				.setHistoryType(HistoryObject.TYPE_REMAIMS)
				.setOldValue(String.valueOf(8))
				.setNewValue(String.valueOf(6))
				.setCreateTime(changeRemainsDate2.getTime());
		history2.save();
		// create a update task remains history3
		Date changeRemainsDate3 = DateUtil.dayFillter("2015/02/06", DateUtil._8DIGIT_DATE_1);
		HistoryObject history3 = new HistoryObject();
		history3.setIssueId(task.getId()).setIssueType(IssueTypeEnum.TYPE_TASK)
				.setHistoryType(HistoryObject.TYPE_REMAIMS)
				.setOldValue(String.valueOf(6))
				.setNewValue(String.valueOf(3))
				.setCreateTime(changeRemainsDate3.getTime());
		history3.save();
		// check task remains after add three change remains histories
		assertEquals(13, task.getRemains(DateUtil.dayFillter("2015/02/01", DateUtil._8DIGIT_DATE_1)));
		assertEquals(13, task.getRemains(DateUtil.dayFillter("2015/02/02", DateUtil._8DIGIT_DATE_1)));
		assertEquals(8, task.getRemains(DateUtil.dayFillter("2015/02/03", DateUtil._8DIGIT_DATE_1)));
		assertEquals(8, task.getRemains(DateUtil.dayFillter("2015/02/04", DateUtil._8DIGIT_DATE_1)));
		assertEquals(6, task.getRemains(DateUtil.dayFillter("2015/02/05", DateUtil._8DIGIT_DATE_1)));
		assertEquals(3, task.getRemains(DateUtil.dayFillter("2015/02/06", DateUtil._8DIGIT_DATE_1)));
	}
}
