Ext.ns('ezScrum');

/* Drop Relation Widget */
ezScrum.DropRelationWidget = Ext.extend(Ext.Window, {
	title:'Drop Relation',
	height:140,
	width:450,
	modal:false,
	srcId:'-1',
	desId:'-1',
	closeAction:'hide',
	initComponent:function() {
		var config = {
			// Drop Relation action url
			url : 'ajaxDropRelation.do',
			items:{
				xtype:'label',
				html:'請再次確認是否刪除與此WorkItem的關連<br/>'
			},
			buttons:[
				{text:'Drop',scope:this, handler:this.onDrop},
				{text:'Cancel',scope:this, handler:this.onCancel}
			]
        }
        
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.DropRelationWidget.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('DropSuccess', 'DropeFailure');
	},
	dropRelation:function(srcId, desId){
		this.srcId = srcId;
		this.desId = desId;
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
			params:{srcID : this.srcId, desID : this.desId}
		});
	},
	// 按下取消按鈕 關閉DropRelation視窗
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
			this.fireEvent('DropSuccess', this, response, record.data['Id']);
		}
		else
			this.fireEvent('DropFailure', this, response, this.desId);

	},
	onFailure : function(response){
		// 隱藏 Mask
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		
		this.fireEvent('DropFailure', this, response, this.desId);
	}
});