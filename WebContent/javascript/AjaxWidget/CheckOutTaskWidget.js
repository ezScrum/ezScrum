Ext.ns('ezScrum');

// Handler Data
var handlerComboStoreForCO = new Ext.data.Store({
    id		: 0,
    fields	: [{name: 'Name'}],
    reader	: handlerReader,
    proxy	: new Ext.data.HttpProxy({url: "./AjaxGetHandlerList.do", method: 'POST'})
});

// Handler ComboBox
var handlerComboForCO = new Ext.form.ComboBox({
    typeAhead		: true,
    triggerAction	: 'all',
    allowBlank		: false,
    lazyRender		: true,
    editable		: false,
    mode			: 'local',
    store			: handlerComboStoreForCO,
    emptyText		: 'Please choose a handler',
    name			: 'Handler',
    valueField		: 'Name',
    displayField	: 'Name',
    fieldLabel		: 'Handler',
    id				: 'HandlerComboBoxForCO',
    anchor    		: '96%'
});

var TaskStore = new Ext.data.Store({
	idIndex	: 0,
	id		: 0,
	fields	:[
		{name : 'Id'},
		{name : 'Name'},
		{name : 'Notes'},
		{name : 'Handler'},
		{name : 'Partners'}
		],
	reader	: taskJSReader
});

var CODate = new Ext.form.DateField({
	allowBlank	: true,
	fieldLabel	: 'Specific Checked Out Time',
	name		: 'ChangeDate',
	format		: 'Y/m/d-H:i:s',
	anchor		: '96%'
});

// Check out Issue Form
ezScrum.CheckOutForm = Ext.extend(Ext.form.FormPanel, {
    bodyStyle     : 'padding:15px',
    border        : false,
    defaultType   : 'textfield',
    labelAlign    : 'right',
    labelWidth    : 100,
    defaults      : {
        width     : 450,
        msgTarget : 'side'
    },
    monitorValid  : true,
    initComponent : function() {
        var config = {
            url     : 'checkOutTask.do',
            loadUrl : 'showCheckOutIssue.do',
            items   : 
            		[{
                        fieldLabel	: 'ID',
                        name      	: 'Id',
                        readOnly	: true,
                        width 		: '95%'
                    },
                    {
                        fieldLabel	: 'Task Name',
                        name      	: 'Name',
                        width 		: '95%'
                    },
                    handlerComboForCO,
                    { 
	                    fieldLabel	: 'Partners',
                        name      	: 'Partners',
                        width 		: '95%'
					},
                    {
                        fieldLabel	: 'Notes',
                        xtype     	: 'textarea',
                        name      	: 'Notes',
                        width 		: '95%',
                        height    	: 150
                    },
                    CODate
                    ],
            buttons : 
            		[{
						formBind : true,
                        text     : 'Check Out',
                        scope    : this,
                        handler  : this.submit,
                        disabled : true
                    }, {
                        text    : 'Cancel',
                        scope   : this,
                        handler : function() {	this.ownerCt.hide();  }
                    }]
        }

        Ext.apply(this, Ext.apply(this.initialConfig, config));
        ezScrum.CheckOutForm.superclass.initComponent.apply(this, arguments);

        this.addEvents('COSuccess', 'COFailure', 'LoadTaskFailure');
    },
    submit: function() {
        var form = this.getForm();
        var obj = this;
        
        Ext.Ajax.request({
			url     : this.url,
			params  : form.getValues(),
			success : function(response) {
				obj.onEditSuccess(response);
			},
			failure : function(response) {
				obj.onEditFailure(response);
			}		
		});
    },
    onEditSuccess: function(response) {
    	ConfirmWidget.loadData(response);
    	if (ConfirmWidget.confirmAction()) {
    		this.fireEvent('COSuccess', this, response);
    	}
    },
    onEditFailure: function(response) {
        this.fireEvent('COFailure', this, response);
    },
    onLoadSuccess: function(response) {
    	ConfirmWidget.loadData(response);
    	if (ConfirmWidget.confirmAction()) {
			TaskStore.loadData(Ext.decode(response.responseText));	// load task info
			handlerComboStoreForCO.load();							// load handler combo box value
				
			var record = TaskStore.getAt(0);
			if(record) {
				this.getForm().setValues({
					Id: record.data['Id'],
					Name: record.data['Name'], 
					Partners: record.data['Partners'],
					Notes: record.data['Notes']
				});
				
				handlerComboForCO.originalValue = record.data['Handler'];
				handlerComboForCO.reset();
				
				//this.fireEvent('LoadSuccess', this, response, record);
			}
    	}
    },
    onLoadFailure: function(response) {
        this.fireEvent('LoadTaskFailure', this, response);
    },
    reset: function() {
        this.getForm().reset();
    },
    loadTask: function(id) {
        var obj = this;
        
    	Ext.Ajax.request({
			url: this.loadUrl,
			success: function(response) { obj.onLoadSuccess(response); },
			failure: function(response) { obj.onLoadFailure(response); },
			params : {issueID : id}
		});
    }
});

Ext.reg('CheckOutTaskForm', ezScrum.CheckOutForm);

ezScrum.CheckOutWidget = Ext.extend(Ext.Window, {
	title	: 'Check Out Task',
	width	: 600,
	modal         : true,
    constrain	  : true,
	closeAction	: 'hide',
	initComponent : function() {
		var config = {
			layout : 'form',
			items  : [{	xtype : 'CheckOutTaskForm'	}]
		}
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.CheckOutWidget.superclass.initComponent.apply(this, arguments);

		this.addEvents('CheckOutSuccess', 'CheckOutFailure', 'LoadFailure');		
		this.items.get(0).on('COSuccess', function(obj, response) { this.fireEvent('CheckOutSuccess', this, response); }, this);
		this.items.get(0).on('COFailure', function(obj, response) { this.fireEvent('CheckOutFailure', this, response); }, this);
		this.items.get(0).on('LoadTaskFailure', function(obj, response) { this.fireEvent('LoadFailure', this, response); }, this);
	},
	getFormValues:function(valueName)
	{
		return this.items.get(0).getForm().getValues();
	}
	,
	showWidget : function(taskID) {
		this.items.get(0).reset();
		this.items.get(0).loadTask(taskID);
		this.show();
	}
});