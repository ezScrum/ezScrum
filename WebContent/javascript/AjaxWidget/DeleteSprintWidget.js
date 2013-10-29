Ext.ns('ezScrum');

/* Delete Retrospective Widget */
ezScrum.DeleteSprintWidget = Ext.extend(Ext.Window, {
	title:'Delete Sprint',
	height:140,
	width:450,
	modal : true,
	constrain : true,
	issueId:'-1',
	closeAction:'hide',
	initComponent:function() {
		var config = {
			// Delete Sprint action url
			url : 'removeSprintPlan.do',
			items:{
				xtype:'label'
			},
			buttons:[
				{text:'Delete',scope:this, handler:this.onDelete},
				{text:'Cancel',scope:this, handler:this.onCancel}
			]
        }
        
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.DeleteSprintWidget.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('DeleteSuccess', 'DeleteFailure');
	},
    
    /*-----------------------------------------------------------
     *  外部Function要呼叫Delete Sprint這個動作的話就是從這邊開始的啦
     *-------------------------------------------------------------*/
	deleteSprint:function(sprintData){
		this.sprintData = sprintData;
        this.sprintID = sprintData.get('Id');
        this.items.get(0).setText("Delete Sprint"+sprintData.get('Id')+":"+sprintData.get('Goal'));
		this.show();
	},
	// Delete action
	onDelete : function(){
		// 顯示 Mask
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.show();

		// Ajax request
		var obj = this;
		Ext.Ajax.request({
			url:this.url,
			success:function(response){obj.onSuccess(response);},
			failure:function(response){obj.onFailure(response);},
			params:{sprintID : this.sprintID}
		});
	},
	// 按下取消按鈕 關閉刪除Retrospective視窗
	onCancel : function(){
		this.hide();
	},
	// Ajax request 成功
	onSuccess : function(response){
		// 隱藏 Mask
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();

		this.fireEvent('DeleteSuccess', this, response);
        this.hide();
	},
	onFailure : function(response){
		// 隱藏 Mask
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		
		this.fireEvent('DeleteFailure', this, response);
        this.hide();
	}
});