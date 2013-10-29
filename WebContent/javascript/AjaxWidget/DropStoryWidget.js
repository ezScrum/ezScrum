Ext.ns('ezScrum');

/* Drop Story Widget */
ezScrum.DropStoryWidget = Ext.extend(Ext.Window, {
	title:'Drop Story',
	height:140,
	width:450,
	modal : true,
	constrain : true,
	issueId:'-1',
	sprintId:'-1',
	closeAction:'hide',
	initComponent:function() {
		var config = {
			// Delete Retrospective action url
			url : 'ajaxRemoveSprintBacklog.do',
			items:{
				xtype:'label',
				html:'Make sure you want to drop the story!<br/>'
			},
			buttons:[
				{text:'Drop',scope:this, handler:this.onDrop},
				{text:'Cancel',scope:this, handler:this.onCancel}
			]
        }
        
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.DropStoryWidget.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('DropSuccess', 'DropFailure');
	},
	dropStory:function(issueId, sprintId){
		this.issueId = issueId;
		this.sprintId = sprintId;
		this.show();
	},
	// Drop action
	onDrop : function(){
		// 顯示 Mask
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.show();

		// Ajax request
		var obj = this;
		Ext.Ajax.request({
			url:this.url,
			success:function(response){obj.onSuccess(response);},
			failure:function(response){obj.onFailure(response);},
			params:{issueID : this.issueId, sprintID : this.sprintId}
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

		// check action permission
		ConfirmWidget.loadData(response);
		if (ConfirmWidget.confirmAction()) {
			// 讀取回應資料
			var rs = myReader.readRecords(response.responseXML);
			
			// 顯示回應資料
			var record = rs.records[0];
			if(record)
			{
				this.fireEvent('DropSuccess', this, response, record.data['Id']);
			}
			else
				this.fireEvent('DropFailure', this, response, this.issueId);
		}
	},
	onFailure : function(response){
		// 隱藏 Mask
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		
		this.fireEvent('DropFailure', this, response, this.issueId);
	}
});