function getCookie(cname) {
	var name = cname + "=";
	var ca = document.cookie.split(';');
	for(var i=0; i<ca.length; i++) {
		var c = ca[i].trim();
		if (c.indexOf(name)==0) return c.substring(name.length,c.length-1).replace('"', '');
	}
	return "";
}

function getQueryStringByName(name) {
	var result = location.search.match(new RegExp("[\?\&]" + name+ "=([^\&]+)","i"));
	if(result == null || result.length < 1) {
		return "";
	}
	return result[1];
}

function isNumber(input) {
    return (input - 0) == input && (''+input).replace(/^\s+|\s+$/g, "").length > 0;
}