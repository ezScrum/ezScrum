Ext.ns('ezScrum');

/* Delete WorkItem Widget */
ezScrum.DeleteWorkItemWidget = Ext.extend(Ext.Window, {
	title:'Delete WorkItem',
	height:140,
	width:450,
	modal:false,
	workitemType: '',
	issueId:'-1',
	closeAction:'hide',
	initComponent:function() {
		var config = {
			// Delete WorkItem action url
			url : 'ajaxDeleteWorkItem.do',
			items:{
				xtype:'label',
				html:'<p style="font-size:12pt">請再次確認是否刪除此 Item</p><br/>'
			},
			buttons:[
				{text:'Delete',scope:this, handler:this.onDelete},
				{text:'Cancel',scope:this, handler:this.onCancel}
			]
        }
        
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.DeleteWorkItemWidget.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('DeleteSuccess', 'DeleteFailure');
	},
	deleteWorkItem:function(issueId, workitemType){
		this.issueId = issueId;
		this.workitemType = workitemType;
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

		// 讀取回應資料
		var rs = jsonWorkItemReader.read(response);
		
		// 顯示回應資料
		var record = rs.records[0];
		if(record)
		{
			this.fireEvent('DeleteSuccess', this, response, record.data['Id'], this.workitemType);
		}
		else
			this.fireEvent('DeleteFailure', this, response, this.issueId);

	},
	onFailure : function(response){
		// 隱藏 Mask
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		
		this.fireEvent('DeleteFailure', this, response, this.issueId);
	}
});