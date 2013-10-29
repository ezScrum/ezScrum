package ntut.csie.ezScrum.SaaS.aspect;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.SaaS.PMF;
import ntut.csie.ezScrum.SaaS.database.AccountDataStore;
import ntut.csie.ezScrum.SaaS.database.TenantDataStore;
import ntut.csie.ezScrum.SaaS.multitenancy.TenantMapper;
import ntut.csie.ezScrum.SaaS.util.ScrumEnum;
import ntut.csie.ezScrum.SaaS.util.ezScrumUtil;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.LogonAction;
import ntut.csie.ezScrum.web.action.LogoutAction;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.ezScrum.web.mapper.ProductBacklogMapper;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.ezScrum.web.mapper.ReleasePlanMapper;
import ntut.csie.ezScrum.web.mapper.ScrumRoleMapper;
import ntut.csie.ezScrum.web.mapper.SprintPlanMapper;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.jcis.account.core.LogonException;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.google.appengine.api.NamespaceManager;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;


public aspect MultiTenantAspect {
	private String _tenantId = "";
	private TenantMapper _tenantMpr = new TenantMapper();
	
	//pointcuts
	pointcut executeLogonActionPT(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) 
	: execution(ActionForward LogonAction.execute(ActionMapping, ActionForm, HttpServletRequest, HttpServletResponse)) && args(mapping, form, request, response);
	
	ActionForward around(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws LogonException
	: executeLogonActionPT(mapping, form, request, response) {
		System.out.println("replaced by AOP...executeLogonActionPT in MultiTenantAspect: " + thisJoinPoint);
		
		_tenantId = (String) request.getParameter("tenantId");
		if (_tenantId == null) _tenantId = "";
		
		if (!_tenantId.equals("")) {
			TenantDataStore tenant = _tenantMpr.getTenant(_tenantId);
			if( (tenant==null) || (!tenant.getEnable()) ){
				return mapping.findForward("InvalidTenancy");
			}
//			if (tenant == null){
//				throw new LogonException(false, false);
//			}
		}
		
		//SessionManager sessionMgr = new SessionManager(request);
		//sessionMgr.setSessionAttribute(TenantManager.TENANT_SESSION_NAME, _tenantId);
		
		return proceed(mapping, form, request, response);
	}
	
	pointcut executeLogoutSubmitPT(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) 
	: execution(ActionForward LogoutAction.execute(ActionMapping, ActionForm, HttpServletRequest, HttpServletResponse)) && args(mapping, form, request, response);
	
	ActionForward around(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) 
	: executeLogoutSubmitPT(mapping, form, request, response){
		System.out.println("replaced by AOP...executeLogoutSubmitPT in MultiTenantAspect: " + thisJoinPoint);
		
		request.setAttribute("tenantId", _tenantId);
		return proceed(mapping, form, request, response); 
	}
	
	/**
	 * Account Mapper
	 */
	pointcut AccountMapperConstructorPT()
	: call(public AccountMapper.new());
	
	before() : AccountMapperConstructorPT() {
		System.out.println("AccountMapperConstructorPT-tenantId = "+_tenantId);
		
		NamespaceManager.set(_tenantId);
		
		if (_tenantId.equals("")) {
			String id = "admin";
			String password = ezScrumUtil.getMd5("admin");
			
			PersistenceManager pm = PMF.get().getPersistenceManager();
			Key key = KeyFactory.createKey(AccountDataStore.class.getSimpleName(), id);

			try {
				pm.getObjectById(AccountDataStore.class, key);
			} 
			catch (JDOObjectNotFoundException e) {
				AccountDataStore accountData = new AccountDataStore(key, id, password);
				accountData.setName(id);
				accountData.setEnable(String.valueOf(true));
				
				List<String> permissions = new ArrayList<String>();
				permissions.add(ScrumEnum.ADMINISTRATOR_PERMISSION);
				permissions.add(ScrumEnum.TENANT_PERMISSION);
				accountData.setPermissions(permissions);
				pm.makePersistent(accountData);
			}
			finally {
				pm.close();
			}
		}
	}
	
	/**
	 * Project Mapper
	 */
	pointcut ProjectMapperConstructorPT()
	: call(public ProjectMapper.new());
	
	before() : ProjectMapperConstructorPT() {
		System.out.println("ProjectMapperConstructorPT-tenantId = "+_tenantId);
		NamespaceManager.set(_tenantId);
	}
	
	/**
	 * Product Backlog Mapper
	 * replace : public ProductBacklogMapper(IProject project, IUserSession userSession)
	 * @param projectId
	 */
	
	pointcut ProductBacklogMapperConstructorPT(IProject project, IUserSession userSession)
	: call(public ProductBacklogMapper.new(IProject, IUserSession)) && args(project, userSession);
	
	before(IProject project, IUserSession userSession)
	: ProductBacklogMapperConstructorPT(project, userSession){
		System.out.println("ProductBacklogMapperConstructorPT-tenantId = "+_tenantId);
		NamespaceManager.set(_tenantId);
	}
	
	/**
	 * Sprint Plan Mapper
	 * @param projectId
	 */
	//	replace: public SprintPlanMapper(IProject project) 
	pointcut SprintPlanMapperConstructorPT(IProject project) 
	: call(public SprintPlanMapper.new(IProject)) && args(project);
	
	before(IProject project) 
	: SprintPlanMapperConstructorPT(project){
		System.out.println("SprintPlanMapperConstructorPT-tenantId = "+_tenantId);
		NamespaceManager.set(_tenantId);
	}
	
	/**
	 * Scrum Role Mapper
	 */
	pointcut ScrumRoleMapperConstructorPT()
	: call(public ScrumRoleMapper.new()) && args();
	
	before()
	: ScrumRoleMapperConstructorPT(){
		System.out.println("ScrumRoleMapperConstructorPT-tenantId = "+_tenantId);
		NamespaceManager.set(_tenantId);
	}
	
	/**
	 * Release Plan Mapper
	 * @param project
	 */
	pointcut ReleasePlanMapperConstructorPT(IProject project)
	: call(public ReleasePlanMapper.new(IProject)) && args(project);
	
	before(IProject project)
	: ReleasePlanMapperConstructorPT(project){
		System.out.println("ReleasePlanMapperConstructorPT-tenantId = "+_tenantId);
		NamespaceManager.set(_tenantId);
	}
	
	/**
	 * Sprint Backlog Mapper
	 * @param project
	 */
	pointcut SprintBacklogMapperConstructor1PT(IProject project, IUserSession userSession)
	: call(public SprintBacklogMapper.new(IProject, IUserSession)) && args(project, userSession);
	
	before(IProject project, IUserSession userSession)
	: SprintBacklogMapperConstructor1PT(project, userSession){
		System.out.println("SprintBacklogMapperConstructor1PT-tenantId = "+_tenantId);
		NamespaceManager.set(_tenantId);
	}
	
	/**
	 * Sprint Backlog Mapper
	 * @param project
	 */
	pointcut SprintBacklogMapperConstructor2PT(IProject project, IUserSession userSession, int sprintId)
	: call(public SprintBacklogMapper.new(IProject, IUserSession, int)) && args(project, userSession, sprintId);
	
	before(IProject project, IUserSession userSession, int sprintId)
	: SprintBacklogMapperConstructor2PT(project, userSession, sprintId){
		System.out.println("SprintBacklogMapperConstructor2PT-tenantId = "+_tenantId);
		NamespaceManager.set(_tenantId);
	}
	
//	pointcut TaskMapperConstructorPT(String projectId)
//	: execution(public TaskMapper.new(String)) && args(projectId);
//	
//	before(String projectId)
//	: TaskMapperConstructorPT(projectId){
//		log.info("TaskMapperConstructorPT-tenantId = "+_tenantId);
//		NamespaceManager.set(_tenantId);
//	}
//	
//	pointcut ScrumIssueMapperConstructorPT(String projectId)
//	: execution(public ScrumIssueMapper.new(String)) && args(projectId);
//	
//	before(String projectId)
//	: ScrumIssueMapperConstructorPT(projectId){
//		log.info("ScrumIssueMapperConstructorPT-tenantId = "+_tenantId);
//		NamespaceManager.set(_tenantId);
//	}
//	
//	pointcut ExtensionFieldMapperConstructorPT()
//	: execution(public ExtensionFieldMapper.new()) && !within(TenantManager);
//	
//	before()
//	: ExtensionFieldMapperConstructorPT(){
//		log.info("ExtensionFieldMapperConstructorPT-tenantId = "+_tenantId);
//		NamespaceManager.set(_tenantId);
//	}
//	
//	pointcut CustomFieldMapperConstructorPT()
//	: execution(public CustomFieldMapper.new());
//	
//	before()
//	: CustomFieldMapperConstructorPT(){
//		log.info("CustomFieldMapperConstructorPT-tenantId = "+_tenantId);
//		NamespaceManager.set(_tenantId);
//	}
}
