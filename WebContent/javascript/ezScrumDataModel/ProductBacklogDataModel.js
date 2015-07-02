// Data store
var ProductBacklogStore = new Ext.data.Store({
	fields: [{
		name: 'Id',
		type: 'int'
	}, {
		name: 'Type'
	}, {
		name: 'Link'
	}, {
		name: 'Name'
	}, {
		name: 'Importance',
		type: 'int'
	}, {
		name: 'Estimate',
		type: 'float'
	}, {
		name: 'Status'
	}, {
		name: 'Release'
	}, {
		name: 'Sprint'
	}, {
		name: 'Notes'
	}, {
		name: 'HowToDemo'
	}, {
		name: 'Tag'
	}, {
		name: 'Attach'
	}],
	reader: jsonStoryReader,
	url: 'showProductBacklog2.do',
	proxy: new Ext.ux.data.MultiSingleSortingPagingMemoryProxy(null, IssueGridPanelFilter),
	remoteSort: true
});

var ProductBacklogRecords = new Ext.data.Store;

var ProductBacklogExpander = new Ext.ux.grid.RowExpander({
	tpl: new Ext.XTemplate(
		'<br><p><b>Name:</b><br /> {Name:nl2br}</p>',
		'<tpl if="Notes"><p><b>Notes:</b><br /> {Notes:nl2br}</p></tpl>',
		'<tpl if="HowToDemo"><p><b>How To Demo:</b><br /> {HowToDemo:nl2br}</p></tpl>',
		'<tpl for="AttachFileList"><p><b>Attach Files:</b><br /><a href="{FilePath}" target="_blank">{FileName}</a> [<a href="#" onclick="Ext.getCmp(\'productBacklogMasterPanel\').deleteAttachFile({FileId}, {IssueId}); return false;">Delete</a>]<br /></tpl>',
		'<br />'),
	enableCaching: false,
	getRowClass: function(record, rowIndex, p, store) {
		if (record.data['FilterType'] == null) {
			return "BACKLOG";
		}

		return record.data['FilterType'];
	}
});

var ProductBacklogCreateColModel = function() {

	var columns = [ProductBacklogExpander, {
		dataIndex: 'Id',
		header: 'Id',
		width: 50,
		filterable: true
	}, {
		dataIndex: 'Tag',
		header: 'Tag',
		width: 100
	}, {
		dataIndex: 'Name',
		header: 'Name',
		width: 300,
		renderer: function(value, metaData, record, rowIndex, colIndex, store) {
			if (record.data['Attach'] == 'true') return "<image src = \"./images/paperclip.png\" />" + value;
			return value
		}
	}, {
		dataIndex: 'Release',
		header: 'Release',
		width: 70
	}, {
		dataIndex: 'Sprint',
		header: 'Sprint',
		width: 70
	}, {
		dataIndex: 'Value',
		header: 'Value',
		width: 70
	}, {
		dataIndex: 'Estimate',
		header: 'Estimate',
		width: 70
	}, {
		dataIndex: 'Importance',
		header: 'Importance',
		width: 70
	}, {
		dataIndex: 'Status',
		header: 'Status',
		width: 70
	}];

	return new Ext.grid.ColumnModel({
		columns: columns,
		defaults: {
			sortable: true
		}
	});
};