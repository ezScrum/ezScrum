<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=BIG5">
<link rel="stylesheet" type="text/css" href="report.css">
<title>Release Plan #1</title>
</head>
<body>
	<table class="PrintableReportBorder" align="center"><tr><td>
		<!-- release information -->
		<table cellspacing="0" cellpadding="3" width="100%" border="0">
			<tr class="ReportTitle"><td colspan="4">
				Release Plan #${release.ID}：${release.name}</td>
			</tr>
			<tr class="ReportInfo"><td align="right" width="15%">
				Start Date：</td>
				<td align="left">${release.startDate}</td>
				<td  colspan="2"></td>
			</tr>
			<tr class="ReportInfo"><td align="right" width="15%">
				End Date：</td>
				<td align="left">${release.endDate}</td>
				<td colspan="2"></td>
			</tr>
			<tr><td align="right" width="15%" class="ReportInfoButtom">
				Description：</td>
				<td align="left" class="ReportInfoButtom">${release.description}&nbsp;</td>
				<td class="ReportInfoButtom" colspan="2"> &nbsp;</td>
			</tr>
		</table>
		<!-- sprint information -->
		<table cellspacing="0" border="0">
			<logic:iterate id="element" name="sprints" indexId="index">
				<tr><td>&nbsp;</td>
				</tr>
				<tr>
					<td width="5%" align="right">●</td>
					<td colspan="5" class="ReportElementTitle">Sprint #${element.ID}: ${element.goal}&nbsp;</td>
				</tr>
				<tr>
					<td width="5%"></td>
					<td class="ReportFrameWithoutBorder" colspan="2" align="left" width="15%">Start Date: ${element.startDate}&nbsp;</td>
					<td class="ReportFrameWithoutBorder" colspan="3"></td>
				</tr>
				<tr>
					<td width="5%"></td>
					<td class="ReportFrameWithoutBorder" colspan="2" align="left" width="15%">&nbsp;End Date: ${element.endDate}&nbsp;</td>
					<td class="ReportFrameWithoutBorder" colspan="3" align="right">Total Story Points: ${tatolStoryPoints[element.ID]}</td>
				</tr>
				<!-- story information -->
				<tr>
					<td></td>
					<td class="ReportFrameTitle" align="center" width="5%">ID</td>
					<td class="ReportFrameTitle" align="center" width="40%">Story Name</td>
					<td class="ReportFrameTitle" align="center" width="10%">Imp.</td>
					<td class="ReportFrameTitle" align="center" width="10%">Est.</td>
					<td class="ReportFrameTitle" align="center" width="35%">Notes</td>
				</tr>
				<logic:iterate id="story" property="${element.ID}" name="stories">
					<tr>
						<td></td>
						<td class="ReportFrame" align="center" width="5%">${story.issueID}&nbsp;</td>
						<td class="ReportFrame" align="left" width="40%">${story.summary}&nbsp;</td>
						<td class="ReportFrame" align="center" width="10%">${story.importance}&nbsp;</td>
						<td class="ReportFrame" align="center" width="10%">${story.estimated}&nbsp;</td>
						<td class="ReportFrame" align="center" width="35%">
							<c:if test="${story.notes!=null}">
								${story.notes}
							</c:if>&nbsp;
						</td>
					</tr>
					
					<!-- task information -->
					<c:if test="${TaskMap[story.issueID]!=null}">
					<tr>
						<td></td>
						<td class="ReportFrame" colspan="5">
							<table width="100%" border="0">
								<tr>
									<td width="5%"></td>
									<td class="ReportFrameTitle" width="5%" align="center">ID</td>
									<td class="ReportFrameTitle" width="40%" align="center">Task Name</td>
									<td class="ReportFrameTitle" width="5%" align="center">Est.</td>
									<td class="ReportFrameTitle" width="10%" align="center">Handler</td>
									<td class="ReportFrameTitle" width="10%" align="center">Partners</td>
									<td class="ReportFrameTitle" width="25%" align="center">Notes</td>
								</tr>
								<c:forEach var="task" items="${TaskMap[story.issueID]}">
									<tr>
										<td width="5%"></td>
										<td class="ReportFrame" width="5%" align="center">${task.issueID }&nbsp;</td>
										<td class="ReportFrame" width="40%" align="left">${task.summary }&nbsp;</td>
										<td class="ReportFrame" width="5%" align="center">${task.estimated }&nbsp;</td>
										<td class="ReportFrame" width="10%" align="center">${task.assignto}&nbsp;</td>
										<td class="ReportFrame" width="10%" align="center">
											<c:if test="${task.partners!=null}">
												${task.partners}
											</c:if>
												&nbsp;
										</td>
										<td class="ReportFrame" width="25%" align="center">
											<c:if test="${task.notes!=null}">
												${task.notes}
											</c:if>
												&nbsp;
										</td>
									</tr>
								</c:forEach>
							</table>
						</td>
					</tr>
					</c:if>										
				</logic:iterate>
			</logic:iterate>
		</table>
	
		
	<br></td></tr>
</table>

</body>
</html>