	Ext.ns('ezScrum');
	
	var selectedData = new Array();
	
	function showMask(targetId, msg)
	{
		new Ext.LoadMask(Ext.get(targetId), {msg:msg}).show();
	}
	
	function hideMask(targetId)
	{
		new Ext.LoadMask(Ext.get(targetId)).hide();
	}
	
	function failureFn()
	{
		alert('Failure');
		this.hideMask('taskBacklogWidgetId');
	}
	
	/* 選取 Sprint 用的 CheckBox */
	var sm = new Ext.grid.CheckboxSelectionModel({
		dataIndex: 'Check',
		width: 20,
		listeners:{
    		selectionchange: function(){
    			//有選取項目則 enable button
	   			if (this.getCount() > 0 || selectedData.length > 0){
	   				addExistedTaskButton.enable();
	   				deleteExistedTaskButton.enable();
	   			}
				else{
					addExistedTaskButton.disable();
					deleteExistedTaskButton.disable();
				}
    		}
    	}
	});
	/* 建立 GridView 的 Column */
    var createColModel = function () {
        var columns = [
        			sm,
		            {dataIndex: 'Id',header: 'Id', width: 50,filterable: true/*, renderer: makeIssueDetailUrl*/},
		            {dataIndex: 'Name',header: 'Name', width: 300},
		            {dataIndex: 'Status',header: 'Status', width: 70},
		            {dataIndex: 'Estimate',header: 'Estimate', width: 70},
		            {dataIndex: 'Handler',header: 'Handler', width: 70}
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
    var taskStore = new Ext.data.Store({
    	remoteSort: true,
		fields:[
			{name : 'Id', type:'int'},
			{name : 'Link'},
			{name : 'Name'},
			{name : 'Status'},
			{name : 'Estimate', type:'float'},
			{name : 'Actual'},
			{name : 'Handler'},
			{name : 'Partners'},
			{name : 'Notes'}
		],
		reader:taskReader,
		proxy: new Ext.ux.data.PagingMemoryProxy()
	});
    
    /* Grid View */
	var taskBacklogWidget = new Ext.grid.GridPanel({
		id : 'taskBacklogWidgetId',
		region : 'center',
		height:400,
		contrain:true,
		store: taskStore,
	    viewConfig: {
	    	forceFit:true
	    },
		plugins: [filters],
		colModel: createColModel(),
		selModel: sm,
		stripeRows: true,
		frame: true
	});
		
	var addExistedTaskButton = new Ext.Toolbar.Button({
		text:'Add Task To Story' , 
		id:'addTaskButtonId',
		disabled:true,
		icon:'images/add3.png',
	    handler:function(){
	    	existedTaskPanel.addExistTask();
		}
	});

	var deleteExistedTaskButton = new Ext.Toolbar.Button({
		text:'Delete Task' , 
		id:'deleteTaskButtonId',
		disabled:true,
		icon:'images/delete.png',
	    handler:function(){
	    	existedTaskPanel.deleteExistTask();
		}
	});

		
	/* Main Widget */
	var existedTaskPanel = new Ext.Panel({
		id:'existedTaskPanelId',
		border : false,
		region : 'center',
		sprintID : 0,
		issueID  : 0,
		notifyPanel:undefined,
		// Add Exist Task action
		addExistTask:function()
		{
			var obj = this;
			var selections = selectedData.concat(existedTaskPanel.getSelections());
			Ext.Ajax.request({
				url:'addExistedTask.do',
				params: { 
					sprintID: obj.getSprintID(),
					issueID: obj.getIssueID(),
					selected: selections
				},
				success:function(response){
					// check action permission
					ConfirmWidget.loadData(response);
					if (ConfirmWidget.confirmAction()) {
						obj.notifyPanel.Notify_AddExistedTaskSuccess();
					}
				},
				failure:failureFn
			});
		},
		deleteExistTask:function() {
			var obj = this;
			var selections = selectedData.concat(existedTaskPanel.getSelections());
			Ext.Ajax.request({
				url:'deleteExistedTask.do',
				params: { 
					sprintID: obj.getSprintID(),
					issueID: obj.getIssueID(),
					selected: selections
				},
				success:function(response){
					// check action permission
					ConfirmWidget.loadData(response);
					if (ConfirmWidget.confirmAction()) {
						obj.notifyPanel.Notify_DeleteExistedTaskSuccess();
					}
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
		setSprintID : function( sprintID )
		{
			this.sprintID = sprintID;
		},
		setIssueID : function( issueID )
		{
			this.issueID = issueID;
		},
		getSprintID : function ()
		{
			return this.sprintID;
		},
		getIssueID : function()
		{
			return this.issueID;
		},
		tbar:[addExistedTaskButton, deleteExistedTaskButton],
		bbar: new Ext.PagingToolbar({
		pageSize: 25,
		store: taskStore,
		displayInfo: true,
		displayMsg: 'Displaying topics {0} - {1} of {2}',
		emptyMsg: "No topics to display",
		listeners: {
			render: function(c){
				c.refresh.hideParent = true;
				c.refresh.hide();
			},
			beforechange: function(){
				//換頁前將有被勾選的資料存入selectedData
				selectedData = selectedData.concat(existedTaskPanel.getSelections());
			},
			change: function(){
				var currentData = new Array();
				var otherData = new Array();
				//所有被勾選的資料
				for (var i = 0; i < selectedData.length; i++)
				{
					//目前頁面的資料有被勾選則存入currentData
					//別的頁面被勾選的資料則存入otherData
					var record = taskStore.getById(selectedData[i]);
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
		}),
		items : [taskBacklogWidget]
	});
	//原本AddExistedTask的頁面是用轉頁所以直接用Panel表示，但是後來改成彈出式的所以該Panel最外層設計一個window包住，已產生此功能
	ezScrum.ExistedTaskWidget = Ext.extend(Ext.Window, {
		title         : 'Existed Task',
		width         : 700,
		height        : 485,
		modal         : true,
		closeAction   : 'hide',
		constrain	  : true,
		sprintID      : 0,
		issueID       : 0,
		notifyPanel   : undefined,
		initComponent : function()
		{
			var config = {
					layout : 'form',
					items  : [existedTaskPanel]
			};
			Ext.apply(this, Ext.apply(this.initialConfig, config));
			ezScrum.ExistedTaskWidget.superclass.initComponent.apply(this,arguments);
		},
		showWidget : function( notifyPanel , sprintID , issueID )
		{
			this.items.get(0).notifyPanel = notifyPanel;
			this.setSprintID( sprintID );
			this.setIssueID( issueID );
			this.updatePagingMemoryProxyData(); 
			this.show();
			
			var loadmask = new Ext.LoadMask(this.getEl(), {msg:"loading data and paging..."});
			loadmask.show();
		},
		hideWidget : function(){
			this.hide();
			taskStore.loadData([]);//清空store裡所有的資料
		},
		updatePagingMemoryProxyData : function(){
			var obj = this;
			//request Task 資料
			Ext.Ajax.request({
				url:'showAddExistedTask2.do?sprintID=' + this.getSprintID() + '&issueID=' + this.getIssueID(),
				success:function(response){
					// check action permission
					ConfirmWidget.loadData(response);
					if (ConfirmWidget.confirmAction()) {
						/*要使用一個全新的pagingMemoryProxy store才會有所更新
						 * 如果是taskStore.proxy.data = response;則第一次執行updateTaskStore()時可以正確載入資料
						 * 但之後的載入就都無法更新會一直維持在第一次
						 */
						pmp = new Ext.ux.data.PagingMemoryProxy();
						pmp.data = response;
						taskStore.proxy = pmp;
						taskStore.load({params:{start:0, limit:25}});
						taskStore.reload();// 去通知所有引用到taskStore的元件來更新taskStore的資料
						var loadmask = new Ext.LoadMask(obj.getEl(), {msg:"loading data and paging..."});
						loadmask.hide();
					}
				},
				failure:failureFn
			})
		},
		setSprintID : function( sprintID )
		{
			this.sprintID = sprintID;
			this.items.get(0).setSprintID( sprintID );
		},
		setIssueID : function( issueID )
		{
			this.issueID = issueID;
			this.items.get(0).setIssueID( issueID );
		},
		getSprintID : function ()
		{
			return this.sprintID;
		},
		getIssueID : function()
		{
			return this.issueID;
		},
		addExistedTaskMode: function() {
            deleteExistedTaskButton.hide();
            addExistedTaskButton.show();
		},
		deleteExistedTaskMode: function() {
            addExistedTaskButton.hide();
            deleteExistedTaskButton.show();
		}
	});