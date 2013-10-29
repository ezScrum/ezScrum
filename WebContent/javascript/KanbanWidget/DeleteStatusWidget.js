Ext.ns('ezScrum');

/* Delete Status Widget */
ezScrum.DeleteStatusWidget = Ext.extend(Ext.Window, {
	title:'Delete Status',
	height:140,
	width:450,
	modal:false,
	issueId:'-1',
	closeAction:'hide',
	initComponent:function() {
		var config = {
			// Delete Status action url
			url : 'ajaxDeleteStatus.do',
			items:{
				xtype:'label',
				html:'請再次確認是否刪除此Status<br/>'
			},
			buttons:[
				{text:'Delete',scope:this, handler:this.onDelete},
				{text:'Cancel',scope:this, handler:this.onCancel}
			]
        }
        
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.DeleteStatusWidget.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('DeleteSuccess', 'DeleteFailure');
	},
	deleteStatus:function(issueId){
		this.issueId = issueId;
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
		var rs = jsonStatusReader.read(response);
		// 顯示回應資料
		var record = rs.records[0];
		if(record)
		{
			this.fireEvent('DeleteSuccess', this, response, record.data['Id']);
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