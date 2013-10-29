Ext.ns('ezScrum');
Ext.ns('ezScrum.window');

/* Attach File Form */
ezScrum.AddPluginFileFormPanel = Ext.extend(Ext.form.FormPanel, {
	bodyStyle: 'padding:15px',
	border : false,
	defaultType: 'textfield',
	labelAlign : 'right',
	labelWidth : 100,
	defaults: {
        width: 500,
        msgTarget: 'side'
    },
    monitorValid:true,
	initComponent:function() {
		var config = {
			url : 'addPlugin',
			fileUpload : true,
			items: [{
					xtype:'fileuploadfield',
					emptyText: 'Select a file',
		            fieldLabel: 'Update File',
		            name: 'file',
		            vtype: 'checkUploadFileName',
		            allowBlank: false
			}],
		    buttons: 
		    [{
		    	formBind:true,
		    	id: 'SubmitBtn',
	    		text: 'Submit',
	    		scope:this,
	    		handler: this.submit,
	    		disabled:true
	    	},
	        {
	        	text: 'Cancel',
	        	scope:this,
	        	handler: function(){this.ownerCt.hide();}
	        }]
        }
        
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		
		ezScrum.AddPluginFileFormPanel.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('AttachSuccess', 'AttachFailure');
	},
	onRender:function() {
		ezScrum.AddPluginFileFormPanel.superclass.onRender.apply(this, arguments);
		this.getForm().waitMsgTarget = this.getEl();
	},
	submit : function()
	{
		if(this.getForm().isValid()){
			var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
			myMask.show();
			var obj = this;

			this.getForm().submit({
				url:obj.url,
				success: function(form, action){
			        obj.onSuccess();
			    },
			    failure:function(form, action){
			        obj.onFailure();
			    }
			});
		}

	},
	onSuccess:function() 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		this.fireEvent('AttachSuccess');
	},
	onFailure:function() 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
	},
	reset:function(){
		this.getForm().reset();
	}
});

Ext.reg('addPluginFileFormPanel', ezScrum.AddPluginFileFormPanel);

ezScrum.window.AddPluginFileWindow = Ext.extend(Ext.Window, {
	title:'Add Plugin File',
	width:700,
	modal:true,
	closeAction:'hide',
	constrain : true,
	initComponent:function() {
		var config = {
			layout:'form',
			items : [{xtype:'addPluginFileFormPanel'}]
        }
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.window.AddPluginFileWindow.superclass.initComponent.apply(this, arguments);
		
		this.attachForm = this.items.get(0); 
	},
	attachFile:function(panel){
		var obj = this;
		this.attachForm.reset();
		this.attachForm.on('AttachSuccess', function(){ 
			panel.notify_AddPlugin( "true" );//reload grid panel data
			obj.hide();// hide add plugin window
		});
        this.setTitle('Add Plugin File');
		this.show();
	}
});
