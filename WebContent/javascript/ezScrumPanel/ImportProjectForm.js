var Project = Ext.data.Record.create(['ID', 'Name', 'Comment', 'ProjectManager', 'CreateDate', 'DemoDate']);

var ProjectReader = new Ext.data.XmlReader({
	   record: 'Project',
	   idPath : 'ID',
	   successProperty: 'Result'
}, Project);

var ProjectStore = new Ext.data.Store({
	fields:[
		{name : 'ID'},
		{name : 'Name'},
		{name : 'Comment'},
		{name : 'ProjectManager'},
		{name : 'CreateDate'},
		{name : 'DemoDate'}
	],
	reader : ProjectReader
});

ImportProjectFormLayout = Ext.extend(Ext.form.FormPanel, {
	id 				: 'ImportProject_Form',
	border			: false,
	frame			: true,
	store			: ProjectStore,
	title			: 'Import Project',
	bodyStyle		: 'padding:0px',
	labelAlign		: 'right',
	labelWidth		: 150,
	buttonAlign		: 'left',
	monitorValid	: true,
	initComponent	: function() {
		var config = {
			url			: 'viewProjectList.do',
			export_url	: 'saveConfiguration.do',	
			items		: [{
				xtype:'fileuploadfield',
				emptyText: 'Select a file',
	            fieldLabel: 'Update File',
	            name: 'file',
	            vtype: 'checkUploadFileName',
	            allowBlank: false
		}],
	        buttons : [{
	        	formBind : true,
	        	scope    : this,
	        	text     : 'Import',
	        	disabled : true,
	        	handler  : this.doImport
	        }]
		}
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ImportProjectFormLayout.superclass.initComponent.apply(this, arguments);
	},
	showMask : function(msg) {
		this.loadmask = new Ext.LoadMask(this.getEl(), {msg: msg});
		this.loadmask.show();
	},
	closeMask : function() {
		this.loadmask.hide();
	},
	loadDataModel: function() {
		var obj = this;
		var loadmask = new Ext.LoadMask(this.getEl(), {msg:"loading info..."});
		loadmask.show();
		Ext.Ajax.request({
			url: obj.url,
			success : function(response) {
				ProjectStore.loadData(response.responseXML);
				var loadmask = new Ext.LoadMask(obj.getEl(), {msg:"loading info..."});
				loadmask.hide();
			},
			failure : function(){
				var loadmask = new Ext.LoadMask(obj.getEl(), {msg:"loading info..."});
    			loadmask.hide();
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
			}
		});
	},
    setDataModel: function(record) {
    	this.getForm().reset();
    	this.getForm().setValues({
			ServerUrl	: record.get('ServerUrl'),
			DBAccount	: record.get('DBAccount'),
			DBType		: record.get('DBType'),
			DBName		: record.get('DBName')
		});
    },
    doImport: function() {
		var obj = this;
    	var form = this.getForm();
    	this.showMask('loading info...');
    	Ext.Ajax.request({
    		url		: obj.export_url,
    		params	: form.getValues(),
    		success	: function(response) {
				var result = response.responseText;
				if (result == "success") {
					Ext.example.msg('Modify DB Config', 'Success.');
				} else {
					Ext.example.msg('Modify DB Config', 'Sorry, the action is failure.');
				}
				obj.closeMask();
    		},
    		failure	: function(response){
    			obj.closeMask();
    			Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
    		}
    	});
	}
});

Ext.reg('ImportProjectForm', ImportProjectFormLayout);