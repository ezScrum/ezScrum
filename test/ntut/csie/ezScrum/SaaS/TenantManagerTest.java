package ntut.csie.ezScrum.SaaS;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;

import junit.framework.TestCase;
import ntut.csie.ezScrum.SaaS.database.TenantDataStore;
import ntut.csie.ezScrum.SaaS.multitenancy.RentService;
import ntut.csie.ezScrum.SaaS.multitenancy.TenantManager;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class TenantManagerTest extends TestCase{
	// GAE Mock DB in memory
	private final LocalServiceTestHelper dbTestHelper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	private TenantManager tenantManager;
	
	//	Tenant Information
	private String tenantId = "testTanant";
	private String tenantName = "testTenantName";
	private String tenantDescription = "testTenantDescription";
	
	//	Rent Information
	private RentService rentService;
	private String rentServiceId = "testRentService";
	private String rentServiceAdminName = "testRentServiceAdmin";
	private boolean rentServiceEnable = true;
	
	
	
	public TenantManagerTest(String method) {
		super(method);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		dbTestHelper.setUp();
		// 
		this.tenantManager = new TenantManager();
		this.rentService = new RentService(rentServiceId, rentServiceAdminName, rentServiceEnable);
	}

	protected void tearDown() throws Exception {
		//
		dbTestHelper.tearDown();
		super.tearDown();
		
		this.rentService = null;
		this.tenantManager = null;
	}
	
	public void testAddtenant(){
		this.tenantManager.addTenant(tenantId, tenantName, tenantDescription, rentService);
		
		// 直接從資料庫取出來比對
		TenantDataStore tenantDS = this.getTenantDS(this.tenantId);	// id 即是 name
		RentService rs = tenantDS.getRentService();
		
		//	assert Tenant Information
		assertEquals(this.tenantId, tenantDS.getTenantId());
		assertEquals(this.tenantName, tenantDS.getTenantName());
		assertEquals(this.tenantDescription, tenantDS.getDescription());
		
		//	assert Rent Service Information
		assertEquals(this.rentServiceId, rs.getId());
		assertEquals(this.rentServiceAdminName, rs.getAdminName());
		assertEquals(this.rentServiceEnable, tenantDS.getEnable());
	}
	
	public void testStopTenant(){
		this.createTenant(this.tenantId, this.tenantName, this.tenantDescription, this.rentService);
		
		this.tenantManager.stopTenant(this.tenantId);
		
		// 直接從資料庫取出來比對
		TenantDataStore tenantDS = this.getTenantDS(this.tenantId);	// id 即是 name
		RentService rs = tenantDS.getRentService();
		boolean expectedEnable = false;
		
		//	assert Tenant Information
		assertEquals(this.tenantId, tenantDS.getTenantId());
		assertEquals(this.tenantName, tenantDS.getTenantName());
		assertEquals(this.tenantDescription, tenantDS.getDescription());
		
		//	assert Rent Service Information
		assertEquals(this.rentServiceId, rs.getId());
		assertEquals(this.rentServiceAdminName, rs.getAdminName());
		assertEquals(expectedEnable, tenantDS.getEnable());
	}
	
	public void testRenewTenant(){
		this.rentService = new RentService(rentServiceId, rentServiceAdminName, false);
		this.createTenant(this.tenantId, this.tenantName, this.tenantDescription, this.rentService);
		
		this.tenantManager.renewTenant(this.tenantId);
		
		// 直接從資料庫取出來比對
		TenantDataStore tenantDS = this.getTenantDS(this.tenantId);	// id 即是 name
		RentService rs = tenantDS.getRentService();
		boolean expectedEnable = true;
		
		//	assert Tenant Information
		assertEquals(this.tenantId, tenantDS.getTenantId());
		assertEquals(this.tenantName, tenantDS.getTenantName());
		assertEquals(this.tenantDescription, tenantDS.getDescription());
		
		//	assert Rent Service Information
		assertEquals(this.rentServiceId, rs.getId());
		assertEquals(this.rentServiceAdminName, rs.getAdminName());
		assertEquals(expectedEnable, tenantDS.getEnable());
	}
	
	public void testGetTenant(){
		this.createTenant(this.tenantId, this.tenantName, this.tenantDescription, this.rentService);
		
		TenantDataStore tenantDS = this.tenantManager.getTenant(this.tenantId);
		
		// 直接從資料庫取出來比對
		RentService rs = tenantDS.getRentService();
		
		//	assert Tenant Information
		assertEquals(this.tenantId, tenantDS.getTenantId());
		assertEquals(this.tenantName, tenantDS.getTenantName());
		assertEquals(this.tenantDescription, tenantDS.getDescription());
		
		//	assert Rent Service Information
		assertEquals(this.rentServiceId, rs.getId());
		assertEquals(this.rentServiceAdminName, rs.getAdminName());
		assertEquals(this.rentServiceEnable, tenantDS.getEnable());
	}
	
	public void testUpdateTenant(){
		this.createTenant(this.tenantId, this.tenantName, this.tenantDescription, this.rentService);
		
		//	Tenant Information
		String tenantName_update = "testTenantName_update";
		String tenantDescription_update = "testTenantDescription_update";
		
		//	Rent Information
		boolean rentServiceEnable_update = false;
		RentService rentService_update = new RentService(this.rentServiceId, this.rentServiceAdminName, rentServiceEnable_update); 
		this.tenantManager.updateTenant(this.tenantId, tenantName_update, tenantDescription_update, rentService_update);
		
		// 直接從資料庫取出來比對
		TenantDataStore tenantDS = this.getTenantDS(this.tenantId);	// id 即是 name
		RentService rs = tenantDS.getRentService();
		
		//	assert Tenant Information
		assertEquals(this.tenantId, tenantDS.getTenantId());
		assertEquals(tenantName_update, tenantDS.getTenantName());
		assertEquals(tenantDescription_update, tenantDS.getDescription());
		
		//	assert Rent Service Information
		assertEquals(this.rentServiceId, rs.getId());
		assertEquals(this.rentServiceAdminName, rs.getAdminName());
		assertEquals(rentServiceEnable_update, tenantDS.getEnable());
	}
	
	public void testGetTenantList(){
		List<String> tenantIdList = new ArrayList<String>();
		List<String> tenantNameList = new ArrayList<String>();
		List<String> tenantDescriptionList = new ArrayList<String>();
		List<String> rentServiceIdList = new ArrayList<String>();
		List<String> rentServiceAdminNameList = new ArrayList<String>();
		List<Boolean> rentServiceEnableList = new ArrayList<Boolean>();
		for( int i = 1; i <= 2; i++ ){
			//	Tenant Information
			tenantIdList.add(this.tenantId + "_" +  (i+1));
			tenantNameList.add(this.tenantName + "_" +  (i+1));
			tenantDescriptionList.add(this.tenantDescription + "_" +  (i+1));
			
			//	Rent Information
			rentServiceIdList.add(this.rentServiceId + "_" +  (i+1));
			rentServiceAdminNameList.add(this.rentServiceAdminName + "_" +  (i+1));
			rentServiceEnableList.add(true);
			RentService rentService = new RentService(rentServiceIdList.get(i-1), rentServiceAdminNameList.get(i-1), rentServiceEnableList.get(i-1));
			
			this.createTenant(tenantIdList.get(i-1), tenantNameList.get(i-1), tenantDescriptionList.get(i-1), rentService);
		}
		
		List<TenantDataStore> tenantDSList = this.tenantManager.getTenantList();
		
		//	assert
		for( int i = 0; i < tenantDSList.size(); i++ ){
			TenantDataStore tenantDS = tenantDSList.get(i);
			RentService rs = tenantDSList.get(i).getRentService();
			//	assert Tenant Information
			assertEquals(tenantIdList.get(i), tenantDS.getTenantId());
			assertEquals(tenantNameList.get(i), tenantDS.getTenantName());
			assertEquals(tenantDescriptionList.get(i), tenantDS.getDescription());
			
			//	assert Rent Service Information
			assertEquals(rentServiceIdList.get(i), rs.getId());
			assertEquals(rentServiceAdminNameList.get(i), rs.getAdminName());
			assertEquals(rentServiceEnableList.get(i), Boolean.valueOf(tenantDS.getEnable()));
		}
	}
	
	/**
	 * 驗證Tenant是否存在。
	 * 1. id existed
	 * 1. id does not existed
	 */
	public void testIsTenantExist(){
		this.createTenant(this.tenantId, this.tenantName, this.tenantDescription, this.rentService);
		
		//	assert id existed
		boolean isExisted = this.tenantManager.isTenantExist(this.tenantId);
		assertEquals(true, isExisted);
		
		//	assert id does not existed
		isExisted = this.tenantManager.isTenantExist(this.tenantId + "1");
		assertEquals(false, isExisted);
	}
	
	private TenantDataStore getTenantDS(String tenantId) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Key tenantKey = KeyFactory.createKey(TenantDataStore.class.getSimpleName(), tenantId);
		TenantDataStore tenantDS;
		try {
			tenantDS = pm.getObjectById(TenantDataStore.class, tenantKey);

		} finally {
			pm.close();
		}
		
		return tenantDS;
	}
	
	private void createTenant( String tenantId, String tenantName, String tenantDescription, RentService rentService){
		PersistenceManager pm = PMF.get().getPersistenceManager();

		Key key = KeyFactory.createKey(TenantDataStore.class.getSimpleName(), tenantId);
		TenantDataStore tenant = new TenantDataStore(key);
		tenant.setTenantId(tenantId);
		tenant.setTenantname(tenantName);
		tenant.setDescription(tenantDescription);	
		tenant.setRentService(rentService);
		
		try {
			pm.makePersistent(tenant);
		} finally {
			pm.close();
		}	
	}
}
