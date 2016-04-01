Ext.ns('ezScrum');
Ext.ns('ezScrum.layout');
Ext.ns('ezScrum.window');

var Page_Selected_items = new Array();

var ExistedStory_CheckBoxModel = new Ext.grid.CheckboxSelectionModel({
	listeners:{
		selectionchange: function() {
   			if (this.getCount() > 0 || Page_Selected_items.length > 0) {
				Ext.getCmp('SelectTask_Window').getTopToolbar().get('AddExistedStoryBtn').enable();
   			} else {
				Ext.getCmp('SelectTask_Window').getTopToolbar().get('AddExistedStoryBtn').disable();
   			}
		}
	}
});

var SelectTasksColumnModel = function() {
    var columns = [
		ExistedStory_Expander, ExistedStory_CheckBoxModel,
		{dataIndex: 'Id',header: 'Id', width: 30, filterable: true/*, renderer: makeIssueDetailUrl*/},
		{dataIndex: 'Name',header: 'Name', width: 300},
	];

    return new Ext.grid.ColumnModel({
        columns: columns,
        defaults: {
            sortable: true
        }
    });
};
var ExistedStoryStore = new Ext.data.Store({
	fields:[
		{name : 'Id', type:'int'},
		{name : 'Link'},
		{name : 'Name'},
	],
	reader : myReader,
	proxy : new Ext.ux.data.PagingMemoryProxy()
});