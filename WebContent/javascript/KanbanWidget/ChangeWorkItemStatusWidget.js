Ext.ns('ezScrum');

/* Change WorkItem Status Widget */
ezScrum.ChangeWorkItemStatusWidget = Ext.extend(Ext.Window, {
	title:'Change WorkItem Status',
	height:140,
	width:450,
	modal:false,
	issueId:'-1',
	statusId:'-1',
	oldStatusId: '-1',
	oldPosition: '-1', // 在舊 Status Panel 中的位置
	closeAction:'hide',
	initComponent:function() {
		var config = {
			// Change WorkItem Status action url
			url : 'ajaxChangeWorkItemStatus.do',
			items:{
				xtype:'label',
				html:'<p style="font-size:12pt">請再次確認是否改變此WorkItem 狀態</p><br/>'
			},
			buttons:[
				{text:'Change',scope:this, handler:this.onChange},
				{text:'Cancel',scope:this, handler:this.onCancel}
			]
        }
        
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.ChangeWorkItemStatusWidget.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('ChangeSuccess', 'ChangeFailure', 'ChangeCancel');
	},
	changeStatus:function(issueId, statusId, oldStatusId, oldPosition){
		this.issueId = issueId;
		this.statusId = statusId;
		this.oldStatusId = oldStatusId;
		this.oldPosition = oldPosition;
		this.show();
	},
	// Change action
	onChange : function(){
		// 顯示 Mask
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.show();

		// Ajax request
		var obj = this;
		Ext.Ajax.request({
			url:this.url,
			success:function(response){obj.onSuccess(response);},
			failure:function(response){obj.onFailure(response);},
			params:{issueID : this.issueId, statusID : this.statusId}
		});
	},
	// 按下取消按鈕 關閉刪除WorkItem視窗
	onCancel : function(){
		this.fireEvent('ChangeCancel', this);
		this.hide();
	},
	// Ajax request 成功
	onSuccess : function(response){
		// 隱藏 Mask
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();

		this.fireEvent('ChangeSuccess', this, response);
	},
	onFailure : function(response){
		// 隱藏 Mask
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		
		this.fireEvent('ChangeFailure', this, response);
	}
});