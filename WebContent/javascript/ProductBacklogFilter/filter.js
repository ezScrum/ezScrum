function TheFilter() {
	initial();
	var FilterType = document.getElementById("filter").value;
	
	if (FilterType=="Status") {
		doStatus();
	} else if (FilterType=="Importance" || FilterType=="Estimate") {
		doInput();
	} else {	// default
		document.getElementById("NextItemTd").innerHTML = "";
	}
}

// remove Children
function initial() {
	var NextItemTd = document.getElementById("NextItemTd");
	while (NextItemTd.hasChildNodes()) {
		NextItemTd.removeChild(NextItemTd.childNodes[0]);		
	}
}

// create <select> and <option>
function doStatus() {
	var SelectType = document.createElement("SELECT");	// Create <SELECT> Tag
	SelectType.setAttribute('name', "StatusValue");		// set name attribute
	
	var OptionType = null;
	var TextNode = "";
	
	OptionType = document.createElement("OPTION");	// Create <OPTION> Tag
	TextNode = document.createTextNode("new");		// Create a 'new' value
	OptionType.appendChild(TextNode);
	SelectType.appendChild(OptionType);
		
	OptionType = document.createElement("OPTION");	// Create <OPTION> Tag
	TextNode = document.createTextNode("closed");	// Create a 'closed' value
	OptionType.appendChild(TextNode);
	SelectType.appendChild(OptionType);
		
	document.getElementById("NextItemTd").appendChild(SelectType);
}

// create <input text>
function doInput() {
	var InputType = null;
	var TextType = null;
	var TextNode = "";
	
	TextNode = document.createTextNode("Range: From ");	// Create a 'Range: From' text 
	document.getElementById("NextItemTd").appendChild(TextNode);
	
	for (var i=0 ; i<2 ; i++) {
		InputType = document.createElement("INPUT");	// Create <INPUT> Tag
		InputType.setAttribute('type', 'text');			// set type='text'
		InputType.setAttribute('name', "RangValue" + (i+1));
		InputType.setAttribute('size', 3);				// set size='3'
		InputType.setAttribute('onkeyup', "this.value=this.value.replace(/\\D/g,'')");

		document.getElementById("NextItemTd").appendChild(InputType);
		if (i%2==0) {
			TextNode = document.createTextNode(" to ");	// Create a ' to ' text
			document.getElementById("NextItemTd").appendChild(TextNode);
		}
	}
}