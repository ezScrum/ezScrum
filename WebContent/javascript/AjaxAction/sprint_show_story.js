function showMes(element, s_id) {
	if (TableDisplay(element,s_id)) {
		AjaxRequestShowStory("./AjaxShowStoryfromSprint.do?Sid=" + s_id, showStory);
	}
}

function AjaxRequestShowStory(url, successFunc) {
	new Ajax.Request(url,
	{
		onSuccess:	successFunc
	});
}

// remove Children
function initial(S_id) {
	var TbodyTag = document.getElementById("storyList" + S_id);
	while (TbodyTag.hasChildNodes()) {
		TbodyTag.removeChild(TbodyTag.childNodes[0]);		
	}
}

function TableDisplay(element, S_id) {
	var Table = document.getElementById("storyInfo" + S_id);
	var Img = getImgElm(element);
	
	if (Table.style.display=="none"){
		initial(S_id);
		Table.style.display="";
		Img.src="images/open.gif";
		return true;
	} else{
		Table.style.display="none";
		Img.src="images/close.gif"; 
	}
}

function showStory(response) {
	var root = response.responseXML;
	var total=0;
	var S_id = root.documentElement.getAttribute("id");
	var stories = root.getElementsByTagName("issue");
	var TbodyTag = document.getElementById("storyList" + S_id);
	var TRTag = null;
	
	for (var i=0 ; i<stories.length ; i++) {
		// <tr>
		TRTag = document.createElement("TR");
		TRTag.setAttribute('onmouseover', 'OMOver(this)');
		TRTag.setAttribute('onmouseout', 'OMOut(this)');
	
		// <text>
		var story = stories[i];
		var id = story.getAttribute("id");
		var name = story.getElementsByTagName("name")[0].firstChild.nodeValue;
		var im = story.getElementsByTagName("importance")[0].firstChild.nodeValue;
		var es = story.getElementsByTagName("estimated")[0].firstChild.nodeValue;
		var ITSurl = story.getElementsByTagName("ITSurl")[0].firstChild.nodeValue;
		//total estimate
		total = total + parseFloat(es);
		// <td>
		var TDTag_URL = new Element('a', {'href': ITSurl}).update(id);		// <a href='link'> ID </a>
		var TDTag_ID = new Element('td',{'align': 'center', 'class': 'ReportBody', 'width': '5%' });
		var TDTag_Name = new Element('td',{'align': 'left', 'class': 'ReportBody', 'width': '20%' }).update(name);
		var TDTag_Im = new Element('td',{'align': 'center', 'class': 'ReportBody', 'width': '6%' }).update(im);
		var TDTag_Es = new Element('td',{'align': 'center', 'class': 'ReportBody', 'width': '6%' }).update(es);

		TDTag_ID.appendChild(TDTag_URL);	// <td> <a href='link'> ID </a> </td>
		TRTag.appendChild(TDTag_ID);		// <tr> <td> ID </td> </tr>
		TRTag.appendChild(TDTag_Name);		// <tr> <td> name </td> </tr>
		TRTag.appendChild(TDTag_Im);		// <tr> <td> importance </td> </tr>
		TRTag.appendChild(TDTag_Es);		// <tr> <td> estimate </td> </tr>
		
		TbodyTag.appendChild(TRTag);		// <tbody> all table value </tbody>
	}
	//show the total estimate of stories
	document.getElementById("total"+S_id).innerHTML=total;
}