<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<script type="text/javascript" src="javascript/ux/gridfilters/menu/RangeMenu.js"></script>

<script type="text/javascript" src="javascript/ux/BufferView.js"></script>
<script type="text/javascript" src="javascript/ux/RowExpander.js"></script>

<script type="text/javascript" src="javascript/ux/treegrid/TreeGridSorter.js"></script>
<script type="text/javascript" src="javascript/ux/treegrid/TreeGridColumnResizer.js"></script>
<script type="text/javascript" src="javascript/ux/treegrid/TreeGridNodeUI.js"></script>
<script type="text/javascript" src="javascript/ux/treegrid/TreeGridLoader.js"></script>
<script type="text/javascript" src="javascript/ux/treegrid/TreeGridColumns.js"></script>
<script type="text/javascript" src="javascript/ux/treegrid/TreeGrid.js"></script>

<script type="text/javascript" src="javascript/AjaxWidget/Common.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/CommonFormVTypes.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/CreateStoryWidget.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/EditStoryWidget.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/DropStoryWidget.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/CreateTaskWidget.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/EditTaskWidget.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/DropTaskWidget.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/DeleteTaskWidget.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/MoveStoryWidget.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/Message.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/CreateSprintWidget.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/SprintBacklogGrouping.js"></script>

<link rel="stylesheet" type="text/css" href="javascript/ux/treegrid/treegrid.css" rel="stylesheet" />
<link rel="stylesheet" type="text/css" href="javascript/ux/gridfilters/css/GridFilters.css" />
<link rel="stylesheet" type="text/css" href="css/Message.css"/>
<link rel="stylesheet" type="text/css" href="css/ezScrum/Issue.css"/>

<script type="text/javascript" src="javascript/AjaxAction/edit_tag.js"></script>
<script type="text/javascript" src="javascript/CommonUtility.js"></script>

<script type="text/javascript">
	Ext.ns('ezScrum');

   
    /************************************************************
    *
    *   Function 宣告區域
    *
    *************************************************************/
    /*
	// 取網址列參數
	function getParameter( queryString, parameterName ) {
		// Add "=" to the parameter name (i.e. parameterName=value)
		var parameterName = parameterName + "=";
	
	    if ( queryString.length > 0 ) {
	        // Find the beginning of the string
	        begin = queryString.indexOf ( parameterName );
	        // If the parameter name is not found, skip it, otherwise return the
            // value
	        if ( begin != -1 ) {
	        // Add the length (integer) to the beginning
	            begin += parameterName.length;
	            // Multiple parameters are separated by the "&" sign
	            end = queryString.indexOf ( "&" , begin );
	            if ( end == -1 ) {
	                end = queryString.length
	            }
	            // Return the string
	            return unescape ( queryString.substring ( begin, end ) );
	        }
	        // Return "null" if no parameter has been found
	        return "";
	    }
	}
	
	function floatOperation(operandA, operandB, isAdd) {
		var numA = parseFloat(operandA);
		var numB = parseFloat(operandB);
		if (isAdd)
			return (numA + numB);
		else
			return (numA - numB);
	}
	
	function showMask(targetId, msg) {
		new Ext.LoadMask(Ext.get(targetId), {msg:msg}).show();
	}
	
	function hideMask(targetId) {
		new Ext.LoadMask(Ext.get(targetId), {msg:msg}).hide();
	}
	
	function failureFn() {
		alert('Failure');
		hideMask('BacklogWidget');
	}
	
	function getHandlers(sprintId) {
        // 取得 Handler 資料
		Ext.Ajax.request({
			url:'getAddSprintTaskInfo.do?sprintId=' + sprintId,
			success:function(response){
				handlerComboStoreForCreate.loadData(response.responseXML);
				handlerComboStoreForEdit.loadData(response.responseXML);
				partnerStoreForCreate.loadData(response.responseXML);
			}
		});
    }
	
	/************************************************************
    *
    *   儲存的資料結構區域
    *
    *************************************************************/
    /* 
	var expander = new Ext.ux.grid.RowExpander({
        tpl : new Ext.XTemplate(
            '<br><p><b>Name:</b><br /> {Name:nl2br}</p>',
            '<tpl if="Notes"><p><b>Notes:</b><br /> {Notes:nl2br}</p></tpl>',
            '<tpl if="HowToDemo"><p><b>How To Demo:</b><br /> {HowToDemo:nl2br}</p></tpl>',
            '<tpl for="AttachFileList"><p><b>Attach Files:</b><br /><a href="{DownloadPath}" target="_blank">{FileName}</a> <tpl if="this.hasPermission()">[<a href="#" onclick="Ext.getCmp(\'BacklogWidget\').deleteAttachFile({FileId}, {IssueId}); return false;">Delete</a>]</tpl><br /></tpl>',
            '<br />',{
            hasPermission:function()
            {
            	return Ext.getCmp('BacklogWidget').editStoryPermission;
            }}
        ),
        enableCaching :false
    });
	
	// 建立 Story GridView 的 Column
    var createColModel = function () {
        var columns = 
        	[expander,
		    	{dataIndex: 'Id',header: 'Id', width: 50, filterable: true, renderer: function(value, metaData, record, rowIndex, colIndex, store){var link = "<a href=\"" + record.data['Link'] + "\" target=\"_blank\">" + value + "</a>"; return link;}},
		    	{dataIndex: 'Tag',header: 'Tag', width: 100},
				{dataIndex: 'Name',header: 'Name', width: 300,renderer: function(value, metaData, record, rowIndex, colIndex, store){if(record.data['Attach'] == 'true') return "<image src = \"./images/paperclip.png\" />" + value; return value}},
				{dataIndex: 'Hanlder', header: 'Hanlder', width: 70},
				{dataIndex: 'Value',header: 'Value', width: 70},
				{dataIndex: 'Estimate',header: 'Estimate', width: 70},
				{dataIndex: 'Importance',header: 'Importance', width: 70},		            
				{dataIndex: 'Status',header: 'Status', width: 70}
		    ];

        return new Ext.grid.ColumnModel({
            columns: columns,
            defaults: {
                sortable: true
            }
        });
    };
    
    // 建立 Task GridView 的 Column
    var createTaskColModel = function () {
        var columns = [
		            {dataIndex: 'Id',header: 'Id', width: 50,filterable: true,renderer: function(value, metaData, record, rowIndex, colIndex, store){var link = "<a href=\"" + record.data['Link'] + "\" target=\"_blank\">" + value + "</a>"; return link;}},
		            {dataIndex: 'Name',header: 'Name', width: 300},
		            {dataIndex: 'Estimate',header: 'Estimate', width: 70},
		            {dataIndex: 'Actual',header: 'Actual', width: 70},
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
    
    // 儲存 Task 資料
    var taskStore = new Ext.data.Store({
        fields:[
            {name:'Id', type:'int'},
            {name:'Link'},
            {name:'Name'},
            {name : 'Estimate', type:'float'},
            {name : 'Actual'},
            {name : 'Handler'},
            {name : 'Partners'},
            {name : 'Notes'}
        ],
        reader:taskReader
    });
    
	// Data store
    var storyStore = new Ext.data.Store({
        fields:[
            {name:'Id', type:'int'},
            {name:'Link'},
            {name:'Name'},
            {name : 'Value', type:'int'},
            {name : 'Importance', type:'int'},
            {name : 'Estimate', type:'float'},
            {name : 'Status'},
            {name : 'Release'},
            {name : 'Sprint'},
            {name : 'Notes'},
            {name : 'HowToDemo'},
            {name : 'Tag'},
            {name : 'Attach'}
        ],
        reader:jsonStoryReader
    });
    
	// 儲存 Sprint 資料
    var sprintComboStore = new Ext.data.Store({
        id: 0,
        fields: [
            {name: 'Id', type: 'int'},
            {name: 'Name'},
            {name: 'Start'},
            {name: 'Goal'}
        ],
        reader:sprintForComboReader
    });
     
    // create the Data Store
    var thisSprintStore = new Ext.data.Store({
        fields:[
            {name : 'Id', sortType:'asInt'},
            {name : 'Name'},
            {name : 'CurrentPoint'},
            {name : 'LimitedPoint'},
            {name : 'TaskPoint'},
            {name : 'ReleaseID'},
            {name : 'SprintGoal'}
        ],
        reader:jsonSprintReader
    });
        
    /************************************************************
    *
    *   視窗物件宣告區
    *
    *************************************************************/
    /*
    // Move Story Widget
    var moveStoryWidget = new ezScrum.MoveStoreWidget({
    	listeners:{
            MoveSuccess:function(issueId){
				// Drop Story Success
                //sprintBacklogWidget.removeStory(issueId);
                //taskWidget.removeAllTask();
                
                // 更新 Sprint Backlog Title
                var firstRecord = comboInGrid.getStore().getAt(comboInGrid.selectedIndex);
                comboInGrid.fireEvent('select',comboInGrid,firstRecord,firstRecord.data['Id']);                
                
                this.hide();
                Ext.example.msg('Move Story','Move Story Success.');
            },
            MoveFailure:function(issueId){
                Ext.example.msg('Move Story', 'Move Story Failure.');
            }
    	}
    });
        
    // Create Story Widget
    var createStoryWidget = new ezScrum.AddNewStoryWidget({
        listeners:{
            CreateSuccess:function(win, form, response, record){
                //sprintBacklogWidget.addRecord(record);
                
                // 更新 Sprint Backlog Title
                var firstRecord = comboInGrid.getStore().getAt(comboInGrid.selectedIndex);
                comboInGrid.fireEvent('select',comboInGrid,firstRecord,firstRecord.data['Id']);
                
                this.hide();
                Ext.example.msg('Create Story', 'Create Story Success.');
            },
            CreateFailure:function(win, form, response, issueId){
                Ext.example.msg('Create Story', 'Create Story Failure.');
            }
        }
    });
    
    // Edit Story Widget
    var editStoryWidget = new ezScrum.EditStoryWidget({
        listeners:{
            LoadSuccess:function(win, form, response, record){
                // Load Story Success
            },
            LoadFailure:function(win, form, response, issueId){
                Ext.example.msg('Load Story', 'Load Story Failure.');
            },
            EditSuccess:function(win, form, response, record){
                //sprintBacklogWidget.updateRecord(record);
                
                // 更新 Sprint Backlog Title
                var firstRecord = comboInGrid.getStore().getAt(comboInGrid.selectedIndex);
                comboInGrid.fireEvent('select',comboInGrid,firstRecord,firstRecord.data['Id']);
                
                this.hide();              
                Ext.example.msg('Edit Story', 'Edit Story Success.');
            },
            EditFailure:function(win, form, response, issueId){
                Ext.example.msg('Edit Story', 'Edit Story Failure.');
            }
        }
    });
    
    // Drop Story Widget
    var dropStoryWidget = new ezScrum.DropStoryWidget({
        listeners:{
            DropSuccess:function(win, response, issueId){
                // Drop Story Success
                //sprintBacklogWidget.removeStory(issueId);
                //taskWidget.removeAllTask();
                
                // 更新 Sprint Backlog Title
                var firstRecord = comboInGrid.getStore().getAt(comboInGrid.selectedIndex);
                comboInGrid.fireEvent('select',comboInGrid,firstRecord,firstRecord.data['Id']);
                
                this.hide();
                Ext.example.msg('Drop Story', 'Drop Story Success.');
            },
            DropFailure:function(win, response, issueId){
                // Drop Story Error
                Ext.example.msg('Drop Story', 'Drop Story Failure.');
            }
        }
    });
    
    // Create Task Widget
    var createTaskWidget = new ezScrum.AddNewTaskWidget({
        listeners:{
            CreateSuccess:function(win, form, response, record){
                //taskWidget.addRecord(record);
                
                // 更新 Sprint Backlog Title
                var firstRecord = comboInGrid.getStore().getAt(comboInGrid.selectedIndex);
                comboInGrid.fireEvent('select',comboInGrid,firstRecord,firstRecord.data['Id']);
                
                this.hide();
                Ext.example.msg('Create Task', 'Create Task Success.');
            },
            CreateFailure:function(win, form, response, issueId){
                Ext.example.msg('Create Task', 'Create Task Failure.');
            }
        }
    });
    
    // Edit Task Widget
    var editTaskWidget = new ezScrum.EditTaskWidget({
        listeners:{
            LoadSuccess:function(win, form, response, record){
                // Load Task Success
            },
            LoadFailure:function(win, form, response, issueId){
                Ext.example.msg('Load Task', 'Load Task Failure.');
            },
            EditSuccess:function(win, form, response, record){              
                //taskWidget.updateRecord(record);
                
                // 更新 Sprint Backlog Title
                var firstRecord = comboInGrid.getStore().getAt(comboInGrid.selectedIndex);
                comboInGrid.fireEvent('select',comboInGrid,firstRecord,firstRecord.data['Id']);
                
                this.hide();
                Ext.example.msg('Edit Task', 'Edit Task Success.');
            },
            EditFailure:function(win, form, response, issueId){
                Ext.example.msg('Edit Task', 'Edit Task Failure.');
            }
        }
    });
    
    // Drop Task Widget
    var dropTaskWidget = new ezScrum.DropTaskWidget({
        listeners:{
            DropSuccess:function(win, response, issueId){
                // Drop Task Success
                //taskWidget.removeTask(issueId);
                
                // 更新 Sprint Backlog Title
                var firstRecord = comboInGrid.getStore().getAt(comboInGrid.selectedIndex);
                comboInGrid.fireEvent('select',comboInGrid,firstRecord,firstRecord.data['Id']);
                
                this.hide();
            },
            DropFailure:function(win, response, issueId){
                // Drop Task Error
            }
        }
    });
    
    // Edit Sprint Widget
    var detailWin = new ezScrum.ShowSprintDetailWin({
        listeners:{
            //-----------------------------------------------------------
            //  Sprint修改完成之後，修改MasterWidget上顯示的Sprint資訊   
            //-------------------------------------------------------------
            Success:function(obj, response, values) {
                //發一個假的Select Event讓畫面針對Sprint作更新
                var firstRecord = comboInGrid.getStore().getAt(comboInGrid.selectedIndex);
                comboInGrid.fireEvent('select', comboInGrid, firstRecord, firstRecord.data['Id']);
                Ext.example.msg('Edit Sprint', 'Edit Sprint Success.');
            },
            Failure:function(obj, response, values) {
                Ext.example.msg('Edit Sprint', 'Edit Sprint Failure.');
            }
        }
    });
    
    // Sprint Widget
    var sprintBacklogWidget = new Ext.grid.GridPanel({
        id : 'gridPanel',
        region : 'center',
        store: storyStore,
        viewConfig: {
            forceFit:true
        },
        plugins: [expander],
        colModel: createColModel(),
        sm: new Ext.grid.RowSelectionModel({
            singleSelect:true,
            listeners:{
                // 選項改變則 request 該 Sprint 的資料
                rowselect:function(rowSelectionModel, rowIndex, record){
                    detailWidget.setTitle('Story #' + record.data['Id'] + ' - ' + record.data['Name']);
                    // 取得該 Story 底下所有的 Task 資料
                    Ext.Ajax.request({
                        url:'getTasksByStoryID.do?sprintID=' + record.data['Sprint'] + '&storyID=' + record.data['Id'],
                        success:function(response){
                            taskStore.removeAll();
                            taskStore.loadData(response.responseXML);
                        },
                        failure:function(){
                            alert('Failure');
                        }
                    });
                }
            }
        }),
        stripeRows: true,
        frame: true,
        removeStory:function(issueId)
        {
            // Drop Story Success
            var record = this.getStore().getById(issueId);
            this.getStore().remove(record);
            
            //變更masterWidget的Story Point
            //移除Story之後，要扣掉移除的Story與Task的點數
            masterWidget.changeStoryPoint(record.data['Estimate'],0,false);
        }
        ,
        addRecord:function(record)
        {
            // 將新增的 Story 新增至 Grid Panel
            this.getStore().insert(0, record);
            var id = record.data['Id'];
            var index = this.getStore().indexOfId(id);
            this.getSelectionModel().selectRow(index);
            this.getView().focusRow(index);
            
             // 變更目前 Sprint 的點數
            masterWidget.changeStoryPoint(record.data['Estimate'],0,true);               
        },
        updateRecord:function(record)
        {
			//保存舊的Estimate
            var oldValue = this.getStore().getById(record.data['Id']).data['Estimate'];
            var id = record.data['Id'];
            var data = this.getStore().getById(id);
            var index = this.getStore().indexOf(data);
            data.data = record.data;
            // 暫時使用這種方法強迫Store更新資料
            data.commit(true);
            this.getStore().afterCommit(data);
            this.getSelectionModel().selectRow(index);
            this.getView().focusRow(index);
             //計算要Story變更之後，要增加的點數
            var changeStoryPoint = parseFloat(record.data['Estimate']) - parseFloat(oldValue);
            // 變更目前 Sprint 的點數
            masterWidget.changeStoryPoint(changeStoryPoint,0,true);
        }
    });
    
    // Task Widget
    var taskWidget = new Ext.grid.GridPanel({
        id : 'taskGridPanel',
        region : 'center',
        store: taskStore,
        viewConfig: {
            forceFit:true
        },
        colModel: createTaskColModel(),
        sm: new Ext.grid.RowSelectionModel({
            singleSelect:true
        }),
        stripeRows: true,
        frame: true,
        
        // 取得目前Task列表中，所有Task總共的Point
        getAllTaskPoint:function()
        {
            var point = 0;
            this.getStore().each(function(rec)
            {
                point += (rec.get('Estimate') - 0);
            });
            return point;
        },
        removeAllTask:function()
        {
            masterWidget.changeStoryPoint(0,this.getAllTaskPoint(),false);
            this.getStore().removeAll();
        }
        ,
        removeTask:function(issueId)
        {
            // Drop Story Success
            var record = this.getStore().getById(issueId);
            this.getStore().remove(record);
            //變更masterWidget的Task Point
            var taskPoint = record.data['Estimate'];
            //移除Task之後，要扣掉Task的點數
            masterWidget.changeStoryPoint(0,taskPoint,false);
        }
        ,
        addRecord:function(record)
        {
            // 將新增的 Task 新增至 Grid Panel
            this.getStore().insert(0, record);
            var id = record.data['Id'];
            var index = this.getStore().indexOfId(id);
            this.getSelectionModel().selectRow(index);
            this.getView().focusRow(index);
            
            masterWidget.changeStoryPoint(0,record.data['Estimate'],true);
        },
        updateRecord:function(record)
        {
            //保存舊的Estimate
            var oldValue = this.getStore().getById(record.data['Id']).data['Estimate'];
            
            var id = record.data['Id'];
            var data = this.getStore().getById(id);
            var index = this.getStore().indexOf(data);
            data.data = record.data;
            
            // 暫時使用這種方法強迫Store更新資料
            data.commit(true);
            this.getStore().afterCommit(data);
            this.getSelectionModel().selectRow(index);
            this.getView().focusRow(index);
            
            //計算要Story變更之後，要增加的點數
            var changeTaskPoint = parseFloat(record.data['Estimate']) - parseFloat(oldValue);
            // 變更目前 Sprint 的點數
            masterWidget.changeStoryPoint(0,changeTaskPoint,true);
        }
    });
    
    // Detail Widget(for Task) 
    var detailWidget = new Ext.Panel({
        id:'DetailWidget',
        layout:'border',
        region : 'south',
        collapsible : true,
        height : 200,
        // Add Task action
        addTask:function()
        {
            if(Ext.getCmp('gridPanel').getSelectionModel().getSelected() != null)
            {
                var id = Ext.getCmp('gridPanel').getSelectionModel().getSelected().data['Id'];
                createTaskWidget.showWidget(document.getElementById("currentSprintID").value, id);
            }
        },
        // Edit Task action
        editTask:function()
        {
            if(Ext.getCmp('taskGridPanel').getSelectionModel().getSelected() != null)
            {
                var id = Ext.getCmp('taskGridPanel').getSelectionModel().getSelected().data['Id'];
                editTaskWidget.loadEditTask(document.getElementById("currentSprintID").value, id);
            }
        },
        // Drop Task action
        dropTask:function()
        {
            if(Ext.getCmp('taskGridPanel').getSelectionModel().getSelected() != null)
            {
                var id = Ext.getCmp('taskGridPanel').getSelectionModel().getSelected().data['Id'];
                var storyId = Ext.getCmp('gridPanel').getSelectionModel().getSelected().data['Id'];
                dropTaskWidget.dropTask(id, document.getElementById("currentSprintID").value, storyId);
            }
        },
        // Show Task History action
        showHistory:function()
        {
            if(Ext.getCmp('taskGridPanel').getSelectionModel().getSelected() != null)
            {
                var id = Ext.getCmp('taskGridPanel').getSelectionModel().getSelected().data['Id'];
                document.location.href  = "<html:rewrite action='/showIssueHistory' />?issueID="+id+"&type=sprint&sprintID="+document.getElementById("currentSprintID").value;
            }
        },
        tbar: [
        ],bbar: [
        ],
        items : [taskWidget]
    });
       
    // Sprint Combobox
    var comboInGrid = new Ext.form.ComboBox({
        tpl           : '<tpl for="."><div ext:qtip="{Goal}" class="x-combo-list-item">Sprint {Id}</div></tpl>',
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
            // 選項改變則 request 該 Sprint 的資料
            select:function(combox, record, index){
                Ext.Ajax.request({
                    url:'showSprintBacklog2.do?sprintID=' + record.data['Id'],
                    success:function(response){
                        // 重新 Load Sprint Story
                        //Ext.getCmp('gridPanel').getStore().removeAll();
                        //Ext.getCmp('gridPanel').getStore().loadData(Ext.decode(response.responseText));

                        thisSprintStore.loadData(Ext.decode(response.responseText));
                        
                        // 將目前 Sprint 的點數顯示在 Title 上面
						masterWidget.setTitle(thisSprintStore.getAt(0).get('ReleaseID') + "  ;  " + thisSprintStore.getAt(0).get('Name') + " - " + thisSprintStore.getAt(0).get('SprintGoal') + "  |  "  + 
						                      "Story Point : " + thisSprintStore.getAt(0).get('CurrentPoint') + " / " + thisSprintStore.getAt(0).get('LimitedPoint') + " ; Task Point : " + thisSprintStore.getAt(0).get('TaskPoint') );
                        
                        // 將目前選擇到的 StoryPoint TaskPoint 存入 Input
                        document.getElementById("currentStoryPoint").value = thisSprintStore.getAt(0).get('CurrentPoint');
                        document.getElementById("currentTaskPoint").value = thisSprintStore.getAt(0).get('TaskPoint');
                    },
                    failure:failureFn
                });
                combox.originalValue = record.data['Id'];
                combox.reset();
                
                // 將目前選擇到的 SprintID 存入 Input
                document.getElementById("currentSprintID").value = record.data['Id'];
                
                getHandlers(record.data['Id']);
            }
        }
    });
    
    // 主頁面 
    var masterWidget = new Ext.Panel({
        id:'BacklogWidget',
        title : 'Sprint Backlog',
        layout: 'border',
        region : 'center',
        height: 400,
        
        addStoryPermission: false,
        editStoryPermission: false,
        dropStoryPermission: false,
        showStoryPermission: false,
        showSprintPermission: false,
        showPrintablePermission: false,
        addTaskPermission: false,
        editTaskPermission: false,
        dropTaskPermission: false,
        showTaskPermission: false,
        //-----------------------------------------------------------
        //  進行點數的變更，如果isAdd為True代表是增加，False代表是移除
        //  storyPoint代表增加或減少的Story Point
        //  taskPoint代表要增加或減少的Task Point
        //-----------------------------------------------------------
        
        changeStoryPoint:function(storyPoint,taskPoint,isAdd) {
            // 變更目前 Sprint 的點數
            var newStoryPoint = floatOperation(document.getElementById("currentStoryPoint").value,storyPoint,isAdd);
            var newTaskPoint = floatOperation(document.getElementById("currentTaskPoint").value,taskPoint,isAdd);
            
			masterWidget.setTitle(thisSprintStore.getAt(0).get('ReleaseID') + "  ;  " + thisSprintStore.getAt(0).get('Name') + " - " + thisSprintStore.getAt(0).get('SprintGoal') + "  |  "  + 
			                      "Story Point : " + thisSprintStore.getAt(0).get('CurrentPoint') + " / " + thisSprintStore.getAt(0).get('LimitedPoint') + " ; Task Point : " + thisSprintStore.getAt(0).get('TaskPoint') );
            document.getElementById("currentStoryPoint").value = newStoryPoint;
            document.getElementById("currentTaskPoint").value = newTaskPoint;
            
        },
        // Add Story action
        addStory:function() {
            createStoryWidget.showWidget(document.getElementById("currentSprintID").value);
        },
        // Edit Story action
        editStory:function() {
            if(Ext.getCmp('gridPanel').getSelectionModel().getSelected() != null) {
                var id = Ext.getCmp('gridPanel').getSelectionModel().getSelected().data['Id'];
                editStoryWidget.loadEditStory(id);
            }
        },
        // Drop Story action
        dropStory:function() {
            if(Ext.getCmp('gridPanel').getSelectionModel().getSelected() != null) {
                var id = Ext.getCmp('gridPanel').getSelectionModel().getSelected().data['Id'];
                dropStoryWidget.dropStory(id, document.getElementById("currentSprintID").value);
            }
        },
        // Add Exist Story action
        addExistStory:function() {
            document.location.href  = "<html:rewrite action='/showExistedStory' />?sprintID=" + document.getElementById("currentSprintID").value;
        },
        moveStory:function() {
            //var storyRecord = sprintBacklogWidget.getSelectionModel().getSelected();
            //moveStoryWidget.moveStory(storyRecord.data['Id'],storyRecord.data['Sprint'],storyRecord.data['Release']);
        },
        showSprintInfo:function() {
            window.open("<html:rewrite action='/showSprintInformation' />?sprintID=" + document.getElementById("currentSprintID").value);
        },
        showPrintableStory:function() {
            window.open("<html:rewrite action='/showPrintableStory' />?sprintID=" + document.getElementById("currentSprintID").value);
        },
        // Show Story History action
        showHistory:function() {
            if(Ext.getCmp('gridPanel').getSelectionModel().getSelected() != null) {
                var id = Ext.getCmp('gridPanel').getSelectionModel().getSelected().data['Id'];
                document.location.href  = "<html:rewrite action='/showIssueHistory' />?issueID="+id+"&type=sprint&sprintID="+document.getElementById("currentSprintID").value;
            }
        },
        // Show Add Existed Task action
        addExistedTask:function() {
            if(Ext.getCmp('gridPanel').getSelectionModel().getSelected() != null) {
                var id = Ext.getCmp('gridPanel').getSelectionModel().getSelected().data['Id'];
                document.location.href  = "<html:rewrite action='/showAddExistedTask' />?sprintID=" + document.getElementById("currentSprintID").value + "&issueID=" + id;
            }
        },
        tbar: [
            {
                id:'preSprintBtn',
                icon:'images/previous.png',
                handler:function()
                {
                  var preValue = comboInGrid.selectedIndex - 1;
                  if(preValue >= 0)
                  {
                        comboInGrid.selectedIndex = preValue;
                        var firstRecord = comboInGrid.getStore().getAt(preValue);
                        comboInGrid.fireEvent('select',comboInGrid,firstRecord,firstRecord.data['Id']);
                  }
                }
            }
            ,
            comboInGrid
            ,
            {
                id:'nextSprintBtn',
                icon:'images/next.png',
                handler:function()
                {
                    var nextValue = comboInGrid.selectedIndex + 1;
                    if(nextValue < comboInGrid.getStore().getCount())
                    {
                        comboInGrid.selectedIndex = nextValue;
                        var firstRecord = comboInGrid.getStore().getAt(nextValue);
                        comboInGrid.fireEvent('select',comboInGrid,firstRecord,firstRecord.data['Id']);
                    }
                }
            },
            {
                id: 'sprintAction',
                xtype: 'buttongroup',
                columns: 3,
                title: '<u><b>Sprint Action</b></u>',
                items: [
                    {id:'addStoryBtn', text:'Add Story', icon:'images/add3.png', handler: function(){masterWidget.addStory();}},
                    {id:'addExistStoryBtn', text:'Add Existing Stories', icon:'images/add.gif', handler:function(){masterWidget.addExistStory();}},
                    {id:'showPrintableStoryBtn', text:'Printable Stories', icon:'images/text.png', handler:function(){masterWidget.showPrintableStory();}},
                    {id:'showSprintInfoBtn', text:'Sprint Information', icon:'images/clipboard.png', handler:function(){masterWidget.showSprintInfo();}},
                    {id:'editSprintBtn', text:'Edit Sprint', icon:'images/edit.png', 
                    handler:function(){ 
                            //取得目前選擇的Sprint ID 傳入Edit Sprint的對話框
                            //讓他自動去Server取得要編輯的Sprint資訊
                            var firstRecord = comboInGrid.getStore().getAt(comboInGrid.selectedIndex);
                            detailWin.autoLoadData(firstRecord.data['Id']);
                            detailWin.showWidget('Edit Sprint');
                        }
                    }
                    
                ]
            },{
                id: 'storyAction',
                xtype: 'buttongroup',
                columns: 3,
                title: '<u><b>Story Action</b></u>',
                items: [
                    {id:'editStoryBtn', disabled:true, text:'Edit Story', icon:'images/edit.png', handler: function(){masterWidget.editStory();}},
                    {id:'droptStoryBtn', disabled:true, text:'Drop Story', icon:'images/drop2.png', handler:function(){masterWidget.dropStory();}},
                    {id:'showStoryHistoryBtn', disabled:true, text:'Story History', icon:'images/history.png', handler:function(){masterWidget.showHistory();}},
                    {id:'addTaskBtn', disabled:true, text:'Add Task', icon:'images/add.gif', handler:function(){detailWidget.addTask();}},
                    {id:'addExistedTaskBtn', disabled:true, text:'Add Existing Task', icon:'images/add2.gif', handler: function(){masterWidget.addExistedTask();}},
                    {id:'moveStoryBtn',disabled:true,text:'Move Story',icon:'images/arrow_right.png',handler:function(){masterWidget.moveStory();}}
                ]
            },{
                id: 'taskAction',
                xtype: 'buttongroup',
                columns: 2,
                title: '<u><b>Task Action</b></u>',
                items: [
                    {id:'editTaskBtn', disabled:true, text:'Edit Task', icon:'images/edit.png', handler: function(){detailWidget.editTask();}},
                    {id:'dropTaskBtn', disabled:true, text:'Drop Task', icon:'images/drop2.png', handler:function(){detailWidget.dropTask();}},
                    {id:'showTaskHistoryBtn', disabled:true, text:'Task History', icon:'images/history.png', handler:function(){detailWidget.showHistory();}}
                ]
            },
            '->'
        ],
        items : [TreeWidget],
        storySelectionChange:function()
        {
            // story grid 中資料列只選擇一列則可以編輯+刪除
            var single = sprintBacklogWidget.getSelectionModel().getCount()==1;
            if(single && sprintBacklogWidget.getSelectionModel().getSelected().data["Status"] != "closed")
            {
                this.getTopToolbar().get('storyAction').get('editStoryBtn').setDisabled(!this.editStoryPermission);
                this.getTopToolbar().get('storyAction').get('droptStoryBtn').setDisabled(!this.dropStoryPermission);
                this.getTopToolbar().get('storyAction').get('showStoryHistoryBtn').setDisabled(!this.showStoryPermission);
                this.getTopToolbar().get('storyAction').get('addTaskBtn').setDisabled(!this.addTaskPermission);
                this.getTopToolbar().get('storyAction').get('addExistedTaskBtn').setDisabled(!this.addTaskPermission);
                this.getTopToolbar().get('storyAction').get('moveStoryBtn').setDisabled(!this.addTaskPermission);

            }
            else
            {
                this.getTopToolbar().get('storyAction').get('editStoryBtn').disable();
                this.getTopToolbar().get('storyAction').get('droptStoryBtn').disable();
                this.getTopToolbar().get('storyAction').get('showStoryHistoryBtn').disable();
                this.getTopToolbar().get('storyAction').get('addTaskBtn').disable();
                this.getTopToolbar().get('storyAction').get('addExistedTaskBtn').disable();
                this.getTopToolbar().get('storyAction').get('moveStoryBtn').disable();
            }
        },
        taskSelectionChange:function()
        {
            // task grid 中資料列只選擇一列則可以編輯+刪除
            var single = taskWidget.getSelectionModel().getCount()==1;
            if(single)
            {
                this.getTopToolbar().get('taskAction').get('editTaskBtn').setDisabled(!this.editTaskPermission);
                this.getTopToolbar().get('taskAction').get('dropTaskBtn').setDisabled(!this.dropTaskPermission);
                this.getTopToolbar().get('taskAction').get('showTaskHistoryBtn').setDisabled(!this.showTaskPermission);
            }
            else
            {
                this.getTopToolbar().get('taskAction').get('editTaskBtn').disable();
                this.getTopToolbar().get('taskAction').get('dropTaskBtn').disable();
                this.getTopToolbar().get('taskAction').get('showTaskHistoryBtn').disable();
            }
        },
        loadPermission:function(addStoryPermission, editStoryPermission, dropStoryPermission, showStoryPermission, showSprintPermission, showPrintablePermission, 
                            addTaskPermission, editTaskPermission, dropTaskPermission, showTaskPermission)
        {
            // 設定權限, 'true' 為可操作
            this.addStoryPermission = addStoryPermission == "true";
            this.editStoryPermission = editStoryPermission == "true";
            this.dropStoryPermission = dropStoryPermission == "true";
            this.showStoryPermission = showStoryPermission == "true";
            this.showSprintPermission = showSprintPermission == "true";
            this.showPrintablePermission = showPrintablePermission == "true";
            this.addTaskPermission = addTaskPermission == "true";
            this.editTaskPermission = editTaskPermission == "true";
            this.dropTaskPermission = dropTaskPermission == "true";
            this.showTaskPermission = showTaskPermission == "true";
            if(this.addStoryPermission){
                this.getTopToolbar().get('sprintAction').get('addStoryBtn').enable();
                this.getTopToolbar().get('sprintAction').get('addExistStoryBtn').enable();
            }
            if(this.showSprintPermission)
                this.getTopToolbar().get('sprintAction').get('showSprintInfoBtn').enable();
            if(this.showPrintablePermission)
                this.getTopToolbar().get('sprintAction').get('showPrintableStoryBtn').enable();
        }
    });
    
    /************************************************************
    *
    *   Ext onReady，此網頁JavaScript的開始點
    *
    *************************************************************/
    /*
	Ext.onReady(function() {
		Ext.QuickTips.init();
		
		// 取網址列參數
		var queryString = window.location.toString();
		var sprintID = getParameter(queryString, "sprintID");

		// ============== initial all widget data =============
		// 取得 Sprint 資料
		Ext.Ajax.request({
			url: 'getAddNewRetrospectiveInfo.do',
			success: function(response){
				sprintComboStore.loadData(response.responseXML);
			}
		});
		
		// 取得 SprintBacklog 資料
		Ext.Ajax.request({
			url:'showSprintBacklog2.do?sprintID='+ sprintID,
			success:function(response){
	 			storyStore.loadData(Ext.decode(response.responseText));
	 			thisSprintStore.loadData(Ext.decode(response.responseText));
                
                var thisSprintRecord = thisSprintStore.getAt(0);
                
				// 將 Sprint ComboBox 預設值設定為目前 Sprint
				comboInGrid.originalValue = thisSprintRecord.get('Name');
                var SprintID = thisSprintRecord.get('Id');
                var tmpRecord = comboInGrid.getStore().getById(SprintID);
                comboInGrid.selectedIndex = comboInGrid.getStore().indexOf(tmpRecord);
				comboInGrid.reset();
                
				// 將目前選擇到的 SprintID StoryPoint TaskPoint 存入 Input
	    		document.getElementById("currentSprintID").value = thisSprintRecord.get('Id');
	    		document.getElementById("currentStoryPoint").value = thisSprintRecord.get('CurrentPoint');
	    		document.getElementById("currentTaskPoint").value = thisSprintRecord.get('TaskPoint');
                
                // 取得 Handlers
	    		getHandlers(thisSprintRecord.get('Id'));
			},
			failure:failureFn
		});
		
		// 取得 SprintBacklog Permission 資料
		Ext.Ajax.request({
			url:'AjaxGetSPBPermission.do',
			success:function(response){
				var permissionRs = spbPermissionReader.readRecords(response.responseXML);
		 		var permissionCount = permissionRs.totalRecords;
		 		for(var i = 0; i < permissionCount; i++)
		 		{
		 			// 設定 Permission 至 Widget 中
		 			var permissionRecord = permissionRs.records[i];
		 		}
			}
		});
	
		//sprintBacklogWidget.getSelectionModel().on({'selectionchange':{buffer:10, fn:function(selectionModel){masterWidget.storySelectionChange();}}});
		//taskWidget.getSelectionModel().on({'selectionchange':{buffer:10, fn:function(){masterWidget.taskSelectionChange();}}});

		masterWidget.updateTitle();
		// 設定主要顯示的畫面
		masterWidget.render('centent');
	});
	*/
</script>

<div id = "centent"></div>
<% session.setAttribute("currentSideItem","showSprintBacklog");%>
<div id="SideShowItem" style="display:none;"><%=session.getAttribute("currentSideItem") %></div>