/* Retrospective Type ComboBox 預設值為Good */
var TypeCombo_AddRetrospective = new Ext.form.ComboBox({
    typeAhead: true,
    triggerAction: 'all',
    lazyRender: true,
    editable: false,
    mode: 'local',
    store: ['Good', 'Improvement'],
    value: ['Good', 'Improvement'],
    name: 'Type',
    fieldLabel: 'Type',
    originalValue: 'Good'
});

/* Create Story Form */
ezScrum.CreateRetrospectiveForm = Ext.extend(Ext.form.FormPanel, {
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
			url : 'ajaxAddNewRetrospective.do',
			items: [
				{
		            fieldLabel: 'Name',
		            name: 'Name',
		            allowBlank: false,
		            maxLength: 128
		        },
		        {
		        	id 		: 'SprintCombo_AddRetrospective',
		        	fieldLabel: 'Sprint ID',
		            name: 'SprintID',
		            xtype	: 'SprintComboWidget',
		            ref		: 'AddRetrospective_SprintCombo',
		            setNewValue: function(value) {
		            	this.originalValue = value;
		            	this.reset();
		            }
		        },
		        TypeCombo_AddRetrospective,
		        {
		            fieldLabel: 'Description',
		            xtype: 'textarea',
		            name: 'Description',
		            height:200
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
		ezScrum.CreateRetrospectiveForm.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('CreateSuccess', 'CreateFailure');
	},
	onRender:function() {
		ezScrum.CreateRetrospectiveForm.superclass.onRender.apply(this, arguments);
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
			var rs = retReader.readRecords(response.responseXML);
			
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

Ext.reg('createRetrospectiveForm', ezScrum.CreateRetrospectiveForm);

ezScrum.AddNewRetrospectiveWidget = Ext.extend(Ext.Window, {
	id:'AddNewRetrospective_Widget',
	title:'Add New Retrospective',
	width:700,
	modal: true,
	constrain  : true,
	closeAction:'hide',
	initComponent:function() {
		var config = {
			layout:'form',
			items : [{xtype:'createRetrospectiveForm', ref:'createRetrospectiveForm_refID'}]
        }
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.AddNewRetrospectiveWidget.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('CreateSuccess', 'CreateFailure');
		
		this.items.get(0).on('CreateSuccess', function(obj, response, record){ this.fireEvent('CreateSuccess', this, obj, response, record); }, this);
		this.items.get(0).on('CreateFailure', function(obj, response){ this.fireEvent('CreateFailure', this, obj, response, issueId); }, this);
	},
	showWidget:function(sprintID){
		if (sprintID != 'ALL') {
			this.createRetrospectiveForm_refID.AddRetrospective_SprintCombo.setNewValue('Sprint #' + sprintID);
		}
		
		this.createRetrospectiveForm_refID.reset();
		this.show();
	}
});