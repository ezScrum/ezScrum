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
				Release Plan #${release.serialId}：${release.name}</td>
			</tr>
			<tr class="ReportInfo"><td align="right" width="15%">
				Start Date：</td>
				<td align="left">${release.startDateString}</td>
				<td  colspan="2"></td>
			</tr>
			<tr class="ReportInfo"><td align="right" width="15%">
				End Date：</td>
				<td align="left">${release.endDateString}</td>
				<td colspan="2"></td>
			</tr>
			<tr><td align="right" width="15%" class="ReportInfoButtom">
				Description：</td>
				<td align="left" width="80%" class="ReportInfoButtom">${release.description}&nbsp;</td>
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
					<td colspan="5" class="ReportElementTitle">Sprint #${element.serialId}: ${element.goal}&nbsp;</td>
				</tr>
				<tr>
					<td width="5%"></td>
					<td class="ReportFrameWithoutBorder" colspan="2" align="left" width="15%">Start Date: ${element.startDateString}&nbsp;</td>
					<td class="ReportFrameWithoutBorder" colspan="3"></td>
				</tr>
				<tr>
					<td width="5%"></td>
					<td class="ReportFrameWithoutBorder" colspan="2" align="left" width="15%">Demo Date: ${element.demoDateString}&nbsp;</td>
					<td class="ReportFrameWithoutBorder" colspan="3"></td>
				</tr>
				<tr>
					<td width="5%"></td>
					<td class="ReportFrameWithoutBorder" colspan="2" align="left" width="15%">&nbsp;End Date: ${element.endDateString}&nbsp;</td>
					<td class="ReportFrameWithoutBorder" colspan="3" align="left">Total Story Points: ${totalStoryPoints[element.serialId]}</td>
				</tr>
				<!-- story information -->
				<tr>
					<td></td>
					<td class="ReportFrameTitle" align="center" width="10%">ID</td>
					<td class="ReportFrameTitle" align="left" width="70%">Story Name</td>
					<td class="ReportFrameTitle" align="center" width="8%">Imp.</td>
					<td class="ReportFrameTitle" align="center" width="8%">Est.</td>
				</tr>
				<logic:iterate id="story" property="${element.serialId}" name="stories">
					<tr>
						<td></td>
						<td class="ReportFrame" align="center" width="10%">${story.serialId}&nbsp;</td>
						<td class="ReportFrame" align="left" width="70%">${story.name}&nbsp;</td>
						<td class="ReportFrame" align="center" width="8%">${story.importance}&nbsp;</td>
						<td class="ReportFrame" align="center" width="8%">${story.estimate}&nbsp;</td>
					</tr>								
				</logic:iterate>
			</logic:iterate>
		</table>
	
		
	<br></td></tr>
</table>

</body>
</html>