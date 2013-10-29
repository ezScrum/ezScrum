//When mouse move on <tr>, would change background of <tr>
function OMOver(element,color){
	if( color == null || color == "" ){
		element.style.backgroundColor='#C5C1AA';
	} else {
		element.style.backgroundColor = color;
	}
}

//When mouse move out <tr>, would recover background of <tr>
function OMOut(element,color){
	element.style.backgroundColor='';
}

function displayMessage(elm, issueID){ 
	var	targetElm=document.getElementById("tr"+issueID); 
	var imgElm=getImgElm(elm);
	if (targetElm.style.display=="none"){
		targetElm.style.display="";
		imgElm.src="images/open.gif";
	} else{
		targetElm.style.display="none";
		imgElm.src="images/close.gif"; 
	}
}	

function getImgElm(elm){ 
	var returnElm=elm.childNodes[0]; 
	while (returnElm.nodeName!="IMG"){
		returnElm=returnElm.nextSibling; 
	} return returnElm; 
}