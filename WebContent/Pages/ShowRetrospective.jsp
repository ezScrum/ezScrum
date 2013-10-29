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
<script type="text/javascript" src="javascript/ux/BufferView.js"></script>
<script type="text/javascript" src="javascript/ux/RowExpander.js"></script>
<script type="text/javascript" src="javascript/ux/RowEditor.js"></script>
	
<script type="text/javascript" src="javascript/AjaxWidget/Common.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/CreateRetrospectiveWidget.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/EditRetrospectiveWidget.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/DeleteRetrospectiveWidget.js"></script>
	
<link rel="stylesheet" type="text/css" href="javascript/ux/gridfilters/css/GridFilters.css" />
<link rel="stylesheet" type="text/css" href="javascript/ux/gridfilters/css/RangeMenu.css" />
<link rel="stylesheet" type="text/css" href="javascript/ux/css/RowEditor.css" />

<script type="text/javascript" src="javascript/AjaxAction/edit_tag.js"></script>

<script type="text/javascript">
	Ext.ns('ezScrum');

	function showMask(targetId, msg)
	{
		new Ext.LoadMask(Ext.get(targetId), {msg:msg}).show();
	}
	
	function hideMask(targetId)
	{
		new Ext.LoadMask(Ext.get(targetId), {msg:msg}).hide();
	}

	function successFn(response)
	{
	 	Ext.getCmp('gridPanel').getStore().loadData(response.responseXML);

	 	hideMask('BacklogWidget');
	}
	
	function failureFn()
	{
		alert('Failure');
		hideMask('BacklogWidget');
	}
	
	var sprintComboStore = new Ext.data.Store({
	    id: 0,
	    fields: [
	        {name: 'Id', type: 'int'},
	   		{name: 'Name'}
	    ],
	    reader:sprintForComboReader
	});
	
	/* 建立 GridView 的 Column */
    var createColModel = function () {
        var columns = [
		            {dataIndex: 'Id',header: 'Id', width: 50,filterable: true,renderer: function(value, metaData, record, rowIndex, colIndex, store){return "<a href=\"" + record.data['Link'] + "\" target=\"_blank\">" + value + "</a>";}},
		            {dataIndex: 'SprintID',header: 'SprintID', width: 50},
		            {dataIndex: 'Name',header: 'Name', width: 200},
		            {dataIndex: 'Type',hidden: true, header: 'Type', width: 90},
		            {dataIndex: 'Description',header: 'Description', width: 150},
		            {dataIndex: 'Status',header: 'Status', width: 90}
		        ];

        return new Ext.grid.ColumnModel({
            columns: columns,
            defaults: {
                sortable: true
            }
        });
    };
    
	var thisSprintStore = new Ext.data.Store({
		fields:[
			{name : 'Id', sortType:'asInt'},
			{name : 'Name'}
		],
		reader:sprintForComboReader
	});
    
    /* Grid View */
	Ext.onReady(function() {
		Ext.QuickTips.init();
		
		// Create Retrospective Widget
		var createRetrospectiveWidget = new ezScrum.AddNewRetrospectiveWidget({
			listeners:{
				CreateSuccess:function(win, form, response, record){
					// Create Retrospective Success
					/* 將這頁的 Sprint ComboBox 設定成新增的 SprintID */
					var comboInGrid = Ext.getCmp('BacklogWidget').getTopToolbar().findById('SprintCombo');
					/*假如combobox目前的狀態是All代表傳入的是當下的sprintID而非All
					  所以會導致雖然新增到該sprintID的retrospective但是新增成功後的畫面會
					  跑到該sprintID但因為設計的關係要強制將畫面跳轉到All的畫面
					*/
					var tempSprintID ;
			 		if( comboInGrid.getValue() == "All" ){
			 			comboInGrid.originalValue = "All";
			 			tempSprintID = "All";
			 		}else{
			 			tempSprintID = record.data['SprintID'];
			 			comboInGrid.originalValue = "Sprint #" + tempSprintID;
			 		}
			 		comboInGrid.reset();
			 		this.hide();
			 		/* 取得剛新增 Retrospective 所屬 Sprint 的資料 */
			 		Ext.Ajax.request({
						url:'showRetrospective2.do?sprintID=' + tempSprintID,
						success:successFn,
						failure:failureFn
					});
				},
				CreateFailure:function(win, form, response, issueId){
					// Create Retrospective Error
				}
			}
		});
		
		// Edit Retrospective Widget
		var editRetrospectiveWidget = new ezScrum.EditRetrospectiveWidget({
			listeners:{
				LoadSuccess:function(win, form, response, record){
					// Load Retrospective Success
				},
				LoadFailure:function(win, form, response, issueId){
					// Load Retrospective Error
				},
				EditSuccess:function(win, form, response, record){
					// Edit Retrospective Success
					var s = Ext.getCmp('gridPanel').getStore();
					var index = (s.findExact('Id', record.data['Id']));
					s.removeAt(index);
			 		var d = new (s.recordType)({Id : record.data['Id'], Name : record.data['Name'], SprintID : record.data['SprintID'], Type : record.data['Type'], Status : record.data['Status'], Description : record.data['Description'], Link:record.data['Link']});
			 		s.insert(index,d);
			 		/* 修改 Type 後需排序, Group 才不會有問題 */
			 		s.sort('Type', 'ASC');
			 		index = (s.findExact('Id', record.data['Id']));
			 		
			 		Ext.getCmp('gridPanel').getSelectionModel().selectRow(index);
			 		Ext.getCmp('gridPanel').getView().focusRow(index);
			 		this.hide();
				},
				EditFailure:function(win, form, response, issueId){
					// Edit Retrospective Error
				}
			}
		});
		
		// Delete Retrospective Widget
		var deleteRetrospectiveWidget = new ezScrum.DeleteRetrospectiveWidget({
			listeners:{
				DeleteSuccess:function(win, response, issueId, sprintID){
					// Delete Retrospective Success
					var s = Ext.getCmp('gridPanel').getStore();
					var index = s.indexOfId(issueId);
					s.removeAt(index);
					this.hide();
					
					var comboInGrid = Ext.getCmp('BacklogWidget').getTopToolbar().findById('SprintCombo');
					if( comboInGrid.getValue() == "All" ){//假如combo狀態是All則將刪除該retrospective後的畫面導向All
						sprintID = "All";			
					}
					/* 取得剛刪除 Retrospective 後 該 Sprint 的資料 */
			 		Ext.Ajax.request({
						url:'showRetrospective2.do?sprintID=' + sprintID,
						success:successFn,
						failure:failureFn
					});
				},
				DeleteFailure:function(win, response, issueId){
					// Delete Retrospective Error
				}
			}
		});

		var filters = new Ext.ux.grid.GridFilters({
			local:true,
	        filters: [{
	            type: 'numeric',
	            dataIndex: 'Id'
	        }]
	    });
	    
	    // create the Data Store
	    var thisSprintStore = new Ext.data.Store({
			fields:[
				{name : 'Id', sortType:'asInt'},
				{name : 'Name'}
			],
			reader:sprintForComboReader
		});
	    
	    // create the Data Store
	    var retrospectiveStore = new Ext.data.GroupingStore({
			fields:[
				{name : 'Id', type:'int'},
				{name : 'Link'},
				{name : 'SprintID'},
				{name : 'Name'},
				{name : 'Type'},
				{name : 'Description'},
				{name : 'Status'}
			],
			reader:retReader,
			sortInfo:{field: 'Id', direction: "ASC"},
			groupField:'Type'
		});
		
		var retrospectiveBacklogWidget = new Ext.grid.GridPanel({
			id : 'gridPanel',
			region : 'center',
			store: retrospectiveStore,
	        viewConfig: {
	            forceFit:true
	        },
		    colModel: createColModel(),
		    sm: new Ext.grid.RowSelectionModel({
		    	singleSelect:true
		    }),
		    stripeRows: true,
		    frame: true,
		    view: new Ext.grid.GroupingView({
	            forceFit:true,
	            groupTextTpl: '{text} ({[values.rs.length]} {[values.rs.length > 1 ? "Items" : "Item"]})'
	        })
		});
		
		Ext.Ajax.request({
			url:'AjaxGetSprintPlanList.do',
			success:function(response){
				sprintComboStore.loadData(response.responseXML);
			},
			failure:failureFn
		});
		
		/* Main Widget */
		var masterWidget = new Ext.Panel({
			id:'BacklogWidget',
			layout:'border',
			title : 'Retrospective Info',
			region : 'center',
			addRetrospectivePermission: false,
			editRetrospectivePermission: false,
			deleteRetrospectivePermission: false,
			// Add Retrospective action
			addRetrospective:function()
			{
				/* 將這頁所選擇的 Sprint 傳到 CreateWidget */
				var comboInGrid = masterWidget.getTopToolbar().findById('SprintCombo');
				var selectedItem ;
 				if( comboInGrid.getValue() == "All" ){//假如傳入的是All則傳送目前的sprintID
 					selectedItem = thisSprintStore.getAt(0).get('Id');
				}else{
					selectedItem = comboInGrid.getValue();
				}
				createRetrospectiveWidget.showWidget( selectedItem );
			},
			// Edit Retrospective action
			editRetrospective:function()
			{
				if(Ext.getCmp('gridPanel').getSelectionModel().getSelected() != null)
				{
					var id = Ext.getCmp('gridPanel').getSelectionModel().getSelected().data['Id'];
					editRetrospectiveWidget.loadEditRetrospective(id);
				}
			},
			// Delete Retrospective action
			deleteRetrospective:function()
			{
				if(Ext.getCmp('gridPanel').getSelectionModel().getSelected() != null)
				{
					var id = Ext.getCmp('gridPanel').getSelectionModel().getSelected().data['Id'];
					deleteRetrospectiveWidget.deleteRetrospective(id);
				}
			},
			tbar: [
				{id:'addRetrospectiveBtn', text:'Add Retrospective', icon:'images/add3.png', handler: function(){masterWidget.addRetrospective();}},
				{id:'editRetrospectiveBtn', disabled:true, text:'Edit Retrospective', icon:'images/edit.png', handler:function(){masterWidget.editRetrospective();}},
				{id:'deleteRetrospectiveBtn', disabled:true, text:'Delete Retrospective', icon:'images/delete.png', handler: function(){masterWidget.deleteRetrospective();}},
				'->',
				(
					new Ext.form.ComboBox({
					    typeAhead: true,
					    triggerAction: 'all',
					    lazyRender: true,
					    editable: false,
					    mode: 'local',
					    store: sprintComboStore,
					    valueField: 'Id',
					    displayField: 'Name',
					    id: 'SprintCombo',
					    width: 150,
					    listeners:{
					    	select:function(combox, record, index){
					    		Ext.Ajax.request({
									url:'showRetrospective2.do?sprintID=' + record.data['Id'],
									success:successFn,
									failure:failureFn
								});
					    	}
						}
					})
				)
			],bbar: [
			],
			items : [retrospectiveBacklogWidget],
			selectionChange:function()
			{
				/* grid 中資料列只選擇一列則可以編輯+刪除  */
				var single = retrospectiveBacklogWidget.getSelectionModel().getCount()==1;
				if(single)
				{
					this.getTopToolbar().get('editRetrospectiveBtn').setDisabled(!this.editRetrospectivePermission);					
					this.getTopToolbar().get('deleteRetrospectiveBtn').setDisabled(!this.deleteRetrospectivePermission);
				}
				else
				{
					this.getTopToolbar().get('editRetrospectiveBtn').disable();
					this.getTopToolbar().get('deleteRetrospectiveBtn').disable();
				}
			},
			loadPermission:function(addRetrospectivePermission, editRetrospectivePermission, deleteRetrospectivePermission)
			{
				/* 設定權限, 'true' 為可操作 */
				this.addRetrospectivePermission = addRetrospectivePermission == "true";
				this.editRetrospectivePermission = editRetrospectivePermission == "true";
				this.deleteRetrospectivePermission = deleteRetrospectivePermission == "true";
				if(this.addRetrospectivePermission ){
					this.getTopToolbar().get('addRetrospectiveBtn').enable();
				}
			}
		});
		retrospectiveBacklogWidget.getSelectionModel().on({'selectionchange':{buffer:10, fn:function(){masterWidget.selectionChange();}}});
		
		var contentWidget = new Ext.Panel({
			height: 600,
			layout : 'border',
			renderTo: Ext.get("centent"),
			items : [masterWidget]
		});

		/* 取得 Retrospective 資料 */
		Ext.Ajax.request({
			url:'showRetrospective2.do',
			success:function(response){
	 			retrospectiveStore.loadData(response.responseXML);
			},
			failure:failureFn
		});
		
		/* 取得 Retrospective Permission 資料 */
		Ext.Ajax.request({
			url:'AjaxGetRETPermission.do',
			success:function(response){
				var permissionRs = retPermissionReader.readRecords(response.responseXML);
		 		var permissionCount = permissionRs.totalRecords;
		 		for(var i = 0; i < permissionCount; i++)
		 		{
		 			/* 設定 Permission Widget 中 */
		 			var permissionRecord = permissionRs.records[i];
					masterWidget.loadPermission(permissionRecord.data['AddRetrospective'], permissionRecord.data['EditRetrospective'],permissionRecord.data['DeleteRetrospective'],"true");     
		 		}
			}
		});
		/* 取得 sprint combo 當下的 sprint ID */ 
		Ext.Ajax.request({
			url:'showUnplannedItem2.do',
			params:{ SprintID:""},
			success:function(response){
				thisSprintStore.loadData(response.responseXML);
				var combo = Ext.getCmp('SprintCombo');
				if (combo.getStore().getTotalCount()  > 1) {//因為上面 showUnplannedItem2.do 載入的資料沒有sprint也會有一個sprintID叫做Sprint # -1 所以至少有一個預設值
					combo.originalValue = thisSprintStore.getAt(0).get('Id');
					combo.reset();
				}else{
					combo.originalValue = "All";//如果沒有存在任何一筆sprint的資料則預設為All
					Ext.getCmp('BacklogWidget').getTopToolbar().get('addRetrospectiveBtn').disable();//當沒有Sprint的時候預設為All並且不能add Retrospective
					combo.reset();//combobox 重畫
				}
			},
			failure:failureFn
		});
	});

</script>

<div id = "centent"></div>

<div id="SideShowItem" style="display:none;">showRetrospective</div>