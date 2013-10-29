<%@ page contentType="text/html; charset=utf-8"%>

<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script type="text/javascript">

function refreshImage(imageSrc)
{
	img = document.getElementById("wipChangeDiagramImage");
	img.src = imageSrc+"?"+Math.random();
}

</script>

<html>
	<head>
    <title></title>
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">    
	<link rel="stylesheet" type="text/css" href="style.css">
	<link rel="stylesheet" type="text/css" href="form.css">
	<link rel="stylesheet" type="text/css" href="report.css">
  </head>
  
<BODY onload="refreshImage('${WipChangeDiagram.wipChangeDiagramChartPath}')">
<table width="90%" height="80%" border="0" cellpadding="0" cellspacing="0" align="center">
	<tr>
		<td><img src="images/Design1_r5_c2.gif" width="15" height="15" /></td>
		<td background="images/Design1_r5_c3.gif"><img
			src="images/spacer.gif" width="100%" height="15" /></td>
		<td>
		<div align="right"><img src="images/Design1_r5_c6.gif"
			width="15" height="15" /></div>
		</td>
	</tr>
	<tr valign="top">
		<td background="images/Design1_r6_c2.gif"><img
			src="images/spacer.gif" width="15" height="100%" /></td>
		<td class="innerTable" height="100%"><br />
		
		<div>
			<table width="95%" align="center" cellpadding="2" cellspacing="0" class="TaskBoardReportBorder">
				
				<tr>
					<td colspan="2" class="TaskBoardReportBody"  align="center">
						<img id="wipChangeDiagramImage" src="${WipChangeDiagram.wipChangeDiagramChartPath}" class="LinkBorder" />
					</td>
				</tr>
				
				<tr>
					<td style="font-size: medium">
						<logic:iterate id="history" name="WipChangeHistory" indexId="counter">
							<b>WIP Change History #<%= counter + 1 %></b>
							<textarea readonly="true" style="height:100px; width:100%"><bean:write name="history" /></textarea>
							<br />
							<br />
						</logic:iterate>
					</td>
				</tr>
			</table>
		</div>
		
		<br />
		</td>
		<td background="images/Design1_r6_c6.gif" />
	</tr>
	<tr>
		<td><img src="images/Design1_r7_c2.gif" width="15" height="15" /></td>
		<td width="100%" background="images/Design1_r7_c3.gif" />
		<td><img src="images/Design1_r7_c6.gif" width="15" height="15" /></td>
	</tr>
</table>
</BODY>
</html>