Ext.ns('ezScrum');

/* Delete Release Widget */
ezScrum.DeleteReleaseWidget = Ext.extend(Ext.Window, {
	title: 'Delete Release',
	height: 140,
	width: 450,
	modal: true,
	constrain: true,
	issueId: '-1',
	closeAction: 'hide',
	initComponent: function() {
		var config = {
			// Delete Release action url
			url: 'removeReleasePlan.do',
			items: {
				xtype: 'label'
			},
			buttons: [{
				text: 'Delete',
				scope: this,
				handler: this.onDelete
			}, {
				text: 'Cancel',
				scope: this,
				handler: this.onCancel
			}]
		}

		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.DeleteReleaseWidget.superclass.initComponent.apply(this, arguments);

		this.addEvents('DeleteSuccess', 'DeleteFailure');
	},

	/*-----------------------------------------------------------
	 *  外部Function要呼叫Delete Sprint這個動作的話就是從這邊開始的啦
	 *-------------------------------------------------------------*/
	deleteRelease: function(selectedNode) {
		this.selectedNode = selectedNode;
		this.releaseID = selectedNode.attributes['ID'];
		this.name = replaceJsonSpecialChar(selectedNode.attributes['Name']);
		this.items.get(0).setText("Delete Release " + this.releaseID + " : " + this.name);
		this.show();
	},
	// Delete action
	onDelete: function() {
		// 顯示 Mask
		var myMask = new Ext.LoadMask(this.getEl(), {
			msg: "Please wait..."
		});
		myMask.show();

		// Ajax request
		var obj = this;
		Ext.Ajax.request({
			url: this.url,
			success: function(response) {
				obj.onSuccess(response);
			},
			failure: function(response) {
				obj.onFailure(response);
			},
			params: {
				releaseID: this.releaseID
			}
		});
	},
	// 按下取消按鈕 關閉刪除Retrospective視窗
	onCancel: function() {
		this.hide();
	},
	// Ajax request 成功
	onSuccess: function(response) {
		// 隱藏 Mask
		var myMask = new Ext.LoadMask(this.getEl(), {
			msg: "Please wait..."
		});
		myMask.hide();

		ConfirmWidget.loadData(response);
		if (ConfirmWidget.confirmAction()) {
			this.fireEvent('DeleteSuccess', this, response);
			this.hide();
		}
	},
	onFailure: function(response) {
		// 隱藏 Mask
		var myMask = new Ext.LoadMask(this.getEl(), {
			msg: "Please wait..."
		});
		myMask.hide();

		this.fireEvent('DeleteFailure', this, response);
		this.hide();
	}
});