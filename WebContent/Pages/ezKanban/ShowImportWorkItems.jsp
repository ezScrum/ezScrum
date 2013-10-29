<%@ page contentType="text/html; charset=utf-8" %>

<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>

<script type="text/javascript" src="javascript/prototype.js"></script>
<script>
<!--
var msg = "Please wait...";

	function check(element){	
		new Ajax.Request('checkExcel.do',
		{
			parameters: {path: element.value},
			onSuccess: function(transport){
				var result = transport.responseText;
				if( result == "true" ){
					document.getElementById("importResult").innerHTML='<input type="image" src="images/ok.png" disabled="disabled">';
					submitable(false);
				} else {
					document.getElementById("importResult").innerHTML='<input type="image" src="images/fail.png" disabled="disabled">';
					submitable(true);
				}
			},
			onFailure: function(){
				alert("lose connect with server!");
				hideLoadMask(msg);
				showLoadMask(msg);
			}
  		});
	}
	
	
	function submitable(disable){
		document.getElementById("SubmitButton").disabled = disable;
	}
	
-->
</script>
<html:form action="/importWorkItems" enctype="multipart/form-data">
        
        <table width="65%" border="0" align="center" cellpadding="1" cellspacing="1" class="ReportFrame">
          <tr>
            <td colspan="3" class="subtitle">Import WorkItems By XLS</td>
          </tr>
          <tr>
            <td width="25%" align="right" class="FieldHead">Upload File</td>
            <td>
              <input type="file" name="file" size="55" maxlength="20" onchange="check(this)"/>
              <span id="importResult"></span>
            </td>
          </tr>
          <tr align="center">
            <td colspan="2"><input id="SubmitButton" type="submit" value="Submit" disabled onclick="">
                <input type="button" value="Cancel" onclick="history.back()"></td>
            
          </tr>
        </table>
</html:form>