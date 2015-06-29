package ntut.csie.ezScrum.web.mapper;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.iteration.iternal.SprintPlanDesc;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.core.util.XmlFileUtil;
import ntut.csie.jcis.resource.core.IProject;
import ntut.csie.jcis.resource.core.IWorkspaceRoot;

import org.jdom.Document;
import org.jdom.Element;

public class SprintPlanMapper {
	/*
	 * merge SprintPlanDescLoader & SprintPlanDescSaver
	 */
	private final String ITER_PLAN_FILE = ScrumEnum.ITER_PLAN_FILE;
	private IProject m_project;
	private String m_projectId;
	
	public SprintPlanMapper(ProjectObject project) {
		m_project = new ProjectMapper().getProjectByID(project.getName());
		m_projectId = m_project.getName();
		loaderLoadElement();
	}
	
	public SprintPlanMapper(IProject project) {
		m_project = project;
		m_projectId = m_project.getName();
		loaderLoadElement();
	}
	
	// ori name: save()
	public void addSprintPlan(ISprintPlanDesc desc) {
		String prefsPath = getUsrMetadataPath() + File.separator + ITER_PLAN_FILE;
		Document doc = loadElement();
		if (doc == null) {
			doc = new Document(new Element(ScrumEnum.PLAN_TAG));
		}
		
		Element root = doc.getRootElement();
		List<Element> elements = root.getChildren();	// get all elements

		for (Element sprint : elements) {
			if (sprint.getAttribute(ScrumEnum.ID_Release_ATTR).getValue().equals(desc.getID())) {
				return ;
			}
		}
		// 增加新的Sprint Element
		addSprintElem(root, desc);

		XmlFileUtil.SaveXmlFile(prefsPath, doc);
	}
	
	// ori name: load()
	public ISprintPlanDesc getSprintPlan(String sprintId) {
		Element root = loaderLoadElement();
		@SuppressWarnings("unchecked")
		List<Element> sprints = root.getChildren(ScrumEnum.SPRINT_TAG);
		for (Element sprint : sprints) {
			if (sprint.getAttribute(ScrumEnum.ID_ATTR).getValue().equals(sprintId)) {	
				// 此 sprint Id 為將要秀出來的
				return getDescription(sprint);
			}
		}
		return new SprintPlanDesc();
	}	

	public List<ISprintPlanDesc> getSprintPlanList() {
		List<ISprintPlanDesc> list = new ArrayList<ISprintPlanDesc>();
		Element root = loaderLoadElement();
		@SuppressWarnings("unchecked")
		List<Element> sprints = root.getChildren(ScrumEnum.SPRINT_TAG);
		for (Element sprint : sprints)
			list.add(getDescription(sprint));
				
		return list;	
	}		
	
	// ori name: editSprintPlanForActualCost()
	public void updateSprintPlanForActualCost(ISprintPlanDesc desc) {
		String prefsPath = getUsrMetadataPath() + File.separator + ITER_PLAN_FILE;
		Document doc = loadElement();
		if (doc == null) {
			doc = new Document(new Element(ScrumEnum.PLAN_TAG));
		}
		
		Element root = doc.getRootElement();
		List<Element> elements = root.getChildren();	// get all elements
		
		for (Element sprint : elements) {
			// update the all informations
			if (sprint.getAttribute(ScrumEnum.ID_Release_ATTR).getValue().equals(desc.getID())) {
				List<Element> goal = sprint.getChildren(ScrumEnum.GOAL_TAG);
				goal.get(0).setText(desc.getGoal());

				List<Element> interval = sprint.getChildren(ScrumEnum.INTERVAL_TAG);
				interval.get(0).setText(desc.getInterval());
				
				List<Element> MemberNumber = sprint.getChildren(ScrumEnum.NUMBER_MEMBER_TAG);
				MemberNumber.get(0).setText(desc.getMemberNumber());
				
				List<Element> FocusFactor = sprint.getChildren(ScrumEnum.FOCUSFACTOR_TAG);
				FocusFactor.get(0).setText(desc.getFocusFactor());
				
				List<Element> AvailableDays = sprint.getChildren(ScrumEnum.AVAILABLE_DAYS_TAG);
				AvailableDays.get(0).setText(desc.getAvailableDays());
				
				List<Element> StartDate = sprint.getChildren(ScrumEnum.START_DATE_TAG);
				StartDate.get(0).setText(desc.getStartDate());
				
				List<Element> Notes = sprint.getChildren(ScrumEnum.NOTES_TAG);
				Notes.get(0).setText(desc.getNotes());
				
				List<Element> DemoDate = sprint.getChildren(ScrumEnum.DEMO_DATE_TAG);
				DemoDate.get(0).setText(desc.getDemoDate());
				
				List<Element> DemoPlace = sprint.getChildren(ScrumEnum.DEMO_PLACE_TAG);
				DemoPlace.get(0).setText(desc.getDemoPlace());				
				
				List<Element> ActualCost = sprint.getChildren(ScrumEnum.ACTUAL_COST);
				
				if( ActualCost.isEmpty() ){    //iterPlan.xml外部檔案沒有該欄位則新增
					Element actualCost = new Element(ScrumEnum.ACTUAL_COST);
					actualCost.setText(desc.getActualCost());
					sprint.addContent(actualCost);

				}else{
					ActualCost.get(0).setText(desc.getActualCost());
				}
				break;
			}
		}
		
		XmlFileUtil.SaveXmlFile(prefsPath, doc);
	}
	
	// ori name: edit()
	public void updateSprintPlan(ISprintPlanDesc desc) {
		String prefsPath = getUsrMetadataPath() + File.separator + ITER_PLAN_FILE;
		Document doc = loadElement();
		if (doc == null) {
			doc = new Document(new Element(ScrumEnum.PLAN_TAG));
		}
		
		Element root = doc.getRootElement();
		List<Element> elements = root.getChildren();	// get all elements
		
		for (Element sprint : elements) {
			// update the all informations
			if (sprint.getAttribute(ScrumEnum.ID_Release_ATTR).getValue().equals(desc.getID())) {
				List<Element> goal = sprint.getChildren(ScrumEnum.GOAL_TAG);
				goal.get(0).setText(desc.getGoal());

				List<Element> interval = sprint.getChildren(ScrumEnum.INTERVAL_TAG);
				interval.get(0).setText(desc.getInterval());
				
				List<Element> MemberNumber = sprint.getChildren(ScrumEnum.NUMBER_MEMBER_TAG);
				MemberNumber.get(0).setText(desc.getMemberNumber());
				
				List<Element> FocusFactor = sprint.getChildren(ScrumEnum.FOCUSFACTOR_TAG);
				FocusFactor.get(0).setText(desc.getFocusFactor());
				
				List<Element> AvailableDays = sprint.getChildren(ScrumEnum.AVAILABLE_DAYS_TAG);
				AvailableDays.get(0).setText(desc.getAvailableDays());
				
				List<Element> StartDate = sprint.getChildren(ScrumEnum.START_DATE_TAG);
				StartDate.get(0).setText(desc.getStartDate());
				
				List<Element> Notes = sprint.getChildren(ScrumEnum.NOTES_TAG);
				Notes.get(0).setText(desc.getNotes());
				
				List<Element> DemoDate = sprint.getChildren(ScrumEnum.DEMO_DATE_TAG);
				DemoDate.get(0).setText(desc.getDemoDate());
				
				List<Element> DemoPlace = sprint.getChildren(ScrumEnum.DEMO_PLACE_TAG);
				DemoPlace.get(0).setText(desc.getDemoPlace());				
				
				break;
			}
		}
		
		XmlFileUtil.SaveXmlFile(prefsPath, doc);
	}

	// ori name: delete()
	public void deleteSprintPlan(String sprintId) {
		String prefsPath = getUsrMetadataPath() + File.separator + ITER_PLAN_FILE;
		Document doc = loadElement();
		if (doc == null) {
			doc = new Document(new Element(ScrumEnum.PLAN_TAG));
		}
		Element root = doc.getRootElement();
		
		// 藉由檢查重複的方式來順便刪除
		checkDuplicate(root, sprintId, ScrumEnum.SPRINT_TAG);

		XmlFileUtil.SaveXmlFile(prefsPath, doc);
	}
	
	// ori name: moveSprint()	
	public void moveSprintPlan(int oldId, int newId) {			
		Document doc = loadElement();
		
		if (doc == null) {
			doc = new Document(new Element(ScrumEnum.PLAN_TAG));
			return ;
		}
		
		Element root = doc.getRootElement();
		Element temp = loadElement(doc, oldId);
		String interval = temp.getChildText(ScrumEnum.INTERVAL_TAG);
		List<Element> list = new ArrayList<Element>();
		boolean reverse = false;
		if(oldId > newId) {
			for(int i = newId; i<= oldId;i++){
				Element element = loadElement(doc,i);
				if(element!=null)
					list.add(element);
			}
		} else {
			for(int i = 0; i<= newId - oldId;i++){
				Element element = loadElement(doc,newId-i);
				if(element!=null)
					list.add(element);
			}
			reverse = true;
		}
		
		if(list.size()!=0){
			Element tempElement = (Element) list.get(0).clone();
			for(int i = 0;i<list.size(); i++){
				if((i+1)!=list.size()){
					Element element = list.get(i);
					Element nextElement = list.get(i+1);
					element.setAttribute(ScrumEnum.ID_ATTR, nextElement.getAttributeValue(ScrumEnum.ID_ATTR));
					String startDate = moveDate(element.getChildText(ScrumEnum.START_DATE_TAG), interval, reverse);
					element.getChild(ScrumEnum.START_DATE_TAG).setText(startDate);
					String demoDate = nextElement.getChildText(ScrumEnum.DEMO_DATE_TAG);
					if(demoDate==null)
						demoDate = "";
					element.getChild(ScrumEnum.DEMO_DATE_TAG).setText(demoDate);
				}
				else{
					Element element = list.get(i);
					Element nextElement = tempElement;
					element.setAttribute(ScrumEnum.ID_ATTR, nextElement.getAttributeValue(ScrumEnum.ID_ATTR));
					element.getChild(ScrumEnum.START_DATE_TAG).setText(nextElement.getChildText(ScrumEnum.START_DATE_TAG));
					String demoDate = nextElement.getChildText(ScrumEnum.DEMO_DATE_TAG);
					if(demoDate==null)
						demoDate = "";
					element.getChild(ScrumEnum.DEMO_DATE_TAG).setText(demoDate);
				}
			}
		}
		
		for(int i =0;i<list.size();i++){
			checkDuplicate(root, list.get(i).getAttributeValue(ScrumEnum.ID_ATTR), ScrumEnum.SPRINT_TAG);
			// 增加新的Sprint Element
			addSprintElem(root, list.get(i));
		}
		
		String prefsPath = getUsrMetadataPath() + File.separator + ITER_PLAN_FILE;
		XmlFileUtil.SaveXmlFile(prefsPath, doc);
	}	
	
	private ISprintPlanDesc getDescription(Element element) {
		ISprintPlanDesc desc = new SprintPlanDesc();
		desc.setID(element.getAttributeValue(ScrumEnum.ID_ATTR));
		desc.setStartDate(element.getChildText(ScrumEnum.START_DATE_TAG));
		desc.setInterval(element.getChildText(ScrumEnum.INTERVAL_TAG));
		desc.setMemberNumber(element.getChildText(ScrumEnum.NUMBER_MEMBER_TAG));
		desc.setFocusFactor(element.getChildText(ScrumEnum.FOCUSFACTOR_TAG));
		desc.setGoal(element.getChildText(ScrumEnum.GOAL_TAG));
		desc.setAvailableDays(element.getChildText(ScrumEnum.AVAILABLE_DAYS_TAG));
		desc.setDemoDate(element.getChildText(ScrumEnum.DEMO_DATE_TAG));
		desc.setNotes(element.getChildText(ScrumEnum.NOTES_TAG));
		desc.setDemoPlace(element.getChildText(ScrumEnum.DEMO_PLACE_TAG));
		desc.setActualCost(element.getChildText(ScrumEnum.ACTUAL_COST));
		
		//added for plugin extension point
		Map<Integer,String> taskBoardStageMap = null;
		if( element.getChild(ScrumEnum.TASKBOARD_STAGES) != null ){
			taskBoardStageMap = this.getTaskBoardStagesByElementList( element.getChild(ScrumEnum.TASKBOARD_STAGES).getChildren() );
		}else{
			//compatibility for old system
			taskBoardStageMap = this.getDefaultTaskBoardStages();
		}
		desc.setTaskBoardStageMap( taskBoardStageMap );
   		
		return desc;
	}
	
	private Map<Integer,String> getTaskBoardStagesByElementList(List<Element> list){
		Map<Integer,String> taskBoardStagesMap = new TreeMap<Integer,String>();
		for( Element element : list ){
			taskBoardStagesMap.put( Integer.parseInt( element.getAttributeValue("id") ), element.getText() );
		}
		return taskBoardStagesMap;
	}
	
	private Map<Integer,String> getDefaultTaskBoardStages(){
		Map<Integer,String> taskBoardStagesMap = new HashMap<Integer,String>();
		taskBoardStagesMap.put( 10, "new" );
		taskBoardStagesMap.put( 50, "assigned" );
		taskBoardStagesMap.put( 90, "closed" );
		return taskBoardStagesMap;
	}
	
	public Element loaderLoadElement() {
		String prefsPath = getUsrMetadataPath() + File.separator + ITER_PLAN_FILE;
		if (!new File(prefsPath).exists()) {
			prefsPath = getSysMetadataPath() + File.separator + ITER_PLAN_FILE;
		}
		
		Document doc = XmlFileUtil.LoadXmlFile(prefsPath);
		
		if (doc == null) {
			return null;
		}
		
		return doc.getRootElement();
	}
	
	private String getUsrMetadataPath() {
		String theUsrFile;
		if( m_project != null ){
			theUsrFile = m_project.getFolder(IProject.METADATA)
					.getFullPath().toString();
		}else{
			//if construct SprintPlanDescLoader is not by m_project, we use another constructor which is by projectName
			theUsrFile = new File( "WebContent/Workspace/" + m_projectId + "/_metadata" ).getAbsolutePath();
		}
		
		return theUsrFile;
	}

	private String getSysMetadataPath() {
		IWorkspaceRoot m_workspaceRoot = m_project.getWorkspaceRoot();

		String theSysFile = m_workspaceRoot.getFolder(IProject.METADATA).getFullPath().toString();
		return theSysFile;
	}	
		

	private void checkDuplicate(Element root, String id, String Type) {
		@SuppressWarnings("unchecked")
		List<Element> descs = root.getChildren(Type);
		List<Element> list = new ArrayList<Element>();
		for (Element desc : descs) {
			if (desc.getAttributeValue(ScrumEnum.ID_ATTR).equals(id)) {
				list.add(desc);
			}
		}
		
		for (Element desc : list)
			root.removeContent(desc);
	}

	private void addSprintElem(Element root, ISprintPlanDesc desc) {
		Element sprint = new Element(ScrumEnum.SPRINT_TAG);
		sprint.setAttribute(ScrumEnum.ID_ATTR, desc.getID());

		Element start = new Element(ScrumEnum.START_DATE_TAG);
		start.setText(desc.getStartDate());

		Element interval = new Element(ScrumEnum.INTERVAL_TAG);
		interval.setText(desc.getInterval());

		Element memNumber = new Element(ScrumEnum.NUMBER_MEMBER_TAG);
		memNumber.setText(desc.getMemberNumber());

		Element factor = new Element(ScrumEnum.FOCUSFACTOR_TAG);
		factor.setText(desc.getFocusFactor());

		Element goal = new Element(ScrumEnum.GOAL_TAG);
		goal.setText(desc.getGoal());

		Element aDays = new Element(ScrumEnum.AVAILABLE_DAYS_TAG);
		aDays.setText(desc.getAvailableDays());

		Element demoDate = new Element(ScrumEnum.DEMO_DATE_TAG);
		demoDate.setText(desc.getDemoDate());

		Element notes = new Element(ScrumEnum.NOTES_TAG);
		notes.setText(desc.getNotes());

		Element demoPlace = new Element(ScrumEnum.DEMO_PLACE_TAG);
		demoPlace.setText(desc.getDemoPlace());
		
		sprint.addContent(start);
		sprint.addContent(interval);
		sprint.addContent(memNumber);
		sprint.addContent(factor);
		sprint.addContent(goal);
		sprint.addContent(aDays);
		sprint.addContent(demoDate);
		sprint.addContent(demoPlace);
		sprint.addContent(notes);
		
		root.addContent(sprint);
	}
	
	private void addSprintElem(Element root, Element element) {
		root.addContent(element);
	}	

	private Document loadElement() {
		String prefsPath = getUsrMetadataPath() + File.separator + ITER_PLAN_FILE;
		File file = new File(prefsPath);
		
		if (!file.exists()) {
			return null;
		}
		
		Document doc = XmlFileUtil.LoadXmlFile(prefsPath);

		return doc;
	}
	
	private Element loadElement(Document doc , int iter) {
		Element root = doc.getRootElement();
		@SuppressWarnings("unchecked")
		List<Element> sprints = root.getChildren(ScrumEnum.SPRINT_TAG);
		String sprintID = String.valueOf(iter);
		for (Element sprint : sprints) {
			if (sprint.getAttribute(ScrumEnum.ID_HISTORY_ATTR).getValue().equals(sprintID)) {		// 此 sprint Id 為將要秀出來的
				return sprint;
			}
		}
		
		return null;
	}
	
	private String moveDate(String startDate, String interval, boolean reverse){
		int inter = Integer.parseInt(interval);
		Date s = DateUtil.dayFilter(startDate);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(s);
		if(reverse)
			calendar.add(Calendar.WEEK_OF_YEAR, -inter);
		else
			calendar.add(Calendar.WEEK_OF_YEAR, inter);
		return DateUtil.formatBySlashForm(calendar.getTime());
	}		
	
}
