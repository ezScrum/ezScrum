Ext.ns('ezScrum');

/* Status Data */
/*
var statusStoreForCreate = new Ext.data.Store({
    id: 0,
    fields: [
   		{name: 'Name'}
    ],
    reader:statusReader
});
*/
/* Status ComboBox */
/*
var statusComboForCreate = new Ext.form.ComboBox({
    typeAhead: true,
    editable: false,
    mode: 'local',
    store: statusStoreForCreate,
    name: 'StatusComboBox',
    valueField: 'Name',
    displayField: 'Name',
    fieldLabel: 'Status',
    id: 'StatusComboBoxForCreate'
});
*/

/* Priority Data */
var priorityStoreForCreate = new Ext.data.Store({
    id: 0,
    fields: [
   		{name: 'Name'}
    ],
    reader:priorityReader
});

/* Handler Data */
var handlerStoreForCreate = new Ext.data.Store({
    id: 0,
    fields: [
   		{name: 'Name'}
    ],
    reader:handlerReader
});

/* Priority ComboBox */
var priorityComboForCreate = new Ext.form.ComboBox({
    typeAhead: true,
    triggerAction: 'all',
    lazyRender: true,
    editable: false,
    mode: 'local',
    store: priorityStoreForCreate,
    name: 'PriorityComboBox',
    valueField: 'Name',
    displayField: 'Name',
    fieldLabel: 'Priority',
    id: 'PriorityComboBoxForCreate'
});

/* Handler ComboBox */
var handlerComboForCreate = new Ext.form.ComboBox({
    typeAhead: true,
    triggerAction: 'all',
    lazyRender: true,
    editable: false,
    mode: 'local',
    store: handlerStoreForCreate,
    name: 'HandlerComboBox',
    valueField: 'Name',
    displayField: 'Name',
    fieldLabel: 'Handler',
    id: 'HandlerComboBoxForCreate'
});

/* Create Custom Issue Form */
ezScrum.CreateCustomIssueForm = Ext.extend(Ext.form.FormPanel, {
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
			url : 'ajaxAddNewCustomIssue.do',
			items: [{
		            fieldLabel: 'Name',
		            name: 'Name',
		            allowBlank: false,
		            maxLength: 128
		        }, {
		            fieldLabel: 'Category',
		            name: 'Category',
		            disabled: true
		        },
		        priorityComboForCreate,
		        handlerComboForCreate
		        , {
		        	fieldLabel: 'Description',
		            xtype: 'textarea',
		            name: 'Description',
		            height:200
		        }, {
		            name: 'typeID',
		            hidden: true
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
		ezScrum.CreateCustomIssueForm.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('CreateSuccess', 'CreateFailure');
	},
	onRender:function() {
		ezScrum.CreateCustomIssueForm.superclass.onRender.apply(this, arguments);
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
		var rs = jsonCustomIssueReader.read(response);
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

Ext.reg('createCustomIssueForm', ezScrum.CreateCustomIssueForm);

ezScrum.AddNewCustomIssueWidget = Ext.extend(Ext.Window, {
	width:700,
	modal:false,
	closeAction:'hide',
	initComponent:function() {
		var config = {
			layout:'form',
			items : [{xtype:'createCustomIssueForm'}]
        }
			Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.AddNewCustomIssueWidget.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('CreateSuccess', 'CreateFailure');
		
		this.items.get(0).on('CreateSuccess', function(obj, response, record){ this.fireEvent('CreateSuccess', this, obj, response, record); }, this);
		this.items.get(0).on('CreateFailure', function(obj, response){ this.fireEvent('CreateFailure', this, obj, response, issueId); }, this);
	},
	showWidget:function(){
		this.items.get(0).reset();
		this.show();
	},
	showWidget:function(typeId, typeName){
		this.items.get(0).reset();
		this.items.get(0).getForm().setValues({typeID: typeId, Category: typeName});
		this.loadInfo();
		this.show();
	},
	loadInfo:function(){
        /* get the data of handler and priority*/
		Ext.Ajax.request({
			url:'getCustomIssueComboInfo.do',
			success:function(response){
				//statusStoreForCreate.loadData(response.responseXML);
				priorityStoreForCreate.loadData(response.responseXML);
				handlerStoreForCreate.loadData(response.responseXML);
			}
		});
	}
});