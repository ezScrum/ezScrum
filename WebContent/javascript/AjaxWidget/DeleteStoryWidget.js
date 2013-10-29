Ext.ns('ezScrum');

/* Delete Story Widget */
ezScrum.DeleteStoryWidget = Ext.extend(Ext.Window, {
	title:'Delete Story',
	height:140,
	width:450,
	modal : true,
	constrain : true,
	issueId:'-1',
	closeAction:'hide',
	initComponent:function() {
		var config = {
			// Delete story action url
			url : 'ajaxDeleteStory.do',
			items:{
				xtype:'label',
				html:'請再次確認是否刪除此Story<br/>此動作會造成Story包含的Tasks不屬於任何一個Story<br />移除後您可以在Sprint Backlog中透過add exist task的方式加入至其他的Story'
			},
			buttons:[
				{text:'Delete',scope:this, handler:this.onDelete},
				{text:'Cancel',scope:this, handler:this.onCancel}
			]
        }
        
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.DeleteStoryWidget.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('DeleteSuccess', 'DeleteFailure');
	},
	deleteStory:function(issueId){
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
	// 按下取消按鈕 關閉刪除Story視窗
	onCancel : function(){
		this.hide();
	},
	// Ajax request 成功
	onSuccess : function(response){
		// 隱藏 Mask
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();

		ConfirmWidget.loadData(response);
		if (ConfirmWidget.confirmAction()) {
			// 讀取回應資料
			var rs = jsonStoryReader.read(response);
			
			// 顯示回應資料
			var record = rs.records[0];
			if(record) {
				this.fireEvent('DeleteSuccess', this, response, record.data['Id']);
			} else {
				this.fireEvent('DeleteFailure', this, response, this.issueId);
			}
		}
	},
	onFailure : function(response){
		// 隱藏 Mask
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		
		this.fireEvent('DeleteFailure', this, response, this.issueId);
	}
});