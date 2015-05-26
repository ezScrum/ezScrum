/*
 * 這個Panel主要是拿來當sprintBacklogTree的代理人(proxy pattern)
 * */

ezScrum.SprintBacklog_TreePanel = Ext.extend(Ext.Panel,{
	header : false,
	layout : 'fit',
    initComponent : function() {
		this.addListener('updateSprintBacklogTree', function() {//add Listener要在呼叫super class之前否則super class也會監聽該事件
			this.updateSprintBacklogTree();	//更新樹的資料並且保留狀態
		});
		ezScrum.SprintBacklog_TreePanel.superclass.initComponent.call(this);
		this.createSprintBacklogTree( '' );//空字串預設就是當下spintID
	},
    reloadData: function( sprintID ) { 
		this.removeAll();//移除sprintBacklogTree
		this.createSprintBacklogTree( sprintID );//依據sprintID的不同在基本的SprintBacklog的cloumns後增加該sprint日期的標題欄位
	},
    createSprintBacklogTree:function( sprintID ) {
		MainLoadMaskShow();
    	var dynamicDateColumn = [];
    	var columns = SprintBacklogColumns;
    	var obj = this;
    	Ext.Ajax.request({
    		url: 'AjaxGetSprintBacklogDateInfo.do?sprintID=' + sprintID,
    		async: false,
    		success: function(response) {
    			var isSprintExist;
    			if(response.responseText == '')
    				isSprintExist = false;
    			else{
    				isSprintExist = true;
    				DateColumnStore.loadData(Ext.decode(response.responseText));
    			}
    			
    			for (var i=0 ; i<DateColumnStore.getCount() ; i++) {
    				var record = DateColumnStore.getAt(i);
    				var index = record.data['Id'];
    				var head = record.data['Name'];
    				var plugin = {dataIndex: index, header: head, align: 'center', width: 50};
    						
    				dynamicDateColumn.push(plugin);
    			}
    			//這裡因為生命周期的關係所以有一定的生成順序
    			columns = SprintBacklogColumns.concat(dynamicDateColumn);//製造出加上日期的columns
    			obj.add( { ref: 'SprintBacklog_Tree_ID', xtype: 'SprintBacklog_Tree' });//實體化SprintBacklogTree UI
    			obj.SprintBacklog_Tree_ID.columns = columns;//立即將columns指給SprintBacklogTree UI
        		obj.SprintBacklog_Tree_ID.reloadData( sprintID );//並且重新載入SprintBacklogTree的資料
    			obj.doLayout();//重新畫UI 並且在此動作mask會自動消失
    		}
    	});
    },
    updateSprintBacklogTree: function() {
    	this.SprintBacklog_Tree_ID.updateSprintBacklogTree();
    },
    getSelectionModel : function(){
    	return this.SprintBacklog_Tree_ID.getSelectionModel();
    },
    isStoryNodeSelected: function(){
    	if( this.SprintBacklog_Tree_ID == undefined ){
    		return false;
    	}
    	var selectedNode = this.SprintBacklog_Tree_ID.getSelectionModel().getSelectedNode();
    	if( selectedNode == undefined ){
    		return false;
    	}
    	var type = selectedNode.attributes['Type'];
    	if( type == "Story" ){
    		return true;
    	}
    },
    isTaskNodeSelected: function(){
    	if( this.SprintBacklog_Tree_ID  == undefined ){
    		return false;
    	}
    	var selectedNode = this.SprintBacklog_Tree_ID.getSelectionModel().getSelectedNode();
    	if( selectedNode == undefined ){
    		return false;
    	}
    	var type = selectedNode.attributes['Type'];
    	if( type == "Task" ){
    		return true;
    	}
    }
});
Ext.reg('SprintBacklog_TreeGrid', ezScrum.SprintBacklog_TreePanel);
/*
 * sprintBacklog的tree是由兩層的架構構成的
 * 第一個rootNode
 * 第一層列出story
 * 第二層列出task
 * */
ezScrum.SprintBacklog_Tree = Ext.extend(Ext.ux.tree.TreeGrid, {
	title		: 'Story & Task List',
	frame		: false,
	border		: false,
	region		: 'center',
	margins		: '0 0 0 0',
	enableSort	: false,
	enableHdMenu: false,
	singleExpand: false,
	viewConfig	: { forceFit: true },
    columns		: SprintBacklogColumns,
    dataUrl		: 'showSprintBacklogTreeListInfo.do',
	initComponent : function() {
		var obj = this;
    	this.getSelectionModel().on({
    	    'selectionchange': {
    	        buffer: 25,
    	        fn: function () {
    	            var selectedNode = obj.getSelectionModel().getSelectedNode();
    	            if (selectedNode != null ) {
    		            var type = selectedNode.attributes['Type'];
    		
    		            var PageEventObj = Ext.getCmp('SprintBacklog_Page_Event');
    		            if (type == "Story") {
    		            	PageEventObj.set_Story_Permission_disable(false);
    		            	PageEventObj.set_Task_Permission_disable(true);
    		            } else {
    		            	PageEventObj.set_Story_Permission_disableAll(true);
    		            	PageEventObj.set_Task_Permission_disable(false);
    		            }
    				}
    	        }
    	    }
    	});
		this.addListener('render', function() {
			this.expandAll();
    	});
    	ezScrum.SprintBacklog_Tree.superclass.initComponent.apply(this, arguments);
    	// Override DblClick Event to prevent
    	// toggle node on double click
    	Ext.override(Ext.tree.TreeNodeUI, { 
    		onDblClick : function(e){
    	        e.preventDefault();
    	        if(this.disabled){
    	            return;
    	        }
    	        if(this.checkbox){
    	            this.toggleCheck();
    	        }
    	        this.fireEvent("dblclick", this.node, e);
    	    }
    	});
    },
    saveTreeSate : function () {//紀錄操作動作前樹的狀態
        var nodePathArray = [];
    	var selectedNodePath;
    	//function to store state of tree
        var storeTreeState = function ( node , expandedNodes ) {
            if( node.isExpanded() ) {
                expandedNodes.push(node.getPath());
            }
            for( var i = 0 ; i < node.childNodes.length ; i++ ){
                if( node.childNodes[i].isSelected() ){
                	selectedNodePath = node.childNodes[i].getPath();//對task層找到seletedNode
                }
            }
        };
        this.getRootNode().eachChild(function ( child ) {
        	//設定class的屬性
        	if( child.isLeaf ){
        		child.ui.addClass('STORY');//為了幫story層加上顏色，必須提供class
        	}
            if( child.isSelected() ){
            	selectedNodePath = child.getPath();//對story層找到seletedNode
            }
            storeTreeState( child , nodePathArray );
        }); 
        if(typeof selectedNodePath  != 'undefined') {
        	nodePathArray.push( selectedNodePath );//把目前使用者所點選的node path藏在expandedNodes陣列的最後一個
        }
        return nodePathArray;
        
    },
    restoreTreeState : function ( nodePathArray ) {//將之前樹的狀態還原
        for(var i = 0; i < nodePathArray.length - 1 ; i++) {
            if(typeof nodePathArray[i] != 'undefined') {
                this.expandPath( nodePathArray[i] );
            }
        }
        if(typeof nodePathArray[ nodePathArray.length - 1 ] != 'undefined') {
            this.selectPath( nodePathArray[ nodePathArray.length - 1 ]);
        }
    },
    listeners: {
        dblclick: function() {
        	// get selected Node
            var selectedNode = this.getSelectionModel().getSelectedNode();
            // get Node Type
            var nodeType = selectedNode.attributes['Type'];
            // handle Story or Task
            if(nodeType == 'Story'){
            	Ext.getCmp('SprintBacklog_Page_Event').editStory();
            } else if(nodeType == 'Task'){
            	Ext.getCmp('SprintBacklog_Page_Event').editTask();
            }
        },
        element: 'el'
    },
    reloadData: function( sprintID ) {
    	var newUrl;
		if( sprintID != 0 ){
    		newUrl = 'showSprintBacklogTreeListInfo.do?sprintID=' + sprintID;
		} else {
			newUrl = 'showSprintBacklogTreeListInfo.do';
    	}
		this.getLoader().dataUrl = newUrl;//改了load的位置之後 再載入時會自動將樹的資料載好
    },
    updateSprintBacklogTree: function() {
    	var that = this;
    	var state = this.saveTreeSate();//先將樹的狀態儲存
    	this.getLoader().load(this.getRootNode(), function () {
            that.restoreTreeState( state );//在樹load完之後restore樹的狀態 							
    	});
    }
});
Ext.reg('SprintBacklog_Tree', ezScrum.SprintBacklog_Tree);