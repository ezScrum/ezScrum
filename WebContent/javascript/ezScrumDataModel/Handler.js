var HandlerRecord = Ext.data.Record.create([ 'Name' ]);

var HandlerReader = new Ext.data.XmlReader({
	record : 'Handler',
	idPath : 'Name',
	successProperty : 'Result'
}, HandlerRecord);

var HandlerComboStore = new Ext.data.Store({
	fields : [{
		name : 'Name'
	}],
	reader : HandlerReader
});

// for Taskboard Handler combe
var AllHandlerComboStore = new Ext.data.Store({
	fields : [{
		name : 'Name'
	}],
	reader : HandlerReader
});

var PartnerReader = new Ext.data.XmlReader({
	record : 'Partner',
	idPath : 'Name',
	successProperty : 'Result'	
}, HandlerRecord);