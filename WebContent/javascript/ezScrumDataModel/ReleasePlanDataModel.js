/*-----------------------------------------------------------
 *   Story的儲存結構
 -------------------------------------------------------------*/
var releaseStoryStore = new Ext.data.Store({
	fields: [{
		name: 'Id',
		type: 'int'
	}, {
		name: 'Link'
	}, {
		name: 'Tag'
	}, {
		name: 'Name'
	}, {
		name: 'Value',
		type: 'int'
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
	}],
	reader: myReader
});

var Sprint_release = Ext.data.Record.create([{
	name: 'Id',
	sortType: 'asInt'
}, 'Goal', 'StartDate', 'Interval', 'Members', 'AvaliableDays', 'FocusFactor', 'DailyScrum', 'DemoDate', 'DemoPlace', 'Check']);

var SprintReader_release = new Ext.data.XmlReader({
	record: 'Sprint',
	idPath: 'Id',
	successProperty: 'Result',
	totalProperty: "StoryPoint"
}, Sprint_release);

var createSprintStore_release = new Ext.data.Store({
	id: 'createSprintStore_release',
	fields: [{
		name: 'Id',
		type: 'int'
	}, {
		name: 'Goal'
	}, {
		name: 'StartDate'
	}, {
		name: 'Interval'
	}, {
		name: 'Members'
	}, {
		name: 'AvaliableDays'
	}, {
		name: 'FocusFactor'
	}, {
		name: 'DailyScrum'
	}, {
		name: 'DemoDate'
	}, {
		name: 'DemoPlace'
	}],
	reader: SprintReader_release
});

/*
 * 提供給Release List的欄位內容
 */
var releaseColumns = [{
	header: 'ID',
	dataIndex: 'ID',
	width: 150,
	align: 'center'
}, {
	header: 'Name',
	dataIndex: 'Name',
	width: 250,
	align: 'left'
}, {
	header: 'Start Date',
	width: 150,
	dataIndex: 'StartDate',
	align: 'center'
}, {
	header: 'End Date',
	width: 150,
	dataIndex: 'EndDate',
	align: 'center'
}, {
	header: 'Description',
	width: 250,
	dataIndex: 'Description',
	align: 'center'
}];

var createReleaseStoryCloumns = function() {
	var columns = [{
		dataIndex: 'Id',
		header: 'ID',
		width: 50,
	}, {
		dataIndex: 'Tag',
		header: 'Tag',
		width: 90
	}, {
		dataIndex: 'Name',
		header: 'Story Name',
		width: 400
	}, {
		dataIndex: 'Value',
		header: 'Value',
		width: 90
	}, {
		dataIndex: 'Estimate',
		header: 'Estimate',
		width: 90
	}, {
		dataIndex: 'Importance',
		header: 'Importance',
		width: 90
	}, {
		dataIndex: 'Status',
		header: 'Status',
		width: 90
	}, {
		dataIndex: 'Release',
		header: 'Release Id',
		width: 90
	}, {
		dataIndex: 'Sprint',
		header: 'Sprint Id',
		width: 90
	}];

	return new Ext.grid.ColumnModel({
		columns: columns,
		defaults: {
			sortable: true
		}
	});
};

/*
 * =================================== Show Release Backlog ==================================
 */
var pageSize_ReleaseBacklog = 15;

var ReleaseBacklogStoryStore = new Ext.data.Store({
	fields: [{
		name: 'Id',
		type: 'int'
	}, {
		name: 'Link'
	}, {
		name: 'Tag'
	}, {
		name: 'Name'
	}, {
		name: 'Value',
		type: 'int'
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
	}],
	reader: myReader,
	proxy: new Ext.ux.data.PagingMemoryProxy(null, ReleasebacklogGridFilter)
});

var ReleaseBacklogColumns = function() {
	var columns = [{
		dataIndex: 'Id',
		header: 'Story ID',
		width: 60
	}, {
		dataIndex: 'Name',
		header: 'Story Name',
		width: 325
	}, {
		dataIndex: 'Importance',
		header: 'Importance',
		width: 80
	}, {
		dataIndex: 'Sprint',
		header: 'Sprint ID',
		width: 65
	}, {
		dataIndex: 'Estimate',
		header: 'Estimate',
		width: 80
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

// Filter
var ReleasebacklogGridFilter = new Ext.ux.grid.GridFilters({
	local: false,
	filters: [{
		type: 'numeric',
		dataIndex: 'Id'
	}, {
		type: 'list',
		dataIndex: 'Status',
		options: ['new', 'closed']
	}]
});

var ReleaseBacklogGridExpander = new Ext.ux.grid.RowExpander({
	tpl: new Ext.XTemplate('<br><p><b>Name:</b><br /> {Name:nl2br}</p>', '<tpl if="Value"><p><b>Value:</b><br /> {Value:nl2br}</p></tpl>',
			'<tpl if="Notes"><p><b>Notes:</b><br /> {Notes:nl2br}</p></tpl>', '<tpl if="HowToDemo"><p><b>How To Demo:</b><br /> {HowToDemo:nl2br}</p></tpl>', '<br />'),
	enableCaching: false
});
