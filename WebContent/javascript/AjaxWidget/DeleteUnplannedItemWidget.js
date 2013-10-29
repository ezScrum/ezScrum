Ext.ns('ezScrum');

/* Delete Unplanned Item Widget */
ezScrum.DeleteUnplannedItemWidget = Ext.extend(Ext.Window, {
	title:'Delete Unplanned Item',
	height:140,
	width:450,
	modal : true,
	constrain : true,
	issueId:'-1',
	closeAction:'hide',
	initComponent:function() {
		var config = {
			// Delete Unplanned Item action url
			url : 'removeUnplannedItem.do',
			items:{
				xtype:'label',
				html:'請再次確認是否刪除此Unplanned Item'
			},
			buttons:[
				{text:'Delete',scope:this, handler:this.onDelete},
				{text:'Cancel',scope:this, handler:this.onCancel}
			]
        }
        
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.DeleteUnplannedItemWidget.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('DeleteSuccess', 'DeleteFailure');
	},
	deleteUnplannedItem:function(issueId){
		this.issueId = issueId;
		this.show();
	},
	// Delete action
	onDelete : function(){
		// Show Mask
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
	// Cancel
	onCancel : function(){
		this.hide();
	},
	// Ajax request Success
	onSuccess : function(response){
		// Hide Mask
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();

		// check action permission
		ConfirmWidget.loadData(response);
		if (ConfirmWidget.confirmAction()) {
			// read the response
			var rs = unplannedItemReader.readRecords(response.responseXML);
			
			var record = rs.records[0];
			if(record)
			{
				this.fireEvent('DeleteSuccess', this, response, record.data['Id']);
			}
			else
				this.fireEvent('DeleteFailure', this, response, this.issueId);
		}
	},
	onFailure : function(response){
		// Hide Mask
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		
		this.fireEvent('DeleteFailure', this, response, this.issueId);
	}
});