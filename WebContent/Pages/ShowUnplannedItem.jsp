<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>

<script type="text/javascript" src="javascript/ux/gridfilters/menu/RangeMenu.js"></script>
<script type="text/javascript" src="javascript/ux/gridfilters/menu/ListMenu.js"></script>
<script type="text/javascript" src="javascript/ux/gridfilters/GridFilters.js"></script>
<script type="text/javascript" src="javascript/ux/gridfilters/filter/Filter.js"></script>
<script type="text/javascript" src="javascript/ux/gridfilters/filter/StringFilter.js"></script>
<script type="text/javascript" src="javascript/ux/gridfilters/filter/DateFilter.js"></script>
<script type="text/javascript" src="javascript/ux/gridfilters/filter/ListFilter.js"></script>
<script type="text/javascript" src="javascript/ux/gridfilters/filter/NumericFilter.js"></script>
<script type="text/javascript" src="javascript/ux/gridfilters/filter/BooleanFilter.js"></script>
<script type="text/javascript" src="javascript/ux/gridfilters/filter/TagFilter.js"></script>

<script type="text/javascript" src="javascript/ux/PagingMemoryProxy.js"></script>

<script type="text/javascript" src="javascript/AjaxWidget/Common.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/CreateUnplannedItemWidget.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/EditUnplannedItemWidget.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/DeleteUnplannedItemWidget.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/Message.js"></script>

<link rel="stylesheet" type="text/css" href="javascript/ux/gridfilters/css/GridFilters.css" />
<link rel="stylesheet" type="text/css" href="javascript/ux/gridfilters/css/RangeMenu.css" />
<link rel="stylesheet" type="text/css" href="css/Message.css"/>

<script type="text/javascript">
	Ext.ns('ezScrum');
	
	/* 定義 UnplannedItem 資料欄位 */
	var UnplannedItem = Ext.data.Record.create([
	   {name:'Id', sortType:'asInt'}, 'Name', 'SprintID', {name:'Estimate', sortType:'asFloat'}, 'Status', 'ActualHour', 'Handler', 'Partners', 'Notes', 'Link'
	]);
	
	var unplannedItemReader = new Ext.data.XmlReader({
	   record: 'UnplannedItem',
	   idPath : 'Id',
	   successProperty: 'Result'
	}, UnplannedItem);
	
	var unplannedItemStore = new Ext.data.Store({
    	fields:[
			{name : 'Id', type:'int'},
			{name : 'Link'},
			{name : 'Name'},
			{name : 'SprintID'},
			{name : 'Estimate', type:'float'},
			{name : 'Status'},
			{name : 'ActualHour'},
			{name : 'Handler'},
			{name : 'Partners'},
			{name : 'Notes'}
		],
		reader : unplannedItemReader
	});
	
	var sprintComboStore = new Ext.data.Store({
	    id: 0,
	    fields: [
	        {name: 'Id', type: 'int'},
	   		{name: 'Name'}
	    ],
	    reader:sprintForComboReader
	});
	
	// create the Data Store
    var thisSprintStore = new Ext.data.Store({
		fields:[
			{name : 'Id', sortType:'asInt'},
			{name : 'Name'}
		],
		reader:sprintForComboReader
	});
	
	var createColModel = function () {
        var columns = [
		            {dataIndex: 'Id',header: 'Id', width: 50,filterable: true,renderer: function(value, metaData, record, rowIndex, colIndex, store){return "<a href=\"" + record.data['Link'] + "\" target=\"_blank\">" + value + "</a>";}},
		            {dataIndex: 'Name',header: 'Name', width: 300},
		            {dataIndex: 'Estimate',header: 'Estimate', width: 70},
		            {dataIndex: 'Status',header: 'Status', width: 70},
		            {dataIndex: 'ActualHour',header: 'Actual', width: 70},
		            {dataIndex: 'Handler',header: 'Handler', width: 70},
		            {dataIndex: 'Partners',header: 'Partners', width: 70},
		            {dataIndex: 'Notes',header: 'Notes', width: 70}
		        ];

        return new Ext.grid.ColumnModel({
            columns: columns,
            defaults: {
                sortable: true
            }
        });
    };
    
    var filters = new Ext.ux.grid.GridFilters({
		local:true,
        filters: [{
            type: 'numeric',
            dataIndex: 'Id'
        },{
        	type: 'list',
        	dataIndex: 'Status',
        	options: ['new', 'closed']
        }]
    });

	function showMask(targetId, msg)
	{
		new Ext.LoadMask(Ext.get(targetId), {msg:msg}).show();
	}
	
	function hideMask(targetId)
	{
		new Ext.LoadMask(Ext.get(targetId), {msg:msg}).hide();
	}
	
	function failureFn()
	{
		alert('Failure');
		hideMask('UnplannedItemsWidget');
	}
	
	
	Ext.onReady(function() {
		// Create Unplanned Item Widget
		var createUnplannedItemWidget = new ezScrum.CreateUnplannedItemWidget({
			listeners:{
				CreateSuccess:function(win, form, response, record){
			 		this.hide();
			 		
			 		Ext.Ajax.request({
						url:'showUnplannedItem2.do',
						params:{ SprintID:record.data['SprintID']},
						success:function(response){
							hideMask('UnplannedItemsWidget');
							// check action permission
							ConfirmWidget.loadData(response);
								if (ConfirmWidget.confirmAction()) {
							
								unplannedItemStore.loadData(response.responseXML);
	
								var combo = Ext.getCmp('selectSprintCombo');
								combo.originalValue = combo.getStore().getAt(combo.getStore().indexOfId(record.data['SprintID'])).get('Name');
								combo.reset();
								
								var s = Ext.getCmp('UnplannedItemsWidget').getStore();
								var index = s.indexOfId(record.data['Id']);
								Ext.getCmp('UnplannedItemsWidget').getSelectionModel().selectRow(index);
						 		Ext.getCmp('UnplannedItemsWidget').getView().focusRow(index);
						 		
						 		Ext.example.msg('Create UnplannedItem', 'Create UnplannedItem Success.');
						 	}
						},
						failure: failureFn
					});
					
					
				},
				CreateFailure:function(win, form, response, issueId){
					// Create Unplanned Item Error
					Ext.example.msg('Create UnplannedItem', 'Create UnplannedItem Failure.');
				}
			}
		});

		// Edit Unplanned Item Widget
		var editUnplannedItemWidget = new ezScrum.EditUnplannedItemWidget({
			listeners:{
				LoadSuccess:function(win, form, response, record){
					// Load Story Success
				},
				LoadFailure:function(win, form, response, issueId){
					// Load Unplanned Item Error
				},
				EditSuccess:function(win, form, response, record){
					// Edit Unplanned Item Success
					var s = Ext.getCmp('UnplannedItemsWidget').getStore();
					var index = (s.findExact('Id', record.data['Id']));
					s.removeAt(index);

			 		this.hide();
		 		
			 		Ext.Ajax.request({
						url:'showUnplannedItem2.do',
						params:{ SprintID:record.data['SprintID']},
						success:function(response){
							// check action permission
							ConfirmWidget.loadData(response);
							if (ConfirmWidget.confirmAction()) {	
								unplannedItemStore.loadData(response.responseXML);
								hideMask('UnplannedItemsWidget');
	
								var combo = Ext.getCmp('selectSprintCombo');
								combo.originalValue = combo.getStore().getAt(combo.getStore().indexOfId(record.data['SprintID'])).get('Name');
								combo.reset();
								
								var s = Ext.getCmp('UnplannedItemsWidget').getStore();
								var index = s.indexOfId(record.data['Id']);
								Ext.getCmp('UnplannedItemsWidget').getSelectionModel().selectRow(index);
						 		Ext.getCmp('UnplannedItemsWidget').getView().focusRow(index);
						 		
						 		Ext.example.msg('Edit UnplannedItem', 'Edit UnplannedItem Success.');
						 	}
						},
						failure:failureFn
					});
				},
				EditFailure:function(win, form, response, issueId){
					// Edit Unplanned Item Error
					Ext.example.msg('Edit UnplannedItem', 'Edit UnplannedItem Failure.');
				}
			}
		});
		
		// Delete Unplanned Item Widget
		var deleteUnplannedItemWidget = new ezScrum.DeleteUnplannedItemWidget({
			listeners:{
				DeleteSuccess:function(win, response, issueId){
					// Delete Unplanned Item Success
					var s = Ext.getCmp('UnplannedItemsWidget').getStore();
					var index = s.indexOfId(issueId);
					s.removeAt(index);
					
					this.hide();
					Ext.example.msg('Delete UnplannedItem', 'Delete UnplannedItem Success.');
				},
				DeleteFailure:function(win, response, issueId){
					// Delete Unplanned Item Error
					Ext.example.msg('Delete UnplannedItem', 'Delete UnplannedItem Failure.');
				}
			}
		});
		
		var unplannedItemsWidget = new Ext.grid.GridPanel({
			id : 'UnplannedItemsWidget',
			region : 'center',
			store : unplannedItemStore,
			viewConfig: {
	            forceFit:true
	        },
			plugins: [filters],
			colModel: createColModel(),
			sm: new Ext.grid.RowSelectionModel({
		    	singleSelect:true
		    }),
		    stripeRows: true,
		    frame: true
		});
		
		
		var masterWidget = new Ext.Panel({
			id:'MasterWidget',
			layout:'border',
			region : 'center',
			addUnplannedItemPermission: false,
			editUnplannedItemPermission: false,
			deleteUnplannedItemPermission: false,
			// Create Unplanned Item action
			createUnplannedItem:function()
			{
                if(Ext.getCmp('selectSprintCombo').getStore().getTotalCount() == 0)
                {
                    Ext.Msg.alert("Error","現在沒有Sprint可以新增");
                }
                else
                {
				    createUnplannedItemWidget.showWidget(Ext.getCmp('selectSprintCombo').getValue());
                }
			},
			// Edit Unplanned Item action
			editUnplannedItem:function()
			{
				if(Ext.getCmp('UnplannedItemsWidget').getSelectionModel().getSelected() != null)
				{
					var id = Ext.getCmp('UnplannedItemsWidget').getSelectionModel().getSelected().data['Id'];
					editUnplannedItemWidget.loadEditUnplannedItem(id);
				}
			},
			// Delete Unplanned Item action
			deleteUnplannedItem:function()
			{
				if(Ext.getCmp('UnplannedItemsWidget').getSelectionModel().getSelected() != null)
				{
					var id = Ext.getCmp('UnplannedItemsWidget').getSelectionModel().getSelected().data['Id'];
					deleteUnplannedItemWidget.deleteUnplannedItem(id);
				}
			},
			// Show Issue History action
		    showHistory:function() {
		        if(Ext.getCmp('UnplannedItemsWidget').getSelectionModel().getSelected() != null) {
		            var id = Ext.getCmp('UnplannedItemsWidget').getSelectionModel().getSelected().data['Id'];
		            document.location.href  = "showIssueHistory.do?issueID=" + id;
		        }
		    },
			tbar: [
				{id:'addUnplannedItemBtn', disabled:true, text:'Add Unplanned Item', icon:'images/add3.png',handler: function(){masterWidget.createUnplannedItem();}},
				{id:'editUnplannedItemBtn', disabled:true, text:'Edit Unplanned Item', icon:'images/edit.png', handler:function(){masterWidget.editUnplannedItem();}},
				{id:'deleteUnplannedItemBtn', disabled:true, text:'Delete Unplanned Item', icon:'images/delete.png', handler: function(){masterWidget.deleteUnplannedItem();}},
				{id:'showUnplannedItemHistoryBtn', disabled:true, text:'Unplanned Item History', icon:'images/history.png', handler:function(){masterWidget.showHistory();}},
				'->',
				{
					xtype:'combo',
					id:'selectSprintCombo', 
					editable:false, 
					triggerAction:'all',
					forceSelection: true,
					mode:'local',
					store: sprintComboStore,
	    			valueField: 'Id',
	    			displayField: 'Name',
	    			listeners     : {
	    				// hide cursor
					    'expand': function(combo) {
					        var blurField = function(el) {
					            el.blur();
					        }
					        blurField.defer(10,this,[combo.el]);
					    },
					    'collapse': function(combo) {
					        var blurField = function(el) {
					            el.blur();
					        }
					        blurField.defer(10,this,[combo.el]);
				    	},
				    	'select': function (combo,record){
				    		Ext.Ajax.request({
								url:'showUnplannedItem2.do',
								params:{ SprintID:record.data['Id']},
								success: function(response){
									hideMask('UnplannedItemsWidget');
									// check action permission
									ConfirmWidget.loadData(response);
									if (ConfirmWidget.confirmAction()) {
										unplannedItemStore.loadData(response.responseXML);
									}
								},
								failure:failureFn
							});
				    	}
					}
				}
			],bbar: [
			],
			items : [unplannedItemsWidget],
			selectionChange:function()
			{
				var single = unplannedItemsWidget.getSelectionModel().getCount()==1;
				if(single)
				{
					this.getTopToolbar().get('editUnplannedItemBtn').setDisabled(!this.editUnplannedItemPermission);					
					this.getTopToolbar().get('deleteUnplannedItemBtn').setDisabled(!this.deleteUnplannedItemPermission);	
					this.getTopToolbar().get('showUnplannedItemHistoryBtn').enable();		
				}
				else
				{
					this.getTopToolbar().get('editUnplannedItemBtn').disable();
					this.getTopToolbar().get('deleteUnplannedItemBtn').disable();
					this.getTopToolbar().get('showUnplannedItemHistoryBtn').disable();
				}
				
			},
			loadPermission:function(addUnplannedItemPermission, editUnplannedItemPermission, deleteUnplannedItemPermission)
			{
				/* 設定權限, 'true' 為可操作 */
				this.addUnplannedItemPermission = addUnplannedItemPermission == "true";
				this.editUnplannedItemPermission = editUnplannedItemPermission == "true";
				this.deleteUnplannedItemPermission = deleteUnplannedItemPermission == "true";
				if(this.addUnplannedItemPermission)
					this.getTopToolbar().get('addUnplannedItemBtn').enable();
			}
		});
		
		unplannedItemsWidget.getSelectionModel().on({'selectionchange':{buffer:10, fn:function(){masterWidget.selectionChange();}}});
		
		var contentWidget = new Ext.Panel({
			height: 600,
			layout : 'border',
			title : 'Unplanned Item',
			renderTo: Ext.get("content"),
			items : [masterWidget]
		});
		
		showMask('UnplannedItemsWidget', "Loading...");
		
		Ext.Ajax.request({
			url:'AjaxGetSprintPlanList.do',
			success:function(response){
				sprintComboStore.loadData(response.responseXML);
			},
			failure:failureFn
		});
		
		Ext.Ajax.request({
			url:'showUnplannedItem2.do',
			params:{ SprintID:""},
			success:function(response){
				// check action permission
				ConfirmWidget.loadData(response);
				if (ConfirmWidget.confirmAction()) {
					unplannedItemStore.loadData(response.responseXML);
					thisSprintStore.loadData(response.responseXML);
					
					var combo = Ext.getCmp('selectSprintCombo');
					if (combo.getStore().getTotalCount() > 0)
					{
						combo.originalValue = thisSprintStore.getAt(0).get('Name');
						combo.reset();
					}
				}
				
			 	hideMask('UnplannedItemsWidget');
			},
			failure:failureFn
		});
		
		Ext.Ajax.request({
			url:'AjaxGetUIPermission.do',
			success:function(response){
				var permissionRs = UIPermissionReader.readRecords(response.responseXML);
		 		var permissionCount = permissionRs.totalRecords;
		 		for(var i = 0; i < permissionCount; i++)
		 		{
		 			/* 設定 Permission Widget 中 */
		 			var permissionRecord = permissionRs.records[i];
					masterWidget.loadPermission(permissionRecord.data['AddUnplannedItem'], permissionRecord.data['EditUnplannedItem'],permissionRecord.data['DeleteUnplannedItem'],"true");     
		 		}
			}
		});
	
      
	});
</script>

<div id = "content"></div>

<% session.setAttribute("currentSideItem","showUnplannedItem");%>
<div id="SideShowItem" style="display:none;">showUnplannedItem</div>