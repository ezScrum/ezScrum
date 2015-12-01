package ntut.csie.ezScrum.web.dataObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ntut.csie.ezScrum.dao.UnplanDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.databaseEnum.AccountEnum;
import ntut.csie.ezScrum.web.databaseEnum.IssueTypeEnum;
import ntut.csie.ezScrum.web.databaseEnum.UnplanEnum;

public class UnplanObjectTest {
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
		long TEST_UNPLAN_ID = 1;
		// Create account
		AccountObject account1 = new AccountObject("account1"); // handler
		account1.save();
		AccountObject account2 = new AccountObject("account2"); // old partner1
		account2.save();
		AccountObject account3 = new AccountObject("account3"); // old partner2, new partner1
		account3.save();
		AccountObject account4 = new AccountObject("account4"); // new partner2
		account4.save();
		ArrayList<Long> oldPartnersId = new ArrayList<>();
		oldPartnersId.add(account2.getId());
		oldPartnersId.add(account3.getId());
		ArrayList<Long> newPartnersId = new ArrayList<>();
		newPartnersId.add(account3.getId());
		newPartnersId.add(account4.getId());
		// create a unplan
		UnplanObject unplan = new UnplanObject(sSprintId, sProjectId);
		unplan.setName("TEST_NAME").setHandlerId(account1.getId()).setPartnersId(oldPartnersId).save();
		// before testSetPartnersId
		ArrayList<Long> oldPartnersFromDAO = UnplanDAO.getInstance().getPartnersId(TEST_UNPLAN_ID);
		assertEquals(2, oldPartnersFromDAO.size());
		assertEquals(account2.getId(), oldPartnersFromDAO.get(0));
		assertEquals(account3.getId(), oldPartnersFromDAO.get(1));
		// set new partners
		unplan.setPartnersId(newPartnersId).save();
		// test partners
		ArrayList<Long> newPartnersFromDAO = UnplanDAO.getInstance().getPartnersId(TEST_UNPLAN_ID);
		assertEquals(2, newPartnersFromDAO.size());
		assertEquals(account3.getId(), newPartnersFromDAO.get(0));
		assertEquals(account4.getId(), newPartnersFromDAO.get(1));
		// test partners history
		ArrayList<HistoryObject> histories = unplan.getHistories();
		assertEquals(3, histories.size());
		assertEquals(HistoryObject.TYPE_REMOVE_PARTNER, histories.get(1).getHistoryType());
		assertEquals(IssueTypeEnum.TYPE_UNPLAN, histories.get(1).getIssueType());
		assertEquals(unplan.getId(), histories.get(1).getIssueId());
		assertEquals(account2.getId(), Long.parseLong(histories.get(1).getNewValue())); // account2 remove from task
		assertEquals("Remove Partner", histories.get(1).getHistoryTypeString());
		assertEquals(account2.getUsername(), histories.get(1).getDescription());
		assertEquals(HistoryObject.TYPE_ADD_PARTNER, histories.get(2).getHistoryType());
		assertEquals(IssueTypeEnum.TYPE_UNPLAN, histories.get(2).getIssueType());
		assertEquals(unplan.getId(), histories.get(2).getIssueId());
		assertEquals(account4.getId(), Long.parseLong(histories.get(2).getNewValue())); // add account4 to task
		assertEquals("Add Partner", histories.get(2).getHistoryTypeString());
		assertEquals(account4.getUsername(), histories.get(2).getDescription());
	}

	@Test
	public void testSetPartnersId_WithTwoPartners() {
		long TEST_UNPLAN_ID = 1;
		// Create account
		AccountObject account1 = new AccountObject("account1");
		account1.save();
		AccountObject account2 = new AccountObject("account2");
		account2.save();
		// create a unplan
		UnplanObject unplan = new UnplanObject(sSprintId, sProjectId);
		unplan.setName("TEST_NAME").save();
		// before testSetPartnersId
		ArrayList<Long> partnersId = UnplanDAO.getInstance().getPartnersId(TEST_UNPLAN_ID);
		assertEquals(0, partnersId.size());
		// set two partners
		ArrayList<Long> testPartnersId = new ArrayList<Long>();
		testPartnersId.add(account1.getId());
		testPartnersId.add(account2.getId());
		unplan.setPartnersId(testPartnersId).save();
		// testSetPartnersId_withTwoPartners
		partnersId.clear();
		partnersId = UnplanDAO.getInstance().getPartnersId(TEST_UNPLAN_ID);
		assertEquals(2, partnersId.size());
		assertEquals(account1.getId(), partnersId.get(0));
		assertEquals(account2.getId(), partnersId.get(1));
	}

	@Test
	public void testSetPartnersId_WithTwoPartnersAddOneAndRemoveOnePartner() {
		long TEST_UNPLAN_ID = 1;
		// Create account
		AccountObject account1 = new AccountObject("account1");
		account1.save();
		AccountObject account2 = new AccountObject("account2");
		account2.save();
		AccountObject account3 = new AccountObject("account3");
		account3.save();
		// set old partners
		ArrayList<Long> oldPartnersId = new ArrayList<Long>();
		oldPartnersId.add(account1.getId());
		oldPartnersId.add(account2.getId());
		// set new partners
		ArrayList<Long> newPartnersId = new ArrayList<Long>();
		newPartnersId.add(account2.getId());
		newPartnersId.add(account3.getId());
		// create a unplan
		UnplanObject unplan = new UnplanObject(sSprintId, sProjectId);
		unplan.setName("TEST_NAME").setPartnersId(oldPartnersId).save();
		// set new partners for unplan
		unplan.setPartnersId(newPartnersId).save();
		ArrayList<Long> partnersId = UnplanDAO.getInstance().getPartnersId(TEST_UNPLAN_ID);
		assertEquals(2, partnersId.size());
		assertEquals(account2.getId(), partnersId.get(0));
		assertEquals(account3.getId(), partnersId.get(1));
	}

	@Test
	public void testAddPartner() {
		long TEST_UNPLAN_ID = 1;
		// create a unplan
		UnplanObject unplan = new UnplanObject(sSprintId, sProjectId);
		unplan.setName("TEST_NAME").setEstimate(10).setActual(0);
		unplan.save();
		// check status before add partner
		ArrayList<Long> partnersId = UnplanDAO.getInstance().getPartnersId(TEST_UNPLAN_ID);
		assertEquals(0, partnersId.size());
		// testAddPartner
		unplan.addPartner(1);
		partnersId.clear();
		partnersId = UnplanDAO.getInstance().getPartnersId(TEST_UNPLAN_ID);
		assertEquals(1, partnersId.size());
		assertEquals(1L, partnersId.get(0));
	}

	@Test
	public void testAddPartner_WithExistPartner() {
		long TEST_UNPLAN_ID = 1;
		// create a unplan
		UnplanObject unplan = new UnplanObject(sSprintId, sProjectId);
		unplan.setName("TEST_NAME").setEstimate(10).setActual(0);
		unplan.save();
		unplan.addPartner(1);
		// check status before test
		ArrayList<Long> partnersId = UnplanDAO.getInstance().getPartnersId(TEST_UNPLAN_ID);
		assertEquals(1, partnersId.size());
		assertEquals(1L, partnersId.get(0));
		// testAddPartner_withExistPartner
		unplan.addPartner(1);
		partnersId.clear();
		partnersId = UnplanDAO.getInstance().getPartnersId(TEST_UNPLAN_ID);
		assertEquals(1, partnersId.size());
		assertEquals(1L, partnersId.get(0));
	}

	@Test
	public void testRemovePartner() {
		long TEST_UNPLAN_ID = 1;
		// create a unplan
		UnplanObject unplan = new UnplanObject(sSprintId, sProjectId);
		unplan.setName("TEST_NAME").setEstimate(10).setActual(0);
		unplan.save();
		// add a partner
		UnplanDAO.getInstance().addPartner(TEST_UNPLAN_ID, 1);
		// check status before test
		ArrayList<Long> partnersId = UnplanDAO.getInstance().getPartnersId(TEST_UNPLAN_ID);
		assertEquals(1, partnersId.size());
		assertEquals(1L, partnersId.get(0));
		// testRemovePartner
		unplan.removePartner(1);
		partnersId.clear();
		partnersId = UnplanDAO.getInstance().getPartnersId(TEST_UNPLAN_ID);
		assertEquals(0, partnersId.size());
	}

	@Test
	public void testRemovePartner_WithTwoPartners() {
		long TEST_UNPLAN_ID = 1;
		// create a unplan
		UnplanObject unplan = new UnplanObject(sSprintId, sProjectId);
		unplan.setName("TEST_NAME").setEstimate(10).setActual(0);
		unplan.save();
		// add two partners
		UnplanDAO.getInstance().addPartner(TEST_UNPLAN_ID, 1);
		UnplanDAO.getInstance().addPartner(TEST_UNPLAN_ID, 2);
		// check status before test
		ArrayList<Long> partnersId = UnplanDAO.getInstance().getPartnersId(TEST_UNPLAN_ID);
		assertEquals(2, partnersId.size());
		assertEquals(1L, partnersId.get(0));
		assertEquals(2L, partnersId.get(1));
		// testRemovePartner_withTwoPartners
		unplan.removePartner(1);
		partnersId.clear();
		partnersId = UnplanDAO.getInstance().getPartnersId(TEST_UNPLAN_ID);
		assertEquals(1, partnersId.size());
		assertEquals(2L, partnersId.get(0));
	}

	@Test
	public void testGetHandler_UnassignHandler() {
		// create a unplan
		UnplanObject unplan = new UnplanObject(sSprintId, sProjectId);
		unplan.setName("TEST_NAME").setEstimate(10).setActual(0);
		unplan.save();
		// testGetHandler
		assertEquals(null, unplan.getHandler());
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
		// create a unplan
		UnplanObject unplan = new UnplanObject(sSprintId, sProjectId);
		unplan.setName("TEST_NAME").setEstimate(10).setActual(0)
			.setHandlerId(account.getId());
		unplan.save();
		// testGetHandler
		assertEquals(account.getId(), unplan.getHandler().getId());
	}

	@Test
	public void testGetPartnersId() {
		long TEST_UNPLAN_ID = 1;
		// create a unplan
		UnplanObject unplan = new UnplanObject(sSprintId, sProjectId);
		unplan.setName("TEST_NAME").setEstimate(10).setActual(0);
		unplan.save();
		// check status before get partners id
		ArrayList<Long> partnersId = unplan.getPartnersId();
		assertEquals(0, partnersId.size());
		// add a partner
		UnplanDAO.getInstance().addPartner(TEST_UNPLAN_ID, 1);
		// testGetPartnersId
		partnersId.clear();
		partnersId = unplan.getPartnersId();
		assertEquals(1, partnersId.size());
		assertEquals(1L, partnersId.get(0));
	}

	@Test
	public void testGetPartnersId_WithTwoPartners() {
		long TEST_UNPLAN_ID = 1;
		// create a unplan
		UnplanObject unplan = new UnplanObject(sSprintId, sProjectId);
		unplan.setName("TEST_NAME").setEstimate(10).setActual(0);
		unplan.save();
		// check status before get partners id
		ArrayList<Long> partnersId = unplan.getPartnersId();
		assertEquals(0, partnersId.size());
		// add two partners
		UnplanDAO.getInstance().addPartner(TEST_UNPLAN_ID, 1);
		UnplanDAO.getInstance().addPartner(TEST_UNPLAN_ID, 2);
		// testGetPartnersId_withTwoPartners
		partnersId.clear();
		partnersId = unplan.getPartnersId();
		assertEquals(2, partnersId.size());
		assertEquals(1L, partnersId.get(0));
		assertEquals(2L, partnersId.get(1));
	}

	@Test
	public void testGetPartners() {
		long TEST_UNPLAN_ID = 1;
		String USERNAME = "test_username";
		String PASSWORD = "test_password";
		boolean ENABLE = true;
		// create a unplan
		UnplanObject unplan = new UnplanObject(sSprintId, sProjectId);
		unplan.setName("TEST_NAME").setEstimate(10).setActual(0);
		unplan.save();
		// before add a partner
		assertEquals(0, unplan.getPartners().size());
		// create a account
		AccountObject account = new AccountObject(USERNAME);
		account.setPassword(PASSWORD).setEnable(ENABLE);
		account.save();
		// add partner
		UnplanDAO.getInstance().addPartner(TEST_UNPLAN_ID, account.getId());
		// testGetPartners
		assertEquals(1, unplan.getPartners().size());
		assertEquals(account.getId(), unplan.getPartners().get(0).getId());
	}

	@Test
	public void testGetPartners_WithTwoPartners() {
		long TEST_UNPLAN_ID = 1;
		String FIRST_USERNAME = "test_first_username";
		String SECOND_USERNAME = "test_second_username";
		String PASSWORD = "test_password";
		boolean ENABLE = true;
		// create a unplan
		UnplanObject unplan = new UnplanObject(sSprintId, sProjectId);
		unplan.setName("TEST_NAME").setEstimate(10).setActual(0);
		unplan.save();
		// before add partners
		assertEquals(0, unplan.getPartners().size());
		// create first account
		AccountObject firstAccount = new AccountObject(FIRST_USERNAME);
		firstAccount.setPassword(PASSWORD).setEnable(ENABLE);
		firstAccount.save();
		// create first account
		AccountObject secondAccount = new AccountObject(SECOND_USERNAME);
		secondAccount.setPassword(PASSWORD).setEnable(ENABLE);
		secondAccount.save();
		// add two partners
		UnplanDAO.getInstance().addPartner(TEST_UNPLAN_ID, firstAccount.getId());
		UnplanDAO.getInstance().addPartner(TEST_UNPLAN_ID, secondAccount.getId());
		// testGetPartners
		assertEquals(2, unplan.getPartners().size());
		assertEquals(firstAccount.getId(), unplan.getPartners().get(0).getId());
		assertEquals(secondAccount.getId(), unplan.getPartners().get(1).getId());
	}

	@Test
	public void testGetPartnersUsername() {
		long TEST_UNPLAN_ID = 1;
		String USERNAME = "test_username";
		String PASSWORD = "test_password";
		boolean ENABLE = true;
		// create a unplan
		UnplanObject unplan = new UnplanObject(sSprintId, sProjectId);
		unplan.setName("TEST_NAME").setEstimate(10).setActual(0);
		unplan.save();
		// before add a partner get partners username
		assertEquals("", unplan.getPartnersUsername());
		// create a account
		AccountObject account = new AccountObject(USERNAME);
		account.setPassword(PASSWORD).setEnable(ENABLE);
		account.save();
		// add partner
		UnplanDAO.getInstance().addPartner(TEST_UNPLAN_ID, account.getId());
		// testGetPartnersUsername
		assertEquals("test_username", unplan.getPartnersUsername());
	}

	@Test
	public void testGetPartnersUsername_WithTwoPartners() {
		long TEST_UNPLAN_ID = 1;
		String FIRST_USERNAME = "test_first_username";
		String SECOND_USERNAME = "test_second_username";
		String PASSWORD = "test_password";
		boolean ENABLE = true;
		// create a unplan
		UnplanObject unplan = new UnplanObject(sSprintId, sProjectId);
		unplan.setName("TEST_NAME").setEstimate(10).setActual(0);
		unplan.save();
		// before add partners
		assertEquals("", unplan.getPartnersUsername());
		// create first account
		AccountObject firstAccount = new AccountObject(FIRST_USERNAME);
		firstAccount.setPassword(PASSWORD).setEnable(ENABLE);
		firstAccount.save();
		// create first account
		AccountObject secondAccount = new AccountObject(SECOND_USERNAME);
		secondAccount.setPassword(PASSWORD).setEnable(ENABLE);
		secondAccount.save();
		// add two partners
		UnplanDAO.getInstance().addPartner(TEST_UNPLAN_ID, firstAccount.getId());
		UnplanDAO.getInstance().addPartner(TEST_UNPLAN_ID, secondAccount.getId());
		// testGetPartnersUsername_withTwoPartners
		assertEquals("test_first_username;test_second_username", unplan.getPartnersUsername());
	}

	@Test
	public void testGetHistories() {
		String TEST_NAME = "TEST_NAME", TEST_NAME_NEW = "TEST_NAME_NEW";
		String TEST_NOTE = "TEST_NOTE", TEST_NOTE_NEW = "TEST_NOTE_NEW";
		int TEST_ESTIMATE = 1, TEST_ESTIMATE_NEW = 2;
		int TEST_ACTUAL = 3, TEST_ACTUAL_NEW = 4;
		int TEST_STATUS = UnplanObject.STATUS_UNCHECK, TEST_STATUS_NEW = UnplanObject.STATUS_DONE;
		long TEST_HANDLER_ID = 1;
		long TEST_SPRINT_ID = 2;

		UnplanObject unplan = new UnplanObject(sSprintId, sProjectId);
		unplan.setName(TEST_NAME).setNotes(TEST_NOTE).setEstimate(TEST_ESTIMATE)
				.setActual(TEST_ACTUAL).setStatus(TEST_STATUS).save();

		unplan.setName(TEST_NAME_NEW).setNotes(TEST_NOTE_NEW)
				.setEstimate(TEST_ESTIMATE_NEW).setActual(TEST_ACTUAL_NEW)
				.setStatus(TEST_STATUS_NEW).setHandlerId(TEST_HANDLER_ID)
				.setSprintId(TEST_SPRINT_ID).save();

		assertEquals(8, unplan.getHistories().size());
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

		UnplanObject unplan = new UnplanObject(TEST_SPRINT_ID, TEST_PROJECT);
		unplan.setName(TEST_NAME).setNotes(TEST_NOTE).setEstimate(TEST_ESTIMATE)
				.setActual(TEST_ACTUAL).setStatus(TaskObject.STATUS_DONE)
				.setHandlerId(handler.getId()).setSprintId(TEST_SPRINT_ID).save();
		ArrayList<Long> partners = new ArrayList<Long>();
		partners.add(partner.getId());
		unplan.addPartner(partner.getId());

		// assert json object
		JSONObject json = unplan.toJSON();
		assertEquals(TEST_NAME, json.getString(UnplanEnum.NAME));
		assertEquals(TEST_NOTE, json.getString(UnplanEnum.NOTES));
		assertEquals(TEST_ESTIMATE, json.getInt(UnplanEnum.ESTIMATE));
		assertEquals(TEST_ESTIMATE, json.getInt(UnplanEnum.ACTUAL));
		assertEquals(TEST_PROJECT, json.getInt(UnplanEnum.PROJECT_ID));
		assertEquals(TEST_SPRINT_ID, json.getInt(UnplanEnum.SPRINT_ID));

		JSONObject handlerJson = json.getJSONObject(UnplanEnum.HANDLER);
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
	 * 測試新增一個 unplan
	 */
	@Test
	public void testSave_CreateANewUnplan() {
		UnplanObject unplan = new UnplanObject(sSprintId, sProjectId);
		unplan.setName("TEST_NAME").setNotes("TEST_NOTES").setEstimate(10)
			.setActual(0).save();

		assertEquals(1, unplan.getId());
		assertEquals(1, unplan.getSerialId());
		assertEquals("TEST_NAME", unplan.getName());
		assertEquals("TEST_NOTES", unplan.getNotes());
		assertEquals(10, unplan.getEstimate());
		assertEquals(10, unplan.getActual());
		assertEquals(sSprintId, unplan.getSprintId());
		assertEquals(sProjectId, unplan.getProjectId());
	}

	/**
	 * 測試一個已存在的 unplan
	 */
	@Test
	public void testSave_UpdateUnplan() {
		UnplanObject unplan = new UnplanObject(sSprintId, sProjectId);
		unplan.setName("TEST_NAME").setNotes("TEST_NOTES").setEstimate(10)
			.setActual(0).save();

		assertEquals(1, unplan.getId());
		assertEquals(1, unplan.getSerialId());
		assertEquals("TEST_NAME", unplan.getName());
		assertEquals("TEST_NOTES", unplan.getNotes());
		assertEquals(10, unplan.getEstimate());
		assertEquals(10, unplan.getActual());
		assertEquals(sSprintId, unplan.getSprintId());
		assertEquals(sProjectId, unplan.getProjectId());

		unplan.setName("TEST_NAME2").setNotes("TEST_NOTES2").setEstimate(3)
			.setActual(1).setSprintId(3).save();

		assertEquals(1, unplan.getId());
		assertEquals(1, unplan.getSerialId());
		assertEquals("TEST_NAME2", unplan.getName());
		assertEquals("TEST_NOTES2", unplan.getNotes());
		assertEquals(3, unplan.getEstimate());
		assertEquals(1, unplan.getActual());
		assertEquals(3, unplan.getSprintId());
		assertEquals(sProjectId, unplan.getProjectId());
	}

	@Test
	public void testDelete() {
		UnplanObject unplan = new UnplanObject(sSprintId, sProjectId);
		unplan.setName("TEST_NAME").setNotes("TEST_NOTES").setEstimate(10)
			.setActual(0).save();

		assertEquals(1, unplan.getId());
		assertEquals(1, unplan.getSerialId());
		assertEquals("TEST_NAME", unplan.getName());
		assertEquals("TEST_NOTES", unplan.getNotes());
		assertEquals(10, unplan.getEstimate());
		assertEquals(10, unplan.getActual());

		boolean deleteSuccess = unplan.delete();

		assertTrue(deleteSuccess);
		assertEquals(-1, unplan.getId());
		assertEquals(-1, unplan.getSerialId());
		assertEquals(null, UnplanDAO.getInstance().get(1));
	}

	@Test
	public void testReload() {
		UnplanObject unplan = new UnplanObject(sSprintId, sProjectId);
		unplan.setName("TEST_NAME").setNotes("TEST_NOTES").setEstimate(10)
			.setActual(0).save();

		assertEquals(1, unplan.getId());
		assertEquals(1, unplan.getSerialId());
		assertEquals("TEST_NAME", unplan.getName());
		assertEquals("TEST_NOTES", unplan.getNotes());
		assertEquals(10, unplan.getEstimate());
		assertEquals(10, unplan.getActual());

		unplan.setName("TEST_NAME2").setNotes("TEST_NOTES2").setEstimate(5)
			.setActual(1);

		assertEquals(1, unplan.getId());
		assertEquals(1, unplan.getSerialId());
		assertEquals("TEST_NAME2", unplan.getName());
		assertEquals("TEST_NOTES2", unplan.getNotes());
		assertEquals(5, unplan.getEstimate());
		assertEquals(1, unplan.getActual());

		try {
			unplan.reload();

			assertEquals(1, unplan.getId());
			assertEquals(1, unplan.getSerialId());
			assertEquals("TEST_NAME", unplan.getName());
			assertEquals("TEST_NOTES", unplan.getNotes());
			assertEquals(10, unplan.getEstimate());
			assertEquals(10, unplan.getActual());
		} catch (Exception e) {
		}
	}

	/**
	 * 之後要拔掉,為了符合目前的 IIssue
	 */
	@Test
	public void testGetStatusString() {
		// create a unplan
		UnplanObject unplan = new UnplanObject(sSprintId, sProjectId);
		unplan.setName("TEST_NAME").setNotes("TEST_NOTES").setEstimate(10)
			.setActual(0).save();
		
		// check unplan status before test
		assertEquals("new", unplan.getStatusString());
		// check unplan status after check it out
		unplan.setStatus(UnplanObject.STATUS_CHECK);
		assertEquals("assigned", unplan.getStatusString());
		// check unplan status after close it
		unplan.setStatus(UnplanObject.STATUS_DONE);
		assertEquals("closed", unplan.getStatusString());
		// check unplan status after reopen it
		unplan.setStatus(UnplanObject.STATUS_UNCHECK);
		assertEquals("new", unplan.getStatusString());
	}
}
