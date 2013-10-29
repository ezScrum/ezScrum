<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="BIG5"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=BIG5">
<title>Insert title here</title>
</head>
<link rel="stylesheet" type="text/css" href="css/ext/css/ext-all.css" />
<script type="text/javascript" src="javascript/ext-base.js"></script>
<script type="text/javascript" src="javascript/ext-all.js"></script>
<script type="text/javascript">
Ext.onReady( 
	function() {
		var x = document.getElementById("messageID");
		Ext.MessageBox.alert('Tip', x.innerHTML);
	}
); 

</script>
<body>
<h1 id = "messageID">${message}</h1>
</body>
</html>