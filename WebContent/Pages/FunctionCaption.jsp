<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<table width="100%" border="0" cellpadding="5" cellspacing="0" bgcolor="#E9EBD8">
	<tr>
		<td>
			<span class="ProjectCaption">
				<tiles:getAsString name="captionString"/>
			</span>
			
			<span class="ProjectCaption">
				<tiles:insert name="dynamicCaption"/>
			</span>
			
			<span class="ProjectCaption">
				<tiles:getAsString name="subCaptionString" />
			</span>
		</td>
	    <td align="right">&nbsp;</td>
	</tr>
</table>