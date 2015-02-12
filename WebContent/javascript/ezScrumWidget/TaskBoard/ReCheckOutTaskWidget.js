Ext.ns('ezScrum');
Ext.ns('ezScrum.layout');
Ext.ns('ezScrum.window');

var TaskStore_ForReCheckOutTask = new Ext.data.Store({
	idIndex	: 0,
	id		: 0,
	fields	:[
		{name : 'Id'},
		{name : 'Name'},
		{name : 'IssueType'},
		{name : 'Notes'}
		],
	reader	: taskJSReader
});

/* Check out Issue Form */
ezScrum.ReCheckOutForm = Ext.extend(ezScrum.layout.TaskBoardCardWindowForm, {
    initComponent : function() {
        var config = {
            url     : 'resetTask.do',
            loadUrl : 'showCheckOutIssue.do',
            items   : 
            		[{
                        fieldLabel	: 'ID',
                        name      	: 'Id',
						readOnly	: true,
						xtype: 'hidden'
                    },
                    {
                    	fieldLabel	: 'IssueType',
                    	name		: 'IssueType',
                    	xtype		: 'hidden'
                    },
                    {
                        fieldLabel	: 'Task Name',
                        name      	: 'Name',
                        allowBlank	: false
                    }, 
                    {
                        fieldLabel	: 'Notes',
                        xtype     	: 'textarea',
                        name      	: 'Notes',
                        height    	: 150
                    },
                    {
                    	allowBlank	: true,
                    	fieldLabel	: 'Specific Checked Out Time',
                    	name		: 'ChangeDate',
                    	format		: 'Y/m/d-H:i:s',
                    	xtype		: 'datefield'
                    },{
                    	xtype: 'RequireFieldLabel'
                    }],
            buttons : 
            		[{
						formBind : true,
                        text     : 'Reset Check Out',
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
        ezScrum.ReCheckOutForm.superclass.initComponent.apply(this, arguments);

        this.addEvents('RECOSuccess', 'RECOFailure', 'LoadTaskFailure');
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
    		var rs = jsonIssueReader.read(response);
			if(rs.success) {
				var record = rs.records[0];
				if(record)
				{
					this.fireEvent('RECOSuccess', this, response, record);
				}
			}
    	}
    },
    onEditFailure: function(response) {
        this.fireEvent('RECOFailure', this, response);
    },
    onLoadSuccess: function(response) {
    	ConfirmWidget.loadData(response);
    	if (ConfirmWidget.confirmAction()) {
			TaskStore_ForReCheckOutTask.loadData(Ext.decode(response.responseText));	// load task info

			var record = TaskStore_ForReCheckOutTask.getAt(0);
			if(record) {
				this.getForm().setValues({
					Id: record.data['Id'],
					Name: record.data['Name'], 
					IssueType : record.json['IssueType'],
					Partners: record.data['Partners'], 
					Notes: record.data['Notes'] 
				});
				
				// append issueID to window title. "RE_CheckOutTaskWindow" define in TaskBoardCardFormPanel.js
				RE_CheckOutTaskWindow.setTitle('Reset Checked Out Task #' + record.data['Id']);
				// this.fireEvent('LoadSuccess', this, response, record);
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
			url: obj.loadUrl,
			success: function(response) { obj.onLoadSuccess(response); },
			failure: function(response) { obj.onLoadFailure(response); },
			params : {issueID : id, issueType : 'Task'}
		});
    }
});

Ext.reg('ResetCheckOutTaskForm', ezScrum.ReCheckOutForm);

ezScrum.window.ReCheckOutWindow = Ext.extend(ezScrum.layout.Window, {
	title	: 'Reset Checked Out Task',
	initComponent : function() {
		var config = {
			layout : 'form',
			items  : [{	xtype : 'ResetCheckOutTaskForm'	}]
		}
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.window.ReCheckOutWindow.superclass.initComponent.apply(this, arguments);

		this.addEvents('RECheckOutSuccess', 'RECheckOutFailure', 'LoadFailure');		
		this.items.get(0).on('RECOSuccess', function(obj, response, record) { this.fireEvent('RECheckOutSuccess', this, response, record); }, this);
		this.items.get(0).on('RECOFailure', function(obj, response) { this.fireEvent('RECheckOutFailure', this, response); }, this);
		this.items.get(0).on('LoadTaskFailure', function(obj, response) { this.fireEvent('LoadFailure', this, response); }, this);
	},
	showWidget : function(taskID) {
		this.items.get(0).reset();
		this.items.get(0).loadTask(taskID);
		this.show();
	}
});