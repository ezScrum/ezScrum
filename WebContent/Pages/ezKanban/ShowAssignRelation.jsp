<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

	<script type="text/javascript" src="javascript/ux/gridfilters/menu/RangeMenu.js"></script>
	<script type="text/javascript" src="javascript/ux/gridfilters/menu/ListMenu.js"></script>
	<script type="text/javascript" src="javascript/ux/gridfilters/GridFilters.js"></script>
	<script type="text/javascript" src="javascript/ux/gridfilters/filter/Filter.js"></script>
	<script type="text/javascript" src="javascript/ux/gridfilters/filter/NumericFilter.js"></script>
	<script type="text/javascript" src="javascript/ux/BufferView.js"></script>
	<script type="text/javascript" src="javascript/ux/RowExpander.js"></script>
	<script type="text/javascript" src="javascript/ux/RowEditor.js"></script>
	<script type="text/javascript" src="javascript/ux/PagingMemoryProxy.js"></script>
	
	<script type="text/javascript" src="javascript/CustomWidget/Common.js"></script>
	<script type="text/javascript" src="javascript/KanbanWidget/Common.js"></script>
	
	<link rel="stylesheet" type="text/css" href="javascript/ux/gridfilters/css/GridFilters.css" />
	<link rel="stylesheet" type="text/css" href="javascript/ux/gridfilters/css/RangeMenu.css" />
	
<script type="text/javascript">
	Ext.ns('ezScrum');
	
	var selectedData = new Array();
	
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
		hideMask('BacklogWidget');
	}
	/* 選取 WorkItem 用的 CheckBox */
	var sm = new Ext.grid.CheckboxSelectionModel({
		dataIndex: 'Check',
		width: 20,
		listeners:{
    		selectionchange: function(){
    			//有選取項目則 enable button
	   			if (this.getCount() > 0 || selectedData.length > 0)
					Ext.getCmp('BacklogWidget').getTopToolbar().get('assignBtn').enable();
				else
					Ext.getCmp('BacklogWidget').getTopToolbar().get('assignBtn').disable();
    		}
    	}
	});
	/* 建立 GridView 的 Column */
    var createColModel = function () {
        var columns = [
        			sm,
		            {dataIndex: 'Id',header: 'Id', width: 50,filterable: true,renderer: function(value, metaData, record, rowIndex, colIndex, store){var link = "<a href=\"" + record.data['Link'] + "\" target=\"_blank\">" + value + "</a>"; return link;}},
		            {dataIndex: 'Name',header: 'Name', width: 300},
		            {dataIndex: 'Status',header: 'Status', width: 70},
		            {dataIndex: 'Priority',header: 'Priority', width: 70}
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
        }]
    });
    
    /* create the Data Store */
    var workitemStore = new Ext.data.Store({
    	remoteSort: true,
		fields:[
			{name : 'Id', type:'int'},
			{name : 'Link'},
			{name : 'Name'},
			{name : 'Status'},
			{name : 'Priority'}
		],
		reader:jsonWorkItemReader,
		proxy: new Ext.ux.data.PagingMemoryProxy()
	});
    
    /* Grid View */
	Ext.onReady(function() {
		Ext.QuickTips.init();
		/* issue Widget */
		var workitemsWidget = new Ext.grid.GridPanel({
			id : 'gridPanel',
			region : 'center',
			store: workitemStore,
	        viewConfig: {
	            forceFit:true
	        },
		    plugins: [filters],
		    colModel: createColModel(),
		    selModel: sm,
		    stripeRows: true,
		    frame: true
		});
		
		/* Main Widget */
		var masterWidget = new Ext.Panel({
			id:'BacklogWidget',
			layout:'border',
			title : 'Create Relation',
			region : 'center',
			// create relation action
			createRelation:function()
			{
				var selections = selectedData.concat(masterWidget.getSelections());

				//送出 Release 加入 Sprint 資
				Ext.Ajax.request({
					url:'ajaxCreateIssueRelation.do',
					params: { 
						issueID: getIssueID(),
						selected: selections.toString()
					},
					success:function(){
						document.location.href  = "./showKanbanBacklog.do";
					},
					failure:failureFn
				});
			},
			getSelections:function()
			{
				//回傳目前有被勾選的資料
				var selections = new Array();
				records = sm.getSelections();
				for(var i = 0; i < records.length; i++)
				{
					selections.push(records[i].data['Id']);
				}
				return selections;
			},
			tbar: [
				{id:'assignBtn', text:'Create Relation', disabled:true, icon:'images/add3.png', handler: function(){masterWidget.createRelation();}},
				{id:'backBtn', text:'Back', icon:'images/back_16.gif', handler: function(){document.location.href  = "<html:rewrite action='/showKanbanBacklog' />";}}
			],bbar: new Ext.PagingToolbar({
			            pageSize: 25,
			            store: workitemStore,
			            displayInfo: true,
			            displayMsg: 'Displaying topics {0} - {1} of {2}',
			            emptyMsg: "No topics to display",
			            items:[
			            ],
			            listeners: {
							render: function(c){
								c.refresh.hideParent = true;
								c.refresh.hide();
							},
							beforechange: function(){
								//換頁前將有被勾選的資料存入selectedData
								selectedData = selectedData.concat(masterWidget.getSelections());
							},
							change: function(){
								var currentData = new Array();
								var otherData = new Array();
								//所有被勾選的資料
								for (var i = 0; i < selectedData.length; i++)
								{
									//目前頁面的資料有被勾選則存入currentData
									//別的頁面被勾選的資料則存入otherData
									var record = workitemStore.getById(selectedData[i]);
									if (record)
										currentData.push(record);
									else
										otherData.push(selectedData[i]);
								}
								selectedData = otherData;
								//勾選資料
								if(sm.grid)
									sm.selectRecords(currentData);
							}
						}
			        })
			,
			items : [workitemsWidget]
		});
		
		var contentWidget = new Ext.Panel({
			height: 600,
			layout : 'border',
			renderTo: Ext.get("centent"),
			items : [masterWidget]
		});

		//showMask('BacklogWidget', "Loading...");
		
		//request workitems 資料
		Ext.Ajax.request({
			url:'showAssignRelationAction.do?issueID=' + getIssueID(),
			success:function(response){
				workitemStore.proxy.data = response;
				workitemStore.load({params:{start:0, limit:25}});
			},
			failure:failureFn
		});
	});
	
	function getIssueID()
	{
		/* 取得網址參數 IssueID */
		var url = location.search;
		var iid;
		if(url.indexOf("?")!=-1) {
			var str = url.substr(1);
			str = str.split("&")[0];
			iid = str.split("=")[1];
		}
		if (iid == undefined){
			iid = '';
		}
		return iid;
	}
</script>

<div id = "centent">
</div>