package ntut.csie.ezScrum.web.mapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.iteration.iternal.ReleasePlanDesc;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.jcis.core.util.XmlFileUtil;
import ntut.csie.jcis.resource.core.IProject;
import ntut.csie.jcis.resource.core.IWorkspaceRoot;

import org.jdom.Document;
import org.jdom.Element;

public class ReleasePlanMapper {
	/*
	 * merge Loader & Saver
	 */
	private final String RELEASE_PLAN_FILE = ScrumEnum.RELEASE_PLAN_FILE;	// releasePlan.xml	
	private ProjectObject mProject;

	public ReleasePlanMapper(ProjectObject project) {
		mProject = project;
	}	
	
	public void addReleasePlan(IReleasePlanDesc desc) {
		String prefsPath = getUsrMetadataPath() + File.separator + RELEASE_PLAN_FILE;
		Document doc = saverLoadElement();
		if (doc == null) {
			doc = new Document(new Element(ScrumEnum.RELEASES));
		}
		
		Element root = doc.getRootElement();
		List<Element> elements = root.getChildren();	// get all elements

		for (Element sprint : elements) {
			if (sprint.getAttribute(ScrumEnum.ID_Release_ATTR).getValue().equals(desc.getID())) {
				return ;
			}
		}
		
		// 增加新的 Release Element
		addReleaseElem(root, desc);

		XmlFileUtil.SaveXmlFile(prefsPath, doc);
	}
		
	public List<IReleasePlanDesc> getReleasePlanList() {
		List<IReleasePlanDesc> list = new ArrayList<IReleasePlanDesc>();
		Element root = loadElement();
		@SuppressWarnings("unchecked")
		List<Element> releases = root.getChildren(ScrumEnum.RELEASE_TAG);
		
		// 連結SprintList動作移到ReleaesPlanHelper
		for (Element release : releases)
			list.add(getReleasePlanDesc(release));
		
		return list;
	}	
	
	// 修改 release plan
	public void updateReleasePlan(IReleasePlanDesc desc) {
		String prefsPath = getUsrMetadataPath() + File.separator + RELEASE_PLAN_FILE;
		Document doc = saverLoadElement();
		if (doc == null) {
			doc = new Document(new Element(ScrumEnum.RELEASES));
		}
		
		Element root = doc.getRootElement();			// get root
		List<Element> elements = root.getChildren();	// get all elements
		
		for (Element release : elements) {
			// update the all informations
			if (release.getAttribute(ScrumEnum.ID_Release_ATTR).getValue().equals(desc.getID())) {
				List<Element> name = release.getChildren(ScrumEnum.RELEASE_NAME);
				name.get(0).setText(desc.getName());

				List<Element> startdate = release.getChildren(ScrumEnum.RELEASE_START_DATE);
				startdate.get(0).setText(desc.getStartDate());
				
				List<Element> enddate = release.getChildren(ScrumEnum.RELEASE_END_DATE);
				enddate.get(0).setText(desc.getEndDate());
				
				List<Element> description = release.getChildren(ScrumEnum.RELEASE_DESCRIPTION);
				description.get(0).setText(desc.getDescription());
				
				break;
			}
		}
		
		XmlFileUtil.SaveXmlFile(prefsPath, doc);
	}
	
	// 刪除 Release
	public void deleteReleasePlan(String id) {
		String prefsPath = getUsrMetadataPath() + File.separator + RELEASE_PLAN_FILE;
		Document doc = saverLoadElement();
		
		if (doc == null) {
			doc = new Document(new Element(ScrumEnum.RELEASES));
		}
		
		Element root = doc.getRootElement();

		List<Element> descs = root.getChildren(ScrumEnum.RELEASE_TAG);
		for (Element desc : descs) {
			if (desc.getAttribute(ScrumEnum.ID_Release_ATTR).getValue().equals(id)) {
				root.removeContent(desc);
				break;
			}
		}
		
		XmlFileUtil.SaveXmlFile(prefsPath, doc);
	}		
	
	private String getUsrMetadataPath() {
		IProject iProject = new ProjectMapper().getProjectByID(mProject.getName());
		String theUsrFile = iProject.getFolder(IProject.METADATA).getFullPath().toString();
		return theUsrFile;
	}	
		
	private IReleasePlanDesc getReleasePlanDesc(Element element) {
		IReleasePlanDesc desc = new ReleasePlanDesc();
	
		desc.setID(element.getAttributeValue(ScrumEnum.ID_Release_ATTR));
		desc.setName(element.getChildText(ScrumEnum.RELEASE_NAME));
		desc.setStartDate(element.getChildText(ScrumEnum.RELEASE_START_DATE));
		desc.setEndDate(element.getChildText(ScrumEnum.RELEASE_END_DATE));
		desc.setDescription(element.getChildText(ScrumEnum.RELEASE_DESCRIPTION));
		
		// 連結SprintList動作移到ReleaesPlanHelper
		
		// 自動尋找此 release date 內的 sprint plan
//		List<ISprintPlanDesc> ReleaseSprintList = new LinkedList<ISprintPlanDesc>();
//		SprintPlanMapper spMapper = new SprintPlanMapper(m_project);
//		List<ISprintPlanDesc> AllSprintList = spMapper.getSprintPlanList();
//		
//		Date releaseStartDate = DateUtil.dayFilter(desc.getStartDate());	// release start date get from XML
//		Date releaseEndDate = DateUtil.dayFilter(desc.getEndDate());		// release end date get from XML
//		
//		for (ISprintPlanDesc sprintDesc : AllSprintList) {
//			Date sprintStartDate = DateUtil.dayFilter(sprintDesc.getStartDate());
//			Date sprintEndDate = DateUtil.dayFilter(sprintDesc.getEndDate());
//			
//			// 判斷 sprint plan 日期是否為 release plan 內的日期
//			if (sprintStartDate.compareTo(releaseStartDate) >= 0) {
//				if (releaseEndDate.compareTo(sprintEndDate) >= 0) {						
//					ReleaseSprintList.add(sprintDesc);			// add to the list
//				}
//			}
//		}
//		
//		desc.setSprintDescList(ReleaseSprintList);
		
		return desc;
	}

	private Element loadElement() {
		String prefsPath = getUsrMetadataPath() + File.separator + RELEASE_PLAN_FILE;
		if (!new File(prefsPath).exists())
			prefsPath = getSysMetadataPath() + File.separator + RELEASE_PLAN_FILE;
		
		Document doc = XmlFileUtil.LoadXmlFile(prefsPath);
		if (doc == null)
			return null;
		
		return doc.getRootElement();
	}
	

	private String getSysMetadataPath() {
		IProject iProject = new ProjectMapper().getProjectByID(mProject.getName());
		IWorkspaceRoot m_workspaceRoot = iProject.getWorkspaceRoot();
		String theSysFile = m_workspaceRoot.getFolder(IProject.METADATA).getFullPath().toString();

		return theSysFile;
	}


	
	// 新增一筆 Release
	private void addReleaseElem(Element root, IReleasePlanDesc desc) {
		Element Release = new Element(ScrumEnum.RELEASE_TAG);
		Release.setAttribute(ScrumEnum.ID_Release_ATTR, desc.getID());

		Element Name = new Element(ScrumEnum.RELEASE_NAME);
		Name.setText(desc.getName());
		
		Element ST_Date = new Element(ScrumEnum.RELEASE_START_DATE);
		ST_Date.setText(desc.getStartDate());
		
		Element ED_Date = new Element(ScrumEnum.RELEASE_END_DATE);
		ED_Date.setText(desc.getEndDate());
		
		Element Description = new Element(ScrumEnum.RELEASE_DESCRIPTION);
		Description.setText(desc.getDescription());

		Release.addContent(Name);
		Release.addContent(ST_Date);
		Release.addContent(ED_Date);
		Release.addContent(Description);

		root.addContent(Release);
	}

	private Document saverLoadElement() {
		String prefsPath = getUsrMetadataPath() + File.separator + RELEASE_PLAN_FILE;
		if ( ! new File(prefsPath).exists())	return null;
		Document doc = XmlFileUtil.LoadXmlFile(prefsPath);

		return doc;
	}
	
}
