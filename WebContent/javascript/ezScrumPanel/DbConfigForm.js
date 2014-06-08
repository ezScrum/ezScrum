DbConfigFormLayout = Ext.extend(Ext.form.FormPanel, {
	id 				: 'DBConfig_Form',
	border			: false,
	frame			: true,
	store			: DBConfigStore,
	title			: 'DB Config Setting',
	bodyStyle		: 'padding:0px',
	labelAlign		: 'right',
	labelWidth		: 150,
	buttonAlign		: 'left',
	monitorValid	: true,
	initComponent	: function() {
		var config = {
			url			: 'showConfiguration.do',
			modify_url	: 'saveConfiguration.do',	
			items		: [DBConfigItem],
	        buttons : [{
	        	formBind : true,
	        	scope    : this,
	        	text     : 'Modify',
	        	disabled : true,
	        	handler  : this.doModify
	        }]
		}
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		DbConfigFormLayout.superclass.initComponent.apply(this, arguments);
	},
    loadDataModel: function() {
    	var obj = this;
    	var loadmask = new Ext.LoadMask(this.getEl(), {msg:"loading info..."});
		loadmask.show();
    	Ext.Ajax.request({
    		url		: obj.url,
    		success	: function(response) {
				DBConfigStore.loadData(Ext.decode(response.responseText));
				var record = DBConfigStore.getAt(0);
				obj.setDataModel(record);
    		},
    		failure	: function(response) {
    			var loadmask = new Ext.LoadMask(this.getEl(), {msg:"loading info..."});
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
    	var loadmask = new Ext.LoadMask(this.getEl(), {msg:"loading info...3"});
		loadmask.hide();
    },
    doModify: function() {
		var obj = this;
    	var form = this.getForm();
    	var loadmask = new Ext.LoadMask(this.getEl(), {msg:"loading info...4"});
		loadmask.show();
    	Ext.Ajax.request({
    		url		: obj.modify_url,
    		params	: form.getValues(),
    		success	: function(response) {
				var result = response.responseText;
				if (result == "success") {
					Ext.example.msg('Modify DB Config', 'Success.');
				} else {
					Ext.example.msg('Modify DB Config', 'Sorry, the action is failure.');
				}
				var loadmask = new Ext.LoadMask(obj.getEl(), {msg:"loading info...5"});
				loadmask.hide();
    		},
    		failure	: function(response){
    			console.log(response);
    			console.log(form);
    			Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
    		}
    	});
	}
});

Ext.reg('DbConfigForm', DbConfigFormLayout);