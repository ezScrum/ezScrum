package ntut.csie.ezScrum.SaaS.interfaces.pic.internal;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ntut.csie.ezScrum.web.form.ProjectInfoForm;
import ntut.csie.jcis.core.QualifiedName;
import ntut.csie.jcis.project.core.IBuilder;
import ntut.csie.jcis.project.core.IProjectBuildInfo;
import ntut.csie.jcis.project.core.IProjectBuilderCatalog;
import ntut.csie.jcis.project.core.IProjectDescription;
import ntut.csie.jcis.project.core.internal.ProjectDescription;
import ntut.csie.jcis.report.core.IIntegrationReport;
import ntut.csie.jcis.resource.core.IContainer;
import ntut.csie.jcis.resource.core.IFile;
import ntut.csie.jcis.resource.core.IFolder;
import ntut.csie.jcis.resource.core.IPath;
import ntut.csie.jcis.resource.core.IProject;
import ntut.csie.jcis.resource.core.IReportResource;
import ntut.csie.jcis.resource.core.IResource;
import ntut.csie.jcis.resource.core.IResourceVisitor;
import ntut.csie.jcis.resource.core.IWorkspaceRoot;
import ntut.csie.jcis.resource.core.internal.BackupProjectException;
import ntut.csie.ezScrum.pic.core.ScrumRole;

public class Project implements IProject {
	
	private static final long serialVersionUID = 1912365782632305694L;
	private String projectId = "";
	private String displayName = "";	
	private String Comment = "";
	private String Manager = "";
	private Date CreateDate = null;
	public Map<String, ScrumRole> scrumRoles = new HashMap<String, ScrumRole>();
	// follow ori.
	private IProjectDescription description = null; 
	
	public Project(String projectId) {
		this.projectId = projectId;
		// follow ori.
		this.description = new ProjectDescription(this.projectId);	
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setProjectId(String projectid) {
		projectId = projectid;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setComment(String comment) {
		Comment = comment;
	}

	public String getComment() {
		return Comment;
	}

	public void setManager(String manager) {
		Manager = manager;
	}

	public String getProjectManager() {
		return Manager;
	}

	public void setCreateDate(Date createDate) {
		CreateDate = createDate;
	}

	public Date getCreateDate() {
		return CreateDate;
	}

	public ScrumRole getScrumRole(String roleName) {
		// 租戶下的系統管理員 
//		if (roleName.equals(ScrumEnum.SCRUMROLE_ADMINISTRATOR))
//			return new ScrumRole(ScrumEnum.SCRUMROLE_ADMINISTRATOR);

		// Scrum 的角色
		return scrumRoles.get(roleName);
	}

	public void setScrumRole(String roleName, ScrumRole scrumRole) {
		scrumRoles.put(roleName, scrumRole);
	}

	// follow ori.
	public void setDescription(ProjectInfoForm form) {	
		this.description.setComment(form.getComment());
		this.description.setProjectManager(form.getProjectManager());
		this.description.setDisplayName(form.getDisplayName());
		this.description.setVersion(form.getVersion());	// ?
		this.description.setState(form.getState());	// ?
//		this.description.setOutput(convertStringToOutPath(form.getOutputPath()));
//		this.description.setSrc(convertStringToSourcePath(form.getSourcePaths()));
		
		// attach file
        String fileSize = form.getAttachFileSize();
        //如果fileSize沒有填值的話，則自動填入2
        if(fileSize.compareTo("")==0)
        	this.description.setAttachFileSize("2");
        else
        	this.description.setAttachFileSize(form.getAttachFileSize());
	}
	 
	/*
	 * (non-Javadoc)
	 * @see ntut.csie.jcis.resource.core.IContainer#exists(ntut.csie.jcis.resource.core.IPath)
	 */

	public boolean exists(IPath path) {
		return true;
	}

	@Override
	public void addExecutedPath(String executedPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accept(IResourceVisitor visitor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addFile(IFile aFile) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void copy(String destination) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete() throws IOException {
		// TODO Auto-generated method stub
		
	}

	public boolean exists() {
		return true;
	}

	@Override
	public String getFileExtension() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPath getFullPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPath getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	// follow ori.
	public String getName() {
		// TODO Auto-generated method stub
		return this.projectId;
	}

	@Override
	public IContainer getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPath getPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPersistentProperty(QualifiedName key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPath getProjectRelativePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getSessionProperty(QualifiedName key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IWorkspaceRoot getWorkspaceRoot() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAccessible() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDerived() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isReadOnly() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void refreshLocal(int depth) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDerived(boolean isDerived) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPersistentProperty(QualifiedName key, String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSessionProperty(QualifiedName key, Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getAdapter(Class arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	// follow ori.
	public IProjectDescription getProjectDesc() {		
		return this.description;
	}

	// follow ori.
	public void setProjectDesc(IProjectDescription aProjDesc) {
		// TODO Auto-generated method stub
		this.description.setName(aProjDesc.getName());	// ID
		this.description.setDisplayName(aProjDesc.getDisplayName());
		this.description.setComment(aProjDesc.getComment());		
		this.description.setProjectManager(aProjDesc.getProjectManager());
		this.description.setCreateDate(aProjDesc.getCreateDate());
	}

	@Override
	public IProjectBuilderCatalog getProjectBuilderCatalog() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setProjectBuilderCatalog(IBuilder builder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IProjectBuildInfo getProjectBuildInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public void create() {
		// do nothing?
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IResource findMember(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFile getFile(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IFile> getFiles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFolder getFolder(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IFolder> getFolders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPath getConfigPath(String config) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResource[] members() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IIntegrationReport[] getIntegrationReports() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void backup(String target) throws BackupProjectException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IIntegrationReport getIntegrationReport(String integrationId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IReportResource getReport(String builderID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IReportResource[] getReportHistory(String builderID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IReportResource getDerivedResource(String builderID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IReportResource[] getDerivedResourceHistory(String builderID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		
	}
}
