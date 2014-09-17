ezScrum.CreateUnplannedItemForm = Ext.extend(Ext.form.FormPanel, {
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
			url : 'addNewUnplannedItem.do',
			items: [
				{
		            fieldLabel: 'Name',
		            name: 'Name',
		            allowBlank: false,
		            maxLength: 128
		        },
		        {
		        	id 		: 'SprintCombo_AddUnplannedItem',
		        	fieldLabel: 'Sprint ID',
		            name: 'SprintID',
		            xtype	: 'SprintComboWidget',
		            ref		: 'AddUnplanned_SprintCombo_refID',
		            setNewValue: function(value) {
		            	this.originalValue = value;
		            	this.reset();
		            }
		        },
		        {
		        	id: 'AddUnplannedHandlerCombo',
		        	fieldLabel: 'Handler',
		            name: 'Handler',
		            xtype: 'HandlerComboBox'
		        },
		        {
		        	fieldLabel: 'Partners',
		            name: 'Partners'
		        },
		        {
		        	fieldLabel: 'Estimate',
		            name: 'Estimate',
		            xtype: 'numberfield',
		            allowNegative: false
		        }, 
		        {
		        	fieldLabel: 'Notes',
		            xtype: 'textarea',
		            name: 'Notes',
		            height:200
		        }, 
		        {
		        	fieldLabel: 'Specific Time',
		            name: 'SpecificTime',
		            xtype: 'datefield',
		            format:'Y/m/d',
		            editable: false
		        },{
		        	xtype: 'RequireFieldLabel'
		        }
		    ],
		    buttons: 
		    [{
		    	formBind:true,
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
		ezScrum.CreateUnplannedItemForm.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('CreateSuccess', 'CreateFailure');
	},
	onRender:function() {
		ezScrum.CreateUnplannedItemForm.superclass.onRender.apply(this, arguments);
		this.getForm().waitMsgTarget = this.getEl();
	},
	submit : function()
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.show();	
		var form = this.getForm();
		var obj = this;
		Ext.Ajax.request({
			url:this.url,
			success:function(response){obj.onSuccess(response);},
			failure:function(response){obj.onFailure(response);},
			params:form.getValues()
		});
	},
	onSuccess:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		
		// check action permission
		ConfirmWidget.loadData(response);
		if (ConfirmWidget.confirmAction()) {
			var rs = UnplannedItemReader.readRecords(response.responseXML);
			if(rs.success)
			{
				var record = rs.records[0];
				if(record)
				{
					this.fireEvent('CreateSuccess', this, response, record);
				}
	
			}
			else
				this.fireEvent('CreateFailure', this, response);
		}
	},
	onFailure:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		this.fireEvent('CreateFailure', this, response);
	},
	reset:function(){
		this.getForm().reset();
	}
});

Ext.reg('createUnplannedItemForm', ezScrum.CreateUnplannedItemForm);

ezScrum.CreateUnplannedItemWidget = Ext.extend(Ext.Window, {
	id:'AddNewUnplannedItem_Widget',
	title:'Add New Unplanned Item',
	width:700,
	constrain : true,
	modal:true,
	closeAction:'hide',
	initComponent:function() {
		var config = {
			layout:'form',
			items : [{xtype:'createUnplannedItemForm', ref: 'createUnplannedItemForm_refID'}]
        }
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.CreateUnplannedItemWidget.superclass.initComponent.apply(this, arguments);
		this.addEvents('CreateSuccess', 'CreateFailure');
		
		this.items.get(0).on('CreateSuccess', function(obj, response, record){ this.fireEvent('CreateSuccess', this, obj, response, record); }, this);
		this.items.get(0).on('CreateFailure', function(obj, response){ this.fireEvent('CreateFailure', this, obj, response); }, this);
	},
	
	showWidget:function(sprintID){
		if (sprintID != 'ALL') {
			this.createUnplannedItemForm_refID.AddUnplanned_SprintCombo_refID.setNewValue('Sprint #' + sprintID);
		}
		
		this.createUnplannedItemForm_refID.reset();
		this.show();
	}
});