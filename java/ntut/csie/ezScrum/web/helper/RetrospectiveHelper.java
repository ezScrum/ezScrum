package ntut.csie.ezScrum.web.helper;

import java.sql.SQLException;
import java.util.ArrayList;

import ntut.csie.ezScrum.web.dataInfo.RetrospectiveInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.RetrospectiveObject;
import ntut.csie.ezScrum.web.mapper.RetrospectiveMapper;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;

public class RetrospectiveHelper {
	private RetrospectiveMapper mRetrospectiveMapper;

	public RetrospectiveHelper(ProjectObject project) {
		mRetrospectiveMapper = new RetrospectiveMapper(project);
	}

	public RetrospectiveHelper(ProjectObject project, long sprintId) {
		mRetrospectiveMapper = new RetrospectiveMapper(project, sprintId);
	}

	public RetrospectiveObject getRetrospective(long retrospectiveId) {
		return mRetrospectiveMapper.getRetrospective(retrospectiveId);
	}

	public long addRetrospective(RetrospectiveInfo retrospectiveInfo) {
		return mRetrospectiveMapper.addRetrospective(retrospectiveInfo);
	}

	public void editRetrospective(RetrospectiveInfo retrospectiveInfo) {
		mRetrospectiveMapper.updateRetrospective(retrospectiveInfo);
	}

	public void deleteRetrospective(long retrospectiveId) {
		mRetrospectiveMapper.deleteRetrospective(retrospectiveId);
	}

	// 前端XML格式定義在: Common.js 之 變數 Retrospective, Parser 為 retReader
	public StringBuilder getXML(String actionType, RetrospectiveObject retrospective) {
		String tag = null;
		TranslateSpecialChar translateSpecialChar = new TranslateSpecialChar();

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
		result.append("<" + tag
				+ "Retrospective><Result>true</Result><Retrospective>");
		result.append("<Id>" + retrospective.getId() + "</Id>");

		// get的順序跟別人不一樣,先 Name再SprintID -> 前端認XML tag所以順序沒關係
		if (actionType.equals("add") || actionType.equals("edit")
				|| actionType.equals("get")) {
			result.append("<Link></Link>");
			result.append("<SprintID>" + retrospective.getSprintId() + "</SprintID>");
			result.append("<Name>" + translateSpecialChar.TranslateXMLChar(retrospective.getName())
					+ "</Name>");
			result.append("<Type>" + retrospective.getTypeString() + "</Type>");
			result.append("<Description>"
					+ translateSpecialChar.TranslateXMLChar(retrospective.getDescription())
					+ "</Description>");
			result.append("<Status>" + retrospective.getStatus() + "</Status>");
		} else if (actionType.equals("delete")) {
			result.append("<SprintID>" + retrospective.getSprintId() + "</SprintID>");
		}

		result.append("</Retrospective></" + tag + "Retrospective>");

		return result;
	}

	// 前端XML格式定義在: ShowRetrospective.jsp 之 變數 retrospectiveStore
	public StringBuilder getListXML(String sprintId) throws SQLException {
		TranslateSpecialChar translateSpecialChar = new TranslateSpecialChar();
		// Good Retrospective 封裝成 XML 給 Ext 使用
		ArrayList<RetrospectiveObject> goods = mRetrospectiveMapper
				.getRetrospectivesByType(RetrospectiveObject.TYPE_GOOD);

		StringBuilder result = new StringBuilder();

		result.append("<Retrospectives><Sprint><Id>" + sprintId
				+ "</Id><Name>Sprint #" + sprintId + "</Name></Sprint>");

		String specialSprintId = "All";// 這個特殊的sprintID主要是為了抓出所有 good 和
										// improve(待改善) 的 retrospective
		for (int i = 0; i < goods.size(); i++) {
			RetrospectiveObject good = goods.get(i);
			// 如果sprintID是All則立即加入該筆retrospective如果不是則轉換到檢查good retrospectives
			// 裡是否存在屬於該sprintID的retrospective
			if (sprintId.equalsIgnoreCase(specialSprintId)
					|| String.valueOf(good.getSprintId()).compareTo(sprintId) == 0) {
				result.append("<Retrospective>");
				result.append("<Id>" + good.getId() + "</Id>");
				result.append("<Link></Link>");
				result.append("<SprintID>" + good.getSprintId() + "</SprintID>");
				result.append("<Name>" + translateSpecialChar.TranslateXMLChar(good.getName())
						+ "</Name>");
				result.append("<Type>" + good.getTypeString() + "</Type>");
				result.append("<Description>"
						+ translateSpecialChar.TranslateXMLChar(good.getDescription())
						+ "</Description>");
				result.append("<Status>" + good.getStatusString() + "</Status>");
				result.append("</Retrospective>");
			}
		}
		// Improvement Retrospective 封裝成 XML 給 Ext 使用
		ArrayList<RetrospectiveObject> improvements = this.mRetrospectiveMapper
				.getRetrospectivesByType(RetrospectiveObject.TYPE_IMPROVEMENT);

		for (int i = 0; i < improvements.size(); i++) {
			RetrospectiveObject improvement = improvements.get(i);
			// 如果sprintID是All則立即加入該筆retrospective如果不是則轉換到檢查improve(待改善)
			// retrospectives 裡是否存在屬於該sprintID的retrospective
			if (sprintId.equalsIgnoreCase(specialSprintId)
					|| String.valueOf(improvement.getSprintId()).compareTo(sprintId) == 0) {
				result.append("<Retrospective>");
				result.append("<Id>" + improvement.getId() + "</Id>");
				result.append("<Link></Link>");
				result.append("<SprintID>" + improvement.getSprintId()
						+ "</SprintID>");
				result.append("<Name>"
						+ translateSpecialChar.TranslateXMLChar(improvement.getName()) + "</Name>");
				result.append("<Type>" + improvement.getTypeString() + "</Type>");
				result.append("<Description>"
						+ translateSpecialChar.TranslateXMLChar(improvement.getDescription())
						+ "</Description>");
				result.append("<Status>" + improvement.getStatusString() + "</Status>");
				result.append("</Retrospective>");
			}
		}

		result.append("</Retrospectives>");
		return result;
	}

}
