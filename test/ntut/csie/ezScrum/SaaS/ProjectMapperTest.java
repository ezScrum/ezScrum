package ntut.csie.ezScrum.SaaS;

import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;

import junit.framework.TestCase;
import ntut.csie.ezScrum.SaaS.PMF;
import ntut.csie.ezScrum.SaaS.database.ProjectDataStore;
import ntut.csie.ezScrum.SaaS.util.DateUtil;
import ntut.csie.ezScrum.web.form.ProjectInfoForm;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.jcis.project.core.IProjectDescription;
import ntut.csie.jcis.resource.core.IProject;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;


public class ProjectMapperTest extends TestCase {

	// GAE Mock DB in memory
	private final LocalServiceTestHelper dbTestHelper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

	//
	private String projectId_Name = "testProject";	// ID = NAME
	private String projectDisplayName = "testProjectDisplayName";
	private String projectComment = "testProject comment";
	private String projectPM = "testProject PM";
	private Date projectCreateDate;

	public ProjectMapperTest(String method) {
		super(method);
	}

	protected void setUp() throws Exception {
		super.setUp();
		dbTestHelper.setUp();
		// 
	}

	protected void tearDown() throws Exception {
		//
		dbTestHelper.tearDown();
		super.tearDown();
	}

	public void testCreateProject() throws Exception {
		ProjectInfoForm form = this.fillForm();		
		ProjectMapper pm = new ProjectMapper();		
		pm.createProject(null, null, form);	// test this
		
		// 直接從資料庫取出來比對
		ProjectDataStore pds = this.getProjectDS(this.projectId_Name);	// id 即是 name
				
		assertEquals(this.projectId_Name, pds.getName());
		assertEquals(this.projectDisplayName, pds.getDisplayName());
		assertEquals(this.projectComment, pds.getComment());
		assertEquals(this.projectPM, pds.getManager());
		assertEquals(this.projectCreateDate.getTime()/1000, pds.getCreateDate().getTime()/1000);	// 末三碼不比對		
	}
	
	public void testUpdateProject() throws Exception {						
		ProjectInfoForm form = this.fillForm();				
		ProjectMapper pm = new ProjectMapper();
		int updatedIndex = 123;
		
		pm.createProject(null, null, form);	// modify...		
		this.fillFormByIndex(form, updatedIndex);
		form.setName(this.projectId_Name);	// ID 不能改
		form.setCreateDate(this.projectCreateDate);	// CreateDate 不能改
		pm.updateProject(form);
		
		// 直接從資料庫取出來比對
		ProjectDataStore pds = this.getProjectDS(this.projectId_Name);	// id 即是 name
				
		assertEquals(this.projectId_Name, pds.getName());
		assertEquals(this.projectDisplayName + "_" + updatedIndex, pds.getDisplayName());
		assertEquals(this.projectComment + "_" + updatedIndex, pds.getComment());
		assertEquals(this.projectPM + "_" + updatedIndex, pds.getManager());	
		assertEquals(this.projectCreateDate.getTime()/1000, pds.getCreateDate().getTime()/1000);	// 末三碼不比對
	}	
	
	public void testGetProjectByID() throws Exception {
		ProjectInfoForm form = this.fillForm();		
		ProjectMapper pm = new ProjectMapper();		
		pm.createProject(null, null, form);	// modify...
		IProject project = pm.getProjectByID(this.projectId_Name);	// test this
		IProjectDescription expectPd = project.getProjectDesc();
		
		// 直接從資料庫取出來比對
		ProjectDataStore actualPds = this.getProjectDS(this.projectId_Name);	// id 即是 name		
		compareResult(expectPd, actualPds);
	}

	public void testgetAllProjectList() throws Exception {		
		ProjectMapper pm = new ProjectMapper();		
		int testProjectCount = 3;
		
		// 創建多個專案
		for (int i=1; i<=testProjectCount; i++)
		{
			ProjectInfoForm form = new ProjectInfoForm();			
			this.fillFormByIndex(form, i);
			pm.createProject(null, null, form);	// modify...
			
			System.out.println("project ID = " + form.getName());
		}

		// 測試專案數量是否正確 
		List<IProject> expectedList = pm.getAllProjectList();	// test this
		assertEquals(testProjectCount, expectedList.size());
		
		System.out.println("project size = " + expectedList.size());
				
		// 一一從資料庫取出來比對
		for (int i=0; i<testProjectCount; i++)
		{
			IProjectDescription expectPd = expectedList.get(i).getProjectDesc();
			ProjectDataStore actualPds = this.getProjectDS(this.projectId_Name + "_" + (i+1));	// id 即是 name		

			compareResult(expectPd, actualPds);		
		}
							
	}
	
	
	/*
	 * local use
	 */
	private ProjectInfoForm fillForm() {
		ProjectInfoForm form = new ProjectInfoForm();
		
		form.setName(this.projectId_Name);	// ID
		form.setDisplayName(this.projectDisplayName);
		form.setComment(this.projectComment);
		form.setProjectManager(this.projectPM);
		this.projectCreateDate = DateUtil.getNowDate();
		form.setCreateDate(this.projectCreateDate);
		
		return form;
	}
	
	private void fillFormByIndex(ProjectInfoForm form, int index) {		
		form.setName(this.projectId_Name + "_" + index);	// ID
		form.setDisplayName(this.projectDisplayName + "_" + index);
		form.setComment(this.projectComment + "_" + index);
		form.setProjectManager(this.projectPM + "_" + index);
		form.setCreateDate(DateUtil.getNowDate());
	}	

	private ProjectDataStore getProjectDS(String projectId) {
		PersistenceManager pm = PMF.get().getPersistenceManager();

		Key projectKey = KeyFactory.createKey(ProjectDataStore.class.getSimpleName(), projectId);
		ProjectDataStore projectDS;
		
		try {
			projectDS = pm.getObjectById(ProjectDataStore.class, projectKey);

		} finally {
			pm.close();
		}
		
		return projectDS;
	}
	
	private void compareResult(IProjectDescription expectPd, ProjectDataStore actualPds) {
		// 
		assertEquals(expectPd.getName(), actualPds.getName());	// ID
		assertEquals(expectPd.getDisplayName(), actualPds.getDisplayName());
		assertEquals(expectPd.getComment(), actualPds.getComment());
		assertEquals(expectPd.getProjectManager(), actualPds.getManager());
		//有時候比對內容一致但assert failed -> 不要比對物件 Date!!
		long expectTime = expectPd.getCreateDate().getTime()/1000;
		long actualTime = actualPds.getCreateDate().getTime()/1000;		
		
		System.out.println("expect time = " + expectTime);
		System.out.println("actual time = " + actualTime);
		
		assertEquals(expectTime, actualTime);	// 末三碼不比對
	}
}
