package ntut.csie.ezScrum.web.dataObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import ntut.csie.ezScrum.dao.UnplannedDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.databasEnum.AccountEnum;
import ntut.csie.ezScrum.web.databasEnum.UnplannedEnum;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author AllenHuang 2015/08/21
 */

public class UnplannedObjectTest {
	private Configuration mConfig;
	private CreateProject mCP;
	private CreateSprint mCS;
	private static int sProjectCount = 1;
	private static long sProjectId;
	private static long sSprintId;

	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		mCP = new CreateProject(sProjectCount);
		mCP.exeCreate();
		
		mCS = new CreateSprint(1, mCP);
		mCS.exe();
		
		sProjectId = mCP.getAllProjects().get(0).getId();
		sSprintId = mCS.getSprints().get(0).getId();
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
		mCS = null;
	}

	@Test
	public void testSetPartnersId() {
		long TEST_UNPLANNED_ID = 1;
		// create a unplanned
		UnplannedObject unplanned = new UnplannedObject(sSprintId, sProjectId);
		unplanned.setName("TEST_NAME").setEstimate(10).setActual(0);
		unplanned.save();
		// before testSetPartnersId
		ArrayList<Long> partnersId = UnplannedDAO.getInstance().getPartnersId(TEST_UNPLANNED_ID);
		assertEquals(0, partnersId.size());
		// set one partner
		ArrayList<Long> testPartnersId = new ArrayList<Long>();
		testPartnersId.add(1L);
		unplanned.setPartnersId(testPartnersId);
		// testSetPartnersId_withOnePartner
		partnersId.clear();
		partnersId = UnplannedDAO.getInstance().getPartnersId(TEST_UNPLANNED_ID);
		assertEquals(1, partnersId.size());
		assertEquals(1L, partnersId.get(0));
	}

	@Test
	public void testSetPartnersId_WithTwoPartners() {
		long TEST_UNPLANNED_ID = 1;
		// create a unplanned
		UnplannedObject unplanned = new UnplannedObject(sSprintId, sProjectId);
		unplanned.setName("TEST_NAME").setEstimate(10).setActual(0);
		unplanned.save();
		// before testSetPartnersId
		ArrayList<Long> partnersId = UnplannedDAO.getInstance().getPartnersId(TEST_UNPLANNED_ID);
		assertEquals(0, partnersId.size());
		// set two partners
		ArrayList<Long> testPartnersId = new ArrayList<Long>();
		testPartnersId.add(1L);
		testPartnersId.add(2L);
		unplanned.setPartnersId(testPartnersId);
		// testSetPartnersId_withTwoPartners
		partnersId.clear();
		partnersId = UnplannedDAO.getInstance().getPartnersId(TEST_UNPLANNED_ID);
		assertEquals(2, partnersId.size());
		assertEquals(1L, partnersId.get(0));
		assertEquals(2L, partnersId.get(1));
	}

	@Test
	public void testSetPartnersId_WithTwoPartnersAddOneAndRemoveOnePartner() {
		long TEST_UNPLANNED_ID = 1;
		// create a unplanned
		UnplannedObject unplanned = new UnplannedObject(sSprintId, sProjectId);
		unplanned.setName("TEST_NAME").setEstimate(10).setActual(0);
		unplanned.save();
		// set two partners
		ArrayList<Long> oldPartnersId = new ArrayList<Long>();
		oldPartnersId.add(1L);
		oldPartnersId.add(2L);
		unplanned.setPartnersId(oldPartnersId);
		// testSetPartnersId_withTwoPartnersAddOneAndRemoveOnePartner
		ArrayList<Long> newPartnersId = new ArrayList<Long>();
		newPartnersId.add(2L);
		newPartnersId.add(3L);
		unplanned.setPartnersId(newPartnersId);
		ArrayList<Long> partnersId = UnplannedDAO.getInstance().getPartnersId(TEST_UNPLANNED_ID);
		partnersId.clear();
		partnersId = UnplannedDAO.getInstance().getPartnersId(TEST_UNPLANNED_ID);
		assertEquals(2, partnersId.size());
		assertEquals(2L, partnersId.get(0));
		assertEquals(3L, partnersId.get(1));
	}

	@Test
	public void testAddPartner() {
		long TEST_UNPLANNED_ID = 1;
		// create a unplanned
		UnplannedObject unplanned = new UnplannedObject(sSprintId, sProjectId);
		unplanned.setName("TEST_NAME").setEstimate(10).setActual(0);
		unplanned.save();
		// check status before add partner
		ArrayList<Long> partnersId = UnplannedDAO.getInstance().getPartnersId(TEST_UNPLANNED_ID);
		assertEquals(0, partnersId.size());
		// testAddPartner
		unplanned.addPartner(1);
		partnersId.clear();
		partnersId = UnplannedDAO.getInstance().getPartnersId(TEST_UNPLANNED_ID);
		assertEquals(1, partnersId.size());
		assertEquals(1L, partnersId.get(0));
	}

	@Test
	public void testAddPartner_WithExistPartner() {
		long TEST_UNPLANNED_ID = 1;
		// create a unplanned
		UnplannedObject unplanned = new UnplannedObject(sSprintId, sProjectId);
		unplanned.setName("TEST_NAME").setEstimate(10).setActual(0);
		unplanned.save();
		unplanned.addPartner(1);
		// check status before test
		ArrayList<Long> partnersId = UnplannedDAO.getInstance().getPartnersId(TEST_UNPLANNED_ID);
		assertEquals(1, partnersId.size());
		assertEquals(1L, partnersId.get(0));
		// testAddPartner_withExistPartner
		unplanned.addPartner(1);
		partnersId.clear();
		partnersId = UnplannedDAO.getInstance().getPartnersId(TEST_UNPLANNED_ID);
		assertEquals(1, partnersId.size());
		assertEquals(1L, partnersId.get(0));
	}

	@Test
	public void testRemovePartner() {
		long TEST_UNPLANNED_ID = 1;
		// create a unplanned
		UnplannedObject unplanned = new UnplannedObject(sSprintId, sProjectId);
		unplanned.setName("TEST_NAME").setEstimate(10).setActual(0);
		unplanned.save();
		// add a partner
		UnplannedDAO.getInstance().addPartner(TEST_UNPLANNED_ID, 1);
		// check status before test
		ArrayList<Long> partnersId = UnplannedDAO.getInstance().getPartnersId(TEST_UNPLANNED_ID);
		assertEquals(1, partnersId.size());
		assertEquals(1L, partnersId.get(0));
		// testRemovePartner
		unplanned.removePartner(1);
		partnersId.clear();
		partnersId = UnplannedDAO.getInstance().getPartnersId(TEST_UNPLANNED_ID);
		assertEquals(0, partnersId.size());
	}

	@Test
	public void testRemovePartner_WithTwoPartners() {
		long TEST_UNPLANNED_ID = 1;
		// create a unplanned
		UnplannedObject unplanned = new UnplannedObject(sSprintId, sProjectId);
		unplanned.setName("TEST_NAME").setEstimate(10).setActual(0);
		unplanned.save();
		// add two partners
		UnplannedDAO.getInstance().addPartner(TEST_UNPLANNED_ID, 1);
		UnplannedDAO.getInstance().addPartner(TEST_UNPLANNED_ID, 2);
		// check status before test
		ArrayList<Long> partnersId = UnplannedDAO.getInstance().getPartnersId(TEST_UNPLANNED_ID);
		assertEquals(2, partnersId.size());
		assertEquals(1L, partnersId.get(0));
		assertEquals(2L, partnersId.get(1));
		// testRemovePartner_withTwoPartners
		unplanned.removePartner(1);
		partnersId.clear();
		partnersId = UnplannedDAO.getInstance().getPartnersId(TEST_UNPLANNED_ID);
		assertEquals(1, partnersId.size());
		assertEquals(2L, partnersId.get(0));
	}

	@Test
	public void testGetHandler_UnassignHandler() {
		// create a unplanned
		UnplannedObject unplanned = new UnplannedObject(sSprintId, sProjectId);
		unplanned.setName("TEST_NAME").setEstimate(10).setActual(0);
		unplanned.save();
		// testGetHandler
		assertEquals(null, unplanned.getHandler());
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
		// create a unplanned
		UnplannedObject unplanned = new UnplannedObject(sSprintId, sProjectId);
		unplanned.setName("TEST_NAME").setEstimate(10).setActual(0)
			.setHandlerId(account.getId());
		unplanned.save();
		// testGetHandler
		assertEquals(account.getId(), unplanned.getHandler().getId());
	}

	@Test
	public void testGetPartnersId() {
		long TEST_UNPLANNED_ID = 1;
		// create a unplanned
		UnplannedObject unplanned = new UnplannedObject(sSprintId, sProjectId);
		unplanned.setName("TEST_NAME").setEstimate(10).setActual(0);
		unplanned.save();
		// check status before get partners id
		ArrayList<Long> partnersId = unplanned.getPartnersId();
		assertEquals(0, partnersId.size());
		// add a partner
		UnplannedDAO.getInstance().addPartner(TEST_UNPLANNED_ID, 1);
		// testGetPartnersId
		partnersId.clear();
		partnersId = unplanned.getPartnersId();
		assertEquals(1, partnersId.size());
		assertEquals(1L, partnersId.get(0));
	}

	@Test
	public void testGetPartnersId_WithTwoPartners() {
		long TEST_UNPLANNED_ID = 1;
		// create a unplanned
		UnplannedObject unplanned = new UnplannedObject(sSprintId, sProjectId);
		unplanned.setName("TEST_NAME").setEstimate(10).setActual(0);
		unplanned.save();
		// check status before get partners id
		ArrayList<Long> partnersId = unplanned.getPartnersId();
		assertEquals(0, partnersId.size());
		// add two partners
		UnplannedDAO.getInstance().addPartner(TEST_UNPLANNED_ID, 1);
		UnplannedDAO.getInstance().addPartner(TEST_UNPLANNED_ID, 2);
		// testGetPartnersId_withTwoPartners
		partnersId.clear();
		partnersId = unplanned.getPartnersId();
		assertEquals(2, partnersId.size());
		assertEquals(1L, partnersId.get(0));
		assertEquals(2L, partnersId.get(1));
	}

	@Test
	public void testGetPartners() {
		long TEST_UNPLANNED_ID = 1;
		String USERNAME = "test_username";
		String PASSWORD = "test_password";
		boolean ENABLE = true;
		// create a unplanned
		UnplannedObject unplanned = new UnplannedObject(sSprintId, sProjectId);
		unplanned.setName("TEST_NAME").setEstimate(10).setActual(0);
		unplanned.save();
		// before add a partner
		assertEquals(0, unplanned.getPartners().size());
		// create a account
		AccountObject account = new AccountObject(USERNAME);
		account.setPassword(PASSWORD).setEnable(ENABLE);
		account.save();
		// add partner
		UnplannedDAO.getInstance().addPartner(TEST_UNPLANNED_ID, account.getId());
		// testGetPartners
		assertEquals(1, unplanned.getPartners().size());
		assertEquals(account.getId(), unplanned.getPartners().get(0).getId());
	}

	@Test
	public void testGetPartners_WithTwoPartners() {
		long TEST_UNPLANNED_ID = 1;
		String FIRST_USERNAME = "test_first_username";
		String SECOND_USERNAME = "test_second_username";
		String PASSWORD = "test_password";
		boolean ENABLE = true;
		// create a unplanned
		UnplannedObject unplanned = new UnplannedObject(sSprintId, sProjectId);
		unplanned.setName("TEST_NAME").setEstimate(10).setActual(0);
		unplanned.save();
		// before add partners
		assertEquals(0, unplanned.getPartners().size());
		// create first account
		AccountObject firstAccount = new AccountObject(FIRST_USERNAME);
		firstAccount.setPassword(PASSWORD).setEnable(ENABLE);
		firstAccount.save();
		// create first account
		AccountObject secondAccount = new AccountObject(SECOND_USERNAME);
		secondAccount.setPassword(PASSWORD).setEnable(ENABLE);
		secondAccount.save();
		// add two partners
		UnplannedDAO.getInstance().addPartner(TEST_UNPLANNED_ID, firstAccount.getId());
		UnplannedDAO.getInstance().addPartner(TEST_UNPLANNED_ID, secondAccount.getId());
		// testGetPartners
		assertEquals(2, unplanned.getPartners().size());
		assertEquals(firstAccount.getId(), unplanned.getPartners().get(0).getId());
		assertEquals(secondAccount.getId(), unplanned.getPartners().get(1).getId());
	}

	@Test
	public void testGetPartnersUsername() {
		long TEST_UNPLANNED_ID = 1;
		String USERNAME = "test_username";
		String PASSWORD = "test_password";
		boolean ENABLE = true;
		// create a unplanned
		UnplannedObject unplanned = new UnplannedObject(sSprintId, sProjectId);
		unplanned.setName("TEST_NAME").setEstimate(10).setActual(0);
		unplanned.save();
		// before add a partner get partners username
		assertEquals("", unplanned.getPartnersUsername());
		// create a account
		AccountObject account = new AccountObject(USERNAME);
		account.setPassword(PASSWORD).setEnable(ENABLE);
		account.save();
		// add partner
		UnplannedDAO.getInstance().addPartner(TEST_UNPLANNED_ID, account.getId());
		// testGetPartnersUsername
		assertEquals("test_username", unplanned.getPartnersUsername());
	}

	@Test
	public void testGetPartnersUsername_WithTwoPartners() {
		long TEST_UNPLANNED_ID = 1;
		String FIRST_USERNAME = "test_first_username";
		String SECOND_USERNAME = "test_second_username";
		String PASSWORD = "test_password";
		boolean ENABLE = true;
		// create a unplanned
		UnplannedObject unplanned = new UnplannedObject(sSprintId, sProjectId);
		unplanned.setName("TEST_NAME").setEstimate(10).setActual(0);
		unplanned.save();
		// before add partners
		assertEquals("", unplanned.getPartnersUsername());
		// create first account
		AccountObject firstAccount = new AccountObject(FIRST_USERNAME);
		firstAccount.setPassword(PASSWORD).setEnable(ENABLE);
		firstAccount.save();
		// create first account
		AccountObject secondAccount = new AccountObject(SECOND_USERNAME);
		secondAccount.setPassword(PASSWORD).setEnable(ENABLE);
		secondAccount.save();
		// add two partners
		UnplannedDAO.getInstance().addPartner(TEST_UNPLANNED_ID, firstAccount.getId());
		UnplannedDAO.getInstance().addPartner(TEST_UNPLANNED_ID, secondAccount.getId());
		// testGetPartnersUsername_withTwoPartners
		assertEquals("test_first_username;test_second_username", unplanned.getPartnersUsername());
	}

	@Test
	public void testGetHistories() {
		String TEST_NAME = "TEST_NAME", TEST_NAME_NEW = "TEST_NAME_NEW";
		String TEST_NOTE = "TEST_NOTE", TEST_NOTE_NEW = "TEST_NOTE_NEW";
		int TEST_ESTIMATE = 1, TEST_ESTIMATE_NEW = 2;
		int TEST_ACTUAL = 3, TEST_ACTUAL_NEW = 4;
		int TEST_STATUS = UnplannedObject.STATUS_UNCHECK, TEST_STATUS_NEW = UnplannedObject.STATUS_DONE;
		long TEST_HANDLER_ID = 1;
		long TEST_SPRINT_ID = 2;

		UnplannedObject unplanned = new UnplannedObject(sSprintId, sProjectId);
		unplanned.setName(TEST_NAME).setNotes(TEST_NOTE).setEstimate(TEST_ESTIMATE)
				.setActual(TEST_ACTUAL).setStatus(TEST_STATUS).save();

		unplanned.setName(TEST_NAME_NEW).setNotes(TEST_NOTE_NEW)
				.setEstimate(TEST_ESTIMATE_NEW).setActual(TEST_ACTUAL_NEW)
				.setStatus(TEST_STATUS_NEW).setHandlerId(TEST_HANDLER_ID)
				.setSprintId(TEST_SPRINT_ID).save();

		assertEquals(8, unplanned.getHistories().size());
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
		long TEST_PROJECT = 1;
		long TEST_SPRINT_ID = 5;

		UnplannedObject unplanned = new UnplannedObject(TEST_SPRINT_ID, TEST_PROJECT);
		unplanned.setName(TEST_NAME).setNotes(TEST_NOTE).setEstimate(TEST_ESTIMATE)
				.setActual(TEST_ACTUAL).setStatus(TaskObject.STATUS_DONE)
				.setHandlerId(handler.getId()).setSprintId(TEST_SPRINT_ID).save();
		ArrayList<Long> partners = new ArrayList<Long>();
		partners.add(partner.getId());
		unplanned.addPartner(partner.getId());

		// assert json object
		JSONObject json = unplanned.toJSON();
		assertEquals(TEST_NAME, json.getString(UnplannedEnum.NAME));
		assertEquals(TEST_NOTE, json.getString(UnplannedEnum.NOTES));
		assertEquals(TEST_ESTIMATE, json.getInt(UnplannedEnum.ESTIMATE));
		assertEquals(TEST_ACTUAL, json.getInt(UnplannedEnum.ACTUAL));
		assertEquals(TEST_PROJECT, json.getInt(UnplannedEnum.PROJECT_ID));
		assertEquals(TEST_SPRINT_ID, json.getInt(UnplannedEnum.SPRINT_ID));

		JSONObject handlerJson = json.getJSONObject(UnplannedEnum.HANDLER);
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
	 * 測試新增一個 unplanned
	 */
	@Test
	public void testSave_CreateANewUnplanned() {
		UnplannedObject unplanned = new UnplannedObject(sSprintId, sProjectId);
		unplanned.setName("TEST_NAME").setNotes("TEST_NOTES").setEstimate(10)
			.setActual(0).save();

		assertEquals(1, unplanned.getId());
		assertEquals(1, unplanned.getSerialId());
		assertEquals("TEST_NAME", unplanned.getName());
		assertEquals("TEST_NOTES", unplanned.getNotes());
		assertEquals(10, unplanned.getEstimate());
		assertEquals(0, unplanned.getActual());
		assertEquals(sSprintId, unplanned.getSprintId());
		assertEquals(sProjectId, unplanned.getProjectId());
	}

	/**
	 * 測試一個已存在的 unplanned
	 */
	@Test
	public void testSave_UpdateUnplanned() {
		UnplannedObject unplanned = new UnplannedObject(sSprintId, sProjectId);
		unplanned.setName("TEST_NAME").setNotes("TEST_NOTES").setEstimate(10)
			.setActual(0).save();

		assertEquals(1, unplanned.getId());
		assertEquals(1, unplanned.getSerialId());
		assertEquals("TEST_NAME", unplanned.getName());
		assertEquals("TEST_NOTES", unplanned.getNotes());
		assertEquals(10, unplanned.getEstimate());
		assertEquals(0, unplanned.getActual());
		assertEquals(sSprintId, unplanned.getSprintId());
		assertEquals(sProjectId, unplanned.getProjectId());

		unplanned.setName("TEST_NAME2").setNotes("TEST_NOTES2").setEstimate(3)
			.setActual(1).setSprintId(3).save();

		assertEquals(1, unplanned.getId());
		assertEquals(1, unplanned.getSerialId());
		assertEquals("TEST_NAME2", unplanned.getName());
		assertEquals("TEST_NOTES2", unplanned.getNotes());
		assertEquals(3, unplanned.getEstimate());
		assertEquals(1, unplanned.getActual());
		assertEquals(3, unplanned.getSprintId());
		assertEquals(sProjectId, unplanned.getProjectId());
	}

	@Test
	public void testDelete() {
		UnplannedObject unplanned = new UnplannedObject(sSprintId, sProjectId);
		unplanned.setName("TEST_NAME").setNotes("TEST_NOTES").setEstimate(10)
			.setActual(0).save();

		assertEquals(1, unplanned.getId());
		assertEquals(1, unplanned.getSerialId());
		assertEquals("TEST_NAME", unplanned.getName());
		assertEquals("TEST_NOTES", unplanned.getNotes());
		assertEquals(10, unplanned.getEstimate());
		assertEquals(0, unplanned.getActual());

		boolean deleteSuccess = unplanned.delete();

		assertTrue(deleteSuccess);
		assertEquals(-1, unplanned.getId());
		assertEquals(-1, unplanned.getSerialId());
		assertEquals(null, UnplannedDAO.getInstance().get(1));
	}

	@Test
	public void testReload() {
		UnplannedObject unplanned = new UnplannedObject(sSprintId, sProjectId);
		unplanned.setName("TEST_NAME").setNotes("TEST_NOTES").setEstimate(10)
			.setActual(0).save();

		assertEquals(1, unplanned.getId());
		assertEquals(1, unplanned.getSerialId());
		assertEquals("TEST_NAME", unplanned.getName());
		assertEquals("TEST_NOTES", unplanned.getNotes());
		assertEquals(10, unplanned.getEstimate());
		assertEquals(0, unplanned.getActual());

		unplanned.setName("TEST_NAME2").setNotes("TEST_NOTES2").setEstimate(5)
			.setActual(1);

		assertEquals(1, unplanned.getId());
		assertEquals(1, unplanned.getSerialId());
		assertEquals("TEST_NAME2", unplanned.getName());
		assertEquals("TEST_NOTES2", unplanned.getNotes());
		assertEquals(5, unplanned.getEstimate());
		assertEquals(1, unplanned.getActual());

		try {
			unplanned.reload();

			assertEquals(1, unplanned.getId());
			assertEquals(1, unplanned.getSerialId());
			assertEquals("TEST_NAME", unplanned.getName());
			assertEquals("TEST_NOTES", unplanned.getNotes());
			assertEquals(10, unplanned.getEstimate());
			assertEquals(0, unplanned.getActual());
		} catch (Exception e) {
		}
	}

	/**
	 * 之後要拔掉,為了符合目前的 IIssue
	 */
	@Test
	public void testGetStatusString() {
		// create a unplanned
		UnplannedObject unplanned = new UnplannedObject(sSprintId, sProjectId);
		unplanned.setName("TEST_NAME").setNotes("TEST_NOTES").setEstimate(10)
			.setActual(0).save();
		
		// check unplanned status before test
		assertEquals("new", unplanned.getStatusString());
		// check unplanned status after check it out
		unplanned.setStatus(UnplannedObject.STATUS_CHECK);
		assertEquals("assigned", unplanned.getStatusString());
		// check unplanned status after close it
		unplanned.setStatus(UnplannedObject.STATUS_DONE);
		assertEquals("closed", unplanned.getStatusString());
		// check unplanned status after reopen it
		unplanned.setStatus(UnplannedObject.STATUS_UNCHECK);
		assertEquals("new", unplanned.getStatusString());
	}
}
