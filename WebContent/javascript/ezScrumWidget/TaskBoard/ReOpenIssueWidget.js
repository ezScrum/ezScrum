Ext.ns('ezScrum');
Ext.ns('ezScrum.layout');
Ext.ns('ezScrum.window');

var IssueStore_ForReOpenStory = new Ext.data.Store( {
	idIndex : 0,
	id : 0,
	fields : [
		{name : 'Id'},
		{name : 'IssueType'},
		{name : 'Name'},
		{name : 'Notes'},
		{name : 'Partners'}
	],
	reader : storyJSReader
});

var IssueStore_ForReOpenTask = new Ext.data.Store( {
	idIndex : 0,
	id : 0,
	fields : [
		{name : 'Id'},
		{name : 'IssueType'},
		{name : 'Name'},
		{name : 'Notes'},
		{name : 'Partners'}
	],
	reader : taskJSReader
});

// reopen Issue Form
ezScrum.ReOpenForm = Ext.extend(ezScrum.layout.TaskBoardCardWindowForm, {
	initComponent : function() {
		var config = {
			url			: 'reopenIssue.do',
			loadUrl		: 'showCheckOutIssue.do',
			items : [ {
				fieldLabel	: 'ID',
				name		: 'Id',
				readOnly	: true,
				xtype: 'hidden'
			}, {
            	fieldLabel	: 'IssueType',
            	name		: 'IssueType',
            	xtype		: 'hidden'
            }, {
				fieldLabel	: 'Name',
				name		: 'Name',
				allowBlank	: false
			}, {
				fieldLabel	: 'Notes',
				xtype		: 'textarea',
				name		: 'Notes',
				height 		: 150
			}, {
				allowBlank	: true,
				fieldLabel	: 'Specific Checked Out Time',
				name		: 'ChangeDate',
				format		: 'Y/m/d-H:i:s',
				xtype		: 'datefield'
			},{
            	xtype: 'RequireFieldLabel'
            }],
			buttons : [ {
				formBind : true,
				text : 'Re Open',
				scope : this,
				handler : this.submit,
				disabled : true
			}, {
				text : 'Cancel',
				scope : this,
				handler : function() {
					this.ownerCt.hide();
				}
			} ]
		}

		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.ReOpenForm.superclass.initComponent.apply(this, arguments);

		this.addEvents('ReOSuccess', 'ReOFailure', 'LoadIssueFailure');
	},
	submit : function() {
		var form = this.getForm();
		var obj = this;

		Ext.Ajax.request( {
			url : obj.url,
			params : form.getValues(),
			success : function(response) {
				obj.onEditSuccess(response);
			},
			failure : function(response) {
				obj.onEditFailure(response);
			}
		});
	},
	onEditSuccess : function(response) {
		ConfirmWidget.loadData(response);
    	if (ConfirmWidget.confirmAction()) {
    		var rs = jsonIssueReader.read(response);
			if(rs.success) {
				var record = rs.records[0];
				if(record) {
					this.fireEvent('ReOSuccess', this, response, record);
				}
			}
    	}
	},
	onEditFailure : function(response) {
		this.fireEvent('ReOFailure', this, response);
	},
	onLoadSuccess : function(response) {
		ConfirmWidget.loadData(response);
		if (ConfirmWidget.confirmAction()) {
			var record;
			// load issue info
    		if (response.responseText.includes('Story')) {
    			IssueStore_ForReOpenStory.loadData(Ext.decode(response.responseText));
    			record = IssueStore_ForReOpenStory.getAt(0);
    		} else {
    			IssueStore_ForReOpenTask.loadData(Ext.decode(response.responseText));
    			record = IssueStore_ForReOpenTask.getAt(0);
    		}
    		
			if (record) {
				this.getForm().setValues( {
					Id : record.data['Id'],
					Name : record.data['Name'],
					IssueType : record.json['IssueType'],
					Partners : record.data['Partners'],
					Notes : record.data['Notes']
				});

				// append issueID to window title. "RE_OpenIssueWindow" define in TaskBoardCardFormPanel.js
				RE_OpenIssueWindow.setTitle('Re Opened ' + record.json['IssueType'] + ' #' + record.data['Id']);
			}
		}
	},
	onLoadFailure : function(response) {
		this.fireEvent('LoadIssueFailure', this, response);
	},
	reset : function() {
		this.getForm().reset();
	},
	loadIssue : function(id, type) {
		var obj = this;
		
		Ext.Ajax.request( {
			url : obj.loadUrl,
			params : { issueID : id	, issueType: type},
			success : function(response) { obj.onLoadSuccess(response);	},
			failure : function(response) { obj.onLoadFailure(response);	}
		});
	}
});

Ext.reg('ShowReOpenForm', ezScrum.ReOpenForm);

ezScrum.window.ReOpenIssueWindow = Ext.extend(ezScrum.layout.Window, {
	title : 'Re Opened Issue',
	initComponent : function() {
		var config = {
			layout : 'form',
			items : [ {
				xtype : 'ShowReOpenForm'
			} ]
		}

		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.window.ReOpenIssueWindow.superclass.initComponent.apply(this, arguments);

		this.addEvents('ReOpenSuccess', 'ReOpenFailure', 'LoadFailure');
		this.items.get(0).on('ReOSuccess', function(obj, response, record) { this.fireEvent('ReOpenSuccess', this, response, record); }, this);
		this.items.get(0).on('ReOFailure', function(obj, response) { this.fireEvent('ReOpenFailure', this, response); }, this);
		this.items.get(0).on('LoadIssueFailure', function(obj, response) { this.fireEvent('LoadFailure', this, response); }, this);
	},
	showWidget : function(issueId, issueType) {
		this.items.get(0).reset();
		this.items.get(0).loadIssue(issueId, issueType);
		this.show();
	}
});