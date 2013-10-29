Ext.ns('ezScrum');

/* Delete Custom Issue Widget */
ezScrum.DeleteCustomIssueWidget = Ext.extend(Ext.Window, {
	title:'Delete Custom Issue',
	height:140,
	width:450,
	modal:false,
	issueId:'-1',
	closeAction:'hide',
	initComponent:function() {
		var config = {
			// Delete CustomIssue action url
			url : 'ajaxDeleteCustomIssue.do',
			items:{
				xtype:'label',
				html:'請再次確認是否刪除此Issue'
			},
			buttons:[
				{text:'Delete',scope:this, handler:this.submit},
				{text:'Cancel',scope:this, handler:this.onCancel}
			]
        }
        
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.DeleteCustomIssueWidget.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('DeleteSuccess', 'DeleteFailure');
	},
	deleteCustomIssue: function(issueId){
		this.issueId = issueId;
		this.show();
	},
	// Delete action
	submit : function(){
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
	// 按下取消按鈕 關閉刪除Custom Issue視窗
	onCancel : function(){
		this.hide();
	},
	// Ajax request 成功
	onSuccess : function(response){
		// 隱藏 Mask
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();

		// 讀取回應資料
		var rs = jsonCustomIssueReader.read(response);
		
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