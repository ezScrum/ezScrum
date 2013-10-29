function showLoadMask(msg,divId){
	var mask;
	if( divId == null ){
		mask = new Ext.LoadMask(Ext.getBody(), {msg:msg});
	} else {
		mask = new Ext.LoadMask(Ext.get(divId), {msg:msg});
	}
	mask.show();
}

function hideLoadMask(msg,divId){
	var mask;
	if( divId == null ){
		mask = new Ext.LoadMask(Ext.getBody(), {msg:msg});
	} else {
		mask = new Ext.LoadMask(Ext.get(divId), {msg:msg});
	}
	mask.hide();
}


