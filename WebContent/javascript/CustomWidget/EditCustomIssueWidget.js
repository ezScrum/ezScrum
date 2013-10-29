Ext.ns('ezScrum');

/* Status Data */
var statusStoreForEdit = new Ext.data.Store({
    id: 0,
    fields: [
   		{name: 'Name'}
    ],
    reader: statusReader
});

/* Priority Data */
var priorityStoreForEdit = new Ext.data.Store({
    id: 0,
    fields: [
   		{name: 'Name'}
    ],
    reader: priorityReader
});

/* Handler Data */
var handlerStoreForEdit = new Ext.data.Store({
    id: 0,
    fields: [
   		{name: 'Name'}
    ],
    reader: handlerReader
});

/* Status ComboBox */
var statusComboForEdit = new Ext.form.ComboBox({
    typeAhead: true,
    triggerAction: 'all',
    lazyRender: true,
    editable: false,
    mode: 'local',
    store: statusStoreForEdit,
    name: 'StatusComboBoxForEdit',
    valueField: 'Name',
    displayField: 'Name',
    fieldLabel: 'Status',
    id: 'StatusComboBoxForEditForEdit'
});

/* Priority ComboBox */
var priorityComboForEdit = new Ext.form.ComboBox({
    typeAhead: true,
    triggerAction: 'all',
    lazyRender: true,
    editable: false,
    mode: 'local',
    store: priorityStoreForEdit,
    name: 'PriorityComboBoxForEdit',
    valueField: 'Name',
    displayField: 'Name',
    fieldLabel: 'Priority',
    id: 'PriorityComboBoxForEdit'
});

/* Handler ComboBox */
var handlerComboForEdit = new Ext.form.ComboBox({
    typeAhead: true,
    triggerAction: 'all',
    lazyRender: true,
    editable: false,
    mode: 'local',
    store: handlerStoreForEdit,
    name: 'HandlerComboBoxForEdit',
    valueField: 'Name',
    displayField: 'Name',
    fieldLabel: 'Handler',
    id: 'HandlerComboBoxForEdit'
});


/* Edit Custom Issue Form */
ezScrum.EditCustomIssueForm = Ext.extend(Ext.form.FormPanel, {
	// Default issue id
	issueId : '-1',
	bodyStyle: 'padding:15px',
	border : false,
	defaultType: 'textfield',
	labelAlign : 'right',
	labelWidth : 100,
	defaults: {
        width: 500,
        msgTarget: 'side'
    },
    monitorValid: true,
	initComponent:function() {
		var config = {
			// Ajax edit story url 
			url : 'ajaxEditCustomIssue.do',
			// Ajax load story url
			loadUrl : 'getEditCustomIssueInfo.do',
			loadComboUrl : 'getCustomIssueComboInfo.do',
			items: [
				{
		            fieldLabel: 'ID',
		            name: 'Id',
					disabled: true
		        }, {
		            fieldLabel: 'Name',
		            name: 'Name',
		            allowBlank: false,
		            maxLength: 128
		        }, {
		            fieldLabel: 'Category',
		            name: 'Category',
		            disabled: true
		        },  statusComboForEdit
		         ,	priorityComboForEdit
		         ,  handlerComboForEdit
		         , {
		        	fieldLabel: 'Description',
		            xtype: 'textarea',
		            name: 'Description',
		            height:200
		        }, {
		            name: 'IssueCategory',
		            hidden: true
		        }, {
			        name: 'issueID',
		            hidden: true
		        }
		    ],
		    buttons: 
		    [{
		    	formBind:true,
	    		text: 'Submit',
	    		scope:this,
	    		handler: this.submit
	    	},
	        {
	        	text: 'Cancel',
	        	scope:this,
	        	handler: function(){this.ownerCt.hide();}
	        }]
        }
        
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.EditCustomIssueForm.superclass.initComponent.apply(this, arguments);
					
		this.addEvents('LoadSuccess', 'LoadFailure', 'EditSuccess', 'EditFailure');
	},
	loadStore:function(){
		var obj = this;
		var myMask = new Ext.LoadMask(obj.getEl(), {msg:"Please wait..."});
		myMask.show();
		Ext.Ajax.request({
			url:this.loadUrl,
			success:function(response){obj.onLoadSuccess(response);},
			failure:function(response){obj.onLoadFailure(response);},
			params : {issueID : this.issueId}
		});
	},
	//click submit action 
	submit : function()
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.show();	
		var obj = this;
		var form = this.getForm();
		Ext.Ajax.request({
			url:this.url,
			success:function(response){obj.onEditSuccess(response);},
			failure:function(response){obj.onEditFailure(response);},
			params:form.getValues()
		});
	},
	onRender:function() {
		ezScrum.EditCustomIssueForm.superclass.onRender.apply(this, arguments);
		this.getForm().waitMsgTarget = this.getEl();

	},
	// Load Custom Issue success
	onLoadSuccess:function(response) 
	{
		var rs = jsonCustomIssueReader.read(response);
		if(rs.success)
		{
			var record = rs.records[0];
			if(record)
			{
				this.getForm().setValues({
				 	StatusComboBoxForEdit : record.data['Status'],
					HandlerComboBoxForEdit : record.data['Handler'],
					PriorityComboBoxForEdit : record.data['Priority'],
					Id : record.data['Id'], 
					issueID : record.data['Id'],
					Name : record.data['Name'], 
					Category : record.data['Category'], 
					IssueCategory : record.data['Category'],
					Description : record.data['Description']
				});
				this.loadComboInfo();
			}
		}
	},
	onLoadFailure:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		this.fireEvent('LoadFailure', this, response, this.issueId);
	},
	loadComboInfo: function()
	{
		var obj = this;
		var myMask = new Ext.LoadMask(obj.getEl(), {msg:"Please wait..."});
        /* get the data of handler and priority*/
		Ext.Ajax.request({
			url:this.loadComboUrl,
			success:function(response){
				
				priorityStoreForEdit.loadData(response.responseXML);
				statusStoreForEdit.loadData(response.responseXML);
				handlerStoreForEdit.loadData(response.responseXML);
				myMask.hide();
			},
			failure:function(response){
				myMask.hide();
				this.fireEvent('LoadFailure', this, response, this.issueId);
			},
			params:{category: this.form.findField('Category').getValue()}
		});
		
	},	
	// Update customIssue success
	onEditSuccess:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		var rs = jsonCustomIssueReader.read(response);
		if(rs.success)
		{
			var record = rs.records[0];
			if(record)
			{
				this.fireEvent('EditSuccess', this, response, record);
			}
		}
		else
			this.fireEvent('EditFailure', this, response, this.issueId);
	},
	// Update story failure
	onEditFailure:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		this.fireEvent('EditFailure', this, response, this.issueId);
	},

	reset:function()
	{
		this.getForm().reset();
	}

});
Ext.reg('editCustomIssueForm', ezScrum.EditCustomIssueForm);

ezScrum.EditCustomIssueWidget = Ext.extend(Ext.Window, {
	width:700,
	modal:false,
	closeAction:'hide',
	initComponent:function() {
		var config = {
			layout:'form',
			items : [{xtype:'editCustomIssueForm'}]
        }
			Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.EditCustomIssueWidget.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('LoadSuccess', 'LoadFailure', 'EditSuccess', 'EditFailure');
		
		this.items.get(0).on('LoadSuccess', function(obj, response, record){ this.fireEvent('LoadSuccess', this, obj, response, record); }, this);
		this.items.get(0).on('LoadFailure', function(obj, response, issueId){ this.fireEvent('LoadFailure', this, obj, response, issueId); }, this);
		this.items.get(0).on('EditSuccess', function(obj, response, record){ this.fireEvent('EditSuccess', this, obj, response, record); }, this);
		this.items.get(0).on('EditFailure', function(obj, response, issueId){ this.fireEvent('EditFailure', this, obj, response, issueId); }, this);
	},
	loadEditStory:function(issueId){
		this.items.get(0).issueId = issueId;
		this.items.get(0).reset();
		this.show();
		this.items.get(0).loadStore();
	}
});