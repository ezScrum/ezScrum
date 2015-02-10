Ext.ns('ezScrum');
Ext.ns('ezScrum.layout');
Ext.ns('ezScrum.window');

var IssueHistoryStore = new Ext.data.Store({
	fields: [{
		name: 'Id'
	}, {
		name: 'Link'
	}, {
		name: 'Name'
	}, {
		name: 'IssueType'
	}],
	reader: IssueHistoryReader
});

var IssueHistoryListStore = new Ext.data.Store({
	fields: [{
		name: 'Description'
	}, {
		name: 'HistoryType'
	}, {
		name: 'ModifiedDate'
	}],
	reader: IssueHistoryListReader
});

ezScrum.IssueHistoryGridPanel = Ext.extend(Ext.grid.GridPanel, {
	url			: 'showIssueHistory.do',
	issueID		: '-1',
	issueType	: '',
	store		: IssueHistoryListStore,
	colModel	: IssueHistoryListColumnModel,
	title		: ' ',
	height		: 500,
	stripeRows	: false,
	frame		: false,
	viewConfig	: {
		forceFit	: true,
		getRowClass	: function(record, index, rowParams, store) {
			var key_Css = ['Importance', 'Estimate', 'Sprint', 'Status', 'Add', 'Drop', 'Append', 'Remove', 'ActualHour'];

			for ( var i = 0; i < key_Css.length; i++) {
				if (record.get('HistoryType').match(key_Css[i])) {
					return "ISSUE_" + key_Css[i];
				}
			}
		}
	},
	loadDataModel: function() {
		var obj = this;

		Ext.Ajax.request({
			url: obj.url,
			params: {
				issueID		: obj.issueID,
				issueType	: obj.issueType
			},
			success: function(response) {
				ConfirmWidget.loadData(response);
				if (ConfirmWidget.confirmAction()) {
					IssueHistoryStore.loadData(Ext.decode(response.responseText)); // load issue info
					IssueHistoryListStore.loadData(Ext.decode(response.responseText)); // load issue history info

					obj.notifyTitle(IssueHistoryStore.getAt(0));
				}
			},
			failure: function() {
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
			}
		});
	},
	setIssueID: function(id) {
		this.issueID = id;
	},
	setIssueType: function(type) {
		this.issueType = type;
	},
	notifyTitle: function(record) {
		var title_info = '＜' + record.get('IssueType') + '＞ ' + '#' + record.get('Id') + ' ' + record.get('Name');
		title_info = '<font size="2">' + title_info + '</font>';

		this.setTitle(title_info);
	}
});

Ext.reg('IssueHistoryListGrid', ezScrum.IssueHistoryGridPanel);

ezScrum.window.IssueHistoryWindow = Ext.extend(ezScrum.layout.Window, {
	title: 'Issue History List',
	autoScroll: true,
	buttonAlign: 'center',
	initComponent: function() {
		var config = {
			layout: 'fit',
			items: [{
				xtype: 'IssueHistoryListGrid'
			}],
			buttons: [{
				text: 'Close',
				scope: this,
				handler: function() {
					this.hide();
				}
			}]
		}

		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.window.IssueHistoryWindow.superclass.initComponent.apply(this, arguments);
	},
	showTheWindow: function(issueId, issueType) {
		this.items.get(0).setIssueID(issueId);
		this.items.get(0).setIssueType(issueType);
		this.items.get(0).loadDataModel();

		this.show();
	}
});

/**
 * call method 1. showTheWindow: function(issueID)
 * shared with: 1. ProductBacklog 2. SprintBacklog 3. Unplanned 4. TaskBoard
 */
var IssueHistory_Window = new ezScrum.window.IssueHistoryWindow();