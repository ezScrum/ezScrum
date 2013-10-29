Ext.ns('ezScrum');
Ext.ns('ezScrum.window');

var AddMember_CheckBoxModel = new Ext.grid.CheckboxSelectionModel({
	listeners:{
		selectionchange: function() {
   			if (this.getCount() > 0 || Page_Selected_items.length > 0) {
				Ext.getCmp('AddMember_Window').getTopToolbar().get('Member_AddSelectedMemberBtn').enable();
   			} else {
				Ext.getCmp('AddMember_Window').getTopToolbar().get('Member_AddSelectedMemberBtn').disable();
   			}
		}
	}
});

var ExistedMemberColumnModel = new Ext.grid.ColumnModel({
	columns: [ 
	    AddMember_CheckBoxModel,
		{dataIndex: 'ID',header: 'User ID', width: 200},
		{dataIndex: 'Name',header: 'User Name', width: 200},		            
		{dataIndex: 'Role',header: 'Role', width: 200},
		{dataIndex: 'Enable',header: 'Enable', renderer: checkUser, width: 70},
	]
});

var ExistedMemberRecord = Ext.data.Record.create([
	'ID', 'Name', 'Role', 'Enable'
]);

var ExistedMemberReader = new Ext.data.XmlReader({
	record: 'Member',
	idPath : 'ID'
}, ExistedMemberRecord);

var ExistedMemberStore = new Ext.data.Store({
	fields:[
	        {name : 'ID'},
	        {name : 'Name'},	
	        {name : 'Role'},
	        {name : 'Enable'}
    ],
	reader : ExistedMemberReader
});

ezScrum.AddMemberGridPanel = Ext.extend(Ext.grid.GridPanel, {
	id			: 'Member_AddMemberGrid',
	stripeRows	: true,
	loadMask	: true,
	url			: 'getRemainingProjectMembers.do',
	store		: ExistedMemberStore,
	colModel	: ExistedMemberColumnModel,
	sm			: AddMember_CheckBoxModel,
	viewConfig	: {
        forceFit: true
    },
   
    loadDataModel: function() {
    	
		MainLoadMaskShow();
		
		var obj = this;
		Ext.Ajax.request({
			url: obj.url,
			success : function(response) {
				MainLoadMaskHide(response);
				ExistedMemberStore.loadData(response.responseXML);
			},
			failure : function() {
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
			}
		});
    }
    
});

Ext.reg('AddMemberGrid', ezScrum.AddMemberGridPanel);

ezScrum.window.AddMemberWidget = Ext.extend(ezScrum.layout.Window, {
	
	id			: 'AddMember_Widget',
	title		: 'Add Selected Member',
	height		: 300,
	initComponent : function() {
		var config = {
			layout : 'fit',
			items  : [
				{ ref: 'Member_AddMemberGrid', xtype : 'AddMemberGrid' }
			],
			tbar: [{
				id		: 'Member_AddSelectedMemberBtn',
				text	: 'Add Selected Members',
				icon	: 'images/add3.png',
				disabled: true,
				handler	: function() { }
			}, {
				id		: 'Member_CancelBtn',
				text	: 'Cancel',
				icon	: 'images/cancel.png',
				disabled: false,
				scope   : this,
                handler : function() { this.hide(); }
			}],
			bbar: new Ext.PagingToolbar({
	            pageSize: 15,
	            store: ExistedStoryStore,
	            displayInfo: true,
	            displayMsg: 'Displaying topics {0} - {1} of {2}',
	            emptyMsg: "No topics to display",
	            items:[
	                {
	                	text : 'Reload',
	                	icon : 'images/refresh.png',
	                	handler: function() { Ext.getCmp('AddMember_Window').Member_AddMemberGrid.loadDataModel(); }
	                }
	            ],
	            listeners: {
					render: function(c) {
						c.refresh.hideParent = true;
						c.refresh.hide();
					}					
				}
			})		
		}
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.window.AddMemberWidget.superclass.initComponent.apply(this, arguments);
		
	},
    showWindow: function() {    	
    	this.show();  	
    	this.Member_AddMemberGrid.loadDataModel();
    	
    }
});

var AddMember_Widget = new ezScrum.window.AddMemberWidget();