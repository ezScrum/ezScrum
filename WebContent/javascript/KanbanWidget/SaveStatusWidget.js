Ext.ns('ezScrum');

/* Save Status Widget */
ezScrum.SaveStatusWidget = Ext.extend(Ext.Window, {
	title:'Save Status',
	height:140,
	width:450,
	modal:false,
	issueId:'-1',
	closeAction:'hide',
	initComponent:function() {
		var config = {
			// Save Status action url
			url : 'ajaxSaveStatusOrder.do',
			items:{
				xtype:'label',
				html:'請再次確認是否儲存此Statuses順序<br/>'
			},
			buttons:[
				{text:'Save',scope:this, handler:this.onSave},
				{text:'Cancel',scope:this, handler:this.onCancel}
			]
        }
        
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.SaveStatusWidget.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('SaveSuccess', 'SaveFailure');
	},
	saveStatus:function(issueIds){
		this.issueId = issueIds;
		this.show();
	},
	// Save action
	onSave : function(){
		// 顯示 Mask
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.show();

		// Ajax request
		var obj = this;
		Ext.Ajax.request({
			url:this.url,
			success:function(response){obj.onSuccess(response);},
			failure:function(response){obj.onFailure(response);},
			params:{issueID : this.issueId}
		});
	},
	// 按下取消按鈕 關閉刪除WorkItem視窗
	onCancel : function(){
		this.hide();
	},
	// Ajax request 成功
	onSuccess : function(response){
		// 隱藏 Mask
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();

		this.fireEvent('SaveSuccess', this, response);
	},
	onFailure : function(response){
		// 隱藏 Mask
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		
		this.fireEvent('SaveFailure', this, response);
	}
});