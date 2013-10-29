// the form is for ITS Config Page
ITSConfigModifyFormLayout = Ext.extend(Ext.form.FormPanel, {
	id 			: 'ITSConfig_Form',
	border		: false,
	frame		: true,
	store		: ITSConfigStore,
	title		: 'ITS Preference',
	bodyStyle	: 'padding:0px',
	labelAlign	: 'right',
	labelWidth	: 150,
	buttonAlign	: 'left',
	monitorValid: true,
	initComponent : function() {
		var config = {
			url			: 'showITSPreference.do',
			modify_url	: 'saveITSPreference.do',	
			items   : [
			    ITSConfigItem
	        ],
	        buttons : [{
	        	formBind : true,
	        	scope    : this,
	        	text     : 'Modify It',
	        	disabled : true,
	        	handler  : this.doModify
	        }]
		}
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ITSConfigModifyFormLayout.superclass.initComponent.apply(this, arguments);
	},
    loadDataModel: function() {
    	var obj = this;
    	var loadmask = new Ext.LoadMask(this.getEl(), {msg:"loading info..."});
		loadmask.show();
    	Ext.Ajax.request({
    		url : obj.url,
    		success: function(response) {
    			ConfirmWidget.loadData(response);
    			if (ConfirmWidget.confirmAction()) {
    				ITSConfigStore.loadData(Ext.decode(response.responseText));
    				var record = ITSConfigStore.getAt(0);
					obj.setDataModel(record);
    			}
    		},
    		failure: function(response) {
    			var loadmask = new Ext.LoadMask(this.getEl(), {msg:"loading info..."});
    			loadmask.hide();
    			Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
    		}
    	});
    },
    setDataModel: function(record) {
    	this.getForm().reset();
    	this.getForm().setValues({
			ServerUrl: record.get('ServerUrl'),
			ITSAccount: record.get('ITSAccount')
		});
    	var loadmask = new Ext.LoadMask(this.getEl(), {msg:"loading info..."});
		loadmask.hide();
    },
    doModify: function() {
		var obj = this;
    	var form = this.getForm();
    	var loadmask = new Ext.LoadMask(this.getEl(), {msg:"loading info..."});
		loadmask.show();
    	Ext.Ajax.request({
    		url: obj.modify_url,
    		params: form.getValues(),
    		success: function(response) {
    			ConfirmWidget.loadData(response);
    			if (ConfirmWidget.confirmAction()) {
    				var result = response.responseText;
    				if (result == "success") {
    					Ext.example.msg('Modify ITS Preference', 'Success.');
    				} else {
    					Ext.example.msg('Modify ITS Preference', 'Sorry, the action is failure.');
    				}
    				var loadmask = new Ext.LoadMask(obj.getEl(), {msg:"loading info..."});
    				loadmask.hide();
    			}
    		},
    		failure:function(response){
    			Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
    		}
    	});
	}
});

Ext.reg('ITSConfigModifyForm', ITSConfigModifyFormLayout);