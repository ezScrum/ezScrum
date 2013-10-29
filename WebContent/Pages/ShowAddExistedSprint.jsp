<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<%-- the page remove 2010.10.18
<script type="text/javascript" src="javascript/ux/gridfilters/menu/RangeMenu.js"></script>
<script type="text/javascript" src="javascript/ux/gridfilters/menu/ListMenu.js"></script>
<script type="text/javascript" src="javascript/ux/gridfilters/GridFilters.js"></script>
<script type="text/javascript" src="javascript/ux/gridfilters/filter/Filter.js"></script>
<script type="text/javascript" src="javascript/ux/gridfilters/filter/NumericFilter.js"></script>
<script type="text/javascript" src="javascript/ux/BufferView.js"></script>
<script type="text/javascript" src="javascript/ux/RowExpander.js"></script>
<script type="text/javascript" src="javascript/ux/RowEditor.js"></script>
<script type="text/javascript" src="javascript/ux/PagingMemoryProxy.js"></script>

<link rel="stylesheet" type="text/css" href="javascript/ux/gridfilters/css/GridFilters.css" />
<link rel="stylesheet" type="text/css" href="javascript/ux/gridfilters/css/RangeMenu.css" />

<script type="text/javascript">
	Ext.ns('ezScrum');
	
	var selectedData = new Array();
	
	/* 定義 Sptint 資料欄位 */
	var Sprint = Ext.data.Record.create([
	   {name:'Id', sortType:'asInt'}, 'Goal', 'StartDate', 'Interval', 
	   'Members', 'AvaliableDays', 'FocusFactor',
	   'DailyScrum', 'DemoDate', 'DemoPlace'
	]);
	
	/* Sprint Reader */
	/*
	 *	Example Data
	 *
	 *	<Root>
	 *		<Result>Success</Result>
	 *		<Sprint>
	 *			<Id>2388</Id>
	 *			<Goal>利用開發新功能以達成交接的目的</Goal>
	 *			<StartDate>2008/07/02</StartDate>
	 *			<Interval>2 week(s)</Interval>
	 *			<Members>2 person(s)</Members>
	 *			<AvaliableDays>20 days</AvaliableDays>
	 *			<FocusFactor>70 %</FocusFactor>
	 *			<DailyScrum>
	 *				科研館1321
	 * 				am 9:30   	
	 *			</DailyScrum>
	 *			<DemoDate>2008/07/14</DemoDate>
	 *			<DemoPlace>科研館1321</DemoPlace>
	 *		</Sprint>
	 *		<Sprint>
	 *			...
	 *		</Sprint>
	 *	</Root>
	 */
	var sprintReader = new Ext.data.XmlReader({
	   record: 'Sprint',
	   idPath : 'Id',
	   successProperty: 'Result'
	}, Sprint);
	
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
	/* 選取 Sprint 用的 CheckBox */
	var sm = new Ext.grid.CheckboxSelectionModel({
		dataIndex: 'Check',
		width: 20,
		listeners:{
    		selectionchange: function(){
    			//有選取項目則 enable button
	   			if (this.getCount() > 0 || selectedData.length > 0)
					Ext.getCmp('BacklogWidget').getTopToolbar().get('addSprintBtn').enable();
				else
					Ext.getCmp('BacklogWidget').getTopToolbar().get('addSprintBtn').disable();
    		}
    	}
	});
	/* 建立 GridView 的 Column */
    var createColModel = function () {
        var columns = [
        			sm,
		            {dataIndex: 'Id',header: 'Id', width: 40,filterable: true},
		            {dataIndex: 'Goal',header: 'Goal', width: 300},
		            {dataIndex: 'StartDate',header: 'StartDate', width: 70},
		            {dataIndex: 'Interval',header: 'Interval', width: 70},
		            {dataIndex: 'Members',header: 'Members', width: 70},
		            {dataIndex: 'AvaliableDays',header: 'Avaliable Days', width: 90},
		            {dataIndex: 'FocusFactor',header: 'Focus Factor', width: 90},
		            //{dataIndex: 'DailyScrum',header: 'DailyScrum', width: 70},
		            {dataIndex: 'DemoDate',header: 'DemoDate', width: 70}
		            //{dataIndex: 'DemoPlace',header: 'DemoPlace', width: 70}
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
    var sprintStore = new Ext.data.Store({
		remoteSort: true,
		fields:[
			{name : 'Id', type:'int'},
			{name : 'Goal'},
			{name : 'StartDate'},
			{name : 'Interval'},
			{name : 'Members'},
			{name : 'AvaliableDays'},
			{name : 'FocusFactor'},
			{name : 'DailyScrum'},
			{name : 'DemoDate'},
			{name : 'DemoPlace'}
		],
		reader:sprintReader,
		proxy: new Ext.ux.data.PagingMemoryProxy()
	});
    
    /* Grid View */
	Ext.onReady(function() {
		Ext.QuickTips.init();
		var sprintBacklogWidget = new Ext.grid.GridPanel({
			id : 'gridPanel',
			region : 'center',
			store: sprintStore,
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
			title : 'Add Existed Sprint',
			region : 'center',
			// Add Exist Sprint action
			addExistSprint:function()
			{
				var selections = selectedData.concat(masterWidget.getSelections());
				//填入 被勾選的 SprintID
				document.getElementById("selected").value = selections;
				//填入 ReleaseID
				document.getElementById("releaseID").value = getReleaseID();
				//送出 Release 加入 Sprint 資料
				document.selectForm.submit();
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
				{id:'addSprintBtn', text:'Add Sprint To Release', disabled:true, icon:'images/add3.png', handler: function(){masterWidget.addExistSprint();}},
				{id:'backBtn', text:'Back', icon:'images/back_16.gif', handler: function(){document.location.href  = "<html:rewrite action='/showReleasePlan' />";}}
			],bbar: new Ext.PagingToolbar({
			            pageSize: 25,
			            store: sprintStore,
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
									var record = sprintStore.getById(selectedData[i]);
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
			items : [sprintBacklogWidget]
		});
		
		var contentWidget = new Ext.Panel({
			height: 600,
			layout : 'border',
			renderTo: Ext.get("centent"),
			items : [masterWidget]
		});

		//showMask('BacklogWidget', "Loading...");
		
		//request Sprint 資料
		Ext.Ajax.request({
			url:'showExistedSprint2.do?releaseID=' + getReleaseID(),
			success:function(response){
				sprintStore.proxy.data = response;
				sprintStore.load({params:{start:0, limit:25}});
			},
			failure:failureFn
		});
	});
	
	function getReleaseID()
	{
		/* 取得網址參數 ReleaseID */
		var url = location.search;
		var rid;
		if(url.indexOf("?")!=-1) {
			var str = url.substr(1);
			rid = str.split("=")[1];
		}
		if (rid == undefined){
			rid = '';
		}
		return rid;
	}
</script>

<form name="selectForm" method="post" action="addSprintToRelease.do">
	<input type="hidden" value="" id="releaseID" name="releaseID">
	<input type="hidden" value="" id="selected" name="selected">
	<div id = "centent"></div>
</form>
--%>