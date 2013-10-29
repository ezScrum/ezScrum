<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<script type="text/javascript">

	function showIssueReport() {
		window.location = "showReportIssues.do";
	}
</script>
<script language="javascript" type="text/javascript" src="javascript/jquery-1.7.2.min.js"></script>

<html:form action="/logonSubmit" method="Post">

<% if (request.getParameter("tenantId") != null && !request.getParameter("tenantId").equals("") ) { %>
<table width="60%"  border="0" align="center" cellpadding="0" cellspacing="0">
<tr>
<td align="left">
<font face="標楷體">
<div id="tenantName">Welcome to <%=request.getParameter("tenantId")%></div>
</font>
</td>
</tr>
</table>
<% } else { %>
<% } %>

<br />
<table width="60%"  border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td  ><img src="images/Design1_r5_c2.gif" width="15" height="15"></td>
    <td  background="images/Design1_r5_c3.gif"><img src="images/spacer.gif" width="100%" height="15"></td>
    <td><div align="right"><img src="images/Design1_r5_c6.gif" width="15" height="15"></div></td>
  </tr>
  
  <tr valign="top">
    <td background="images/Design1_r6_c2.gif"><img src="images/spacer.gif" width="15" height="100%"></td>
    <td height="100%" class="innerTable" style="padding:0px "  >
   	<html:errors />
    <table width="100%" border="0" cellspacing="1" cellpadding="1">
      <tr>
        <td align="right" class="FieldHead" >User Name</td>
        <td width="70%"><html:text property="userId" styleClass="field"  />
        </td>
      </tr>
      <tr>
        <td align="right" class="FieldHead" >Password</td>
        <td width="70%"><html:password property="password" styleClass="field"  />
        </td>
      </tr>      
      <tr align="center">
        <td colspan="2"><input name="Next" type="submit" id="Next" value="Submit">
		  <!-- 不對外開放 
          <a href="#" onclick="showIssueReport()" >
          	<font size="3" face="Times">
          		submit an issue
          	</font>
          </a>-->
		</td>
	  </tr>
    </table></td>
    <td background="images/Design1_r6_c6.gif"></td>
  </tr>
  <tr>
    <td><img src="images/Design1_r7_c2.gif" width="15" height="15"></td>
    <td width="100%" background="images/Design1_r7_c3.gif"></td>
    <td><img src="images/Design1_r7_c6.gif" width="15" height="15"></td>
  </tr>
</table>
<br>
<input  type="hidden"  id="method" name="method" />
<input  type="hidden"  id="projectName" name="projectName" />

</html:form>


<style type ="text/css">
body{font-size:13px}
#divFrame{
    border: 1px solid #999;
    box-shadow: -2px 5px 5px #CCC;
    border-radius: 6px;
    left: 20px;
    position: relative;
    font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
    width: 330px;
    color: #333;
    background: white;
}

#divFrame .clsHead{
    height: 38px;
    background: #EEE;#G;#GG;#GGG;#GG;#G;
    border-bottom: 1px solid #999;
    border-radius: 6px 6px 0 0;
}
#divFrame .clsHead span{float:right;margin-top:0px}
#divFrame .clsContent {
    height: 100px;
    border-radius: 0 0 6px 6px;
    background-color: white;
}

.GetFocus{background-color:#eee}

#divFrame .btn {
    border: 1px solid #CCC;
    background: #49AFCD;
    text-decoration: none;
    border-radius: 4px;
    padding: 7px 18px;
    margin: 11px;
    float: right;
    font-size: 14px;
    color: white;
    border-color: rgba(0, 0, 0, 0.1) rgba(0, 0, 0, 0.1) rgba(0, 0, 0, 0.25);
}
</style>

<script type="text/javascript">
	$(function(){
		var display;
		$.getJSON("/ezScrum/feedbackDisplay.do", { status: display },function(data) {
			var isDisplay;
			$.each(data, function(InfoIndex ,Info) { isDisplay = Info });
			if(isDisplay == "true") {
				var htmlTemplate = [
                '<div id ="divFrame">',
                ' <div class ="clsHead">',
                '   <span style="float: left;padding: 10px 0px 1px 9px;font-size: 19px;">',
              '     Thank you for using ezScrum',
                '   </span>',
                '   <span style="padding: 6px 12px 0 0;">',
                '     <img src="images/close_btn.PNG" style="font-size: 24px;cursor: pointer;" />',
                '   </span>',
                ' </div>',
                ' <div class="clsContent">',
                '   <div style="font-size: 17px;margin: 14px 0px 0px 14px;">',
                '     Please give us a feedback for the new feature released in this reversion.',
                '   </div>',
                '   <a class="btn" href="https://docs.google.com/a/scrum.tw/spreadsheet/viewform?formkey=dDRJTkdncWI2NGROMmtUM3JWZU43dEE6MQ" target="_blank">Go</a>',
                ' </div>',
                '</div>'
            ];

          $(htmlTemplate.join('')).appendTo($('body'));

				$(".clsHead").click(function() {	
						$("#divFrame").css("display","none");
						display = false;
						$.post("/ezScrum/feedbackDisplay.do", {status:display}, function(data) {
					})
						
				});	
			}
		});
	});
</script>
