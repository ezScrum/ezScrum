<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<script type="text/javascript">
function trOnLoad(){
	var elements = document.getElementsByName("historyRow");
	for (var i=0; i<elements.length; i++){
		var desc = elements[i].getElementsByTagName("td")[0].innerHTML;
		if (desc.match("Importance"))
			elements[i].style.backgroundColor='#A1EEEF';
		else if (desc.match("Estimate"))
			elements[i].style.backgroundColor='#EFC9A1';
		else if (desc.match("Sprint"))
			elements[i].style.backgroundColor='#FFFF66';
		else if (desc.match("Status"))
			elements[i].style.backgroundColor='#A1EFA2';
		else if (desc.match("Add"))
			elements[i].style.backgroundColor='#A2A1EF';
		else if (desc.match("Drop"))
			elements[i].style.backgroundColor='#EFA2A1';
		else if (desc.match("Append"))
			elements[i].style.backgroundColor='#A2A1EF';
		else if (desc.match("Remove"))
			elements[i].style.backgroundColor='#EFA2A1';
		else if (desc.match("ActualHour"))
			elements[i].style.backgroundColor='#CCFFFF';
			
	}
}

function editHistory(historyID){
	document.location.href  = "<html:rewrite action="/showEditIssueHistory" />?issueID=${Issue.issueID }&sprintID=${sprintID}&type=${backlogType}&historyID="+historyID;
}
</script>
<div id="SideShowItem" style="display:none;"><%=session.getAttribute("currentSideItem") %></div>
<body onload="trOnLoad()">
<table width="65%" border="0" align="center" cellpadding="1"
	cellspacing="1" class="ReportBorder">
	<tr>
		<td colspan="3" class="ReportHead">Modified History Date</td>
	</tr>
	<tr>
		<td colspan="3" class="ReportBody">
			[${Issue.category}] <a href="${Issue.issueLink }" target="_blank">${Issue.issueID }</a>. ${Issue.summary }
		</td>
	</tr>
	<tr>
		<td class="ReportBody" width="55%" align="center">Description</td>
		<td class="ReportBody" width="30%" align="center">Modified Date</td>
		<td class="ReportBody" width="15%" align="center">Action</td>
	</tr>
	<c:forEach var="element" items="${Issue.issueHistories }">
		<c:if test="${ element.description!=''}">
		<tr name="historyRow">
			<td class="ReportBody">${element.description }</td>
			<td class="ReportBody" align="center">
				<fmt:formatDate pattern="yyyy/MM/dd-HH:mm:ss" value="${element.modifyDateDate }" />
			</td>
			<td class="ReportBody" align="center">
				<img src="images/edit.png" id="showCalendar" class="LinkBorder"	title="Edit Modified Date" onclick="editHistory('${element.historyID }')"/> 
			</td>
		</tr>
		</c:if>
	</c:forEach>
</table>
</body>