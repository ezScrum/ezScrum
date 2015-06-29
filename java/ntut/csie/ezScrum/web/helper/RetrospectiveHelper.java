package ntut.csie.ezScrum.web.helper;

import java.sql.SQLException;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.iteration.core.IScrumIssue;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.mapper.RetrospectiveMapper;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;

public class RetrospectiveHelper {
	
	private RetrospectiveMapper retrospectiveMapper;

	public RetrospectiveHelper(ProjectObject project, IUserSession userSession) {
		this.retrospectiveMapper = new RetrospectiveMapper(project, userSession);
	}

	public IIssue get(long id) {
		return this.retrospectiveMapper.getById(id);
	}

	public long add(String name, String description, String sprintID, String type) {
		return this.retrospectiveMapper.add(name, description, sprintID, type);
	}	

	public void edit(long issueID, String name, String description,	String sprintID, String type, String status) {
		this.retrospectiveMapper.update(issueID, name, description, sprintID, type, status);
	}

	public void delete(String issueID) {
		this.retrospectiveMapper.delete(issueID);
	}

	// 前端XML格式定義在: Common.js 之 變數 Retrospective, Parser 為 retReader
	public StringBuilder getXML(String actionType, IIssue issue) {
		String tag = null;
    	TranslateSpecialChar tsc = new TranslateSpecialChar();
		
    	if (actionType.equals("add"))
    		tag = "AddNew";
    	else if (actionType.equals("get"))
    		tag = "Edit";    	    	
    	else if (actionType.equals("edit"))
    		tag = "Edit";    	
    	else if (actionType.equals("delete"))
    		tag = "Delete";    	
    	else
    		tag = "";
    	
		StringBuilder result = new StringBuilder("");
		result.append("<" + tag + "Retrospective><Result>true</Result><Retrospective>");
		result.append("<Id>" + issue.getIssueID() + "</Id>");
				
		// get的順序跟別人不一樣,先 Name再SprintID -> 前端認XML tag所以順序沒關係
		if (actionType.equals("add") || actionType.equals("edit") || actionType.equals("get")) {
			result.append("<Link>" + tsc.TranslateXMLChar(issue.getIssueLink()) + "</Link>");				
			result.append("<SprintID>" + issue.getSprintID() + "</SprintID>");		
			result.append("<Name>" + tsc.TranslateXMLChar(issue.getSummary()) + "</Name>");
			result.append("<Type>" + issue.getCategory() + "</Type>");
			result.append("<Description>" + tsc.TranslateXMLChar(issue.getDescription()) + "</Description>");
			result.append("<Status>" + issue.getStatus() + "</Status>");							
		} else if (actionType.equals("delete")) {
			result.append("<SprintID>" + issue.getSprintID() + "</SprintID>");
		}	
		
		result.append("</Retrospective></" + tag + "Retrospective>");			
		
		return result;
	}		
	
	// 前端XML格式定義在: ShowRetrospective.jsp 之 變數 retrospectiveStore
	public StringBuilder getListXML(String sprintID) throws SQLException {
    	TranslateSpecialChar tsc = new TranslateSpecialChar();
    	//Good Retrospective 封裝成 XML 給 Ext 使用
    	List<IScrumIssue> goodRes = this.retrospectiveMapper.getList(ScrumEnum.GOOD_ISSUE_TYPE);
    	
		StringBuilder result = new StringBuilder();
		
		result.append("<Retrospectives><Sprint><Id>" + sprintID + "</Id><Name>Sprint #" + sprintID + "</Name></Sprint>");
		
		String specialSprintID = "All";//這個特殊的sprintID主要是為了抓出所有 good 和 improve(待改善) 的 retrospective
		for(int i = 0; i < goodRes.size(); i++){			
			IScrumIssue goodR = goodRes.get(i);
			//如果sprintID是All則立即加入該筆retrospective如果不是則轉換到檢查good retrospectives 裡是否存在屬於該sprintID的retrospective
			if( sprintID.equalsIgnoreCase( specialSprintID ) || goodR.getSprintID().compareTo(sprintID) == 0){
				result.append("<Retrospective>");
				result.append("<Id>" + goodR.getIssueID() + "</Id>");
				result.append("<Link>" + tsc.TranslateXMLChar(goodR.getIssueLink()) + "</Link>");
				result.append("<SprintID>" + goodR.getSprintID() + "</SprintID>");
				result.append("<Name>" + tsc.TranslateXMLChar(goodR.getName()) + "</Name>");
				result.append("<Type>" + goodR.getCategory() + "</Type>");
				result.append("<Description>" + tsc.TranslateXMLChar(goodR.getDescription()) + "</Description>");
				result.append("<Status>" + goodR.getStatus() + "</Status>");
				result.append("</Retrospective>");
			}
		}
		//Improvement Retrospective 封裝成 XML 給 Ext 使用
		List<IScrumIssue> improveRes = this.retrospectiveMapper.getList(ScrumEnum.IMPROVEMENTS_ISSUE_TYPE);		
		
		for(int i = 0; i < improveRes.size(); i++){
			IScrumIssue improveR = improveRes.get(i);
			//如果sprintID是All則立即加入該筆retrospective如果不是則轉換到檢查improve(待改善) retrospectives 裡是否存在屬於該sprintID的retrospective
			if( sprintID.equalsIgnoreCase( specialSprintID ) || improveR.getSprintID().compareTo(sprintID) == 0){
				result.append("<Retrospective>");
				result.append("<Id>" + improveR.getIssueID() + "</Id>");
				result.append("<Link>" + tsc.TranslateXMLChar(improveR.getIssueLink()) + "</Link>");
				result.append("<SprintID>" + improveR.getSprintID() + "</SprintID>");
				result.append("<Name>" + tsc.TranslateXMLChar(improveR.getName()) + "</Name>");
				result.append("<Type>" + improveR.getCategory() + "</Type>");
				result.append("<Description>" + tsc.TranslateXMLChar(improveR.getDescription()) + "</Description>");
				result.append("<Status>" + improveR.getStatus() + "</Status>");
				result.append("</Retrospective>");
			}
		}
		
		result.append("</Retrospectives>");	
		return result;
	}
	
}
